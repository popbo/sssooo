import { FormItemType, TreeItem } from '@/components/utils';
import { Meta2d, Pen } from '@topometa2d/core';
import { nextTick, onUnmounted, ref, watchEffect,reactive } from 'vue';
import Bus from '../bus';
declare const meta2d: Meta2d;

export function useIframeDialog() {
  // `https://t.unittec.com/preview/?id=6360bb53da0962a797f1ab75`

  const openDialog = (e) => {
    const pen = e.pen;
    const params = e.params;
    iframeSrc.value=''
    if (params) {
      let frameUrl=''
      let queryIndex = params.indexOf('${');
      if (queryIndex == -1) {
        //不带参数
        frameUrl=params
      }else{
        //带参数时
        let urlQueryArr = params.split('?');
        if(urlQueryArr.length>1){
          let urlQuery= urlQueryArr[1];
          frameUrl=urlQueryArr[0]+'?'
          let queryNameArr = urlQuery.split('&');
          //带有多个参数时
          queryNameArr.forEach(x=>{
            let item=x.split('=')
            
            if(item[1].indexOf('${')>-1){
              let itemParm=item[1].slice(2,-1)
              if(pen[itemParm]){
                frameUrl+=item[0]+'='+pen[itemParm]+'&'
              }else{
                frameUrl+=item[0]+'='+item[1]+'&'
              }
            }else{
              frameUrl+=item[0]+'='+item[1]+'&'
            }
          })
          frameUrl=frameUrl.slice(0,-1)
        }
      }
      if (params.startsWith('http')) {
        iframeSrc.value = frameUrl;
      } else {
        iframeSrc.value = location.origin + frameUrl;
      }
    }
    visible.value = true;
    currentPen.value = pen;
    modalStyle={}
    for(let i=0;i<currentPen.value.events.length;i++){
      console.log('currentPen.value.events[i].modalStyle',currentPen.value.events[i])
      if(currentPen.value.events[i].modalStyle){
       
        modalStyle=currentPen.value.events[i].modalStyle
        break;
      }
    }
    Bus.$emit('modalStyleCustomFrame',modalStyle)
    // if(JSON.stringify(modalStyle)!=='{}'){
    //   Bus.$emit('modalStyleCustomFrame',modalStyle)
    // }
  };

  nextTick(() => {
    meta2d.on('iframe-dialog', openDialog);
    watchEffect(() => {
      if (!visible.value) {
        // TODO: 关闭对话框时，若点击遮罩层的位置与画笔位置相同
        // hover 已经被清除了，再点击无法弹出对话框
        // 必须要晃动鼠标才可以展示对话框
        meta2d.clearHover();
      }
    });
  });

  onUnmounted(() => {
    meta2d.off('iframe-dialog', openDialog);
  });

  const visible = ref(false);
  const currentPen = ref<Pen>({});
  const iframeSrc = ref('');
  let modalStyle=reactive({})
  return {
    visible,
    pen: currentPen as any as Pen & TreeItem & { form: FormItemType[] },
    iframeSrc,
    modalStyle,
    currentPen
  };
}
