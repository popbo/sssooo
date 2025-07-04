<template>
  <div>
    <template
      v-for="(config, index) in configs"
      :key="config.key + config.key2"
    >
      <div class="form-item" style="display: flex; align-items: center">
        <FormItem
          v-if="config.key2"
          v-model:value="obj[config.key][config.key2]"
          :formItem="config"
          @imageVisible="showImage"
          @iconVisible="showIcon"
          @monacoVisible="showMonaco"
          @update:value="updateValue($event, [config.key, config.key2])"
          @blur="onBlur($event, [config.key, config.key2])"
        />
        <FormItem
          v-else
          v-model:value="obj[config.key]"
          :formItem="config"
          @imageVisible="showImage"
          @iconVisible="showIcon"
          @monacoVisible="showMonaco"
          @update:value="updateValue($event, [config.key, config.key2])"
          @blur="onBlur($event, [config.key, config.key2])"
          @refreshHttp="relayRefreshHttp"
        />
        <!-- 加编辑按钮 删除按钮 -->
        <template v-if="hasEdit">
          <!-- 绑定图标 -->
          <i
            class="hover delIcon t-icon t-lianjie--"
            @click="
              showBindModal(config.key, index, config);
              getdata(config);
            "
          ></i>
          <!-- 编辑图标 -->
          <i
            class="hover delIcon t-icon t-bianji--"
            @click="editFormItem(config.key, index)"
          ></i>
          <a-popconfirm
            placement="topRight"
            title="确定删除吗？"
            ok-text="是"
            cancel-text="否"
            @confirm="delFormItem(config.key, index)"
            :getPopupContainer="
              (triggerNode) => {
                return triggerNode.parentNode;
              }
            "
          >
            <!-- 删除图标 -->
            <i class="hover delIcon t-icon t-shanchu-- delete"></i>
          </a-popconfirm>
        </template>
      </div>
      <div class="bind-dataId" v-if="hasEdit && config.dataIds">
        <p
          v-if="config.multiple"
          :title="(config.dataIds as any[]).map((item)=>item.dataId).join(',')"
        >
          {{ (config.dataIds as any[]).map((item) => item.dataId).join(',') }}
        </p>

        <p v-else :title="(config.dataIds as any).dataId">
          {{ (config.dataIds as any).dataId }}
        </p>
        <a-popconfirm
          placement="topRight"
          title="确定取消绑定？"
          ok-text="是"
          cancel-text="否"
          @confirm="delBindData(config.key, index)"
        >
          <i
            class="hover delIcon t-icon t-shanchu-- delete"
            style="color: #fff"
          ></i>
        </a-popconfirm>
      </div>
    </template>
    <ImageDrawer
      v-if="existImage"
      v-model:visible="imageVisible"
      @chooseImage="chooseImage"
    />
    <IconDrawer
      v-if="existIcon"
      v-model:visible="iconVisible"
      @chooseIcon="chooseIcon"
    />

    <EditorModal
      v-if="existEditor"
      v-model:visible="monacoVisible"
      :code="currentCode"
      :title="currentConfig.title"
      :language="currentConfig.language"
      @changeCode="changeCode"
    />

    <BindModal
      v-if="hasEdit"
      v-model:visible="bindVisible"
      :config="bindConfig"
      :dataId="dataId"
      :data-ids="dataIds"
      :pen="obj"
      @choose-id="selectId($event, obj)"
    />
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, PropType, ref, onMounted } from 'vue';
import FormItem from '../common/FormItem.vue';
import ImageDrawer from '../common/ImageDrawer.vue';
import IconDrawer from '../common/IconDrawer.vue';
import EditorModal from '../common/EditorModal.vue';
import { FormItemType } from '../utils';
import { BindId, deepClone, Meta2d } from '@topometa2d/core';
import BindModal from './BindModal/BindModal.vue';
import { useBindModal } from './formEvery';
import { useStore } from 'vuex';
import Bus from '@/components/common/bus';

declare const meta2d: Meta2d;

export default defineComponent({
  name: 'FormEvery',
  components: {
    FormItem,
    ImageDrawer,
    IconDrawer,
    EditorModal,
    BindModal,
  },
  props: {
    configs: Array as PropType<FormItemType[]>,
    obj: Object,
    hasEdit: Boolean,
    pen:Object,
  },
  // 该 emit 向父组件发一个消息，表明某个值已经修改
  emits: [
    'changeValue',
    'blur',
    'delFormItem',
    'editFormItem',
    'delBindData',
    'refreshHttp',
  ],
  setup(props, { emit }) {
    const imageVisible = ref(false);

    let currentKey = ''; // 当前若选择 image/icon 类型的 key 打开 图片选择面板
    let currentKey2 = ''; // key2 存在时使用
    function showImage(keys?: string[]) {
      imageVisible.value = true;
      currentKey = keys[0];
      currentKey2 = keys[1];
    }

    function chooseImage(image: string) {
      if (currentKey2) {
        props.obj[currentKey][currentKey2] = image;
      } else if (currentKey) {
        props.obj[currentKey] = image;
      }
      updateValue(image, [currentKey, currentKey2]);
    }

    const iconVisible = ref(false);
    function showIcon(keys?: string[]) {
      iconVisible.value = true;
      currentKey = keys[0];
      currentKey2 = keys[1];
    }

    function chooseIcon(item: any) {
      if (currentKey2) {
        props.obj[currentKey][currentKey2] = item.data.icon;
      } else if (currentKey) {
        props.obj[currentKey] = item.data.icon;
      }
      // iconFamily 也传出
      updateValue(item.data, [currentKey, currentKey2]);
      // 更改该值用以显示图标
      props.configs.find((item) => item.key === currentKey).iconFamily =
        item.data.iconFamily;
    }

    const monacoVisible = ref(false);
    let currentConfig = ref<Partial<FormItemType>>({});
    const currentCode = ref('');
    function showMonaco(keys?: string[]) {
      currentKey = keys[0];
      currentKey2 = keys[1];
      if (currentKey2) {
        currentCode.value = props.obj[currentKey][currentKey2];
      } else if (currentKey) {
        currentCode.value = props.obj[currentKey];
         //当打开事件的js时--->展示设备相关信息
        // 获取这个图元绑定的所有设备
        if (props.pen?.form?.length > 0) {
          let arr = []
          props.pen.form.forEach(item => {
            // 判断设备是否存在不存在加入
            if (!arr.includes(item.dataIds?.dataId)) {
              arr.push(item.dataIds?.dataId)
            }
          })
          const regex = /\/\/<[^>]*>/g
          // 判断是否有dataId有就加没有就不加,加之前删除之前加的
          let str = `//<` + arr.join('|') + '>\n'//生成字符串
          // 删除前一次加入的str
          currentCode.value = (str + currentCode.value?.replace(regex, '').trim())||''
          // console.log('==>',regex1.test(e.value),e.value,e.value.replace(regex1,null));
        }
      }

      currentConfig.value =
        props.configs.find(
          (config) => config.key === currentKey && config.key2 === currentKey2
        ) ?? {};

      if (currentConfig.value.isNotString && currentConfig.value.language==='json') {//8.14增加同时language为json时才去转换
        // TODO: currentCode 中若存在 calculative
        currentCode.value = JSON.stringify(deepClone(currentCode.value));
      }
      monacoVisible.value = true;
    }

    function changeCode(code: string) {
      if (currentConfig.value.isNotString && currentConfig.value.language === 'json') {//8.14增加同时language为json时才去转换
        try {
          code = JSON.parse(code);
        } catch (error) {
          console.log('无法转换成 js 对象', error);
          return; // return 不修改外界值
        }
      }
      if (currentKey2) {
        props.obj[currentKey][currentKey2] = code;
      } else if (currentKey) {
        props.obj[currentKey] = code;
      }
      updateValue(code, [currentKey, currentKey2]);
    }

    function updateValue(value: any, keys: string[]) {
      // emit 发送消息，参数建议单个对象
      emit('changeValue', { value, keys });
      meta2d && meta2d.emit('components-update-value', true);
    }

    // 减少 ImageDrawer IconDrawer EditorModal 等渲染
    const existImage = computed(() => {
      return props.configs.some((config) => config.type === 'image');
    });
    const existIcon = computed(() => {
      return props.configs.some((config) => config.type === 'icon');
    });
    const existEditor = computed(() => {
      return props.configs.some((config) => config.type === 'code');
    });

    function onBlur(value: any, keys: string[]) {
      emit('blur', { value, keys });
    }

    function delFormItem(key: string, index: number) {
      emit('delFormItem', { key, index });
    }

    function delBindData(key: string, index: number) {
      emit('delBindData', { key, index });
    }

    function editFormItem(key: string, index: number) {
      emit('editFormItem', { key, index });
    }
    function relayRefreshHttp(val: string) {
      if (val === '刷 新') {
        emit('refreshHttp', {
          name: props.obj.name,
          time: props.obj.refreshTime,
        });
      }
    }

    // 发送axios请求 获取设备数据
    const store = useStore();
    async function getdata(obj) {
      // 通知模态框
      Bus.$emit('changeBindValue', obj); //这个项的配置项->点击的时候通知模态框,改变赋值
    }

    const {
      visible: bindVisible,
      showModal: showBindModal,
      config: bindConfig,
      dataId,
      selectId,
      dataIds,
    } = useBindModal();

    return {
      imageVisible,
      showImage,
      chooseImage,
      showIcon,
      iconVisible,
      chooseIcon,
      monacoVisible,
      showMonaco,
      currentKey,
      currentConfig,
      changeCode,
      currentCode,
      updateValue,
      existImage,
      existIcon,
      existEditor,
      onBlur,
      delFormItem,
      editFormItem,
      bindVisible,
      showBindModal,
      bindConfig,
      dataId,
      selectId,
      dataIds,
      delBindData,
      relayRefreshHttp,
      getdata,
    };
  },
});
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.form-item {
  display: flex;
  align-items: center;
  .ant-form-item {
    color: #b4b7c1;
  }

  > :first-child {
    flex: 1;
    max-width: 100%;
  }

  > .delIcon {
    margin-left: 8px;
    visibility: hidden;
    color: #fff;
  }

  &:hover {
    > .delIcon {
      visibility: visible;
    }
  }
}

.bind-dataId {
  margin-left: 60px;
  font-size: 12px;
  color: #7f838c;
  position: relative;
  p {
    width: 100px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    // margin-bottom: 0px;
    display: inline-block;
    height: 22px;
    line-height: 30px;
    margin-top: -8px;
  }

  > .delIcon {
    margin-left: 54px;
    position: absolute;
    top: -4px;
    visibility: hidden;
  }

  &:hover {
    > .delIcon {
      visibility: visible;
    }
  }
}
:deep(.ant-popover-content) {
  .ant-popover-inner {
    background: #3d404d;
    .anticon-exclamation-circle {
      color: #faad14 !important;
    }
    .ant-popover-message-title {
      color: #fff;
      white-space: nowrap;
    }
    .ant-popover-buttons {
      display: flex;
      justify-content: flex-end;
    }
  }
}
:deep(.ant-popover-placement-bottom),
:deep(.ant-popover-placement-bottomLeft),
:deep(.ant-popover-placement-bottomRight) {
  .ant-popover-content {
    .ant-popover-arrow {
      border-top-color: #3d404d;
      border-left-color: #3d404d;
    }
  }
}
:deep(.ant-popover-placement-top),
:deep(.ant-popover-placement-topLeft),
:deep(.ant-popover-placement-topRight) {
  .ant-popover-content {
    .ant-popover-arrow {
      border-right-color: #3d404d;
      border-bottom-color: #3d404d;
    }
  }
}
</style>
