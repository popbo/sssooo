import { Meta2d } from '@topometa2d/core';
declare const meta2d: Meta2d;
export function trainDisplay(ctx: CanvasRenderingContext2D, pen: any) {
  const x = pen.calculative.worldRect.x;
  const y = pen.calculative.worldRect.y;
  const w = pen.calculative.worldRect.width;
  const h = pen.calculative.worldRect.height;
  const fillColor=pen.calculative.color?pen.calculative.color:'#ffffff'
  const contH=h*2/5
  let textValue=pen.status+''
  const {
    Stoped,
    Speed,
    Dir_up,
    Dir_down,
    Mode,
    CBTC_mode,
    EB_alarm,
    Holded,
    Skipstop,
    Dev_name,
  } = pen?.statusObj ?? {};
  let roadInfoArr=[]
  let roadInfo=[]
  if(pen.statusObj&&Dev_name){
    roadInfoArr=meta2d.store.data.pens.filter(el=>(el?.statusObj2?.Dev_name===Dev_name)||(el?.statusObj2UR?.Dev_name===Dev_name)||(el?.statusObj2DL?.Dev_name===Dev_name)||(el?.statusObj2DR?.Dev_name===Dev_name))
    if(roadInfoArr.length>1){
      roadInfo=roadInfoArr.filter(obj=>(obj.name==='daoCha')||(obj.name==='daoChaRight')||(obj.name==='daoChaDwon')||(obj.name==='daoChaRightDwon'))
    }else{
      roadInfo=roadInfoArr
    }
   
    if(roadInfo.length>0){
      const roadInfoGet= meta2d.getPenRect(roadInfo[0])
      const penInfoGet= meta2d.getPenRect(pen)
      let penX=0
      let penY=0
      let isCrossDaoCha=false
      if(roadInfo[0]?.form.length===8){
        isCrossDaoCha=roadInfo[0]?.form.some(obj => obj.key === 'statusObj2UR')
      }
      if(isCrossDaoCha){
        const QDCrossInfo=roadInfo[0]?.form.filter(el=>el.dataIds?.name===Dev_name)
        if(Dir_down==='1'){
          if(QDCrossInfo[0].key==='statusObj2'){
            penX=roadInfoGet.x+roadInfoGet.width/3-penInfoGet.width
            penY = roadInfoGet.y - (penInfoGet.height * 2) / 5;
          }else if(QDCrossInfo[0].key==='statusObj2UR'){
            penX=roadInfoGet.x+roadInfoGet.width-penInfoGet.width
            penY = roadInfoGet.y - (penInfoGet.height * 2) / 5;
          }else if(QDCrossInfo[0].key==='statusObj2DL'){
            penX=roadInfoGet.x+roadInfoGet.width/3-penInfoGet.width
            penY = roadInfoGet.y +roadInfoGet.height - penInfoGet.height/2;
          }else if(QDCrossInfo[0].key==='statusObj2DR'){
            penX=roadInfoGet.x+roadInfoGet.width-penInfoGet.width
            penY = roadInfoGet.y +roadInfoGet.height - penInfoGet.height/2;
          }
          
        }
        if(Dir_up==='1'){
          if(QDCrossInfo[0].key==='statusObj2'){
            penX=roadInfoGet.x+roadInfoGet.width/2-penInfoGet.width
            penY = roadInfoGet.y-roadInfoGet.height/2 + (penInfoGet.height * 2) / 5;
          }else if(QDCrossInfo[0].key==='statusObj2UR'){
            penX=roadInfoGet.x+roadInfoGet.width-penInfoGet.width
            penY = roadInfoGet.y-roadInfoGet.height/2 + (penInfoGet.height * 2) / 5;
          }else if(QDCrossInfo[0].key==='statusObj2DL'){
            penX=roadInfoGet.x+roadInfoGet.width/3-penInfoGet.width
            penY = roadInfoGet.y+roadInfoGet.height/2+penInfoGet.width/5
          }else if(QDCrossInfo[0].key==='statusObj2DR'){
            penX=roadInfoGet.x+roadInfoGet.width-penInfoGet.width
            penY = roadInfoGet.y+roadInfoGet.height/2+penInfoGet.width/5
          }
          
        }
      }else{
        penX=roadInfoGet.x+roadInfoGet.width-penInfoGet.width
        penY = roadInfoGet.y - (penInfoGet.height * 2) / 5;
      }
      if(Dir_down==='1'){
        if(CBTC_mode==='1'){
          if(Mode==='1'||Mode==='4'){
            textValue='1'
          }else if(Mode==='2'){
            textValue='2'
          }else if(Mode==='3'||Mode==='6'||Mode==='7'){
            textValue='3'
          }
          if(EB_alarm==='1'){
            textValue='5'
          }
          if(Holded==='1'){
            textValue='6'
          }
        }else{
          textValue='4'
        }
        pen.rotate=roadInfo[0].rotate
      }
      if(Dir_up==='1'){
        if(CBTC_mode==='1'){
          if(Mode==='1'||Mode==='4'){
            textValue='1'
          }else if(Mode==='2'){
            textValue='2'
          }else if(Mode==='3'||Mode==='6'||Mode==='7'){
            textValue='3'
          }
          if(EB_alarm==='1'){
            textValue='5'
          }
          if(Holded==='1'){
            textValue='6'
          }
        }else{
          textValue='4'
        }
        pen.rotate=Number(roadInfo[0].rotate)+180
      }
      meta2d.setValue({ id: pen.id,rotate:pen.rotate});
      meta2d.setPenRect(pen, { x: penX, y: penY, width: penInfoGet.width, height: penInfoGet.height });
    }
  }
  if(textValue==='1'){
    drawDefault('#00ff00')
  }else if(textValue==='2'){
    drawDefault('#00FFFF')
  }else if(textValue==='3'){
    drawDefault('#FFA600')
  }else if(textValue==='4'){
    drawDefault('#ffffff')
  }else if(textValue==='5'){
    drawDefault('#ff0000')
  }else if(textValue==='6'){
    drawDefault('#FFFF00')
  }else{
    drawDefault(fillColor)
  }
  function drawDefault(color1){
    ctx.beginPath();
    ctx.fillStyle=color1
    ctx.fillRect(x+w/4+2,y,w-w/4-2,contH);
  }
}