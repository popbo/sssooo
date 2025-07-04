<template>
  <a-form-item
    class="FormItem"
    :labelCol="{ span: 10 }"
    :wrapperCol="{ span: 14 }"
    labelAlign="left"
    :colon="false"
  >
    <template #label>
      <span class="label" :title="formItem.name"
        >{{ formItem.name }}
        <a-tooltip v-if="formItem.tips">
          <template #title> <span v-html="formItem.tips"></span></template>
          <i class="t-icon t-help-circle"></i>
        </a-tooltip>
      </span>
    </template>
    <div v-if="formItem.type === 'label'">
      {{ value }}
    </div>
    <div v-else-if="formItem.type === 'text'" class="inputBtn">
      <a-input
        :placeholder="formItem.placeholder"
        :value="value"
        :title="value ? '' : formItem.placeholder"
        @change="onChanged($event.target.value)"
        @blur="onBlur($event.target.value)"
        :readonly="formItem.readonly"
      />
      <a-button
        v-if="formItem.name === '刷新时间'"
        size="small"
        @click="refresh"
      >
        <template #icon> <SyncOutlined /> </template>
      </a-button>
    </div>
    <a-input-number
      v-else-if="formItem.type === 'number'"
      :value="value"
      :placeholder="formItem.placeholder"
      :min="formItem.min"
      :max="formItem.max"
      :step="formItem.step"
      :title="value ? '' : formItem.placeholder"
      @change="onChanged"
      @blur="onBlur($event.target.value)"
      :readonly="formItem.readonly"
      :precision="formItem.precision"
    />
    <a-textarea
      v-else-if="formItem.type === 'textarea'"
      :value="value"
      :placeholder="formItem.placeholder"
      @change="onChanged($event.target.value)"
      @blur="onBlur($event.target.value)"
      :rows="formItem.rows"
      :readonly="formItem.readonly"
    />
    <a-select
      class="line"
      v-else-if="formItem.type == 'select'"
      :value="value"
      :placeholder="formItem.placeholder"
      :title="value ? '' : formItem.placeholder"
      @change="onChanged"
      :mode="formItem.mode"
      :showSearch="formItem.key==='bindSwitch'"
    >
      <a-select-option
        v-for="option in formItem.options"
        :key="option.value"
        :value="option.value"
        :disabled="option.disabled"
      >
        <div
          v-html="option.label"
          class="option"
        ></div> </a-select-option
    ></a-select>
    <a-switch
      v-else-if="formItem.type == 'switch'"
      :checked="value"
      @change="onChanged"
    />
    <div v-else-if="formItem.type == 'image'" @click="showImageDrawer">
      <!-- 值不存在，选图片按钮 -->
      <FormOutlined v-if="!value" />

      <!-- 值存在，展示该 img -->
      <template v-else>
        <img :src="String(value)" style="max-width: 50px; max-height: 50px" />
        <i
          class="t-icon t-close"
          @click.stop="onChanged('')"
          :title="t('清除图片')"
        ></i>
      </template>
    </div>
    <div v-else-if="formItem.type == 'icon'" @click="showIconDrawer">
      <template v-if="value">
        <i
          class="t-icon"
          :style="{
            fontSize: '16px',
            fontFamily:
              formItem.iconFamily && formItem.iconFamily + '!important',
          }"
        >
          {{ value }}
        </i>
        <i class="t-icon t-shanchu ml8" @click.stop="onChanged('')"></i>
      </template>
      <FormOutlined v-else />
    </div>
    <template v-else-if="formItem.type == 'code'">
      <a-button @click="showMonaco" size="small"> ··· </a-button>
    </template>
    <!-- TODO: 备选方案 -->
    <!-- <color-picker
      v-if="formItem.type === 'color'"
      :pureColor="value"
      pickerType="chrome"
      shape="circle"
      format='hex8'
      @update:pureColor="onChanged"
    /> -->
    <!-- active-change 赋值时也会触发一次  -->
    <!-- TODO: 官方 #4729 已经修复，待更新 npm 包  https://github.com/element-plus/element-plus/issues/4722 -->
    <el-color-picker
      v-else-if="formItem.type === 'color'"
      :model-value="colorValue===''?colorValue:value"
      size="mini"
      @change="onChanged($event, 'colorClear')"
      show-alpha
      color-format="hex"
      :predefine="predefineColors"
    />
    <!-- @active-change="onChanged" -->
    <a-slider
      v-else-if="formItem.type === 'slider'"
      :value="value"
      @change="onChanged"
      :min="formItem.min"
      :max="formItem.max"
      :step="formItem.step"
    />
    <a-auto-complete
      class="line"
      v-else-if="formItem.type === 'autoComplete'"
      :value="value"
      :placeholder="formItem.placeholder"
      @change="onChanged"
      @blur="onBlur($event.target.value)"
    >
      <template #options>
        <a-select-option
          v-for="option in formItem.options"
          :key="option.value"
          :value="option.value"
        >
          <div v-html="option.label" class="option"></div>
        </a-select-option>
      </template>
    </a-auto-complete>

    <!-- 增添密码框栏 -->
     <div v-else-if="formItem.type === 'password'" class="inputBtn">
      <a-input-password
        :placeholder="formItem.placeholder"
        :value="value"
        :title="value ? '' : formItem.placeholder"
        @change="onChanged($event.target.value)"
        @blur="onBlur($event.target.value)"
        :readonly="formItem.readonly"
      />
    </div>
  </a-form-item>
</template>

<script lang="ts">
import { defineComponent, PropType, ref, onMounted, watch } from 'vue';

import { Meta2d } from '@topometa2d/core';
import { FormOutlined, SyncOutlined } from '@ant-design/icons-vue';
import { FormItemType } from '../utils';
import Bus from './bus';
import { Col } from 'ant-design-vue/lib/grid';
import { colors as arrColor } from '@/assets/colors';
declare const meta2d: Meta2d;

export default defineComponent({
  name: 'FormItem',
  components: {
    FormOutlined,
    SyncOutlined,
  },
  props: {
    formItem: {
      type: Object as PropType<FormItemType>,
      require: true,
    },
    value: [String, Number, Boolean, Object, Array],
  },
  // props: ['formItem', 'value'],
  emits: [
    'update:value',
    'imageVisible',
    'iconVisible',
    'monacoVisible',
    'blur',
    'refreshHttp',
  ],
  setup(props, { emit }) {
    // 修改精度
    function onChanged(value, type?) {
      if (value === props.value) {
        return;
      }

      if (props.value != undefined || value != undefined) {
        let key = props.formItem.key;
        if (type === 'colorClear' && !value) {
          value = '#FFFFFF00';
        }
        if (
          ['x', 'y', 'width', 'height'].includes(key) &&
          meta2d.store.active[0][key] === value
        ) {
        } else {
          updateValue(value, 'update:value');
        }
      }
    }
    function onBlur(value) {
      if (!['x', 'y', 'width', 'height'].includes(props.formItem.key)) {
        updateValue(value, 'blur');
      }
    }

    function updateValue(value, emitKey) {
      // setValue 不接受 null 在此处改一下
      value === null && (value = undefined);
      if (props.formItem.type === 'number') {
        // 数字类型
        // - 符号，转换为 0
        value = value === '-' ? 0 : value;
      }
      emit(emitKey, value);
    }

    function showImageDrawer() {
      emit('imageVisible', [props.formItem.key, props.formItem.key2]);
    }

    function showIconDrawer() {
      emit('iconVisible', [props.formItem.key, props.formItem.key2]);
    }

    function showMonaco() {
      emit('monacoVisible', [props.formItem.key, props.formItem.key2]);
    }

    // 预置颜色
    const predefineColors = [
      '#FF7875',
      '#FF9C6E',
      '#FFC069',
      '#FFD666',
      '#FFF566',
      '#D3F261',
      '#95DE64',
      '#5CDBD3',
      '#69C0FF',
      '#85A5FF',
      '#B37FEB',
      '#FF85C0',
      '#232630',
      '#000000',
      '#FFFFFF',
      '#FFFFFF00',
    ];

    // 刷修按钮
    function refresh() {
      emit('refreshHttp', '刷 新');
    }

    // 颜色值变化同步
    watch(
      () => props.value,
      (newVal) => {
        if (props.formItem.type === 'color') {
          //如果类型是颜色
          arrColor.map((item) => {
            if (item.color === props.value) {
              onChanged(item.value + 'FF');
            }
          });
          if (props.value) {
            colorValue.value = props.value + '';
          } else {
            colorValue.value = ''
          }
        }
      }
    );
    // 解决当绑定颜色时如果
    const colorValue = ref('')
    onMounted(() => {
      if (props.formItem.type === 'color') {
        if(props.value){
          colorValue.value = props.value + '';
        }else{
          colorValue.value=''
        }
      }
    })
    return {
      onChanged,
      showImageDrawer,
      showIconDrawer,
      showMonaco,
      predefineColors,
      onBlur,
      refresh,
      colorValue
    };
  },
});
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.FormItem {
  margin-bottom: 0;
  margin: 4px 0;
  .label {
    font-size: 12px;
    font-family: PingFangSC, PingFangSC-Regular;
    font-weight: 400;
    color: #7f838c;
    line-height: 18px;
    letter-spacing: 0px;
  }

  .option {
    height: 100%;
    display: flex;
    align-items: center;
  }

  .pureText {
    padding: 0 11px;
    font-size: 12px;
  }
}

.inputBtn {
  display: flex;
  .ant-btn {
    margin-left: 10px;
  }
  .ant-input-affix-wrapper{
    // padding: 0;
    background-color: #191a24;
    height: 28px;
    border: 1px solid #393b4a !important;
  }
}
</style>

<style lang="scss">
.FormItem {
  &.ant-row.ant-form-item {
    .ant-form-item-label {
      padding-top: 0 !important;
    }

    .ant-input-number,
    .ant-input,
    .ant-select-selector,
    .ant-input-password {
      font-size: 12px;
      width: 100%;
      border: 1px solid transparent;
      box-shadow: none;

      &:hover {
        border: 1px solid #1890ff;
      }
      // 自动完成是外面一个嵌里面这个，所有要关掉它
      .ant-input {
        border: none;
      }
    }

    .ant-slider {
      min-width: inherit;
      margin-right: 15px;
    }
  }
}
</style>
