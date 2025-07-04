import {
  Meta2dBackData,
  localMeta2dDataName,
  showModal,
} from '@/components/utils';
import axios from '@/http';
import { t } from '@/i18n/i18n';
import moment from 'moment';
import { LocationQuery } from 'vue-router';

export const objectToQueryString = (obj: any) => {
  return (
    '?' +
    Object.keys(obj)
      .map(function (key) {
        if (obj[key] === undefined || obj[key] === null || obj[key] === '') {
          return '';
        }

        if (
          obj[key] instanceof Array ||
          Object.prototype.toString.call(obj[key]) === '[object Array]'
        ) {
          return obj[key]
            .map(function (item: string) {
              return encodeURIComponent(key) + '=' + encodeURIComponent(item);
            })
            .join('&');
        } else {
          return encodeURIComponent(key) + '=' + encodeURIComponent(obj[key]);
        }
      })
      .join('&')
  );
};

export function setToken(query, store) {
  localStorage.setItem('token', query.token as string);
  store.dispatch('user/profile');
  const newQuery = { ...query };
  delete newQuery.token;
  return newQuery;
}

const lastNoSaveTips = t('检测到上次编辑有未保存的数据，是否恢复？');
/**
 * home preview 页面都使用到，请求数据或请求缓存数据
 */
export async function init(
  query: LocationQuery,
  isPreview = false // 预览页面不弹出提示
): Promise<Meta2dBackData> {
  const { id, version, component, folder, folderId } = query;
  let isComponent: any = component;
  if (isComponent === 'false') {
    isComponent = false;
  }
  if (id) {
    let [ret={}, { baseVer, compareVersion, upgrade }]: any[] = await Promise.all([
      // axios.get(`/api/meta2d/${id}`, {
      //   params: {
      //     version,
      //     view: 1,
      //   },
      // }),
      axios.post(
        `/data/${isComponent ? 'topo2d-components' : 'topo2d'}/get`,
        {
          id,
        }
      ),
      import('./upgrade'),
    ]);
    if (ret.error) {
      return;
    }

    if (!ret.pens) {
      const data = ret.data;
      delete ret.data;
      ret = Object.assign(ret, data);
    }
    if (!ret.version || compareVersion(ret.version, baseVer) === -1) {
      // 如果版本号不存在或者版本号 version < 1.0.0
      ret = upgrade(ret, baseVer);
    }

    const { default: localforage } = await import('localforage');
    const _localmeta2dData: Meta2dBackData = JSON.parse(
      await localforage.getItem(localMeta2dDataName + '-' + id)
    );
    if (_localmeta2dData) {
      if (
        moment(ret.updatedAt).isBefore((_localmeta2dData as any).localSaveAt)
      ) {
        //说明保存的不一定是最新的
        if (await showModal(lastNoSaveTips)) {
          return _localmeta2dData;
        } else {
          return ret;
        }
      }
    }
    //初始化预览和发布页面的不可拖拽和缩放
    if(!Object.hasOwn(ret,'disableMove')){
      ret.disableMove=true
    }
    if(!Object.hasOwn(ret,'disableScale')){
      ret.disableScale=true
    }
    return ret;
  } else {
    // 没有 id ，去 meta2dData 里取
    const { default: localforage } = await import('localforage');
    const meta2dData: Meta2dBackData = JSON.parse(
      await localforage.getItem(localMeta2dDataName)
    );
    if (meta2dData && isPreview) {
      return meta2dData;
    } else if (meta2dData && (await showModal(lastNoSaveTips))) {
      return meta2dData;
    } else {
      return {
        component: !!isComponent,
        folder,
        folderId,
        pens: [],
        disableMove:true,//初始化预览和发布页面的不可拖拽和缩放
        disableScale:true,
      } as Meta2dBackData;
    }
  }
}

//处理路由带绑定变量相关参数
export async function handleVarBind(query, data: Meta2dBackData) {
  let unVar = [
    'id',
    'version',
    'component',
    'folder',
    'e',
    'r',
    'token',
    'params',
  ];
  let varMap = {};
  const { params } = query;
  //TODO 路由传参处理接口
  if (params) {
    //请求接口变量
    let res: any = await axios.get(`/data/${params}`);
    if (res.error) {
      return;
    } else {
      varMap = res;
    }
  }
  for (let key in query) {
    if (key && !unVar.includes(key)) {
      varMap[key] = query[key];
    }
  }
  data &&
    data.pens.forEach((_pen) => {
      if (_pen.form) {
        _pen.form.forEach((_formItem) => {
          if (_formItem.dataIds) {
            if (Array.isArray(_formItem.dataIds)) {
              _formItem.dataIds.forEach((_dataId) => {
                let match = _dataId.dataId.match(/(\{{.*?\}})/g);
                if (match && match[0]) {
                  let matchVar = match[0].slice(2, -2);
                  if (varMap[matchVar]) {
                    _dataId.dataId = _dataId.dataId.replace(
                      match[0],
                      varMap[matchVar]
                    );
                  }
                }
              });
            } else {
              if (_formItem.dataIds.dataId) {
                let match = _formItem.dataIds.dataId.match(/(\{{.*?\}})/g);
                if (match && match[0]) {
                  let matchVar = match[0].slice(2, -2);
                  if (varMap[matchVar]) {
                    _formItem.dataIds.dataId = _formItem.dataIds.dataId.replace(
                      match[0],
                      varMap[matchVar]
                    );
                  }
                }
              }
            }
          }
        });
      }
    });
}
