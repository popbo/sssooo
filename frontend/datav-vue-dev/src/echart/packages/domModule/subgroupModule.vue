<template>
  <div>
    <template v-for="item in nav">
      <div v-if="!item.children" :key="item.index">
        <component
          v-if="!item.display"
          :key="item.index"
          :ref="common.NAME + item.index"
          :id="common.NAME + item.index"
          :is="common.COMPNAME + item.component.name"
          v-bind="item"
          :data-formatter="getFunction(item.dataFormatter)"
          :click-formatter="getFunction(item.clickFormatter, true)"
          :dbl-click-formatter="getFunction(item.dblClickFormatter, true)"
          :echart-formatter="getFunction(item.echartFormatter)"
          :label-formatter="getFunction(item.labelFormatter)"
          :styles-formatter="getFunction(item.stylesFormatter)"
          :formatter="getFunction(item.formatter)"
          :data-query="getFunction(item.dataQuery)"
          :data-header="getFunction(item.dataHeader)"
          :websocket-header="getFunction(item.websocketHeader)"
          :websocket-query="getFunction(item.websocketQuery)"
          :width="item.component.width"
          :height="item.component.height"
          :disabled="!contain.menuFlag"
          :scale="container.stepScale"
          style="position: absolute"
          :style="{
            top: `${item.top}px`,
            left: `${item.left}px`,
            width: `${item.component.width}px`,
            height: `${item.component.height}px`,
            'z-index': `${item.zIndex}`,
        }"
        />
      </div>
      <subgroupModule :key="item.index" v-if="item.children" :nav="item.children"></subgroupModule>
    </template>
  </div>
</template>
<script>
import components from '@/components/'
import { getFunction } from '@/utils/utils.min.js';
import subgroupModule from './subgroupModule.vue'
import common from '@/config';
// import echartComponents from '../../../echart/' 
// import Vue from 'vue'
export default {
  name:'subgroupModule',
  inject: ['contain', 'container'],
  provide() {
    return {
      contain: this.contain,
      container: this.container,
    };
  },
  props:{
    nav: {
      type: Array,
      default: () => {
        return [];
      },
    },
  },
  data(){
    return {
      common: common,
      getFunction: getFunction,
    }
  },
  components: {
    components,
    subgroupModule
  },
  created(){
    // Object.keys(echartComponents).forEach((ele) => {
    //   let component = echartComponents[ele]
    //   Vue.component(component.name, component)
    // })
  },
}
</script>
