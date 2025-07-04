<template>
  <div class="dialogClass" ref="dialogBox">


  <a-drawer
  :getContainer="()=>$refs.dialogBox"
    :title="t('选择图片')"
    placement="right"
    :closable="false"
    :visible="visible"
    @close="close"
    wrapClassName="drawBox"
  >
    <div class="buttons">
      <a-upload
        v-if="uploadUrl"
        :action="uploadUrl"
        :data="uploadParams"
        :headers="uploadHeaders"
        listType="picture"
        :showUploadList="false"
        @change="imageChange"
      >
        <a-button type="primary">{{ t('上传图片') }}</a-button>
      </a-upload>

      <a-button type="primary" @click="modalVisible = true">{{
        t('在线图片')
      }}</a-button>
    </div>
    <div class="images">
      <div class="imageItem" v-for="(item, index) in images" :key="item.id">
        <img
          :src="item.image"
          :draggable="true"
          @click="onSelectImage(item.image)"
          @touchstart.passive="onSelectImage(item.image)"
        />
        <a-popconfirm
          title="确认删除该图片?"
          ok-text="确认"
          cancel-text="取消"
          @confirm.stop="onDelImg(item, index)"
          :getPopupContainer="
              (triggerNode) => {
                return triggerNode.parentNode;
              }
            "
            @cancel.stop=""
        >
          <i class="t-icon t-close"></i>
        </a-popconfirm>
        <!-- <i
          class="t-icon t-close"
          @click.stop="onDelImg(item.image, index)"
          :title="t('从图库中移除，但不删除图片')"
        ></i> -->
      </div>
    </div>
  </a-drawer>
  <a-modal
  :getContainer="()=>$refs.dialogBox"
    wrapClassName="onlineImgModal"
    v-model:visible="modalVisible"
    :title="t('图片URL')"
    :cancelText="t('取消')"
    :okText="t('确定')"
    @ok="addImgUrl"
  >
    <a-input v-model:value="imgUrl" />
  </a-modal>
</div>
</template>
<script lang="ts">
import axios from '@/http';
import { t } from '@/i18n/i18n';
import { getCookie } from '@/services/cookie';
import { addImageUrl, deleteImageComponent, delImage } from '@/services/file';
import { isLogin, noLoginTip } from '@/services/user';
import { Meta2d } from '@topometa2d/core';
import { message } from 'ant-design-vue';
import {
  computed,
  defineComponent,
  onMounted,
  onUnmounted,
  ref,
  watch,
} from 'vue';
import { useStore } from 'vuex';
declare const meta2d: Meta2d;
export default defineComponent({
  props: {
    visible: Boolean,
  },
  emits: ['chooseImage', 'update:visible'],
  setup(props, { emit }) {
    function close() {
      emit('update:visible', false);
    }

    const store = useStore();
    const user = computed(() => store.state.user.profile);

    const images = ref([]);

    onMounted(() => {
      // meta2d.on('t-userImages', getImages);
    });

    onUnmounted(() => {
      // meta2d.off('t-userImages', getImages);
    });

    // function getImages(resImages: any[]) {
    //   images.value = resImages;
    // }

    watch(
      () => props.visible,
      (newV: boolean) => {
        if (newV) {
          // getImages();
          images.value = [];
          getCloudImages();
          getImages();
        }
      }
    );

    async function getCloudImages() {
      const ret: { list: any[]; total: number } = await axios.post(
        '/file/minio/list?username='  + localStorage.getItem('username'),
        {
          type: '图片',
          directory: `/2D/未分类`,
          //directory: `/2D`,
          //allChildren: true,
        },
        {
          params: {
            current: 1,
            pageSize: 100,
          },
        }
      );
      let showList = [];
      if (!ret || !ret.list) {
        return [];
      }
      ret.list.forEach((item) => {
        if (item.metadata.directory.indexOf('/2D/缩略图') === -1) {
          showList.push({
            image: '/api/image/minio/' + item.filename,
          });
        }
      });
      images.value = images.value.concat(showList);
    }

    async function getImages() {
      const ret: any = await axios.post('/data/topo2d-onlines/list?username=' + localStorage.getItem('username')+'&categoryValue=topo2dnocategorykey9999999999999', {
        query: {
          type: '2D',
        },
      });
      if (!ret || !ret.list) {
        return [];
      }
      let onlineList = ret.list.map((item: any) => {
        return {
          id: item._id,
          folderId: item.folderId,
          image: item.image,
          // hasFolder: item.hasFolder,
        };
      });
      images.value = images.value.concat(onlineList);
      // const res:any = await axios.post('/data/folders/get', { query: {
      //         type: '2d-component',
      //         name: "Uncategorized(未分类)",
      // },
      // });
      // console.log("res", res);
      // images.value = res.list.map((item: any) => { return { id: item.id, image: item.image } });
    }

    function onSelectImage(image: string) {
      emit('chooseImage', image);
      close();
    }

    async function onDelImg(item: any, i: number) {
      // images.value.splice(i, 1);
      // delImage(image);
      // deleteImageComponent(image);
      // if (item.folderId) {
      //   //存在文件夹下
      //   //folderId
      //   let folder: any = await axios.post(`/data/folders/get`, {
      //     id: item.folderId,
      //   });
      //   let list = folder.list;
      //   let index = list.findIndex((_item) => _item.onlineId === item.id);
      //   if (index !== -1) {
      //     list.splice(index, 1);
      //   }
      //   await axios.post('/data/folders/update', {
      //     _id: item.folderId,
      //     list: list,
      //   });
      // }
      if (item.image.startsWith('/api/image/')||item.image.startsWith('/file/')) {
        await axios.delete(item.image);
      } else {
        await axios.post('/data/topo2d-onlines/delete', {
          id: item.id,
        });
      }
      images.value.splice(i, 1);
      meta2d.emit('t-save-success', true);
    }

    // 上传操作  TODO: 待抽取
    const uploadUrl = '/api/image/minio/upload';
    const uploadHeaders = {
      Authorization: getCookie('token') || localStorage.getItem('token') || '',
    };
    const uploadParams = {
      randomName: 1,
      public: true,
      tags: '2D',
      directory: '/2D/未分类',
      shared: true,
      type: '图片',
    };

    async function imageChange(info: any) {
      message.loading({
        content: t('图片上传中...').toString(),
        key: 'uploadingImage',
      });
      if (info.file.status === 'done') {
        message.success({
          content: t('图片上传成功！').toString(),
          key: 'uploadingImage',
          duration: 3,
        });
        // 上传图片
        images.value.push({ image: info.file.response.url, onlines: false });
        meta2d.emit('t-save-success', true);
        // addUserImageUrl(info.file.response.url);
        emit('chooseImage', info.file.response.url);
      } else if (info.file.status === 'error') {
        let content = '';
        if (!isLogin(user.value)) {
          content = noLoginTip;
        } else {
          content = t('图片上传失败！').toString();
        }
        message.error({
          content,
          key: 'uploadingImage',
          duration: 2,
        });
      }
    }

    async function addUserImageUrl(url: string) {
      if (!isLogin(user.value)) {
        message.warn(noLoginTip);
        return;
      }
      //添加到topo2d-onlines
      const imgRet: any = await axios.post('/data/topo2d-onlines/add', {
        image: url,
        // hasFolder: false,
        // onlines: true,
        folder: '未分类',
        type: '2D',
        categoryValue: 'topo2dnocategorykey9999999999999'
      });
      if (imgRet.error) {
        return;
      }
      images.value.push({
        image: url,
        id: imgRet._id,
        // hasFolder: false,
        // onlines: true,
      });
      meta2d.emit('t-save-success', true);
      emit('chooseImage', url);
      //该用户的图片列表
    }

    const modalVisible = ref(false);
    const imgUrl = ref('');

    function addImgUrl() {
      if (!imgUrl.value) {
        message.error(t('请填写URL').toString());
        return;
      }
      modalVisible.value = false;
      addUserImageUrl(imgUrl.value);
    }

    return {
      close,
      images,
      onSelectImage,
      onDelImg,
      uploadUrl,
      uploadHeaders,
      uploadParams,
      imageChange,
      modalVisible,
      imgUrl,
      addImgUrl,
    };
  },
});
</script>

<style lang="scss" scoped>
.buttons {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}
.images {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 16px;
  .imageItem {
    position: relative;

    &:hover {
      i {
        display: block;
      }
    }

    img {
      width: 100%;
      height: 100%;
    }

    i {
      position: absolute;
      right: 3px;
      top: 3px;
      display: none;
    }
  }
}
// 下拉框样式
.selectStyle {
  :deep(.ant-select-dropdown) {
    background-color: #2e2f38 !important;
    color: #fff !important;
  }
  :deep(.rc-virtual-list-holder) {
    background-color: #2e2f38 !important;
    color: #fff !important;
  }
}
:deep(.ant-popover-content) {
  .ant-popover-inner {
    background: #3d404d;
    .ant-popover-message-title {
      color: #fff;
      white-space: nowrap;
    }
    .ant-popover-buttons {
      display: flex;
      justify-content: flex-end;
    }
  }
}
:deep(.ant-popover-placement-bottom),
:deep(.ant-popover-placement-bottomLeft),
:deep(.ant-popover-placement-bottomRight) {
  .ant-popover-content {
    .ant-popover-arrow {
      border-top-color: #3d404d;
      border-left-color: #3d404d;
    }
  }
}
:deep(.ant-popover-placement-top),
:deep(.ant-popover-placement-topLeft),
:deep(.ant-popover-placement-topRight) {
  .ant-popover-content {
    .ant-popover-arrow {
      border-right-color: #3d404d;
      border-bottom-color: #3d404d;
    }
  }
}
//弹框样式
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
      .ant-form-item-label{
        > label{
          color: #b4b7c1;
        }
      }
      .ant-select-disabled.ant-select:not(.ant-select-customize-input) .ant-select-selector{
        background: #181a24;
      }
      .ant-select-disabled .ant-select-arrow{
        color: #b4b7c1;
      }
    }
  }
}
</style>
