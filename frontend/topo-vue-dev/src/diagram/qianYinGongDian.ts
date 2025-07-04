const greenColor = '#00ff00';
const grayColor = '#7B7D7B';
const yellowColor = '#ff0';
const redColor = '#f00';
const blueColor = '#0000E1';
const whiteColor = '#fff';
const orangeColor = '#FFA600';
const snowGrey = '#E6F0E8';
//牵引供电
export function qianYinGongDian(ctx: CanvasRenderingContext2D, pen: any) {
  let x = pen.calculative.worldRect.x;
  let y = pen.calculative.worldRect.y;
  let w = pen.calculative.worldRect.width;
  let h = pen.calculative.worldRect.height;
  let textValue = pen.text + '';
  const fillColor = pen.calculative.color ? pen.calculative.color : '#7B7D7B';
  // 解构取值
  const {
    Dev_name,
    On, //状态
    State
  } = pen?.statusObj ?? {};
if(State==='1'){
  if (On === '1') {
    textValue = '1';
  }else{
    textValue = '2';
  }
}
  

  if (textValue === '1') {
    ctx.clearRect(x, y, w, h);
    return
  }else if(textValue === '2'){
    drawDefault('#00ff00')
  }else{
    drawDefault(fillColor)
  }
  function drawDefault(fillColor){
    ctx.save();
    ctx.lineWidth = 20;
    ctx.beginPath();
    ctx.rect(x, y + h / 2 - h / 22, w, h / 11);
    ctx.rect(x, y, h/11, h);
    ctx.rect(x + w - h / 11, y, h / 11, h);
    ctx.fillStyle = fillColor;
    ctx.fill();
    ctx.restore();
  }
  
}


