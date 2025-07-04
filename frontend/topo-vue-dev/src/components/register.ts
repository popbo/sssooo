import {
  activityDiagram,
  activityDiagramByCtx,
} from '@topometa2d/activity-diagram';
import { register as registerEcharts, registerHighcharts, registerLightningChart } from '@topometa2d/chart-diagram';
import { classPens } from '@topometa2d/class-diagram';
import {
  Meta2d,
  register,
  registerAnchors,
  registerCanvasDraw,
} from '@topometa2d/core';
import { flowAnchors, flowPens } from '@topometa2d/flow-diagram';
import { formPens } from '@topometa2d/form-diagram';
import { ftaAnchors, ftaPens, ftaPensbyCtx } from '@topometa2d/fta-diagram';
import { sequencePens, sequencePensbyCtx } from '@topometa2d/sequence-diagram';
import { chartsPens } from '@topometa2d/topo-charts';
// import { iotCanvasPens, iotPens } from '@topometa2d-components/iot';
import axios from '@/http';
import { getIconFoldersByIF } from './icons';
import { getPngFolders } from './pngs';
import { addIcons } from './utils';
import { customEle } from '@/diagram/canvasIndex';

declare const meta2d: Meta2d;
declare const window: Window;

// 注册核心库带的图形
export function registerNormalShape() {
  register(flowPens());
  registerAnchors(flowAnchors());
  register(activityDiagram());
  registerCanvasDraw(activityDiagramByCtx());
  register(classPens());
  register(sequencePens());
  registerCanvasDraw(sequencePensbyCtx());
  registerEcharts();
  registerHighcharts();
  registerLightningChart();
  registerCanvasDraw(formPens());
  // register(iotPens());
  // registerCanvasDraw(iotCanvasPens());
  registerCanvasDraw(chartsPens());
  register(ftaPens());
  registerCanvasDraw(ftaPensbyCtx());
  registerAnchors(ftaAnchors());
  //注册自定义的CanvasDraw
  registerCanvasDraw(customEle());
}

async function getClasses() {
  let classes: any = await axios.get('/api/cms', {
    params: { types: 'classes' },
  });
  if (classes && classes[0] && classes[0].data) {
    classes = classes[0].data.list;
    meta2d.emit('t-classes', classes); // 画布属性面板需要拿到这个数据
  } else if (!classes || typeof classes[0] === 'string') {
    return [];
  }
  return classes;
}

// async function getClassesCount() {
//   const classesCount: any[] = await axios.get('/api/tools/count');
//   return classesCount;
// }

// async function getMeterials() {
//   const meterials: any[] = await axios.get('/api/tools', {
//     params: { min: 1 }
//   });
//   return meterials;
// }

async function getImages() {
  // return [];
  const images: any[] = await axios.get('/images');
  return images;
}

async function getIcons() {
  let icons;
  if ((window as any).meta2dIcons) {
    icons = await Promise.all(
      (window as any).meta2dIcons.map((url) => addIcons(url))
    );
  } else {
    const iconUrls = [
      // '//at.alicdn.com/t/font_2395018_nng9x1qhat.css', // 国家电网
      // '//at.alicdn.com/t/font_2073009_95h923royco.css', // 电气工程常用
      'icon/国家电网/iconfont.css',
      'icon/电气工程/iconfont.css',
    ] as const;
    icons = await Promise.all(
      iconUrls.map((url) => addIcons(url.replace('.css', '.json')))
    );
  }
  return icons;
}

// 注册数据库中的图形，并返回数据
export async function registerOtherShape(onlyRegister = false) {
  const [
    // classes,
    // images,
    icons,
    { defalutMaterials },
    svgs,
    pngs,
    jsDiagrams,
  ] = await Promise.all([
    // getClasses(),
    // !onlyRegister && getImages(),
    !onlyRegister && getIcons(),
    import('./defaults'),
    !onlyRegister && getIconFoldersByIF(),
    !onlyRegister && getPngFolders(),
    !onlyRegister && getNetJsDiagram(),
  ]);

  // 默认已有的图形文件夹
  const normalHasFolder = defalutMaterials.map((mat) => mat.name);
  normalHasFolder.push('工业物联网');
  const groups: any = {};
  // for (const item of classes) {
  //   if (!item.children) {
  //     continue;
  //   }
  //   for (const c of item.children) {
  //     if (!groups[c.name] && !normalHasFolder.includes(c.name)) {
  //       groups[c.name] = {
  //         id: c.id,
  //         name: c.name,
  //         show: true,
  //         list: []
  //       };
  //     }
  //   }
  // }

  const result: any[] = [];
  for (const key in groups) {
    result.push(groups[key]);
  }

  if (!onlyRegister) {
    // console.log('js', jsDiagrams);
    result.push(...jsDiagrams);
    // 只注册无需设置字体图标和 图片资源
    result.push(...svgs);

    // pngs
    result.push(...pngs);

    result.push(...icons);

    // Array.isArray(images) &&
    //   result.push(
    //     ...images.map((item) => {
    //       return {
    //         name: item.name, // 目录名
    //         list: item.list?.map((ele: any) => {
    //           return {
    //             name: ele.name, // hover item 名
    //             image: ele.url, // 预览图以及拖出来的效果图
    //           };
    //         }),
    //         show: true,
    //       };
    //     })
    //   );
  }

  // (window as any).meta2dToolId = '';
  // const lists = await Promise.all(
  //   result.map(item => openMaterial(item.name, item))
  // );

  // result.forEach((item, index) => {
  //   item.list = lists[index];
  // });

  if ((window as any).myCharts) {
    for (const item of (window as any).myCharts) {
      for (const chart of item.list) {
        if (!chart.penFn) {
          continue;
        }

        const pen = {};
        pen[chart.name] = chart.penFn;
        register(pen);

        if (chart.anchorsFn) {
          const anchors = {};
          anchors[chart.name] = chart.anchorsFn;
          registerAnchors(anchors);
        }

        chart.data.name = chart.name;
      }
    }

    result.push(...(window as any).myCharts);
  }

  return result;
}

async function getNetJsDiagram() {
  const market = import.meta.env.VITE_MARKET;
  if (market) {
    return [];
  }
  let arr: any[] = [
    { name: '箭头', id: 'f955205' },
    { name: '拓扑图未分类', id: '6cd3b234' },
    { name: '云', id: '406687' },
    { name: '网络设备', id: '3f264892' },
    { name: '电子产品', id: '768107e4' },
    { name: '楼宇', id: 'c109d4d' },
    { name: '物联网未分类', id: '33253fe' },
    { name: '逻辑门电路', id: '334c1c88' },
    { name: '电阻', id: '597dab3' },
    { name: '电容', id: '2181f192' },
    { name: '电感', id: '1dba554' },
    { name: '开关、转换器', id: '6b39d7ca' },
    { name: '二极管', id: '3180efea' },
    { name: '信号源', id: '6bd86063' },
    { name: '晶体管', id: '017c4d4' },
    { name: '仪表', id: '4e0df5d5' },
    { name: '电子显示设备', id: '68559dd4' },
    { name: '真空电子管', id: '181c247d' },
    { name: '电子波形', id: '42daf6' },
    { name: '转动与机械设备', id: '2a5e218e' },
    { name: '电子逻辑计算器', id: 'f9beb15' },
    { name: '电子传输/传播', id: '37f12dd3' },
    { name: '电子未分类', id: '54aa6d1' },
    { name: '液压符号', id: 'b03ed4e' },
    { name: '工具', id: '108095a' },
  ];
  const lists = await Promise.all(
    arr.map((item) => openNewMaterial(item.name, item))
  );
  arr.forEach((item, index) => {
    item.list = lists[index];
    item.show = true;
  });
  return arr;
}

async function openNewMaterial(subclass: string, group) {
  const [{ GetDBMaterials, SetDBMaterials }] = await Promise.all([
    import('../services/material'),
  ]);
  let materials: any = await GetDBMaterials(subclass);
  if (!materials || !materials.length) {
    materials = await axios.get('/tools', {
      params: {
        subClass: (group as any).id,
        pageCount: 1000,
      },
    });
    if (materials) {
      SetDBMaterials(group.name, materials);
    }
  }

  const newList: any[] = [];
  materials.forEach((material: any) => {
    if (!material.fullname) {
      // fullname 不存在，采用其他方式
      newList.push(material);
    } else {
      newList.push({
        id: material.id,
        name: material.name,
        fullname: material.fullname,
        data: material.data,
        svg: material.svg,
      });
    }
  });
  // console.log(materials, newList);

  (window as any).meta2dTools = materials;
  (window as any).registerToolsNew();

  (window as any).meta2dTools = undefined;

  return newList;
}

async function openMaterial(subclass: string, group) {
  if ((group.list && group.list.length) || !group.id) {
    return group.list;
  }

  const [{ GetDBMaterials, SetDBMaterials }] = await Promise.all([
    import('../services/material'),
  ]);
  let materials: any = await GetDBMaterials(subclass);
  if (!materials || !materials.length) {
    materials = await axios.get('/api/tools', {
      params: {
        subClass: (group as any).id,
        pageCount: 1000,
      },
    });
    if (materials) {
      SetDBMaterials(group.name, materials);
    }
  }

  const newList: any[] = [];
  materials.forEach((material: any) => {
    if (!material.fullname) {
      // fullname 不存在，采用其他方式
      newList.push(material);
    } else {
      newList.push({
        id: material.id,
        name: material.name,
        fullname: material.fullname,
        data: material.data,
        svg: material.svg,
      });
    }
  });

  (window as any).meta2dTools = materials;
  (window as any).registerToolsNew();

  (window as any).meta2dTools = undefined;

  return newList;
}

/**
 * 外层的 rect 的 width height 抽取到内层
 */
function rectToOuter(data) {
  const rect = data.rect;
  for (const k in rect) {
    if (Object.prototype.hasOwnProperty.call(rect, k)) {
      data[k] = rect[k];
    }
  }
  delete data.rect;
}
