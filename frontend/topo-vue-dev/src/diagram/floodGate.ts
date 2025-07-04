const greenColor = '#00ff00';
const grayColor = '#7B7D7B';
const yellowColor = '#ff0';
const redColor = '#f00';
const blueColor = '#0000E1';
const whiteColor = '#fff';

export function floodGate(ctx: CanvasRenderingContext2D, pen: any) {
  let textValue = pen.status + '';
  const fillColor = pen.calculative.color ? pen.calculative.color : '#f00';
  // 初始化动画配置
   pen.frames = [];
   pen.animateCycle = 0;
   pen.animateType = '';
   pen.autoPlay = false;
  // 解构取值
  const {
    Dev_name,
    Agree_close, //具备关闭条件
    Expect_close, //请求关闭
    Open_lock, //打开并闭锁
    State, //联锁中断
    Closed, //防淹门关闭
  } = pen?.statusObj ?? {};
  if (State === '1') {
    if (Agree_close === '1') {
      textValue = '1';
    } else if (Expect_close === '1') {
      textValue = '2';
    } else if (Open_lock === '1') {
      textValue = '3';
    } else if (Closed === '1') {
      textValue = '5';
    }
  } else if (State === '2') {
    textValue = '4';
  } else {
    textValue = '0';
  }

  if (textValue === '1') {
    // 具备关闭条件
    drawFangYanMeng(ctx, pen, blueColor);
  } else if (textValue === '2') {
    pen.frames = [
      {
        background: blueColor,
        bktype: 0,
        duration: 200,
        visible: false,
      },
      {
        background: blueColor,
        bktype: 0,
        duration: 200,
        visible: true,
      },
    ];
    pen.animateCycle = Infinity;
    pen.animateType = 'custom';
    pen.autoPlay = true;

    // 请求关闭(闪烁)
    drawFangYanMeng(ctx, pen, blueColor);
  } else if (textValue === '3') {
    // 打开并锁闭
    drawFangYanMeng(ctx, pen, whiteColor);
  } else if (textValue === '4') {
    // 联锁中断
    drawFangYanMeng(ctx, pen, grayColor);
  } else if (textValue === '5') {
    // 防淹门关闭
    drawFangYanMeng(ctx, pen, yellowColor);
  } else {
    //默认状态 打开锁闭信号丢失
    drawFangYanMeng(ctx, pen, fillColor);
  }
}

function drawFangYanMeng(ctx: CanvasRenderingContext2D, pen: any, color) {
  let x = pen.calculative.worldRect.x;
  let y = pen.calculative.worldRect.y;
  let w = pen.calculative.worldRect.width;
  let h = pen.calculative.worldRect.height;
  let radius = h / 4; // 水滴的半径
  let centerX = x + w / 2; // 水滴中心点的横坐标
  let centerY = y + (h * 7) / 8 - radius; // 水滴中心点的纵坐标

  ctx.save();
  ctx.beginPath();
  ctx.fillStyle = color; //填充颜色
  ctx.moveTo(centerX, centerY + radius); // 移动到起始点（底部）
  ctx.bezierCurveTo(
    centerX + radius + radius / 2,
    centerY + radius,
    centerX + radius + radius / 2,
    centerY - radius,
    centerX,
    centerY - radius * 2
  ); // 第一段贝塞尔曲线
  ctx.bezierCurveTo(
    centerX - radius - radius / 2,
    centerY - radius,
    centerX - radius - radius / 2,
    centerY + radius,
    centerX,
    centerY + radius
  ); // 第二段贝塞尔曲线
  ctx.closePath(); // 封闭路径
  ctx.fill(); // 填充颜色
  ctx.strokeStyle = color; // 设置雨滴边框颜色
  ctx.lineWidth = 2; // 设置矩形边框宽度
  ctx.stroke(); //描边水滴

  // 绘制左侧矩形
  ctx.strokeStyle = color; // 设置矩形边框颜色
  ctx.fillStyle = color; // 设置矩形填充颜色
  ctx.fillRect(x + w / 2 - w / 4 - w / 16, y, w *3/ 40, h); // 绘制矩形（只描边）
  // 绘制右侧矩形
  ctx.fillRect(x + w / 2 + w / 4, y, (w * 3) / 40, h); // 绘制矩形（只描边）
  ctx.stroke(); //描边水滴
  ctx.restore();
}
