<template>
  <a-tabs v-model:activeKey="activeKey" size="small" 
  type="card"
  class="PropsTabs Meta2dRightCard">
    <a-tab-pane key="属性">
      <template #tab>
        <span class="tabText">
          <i class="icon-zt icon-zt-waiguan"></i>
          {{ t('属性') }}
        </span>
      </template>
      <PensExterior />
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
import { defineComponent, inject, Ref, ref, watchEffect, watch } from 'vue';
import PensExterior from './Exterior.vue';
// import Tree from '../common/Tree.vue';
import { Pen, Meta2d } from '@topometa2d/core';
import Layout from '../common/Layout.vue';
import { format, TreeItem } from '../utils';
import Structure from '../common/Structure.vue';

declare const meta2d: Meta2d;
export default defineComponent({
  name: 'PensProps',
  components: { PensExterior, Layout, Structure },
  setup(props, { emit }) {
    const activeKey = ref('属性');
    watch(
      () => activeKey.value,
      (newV) => {
        emit('updateHidden', { value: newV, key: 'Pens' });
      }
    );
    // const activePens: Ref<Pen[]> = inject('activePens');

    // const items = ref<TreeItem[]>([]);
    // watchEffect(() => {
    //   items.value = format(activePens.value);
    // });

    return {
      activeKey,
      // items
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
        width:33.3%;
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
