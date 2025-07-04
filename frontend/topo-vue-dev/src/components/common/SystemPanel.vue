<template>
  <div class="content">
    <a-collapse
      :activeKey="factiveKey"
      expandIconPosition="left"
      :bordered="false"
      @change="activeKeyOnChanged"
    >
      <!-- <template #expandIcon="{ isActive }">
        <FolderOpenOutlined v-if="isActive" />
        <FolderOutlined v-else />
      </template> -->
      <template #expandIcon>
        <span>
          <i class="icon-zt icon-zt-wenjianjia"></i>
        </span>
      </template>
      <a-collapse-panel
        v-for="(item, index) in list"
        v-show="item.show"
        :key="item.name"
        :style="customStyle"
      >
        <template #header>
          <span
            style="
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
              max-width: 84px;
            "
            :style="{
              display:
                type === 1
                  ? 'inline-block'
                  : item.showRename
                  ? 'none'
                  : 'inline-block',
              maxWidth: type === 1 ? '84px' : '110px',
            }"
            :title="item.name"
            @dblclick="onShowRename(item)"
            >{{ `${t(item.name)}` }}</span
          >
          <span
            v-show="!item.showRename && type === 1"
            style="display: inline-block; overflow: hidden"
            >{{ '(' + (item.list.length || item.count) + ')' }}</span
          >
          <a-input
            class="modify-forder-input"
            v-show="item.showRename && type !== 1"
            v-model:value="modifyItem.name"
            @click.stop
            @blur="onRename(item)"
            @pressEnter="onRename(item)"
            @keyup.esc="onRename(item, true)"
          ></a-input>
        </template>
        <div class="thumbs">
          <template v-for="(menu, index) in item.list" v-if="type == 1">
            <div class="thumb" v-show="!menu.hidden">
              <div
                class="center hover"
                :title="menu.name"
                :draggable="true"
                @dragstart="onDrag($event, menu, !!menu.svg, item.id)"
                @drag="drag($event)"
                @dragend="dragEnd(menu)"
                @click.stop="onTouchstart($event, menu, !!menu.svg, item.id)"
                @touchstart.passive="
                  onTouchstart($event, menu, !!menu.svg, item.id)
                "
              >
                <i v-if="menu.icon" class="icon-size" :class="menu.icon"></i>
                <img v-else-if="menu.image" :src="menu.image" />
                <div v-else-if="menu.svg" v-html="menu.svg"></div>
              </div>
            </div>
          </template>

          <div
            v-else
            class="flex"
            :class="{ thumbunone: !showList, list: showList }"
            v-for="(userItem, indexItem) in item.list"
            :key="indexItem"
            v-show="!item.hidden"
          >
            <div
              class="center rel hover"
              :title="userItem.name"
              :draggable="type === 2"
              @dragstart="onDrag($event, userItem)"
              @drag="drag($event)"
              @dragend="dragEnd(userItem)"
              @click.stop="onTouchstart($event, userItem)"
              @touchstart.passive="onTouchstart($event, userItem)"
            >
              <div class="image">
                <img :src="userItem.image" />
              </div>
              <i
                class="t-icon t-gongyong--bianji"
                v-if="userItem.component && !userItem.image?.endsWith('.svg')"
                @click.stop="onOpen(userItem)"
              ></i>
              <div class="text" v-if="type === 0">{{ userItem.name }}</div>
              <i class="t-icon t-lock isLockPass" v-if="userItem.password"></i>
              <a-popconfirm
                placement="topLeft"
                :title="t('确定删除吗')"
                @confirm="removeItem(userItem, index, indexItem)"
                overlay-class-name="screen-gantt-popover"
              >
                <i class="t-icon t-shanchu" @click.stop></i>
              </a-popconfirm>
            </div>
          </div>
        </div>
        <template #extra>
          <a-tooltip
            v-if="type !== 1 && !item.unModify"
            :title="type == 0 ? t('新建图纸') : t('创建组件')"
          >
            <i
              class="t-icon t-file-add add"
              @click.stop="onAddMeta2d(item, $event)"
            ></i>
          </a-tooltip>
          <!-- name 可不传 默认 file ，我们后端也是 file -->
          <a-upload
            v-if="type === 2 && !item.unModify"
            :customRequest="uploadImage"
            :data="{
              type: '图片',
              directory: `/2D/${item.name}`,
              randomName: 1,
              public: true,
              shared: 'true',
            }"
            :headers="uploadHeaders"
            :multiple="false"
            listType="picture"
            :showUploadList="false"
            @change="imageChange($event, item.name)"
          >
            <a-tooltip :title="t('上传图片')">
              <i class="t-icon t-image_upload upload-image"></i>
            </a-tooltip>
          </a-upload>
          <a-popconfirm
            v-if="type !== 1 && !item.unModify"
            placement="topLeft"
            :title="t('确定删除吗？')"
            @confirm="removeName(item, index)"
            @visibleChange="deleteVisibleChange($event, item)"
            overlay-class-name="screen-gantt-popover"
          >
            <template #icon>
              <QuestionCircleOutlined style="color: red" />
              <!-- <i class="t-icon t-help-circle" style="color: red"></i> -->
            </template>

            <a-tooltip :title="t('删除文件夹')">
              <i class="t-icon t-shanchu delete" @click.stop></i>
            </a-tooltip>
          </a-popconfirm>
          <i
            class="t-icon t-angle-top collapse"
            v-if="factiveKey.includes(item.name)"
          ></i>
          <i class="t-icon t-angle-down collapse" v-else></i>
        </template>
      </a-collapse-panel>
    </a-collapse>
  </div>
</template>

<script lang="ts">
import {
  reactive,
  defineComponent,
  PropType,
  watch,
  ref,
  watchEffect,
  computed,
  toRaw,
  onUnmounted,
} from 'vue';
import {
  FileAddOutlined,
  FolderOutlined,
  FolderOpenOutlined,
  QuestionCircleOutlined,
} from '@ant-design/icons-vue';
import { useRouter } from 'vue-router';
import { message } from 'ant-design-vue';
import { t } from '@/i18n/i18n';
import axios from '@/http';
import { getCookie } from '@/services/cookie';
import { deepClone, LockState, Pen, Meta2d } from '@topometa2d/core';
import { isGif, Meta2dBackData,diagramData,diagramDataKeyArr,combinePenInit} from '../utils';
import {
  addImageUrl,
  upload,
  readFile,
  deleteImageComponent,
} from '@/services/file';
import { baseVer, convertPen } from '@/services/upgrade';
import { parseSvg } from '@topometa2d/svg';
import localforage from 'localforage';
import { normalShapeLocalName } from '../defaults';
import { checkData } from '../Meta2dMenu.vue';
import { useStore } from 'vuex';
import {User} from '@/services/user';
declare const meta2d: Meta2d;
export default defineComponent({
  name: 'SystemPanel',
  props: {
    list: {
      type: Array as PropType<any[]>,
      required: true,
    },
    type: {
      type: Number,
      required: true,
    },
    showList: {
      type: Boolean,
      required: true,
    },
    factiveKey: {
      type: Array as PropType<string[]>,
      required: false,
    },
    allPngCombineArr:{
      type: Array as PropType<string[]>,
      required: false,
    }
  },
  emits: ['changeUserList', 'openFolder', 'update:factiveKey'],
  components: {
    FileAddOutlined,
    FolderOutlined,
    FolderOpenOutlined,
    QuestionCircleOutlined,
  },
  setup(props, { emit }) {
    const store = useStore();
    const user = computed<User>(() => store.state.user.profile);
    const router = useRouter();
    //const activeKey = ref<any>([]);
    // const uploadUrl = '/api/image';
    const fileList = ref([]);
    const uploadName = ref('');
    const uploadHeaders = {
      Authorization: getCookie('token') || localStorage.getItem('token') ||'',
      // Authorization: Cookie.get(process.env.VUE_APP_TOKEN) || ''
    };
    const uploadParams = {
      randomName: 1,
      public: true,
    };
    let modifyItem = reactive<any>({});
    const onDrag = async (
      event: DragEvent,
      node: any,
      fn?: boolean,
      folderId?: string
    ) => {
      if (!props.type) {
        // 图纸返回
        return;
      }

      if (!node || !event.dataTransfer) {
        return;
      }

      // 存储到缓存中
      if (props.type === 1 && folderId !== normalShapeLocalName) {
        setOftenUseShapes(toRaw(node));
      }
      meta2d.canvas.addCaches = undefined;
      if (props.type === 2 && node.id && !node.componentDatas) {
        let ret: any = await axios.post(`/data/topo2d-components/get`, {
          id: node.id,
        });
        node.componentDatas = ret.componentDatas;
        node.fullname = ret.fullname;
        node.data = ret.data;
        node.component = true;
      }
      if (fn) {
        event.dataTransfer.setData(
          'Meta2d',
          JSON.stringify({
            name: node.fullname,
            width: 100,
            height: (100 * node.data.rect.height) / node.data.rect.width,
          })
        );
      } else if (node.component) {
        if (node.componentDatas) {
          // 新版本组件存在 componentDatas 里了
          // event.dataTransfer.setData(
          //   'Meta2d',
          //   JSON.stringify(node.componentDatas)
          // );
          meta2d.canvas.addCaches = deepClone(node.componentDatas);
        } else if(node.componentData) {
          // 老版本组件
          const pens = convertPen([node.componentData]);
          // event.dataTransfer.setData('Meta2d', JSON.stringify(pens));
          meta2d.canvas.addCaches = deepClone(pens);
        }
      } else if (!node.data && !node.componentData && node.image) {
        let target: any = event.target;
        target.children[0] && (target = target.children[0]);
        // firefox naturalWidth svg 图片 可能是 0
        const normalWidth = target.naturalWidth || target.width;
        const normalHeight = target.naturalHeight || target.height;
        const [name, lockedOnCombine] = isGif(node.image)
          ? ['gif', LockState.None]
          : ['image', undefined];
          console.log('这是node',node)
          event.dataTransfer.setData(
            'Meta2d',
            JSON.stringify({
              name,
              width: 100,
              height: 100 * (normalHeight / normalWidth),
              image: node.image,
              imageRatio: true,
              lockedOnCombine,
            } as Pen)
          );
        
      } else {
        event.dataTransfer.setData(
          'Meta2d',
          JSON.stringify(node.componentData || node.data)
        );
      }
      const pen = {
        name: 'text', // name 为上面表格中name
        text: '',
        x: -20,
        y: -20,
        width: 20,
        height: 20,
        tags: ['drag'],
      };
      meta2d.addPen(pen, false,false);
      
      meta2d.active([pen]);
      meta2d.canvas.initMovingPens(); //.movingPens = deepClone(meta2d.store.active, true);  //initMovingPens();
      meta2d.canvas.mouseDown = {
        x: -20,
        y: -20,
      };
      meta2d.canvas.initActiveRect = {
        x: -20,
        y: -20,
        ex: 0,
        ey: 0,
        height: 20,
        rotate: 0,
        width: 20,
      };
      if (!meta2d.canvas.dock) {
        meta2d.canvas.dock = { xDock: 0, yDock: 0 } as any;
        return;
      }
    };
    const drag = (e) => {
      if (!props.type) {
        // 图纸返回
        return;
      }
      if (!meta2d.canvas.movingPens) {
        return;
      }
      if (!meta2d.canvas.dock) {
        meta2d.canvas.dock = { xDock: 0, yDock: 0 } as any;
        return;
      }
      setTimeout(() => {
        let newE = {
          altKey: false,
          ctrlKey: false,
          shiftKey: false,
          x: e.clientX - 210,
          y: e.clientY - 60,
        };
        meta2d.canvas.movePens(newE);
      }, 1);
    };
    const dragEnd = (menu) => {
      if (!props.type) {
        // 图纸返回
        return;
      }
      meta2d.delete(meta2d.find('drag'), true, false);
      meta2d.canvas.movingPens = undefined;
      let newPen = meta2d.store.data.pens
        .filter((pen) => !pen.parentId)
        .slice(-1);
      meta2d.active(newPen);
      // 检测对应得信号图元添加对应事件--->查找对应图元名字data{图元名：{对应事件}}whitelist是白名单,
      if (diagramDataKeyArr.includes(newPen[0]?.name)) {
        newPen[0].events = deepClone(diagramData[newPen[0].name])
      }
      combineElement(menu,newPen)
    };
    function combineElement(menuItem,initPen){
      if(menuItem.combineCount>1){
        //拖拽自动组合
        combinePenInit(props.allPngCombineArr,menuItem.name,initPen[0])
        
      }
    }
    async function setOftenUseShapes(node: any) {
      let systemFolder = props.list.find((i) => i.id === normalShapeLocalName);
      if (!systemFolder) {
        return;
      }
      // 1. 读取缓存-常用图形
      const oftenUseShapes: any[] = systemFolder.list;
      // 2. 判断是否存在
      const index = oftenUseShapes.findIndex((item: any) => {
        if (node.id) {
          return item.id === node.id;
        } else {
          return item.name === node.name;
        }
      });
      if (index >= 0) {
        oftenUseShapes.splice(index, 1);
      }
      // 2. 添加到数据中
      oftenUseShapes.unshift(node);
      // 长度超出，踢出最后一个
      if (oftenUseShapes.length > 8) {
        oftenUseShapes.pop();
      }
      // 3. 存储到缓存中
      localforage.setItem(normalShapeLocalName, toRaw(oftenUseShapes));
    }
    
    const onTouchstart = async (
      event: TouchEvent | MouseEvent,
      node: any,
      fn?: boolean,
      folderId?: string
    ) => {
      if (!props.type) {
        // 图纸返回
        onOpen(node);
        return;
      }
      if (!node) {
        return;
      }
      if(node.data&&diagramDataKeyArr.includes(node.data.name)){
        node.data.events=diagramData[node.data.name]
      }
      // 存储到缓存中
      if (props.type === 1 && folderId !== normalShapeLocalName) {
        setOftenUseShapes(toRaw(node));
      }
      if (fn) {
        meta2d.canvas.addCaches = deepClone([
          {
            name: node.fullname,
            width: 100,
            height: (100 * node.data.rect.height) / node.data.rect.width,
          },
        ]);
      } else if (node.component) {
        if (node.componentDatas) {
          // 新版本组件存在 componentDatas 里了
          meta2d.canvas.addCaches = deepClone(node.componentDatas);
        } else {
          // 老版本组件
          const pens = convertPen([node.componentData]);
          meta2d.canvas.addCaches = deepClone(pens);
        }
      } else if (!node.data && !node.componentData && node.image) {
        let target: any = event.target;
        target.children[0] && (target = target.children[0]);
        const normalWidth = target.naturalWidth || target.width;
        const normalHeight = target.naturalHeight || target.height;
        const [name, lockedOnCombine] = isGif(node.image)
          ? ['gif', LockState.None]
          : ['image', undefined];
        meta2d.canvas.addCaches = deepClone([
          {
            name,
            width: 100,
            height: 100 * (normalHeight / normalWidth),
            image: node.image,
            imageRatio: true,
            lockedOnCombine,
          } as Pen,
        ]);
      } else {
        meta2d.canvas.addCaches = deepClone([node.componentData || node.data]);
      }
    };

    const dragstart = (event) => {
      (event.target as any).style.opacity = 0.5;
    };

    const dragend = (event) => {
      (event.target as any).style.opacity = 1;
    };
    //拖拽开始
    document.addEventListener('dragstart', dragstart, false);
    //拖拽结束
    document.addEventListener('dragend', dragend, false);

    onUnmounted(() => {
      document.removeEventListener('dragstart', dragstart);
      document.removeEventListener('dragend', dragend);
    });

    const customStyle =
      'background: #ffffff;border-radius: 4px;border: 0;overflow: hidden;';

    const removeName = (item: any, index: number) => {
      if (item.list?.length) {
        message.error(t('文件夹必须为空').toString(),1);
        return;
      }
      props.list.splice(index, 1);
      removeUserFolder({ name: item.name, index: index, id: item.id });
    };

    const deleteVisibleChange = (event, item) => {
      if (event) {
        if (!item.list?.length) {
          if (props.type === 2) {
            emit('openFolder', {
              type: props.type,
              folder: item.name,
            });
          }
        }
      }
    };

    const removeUserFolder = async (params: any) => {
      if (props.type == 0) {
        params.type = 'meta2d';
        meta2d.emit(
          't-meta2dFolder',
          props.list.map((i) => i.name)
        );
      } else if (props.type == 2) {
        params.type = 'user';
        meta2d.emit(
          't-userFolder',
          props.list.map((i) => i.name)
        );
      }
      //图纸删除数据库集合-->已更换新接口
      if (props.type === 0) {
        const folder: any = await axios.post('/directory/dir/delete', {
          id: params.id,
        });
        if (folder && folder.error) {
          message.error('删除文件夹失败：' + folder.error);
        }
      }
      //组件删除网盘文件夹
      if (props.type === 2) {
        let del_ret: any = await axios.post('/directory/dir/delete', {
          id: params.id,
        });
        if (del_ret && del_ret.error) {
          message.error('删除文件夹失败：' + del_ret.error);
        }
        //let del_ret: any = await axios.post('/directory/delete', {
        //  fullpaths: [`/2D/${params.name}`],
        //});
        //if (del_ret.deleted > 0) {
        //  message.success('删除文件夹成功');
        //}
      }

      // const folder: any = await axios.post('/api/user/folder/delete', params);
      // if (folder.error) {
      //   message.error('删除文件夹失败：' + folder.error);
      // }
    };

    const onAddMeta2d = (item: any, event: any) => {
      const categoryValue=user.value.categoryValue
      const query = {
        fileName:'未命名图纸',
        folder: item.name,
        categoryValue:categoryValue?categoryValue:'topo2dnocategorykey9999999999999',
        r: Date.now() + '',
        component: undefined,
        folderType: categoryValue?categoryValue:'topo2dnocategorykey9999999999999',
        visualizationEdit:false,
        headtitle:'未命名图纸',
      };
      if (props.type === 0) {
      } else if (props.type === 2) {
        query.component = true;
        query.categoryValue='topo2dnocategorykey9999999999999'
        query.folderType='topo2dnocategorykey9999999999999'
      }
      meta2d.emit('t-newFile', query);
    };
    const onOpen = (item: any) => {
      if(item.password){
        const location=window.location
        const newHref=location.origin+location.pathname+'?id='+item._id
        window.open(newHref,'_blank')
      }else{
        const query = {
          id: item._id,
          component: item.component,
        };
        meta2d.emit('t-newFile', query);
      }
    };

    const removeItem = async (item: any, index: number, indexItem: any) => {
      // if (props.type === 0) {
      //   props.list[index].list.splice(indexItem, 1);
      //   //删除图纸
      //   axios.delete(`/api/user/meta2d/${item.id}`);
      // } else if (props.type === 2) {
      //   props.list[index].list.splice(indexItem, 1);
      //   if (item.component) {
      //     // 删除组件
      //     axios.delete(`/api/user/meta2d/${item.id}`);
      //   } else {
      //     // 删除图片
      //     deleteImageComponent(item.image, item.folder);
      //   }
      // }
      let collection = '';
      if (props.type === 0) {
        collection = 'topo2d';
      } else if (props.type === 2) {
        collection = 'topo2d-components';
      }
      props.list![index].list.splice(indexItem, 1);
      //删除 图片online集合
      //删除 图纸
      //删除 缩略图片
      //更新当前 文件夹list
      if (item.onlineId) {
        await axios.post('/data/topo2d-onlines/delete', {
          id: item.onlineId,
        });
      }
      if (item._id) {
        await axios.post(`/data/${collection}/delete`, { id: item._id });
      }
      if (item.image.startsWith('/api/image/')) {
        await axios.delete(item.image);
      }
      // 原删除接口，图片删除无法进入
      // if (item.image.startsWith('/image/')) {
      //   await axios.delete(item.image);
      // }
      if (props.type !== 2&&props.list![index].id) {
        await axios.post('/directory/dir/list', {
          fullpath:"/2D",
          username:localStorage.getItem('username'),
          categoryValue: user.value.categoryValue
        });
      }
    };

    function addBlankImageUrl(url: string, folder: string) {
      this.images.push({ image: url });
    }
    const imageChange = async (info: any, folder: string) => {
      message.loading({
        content: t('图片上传中...').toString(),
        key: 'uploadingImage',
      });
      if (info.file.status === 'done') {
        const { url, pens, error } = info.file.response;
        if (url && !pens) {
          message.success({
            content: t('图片上传成功！').toString(),
            key: 'uploadingImage',
            duration: 3,
          });

          emit('changeUserList', info.file.response.url, folder);
          //空图片
          // await addImageUrl(info.file.response.url, folder);
          // await addBlankImageUrl(info.file.response.url,'未分类');
        } else if (pens) {
          message.success({
            content: t('svg 文件上传成功').toString(),
            key: 'uploadingImage',
            duration: 3,
          });
          const data: Meta2dBackData = meta2d.data();
          checkData(data);
          data.folder = folder;
          data.image = url;
          data.component = true;
          data.componentDatas = pens;
          data.name = `meta2d.${new Date().toLocaleString()}`;
          data.version = baseVer;
          await axios.post('/data/topo2d-components/add', data); // 新增
          //保存组件逻辑
          /* TODO 复用图片文件夹，不再单独建立文件夹关系
          let list = undefined;
          let _folder: any = await axios.post('/data/folders/get', {
            query: {
              type: 'topo2d-components',
              name: data.folder,
            },
          });
          if (_folder.error) {
            return;
          }

          list = _folder.list;
          let folderId = _folder._id;
          if (!list) {
            list = [];
          }
          list.push({
            id: ret._id,
            image: url,
            name: data.name,
            component: data.component,
          });

          await axios.post('/data/folders/update', { _id: folderId, list });
          */
          // 保存成功，重新请求文件夹
          meta2d.emit('t-save-success', true);
        } else if (error) {
          message.error({
            content: t(error),
            key: 'uploadingImage',
            duration: 2,
          });
        }
      } else if (info.file.status === 'error') {
        message.error({
          content: t('图片上传失败，请稍后重试！'),
          key: 'uploadingImage',
          duration: 2,
        });
      }
    };

    const onShowRename = (item: any) => {
      if (props.type === 1) {
        return;
      }
      if (item.name === '未分类') {
        return;
      }
      props.list.forEach((i: any) => {
        i.showRename = false;
      });
      item.oldName = item.name;
      item.showRename = true;
      modifyItem.name = item.name;
      modifyItem.oldName = item.name;
    };

    const onRename = (item: any, esc?: boolean) => {
      if (esc) {
        item.showRename = false;
        return;
      }

      if (item.showRename) {
        const folder = props.list.filter((n) => n.name === modifyItem.name);
        if (folder && folder.length >= 1 && folder[0].oldName !== item.name) {
          // 本身和其他，说明存在重复
          item.name = item.oldName;
          item.showRename = false;
          message.error(t('文件夹已经存在').toString());
          return;
        }
        if (modifyItem.name.length === 0) {
          item.name = item.oldName;
          item.showRename = false;
          message.error(t('文件夹不能为空').toString());
          return;
        }

        item.showRename = false;
        item.name = modifyItem.name;
        // 修改文件名称后台处理
        modifyItem.id = item.id;
        renameUserFolder(modifyItem);
        renameUserFolderImage(modifyItem)
        // modifyDrawingFolder(item);//原本我的组件部分修改文件夹每图纸发一次请求
        return;
      }
    };
    //TODO 修改文件夹名，对应图纸文件夹需要修改
    const renameUserFolder = async (params: any) => {
      if (props.type === 0) {
        // params.type = 'meta2d';
        meta2d.emit(
          't-meta2dFolder',
          props.list.map((i) => i.name)
        );
      } else if (props.type === 2) {
        // params.type = 'user';
        meta2d.emit(
          't-userFolder',
          props.list.map((i) => i.name)
        );
      }
      // const folder: any = await axios.post('/api/user/folder', params);
      // if (folder.error) {
      //   message.error('修改文件夹失败：' + folder.error);
      // }
      // let prop = props.list.find((prop) => prop.id == params.id);
      // console.log("find", prop);

      // prop.list.forEach((item) => item.name = params.name);
      if (props.type === 0) {
        const ret: any = await axios.post('/directory/dir/update', {
          // _id: params.id,
          // name: params.name,
          fullpath:'/2d',
          newFullpath: params.name,
          oldFullpath: params.oldName,//旧文件名
          username:localStorage.getItem('username'),
          categoryValue: (meta2d.data() as any).categoryValue
        });
        if (ret.error) {
          message.error('修改文件夹失败：' + ret.error);
        }else{
          // 当更改得文件夹和当前图纸所在文件夹相同
          if(meta2d.store.data['folder'] ===params.oldName){
            meta2d.store.data['folder']= params.name//同步修改
            meta2d.emit('changeFolder', {value:params.name,keys:['folder',undefined]})//修改名字
          }
        }
      } else if (props.type === 2) {
        //TODO 修改文件名 需要修改该文件夹下所有图纸folder,云盘中的图片也要相应进行移动
        const ret_dir: any = await axios.post('/directory/components/update', { //修改
          fullpath: '/2D',
          newFullpath: params.name,
          oldFullpath: params.oldName,//旧文件名
          username:localStorage.getItem('username'),
          categoryValue: `topo2dnocategorykey9999999999999`
        });
        if (!ret_dir) {
          message.error('修改文件夹失败');
          return;
        }
      }
    };
     const renameUserFolderImage = async (params: any) => {
    if (props.type === 2) {
        //TODO 修改文件名 需要修改该文件夹下所有图纸folder,云盘中的图片也要相应进行移动
        const ret_dir: any = await axios.post('/image/minio/directory/update', { //修改
          newFullpath:  '/2D/'+params.name,
          oldFullpath: '/2D/'+ params.oldName,//旧文件名
          username: localStorage.getItem('username')
        });
        if (!ret_dir) {
          message.error('修改文件夹失败');
          return;
        }
      }
    };

    // const modifyDrawingFolder = async (item: any) => {
    //   let collection = props.type===2 ? 'topo2d-components' : 'topo2d';
      
    //   item.list?.forEach(async (_item) => {
    //     if (_item.id) {
    //       await axios.post(`/data/${collection}/update`, {
    //         _id: _item.id,
    //         folder: item.name
    //       });
    //     }
    //   })
    // }

    const activeKeyOnChanged = (e) => {
      // props.factiveKey = e
      // activeKey.value = e;
      emit('update:factiveKey', e);
    };

    watch(
      () => props.factiveKey,
      (newV: string[], oldV: string[]) => {
        // newV 中有， oldV 中没有的，即新打开的
        // if (props.type !== 2) {
        //   return;
        // }
        const newOpen = [];
        for (const n of newV) {
          !oldV.includes(n) && newOpen.push(n);
        }
        if (!newOpen[0]) {
          return;
        }
        // 每次默认一个
        const item = props.list.find((item) => item.name === newOpen[0]);
        switch (props.type) {
          // 当打开的是图纸
          case 0:
            if (
              !item.list ||
              item.list.length === 0
            ) {
              emit('openFolder', {
                type: props.type,
                folder: newOpen[0],
              });
            }
            break;
          case 2:
            if (
              !item.list ||
              item.list.length === 0 ||
              item.list.every(
                // component === undefined 说明是图片
                (item: any) =>
                  item.component === undefined &&
                  !item.componentDatas &&
                  !item.componentData &&
                  !item.data
              ) // 全部是图片，仍然需要去查询
            ) {
              emit('openFolder', {
                type: props.type,
                folder: newOpen[0],
              });
            }
            break;
          default:
            break;
        }
      }
    );
    async function uploadImage(e: any) {
      const name: string = e.file.name;
      let uploadRes: any;
      // uploadRes 中的 url 作为 svg 的缩略图
      if (name.endsWith('.svg')) {
        uploadRes = await upload(e.file, e.data.public, e.file.name, e.data.directory);
        // svg 解析成组件
        const text = await readFile(e.file);
        const pens = parseSvg(text);
        e.onSuccess({
          ...uploadRes,
          pens,
        });
      } else {
        uploadRes = await upload(
          e.file,
          e.data.public,
          e.file.name,
          e.data.directory
        );
        e.onSuccess(uploadRes);
      }
    }
    return {
      // activeKey,
      // list,
      onDrag,
      onTouchstart,
      customStyle,
      removeName,
      deleteVisibleChange,
      onAddMeta2d,
      onOpen,
      removeItem,
      // uploadUrl,
      fileList,
      uploadName,
      uploadHeaders,
      uploadParams,
      imageChange,
      onRename,
      onShowRename,
      modifyItem,
      maxWidth: computed(() =>
        props.type === 2 ? '52px' : props.type === 1 ? '84px' : '80px'
      ),
      activeKeyOnChanged,
      uploadImage,
      drag,
      dragEnd,
    };
  },
});
</script>
<style lang="scss" scoped>
@import '@/styles/variables.scss';
.content {
  margin-bottom: 40px;
  &:deep(.ant-collapse) {
    background: #181a24;
    .ant-collapse-item {
      background: #181a24!important;
      font-size: 12px;
      .ant-collapse-header {
        text-overflow: ellipsis;
        padding: 4px 16px;
        padding-left: 40px;
        color:#a5a8b2;

        .ant-collapse-extra {
          display: flex;

          margin-top: 4px;
          .add {
            display: none;
            color: #1890ff;
          }

          .delete {
            //  pointer-events:none;
            display: none;
          }
          .upload-image {
            display: none;
            color: #1890ff;
          }
        }
        .modify-forder-input {
          padding-left: 2px;
          flex-grow: 1;
          overflow: hidden;
          width: 100px;
          height: 20px;
          &:hover {
            width: 50px;
          }
        }
        &:hover {
          color: #1890ff;
          cursor: pointer;
          // text-overflow: ellipsis;
          .ant-collapse-extra {
            .add {
              position: absolute;
              display: block;
              right: 54px;
            }

            .delete {
              position: absolute;
              display: block;
              right: 32px;
            }
            .upload-image {
              // position: absolute;
              display: inline-block;
              margin-left: -60px;
              // line-height: 46px;
              // right: 76px;
              // top: 4px;
              // margin-right: 104px;
              // z-index: 2;
            }
            .collapse {
            }
          }
          .modify-forder-input {
            width: 50px;
          }
        }

        &:hover {
          span:nth-child(2) {
            // span:first-child {
            max-width: v-bind('maxWidth') !important;
            // }
          }
        }
      }

      .ant-collapse-content {
        .thumbs {
          display: flex;
          flex-wrap: wrap;
          margin-left: 8px;

          .thumb {
            width: 25%;

            & > div {
              width: 100%;
            }

            .icon-size {
              font-size: 25px !important;
              width: 25px;
              color: #a5a8b2;
            }

            img {
              max-width: 25px;
              max-height: 25px;
              margin: 4px;
            }

            &:hover img {
              background: rgba(255,255,255,0.2);
            }
          }
          .thumbunone {
            margin-top: 8px;
            width: 48%;
            cursor: pointer;

            & > div {
              width: 100%;
            }

            .image {
              padding: 5px;
              border-radius: 2px;
              border: 1px solid #f4f4f4;
            }

            img {
              height: 64px;
            }

            &:hover {
              .image {
                border-color: $color-primary;
              }

              .t-gongyong--bianji {
                display: block;
              }

              .t-shanchu {
                display: block;
              }
              .isLockPass {
                display: block;
              }
            }

            .text {
              width: 100%;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
              margin: 4px 0;
            }

            .t-gongyong--bianji {
              position: absolute;
              right: 24px;
              top: 4px;
              display: none;
              color: $color-primary;
              cursor: pointer;
            }

            .t-shanchu {
              position: absolute;
              right: 4px;
              top: 4px;
              display: none;
              color: $color-primary;
              cursor: pointer;
            }
            .isLockPass{
              position: absolute;
              right: 24px;
              top: 4px;
              display: none;
              color: $color-primary;
              cursor: pointer;
            }
          }

          .thumbunone:nth-child(2n+1){
            margin-right: 2px;
          }
          .thumbunone:nth-child(2n){
            margin-left: 2px;
          }

          .list {
            width: 100%;
            margin: 4px 0;
            & > div {
              display: flex;
              align-items: center;
              width: 100%;
              text-align: left;
            }
            img {
              width: 16px;
              height: 16px;
              margin-right: 4px;
            }

            .text {
              width: 100%;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
              line-height: 1;
            }
          }
        }
      }
    }
    .ant-collapse-item:first-child {
      .ant-collapse-header {
        margin-top: 8px;
      }
    }
  }
}
:deep(.ant-collapse-content-box){
  color: #b4b7c1;
}


</style>
<style lang="scss">
/*确认删除弹窗*/
.screen-gantt-popover{
  .ant-popover-content {
    .ant-popover-inner {
      background: #3d404d;
      .ant-popover-message-title {
        color: #fff;
      }
      .ant-popover-buttons {
        display: flex;
      }
    }
  }
  &.ant-popover-placement-bottom,
  &.ant-popover-placement-bottomLeft,
  &.ant-popover-placement-bottomRight {
    .ant-popover-content {
      .ant-popover-arrow {
        border-top-color: #3d404d;
        border-left-color: #3d404d;
      }
    }
  }
  &.ant-popover-placement-top,
  &.ant-popover-placement-topLeft,
  &.ant-popover-placement-topRight {
    .ant-popover-content {
      .ant-popover-arrow {
        border-right-color: #3d404d;
        border-bottom-color: #3d404d;
      }
    }
  }
}
</style>
