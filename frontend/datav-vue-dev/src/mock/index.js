import Mock from 'mockjs'
import { getResult } from './utils.js'
//柱状图
Mock.mock(/\/bar/, 'get', res => {
  const data = {
    categories: ['一月', '二月', '三月', '四月', '五月'],
    series: [
      {
        name: 'Type1',
        data: [180, 200, 160, 200, 180],
      },
      {
        name: 'Type2',
        data: [320, 120, 180, 160, 220],
      },
    ],
  }
  return getResult(data)
})
//折线图
Mock.mock(/\/line/, 'get', () => {
  const data = {
    categories: ['一月', '二月', '三月', '四月', '五月'],
    series: [
      {
        name: 'Type1',
        data: [65, 55, 100, 95, 105],
      },
      {
        name: 'Type2',
        data: [20, 20, 155, 54, 55],
      },
    ],
  }
  return getResult(data)
})
//饼图
Mock.mock(/\/pie/, 'get', () => {
  const data = [
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
  return getResult(data)
})
//表格1
Mock.mock(/\/table1/, 'get', () => {
  const data = [
    {
      jg: '开源中国',
      je: '12345/2333',
      sy: '<span>-26%</span>/+5%',
      yx: '12345/2333',
      jnc: '-26%/+5%',
      jl: 'smallwei',
    },
    {
      jg: '开源中国',
      je: '12345/2333',
      sy: '<span>-26%</span>/+5%',
      yx: '12345/2333',
      jnc: '-26%/+5%',
      jl: 'smallwei',
    },
    {
      jg: '开源中国',
      je: '12345/2333',
      sy: '<span>-26%</span>/+5%',
      yx: '12345/2333',
      jnc: '-26%/+5%',
      jl: 'smallwei',
    },
    {
      jg: '开源中国',
      je: '12345/2333',
      sy: '<span>-26%</span>/+5%',
      yx: '12345/2333',
      jnc: '-26%/+5%',
      jl: 'smallwei',
    },
    {
      jg: '开源中国',
      je: '12345/2333',
      sy: '<span>-26%</span>/+5%',
      yx: '12345/2333',
      jnc: '-26%/+5%',
      jl: 'smallwei',
    },
    {
      jg: '开源中国',
      je: '12345/2333',
      sy: '<span>-26%</span>/+5%',
      yx: '12345/2333',
      jnc: '-26%/+5%',
      jl: 'smallwei',
    },
    {
      jg: '开源中国',
      je: '12345/2333',
      sy: '<span>-26%</span>/+5%',
      yx: '12345/2333',
      jnc: '-26%/+5%',
      jl: 'smallwei',
    },
    {
      jg: '开源中国',
      je: '12345/2333',
      sy: '<span>-26%</span>/+5%',
      yx: '12345/2333',
      jnc: '-26%/+5%',
      jl: 'smallwei',
    },
    {
      jg: '开源中国',
      je: '12345/2333',
      sy: '<span>-26%</span>/+5%',
      yx: '12345/2333',
      jnc: '-26%/+5%',
      jl: 'smallwei',
    },
    {
      jg: '开源中国',
      je: '12345/2333',
      sy: '<span>-26%</span>/+5%',
      yx: '12345/2333',
      jnc: '-26%/+5%',
      jl: 'smallwei',
    },
    {
      jg: '开源中国',
      je: '12345/2333',
      sy: '<span>-26%</span>/+5%',
      yx: '12345/2333',
      jnc: '-26%/+5%',
      jl: 'smallwei',
    },
    {
      jg: '开源中国',
      je: '12345/2333',
      sy: '<span>-26%</span>/+5%',
      yx: '12345/2333',
      jnc: '-26%/+5%',
      jl: 'smallwei',
    },
    {
      jg: '开源中国',
      je: '12345/2333',
      sy: '<span>-26%</span>/+5%',
      yx: '12345/2333',
      jnc: '-26%/+5%',
      jl: 'smallwei',
    },
    {
      jg: '开源中国',
      je: '12345/2333',
      sy: '<span>-26%</span>/+5%',
      yx: '12345/2333',
      jnc: '-26%/+5%',
      jl: 'smallwei',
    },
    {
      jg: '开源中国',
      je: '12345/2333',
      sy: '<span>-26%</span>/+5%',
      yx: '12345/2333',
      jnc: '-26%/+5%',
      jl: 'smallwei',
    },
    {
      jg: '开源中国',
      je: '12345/2333',
      sy: '<span>-26%</span>/+5%',
      yx: '12345/2333',
      jnc: '-26%/+5%',
      jl: 'smallwei',
    },
  ]
  return getResult(data)
})
//表格2
Mock.mock(/\/table2/, 'get', () => {
  const data = [
    {
      name: 'smallwei',
      sj: '60',
      type: '抵押',
      je: '32400',
      jg: '开源中国',
      jl: 'smallwei',
    },
    {
      name: 'smallwei',
      sj: '60',
      type: '抵押',
      je: '32400',
      jg: '开源中国',
      jl: 'smallwei',
    },
    {
      name: 'smallwei',
      sj: '60',
      type: '抵押',
      je: '32400',
      jg: '开源中国',
      jl: 'smallwei',
    },
    {
      name: 'smallwei',
      sj: '60',
      type: '抵押',
      je: '32400',
      jg: '开源中国',
      jl: 'smallwei',
    },
    {
      name: 'smallwei',
      sj: '60',
      type: '抵押',
      je: '32400',
      jg: '开源中国',
      jl: 'smallwei',
    },
    {
      name: 'smallwei',
      sj: '60',
      type: '抵押',
      je: '32400',
      jg: '开源中国',
      jl: 'smallwei',
    },
    {
      name: 'smallwei',
      sj: '60',
      type: '抵押',
      je: '32400',
      jg: '开源中国',
      jl: 'smallwei',
    },
    {
      name: 'smallwei',
      sj: '60',
      type: '抵押',
      je: '32400',
      jg: '开源中国',
      jl: 'smallwei',
    },
  ]
  return getResult(data)
})
//翻牌器
Mock.mock(/\/table3/, 'get', () => {
  const data = [
    {
      suffixText: '申请户数(时)',
      data: 2333,
    },
    {
      suffixText: '申请户数(时)',
      data: 2333,
    },
    {
      suffixText: '申请户数(时)',
      data: 2333,
    },
    {
      suffixText: '申请户数(时)',
      data: 2333,
    },
    {
      suffixText: '申请户数(时)',
      data: 2333,
    },
    {
      suffixText: '申请户数(时)',
      data: 2333,
    },
  ]
  return getResult(data)
})
//表格4
Mock.mock(/\/table4/, 'get', () => {
  const data = [
    {
      suffixText: '申请户数(时)',
      data: 2333,
    },
    {
      suffixText: '申请户数(时)',
      data: 2333,
    },
    {
      suffixText: '申请户数(时)',
      data: 2333,
    },
    {
      suffixText: '申请户数(时)',
      data: 2333,
    },
    {
      suffixText: '申请户数(时)',
      data: 2333,
    },
    {
      suffixText: '申请户数(时)',
      data: 2333,
    },
  ]
  return getResult(data)
})

//字符云
Mock.mock(/\/wordCloud/, 'get', () => {
  const data = [
    {
      name: '三星',
      value: '1234',
    },
    {
      name: '小米',
      value: '1234',
    },
    {
      name: '华为',
      value: '1234',
    },
    {
      name: 'oppo',
      value: '1234',
    },
    {
      name: '抖音',
      value: '1234',
    },
    {
      name: '快手',
      value: '1234',
    },
    {
      name: '淘宝',
      value: '1234',
    },
    {
      name: '百度',
      value: '1234',
    },
    {
      name: '京东',
      value: '1234',
    },
    {
      name: '天猫',
      value: '1234',
    },
    {
      name: '字符1',
      value: '1234',
    },
    {
      name: '字符1',
      value: '1234',
    },
  ]
  return getResult(data)
})
//象型图
Mock.mock(/\/pictorialbar/, 'get', () => {
  const data = [
    {
      name: '一月',
      value: 180,
    },
    {
      name: '二月',
      value: 220,
    },
    {
      name: '三月',
      value: 250,
    },
    {
      name: '四月',
      value: 200,
    },
    {
      name: '五月',
      value: 150,
    },
    {
      name: '六月',
      value: 190,
    },
  ]
  return getResult(data)
})
//雷达图
Mock.mock(/\/radar/, 'get', () => {
  const data = {
    indicator: [
      {
        name: '审批',
        max: 100,
      },
      {
        name: '执法监督',
        max: 100,
      },
      {
        name: '运维',
        max: 100,
      },
      {
        name: '预警',
        max: 100,
      },
      {
        name: '协查',
        max: 100,
      },
      {
        name: '研判',
        max: 100,
      },
      {
        name: '侦听',
        max: 100,
      },
    ],
    series: [
      {
        data: [
          {
            value: [62, 54, 36, 88, 56, 91, 52],
            name: 'Type1',
          },
        ],
      },
    ],
    // indicator: [
    //   {
    //     name: '销售',
    //     max: 100,
    //   },
    //   {
    //     name: '管理',
    //     max: 100,
    //   },
    //   {
    //     name: '信息技术',
    //     max: 100,
    //   },
    //   {
    //     name: '客服',
    //     max: 100,
    //   },
    //   {
    //     name: '研发',
    //     max: 100,
    //   },
    //   {
    //     name: '市场',
    //     max: 100,
    //   },
    // ],
    // series: [
    //   {
    //     data: [
    //       {
    //         value: [43, 80, 28, 35, 50, 19],
    //         name: '预算分配（Allocated Budget）',
    //       },
    //       {
    //         value: [50, 80, 70, 30, 65, 50],
    //         name: '实际开销（Actual Spending）',
    //       },
    //     ],
    //   },
    // ],
  }
  return getResult(data)
})
//散点图
Mock.mock(/\/scatter/, 'get', () => {
  const data = [
    {
      data: [
        [1, 8.04],
        [2, 6.95],
      ],
    },
    {
      data: [
        [1, 4.04],
        [2, 3.95],
      ],
    },
  ]
  return getResult(data)
})
//仪表盘
Mock.mock(/\/gauge/, 'get', () => {
  const data = {
    min: 0,
    max: 100,
    label: '名称',
    value: 50,
    unit: '%',
  }
  return getResult(data)
})
//轮播图
Mock.mock(/\/swiper/, 'get', () => {
  const data = [
    {
      value:
        'https://img.alicdn.com/tfs/TB1v28TC8v0gK0jSZKbXXbK2FXa-1880-640.jpg',
      title: '标题一',
    },
    {
      value:
        'https://img.alicdn.com/tfs/TB1uevcCrj1gK0jSZFuXXcrHpXa-1880-640.jpg',
      title: '标题二',
    },
  ]
  return getResult(data)
})
//漏斗图
Mock.mock(/\/funnel/, 'get', () => {
  const data = [
    {
      value: 335,
      name: '直接访问',
    },
    {
      value: 310,
      name: '邮件营销',
    },
    {
      value: 234,
      name: '联盟广告',
    },
  ]
  return getResult(data)
})
