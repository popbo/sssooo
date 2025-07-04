<template>
  <div class="dialogClass" ref="dialogBox"></div>
  <a-modal
  :getContainer="()=>$refs.dialogBox"
    :visible="visible"
    :title="title"
    @ok="handleOk"
    @cancel="cancel"
    wrapClassName="shareModal"
    width="600px"
  >
    <div>
      <!-- <a-form-item
        v-if="shareFlag"
        :labelCol="{ span: 0 }"
        :wrapperCol="{ span: 24 }"
      >
        <a-radio-group v-model:value="isShare" @change="onChange">
          <a-radio :value="true"> 分享公开 </a-radio>
          <a-radio :value="false"> 不分享 </a-radio>
        </a-radio-group>
      </a-form-item> -->
      <a-form-item
        :labelCol="{ span: 4 }"
        :wrapperCol="{ span: 20 }"
        label="是否发布"
      >
        <a-switch v-model:checked="isRelease" @change="cancleRelease"/>
      </a-form-item>
      <template v-if="isRelease">
        <a-form
         ref="formRef"
         :model="customformState"
         :rules="rules"
         >
          <a-form-item
            :labelCol="{ span: 4 }"
            :wrapperCol="{ span: 20 }"
            label="发布版本"
          >
          <a-select v-model:value="disabledParams.version" placeholder="请选择发布版本">
          </a-select>
          </a-form-item>
          <a-form-item
            :labelCol="{ span: 4 }"
            :wrapperCol="{ span: 20 }"
            label="自定义"
          >
            <a-switch  v-model:checked="disabledParams.isCustom" />
          </a-form-item>
          <!-- 当自定义为true时 -->
          <a-form-item
            v-if="disabledParams.isCustom"
            :labelCol="{ span: 4 }" :wrapperCol="{ span: 20 }"
            label="发布链接"
            name="customValue"
            class="link">
              <span class="Custom">
                <span>
                  {{ `http://${hostname}:${port}/2d/` }}
                </span>
                <a-input v-model:value="customformState.customValue" name="customValue"
                placeholder="请输入"  />
                <a-button type="link" @click="onSearch('customValue')">复制</a-button>
              </span>
           </a-form-item>
          <a-form-item 
          v-else
          :labelCol="{ span: 4 }" :wrapperCol="{ span: 20 }"
          label="发布链接" >
          <div class="textareaAndsearch">
            <a-textarea
              v-model:value="link"
               enter-button="复制链接"
              :readonly="true"
              :auto-size="{ minRows: 2, maxRows: 5 }"
            />
            <a-button type="link" @click="onSearch(link)">复制</a-button>
          </div>
          </a-form-item>
          <a-form-item
            :labelCol="{ span: 4 }"
            :wrapperCol="{ span: 20 }"
            label="分享加密"
          >
            <a-switch  v-model:checked="disabledParams.isEnc" />
          </a-form-item>
          <!-- 密码项 -->
          <a-form-item
            v-show="disabledParams.isEnc"
            :labelCol="{ span: 4 }"
            :wrapperCol="{ span: 20 }"
            label="密码"
            name="customPassword"
          >
            <!-- <a-input-password v-model:customPassword="customPassword" placeholder="请输入" /> -->
            <el-input placeholder="请输入密码" v-model="customformState.customPassword" :type="pwdObj.pwdType">
            <template #suffix>
              <el-icon class="el-input__icon" v-if="pwdObj.pwdFlag" @click="getFlag('')"><View /></el-icon>
              <el-icon class="el-input__icon" v-else  @click="getFlag('View')"><Minus /></el-icon>
            </template>
            </el-input>
          </a-form-item>
        </a-form>
       
      </template> 
      <!-- <div class="desc">分享到</div>
      <a-spin :spinning="loading">
        <div class="code-box">
          <div class="code">
            <img class="code-img" src="" id="qrcode" />
            <p class="subdesc">扫一扫</p>
          </div>
          <div class="code">
            <img class="code-img" ref="codeImg" />
            <p class="subdesc">微信扫一扫</p>
          </div>
        </div>
      </a-spin> -->

    </div>
  </a-modal>
</template>

<script lang="ts">
import { defineComponent, nextTick, ref, computed, watch, reactive ,onMounted} from 'vue';
import { Meta2d } from '@topometa2d/core';
import QRCode from 'qrcode';
import { useRoute,useRouter } from 'vue-router';
import { message } from 'ant-design-vue';
import axios from '@/http';
import { User } from '@/services/user';
import { Meta2dBackData } from '../utils';
import { useStore } from 'vuex';
import { View, Minus } from '@element-plus/icons-vue'
declare const meta2d: Meta2d;

export default defineComponent({
  name: 'ShareModal',
  props: {
    visible: {
      type: Boolean,
      require: true,
    },
    title: {
      type: String,
      default: () => {
        return '发布';
      },
    }
  },
  components: {
    Minus,
    View

  },
  emits: ['update:visible','update:isOnRelease','update:isOffRelease'],
  setup(props, { emit }) {
    const link = ref('');
    const isShare = ref(false);
    const loading = ref(false);
    const shareFlag = ref(false);
    const isRelease = ref(false)
    const router = useRouter();
    const disabledParams=reactive({
      version:'',
      isCustom:false,
      isEnc:false
    })
    
    // const wxImg = ref<string>('');
    const route = useRoute();
    const codeImg = ref<any>();
    const store = useStore();
    const user = computed<User>(() => store.state.user.profile);
    function handleOk() {
      console.log('发布的值',meta2d.store.data,)
      if(isRelease.value){
       //有没有自定义链接，有怎么校验
        let arr=[]
        let flag=false
      if (disabledParams.isCustom === true)  arr.push('customValue')
      if (disabledParams.isEnc === true) arr.push('customPassword')
      if(arr.length!==0){  
          formRef.value.validateFields(arr).then(() => { 
          }).catch(err=>{
           flag=true
          })
        } 
      setTimeout(()=>{
           if (flag) return //验证不通过中断
           release(isRelease.value)
          // const link = router.resolve({
          //   path: '/release',
          //   query:{
          //     r: Date.now() + '',
          //     id:route.query.id
          //   }
          // });
          // console.log('link',link)
          // window.open(link.value, '_blank')
          // // 按下确认以后修改外界值
          emit('update:visible', false);
          emit('update:isOnRelease', true);
      })
      }else{
        message.info('请开启发布按钮')
      }
    }

    function cancel() {
      emit('update:visible', false);
    }
    function onChange(e) {
      share(e.target.value);
    }
    function onSearch(value) {
      console.log(value);
      
      value==='customValue'? copy2clipboard(customLink.value):copy2clipboard(value);
      message.success('复制链接成功！');
    }
    function copy2clipboard(copyText: string) {
      //1 手动创建可编辑元素，比如textarea，
      var textArea = document.createElement('textarea');
      //2 然后将要拷贝的值设置为它的Value
      textArea.value = copyText;
      //将textarea插入页面
      document.body.appendChild(textArea);
      //调用textarea的 select() 方法将值选中
      textArea.select();
      // 3 textarea要设置样式为不可见(使用样式将其移出可视区域即可)
      textArea.style.position = 'fixed';
      textArea.style.top = '-9999px';
      textArea.style.left = '-9999px';
      // 4 调用document.execCommand('copy')
      document.execCommand('copy'); // 复制
      // document.execCommand('cut')// 剪切
      textArea.remove();
    }
    async function release(shared: boolean) {
      const id = route.query.id;
      if (!id) {
        // 提示需要先保存
        message.warn('请先保存！');
        return;
      }
      const data: Meta2dBackData = meta2d.data();
      let collection = data.component ? 'topo2d-components' : 'topo2d';
      console.log('data',meta2d.store.data,data)
      const params={
        _id:data._id,
        shared,
        version:data.version,
        sharedurl: disabledParams.isCustom ?customLink.value:link.value,// 自定义接口？自定义链接||默认链接
        sharedcustom: disabledParams.isCustom,//自定义接口是否开启
        sharedencryption: disabledParams.isEnc,//自定义密码是否开启
        sharedpassword: disabledParams.isEnc? customformState.customPassword:null//自定义密码
      }
      const res:any = await axios.post(`/data/${collection}/share`, params);
      console.log('发布接口',res)
      if (res.error) {
        return;
      }
      (meta2d.store.data as Meta2dBackData).shared = shared;//发布页是否开启
      (meta2d.store.data as Meta2dBackData).sharedcustom=disabledParams.isCustom,//自定义链接是否开启
      (meta2d.store.data as Meta2dBackData).sharedurl= disabledParams.isCustom ? customLink.value : link.value,//自定义链接是否开启
      (meta2d.store.data as Meta2dBackData).sharedencryption = disabledParams.isEnc;//自定义密码是否开启
      (meta2d.store.data as Meta2dBackData).sharedpassword = disabledParams.isEnc ? customformState.customPassword : null;//自定义密码
      message.success(shared ? '发布成功！' : '取消发布成功');
    }
    // 取消发布
    async function cancleRelease(val){
      if(!val){
        release(false)
        emit('update:isOffRelease', false);
      }
    }
    //TODO 配置shared无用，图纸不能根据用户id查找
    async function share(shared: boolean) {
      const id = route.query.id;
      if (!id) {
        // 提示需要先保存
        message.warn('请先保存！');
        return;
      }
      const res:any = await axios.post(`/data/topo2d/update`, {_id:id,shared});
      // TODO: 判断是否登录
      // const res: any = await axios.patch('/api/user/meta2d', {
      //   id,
      //   shared,
      // });
      if (res.error) {
        return;
      }
      (meta2d.store.data as Meta2dBackData).shared = shared;
      message.success(shared ? '分享成功！' : '取消分享成功');
    }
    function getQRcode(url: string) {
      var opts = {
        errorCorrectionLevel: 'H',
        type: 'image/jpeg',
        quality: 0.3,
        margin: 1,
        width: 120,
        height: 120,
      };

      QRCode.toDataURL(url, opts, (err, url) => {
        if (err) throw err;

        var img: HTMLElement = document.getElementById('qrcode');
        (img as HTMLImageElement).src = url;
      });
    }
    //TODO 小程序码接口
    async function getWXCode(id: string) {
      const res: any = await axios.get('/account/miniprogram/code', {
        params: { id },
        responseType: 'blob',
      });
      codeImg.value.src = URL.createObjectURL(res); // 将生成的blob对象的值赋值给img的src属性
      codeImg.value.onLoad = () => {
        URL.revokeObjectURL(codeImg.value.src); // 在图片加载完成后释放
      };
    }

    watch(
      () => props.visible,
      (newV) => {
        if (newV) {
          const id = route.query.id;
          const locationOrigin=window.location.origin
          const url = router.resolve({
            path: '/release',
            query:{
              r: Date.now() + '',
              id:route.query.id
            }
          });
          link.value = locationOrigin+url.href;
          const data: Meta2dBackData = meta2d.data();
          // 只有图纸的拥有者才有分享和取消分享的曲线
          shareFlag.value = data.owner.id === user.value.id;
          nextTick(async () => {
            loading.value = true;
            isShare.value = (meta2d.store.data as Meta2dBackData).shared||false;
            isRelease.value= (meta2d.store.data as Meta2dBackData).shared || false;
            disabledParams.version= (meta2d.store.data as Meta2dBackData).version || '1.0.0';
            disabledParams.isCustom =  (meta2d.store.data as Meta2dBackData).sharedcustom ||false;
            disabledParams.isEnc = (meta2d.store.data as Meta2dBackData).sharedencryption ||false;
            customformState.customValue = (meta2d.store.data as Meta2dBackData).sharedurl?.split('share=')[1]||'';
            customformState.customPassword = (meta2d.store.data as Meta2dBackData).sharedpassword ||''
            // try {
            //   await getQRcode(url);
            // } catch (error) {
            //   message.warning('获取二维码失败！');
            // }
            // try {
            //   await getWXCode(id as string);
            // } catch (error) {
            //   message.warning('获取微信小程序码失败！');
            //   loading.value = false;
            // }
            loading.value = false;
          });
        }
      }
    );
    //定义
    const hostname = window.location.hostname//域名
    const port = window.location.port;//端口
    const customformState=reactive<{customValue:string, customPassword:string}>({
      customValue:'',//自定义链接
      customPassword:''//自定义密码
    })
    const id = computed(()=>{
      return  route.query.id
    })
    const customLink= computed(()=>{
      return `http://${hostname}:${port}/2d/release?id=${id.value}&share=${customformState.customValue}`
    })

    //校验
    const formRef = ref();
    const rules= {
      customValue: [
        { required: true, message: '不能为空请输入自定义链接' },
        { pattern: /^[a-zA-Z0-9]+$/, message: '无法使用中文和特殊字符' },
      ],
      customPassword: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { pattern: /^[^\u4e00-\u9fa5\s]+$/, message: '密码不能包含中文和空格', trigger: 'blur' },
        { min: 6, max: 16, message: '密码长度为6~16位', trigger: 'blur' },
      ],
    };
   // 密码图标切换
   interface PasswordObject {
      pwdType: string;
      pwdFlag:boolean;
    }
    const pwdObj = reactive<PasswordObject>({ pwdType: 'password' , pwdFlag :true});
    const getFlag =(str:String):void=>{
      if(str=== 'View'){
        pwdObj.pwdFlag = true
        pwdObj.pwdType = 'password'
      }else {
        pwdObj.pwdFlag = false
        pwdObj.pwdType = 'text'
      }
  }
    return {
      link,
      handleOk,
      cancel,
      onChange,
      isShare,
      onSearch,
      codeImg,
      loading,
      shareFlag,
      isRelease,
      disabledParams,
      cancleRelease,
      hostname,
      port,
      customformState,
      formRef,
      id,
      rules,
      pwdObj,
      getFlag
    };
  },
});
</script>

<style lang="scss" scoped>
.ant-modal-wrap{
  z-index: 9999 !important;
}
.shareModal {
  .ant-row.ant-form-item {
    margin-bottom: 24px;
    :deep(.ant-form-item-label){
      padding-top: 0!important;
    }
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
      .ant-form-item-control{
         .Custom{
          display:flex;
          align-items:center;
          font-size:14px;
          color:#b4b7c1;
          white-space:nowrap;//换行
          .ant-input{
           width:120px
          }
          .ant-btn-link{
            margin:0 0 0 5px;
            padding: 0;
            font-size: 14px;
          }
        }
        .textareaAndsearch{
          display: flex;
          align-items: center;
          &>{
          .ant-input{
           width:100%
          }
          }
        }
       
        .el-input__inner{
          height:px;
          background-color:#191a24;
          border:none;
          color:#b4b7c1;
        }
      }
      .ant-select-disabled.ant-select:not(.ant-select-customize-input) .ant-select-selector{
        background: #181a24;
      }
      .ant-select-disabled .ant-select-arrow{
        color: #b4b7c1;
      }
    }
  }
}
</style>
