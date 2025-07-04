<template>
  <div class="Meta2dCanvas" ref="canvasRef" >
    <a-dropdown :visible="contextMenuVisible" :trigger="['contextmenu']">
      <div id="meta2d"></div>
      <!-- 菜单项 -->
      <template #overlay>
        <CanvasContextMenu
          @mousemove="menuMousemove"
          v-model:contextVisible="contextMenuVisible"
        />
      </template>
    </a-dropdown>
    <LDialog />
    <IframeDialog />
 
</div>
</template>

<script lang="ts">
import { getCookie } from '@/services/cookie';
import { upload } from '@/services/file';
import { Options, Meta2d, setHover,deepClone } from '@topometa2d/core';
import { computed, defineComponent, onMounted, onUnmounted, ref } from 'vue';
import { useStore } from 'vuex';
import CanvasContextMenu from './CanvasContextMenu.vue';
import { registerNormalShape } from './register';
import { message, notification } from 'ant-design-vue';
import { isLogin, noLoginTip, User } from '@/services/user';
import LDialog from './common/LDialog/LDialog.vue';
import IframeDialog from './common/IframeDialog/IframeDialog.vue';
import debounce from "lodash/debounce"; // 防抖函数
import Bus from '@/components/common/bus';
import { combinePenInit} from './utils';
// declare const Stats: any;
declare const meta2d: Meta2d;

export default defineComponent({
  name: 'Meta2dCanvas',
  components: { CanvasContextMenu, LDialog, IframeDialog },
  setup() {
    const store = useStore();
    const user = computed<User>(() => store.state.user.profile);
    const meta2dOptions: Options = {
      // scroll: true,
      // disableEmptyLine: true   // 禁用空连线
      // disableRepeatLine: true,    // 禁用重复连线，即入点出点相同
      // disableDockLine: true,   // 禁用停靠对齐线
      // disableTranslate: true  // 禁用移动
      color:'#a5a8b2',
      background:'#232630',
      hoverColor:undefined,//默认没有hover效果
      activeColor:'#40a9ff',//默认没有选中效果--->改唯有默认效果
      hoverCursor:'default',
      // uploadFn: async (file: File) => {
      //   if (!isLogin(user.value)) {
      //     message.warn(noLoginTip);
      //     return;
      //   }
      //   const res = await upload(file, true, file.name);
      //   return res.url;
      // },
      uploadFn: () => {} //解决图元上传重复问题
    };
    let allPngCombineArr=[] //组合的图元数组
    let initPen={} //点击图元样本
    let  firstAdd=0 //用来监控点击图元meta2d.canvas.addCaches存储值的变化
    onMounted(async () => {
      new Meta2d('meta2d', meta2dOptions);
      registerNormalShape();
      // TODO: 测试性能，需要时才使用
      // var stats = new Stats();
      // stats.showPanel(0); // 0: fps, 1: ms, 2: mb, 3+: custom
      // document.body.appendChild(stats.dom);
      // function animate() {
      //   stats.begin();
      //   // monitored code goes here
      //   stats.end();
      //   requestAnimationFrame(animate);
      // }
      // requestAnimationFrame(animate);
      Bus.$on('transferAllPngCombine',val=>{
        allPngCombineArr=val
      })
      meta2d.on('contextmenu', contextmenu);
      meta2d.on('click', click);
      meta2d.on('add',(e)=>{
        //初次点击的时候给初始的pen赋值
        if(meta2d.canvas?.addCaches?.length>0){
          //点击图元时更改状态用来给样本图元赋值，防止初始添加同类型图元组合时再次进入
          firstAdd=1
        }
        if(firstAdd=1){
          initPen=e[0]
        }       
      })

      meta2d.on('info', (e) => {
        notification.open({
          message: e.title,
          description: e.text,
        });
      });

      const canvas = canvasRef.value;
      canvas.addEventListener('mousedown', handleMouseDown)
      canvas.addEventListener('mouseup', debounce(handleMouseUp,1000))
      canvas.addEventListener('mouseout', handleMouseOut);
    });
    onUnmounted(() => {
      if (meta2d) {
        meta2d.off('contextmenu', contextmenu);
        meta2d.off('click', click);
        meta2d.destroy();
      }

      removeEventListeners();//销毁监听事件
    });
    const menuMousemove = () => {
      let hover = meta2d.store.data.pens.find(
        (item) => item.calculative.hover === true
      );
      setHover(hover, false);
    };
    const contextMenuVisible = ref(false);
    function contextmenu() {
      contextMenuVisible.value = false;
      setTimeout(() => {
        contextMenuVisible.value = true;
      }, 100);
    }

    function click() {
      contextMenuVisible.value = false;
      //用于点击左侧图元，点击编辑界面自动组合同状态的图元
      firstAdd=0 //置空为0，防止强制meta2d.addPen图元时再次触发监听add
      if(initPen?.image){
        console.log('这里的值',initPen)
        const penImage=initPen['image']
        const regex = /\/([^\/]+)\.[^.]+$/;
        const match = regex.exec(penImage);
        const penImageName=match?match[1]:''
        if(penImageName){
          combinePenInit(allPngCombineArr,penImageName,initPen)
        }
      }
      initPen={} //清空初始样本 防止meta2d.canvas.addCaches没数据时点击左建触发组合状态
    }

    const canvasRef = ref();
    const ishandleMouseDown=ref(false)
    function handleMouseOut() {
      // 监听鼠标的弹起事件
      if(ishandleMouseDown) window.getSelection().removeAllRanges();
    }
    function handleMouseDown(){
      ishandleMouseDown.value=true
    }
    function handleMouseUp(){
      ishandleMouseDown.value=false
    }
    // 移除事件
    function removeEventListeners() {  
      const canvas = canvasRef.value;
      if (canvas) {
        canvas.removeEventListener('mousedown', handleMouseDown);
        canvas.removeEventListener('mouseup', handleMouseUp);
        canvas.removeEventListener('mouseout', handleMouseOut);
      }
    }

    return {
      contextMenuVisible,
      menuMousemove,
      canvasRef
    };
  },
});
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.Meta2dCanvas {
  position: relative;
  background-color: #232630;
  #meta2d {
    position: absolute !important;
    width: 100%;
    height: calc(100% - 50px);
    touch-action: none;
    overflow: hidden;
  }
  .passwordModal{
    position: absolute;
    left: 0;
    right: 0;
    top: 0;
    bottom: 0;
    background: #232630;
    z-index:10
  }
}
</style>
