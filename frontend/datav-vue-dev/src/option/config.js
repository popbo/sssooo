const path = process.env.VUE_APP_PATH
//基本配置
export const config = {
  width: 1920,
  height: 1080,
  query: '',
  header: '',
  mark: {
    show: false,
    text: '',
    fontSize: 20,
    color: 'rgba(100,100,100,0.2)',
    degree: -20,
  },
  scale: 1,
  // backgroundImage: `${path}img/bg/bg.png`,
  backgroundImage: '',
  backgroundColor: '#232630',
  url: '',
  gradeShow: false,
  gradeLen: 30,
  filter: {
    show: false,
    hueRotate: 0, // 色相 -180~180 单位deg
    saturate: 0, // 饱和度 -100~100 单位%
    brightness: 0, // 明度 -100~100 单位%
    contrast: 0, // 对比度 -100~100 单位%
    opacity: 100, // 不透明度 0~100 单位%
    grayscale: 0, // 灰度 0~100 单位%
  },
}
// 颜色的配置
export const colorOption = {
  menuWidth: 110,
  refreshBtn: false,
  columnBtn: false,
  labelWidth: 100,
  column: [
    {
      label: '颜色1',
      prop: 'color1',
      type: 'color',
    },
    {
      label: '渐变色',
      prop: 'color2',
      type: 'color',
    },
    {
      label: '位置',
      prop: 'postion',
      type: 'number',
    },
  ],
}
export const axisLineColorOption = {
  menuWidth: 110,
  refreshBtn: false,
  columnBtn: false,
  labelWidth: 100,
  column: [
    {
      label: '颜色1',
      prop: 'color1',
      type: 'color',
    },
    {
      label: '渐变色',
      prop: 'color2',
      type: 'color',
    },
    {
      label: '位置',
      prop: 'postion',
      type: 'number',
      required: true,
      rules: [
        {
          required: true,
          message: '位置不能为空',
          trigger: 'blur',
        },
      ],
    },
  ],
}
// 预测面积图面积堆积色
export const basicAreaColorOption = {
  menuWidth: 110,
  // menu: false,
  refreshBtn: false,
  columnBtn: false,
  addBtn: false,
  delBtn: false,
  labelWidth: 100,
  column: [
    {
      label: '颜色',
      prop: 'color1',
      type: 'color',
    },
    {
      label: '渐变色',
      prop: 'color2',
      type: 'color',
    },
    {
      label: '位置',
      prop: 'postion',
      type: 'number',
    },
  ],
}
// 不带位置和渐变色的配置
export const colorOption1 = {
  menuWidth: 150,
  refreshBtn: false,
  columnBtn: false,
  labelWidth: 100,
  column: [
    {
      label: '颜色',
      prop: 'color1',
      type: 'color',
    },
  ],
}
// 不带位置和渐变色的配置
export const colorOption2 = {
  menuWidth: 100,
  refreshBtn: false,
  columnBtn: false,
  labelWidth: 100,
  column: [
    {
      label: '颜色',
      prop: 'color1',
      type: 'color',
    },
    {
      label: '渐变色',
      prop: 'color2',
      type: 'color',
      align: 'left',
    },
    {
      label: '透明度',
      prop: 'opacity',
      type: 'number',
      value: 90,
      rules: {
        required: true,
        message: '请输入透明度',
        trigger: 'blur',
      },
    },
  ],
}
// 带渐变色和透明度的配置
export const colorOption3 = {
  menuWidth: 150,
  refreshBtn: false,
  columnBtn: false,
  labelWidth: 100,
  column: [
    {
      label: '颜色',
      prop: 'color1',
      type: 'color',
    },
    {
      label: '位置',
      prop: 'postion',
      type: 'number',
    },
  ],
}
// 表格的列配置
export const tableOption = {
  menuWidth: 150,
  refreshBtn: false,
  columnBtn: false,
  labelWidth: 100,
  column: [
    {
      label: '名称',
      prop: 'label',
    },
    {
      label: 'key值',
      prop: 'prop',
    },
    {
      label: '宽度',
      prop: 'width',
      hide: true,
    },
    {
      label: '状态',
      prop: 'hide',
      type: 'switch',
      hide: true,
      value: false,
      dicData: [
        {
          label: '隐藏',
          value: true,
        },
        {
          label: '显示',
          value: false,
        },
      ],
    },
  ],
}
//一些字典的配置
export const dicOption = {
  line: [
    {
      label: '线条',
      value: 'line',
    },
    {
      label: '圆环',
      value: 'circle',
    },
  ],
  fontWeight: [
    {
      label: 'normal',
      value: 'normal',
    },
    {
      label: 'bold',
      value: 'bold',
    },
    {
      label: 'bolder',
      value: 'bolder',
    },
    {
      label: 'lighter',
      value: 'lighter',
    },
  ],
  border: [
    {
      label: '无边框',
      value: '',
    },
    {
      label: '内置图片',
      value: 'img',
    },
    {
      label: '内置边框',
      value: 'border',
    },
  ],
  borderStyle: [
    {
      label: 'none',
      value: 'none',
    },
    {
      label: 'hidden',
      value: 'hidden',
    },
    {
      label: 'dotted',
      value: 'dotted',
    },
    {
      label: 'dashed',
      value: 'dashed',
    },
    {
      label: 'solid',
      value: 'solid',
    },
    {
      label: 'double',
      value: 'double',
    },
    {
      label: 'groove',
      value: 'groove',
    },
    {
      label: 'ridge',
      value: 'ridge',
    },
    {
      label: 'inset',
      value: 'inset',
    },
    {
      label: 'outset',
      value: 'outset',
    },
  ],
  fontFamily: [
    {
      label: '宋体',
      value: 'SimSun',
    },
    // {
    //   label: '微软雅黑',
    //   value: 'Microsoft Yahei',
    // },
    // {
    //   label: '华文黑体',
    //   value: 'STHeiti',
    // },
    {
      label: 'SourceHanSansCN-Regular',
      value: 'SourceHanSansCN-Regular',
    },
  ],
  flexAlign: [
    {
      label: '居中',
      value: 'center',
    },
    {
      label: '左对齐',
      value: 'flex-start',
    },
    {
      label: '右对齐',
      value: 'flex-end',
    },
  ],
  textAlign: [
    {
      label: '居中',
      value: 'center',
    },
    {
      label: '左对齐',
      value: 'left',
    },
    {
      label: '右对齐',
      value: 'right',
    },
  ],
  xAlign: [
    {
      label: '居中',
      value: 'center',
    },
    {
      label: '左对齐',
      value: 'left',
    },
    {
      label: '右对齐',
      value: 'right',
    },
  ],
  yAlign: [
    {
      label: '居中',
      value: 'center',
    },
    {
      label: '上对齐',
      value: 'top',
    },
    {
      label: '下对齐',
      value: 'bottom',
    },
  ],
  alignment: [
    {
      label: '居中',
      value: 'center',
    },
    {
      label: '左对齐',
      value: 'flex-start',
    },
    {
      label: '右对齐',
      value: 'flex-end',
    },
  ],
  lineDirection: [
    {
      label: '横向',
      value: 'transverse',
    },
    {
      label: '纵向',
      value: 'portrait',
    },
  ],
  linePosition: [
    {
      label: '下方',
      value: 'borderBottom',
    },
    {
      label: '上方',
      value: 'borderTop',
    },
    // {
    //   label: '左侧',
    //   value: 'borderLeft',
    // },
    // {
    //   label: '右侧',
    //   value: 'borderRight',
    // },
  ],
  iconPosition: [
    {
      label: '左侧',
      value: 'left',
    },
    {
      label: '右侧',
      value: 'right',
    },
  ],
  animationModals: [
    {
      label: '逐条滚动',
      value: 'byItem',
    },
    {
      label: '逐页滚动',
      value: 'byPage',
    },
  ],
  arrowSet: [
    {
      label: '无',
      value: 'none',
    },
    {
      label: '前',
      value: 'front',
    },
    {
      label: '后',
      value: 'back',
    },
    {
      label: '双向',
      value: 'both',
    },
  ],
  sharpStyle: [
    {
      label: '实线',
      value: 'solid',
    },
    {
      label: '虚线',
      value: 'dashed',
    },
  ],
  customSegmentsNodeStyle: [
    {
      label: '无',
      value: 'none',
    },
    {
      label: '空心箭头',
      value: 'hollowArrows',
    },
    {
      label: '实心箭头',
      value: 'solidArrows',
    },
    {
      label: '空心圆',
      value: 'hollowCircles',
    },
    {
      label: '实心圆',
      value: 'solidCircles',
    },
  ],
  customSegmentsMidNodeStyle: [
    {
      label: '无',
      value: 'none',
    },
    {
      label: '空心圆',
      value: 'hollowCircles',
    },
    {
      label: '实心圆',
      value: 'solidCircles',
    },
  ],
  dataType: [
    {
      label: '静态数据',
      value: 0,
    },
    {
      label: '接口数据源',
      value: 1,
    },
    {
      label: '数据库数据源',
      value: 2,
    },
    {
      label: '实时接口数据源',
      value: 3,
    },
  ],
  orientList: [
    {
      label: '竖排',
      value: 'vertical',
    },
    {
      label: '横排',
      value: 'horizontal',
    },
  ],
  dataMethod: [
    {
      label: 'POST',
      value: 'post',
    },
    {
      label: 'GET',
      value: 'get',
    },
  ],
  renderingList:['text','img','sharpLine','table','customSegments','bar'],
  eventList: ['tabs', 'text', 'flop', 'universalTabs'],
  dataList: [
    'datav',
    'text',
    'wordcloud',
    'img',
    'tabs',
    'map',
    'video',
    'clapper',
    'pie',
    'pictorialbar',
    'iframe',
    'swiper',
    'flop',
    'bar',
    'line',
    'progress',
    'table',
    'crosstable',
    'gauge',
    'funnel',
    'scatter',
    'radar',
    'img',
    'imgborder',
    'test',
    'imgList',
    'imgTabs',
    'basicarea',
    'line-bar',
    'biaxlinebar',
    'tree',
    'crosstable',
    'dataStorage',
    'progressView',
    'progressBar',
    'ZebraProgressBar',
    'flv',
    'select',
    'sharpLine',
    'stereoscopicBar',
    'stereoscopicPie',
    'newTree',
    'switchMultiple',
    'newPictorialBar',
    'PeakBar',
    'universalTabs',
    'scrollRanking',
    'eventRankingList',
    'threeModel',
    'customSegments',
    'radioMultiple',
    'checkbox',
    'common',
    'domModule',
    'liquidFill',
    'textRankingList',
    'relationshipDiagram',
    'roadMap',
    'customVue',
    'easyWasmPlayer'
  ],
  conditionList:[
    'datav',
    'text',
    'wordcloud',
    'img',
    'tabs',
    'map',
    'video',
    'clapper',
    'pie',
    'pictorialbar',
    'iframe',
    'swiper',
    'flop',
    'bar',
    'line',
    'progress',
    'table',
    'crosstable',
    'gauge',
    'funnel',
    'scatter',
    'radar',
    'img',
    'imgborder',
    'test',
    'imgList',
    'imgTabs',
    'basicarea',
    'line-bar',
    'biaxlinebar',
    'tree',
    'crosstable',
    'dataStorage',
    'progressView',
    'progressBar',
    'ZebraProgressBar',
    'flv',
    'select',
    'sharpLine',
    'stereoscopicBar',
    'stereoscopicPie',
    'newTree',
    'switchMultiple',
    'newPictorialBar',
    'PeakBar',
    'universalTabs',
    'scrollRanking',
    'eventRankingList',
    'threeModel',
    'customSegments',
    'radioMultiple',
    'checkbox',
    'common',
    'domModule',
    'liquidFill',
    'textRankingList',
    'relationshipDiagram',
    'roadMap',
    'easyWasmPlayer'
  ],
  themeList: [
    {
      label: '默认配色',
      value: 'avue',
    },
    {
      label: '紫色主题',
      value: 'macarons',
    },
    {
      label: '绿色主题',
      value: 'wonderland',
    },
  ],
  settingAxis: ['bar'],
  barList: [
    'bar',
    'line',
    'basicarea',
    'line-bar',
    'stereoscopicBar',
    'PeakBar',
    'scatter',
  ],
  titleList: [
    'bar',
    'pie',
    'line',
    'radar',
    'funnel',
    'basicarea',
    'line-bar',
    'biaxlinebar',
    'stereoscopicBar',
    'PeakBar',
  ],
  labelList: [
    'bar',
    'line',
    'pie',
    'radar',
    'scatter',
    'basicarea',
    'line-bar',
    'biaxlinebar',
    'stereoscopicBar',
    'PeakBar',
  ],
  legendList: [
    'bar',
    'pie',
    'line',
    'radar',
    'funnel',
    'basicarea',
    'line-bar',
    'biaxlinebar',
    'stereoscopicBar',
    'PeakBar',
  ],
  xYlegendList: ['pie'],
  colorList: [
    'bar',
    'line',
    'funnel',
    'scatter',
    // 'radar',
    // 'basicarea',
    'line-bar',
    'biaxlinebar',
    'stereoscopicBar',
    'PeakBar',
  ],
  pieColorList: [
    'pie',
    'stereoscopicPie'
  ],
  pieLine: [
    'pie',
  ],
  tipCarouselList:[
    'bar',
    'line',
    'newPictorialBar',
    'PeakBar',
    'scatter',
    'line-bar',
    'biaxlinebar',
  ],
  tipList: [
    'bar',
    'pie',
    'line',
    'funnel',
    'scatter',
    'radar',
    'basicarea',
    'line-bar',
    'biaxlinebar',
    'stereoscopicBar',
    'PeakBar',
    'pictorialbar',
    'newPictorialBar',
  ],
  postionList: [
    'bar',
    'line',
    'pictorialbar',
    'basicarea',
    'line-bar',
    'biaxlinebar',
    'stereoscopicBar',
    'PeakBar',
  ],
  labelFormatterList: [
    'bar',
    'map',
    'line',
    'pie',
    'gauge',
    'funnel',
    'scatter',
    'radar',
    'basicarea',
    'line-bar',
    'biaxlinebar',
  ],
  animationList: ['pie'],
  // animationBarList: ['bar'],
  noBarColorList: ['pie', 'scatter', 'funnel','biaxlinebar'],
  funnel: ['funnel'],
  radar: ['radar'],
  bar: ['bar'],
  tabsTypeList: [
    {
      label: '选项卡',
      value: 'tabs',
    },
    {
      label: '选择框',
      value: 'select',
    },
  ],
  functionTypes: [
    {
      label: '导出',
      value: 'leadingOut',
    },
    {
      label: '获取表格选中数据',
      value: 'selectData',
    },
    {
      label: '导入',
      value: 'leadingIn',
    },
    {
      label: '打印',
      value: 'print',
    },
    {
      label: '下载',
      value: 'download',
    },
    {
      label: '上传',
      value: 'upload',
    },
  ],
  associatedComponents: [
    {
      label: '表格',
      value: 'table',
    },
  ],
  mapType: [
    {
      label: '原生',
      value: 0,
    },
  ],
  target: [
    {
      label: '本窗口',
      value: '_self',
    },
    {
      label: '新窗口',
      value: '_blank',
    },
  ],
  swiperType: [
    {
      label: '普通',
      value: '',
    },
    {
      label: '立体',
      value: 'card',
    },
  ],
  // swiperIndicator: [
  //   {
  //     label: '外部',
  //     value: 'outside',
  //   },
  //   {
  //     label: '不显示',
  //     value: 'none',
  //   },
  // ],
  swiperIndicator: [
    {
      label: '样式一',
      value: 'inside',
    },
    {
      label: '样式二',
      value: 'outside',
    },
    {
      label: '样式三',
      value: 'type3',
    },
  ],
  format: [
    {
      label: '日期',
      value: 'yyyy-MM-dd',
    },
    {
      label: '日期+时分',
      value: 'yyyy-MM-dd hh:mm',
    },
    {
      label: '日期+时分秒',
      value: 'yyyy-MM-dd hh:mm:ss',
    },
    {
      label: '日期(无年)',
      value: 'MM-dd',
    },
    {
      label: '时分',
      value: 'hh:mm',
    },
    {
      label: '时分秒',
      value: 'hh:mm:ss',
    },
    {
      label: '星期',
      value: 'day',
    },
  ],
  timeFormatList: [
    {
      label: 'YYYY-MM-DD',
      value: 'yyyy-MM-dd',
    },
    {
      label: 'YYYY-MM-DD HH:MM',
      value: 'yyyy-MM-dd hh:mm',
    },
  ],
  authenticationArr: [
    {
      label: 'No Auth',
      value: 'noAuth',
    },
    {
      label: 'Basic Auth',
      value: 'basicAuth',
    },
  ],
  positionList: [
    {
      label: '固定位置',
      value: 1,
    },
    {
      label: '跟随鼠标',
      value: 2,
    },
  ],
  verticalAlign: [
    {
      label: '顶部',
      value: 'top',
    },
    {
      label: '中间',
      value: 'middle',
    },
    {
      label: '底部',
      value: 'bottom',
    },
  ],
  OverflowEllipsis: [
    {
      label: '隐藏',
      value: 0,
    },
    {
      label: '换行',
      value: 1,
    },
  ],
  swiperConfig: ['swiper'],
  paginationPosition: [
    { label: '居左', value: 'flex-start' },
    { label: '居中', value: 'center' },
    { label: '居右', value: 'flex-end' },
  ],
  lineTypeList: [
    {
      label: '圆形',
      value: 'circle',
    },
    {
      label: '矩形',
      value: 'rect',
    },
  ],
  barlineTypeList: [
    {
      label: '矩形',
      value: 'rect',
    },
    {
      label: '样式一',
      value: 'yangOne',
    },
  ],
  iconList:['line'],
  barIconList:['bar']
}

function concat(prop, count, type, extend = [], defaults) {
  let list = []
  for (let i = 1; i <= count; i++) {
    list.push({
      label: prop + i,
      value: `${path}img/${prop}/${prop}${i}.${
        extend.includes(i) ? defaults : type
      }`,
    })
  }
  return list
}
export const dimensionFieldRequireList = [
  'bar',
  'stereoscopicBar',
  'stereoscopicPie',
  'line',
  'pie',
  'pictorialbar',
  'radar',
  'scatter',
  'funnel',
  'basicarea',
  'line-bar',
  'select',
  'PeakBar',
] // 维度必选的组件
export const measureFieldRequireList = [
  'bar',
  'stereoscopicBar',
  'stereoscopicPie',
  'line',
  'pie',
  'pictorialbar',
  'radar',
  'scatter',
  'funnel',
  'basicarea',
  'line-bar',
  'gauge',
  'progressView',
  'progressBar',
  'flop',
  'PeakBar',
] // 度量必选的组件
//加载图片素材库
export const imgOption = [
  concat('bg', 10, 'jpg', [1, 2, 3], 'png'),
  concat('border', 16, 'png'),
  concat(
    'source',
    260,
    'svg',
    [
      1, 15, 16, 20, 239.24, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250,
      251, 252, 253, 254, 255, 256, 257, 258, 259, 260,
    ],
    'png'
  ),
  concat('banner', 10, 'png'),
]

export function rgbaNum(rgba, index) {
  let val = rgba.match(/(\d(\.\d+)?)+/g)
  return val[index]
}
