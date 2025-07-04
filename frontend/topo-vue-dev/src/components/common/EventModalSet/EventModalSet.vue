<template>
  <div class="dialogClass" ref="dialogBox">
    <a-modal
      :getContainer="() => $refs.dialogBox"
      :visible="visible"
      width="400px"
      :bodyStyle="{ height: '590px', overflowY: 'scroll' }"
      @cancel="cancel"
      @ok="handleOk"
    >
      <template #title>
        弹窗设置
      </template>
      <a-form :model="modalSetForm"
        ref="modalSetFormRef"
      :label-col="labelCol" :wrapper-col="wrapperCol">
        <a-collapse v-model:activeKey="activeKey" :bordered="false">
          <a-collapse-panel key="1" header="弹窗设置">
            <a-form-item
              class="FormItem"
              labelAlign="left"
              label="显示位置"
              :colon="false"
            >
            <a-select v-model:value="modalSetForm.showPos">
              
              <a-select-option
                v-for="option in dicOptionS.showPos"
                :key="option.value"
                :value="option.value"
                :disabled="option.disabled">{{ option.label}}
              </a-select-option>
            </a-select>
            </a-form-item>
            <a-form-item
              class="FormItem"
              labelAlign="left"
              label="关闭方式"
              :colon="false"
            >
              <a-select v-model:value="modalSetForm.closeMethod"
              @change="changeCloseMethod">
                <a-select-option
                v-for="option in dicOptionS.closeMethod"
                :key="option.value"
                :disabled="option.disabled"
                :value="option.value">{{ option.label}}</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item
              class="FormItem"
              labelAlign="left"
              label="是否遮罩"
              :colon="false"
            >
              <a-switch v-model:checked="modalSetForm.isMask"/>
            </a-form-item>
            <div class="posOther" v-if="modalSetForm.closeMethod==='default'||modalSetForm.closeMethod==='closeBtn'">
              <a-form-item
              class="FormItem"
              labelAlign="left"
              label="按钮颜色"
              :colon="false"
              > 
              <a-row>
                <a-col :span="5">
                  <el-color-picker size="mini"  v-model="modalSetForm.btnColor"
                  show-alpha
                  color-format="hex"
                  :predefine="predefineColors"
                  ></el-color-picker>
                </a-col>
                <a-col :span="12">
                  <a-input v-model:value="modalSetForm.btnColor"/>
                </a-col>
              </a-row>
                
              </a-form-item>
              <a-form-item
              class="FormItem"
              labelAlign="left"
              label="浮动颜色"
              :colon="false"
              >
                <a-row>
                  <a-col :span="5">
                    <el-color-picker size="mini" v-model="modalSetForm.btnFlowColor"
                    show-alpha
                    color-format="hex"
                    :predefine="predefineColors"
                    ></el-color-picker>
                  </a-col>
                  <a-col :span="12">
                    <a-input v-model:value="modalSetForm.btnFlowColor"/>
                  </a-col>
                </a-row>
                
              </a-form-item>
              <a-form-item
              class="FormItem"
              labelAlign="left"
              label="按钮大小"
              :colon="false"
              >
                <a-input v-model:value="modalSetForm.btnSize"/>
              </a-form-item>
            </div>
          </a-collapse-panel>
          <a-collapse-panel key="2" header="样式设置">
            <a-form-item
            class="FormItem"
            labelAlign="left"
            label="弹窗宽度"
            :colon="false"
            >
              <a-input v-model:value="modalSetForm.modalWidth"
              @change="(val)=>{
                setSize(val,'width')
              }"/>
            </a-form-item>
            <a-form-item
            class="FormItem"
            labelAlign="left"
            label="弹窗高度"
            :colon="false"
            >
              <a-input v-model:value="modalSetForm.modalHeight"
              @change="(val)=>{
                setSize(val,'height')
              }"/>
            </a-form-item>
            <a-form-item
            class="FormItem"
            labelAlign="left"
            label="锁定宽高比"
            :colon="false"
            >
            <a-switch v-model:checked="modalSetForm.isLockRadio"
            @change="(val)=>{
              switchState(val,'isLockRadio')
            }"
            />
            </a-form-item>
            <a-form-item
            class="FormItem"
            labelAlign="left"
            label="背景颜色"
            :colon="false"
            >
              <a-row>
                <a-col :span="5">
                  <el-color-picker size="mini" v-model="modalSetForm.modalBgColor"
                  show-alpha
                  color-format="hex"
                  :predefine="predefineColors"
                  ></el-color-picker>
                </a-col>
                <a-col :span="12">
                  <a-input v-model:value="modalSetForm.modalBgColor"/>
                </a-col>
              </a-row>
              
            </a-form-item>
            <a-form-item
            class="FormItem"
            labelAlign="left"
            label="背景图片"
            :colon="false"
            >
            <div @click="imageVisible=true">
              <!-- 值不存在，选图片按钮 -->
              <FormOutlined v-if="!modalSetForm.imageVal" />
        
              <!-- 值存在，展示该 img -->
              <template v-else>
                <img :src="String(modalSetForm.imageVal)" style="max-width: 50px; max-height: 50px" />
                <i
                  class="t-icon t-close"
                  @click.stop="clearImage"
                  :title="t('清除图片')"
                  style="margin-left:5px"
                ></i>
              </template>
            </div>
            </a-form-item>
            <a-form-item
            class="FormItem"
            labelAlign="left"
            label="标题头"
            :colon="false"
            >
            <a-switch v-model:checked="modalSetForm.isShowTitle" @change="(val)=>{
              switchState(val,'isShowTitle')
            }"/>
            </a-form-item>
            <div class="posOther" v-if="modalSetForm.isShowTitle">
              <div class="posOther_pos">
                <a-form-item
                class="FormItem"
                labelAlign="left"
                label="背景颜色"
                :colon="false"
                > 
                  <a-row>
                    <a-col :span="5">
                      <el-color-picker size="mini" v-model="modalSetForm.titleBgColor"
                      show-alpha
                      color-format="hex"
                      :predefine="predefineColors"
                      ></el-color-picker>
                    </a-col>
                    <a-col :span="12">
                      <a-input v-model:value="modalSetForm.titleBgColor"/>
                    </a-col>
                  </a-row>
                  
                </a-form-item>
                <a-form-item
                class="FormItem"
                labelAlign="left"
                label="标题"
                :colon="false"
                >
                  <a-switch v-model:checked="modalSetForm.titleSwitch" @change="(val)=>{
                    switchState(val,'title')
                  }"/>
                </a-form-item>
                <div class="posOther posOther_posOther" v-if="modalSetForm.titleSwitch">
                  <a-form-item
                  class="FormItem"
                  labelAlign="left"
                  label="文字大小"
                  :colon="false"
                  >
                    <a-input v-model:value="modalSetForm.titleSize"/>
                  </a-form-item>
                  <a-form-item
                  class="FormItem"
                  labelAlign="left"
                  label="文字颜色"
                  :colon="false"
                  >
                    <a-row>
                      <a-col :span="5">
                        <el-color-picker size="mini" v-model="modalSetForm.titleColor"
                        show-alpha
                        color-format="hex"
                        :predefine="predefineColors"  
                        ></el-color-picker>
                      </a-col>
                      <a-col :span="12">
                        <a-input v-model:value="modalSetForm.titleColor"/>
                      </a-col>
                    </a-row>
                  </a-form-item>
                  <a-form-item
                    class="FormItem"
                    labelAlign="left"
                    label="倾斜"
                    :colon="false"
                  >
                    <a-select v-model:value="modalSetForm.titleLean">
                      <a-select-option
                        v-for="option in dicOptionS.textLean"
                        :key="option.value"
                        :value="option.value">{{ option.label}}
                      </a-select-option>
                    </a-select>
                  </a-form-item>
                  <a-form-item
                    class="FormItem"
                    labelAlign="left"
                    label="加粗"
                    :colon="false"
                  >
                    <a-select v-model:value="modalSetForm.titleBold">
                      <a-select-option
                        v-for="option in dicOptionS.textBold"
                        :key="option.value"
                        :value="option.value">{{ option.label}}
                      </a-select-option>
                    </a-select>
                  </a-form-item>
                  <a-form-item
                    class="FormItem"
                    labelAlign="left"
                    label="水平对齐"
                    :colon="false"
                  >
                    <a-select v-model:value="modalSetForm.titleLevel">
                      <a-select-option
                        v-for="option in dicOptionS.textLevel"
                        :key="option.value"
                        :value="option.value">{{ option.label}}
                      </a-select-option>
                    </a-select>
                  </a-form-item>
                  <a-form-item
                    class="FormItem"
                    labelAlign="left"
                    label="垂直对齐"
                    :colon="false"
                  >
                    <a-select v-model:value="modalSetForm.titleVertical">
                      <a-select-option
                        v-for="option in dicOptionS.textVertical"
                        :key="option.value"
                        :value="option.value">{{ option.label}}
                      </a-select-option>
                    </a-select>
                  </a-form-item>
                  <a-form-item
                    class="FormItem"
                    labelAlign="left"
                    :colon="false"
                  >
                  <template #label>
                    <span class="label" title="标题内容"
                      >标题内容
                      <a-tooltip>
                        <template #title> 传递动态参数，${id}，- 链接多个参数</template>
                        <i class="t-icon t-help-circle"></i>
                      </a-tooltip>
                    </span>
                  </template>
                  <a-textarea v-model:value="modalSetForm.titleContent" />
                  </a-form-item>
                </div>
              </div>
            </div>
            <a-form-item
            class="FormItem"
            labelAlign="left"
            label="边框"
            >
            <a-switch v-model:checked="modalSetForm.borderSwitch"
            @change="(val)=>{
              switchState(val,'border')
            }"/>
            </a-form-item>
            <div class="posOther" v-if="modalSetForm.borderSwitch">
              <a-form-item
                class="FormItem"
                labelAlign="left"
                label="线条样式"
              >
                <a-select v-model:value="modalSetForm.borderStyle">
                  <a-select-option
                    v-for="option in dicOptionS.borderStyle"
                    :key="option.value"
                    :value="option.value">{{ option.label}}
                  </a-select-option>
                </a-select>
              </a-form-item>
              <a-form-item
              class="FormItem"
              labelAlign="left"
              label="边框粗细"
              >
                <a-input v-model:value="modalSetForm.borderWidth"/>
              </a-form-item>
              <a-form-item
              class="FormItem"
              labelAlign="left"
              label="边框颜色"
              >
                <a-row>
                  <a-col :span="5">
                    <el-color-picker size="mini" v-model="modalSetForm.borderColor"
                    show-alpha
                    color-format="hex"
                    :predefine="predefineColors"
                    ></el-color-picker>
                  </a-col>
                  <a-col :span="12">
                    <a-input v-model:value="modalSetForm.borderColor"/>
                  </a-col>
                </a-row>
              </a-form-item>
              <a-form-item
              class="FormItem"
              labelAlign="left"
              label="边框圆角"
              >
                <a-input v-model:value="modalSetForm.borderRound"/>
              </a-form-item>
              <a-form-item
              class="FormItem"
              labelAlign="left"
              label="阴影颜色"
              >
                <a-row>
                  <a-col :span="5">
                    <el-color-picker size="mini" v-model="modalSetForm.shadowColor"
                    show-alpha
                    color-format="hex"
                    :predefine="predefineColors"
                    ></el-color-picker>
                  </a-col>
                  <a-col :span="12">
                    <a-input v-model:value="modalSetForm.shadowColor"/>
                  </a-col>
                </a-row>
              </a-form-item>
              <a-form-item
              class="FormItem"
              labelAlign="left"
              label="阴影模糊"
              >
                <a-input v-model:value="modalSetForm.shadowBlur"/>
              </a-form-item>
              <a-form-item
              class="FormItem"
              labelAlign="left"
              label="阴影X偏移"
              >
                <a-input v-model:value="modalSetForm.shadowOffsetX"/>
              </a-form-item>
              <a-form-item
              class="FormItem"
              labelAlign="left"
              label="阴影Y偏移"
              >
                <a-input v-model:value="modalSetForm.shadowOffsetY"/>
              </a-form-item>
              <a-form-item
              class="FormItem"
              labelAlign="left"
              label="滚动条"
              >
                <a-switch v-model:checked="modalSetForm.scrollBar"/>
              </a-form-item>
            </div>
            <a-form-item
              class="FormItem"
              labelAlign="left"
              label="更多参数"
              >
                <a-button type="primary" @click="moreParam">...</a-button>
              </a-form-item>
          </a-collapse-panel>
        </a-collapse>
      </a-form>
    </a-modal>
  </div>  
  <MoreParamModal v-model:visible="moreParamModalVisible" 
  v-model:moreCustonStyle="moreCustonStyle"
  @changeCode="changeCode"/>
  <ImageDrawer
    v-model:visible="imageVisible"
    @chooseImage="chooseImage"
  />
</template>
<script lang="ts">
import Bus from '../bus';
import MoreParamModal from './moreParamModal.vue'
import {modalSetForm} from '../../utils'
import { defineComponent, inject, ref,Ref, computed, watch, reactive ,onMounted,UnwrapRef} from 'vue';
import { FormOutlined } from '@ant-design/icons-vue';
import { Pen } from '@topometa2d/core';
import {dicOption} from './EventModalSet'
import ImageDrawer from '../ImageDrawer.vue';

export default defineComponent({
  name: 'EventModalSet',
  components:{
    MoreParamModal,
    FormOutlined,
    ImageDrawer
  },
  props: {
    visible: {
      type: Boolean,
      require: true,
    },
  },
  emits: ['update:visible','changeModalStyle'],
  setup(props, { emit }) {
    // 预置颜色
    const predefineColors = [
      '#FF7875',
      '#FF9C6E',
      '#FFC069',
      '#FFD666',
      '#FFF566',
      '#D3F261',
      '#95DE64',
      '#5CDBD3',
      '#69C0FF',
      '#85A5FF',
      '#B37FEB',
      '#FF85C0',
      '#232630',
      '#000000',
      '#FFFFFF',
      '#FFFFFF00',
    ];
    const activeKey = ref([1,2]);
    const moreParamModalVisible = ref(false);
    const imageVisible=ref(false)
    function cancel() {
      emit('update:visible', false);
      for(var k in modalSetFormDeep){
        modalSetForm[k]=modalSetFormDeep[k]
      }
    }
    const dicOptionS = dicOption
    const modalSetForm: UnwrapRef<modalSetForm> = reactive({
      showPos: 'static',
      closeMethod: 'default',
      isMask:true,
      btnColor: '#ffffffff',
      btnFlowColor: '#ffffffff',
      btnSize: '16',
      modalWidth: '520',
      modalHeight: '300',
      modalBgColor:'#232630ff',
      titleSwitch:false,
      isShowTitle:false,
      titleBgColor:'',
      titleSize:'',
      titleColor:'',
      titleLean:'',
      titleBold:'',
      titleLevel:'',
      titleVertical:'',
      titleContent:'',
      borderSwitch:false,
      borderStyle:'',
      borderWidth:'',
      borderColor:'',
      borderRound:'',
      shadowColor:'',
      shadowBlur:'',
      shadowOffsetX:'',
      shadowOffsetY:'',
      scrollBar:false,
      moreStyleParam:'',
      isLockRadio:false, //锁定宽高比
      modalRatio:'',//宽高比例
      backImageVal:'',//背景图，为了处理样式方便
      imageVal:'', //上传图片，方便回显
    });
    const modalSetFormDeep = JSON.parse(JSON.stringify(modalSetForm))
    const modalSetFormRef = ref()
    let moreCustonStyle=ref()
    Bus.$on('modalStyle',(val)=>{
      if(JSON.stringify(val)!=='{}'){
        for(var k in val){
          modalSetForm[k]=val[k]
        }
      }
      console.log(11111,modalSetForm)
      console.log(2222,val)
    })
    
    
    const pen: Ref<Pen | any> = inject('activePen');
    function handleOk(){
      emit('changeModalStyle',modalSetForm)
      cancel()
    }
    function moreParam(){
      moreParamModalVisible.value=true
      moreCustonStyle.value=modalSetForm.moreStyleParam
      console.log('我是传过去的自定义参数',typeof moreCustonStyle)
    }
    function changeCode(code: string){
      modalSetForm.moreStyleParam=code
      console.log('接收到code了1111',modalSetForm.moreStyleParam)
    }
    function switchState(val,type){
      if(type==='title'){
        if(!val){
          modalSetForm.titleSize=''
          modalSetForm.titleColor=''
          modalSetForm.titleLean=''
          modalSetForm.titleBold=''
          modalSetForm.titleLevel=''
          modalSetForm.titleVertical=''
          modalSetForm.titleContent=''
        }else{
          modalSetForm.titleSize='14'
          modalSetForm.titleColor='#ffffffff'
          modalSetForm.titleLean='normal'
          modalSetForm.titleBold='normal'
          modalSetForm.titleLevel='flex-start'
          modalSetForm.titleVertical='center'
        }
      }else if(type==='border'){
        if(!val){
          modalSetForm.borderStyle=''
          modalSetForm.borderWidth=''
          modalSetForm.borderColor=''
          modalSetForm.borderRound=''
          modalSetForm.scrollBar=false
          modalSetForm.shadowColor=''
          modalSetForm.shadowBlur=''
          modalSetForm.shadowOffsetX=''
          modalSetForm.shadowOffsetY=''
        }else{
          modalSetForm.borderStyle='solid'
          modalSetForm.borderWidth='1'
          modalSetForm.borderColor='#232630'
          modalSetForm.borderRound='4'
          modalSetForm.shadowBlur='0'
          modalSetForm.shadowOffsetX='0'
          modalSetForm.shadowOffsetY='0'
        }
      }else if(type==='isShowTitle'){
        if(!val){
          modalSetForm.titleBgColor=''
          modalSetForm.titleSwitch=false
        }else{
          modalSetForm.titleBgColor='#232630'
        }
      }else if(type==='isLockRadio'){
        if(val){
          modalSetForm.modalRatio = (Number(modalSetForm.modalWidth)/Number(modalSetForm.modalHeight)).toFixed(4)
        }else{
          modalSetForm.modalRatio = (26/15).toFixed(4)
        }
      }
    }
    function changeCloseMethod(val){
      if(val==='blankHide'||val==='timeClose'){
        modalSetForm.btnColor='#ffffffff'
        modalSetForm.btnFlowColor='#ffffffff'
        modalSetForm.btnSize='16'
      }
    }
    //选择图片
    function chooseImage(image: string) {
      console.log('image',image)
      modalSetForm.imageVal=image
      modalSetForm.backImageVal='url('+image+')'
    }
    //清空图片
    function clearImage(){
      modalSetForm.imageVal=''
      modalSetForm.backImageVal=''
    }
    function setSize(val,type){
      if(modalSetForm.isLockRadio){
        if(type==='width'){
          modalSetForm.modalHeight= Math.round(Number(modalSetForm.modalWidth)/Number(modalSetForm.modalRatio))+''
        }
        if(type==='height'){
          modalSetForm.modalWidth = Math.round(Number(modalSetForm.modalHeight)*Number(modalSetForm.modalRatio))+''
        }
        console.log('锁定宽高比',modalSetForm.modalRatio)
      }
    }
    return {
      labelCol: { span: 8 },
      wrapperCol: { span: 14 },
      cancel,
      activeKey,
      modalSetForm,
      dicOptionS,
      handleOk,
      modalSetFormRef,
      moreParam,
      moreParamModalVisible,
      changeCode,
      moreCustonStyle,
      switchState,
      predefineColors,
      changeCloseMethod,
      imageVisible,
      chooseImage,
      clearImage,
      setSize,
    }
  }
})
</script>
<style lang="scss" scoped>
.posOther{
  position: relative;
  margin: 15px 0;
  &::before{
    content:'';
    position:absolute;
    width: 6px;
    height: 1px;
    background: #383a48;
    left: 0;
    top:0;
  }
  &::after{
    content:'';
    position:absolute;
    width: 6px;
    height: 1px;
    background: #383a48;
    left: 0;
    bottom:-1px;
  }
  .FormItem{
    position: relative;
    margin: 0;
    padding: 5px 0 5px 15px;
    &::before{
      content: "";
      position: absolute;
      left: 0;
      top: 0;
      width: 1px;
      height: 100%;
      background:#383a48;
    }
    .el-color-picker{
      vertical-align: middle;
    }
  }
}
.posOther_pos{
  position: relative;
  padding-bottom: 10px;
  &::before{
    content: '';
    display: block;
    position: absolute;
    width: 1px;
    height: 100%;
    background:#383a48;
    left: 0;
    top: 0;
  }
}
.posOther_posOther{
  padding-left: 20px;
  &::before{
    left: 20px;
  }
  &::after{
    left: 20px;
  }
}
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
      .ant-form-item-control-input{
        color: #b4b7c1;
      }
      .ant-select-disabled.ant-select:not(.ant-select-customize-input) .ant-select-selector{
        background: #181a24;
      }
      .ant-select-arrow{
        color: #b4b7c1;
      }
    }
    .ant-collapse{
      background: #232630;
      .ant-collapse-item{
        border:none;
        padding-left:20px;
        .ant-collapse-header{
          color: #b4b7c1;
          padding-left:16px;
          .ant-collapse-arrow{
            margin-left: -25px;
          }
        }
        .ant-btn{
          display: flex;
          align-items: center;
          justify-content: center;
          span{
            height: inherit;
          }
        }
      }
    }
  }
}
</style>