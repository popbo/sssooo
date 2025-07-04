<template>
  <a-tabs
  type="card"
  v-model:activeKey="activeKey" size="small" class="PropsTabs Meta2dRightCard">
    <a-tab-pane key="图纸">
      <template #tab>
        <span class="tabText">
          <i class="icon-zt icon-zt-zujian"></i>
          {{ t(firstTabTitle) }}
        </span>
      </template>
      <FilePropsSettings />
    </a-tab-pane>
    <a-tab-pane key="通信">
      <template #tab>
        <span class="tabText">
          <i class="icon-zt icon-zt-tongxin"></i>
          {{ t('通信') }}
        </span>
      </template>
      <Communication />
    </a-tab-pane>
    <a-tab-pane key="布局">
      <template #tab>
        <span class="tabText">
          <i class="icon-zt icon-zt-buju"></i>
          {{ t('布局') }}
        </span>
      </template>
      <Layout />
    </a-tab-pane>
    <a-tab-pane key="结构">
      <template #tab>
        <span class="tabText">
          <i class="icon-zt icon-zt-jiegou"></i>
          {{ t('结构') }}
        </span>
      </template>
      <!-- <Tree :items="items" /> -->
      <!-- <Structure /> -->
    </a-tab-pane>
  </a-tabs>
</template>

<script lang="ts">
import {
  defineComponent,
  onMounted,
  onUnmounted,
  ref,
  watch,
  onDeactivated,
} from 'vue';
import FilePropsSettings from './FilePropsSettings.vue';
// import Tree from '../common/Tree.vue';
import { Pen, Meta2d } from '@topometa2d/core';
import Layout from '../common/Layout.vue';
import Communication from './Communication.vue';
import { format, Meta2dBackData, TreeItem } from '../utils';
import Structure from '../common/Structure.vue';
declare const meta2d: Meta2d;
export default defineComponent({
  name: 'FileProps',
  components: { FilePropsSettings, Layout, Communication, Structure },
  emits: ['updateHidden'],
  setup(props, { emit }) {
    const activeKey = ref('图纸');
    watch(
      () => activeKey.value,
      (newV) => {
        emit('updateHidden', { key: 'Canvas', value: newV });
      }
    );

    // const items = ref<TreeItem[]>([]);
    // onMounted(() => {
    //   meta2d.on('inactive', getTree);
    //   meta2d.on('opened', getTree);
    //   meta2d.on('add', getTree);
    //   meta2d.on('undo', getTree);
    //   meta2d.on('redo', getTree);
    //   meta2d.on('delete', getTree);
    // });

    // function getTree() {
    //   const outerPens = meta2d.store.data.pens.filter(pen => !pen.parentId);
    //   items.value = format(outerPens);
    //   items.value = items.value.reverse();
    // }

    // onUnmounted(() => {
    //   meta2d.off('inactive', getTree);
    //   meta2d.off('opened', getTree);
    //   meta2d.off('add', getTree);
    //   meta2d.off('undo', getTree);
    //   meta2d.off('redo', getTree);
    //   meta2d.off('delete', getTree);
    // });

    const firstTabTitle = ref('图纸');
    onMounted(() => {
      meta2d.on('opened', open);
    });

    function open() {
      const component = (meta2d.store.data as Meta2dBackData).component;
      firstTabTitle.value = component ? '组件' : '图纸';
    }

    onUnmounted(() => {
      meta2d.off('opened', open);
    });

    return {
      activeKey,
      // items,
      firstTabTitle,
    };
  },
});
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';
.Meta2dRightCard{
  color: #fff;
  &.PropsTabs{
    background: #232630;
  }
  :deep(.ant-tabs-bar){
    .ant-tabs-nav-container{
      height: 45px;
    }
    .ant-tabs-nav-wrap{
      .ant-tabs-nav-scroll{
        background: #3d404d;
        height:45px;
      }
      .ant-tabs-nav{
        width:100%;
        //display: flex;
        //justify-content: space-between;
      }
      .ant-tabs-tab{
        margin-left: 0;
        background: #3d404d;
        padding:0 12px;
        border:none;
        border-top: 2px solid #3d404d;
        height:45px;
        line-height: 43px;
        width:25%;
        text-align: center;
        &.ant-tabs-tab-active{
          background: #232630;
          border-top:2px solid #409eff;
          .tabText{
            color: #fff;
          }
        }
      }
      
    }
  }
  :deep(.ant-tabs-tabpane){
    &.ant-tabs-tabpane-inactive{
      height:0
    }
  }
  :deep(.ant-form-item-control-input){
    .ant-btn{
      width: 34px;
      height: 28px;
      background: #303640;
      border:1px solid #454851;
      color: #b4b7c1;
    }
    
  }
}
</style>
