<template>
  <section class="Meta2dProps">
    <FileProps v-show="type === 0" @updateHidden="hiddenChange" />
    <PenProps v-show="type === 1" @updateHidden="hiddenChange" />
    <!-- 批量修改图元重置外观数据 -->
    <PensProps v-if="type === 2" @updateHidden="hiddenChange" />
    <Structure class="structure" v-if="isHidden" :isHidden="isHidden"/>
  </section>
</template>

<script lang="ts">
import { deepClone, Pen, Meta2d } from '@topometa2d/core';
import { defineComponent, nextTick, onUnmounted, provide, ref,defineEmits } from 'vue';
import FileProps from './fileProps/FileProps.vue';
import PenProps from './penProps/PenProps.vue';
import PensProps from './pensProps/PensProps.vue';
import { strictAssign } from './utils';
import Bus from './common/bus';
import {customEle} from '@/diagram/canvasIndex'
declare const meta2d: Meta2d;

enum Type {
  Canvas, // 画布
  Pen, // 单画笔
  Pens, // 多画笔
}

export default defineComponent({
  name: 'Meta2dProps',
  components: {
    FileProps,
    PenProps,
    PensProps,
  },
  setup() {
    // 当前需要右侧需要展示的内容
    const type = ref(Type.Canvas);
    const activePen = ref({});
    const activePens = ref([]);
    provide('activePen', activePen);
    provide('activePens', activePens);
    const customPen=customEle()
    let isClickPen=false//用来信号图元实时更新重新计算x,y,width,height的后，自动图元高亮后显示右侧面板问题
    const active = (oldPens: Pen[]) => {
      //初始化时到这一步停了没继续执行定时器bug
      //console.log('激活的图元',activePens)
      nextTick(()=>{
        setTimeout(() => {
        const pens = getPenRectPens(oldPens);
        checkPropType(pens);
        //船体当前激活图元的值
        Bus.$emit('activePenType', {type:type.value,activePen:activePen.value});
        // emit('activePenType',type.value)
        // 永远提供一个数组值
        activePens.value = oldPens;
        }, 1);
      })
    };

    /**
     * 根据当前传入的 pens 判断属性面板的类型
     */
    const checkPropType = (pens: Pen[]) => {
      if (pens.length === 1) {
        // 选中单个画笔
        type.value = Type.Pen;
        if (Array.isArray(pens[0].frames)) {
          (pens[0] as any).showDuration = meta2d.calcAnimateDuration(pens[0]);
        }
        strictAssign(activePen.value, pens[0]);
        changeHidden(lastActive.Pen);
      } else if (pens.length > 1) {
        type.value = Type.Pens;
        lastActive['Pens']='属性' //解决框选图元，切换到结构重新框选属性和结构重合问题
        changeHidden(lastActive.Pens);
      } else {
        type.value = Type.Canvas;
        changeHidden(lastActive.Canvas);
      }
    };

    const inactive = (pens: Pen[]) => {
      // 核心库先发出消息，后将 meta2d.store.active 内容清空
      // 操作写在 定时器 中，等同步代码执行完成，这样 meta2d.store.active 中的值就是最新的了
      setTimeout(() => {
        active(meta2d.store.active);
      });
    };

    const changePen = (oldPens: Pen[] | Pen) => {
      const operationPens = Array.isArray(oldPens) ? oldPens : [oldPens];
      active(operationPens);
    };
    const clickPen = ({pen})=>{
      if(pen){
        isClickPen=true
      }else{
        isClickPen=false
      }
    }
    nextTick(() => {
      meta2d.on('active', active);
      meta2d.on('resizePens', changePen);
      meta2d.on('rotatePens', changePen);
      meta2d.on('translatePens', changePen);
      meta2d.on('inactive', inactive);
      meta2d.on('opened', opened);
      meta2d.on('valueUpdate', valueUpdate);
      meta2d.on('click', clickPen);
    });
    const valueUpdate = (e) => {
      const activeCurPen = meta2d.store.active;
      if (activeCurPen.length == 1 && activeCurPen[0].id === e.id) {
        strictAssign(activePen.value, e);
      }
      if(e&&Object.keys(customPen).includes(e['name'])){
        if(activePen.value['id']===e.id){
          if(isClickPen){
            //console.log('执行更新参数')
            active([e])
          }
        }
      }
    };

    onUnmounted(() => {
      meta2d.off('active', active);
      meta2d.off('resizePens', changePen);
      meta2d.off('rotatePens', changePen);
      meta2d.off('translatePens', changePen);
      // meta2d.off('valueUpdate', changePen);
      meta2d.off('inactive', inactive);
      meta2d.off('opened', opened);
      meta2d.off('valueUpdate', valueUpdate);
      meta2d.off('click', clickPen);
    });

    function opened() {
      active(meta2d.store.active);
    }
    const lastActive = {
      Pen: '属性',
      Pens: '属性',
      Canvas: '图纸',
    };
    const isHidden = ref(false);
    const hiddenChange = (map: any) => {
      changeHidden(map.value);
      lastActive[map.key] = map.value;
    };
    function changeHidden(val) {
      if (val === '结构') {
        isHidden.value = true;
      } else {
        isHidden.value = false;
      }
    }

    return {
      type,
      hiddenChange,
      isHidden
    };
  },
});

function getPenRectPens(oldPens: Pen[]): Pen[] {
  return oldPens.map((pen) => {
    const rect = meta2d.getPenRect(pen);
    return {
      ...pen,
      ...rect,
    };
  });
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.Meta2dProps {
  //background: #ffffff;
  //box-shadow: 0px 2px 4px 0px #dad7d7;
}
</style>

<style lang="scss">
.PropsTabs {
  position: relative;
  height: 100%;
  background: #181a24;

  .ant-tabs-bar {
    margin-bottom: 0;
    border-bottom: 1px solid #232630;
    .ant-tabs-nav-wrap {
      .ant-tabs-tab {
        margin-right: 0;
        margin-left: 16px;
        padding-top: 10px;
        padding-bottom: 10px;
        padding-left: 0;
        padding-right: 0;

        .tabText {
          font-size: 14px;
          line-height: 20px;
          letter-spacing: 0px;
          font-family: PingFangSC, PingFangSC-Regular;
          font-weight: 400;
          color: #b4b7c1;
        }

        &.ant-tabs-tab-active {
          .tabText {
            font-family: PingFangSC, PingFangSC-Medium;
            font-weight: 500;
            color: #3a89fe;
          }
        }
      }

      //.ant-tabs-ink-bar {
      // background: #000000;
      //}
    }
  }

  .ant-tabs-content {
    // TODO: 60px 可能不标准
    height: calc(100% - 60px);

    .ant-tabs-tabpane {
      height: 100%;
      overflow-y: auto;
    }
  }
}
</style>

<style lang="scss">
.PenProps {
  background-color: #232630;
  margin-top: 18px;
  &.ant-collapse {
    border-left: none;
    border-right: none;
    background: #2d2f38;
    border-top: 1px solid #393b4a;
  }
  .ant-collapse-item {
    border-bottom: 1px solid #393b4a;
    .ant-collapse-header {
      padding-left: 0;
      padding-top: 0;
      padding-bottom: 0;
      font-size: 14px;
      color: #fff;
      font-weight: 600;
      padding: 8px 20px 8px 40px;
      .ant-collapse-arrow {
        left: 20px;
      }
    }
    .ant-collapse-content {
      background: #232630;
      padding: 8px 20px 8px 40px;
      border-top: 1px solid #393b4a;
    }
    .ant-collapse-content-box {
      padding: 0;
      .ant-input {
        background: #181a24;
        border: 1px solid #393b4a;
        color: #b4b7c1;
      }
    }
  }
}
</style>

<style lang="scss">
.Meta2dProps {
  position: relative;
  .structure {
    position: absolute;
    top: 50px;
    width: 100%;
    max-height: calc(100% - 50px);
    overflow-y: auto;
    .content {
      color: #b4b7c1;
    }
  }
  .ant-tabs-tabpane {
    overflow-x: hidden;
  }
}
</style>
