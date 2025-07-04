<template>
  <div class="dialogClass" ref="dialogBox">
    <a-modal
      :getContainer="()=>$refs.dialogBox"
      :visible="visible"
      :title="title"
      width="900px"
      @ok="handleOk"
      @cancel="cancel"
      wrapClassName="editorModal"
    >
      <div ref="monacoDom" class="monaco"></div>
    </a-modal>
  </div>
</template>

<script lang="ts">
import {
  defineComponent,
  nextTick,
  onMounted,
  onUnmounted,
  PropType,
  reactive,
  ref,
  watch
} from 'vue';

export default defineComponent({
  name: 'EditorModal',
  props: {
    visible: {
      type: Boolean,
      require: true
    },
    title: {
      type: String,
      default: () => {
        return 'JavaScript';
      }
    },
    code: {
      type: String,
      default: () => {
        return '';
      }
    },
    language: {
      type: String,
      default: () => {
        return 'javascript';
      },
      validator: (value: string) => {
        // 这个值必须匹配下列字符串中的一个
        return ['javascript', 'json', 'markdown'].includes(value);
      }
    }
  },
  emits: ['update:visible', 'changeCode'],
  setup(props, { emit }) {
    function handleOk() {
      // 按下确认以后修改外界值
      const code = editor.getValue();
      emit('changeCode', code);
      emit('update:visible', false);
    }

    function cancel() {
      emit('update:visible', false);
    }

    const curTheme = 'vs-dark'; // 暗主题
    const monacoDom = ref(null);

    let editor = null;

    watch(
      () => props.visible,
      newV => {
        if (newV) {
          nextTick(async () => {
          try {
            const { monaco } = await import('./customMonaco');
            if (!editor) {
              editor = monaco.editor.create(monacoDom.value, {
                theme: curTheme,
                automaticLayout: true,
                language: props.language
              });
            }
            // 可见状态
            editor.setValue(props.code);
            monaco.editor.setModelLanguage(editor.getModel(), props.language);
            // 格式化
            setTimeout(() => {
              editor.getAction(['editor.action.formatDocument'])._run();
            }, 300);
          } catch (error) {
            console.log(error,'error')
          }
          });
        }
      }
    );

    onUnmounted(() => {
      editor?.dispose();
    });

    return { handleOk, monacoDom, cancel };
  }
});
</script>

<style lang="scss">
@import '@/styles/variables.scss';

.editorModal {
  .ant-modal-content{
    background: #232630;
    .ant-modal-header{
      background: #232630;
        padding: 16px 0;
        margin: 0 24px;
        border-bottom:1px solid #363841;
        .ant-modal-title{
          color: #fff;
          font-size: 14px;
        }
    }
    .ant-modal-close-x{
      color:#fff
    }
    .ant-modal-body {
      padding: 0;
      .monaco {
        height: 400px;
      }
    }
    .ant-modal-footer{
      border-top:none;
      padding: 30px 16px;
      .ant-btn{
        background: #3d404c;
        border:1px solid #3d404c;
        color:#fff;
        &.ant-btn-primary{
          background: #1890ff;
          border-color: #1890ff;
        }
      }
    }
  }
  
}
</style>
