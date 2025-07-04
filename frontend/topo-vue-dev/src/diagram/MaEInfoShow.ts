import { Meta2d } from '@topometa2d/core';
declare const meta2d: Meta2d;
export function MaEInfoShow(ctx: CanvasRenderingContext2D, pen: any) {
  const x = pen.calculative.worldRect.x;
  const y = pen.calculative.worldRect.y;
  const w = pen.calculative.worldRect.width;
  const h = pen.calculative.worldRect.height;
  const fillColor=pen.calculative.color?pen.calculative.color:'#ffffff'
  const contH = (h * 2) / 5;
  let textValue = pen.status + '';
  const {
    Stoped,
    Dir_up,
    Dir_down,
    Mode,
    CBTC_mode,
    EB_alarm,
    Holded,
    Skipstop,
    Dev_name,
    Schedule,
    Otp_time,
    Speed,
    dataId
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
          if(Schedule==='1'){
            if(Otp_time>-120&&Otp_time<-60){
              textValue='3'
            }else if(Otp_time<-120){
              textValue='4'
            }else if(Otp_time>60&&Otp_time<120){
              textValue='5'
            }else if(Otp_time>=120){
              textValue='6'
            }else{
              textValue='2'
            }
          }else if(Schedule==='0'){
            textValue='7'
          }
          if(Skipstop==='1'){
            textValue='8'
          }
          if(Stoped==='1'||Speed==='0'){
            textValue='1'
          }
        }else{
          textValue='7'
        }
        pen.rotate=roadInfo[0].rotate
      }
      if(Dir_up==='1'){
        if(CBTC_mode==='1'){
          if(Schedule==='1'){
            if(Otp_time>-120&&Otp_time<-60){
              textValue='3'
            }else if(Otp_time<-120){
              textValue='4'
            }else if(Otp_time>60&&Otp_time<120){
              textValue='5'
            }else if(Otp_time>=120){
              textValue='6'
            }else{
              textValue='2'
            }
          }else if(Schedule==='0'){
            textValue='7'
          }
          if(Skipstop==='1'){
            textValue='8'
          }
          if(Stoped==='1'||Speed==='0'){
            textValue='1'
          }
        }else{
          textValue='7'
        }
        pen.rotate=Number(roadInfo[0].rotate)+180
      }
      meta2d.setValue({ id: pen.id,rotate:pen.rotate});
      meta2d.setPenRect(pen, { x: penX, y: penY, width: penInfoGet.width, height: penInfoGet.height });
    }
  }

    if(textValue==='1'){
      ctx.beginPath();
      ctx.fillStyle='#00ff00'
      ctx.fillRect(x,y,w/4,contH);
      ctx.closePath()
    }else if(textValue==='2'){
      drawDefault('#00ff00')
    }else if(textValue==='3'){
      drawDefault('#0000FF')
    }else if(textValue==='4'){
      drawDefault('#00FFFF')
    }else if(textValue==='5'){
      drawDefault('#ffff00')
    }else if(textValue==='6'){
      drawDefault('#ff0000')
    }else if(textValue==='7'){
      drawDefault('#ffffff')
    }else if(textValue==='8'){
      drawDefault1('#00ff00')
    }else{
      drawDefault(fillColor)
    }
  function drawDefault(color1){
    ctx.save();
    ctx.beginPath();
      ctx.moveTo(x,y+contH/2); // 第一个点
      ctx.lineTo(x+w/4, y); // 第二个点
      ctx.lineTo(x+w/4, y+contH); // 第三个点
      ctx.fillStyle = color1;
      ctx.fill();
      ctx.closePath();
    ctx.restore();
  }
    function drawDefault1(color1) {
      ctx.save();
      ctx.beginPath();
      ctx.moveTo(x, y + contH / 2); // 第一个点
      ctx.lineTo(x + w / 4, y); // 第二个点
      ctx.lineTo(x + w / 4, y + contH); // 第三个点
      ctx.fillStyle = color1;
      ctx.fill();
      ctx.closePath();

      // 设置文字样式
      ctx.font = `bold  ${contH}px serif`;
      ctx.fillStyle = '#EA4BF7';
      const textWidth = ctx.measureText('S').width;
      ctx.fillText('S', x + w - textWidth, y - contH/2);
      ctx.restore();
  }
}