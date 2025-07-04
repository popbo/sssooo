const greenColor = '#00ff00'; //绿色
const grayColor = '#6B696B'; //灰色
const grayColor2 = '#7B7D7B'; //灰色
const yellowColor = '#ffff00'; //黄色
const redColor = '#ff0000'; //红色
const whiteColor = '#ffffff';
const garyredColor = '#A52A2A';

export function zhanTaiMen(ctx: CanvasRenderingContext2D, pen: any) {
  const x = pen.calculative.worldRect.x;
  const y = pen.calculative.worldRect.y;
  const w = pen.calculative.worldRect.width;
  const h = pen.calculative.worldRect.height;
  const r = h / 2;
  const rx = x + r;
  const ry = y + h / 2;
  const fillColor = pen.calculative.color ? pen.calculative.color : '#ffffff';
  const gap = w / 30;
  let textValue = pen.status + '';
  // 解构取值
  const {
    Dev_name,
    PSD_close, //关闭且闭锁
    PSD_open, //打开
    PSD_cut, // 互锁解除
    State, // 与联锁中断
  } = pen?.statusObj ?? {};

  function drawStright(color) {
    ctx.beginPath();
    ctx.fillStyle = color;
    ctx.fillRect(x, y, w, h);
    ctx.closePath();
  }
  function drawDash(color) {
    ctx.clearRect(x, y - h / 2, w, 2 * h);
    ctx.beginPath();
    ctx.fillStyle = color;
    ctx.fillRect(x, y, w, h);
    ctx.closePath();
    ctx.beginPath();
    ctx.fillStyle = '#232630';
    for (var i = 0; i < 4; i++) {
      var Sx = w / 5 - 2 * gap + (w / 5 + gap / 2) * i;
      ctx.fillRect(x + Sx, y, gap * 2, h);
    }
    ctx.closePath();
  }
  if (PSD_close === '1') {
    textValue = '1';
  } else if (PSD_open === '1') {
    textValue = '2';
  } else if (PSD_cut === '1') {
    textValue = '3';
  } else if (State === '2') {
    textValue = '4';
  }
  if (textValue === '1') {
    // 关闭且锁闭
    drawStright(grayColor2);
  } else if (textValue === '2') {
    // 打开
    drawDash(greenColor);
  } else if (textValue === '3') {
    // 互相解除
    ctx.save();
    ctx.beginPath();
    ctx.fillStyle = yellowColor;
    ctx.fillRect(x, y, w, h);
    ctx.closePath();

    // 两个半圆
    ctx.clearRect(x, y, r, h);
    ctx.clearRect(x + w - r, y, r, h);
    ctx.beginPath();
    ctx.fillStyle = yellowColor;
    ctx.arc(rx, ry, r, 0, Math.PI * 2);
    ctx.arc(x + w - r, ry, r, 0, Math.PI * 2);
    ctx.fill();
    ctx.closePath();
    ctx.restore();
  } else if (textValue === '4') {
    // 联锁中断
    drawStright(grayColor);
  } else {
    // 绘制中间狭长矩形
    drawStright(fillColor);
  }
}
