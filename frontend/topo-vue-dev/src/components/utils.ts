import { ExclamationCircleOutlined } from '@ant-design/icons-vue';
import { FormItem, Meta2d, Meta2dData, Pen,deepClone } from '@topometa2d/core';
import { Button, notification } from 'ant-design-vue';
import { h } from 'vue';
export async function addIcons(url: string) {
  return new Promise((resolve, reject) => {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', url, true);
    xhr.send();
    xhr.onreadystatechange = () => {
      if (xhr.readyState === 4 && xhr.status === 200) {
        try {
          const iconfont = JSON.parse(xhr.responseText);
          const iconGroup = {
            name: iconfont.name,
            loaded: true,
            show: true,
            list: [],
          };

          iconfont.glyphs.forEach((item: any) => {
            iconGroup.list.push({
              name: item.name,
              icon:
                iconfont.font_family +
                ' ' +
                iconfont.css_prefix_text +
                item.font_class,
              data: {
                width: 100,
                height: 100,
                name: 'image',
                iconFamily: iconfont.font_family,
                icon: String.fromCharCode(item.unicode_decimal),
              },
            });
          });
          resolve(iconGroup);
        } catch (error) {
          reject(error);
        }
      }
    };
  });
}

/**
 * 判断是否是 gif 图片
 */
export function isGif(url: string): boolean {
  return url.endsWith('.gif');
}

export interface TreeItem {
  id: string;
  type: number;
  name: string;
  description: string; // 业务字段
  locked: number;
  visible: boolean;
  children: TreeItem[];
  opened: boolean;
  showInput?: boolean; // 是否展示输入框
}

declare const meta2d: Meta2d;
export function format(pens: Pen[]) {
  const result: TreeItem[] = [];
  pens.forEach((pen) => {
    let children: TreeItem[] = null;
    if (pen.children && pen.children.length > 0) {
      // 找到所有的孩子 pens
      children = [];
      for (const child of pen.children) {
        children.push(...format(meta2d.find(child)));
      }

      if (children && !children.length) {
        children = null;
      }
    }

    result.push({
      id: pen.id,
      type: pen.type,
      name: pen.name,
      description: (pen as any).description, // 业务字段
      locked: pen.locked,
      visible: pen.visible,
      children,
      opened: false,
    });
  });

  return result;
}

/**
 * 后端实际存储的 图纸数据类型
 */
export interface Meta2dBackData extends Meta2dData {
  id?: string;
  name?: string;
  userId?: string;
  image?: string;
  component?: boolean;
  componentDatas?: Pen[];
  version?: string;
  folder?: string;
  shared?: boolean; // 是否分享
  class?: string; // 分类，架构拓扑图那些
  // 组合为状态，组件保存使用，无需存储到后端
  showChild?: number;
  _id?: string;
  owner?: {
    id?: string;
  };
  folderId?: string;
  editor?: {
    id?: string;
    username?: string;
  };
  username?: string;
  editorId?: string;
  editorName?: string;
  shortFill?:boolean;//是否短边自适应
  fill?:boolean;//是否自适应
  showMap?:boolean;
  disableMove?:boolean;//是否拖拽
  disableScale?:boolean;//是否缩放
  sharedcustom?:boolean;//是否开启自定义链接
  sharedurl?:string;//是否自定义链接
  sharedencryption?:boolean;//是否开启自定义密码
  sharedpassword?:string//自定义发布页密码
  categoryValue?:string //分类
}

export interface FormItemType extends FormItem {
  key: string; // 属性标识，绑定变量时使用
  key2?: string; // 有些属性存在嵌套
  name: string; // 标题
  tips?: string; // 提示
  placeholder?: string; // input placeholder
  type:
    | 'text' // string 类型输入框
    | 'number'
    | 'color'
    | 'textarea'
    | 'select'
    | 'switch'
    | 'code'
    | 'image'
    | 'icon'
    | 'slider'
    | 'autoComplete'
    | 'label' // 文字，不含输入框;
    | 'password'//密码
    | 'radio' //单选框
  options?: {
    // 选项
    label: string; // 选项的标题，可以使用 html
    value: any; // 选项的值
    disabled?: boolean;
  }[];
  min?: number; // 最小值
  max?: number; // 最大值
  step?: number; // 步长
  rows?: number; // textarea 所需要的行数
  iconFamily?: string; // icon 类型节点需要
  title?: string; // code 类型编辑器需要
  language?: 'javascript' | 'json' | 'markdown'; // code 编辑器需要
  readonly?: boolean; // 是否只读
  mode?: 'multiple' | 'tags'; // select 选项
  isNotString?: boolean; // monaco 需要 string 类型的 code ，不是 string 协助转换

  multiple?: boolean; // 绑定多个属性
  isTime?: boolean; // 是否是时序的， undefined 不会出现 历史趋势 的 checkbox
  isYCategory?: boolean; // y 轴是否是分类，true y 轴分类，false x 轴分类，undefined 不会出现 y轴分类轴 的 checkbox
  // 绑定单个属性是对象， 多个数组
  // dataIds?: BindId | BindId[]; // 关联业务数据
  precision?: number; //精度
}

export function showModal(title: string): Promise<boolean> {
  return new Promise<boolean>((resolve) => {
    const key = `open${Date.now()}`;
    const btnClick = function () {
      notification.close(key); // to hide notification box
      resolve(true);
    };
    console.log('弹窗')
    notification.open({
      message: '提示',
      description: title,
      icon: h(ExclamationCircleOutlined, { style: 'color: #108ee9' }),
      style:{
        background:'#232630',
        color:'#a5a8b2'
      },
      btn: h(
        Button,
        {
          type: 'primary',
          size: 'small',
          onClick: btnClick,
        },
        '确定'
      ),
      key,
      onClose: () => {
        resolve(false);
      },
    });
  });
}

// localForage 存储为保存的图纸 key
export const localMeta2dDataName = 'meta2dData';

/**
 * 正常的 assign 操作，是存在弊端的，
 * 若源对象存在该属性，但目标对象不存在该属性（不存在并非 undefined），则会导致无法覆盖
 * 该方法会把源对象的属性全部清空，然后再把目标对象的属性覆盖到源对象上
 * source 可能是个监听的对象，所有最后一步再更改它的属性值
 * @param source 原对象
 * @param target 目标对象
 */
export function strictAssign(
  source: Record<string, any>,
  target: Record<string, any>
) {
  // source 的全部属性都是 undefined 的对象，而非没有这个属性
  const undefinedSource: Record<string, any> = {};
  Object.keys(source).forEach((key) => {
    undefinedSource[key] = undefined;
  });
  Object.assign(undefinedSource, target);
  Object.assign(source, undefinedSource);
}
export function deepCopy (obj) {
  var result = Array.isArray(obj) ? [] : {}
  for (var key in obj) {
    if (Object.prototype.hasOwnProperty.call(obj, key)) {
      if (typeof obj[key] === 'object' && obj[key] !== null) {
        result[key] = deepCopy(obj[key])
      } else {
        result[key] = obj[key]
      }
    }
  }
  return result
}
export interface modalSetForm {
  showPos: string;
  closeMethod: string;
  isMask:Boolean;
  btnColor: string;
  btnFlowColor: string;
  btnSize: string;
  modalWidth:string;
  modalHeight:string;
  modalBgColor:string;
  titleSwitch?:Boolean;
  isShowTitle:Boolean;
  titleBgColor?:string;
  titleSize?:string
  titleColor?:string;
  titleLean?:string;
  titleBold?:string;
  titleLevel?:string;
  titleVertical?:string;
  titleContent?:string;
  borderSwitch:Boolean;
  borderStyle?:string;
  borderWidth?:string;
  borderColor?:string;
  borderRound?:string;
  shadowColor?:string;
  shadowBlur?:string;
  shadowOffsetX?:string;
  shadowOffsetY?:string;
  scrollBar?:Boolean;
  moreStyleParam?:string;
  isLockRadio?:Boolean;
  modalRatio?:string;
  backImageVal?:string;
  imageVal?:string;
}

export const diagramData = {
  lineSignal: [
    {
      eventName: '信号机开放(开灯)',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#00FF00' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 1,
      },
    },
    {
      eventName: '道岔处反位(开灯)',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#FFFF00' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 2,
      },
    },
    {
      eventName: '信号机关闭(开灯)',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#FF0000' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 3,
      },
    },
    {
      eventName: '信号机开放(灭灯)',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#00ff00' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 4,
      },
    },
    {
      eventName: '道岔处反位(灭灯)',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#FFFF00' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 5,
      },
    },
    {
      eventName: '信号机关闭(灭灯)',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#FF0000' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 6,
      },
    },
    {
      eventName: '信号机引导',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#FFFF00' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 7,
      },
    },
    {
      eventName: '自动通过进路',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#00ff00' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 8,
      },
    },
  ],
  AxleCounterDefault: [
    {
      eventName: 'CBTC占用',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#FF0000' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 1,
      },
    },
    {
      eventName: '非CBTC占用',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#804000' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 2,
      },
    },
    {
      eventName: '计轴受扰',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#FF66CC' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 3,
      },
    },
    {
      eventName: '进路',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#00FF00' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 4,
      },
    },
    {
      eventName: '临时限速',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#00A6FF' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 5,
      },
    },
    {
      eventName: '封锁',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#FF00FF' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 6,
      },
    },
    {
      eventName: '保护区段锁闭',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#ffff00' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 7,
      },
    },
    {
      eventName: '与联锁中断',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#6B696B' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 8,
      },
    },
  ],
  AxleCounterDefault2: [
    {
      eventName: 'CBTC占用',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#FF0000' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 1,
      },
    },
    {
      eventName: '非CBTC占用',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#804000' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 2,
      },
    },
    {
      eventName: '计轴受扰',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#FF66CC' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 3,
      },
    },
    {
      eventName: '进路',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#00FF00' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 4,
      },
    },
    {
      eventName: '临时限速',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#00A6FF' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 5,
      },
    },
    {
      eventName: '封锁',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#FF00FF' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 6,
      },
    },
    {
      eventName: '保护区段锁闭',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#ffff00' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 7,
      },
    },
    {
      eventName: '与联锁中断',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#6B696B' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 8,
      },
    },
  ],
  daoCha: [
    {
      eventName: '定位(正常)',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#00FF00' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 1,
      },
    },
    {
      eventName: '反位(正常)',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#FFFF00' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 2,
      },
    },
    {
      eventName: '定位(单锁)',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#00FF00' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 3,
      },
    },
    {
      eventName: '反位(单锁)',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#FFFF00' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 4,
      },
    },
    {
      eventName: '定位(进路锁闭)',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#00FF00' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 5,
      },
    },
    {
      eventName: '反位(进路锁闭)',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#FFFF00' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 6,
      },
    },
    {
      eventName: '定位(占用)',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#00FF00' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 7,
      },
    },
    {
      eventName: '反位(占用)',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#FFFF00' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 8,
      },
    },
    {
      eventName: '失表',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#FF0000' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 9,
      },
    },
    {
      eventName: '联锁中断',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#6B696B' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 10,
      },
    },
  ],
  // zhanTaiMen: [
  //   {
  //     eventName: '关闭且锁闭',
  //     flag: true,
  //     action: 1,
  //     name: 'valueUpdate',
  //     value: { color: '#7B7D7B' },
  //     where: {
  //       comparison: '==',
  //       key: 'status',
  //       type: 'comparison',
  //       value: 1,
  //     },
  //   },
  //   {
  //     eventName: '打开',
  //     flag: true,
  //     action: 1,
  //     name: 'valueUpdate',
  //     value: { color: '#00FF00' },
  //     where: {
  //       comparison: '==',
  //       key: 'status',
  //       type: 'comparison',
  //       value: 2,
  //     },
  //   },
  //   {
  //     eventName: '互锁解除',
  //     flag: true,
  //     action: 1,
  //     name: 'valueUpdate',
  //     value: { color: '#FFFF00' },
  //     where: {
  //       comparison: '==',
  //       key: 'status',
  //       type: 'comparison',
  //       value: 3,
  //     },
  //   },
  //   {
  //     eventName: '与联锁中断',
  //     flag: true,
  //     action: 1,
  //     name: 'valueUpdate',
  //     value: { color: '#6B696B' },
  //     where: {
  //       comparison: '==',
  //       key: 'status',
  //       type: 'comparison',
  //       value: 4,
  //     },
  //   },
  // ],
  platformDisplay: [
    {
      eventName: '车门关闭',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#7B7D7B' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 1,
      },
    },
    {
      eventName: '车门打开',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#00FF00' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 2,
      },
    },
    {
      eventName: '联锁中断',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#6B696B' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 3,
      },
    },
    // 紧急停车statusEmergstop
    {
      eventName: '紧急停车',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#f00' },
      where: {
        comparison: '==',
        key: 'statusEmergstop',
        type: 'comparison',
        value: 1,
      },
    },
    // statusSkipStop跳停
    {
      eventName: '跳停',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#FFFF00' },
      where: {
        comparison: '==',
        key: ' statusSkipStop',
        type: 'comparison',
        value: 1,
      },
    },
    // 扣车 statusDetainCar
    {
      eventName: '中心扣车',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#ffffff' },
      where: {
        comparison: '==',
        key: 'statusDetainCar',
        type: 'comparison',
        value: 1,
      },
    },
    {
      eventName: '车站扣车',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#FFA600' },
      where: {
        comparison: '==',
        key: 'statusDetainCar',
        type: 'comparison',
        value: 2,
      },
    },
    {
      eventName: '中心与车站同时扣车',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#FF0000' },
      where: {
        comparison: '==',
        key: 'statusDetainCar',
        type: 'comparison',
        value: 3,
      },
    },
    // 站台门 statusZhantaimeng
    {
      eventName: '关闭且锁闭',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#7B7D7B' },
      where: {
        comparison: '==',
        key: 'statusZhantaimeng',
        type: 'comparison',
        value: 1,
      },
    },
    {
      eventName: '打开',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#00FF00' },
      where: {
        comparison: '==',
        key: 'statusZhantaimeng',
        type: 'comparison',
        value: 2,
      },
    },
    {
      eventName: '互锁解除',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#FFFF00' },
      where: {
        comparison: '==',
        key: 'statusZhantaimeng',
        type: 'comparison',
        value: 3,
      },
    },
    {
      eventName: '与联锁中断',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#6B696B' },
      where: {
        comparison: '==',
        key: 'statusZhantaimeng',
        type: 'comparison',
        value: 4,
      },
    },
  ],
  platformDisplayUnder: [
    {
      eventName: '车门关闭',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#7B7D7B' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 1,
      },
    },
    {
      eventName: '车门打开',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#00FF00' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 2,
      },
    },
    {
      eventName: '联锁中断',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#6B696B' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 3,
      },
    },
    // 紧急停车statusEmergstop
    {
      eventName: '紧急停车',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#f00' },
      where: {
        comparison: '==',
        key: 'statusEmergstop',
        type: 'comparison',
        value: 1,
      },
    },
    // statusSkipStop跳停
    {
      eventName: '跳停',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#FFFF00' },
      where: {
        comparison: '==',
        key: ' statusSkipStop',
        type: 'comparison',
        value: 1,
      },
    },
    // 扣车 statusDetainCar
    {
      eventName: '中心扣车',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#ffffff' },
      where: {
        comparison: '==',
        key: 'statusDetainCar',
        type: 'comparison',
        value: 1,
      },
    },
    {
      eventName: '车站扣车',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#FFA600' },
      where: {
        comparison: '==',
        key: 'statusDetainCar',
        type: 'comparison',
        value: 2,
      },
    },
    {
      eventName: '中心与车站同时扣车',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#FF0000' },
      where: {
        comparison: '==',
        key: 'statusDetainCar',
        type: 'comparison',
        value: 3,
      },
    },
    // 站台门 statusZhantaimeng
    {
      eventName: '关闭且锁闭',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#7B7D7B' },
      where: {
        comparison: '==',
        key: 'statusZhantaimeng',
        type: 'comparison',
        value: 1,
      },
    },
    {
      eventName: '打开',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#00FF00' },
      where: {
        comparison: '==',
        key: 'statusZhantaimeng',
        type: 'comparison',
        value: 2,
      },
    },
    {
      eventName: '互锁解除',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#FFFF00' },
      where: {
        comparison: '==',
        key: 'statusZhantaimeng',
        type: 'comparison',
        value: 3,
      },
    },
    {
      eventName: '与联锁中断',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#6B696B' },
      where: {
        comparison: '==',
        key: 'statusZhantaimeng',
        type: 'comparison',
        value: 4,
      },
    },
  ],
  trainDisplay: [
    {
      eventName: '运行状态(ATO)',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#00FF00' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 1,
      },
    },
    {
      eventName: '运行状态(ATPM)',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#00FFFF' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 2,
      },
    },
    {
      eventName: '带通信(IATP/RM)',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#FFA600' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 3,
      },
    },
    {
      eventName: '非通信列车',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#FFFFFF' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 4,
      },
    },
    {
      eventName: '列车故障',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#FF0000' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 5,
      },
    },
    {
      eventName: '列车扣车',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#FFFF00' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 6,
      },
    },
  ],
  MaEInfoShow: [
    {
      eventName: '运行方向',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#00ff00' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 1,
      },
    },
    {
      eventName: '正点',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#00ff00' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 2,
      },
    },
    {
      eventName: '轻微早点',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#0000FF' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 3,
      },
    },
    {
      eventName: '严重早点',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#00FFFF' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 4,
      },
    },
    {
      eventName: '轻微晚点',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#ffff00' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 5,
      },
    },
    {
      eventName: '严重晚点',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#ff0000' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 6,
      },
    },
    {
      eventName: '非时刻表运行',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#ffffff' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 7,
      },
    },
    {
      eventName: '列车跳停',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#EA4BF7' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 8,
      },
    },
  ],
  floodGate: [
    {
      eventName: '具备关闭条件',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#0000E1' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 1,
      },
    },
    {
      eventName: '请求关闭',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#0000E1' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 2,
      },
    },
    {
      eventName: '打开并锁闭',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#ffffff' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 3,
      },
    },
    {
      eventName: '联锁中断',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#7B7D7B' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 4,
      },
    },
    {
      eventName: '防淹门关闭',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#ff0' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 5,
      },
    },
  ],
  // emergencyStop: [
  //   {
  //     eventName: '紧急停车',
  //     flag: true,
  //     action: 1,
  //     name: 'valueUpdate',
  //     value: { color: '#f00' },
  //     where: {
  //       comparison: '==',
  //       key: 'status',
  //       type: 'comparison',
  //       value: 1,
  //     },
  //   },
  // ],
  // skipStop: [
  //   {
  //     eventName: '跳停',
  //     flag: true,
  //     action: 1,
  //     name: 'valueUpdate',
  //     value: { color: '#FFFF00' },
  //     where: {
  //       comparison: '==',
  //       key: 'status',
  //       type: 'comparison',
  //       value: 1,
  //     },
  //   },
  // ],
  // detainCar: [
  //   {
  //     eventName: '中心扣车',
  //     flag: true,
  //     action: 1,
  //     name: 'valueUpdate',
  //     value: { color: '#ffffff' },
  //     where: {
  //       comparison: '==',
  //       key: 'status',
  //       type: 'comparison',
  //       value: 1,
  //     },
  //   },
  //   {
  //     eventName: '车站扣车',
  //     flag: true,
  //     action: 1,
  //     name: 'valueUpdate',
  //     value: { color: '#FFA600' },
  //     where: {
  //       comparison: '==',
  //       key: 'status',
  //       type: 'comparison',
  //       value: 2,
  //     },
  //   },
  //   {
  //     eventName: '中心与车站同时扣车',
  //     flag: true,
  //     action: 1,
  //     name: 'valueUpdate',
  //     value: { color: '#FF0000' },
  //     where: {
  //       comparison: '==',
  //       key: 'status',
  //       type: 'comparison',
  //       value: 3,
  //     },
  //   },
  // ],
  qianYinGongDian: [
    {
      eventName: '牵引供电状态未供电',
      flag: true,
      action: 1,
      name: 'valueUpdate',
      value: { color: '#00FF00' },
      where: {
        comparison: '==',
        key: 'status',
        type: 'comparison',
        value: 1,
      },
    },
  ],
};
export const diagramDataKeyArr=[
  'lineSignal',
  'AxleCounterDefault',
  'AxleCounterDefault2',
  'MaEInfoShow',
  'trainDisplay',
  'platformDisplay',
  'daoCha',
  'zhanTaiMen',
  'floodGate',
  'emergencyStop',
  'skipStop',
  'detainCar',
  'qianYinGongDian',
]
//重组png文件图元图片数据-按照同一类型的不同状态组成二维数组
export function allPngsCombine(allPng){
  let allPngSameName=[];
  for (let i = 0; i < allPng.length; i++) {
      let part = allPng[i].name.split("_")[0];
      if (allPngSameName.indexOf(part)<0) {
        allPngSameName.push(part);
      }
  }
  let allPngCombine=[]
  for(let j=0;j<allPngSameName.length;j++){
    allPngCombine[j]=[]
    for(let k=0;k<allPng.length;k++){
      const part1=allPng[k].name.split("_")[0]
      if(part1===allPngSameName[j]){
        allPngCombine[j].push(allPng[k])
      }
    }
  }
  
  allPngCombine.forEach(el=>{
    if(el.length>1){
      el.sort(function(a, b) {
        let splitA=a.name.split('_')
        let splitB=b.name.split('_')
        if(splitA.length>1&&splitB.length>1){
          var numA = parseInt(splitA[1]);
          var numB = parseInt(splitB[1]);
          
          if(!isNaN(numA)&&!isNaN(numB)){
            return numA - numB;
          }
        }
      });
    }
  })
  return allPngCombine
}
//拖拽或者点击图元有多个状态的图元默认组合为状态
export function combinePenInit(allPngCombine,imageName,initPen){
  const tempArr=allPngCombine.find(innerArr => innerArr.some(item => item.name === imageName));
  if(tempArr.length>1){
    const event={
      action:1,
      eventName: "事件1",
      flag: true,
      name:"valueUpdate",
      value:{showChild: 0},
      where:{
        comparison:"==",
        key:"text",
        type: "comparison",
        value:0
      }
    }
    const deepPen=[]
    const events=[]
    for(let i=0;i<tempArr.length;i++){
      if(i<tempArr.length-1){
        deepPen[i]=deepClone(initPen)
        deepPen[i].id=initPen.id+i
        deepPen[i]?.anchors.forEach(x=>{
          x.penId=deepPen[i].id
        })
        deepPen[i].image=tempArr[i+1]['image']
        deepPen[i].name=tempArr[i+1]['image'].split('.')[1]==='png'?'image':tempArr[i+1]['image'].split('.')[1]
        meta2d.addPen(deepPen[i]);
      }
      events[i]=deepClone(event)
      const isStatus=tempArr[i].name.split('-')
      const isValue=tempArr[i].name.split('_')
      if(isStatus.length>1){
        events[i].eventName=isStatus[isStatus.length-1]
      }else{
        if(isValue.length>1){
          events[i].eventName=isValue[isValue.length-1]
        }else{
          events[i].eventName='事件'+(i+1)
        }
      }
      
      if(isValue.length>1){
        const value=parseInt(isValue[isValue.length-1])
        events[i].value.showChild=value
        events[i].where.value=value
      }
    }
    meta2d.combine([initPen,...deepPen],0)
    const getParentPen=meta2d.getParent(initPen)
    getParentPen.events=deepClone(events)
  }
}
// 获取浏览器链接地址的字段
export function showWindowHref() {
  var sHref = window.location.href
  var args = sHref.split('?')
  if (args[0] === sHref) {
      return ''
  }
  var arr = args[1].split('&')
  var obj = {}
  for (var i = 0; i < arr.length; i++) {
      var arg = arr[i].split('=')
      obj[arg[0]] = arg[1]
  }
  return obj
}