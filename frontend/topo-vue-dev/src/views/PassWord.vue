<template>
  <el-dialog
    :modelValue="dialogVisible"
    title="提示"
    width="30%"
    :before-close="closeHander"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    :show-close="false"
    custom-class="dialogModel"
    destroy-on-close
  >
    <span>
      <el-form
        ref="ruleFormRef"
        :model="ruleForm"
        :rules="rules"
        label-position="top"
        label-width="120px"
        class="demo-ruleForm"
        @submit.native.prevent
      >
        <el-form-item label="请输入密码" prop="input">
          <el-input v-model="ruleForm.input" clearable />
        </el-form-item>
        <el-form-item>
          <div class="dialog-footer">
            <el-button type="primary" @click="closeHander(ruleFormRef)">
              确定
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </span>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, watch, onUnmounted } from 'vue';
import type { FormInstance, FormRules } from 'element-plus';

const ruleForm = reactive({
  input: '',
});
const ruleFormRef = ref<InstanceType<typeof FormInstance>>();
const props = defineProps({
  dialogVisible: {
    type: Boolean,
    default: false,
  },
  password: {
    type: String,
  },
});
let $myemit = defineEmits(['update:dialogVisible']);

const closeHander = async (
  formEl: InstanceType<typeof FormInstance> | undefined
) => {
  if (!formEl) return;
  await formEl.validate((valid) => {
    if (valid) {
      $myemit('update:dialogVisible', false); //子传父，关闭弹窗
    } else {
      return false;
    }
  });
};

// 校验规则
const validatePass = (rule: any, value: any, callback: any) => {
  if (props.password === null) {
    $myemit('update:dialogVisible', false); //子传父，关闭弹窗
    return;
  }
  // 验证输入内容密码是否相同
  if (value !== props.password) {
    callback(new Error('密码不正确,请重新输入'));
  } else {
    callback();
  }
};
const rules = reactive<InstanceType<typeof FormRules>>({
  input: [
    { required: true,type:'string', message: '密码不正确,请重新输入', trigger: 'blur' },
    { validator: validatePass ,trigger: 'blur'},
  ],
});
// 重置验证
const resetForm = (formEl: InstanceType<typeof FormRules> | undefined) => {
  if (!formEl) return;
  formEl.resetFields();
};

let stopDialogVisible = null;
stopDialogVisible = watch(
  () => props.dialogVisible,
  (newVal) => {
    if (newVal) {
      ruleForm.input = '';
      resetForm(ruleFormRef.value);
    }
  }
);
onUnmounted(() => {
  stopDialogVisible();
});
</script>

<style lang="scss">
.dialogModel {
  background-color: #252630;
  .el-dialog__header .el-dialog__title {
    color: #9c9ea7;
  }
  .el-dialog__body {
    padding-top: 10px;
    .demo-ruleForm {
      .el-form-item__label {
        color: #b4b7c1;
        height: 36px;
      }
      .el-form-item__content {
        .el-input__inner {
          height: 36px;
          background-color: #181a24;
          border: 1px solid #2e303d;
          color: #f0f0f0;
        }
      }
    }
  }

  .dialog-footer {
    display: flex;
    flex-direction: row-reverse;
    /* margin-right: 30px; */
    .el-button{
      background-color: transparent;
      border: 1px solid #859094;
    }
  }
}
</style>
