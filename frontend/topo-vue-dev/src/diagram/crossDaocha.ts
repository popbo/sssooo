import { Meta2d } from '@topometa2d/core';
declare const meta2d: Meta2d;
const greenColor = '#00ff00'; //绿色
const grayColor = '#6B696B'; //灰色
const yellowColor = '#ffff00'; //黄色
const redColor = '#ff0000'; //红色
const whiteColor = '#ffffff';
const testColor = 'rgba(0,0 ,0 ,0)';
export function crossDaocha(ctx: CanvasRenderingContext2D, pen: any) {
  let x = pen.calculative.worldRect.x;
  let y = pen.calculative.worldRect.y;
  let w = pen.calculative.worldRect.width;
  let h = pen.calculative.worldRect.height;
  let r = w / 20; //圆半径
  let lineWidth = h /24.5; //线条宽度
  let textValue = pen.status + '';
  const newR=w/35  //调小圆的直径
  const fillColor = pen.calculative.color ? pen.calculative.color : '#7B7D7B';
  // 初始化动画配置
  pen.frames = [];
  pen.animateCycle = 0;
  pen.animateType = '';
  pen.autoPlay = false;
  const rxLeft=x +w/2-h/2 //左圆心x
  const rxRight=x+w/2+h/2 //右边圆心x
  const ryDown=y+h //下边圆心y
  // 解构取值
  const {
    Dev_name='',
    Normal, //正常
    Reverse, //正常反位
    Blocked, // 单锁0/1是否存在2或者3这样的数值让其不激活
    Overlap, //进路锁闭
    CBI_occupied, //占用
    Lost, //失表
    State, // 连锁中断
  } = pen.statusObj ?? {};
  const {
    CBTC_occupied,
    Invalid,
    Locked,
    CBI_occupied:qdCBI_occupied,
    Overlap:qdOverlap,
    State:qdState
  } = pen.statusObj2 ?? {};
  const {
    Dev_name:DC_UR_Dev_name='',
    Normal:DC_UR_Normal, //正常
    Reverse:DC_UR_Reverse, //正常反位
    Blocked:DC_UR_Blocked, // 单锁0/1是否存在2或者3这样的数值让其不激活
    Overlap:DC_UR_Overlap, //进路锁闭
    CBI_occupied:DC_UR_CBI_occupied, //占用
    Lost:DC_UR_Lost, //失表
    State:DC_UR_State, // 连锁中断
  } = pen.statusObjUR ?? {};
  const {
    CBTC_occupied:QD_UR_CBTC_occupied,
    Invalid:QD_UR_CBTC_Invalid,
    Locked:QD_UR_Locked,
    CBI_occupied:QD_UR_CBI_occupied,
    Overlap:QD_UR_Overlap,
    State:QD_UR_State
  } = pen.statusObj2UR ?? {};

  const {
    Dev_name:DC_DL_Dev_name='',
    Normal:DC_DL_Normal, //正常
    Reverse:DC_DL_Reverse, //正常反位
    Blocked:DC_DL_Blocked, // 单锁0/1是否存在2或者3这样的数值让其不激活
    Overlap:DC_DL_Overlap, //进路锁闭
    CBI_occupied:DC_DL_CBI_occupied, //占用
    Lost:DC_DL_Lost, //失表
    State:DC_DL_State, // 连锁中断
  } = pen.statusObjDL ?? {};
  const {
    CBTC_occupied:QD_DL_CBTC_occupied,
    Invalid:QD_DL_CBTC_Invalid,
    Locked:QD_DL_Locked,
    CBI_occupied:QD_DL_CBI_occupied,
    Overlap:QD_DL_Overlap,
    State:QD_DL_State
  } = pen.statusObj2DL ?? {};

  const {
    Dev_name:DC_DR_Dev_name='',
    Normal:DC_DR_Normal, //正常
    Reverse:DC_DR_Reverse, //正常反位
    Blocked:DC_DR_Blocked, // 单锁0/1是否存在2或者3这样的数值让其不激活
    Overlap:DC_DR_Overlap, //进路锁闭
    CBI_occupied:DC_DR_CBI_occupied, //占用
    Lost:DC_DR_Lost, //失表
    State:DC_DR_State, // 连锁中断
  } = pen.statusObjDR ?? {};
  const {
    CBTC_occupied:QD_DR_CBTC_occupied,
    Invalid:QD_DR_CBTC_Invalid,
    Locked:QD_DR_Locked,
    CBI_occupied:QD_DR_CBI_occupied,
    Overlap:QD_DR_Overlap,
    State:QD_DR_State
  } = pen.statusObj2DR ?? {};
  // const qdCBI_occupied=pen.statusObj2?.CBI_occupied ?? {}
  // const qdOverlap=pen.statusObj2?.Overlap ?? {}
  // const qdState=pen.statusObj2?.State ?? {}
  // 显示
  ctx.save();
  defaultStatus()
//左上
if(State === '1'){
  if (Normal === '1') {
    if (Blocked === '0' && Overlap === '0' && CBI_occupied === '0') {
      //1 正常-定位
      normal(greenColor,rxLeft,y + lineWidth/2);
      textValue = '1'; //赋值操作
    }
    if (Blocked === '1') {
      // 单锁-定位
      blocked(greenColor,rxLeft,y + lineWidth/2);
      textValue = '3';
    }
    if (Overlap === '1'||CBI_occupied === '1') {
      //  进路锁闭-定位
      overlap(greenColor,rxLeft);
      textValue = '5';
    }
  } else if (Reverse === '1') {
    if (Blocked === '0' && Overlap === '0' && CBI_occupied === '0') {
      // 正常 - 反位;
      reverse(yellowColor);
      textValue = '2';
    }
    if (Blocked === '1') {
      // 单锁- 反位
      blockedReverse(yellowColor);
      textValue = '4';
    }
    if (Overlap === '1'|| CBI_occupied === '1') {
      //  进路锁闭-反位
      overlapReverse(yellowColor);
      textValue = '6';
    }
  } else if (Lost === '1') {
    //失表
    pen.frames = [
      {
        background: redColor,
        bktype: 0,
        duration: 1000,
        visible: true,
      },
      {
        background: testColor,
        bktype: 0,
        duration: 1000,
        visible: true,
      },
    ];
    pen.animateCycle = Infinity;
    pen.animateType = 'custom';
    pen.autoPlay = true;
    lossWatch(pen?.background || redColor);
    textValue = '9';
  }
} else if (State !== undefined) {
    // 联锁中断
    interlockInterrupt(grayColor);
    textValue = '10';
}
  //右上
  if(DC_UR_State === '1'){
    if (DC_UR_Normal === '1') {
      if (DC_UR_Blocked === '0' && DC_UR_Overlap === '0' && DC_UR_CBI_occupied === '0') {
        //1 正常-定位
        normal(greenColor,rxRight,y + lineWidth/2);
        textValue = '1'; //赋值操作
      }
      if (DC_UR_Blocked === '1') {
        // 单锁-定位
        blocked(greenColor,rxRight,y+ lineWidth/2);
        textValue = '3';
      }
      if (DC_UR_Overlap === '1'||DC_UR_CBI_occupied === '1') {
        //  进路锁闭-定位
        overlap(greenColor,rxRight);
        textValue = '5';
      }
    } else if (DC_UR_Reverse === '1') {
      if (DC_UR_Blocked === '0' && DC_UR_Overlap === '0' && DC_UR_CBI_occupied === '0') {
        // 正常 - 反位;
        reverse2(yellowColor);
        textValue = '2';
      }
      if (DC_UR_Blocked === '1') {
        // 单锁- 反位
        blockedReverse2(yellowColor)
        textValue = '4';
      }
      if (DC_UR_Overlap === '1'|| DC_UR_CBI_occupied === '1') {
        //  进路锁闭-反位
        overlapReverse2(yellowColor);
        textValue = '6';
      }
    } else if (DC_UR_Lost === '1') {
      //失表
      pen.frames = [
        {
          background: redColor,
          bktype: 0,
          duration: 1000,
          visible: true,
        },
        {
          background: testColor,
          bktype: 0,
          duration: 1000,
          visible: true,
        },
      ];
      pen.animateCycle = Infinity;
      pen.animateType = 'custom';
      pen.autoPlay = true;
      lossWatch2(pen?.background || redColor);
      textValue = '9';
    }
  }else if (DC_UR_State !== undefined) {
    // 联锁中断
    interlockInterrupt2(grayColor);
    textValue = '10';
  }
  //左下
  if(DC_DL_State === '1'){
    if (DC_DL_Normal === '1') {
      if (DC_DL_Blocked === '0' && DC_DL_Overlap === '0' && DC_DL_CBI_occupied === '0') {
        //1 正常-定位
        normal(greenColor,rxLeft, ryDown-lineWidth/2);
        textValue = '1'; //赋值操作
      }
      if (DC_DL_Blocked === '1') {
        // 单锁-定位
        blocked(greenColor,rxLeft,ryDown- lineWidth / 2);
        textValue = '3';
      }
      if (DC_DL_Overlap === '1'||DC_DL_CBI_occupied === '1') {
        //  进路锁闭-定位
        overlapDown(greenColor,rxLeft)
        textValue = '5';
      }
    } else if (DC_DL_Reverse === '1') {
      if (DC_DL_Blocked === '0' && DC_DL_Overlap === '0' && DC_DL_CBI_occupied === '0') {
        // 正常 - 反位;
        reverse3(yellowColor);
        textValue = '2';
      }
      if (DC_DL_Blocked === '1') {
        // 单锁- 反位
        blockedReverse3(yellowColor)
        textValue = '4';
      }
      if (DC_DL_Overlap === '1'|| DC_DL_CBI_occupied === '1') {
        //  进路锁闭-反位
        overlapReverse3(yellowColor);
        textValue = '6';
      }
    } else if (DC_DL_Lost === '1') {
      //失表
      pen.frames = [
        {
          background: redColor,
          bktype: 0,
          duration: 1000,
          visible: true,
        },
        {
          background: testColor,
          bktype: 0,
          duration: 1000,
          visible: true,
        },
      ];
      pen.animateCycle = Infinity;
      pen.animateType = 'custom';
      pen.autoPlay = true;
      lossWatch3(pen?.background || redColor);
      textValue = '9';
    }
  }else if (DC_DL_State !== undefined) {
    // 联锁中断
    interlockInterrupt3(grayColor);
    textValue = '10';
  }

  //右下
  if(DC_DR_State === '1'){
    if (DC_DR_Normal === '1') {
      if (DC_DR_Blocked === '0' && DC_DR_Overlap === '0' && DC_DR_CBI_occupied === '0') {
        //1 正常-定位
        normal(greenColor,rxRight, ryDown-lineWidth/2);
        textValue = '1'; //赋值操作
      }
      if (DC_DR_Blocked === '1') {
        // 单锁-定位
        blocked(greenColor,rxRight,ryDown- lineWidth / 2);
        textValue = '3';
      }
      if (DC_DR_Overlap === '1'||DC_DR_CBI_occupied === '1') {
        //  进路锁闭-定位
        overlapDown(greenColor,rxRight)
        textValue = '5';
      }
    } else if (DC_DR_Reverse === '1') {
      if (DC_DR_Blocked === '0' && DC_DR_Overlap === '0' && DC_DR_CBI_occupied === '0') {
        // 正常 - 反位;
        reverse4(yellowColor)
        textValue = '2';
      }
      if (DC_DR_Blocked === '1') {
        // 单锁- 反位
        blockedReverse4(yellowColor)
        textValue = '4';
      }
      if (DC_DR_Overlap === '1'|| DC_DR_CBI_occupied === '1') {
        //  进路锁闭-反位
        overlapReverse4(yellowColor);
        textValue = '6';
      }
    } else if (DC_DR_Lost === '1') {
      //失表
      pen.frames = [
        {
          background: redColor,
          bktype: 0,
          duration: 1000,
          visible: true,
        },
        {
          background: testColor,
          bktype: 0,
          duration: 1000,
          visible: true,
        },
      ];
      pen.animateCycle = Infinity;
      pen.animateType = 'custom';
      pen.autoPlay = true;
      lossWatch4(pen?.background || redColor);
      textValue = '9';
    }
  }else if (DC_DR_State !== undefined) {
    // 联锁中断
    interlockInterrupt4(grayColor);
    textValue = '10';
  }
  ctx.font = `${r}px serif`;
  ctx.fillStyle = 'white';
  ctx.textAlign = 'center';
  // ctx.textBaseline = 'middle';
  ctx.fillText(Dev_name, rxLeft, y-newR*2);
  ctx.fillText(DC_UR_Dev_name, rxRight, y-newR*2);
  ctx.fillText(DC_DL_Dev_name, rxLeft, y+h+newR*2);
  ctx.fillText(DC_DR_Dev_name, rxRight, y+h+newR*2);
  ctx.lineWidth = 1; //重置画板的ctx.lineWidth
  ctx.restore();
  // if (textValue === '1') {
  //   //1 正常-定位
  //   //上y   y + lineWidth/2
  //   //下y  ryDown-lineWidth/2
  //   normal(greenColor,rxLeft,y + lineWidth/2);
  //   normal(greenColor,rxRight,y + lineWidth/2);
  //   normal(greenColor,rxLeft, ryDown-lineWidth/2);
  //   normal(greenColor,rxRight, ryDown-lineWidth/2);
  // } else if (textValue === '2') {
  //   // 正常 - 反位;
  //   reverse(yellowColor);
  //   reverse2(yellowColor);
  //   reverse3(yellowColor);
  //   reverse4(yellowColor)
  // } else if (textValue === '3') {
  //   // 单锁-定位
  //   blocked(greenColor,rxLeft,y+ lineWidth/2);
  //   blocked(greenColor,rxRight,y+ lineWidth/2);
  //   blocked(greenColor,rxLeft,ryDown- lineWidth / 2);
  //   blocked(greenColor,rxRight,ryDown- lineWidth / 2);
  // } else if (textValue === '4') {
  //   // 单锁- 反位
  //   blockedReverse(yellowColor);
  //   blockedReverse2(yellowColor)
  //   blockedReverse3(yellowColor)
  //   blockedReverse4(yellowColor)
  // } else if (textValue === '5') {
  //   //  进路锁闭-定位
  //   overlap(greenColor,rxLeft);
  //   overlap(greenColor,rxRight);
  //   overlapDown(greenColor,rxLeft)
  //   overlapDown(greenColor,rxRight)
  // } else if (textValue === '6') {
  //   //  进路锁闭-反位
  //   overlapReverse(yellowColor);
  //   overlapReverse2(yellowColor);
  //   overlapReverse3(yellowColor);
  //   overlapReverse4(yellowColor);
  // } 
  //  else if (textValue === '9') {
  //   pen.frames = [
  //     {
  //       background: redColor,
  //       bktype: 0,
  //       duration: 1000,
  //       visible: true,
  //     },
  //     {
  //       background: testColor,
  //       bktype: 0,
  //       duration: 1000,
  //       visible: true,
  //     },
  //   ];
  //   pen.animateCycle = Infinity;
  //   pen.animateType = 'custom';
  //   pen.autoPlay = true;
  //   // 失表
  //   lossWatch(pen?.background || redColor);
  //   lossWatch2(pen?.background || redColor);
  //   lossWatch3(pen?.background || redColor);
  //   lossWatch4(pen?.background || redColor);
  // } else if (textValue === '10') {
  //   // 联锁中断
  //   interlockInterrupt(grayColor);
  //   interlockInterrupt2(grayColor);
  //   interlockInterrupt3(grayColor);
  //   interlockInterrupt4(grayColor);
  // } else {
  //   //基础形状
  //   defaultStatus()
  // }
  ctx.lineWidth = 1; //重置画板的ctx.lineWidth
  ctx.restore();
  //圆心：x +w/2-h/2，y
  //1 正常-定位
  function normal(color,rx,norY) {
    const deleteSatrtX = rx - r + lineWidth / 2;
    const deleteEndX = rx + r - lineWidth / 2;
    ctx.save();
    // 绘制岔心部分
    // 绘制横线
    ctx.beginPath();
    ctx.moveTo(deleteSatrtX, norY);
    ctx.lineTo(deleteEndX, norY);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  //2 正常-反位
  function reverse(color) {
    const deleteEndX = rxLeft - r + lineWidth;
    // 与半径圆的交点
    let intersectionX = rxLeft + Math.SQRT1_2 * r;
    let intersectionY = y + Math.SQRT1_2 * r;
    ctx.save();
    // 绘制岔心部分
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rxLeft+lineWidth / 2, y+lineWidth / 2);
    ctx.lineTo(deleteEndX, y+lineWidth / 2);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  //2 正常-反位
  function reverse2(color) {
    const deleteEndX = rxRight + r - lineWidth;
    // 与半径圆的交点
    let intersectionX = rxRight - Math.SQRT1_2 * r;
    let intersectionY = y + Math.SQRT1_2 * r;
    ctx.save();
    // 绘制岔心部分
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rxRight-lineWidth / 2, y+lineWidth / 2);
    ctx.lineTo(deleteEndX, y+lineWidth / 2);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  function reverse3(color) {
    const deleteEndX = rxLeft - r + lineWidth;
    // 与半径圆的交点
    let intersectionX = rxLeft + Math.SQRT1_2 * r;
    let intersectionY = ryDown - Math.SQRT1_2 * r;
    ctx.save();
    // 绘制岔心部分
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rxLeft+lineWidth / 2, ryDown-lineWidth / 2);
    ctx.lineTo(deleteEndX, ryDown-lineWidth / 2);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  function reverse4(color) {
    const deleteEndX = rxRight + r - lineWidth / 2;
    // 与半径圆的交点
    let intersectionX = rxRight - Math.SQRT1_2 * r;
    let intersectionY = ryDown - Math.SQRT1_2 * r;
    ctx.save();

    // 绘制岔心部分
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rxRight-lineWidth / 2, ryDown-lineWidth / 2);
    ctx.lineTo(deleteEndX-lineWidth / 2, ryDown-lineWidth / 2);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  //3 单锁-定位
    //y+ lineWidth/2    ryDown- lineWidth / 2
  function blocked(color,rx,bloY) {
    const deleteSatrtX = rx - newR + lineWidth / 2;
    const deleteEndX = rx + newR - lineWidth / 2;

    ctx.save();
    // 绘制中间同心 圆圈
    ctx.fillStyle = color;
    ctx.beginPath();
    ctx.arc(
      rx,
      bloY,
      Math.sqrt(Math.pow(newR, 2) + Math.pow(h / 12, 2)),
      0,
      Math.PI * 2
    );
    ctx.fill();
    ctx.globalCompositeOperation = 'destination-out';
    ctx.beginPath();
    ctx.arc(
      rx,
      bloY,
      Math.sqrt(Math.pow(newR, 2) + Math.pow(h / 12, 2)) - lineWidth,
      0,
      Math.PI * 2
    );
    ctx.fill();
    ctx.restore();

    // 绘制岔心部分
    ctx.save();
    ctx.beginPath();
    ctx.moveTo(deleteSatrtX, bloY);
    ctx.lineTo(deleteEndX, bloY);
    ctx.strokeStyle = greenColor;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  //4 单锁-反位
  function blockedReverse(color) {
    const deleteSatrtX = rxLeft - newR;
    const deleteEndX = rxLeft - newR;
    // 与半径圆的交点
    let intersectionX = rxLeft + Math.SQRT1_2 * newR;
    let intersectionY = y + Math.SQRT1_2 * newR;

    ctx.save();
    // 绘制中间同心 圆圈
    ctx.fillStyle = color;
    ctx.beginPath();
    ctx.arc(
      rxLeft,
      y+ lineWidth/2,
      Math.sqrt(Math.pow(newR, 2) + Math.pow(h / 12, 2)),
      0,
      Math.PI * 2
    );
    ctx.fill();
    ctx.globalCompositeOperation = 'destination-out';
    ctx.beginPath();
    ctx.arc(
      rxLeft,
      y+ lineWidth/2,
      Math.sqrt(Math.pow(newR, 2) + Math.pow(h / 12, 2)) - lineWidth,
      0,
      Math.PI * 2
    );
    ctx.fill();
    ctx.restore();

    // 绘制岔心部分
    ctx.save();
    ctx.beginPath();
    ctx.moveTo(
      intersectionX - (Math.SQRT1_2 * lineWidth)+lineWidth/2,
      intersectionY - (Math.SQRT1_2 * lineWidth) / 2+lineWidth/2
    );
    ctx.lineTo(rxLeft, y+ lineWidth/2);
    ctx.lineTo(deleteEndX+ lineWidth/2, y+ lineWidth/2);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();

    ctx.restore();
  }
  function blockedReverse2(color) {
    const deleteSatrtX = rxRight - newR;
    const deleteEndX = rxRight + newR;
    // 与半径圆的交点
    let intersectionX = rxRight - Math.SQRT1_2 * newR;
    let intersectionY = y + Math.SQRT1_2 * newR;

    ctx.save();
    // 绘制中间同心 圆圈
    ctx.fillStyle = color;
    ctx.beginPath();
    ctx.arc(
      rxRight,
      y+ lineWidth/2,
      Math.sqrt(Math.pow(newR, 2) + Math.pow(h / 12, 2)),
      0,
      Math.PI * 2
    );
    ctx.fill();
    ctx.globalCompositeOperation = 'destination-out';
    ctx.beginPath();
    ctx.arc(
      rxRight,
      y+ lineWidth/2,
      Math.sqrt(Math.pow(newR, 2) + Math.pow(h / 12, 2)) - lineWidth,
      0,
      Math.PI * 2
    );
    ctx.fill();
    ctx.restore();

    // 绘制岔心部分
    ctx.save();
    ctx.beginPath();
    ctx.moveTo(
      intersectionX + (Math.SQRT1_2 * lineWidth)-lineWidth/2,
      intersectionY - (Math.SQRT1_2 * lineWidth) / 2+lineWidth/2
    );
    ctx.lineTo(rxRight, y+ lineWidth/2);
    ctx.lineTo(deleteEndX-lineWidth/2 , y+ lineWidth/2);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();

    ctx.restore();
  }
  function blockedReverse3(color) {
    const deleteSatrtX = rxLeft - newR;
    const deleteEndX = rxLeft - newR;
    // 与半径圆的交点
    let intersectionX = rxLeft + Math.SQRT1_2 * newR;
    let intersectionY = ryDown - Math.SQRT1_2 * newR;

    ctx.save();
    // 绘制中间同心 圆圈
    ctx.fillStyle = color;
    ctx.beginPath();
    ctx.arc(
      rxLeft,
      ryDown-lineWidth/2,
      Math.sqrt(Math.pow(newR, 2) + Math.pow(h / 12, 2)),
      0,
      Math.PI * 2
    );
    ctx.fill();
    ctx.globalCompositeOperation = 'destination-out';
    ctx.beginPath();
    ctx.arc(
      rxLeft,
      ryDown-lineWidth/2,
      Math.sqrt(Math.pow(newR, 2) + Math.pow(h / 12, 2)) - lineWidth,
      0,
      Math.PI * 2
    );
    ctx.fill();
    ctx.restore();

    // 绘制岔心部分
    ctx.save();
    ctx.beginPath();
    ctx.moveTo(
      intersectionX - (Math.SQRT1_2 * lineWidth) / 2+lineWidth/2,
      intersectionY + (Math.SQRT1_2 * lineWidth) / 2-lineWidth/2
    );
    ctx.lineTo(rxLeft+lineWidth/2, ryDown-lineWidth/2);
    ctx.lineTo(deleteEndX, ryDown-lineWidth/2);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  function blockedReverse4(color) {
    const deleteSatrtX = rxRight - newR;
    const deleteEndX = rxRight + newR;
    // 与半径圆的交点
    let intersectionX = rxRight - Math.SQRT1_2 * newR;
    let intersectionY = ryDown - Math.SQRT1_2 * newR;

    ctx.save();
    // 绘制中间同心 圆圈
    ctx.fillStyle = color;
    ctx.beginPath();
    ctx.arc(
      rxRight,
      ryDown-lineWidth/2,
      Math.sqrt(Math.pow(newR, 2) + Math.pow(h / 12, 2)),
      0,
      Math.PI * 2
    );
    ctx.fill();
    ctx.globalCompositeOperation = 'destination-out';
    ctx.beginPath();
    ctx.arc(
      rxRight,
      ryDown-lineWidth/2,
      Math.sqrt(Math.pow(newR, 2) + Math.pow(h / 12, 2)) - lineWidth,
      0,
      Math.PI * 2
    );
    ctx.fill();
    ctx.restore();

    // 绘制岔心部分
    ctx.save();
    ctx.beginPath();
    ctx.moveTo(
      intersectionX + (Math.SQRT1_2 * lineWidth) / 2-lineWidth/2,
      intersectionY + (Math.SQRT1_2 * lineWidth) / 2-lineWidth/2
    );
    ctx.lineTo(rxRight-lineWidth/2, ryDown-lineWidth/2);
    ctx.lineTo(deleteEndX, ryDown-lineWidth/2);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();

    ctx.restore();
  }
  //5 进路锁闭-定位
  function overlap(color,rx) {
    const deleteSatrtX = rx - r;
    const deleteEndX = rx + r;
    ctx.save()
    ctx.fillStyle = grayColor;
    ctx.fillRect(
      deleteSatrtX,
      y + lineWidth,
      deleteEndX - deleteSatrtX,
      lineWidth
    );
    ctx.fillStyle = color;
    ctx.fillRect(
      deleteSatrtX,
      y,
      deleteEndX - deleteSatrtX,
      lineWidth
    );
    ctx.restore();
  }
  function overlapDown(color,rx) {
    const deleteSatrtX = rx - r;
    const deleteEndX = rx + r;
    ctx.save();
    ctx.fillStyle = grayColor;
    ctx.fillRect(
      deleteSatrtX,
      ryDown - lineWidth*2,
      deleteEndX - deleteSatrtX,
      lineWidth
    );
    ctx.fillStyle = color;
    ctx.fillRect(
      deleteSatrtX,
      ryDown - lineWidth,
      deleteEndX - deleteSatrtX,
      lineWidth
    );
    ctx.restore();
  }
  //6 进路锁闭-反位
  function overlapReverse(color) {
    const deleteSatrtX = rxLeft - r;
    const deleteEndX = rxLeft + r;
    // 绘制矩形
    // 与半径圆的交点
    let intersectionX = rxLeft - Math.SQRT1_2 * r;
    let intersectionY = y + Math.SQRT1_2 * r;
    let intersectionXRight = rxLeft + Math.SQRT1_2 * r;
    ctx.save();
    ctx.fillStyle = grayColor;
    ctx.fillRect(
      rxLeft - r,
      y ,
      deleteEndX - (intersectionX - Math.SQRT1_2 * lineWidth),
      Math.SQRT1_2 * r + Math.SQRT1_2 * lineWidth
    );
    // 绘制折线
    ctx.beginPath();
    ctx.moveTo(intersectionXRight, intersectionY);
    ctx.lineTo(rxLeft+ lineWidth / 2, y+ lineWidth / 2);
    ctx.lineTo(rxLeft - r+ lineWidth / 2, y+ lineWidth / 2);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
    // ctx.fillRect()
  }
  function overlapReverse2(color) {
    const deleteSatrtX = rxRight - r;
    const deleteEndX = rxRight + r;
    // 绘制矩形
    // 与半径圆的交点
    let intersectionX = rxRight - Math.SQRT1_2 * r;
    let intersectionY = y + Math.SQRT1_2 * r;

    ctx.save();
    ctx.fillStyle = grayColor;
    ctx.fillRect(
      intersectionX - Math.SQRT1_2 * lineWidth,
      y,
      deleteEndX - (intersectionX - Math.SQRT1_2 * lineWidth),
      Math.SQRT1_2 * r + Math.SQRT1_2 * lineWidth
    );
    // 绘制折线
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rxRight- lineWidth / 2, y+ lineWidth / 2);
    ctx.lineTo(rxRight + r- lineWidth / 2, y+ lineWidth / 2);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
    // ctx.fillRect()
  }
  function overlapReverse3(color) {
    const deleteSatrtX = rxLeft - r;
    const deleteEndX = rxLeft + r;
    // 绘制矩形
    // 与半径圆的交点
    let intersectionX = rxLeft - Math.SQRT1_2 * r;
    let intersectionY = ryDown - Math.SQRT1_2 * r;
    let intersectionXRight = rxLeft + Math.SQRT1_2 * r;
    ctx.save();
    ctx.fillStyle = grayColor;
    ctx.fillRect(
      rxLeft - r,
      intersectionY - Math.SQRT1_2 * lineWidth,
      deleteEndX - (intersectionX - Math.SQRT1_2 * lineWidth),
      ryDown - intersectionY + Math.SQRT1_2 * lineWidth
    );
    // 绘制折线
    ctx.beginPath();
    ctx.moveTo(intersectionXRight, intersectionY);
    ctx.lineTo(rxLeft + lineWidth / 2, ryDown-lineWidth / 2);
    ctx.lineTo(rxLeft - r+ lineWidth / 2, ryDown-lineWidth / 2);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
    // ctx.fillRect()
  }
  function overlapReverse4(color) {
    const deleteSatrtX = rxRight - r;
    const deleteEndX = rxRight + r;
    // 绘制矩形
    // 与半径圆的交点
    let intersectionX = rxRight - Math.SQRT1_2 * r;
    let intersectionY = ryDown - Math.SQRT1_2 * r;

    ctx.save();
    ctx.fillStyle = grayColor;
    ctx.fillRect(
      intersectionX - Math.SQRT1_2 * lineWidth,
      intersectionY - Math.SQRT1_2 * lineWidth,
      deleteEndX - (intersectionX - Math.SQRT1_2 * lineWidth),
      ryDown - intersectionY + Math.SQRT1_2 * lineWidth
    );
    // 绘制折线
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rxRight- lineWidth / 2, ryDown- lineWidth / 2);
    ctx.lineTo(rxRight + r - lineWidth / 2, ryDown- lineWidth / 2);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
    // ctx.fillRect()
  }
  //9 失表 与圆焦点区域和清理区域坐标可传入（intersectionX，intersectionY）
  function lossWatch(color) {
    // 失表特制圆心焦点
    // 与半径圆的交点
    let intersectionX = rxLeft + Math.SQRT1_2 * r;
    let intersectionY = y + Math.SQRT1_2 * r;
    // 清理即将画图的矩形区域
    const deleteSatrtX = rxLeft - r + lineWidth / 2;
    const deleteEndX = rxLeft + r - lineWidth / 2;
    ctx.save();
    // 绘制岔心部分
    // 绘制横线
    ctx.beginPath();
    ctx.moveTo(rxLeft, y+ lineWidth / 2);
    ctx.lineTo(deleteSatrtX, y+lineWidth/2);
    ctx.strokeStyle = redColor;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    // 绘制斜线
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rxLeft+ lineWidth / 2, y+ lineWidth / 2);
    ctx.lineTo(deleteEndX, y+ lineWidth / 2);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  function lossWatch2(color) {
    // 失表特制圆心焦点
    // 与半径圆的交点
    let intersectionX = rxRight - Math.SQRT1_2 * r;
    let intersectionY = y + Math.SQRT1_2 * r;
    // 清理即将画图的矩形区域
    const deleteSatrtX = rxRight - r + lineWidth / 2;
    const deleteEndX = rxRight + r - lineWidth / 2;
    const deleteWidth = deleteEndX - deleteSatrtX; //宽
    const deleteHeight = y - intersectionY; //intersectionY与底下线的距离 高

    ctx.save();
    // ctx.clearRect(deleteSatrtX, intersectionY, deleteWidth, deleteHeight + lineWidth);

    // 绘制岔心部分
    // 绘制横线
    ctx.beginPath();
    ctx.moveTo(rxRight, y+lineWidth/2);
    ctx.lineTo(deleteEndX, y+lineWidth/2);
    ctx.strokeStyle = redColor;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    // 绘制斜线
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rxRight- lineWidth / 2, y+lineWidth/2);
    ctx.lineTo(deleteSatrtX, y+lineWidth/2);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  function lossWatch3(color) {
    // 失表特制圆心焦点
    // 与半径圆的交点
    let intersectionX = rxLeft + Math.SQRT1_2 * r;
    let intersectionY = ryDown - Math.SQRT1_2 * r;
    // 清理即将画图的矩形区域
    const deleteSatrtX = rxLeft - r + lineWidth / 2;
    const deleteEndX = rxLeft + r - lineWidth / 2;
    ctx.save();

    // 绘制岔心部分
    // 绘制横线
    ctx.beginPath();
    ctx.moveTo(rxLeft, ryDown- lineWidth / 2);
    ctx.lineTo(deleteSatrtX, ryDown- lineWidth / 2);
    ctx.strokeStyle = redColor;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    // 绘制斜线
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rxLeft+ lineWidth / 2, ryDown- lineWidth / 2);
    ctx.lineTo(deleteEndX, ryDown- lineWidth / 2);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  function lossWatch4(color) {
    // 失表特制圆心焦点
    // 与半径圆的交点
    let intersectionX = rxRight - Math.SQRT1_2 * r;
    let intersectionY = ryDown - Math.SQRT1_2 * r;
    // 清理即将画图的矩形区域
    const deleteSatrtX = rxRight - r + lineWidth / 2;
    const deleteEndX = rxRight + r - lineWidth / 2;
    ctx.save();
    // 绘制岔心部分
    // 绘制横线
    ctx.beginPath();
    ctx.moveTo(rxRight, ryDown - lineWidth / 2);
    ctx.lineTo(deleteEndX, ryDown - lineWidth / 2);
    ctx.strokeStyle = redColor;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    // 绘制斜线
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rxRight- lineWidth / 2, ryDown - lineWidth / 2);
    ctx.lineTo(deleteSatrtX, ryDown - lineWidth / 2);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  //10 联锁中断
  function interlockInterrupt(color) {
    let intersectionX = rxLeft + (Math.SQRT1_2 * r)/1.5;
    let intersectionY = y + (Math.SQRT1_2 * r)/1.5;
    ctx.save();
    // 绘制斜线
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rxLeft+ lineWidth / 2 ,y+ lineWidth / 2 );
    ctx.lineTo(rxLeft +r , y+ lineWidth / 2);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    // 绘制横线
    ctx.beginPath();
    ctx.moveTo(rxLeft -r + lineWidth, y+ lineWidth / 2);
    ctx.lineTo(rxLeft +r , y+ lineWidth / 2);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  function interlockInterrupt2(color) {
    let intersectionX = rxRight - (Math.SQRT1_2 * r) / 2;
    let intersectionY = y + (Math.SQRT1_2 * r) / 2;
    ctx.save();
    // 绘制斜线
    ctx.beginPath();
    ctx.moveTo(intersectionX-lineWidth / 2, intersectionY+ lineWidth / 2);
    ctx.lineTo(rxRight-lineWidth / 2,y+ lineWidth / 2);
    ctx.lineTo(rxRight -r, y+ lineWidth / 2);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();

    // 绘制横线
    ctx.beginPath();
    ctx.moveTo(rxRight -r+ lineWidth, y+ lineWidth / 2);
    ctx.lineTo(rxRight +r - lineWidth, y+ lineWidth / 2);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
  }
  function interlockInterrupt3(color) {
    let intersectionX = rxLeft + (Math.SQRT1_2 * r) / 2;
    let intersectionY = ryDown - (Math.SQRT1_2 * r) / 2;
    ctx.save();
    // 绘制斜线
    ctx.beginPath();
    ctx.moveTo(intersectionX+lineWidth / 2, intersectionY-lineWidth / 2);
    ctx.lineTo(rxLeft+lineWidth / 2 ,ryDown - lineWidth / 2);
    ctx.lineTo(rxLeft +r, ryDown- lineWidth / 2);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();

    // 绘制横线
    ctx.beginPath();
    ctx.moveTo(rxLeft -r +lineWidth, ryDown- lineWidth / 2);
    ctx.lineTo(rxLeft +r , ryDown- lineWidth / 2);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  function interlockInterrupt4(color) {
    const intersectionX = rxRight - (Math.SQRT1_2 * r) / 2;
    const intersectionY = ryDown - (Math.SQRT1_2 * r) / 2;
    ctx.save();
    // 绘制斜线
    ctx.beginPath();
    ctx.moveTo(intersectionX- lineWidth / 2, intersectionY- lineWidth / 2);
    ctx.lineTo(rxRight- lineWidth / 2 ,ryDown  - lineWidth / 2);
    ctx.lineTo(rxRight +r- lineWidth, ryDown - lineWidth / 2);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
    // 绘制横线
    ctx.beginPath();
    ctx.moveTo(rxRight -r , ryDown - lineWidth / 2);
    ctx.lineTo(rxRight +r - lineWidth, ryDown - lineWidth / 2);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
  }

  function defaultStatus(){
    //绘制上左交叉线
    drawUpLeftCross()
    //绘制上左横线
    drawUpLeftLine()
    //绘制上右交叉线
    drawUpRightCross()
    //绘制下左交叉线
    drawDownLeftCross()

    //绘制下左绝缘节
    ctx.beginPath()
    ctx.moveTo(x+w/2-h/6+lineWidth/3, y+h/2+h/6-2.5*lineWidth/Math.SQRT2+lineWidth/3);
    ctx.lineTo(x+w/2-h/6+2.5*lineWidth/Math.SQRT2-lineWidth/3, y+h/2+h/6-lineWidth/3);
    ctx.strokeStyle= '#7B7D7B'
    ctx.lineWidth = 2;
    ctx.stroke();

    //绘制下右交叉线
    drawDownRightCross()

    //绘制下右绝缘节
    ctx.beginPath()
    ctx.moveTo(x+w/2+h/6-lineWidth/3, y+h/2+h/6-1.5*lineWidth/Math.SQRT2-lineWidth/3);
    ctx.lineTo(x+w/2+h/6-2*lineWidth/Math.SQRT2, y+h/2+h/6-lineWidth/3);
    ctx.strokeStyle= '#7B7D7B'
    ctx.lineWidth = 2;
    ctx.stroke();
      // 绘制上右横线
    drawUpRightLine()
    // 绘制下左横线
    drawDownLeftLine()

    // 绘制下右横线
    drawDownRightLine()

    //绘制上方中间绝缘节
    ctx.beginPath();
    ctx.moveTo(x +w/2, y-lineWidth/4);
    ctx.lineTo(x +w/2, y+lineWidth*1.25);
    ctx.strokeStyle='#7B7D7B'
    ctx.lineWidth = 2;
    ctx.stroke();
    //绘制下方中间绝缘节
    ctx.beginPath();
    ctx.moveTo(x +w/2, y+h-lineWidth*1.25);
    ctx.lineTo(x +w/2, y+h+lineWidth/4);
    ctx.strokeStyle='#7B7D7B'
    ctx.lineWidth = 2;
    ctx.stroke();
    ctx.save()
  }
  //上左位置交叉线
  function drawUpLeftCross(){
    //绘制上左交叉线
    ctx.beginPath();
    ctx.moveTo(x+ lineWidth / 2, y+lineWidth / 2);
    ctx.lineTo(x +w/2+ lineWidth / 2-h/2, y+ lineWidth / 2);
    ctx.lineTo(x+w/2+h/6-lineWidth, y+h/2+h/6-lineWidth);
    ctx.strokeStyle=fillColor
    if(Reverse === '1'){
      QDLeftUpStatusChange()
    }
    ctx.lineWidth = lineWidth;
    ctx.stroke();
  }
  //上左位置横线
  function drawUpLeftLine(){
    // 绘制上左横线(以上左道岔中心结束)
    ctx.beginPath();
    ctx.moveTo(x + lineWidth / 2, y + lineWidth / 2);
    ctx.lineTo(x + w/2-h/2, y + lineWidth / 2);
    ctx.strokeStyle=fillColor
    if(Normal === '1'||Reverse === '1'){
      QDLeftUpStatusChange()
    }
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    // 绘制上左横线(从上左道岔中心开始,中心点结束)
    ctx.beginPath();
    ctx.moveTo(x +w/2-h/2+lineWidth, y+ lineWidth / 2);
    ctx.lineTo(x + w/2 - lineWidth / 2, y + lineWidth / 2);
    ctx.strokeStyle=fillColor
    if(Normal === '1'){
      QDLeftUpStatusChange()
    }
    ctx.lineWidth = lineWidth;
    ctx.stroke();
  }
  //上右位置交叉线
  function drawUpRightCross(){
    //绘制上右交叉线
    ctx.beginPath();
    ctx.moveTo(x+w- lineWidth / 2, y+lineWidth / 2);
    ctx.lineTo(x +w/2+h/2 - lineWidth / 2, y+ lineWidth / 2);
    ctx.lineTo(x+w/2-h/6+lineWidth / 2, y+h/2+h/6-lineWidth / 2);
    ctx.strokeStyle=fillColor
    if(DC_UR_Reverse === '1'){
      QDRightUpStatusChange()
    }
    ctx.lineWidth = lineWidth;
    ctx.stroke();
  }
  //上右位置横线
  function drawUpRightLine(){
    // 绘制上左横线(最右边开始，岔中心结束)
    ctx.beginPath();
    ctx.moveTo(x + w - lineWidth / 2, y + lineWidth / 2);
    ctx.lineTo(x +w/2+h/2, y + lineWidth / 2);
    ctx.strokeStyle=fillColor
    if(DC_UR_Normal === '1'||DC_UR_Reverse === '1'){
      QDRightUpStatusChange()
    }
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    // 绘制上左横线(以岔中心开始,中心点结束)
    ctx.beginPath();
    ctx.moveTo(x +w/2+h/2- lineWidth / 2, y + lineWidth / 2);
    ctx.lineTo(x +w/2+lineWidth/2, y + lineWidth / 2);
    ctx.strokeStyle=fillColor
    if(DC_UR_Normal === '1'){
      QDRightUpStatusChange()
    }
    ctx.lineWidth = lineWidth;
    ctx.stroke();
  }

  //下左位置交叉线
  function drawDownLeftCross(){
    ctx.beginPath();
    ctx.moveTo(x+ lineWidth / 2, y+h-lineWidth / 2);
    ctx.lineTo(x +w/2-h/2+ lineWidth / 2, y+ h-lineWidth / 2);
    ctx.lineTo(x+w/2-h/6+lineWidth / 2, y+h/2+h/6-lineWidth / 2);
    ctx.strokeStyle=fillColor
    if(DC_DL_Reverse === '1'){
      QDleftDownStatusChange()
    }
    ctx.lineWidth = lineWidth;
    ctx.stroke();
  }
  //下左位置横线
  function drawDownLeftLine(){
    //横线-从最左端到岔心
    ctx.beginPath();
    ctx.moveTo(x + lineWidth / 2, y +h- lineWidth / 2);
    ctx.lineTo(x + w/2 -h/2, y +h- lineWidth / 2);
    ctx.strokeStyle=fillColor
    if(DC_DL_Normal === '1'||DC_DL_Reverse === '1'){
      QDleftDownStatusChange()
    }
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    //横线-从岔心到中心点
    ctx.beginPath();
    ctx.moveTo(x + w/2 -h/2+lineWidth, y +h- lineWidth / 2);
    ctx.lineTo(x + w/2 - lineWidth / 2, y +h- lineWidth / 2);
    ctx.strokeStyle=fillColor
    if(DC_DL_Normal === '1'){
      QDleftDownStatusChange()
    }
    ctx.lineWidth = lineWidth;
    ctx.stroke();
  }
  //下右位置交叉线
  function drawDownRightCross(){
    ctx.beginPath();
    ctx.moveTo(x+w- lineWidth / 2, y+h-lineWidth / 2);
    ctx.lineTo(x +w/2- lineWidth / 2+h/2, y+ h-lineWidth / 2);
    ctx.lineTo(x+w/2+h/6-lineWidth / 2, y+h/2+h/6-lineWidth / 2);
    ctx.strokeStyle=fillColor
    if(DC_DR_Reverse === '1'){
      QDRightDownStatusChange()
    }
    ctx.lineWidth = lineWidth;
    ctx.stroke();
  }
  //下右位置横线
  function drawDownRightLine(){
    //横线-从最右端到岔心
    ctx.beginPath();
    ctx.moveTo(x + w - lineWidth / 2, y +h- lineWidth / 2);
    ctx.lineTo(x +w/2+h/2, y +h- lineWidth / 2);
    ctx.strokeStyle=fillColor
    if(DC_DR_Reverse === '1'||DC_DR_Normal==='1'){
      QDRightDownStatusChange()
    }
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    //横线-从岔心到中心点
    ctx.beginPath();
    ctx.moveTo(x +w/2+h/2 - lineWidth / 2, y +h- lineWidth / 2);
    ctx.lineTo(x +w/2+ lineWidth / 2, y +h- lineWidth / 2);
    ctx.strokeStyle=fillColor
    if(DC_DR_Normal==='1'){
      QDRightDownStatusChange()
    }
    ctx.lineWidth = lineWidth;
    ctx.stroke();
  }
  //上左区段状态变化
  function  QDLeftUpStatusChange(){
    if(qdState === '1'){
      if(CBTC_occupied ==='1'){
        ctx.strokeStyle='#FF0000'
      }else if(qdCBI_occupied==='1'){
        ctx.strokeStyle='#804000'
      }else if (Invalid === '1'){
        ctx.strokeStyle='#FF66CC'
      }else if (Locked === '1'){
        ctx.strokeStyle='#00FF00'
      }else if (qdOverlap === '1'){
        ctx.strokeStyle='#FFFF00'
      }
    }else if (qdState !== undefined){
      ctx.strokeStyle='#6B696B'
    }
  }
  //上右区段状态变化
  function  QDRightUpStatusChange(){
    if(QD_UR_State === '1'){
      if(QD_UR_CBTC_occupied ==='1'){
        ctx.strokeStyle='#FF0000'
      }else if(QD_UR_CBI_occupied==='1'){
        ctx.strokeStyle='#804000'
      }else if (QD_UR_CBTC_Invalid === '1'){
        ctx.strokeStyle='#FF66CC'
      }else if (QD_UR_Locked === '1'){
        ctx.strokeStyle='#00FF00'
      }else if (QD_UR_Overlap === '1'){
        ctx.strokeStyle='#FFFF00'
      }
    }else if (QD_UR_State !== undefined){
      ctx.strokeStyle='#6B696B'
    }
  }
  //下左区段状态变化
  function  QDleftDownStatusChange(){
    if(QD_DL_State === '1'){
      if(QD_DL_CBTC_occupied ==='1'){
        ctx.strokeStyle='#FF0000'
      }else if(QD_DL_CBI_occupied==='1'){
        ctx.strokeStyle='#804000'
      }else if (QD_DL_CBTC_Invalid === '1'){
        ctx.strokeStyle='#FF66CC'
      }else if (QD_DL_Locked === '1'){
        ctx.strokeStyle='#00FF00'
      }else if (QD_DL_Overlap === '1'){
        ctx.strokeStyle='#FFFF00'
      }
    }else if (QD_DL_State !== undefined){
      ctx.strokeStyle='#6B696B'
    }
  }
  //下右区段状态变化
  function  QDRightDownStatusChange(){
    if(QD_DR_State === '1'){
      if(QD_DR_CBTC_occupied ==='1'){
        ctx.strokeStyle='#FF0000'
      }else if(QD_DR_CBI_occupied==='1'){
        ctx.strokeStyle='#804000'
      }else if (QD_DR_CBTC_Invalid === '1'){
        ctx.strokeStyle='#FF66CC'
      }else if (QD_DR_Locked === '1'){
        ctx.strokeStyle='#00FF00'
      }else if (QD_DR_Overlap === '1'){
        ctx.strokeStyle='#FFFF00'
      }
    }else if (QD_DR_State !== undefined){
      ctx.strokeStyle='#6B696B'
    }
  }
}