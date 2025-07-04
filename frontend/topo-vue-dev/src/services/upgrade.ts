import { Meta2dBackData, isGif } from '@/components/utils';
import {
  Event,
  EventAction,
  EventName,
  Meta2d,
  Pen,
  calcRelativePoint,
  isEqual,
  s8,
} from '@topometa2d/core';
import {
  transAnchorId,
  transAutoPlay,
  transLineAnimateType,
  transLineDash,
  transNodeAnimateFrame,
} from '../utils/transfrom';
type Point = any;
type Rect = any;

export const baseVer = '1.0.0';
declare const meta2d: Meta2d;

enum LineSType {
  Line = 'line',
  Polyline = 'polyline',
  Curve = 'curve',
  Mind = 'mind',
}
/**
 * 旧版本到新版本的格式转换函数
 * 该方法存在定时器，随后执行的 meta2d.open 必须是同步代码
 */
export function upgrade(data: any, baseVer: string): Meta2dBackData {
  const newData: Meta2dBackData = {
    x: data.x ?? 0,
    y: data.y ?? 0,
    version: baseVer,
    scale: data.scale,
    name: data.name,
    background: data.bkColor,
    grid: data.grid,
    gridColor: data.gridColor,
    gridSize: data.gridSize,
    locked: data.locked,
    rule: data.rule,
    ruleColor: data.ruleColor,
    pens: convertPen(data.pens, data.scale),
    folder: data.folder,
    component: data.component,
    class: data.class,
    id: data.id,
    websocket: data.websocket,
    mqtt: data.mqttUrl,
    mqttOptions: data.mqttOptions || {},
    mqttTopics: data.mqttTopics,
    shared: data.shared,
    initJs: data.initJS,
    origin: { x: 0, y: 0 },
    center: { x: 0, y: 0 },
    userId: data.owner.id,
    image: data.image,
  };
  return newData;
}
// pen格式转换
export function convertPen(
  pens: any[],
  scale = 1,
  parentEl?: any,
  options = [],
  level = 0
) {
  pens?.forEach((el, i) => {
    const obj: Pen = {};
    obj.id = el.id;
    obj.tags = el.tags;
    if (!el.type && el.name === 'line') {
      const { y, height } = el.rect;
      el.rect.y = y + height / 2;
      el.rect.height = 0;
    }
    if (el.type === 1 && !parentEl) {
      // 元素为线，并且不属于组合图形
      if (!setLineData(el, obj, scale)) {
        // 本次数据丢弃，脏数据
        return false;
      }
    } else {
      if (!setNodeData(el, obj, parentEl, scale)) {
        return false;
      }
      if (obj.name === 'combine') {
        convertPen(el.children, scale, el, options, level++);
      }
    }
    obj.paddingLeft = el.paddingLeft;
    obj.paddingRight = el.paddingRight;
    obj.paddingTop = el.paddingTop;
    obj.paddingBottom = el.paddingBottom;
    obj.color = el.strokeStyle;
    obj.lineWidth = el.lineWidth / scale;
    obj.lineCap = el.lineCap;
    obj.lineDash = transLineDash(el.dash);
    obj.borderColor = el.borderColor;
    obj.borderWidth = el.borderWidth / scale;
    obj.rotate = el.rotate;
    obj.visible = el.visible;
    obj.title = el.title || el.markdown;
    obj.background = el.fillStyle;
    obj.lineHeight = el.lineHeight;

    if (el.font) {
      // 老版本存在 font ，把 font 抽出来
      Object.assign(el, { ...el.font });
    }

    obj.fontSize = el.fontSize / scale;
    obj.fontFamily = el.fontFamily;
    obj.fontStyle = el.fontStyle;
    obj.fontWeight = el.fontWeight;

    obj.text = el.text;
    obj.textColor = el.fontColor;
    obj.textAlign = el.textAlign;
    obj.textBaseline = el.textBaseline;
    obj.textBackground = el.textBackground;
    obj.whiteSpace = el.whiteSpace;

    obj.animateSpan = el.animateSpan;
    obj.animateColor = el.animateColor;
    obj.animateCycle = el.animateCycle;
    obj.animateReverse = el.animateReverse;
    obj.nextAnimate = el.nextAnimate;
    obj.lineAnimateType = transLineAnimateType(el.animateType);
    obj.animateDotSize = el.animateDotSize;
    obj.animateLineDash = el.animateLineDash;
    (obj as any).animateType = el.animateType;
    obj.frames = transNodeAnimateFrame(el);
    obj.autoPlay = transAutoPlay(el);
    obj.playLoop = el.playLoop;

    obj.globalAlpha = el.globalAlpha;
    obj.bkType = el.bkType;
    obj.gradientFromColor = el.gradientFromColor;
    obj.gradientToColor = el.gradientToColor;
    obj.gradientAngle = el.gradientAngle;
    obj.gradientRadius = el.gradientRadius;
    obj.borderRadius = el.borderRadius;

    obj.events = convertEvents(el.events || [], el.wheres || []);
    options.push(obj);
  });
  return options;
}

function setNodeData(el: any, obj: any, parentEl?: any, scale = 1): boolean {
  if (parentEl) {
    obj.parentId = parentEl.id;
    obj.locked = 2;
  }
  if (isLine(el.name) || isPen(el.name)) {
    if (!setLineData(el, obj, scale)) {
      return false;
    }
    obj.type = 0;
    obj.close = el.closePath;
  } else {
    if (el.name === 'combine') {
      obj.name = 'combine';
      obj.children = el.children.map((child) => child.id);
    } else {
      obj.type = 0;
      obj.name = el.name;
      if (el.icon) {
        obj.icon = el.icon;
        obj.iconColor = el.iconColor;
        obj.iconFamily = el.iconFamily;
        obj.iconSize = el.iconSize / scale;
      }
      // 处理图片节点
      if (el.image) {
        obj.image = el.image;
        obj.imageRatio = el.imageRatio;
        obj.iconAlign = el.imageAlign;
        obj.iconWidth = el.imageWidth / scale;
        obj.iconHeight = el.imageHeight / scale;
        isGif(el.image) && (obj.name = 'gif');
      }
      //  处理echarts节点
      if (el.name === 'echarts') {
        obj.echarts = el.data.echarts;
      }
      // 处理iframe节点
      if (el.iframe) {
        obj.name = 'iframe';
        obj.iframe = el.iframe;
      }
      // 处理视频节点
      if (el.video) {
        obj.name = 'video';
        obj.video = el.video;
      }
      // 处理音频节点
      if (el.audio) {
        obj.name = 'video';
        obj.audio = el.audio;
      }
    }
  }
  let rect = { ...el.rect };
  if (parentEl) {
    if (obj.name === 'line') {
      rect = calcRelativeRect(obj, parentEl.rect);
    } else {
      rect = calcRelativeRect(el.rect, parentEl.rect);
    }
  }

  obj.x = rect.x;
  obj.y = rect.y;
  if (isEqual(rect.width, 1)) {
    // 数据库中存在 1.0000000000000004 的值，认为是 1
    obj.width = 1;
  } else {
    obj.width = rect.width;
  }
  obj.height = rect.height;
  return true;
}

function setLineData(el: any, obj: any, scale = 1): boolean {
  obj.name = 'line';
  obj.type = 1;
  obj.fromArrow = el.fromArrow;
  // TODO: 老版本转换新版本，箭头大小不设置，采用默认，有问题用户自行设置
  // obj.fromArrowSize = el.fromArrowSize;
  obj.toArrow = el.toArrow;
  // obj.toArrowSize = el.toArrowSize;
  let calcAnchors = [];
  if (isPolyline(el.name)) {
    calcAnchors = getPolylineCalcAnchors(el);
  } else if (isCurveline(el.name)) {
    calcAnchors = getCurvelineCalcAnchors(el);
  } else if (isPen(el.name)) {
    calcAnchors = getPenCalcAnchors(el);
  }
  if (!calcAnchors.length) {
    // 长度为空，说明是脏数据，不转换
    return false;
  }
  const rect = getLineRect(el, calcAnchors);
  calcCenter(rect);
  const anchors = [];
  calcAnchors.forEach((pt) => {
    anchors.push(calcRelativePoint(pt, rect));
  });
  obj.x = rect.x;
  obj.y = rect.y;
  obj.width = rect.width;
  obj.height = rect.height;
  obj.anchors = anchors;
  if (el.textRect && anchors.length > 3) {
    let textRatioX =
      rect.width <= 1
        ? 0
        : ((el.fontSize / scale) * el.text.length) / rect.width;
    let textRatioY =
      rect.height <= 1
        ? 0
        : ((el.fontSize / scale) * el.lineHeight) / rect.height;
    //老版本文字默认在最后两个控制点中心
    let end = anchors[anchors.length - 2];
    let start = anchors[anchors.length - 3];
    obj.textLeft = (start.x + end.x - textRatioX) / 2;
    if (obj.textLeft === 1) {
      obj.textLeft = 0.99999;
    }
    obj.textTop = (start.y + end.y - textRatioY) / 2;
    if (obj.textTop === 1) {
      obj.textTop = 0.99999;
    }
    if (rect.width <= 1) {
      obj.textLeft = ((-el.fontSize / scale / scale) * el.text.length) / 2;
    }
    if (rect.height <= 1) {
      obj.textTop = ((-el.fontSize / scale / scale) * el.lineHeight) / 2;
    }
    obj.textWidth = rect.width;
    el.textAlign = 'left';
    el.textBaseline = 'top';
  }
  setControledLines(el, calcAnchors);
  return true;
}

function getPolylineCalcAnchors(el: any) {
  const { from, to } = getLineFromTo(el);
  const calcAnchors = [
    {
      id: s8(),
      connectTo: from.id,
      penId: el.id,
      x: from.x,
      y: from.y,
      anchorId: String(from.anchorIndex),
    },
    ...(el.controlPoints || []).map((p) => ({
      id: s8(),
      connectTo: undefined,
      penId: el.id,
      x: p.x,
      y: p.y,
    })),
    {
      id: s8(),
      connectTo: to.id,
      penId: el.id,
      x: to.x,
      y: to.y,
      anchorId: String(to.anchorIndex),
    },
  ];
  return calcAnchors;
}

function getCurvelineCalcAnchors(el: any) {
  const [nextPoint, prevPoint] = el.controlPoints;
  const { from, to } = getLineFromTo(el);
  const calcAnchors = [
    {
      id: s8(),
      connectTo: from.id,
      penId: el.id,
      x: from.x,
      y: from.y,
      next: {
        connectTo: from.id,
        penId: el.id,
        x: nextPoint.x,
        y: nextPoint.y,
      },
      prev: {
        connectTo: from.id,
        penId: el.id,
        x: 2 * from.x - nextPoint.x,
        y: 2 * from.y - nextPoint.y,
      },
    },
    {
      id: s8(),
      connectTo: to.id,
      penId: el.id,
      x: to.x,
      y: to.y,
      prev: {
        connectTo: to.id,
        penId: el.id,
        x: prevPoint.x,
        y: prevPoint.y,
      },
      next: {
        connectTo: to.id,
        penId: el.id,
        x: 2 * to.x - prevPoint.x,
        y: 2 * to.y - prevPoint.y,
      },
    },
  ];
  return calcAnchors;
}

function getPenCalcAnchors(el: any) {
  if (el.children && el.children.length > 0) {
    return el.children.reduce((arr, child, i, children) => {
      if (i === 0) {
        arr.push({
          connectTo: undefined,
          penId: el.id,
          x: child.from.x,
          y: child.from.y,
        });
      }
      arr.push({
        connectTo: undefined,
        penId: el.id,
        x: child.to.x,
        y: child.to.y,
      });
      return arr;
    }, []);
  }
  if (el.points && el.points.length > 0) {
    return el.points.reduce((arr, point, i, points) => {
      let flag = false;
      if (i > 0) {
        const prev = points[i - 1];
        if (point.x !== prev.x && point.y !== prev.y) {
          flag = true;
        }
      } else {
        flag = true;
      }
      if (flag) {
        arr.push({
          connectTo: undefined,
          penId: el.id,
          x: point.x,
          y: point.y,
        });
      }
      return arr;
    }, []);
  }
  return [];
}

function getLineRect(pen: Pen, calcAnchors: any) {
  getLineLength(pen, calcAnchors);
  return getRectOfPoints(getLinePoints(pen, calcAnchors));
}

function getLineLength(pen: Pen, calcAnchors: any): number {
  if (calcAnchors.length < 2) {
    return 0;
  }

  let len = 0;
  let from: Point;
  calcAnchors.forEach((pt: Point) => {
    if (from) {
      from.lineLength = lineLen(from, from.next, pt.prev, pt);
      len += from.lineLength;
    }
    from = pt;
  });
  if (pen.close) {
    let to = calcAnchors[0];
    from.lineLength = lineLen(from, from.next, to.prev, to);
    len += from.lineLength;
  }
  pen.length = len;
  return len;
}

function getLinePoints(pen: Pen, calcAnchors: any) {
  const pts: Point[] = [];
  let from: Point;
  calcAnchors.forEach((pt: Point) => {
    pts.push(pt);
    from && pts.push(...getPoints(from, pt, pen));
    from = pt;
  });
  if (pen.close && calcAnchors.length > 1) {
    pts.push(...getPoints(from, calcAnchors[0], pen));
  }
  return pts;
}

function getRectOfPoints(points: Point[]): Rect {
  let x = Infinity;
  let y = Infinity;
  let ex = -Infinity;
  let ey = -Infinity;

  points.forEach((item) => {
    x = Math.min(x, item.x);
    y = Math.min(y, item.y);
    ex = Math.max(ex, item.x);
    ey = Math.max(ey, item.y);
  });
  return { x, y, ex, ey, width: ex - x || 1, height: ey - y || 1 };
}

function getPoints(from: Point, to: Point, pen?: Pen) {
  const pts: Point[] = [];
  if (!to) {
    return pts;
  }

  let step = 0.02;
  if (from.lineLength) {
    let r = 4;
    if (pen && pen.lineWidth) {
      r += pen.lineWidth / 2;
    }
    step = r / from.lineLength;
  }
  if (from.next) {
    if (to.prev) {
      for (let i = step; i < 1; i += step) {
        pts.push(getBezierPoint(i, from, from.next, to.prev, to));
      }
    } else {
      for (let i = step; i < 1; i += step) {
        pts.push(getQuadraticPoint(i, from, from.next, to));
      }
    }
  } else {
    if (to.prev) {
      for (let i = step; i < 1; i += step) {
        pts.push(getQuadraticPoint(i, from, to.prev, to));
      }
    } else {
      pts.push({ x: to.x, y: to.y });
    }
  }
  if (pts.length > 1) {
    from.curvePoints = pts;
  }

  return pts;
}

function getBezierPoint(
  step: number,
  from: Point,
  cp1: Point,
  cp2: Point,
  to: Point
) {
  const { x: x1, y: y1 } = from;
  const { x: x2, y: y2 } = to;
  const { x: cx1, y: cy1 } = cp1;
  const { x: cx2, y: cy2 } = cp2;

  const pos = 1 - step;
  const x =
    x1 * pos * pos * pos +
    3 * cx1 * step * pos * pos +
    3 * cx2 * step * step * pos +
    x2 * step * step * step;
  const y =
    y1 * pos * pos * pos +
    3 * cy1 * step * pos * pos +
    3 * cy2 * step * step * pos +
    y2 * step * step * step;
  return { x, y, step };
}

function getQuadraticPoint(step: number, from: Point, cp: Point, to: Point) {
  const pos = 1 - step;
  const x = pos * pos * from.x + 2 * pos * step * cp.x + step * step * to.x;
  const y = pos * pos * from.y + 2 * pos * step * cp.y + step * step * to.y;
  return { x, y, step };
}

function lineLen(from: Point, cp1?: Point, cp2?: Point, to?: Point): number {
  if (!cp1 && !cp2) {
    return (
      Math.sqrt(
        Math.pow(Math.abs(from.x - to.x), 2) +
          Math.pow(Math.abs(from.y - to.y), 2)
      ) || 0
    );
  }

  const path = document.createElementNS('http://www.w3.org/2000/svg', 'path');
  if (cp1 && cp2) {
    path.setAttribute(
      'd',
      `M${from.x} ${from.y} C${cp1.x} ${cp1.y} ${cp2.x} ${cp2.y} ${to.x} ${to.y}`
    );
  } else if (cp1) {
    path.setAttribute(
      'd',
      `M${from.x} ${from.y} Q${cp1.x} ${cp1.y} ${to.x} ${to.y}`
    );
  } else {
    path.setAttribute(
      'd',
      `M${from.x} ${from.y} Q${cp2.x} ${cp2.y} ${to.x} ${to.y}`
    );
  }
  return path.getTotalLength() || 0;
}

function calcRelativeRect(rect: Rect, worldRect: Rect) {
  const relRect: Rect = {
    x: (rect.x - worldRect.x) / worldRect.width || 0,
    y: (rect.y - worldRect.y) / worldRect.height || 0,
    width: rect.width / worldRect.width || 0,
    height: rect.height / worldRect.height || 0,
  };
  relRect.ex = relRect.x + relRect.width;
  relRect.ey = relRect.y + relRect.height;

  return relRect;
}

function calcCenter(rect: Rect) {
  if (!rect.center) {
    rect.center = {} as Point;
  }
  rect.center.x = rect.x + rect.width / 2;
  rect.center.y = rect.y + rect.height / 2;
}

// 锚点无需转换的 画笔类型
const notTransformPens = ['triangle', 'mindNode', 'mindLine'];

function setControledLines(el: any, calcAnchors: any) {
  setTimeout(() => {
    // 等节点被添加到数据中后，将节点与连线关联
    const [fromNode] = meta2d.find((el.from && el.from.id) || '');
    const [toNode] = meta2d.find((el.from && el.to.id) || '');
    if (fromNode && fromNode.type === 0) {
      if (!fromNode.connectedLines) {
        fromNode.connectedLines = [];
      }
      fromNode.connectedLines.push({
        anchor: `${
          !notTransformPens.includes(fromNode.name)
            ? transAnchorId(el.from.anchorIndex)
            : el.from.anchorIndex
        }`,
        lineAnchor: calcAnchors[0].id,
        lineId: el.id,
      });
    }
    if (toNode && toNode.type === 0) {
      if (!toNode.connectedLines) {
        toNode.connectedLines = [];
      }
      toNode.connectedLines.push({
        anchor: `${
          !notTransformPens.includes(toNode.name)
            ? transAnchorId(el.to.anchorIndex)
            : el.to.anchorIndex
        }`,
        lineAnchor: calcAnchors[1].id,
        lineId: el.id,
      });
    }
  }, 0);
}

function isLine(name: string) {
  return (
    {
      [LineSType.Curve]: true,
      [LineSType.Mind]: true,
      [LineSType.Line]: true,
      [LineSType.Polyline]: true,
    }[name] || false
  );
}

function isPolyline(name: string) {
  return (
    {
      [LineSType.Line]: true,
      [LineSType.Polyline]: true,
    }[name] || false
  );
}

function isCurveline(name: string) {
  return (
    {
      [LineSType.Curve]: true,
      [LineSType.Mind]: true,
    }[name] || false
  );
}

function isPen(name: string) {
  return name === 'lines' || name === 'graffiti';
}

// 版本比较函数
export function compareVersion(version1: string, version2: string) {
  const arr1 = version1.split('.');
  const arr2 = version2.split('.');
  const length1 = arr1.length;
  const length2 = arr2.length;
  const minlength = Math.min(length1, length2);
  let i = 0;
  for (i; i < minlength; i++) {
    let a = parseInt(arr1[i]);
    let b = parseInt(arr2[i]);
    if (a > b) {
      return 1;
    } else if (a < b) {
      return -1;
    }
  }
  if (length1 > length2) {
    for (let j = i; j < length1; j++) {
      if (parseInt(arr1[j]) != 0) {
        return 1;
      }
    }
    return 0;
  } else if (length1 < length2) {
    for (let j = i; j < length2; j++) {
      if (parseInt(arr2[j]) != 0) {
        return -1;
      }
    }
    return 0;
  }
  return 0;
}

function getLineFromTo(el: any) {
  const rect = el.rect;
  const from = el.from || {
    x: rect.x,
    y: rect.y + rect.height / 2,
  };
  const to = el.to || {
    x: rect.x + rect.width,
    y: rect.y + rect.height / 2,
  };
  return { from, to };
}

function convertEvents(events: any[], wheres: any[]): Event[] {
  const newEvents = [];
  const newEventType: EventName[] = [
    'mousedown', // 老版本 click 对应新版本的 mousedown
    'dblclick',
    'valueUpdate',
    'valueUpdate',
    'enter',
    'leave',
    'click', // 老版本 mouseUp 对应新版本 click
  ];
  const newEventAction = [
    EventAction.Link,
    EventAction.StartAnimate,
    EventAction.JS,
    EventAction.GlobalFn,
    undefined,
    EventAction.PauseAnimate,
    EventAction.StopAnimate,
    EventAction.Emit,
  ];

  for (const event of events) {
    const newEvent: any = {
      name: newEventType[event.type],
      action: newEventAction[event.action],
      value: event.value,
      params: event.params,
      // TODO: 老版本 name 没有意义
    };

    newEvents.push(newEvent);
  }

  const whereActions = {
    link: EventAction.Link,
    StartAnimate: EventAction.StartAnimate,
    PauseAnimate: EventAction.PauseAnimate,
    StopAnimate: EventAction.StopAnimate,
    Function: EventAction.JS,
    GlobalFn: EventAction.GlobalFn,
    Emit: EventAction.Emit,
  };

  for (const where of wheres) {
    // 一个 where 可以有多个事件
    for (const action of where.actions) {
      const newEvent: any = {
        name: 'valueUpdate',
        action: whereActions[action.do],
        where: {
          key: where.key,
          comparison: where.comparison,
          value: where.value,
          fnJs: where.fn,
        },
      };
      switch (newEvent.action) {
        case EventAction.Link:
          newEvent.value = action.url;
          newEvent.params = action._blank;
          break;
        case EventAction.StartAnimate:
        case EventAction.PauseAnimate:
        case EventAction.StopAnimate:
          newEvent.value = action.tag;
          break;
        case EventAction.JS:
        case EventAction.GlobalFn:
        case EventAction.Emit:
          newEvent.value = action.fn;
          newEvent.params = action.params;
          break;
      }

      newEvents.push(newEvent);
    }
  }
  return newEvents;
}
