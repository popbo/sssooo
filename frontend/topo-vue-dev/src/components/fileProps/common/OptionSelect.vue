<template>
  <a-select
    v-model:value="inputValue"
    mode="multiple"
    show-search
    v-bind="$attrs"
    placeholder="请选择"
    :maxTagCount="0"
    allow-clear
    :style="{ width: props.width ? props.width : '200px' }"
    :filterOption="filterOption"
    optionFilterProp="label"
    :autoClearSearchValue="false"
    @search="handleSearch"
    @blur="handleBlur"
  >
    <template #dropdownRender="{ menuNode: menu }">
      <div
        style="padding: 4px 8px; cursor: pointer"
        @mousedown="(e) => e.preventDefault()"
      >
        <!-- 三个状态 -->
        <a-checkbox
          :checked="checkedAll"
          :indeterminate="indeterminate"
          style="margin: 0 0 0 11px"
          @change="checkboxChange"
        ></a-checkbox>
        <a-button
          type="link"
          style="margin: 0; padding: 0 0 0 5px; color: #a5a8b2"
          @click="allSelectedFun"
          v-if="!checkedAll && !indeterminate"
          >全选</a-button
        >
        <a-button
          type="link"
          @click="clearFun"
          v-else
          style="margin: 0; padding: 0 0 0 5px; color: #a5a8b2"
          >全选</a-button
        >
        <a-divider style="margin: 0" />
        <v-nodes :vnodes="menu" />
      </div>
    </template>
      <a-select-option
        :value="item.value"
        :label="item.label"
        class="custom-select"
        v-for="item in filterOption3"
        :key="`${item.value}-${item.label}`"
      >
        <a-checkbox :checked="inputValue.includes(item.value)"></a-checkbox>
        {{ item.label }}
      </a-select-option>
  </a-select>
</template>
<script lang="ts">
import { defineComponent } from 'vue';
export default defineComponent({
  components: {
    VNodes: (_, { attrs }) => {
      return attrs.vnodes;
    },
  },
});
</script>
<script lang="ts" setup>
import { ref, watch, computed, onUnmounted } from 'vue';
const props = defineProps<{
  options: any; //传入的列表，仅支持value label格式
  value: any; //外面v-model绑定的值   必须为v-model:value="绑定的值（根据需求自己定义）"
  width?: string; //设置a-select宽度
}>();

// 全选checkbox状态 比较
const checkedAll = computed(() => {
  if (
    inputValue.value.length === 0 ||
    inputValue.value.length !== props.options.length
  )
    return false;
  return true;
});
const indeterminate = computed(() => {
  if (
    inputValue.value.length > 0 &&
    inputValue.value.length < props.options.length
  )
    return true;
  return false;
});

const searchValue = ref('');
// 监听文本框输入
const inputValue = ref(props.value);

//将传入的v-model值赋值给当前a-select
let stopPropsValue = watch(
  () => props.value,
  (newValue) => {
    inputValue.value = newValue;
  }
);

// 搜索内容
const handleSearch = (value) => {
  searchValue.value = value; //缓存搜索内容
};
// 全选
const allSelectedFun = () => {
  //获取搜索的内容，根据搜索内容去全选
  if (searchValue.value) {
    const filteredItems = props.options.filter((item) =>
      filterOption2(searchValue.value, item)
    );
    filteredItems.forEach((item) => {
      let index = inputValue.value.indexOf(item.value);
      if (index == -1) {
        inputValue.value.push(item.value);
      }
    });
  } else {
    props.options.forEach((item) => {
      let index = inputValue.value.indexOf(item.value);
      if (index == -1) {
        inputValue.value.push(item.value);
      }
    });
  }
};
// 清空
const clearFun = () => {
  //使用splice是为了可以监听到变化
  inputValue.value.splice(0, inputValue.value.length);
};
// 模糊匹配
function filterOption(input, option) {
  return `${option.label}`.toLowerCase().includes(`${input || ''}`.toLowerCase());
}
// 定向筛选label
const filterOption2 = (input, option) =>
  option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;

  // 连续列表
const filterOption3 =computed (()=>{
 return props.options.filter((item) => {
  return `${item.label}`.toLowerCase().includes(`${searchValue.value || ''}`.toLowerCase());
  });
})

const handleBlur=()=>{
  //失焦时清空搜索值
  searchValue.value=''
}
  // checkbox和全选功能连动
const checkboxChange=(e) => {
  if(checkedAll.value===false&&indeterminate.value ===true) return clearFun();
  if (e.target.checked) {
    // 如果复选框被选中，则执行全选功能
    allSelectedFun();
  } else {
    // 如果复选框未被选中，则执行清除功能
    clearFun();
  }
};
onUnmounted(() => {
  stopPropsValue();
});
</script>

<style lang="scss" scoped>
// 去除选项右侧对勾
:deep(.ant-select-item-option-state) {
  display: none;
}
//  ant-checkbox-wrapper
.ant-checkbox-wrapper {
  padding-right: 5px;
}
</style>
