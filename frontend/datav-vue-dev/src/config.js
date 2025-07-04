export default {
  COMPNAME: 'avue-echart-',
  NAME: 'list',
  DEAFNAME: 'item',
  SHARESECRETKEY: 'Y1z4sCWnKNHB9kfanWIb',
}
// export const url = window.$website.ur
export const url =
  process.env.NODE_ENV == 'production' ? '/stdc-visual' : window.$website.url
// export const url = "http://192.168.152.43:8083";
export const tip = `
//data为返回的数据
/**
 * 说明：只有样式编辑、数据处理、组件事件、请求头、请求参数方法需要返回函数
 * 静态数据或者配置数据直接返回JSON对象即可
 * 不写的话采用默认加载
*/
#样式编辑、数据处理、组件事件、请求头、请求参数
(data)=>{
  //处理数据逻辑
  return {
    //返回图表的标准数据结构体
  }
}

#事件处理
({name,type,value,data})=>{
  //直接写执行的逻辑即可
  alert(data,name)
}

#提示处理
(name,data) => {
  return 返回需要展示的文本
}

#文本框/图片框/Iframe框/数字翻牌器/视频播放器/实时视频播放器/图片等通用数据格式
{
  value:'xxxxx'
}

#柱状图/2.5D柱状图/折线图/斑马柱状图/山峰柱状图数据格式
{
  categories: ['一月', '二月', '三月', '四月', '五月'],
  series: [
    {
      name: 'Type1',
      data: [800, 400, 200, 100, 50],
    },
    {
      name: 'Type2',
      data: [800, 400, 200, 100, 50],
    },
  ],
}

#3D饼图数据格式
[
  {
    name: '一月',
    value: 800,
  },
  {
    name: '二月',
    value: 400,
  },
  {
    name: '三月',
    value: 200,
  },
  {
    name: '四月',
    value: 100,
  },
  {
    name: '五月',
    value: 50,
  },
  {
    name: '六月',
    value: 25,
  },
]

#饼图数据格式
[{
    "name": "PC",
    "value": 97,
    "url": "http://www.baidu.com"
},{
    "name": "PC",
    "value": 97,
    "url": "http://www.baidu.com"
}]

#象型图数据格式
[
  {
    name: '一月',
    value: 43,
  },
  {
    name: '二月',
    value: 80,
  },
  {
    name: '三月',
    value: 25,
  },
  {
    name: '四月',
    value: 28,
  },
  {
    name: '五月',
    value: 35,
  },
  {
    name: '六月',
    value: 50,
  },
]

#滚动排行数据格式
[
  {
    label: '北京',
    value: 90,
  },
  {
    label: '上海',
    value: 80,
  },
  {
    label: '广州',
    value: 70,
  },
  {
    label: '深圳',
    value: 60,
  },
],

#雷达图数据格式
{
  indicator: [{
    name: '销售',
    max: 6500
  }],
  series: [{
    data: [{
      value: [4300, 10000, 28000, 35000, 50000, 19000],
      name: '预算分配（Allocated Budget）'
    }]
  }]
}

#散点图数据格式
[{
  data: [
    [1, 8.04],
    [2, 6.95]
  ]
},
{
  data: [
    [1, 4.04],
    [2, 3.95]
  ]
}]

#漏斗图数据格式
[{
  value: 335,
  name: '直接访问'
},
{
  value: 310,
  name: '邮件营销'
},
{
  value: 234,
  name: '联盟广告'
}]

#预测面积图数据格式
{
  categories: [
    '1月',
    '2月',
    '3月',
    '4月',
    '5月',
    '6月',
    '7月',
    '8月',
    '9月',
    '10月',
    '11月',
    '12月',
  ],
  series: [
    {
      name: '实际',
      data: [26, 25, 20, 21],
    },
    {
      name: '预测',
      //  实际数据有的在预测数据中需要使用-来区分
      data: [999, 99, 90, 21, 30, 32, 44, 30, 13, 17, 50, 23],
    },
  ],
}

#折线柱状混合图数据格式
{
  categories: ['一月', '二月', '三月', '四月', '五月'],
  series: [
    {
      name: 'Type1',
      type: 'bar',
      data: [28, 18, 38, 84, 58],
    },
    {
      name: 'Type2',
      type: 'line',
      data: [88, 78, 68, 54, 48],
    },
  ],
}

#仪表盘数据格式
{
  min: 0,
  max: 100,
  label: '名称',
  value: 50,
  unit: '%',
}

#环形进度条/条形进度条/斑马进度条数据格式
{
  label: '人数增涨',
  value: 181,
}

#表格数据格式
{
  data: [
    {
      id: 2,
      type1: '数据2',
      type2: '数据2',
      type3: '数据2',
      type4: '数据2',
    },
    {
      id: 1,
      type1: '数据1',
      type2: '数据1',
      type3: '数据1',
      type4: '数据1',
    },
    {
      id: 3,
      type1: '数据3',
      type2: '数据3',
      type3: '数据3',
      type4: '数据3',
    },
  ],
  column: [
    {
      label: '序列',
      prop: 'id',
      isSequence: true,
    },
    {
      label: '车牌号',
      prop: 'type1',
    },
    {
      label: '驾驶员',
      prop: 'type2',
    },
    {
      label: '里程',
      prop: 'type3',
    },
    {
      label: '油耗',
      prop: 'type4',
    },
  ],
}

#事件排行列表数据格式
[
  {
    title: '事件1',
    detail: '详情1',
    time: '2022-9-12 12:00:00',
  },
  {
    title: '事件2',
    detail: '详情2',
    time: '2022-9-12 12:00:00',
  },
  {
    title: '事件3',
    detail: '详情3',
    time: '2022-9-12 12:00:00',
  },
]

#层级树数据格式/#侧边菜单栏数据格式
[
  {
    id: 1,
    label: '三国演义',
    children: [
      {
        id: 4,
        label: '官渡之战',
        children: [
          {
            id: 9,
            label: '曹孟德',
          },
          {
            id: 10,
            label: '袁本初',
          },
        ],
      },
    ],
  },
  {
    id: 2,
    label: '水浒传',
    children: [
      {
        id: 5,
        label: '三打祝家庄',
      },
      {
        id: 6,
        label: '曾头市之战',
      },
    ],
  },
]

#选项卡数据格式/#通用Tabs数据格式
[
  {
    label: '选项卡1',
    value: '1',
    icon: '/img/icon1.png',
    empIcon: '/img/icon2.png',
  },
  {
    label: '选项卡2',
    value: '2',
    icon: '/img/icon1.png',
    empIcon: '/img/icon2.png',
  },
]

#下拉框数据格式
[
  { label: '上海', value: 'sh' },
  { label: '南京', value: 'nj' },
  { label: '重庆', value: 'cq' },
  { label: '武汉', value: 'wh' },
]

#开关组数据格式
[
  {
    deviceId: 'L06-S01-GOK62501',
    key: 'value',
    value: {
      current: true,
      0: '开站',
      1: '关站',
    },
    label: '开站/关站控制',
    event: {
      openKey: 'DI001',
      openValue: '1',
      closeKey: 'DI002',
      closeValue: '2',
    },
  },
  {
    deviceId: 'L06-S01-ASD218',
    key: 'value',
    value: {
      current: false,
      0: '开站',
      1: '关站',
    },
    label: '开站/关站控制',
    event: {
      openKey: 'DI001',
      openValue: '1',
      closeKey: 'DI002',
      closeValue: '2',
    },
  },
]

#3d模型数据格式
{
  model: '/model/ldd.glb',
  img: '/model/monkey.png',
}

#轮播图数据格式
[{
  title: '标题一',
  value: '图片地址'
},
{
  title: '标题二',
  value: '图片地址2'
}]

#FLV视频播放器数据格式
[
  {
    name: 0,
    url: '/cs.flv',
  },
  {
    name: 1,
    url: '/cs.flv',
  },
]

#单选组数据格式/#复选框数据格式
[
  {
    id: 1,
    label: 'content-1',
    value: 1,
  },
  {
    id: 2,
    label: 'content-2',
    value: 2,
  },
  {
    id: 3,
    label: 'content-3',
    value: 3,
  },
]

#交叉表数据格式
{
  "total": 8,
  "data": [
    ["15", "33", "22", "44"],
    ["98", "23", "55", "0"],
    ["2", "2323", "105", "1998"],
    ["2213", "213", "0", "44"],
    ["89", "58", "76", "27"],
    ["70", "24", "44", "0"],
    ["90", "78", "111", "67"],
    ["121", "90", "0", "23"]
  ],
  "fieldS": [
    {
      "originName": "LINE_ID",
      "name": "线路编号"
    },
    {
      "originName": "LINE_NAME",
      "name": "线路名称"
    },
    {
      "originName": "STATION_ID",
      "name": "车站编号"
    },
    {
      "originName": "STATION_NAME",
      "name": "车站名称"
    },
    {
      "originName": "TOTAL_IN",
      "name": "进站量"
    },
    {
      "originName": "SALE_COUNT",
      "name": "售票量"
    }
  ],
  "rowHeads": [
    {
      "fieldName": "线路名称",
      "value": "杭州地铁1号线",
      "child": [
        {
          "fieldName": "线路编号",
          "value": "3",
          "child": [],
          "originName": "LINE_ID"
        }
      ],
      "originName": "LINE_NAME"
    },
    {
      "fieldName": "线路名称",
      "value": "杭州地铁2号线",
      "child": [
        {
          "fieldName": "线路编号",
          "value": "4",
          "child": [],
          "originName": "LINE_ID"
        }
      ],
      "originName": "LINE_NAME"
    },
    {
      "fieldName": "线路名称",
      "value": "杭州地铁3号线",
      "child": [
        {
          "fieldName": "线路编号",
          "value": "1",
          "child": [],
          "originName": "LINE_ID"
        }
      ],
      "originName": "LINE_NAME"
    },
    {
      "fieldName": "线路名称",
      "value": "杭州地铁6号线",
      "child": [
        {
          "fieldName": "线路编号",
          "value": "2",
          "child": [],
          "originName": "LINE_ID"
        }
      ],
      "originName": "LINE_NAME"
    }
  ],
  "colHeads": [
    {
      "fieldName": "车站名称",
      "value": "江汉路",
      "child": [
        {
          "fieldName": "车站编号",
          "value": "12627",
          "child": [
            {
              "fieldName": "进站量",
              "value": "进站量",
              "child": [],
              "originName": "TOTAL_IN"
            },
            {
              "fieldName": "售票量",
              "value": "售票量",
              "child": [],
              "originName": "SALE_COUNT"
            }
          ],
          "originName": "STATION_ID"
        }
      ],
      "originName": "STATION_NAME"
    },
    {
      "fieldName": "车站名称",
      "value": "龙翔桥",
      "child": [
        {
          "fieldName": "车站编号",
          "value": "582",
          "child": [
            {
              "fieldName": "进站量",
              "value": "进站量",
              "child": [],
              "originName": "TOTAL_IN"
            },
            {
              "fieldName": "售票量",
              "value": "售票量",
              "child": [],
              "originName": "SALE_COUNT"
            }
          ],
          "originName": "STATION_ID"
        }
      ],
      "originName": "STATION_NAME"
    },
    {
      "fieldName": "车站名称",
      "value": "武林广场",
      "child": [
        {
          "fieldName": "车站编号",
          "value": "265",
          "child": [
            {
              "fieldName": "进站量",
              "value": "进站量",
              "child": [],
              "originName": "TOTAL_IN"
            },
            {
              "fieldName": "售票量",
              "value": "售票量",
              "child": [],
              "originName": "SALE_COUNT"
            }
          ],
          "originName": "STATION_ID"
        }
      ],
      "originName": "STATION_NAME"
    },
    {
      "fieldName": "车站名称",
      "value": "火车东站",
      "child": [
        {
          "fieldName": "车站编号",
          "value": "595",
          "child": [
            {
              "fieldName": "进站量",
              "value": "进站量",
              "child": [],
              "originName": "TOTAL_IN"
            },
            {
              "fieldName": "售票量",
              "value": "售票量",
              "child": [],
              "originName": "SALE_COUNT"
            }
          ],
          "originName": "STATION_ID"
        }
      ],
      "originName": "STATION_NAME"
    }
  ]
}

 `
