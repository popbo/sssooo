<template>
  <div class="Meta2dMaterial">
    <!-- <div class="more-graphical">
      <a-button class="btn-more" @click="openModalMore">
        <i class="t-icon icon-zt-tuxingkuguanli mr4"></i>
        {{ t('图形库管理') }}
      </a-button>
    </div> -->
    <div class="search">
      <!-- <a-input-search
        v-model:value="searchKey"
        :disabled="activeKey != '系统组件'"
        :placeholder="t('搜索')"
        style="width: 200px"
        @change="onSearch($event)"
      />-->
      <a-input
        v-model:value="searchKey"
        :placeholder="t('搜索')"
        style="width: 200px"
        @change="onSearch"
      />
    </div>

    <a-tabs v-model:activeKey="activeKey" size="small" class="PropsTabs">
      <a-tab-pane key="图纸">
        <template #tab>
          <span class="tabText">{{ t('图纸') }}</span>
        </template>
        <div>
          <div class="hover new-fold">
            <div class="div-new" @click="onAdd(0)">
              <i class="t-icon t-box-add mr4"></i>
              {{ t('新建文件夹') }}
            </div>
            <!-- <i class="t-icon t-box-add mr4"></i>
            {{ t('新建文件夹') }}-->
            <div class="div-sort">
              <i
                class="t-icon hover"
                :class="!showList ? 't-tiled' : 't-sort'"
                @click="showList = !showList"
              >
              </i>
            </div>
          </div>
          <SystemPanel
            :list="meta2dList2"
            :type="0"
            :showList="showList"
            @openFolder="openFolder2"
            v-model:factiveKey="meta2dActive"
          ></SystemPanel>
        </div>
      </a-tab-pane>
      <a-tab-pane key="系统组件">
        <template #tab>
          <span class="tabText">{{ t('系统组件') }}</span>
        </template>
        <SystemPanel
          :list="list"
          :type="1"
          v-model:factiveKey="panelActive"
          :showList="false"
          :allPngCombineArr="allPngCombineArr"
        ></SystemPanel>
      </a-tab-pane>
      <a-tab-pane key="我的组件">
        <template #tab>
          <span class="tabText">{{ t('我的组件') }}</span>
        </template>
        <div>
          <div class="hover new-fold" @click="onAdd(2)">
            <div class="div-new">
              <i class="t-icon t-box-add mr4"></i>
              {{ t('新建文件夹') }}
            </div>
          </div>
          <SystemPanel
            :list="usersList"
            :type="2"
            :showList="false"
            @changeUserList="usersListChange"
            @openFolder="openUserFolder"
            v-model:factiveKey="userActive"
          ></SystemPanel>
        </div>
      </a-tab-pane>
    </a-tabs>
    <div class="dialogClass" ref="dialogBox">
      <a-modal
      :getContainer="()=>$refs.dialogBox"
        v-model:visible="showModelNewFolder"
        :title="t('新建文件夹')"
        :cancelText="t('取消')"
        :okText="t('确定')"
        :centered="true"
        @ok="okAdd"
      >
        <a-input v-model:value="newFolder" />
      </a-modal>
      <a-modal
        v-model:visible="showModal"
        :getContainer="()=>$refs.dialogBox"
        :title="t('图形库管理')"
        width="400px"
        :bodyStyle="{ maxHeight: '500px', overflowY: 'scroll' }"
        :cancelText="t('取消')"
        :okText="t('确定')"
        :centered="true"
        @ok="onModalOk"
      >
        <a-checkbox
          class="block m4 pb12"
          :checked="showAll"
          @change="onShowMaterialAll"
        >
          <b>{{ t('全选') }}</b>
        </a-checkbox>

        <a-checkbox
          class="block m4"
          v-for="(item, index) in list"
          :key="item.name"
          v-model:checked="item.show"
          @change="onShowMaterials()"
          >{{ t(item.name) }}</a-checkbox
        >
      </a-modal>
    </div>
  </div>
</template>

<script lang="ts">
import {
  computed,
  defineComponent,
  nextTick,
  onUnmounted,
  ref,
  watch,
  watchEffect,
  onMounted
} from 'vue';
import { useStore } from 'vuex';

import { message } from 'ant-design-vue';
import axios from '@/http';
import SystemPanel from './common/SystemPanel.vue';
import { t } from '@/i18n/i18n';
import { Meta2d } from '@topometa2d/core';
import { MaterialsGroup } from '@/services/material';
import localforage from 'localforage';
import { getIconListByIF } from './icons';
import {
  isLogin,
  User,
  noLoginTip,
  ShowGroupsType,
  getShowGroups,
  setShowGroups,
} from '@/services/user';
import { getPngs } from './pngs';
import { getPinyin } from './pinyin';
import {allPngsCombine} from '@/components/utils'
import Bus from '@/components/common/bus';
declare const window: any;
declare const meta2d: Meta2d;

export default defineComponent({
  name: 'Meta2dMaterial',
  components: {
    SystemPanel,
  },
  setup() {
    const store = useStore();
    const user = computed<User>(() => store.state.user.profile);
    const list = ref([]);//系统组件
    const showList = ref<boolean>(false);
    const meta2dList = ref([]);//图纸
    const meta2dList2=ref([])//图纸---->新写法
    const usersList = ref([]);//我的组件

    let allPngCombineArr=ref([]) //组合状态之后的系统组件

    const activeKey = ref('系统组件');
    const showModelNewFolder = ref<boolean>(false);
    const newFolder = ref('');
    const newFolderType = ref(-1);
    const searchKey = ref('');
    const panelActive = ref<string[]>([]);
    const showModal = ref(false);
    const showAll = ref(false);
    const onAdd = (type: number) => {
      newFolderType.value = type;
      showModelNewFolder.value = true;
    };

    const okAdd = async () => {
      if (!isLogin(user.value)) {
        message.warn('请先登录');
        return;
      }
      if (!newFolder.value) {
        return message.warn('请输入文件夹名',0.5);//提示用户
      }
      newFolder.value = newFolder.value.trim();
      if (newFolder.value === '') {
        return;
      }

      const list = newFolderType.value === 2 ? usersList : meta2dList2;
      const folder = list.value.find((n) => n.name === newFolder.value);
      if (folder) {
        message.error(t('文件夹已经存在').toString());
        return;
      }
      if (newFolderType.value === 2) {
        const ret: any = await axios.post('/directory/components/add', {
          fullpath: '/2D/' + newFolder.value,
          username:localStorage.getItem('username'),
          categoryValue: `topo2dnocategorykey9999999999999`
        });
        (newFolder as any).id = ret._id;
        if (!ret) {
          return;
        }
      } else {
        //数据发送到后端
        const addFolderRes = await addFolder(
          newFolder.value,
          newFolderType.value === 2 ? 'component' : 'meta2d'
        );
        if (!addFolderRes) {
          // 添加失败，不加入到列表
          return;
        }
      }
      list.value.unshift({
        name: newFolder.value,
        id: (newFolder as any).id,
        show: true,
        end: 10,
        list: [],
      });
      newFolder.value = '';
      showModelNewFolder.value = false;
      if (newFolderType.value === 2) {
        meta2d.emit(
          't-userFolder',
          usersList.value.map((i) => i.name)
        );
      } else {
        meta2d.emit(
          't-meta2dFolder',
          meta2dList2.value.map((i) => i.name)
        );
      }
    };

    async function addFolder(
      name: string,
      type: 'meta2d' | 'component'
    ): Promise<boolean> {
      if (!isLogin(user.value)) {
        message.warn(noLoginTip);
        return false;
      }
      // const folder: any = await axios.post('/api/user/folder', {
      //   type,
      //   name,
      // });
      let str= type === 'meta2d' ? 'dir' : 'components'
      const folder: any = await axios.post(`/directory/${str}/add`, {
        type: `${type === 'meta2d' ? 'topo2d' : 'components'}`,
        list: [],
        name,
        fullpath:name,
        username:localStorage.getItem('username'),
        categoryValue: `${type === 'meta2d' ? user.value.categoryValue : `topo2dnocategorykey9999999999999`}`
      });
      (newFolder as any).id = folder._id;
      if (folder && folder.error) {
        message.warn('新增文件夹失败：' + folder.error);
        return false;
      }
      return true;
    }

    let searchTimer: NodeJS.Timeout = undefined;
    const onSearch = () => {
      if (searchTimer) {
        clearTimeout(searchTimer);
      }
      searchTimer = setTimeout(async () => {
        // const { getPinyin } = await import('../components/pinyin');
        if(activeKey.value==='图纸'){
        getSearchList(meta2dList2.value)//处理原数组
        }else if(activeKey.value==='系统组件'){
        const activeArray: string[] = [];
        const showGroups = getShowGroups(user.value);
        const length = Object.keys(showGroups).length;
        list.value.forEach(
          (item: {
            list?: any[];
            name: string;
            show?: boolean;
            pinyin?: string;
            pinyinShort?: string;
          }) => {
            if (length > 0 && !showGroups[item.name]) {
              // showGroups 中不展示的，不要更改它的 show
              return;
            }
            item.show = false;
            let tem = false;
            if (!item.pinyin) {
              item.pinyin = getPinyin(item.name);
              item.pinyinShort = getPinyin(item.name, true);
            }
            if (!searchKey.value) {
              item.show = true;
            } else {
              //搜索文件夹
              if (
                (item.name && item.name.includes(searchKey.value)) ||
                item.pinyin.includes(searchKey.value) ||
                item.pinyinShort.includes(searchKey.value)
              ) {
                tem = true;
                item.show = true;
              }
            }
            if (!tem) {
              item.list?.forEach((e: any) => {
                if (!e.pinyin) {
                  e.pinyin = getPinyin(e.name);
                  e.pinyinShort = getPinyin(e.name, true);
                }

                if (!searchKey.value) {
                  e.hidden = false;
                  item.show = true;
                } else {
                  if (
                    (e.name && e.name.includes(searchKey.value)) ||
                    e.pinyin.includes(searchKey.value) ||
                    e.pinyinShort.includes(searchKey.value)
                  ) {
                    e.hidden = false;
                    tem = true;
                    item.show = true;
                  } else {
                    e.hidden = true;
                  }
                }
              });
            }
            if (tem) {
              activeArray.push(item.name);
            }
          }
        );
        panelActive.value = activeArray;
        }else if(activeKey.value==='我的组件'){
        getSearchList(usersList.value)
        }else{
          return
        }
      }, 1000);
    };

    // 图纸搜索和我的搜索meta2dList，usersList
    // 遍历数组-》比较searchKey.value 和 meta2dList遍历不符合的都是 item.show = false;meta2dActive userActive

    const getSearchList=(arr)=>{
       const activeArray: string[] = [];
        arr.forEach((item:{
            list?: any[];
            name: string;
            show?: boolean;//通过控制show来显示隐藏文件夹，达到查找的目的
          })=>{
            item.show = false;
            let tem = false;
            if (!searchKey.value) {//当搜索框内的值不存在
              item.show = true;
            } else {
              //搜索文件夹
              if (item.name && item.name.includes(searchKey.value)) {
                tem = true;
                item.show = true;
              }
            }
            if (!tem) {
              item.list?.forEach((e: any) => {
                if (!searchKey.value) {
                  item.show = true;
                } else {
                  if (
                  e.name && e.name.includes(searchKey.value)
                  ) {
                    tem = true;
                    item.show = true;
                  }
                }
              });
            }
            if (tem) {
             activeArray.push(item.name);
            }
        })
    }

    const openModalMore = () => {
      showModal.value = true;
      showAll.value = list.value.every((item) => item.show);
    };

    const onModalOk = () => {
      const showGroups: ShowGroupsType = {};
      list.value.forEach((item) => {
        item.name && (showGroups[item.name] = item.show);
      });
      setShowGroups(user.value, showGroups);
      showModal.value = false;
    };

    const onShowMaterials = () => {
      showAll.value = list.value.every((item) => item.show);
    };

    const onShowMaterialAll = (e: any) => {
      const checked: boolean = e.target.checked;
      showAll.value = checked;
      for (const item of list.value) {
        item.show = checked;
      }
    };

    function usersListChange(image: string, folder: string) {
      usersList.value.forEach((item) => {
        if (item.name === folder) {
          item.list.push({
            show: true,
            folder,
            image,
          });
        }
      });
    }

    getSystemTools();

    async function getSystemTools() {
      const [
        { defalutMaterials, normalShapeLocalName },
        { registerOtherShape },
      ] = await Promise.all([import('./defaults'), import('./register')]);
      list.value = [...defalutMaterials];
      const oftenUseShapes: any[] = await localforage.getItem(
        normalShapeLocalName
      );
      list.value[0].list = oftenUseShapes || [];
      if (window.userDefinedDiagram) {
        //自定义系统图形库
        list.value.splice(2, 0, ...window.userDefinedDiagram);
      }
      list.value.push(...(await registerOtherShape()));

      // 根据用户个性化的图形库，是否显示
      habitsList();

      // if (window.meta2dTools.length !== window.jsDiagramNum) {
      //   let jsLoadInterval = setInterval(() => {
      //     if (window.meta2dTools.length == window.jsDiagramNum) {
      //       getJsDiagram();
      //       clearInterval(jsLoadInterval);
      //     }
      //   }, 500);
      // } else {
      //   getJsDiagram();
      // }
    }

    //tip 侧视图库和购买图库代码冲突
    /*function getJsDiagram() {
      if (window.registerToolsNew) {
        (window as any).registerToolsNew();
        const temJSClass = [];
        const temJSMaterial = [];
        window.meta2dTools.forEach((item: any) => {
          if (temJSClass.indexOf(item.subClassName) === -1) {
            temJSClass.push(item.subClassName);
            temJSMaterial.push({
              name: item.subClassName,
              show: true,
              list: [],
            });
          }
          temJSMaterial.forEach((_class: any) => {
            if (_class.name === item.subClassName) {
              _class.list.push(item);
            }
          });
        });
        list.value = list.value.concat(temJSMaterial);
        // 切换回页面仍需要注册，所以此处不清空
        // window.meta2dTools = undefined;
      }
    }*/

    function habitsList() {
      // 是否显示
      if (!user || !user.value) {
        return;
      }
      const showGroups = getShowGroups(user.value);
      if (!list || !list.value) {
        return;
      }
      list.value.forEach((item) => {
        if (showGroups[item.name] === true || showGroups[item.name] === false) {
          item.show = showGroups[item.name];
        }
      });
    }

    watchEffect(() => {
      // 监听到 user 值变化，向后端请求图形库
      // 增添判断时机，当刷新操作后，当home.vue组件，图纸请求完后再去进行图纸的列表请求
      if(user.value.categoryValue) getMeta2dAndUser()
    });

    function materialsMap(list: any[]) {
      if (!list) {return [];};
      return list.map((item) => {
        return {
          name: item.name,
          show: true,
          id: item._id,
          list: item.list ? item.list : [],
        };
      });
    }

    //获取我的组件文件列表 （云盘一级目录）
    async function getImageList() {
      let ret: { list: any[] } = await axios.post('/directory/components/list', {
        fullpath: '/2D',
        username: localStorage.getItem('username'),
        categoryValue: `topo2dnocategorykey9999999999999`
      });
      if (!ret) {
        return [];
      }
      //按id大小排序 更改排序
      if(ret.list.length > 0) sortById(ret.list)
      
      usersList.value = [];
      let temList = [];
      ret.list.forEach((item) => {
        let secondaryDir = item.fullpath.split('/');
        if (
          secondaryDir.length > 2 &&
          !temList.includes(secondaryDir[2]) &&
          secondaryDir[2] !== '缩略图' &&
          secondaryDir[2] !== '未分类'
        ) {
          temList.push(secondaryDir[2]);
          usersList.value.push({
            name: secondaryDir[2],
            show: true,
            list: [],
            id:item._id
          });
        }
      });
      usersList.value.push({
        name: '未分类',
        show: true,
        unModify: true,
        list: [],
      });
    }

    async function getUncategorizedImages() {
      const ret: { list: any[]; total: number } = await axios.post(
        '/file/minio/list?username=' + localStorage.getItem('username'),
        {
          type: '图片',
          directory: `/2D/未分类`,
        },
        {
          params: {
            current: 1,
            pageSize: 100,
          },
        }
      );
      if (!ret) {return [];};
      return ret.list.map((item) => {
        item.image = '/api/image/minio/' + item.filename;
        item.folder = `/2D/未分类`;
        return item;
      });
    }

    //TODO 一开始并没有“未分类”文件夹
    function addUncategorizedImages(folders: any[], list: any[]) {
      let folder = folders?.find((item) => {
        return item.name === '未分类';
      });
      if (!folder) {
        folders.push({
          name: '未分类',
          show: true,
          unModify: true,
          list: [],
        });
        folder = folders[folders.length - 1];
      }
      folder && folder.list.push(...list);
    }

    function addUncategorizedComponents(folders: any[], list: any[]) {
      let folderList = [];
      folders.forEach((item) => {
        if (item.name !== '未分类') { 
          folderList.push(item.name);
         }
      });
      let UncategorizedList = [];
      if (list) {
        UncategorizedList = list.filter((item) => { return !folderList.includes(item.folder) });
      }
      
      let folder = folders?.find((item) => {
        return item.name === '未分类';
      });
      folder && folder.list.push(...UncategorizedList);
    }

    async function getOldtopo2d() {
      let res: any = await axios.post(
        '/data/topo2d/list?username=' + localStorage.getItem('username'),
        {
          projection: {
            image: 1,
            _id: 1,
            name: 1,
          },
        },
        {
          params: {
            current: 1,
            pageSize: 100,
          },
        }
      );
      if (!res) {return [];};
      return res.list.map((item) => {
        return {
          image: item.image,
          id: item._id,
          name: item.name,
        };
      });
    }

    async function getOldtopo2dComponents() {
      let res: any = await axios.post(
        '/data/topo2d-components/list?username=' + localStorage.getItem('username'),
        {
          projection: {
            image: 1,
            _id: 1,
            name: 1,
            folder:1
          },
        },
        {
          params: {
            current: 1,
            pageSize: 100,
          },
        }
      );
      if (!res) {return [];};
      return res.list.map((item) => {
        return {
          image: item.image,
          id: item._id,
          name: item.name,
          folder:item.folder
        };
      });
    }
    
    function filterList(list: any[], folders: any[]=[]) {//给一个默认空数组，防止
      let idArr = [];
      let _list = [];
      folders.forEach((item) => {
        if (item&&item.list) {
          item.list?.forEach((_item) => {
            if (_item) {
              idArr.push(_item._id);
            }
          });
        }
      });
      if (list != null) {
        return list.filter((item) => !idArr.includes(item._id));
      } else {
        return [];
      }
    }

    async function getMeta2dAndUser() {
      if (isLogin(user.value)) {
        // 已经登陆的才去拿文件夹内容
        getImageList();
        await getFolderList();//获取图纸文件列表,阻塞进程完成图纸目录生成以便传给右侧
        getFolderType()//请求获取文件分类列表
        // const [meta2dFolder, componentFolder, images] = await Promise.all([getFolder('meta2d'), getFolder('component'), getUnFolderImages()]);
        const [meta2dFolder, onlineImages, cloudImages, topo2dList=[],topo2dComponentsList=[]] =
          await Promise.all([
            getFolder('meta2d'),//原获取图纸列表文件
            getUnFolderImages(),//获取未分类图元
            getUncategorizedImages(),
            //getOldtopo2d(),
            //getOldtopo2dComponents(),
          ]);

        // usersList.value = materialsMap(componentFolder);//  MaterialsGroup(images, folder.user);
        // usersList.value.push({
        //   name: 'Uncategorized(未分类)',
        //   show: true,
        //   unModify:true,
        //   list:images
        // })
        meta2dList.value = materialsMap(meta2dFolder); //MaterialsGroup([], folder.meta2d);
        let filtertopo2dList: any[] = filterList(topo2dList, meta2dFolder); //过滤掉没有分组的图纸
        addUncategorizedImages(meta2dList.value, filtertopo2dList);
        addUncategorizedImages(usersList.value, onlineImages);
        addUncategorizedImages(usersList.value, cloudImages);
        addUncategorizedComponents( usersList.value, topo2dComponentsList);
        // meta2d.emit('t-classes', [
        //   { name: '架构拓扑图' },
        //   { name: 'UML图' },
        //   { name: '物联网' },
        //   { name: '电力能源' },
        //   { name: '水利水务' },
        //   { name: '安防' },
        //   { name: '图表' },
        //   { name: '工具' },
        // ]);
        meta2d.emit(
          't-userFolder',
          usersList.value.map((i) => i.name)
        );
        let _meta2dFolder =  meta2dList2.value.map((i) => i.name);
        meta2d.emit(
          't-meta2dFolder',
          meta2dList2.value.map((i) => i.name)
        );
        window.meta2dFolder = _meta2dFolder||[];
        // 清空已经打开的文件夹
        meta2dActive.value = [];
        userActive.value = [];
      } else {
        meta2dList2.value = [];
        usersList.value = [];
      }
    }
    async function getMeta2dAndUserSecond(){
      if (isLogin(user.value)) {
        if(user.value.component){
          
          getImageList()//获取我的组件
          meta2d.emit(
            't-userFolder',
            usersList.value.map((i) => i.name)
          );
          userActive.value = [];
        }else{
          
          getFolderList();//获取图纸文件列表
          let _meta2dFolder = meta2dList2.value.map((i) => i.name);
          meta2d.emit(
            't-meta2dFolder',
            _meta2dFolder
          );
          window.meta2dFolder = _meta2dFolder || [];
          // 清空已经打开的文件夹
          meta2dActive.value = [];
        }
        
      } else {
        meta2dList2.value = [];
        usersList.value = [];
      }
    }
    //未分类图元
    async function getUnFolderImages() {
      const res: any = await axios.post('/data/topo2d-onlines/list?username=' + localStorage.getItem('username')+'&categoryValue=topo2dnocategorykey9999999999999', {
        query: {
          type: '2D',
          hasFolder: false,
        },
        categoryValue:user.value.categoryValue
      });
      if (!res || res.error) {
        return [];
      }
      return res.list.map((item) => {
        return { image: item.image, onlineId: item._id };
      });
    }

    function reloadFolder(str) {
      if(str==='保存') return getMeta2dAndUserSecond()  //只更新文件夹列表
      getMeta2dAndUser();
    }

    nextTick(() => {
      meta2d.on('t-save-success', reloadFolder);
    });

    onUnmounted(() => {
      meta2d.off('t-save-success', reloadFolder);
    });

    /**
     *
     * @param type component meta2d
     */
    // 获取所有图纸---->改成获取
    async function getFolder(type: string) {
      // let folder: any = await axios.get('/api/user/folder');
      // if (folder.error) {
      //   folder = {};
      // }
      // return folder;
      // let folder: any = await axios.post('/data/folders/list?username=' + localStorage.getItem('username'), {
      //   projection: {
      //     image: 1,
      //     _id: 1,
      //     name: 1,
      //     list: 1,
      //     editor: 1,
      //     createdAt: 1,
      //     updatedAt: 1,
      //     owner: 1,
      //   },
      //   query: {
      //     type: `${type === 'meta2d' ? 'topo2d' : 'topo2d-components'}`,
      //   },
      // });
      // if (!folder || folder.error) {
      //   folder = {};
      // }
      // return folder.list;
      return []
    }

    async function getImages() {
      return [];
      if (!isLogin(user.value)) {
        return [];
      }
      const result: { list: any[] } = await axios.get(
        '/api/user/component/images',
        {
          params: { pageIndex: 1, pageCount: 1000 },
        }
      );
      // 发出 images 数据，避免其它页面重新请求
      meta2d.emit('t-userImages', result.list || []);

      return result.list || [];
    }

    //该方法不会执行
    async function openFolder({
      type,
      folder: folderName,
    }: {
      type: number;
      folder: string;
    }) {
      // // 默认每次打开一个文件夹，此处取第一个
      // const folder = folderName === 'Uncategorized(未分类)' ? '' : folderName;
      // const rep: any = await axios.get('/api/user/topologies', {
      //   params: {
      //     folder,
      //     component: type === 2,
      //     pageIndex: 1,
      //     pageCount: 100,
      //   },
      // });
      // // 对数据进行调整
      // if (type === 2) {
      //   // 组件
      //   setFolderData(usersList.value, folderName, rep);
      // } else if (!type) {
      //   // 图纸
      //   setFolderData(meta2dList.value, folderName, rep);
      // }
    }

    async function openUserFolder({
      type,
      folder: folderName,
    }: {
      type: number;
      folder: string;
    }) {
      if (type !== 2) {
        return;
      }
      //如果已经存在就不再重复请求
      const folder = usersList.value.find((item) => {
        return item.name === folderName;
      });
      if (folder) {
        if (folder.list.length > 0) {
          return;
        }
      }
      //1.图片文件请求
      //2.自定义组件请求
      //3.在线图片请求
      const ret: { list: any[]; total: number } = await axios.post(
        '/file/minio/list?username='  + localStorage.getItem('username'),
        {
          type: '图片',
          directory: `/2D/${folderName}`,
        },
        {
          params: {
            current: 1,
            pageSize: 100,
          },
        }
      );
      if (!ret) {return [];};
      ret.list.map((item) => {
        item.image = '/api/image/minio/' + item.filename;
        item.folder = `/2D/${folderName}`;
        return item;
      });
      //TODO 自定义组件请求
      const ret_component: any = await axios.post(
        // '/data/topo2d-components/list?username=' + localStorage.getItem('username'),
        '/api/data/topo2d-components/list?username='+localStorage.getItem('username')+'&name='+folderName+'&categoryValue=topo2dnocategorykey9999999999999',
        // {
        //   query: {
        //     folder: folderName,
        //   },
        // }
      );
      if (!ret_component) {return [];};
      ret_component.list?.map((item) => {
        item.id = item._id;
        return item;
      });
      setFolderData(usersList.value, folderName, ret);
      setFolderData(usersList.value, folderName, ret_component);
    }
    /**
     * 为对应文件夹设置数据
     * @param folders 这里指 materials 中的 user 或 meta2d
     * @param folderName 文件名，展开的文件名
     * @param rep 请求到的结果数据
     */
    function setFolderData(folders: any[], folderName: string, rep: any) {
      // 组件
      const folder = folders?.find((item) => {
        return item.name === folderName;
      });
      if (folder) {
        !Array.isArray(folder.list) && (folder.list = []);
        // 已经是数组了
        // 未分类
        if (folderName === '未分类') {
          // 对查到的结果进行筛选，''  和 未分类都属于该文件夹
          rep.list?.forEach((item) => {
            if (!item.folder || item.folder === '未分类')
              folder.list.push(item);
          });
          return;
        }
        // 其他类型文件夹
        rep?.list && folder.list.push(...rep.list);
      }
    }

    // 图纸 我创建的  tab 已打开的文件夹
    const meta2dActive = ref<string[]>([]);
    const userActive = ref<string[]>([]);
    //备注
    // 监听系统组件的打开
    watch(
      () => panelActive.value,
      async (newV: string[], oldV: string[]) => {
        // newV 中有， oldV 中没有的，即新打开的
        const newOpen: string[] = [];
        for (const n of newV) {
          !oldV.includes(n) && newOpen.push(n);
        }
        if (!newOpen[0]) {
          return;
        }
        // 每次默认一个
        const item = list.value.find((item) => item.name === newOpen[0]);

        if (!item.list || item.list.length === 0) {
          if (item.svgFolder) {
            // svg 文件夹 true
            item.list = await getIconListByIF(item.name);
          } else if (item.svgFolder == false) {
            item.list=[]
            // png 文件夹值 false
            const allPng=await getPngs(item.name);
            allPngCombineArr.value=allPngsCombine(allPng)
          
            allPngCombineArr.value.forEach(x=>{
              x[0].combineCount=x.length
              item.list.push(x[0])
            })
            // item.list = await getPngs(item.name);
          } // 其它文件夹 undefined
        }
        Bus.$emit('transferAllPngCombine',allPngCombineArr.value)
      }
    );

    // 获取分类数据
    const userFolderType=ref([])
    async function getFolderType(){
    const ret_FolderType: any = await axios.get(
        '/api/data/topo2d/categoryKey/list?username='+localStorage.getItem('username')
      ); 
    if (!ret_FolderType) return
    if(ret_FolderType[0].error!=="查询分类列表失败"){
     userFolderType.value= ret_FolderType.map(i=>{
      return {name:i.categoryKey,key:i.categoryValue}
      })
    }
    meta2d.emit('t-classes', userFolderType.value);//更新文件分类选择列表
   }

  // 更改 获取所有图纸文件列表 （云盘一级目录）
    async function getFolderList() {
      let ret: { list: any[] } = await axios.post('/directory/dir/list' , {
        fullpath: '/2D',
        username:localStorage.getItem('username'),
        categoryValue: user.value.categoryValue
      });
      if (!ret) {
        return [];
      }
      meta2dList2.value = [];//重置
      let temList = [];
      // 对ret进行排序处理
      if(ret.list.length>0) sortById(ret.list)
      ret.list.forEach((item) => {
        let secondaryDir = item.fullpath;
        if (
          secondaryDir.length > 0 &&
          !temList.includes(secondaryDir) &&
          secondaryDir!== '未分类'
        ) {
          temList.push(secondaryDir);
          meta2dList2.value.push({
            name: secondaryDir,
            id: item._id,
            categoryValue: item.categoryValue,
            show: true,
            list: [],
          });
        }
      });
      meta2dList2.value.push({
        name: '未分类',
        show: true,
        unModify: true,
        list: [],
      });
    }
    
    // 点开图纸文件夹
    async function openFolder2({
      type,
      folder: folderName,
    }: {
      type: number;
      folder: string;
    }) {
      if (type !== 0) {
        return;
      }
      //如果已经存在就不再重复请求
      const folder = meta2dList2.value.find((item) => {
        return item.name === folderName;
      });
      if (folder&& folder.list.length > 0) {
        return;
      }
      //1.图纸文件请求
      const ret: { list: any[]; total: number } = await axios.post(
        `/api/directory/dir/data/small/list/?folder=${folderName}&username=${localStorage.getItem('username')}&collection=topo2d&categoryValue=${(meta2d.data()as any).categoryValue}`,
      );
      if (!ret) {return [];};
      setFolderData(meta2dList2.value, folderName, ret);
    }

    // 添加新的文件夹
    // 删除图纸
    // 更新图纸--->分成两个部分，1、更新这个图纸，2、更新这个图纸对应文件下的列表
    
    // 定义一个排序方法
    function sortById(list) {
      list.sort(function (a, b) {
        return (+b._id) - (+a._id);
      });
    }

    onUnmounted(() => {
      list.value = [];
    });

    return {
      list,
      usersList,
      activeKey,
      showList,
      showModelNewFolder,
      newFolder,
      newFolderType,
      onAdd,
      okAdd,
      searchKey,
      onSearch,
      panelActive,
      showModal,
      showAll,
      onModalOk,
      openModalMore,
      onShowMaterials,
      onShowMaterialAll,
      usersListChange,
      meta2dList,
      openFolder,
      openUserFolder,
      panelH: computed(() =>
        activeKey.value === '系统组件' ? 'calc(100% - 100px)' : '100%'
      ),
      meta2dActive,
      userActive,
      userFolderType,
      meta2dList2,//获取图纸文件夹列表
      openFolder2,//获取当前图纸文件夹内图纸
      allPngCombineArr,//组合状态后的系统组件全部内容
    };
  },
});
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.Meta2dMaterial {
  overflow: auto;
  overflow-y: hidden;
  .search {
    width: 100%;
    margin-top: 12px;
    margin-bottom: 12px;
    padding: 0px 9px;
    &:deep(.ant-input) {
      width: 190px !important;
      background: #232630 !important;
      border: 1px solid #283241;
      input {
        background: #f3f3f3 !important;
      }
    }
  }

  &:deep(.ant-tabs-content) {
    .new-fold {
      position: relative;
      padding-left: 16px;
      margin-top: 12px;
      padding-bottom: 20px;
      .div-new {
        display: inline-block;
        position: absolute;
      }
      .div-sort {
        display: inline-block;
        position: absolute;
        right: 20px;
      }
    }
    .ant-tabs-tabpane {
      height: v-bind('panelH'); //calc(100% - 100px);
      color: #a5a8b2;
    }
    .more-graphical {
      width: 176px;
      height: 30px;
      position: absolute;
      display: flex;
      justify-content: center;
      align-items: center;
      left: 50%;
      bottom: 100px;
      margin: -88px;
      border: 1px solid #232630;
      background: #181a24;
      .btn-more {
        &:hover {
          border: none !important ;
          color: #3a89fe;
        }
        color: #b4b7c1;
        border: none !important ;
        background: #232630;
        width: 100%;
        height: 28px;
        border-radius: 0;
        padding:0;
      }
    }
  }
}
.dialogClass{
  
  :deep(.ant-modal-content){
    background: #232630;
    .ant-modal-header{
      background: #232630;
      padding: 16px 0;
      margin: 0 24px;
      border-bottom:1px solid #363841;
      .ant-modal-title{
        color: #fff;
        font-size: 14px;
      }
      
    }
    .ant-modal-close-x{
      color:#fff
    }
    .ant-modal-footer{
      border-top:none;
      padding: 30px 16px;
      .ant-btn{
        background: #3d404c;
        border:1px solid #3d404c;
        color:#fff;
        &.ant-btn-primary{
          background: #1890ff;
          border-color: #1890ff;
        }
      }
    }
    .ant-modal-body{
      .ant-input{
        background: #181a24;
        color: #b4b7c1;
        border:1px solid #2e303d;
      }
    }
  }
}
</style>
 