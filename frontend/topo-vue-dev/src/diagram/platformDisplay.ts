const greenColor = '#00ff00';
const grayColor = '#7B7D7B';
const grayColor2 = '#6B696B';
const yellowColor = '#ff0';
const redColor = '#f00';
const blueColor = '#0000E1';
const whiteColor = '#fff';
const orangeColor = '#FFA600';
const bgColor = '#232630';
const hrefParam = window.location.pathname;
console.log('hrefParam', hrefParam);

export function platformDisplay(ctx: CanvasRenderingContext2D, pen: any) {
  const x = pen.calculative.worldRect.x;
  const y = pen.calculative.worldRect.y;
  const w = pen.calculative.worldRect.width;
  const h = pen.calculative.worldRect.height;
  const lineW = (h * 5) / 88; //线条宽度
  const lineL= (w * 165) / 249; //线条长度

  const fillColor = pen.calculative.color ? pen.calculative.color : '#ffffff'; //初始图元颜色
  let textValue = pen.status + '';
  let textValueEmergstop = pen.statusEmergstop + ''; //紧急停车
  let textValueSkipStop = pen.statusSkipStop + ''; //跳停
  let textValueDetainCar = pen.statusDetainCar + ''; //扣车
  let textValueZhantaimeng = pen.statusZhantaimeng + ''; //站台门
  // 解构取值
  const {
    Dev_name,
    PSD_close, //车门关闭且闭锁
    Trainberth, //在站台时车门打开
    State, // 联锁中断
    Emergstop, //紧急停车
    Up_train_skipstop, //跳停
    Down_train_skipstop, //跳停
    Down_skipstop, //跳停
    Up_skipstop, //跳停 这四个任意一个都表示跳停
    Up_OCC_hold, //中心扣车
    Down_OCC_hold, //中心扣车
    Up_hold, //车站扣车
    Down_hold, //车站扣车
    // PSD_close, //关闭且闭锁
    PSD_open, //打开
    PSD_cut, // 互锁解除
    // State, // 与联锁中断
  } = pen?.statusObj ?? {};
  //初始图元绘制开始
  //drawDefault(fillColor);
  //初始图元绘制结束

  // 站台
  if (State === '1') {
    if (PSD_close === '1') {
      textValue = '1';
    } else if (Trainberth === '1') {
      textValue = '2';
    }
  } else if (State === '2') {
    textValue = '3';
  }else{
    textValue = '0';
  }

  if (textValue === '1') {
    drawDefault('#7B7D7B');
  } else if (textValue === '2') {
    drawDefault('#00FF00');
  } else if (textValue === '3') {
    drawDefault('#6B696B');
  } else {
    drawDefault('#7B7D7B');
  }

  function drawDefault(fillColor) {
    ctx.save();
    ctx.beginPath();
    ctx.strokeStyle = fillColor;
    ctx.lineWidth = lineW;
    ctx.strokeRect(
      x + (w * 42) / 249,
      y + (h * 44) / 88,
      (w * 165) / 249,
      (h * 25) / 88
    );
    ctx.closePath();
    ctx.restore();
  }

  // 紧急停车
  if(State==='1'){
  if (Emergstop === '1') {
    textValueEmergstop = '1';
  }
  }else{
    textValueEmergstop = '0';
  }
  if (textValueEmergstop === '1') {
    drawEmergencyStop(ctx, 'E', redColor, whiteColor);
  } else {
    if (!(hrefParam.includes('preview') || hrefParam.includes('release'))) {
      drawEmergencyStop(ctx, 'E', fillColor, bgColor);
    }
  }
  function drawEmergencyStop(
    ctx: CanvasRenderingContext2D,
    str,
    color1,
    color2
  ) {
    let r = (h * 15) / 88; //半径
    let rx = x +w/2; // 圆心
    let ry = y + r; // 圆心
    ctx.save();
    ctx.beginPath();
    ctx.moveTo(rx - r, ry); //左
    ctx.lineTo(rx, ry + r); //下
    ctx.lineTo(rx + r, ry); //右
    ctx.lineTo(rx, ry - r); //上
    ctx.lineTo(rx - r, ry); //左
    ctx.closePath();
    ctx.fillStyle = color1;
    ctx.fill();

    // 设置文字样式
    ctx.font = `bold  ${(3 / 2) * r}px serif`;
    ctx.fillStyle = color2;
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';

    // 裁剪出文字形状
    ctx.clip();
    ctx.fillText(str, rx, ry);
    ctx.restore();
  }

  // 跳停
  if(State==='1'){
  if (
    Up_train_skipstop === '1' ||
    Down_train_skipstop === '1' ||
    Down_skipstop === '1' ||
    Up_skipstop === '1'
  ) {
    textValueSkipStop = '1';
  }
  }else{
    textValueSkipStop = '0';
  }
  if (textValueSkipStop === '1') {
    drawSkipStop(ctx, 'S', yellowColor, whiteColor);
  } else {
    // 显示默认
    if (!(hrefParam.includes('preview') || hrefParam.includes('release'))) {
      drawSkipStop(ctx, 'S', fillColor, bgColor);
    }
  }
  function drawSkipStop(ctx: CanvasRenderingContext2D, str, color1, color2) {
    let r = (w * 15) / 249; //半径
    let rx = x + (w * 234) / 249; // 圆心
    let ry = y + (h * (30 + 14 + 25 / 2)) / 88; // 圆心
    ctx.save();
    ctx.beginPath();
    ctx.arc(rx, ry, r, 0, 2 * Math.PI);
    ctx.fillStyle = color1;
    ctx.fill();

    // 设置文字样式
    ctx.font = `bold  ${(3 / 2) * r}px serif`;
    ctx.fillStyle = color2;
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';

    // 裁剪出文字形状
    ctx.clip();
    ctx.fillText(str, rx, ry);
    ctx.restore();
  }

  // 扣车
  if(State==='1'){
     if (
    (Up_OCC_hold === '1' || Down_OCC_hold === '1') &&
    (Up_hold === '1' || Down_hold === '1')
  ) {
    textValueDetainCar = '3';
  } else if (Up_OCC_hold === '1' || Down_OCC_hold === '1') {
    textValueDetainCar = '1';
  } else if (Up_hold === '1' || Down_hold === '1') {
    textValueDetainCar = '2';
  }
  }else{
    textValueZhantaimeng = '0';
  }
 

  if (textValueDetainCar === '1') {
    // 中心扣车
    drawDetainCar(ctx, 'H', whiteColor, greenColor);
  } else if (textValueDetainCar === '2') {
    // 车站扣车
    drawDetainCar(ctx, 'H', orangeColor, greenColor);
  } else if (textValueDetainCar === '3') {
    // 中心和车站同时扣车
    drawDetainCar(ctx, 'H', redColor, greenColor);
  } else {
    if (!(hrefParam.includes('preview') || hrefParam.includes('release'))) {
      // 显示默认
      drawDetainCar(ctx, 'H', bgColor, fillColor);
    }
  }
  function drawDetainCar(ctx: CanvasRenderingContext2D, str, color, color1) {
    let r = (w * 15) / 249; //半径
    let rx = x + (w * 15) / 249; // 圆心
    let ry = y + (h * (30 + 14 + 25 / 2)) / 88; // 圆心
    ctx.save();
    ctx.beginPath();
    ctx.arc(rx, ry, r, 0, 2 * Math.PI);
    ctx.fillStyle = color1;
    ctx.fill();

    // 设置文字样式
    ctx.font = `bold  ${(3 / 2) * r}px serif`;
    ctx.fillStyle = color;
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    // 裁剪出文字形状
    ctx.clip();
    ctx.fillText(str, rx, ry);
    ctx.restore();
  }

  // 站台门
  if (State === '1') {
    if (PSD_close === '1') {
      textValueZhantaimeng = '1';
    } else if (PSD_open === '1') {
      textValueZhantaimeng = '2';
    } else if (PSD_cut === '1') {
      textValueZhantaimeng = '3';
    }
  } else if (State === '2') {
    textValueZhantaimeng = '4';
  }else{
    textValueZhantaimeng='0'
  }

  if (textValueZhantaimeng === '1') {
    // 关闭且锁闭
    drawStright(grayColor);
  } else if (textValueZhantaimeng === '2') {
    // 打开
    drawDash(greenColor);
  } else if (textValueZhantaimeng === '3') {
    // 互相解除
    drawRoundString(yellowColor);
  } else if (textValueZhantaimeng === '4') {
    // 联锁中断
    drawStright(grayColor2);
  } else {
    // 绘制中间狭长矩形
    drawStright('#7B7D7B');
  }
  function drawStright(color) {
    ctx.beginPath();
    ctx.fillStyle = color;
    ctx.fillRect(x + (w * 42) / 249, y + (h * 83) / 88, lineL, lineW);
    ctx.closePath();
  }
  function drawDash(color) {
    let rw = lineL;
    const gap = rw / 30;
    ctx.clearRect(x + (w * 42) / 249, y + (h * 83) / 88, lineL, lineW);
    ctx.beginPath();
    ctx.fillStyle = color;
    ctx.fillRect(x + (w * 42) / 249, y + (h * 83) / 88, lineL, lineW);
    ctx.closePath();
    ctx.beginPath();
    ctx.fillStyle = '#232630';
    for (var i = 0; i < 5; i++) {
      var Sx = rw / 5 - 2 * gap + (rw / 5 + gap / 2) * i;
      ctx.fillRect(x + Sx, y + (h * 83) / 88, gap * 2, lineW);
    }
    ctx.closePath();
  }
  function drawRoundString(color) {
    const r = lineW / 2;
    const rx = x + (w * 42) / 249;
    const ry = y + (h * 85.5) / 88;
    const gap = w / 30;
    ctx.save();
    ctx.beginPath();
    ctx.fillStyle = yellowColor;
    ctx.fillRect(x + (w * 42) / 249, y + (h * 83) / 88, lineL, lineW);
    ctx.closePath();

    // 两个半圆
    // ctx.clearRect(rx, ry, r, 2 * r);
    // ctx.clearRect(rx + (w * 5) / 7, ry, r, 2 * r);
    ctx.beginPath();
    ctx.fillStyle = yellowColor;
    ctx.arc(rx, ry, r, 0, Math.PI * 2);
    ctx.arc(rx + lineL, ry, r, 0, Math.PI * 2);
    ctx.fill();
    ctx.closePath();
    ctx.restore();
  }
}
