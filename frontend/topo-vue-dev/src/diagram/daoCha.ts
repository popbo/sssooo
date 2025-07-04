const greenColor = '#00ff00'; //绿色
const grayColor = '#6B696B'; //灰色
const yellowColor = '#ffff00'; //黄色
const redColor = '#ff0000'; //红色
const whiteColor = '#ffffff';
const testColor = 'rgba(0,0 ,0 ,0)';
export function daoCha(ctx: CanvasRenderingContext2D, pen: any) {
  let x = pen.calculative.worldRect.x;
  let y = pen.calculative.worldRect.y;
  let w = pen.calculative.worldRect.width;
  let h = pen.calculative.worldRect.height;
  let r = w / 20; //圆半径
  let rx = x + w / 2; // 圆心
  let ry = y + h / 2; // 圆心
  let lineWidth = h / 4; //线条宽度
  const newR=w/35  //调小圆的直径
  let textValue = pen.text + '';
  const fillColor = pen.calculative.color ? pen.calculative.color : '#7B7D7B';
  // 初始化动画配置
  pen.frames = [];
  pen.animateCycle = 0;
  pen.animateType = '';
  pen.autoPlay = false;
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
   CBI_occupied: qdCBI_occupied,
   Overlap: qdOverlap,
   State: qdState,
 } = pen.statusObj2 ?? {};
  // 显示
  if (State === '1') {
    if (Normal === '1') {
      if (Blocked === '0' && Overlap === '0' && CBI_occupied === '0') {
        //1 正常-定位
        textValue = '1'; //赋值操作
      }
      if (Blocked === '1') {
        // 单锁-定位
        textValue = '3';
      }
      if (Overlap === '1' || CBI_occupied === '1') {
        //  进路锁闭-定位
        textValue = '5';
      }
      // if (CBI_occupied === '1') {
      //   // 占用-定位
      //   textValue = '7';
      // }
    } else if (Reverse === '1') {
      if (Blocked === '0' && Overlap === '0' && CBI_occupied === '0') {
        // 正常 - 反位;
        textValue = '2';
      }
      if (Blocked === '1') {
        // 单锁- 反位
        textValue = '4';
      }
      if (Overlap === '1' || CBI_occupied === '1') {
        //  进路锁闭-反位
        textValue = '6';
      }
      // if (CBI_occupied === '1') {
      //   // 占用-反位
      //   textValue = '8';
      // }
    } else if (Lost === '1') {
      //失表
      textValue = '9';
    }
  } else if(State !== undefined ){
    // 联锁中断
    textValue = '10';
  }
 

  ctx.save();
  defaultStatus();
  if (textValue === '1') {
    //1 正常-定位
    normal(greenColor);
  } else if (textValue === '2') {
    // 正常 - 反位;
    reverse(yellowColor);
  } else if (textValue === '3') {
    // 单锁-定位
    blocked(greenColor);
  } else if (textValue === '4') {
    // 单锁- 反位
    blockedReverse(yellowColor);
  } else if (textValue === '5') {
    //  进路锁闭-定位
    overlap(greenColor);
  } else if (textValue === '6') {
    //  进路锁闭-反位
    overlapReverse(yellowColor);
  }
  // else if (textValue === '7') {
  //   // 占用-定位
  //   CBI_occupiedFn(greenColor);
  // } else if (textValue === '8') {
  //   // 占用-反位
  //   CBI_occupiedReverse(yellowColor);
  // }
  else if (textValue === '9') {
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
    // 失表
    lossWatch(pen?.background || redColor);
  } else if (textValue === '10') {
    // 联锁中断
    interlockInterrupt(grayColor);
  } else {
    //基础形状
    defaultStatus();
  }
  ctx.font = `${r}px serif`;
  ctx.fillStyle='white';
  ctx.textAlign = 'center';
  // ctx.textBaseline = 'middle'
  ctx.fillText(Dev_name, rx, ry+newR*2);
  ctx.lineWidth = 1; //重置画板的ctx.lineWidth
  ctx.restore();

  //1 正常-定位
  function normal(color) {
    const deleteSatrtX = rx - r + lineWidth / 2;
    const deleteEndX = rx + r - lineWidth / 2;
    ctx.save();
    // 绘制岔心部分
    // 绘制横线
    ctx.beginPath();
    ctx.moveTo(deleteSatrtX, ry);
    ctx.lineTo(deleteEndX, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  //2 正常-反位
  function reverse(color) {
    const deleteSatrtX = rx - r;
    const deleteEndX = rx + r - lineWidth / 2;
    // 与半径圆的交点
    let intersectionX = rx - Math.SQRT1_2 * r;
    let intersectionY = ry - Math.SQRT1_2 * r;
    ctx.save();
    // ctx.clearRect(deleteSatrtX, intersectionY, deleteWidth, deleteHeight + 10);

    // 绘制岔心部分
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rx, ry);
    ctx.lineTo(deleteEndX, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  //3 单锁-定位
  function blocked(color) {
    const deleteSatrtX = rx - newR + lineWidth / 2;
    const deleteEndX = rx + newR - lineWidth / 2;

    ctx.save();
    // 绘制中间同心 圆圈
    ctx.fillStyle = color;
    ctx.beginPath();
    ctx.arc(
      rx,
      ry,
      Math.sqrt(Math.pow(newR, 2) + Math.pow(h / 2, 2)),
      0,
      Math.PI * 2
    );
    ctx.fill();
    ctx.globalCompositeOperation = 'destination-out';
    ctx.beginPath();
    ctx.arc(
      rx,
      ry,
      Math.sqrt(Math.pow(newR, 2) + Math.pow(h / 2, 2)) - lineWidth,
      0,
      Math.PI * 2
    );
    ctx.fill();
    ctx.restore();

    // 绘制岔心部分
    ctx.save();
    ctx.beginPath();
    ctx.moveTo(deleteSatrtX, ry);
    ctx.lineTo(deleteEndX, ry);
    ctx.strokeStyle = greenColor;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  //4 单锁-反位
  function blockedReverse(color) {
    const deleteSatrtX = rx - newR;
    const deleteEndX = rx + newR;
    // 与半径圆的交点
    let intersectionX = rx - Math.SQRT1_2 * newR;
    let intersectionY = ry - Math.SQRT1_2 * newR;

    ctx.save();
    // 绘制中间同心 圆圈
    ctx.fillStyle = color;
    ctx.beginPath();
    ctx.arc(
      rx,
      ry,
      Math.sqrt(Math.pow(newR, 2) + Math.pow(h / 2, 2)),
      0,
      Math.PI * 2
    );
    ctx.fill();
    ctx.globalCompositeOperation = 'destination-out';
    ctx.beginPath();
    ctx.arc(
      rx,
      ry,
      Math.sqrt(Math.pow(newR, 2) + Math.pow(h / 2, 2)) - lineWidth,
      0,
      Math.PI * 2
    );
    ctx.fill();
    ctx.restore();

    // 绘制岔心部分
    ctx.save();
    ctx.beginPath();
    ctx.moveTo(
      intersectionX + (Math.SQRT1_2 * lineWidth) / 2,
      intersectionY + (Math.SQRT1_2 * lineWidth) / 2
    );
    ctx.lineTo(rx, ry);
    ctx.lineTo(deleteEndX, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  //5 进路锁闭-定位
  function overlap(color) {
    const deleteSatrtX = rx - r;
    const deleteEndX = rx + r;
    // 绘制矩形
    // 与半径圆的交点
    let intersectionX = rx - Math.SQRT1_2 * r;
    let intersectionY = ry - Math.SQRT1_2 * r;

    ctx.save();
    ctx.fillStyle = grayColor;
    ctx.fillRect(
      deleteSatrtX,
      y + h / 4 - lineWidth / 2,
      deleteEndX - deleteSatrtX,
      lineWidth
    );
    ctx.fillStyle = color;
    ctx.fillRect(
      deleteSatrtX,
      y + h / 4 + lineWidth / 2,
      deleteEndX - deleteSatrtX,
      lineWidth
    );
    ctx.restore();
    // ctx.fillRect()
  }
  //6 进路锁闭-反位
  function overlapReverse(color) {
    const deleteSatrtX = rx - r;
    const deleteEndX = rx + r;
    // 绘制矩形
    // 与半径圆的交点
    let intersectionX = rx - Math.SQRT1_2 * r;
    let intersectionY = ry - Math.SQRT1_2 * r;

    ctx.save();
    ctx.fillStyle = grayColor;
    ctx.fillRect(
      intersectionX - Math.SQRT1_2 * lineWidth,
      intersectionY - Math.SQRT1_2 * lineWidth,
      deleteEndX - (intersectionX - Math.SQRT1_2 * lineWidth),
      ry + lineWidth / 2 - intersectionY + Math.SQRT1_2 * lineWidth
    );
    // 绘制折线
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rx, ry);
    ctx.lineTo(rx + r - lineWidth / 2, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
    // ctx.fillRect()
  }
  //7 占用-定位
  function CBI_occupiedFn(color) {
    const deleteSatrtX = rx - r + lineWidth / 2;
    const deleteEndX = rx + r - lineWidth / 2;
    ctx.save();
    // 绘制岔心部分
    // ctx.fillStyle = grayColor;
    // ctx.fillRect(x, y - lineWidth / 2, w, lineWidth);
    ctx.beginPath();
    ctx.moveTo(deleteSatrtX, ry - lineWidth);
    ctx.lineTo(deleteEndX, ry - lineWidth);
    ctx.strokeStyle = grayColor;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    // 绘制横线
    ctx.beginPath();
    ctx.moveTo(deleteSatrtX - r, ry);
    ctx.lineTo(deleteEndX + r, ry);
    ctx.strokeStyle = redColor;
    ctx.lineWidth = lineWidth;
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(deleteSatrtX, ry);
    ctx.lineTo(deleteEndX, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    // ctx.lineWidth = lineWidth;
    // ctx.stroke();
    ctx.restore();
  }
  //8 占用-反位
  function CBI_occupiedReverse(color) {
    const deleteSatrtX = rx - r + lineWidth / 2;
    const deleteEndX = rx + r - lineWidth / 2;
    // 与半径圆的交点
    let intersectionX = rx - Math.SQRT1_2 * r;
    let intersectionY = ry - Math.SQRT1_2 * r;
    ctx.save();
    // ctx.clearRect(deleteSatrtX, intersectionY, deleteWidth, deleteHeight + 10);

    ctx.beginPath();
    ctx.moveTo(
      intersectionX - Math.SQRT1_2 * r,
      intersectionY - Math.SQRT1_2 * r
    );
    ctx.lineTo(rx, ry);
    ctx.lineTo(deleteEndX + r, ry);
    ctx.strokeStyle = redColor;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.fillStyle = grayColor;
    ctx.fillRect(
      intersectionX - Math.SQRT1_2 * lineWidth,
      intersectionY - Math.SQRT1_2 * lineWidth,
      rx + r - (intersectionX - Math.SQRT1_2 * lineWidth),
      ry + lineWidth / 2 - intersectionY + Math.SQRT1_2 * lineWidth
    );
    // 绘制岔心部分
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rx, ry);
    ctx.lineTo(deleteEndX, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  //9 失表 与圆焦点区域和清理区域坐标可传入（intersectionX，intersectionY）
  function lossWatch(color) {
    // 失表特制圆心焦点
    // 与半径圆的交点
    let intersectionX = rx - Math.SQRT1_2 * r;
    let intersectionY = ry - Math.SQRT1_2 * r;
    // 清理即将画图的矩形区域
    const deleteSatrtX = rx - r + lineWidth / 2;
    const deleteEndX = rx + r - lineWidth / 2;
    const deleteWidth = deleteEndX - deleteSatrtX; //宽
    const deleteHeight = ry - intersectionY; //intersectionY与底下线的距离 高

    ctx.save();
    // ctx.clearRect(deleteSatrtX, intersectionY, deleteWidth, deleteHeight + lineWidth);

    // 绘制岔心部分
    // 绘制横线
    ctx.beginPath();
    ctx.moveTo(rx, ry);
    ctx.lineTo(deleteEndX, ry);
    ctx.strokeStyle = redColor;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    // 绘制斜线
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rx, ry);
    ctx.lineTo(deleteSatrtX, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  //10 联锁中断
  function interlockInterrupt(color) {
    const intersectionX = rx - (Math.SQRT1_2 * r) / 2;
    const intersectionY = ry - (Math.SQRT1_2 * r) / 2;
    ctx.save();
    // 绘制斜线
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rx, ry);
    ctx.lineTo(rx + r - lineWidth / 2, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
    // 绘制横线
    ctx.beginPath();
    ctx.moveTo(rx - r + lineWidth / 2, ry);
    ctx.lineTo(rx + r - lineWidth / 2, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
  }
  function defaultStatus() {
    // 与半径圆的交点
    const intersectionX = rx - (Math.SQRT1_2 * w) / 2;
    const intersectionY = ry - (Math.SQRT1_2 * w) / 2;
    // 绘制斜线
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rx, ry);
    ctx.lineTo(x + w - lineWidth / 2, ry);
    ctx.strokeStyle = fillColor;
    if (Reverse === '1') {
      if (qdState === '1'&& State === '1') {
        if (CBTC_occupied === '1') {
          ctx.strokeStyle = '#FF0000';
        } else if (qdCBI_occupied === '1') {
          ctx.strokeStyle = '#804000';
        } else if (Invalid === '1') {
          ctx.strokeStyle = '#FF66CC';
        } else if (Locked === '1') {
          ctx.strokeStyle = '#00FF00';
        } else if (qdOverlap === '1') {
          ctx.strokeStyle = '#FFFF00';
        }
      }else{
        if(qdState !== '1'){
          ctx.strokeStyle = '#6B696B';
        }
      }
    }
    // ctx.strokeStyle = fillColor;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    // 绘制定位横线
    ctx.beginPath();
    ctx.moveTo(x + lineWidth / 2, ry);
    ctx.lineTo(x + w / 2 - lineWidth / 2, ry);
    ctx.strokeStyle = fillColor;
    if (Normal === '1') {
      if (qdState === '1'&& State === '1'){
        if (CBTC_occupied === '1') {
          ctx.strokeStyle = '#FF0000';
        } else if (qdCBI_occupied === '1') {
          ctx.strokeStyle = '#804000';
        } else if (Invalid === '1') {
          ctx.strokeStyle = '#FF66CC';
        } else if (Locked === '1') {
          ctx.strokeStyle = '#00FF00';
        } else if (qdOverlap === '1') {
          ctx.strokeStyle = '#FFFF00';
        }
      }else{
        if(qdState !== '1'){
          ctx.strokeStyle = '#6B696B';
        }
      }
    }
    ctx.lineWidth = lineWidth;
    ctx.stroke();

    // 绘制固定横线
    ctx.beginPath();
    ctx.moveTo(x + w / 2 + lineWidth / 2, ry);
    ctx.lineTo(x + w - lineWidth / 2, ry);
    ctx.strokeStyle = fillColor;
    if (Normal === '1' || Reverse === '1') {
      if (qdState === '1'&& State === '1') {
        if (CBTC_occupied === '1') {
          ctx.strokeStyle = '#FF0000';
        } else if (qdCBI_occupied === '1') {
          ctx.strokeStyle = '#804000';
        } else if (Invalid === '1') {
          ctx.strokeStyle = '#FF66CC';
        } else if (Locked === '1') {
          ctx.strokeStyle = '#00FF00';
        } else if (qdOverlap === '1') {
          ctx.strokeStyle = '#FFFF00';
        }
      }else{
        if(qdState !== '1'){
          ctx.strokeStyle = '#6B696B';
        }
      }
    }
    ctx.lineWidth = lineWidth;
    ctx.stroke();
  }
}
export function daoChaRight(ctx: CanvasRenderingContext2D, pen: any) {
  let x = pen.calculative.worldRect.x;
  let y = pen.calculative.worldRect.y;
  let w = pen.calculative.worldRect.width;
  let h = pen.calculative.worldRect.height;
  let r = w / 20; //圆半径
  let rx = x + w / 2; // 圆心
  let ry = y + h / 2; // 圆心
  let lineWidth = h / 4; //线条宽度
  const newR=w/35  //调小圆的直径
  let textValue = pen.text + '';
  const fillColor = pen.calculative.color ? pen.calculative.color : '#7B7D7B';
  // 初始化动画配置
  pen.frames = [];
  pen.animateCycle = 0;
  pen.animateType = '';
  pen.autoPlay = false;
  // 解构取值
  const {
    Dev_name = '',
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
    CBI_occupied: qdCBI_occupied,
    Overlap: qdOverlap,
    State: qdState,
  } = pen.statusObj2 ?? {};
  // 显示
   if (State === '1') {
     if (Normal === '1') {
       if (Blocked === '0' && Overlap === '0' && CBI_occupied === '0') {
         //1 正常-定位
         textValue = '1'; //赋值操作
       }
       if (Blocked === '1') {
         // 单锁-定位
         textValue = '3';
       }
       if (Overlap === '1' || CBI_occupied === '1') {
         //  进路锁闭-定位
         textValue = '5';
       }
       // if (CBI_occupied === '1') {
       //   // 占用-定位
       //   textValue = '7';
       // }
     } else if (Reverse === '1') {
       if (Blocked === '0' && Overlap === '0' && CBI_occupied === '0') {
         // 正常 - 反位;
         textValue = '2';
       }
       if (Blocked === '1') {
         // 单锁- 反位
         textValue = '4';
       }
       if (Overlap === '1' || CBI_occupied === '1') {
         //  进路锁闭-反位
         textValue = '6';
       }
       // if (CBI_occupied === '1') {
       //   // 占用-反位
       //   textValue = '8';
       // }
     } else if (Lost === '1') {
       //失表
       textValue = '9';
     }
   } else if(State !== undefined ){
    // 联锁中断
    textValue = '10';
  }

  ctx.save();
  defaultStatus();
  if (textValue === '1') {
    //1 正常-定位
    normal(greenColor);
  } else if (textValue === '2') {
    // 正常 - 反位;
    reverse(yellowColor);
  } else if (textValue === '3') {
    // 单锁-定位
    blocked(greenColor);
  } else if (textValue === '4') {
    // 单锁- 反位
    blockedReverse(yellowColor);
  } else if (textValue === '5') {
    //  进路锁闭-定位
    overlap(greenColor);
  } else if (textValue === '6') {
    //  进路锁闭-反位
    overlapReverse(yellowColor);
  }
  // else if (textValue === '7') {
  //   // 占用-定位
  //   CBI_occupiedFn(greenColor);
  // } else if (textValue === '8') {
  //   // 占用-反位
  //   CBI_occupiedReverse(yellowColor);
  // }
  else if (textValue === '9') {
    // 失表
    lossWatch(pen?.background || redColor);
  } else if (textValue === '10') {
    // 联锁中断
    interlockInterrupt(grayColor);
  } else {
    //基础形状
    defaultStatus();
    // 与半径圆的交点
    // let intersectionX = rx + (Math.SQRT1_2 * w) / 2;
    // let intersectionY = ry - (Math.SQRT1_2 * w) / 2;

    // // 绘制横线
    // ctx.beginPath();
    // ctx.moveTo(x + lineWidth / 2, ry);
    // ctx.lineTo(x + w - lineWidth / 2, ry);
    // ctx.strokeStyle = fillColor;
    // ctx.lineWidth = lineWidth;
    // ctx.stroke();

    // // 绘制斜线
    // ctx.beginPath();
    // ctx.moveTo(intersectionX, intersectionY);
    // ctx.lineTo(rx, ry);
    // ctx.lineTo(x + w - lineWidth / 2, ry);
    // ctx.strokeStyle = fillColor;
    // ctx.lineWidth = lineWidth;
    // ctx.stroke();
  }
  ctx.font = `${r}px serif`;
  ctx.fillStyle = 'white';
  ctx.textAlign = 'center';
  // ctx.textBaseline = 'middle';
  ctx.fillText(Dev_name, rx, ry+newR*2);
  ctx.lineWidth = 1; //重置画板的ctx.lineWidth
  ctx.restore();

  //1 正常-定位
  function normal(color) {
    const deleteSatrtX = rx - newR + lineWidth / 2;
    const deleteEndX = rx + newR - lineWidth / 2;
    ctx.save();
    // 绘制岔心部分
    // 绘制横线
    ctx.beginPath();
    ctx.moveTo(deleteSatrtX, ry);
    ctx.lineTo(deleteEndX, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  //2 正常-反位
  function reverse(color) {
    const deleteSatrtX = rx - r;
    const deleteEndX = rx - r + lineWidth / 2;
    // 与半径圆的交点
    let intersectionX = rx + Math.SQRT1_2 * r;
    let intersectionY = ry - Math.SQRT1_2 * r;
    ctx.save();
    // ctx.clearRect(deleteSatrtX, intersectionY, deleteWidth, deleteHeight + 10);

    // 绘制岔心部分
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rx, ry);
    ctx.lineTo(deleteEndX, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  //3 单锁-定位
  function blocked(color) {
    const deleteSatrtX = rx - newR + lineWidth / 2;
    const deleteEndX = rx + newR - lineWidth / 2;

    ctx.save();
    // 绘制中间同心 圆圈
    ctx.fillStyle = color;
    ctx.beginPath();
    ctx.arc(
      rx,
      ry,
      Math.sqrt(Math.pow(newR, 2) + Math.pow(h / 2, 2)),
      0,
      Math.PI * 2
    );
    ctx.fill();
    ctx.globalCompositeOperation = 'destination-out';
    ctx.beginPath();
    ctx.arc(
      rx,
      ry,
      Math.sqrt(Math.pow(newR, 2) + Math.pow(h / 2, 2)) - lineWidth,
      0,
      Math.PI * 2
    );
    ctx.fill();
    ctx.restore();

    // 绘制岔心部分
    ctx.save();
    ctx.beginPath();
    ctx.moveTo(deleteSatrtX, ry);
    ctx.lineTo(deleteEndX, ry);
    ctx.strokeStyle = greenColor;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  //4 单锁-反位
  function blockedReverse(color) {
    const deleteEndX = rx - newR;
    // 与半径圆的交点
    let intersectionX = rx + Math.SQRT1_2 * newR;
    let intersectionY = ry - Math.SQRT1_2 * newR;

    ctx.save();
    // 绘制中间同心 圆圈
    ctx.fillStyle = color;
    ctx.beginPath();
    ctx.arc(
      rx,
      ry,
      Math.sqrt(Math.pow(newR, 2) + Math.pow(h / 2, 2)),
      0,
      Math.PI * 2
    );
    ctx.fill();
    ctx.globalCompositeOperation = 'destination-out';
    ctx.beginPath();
    ctx.arc(
      rx,
      ry,
      Math.sqrt(Math.pow(newR, 2) + Math.pow(h / 2, 2)) - lineWidth,
      0,
      Math.PI * 2
    );
    ctx.fill();
    ctx.restore();

    // 绘制岔心部分
    ctx.save();
    ctx.beginPath();
    ctx.moveTo(
      intersectionX - (Math.SQRT1_2 * lineWidth) / 2,
      intersectionY + (Math.SQRT1_2 * lineWidth) / 2
    );
    ctx.lineTo(rx, ry);
    ctx.lineTo(deleteEndX, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  //5 进路锁闭-定位
  function overlap(color) {
    const deleteSatrtX = rx - r;
    const deleteEndX = rx + r;
    // 绘制矩形
    // 与半径圆的交点
    let intersectionX = rx - Math.SQRT1_2 * r;
    let intersectionY = ry - Math.SQRT1_2 * r;

    ctx.save();
    ctx.fillStyle = grayColor;
    ctx.fillRect(
      deleteSatrtX,
      y + h / 4 - lineWidth / 2,
      deleteEndX - deleteSatrtX,
      lineWidth
    );
    ctx.fillStyle = color;
    ctx.fillRect(
      deleteSatrtX,
      y + h / 4 + lineWidth / 2,
      deleteEndX - deleteSatrtX,
      lineWidth
    );
    ctx.restore();
    // ctx.fillRect()
  }
  //6 进路锁闭-反位
  function overlapReverse(color) {
    const deleteSatrtX = rx - r;
    const deleteEndX = rx + r;
    // 绘制矩形
    // 与半径圆的交点
    let intersectionX = rx - Math.SQRT1_2 * r;
    let intersectionY = ry - Math.SQRT1_2 * r;
    let intersectionXRight = rx + Math.SQRT1_2 * r;
    ctx.save();
    ctx.fillStyle = grayColor;
    ctx.fillRect(
      rx - r,
      intersectionY - Math.SQRT1_2 * lineWidth,
      deleteEndX - (intersectionX - Math.SQRT1_2 * lineWidth),
      ry + lineWidth / 2 - intersectionY + Math.SQRT1_2 * lineWidth
    );
    // 绘制折线
    ctx.beginPath();
    ctx.moveTo(intersectionXRight, intersectionY);
    ctx.lineTo(rx, ry);
    ctx.lineTo(rx - r + lineWidth / 2, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
    // ctx.fillRect()
  }
  //7 占用-定位
  function CBI_occupiedFn(color) {
    const deleteSatrtX = rx - r + lineWidth / 2;
    const deleteEndX = rx + r - lineWidth / 2;
    ctx.save();
    // 绘制岔心部分
    // ctx.fillStyle = grayColor;
    // ctx.fillRect(x, y - lineWidth / 2, w, lineWidth);
    ctx.beginPath();
    ctx.moveTo(deleteSatrtX, ry - lineWidth);
    ctx.lineTo(deleteEndX, ry - lineWidth);
    ctx.strokeStyle = grayColor;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    // 绘制横线
    ctx.beginPath();
    ctx.moveTo(deleteSatrtX - r, ry);
    ctx.lineTo(deleteEndX + r, ry);
    ctx.strokeStyle = redColor;
    ctx.lineWidth = lineWidth;
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(deleteSatrtX, ry);
    ctx.lineTo(deleteEndX, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    // ctx.lineWidth = lineWidth;
    // ctx.stroke();
    ctx.restore();
  }
  //8 占用-反位
  function CBI_occupiedReverse(color) {
    const deleteSatrtX = rx - r + lineWidth / 2;
    const deleteEndX = rx + r - lineWidth / 2;
    // 与半径圆的交点
    let intersectionX = rx + Math.SQRT1_2 * r;
    let intersectionY = ry - Math.SQRT1_2 * r;
    let intersectionXRight = rx + Math.SQRT1_2 * r;
    ctx.save();
    // ctx.clearRect(deleteSatrtX, intersectionY, deleteWidth, deleteHeight + 10);

    ctx.beginPath();
    ctx.moveTo(
      intersectionX + Math.SQRT1_2 * r,
      intersectionY - Math.SQRT1_2 * r
    );
    ctx.lineTo(rx, ry);
    ctx.lineTo(deleteSatrtX - r, ry);
    ctx.strokeStyle = redColor;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.fillStyle = grayColor;
    ctx.fillRect(
      rx - r,
      intersectionY - Math.SQRT1_2 * lineWidth,
      rx + r - (rx - Math.SQRT1_2 * r - Math.SQRT1_2 * lineWidth),
      ry + lineWidth / 2 - intersectionY + Math.SQRT1_2 * lineWidth
    );
    // 绘制岔心部分
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rx, ry);
    ctx.lineTo(deleteSatrtX, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  //9 失表 与圆焦点区域和清理区域坐标可传入（intersectionX，intersectionY）
  function lossWatch(color) {
    // 失表特制圆心焦点
    // 与半径圆的交点
    let intersectionX = rx + Math.SQRT1_2 * r;
    let intersectionY = ry - Math.SQRT1_2 * r;
    // 清理即将画图的矩形区域
    const deleteSatrtX = rx - r + lineWidth / 2;
    const deleteEndX = rx + r - lineWidth / 2;
    const deleteWidth = deleteEndX - deleteSatrtX; //宽
    const deleteHeight = ry - intersectionY; //intersectionY与底下线的距离 高

    ctx.save();
    // ctx.clearRect(deleteSatrtX, intersectionY, deleteWidth, deleteHeight + lineWidth);

    // 绘制岔心部分
    // 绘制横线
    ctx.beginPath();
    ctx.moveTo(rx, ry);
    ctx.lineTo(deleteEndX, ry);
    ctx.strokeStyle = redColor;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    // 绘制斜线
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rx, ry);
    ctx.lineTo(deleteSatrtX, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  //10 联锁中断
  function interlockInterrupt(color) {
    let intersectionX = rx + (Math.SQRT1_2 * r) / 2;
    let intersectionY = ry - (Math.SQRT1_2 * r) / 2;
    ctx.save();
    // 绘制斜线
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rx, ry);
    ctx.lineTo(rx + r - lineWidth / 2, ry);
    // ctx.lineTo(
    //   rx + Math.SQRT1_2 * lineWidth * 2,
    //   ry - Math.SQRT1_2 * lineWidth * 2
    // );
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();

    // 绘制横线
    ctx.beginPath();
    ctx.moveTo(rx - r + lineWidth / 2, ry);
    ctx.lineTo(rx + r - lineWidth / 2, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }

  function defaultStatus() {
    //右上
    // 与半径圆的交点
    let intersectionX = rx + (Math.SQRT1_2 * w) / 2;
    let intersectionY = ry - (Math.SQRT1_2 * w) / 2;

    // 绘制斜线
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rx, ry);
    ctx.lineTo(x + w - lineWidth / 2, ry);
    ctx.strokeStyle = fillColor;
    if (Reverse === '1') {
      if (qdState === '1'&& State === '1') {
        if (CBTC_occupied === '1') {
          ctx.strokeStyle = '#FF0000';
        } else if (qdCBI_occupied === '1') {
          ctx.strokeStyle = '#804000';
        } else if (Invalid === '1') {
          ctx.strokeStyle = '#FF66CC';
        } else if (Locked === '1') {
          ctx.strokeStyle = '#00FF00';
        } else if (qdOverlap === '1') {
          ctx.strokeStyle = '#FFFF00';
        }
      }else{
        if(qdState !== '1'){
          ctx.strokeStyle = '#6B696B';
        }
      }
    }
    ctx.lineWidth = lineWidth;
    ctx.stroke();

    // 绘制固定横线
    ctx.beginPath();
    ctx.moveTo(x + lineWidth / 2, ry);
    ctx.lineTo(x + w / 2 - lineWidth / 2, ry);
    ctx.strokeStyle = fillColor;
    if (Normal === '1' || Reverse === '1') {
      if (qdState === '1'&& State === '1') {
        if (CBTC_occupied === '1') {
          ctx.strokeStyle = '#FF0000';
        } else if (qdCBI_occupied === '1') {
          ctx.strokeStyle = '#804000';
        } else if (Invalid === '1') {
          ctx.strokeStyle = '#FF66CC';
        } else if (Locked === '1') {
          ctx.strokeStyle = '#00FF00';
        } else if (qdOverlap === '1') {
          ctx.strokeStyle = '#FFFF00';
        }
      }else{
        if(qdState !== '1'){
          ctx.strokeStyle = '#6B696B';
        }
      }
    }
    ctx.lineWidth = lineWidth;
    ctx.stroke();

    // 绘制定位横线
    ctx.beginPath();
    ctx.moveTo(x + w / 2 + lineWidth / 2, ry);
    ctx.lineTo(x + w - lineWidth / 2, ry);
    ctx.strokeStyle = fillColor;
    if (Normal === '1') {
      if (qdState === '1'&& State === '1'){
        if (CBTC_occupied === '1') {
          ctx.strokeStyle = '#FF0000';
        } else if (qdCBI_occupied === '1') {
          ctx.strokeStyle = '#804000';
        } else if (Invalid === '1') {
          ctx.strokeStyle = '#FF66CC';
        } else if (Locked === '1') {
          ctx.strokeStyle = '#00FF00';
        } else if (qdOverlap === '1') {
          ctx.strokeStyle = '#FFFF00';
        }
      }else{
        if(qdState !== '1'){
          ctx.strokeStyle = '#6B696B';
        }
      }
    }
    ctx.lineWidth = lineWidth;
    ctx.stroke();
  }
}
export function daoChaDwon(ctx: CanvasRenderingContext2D, pen: any) {
  let x = pen.calculative.worldRect.x;
  let y = pen.calculative.worldRect.y;
  let w = pen.calculative.worldRect.width;
  let h = pen.calculative.worldRect.height;
  let r = w / 20; //圆半径
  let rx = x + w / 2; // 圆心
  let ry = y + h / 2; // 圆心
  let lineWidth = h / 4; //线条宽度
  const newR=w/35  //调小圆的直径
  let textValue = pen.status + '';
  const fillColor = pen.calculative.color ? pen.calculative.color : '#7B7D7B';
  // 初始化动画配置
  pen.frames = [];
  pen.animateCycle = 0;
  pen.animateType = '';
  pen.autoPlay = false;
  // 解构取值
  const {
    Dev_name = '',
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
    CBI_occupied: qdCBI_occupied,
    Overlap: qdOverlap,
    State: qdState,
  } = pen.statusObj2 ?? {};
  // 显示
  if (State === '1') {
    if (Normal === '1') {
      if (Blocked === '0' && Overlap === '0' && CBI_occupied === '0') {
        //1 正常-定位
        textValue = '1'; //赋值操作
      }
      if (Blocked === '1') {
        // 单锁-定位
        textValue = '3';
      }
      if (Overlap === '1' || CBI_occupied === '1') {
        //  进路锁闭-定位
        textValue = '5';
      }
      // if (CBI_occupied === '1') {
      //   // 占用-定位
      //   textValue = '7';
      // }
    } else if (Reverse === '1') {
      if (Blocked === '0' && Overlap === '0' && CBI_occupied === '0') {
        // 正常 - 反位;
        textValue = '2';
      }
      if (Blocked === '1') {
        // 单锁- 反位
        textValue = '4';
      }
      if (Overlap === '1' || CBI_occupied === '1') {
        //  进路锁闭-反位
        textValue = '6';
      }
      // if (CBI_occupied === '1') {
      //   // 占用-反位
      //   textValue = '8';
      // }
    } else if (Lost === '1') {
      //失表
      textValue = '9';
    }
  } else if (State !== undefined) {
    // 联锁中断
    textValue = '10';
  }

  ctx.save();
  defaultStatus();
  if (textValue === '1') {
    //1 正常-定位
    normal(greenColor);
  } else if (textValue === '2') {
    // 正常 - 反位;
    reverse(yellowColor);
  } else if (textValue === '3') {
    // 单锁-定位
    blocked(greenColor);
  } else if (textValue === '4') {
    // 单锁- 反位
    blockedReverse(yellowColor);
  } else if (textValue === '5') {
    //  进路锁闭-定位
    overlap(greenColor);
  } else if (textValue === '6') {
    //  进路锁闭-反位
    overlapReverse(yellowColor);
  }
  // else if (textValue === '7') {
  //   // 占用-定位
  //   CBI_occupiedFn(greenColor);
  // } else if (textValue === '8') {
  //   // 占用-反位
  //   CBI_occupiedReverse(yellowColor);
  // }
  else if (textValue === '9') {
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
    // 失表
    lossWatch(pen?.background || redColor);
  } else if (textValue === '10') {
    // 联锁中断
    interlockInterrupt(grayColor);
  } else {
    //基础形状
    defaultStatus();
  }
  ctx.font = `${r}px serif`;
  ctx.fillStyle = 'white';
  ctx.textAlign = 'center';
  // ctx.textBaseline = 'middle';
  ctx.fillText(Dev_name, rx, ry-newR*2);
  ctx.lineWidth = 1; //重置画板的ctx.lineWidth
  ctx.restore();

  //1 正常-定位
  function normal(color) {
    const deleteSatrtX = rx - r + lineWidth / 2;
    const deleteEndX = rx + r - lineWidth / 2;
    ctx.save();
    // 绘制岔心部分
    // 绘制横线
    ctx.beginPath();
    ctx.moveTo(deleteSatrtX, ry);
    ctx.lineTo(deleteEndX, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  //2 正常-反位
  function reverse(color) {
    const deleteSatrtX = rx - r;
    const deleteEndX = rx + r - lineWidth / 2;
    // 与半径圆的交点
    let intersectionX = rx - Math.SQRT1_2 * r;
    let intersectionY = ry + Math.SQRT1_2 * r;
    ctx.save();
    // ctx.clearRect(deleteSatrtX, intersectionY, deleteWidth, deleteHeight + 10);

    // 绘制岔心部分
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rx, ry);
    ctx.lineTo(deleteEndX, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  //3 单锁-定位
  function blocked(color) {
    const deleteSatrtX = rx - newR + lineWidth / 2;
    const deleteEndX = rx + newR - lineWidth / 2;

    ctx.save();
    // 绘制中间同心 圆圈
    ctx.fillStyle = color;
    ctx.beginPath();
    ctx.arc(
      rx,
      ry,
      Math.sqrt(Math.pow(newR, 2) + Math.pow(h / 2, 2)),
      0,
      Math.PI * 2
    );
    ctx.fill();
    ctx.globalCompositeOperation = 'destination-out';
    ctx.beginPath();
    ctx.arc(
      rx,
      ry,
      Math.sqrt(Math.pow(newR, 2) + Math.pow(h / 2, 2)) - lineWidth,
      0,
      Math.PI * 2
    );
    ctx.fill();
    ctx.restore();

    // 绘制岔心部分
    ctx.save();
    ctx.beginPath();
    ctx.moveTo(deleteSatrtX, ry);
    ctx.lineTo(deleteEndX, ry);
    ctx.strokeStyle = greenColor;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  //4 单锁-反位
  function blockedReverse(color) {
    const deleteSatrtX = rx - newR;
    const deleteEndX = rx + newR;
    // 与半径圆的交点
    let intersectionX = rx - Math.SQRT1_2 * newR;
    let intersectionY = ry + Math.SQRT1_2 * newR;

    ctx.save();
    // 绘制中间同心 圆圈
    ctx.fillStyle = color;
    ctx.beginPath();
    ctx.arc(
      rx,
      ry,
      Math.sqrt(Math.pow(newR, 2) + Math.pow(h / 2, 2)),
      0,
      Math.PI * 2
    );
    ctx.fill();
    ctx.globalCompositeOperation = 'destination-out';
    ctx.beginPath();
    ctx.arc(
      rx,
      ry,
      Math.sqrt(Math.pow(newR, 2) + Math.pow(h / 2, 2)) - lineWidth,
      0,
      Math.PI * 2
    );
    ctx.fill();
    ctx.restore();

    // 绘制岔心部分
    ctx.save();
    ctx.beginPath();
    ctx.moveTo(
      intersectionX + (Math.SQRT1_2 * lineWidth) / 2,
      intersectionY - (Math.SQRT1_2 * lineWidth) / 2
    );
    ctx.lineTo(rx, ry);
    ctx.lineTo(deleteEndX, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();

    ctx.restore();
  }
  //5 进路锁闭-定位
  function overlap(color) {
    const deleteSatrtX = rx - r;
    const deleteEndX = rx + r;
    // 绘制矩形
    // 与半径圆的交点
    let intersectionX = rx - Math.SQRT1_2 * r;
    let intersectionY = ry - Math.SQRT1_2 * r;

    ctx.save();
    ctx.fillStyle = grayColor;
    ctx.fillRect(
      deleteSatrtX,
      y + h / 4 + (lineWidth * 3) / 2,
      deleteEndX - deleteSatrtX,
      lineWidth
    );
    ctx.fillStyle = color;
    ctx.fillRect(
      deleteSatrtX,
      y + h / 4 + lineWidth / 2,
      deleteEndX - deleteSatrtX,
      lineWidth
    );
    ctx.restore();
    // ctx.fillRect()
  }
  //6 进路锁闭-反位
  function overlapReverse(color) {
    const deleteSatrtX = rx - r;
    const deleteEndX = rx + r;
    // 绘制矩形
    // 与半径圆的交点
    let intersectionX = rx - Math.SQRT1_2 * r;
    let intersectionY = ry + Math.SQRT1_2 * r;

    ctx.save();
    ctx.fillStyle = grayColor;
    ctx.fillRect(
      intersectionX - Math.SQRT1_2 * lineWidth,
      ry - lineWidth / 2,
      deleteEndX - (intersectionX - Math.SQRT1_2 * lineWidth),
      ry + lineWidth / 2 - (ry - Math.SQRT1_2 * r) + Math.SQRT1_2 * lineWidth
    );
    // 绘制折线
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rx, ry);
    ctx.lineTo(rx + r - lineWidth / 2, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
    // ctx.fillRect()
  }
  //7 占用-定位
  function CBI_occupiedFn(color) {
    const deleteSatrtX = rx - r + lineWidth / 2;
    const deleteEndX = rx + r - lineWidth / 2;
    ctx.save();
    // 绘制岔心部分
    // ctx.fillStyle = grayColor;
    // ctx.fillRect(x, y - lineWidth / 2, w, lineWidth);
    ctx.beginPath();
    ctx.moveTo(deleteSatrtX, ry + lineWidth);
    ctx.lineTo(deleteEndX, ry + lineWidth);
    ctx.strokeStyle = grayColor;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    // 绘制横线
    ctx.beginPath();
    ctx.moveTo(deleteSatrtX - r, ry);
    ctx.lineTo(deleteEndX + r, ry);
    ctx.strokeStyle = redColor;
    ctx.lineWidth = lineWidth;
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(deleteSatrtX, ry);
    ctx.lineTo(deleteEndX, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    // ctx.lineWidth = lineWidth;
    // ctx.stroke();
    ctx.restore();
  }
  //8 占用-反位
  function CBI_occupiedReverse(color) {
    const deleteSatrtX = rx - r + lineWidth / 2;
    const deleteEndX = rx + r - lineWidth / 2;
    // 与半径圆的交点
    let intersectionX = rx - Math.SQRT1_2 * r;
    let intersectionY = ry + Math.SQRT1_2 * r;
    ctx.save();
    // ctx.clearRect(deleteSatrtX, intersectionY, deleteWidth, deleteHeight + 10);

    ctx.beginPath();
    ctx.moveTo(
      intersectionX - Math.SQRT1_2 * r,
      intersectionY + Math.SQRT1_2 * r
    );
    ctx.lineTo(rx, ry);
    ctx.lineTo(deleteEndX + r, ry);
    ctx.strokeStyle = redColor;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.fillStyle = grayColor;
    ctx.fillRect(
      intersectionX - Math.SQRT1_2 * lineWidth,
      ry - lineWidth / 2,
      rx + r - (intersectionX - Math.SQRT1_2 * lineWidth),
      ry + lineWidth / 2 - (ry - Math.SQRT1_2 * r) + Math.SQRT1_2 * lineWidth
    );
    // 绘制岔心部分
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rx, ry);
    ctx.lineTo(deleteEndX, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  //9 失表 与圆焦点区域和清理区域坐标可传入（intersectionX，intersectionY）
  function lossWatch(color) {
    // 失表特制圆心焦点
    // 与半径圆的交点
    let intersectionX = rx - Math.SQRT1_2 * r;
    let intersectionY = ry + Math.SQRT1_2 * r;
    // 清理即将画图的矩形区域
    const deleteSatrtX = rx - r + lineWidth / 2;
    const deleteEndX = rx + r - lineWidth / 2;
    const deleteWidth = deleteEndX - deleteSatrtX; //宽
    const deleteHeight = ry - intersectionY; //intersectionY与底下线的距离 高

    ctx.save();
    // ctx.clearRect(deleteSatrtX, intersectionY, deleteWidth, deleteHeight + lineWidth);

    // 绘制岔心部分
    // 绘制横线
    ctx.beginPath();
    ctx.moveTo(rx, ry);
    ctx.lineTo(deleteEndX, ry);
    ctx.strokeStyle = redColor;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    // 绘制斜线
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rx, ry);
    ctx.lineTo(deleteSatrtX, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  //10 联锁中断
  function interlockInterrupt(color) {
    let intersectionX = rx - (Math.SQRT1_2 * r) / 2;
    let intersectionY = ry + (Math.SQRT1_2 * r) / 2;
    ctx.save();
    // 绘制斜线
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rx, ry);
    ctx.lineTo(rx + r - lineWidth / 2, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();

    // 绘制横线
    ctx.beginPath();
    ctx.moveTo(rx - r + lineWidth / 2, ry);
    ctx.lineTo(rx + r - lineWidth / 2, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
  }
  function defaultStatus() {
    // 与半径圆的交点
    let intersectionX = rx - (Math.SQRT1_2 * w) / 2;
    let intersectionY = ry + (Math.SQRT1_2 * w) / 2;
    // 绘制斜线
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rx, ry);
    ctx.lineTo(x + w - lineWidth / 2, ry);
    ctx.strokeStyle = fillColor;
    if (Reverse === '1') {
      if (qdState === '1'&& State === '1') {
        if (CBTC_occupied === '1') {
          ctx.strokeStyle = '#FF0000';
        } else if (qdCBI_occupied === '1') {
          ctx.strokeStyle = '#804000';
        } else if (Invalid === '1') {
          ctx.strokeStyle = '#FF66CC';
        } else if (Locked === '1') {
          ctx.strokeStyle = '#00FF00';
        } else if (qdOverlap === '1') {
          ctx.strokeStyle = '#FFFF00';
        }
      }else{
        if(qdState !== '1'){
          ctx.strokeStyle = '#6B696B';
        }
      }
    }
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    // 绘制定位横线
    ctx.beginPath();
    ctx.moveTo(x + lineWidth / 2, ry);
    ctx.lineTo(x + w / 2 - lineWidth / 2, ry);
    ctx.strokeStyle = fillColor;
    if (Normal === '1') {
      if (qdState === '1'&& State === '1'){
        if (CBTC_occupied === '1') {
          ctx.strokeStyle = '#FF0000';
        } else if (qdCBI_occupied === '1') {
          ctx.strokeStyle = '#804000';
        } else if (Invalid === '1') {
          ctx.strokeStyle = '#FF66CC';
        } else if (Locked === '1') {
          ctx.strokeStyle = '#00FF00';
        } else if (qdOverlap === '1') {
          ctx.strokeStyle = '#FFFF00';
         }
      }else{
        if(qdState !== '1'){
          ctx.strokeStyle = '#6B696B';
        }
      }
    }
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    // 绘制固定横线
    ctx.beginPath();
    ctx.moveTo(x + w / 2 + lineWidth / 2, ry);
    ctx.lineTo(x + w - lineWidth / 2, ry);
    ctx.strokeStyle = fillColor;
    if (Normal === '1' || Reverse === '1') {
      if (qdState === '1'&& State === '1') {
        if (CBTC_occupied === '1') {
          ctx.strokeStyle = '#FF0000';
        } else if (qdCBI_occupied === '1') {
          ctx.strokeStyle = '#804000';
        } else if (Invalid === '1') {
          ctx.strokeStyle = '#FF66CC';
        } else if (Locked === '1') {
          ctx.strokeStyle = '#00FF00';
        } else if (qdOverlap === '1') {
          ctx.strokeStyle = '#FFFF00';
        }
      }else{
        if(qdState !== '1'){
          ctx.strokeStyle = '#6B696B';
        }
      }
    }
    // ctx.strokeStyle = fillColor;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
  }
}
export function daoChaRightDwon(ctx: CanvasRenderingContext2D, pen: any) {
  let x = pen.calculative.worldRect.x;
  let y = pen.calculative.worldRect.y;
  let w = pen.calculative.worldRect.width;
  let h = pen.calculative.worldRect.height;
  let r = w / 20; //圆半径
  let rx = x + w / 2; // 圆心
  let ry = y + h / 2; // 圆心
  let lineWidth = h / 4; //线条宽度
  const newR=w/35  //调小圆的直径
  let textValue = pen.status + '';
  const fillColor = pen.calculative.color ? pen.calculative.color : '#7B7D7B';
  // 初始化动画配置
  pen.frames = [];
  pen.animateCycle = 0;
  pen.animateType = '';
  pen.autoPlay = false;
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

  const { CBTC_occupied, Invalid, Locked } = pen.statusObj2 ?? {};
  const qdCBI_occupied = pen.statusObj2?.CBI_occupied ?? {};
  const qdOverlap = pen.statusObj2?.Overlap ?? {};
  const qdState = pen.statusObj2?.State ?? {};
  // 显示
  if (State === '1') {
    if (Normal === '1') {
      if (Blocked === '0' && Overlap === '0' && CBI_occupied === '0') {
        //1 正常-定位
        textValue = '1'; //赋值操作
      }
      if (Blocked === '1') {
        // 单锁-定位
        textValue = '3';
      }
      if (Overlap === '1' || CBI_occupied === '1') {
        //  进路锁闭-定位
        textValue = '5';
      }
      // if (CBI_occupied === '1') {
      //   // 占用-定位
      //   textValue = '7';
      // }
    } else if (Reverse === '1') {
      if (Blocked === '0' && Overlap === '0' && CBI_occupied === '0') {
        // 正常 - 反位;
        textValue = '2';
      }
      if (Blocked === '1') {
        // 单锁- 反位
        textValue = '4';
      }
      if (Overlap === '1' || CBI_occupied === '1') {
        //  进路锁闭-反位
        textValue = '6';
      }
      // if (CBI_occupied === '1') {
      //   // 占用-反位
      //   textValue = '8';
      // }
    } else if (Lost === '1') {
      //失表
      textValue = '9';
    }
  } else if (State !== undefined) {
    // 联锁中断
    textValue = '10';
  }

  ctx.save();
  defaultStatus();
  if (textValue === '1') {
    //1 正常-定位
    normal(greenColor);
  } else if (textValue === '2') {
    // 正常 - 反位;
    reverse(yellowColor);
  } else if (textValue === '3') {
    // 单锁-定位
    blocked(greenColor);
  } else if (textValue === '4') {
    // 单锁- 反位
    blockedReverse(yellowColor);
  } else if (textValue === '5') {
    //  进路锁闭-定位
    overlap(greenColor);
  } else if (textValue === '6') {
    //  进路锁闭-反位
    overlapReverse(yellowColor);
  }
  // else if (textValue === '7') {
  //   // 占用-定位
  //   CBI_occupiedFn(greenColor);
  // } else if (textValue === '8') {
  //   // 占用-反位
  //   CBI_occupiedReverse(yellowColor);
  // }
  else if (textValue === '9') {
    // 失表
    lossWatch(pen?.background || redColor);
  } else if (textValue === '10') {
    // 联锁中断
    interlockInterrupt(grayColor);
  } else {
    //基础形状
    defaultStatus();
  }
  ctx.font = `${r}px serif`;
  ctx.fillStyle = 'white';
  ctx.textAlign = 'center';
  // ctx.textBaseline = 'middle';
  ctx.fillText(Dev_name, rx, ry-newR*2);
  ctx.lineWidth = 1; //重置画板的ctx.lineWidth
  ctx.restore();

  //1 正常-定位
  function normal(color) {
    const deleteSatrtX = rx - r + lineWidth / 2;
    const deleteEndX = rx + r - lineWidth / 2;
    ctx.save();
    // 绘制岔心部分
    // 绘制横线
    ctx.beginPath();
    ctx.moveTo(deleteSatrtX, ry);
    ctx.lineTo(deleteEndX, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  //2 正常-反位
  function reverse(color) {
    const deleteSatrtX = rx - r;
    const deleteEndX = rx - r + lineWidth / 2;
    // 与半径圆的交点
    let intersectionX = rx + Math.SQRT1_2 * r;
    let intersectionY = ry + Math.SQRT1_2 * r;
    ctx.save();
    // ctx.clearRect(deleteSatrtX, intersectionY, deleteWidth, deleteHeight + 10);

    // 绘制岔心部分
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rx, ry);
    ctx.lineTo(deleteEndX, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  //3 单锁-定位
  function blocked(color) {
    const deleteSatrtX = rx - newR + lineWidth / 2;
    const deleteEndX = rx + newR - lineWidth / 2;

    ctx.save();
    // 绘制中间同心 圆圈
    ctx.fillStyle = color;
    ctx.beginPath();
    ctx.arc(
      rx,
      ry,
      Math.sqrt(Math.pow(newR, 2) + Math.pow(h / 2, 2)),
      0,
      Math.PI * 2
    );
    ctx.fill();
    ctx.globalCompositeOperation = 'destination-out';
    ctx.beginPath();
    ctx.arc(
      rx,
      ry,
      Math.sqrt(Math.pow(newR, 2) + Math.pow(h / 2, 2)) - lineWidth,
      0,
      Math.PI * 2
    );
    ctx.fill();
    ctx.restore();

    // 绘制岔心部分
    ctx.save();
    ctx.beginPath();
    ctx.moveTo(deleteSatrtX, ry);
    ctx.lineTo(deleteEndX, ry);
    ctx.strokeStyle = greenColor;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  //4 单锁-反位
  function blockedReverse(color) {
    const deleteSatrtX = rx - newR;
    const deleteEndX = rx - newR;
    // 与半径圆的交点
    let intersectionX = rx + Math.SQRT1_2 * newR;
    let intersectionY = ry + Math.SQRT1_2 * newR;

    ctx.save();
    // 绘制中间同心 圆圈
    ctx.fillStyle = color;
    ctx.beginPath();
    ctx.arc(
      rx,
      ry,
      Math.sqrt(Math.pow(newR, 2) + Math.pow(h / 2, 2)),
      0,
      Math.PI * 2
    );
    ctx.fill();
    ctx.globalCompositeOperation = 'destination-out';
    ctx.beginPath();
    ctx.arc(
      rx,
      ry,
      Math.sqrt(Math.pow(newR, 2) + Math.pow(h / 2, 2)) - lineWidth,
      0,
      Math.PI * 2
    );
    ctx.fill();
    ctx.restore();

    // 绘制岔心部分
    ctx.save();
    ctx.beginPath();
    ctx.moveTo(
      intersectionX - (Math.SQRT1_2 * lineWidth) / 2,
      intersectionY - (Math.SQRT1_2 * lineWidth) / 2
    );
    ctx.lineTo(rx, ry);
    ctx.lineTo(deleteEndX, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();

    ctx.restore();
  }
  //5 进路锁闭-定位
  function overlap(color) {
    const deleteSatrtX = rx - r;
    const deleteEndX = rx + r;
    // 绘制矩形
    // 与半径圆的交点
    let intersectionX = rx - Math.SQRT1_2 * r;
    let intersectionY = ry - Math.SQRT1_2 * r;

    ctx.save();
    ctx.fillStyle = grayColor;
    ctx.fillRect(
      deleteSatrtX,
      y + h / 4 + (lineWidth * 3) / 2,
      deleteEndX - deleteSatrtX,
      lineWidth
    );
    ctx.fillStyle = color;
    ctx.fillRect(
      deleteSatrtX,
      y + h / 4 + lineWidth / 2,
      deleteEndX - deleteSatrtX,
      lineWidth
    );
    ctx.restore();
    // ctx.fillRect()
  }
  //6 进路锁闭-反位
  function overlapReverse(color) {
    const deleteSatrtX = rx - r;
    const deleteEndX = rx + r;
    // 绘制矩形
    // 与半径圆的交点
    let intersectionX = rx - Math.SQRT1_2 * r;
    let intersectionY = ry + Math.SQRT1_2 * r;
    let intersectionXRight = rx + Math.SQRT1_2 * r;
    ctx.save();
    ctx.fillStyle = grayColor;
    ctx.fillRect(
      rx - r,
      ry - lineWidth / 2,
      deleteEndX - (intersectionX - Math.SQRT1_2 * lineWidth),
      ry + lineWidth / 2 - (ry - Math.SQRT1_2 * r) + Math.SQRT1_2 * lineWidth
    );
    // 绘制折线
    ctx.beginPath();
    ctx.moveTo(intersectionXRight, intersectionY);
    ctx.lineTo(rx, ry);
    ctx.lineTo(rx - r + lineWidth / 2, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
    // ctx.fillRect()
  }
  //7 占用-定位
  function CBI_occupiedFn(color) {
    const deleteSatrtX = rx - r + lineWidth / 2;
    const deleteEndX = rx + r - lineWidth / 2;
    ctx.save();
    // 绘制岔心部分
    // ctx.fillStyle = grayColor;
    // ctx.fillRect(x, y - lineWidth / 2, w, lineWidth);
    ctx.beginPath();
    ctx.moveTo(deleteSatrtX, ry + lineWidth);
    ctx.lineTo(deleteEndX, ry + lineWidth);
    ctx.strokeStyle = grayColor;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    // 绘制横线
    ctx.beginPath();
    ctx.moveTo(deleteSatrtX - r, ry);
    ctx.lineTo(deleteEndX + r, ry);
    ctx.strokeStyle = redColor;
    ctx.lineWidth = lineWidth;
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(deleteSatrtX, ry);
    ctx.lineTo(deleteEndX, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    // ctx.lineWidth = lineWidth;
    // ctx.stroke();
    ctx.restore();
  }
  //8 占用-反位
  function CBI_occupiedReverse(color) {
    const deleteSatrtX = rx - r + lineWidth / 2;
    const deleteEndX = rx + r - lineWidth / 2;
    // 与半径圆的交点
    let intersectionX = rx + Math.SQRT1_2 * r;
    let intersectionY = ry + Math.SQRT1_2 * r;
    ctx.save();
    // ctx.clearRect(deleteSatrtX, intersectionY, deleteWidth, deleteHeight + 10);

    ctx.beginPath();
    ctx.moveTo(
      intersectionX + Math.SQRT1_2 * r,
      intersectionY + Math.SQRT1_2 * r
    );
    ctx.lineTo(rx, ry);
    ctx.lineTo(deleteSatrtX - r, ry);
    ctx.strokeStyle = redColor;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.fillStyle = grayColor;
    ctx.fillRect(
      rx - r,
      ry - lineWidth / 2,
      rx + r - (rx - Math.SQRT1_2 * r - Math.SQRT1_2 * lineWidth),
      ry + lineWidth / 2 - (ry - Math.SQRT1_2 * r) + Math.SQRT1_2 * lineWidth
    );
    // 绘制岔心部分
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rx, ry);
    ctx.lineTo(deleteSatrtX, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  //9 失表 与圆焦点区域和清理区域坐标可传入（intersectionX，intersectionY）
  function lossWatch(color) {
    // 失表特制圆心焦点
    // 与半径圆的交点
    let intersectionX = rx + Math.SQRT1_2 * r;
    let intersectionY = ry + Math.SQRT1_2 * r;
    // 清理即将画图的矩形区域
    const deleteSatrtX = rx - r + lineWidth / 2;
    const deleteEndX = rx + r - lineWidth / 2;
    const deleteWidth = deleteEndX - deleteSatrtX; //宽
    const deleteHeight = ry - intersectionY; //intersectionY与底下线的距离 高

    ctx.save();
    // ctx.clearRect(deleteSatrtX, intersectionY, deleteWidth, deleteHeight + lineWidth);

    // 绘制岔心部分
    // 绘制横线
    ctx.beginPath();
    ctx.moveTo(rx, ry);
    ctx.lineTo(deleteEndX, ry);
    ctx.strokeStyle = redColor;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    // 绘制斜线
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rx, ry);
    ctx.lineTo(deleteSatrtX, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    ctx.restore();
  }
  //10 联锁中断
  function interlockInterrupt(color) {
    let intersectionX = rx + (Math.SQRT1_2 * r) / 2;
    let intersectionY = ry + (Math.SQRT1_2 * r) / 2;
    ctx.save();
    // 绘制斜线
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rx, ry);
    ctx.lineTo(rx + r - lineWidth / 2, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    // 绘制横线
    ctx.beginPath();
    ctx.moveTo(rx - r + lineWidth / 2, ry);
    ctx.lineTo(rx + r - lineWidth / 2, ry);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();

    ctx.restore();
  }

  function defaultStatus() {
    // 与半径圆的交点
    let intersectionX = rx + (Math.SQRT1_2 * w) / 2;
    let intersectionY = ry + (Math.SQRT1_2 * w) / 2;
    // 绘制斜线
    ctx.beginPath();
    ctx.moveTo(intersectionX, intersectionY);
    ctx.lineTo(rx, ry);
    ctx.lineTo(x + w - lineWidth / 2, ry);
    ctx.strokeStyle = fillColor;
    if (Reverse === '1') {
      if (qdState === '1'&& State === '1') {
        if (CBTC_occupied === '1') {
          ctx.strokeStyle = '#FF0000';
        } else if (qdCBI_occupied === '1') {
          ctx.strokeStyle = '#804000';
        } else if (Invalid === '1') {
          ctx.strokeStyle = '#FF66CC';
        } else if (Locked === '1') {
          ctx.strokeStyle = '#00FF00';
        } else if (qdOverlap === '1') {
          ctx.strokeStyle = '#FFFF00';
        }
      }else{
        if(qdState !== '1'){
          ctx.strokeStyle = '#6B696B';
        }
      }
    }
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    // 绘制固定横线
    ctx.beginPath();
    ctx.moveTo(x + lineWidth / 2, ry);
    ctx.lineTo(x + w / 2 - lineWidth / 2, ry);
    ctx.strokeStyle = fillColor;
    if (Normal === '1' || Reverse === '1') {
      if (qdState === '1'&& State === '1') {
        if (CBTC_occupied === '1') {
          ctx.strokeStyle = '#FF0000';
        } else if (qdCBI_occupied === '1') {
          ctx.strokeStyle = '#804000';
        } else if (Invalid === '1') {
          ctx.strokeStyle = '#FF66CC';
        } else if (Locked === '1') {
          ctx.strokeStyle = '#00FF00';
        } else if (qdOverlap === '1') {
          ctx.strokeStyle = '#FFFF00';
        }
      }else{
        if(qdState !== '1'){
          ctx.strokeStyle = '#6B696B';
        }
      }
    }
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    // 绘制定位横线
    ctx.beginPath();
    ctx.moveTo(x + w / 2 + lineWidth / 2, ry);
    ctx.lineTo(x + w - lineWidth / 2, ry);
    ctx.strokeStyle = fillColor;
    if (Normal === '1') {
      if (qdState === '1'&& State === '1'){
        if (CBTC_occupied === '1') {
          ctx.strokeStyle = '#FF0000';
        } else if (qdCBI_occupied === '1') {
          ctx.strokeStyle = '#804000';
        } else if (Invalid === '1') {
          ctx.strokeStyle = '#FF66CC';
        } else if (Locked === '1') {
          ctx.strokeStyle = '#00FF00';
        } else if (qdOverlap === '1') {
          ctx.strokeStyle = '#FFFF00';
        }
      }else{
        if(qdState !== '1'){
          ctx.strokeStyle = '#6B696B';
        }
      }
    }
    ctx.lineWidth = lineWidth;
    ctx.stroke();
  }
}