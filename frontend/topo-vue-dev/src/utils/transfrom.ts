import { lineDashObj, normalAnimate } from '@/components/defaults';
import { LineAnimateType } from '@topometa2d/core';

/*
 * @Description:
 * @Author: G
 * @Date: 2021-10-18 17:17:37
 * @LastEditTime: 2021-10-27 10:48:48
 */
export function transAnchorId(oldId: string | number) {
  let id = Number(oldId);
  if (id === 0) {
    return 3;
  }
  return id - 1;
}

export function transLineDash(oldDash: number) {
  return lineDashObj[oldDash];
}

// oldType: '', 'beads', 'dot', 'comet'
export function transLineAnimateType(oldType: string) {
  switch (oldType) {
    case 'beads':
      return LineAnimateType.Beads;
    case 'dot':
      return LineAnimateType.Dot;
    case 'comet': // TODO: 彗星在新版本中没有了，转换成默认动画
      return LineAnimateType.Normal;
    default:
      return LineAnimateType.Normal;
  }
}

export function transNodeAnimateFrame(node: any) {
  return normalAnimate[node.animateType] || [];
}

export function transAutoPlay(el: any) {
  if (el.hasOwnProperty('animatePlay')) {
    return el.animatePlay;
  } else {
    return el.playLoop;
  }
}
