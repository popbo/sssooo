<template>
  <div class="dialogClass" ref="dialogBox">
    <a-modal
      :getContainer="() => $refs.dialogBox"
      :visible="visible"
      width="700px"
      :bodyStyle="{ height: '590px', overflowY: 'scroll' }"
      @cancel="cancel"
      @ok="handleOk"
    >
      <template #title>更多参数</template>
      <div ref="monacoDom" class="monaco"></div>
    </a-modal>
    </div>
  </template>
  <script lang="ts">
import { defineComponent,watch,ref,nextTick} from 'vue';
    export default defineComponent({
      name: 'MoreParamModal',

      props: {
        visible: {
          type: Boolean,
          require: true,
        },
        moreCustonStyle:{
          type: String,
          require: true,
        }
      },
      emits: ['update:visible','changeCode'],
      setup(props, { emit }) {
        function cancel(){
          emit('update:visible', false);
        }
        function handleOk(){
          const code = editor.getValue();
          emit('changeCode', code);
          cancel()
        }
        const curTheme = 'vs-dark'; // 暗主题
        const monacoDom = ref(null);
        let editor = null;
        let code=ref()
        const language='css'
        watch(
          () => props.visible,
          newV => {
            if (newV) {
              nextTick(async () => {
                const { monaco } = await import('../customMonaco');
                if (!editor) {
                  editor = monaco.editor.create(monacoDom.value, {
                    theme: curTheme,
                    automaticLayout: true,
                    language: language
                  });
                }
                code.value=props.moreCustonStyle
                // 可见状态
                editor.setValue(code.value);
                monaco.editor.setModelLanguage(editor.getModel(), language);
                // 格式化
                setTimeout(() => {
                  editor.getAction(['editor.action.formatDocument'])._run();
                }, 300);
              });
            }
          }
        );
        return {
          cancel,
          handleOk,
          monacoDom,
          code
        }
      }
    })
  </script>
<style lang="scss">
.dialogClass{
  .ant-modal-body {
    .monaco {
      height: calc(100% - 30px);
    }
  }
}
</style>