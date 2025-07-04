<template>
  <div class="copyModel" ref="copyModel">
    <a-button type="primary" @click="showModal" class="btn"
      >工程化管理</a-button
    >
    <a-modal
      title="工程化编辑"
      v-model:visible="visible"
      width="700px"
      :getContainer="() => $refs.copyModel"
      @ok="handleOk"
      @cancel="handleCancel"
      ok-text="一键同步"
      cancel-text="取消"
    >
      <a-button type="primary" @click="addItem('全局')" class="antBtn">
        <PlusCircleOutlined />
        全局同步</a-button
      >
      <a-button type="primary" @click="addItem('组件')" class="antBtn">
        <PlusCircleOutlined />组件同步</a-button
      >
      <div
        class="content"
        :class="
          formState.domains.length === 0 && formState.selcets.length === 0
            ? 'temporary-bgc'
            : 'content'
        "
      >
        <!-- 全局 -->
        <div v-if="flag.globalFlag">
          <p>· 全局同步</p>
          <div
            :class="
              formState.selcets.length === 0
                ? 'temporary-height'
                : 'globalContent'
            "
          >
            <a-form
              ref="formRef"
              :model="formState"
              layout="inline"
              autocomplete="off"
            >
              <a-row type="flex" justify="space-around">
                <!-- 1 -->
                <a-form-item
                  v-for="(domain, index) in formState.domains"
                  :key="domain.id"
                  v-bind="formItemLayout"
                  :label="domain.id % 2 === 1 ? '查找内容:' : '替换为'"
                  :name="['domains', index, 'value']"
                >
                  <a-input
                    v-model:value="domain.value"
                    placeholder="请输入"
                    style="width: 85%; background-color: #191a24"
                  />
                  <DeleteOutlined
                    v-if="domain.id % 2 === 0"
                    class="dynamic-delete-button"
                    @click="removeDomain('全局', domain)"
                  />
                </a-form-item>
              </a-row>
            </a-form>
          </div>
        </div>

        <!-- 组件 -->
        <div v-if="flag.componentFlag">
          <p>· 组件同步
            <a-tooltip placement="right">
              <template #title><span>建议在图元数据面板修改组件名称</span></template>
              <i class="t-icon t-help-circle"></i>
            </a-tooltip>
          </p>
          <div
            :class="
              formState.domains.length === 0
                ? 'temporary-height'
                : 'globalContent'
            "
            id="Select"
          >
            <a-form ref="formSelectRef" :model="formState" autocomplete="off">
              <a-row type="flex" justify="space-around">
                <a-col :span="6" style="padding-left: 15px">样板组件 </a-col>
                <a-col :span="6">操作类型 </a-col>
                <a-col :span="6">配置项 </a-col>
                <a-col :span="6">目标组件 </a-col>
              </a-row>
              <a-row
                type="flex"
                justify="space-around"
                style="padding-left: 10px"
              >
                <a-form-item
                  v-for="(select, index) in formState.selcets"
                  :key="select.id"
                >
                  <a-select
                    size="default"
                    show-search
                    allow-clear
                    optionFilterProp="label"
                    style="width: 150px"
                    v-model:value="select.value"
                    placeholder="请选择"
                    :options="options0"
                    :autoClearSearchValue="false"
                    v-if="select.id % 4 === 1"
                  ></a-select>
                  <!-- 配置项组件 -->
                  <a-tree-select
                    v-else-if="select.id % 4 === 2|| select.id % 4 === 3"
                    :autoClearSearchValue="false"
                    v-model:value="select.value"
                    style="width: 150px"
                    dropdownClassName="treeSelcect123"
                    :dropdownStyle="{
                      minHeight: '30px',
                      maxHeight: '220px',
                      overflow: 'auto',
                    }"
                    dropdownMatchSelectWidth
                    :tree-data="select.id % 4 !== 0 ? options[select.id % 4] :[{title:'全选',key:'allsecect',value:'allsecect',options: options0.filter(item => item.value !== formState.selcets[index - 3]?.value)}]"
                    :disabled="
                      select.id % 4 === 3 &&
                      !formState.selcets[index - 1].value?.includes('style')
                    "
                    tree-checkable
                    allow-clear
                    placeholder="请选择"
                    :maxTagCount="0"
                    :replaceFields="{ children: 'options' }"
                    treeNodeFilterProp="title"
                    :getPopupContainer="
                      (triggerNode) => {
                        return triggerNode.parentNode;
                      }
                    "
                    :treeDefaultExpandedKeys="['allsecect']"
                    @change="testFn(select, index)"
                  >
                  </a-tree-select>

                  <!-- 目标组件 -->
                  <OptionSelect
                  v-else
                  :width="'150px'" 
                  :options="options[0]" 
                  v-model:value="select.value" 
                  placeholder="请选择"></OptionSelect>
                  <!-- 删除按钮 -->
                  <DeleteOutlined
                    v-if="select.id % 4 === 0"
                    class="dynamic-delete-button"
                    @click="removeDomain('组件', select)"
                  />
                </a-form-item>
              </a-row>
            </a-form>
          </div>
        </div>
      </div>
    </a-modal>

    <!-- 第二模态框 -->
    <a-modal
      title="提示"
      v-model:visible="modal2Visible"
      width="500px"
      centered
      :getContainer="() => $refs.copyModel"
      @ok="handleOk2"
    >
      <p style="display: flex; justify-content: center">
        <ExclamationCircleFilled
          style="
            font-size: 48px;
            background-color: #232531;
            color: orange;
            border-radius: 24px;
          "
        />
      </p>
      <p>
        本次操作将修改页面中的全局同步{{ arrNumber[0] }}个属性，组件同步{{
          arrNumber[2]
        }}个属性，是否确认修改？
      </p>
      <!-- 加载动画 -->
      <div class="aspin" :style="{display: spinning ? 'block' : 'none'}">
        <img src="@/assets/loading.gif" alt="加载中">
      </div>
    </a-modal>
  </div>
</template>
<script lang="ts">
import {
  ref,
  defineComponent,
  toRaw,
  computed,
  reactive,
  onUnmounted,
  toRefs,
  watch,
  UnwrapRef,
  createVNode,
} from 'vue';

import { Modal, message } from 'ant-design-vue';
import {
  PlusCircleOutlined,
  DeleteOutlined,
  ExclamationCircleFilled,
} from '@ant-design/icons-vue';
import { Meta2d, Pen } from '@topometa2d/core';
import { cloneDeep } from 'lodash';
import  OptionSelect from './common/OptionSelect.vue'
import {globalSynchronization} from '@/api/communication'
interface FormState {
  value: string;
  id: number;
}
import {Meta2dBackData,} from '../utils';
import axios from '@/http';
declare const meta2d: Meta2d;
interface cData {
  events?: any[];
  showDuration?: Number;
  animateCycle?: Number;
  animateType?: string;
  frames?: any[];
  autoPlay?: Boolean;
  keepAnimateState?: Boolean;
  nextAnimate?: string;
  linear?:Boolean;
  title?: string;
  titleFn?: Function;
  titleFnJs?: string;
  form?: any[];
  tags?: any[];
  text?: string;
  lineAnimateType?: string;
  animateDash?: number;
  animateLineDash?:any;
  animateDotSize?: string;
  animateColor?: string;
  animateSpan?: string;
  animateReverse?: Boolean;
  type?: number;
}
export default defineComponent({
  name: 'CopyModel',
  components: {
    PlusCircleOutlined,
    DeleteOutlined,
    ExclamationCircleFilled,
    VNodes: (_, { attrs }) => {
      return attrs.vnodes;
    },
    OptionSelect,
  },
  setup() {
    const visible = ref<Boolean>(false);
    const confirmLoading = ref<Boolean>(false);
    const spinning = ref<Boolean>(false);
    const flag = computed(() => {
      flag.globalFlag = formState.domains.length > 0 ? true : false;
      flag.componentFlag = formState.selcets.length > 0 ? true : false;
      return flag;
    });
    // 增加表单
    const addItem = (str) => {
      if (str === '全局') {
        let id = formState.domains[formState.domains.length - 1]?.id + 1 || 1;
        formState.domains.push(
          {
            value: '',
            id: id,
          },
          {
            value: '',
            id: id + 1,
          }
        ); //一次增添两个表单
      }
      if (str === '组件') {
        let id = formState.selcets[formState.selcets.length - 1]?.id + 1 || 1;
        formState.selcets.push(
          {
            value: null,
            id: id,
          },
          {
            value: [],
            id: id + 1,
          },
          {
            value: [],
            id: id + 2,
          } ,
          {
          value: [],
          id: id + 3,
        },
        ); //一次增添四个下拉框
      }
    };
    const formRef = ref(); //获取form 组件
    const formSelectRef = ref(); //获取formSelectRef 组件
    // 双数后面增加一个删除图标
    const formItemLayout = reactive({
      labelCol: {
        xs: { span: 12 },
        sm: { span: 12 },
      },
      wrapperCol: {
        xs: { span: 22 }, //分辨率小于567px时运用
        sm: { span: 22 }, //分辨率大于567px时运用
      },
    }); //单个表单的样式

    const formState: UnwrapRef<{ domains: FormState[]; selcets: any[] }> =
      reactive({
        domains: [],
        selcets: [],
      }); //表单绑定的变量

    // 删除   要移除当前输入框和上一个输入框
    const removeDomain = (str: String, item: FormState) => {
      if (str === '全局') {
        let index = formState.domains.indexOf(item);
        if (index !== -1 && index >= 1 && index % 2 === 1) {
          //idnex1、必须是奇数；2、必须大于等于1；3、必须存在不为-1
          formState.domains.splice(index - 1, 2); //当前及前一个
        }
      }
      if (str === '组件') {
        let index = formState.selcets.indexOf(item);
        if (index !== -1 && index >= 2 && index % 4 === 3) {
          //idnex1、必须是2；2、必须大于等于2；3、必须存在不为-1
          formState.selcets.splice(index - 3, 4); //4个
        }
      }
    };

    // 解析图纸各个图元data,打开获取当前图纸的所有图元
    const data = ref([]);
    let stopVisible = null;
    stopVisible = watch(visible, (newVal) => {
      if (newVal) {
        data.value.length = 0;
        data.value.push(...cloneDeep(meta2d.store.data.pens));
        // 清空domains和selcets
        formState.domains.length = 0;
        formState.selcets.length = 0;
      }
    });

    // 修改表单
    // 全局表单将查找id的内容替换为id+1对应的内容
    // 全局组件 批量修改
    let changeArrIndex=[]//待修改图元的序列号 changeDraw->batchUpdateData->updateData
    let time=0
    const updateData = async (arrDate) => {
      for (let i = 0; i < formState.domains.length; i++) {
        const { value, id } = formState.domains[i];//value是查找内容
        if (id && id % 2 === 1 && value.trim() !== "") {
          if(value=== formState.domains[i + 1].value) return //如果查找value 和替换formState.domains[i + 1].value 完全相同直接return
         let regValue = new RegExp(value.toString()?.toUpperCase());
          let regValueStr = new RegExp(value, "gi");
           arrDate.map((j)=>{
            const _item = data.value[j];
            if (_item.text === undefined|| _item.text ===null) _item.text='';//如果图元没有给个空字符串
            let replce_item = cloneDeep(_item);
            if (value === _item.text || regValue.test(_item.text.toString()?.toUpperCase())) {//查询内容完全相同或者部分相同
              console.log("replce_item.text", replce_item.text);
              meta2d.setValue({
                id: _item.id,
                text: replce_item.text.replaceAll(regValueStr, cloneDeep(formState.domains[i + 1].value)),
              }, { render: false });
            }
          })
        }
      }
    };
    function batchUpdateData(dataArr) {
      const dataSize = dataArr.length;
      time = dataSize>=100 ? 1000:0
       updateData(dataArr);
    }
    const changeDraw =async () => {
      // TODO什么时候去全局渲染 不是替换是修改关键字段 1、domains、selcets数组不为空2、 domains[x]、selcets[x]不可以为空 为空 则需要提示用户    ---->渲染后需不需要去调用接口去保存
      if (formState.domains.length > 0) {
        // setTimeout(()=>{
        //   batchUpdateData(changeArrIndex)
        // })

        //发送请求
        interface MyObject {
          id: string;
          text: string;
          newtext: string;
        }
        const obj: MyObject={  
          id:'',
          text: '',
          newtext: '',
        }
        const arr: MyObject[]=[]
        for (let index = 0; index < formState.domains.length; index++) {
            const item= formState.domains[index].value
            if(index%2===0){
              obj.text = item
            }else{
              obj.newtext = item
              obj.id=meta2d.data()._id
              arr.push({...obj})
            }
        }
        // 如果没有要修改推出
        if(arrNumber.value[0]===0) return
        // 先把图纸传过去 把要修改的内容传过去
        const data: Meta2dBackData = meta2d.data();
        await axios.post(`/data/topo2d/update`, data).then((ret1)=>{ 
          // globalSynchronization({ sysList: arr })
        })
        globalSynchronization({ sysList: arr }).then((ret)=>{
          meta2d.open(ret as any);
        }).catch(error=>{
         return console.log(error)
        })
      }
      // 组件渲染
      if (formState.selcets.length > 0) {
        // 1、获取目标图纸
        formState.selcets.forEach(({ value, id }, index) => {
          // 只进入样板组件
          if (id && id %  4 === 1) {
            // 解析需要修改的东西
            const souceItem = meta2d.findOne(value); //获取源图纸文件
            const someThing = formState.selcets[index + 1].value; //数组['action','event']
            const configItem = formState.selcets[index + 2].value;//配置项---》前提是得选复用样式
            const targetItem = formState.selcets[index + 3].value; //数组['id']长度是多少个组件
            let arr = getParmars(souceItem, someThing, configItem);
            setParmars(targetItem, arr[0]); //目标图纸设置相关参数
            // event是1，action 10，data:3--->
          }
        });
        // 渲染
      }
      setTimeout(()=>{
        spinning.value = false
        modal2Visible.value = false;
        visible.value = false
        meta2d.render()
        success()//提示用户修改成功
      }, time)
    };
    // 局部组件 修改
    // 配置项的options
    const options2 = [
      {
        title: '全选',
        value: 'allsecect',
        key: 'allsecect',
        options:[
          {
            value: 'style',
            label: '复用外观',
          },
          {
            value: 'event',
            label: '复用事件',
          },
          {
            value: 'action',
            label: '复用动效',
          },
          {
            value: 'data',
            label: '复用数据',
          },
        ]
      }
      
    ];
    const options3 = ref([
      {
        title: '全选',
        value: 'allsecect',
        key: 'allsecect',
        options: [
          {
            title: '位置和大小',
            value: 'positionAndsize',
            key: 'positionAndsize',
            options: [
              {
                value: 'borderRadius',
                label: '圆角',
              },
              {
                value: 'rotate',
                label: '旋转',
              },
              {
                value: 'paddingLeft-paddingRight-paddingTop-paddingBottom',
                label: '边距设置', //内边距左-内边距右-内边距上-内边距下
              },
              {
                value: 'progress-progressColor-verticalProgress',
                label: '进度设置', //进度-进度颜色-垂直进度
              },
              {
                value: 'flipX-flipY',
                label: '翻转设置', //水平翻转和垂直翻转
              },
            ],
          },
          {
            title: '样式',
            value: 'lineStyle',
            key: 'lineStyle',
            options: [
              {
                value:
                  'dash-lineCap-lineJoin-strokeType-lineGradientFromColor-lineGradientToColor-lineGradientAngle-lineDash',
                label: '线条样式', //线条-末端-连接-线性渐变-开始颜色-结束颜色-渐变角度
              },
              {
                value: 'color-hoverColor-activeColor',
                label: '线条颜色', //颜色-浮动颜色-选中颜色
              },
              {
                value: 'lineWidth',
                label: '线条宽度',
              },
              {
                value: 'borderColor-borderWidth',
                label: '线条边框',//边框颜色-板框宽度--->此属性在为线条时展示
              },
              {
                value:
                  'bkType-gradientFromColor-gradientToColor-gradientAngle-gradientRadius-background-hoverBackground-activeBackground',
                label: '背景颜色', //背景-开始颜色-结束颜色-渐变角度-背景颜色-浮动背景颜色-选中背景颜色
              },
              {
                value: 'globalAlpha',
                label: '透明度',
              },
              {
                value:
                  'shadowColor-shadowBlur-shadowOffsetX-shadowOffsetY-textHasShadow',
                label: '阴影设置', //阴影颜色-阴影模糊-阴影x偏移-阴影y偏移
              },
            ],
          },
          {
            title: '文字',
            value: 'fontStyle',
            key: 'fontStyle',
            options: [
              {
                value: 'fontFamily',
                label: '字体',
              },
              {
                value: 'fontStyle-fontWeight-textAlign-textBaseline',
                label: '字体样式', //倾斜、加粗、水平对齐、垂直对齐
              },
              {
                value: 'fontSize',
                label: '字体大小',
              },
              {
                value:
                  'textColor-hoverTextColor-activeTextColor-textBackground',
                label: '字体颜色', //文字颜色、浮动文字颜色、选中文字颜色、文字bei'jing
              },
              {
                value: 'lineHeight',
                label: '行高设置',
              },
              {
                value: 'whiteSpace',
                label: '换行设置',
              },
              {
                value: 'ellipsis',
                label: '超出省略',
              },
              {
                value: 'keepDecimal',
                label: '保留小数',
              },
              {
                value: 'hiddenText',
                label: '隐藏文字',
              },
            ],
          },
          {
            title: '图片',
            value: 'image',
            key: 'image',
            options: [
              {
                value: 'image-backgroundImage-strokeImage',
                label: '图片选择',
              },
              {
                value: 'iconWidth-iconHeight-imageRatio',
                label: '宽高比',
              },
              {
                value: 'iconLeft-iconTop-iconAlign',
                label: '偏移对齐',
              },
            ],
          },
          {
            title: '字体图标',
            value: 'fontIcon',
            key: 'fontIcon',
            options: [
              {
                value: 'icon-iconFamily',
                label: '图标选择',
              },
              {
                value: 'iconSize-iconRotate',
                label: '大小与旋转',
              },
              {
                value: 'iconColor',
                label: '图标颜色',
              },
            ],
          },
          {
            title: '禁止',
            value: 'disable',
            key: 'disable',
            options: [
              {
                value: 'disableInput',
                label: '禁止输入',
              },
              {
                value: 'disableRotate',
                label: '禁止旋转',
              },
              {
                value: 'disableSize',
                label: '禁止缩放',
              },
              {
                value: 'disableAnchor',
                label: '禁用锚点',
              },
            ],
          },
        ],
      },
    ]);
    // 拍平后的options3
    const allOption3 = options3.value[0].options.map((_item, _index) => {
      return _item.options.map((_item, _index) => {
        return _item.value;
      });
    });
    const options0 = computed(() => {
      return data.value.map((item) => {
        if (item) return { value: item.id, label: item.description || item.name };//有名称使用名称没有使用name
      });
    });
    const options = reactive({
      '0': options0, //id 为4，8的  TODO依据1剔除生成后面的选择项
      '1': options0, //id为1，5
      '2': options2, //id为2，6的
      '3': options3 //id为3，7的
    });
    const getParmars = (souceItemFrom: Pen, arr: any[],config?:any[]) => {
      // 循环someThing
      let num = 0;
      let souceItem=cloneDeep(souceItemFrom)
      let newArr= cloneDeep(arr)
      const cData = reactive<cData>({});
      newArr.forEach((item) => {
        switch (item) {
          case 'event':
            // 提取源图纸的事件
            cData.events = souceItem?.events || null;
            num++;
            break;
          case 'action':
            // 分为线条动画和系统组件动画 根据pen.type属性0是node ， 1是line
            // 提取源图纸的动效参数
            if(souceItem.type===1){
              cData.lineAnimateType= (souceItem as any)?.lineAnimateType || null//线条动画效果
              cData.animateDash= (souceItem as any)?.animateDash || 0 //动画线条--->lineAnimateType=1时
              cData.animateLineDash = (souceItem as any)?.animateLineDash ||undefined
              cData.animateDotSize= (souceItem as any)?.animateDotSize || null//圆点大小---》lineAnimateType=2时
              cData.animateColor= (souceItem as any)?.animateColor || null//动画颜色
              cData.animateSpan = (souceItem as any)?.animateSpan || null//线条动画速度
              cData.animateReverse = (souceItem as any)?.animateReverse || false//线条反向滚动
            }else{
              cData.showDuration = (souceItem as any)?.showDuration || null; //时长
              cData.animateType = (souceItem as any)?.animateType || null; //动画效果
              cData.frames = souceItem?.frames || null; //动画效果参数
            }
            cData.animateCycle = souceItem?.animateCycle || null; //循环次数
            cData.autoPlay = souceItem?.autoPlay || null; //自动播放
            cData.keepAnimateState = souceItem?.keepAnimateState || null; //保持动画状态
            cData.nextAnimate = souceItem?.nextAnimate || null; //下个动画
            cData.linear=souceItem?.linear//线性播放false问题
            cData.title = souceItem?.title || null; //Markdown
            cData.titleFn = souceItem?.titleFn || null; //Mark函数
            cData.titleFnJs = souceItem?.titleFnJs || null; //Mark函数
            num += 10;
            break;
          case 'data':
            // 提取源图纸的绑定数据参数
            cData.form = souceItem?.form || null;
            cData.tags = souceItem?.tags || null;
            cData.text = souceItem?.text || null;
            num += 3;
            break;
          case 'style':
            // 提取源图纸的绑定的样式参数
            // 根据配置项去复制
            let configArr= cloneDeep(config)
            if(configArr.length===0) {
              throw('当操作类型有复用外观时配置项不能为空')
            }else{
              // 当目标组件不是线条，重置目标组件边框颜色和宽度
              if (souceItem.type !== 1) {
                souceItem['border-color'] = null
                souceItem['borderWidth'] = 0
              }
              configArr.join('-').split('-').map(item => {
                  //当item=allseclet时跳过，allsecect不是一个属性
                cData[item] = souceItem ? souceItem[item] : null
              })
              num += configArr.join('-').split('-').length;
            }
            break;
          default:
            break;
        }
      });
      return [cData, num];
    };
    const setParmars = (arr: any[], obj: {}) => {
      arr.forEach((item) => {
        meta2d.setValue({ id: item, ...cloneDeep(obj) } ,{ render: false });//每份数据不同
      });
       meta2d.render();
    };

    // 打开模态框
    const showModal = () => {
      visible.value = true;
    };

    // 确认模态框
    // 1、修改属性的个数判断
    const getNumber = () => {
      let i = 0; //全局修改属性个数
      let y = 0; //局部修改组件个数
      let z = 0; //局部修改属性个数
      let flag = false;
      if (formState.domains.length === 0 && formState.selcets.length === 0) {
        return [i, y, z, !flag];
      } //TODO直接提示内容不能为空
      if (formState.domains.length > 0) {
        changeArrIndex.length=0//重置
        formState.domains.forEach(({ value, id }, index) => {
          if (id && id % 2 === 1 && value.trim()!=='') {
          let regValue=new RegExp(value?.toUpperCase())
            data.value.forEach((_item,_index) => {
              if(_item.text===undefined|| _item.text === null) return//当 _item.text
              if (value === _item.text||regValue.test(_item.text.toString()?.toUpperCase())) {
                changeArrIndex.push(_index)
                i++; //同步属性的个数
              }
            });
          }
          // 判断全局各个值是否为空
          if (value.trim() === '') {
            flag = true;
          }
        });
      }

      if (formState.selcets.length > 0) {
        // 1、获取目标图纸
        formState.selcets.forEach(({ value, id }, index) => {
          // 只进入样板组件
          if (id && id % 4 === 1) {
            // 解析需要修改的东西
            const souceItem = meta2d.findOne(value); //获取源图纸文件
            const someThing = formState.selcets[index + 1].value; //数组['style','action','event']
            const configItem= formState.selcets[index + 2].value;//配置项---》前提是得选复用样式
            const targetItem = formState.selcets[index + 3].value; //数组['id']长度是多少个组件
            let arr = getParmars(souceItem, someThing, configItem);
            z += (arr[1] as number) * targetItem.length;
            // event是1，action 10，data:3--->
            // 判断组件的各个值是否为空
            if (
              souceItem === undefined ||
              someThing.length === 0 ||
              targetItem.length === 0
            ) {
              flag = true;
            }
          }
        });
      }
      return [i, y, z, flag];
    };
    const warning = () => {
      message.warning('内容不能为空！');
    };
     const success = () => {
      message.success('修改成功');
    };
    // 组件同步
    const handleOk = () => {
      // 打开第二层
      try {
      arrNumber.value = getNumber();
      if (arrNumber.value[3]) {
          return warning();
        }
      modal2Visible.value = true;
      } catch (error) {
        message.warning(error);//弹出警告
      }
    };
    const handleCancel = () => {
      Modal.destroyAll();
      visible.value = false;
    };

    //第二层模态框
    const modal2Visible = ref<boolean>(false);
    const arrNumber = ref([]);
    const handleOk2 = () => {
      if (changeArrIndex.length >= 20) spinning.value = true//当修改图元超过100个才回去加载动画
      setTimeout(() => {
      changeDraw();
      }, 1000);
    };

    //todo 监视属性-->监视所有的select判断
    // 配置项函数
    const testFn=(obj,index)=>{
      if(obj.id%4===2&& !obj.value?.includes('style')){
        // 检查选择的obj中是否含有style,没有将重置后一个选择框的内容
        formState.selcets[index+1].value.length = 0;
      }
    }
    onUnmounted(() => {
      stopVisible();
    });

    return {
      visible,
      confirmLoading,
      flag,
      addItem,
      formRef,
      formSelectRef,
      formItemLayout,
      formState, //表单绑定变量
      showModal,
      handleOk,
      handleOk2,
      handleCancel,
      removeDomain,
      options,
      options2,
      options0,
      data,
      modal2Visible,
      arrNumber, //2 模态 更改属性的数量
      testFn,
      allOption3,
      spinning,
    };
  },
});
</script>

<style lang="scss">
// 修改配置项下拉框
:deep(.ant-select-item-group) {
  background-color: blue !important;
}
</style>
<style lang="scss" scoped>
.copyModel {
  :deep(.ant-modal-content) {
    background-color: #242630;
    color: #fff;
  }
  :deep(.ant-modal-header) {
    background-color: #242630;
    margin: 0 20px;
    padding-left: 0;
    border-bottom: 1px solid #34353e;
    .ant-modal-title {
      color: #9c9ea7;
    }
  }
  .ant-modal-body h6 {
    color: #9c9ea7;
  }
  :deep(.ant-modal-content) {
    text-align: left;
  }
  :deep(.ant-modal-footer) {
    border-top: none;
    .ant-btn {
      border: none;
      background-color: #3e3f4b;
      color: #fff;
      &:last-child {
        background-color: #5781f9;
      }
    }
  }
  :deep(.ant-form) {
    color: #9c9ea7;
  }
  :deep(.ant-modal-close) {
    color: #9c9ea7;
  }
  // 修改查找内容部分
  :deep(.ant-form-item-label) {
    padding-top: 0 !important;
    label {
      color: #9c9ea7 !important;
    }
  }
  :deep(.ant-form-inline) {
    display: flex;
    justify-content: center;
  }
  // 修改箭头颜色
  :deep(.ant-select-arrow) {
    .ant-select-suffix {
      color: #fff;
    }
  }
  .ant-checkbox-checked :deep(.ant-checkbox-inner) {
    background-color: red;
    border-color: red;
  }

  .ant-select-multiple.ant-select-disabled.ant-select:not(
      .ant-select-customize-input
    )
    :deep(.ant-select-selector) {
    background-color: #191a24;
  }
  .selectDataSourceForm {
    :deep(.ant-row) {
      flex-direction: column;
      .ant-form-item-label {
        text-align: left;
        label {
          color: #b4b7c1;
        }
      }
    }
  }
  .btn {
    width: 281px;
    background-color: #3a89fe;
  }
  .antBtn {
    background-color: #3a89fe;
    border-radius: 2px;
    margin: 0 7px 7px 0;
  }
  .content {
    height: 400px;
    overflow: hidden;
    color: #fff;
    .globalContent {
      max-height: 150px;
      overflow: scroll;
      border: 1px solid #3d3f4c;
      padding-top: 10px;
    }
    .temporary-height {
      height: 300px !important;
      overflow: scroll;
      border: 1px solid #3d3f4c;
      padding-top: 10px;
    }
  }
  .temporary-bgc {
    background: url(@/assets/Empty.png) no-repeat center;
  }
  .dynamic-delete-button {
    cursor: pointer;
    position: relative;
    top: 4px;
    font-size: 18px;
    color: #999;
    transition: all 0.3s;
    margin-left: 5px;
  }
  .dynamic-delete-button:hover {
    color: #777;
  }
  //treeseclect样式
  #Select {
    :deep(.ant-select-tree-dropdown) {
      background-color: #2e2f38;
      width: 150px;
      min-height: 260px;
      overflow: auto;
      .ant-select-tree li span.ant-select-tree-switcher {
        width: 12px;
        left: 120px;//移动选项左侧三角形
      }
    }
    // 各选项缩进样式
    :deep(.ant-select-tree) {
      & > li > ul {
        padding-left: 0;
        & > li > ul {
          padding-left: 20px;
        }
      }
    }
  }
  :deep(
      .ant-select-multiple.ant-select-disabled.ant-select:not(
          .ant-select-customize-input
        )
        .ant-select-selector
    ) {
    background-color: #181a24;
  }
  .aspin{
    width:100%;
    height:100%;
    z-index:3000;
    position: fixed;
    top:0;
    left:0;
    background: rgba(0, 0, 0, 0.8);
    &>img{
      position:absolute;
      top:50%;
      left:50%;
      transform:translate(-50%,-50%)
    }
  }
}
</style>
