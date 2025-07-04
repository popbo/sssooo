<template>
  <div class="dialogClassSet" ref="dialogBox" :style="customStyle" 
  >
  <a-modal 
    :getContainer="()=>$refs.dialogBox"
    v-model:visible="visible" :title="titleContent" :footer="null"
    :mask="style.isMask"
    :closable="closeStyle.closable"
    :maskClosable="closeStyle.maskClosable"
    :style="customModalPos"
   >
    <div class="LDialogContent">
      <div class="row">
        <span class="label">ID:</span> <span>{{ pen.id }}</span>
      </div>
      <div class="row" v-if="pen.description">
        <span class="label">名称:</span> <span>{{ pen.description }}</span>
      </div>
      <div class="row" v-if="pen.tags">
        <span class="label">tags:</span> <span>{{ pen.tags }}</span>
      </div>
      <template v-if="pen.form">
        <div class="row" v-for="formItem in pen.form">
          <template v-if="formItem.key2">
            <span class="label">{{ formItem.name }}:</span>
            <span>{{ pen[formItem.key][formItem.key2] }}</span>
          </template>
          <template v-else>
            <span class="label">{{ formItem.name }}:</span>
            <span>{{ pen[formItem.key] }}</span>
          </template>
        </div>
      </template>
    </div>
  </a-modal>
</div>
</template>

<script lang="ts">
import { defineComponent, nextTick, ref, computed, watchEffect, reactive ,onMounted,UnwrapRef} from 'vue';
import { useRoute } from 'vue-router';
import { useLDialog } from './lDialog';
import Bus from '../bus';
import {modalSetForm} from '../../utils'
import { Meta2d } from '@topometa2d/core';
declare const meta2d: Meta2d;
export default defineComponent({
  name: 'LDialog',
  setup(){
    const { visible, pen,currentPen} = useLDialog();
    const initData:modalSetForm = {
      showPos: 'static',
      closeMethod: 'default',
      isMask:true,
      btnColor: '#fff',
      btnFlowColor: '#fff',
      btnSize: '16px',
      modalWidth: '520px',
      modalHeight: 'auto',
      modalBgColor:'#232630',
      titleSwitch:false,
      isShowTitle:false,
      titleBgColor:'#232630',
      titleSize:'14px',
      titleColor:'#fff',
      titleLean:'normal',
      titleBold:'normal',
      titleLevel:'flex-start',
      titleVertical:'center',
      titleContent:'详情',
      borderSwitch:false,
      borderStyle:'solid',
      borderWidth:'0px',
      borderColor:'transparent',
      borderRound:'2px',
      shadowColor:'transparent',
      shadowBlur:'0px',
      shadowOffsetX:'0px',
      shadowOffsetY:'0px',
      scrollBar:false,
      moreStyleParam:'',
      backImageVal:'',
      imageVal:'',
      isLockRadio:false, //锁定宽高比
      modalRatio:'',//宽高比例
    }
    let style=reactive({...initData})
    const isScollStyle=ref()
    const closeStyle=reactive({
      closable:true,
      maskClosable:true
    })
    const customStyle=ref()
    const customModalPos=reactive({
      margin: '0 auto',
      left:'auto',
      top:'100px',
    })
    const wrapStyle=ref()
    const route = useRoute();
    const showHead=ref()
    const titleHeight=ref('50px')
    let titleContent=ref()
    function resetData(){
      for(var n in initData){
        style[n]=initData[n]
      }
      console.log('我进来了1',style)
    }
    Bus.$on('modalStyleCustom',(val)=>{
      //重置参数
      resetData()
      for(var k in val){
        if(val[k]!==''){
          // if(k==='borderRound'||k==='btnSize'||k==='modalWidth'||k==='modalHeight'||k==='modalHeight'||k==='titleSize')
          if(['borderRound','btnSize','modalWidth','modalHeight','titleSize','borderWidth','shadowBlur','shadowOffsetX','shadowOffsetY'].includes(k)){
            style[k]=parseInt(val[k])+'px'
          }else{
            style[k]=val[k]
          }
        }else{
          style[k]=val[k]
          if(!style.titleSwitch){
            style.titleColor='#fff'
            style.titleLevel='flex-start'
            style.titleVertical='center'
          }
        }
      }
      if(style.moreStyleParam){
        const regex = /\r|\n/g;
        let match;
        match=style.moreStyleParam.replace(regex,'')
        customStyle.value = eval('(' + match + ')')
      }
      //是否滚动条
      if(style.scrollBar){
        isScollStyle.value='auto'
      }else{
        isScollStyle.value='hidden'
      }
      //关闭方式
      if(style.closeMethod==='default'){
        closeStyle.closable=true
        closeStyle.maskClosable=true
      }else if(style.closeMethod==='blankHide'){
        closeStyle.closable=false
        closeStyle.maskClosable=true
      }else if(style.closeMethod==='closeBtn'){
        closeStyle.closable=true
        closeStyle.maskClosable=false
      }
      // 显示位置
      //console.log('跟随鼠标',style.showPos)
      if(style.showPos==='flow'){
        customModalPos.margin='0'
        const modalLeft = ref(Math.floor(currentPen.value.x)+Math.floor(meta2d.store.data.x)+Math.floor(currentPen.value.width))
        const modalTop = ref(Math.floor(currentPen.value.y)+Math.floor(meta2d.store.data.y))
        
        if(route.path.includes('preview')||route.path.includes('release')){
          customModalPos.left=modalLeft.value+'px'
          customModalPos.top=modalTop.value+'px'
        }else{
          customModalPos.left=modalLeft.value+210+'px'
          customModalPos.top=modalTop.value+42+'px'
        }
        
        wrapStyle.value='auto'
      }else{
        wrapStyle.value='0'
        customModalPos.margin='0 auto'
        customModalPos.top='100px'
        customModalPos.left='auto'
      }
      // 标题头
      if(!style.isShowTitle){
        showHead.value='none'
        titleHeight.value='0px'
      }else{
        showHead.value='flex'
        titleHeight.value='50px'
      }
      titleContent.value=''
      if(style.titleContent.indexOf('${')>-1){
        let contentArr = style.titleContent.split('-');
        contentArr.forEach(x=>{
          if(x.indexOf('${')>-1){
            let itemParm=x.slice(2,-1)
            if(currentPen.value[itemParm]){
              titleContent.value+='-'+currentPen.value[itemParm]
            }else{
              titleContent.value+='-'+x
            }
          }else{
            titleContent.value+='-'+x
          }
        })
        titleContent.value=titleContent.value.slice(1)
      }else{
        titleContent.value=style.titleContent+' '
      }
      console.log('style',style)
    })
    return {
      visible,
      pen,
      style,
      isScollStyle,
      closeStyle,
      customStyle,
      customModalPos,
      wrapStyle,
      showHead,
      titleHeight,
      titleContent,
      // resetData
    }
  }
})
</script>
<style lang="scss" scoped>
@import '@/styles/variables.scss';
.LDialogContent {
  .row {
    display: flex;
    color: #b4b7c1;
    .label {
      width: 60px;
      text-align: right;
      margin-right: 10px;
    }
  }
}

.dialogClassSet{
  :deep(.ant-modal-wrap){
    /*top: v-bind(customStyle);
    right: v-bind(customStyle);
    bottom: v-bind(customStyle);
    left: v-bind(customStyle);*/
    overflow: visible;
  }
  :deep(.ant-modal){
    width:v-bind('style.modalWidth')!important;
    height: v-bind('style.modalHeight')!important;
    padding-bottom:0;
  }
  :deep(.ant-modal-content){
    background-color: v-bind('style.modalBgColor');
    background-image:v-bind('style.backImageVal');
    background-size: 100% 100%;
    border-width:v-bind('style.borderWidth');
    border-style:v-bind('style.borderStyle');
    border-color:v-bind('style.borderColor');
    border-radius:v-bind('style.borderRound');
    box-shadow:v-bind('style.shadowOffsetX') v-bind('style.shadowOffsetY') v-bind('style.shadowBlur') v-bind('style.shadowColor');
    overflow: hidden;
    .ant-modal-header{
      background: v-bind('style.titleBgColor');
      padding: 0 24px;
      height: 50px;
      border-bottom:1px solid #363841;
      display: v-bind('showHead');
      justify-content: v-bind('style.titleLevel');
      align-items: v-bind('style.titleVertical');
      border-radius: 0;
      padding-right: 50px;
      .ant-modal-title{
        color: v-bind('style.titleColor');
        font-size: v-bind('style.titleSize');
        font-style: v-bind('style.titleLean');
        font-weight: v-bind('style.titleBold');
        text-align: v-bind('style.titleLevel');
      }
      
    }
    .ant-modal-close-x{
      color: v-bind('style.btnColor');
      font-size: v-bind('style.btnSize');
      &:hover{
        color: v-bind('style.btnFlowColor');
      }
      line-height: 50px;
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
      color:  #b4b7c1;
      height: calc(v-bind('style.modalHeight') - v-bind('titleHeight'));
      overflow: v-bind(isScollStyle);
      .ant-input{
        background: #181a24;
        color: #b4b7c1;
        border:1px solid #2e303d;
      }
    }
  }
}
.modal-box{
  position: fixed;
  z-index:1000;
}
</style>
