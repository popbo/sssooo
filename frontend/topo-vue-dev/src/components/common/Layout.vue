<template>
  <a-collapse
    v-model:activeKey="activeKey"
    expand-icon-position="left"
    class="PenProps"
  >
    <a-collapse-panel key="布局" :header="t('布局')">
      <FormEvery :configs="layoutConfigs" :obj="data" />
      <div class="mt8">
        <a-button type="primary" class="btn" @click="onLayout">{{
          t('开始排版')
        }}</a-button>
      </div>
    </a-collapse-panel>
    <a-collapse-panel key="窗口大小" :header="t('窗口大小')">
      <FormEvery :configs="fillConfigs" :obj="meta2dInfo"  @change-value="changeValue($event)"/>
    </a-collapse-panel>
    <a-collapse-panel key="全局" :header="t('全局')">
      <FormEvery :configs="moveScaleConfigs" :obj="meta2dInfo"
      @change-value="changeValue"/>
    </a-collapse-panel>
  </a-collapse>
</template>

<script lang="ts">
import { t } from '@/i18n/i18n';
import {
  computed,
  defineComponent,
  inject,
  nextTick,
  onMounted,
  onUnmounted,
  PropType,
  reactive,
  Ref,
  ref,
  toRaw,
  watch,
} from 'vue';
import FormEvery from './FormEvery.vue';
import { Pen, Meta2d } from '@topometa2d/core';
import { FormItemType,Meta2dBackData } from '../utils';

declare const meta2d: Meta2d;
export default defineComponent({
  name: 'Layout',
  components: {
    FormEvery,
  },
  setup() {
    const activeKey = ref(['布局','窗口大小','全局']);
    onMounted(() => {
      
      // 不使用同一个对象，避免直接对核心库数据发生改变
      if(Object.hasOwn(meta2d.store.data,'disableMove')){
        (meta2d.store.data as Meta2dBackData).disableMove?true:false;
      }else{
        (meta2d.store.data as Meta2dBackData).disableMove=true
      }
      if(Object.hasOwn(meta2d.store.data,'disableScale')){
        (meta2d.store.data as Meta2dBackData).disableScale?true:false
      }else{
        (meta2d.store.data as Meta2dBackData).disableScale=true
      }
      console.log('meta2d.store.data1',meta2d.store.data);
      // (meta2d.store.data as Meta2dBackData).disableScale=true
      meta2dInfo.value = Object.assign({}, meta2d.store.data);
      console.log('meta2dInfo.value',meta2dInfo.value)
      // meta2dInfo.value.disableMove=true
      // meta2dInfo.value.disableScale=true
    });
    const data = reactive({
      space: 30,
      maxWidth: undefined,
    });
    const meta2dInfo = ref<Meta2dBackData | any>({});
    const layoutConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          key: 'maxWidth',
          type: 'number',
          name: `${t('最大宽度')}`,
          placeholder: `${t('自适应')}`,
        },
        {
          key: 'space',
          type: 'number',
          name: `${t('间距')}`,
        },
      ];

      return configs;
    });
    const fillConfigs=computed(()=>{
      const configs: FormItemType[] = [
        {
          key: 'fill',
          type: 'switch',
          name: `${t('自适应')}`,
        },
        {
          key: 'shortFill',
          type: 'switch',
          name: `${t('短边自适应')}`,
        },
      ];

      return configs;
    })
    const moveScaleConfigs=computed(()=>{
      const configs: FormItemType[] = [
        {
          key: 'disableMove',
          type: 'switch',
          name: `${t('禁止拖拽')}`,
        },
        {
          key: 'disableScale',
          type: 'switch',
          name: `${t('禁止缩放')}`,
        },
      ];

      return configs;
    })
    function onLayout() {
      if (meta2d.store.active.length > 1) {
        meta2d.layout(meta2d.store.active, data.maxWidth, data.space);
      } else {
        // 传入 undefined 即全部
        meta2d.layout(undefined, data.maxWidth, data.space);
      }
    }
    // 画布缩放比例
    const scale = ref(100);
    async function changeValue(
      { value, keys }: { value?: any; keys?: string[] }
    ) {
      console.log('切换进来le ',keys[0],value)
      if(keys[0]==='fill'&&value){
        console.log('长边自适应')
        meta2d.fitView(true);
        (meta2d.store.data as Meta2dBackData).shortFill=false
        meta2dInfo.value.shortFill=false
      }else if(keys[0]==='shortFill'&&value){
        console.log('我是短边自适应')
        meta2d.fitView(false);
        (meta2d.store.data as Meta2dBackData).fill=false
        meta2dInfo.value.fill=false
      }else if((keys[0]==='shortFill'||keys[0]==='fill')&&!value){
        console.log('不开')
        onScale(0)
      }
      meta2d.store.data[keys[0]] = value;
      console.log('meta2d.store.data',meta2d.store.data)
    }
    function onScale(val: number): void {
      if (val) {
        meta2d.scale((scale.value + val) / 100);
      } else {
        meta2d.scale(1);
        meta2d.centerView();
      }
      // 无需修改 scale 的值，scaleListener 监听到后会修改的
    }
    return {
      activeKey,
      data,
      layoutConfigs,
      onLayout,
      fillConfigs,
      meta2dInfo,
      changeValue,
      onScale,
      scale,
      moveScaleConfigs
    };
  },
});
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.PenProps {
  .mt8 {
    .btn {
      width: 100%;
    }
  }
}
</style>
