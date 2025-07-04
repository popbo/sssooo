import { FormItemType, TreeItem } from '@/components/utils';
import { Meta2d, Pen } from '@topometa2d/core';
import { nextTick, onUnmounted, ref, reactive,watchEffect } from 'vue';
import Bus from '../bus';
import http from '@/http';
declare const meta2d: Meta2d;
export function useLDialog() {
  const openDialog = ({ pen }: { pen: Pen }) => {
    currentPen.value = pen;
    visible.value = true;
    modalStyle={}
    for(let i=0;i<currentPen.value.events.length;i++){
      if(currentPen.value.events[i].modalStyle){
        modalStyle=currentPen.value.events[i].modalStyle
        break;
      }
      if (currentPen.value.events[i].action == 7) {
        url.value = currentPen.value.events[i].address;
      }
    }
    //展示示例弹框
    if (url.value !== '') {
      try {
        http({ url: url.value }).then((res) => {
          currentPen.value.description = (res as any).text;
        });
      } catch (error) {
        console.log(error);
      }
    }
    console.log('传递参数',currentPen.value.events,modalStyle)
    Bus.$emit('modalStyleCustom',modalStyle)
    // if(JSON.stringify(modalStyle)!=='{}'){
    //   Bus.$emit('modalStyleCustom',modalStyle)
    // }
  };

  nextTick(() => {
    meta2d.on('l-dialog', openDialog);
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
    console.log('卸载')
    meta2d.off('l-dialog', openDialog);
  });

  const visible = ref(false);
  const currentPen = ref<Pen>({});
  let modalStyle=reactive({})
  let url = ref('')
  return {
    visible,
    pen: currentPen as any as Pen & TreeItem & { form: FormItemType[] },
    modalStyle,
    currentPen
  };
}
