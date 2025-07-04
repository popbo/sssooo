<template>
  <div class="position_box">
    <div class="scaleView">
      <i @click.stop="onScale(-10)" class="icon-zt icon-zt-suoxiao mr16"></i>
      <div class="scale_box">
        <a-input v-model:value="scale" @change="setScale"/>
        <span> %</span>
      </div>
      <i @click.stop="onScale(10)" class="icon-zt icon-zt-sousuofangda ml16"></i>
    </div>
    <div class="sizeBox" v-if="type===1" style="color:#fff">
      <FormEvery
        :configs="sizeConfigs"
        :obj="activePen"
        @change-value="changeValue"
      />
    </div>
  </div>
</template>
<script lang="ts">
import { t } from '@/i18n/i18n';
import { Meta2d,Pen} from '@topometa2d/core';
import { computed, defineComponent, onUnmounted, ref,Ref,inject,nextTick,toRaw,watch } from 'vue';
import { FormItemType } from '../utils';
import Bus from './bus';
// declare const Stats: any;
declare const meta2d: Meta2d;
export default defineComponent({
  name: 'PositionBox',
  setup() {
    const scale = ref(100);
    function onScale(val: number): void {
      if (val) {
        meta2d.scale((scale.value + val) / 100);
      } else {
        meta2d.scale(1);
        meta2d.centerView();
      }
      // 无需修改 scale 的值，scaleListener 监听到后会修改的
    }
    function scaleListener(newScale: number) {
      scale.value = Math.round(newScale * 100);
    }
    function setScale(){
      meta2d.scale((scale.value) / 100);

    }
    function openedListener() {
      const {
        scale: canvasScale
      } = meta2d.store.data;
      scale.value = Math.round(canvasScale * 100);
    }
    nextTick(() => {
      meta2d.on('scale', scaleListener);
      meta2d.on('opened', openedListener);
    });
    onUnmounted(() => {
      meta2d.off('scale', scaleListener);
      meta2d.off('opened', openedListener);
    });
    const type = ref(0);
    const activePen= ref({id:''})
    //获取当前激活的图元
    Bus.$on('activePenType', (params) => {
      type.value=params.type
      activePen.value=params.activePen
      // console.log('获取坐标',activePen.value)
    });
    // 此处的 pen 是响应式的
    const pen: Ref<Pen> = inject('activePen');
    const sizeConfigs = computed<FormItemType[]>(() => {
      const configs: FormItemType[] = [
        {
          key: 'x',
          placeholder: 'px',
          type: 'number',
          name: 'X:',
        },
        {
          key: 'y',
          placeholder: 'px',
          type: 'number',
          name: 'Y:',
        },
        {
          key: 'width',
          placeholder: 'px',
          type: 'number',
          name: `${t('宽:')}`,
          min: 1,
        },
        {
          key: 'height',
          placeholder: 'px',
          type: 'number',
          name: `${t('高:')}`,
          min: 1,
        }
      ];

      // addShowChild(pen.value, configs);
      return configs;
    });
    // 改变了某个值
    async function changeValue({
      value,
      keys,
    }: {
      value: any;
      keys: string[];
    }) {
      //console.log('宽高改变了',keys,value)
      if (!value && ['width', 'height'].includes(keys[0])) {
        value = 1;
      }
      const valueObj = {
        id: activePen.value.id,
        [keys[0]]: value,
      };
      if ((activePen.value as any).ratio) {
        const rect = meta2d.findOne(activePen.value.id);
        if (keys[0] === 'width') {
          Object.assign(valueObj, {
            height: (value / rect.width) * rect.height,
          });
        } else if (keys[0] === 'height') {
          Object.assign(valueObj, {
            width: (value / rect.height) * rect.width,
          });
        }
      }
      //实时更新坐标
      meta2d.setValue(toRaw(valueObj), { history: true });
      const rect = meta2d.getPenRect(activePen.value);
      Object.assign(activePen.value, rect);
    }
    return {
      scale,
      onScale,
      scaleListener,
      setScale,
      sizeConfigs,
      type,
      activePen,
      changeValue,
      pen
    };
  },
});
</script>
<style lang="scss" scoped>
.position_box{
  width: calc(100% - 550px);
  height: 48px;
  background-color: #181a24;
  position: absolute;
  bottom: 0;
  left: 208px;
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  justify-content: space-between;
  align-items: center;
  .scaleView{
    display: flex;
    align-items: center;
    margin-left: 15px;
    color:#b4b7c1;
    .ant-input{
      width: 100px;
    }
    .t-icon{
      cursor: pointer;
    }
    .scale_box{
      .ant-input{
        width: 75px;
      }
    }
  }
  .sizeBox{
    margin-right: 20px;
    &>div{
      display: flex;
    }
    :deep(.form-item){
      .FormItem{
        .ant-form-item-label{
          text-align: right;
        }
        .ant-input-number{
          background: #232630;
        }
      }
    }
  }
}
</style>