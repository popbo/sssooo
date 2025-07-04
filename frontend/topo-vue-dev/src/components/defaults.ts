import { t } from '@/i18n/i18n';
import { Pen } from '@topometa2d/core';
import { FormItemType } from './utils';

export const iconMenus = {
  left: [
    {
      key: 'file',
      name: '文件',
      icon: 't-icon t-folder',
      children: [
        { name: '新建文件', action: 'newFile' },
        { name: '打开文件', action: 'open' },
        { name: '导入文件', action: 'load' },
        // { name: '打开最近文件', recent: true },
        {},
        { name: '保存', action: 'save' },
        { name: '另存为', action: 'saveAsPop' },
        { name: '下载JSON文件', action: 'downloadJson' },
        { name: '下载zip打包文件', action: 'downloadZip' },
        {},
        { name: '导出为HTML', action: 'downloadHtml' },
        { name: '导出为Vue2组件', action: 'downloadVue2' },
        { name: '导出为Vue3组件', action: 'downloadVue3' },
        { name: '导出为React组件', action: 'downloadReact' },
        {},
        { name: '下载为PNG', action: 'downloadPng' },
        { name: '下载为SVG', action: 'downloadSvg' },
      ],
    },
    {
      key: 'edit',
      name: '编辑',
      icon: 't-icon t-gongyong--bianji',
      children: [
        { name: '添加/删除锚点（A）', action: 'toggleAnchorMode' },
        { name: '添加手柄（H）', action: 'addAnchorHand' },
        { name: '删除手柄（D）', action: 'removeAnchorHand' },
        { name: '切换手柄类型（Shift）', action: 'toggleAnchorHand' },
      ],
    },
    {
      key: 'save',
      name: '保存',
      icon: 't-icon t-save',
      action: 'save',
    },
  ],
  middle: [
    {
      key: 'edit',
      name: '编辑',
      icon: 't-icon t-gongyong--bianji',
      children: [
        { name: '添加/删除锚点（A）', action: 'toggleAnchorMode' },
        { name: '添加手柄（H）', action: 'addAnchorHand' },
        { name: '删除手柄（D）', action: 'removeAnchorHand' },
        { name: '切换手柄类型（Shift）', action: 'toggleAnchorHand' },
      ],
    },
    { key: 'pen', name: '钢笔', icon: 't-icon t-curve', action: 'drawPen' },
    {
      key: 'pencil',
      name: '铅笔',
      icon: 't-icon t-qianbi',
      action: 'drawingPencil',
    },
    {
      key: 'magnifier',
      name: '放大镜',
      icon: 't-icon t-fangdajing',
      action: 'showMagnifier',
    },
    // { key: 'map', name: '缩略图', icon: 'icon-zt icon-zt-suolvetu', action: 'showMap' },
  ],
  right: [
    {
      name: '预览',
      icon: 'icon-zt icon-zt-yulan',
      isShow: true,
      action: 'preview',
    },
    {
      key: 'save',
      name: '保存',
      icon: 'icon-zt icon-zt-baocun',
      isShow: true,
      action: 'save',
    },
    {
      name: '发布',
      icon: 'icon-zt icon-zt-fabu',
      isShow: true,
      action: 'share',
    },
    {
      name: '社区',
      icon: 't-icon t-shequ',
      isShow: (window as any).companyName === 'web组态',
      children: [
        {
          name: 'Github',
          url: 'https://github.com/unittec-com',
          target: '_blank',
        },
        { name: '核心库', url: 'https://github.com/unittec-com/meta2d.js' },
        {
          name: '技术交流群',
          url: 'http://developer.unittec.com/community/wechat.html',
          target: '_blank',
        },
      ],
    },
    {
      name: '帮助',
      icon: 't-icon t-help-circle',
      isShow: (window as any).companyName === 'web组态',
      children: [
        {
          name: '使用教程',
          url: 'http://developer.unittec.com/tutorial/introduction.html',
          target: '_blank',
        },
        {
          name: '开发文档',
          url: 'http://developer.unittec.com/api/core.html',
          target: '_blank',
        },
        {
          name: '用户手册',
          url: 'http://developer.unittec.com/instruction/home.html',
          target: '_blank',
        },
        {
          name: '快捷键',
          url: 'http://developer.unittec.com/tutorial/keyboards.html',
          target: '_blank',
        },
        {},
        {
          name: '服务与合作',
          url: 'http://developer.unittec.com/tutorial/commercial.html',
          target: '_blank',
        },
        {},
        {
          name: '关于我们',
          url: 'http://developer.unittec.com/about/us.html',
          target: '_blank',
        },
      ],
    },
  ],
};
const market = import.meta.env.VITE_MARKET;

export const userMenus = [
  {
    name: '个人中心',
    url: market ? '/account' : (window as any).userHome,
    target: '_blank',
  },
  // {},
];
export const fromArrows = [
  { icon: 't-icon t-line', value: '' },
  { icon: 't-icon t-from-triangle', value: 'triangle' },
  { icon: 't-icon t-from-diamond', value: 'diamond' },
  { icon: 't-icon t-from-circle', value: 'circle' },
  { icon: 't-icon t-from-lineDown', value: 'lineDown' },
  { icon: 't-icon t-from-lineUp', value: 'lineUp' },
  { icon: 't-icon t-from-triangleSolid', value: 'triangleSolid' },
  { icon: 't-icon t-from-diamondSolid', value: 'diamondSolid' },
  { icon: 't-icon t-from-circleSolid', value: 'circleSolid' },
  { icon: 't-icon t-from-line', value: 'line' },
];
export const toArrows = [
  { icon: 't-icon t-line', value: '' },
  { icon: 't-icon t-to-triangle', value: 'triangle' },
  { icon: 't-icon t-to-diamond', value: 'diamond' },
  { icon: 't-icon t-to-circle', value: 'circle' },
  { icon: 't-icon t-to-lineDown', value: 'lineDown' },
  { icon: 't-icon t-to-lineUp', value: 'lineUp' },
  { icon: 't-icon t-to-triangleSolid', value: 'triangleSolid' },
  { icon: 't-icon t-to-diamondSolid', value: 'diamondSolid' },
  { icon: 't-icon t-to-circleSolid', value: 'circleSolid' },
  { icon: 't-icon t-to-line', value: 'line' },
];

export const defaultLines = [
  { name: '曲线', icon: 't-icon t-curve2', value: 'curve' },
  { name: '线段', icon: 't-icon t-polyline', value: 'polyline' },
  { name: '直线', icon: 't-icon t-line', value: 'line' },
  { name: '脑图曲线', icon: 't-icon t-mind', value: 'mind' },
];

export const normalShapeLocalName = 't-often-use-shapes';
export const defalutMaterials = [
  {
    id: normalShapeLocalName,
    name: '常用图形',
    show: true,
    count: 0, // 默认值，后续与它无关
    list: [],
  },
  {
    name: '基本形状',
    show: true,
    list: [
      {
        name: 'square',
        icon: 't-icon t-rect',
        id: 1,
        data: {
          text: '正方形',
          width: 100,
          height: 100,
          name: 'square',
          form: [
            {
              key: 'text',
              type: 'text',
              name: '文本',
            },
          ] as FormItemType[],
        },
      },
      {
        name: 'rectangle',
        icon: 't-icon t-rectangle',
        id: 2,
        data: {
          text: '圆角矩形',
          width: 200,
          height: 50,
          borderRadius: 0.1,
          name: 'rectangle',
        },
      },
      {
        name: 'circle',
        icon: 't-icon t-circle',
        id: 3,
        data: {
          text: '圆',
          width: 100,
          height: 100,
          name: 'circle',
        },
      },
      {
        name: 'triangle',
        icon: 't-icon t-triangle',
        id: 4,
        data: {
          text: '三角形',
          width: 100,
          height: 100,
          name: 'triangle',
        },
      },
      {
        name: 'diamond',
        icon: 't-icon t-diamond',
        id: 5,
        data: {
          text: '菱形',
          width: 100,
          height: 100,
          name: 'diamond',
        },
      },
      {
        name: 'pentagon',
        icon: 't-icon t-pentagon',
        id: 6,
        data: {
          text: '五边形',
          width: 100,
          height: 100,
          name: 'pentagon',
        },
      },
      {
        name: 'hexagon',
        icon: 't-icon t-hexagon',
        id: 7,
        data: {
          text: '六边形',
          width: 100,
          height: 100,
          name: 'hexagon',
        },
      },
      {
        name: 'pentagram',
        icon: 't-icon t-pentagram',
        id: 8,
        data: {
          text: '五角星',
          width: 100,
          height: 100,
          name: 'pentagram',
        },
      },
      {
        name: 'leftArrow',
        icon: 't-icon t-arrow-left',
        id: 9,
        data: {
          text: '左箭头',
          width: 120,
          height: 60,
          name: 'leftArrow',
        },
      },
      {
        name: 'rightArrow',
        icon: 't-icon t-arrow-right',
        id: 10,
        data: {
          text: '右箭头',
          width: 120,
          height: 60,
          name: 'rightArrow',
        },
      },
      {
        name: 'twowayArrow',
        icon: 't-icon t-twoway-arrow',
        id: 11,
        data: {
          text: '双向箭头',
          width: 150,
          height: 60,
          name: 'twowayArrow',
        },
      },
      {
        name: 'line',
        icon: 't-icon t-line',
        id: 12,
        data: {
          anchors: [
            {
              x: 0,
              y: 0.5,
              id: '0',
            },
            {
              x: 1,
              y: 0.5,
              id: '1',
            },
          ],
          width: 200,
          height: 5,
          name: 'rectangle',
          lineWidth: 0,
          background: '#b4b7c1',
        },
      },
      {
        name: 'cloud',
        icon: 't-icon t-cloud',
        id: 13,
        data: {
          text: '云',
          width: 100,
          height: 100,
          name: 'cloud',
        },
      },
      {
        name: 'message',
        icon: 't-icon t-msg',
        id: 14,
        data: {
          textTop: -0.1,
          text: '消息框',
          width: 100,
          height: 100,
          name: 'message',
        },
      },
      {
        name: 'file',
        icon: 't-icon t-file',
        id: 15,
        data: {
          text: '文档',
          width: 80,
          height: 100,
          name: 'file',
        },
      },
      {
        name: 'text',
        icon: 't-icon t-text',
        id: 16,
        data: {
          // text: `${(window as any).companyName}meta2d`,
          text: 'text',
          width: 160,
          height: 30,
          name: 'text',
        },
      },
      {
        name: 'image',
        icon: 't-icon t-image',
        id: 17,
        data: {
          text: '',
          width: 100,
          height: 100,
          name: 'image',
          image: '/img/logo.png',
        },
      },
      {
        name: 'cube',
        icon: 't-icon t-cube',
        id: 18,
        data: {
          width: 60,
          height: 100,
          name: 'cube',
          z: 0.25,
          form: [
            {
              key: 'z',
              name: 'z',
              type: 'number',
              min: 0,
              step: 0.1,
              placeholder: '<= 1 即宽度的比例',
            },
            {
              key: 'backgroundFront',
              name: '前背景色',
              type: 'color',
            },
            {
              key: 'backgroundUp',
              name: '顶背景色',
              type: 'color',
            },
            {
              key: 'backgroundRight',
              name: '右背景色',
              type: 'color',
            },
          ] as FormItemType[],
        },
      },
      {
        name: 'people',
        icon: 't-icon t-people',
        id: 19,
        data: {
          width: 70,
          height: 100,
          name: 'people',
        },
      },
      {
        name: 'video',
        icon: 't-icon t-pc',
        id: 20,
        data: {
          width: 200,
          height: 200,
          externElement: true,
          name: 'video',
          video:
            'https://video.699pic.com/videos/17/69/11/a_aa3jeKZ0D63g1556176911_10s.mp4',
        },
      },
      // {
      //   name: 'video',
      //   icon: 't-icon t-pc',
      //   id: 20,
      //   data: {
      //     width: 200,
      //     height: 200,
      //     externElement: true,
      //     name: 'video',
      //     videoType: 'flv',
      //     video: 'http://1011.hlsplay.aodianyun.com/demo/game.flv'
      //   }
      // },
      // {
      //   name: 'video',
      //   icon: 't-icon t-pc',
      //   id: 20,
      //   data: {
      //     width: 200,
      //     height: 200,
      //     externElement: true,
      //     name: 'video',
      //     videoType: 'flv',
      //     video:
      //       // 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8'
      //       'https://sf1-hscdn-tos.pstatp.com/obj/media-fe/xgplayer_doc_video/hls/xgplayer-demo.m3u8'
      //       // 'https://sf1-hscdn-tos.pstatp.com/obj/media-fe/xgplayer_doc_video/mp4/xgplayer-demo-360p.mp4'
      //       // '/vod/mp4:BigBuckBunny_115k.mov'
      //       // '/livetv/gxtv'
      //   }
      // },
      {
        name: 'iframe',
        icon: 't-icon t-02',
        id: 21,
        data: {
          name: 'iframe',
          width: 100,
          height: 100,
          externElement: true,
          iframe: 'http://unittec.com',
        },
      },
      {
        name: 'icon',
        icon: 'ticon ticon-share',
        id: 22,
        data: {
          width: 100,
          height: 100,
          name: 'image',
          icon: '\ue620',
          iconFamily: 'ticon',
        },
      },
    ],
  },
  {
    name: '表单',
    show: true,
    list: [
      {
        name: '表格',
        icon: 't-icon t-biaoge',
        data: {
          name: 'table2',
          width: 0,
          height: 0,
          form: [
            {
              key: 'data',
              name: '表格数据',
              type: 'code',
              language: 'json',
              isNotString: true,
            },
            {
              key: 'styles',
              name: '表格样式',
              type: 'code',
              language: 'json',
              isNotString: true,
            },
          ] as FormItemType[],
          disableAnchor: true,
          colWidth: 150,
          rowHeight: 40,
          data: [
            ['设备 ID', '设备名称', '数据协议', '状态', '操作'],
            ['1', '200', 'MQTT', '正在运行', {}],
            ['2', '湿度传感器', 'MQTT', '正在运行', {}],
            ['3', '物联网设备', 'MQTT', '正在运行', {}],
            ['4', '物联网设备/智能家居/智慧城市', 'MQTT', '正在运行', {}],
          ],
          styles: [
            {
              row: 1,
              col: 1,
              color: '#ff0000',
              background: '#ffff00',
              wheres: [
                //触发条件 成立后才允许配置样式
                {
                  comparison: '<=',
                  value: '123',
                },
              ],
            },
            {
              row: 0,
              height: 60,
            },
            {
              col: 4,
              width: 200, //为该列设置额外的节点
              pens: [
                {
                  name: 'rectangle',
                  width: 50,
                  height: 20,
                  text: '编辑',
                  fontSize: 0.6,
                  disableAnchor: true,
                  activeBackground: '#40a9ff',
                  activeColor: '#40a9ff',
                  background: '#1890ff',
                  color: '#1890ff',
                  hoverBackground: '#40a9ff',
                  hoverColor: '#40a9ff',
                  textColor: '#ffffff',
                  hoverTextColor: '#ffffff',
                  activeTextColor: '#ffffff',
                  events: [
                    {
                      action: 5,
                      name: 'click',
                      value: 'alert("点击了编辑")',
                    },
                  ],
                },
                {
                  name: 'rectangle',
                  width: 80,
                  height: 20,
                  text: '实时数据',
                  fontSize: 0.6,
                  disableAnchor: true,
                  activeBackground: '#40a9ff',
                  activeColor: '#40a9ff',
                  background: '#1890ff',
                  color: '#1890ff',
                  hoverBackground: '#40a9ff',
                  hoverColor: '#40a9ff',
                  textColor: '#ffffff',
                  hoverTextColor: '#ffffff',
                  activeTextColor: '#ffffff',
                  events: [
                    {
                      action: 5,
                      name: 'click',
                      value: 'alert("点击了实时数据")',
                    },
                  ],
                },
              ],
            },
          ],
        },
      },

      {
        name: '复选框',
        icon: 't-icon t-xuanzeqi',
        data: {
          name: 'checkbox',
          width: 100,
          height: 30,
          fontSize: 16,
          disableAnchor: true,
          direction: 'vertical',
          checked: true,
          // isForbidden: true,
          value: '选项一',
          form: [
            {
              key: 'checked',
              name: '选中',
              type: 'switch',
            },
            {
              key: 'isForbidden',
              name: '是否禁用',
              type: 'switch',
            },
            {
              key: 'value',
              name: '选项值',
              type: 'text',
            },
          ] as FormItemType[],
        },
      },
      {
        name: '单选框',
        icon: 't-icon t-danxuankuang',
        data: {
          name: 'radio',
          width: 150,
          height: 30,
          disableAnchor: true,
          direction: 'horizontal', // 'vertical', //'horizontal',
          form: [
            {
              key: 'options',
              name: '选项',
              type: 'code',
              language: 'json',
              isNotString: true,
            },
            {
              key: 'direction',
              name: '方向',
              type: 'select',
              options: [
                {
                  label: '水平',
                  value: 'horizontal',
                },
                {
                  label: '垂直',
                  value: 'vertical',
                },
              ],
            },
            {
              key: 'checked',
              name: '选择项',
              type: 'text',
            },
          ] as FormItemType[],
          options: [
            { text: '选项一', isForbidden: true },
            { text: '选项二' },
            { text: '选项三' },
          ],
          checked: '选项二',
        },
      },
      {
        name: '开关',
        icon: 't-icon t-kaiguan',
        data: {
          name: 'switch',
          disableAnchor: true,
          height: 30,
          width: 60,
          checked: true,
          offColor: '#BFBFBF',
          onColor: '#1890ff',
          disableOffColor: '#E5E5E5',
          disableOnColor: '#A3D3FF',
          hoverBackground: '#40a9ff',
          form: [
            {
              key: 'checked',
              name: '开关状态',
              type: 'switch',
            },
          ] as FormItemType[],
        },
      },
      {
        name: '进度条',
        icon: 't-icon t-jindutiao',
        data: {
          anchors: [],
          disableAnchor: true,
          name: 'slider',
          x: 400,
          y: 400,
          width: 300,
          height: 20,
          value: 10,
          textWidth: 50,
          barHeight: 4, //修改无效
          min: 0,
          max: 100,
          color: '#1890ff',
          background: '#D4D6D9',
          textColor: '#222222',
          unit: '%',
          form: [
            //TODO 添加最大最小
            {
              key: 'value',
              name: '当前进度值',
              type: 'number',
              min: 0,
              max: 100,
            },
            {
              key: 'unit',
              name: '显示单位',
              type: 'text',
            },
            {
              key: 'textColor',
              name: '文字颜色',
              type: 'color',
            },
          ] as FormItemType[],
        },
      },
      {
        name: '按钮',
        icon: 't-icon t-anniu',
        data: {
          name: 'rectangle',
          x: 300,
          y: 200,
          width: 80,
          height: 30,
          disableAnchor: true,
          borderRadius: 2,
          text: '按钮',
          activeBackground: '#40a9ff',
          activeColor: '#40a9ff',
          background: '#1890ff',
          color: '#1890ff',
          hoverBackground: '#40a9ff',
          hoverColor: '#40a9ff',
          textColor: '#ffffff',
          hoverTextColor: '#ffffff',
          activeTextColor: '#ffffff',
        },
      },
      {
        name: '输入框',
        icon: 't-icon t-shurukuang',
        data: {
          x: 100,
          y: 100,
          height: 40,
          width: 200,
          disableAnchor: true,
          name: 'rectangle',
          borderRadius: 0.05,
          input: true,
          ellipsis: true,
          text: '输入数据',
          textAlign: 'left',
          color: '#D9D9D9FF',
          // textColor: '#000000FF',
          // hoverTextColor: '#000000FF',
          // activeTextColor: '#000000FF',
          textLeft: 10,
        },
      },
      {
        name: '选择器',
        icon: 't-icon t-xuanzeqi',
        data: {
          x: 100,
          y: 100,
          height: 40,
          width: 200,
          disableAnchor: true,
          name: 'rectangle',
          borderRadius: 0.05,
          ellipsis: true,
          text: '选项1',
          textAlign: 'left',
          input: true,
          // color: '#D9D9D9FF',
          // textColor: '#000000FF',
          // hoverTextColor: '#000000FF',
          // activeTextColor: '#000000FF',
          textLeft: 10,
          dropdownList: [
            {
              text: '选项1',
            },
            {
              text: '选项2',
            },
            {
              text: '选项3',
            },
          ],
          form: [
            {
              key: 'text',
              name: '选择项',
              type: 'text',
            },
          ] as FormItemType[],
        },
      },
    ],
  },
  {
    name: '脑图',
    show: true,
    list: [
      {
        name: 'mindNode',
        icon: 't-icon t-zhuti',
        data: {
          text: '主题',
          width: 200,
          height: 50,
          name: 'mindNode',
          borderRadius: 0.5,
        },
      },
      {
        name: 'mindLine',
        icon: 't-icon t-zizhuti',
        data: {
          text: '子主题',
          width: 160,
          height: 40,
          name: 'mindLine',
        },
      },
    ],
  },
  {
    name: '流程图',
    show: true,
    list: [
      {
        name: '开始/结束',
        icon: 't-icon t-flow-start',
        id: 21,
        data: {
          text: '开始/结束',
          width: 120,
          height: 40,
          borderRadius: 0.5,
          name: 'rectangle',
        },
      },
      {
        name: '流程',
        icon: 't-icon t-rectangle',
        id: 22,
        data: {
          text: '流程',
          width: 120,
          height: 40,
          name: 'rectangle',
        },
      },
      {
        name: '判定',
        icon: 't-icon t-diamond',
        id: 23,
        data: {
          text: '判定',
          width: 120,
          height: 60,
          name: 'diamond',
        },
      },
      {
        name: '数据',
        icon: 't-icon t-flow-data',
        id: 24,
        data: {
          text: '数据',
          width: 120,
          height: 50,
          name: 'flowData',
          offsetX: 0.14,
          form: [
            {
              key: 'offsetX',
              name: '斜率',
              type: 'number',
              min: 0,
              step: 0.1,
              placeholder: '<= 1 即宽度的比例',
            },
          ] as FormItemType[],
        },
      },
      {
        name: '准备',
        icon: 't-icon t-flow-ready',
        id: 25,
        data: {
          text: '准备',
          width: 120,
          height: 50,
          name: 'hexagon',
        },
      },
      {
        name: '子流程',
        icon: 't-icon t-flow-subprocess',
        id: 26,
        data: {
          text: '子流程',
          width: 120,
          height: 50,
          name: 'flowSubprocess',
        },
      },
      {
        name: '数据库',
        icon: 't-icon t-db',
        id: 27,
        data: {
          text: '数据库',
          width: 80,
          height: 120,
          name: 'flowDb',
        },
      },
      {
        name: '文档',
        icon: 't-icon t-flow-document',
        id: 28,
        data: {
          text: '文档',
          width: 120,
          height: 100,
          name: 'flowDocument',
        },
      },
      {
        name: '内部存储',
        icon: 't-icon t-internal-storage',
        id: 29,
        data: {
          text: '内部存储',
          width: 120,
          height: 80,
          name: 'flowInternalStorage',
        },
      },
      {
        name: '外部存储',
        icon: 't-icon t-extern-storage',
        id: 30,
        data: {
          text: '外部存储',
          width: 120,
          height: 80,
          name: 'flowExternStorage',
        },
      },
      {
        name: '队列',
        icon: 't-icon t-flow-queue',
        id: 31,
        data: {
          text: '队列',
          width: 100,
          height: 100,
          name: 'flowQueue',
        },
      },
      {
        name: '手动输入',
        icon: 't-icon t-flow-manually',
        id: 32,
        data: {
          text: '手动输入',
          width: 120,
          height: 80,
          name: 'flowManually',
        },
      },
      {
        name: '展示',
        icon: 't-icon t-flow-display',
        id: 33,
        data: {
          text: '展示',
          width: 120,
          height: 80,
          name: 'flowDisplay',
        },
      },
      {
        name: '并行模式',
        icon: 't-icon t-flow-parallel',
        id: 34,
        data: {
          text: '并行模式',
          width: 120,
          height: 50,
          name: 'flowParallel',
        },
      },
      {
        name: '注释',
        icon: 't-icon t-flow-comment',
        id: 35,
        data: {
          text: '注释',
          width: 100,
          height: 100,
          name: 'flowComment',
        },
      },
    ],
  },
  {
    name: '信号系统',
    show: true,
    list: [
      {
        name: '信号机',
        image: 'img/svgIcon/lineSignal.svg',
        data: {
          width: 50,
          height: 25,
          name: 'lineSignal',
          status: 0,
          form: [
            {
              key: 'statusObj',
              name: '信号机',
              type: 'code',
            },
          ] as FormItemType[],
        },
      },
      {
        name: '计轴区段',
        image: 'img/svgIcon/AxleCounterDefault.svg',
        data: {
          width: 160,
          height: 10,
          name: 'AxleCounterDefault',
          form: [
            {
              key: 'statusObj2',
              name: '计轴区段',
              type: 'code',
            },
          ] as FormItemType[],
        },
      },
      {
        name: '计轴区段2',
        image: 'img/svgIcon/AxleCounterDefault2.svg',
        data: {
          width: 160,
          height: 10,
          name: 'AxleCounterDefault2',
          form: [
            {
              key: 'statusObj2',
              name: '计轴区段',
              type: 'code',
            },
          ] as FormItemType[],
        },
      },
      {
        name: '列车显示-车头',
        image: 'img/svgIcon/trainDisplayHead.svg',
        data: {
          width: 125,
          height: 60,
          name: 'MaEInfoShow',
          form: [
            {
              key: 'statusObj',
              name: '列车',
              type: 'code',
            },
          ] as FormItemType[],
        },
      },
      {
        name: '列车显示-车身',
        image: 'img/svgIcon/trainDisplayBody.svg',
        data: {
          width: 125,
          height: 60,
          name: 'trainDisplay',
          form: [
            {
              key: 'statusObj',
              name: '列车',
              type: 'code',
            },
          ] as FormItemType[],
        },
      },
      {
        name: '站台(车门)显示向上',
        image: 'img/svgIcon/platformDisplay_up.svg',
        data: {
          width: 249,
          height: 88,
          name: 'platformDisplay',
          form: [
            {
              key: 'statusObj',
              name: '状态',
              type: 'code',
            },
          ] as FormItemType[],
        },
      },
      {
        name: '站台(车门)显示向下',
        image: 'img/svgIcon/platformDisplay_down.svg',
        data: {
          width: 249,
          height: 88,
          name: 'platformDisplayUnder',
          form: [
            {
              key: 'statusObj',
              name: '状态',
              type: 'code',
            },
          ] as FormItemType[],
        },
      },
      {
        name: '单动道岔1',
        image: 'img/svgIcon/daoCha.svg',
        data: {
          width: 320,
          height: 20,
          name: 'daoCha',
          status: 0,
          form: [
            {
              key: 'statusObj',
              name: '道岔',
              type: 'code',
            },
            {
              key: 'statusObj2',
              name: '计轴区段',
              type: 'code',
            },
          ] as FormItemType[],
        },
      },
      {
        name: '单动道岔2',
        image: 'img/svgIcon/daoCha2.svg',
        data: {
          width: 320,
          height: 20,
          name: 'daoChaRight',
          status: 0,
          form: [
            {
              key: 'statusObj',
              name: '道岔',
              type: 'code',
            },
            {
              key: 'statusObj2',
              name: '计轴区段',
              type: 'code',
            },
          ] as FormItemType[],
        },
      },
      {
        name: '单动道岔3',
        image: 'img/svgIcon/daoCha3.svg',
        data: {
          width: 320,
          height: 20,
          name: 'daoChaDwon',
          status: 0,
          form: [
            {
              key: 'statusObj',
              name: '道岔',
              type: 'code',
            },
            {
              key: 'statusObj2',
              name: '计轴区段',
              type: 'code',
            },
          ] as FormItemType[],
        },
      },
      {
        name: '单动道岔4',
        image: 'img/svgIcon/daoCha4.svg',
        data: {
          width: 320,
          height: 20,
          name: 'daoChaRightDwon',
          status: 0,
          form: [
            {
              key: 'statusObj',
              name: '道岔',
              type: 'code',
            },
            {
              key: 'statusObj2',
              name: '计轴区段',
              type: 'code',
            },
          ] as FormItemType[],
        },
      },
      {
        name: '组合交叉渡线道岔',
        image: 'img/svgIcon/daochaCross.svg',
        data: {
          width: 320,
          height: 120,
          name: 'crossDaocha',
          status: 0,
          form: [
            {
              key: 'statusObj',
              name: '上左道岔',
              type: 'code',
            },
            {
              key: 'statusObj2',
              name: '上左区段',
              type: 'code',
            },
            {
              key: 'statusObjUR',
              name: '上右道岔',
              type: 'code',
            },
            {
              key: 'statusObj2UR',
              name: '上右区段',
              type: 'code',
            },
            {
              key: 'statusObjDL',
              name: '下左道岔',
              type: 'code',
            },
            {
              key: 'statusObj2DL',
              name: '下左区段',
              type: 'code',
            },
            {
              key: 'statusObjDR',
              name: '下右道岔',
              type: 'code',
            },
            {
              key: 'statusObj2DR',
              name: '下右区段',
              type: 'code',
            },
          ] as FormItemType[],
        },
      },
	{
        name: '防淹门显示',
        image: 'img/svgIcon/floodGate.svg',
        data: {
          width: 80,
          height: 40,
          name: 'floodGate',
          status: 0,
          form: [
            {
              key: 'statusObj',
              name: '防淹门',
              type: 'code',
            },
          ] as FormItemType[],
        },
      },  
   
      {
        name: '牵引供电',
        image: 'img/svgIcon/qianYinGongDian.svg',
        data: {
          width: 164,
          height: 44,
          name: 'qianYinGongDian',
          status: 0,
          form: [
            {
              key: 'statusObj',
              name: '牵引供电',
              type: 'code',
            },
          ] as FormItemType[],
        },
      },
    ],
  },
  /*{
    name: '活动图',
    show: true,
    list: [
      {
        name: '开始',
        icon: 't-icon t-inital',
        id: 36,
        data: {
          text: '',
          width: 30,
          height: 30,
          name: 'circle',
          background: '#555',
          lineWidth: 0,
        },
      },
      {
        name: '结束',
        icon: 't-icon t-final',
        id: 37,
        data: {
          width: 30,
          height: 30,
          name: 'activityFinal',
        },
      },
      {
        name: '活动',
        icon: 't-icon t-action',
        id: 38,
        data: {
          text: '活动',
          width: 120,
          height: 50,
          borderRadius: 0.25,
          name: 'rectangle',
        },
      },
      {
        name: '决策/合并',
        icon: 't-icon t-diamond',
        id: 39,
        data: {
          text: '决策/合并',
          width: 120,
          height: 50,
          name: 'diamond',
        },
      },
      {
        name: '垂直泳道',
        icon: 't-icon t-swimlane-v',
        id: 40,
        data: {
          text: '垂直泳道',
          width: 200,
          height: 500,
          name: 'swimlaneV',
          textBaseline: 'top',
          textTop: 20,
          // textHeight: ,
          lineTop: 0.08,
        },
      },
      {
        name: '水平泳道',
        icon: 't-icon t-swimlane-h',
        id: 41,
        data: {
          text: '水平泳道',
          width: 500,
          height: 200,
          name: 'swimlaneH',
          textWidth: 0.01,
          textLeft: 0.04,
          textAlign: 'left',
          lineLeft: 0.08,
        },
      },
      {
        name: '垂直分岔/汇合',
        icon: 't-icon t-fork-v',
        id: 42,
        data: {
          text: '垂直分岔/汇合',
          width: 10,
          height: 150,
          name: 'forkV',
          fillStyle: '#555',
          strokeStyle: 'transparent',
        },
      },
      {
        name: '水平分岔/汇合',
        icon: 't-icon t-fork',
        id: 43,
        data: {
          text: '水平分岔/汇合',
          width: 150,
          height: 10,
          name: 'forkH',
          fillStyle: '#555',
          strokeStyle: 'transparent',
        },
      },
    ],
  },
  {
    name: '时序图和类图',
    show: true,
    list: [
      {
        name: '生命线',
        icon: 't-icon t-lifeline',
        id: 44,
        data: {
          text: '生命线',
          width: 150,
          height: 400,
          textHeight: 50,
          name: 'lifeline',
        },
      },
      {
        name: '激活',
        icon: 't-icon t-focus',
        id: 45,
        data: {
          text: '激活',
          width: 12,
          height: 200,
          name: 'sequenceFocus',
        },
      },
      {
        name: '简单类',
        icon: 't-icon t-simple-class',
        id: 46,
        data: {
          text: 'Topolgoy',
          width: 270,
          height: 200,
          textHeight: 200,
          name: 'simpleClass',
          textAlign: 'center',
          textBaseline: 'top',
          textTop: 10,
          list: [
            {
              text: '- name: string\n+ setName(name: string): void',
            },
          ],
        },
      },
      {
        name: '类',
        icon: 't-icon t-class',
        id: 47,
        data: {
          text: 'Topolgoy',
          width: 270,
          height: 200,
          textHeight: 200,
          name: 'interfaceClass',
          textAlign: 'center',
          textBaseline: 'top',
          textTop: 10,
          list: [
            {
              text: '- name: string',
            },
            {
              text: '+ setName(name: string): void',
            },
          ],
        },
      },
    ],
  },
  {
    name: 'Echarts图表',
    show: true,
    list: [
      {
        name: '折线图',
        icon: 't-icon t-line-chart',
        data: {
          name: 'echarts',
          width: 400,
          height: 300,
          externElement: true,
          form: [
            {
              key: 'dataY',
              name: '数据',
              type: 'text',
              readonly: true,
              placeholder: '仅绑定变量',
              multiple: true,
              isTime: false,
              isYCategory: false,
            },
            {
              key: 'echarts',
              name: 'echarts',
              type: 'code',
              language: 'json',
              isNotString: true,
            },
            {
              key: 'echarts',
              key2: 'max',
              type: 'number',
              name: '最大数量',
              placeholder: 'x',
            },
          ] as FormItemType[],
          disableAnchor: true,
          echarts: {
            option: {
              grid: {
                top: 10,
                bottom: 50,
                left: 40,
                right: 5,
              },
              dataZoom: [
                {
                  height: 16,
                  bottom: 10,
                },
              ],
              xAxis: {
                type: 'category',
                data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
                axisLabel: {
                  fontSize: 12,
                },
              },
              yAxis: {
                type: 'value',
                axisLabel: {
                  fontSize: 12,
                },
              },
              series: [
                {
                  data: [820, 932, 901, 934, 1290, 1330, 1320],
                  type: 'line',
                },
              ],
            },
            max: 100,
          },
        },
      },
      {
        name: '柱状图',
        icon: 't-icon t-bar-chart',
        data: {
          width: 300,
          height: 200,
          disableAnchor: true,
          externElement: true,
          name: 'echarts',
          form: [
            {
              key: 'dataY',
              name: '数据',
              type: 'text',
              readonly: true,
              placeholder: '仅绑定变量',
              multiple: true,
              isTime: false,
              isYCategory: false,
            },
            {
              key: 'echarts',
              name: 'echarts',
              type: 'code',
              language: 'json',
              isNotString: true,
            },
            {
              key: 'echarts',
              key2: 'max',
              type: 'number',
              name: '最大数量',
              placeholder: 'x',
            },
          ] as FormItemType[],
          echarts: {
            option: {
              tooltip: {
                trigger: 'axis',
                axisPointer: {
                  // 坐标轴指示器，坐标轴触发有效
                  type: 'shadow', // 默认为直线，可选为：'line' | 'shadow'
                },
              },
              grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true,
              },
              xAxis: {
                type: 'category',
                data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
                axisTick: {
                  alignWithLabel: true,
                },
              },
              yAxis: [
                {
                  type: 'value',
                },
              ],
              series: [
                {
                  name: '直接访问',
                  type: 'bar',
                  barWidth: '60%',
                  data: [10, 52, 200, 334, 390, 330, 220],
                },
              ],
            },
            max: 100,
          },
        },
      },
      {
        name: '饼图',
        icon: 't-icon t-pie-chart',
        data: {
          width: 200,
          height: 200,
          disableAnchor: true,
          externElement: true,
          name: 'echarts',
          form: [
            {
              key: 'dataY',
              name: '数据',
              type: 'text',
              readonly: true,
              placeholder: '仅绑定变量',
              multiple: true,
            },
            {
              key: 'echarts',
              name: 'echarts',
              type: 'code',
              language: 'json',
              isNotString: true,
            },
            {
              key: 'echarts',
              key2: 'max',
              type: 'number',
              name: '最大数量',
              placeholder: 'x',
            },
          ] as FormItemType[],
          echarts: {
            option: {
              tooltip: {
                trigger: 'item',
                formatter: '{a} <br/>{b}: {c} ({d}%)',
              },
              legend: {},
              series: [
                {
                  name: '访问来源',
                  type: 'pie',
                  radius: ['50%', '70%'],
                  avoidLabelOverlap: false,
                  label: {
                    normal: {
                      show: false,
                      position: 'center',
                    },
                    emphasis: {
                      show: true,
                      textStyle: {
                        fontSize: '30',
                        fontWeight: 'bold',
                      },
                    },
                  },
                  labelLine: {
                    normal: {
                      show: false,
                    },
                  },
                  data: [
                    { value: 335, name: '直接访问' },
                    { value: 310, name: '邮件营销' },
                    { value: 234, name: '联盟广告' },
                    { value: 135, name: '视频广告' },
                    { value: 1548, name: '搜索引擎' },
                  ],
                },
              ],
            },
            replaceMode: ReplaceMode.Replace,
          },
        },
      },
      {
        name: '仪表盘',
        icon: 't-icon t-dashboard-chart',
        data: {
          width: 300,
          height: 300,
          disableAnchor: true,
          externElement: true,
          name: 'echarts',
          form: [
            {
              key: 'dataY',
              name: '数据',
              type: 'text',
              readonly: true,
              placeholder: '仅绑定变量',
              multiple: true,
            },
            {
              key: 'echarts',
              name: 'echarts',
              type: 'code',
              language: 'json',
              isNotString: true,
            },
            {
              key: 'echarts',
              key2: 'max',
              type: 'number',
              name: '最大数量',
              placeholder: 'x',
            },
          ] as FormItemType[],
          echarts: {
            option: {
              tooltip: {
                formatter: '{a} <br/>{b} : {c}%',
              },
              series: [
                {
                  name: '业务指标',
                  type: 'gauge',
                  detail: { formatter: '{value}%' },
                  data: [{ value: 50, name: '完成率' }],
                },
              ],
            },
            replaceMode: ReplaceMode.Replace,
          },
        },
      },
    ],
  },
  {
    name: 'Highcharts图表',
    show: true,
    list: [
      {
        name: '折线图',
        icon: 't-icon t-line-chart',
        data: {
          name: 'highcharts',
          width: 400,
          height: 200,
          disableAnchor: true,
          externElement: true,
          form: [
            {
              key: 'highcharts',
              name: 'highcharts',
              type: 'code',
              language: 'json',
              isNotString: true,
            },
          ] as FormItemType[],
          highcharts: {
            option: {
              chart: {
                backgroundColor: '#ffffff00',
              },
              credits: {
                enabled: false,
              },
              xAxis: {
                categories: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
              },
              yAxis: {
                type: 'value',
              },
              series: [
                {
                  data: [820, 932, 901, 934, 1290, 1330, 1320],
                  type: 'line',
                },
              ],
            },
          },
        },
      },
      {
        name: '柱状图',
        icon: 't-icon t-bar-chart',
        data: {
          name: 'highcharts',
          width: 400,
          height: 300,
          disableAnchor: true,
          externElement: true,
          form: [
            {
              key: 'highcharts',
              name: 'highcharts',
              type: 'code',
              language: 'json',
              isNotString: true,
            },
          ] as FormItemType[],
          highcharts: {
            option: {
              chart: {
                backgroundColor: '#ffffff00',
                type: 'column',
              },
              title: {
                text: '月平均降雨量',
              },
              subtitle: {
                text: '数据来源: WorldClimate.com',
              },
              xAxis: {
                categories: [
                  '一月',
                  '二月',
                  '三月',
                  '四月',
                  '五月',
                  '六月',
                  '七月',
                  '八月',
                  '九月',
                  '十月',
                  '十一月',
                  '十二月',
                ],
                crosshair: true,
              },
              yAxis: {
                min: 0,
                title: {
                  text: '降雨量 (mm)',
                },
              },
              tooltip: {
                // head + 每个 point + footer 拼接成完整的 table
                headerFormat:
                  '<span style="font-size:10px">{point.key}</span><table>',
                pointFormat:
                  '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                  '<td style="padding:0"><b>{point.y:.1f} mm</b></td></tr>',
                footerFormat: '</table>',
                shared: true,
                useHTML: true,
              },
              plotOptions: {
                column: {
                  borderWidth: 0,
                },
              },
              series: [
                {
                  name: '东京',
                  data: [
                    49.9, 71.5, 106.4, 129.2, 144.0, 176.0, 135.6, 148.5, 216.4,
                    194.1, 95.6, 54.4,
                  ],
                },
                {
                  name: '纽约',
                  data: [
                    83.6, 78.8, 98.5, 93.4, 106.0, 84.5, 105.0, 104.3, 91.2,
                    83.5, 106.6, 92.3,
                  ],
                },
                {
                  name: '伦敦',
                  data: [
                    48.9, 38.8, 39.3, 41.4, 47.0, 48.3, 59.0, 59.6, 52.4, 65.2,
                    59.3, 51.2,
                  ],
                },
                {
                  name: '柏林',
                  data: [
                    42.4, 33.2, 34.5, 39.7, 52.6, 75.5, 57.4, 60.4, 47.6, 39.1,
                    46.8, 51.1,
                  ],
                },
              ],
            },
          },
        },
      },
      {
        name: '饼图',
        icon: 't-icon t-pie-chart',
        data: {
          name: 'highcharts',
          width: 300,
          height: 300,
          disableAnchor: true,
          externElement: true,
          form: [
            {
              key: 'highcharts',
              name: 'highcharts',
              type: 'code',
              language: 'json',
              isNotString: true,
            },
          ] as FormItemType[],
          highcharts: {
            option: {
              chart: {
                backgroundColor: '#ffffff00',
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false,
                type: 'pie',
              },
              title: {
                text: '2018年1月浏览器市场份额',
              },
              tooltip: {
                pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>',
              },
              plotOptions: {
                pie: {
                  allowPointSelect: true,
                  cursor: 'pointer',
                  dataLabels: {
                    enabled: true,
                    format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                    style: {
                      color: 'black',
                    },
                  },
                },
              },
              series: [
                {
                  name: 'Brands',
                  colorByPoint: true,
                  data: [
                    {
                      name: 'Chrome',
                      y: 61.41,
                      sliced: true,
                      selected: true,
                    },
                    {
                      name: 'Internet Explorer',
                      y: 11.84,
                    },
                    {
                      name: 'Firefox',
                      y: 10.85,
                    },
                    {
                      name: 'Edge',
                      y: 4.67,
                    },
                    {
                      name: 'Safari',
                      y: 4.18,
                    },
                    {
                      name: 'Sogou Explorer',
                      y: 1.64,
                    },
                    {
                      name: 'Opera',
                      y: 1.6,
                    },
                    {
                      name: 'QQ',
                      y: 1.2,
                    },
                    {
                      name: 'Other',
                      y: 2.61,
                    },
                  ],
                },
              ],
            },
          },
        },
      },
      {
        name: '仪表盘',
        icon: 't-icon t-dashboard-chart',
        data: {
          name: 'highcharts',
          width: 300,
          height: 300,
          disableAnchor: true,
          form: [
            {
              key: 'highcharts',
              name: 'highcharts',
              type: 'code',
              language: 'json',
              isNotString: true,
            },
          ] as FormItemType[],
          externElement: true,
          highcharts: {
            option: {
              chart: {
                type: 'gauge',
                plotBackgroundColor: null,
                plotBackgroundImage: null,
                plotBorderWidth: 0,
                plotShadow: false,
                backgroundColor: '#ffffff00',
              },
              title: {
                text: '速度仪',
              },
              pane: {
                startAngle: -150,
                endAngle: 150,
                background: [
                  {
                    backgroundColor: {
                      linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
                      stops: [
                        [0, '#FFF'],
                        [1, '#333'],
                      ],
                    },
                    borderWidth: 0,
                    outerRadius: '109%',
                  },
                  {
                    backgroundColor: {
                      linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
                      stops: [
                        [0, '#333'],
                        [1, '#FFF'],
                      ],
                    },
                    borderWidth: 1,
                    outerRadius: '107%',
                  },
                  {
                    // default background
                  },
                  {
                    backgroundColor: '#DDD',
                    borderWidth: 0,
                    outerRadius: '105%',
                    innerRadius: '103%',
                  },
                ],
              },
              // the value axis
              yAxis: {
                min: 0,
                max: 200,
                minorTickInterval: 'auto',
                minorTickWidth: 1,
                minorTickLength: 10,
                minorTickPosition: 'inside',
                minorTickColor: '#666',
                tickPixelInterval: 30,
                tickWidth: 2,
                tickPosition: 'inside',
                tickLength: 10,
                tickColor: '#666',
                labels: {
                  step: 2,
                  rotation: 'auto',
                },
                title: {
                  text: 'km/h',
                },
                plotBands: [
                  {
                    from: 0,
                    to: 120,
                    color: '#55BF3B', // green
                  },
                  {
                    from: 120,
                    to: 160,
                    color: '#DDDF0D', // yellow
                  },
                  {
                    from: 160,
                    to: 200,
                    color: '#DF5353', // red
                  },
                ],
              },
              series: [
                {
                  name: 'Speed',
                  data: [80],
                  tooltip: {
                    valueSuffix: ' km/h',
                  },
                },
              ],
            },
          },
        },
      },
    ],
  },
  {
    name: 'LightningChart图表',
    show: true,
    list: [
      {
        name: '折线图',
        icon: 't-icon t-line-chart',
        data: {
          name: 'lightningCharts',
          width: 400,
          height: 200,
          disableAnchor: true,
          externElement: true,
          form: [
            {
              key: 'lightningCharts',
              name: 'lightningCharts',
              type: 'code',
              language: 'json',
              isNotString: true,
            },
          ] as FormItemType[],
          lightningCharts: {
            option: {
              type: 'line',
              //主题参考 https://www.arction.com/lightningchart-js-api-documentation/v3.4.0/interfaces/themes.html
              theme: 'darkGreen',
              data: [
                {
                  name: 'Sports Car',
                  data: [
                    { x: 0, y: 0 },
                    { x: 50, y: 10 },
                    { x: 80, y: 20 },
                    { x: 100, y: 30 },
                    { x: 150, y: 40 },
                    { x: 180, y: 50 },
                    { x: 230, y: 60 },
                    { x: 290, y: 70 },
                  ],
                },
                {
                  name: 'Family Car',
                  data: [
                    { x: 0, y: 0 },
                    { x: 100, y: 10 },
                    { x: 230, y: 20 },
                    { x: 390, y: 30 },
                    { x: 470, y: 40 },
                    { x: 540, y: 50 },
                    { x: 600, y: 60 },
                    { x: 800, y: 70 },
                  ],
                },
                {
                  name: 'Pick-up Car',
                  data: [
                    { x: 0, y: 0 },
                    { x: 80, y: 10 },
                    { x: 100, y: 20 },
                    { x: 150, y: 30 },
                    { x: 230, y: 40 },
                    { x: 380, y: 50 },
                    { x: 450, y: 60 },
                    { x: 580, y: 70 },
                  ],
                },
              ],
              title: 'title',
            },
          },
        },
      },
      {
        name: '柱状图',
        icon: 't-icon t-bar-chart',
        data: {
          name: 'lightningCharts',
          width: 400,
          height: 200,
          disableAnchor: true,
          externElement: true,
          form: [
            {
              key: 'lightningCharts',
              name: 'lightningCharts',
              type: 'code',
              language: 'json',
              isNotString: true,
            },
          ] as FormItemType[],
          lightningCharts: {
            option: {
              type: 'bar',
              theme: 'darkGreen',
              groups: ['Finland', 'Germany', 'UK'],
              categories: ['Engineers', 'Sales', 'Marketing'],
              data: [
                [48, 27, 24],
                [19, 40, 14],
                [33, 33, 62],
              ],
              title: 'My first chart',
              yTitle: '纵坐标',
            },
          },
        },
      },
      {
        name: '饼图',
        icon: 't-icon t-pie-chart',
        data: {
          name: 'lightningCharts',
          width: 600,
          height: 300,
          disableAnchor: true,
          externElement: true,
          form: [
            {
              key: 'lightningCharts',
              name: 'lightningCharts',
              type: 'code',
              language: 'json',
              isNotString: true,
            },
          ] as FormItemType[],
          lightningCharts: {
            option: {
              type: 'pie',
              innerRadius: 50,
              data: [
                {
                  name: 'Planning',
                  value: 40,
                },
                {
                  name: 'Development',
                  value: 120,
                },
                {
                  name: 'Testing',
                  value: 60,
                },
                {
                  name: 'Review',
                  value: 24,
                },
                {
                  name: 'Bug Fixing',
                  value: 90,
                },
              ],
            },
          },
        },
      },
      {
        name: '仪表盘',
        icon: 't-icon t-dashboard-chart',
        data: {
          name: 'lightningCharts',
          width: 300,
          height: 300,
          disableAnchor: true,
          externElement: true,
          form: [
            {
              key: 'lightningCharts',
              name: 'lightningCharts',
              type: 'code',
              language: 'json',
              isNotString: true,
            },
          ] as FormItemType[],
          lightningCharts: {
            option: {
              type: 'gauge',
              data: 80,
              title: '进度条',
              startAngle: 90,
              endAngle: -270,
              background: '#0000ff',
            },
          },
        },
      },
    ],
  },
  {
    name: 'topo charts',
    show: true,
    list: [
      {
        name: '折线图',
        icon: 't-icon t-line-chart',
        data: {
          name: 'lineChart',
          width: 400,
          disableAnchor: true,
          height: 200,
          form: [
            {
              key: 'data',
              name: 'data 数据',
              type: 'code',
              language: 'json',
              isNotString: true,
            },
          ] as FormItemType[],
          chartsColor: [
            '#1890ff',
            '#2FC25B',
            '#FACC14',
            '#c23531',
            '#2f4554',
            '#61a0a8',
            '#d48265',
          ],
          xAxisData: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
          smooth: true,
          data: [
            [1820, 1932, 1901, 1934, 1990, 1830, 1920],
            [1710, 1932, 1901, 1834, 1700, 1830, 1720],
          ],
        },
      },
      {
        name: '柱状图',
        icon: 't-icon t-bar-chart',
        data: {
          name: 'histogram',
          x: 600,
          y: 100,
          width: 400,
          height: 200,
          disableAnchor: true,
          form: [
            {
              key: 'data',
              name: 'data 数据',
              type: 'code',
              language: 'json',
              isNotString: true,
            },
          ] as FormItemType[],
          chartsColor: [
            '#1890ff',
            '#2FC25B',
            '#FACC14',
            '#c23531',
            '#2f4554',
            '#61a0a8',
            '#d48265',
          ],
          xAxisData: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
          data: [
            [120, 200, 150, 80, 70, 110, 130],
            [140, 250, 150, 80, 60, 10, 30],
            [40, 50, 180, 210, 60, 70, 30],
          ],
        },
      },
      {
        name: '饼图',
        icon: 't-icon t-pie-chart',
        data: {
          name: 'pieChart',
          x: 100,
          y: 300,
          width: 400,
          height: 200,
          disableAnchor: true,
          form: [
            {
              key: 'data',
              name: 'data 数据',
              type: 'code',
              language: 'json',
              isNotString: true,
            },
          ] as FormItemType[],
          chartsColor: [
            '#1890ff',
            '#36CBCB',
            '#2FC25B',
            '#FACC14',
            '#F2637B',
            '#fc8452',
            '#9a60b4',
            '#ea7ccc',
          ],
          data: [
            [
              { value: 1048, name: 'Search Engine' },
              { value: 735, name: 'Direct' },
              { value: 580, name: 'Email' },
              { value: 484, name: 'Union Ads' },
              { value: 300, name: 'Video Ads' },
            ],
            [
              { value: 1548, name: 'Search' },
              { value: 775, name: 'Direct' },
              { value: 679, name: 'Market' },
            ],
          ],
          chartsRadius: [
            ['60%', '70%'],
            ['0%', '50%'],
          ],
        },
      },
      {
        name: '仪表盘',
        icon: 't-icon t-dashboard-chart',
        data: {
          name: 'gauge',
          x: 600,
          y: 300,
          width: 400,
          height: 400,
          disableAnchor: true,
          value: 90,
          unit: 'm/s',
          axisLine: [
            [0.3, '#67e0e3'],
            [0.7, '#37a2da'],
            [1, '#fd666d'],
          ],
          animateCycle: 1,
          keepAnimateState: 0,
        },
      },
      {
        name: '时钟',
        icon: 't-icon t-07',
        data: {
          name: 'gauge',
          x: 600,
          y: 300,
          width: 400,
          height: 400,
          disableAnchor: true,
          isClock: true,
          startAngle: 90,
          endAngle: -270,
          min: 0,
          max: 12,
          splitNumber: 12,
          background: '#3A3A3A',
          color: '#C0911F',
        },
      },
    ],
  },

  {
    name: '故障树',
    show: true,
    list: [
      {
        name: '与门',
        icon: 't-icon t-ANDmen',
        data: {
          name: 'andGate',
          width: 100,
          height: 150,
        },
      },
      {
        name: '基本事件',
        icon: 't-icon t-jibenshijian',
        data: {
          name: 'basicEvent',
          width: 100,
          height: 150,
        },
      },
      {
        name: '未展开事件',
        icon: 't-icon t-weizhankaishijian',
        data: {
          name: 'unexpandedEvent',
          width: 100,
          height: 150,
        },
      },
      {
        name: '优先AND门',
        icon: 't-icon t-youxianANDmen',
        data: {
          name: 'priorityAndGate',
          width: 100,
          height: 150,
        },
      },
      {
        name: '禁止门',
        icon: 't-icon t-jinzhimen',
        data: {
          name: 'forbiddenGate',
          width: 100,
          height: 150,
        },
      },
      {
        name: '事件',
        icon: 't-icon t-shijian',
        data: {
          name: 'event',
          width: 100,
          height: 150,
        },
      },
      {
        name: '开关事件',
        icon: 't-icon t-kaiguanshijian',
        data: {
          name: 'switchEvent',
          width: 100,
          height: 150,
        },
      },
      {
        name: '条件事件',
        icon: 't-icon t-tiaojianshijian',
        data: {
          name: 'conditionalEvent',
          width: 150,
          height: 100,
        },
      },
      {
        name: '转移符号',
        icon: 't-icon t-zhuanyifuhao',
        data: {
          name: 'transferSymbol',
          width: 100,
          height: 150,
        },
      },
      {
        name: '或门',
        icon: 't-icon t-ORmen',
        data: {
          name: 'orGate',
          width: 100,
          height: 150,
        },
      },
      {
        name: '异或门',
        icon: 't-icon t-yihuomen',
        data: {
          name: 'xorGate',
          width: 100,
          height: 150,
        },
      },
      {
        name: '表决门',
        icon: 't-icon t-biaojuemen',
        data: {
          name: 'votingGate',
          width: 100,
          height: 150,
        },
      },
    ],
  },*/
];

// 新版本核心库已经没有该属性，但我们仍然可以配置一些默认值
export const lineDashObj = [
  undefined,
  [5, 5],
  [10, 10],
  [10, 10, 2, 10],
  [1, 16],
];

export const canCheckProps = [
  {
    label: t('文字'),
    value: 'text',
  },
  {
    label: t('状态'),
    value: 'status',
  },
  {
    label: t('紧急停车'),
    value: 'statusEmergstop',
  },
  {
    label: t('跳停'),
    value: 'statusSkipStop',
  },
  {
    label: t('扣车'),
    value: 'statusDetainCar',
  },
  {
    label: t('站台门'),
    value: 'statusZhantaimeng',
  }
];

export const canSetProps = [
  {
    label: t('背景颜色'),
    value: 'background',
  },
  {
    label: t('颜色'),
    value: 'color',
  },
  {
    label: t('文字'),
    value: 'text',
  },
  {
    label: t('宽度'),
    value: 'width',
  },
  { label: t('高度'), value: 'height' },
  {
    label: t('显示'),
    value: 'visible',
  },
  {
    label: t('进度值'),
    value: 'progress',
  },
  {
    label: t('值'),
    value: 'value',
  },
  {
    label: t('状态'),
    value: 'showChild',
  },
  // TODO: 待新增
];

export function changeType(value: string) {
  if (value === 'false') {
    return false;
  } else if (value === 'true') {
    return true;
  } else if (
    value === '' ||
    isNaN(Number(value)) ||
    (typeof value === 'string' && value.endsWith('.'))
  ) {
    // 小数也进入这个
    return value;
  }
  return Number(value);
}

/**
 * 默认动画
 */
export const normalAnimate = {
  upDown: [
    {
      y: -10,
      duration: 100,
    },
    { y: 0, duration: 100 },
    { y: -10, duration: 200 },
  ],
  leftRight: [
    {
      x: -10,
      duration: 100,
    },
    {
      x: 10,
      duration: 80,
    },
    {
      x: -10,
      duration: 50,
    },
    {
      x: 10,
      duration: 30,
    },
    {
      x: 0,
      duration: 300,
    },
  ],
  heart: [
    {
      // 通过 scale 来替代原版心跳
      scale: 1.1,
      duration: 100,
    },
    {
      scale: 1,
      duration: 400,
    },
  ],
  success: [{ background: '#389e0d22', color: '#237804', duration: 500 }],
  warning: [
    {
      color: '#fa8c16',
      lineDash: [10, 10],
      duration: 300,
    },
    {
      color: '#fa8c16',
      lineDash: undefined,
      duration: 500,
    },
    {
      color: '#fa8c16',
      lineDash: [10, 10],
      duration: 300,
    },
  ],
  error: [{ color: '#cf1322', background: '#cf132222', duration: 100 }],
  show: [
    {
      color: '#fa541c',
      rotate: -10,
      duration: 100,
    },
    {
      color: '#fa541c',
      rotate: 10,
      duration: 100,
    },
    {
      color: '#fa541c',
      rotate: 0,
      duration: 100,
    },
  ],
  rotate: [
    {
      rotate: 360,
      duration: 1000,
    },
  ],
};

export const normalFonts = [
  'serif',
  'sans-serif',
  'monospace',
  'cursive',
  'fantasy',
  'system-ui',
  'emoji',
  'math',
  'fangsong',
  '宋体',
  '微软雅黑',
  '黑体',
  '楷体',
];

export const whiteSpaces = [
  {
    label: `${t('默认')}`,
    value: '',
  },
  {
    label: `${t('不换行')}`,
    value: 'nowrap',
  },
  {
    label: `${t('回车换行')}`,
    value: 'pre-line',
  },
  {
    label: `${t('永远换行')}`,
    value: 'break-all',
  },
] as const;

/**
 * 添加 showChild 配置项到 configs 中，副作用函数，会更改 configs
 * @param pen 画笔，判断它是否有子元素，无状态即显示全部
 * @param configs 原配置项
 */
export function addShowChild(pen: Pen, configs: FormItemType[]) {
  const children = pen.children;
  if (Array.isArray(children) && children.length > 0) {
    configs.push({
      key: 'showChild',
      type: 'select',
      name: `${t('状态')}`,
      options: [
        {
          label: t('无'),
          value: null,
        },
        ...children.map((child, index: number) => {
          return {
            label: `${t('状态')} ${index + 1}`,
            value: index,
          };
        }),
      ],
    });
  }
}
