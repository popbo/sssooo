import { Meta2d } from '@topometa2d/core';
declare const meta2d: Meta2d;

export function lineSignal(ctx: CanvasRenderingContext2D, pen: any) {
  const x = pen.calculative.worldRect.x;
  const y = pen.calculative.worldRect.y;
  const w = pen.calculative.worldRect.width;
  const h = pen.calculative.worldRect.height;
  const fillColor = pen.calculative.color ? pen.calculative.color : '#ffffff';
  const circleW = (w / 20) * 19;
  const lineW = w / 20;
  const r = circleW / 4;
  const rx = x + r * 3; //圆心的x坐标
  const ry = y + h / 2; //圆心的y坐标
  const xLineWR = r - lineW / 2; //斜线的半径，因为斜线的宽导致半径变长
  const startX1 = rx - xLineWR * Math.cos(Math.PI / 4);
  const startY1 = ry - xLineWR * Math.sin(Math.PI / 4);
  const endX1 = rx + xLineWR * Math.cos(Math.PI / 4);
  const endY1 = ry + xLineWR * Math.sin(Math.PI / 4);

  const startX2 = rx - xLineWR * Math.cos((Math.PI * 3) / 4);
  const startY2 = ry + xLineWR * Math.sin((Math.PI * 3) / 4);
  const endX2 = rx + xLineWR * Math.cos((Math.PI * 3) / 4);
  const endY2 = ry - xLineWR * Math.sin((Math.PI * 3) / 4);

  const startX3 = rx - xLineWR * Math.cos((Math.PI * 5) / 4);
  const startY3 = ry + xLineWR * Math.sin((Math.PI * 5) / 4);
  const endX3 = rx + xLineWR * Math.cos((Math.PI * 5) / 4);
  const endY3 = ry - xLineWR * Math.sin((Math.PI * 5) / 4);

  const startX4 = rx - xLineWR * Math.cos((Math.PI * 7) / 4);
  const startY4 = ry - xLineWR * Math.sin((Math.PI * 7) / 4);
  const endX4 = rx + xLineWR * Math.cos((Math.PI * 7) / 4);
  const endY4 = ry + xLineWR * Math.sin((Math.PI * 7) / 4);
  let textValue=pen.status+''
  // 解构取值
  const {
    Dev_name,
    Green_open,
    Yellow_open,
    Red_open,
    Callon,
    Fleet_mode,
    Extinguish,
    State,
  } = pen?.statusObj ?? {};
  // 临时判断系统-思路先判断
  if(State==='1'){
  if (Callon==='1') {
    textValue = '7';
  } else if (Fleet_mode === '1') {
    textValue = '8';
  } else if (Green_open === '1') {
    textValue = '1';
    if (Extinguish === '1') {
      textValue = '4';
    }
    
  } else if (Red_open === '1') {
    textValue = '2';
    if (Extinguish === '1') {
      textValue='5';
    }
  } else if (Yellow_open === '1') {
    textValue = '3';
    if (Extinguish === '1') {
      textValue = '6';
    } 
  }
  }else{
    textValue='0'//默认状态
  }
  if(textValue==='1'){
    drawDefault('#00ff00');
  }else if(textValue==='2'){
    drawDefault('#FF0000');
  }else if(textValue==='3'){
    drawDefault('#FFFF00');
  }else if(textValue==='4'){
    defaultDrawCBTC('#00ff00');
  }else if(textValue==='5'){
    defaultDrawCBTC('#FF0000');
  }else if(textValue==='6'){
    defaultDrawCBTC('#FFFF00');
  }else if(textValue==='7'){
     //绘制红色的圆      
     ctx.beginPath();
     ctx.arc(x+circleW/4,y+h/2,circleW/4,0,2*Math.PI);
     ctx.fillStyle='#ffFF00'
     ctx.fill();
     ctx.closePath()
     //绘制黄色的圆 
     ctx.beginPath();
     ctx.arc(x+circleW/4*3,y+h/2,circleW/4,0,2*Math.PI);
     ctx.fillStyle='#FF0000'
     ctx.fill();
     ctx.closePath()
     ctx.beginPath();
     ctx.fillStyle = '#7b7d7b';
     ctx.fillRect(x+circleW,y+h/2-circleW/4,lineW,circleW/2);
     ctx.closePath()
  }else if(textValue==='8'){
    // 根据上行信号机
    // 自动通过进路
    // 绘制箭头
    ctx.save();
    ctx.beginPath();
    ctx.moveTo(x + circleW / 2 + lineW, y + h / 2);
    ctx.lineTo(x + (circleW / 3) * 2 + lineW, y + (h * 5) / 6);
    ctx.lineTo(x + (circleW / 3) * 2 + lineW, y + h / 6);
    ctx.fillStyle = '#00ff00';
    ctx.fill();
    ctx.closePath();
    // 绘制箭身
    ctx.beginPath();
    ctx.fillStyle = '#00ff00';
    ctx.fillRect(x + (circleW / 3) * 2, y + h / 3, circleW / 3, h / 3);
    ctx.closePath();

    // 绘制圆形
    ctx.beginPath();
    ctx.arc(
      x + (circleW / 4) * 3 - circleW / 2,
      y + circleW / 4,
      circleW / 4,
      0,
      2 * Math.PI
    );
    ctx.lineWidth = ((circleW / 2) * 1) / 25; // 设置线条宽度为1
    ctx.strokeStyle = '#f1f1f1'; // 设置边框颜色为灰色
    ctx.fillStyle = '#808080';
    ctx.fill();
    ctx.stroke();
    ctx.closePath();
    // 中间竖线
    ctx.beginPath();
    ctx.fillStyle = '#7b7d7b';
    ctx.fillRect(x + circleW - circleW / 2, y, lineW, circleW / 2);
    ctx.closePath();
    ctx.restore();
  } else if (textValue === '9') {
    // 根据下行信号机
    // 自动通过进路
    // 绘制箭头
    ctx.save();
    ctx.beginPath();
    ctx.moveTo(x + circleW / 2 - lineW, y + h / 2);
    ctx.lineTo(x + (circleW / 3) - lineW, y + (h * 5) / 6);
    ctx.lineTo(x + (circleW / 3) - lineW, y + h / 6);
    ctx.fillStyle = '#00ff00';
    ctx.fill();
    ctx.closePath();
    // 绘制箭身
    ctx.beginPath();
    ctx.fillStyle = '#00ff00';
    ctx.fillRect(x, y + h / 3, circleW / 3, h / 3);
    ctx.closePath();

    // 绘制圆形
    ctx.beginPath();
    ctx.arc(
      x + (circleW / 4) * 3,
      y + circleW / 4,
      circleW / 4,
      0,
      2 * Math.PI
    );
    ctx.lineWidth = ((circleW / 2) * 1) / 25; // 设置线条宽度为1
    ctx.strokeStyle = '#f1f1f1'; // 设置边框颜色为灰色
    ctx.fillStyle = '#808080';
    ctx.fill();
    ctx.stroke();
    ctx.closePath();
    // 中间竖线
    ctx.beginPath();
    ctx.fillStyle = '#7b7d7b';
    ctx.fillRect(
      x + circleW / 2 - lineW,
      y,
      lineW,
      circleW / 2
    );
    ctx.closePath();
    ctx.restore();
  } else {
    ctx.save();
    ctx.beginPath();
    ctx.arc(
      x +w/2+ (circleW / 4) * 3 - circleW / 2,
      y + circleW / 4,
      circleW / 4,
      0,
      2 * Math.PI
    );
    ctx.lineWidth = ((circleW / 2) * 1) / 25; // 设置线条宽度为1
    ctx.strokeStyle = '#f1f1f1'; // 设置边框颜色为灰色
    ctx.fillStyle = '#808080';
    ctx.fill();
    ctx.stroke();
    ctx.closePath();
    ctx.beginPath();
    ctx.fillStyle = '#7b7d7b';
    ctx.fillRect(x + +w/2+circleW - circleW / 2, y, lineW, circleW / 2);
    ctx.closePath();
    ctx.restore();
  }
  function drawDefault(fillColor) {
    ctx.beginPath();
    ctx.arc(x + (circleW / 4) * 3, y + h / 2, circleW / 4, 0, 2 * Math.PI);
    ctx.fillStyle = fillColor;
    ctx.fill();
    ctx.closePath();
    ctx.beginPath();
    ctx.fillStyle = '#7b7d7b';
    ctx.fillRect(x + circleW, y + h / 2 - circleW / 4, lineW, circleW / 2);
    ctx.closePath();
  }
  function defaultDrawCBTC(fillColor) {
    ctx.beginPath();
    ctx.arc(x + r * 3, ry, r, 0, 2 * Math.PI);
    ctx.fillStyle = fillColor;
    ctx.fill();
    ctx.closePath();
    ctx.beginPath();
    ctx.fillStyle = '#7b7d7b';
    ctx.fillRect(x + circleW, y + h / 2 - circleW / 4, lineW, circleW / 2);
    ctx.closePath();
    ctx.save();
    ctx.beginPath();
    ctx.moveTo(startX1, startY1);
    ctx.lineTo(endX1, endY1);
    ctx.moveTo(startX2, startY2);
    ctx.lineTo(endX2, endY2);
    ctx.moveTo(startX3, startY3);
    ctx.lineTo(endX3, endY3);
    ctx.moveTo(startX4, startY4);
    ctx.lineTo(endX4, endY4);
    ctx.lineWidth = lineW;
    ctx.strokeStyle = '#232630';
    ctx.stroke();
    ctx.closePath();
    ctx.restore();
  }
}
