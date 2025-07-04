import { Meta2d } from '@topometa2d/core';
declare const meta2d: Meta2d;
export function AxleCounterDefault(ctx: CanvasRenderingContext2D, pen: any) {
  const x = pen.calculative.worldRect.x;
  const y = pen.calculative.worldRect.y;
  const w = pen.calculative.worldRect.width;
  const h = pen.calculative.worldRect.height;
  const r = 10;
  const fillColor = pen.calculative.color ? pen.calculative.color : '#7B7D7B';
  const gap = w / 30;
  let textValue=pen.status+''
  const penType=pen.zoneType
  const {
    Dev_name,
    CBTC_occupied, //CBTC占用
    CBI_occupied, //非CBTC占用
    Invalid, // 计轴受扰
    Locked, //进路
    TSR, //临时限速
    Blocked, //封锁
    Overlap, // 保护区段闭锁
    State, // 与联锁中断,
  } = pen.statusObj2 ?? {};
  drawDefault(fillColor);
  const daochaId=pen.bindSwitch
  if(daochaId){
    const daochaPen=meta2d.store.data.pens.filter(el=>el?.statusObj?.Dev_name===daochaId)
    let daochaNormal='';
    let daochaReverse=''
    if(daochaPen.length>0&&(daochaPen[0] as any)?.statusObj){
      daochaNormal=daochaPen[0]['statusObj'].Normal
      daochaReverse=daochaPen[0]['statusObj'].Reverse
      // console.log('道岔的id',daochaNormal)
    }
    
    if(daochaNormal==='1'&&penType!=='antiSite'||daochaReverse==='1'&&penType!=='fixed'){
      changeStatus()
    }
  }else{
    changeStatus()
  }
  
  function drawDefault(fillColor) {
    ctx.beginPath();
    ctx.fillStyle = fillColor;
    ctx.fillRect(x, y+h/4, w, h/2);
    ctx.closePath();
  }
  function changeStatus(){
    if (State === '1') {
      if (CBTC_occupied === '1') {
        textValue = '1';
      } else if (CBI_occupied=== '1') {
        textValue = '2';
      } else if (Invalid === '1') {
        textValue = '3';
      } else if (Locked === '1') {
        textValue = '4';
      } else if (TSR === '1') {
        textValue = '5';
      } else if (Blocked === '1') {
        textValue = '6';
      } else if (Overlap === '1') {
        textValue = '7';
      }
    } else if (State === '2') {
      textValue = '8';
    } else {
      textValue = '0';
    }
    if(textValue==='1'){
      //CBTC占用
      drawDefault('#FF0000');
    }else if(textValue==='2'){
      //非CBTC占用
      drawDefault('#804000');
    }else if(textValue==='3'){
      // 计轴受扰
      drawDefault('#FF66CC');
    }else if(textValue==='4'){
      //进路
      drawDefault('#00FF00');
    }else if(textValue==='5'){
      //临时限速
      ctx.beginPath();
      ctx.fillStyle = '#7B7D7B';
      ctx.fillRect(x, y+h/4, w, h/2);
      ctx.closePath();
      ctx.beginPath();
      ctx.fillStyle = '#00A6FF';
      ctx.fillRect(x, y, w, 2);
      ctx.closePath();
      ctx.beginPath();
      ctx.fillStyle = '#00A6FF';
      ctx.fillRect(x, y+h-2, w, 2);
      ctx.closePath();
    }else if(textValue==='6'){
      //封锁
      ctx.beginPath();
      ctx.fillStyle = '#7B7D7B';
      ctx.fillRect(x, y+h/4, w, h/2);
      ctx.closePath();
      ctx.beginPath();
      ctx.fillStyle = '#FF00FF';
      ctx.fillRect(x, y, w, 2);
      ctx.closePath();
      ctx.beginPath();
      ctx.fillStyle = '#FF00FF';
      ctx.fillRect(x, y+h-2, w, 2);
      ctx.closePath();
    }else if(textValue==='7'){
      // 保护区段闭锁
      drawDefault('#ffff00');
    }else if(textValue==='8'){
      // 联锁中断
      drawDefault('#6B696B');
    }else{
      drawDefault(fillColor);
    }
  }
}
export function AxleCounterDefault2(ctx: CanvasRenderingContext2D, pen: any) {
  const x = pen.calculative.worldRect.x;
  const y = pen.calculative.worldRect.y;
  const w = pen.calculative.worldRect.width;
  const h = pen.calculative.worldRect.height;
  const r = 10;
  const fillColor = pen.calculative.color ? pen.calculative.color : '#7B7D7B';
  const gap = w / 30;
  let textValue=pen.status+''
  const penType=pen.zoneType
  // 解构取值
  const {
    Dev_name,
    CBTC_occupied, //CBTC占用
    CBI_occupied, //非CBTC占用
    Invalid, // 计轴受扰
    Locked, //进路
    TSR, //临时限速
    Blocked, //封锁
    Overlap, // 保护区段闭锁
    State, // 与联锁中断,
  } = pen.statusObj2 ?? {};
  drawDefault(fillColor);
  const daochaId=pen.bindSwitch
  if(daochaId){
    const daochaPen=meta2d.store.data.pens.filter(el=>el?.statusObj?.Dev_name===daochaId)
    let daochaNormal='';
    let daochaReverse=''
    if(daochaPen.length>0&&(daochaPen[0] as any)?.statusObj){
      daochaNormal=daochaPen[0]['statusObj'].Normal
      daochaReverse=daochaPen[0]['statusObj'].Reverse
    }
    if(daochaNormal==='1'&&penType!=='antiSite'||daochaReverse==='1'&&penType!=='fixed'){
      changeStatus()
    }
  }else{
    changeStatus()
  }
  
  function changeStatus(){
    if (State === '1') {
      if (CBTC_occupied === '1') {
        //CBTC占用
        textValue = '1';
      } else if (CBI_occupied === '1') {
        //非CBTC占用
        textValue = '2';
      } else if (Invalid === '1') {
        textValue = '3';
      } else if (Locked === '1') {
        textValue = '4';
      } else if (TSR === '1') {
        textValue = '5';
      } else if (Blocked === '1') {
        textValue = '6';
      } else if (Overlap === '1') {
        textValue = '7';
      }
    } else if (State === '2') {
      textValue = '8';
    } else {
      textValue = '0';
    }
  
    if(textValue==='1'){
      //CBTC占用
      drawDefault('#FF0000');
    }else if(textValue==='2'){
      //非CBTC占用
      drawDefault('#804000');
    }else if(textValue==='3'){
      // 计轴受扰
      drawDefault('#FF66CC');
    }else if(textValue==='4'){
      //进路
      drawDefault('#00FF00');
    }else if(textValue==='5'){
      //临时限速
      ctx.beginPath();
      ctx.fillStyle = '#7B7D7B';
      ctx.fillRect(x, y+h/4, w, h/2);
      ctx.closePath();
      ctx.beginPath();
      ctx.rect(x, y, 2, h);
      ctx.rect(x + w - 2, y, 2, h);
      ctx.fillStyle = '#7B7D7B';
      ctx.fill();
      ctx.closePath();
      ctx.beginPath();
      ctx.fillStyle = '#00A6FF';
      ctx.fillRect(x, y, w, 2);
      ctx.closePath();
      ctx.beginPath();
      ctx.fillStyle = '#00A6FF';
      ctx.fillRect(x, y+h-2, w, 2);
      ctx.closePath();
    }else if(textValue==='6'){
      //封锁
      ctx.beginPath();
      ctx.fillStyle = '#7B7D7B';
      ctx.fillRect(x, y+h/4, w, h/2);
      ctx.closePath();
      ctx.beginPath();
      ctx.rect(x, y, 2, h);
      ctx.rect(x + w - 2, y, 2, h);
      ctx.fillStyle = '#7B7D7B';
      ctx.fill();
      ctx.closePath();
      ctx.beginPath();
      ctx.fillStyle = '#FF00FF';
      ctx.fillRect(x, y, w, 2);
      ctx.closePath();
      ctx.beginPath();
      ctx.fillStyle = '#FF00FF';
      ctx.fillRect(x, y+h-2, w, 2);
      ctx.closePath();
    }else if(textValue==='7'){
      // 保护区段闭锁
      drawDefault('#ffff00');
    }else if(textValue==='8'){
      // 联锁中断
      drawDefault('#6B696B');
    }else{
      drawDefault(fillColor);
    }
  }
  function drawDefault(fillColor) {
    ctx.beginPath();
    ctx.fillStyle = fillColor;
    ctx.fillRect(x, y+h/4, w, h/2);
    ctx.closePath();
    ctx.save();
    ctx.beginPath();
    ctx.rect(x, y, 1/5*h, h);
    ctx.rect(x + w - 1/5*h, y, 1/5*h, h);
    ctx.fillStyle = fillColor;
    ctx.fill();
    ctx.restore();
  }
}