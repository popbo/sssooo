<template>
  <a-tabs
    type="card"
    v-model:activeKey="activeKey" size="small" class="PropsTabs Meta2dRightCard">
    <a-tab-pane key="属性">
      <template #tab>
        <span class="tabText">
          <i class="icon-zt icon-zt-waiguan"></i>
          {{ t('属性') }}
        </span>
      </template>
      <PenExterior />
    </a-tab-pane>
    <a-tab-pane key="事件">
      <template #tab>
        <span class="tabText">
          <i class="icon-zt icon-zt-shijian"></i>
          {{ t('事件') }}
        </span>
      </template>
      <PenEvent />
    </a-tab-pane>
    <a-tab-pane key="动效">
      <template #tab>
        <span class="tabText">
          <i class="icon-zt icon-zt-dongxiao"></i>
          {{ t('动效') }}
        </span>
      </template>
      <PenAnimate />
    </a-tab-pane>
    <a-tab-pane key="数据">
      <template #tab>
        <span class="tabText">
          <i class="icon-zt icon-zt-shuju"></i>
          {{ t('数据') }}
        </span>
      </template>
      <PenData />
    </a-tab-pane>
    <a-tab-pane key="结构">
      <template #tab>
        <span class="tabText">
          <i class="icon-zt icon-zt-jiegou"></i>
          {{ t('结构') }}
        </span>
      </template>
      <!-- <Tree :items="items" /> -->
      <!-- <Structure/> -->
    </a-tab-pane>
  </a-tabs>
</template>

<script lang="ts">
import {
  computed,
  defineComponent,
  inject,
  onMounted,
  Ref,
  ref,
  watch,
  watchEffect
} from 'vue';
import PenExterior from './Exterior.vue';
import PenEvent from './Event.vue';
import PenAnimate from './Animate.vue';
import PenData from './Data.vue';
// import Tree from '../common/Tree.vue';
import { Pen } from '@topometa2d/core';
import { format, TreeItem } from '../utils';
import Structure from '../common/Structure.vue';

export default defineComponent({
  name: 'PenProps',
  components: { PenExterior, PenEvent, PenAnimate, PenData, Structure },
  setup(props, { emit }) {
    const activeKey = ref('属性');

    // const activePens: Ref<Pen[]> = inject('activePens');

    // const items = ref<TreeItem[]>([]);
    // watchEffect(() => {
    //   items.value = format(activePens.value);
    // });
    watch(
      () => activeKey.value,
      (newV) => {
        emit('updateHidden', { key: 'Pen', value: newV });
      }
    );
    return {
      activeKey,
      // items
    };
  }
});
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';
.PropsTabs{
  background: #232630;
}
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
        width:20%;
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
