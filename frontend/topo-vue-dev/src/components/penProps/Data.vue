<template>
  <div class="PenData">
    <FormItem
      class="idFormItem"
      :value="pen.id"
      :form-item="idConfig"
      @update:value="changeValue($event, 'id')"
    />
    <FormItem
      style="padding: 0 12px"
      v-model:value="pen.description"
      :form-item="descriptionConfig"
      @update:value="changeValue($event, 'description')"
    />
    <!-- tags -->
    <a-collapse
      v-model:activeKey="activeKey"
      expand-icon-position="left"
      class="PenProps"
      :bordered="false"
    >
      <a-collapse-panel key="tags" :header="`Tag${t('标签')}`">
        <div class="tags">
          <template v-for="(tag, index) in pen.tags" :key="index">
            <a-tooltip v-if="tag.length > tagMaxLength" :title="tag">
              <a-tag closable @close="onDelTag($event, index)">
                {{ `${tag.slice(0, tagMaxLength)}...` }}
              </a-tag>
            </a-tooltip>
            <a-tag v-else closable @close="onDelTag($event, index)">{{
              tag
            }}</a-tag>
          </template>
        </div>

        <a-input
          :placeholder="t('按回车添加(最大长度100)')"
          v-model:value="tag"
          maxlength="100"
          @pressEnter="onAddTag"
        />
      </a-collapse-panel>
      <a-collapse-panel key="data">
        <template v-if="pen.form && pen.form.length">
          <FormEvery
            class="dataFormEvery"
            has-edit
            :configs="pen.form"
            :obj="pen"
            @change-value="onDataChange"
            @del-form-item="
              pen.form.splice($event.index, 1);
              onDataChange({ keys: ['form'] });
            "
            @edit-form-item="onEditFormItem"
            @del-bind-data="onDelBindData"
          />
          <a-button @click.stop="addDataItem" class="addData">
            <template #icon> <i class="t-icon t-add"></i></template>
            添加数据
          </a-button>
        </template>
        <template v-else>
          <div class="flex center middle small noContent">
            {{ t('暂无数据') }}，
            <a @click="addDataItem">添加</a>
          </div>
        </template>
        <template #header>
          <div class="flex middle collapseTitle">
            {{ t('数据') }}
            <!-- <i class="t-icon t-roundadd" @click.stop="addDataItem"></i> -->
          </div>
        </template>
      </a-collapse-panel>
      <a-collapse-panel key="form" v-if="Array.isArray(pen.dropdownList)">
        <template #header>
          <div class="flex middle collapseTitle">
            {{ t('下拉列表') }}
            <!-- <i class="t-icon t-roundadd" @click.stop="newDropdown"></i> -->
          </div>
        </template>
        <div class="dropdownList">
          <div
            class="dropdownOption"
            v-for="(option, index) in dropdownOptions"
            :key="index"
          >
            <span :title="option.find((i) => i.key === 'text')?.value">{{
              option.find((i) => i.key === 'text')?.value
            }}</span>
            <i
              class="hover t-icon t-gongyong--bianji"
              @click="editDropOption(option, index)"
            ></i>
            <a-popconfirm title="删除该选项？" @confirm="delOption(index)">
              <i class="hover delIcon t-icon t-shanchu delete"></i>
            </a-popconfirm>
          </div>
          <a-button @click.stop="newDropdown" class="addData">
            <template #icon> <i class="t-icon t-add"></i></template>
            添加下拉列表
          </a-button>
        </div>
      </a-collapse-panel>
    </a-collapse>
    <!-- 添加 form 属性的对话框 -->
    <div class="dialogClass" ref="dialogBox">
      <a-modal
        :getContainer="() => $refs.dialogBox"
        v-model:visible="addFormModalVisible"
        :title="t(formModalTitle)"
        :width="420"
        wrapClassName="addFormModal"
      >
        <FormEvery
          :configs="formItemConfigs"
          :obj="currentForm"
          @change-value="formItemConfigsChange"
        />
        <template v-if="['select', 'autoComplete'].includes(currentForm.type)">
          <template v-if="Array.isArray(currentForm.options)">
            <FormEvery
              v-for="option in currentForm.options"
              :configs="optionConfigs"
              :obj="option"
            />
          </template>
          <div class="buttons">
            <a-button type="primary" @click="addFormOption">新增选项</a-button>
            <a-button type="primary" @click="currentForm.options?.pop()"
              >删除选项</a-button
            >
          </div>
        </template>
        <template #footer>
          <a-button type="primary" @click="addForm">确定</a-button>
        </template>
      </a-modal>
    </div>
    <!-- 添加 dropdownList 属性的对话框 -->
    <a-modal
      v-model:visible="addDropModalVisible"
      :title="t(dropModalTitle)"
      :width="420"
      @ok="addDropdown"
      wrapClassName="addFormModal"
    >
      <!-- 标题项 -->
      <div>
        <label for="">选项名：</label>
        <a-input
          class="ml8 mt8"
          v-model:value="currentDropdown.find((i) => i.key === 'text').value"
        />
      </div>
      <div class="mt16">
        <label for="">选项值：</label>
        <a-tooltip>
          <template #title>
            {{
              t(
                '选项值，即选择该选项后同时修改的内容，例如：background: #f40, 表示的意思是，选择该项后，画笔的背景颜色变成 #f40'
              )
            }}</template
          >
          <i class="t-icon t-help-circle"></i>
        </a-tooltip>
      </div>

      <!-- 属性项 -->
      <table class="setPropsTable ml8 mt8">
        <thead>
          <tr>
            <td style="width: 167px">key</td>
            <td>value</td>
            <td>
              <i
                @click="
                  currentDropdown.push({
                    key: '',
                    value: '',
                  })
                "
                class="t-icon t-roundadd"
              ></i>
            </td>
          </tr>
        </thead>
        <tbody>
          <template v-for="(dropItem, index) in currentDropdown">
            <tr v-if="dropItem.key !== 'text'">
              <td>
                <a-auto-complete v-model:value="dropItem.key">
                  <template #options>
                    <a-select-option
                      v-for="option in canSetProps"
                      :value="option.value"
                    >
                      <div v-html="option.label" class="option"></div>
                    </a-select-option>
                  </template>
                </a-auto-complete>
              </td>
              <td>
                <a-input v-model:value="dropItem.value" />
              </td>
              <td>
                <a-popconfirm
                  title="删除该属性？"
                  ok-text="是"
                  cancel-text="否"
                  @confirm="currentDropdown.splice(index, 1)"
                >
                  <i class="hover delIcon t-icon t-shanchu delete"></i>
                </a-popconfirm>
              </td>
            </tr>
          </template>
        </tbody>
      </table>
    </a-modal>
  </div>
</template>

<script lang="ts">
import { Pen, Meta2d } from '@topometa2d/core';
import { message } from 'ant-design-vue';
import {
  computed,
  defineComponent,
  inject,
  onMounted,
  onUnmounted,
  Ref,
  ref,
  toRaw,
  watch,
  watchEffect,
  reactive,
} from 'vue';
import FormEvery from '../common/FormEvery.vue';
import FormItem from '../common/FormItem.vue';
import { t } from '@/i18n/i18n';
import { DeleteOutlined } from '@ant-design/icons-vue';
import { FormItemType } from '../utils';
import { useBindData } from '../common/BindModal/bindModal';
import { useStore } from 'vuex';
import Bus from '../common/bus';

declare const meta2d: Meta2d;
export default defineComponent({
  name: 'PenData',
  components: { FormEvery, FormItem, DeleteOutlined },
  setup(props) {
    // 此处的 pen 是响应式的
    // const pen: Ref<Pen> = inject('activePen');
    const pen: Ref<Pen | any> = inject('activePen');
    const idConfig = computed(() => {
      const config: FormItemType = {
        key: 'id',
        type: 'text',
        name: `ID`,
      };

      return config;
    });
    const dropdownOptions = ref([]);
    watchEffect(() => {
      setDropdownOptions(pen.value.dropdownList);
    });
    function changeValue(value?: any, key?: string) {
      if (key === 'id') {
        if (!value) {
          message.error(t('id 不能为空'));
          return;
        }
        const oldID: string = pen.value.id;
        try {
          meta2d.changePenId(oldID, value);
        } catch (error) {
          console.warn(error.message);
          message.error(t('id 修改失败，请检查 id 是否重复'));
          pen.value.id = oldID;
          return;
        }
        // 由于未双向绑定，所以更改内容
        pen.value.id = value;
      } else {
        meta2d.setValue({
          id: pen.value.id,
          [key]: value,
        });
      }
    }

    const tag = ref('');//tag数据--TODO当绑定时单向给TAG加1、tag=设备id;2、调用onAddTag

    const activeKey = ref(['tags', 'data', 'form']);

    function onDelTag(e: any, index: any) {
      e.preventDefault();
      let dataId= pen.value.tags[index]//删除对应图元的所有该设备号
      let formItem = pen.value.form
      let arr= formItem.map(item=>{
        if(item.dataIds && item.dataIds.dataId===dataId&&item.dataIds.communicationProtocolName==='RealTimeDataBase') delete item['dataIds']
        return toRaw(item)
      })
       meta2d.setValue({
        id: pen.value.id,
        form: [...arr],
      });
      // 修改（更新）bindDate->4、触发watch,重新计算tag
      store.commit('souceDate/setBindDate', {
        id: pen.value.id, //图元id
        data: [...arr], //设备id
      });
      // pen.value.tags.splice(index, 1);
      // meta2d.setValue({
      //   id: pen.value.id,
      //   tags: pen.value.tags,
      // });
    }

    function onAddTag() {
      // 添加 tag
      if (!pen.value.tags) {
        pen.value.tags = [];
      }
      pen.value.tags.push(tag.value);
      tag.value = '';
      meta2d.setValue({
        id: pen.value.id,
        tags: pen.value.tags,
      });
    }
    function onDataChange({ value, keys }: { value?: any; keys?: string[] }) {
      if (keys[0] === 'text') {
        meta2d.hideInput();
      }
      meta2d.setValue(
        {
          id: pen.value.id,
          // keys[1] 修改，仍修改整个 keys[0]
          [keys[0]]: toRaw(pen.value[keys[0]]),
        },
        {
          render: false,
        }
      );
      // TODO: 个别属性的设置，需要重算 path 等
      const cPen = meta2d.store.pens[pen.value.id];
      meta2d.canvas.updatePenRect(cPen);
      meta2d.render();
    }

    // 重写数据动态变化
    const store = useStore();
    const bindDate = computed(() => {
      return store.getters['souceDate/getBindDate'];
    });
   
    //2、对应一个图元的所有pen.value.tags由pens[x]中项目的form控制,tag在pens[x]>form[x]>dataIds.tag
    let stopBindDate=null
    stopBindDate=watch(()=>bindDate,()=>{
      bindDate.value.forEach(item=>{
        let arr=[]//item.id是该图元id；item.data=[]是该图元的绑定信息可能有n个
        item.data?.forEach((form)=>{
          if (form.dataIds &&form.dataIds.communicationProtocolName==='RealTimeDataBase') {
            const {tag } = form.dataIds;//解构出tag单个项
            // 查看arr中没有，就添加，有不添加
            if(!arr.includes(tag)) arr.push(tag)//3、只有实时数据库的tag参与计算，tag合集设备号唯一   tag也是设备号查找对应的图元的设备号并删除
          }
        })
        // 查看这个图元下面tag原本是否有值 TODO 去重  原本加绑定思路在原本的里面再次去重添加
        
        // 把这个tag加入到对应的图元下面
        meta2d.setValue({
          id: item.id,
          tags: [...arr],
        });
      })
    },
     //深度监听
      {deep: true}
    )

    function delOption(index: number) {
      dropdownOptions.value.splice(index, 1);
      onChangeOption();
    }

    /**
     * dropdownOptions 变成 pen.dropdownList
     */
    async function onChangeOption() {
      if (Array.isArray(dropdownOptions.value)) {
        for (const option of dropdownOptions.value) {
          for (const keyValue of option) {
            if (!keyValue.key) {
              return; // 数据有问题，校验不通过
            }
          }
        }
        // 数据没问题，赋值到 pen.dropdownList
        const { changeType } = await import('../defaults');
        pen.value.dropdownList = dropdownOptions.value.map((option) => {
          const value = {};
          option.forEach((keyValue) => {
            value[keyValue.key] = changeType(keyValue.value);
          });

          return value;
        });
        onDataChange({ keys: ['dropdownList'] });
      }
    }

    /**
     * pen.dropdownList 变成 dropdownOptions
     */
    function setDropdownOptions(dropdownList: any) {
      if (Array.isArray(dropdownList)) {
        dropdownOptions.value = dropdownList?.map((option) => {
          if (typeof option === 'object') {
            // 设置成可更改逻辑
            const ret = [];
            for (const key in option) {
              if (Object.prototype.hasOwnProperty.call(option, key)) {
                const value = option[key];
                ret.push({
                  key,
                  value,
                });
              }
            }
            return ret;
          } else if (typeof option === 'string') {
            return [
              {
                key: 'text',
                value: option,
              },
            ];
          }
        });
      } else {
        dropdownOptions.value = [];
      }
    }

    function editDropOption(option: any, index: number) {
      // 1. 对话框内的值
      editDropdownIndex.value = index;
      currentDropdown.value = [...option];
      dropModalTitle.value = '编辑下拉列表';
      // 2. 弹出对话框
      addDropModalVisible.value = true;
      preDropDownText = option.find((i) => i.key === 'text')?.value;
    }
    let preDropDownText = '';
    const addDropModalVisible = ref(false);

    const dropModalTitle = ref('添加下拉列表');

    const normalDropdown = [
      {
        key: 'text',
        value: '',
      },
    ];
    const currentDropdown = ref(normalDropdown);

    const editDropdownIndex = ref(-1);

    const addDropdown = () => {
      // 1. 值添加到 dropdownList
      if (editDropdownIndex.value === -1) {
        // 添加
        dropdownOptions.value.push(currentDropdown.value);
      } else {
        dropdownOptions.value[editDropdownIndex.value] = [
          ...currentDropdown.value,
        ];
      }
      onChangeOption();
      if (preDropDownText === meta2d.findOne(pen.value.id).text) {
        pen.value.text = dropdownOptions.value[editDropdownIndex.value].find(
          (i) => i.key === 'text'
        )?.value;
        meta2d.hideInput();
        onDataChange({ keys: ['text'] });
      }
      preDropDownText = '';
      addDropModalVisible.value = false;
    };

    const eventProps = reactive({ canSetProps: [] });
    importNormalProps();

    async function importNormalProps() {
      const { canSetProps } = await import('../defaults');
      const realSetProps = canSetProps.filter(
        (item) => item.value !== 'text' && item.value !== 'showChild'
      );
      eventProps.canSetProps.push(...realSetProps);
    }

    const addFormModalVisible = ref(false);

    const formItemConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          key: 'name',
          type: 'text',
          name: `${t('显示名称')}`,
        },
        {
          key: 'key',
          type: 'text',
          name: `${t('属性名')}`,
        },
        // TODO: 下拉框情况下的 axios 尚未处理
        {
          key: 'type',
          type: 'select',
          name: `${t('类型')}`,
          options: formItemTypes,
        },
        {
          key: 'placeholder',
          type: 'text',
          name: `${t('提示文字')}`,
        },
      ];

      if (['number', 'slider'].includes(currentForm.value.type)) {
        configs.push({
          key: 'min',
          type: 'number',
          name: `${t('最小值')}`,
        });
        configs.push({
          key: 'max',
          type: 'number',
          name: `${t('最大值')}`,
        });
        configs.push({
          key: 'step',
          type: 'number',
          name: `${t('步长')}`,
        });
        configs.push({
          key: 'precision',
          type: 'number',
          name: `${t('精度')}`,
        });
      }

      return configs;
    });

    const normalForm: FormItemType = {
      key: '',
      name: '',
      type: 'text',
      title: 'json', // 默认值， code 编辑器需要
      language: 'json',
      isNotString: true, // TODO: 默认处理 js 对象
    };
    const currentForm = ref<FormItemType>(
      // 拷贝，避免影响原值
      { ...normalForm }
    );

    function addForm() {
      if (!Array.isArray(pen.value.form)) {
        pen.value.form = [];
      }
      if (!currentForm.value.name) {
        message.warning('显示名称必填');
        return;
      }
      if (editIndex.value !== -1) {
        // 编辑行为
        pen.value.form[editIndex.value] = { ...currentForm.value };
      } else {
        // 新增行为
        pen.value.form.push(currentForm.value);
      }
      // 恢复原值
      currentForm.value = { ...normalForm };
      addFormModalVisible.value = false;
      onDataChange({ keys: ['form'] });
    }

    function addFormOption() {
      if (!Array.isArray(currentForm.value.options)) {
        currentForm.value.options = [];
      }
      currentForm.value.options.push({
        label: '',
        value: '',
      });
    }

    const optionConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          key: 'label',
          type: 'text',
          name: `${t('选项名')}`,
        },
        {
          key: 'value',
          type: 'text',
          name: `${t('选项值')}`,
        },
      ];
      return configs;
    });

    const editIndex = ref(-1);
    function onEditFormItem({ key, index }: { key: string; index: number }) {
      currentForm.value = { ...pen.value.form[index] };
      formModalTitle.value = '编辑数据';
      editIndex.value = index;
      addFormModalVisible.value = true;
    }

    const formModalTitle = ref('添加数据');

    function addDataItem() {
      addFormModalVisible.value = true;
      editIndex.value = -1;
      formModalTitle.value = '添加数据';
    }

    const descriptionConfig = computed(() => {
      const config: FormItemType = {
        key: 'description',
        type: 'text',
        name: `${t('名称')}`,
      };
      return config;
    });

    const { queryBinds } = useBindData();
    queryBinds();
    const newDropdown = () => {
      dropModalTitle.value = '添加下拉列表';
      currentDropdown.value = JSON.parse(JSON.stringify(normalDropdown));
      addDropModalVisible.value = true;
      editDropdownIndex.value = -1;
    };

    const onDelBindData = (event) => {
      delete pen.value.form[event.index].dataIds;
      onDataChange({ keys: ['form'] });
      meta2d.initBindDatas();
    };

    const formItemConfigsChange = ({ value, keys }) => {
      if (keys[0] === 'type') {
        currentForm.value = {
          key: currentForm.value.key,
          name: currentForm.value.name,
          type: currentForm.value.type,
          title: currentForm.value.title,
          language: currentForm.value.language,
          isNotString: currentForm.value.isNotString,
        };
      }
    };
    
    onUnmounted(()=>{
      stopBindDate()//销毁监视
    })
    return {
      idConfig,
      pen,
      changeValue,
      activeKey,
      tag,
      onDelTag,
      onAddTag,
      onDataChange,
      dropdownOptions,
      canSetProps: eventProps.canSetProps,
      delOption,
      addFormModalVisible,
      formItemConfigs,
      currentForm,
      addForm,
      tagMaxLength: 15,
      addFormOption,
      optionConfigs,
      onEditFormItem,
      editIndex,
      formModalTitle,
      editDropOption,
      dropModalTitle,
      currentDropdown,
      addDropModalVisible,
      addDropdown,
      normalDropdown,
      addDataItem,
      descriptionConfig,
      newDropdown,
      onDelBindData,
      formItemConfigsChange,
    };
  },
});
const formItemTypes = [
  {
    label: t('文本'),
    value: 'text',
  },
  {
    label: t('数字'),
    value: 'number',
  },
  {
    label: t('颜色'),
    value: 'color',
  },
  {
    label: t('多行文本'),
    value: 'textarea',
  },
  {
    label: t('下拉框'),
    value: 'select',
  },
  {
    label: t('开关'),
    value: 'switch',
  },
  {
    label: t('Json'),
    value: 'code',
  },
  {
    label: t('滑块'),
    value: 'slider',
  },
  // TODO: 自动完成与下拉框选一个
  // 'autoComplete'
];
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.PenData {
  .idFormItem {
    padding: 8px 12px;
  }

  .dataFormEvery {
    // :deep()
    &:deep(.FormItem) {
      max-width: 143px;
      margin-bottom: 0;
      margin: 4px 0;

      .label {
        font-size: 12px;
        font-family: PingFangSC, PingFangSC-Regular;
        font-weight: 400;
        color: #7f838c;
        line-height: 18px;
        letter-spacing: 0px;
        display: inline-block;
        white-space: nowrap;
        overflow: hidden;
        width: 60px;
        text-overflow: ellipsis;
      }
    }
  }

  .addData {
    width: 100%;
    height: 30px;
    font-size: 12px;
    text-align: left;
    text-align: center;
    padding: 0px;
    background: #1890ff;
    border: 1px solid #1890ff;
    color: #fff;
    :deep(i) {
      font-size: 12px;
    }
    :deep(span) {
      margin-left: 12px;
      //color: #474e59;
    }
  }
  .tags {
    display: flex;
    flex-wrap: wrap;
    width: 100%;
    margin: 8px 0;

    .ant-tag {
      margin-top: 8px;
    }
  }

  .topButton {
    width: 100%;
  }

  .singleOption {
    margin-top: 5px;
    position: relative;
    background-color: #edeef0;
    border-radius: 6px;
    padding: 5px;
    padding-top: 20px;

    .oneKey {
      display: flex;
      margin-top: 5px;
      margin-bottom: 5px;
      justify-content: space-around;

      .key {
        width: 90px;
      }

      .value {
        width: 90px;
      }
    }

    .buttons {
      display: flex;
      align-items: center;
      height: 50px;
      justify-content: space-around;
    }

    .delIcon {
      position: absolute;
      right: 5px;
      top: 5px;
    }
  }

  .dropdownList {
    .dropdownOption {
      display: flex;
      height: 30px;
      align-items: center;

      span {
        flex: 1;
        font-size: 12px;
        display: inline-block;
        white-space: nowrap;
        /* width: 100%; */
        overflow: hidden;
        text-overflow: ellipsis;
      }

      i {
        visibility: hidden;
        margin-right: 5px;
      }

      &:hover {
        i {
          visibility: visible;
        }
      }
    }
    .addData {
      width: 100%;
      height: 30px;
      font-size: 12px;
      text-align: left;
      text-align: center;
      padding: 0px;

      :deep(i) {
        font-size: 12px;
      }
      :deep(span) {
        margin-left: 12px;
        color: #474e59;
      }
    }
  }
}
</style>

<style lang="scss">
.PenProps {
  .dataFormEvery {
    color: #fff;
  }
}
.addFormModal {
  .ant-modal-content {
    background: #232630;
    .ant-modal-header {
      background: #232630;
      padding: 16px 0;
      margin: 0 24px;
      border-bottom: 1px solid #363841;
      .ant-modal-title {
        color: #fff;
        font-size: 14px;
      }
    }
    .ant-modal-close-x {
      color: #fff;
    }
    .ant-modal-body {
      padding: 0;
      padding: 24px;
      .monaco {
        height: 400px;
      }
    }
    .ant-modal-footer {
      border-top: none;
      padding: 30px 16px;
      .ant-btn {
        background: #3d404c;
        border: 1px solid #3d404c;
        color: #fff;
        &.ant-btn-primary {
          background: #1890ff;
          border-color: #1890ff;
        }
      }
    }
  }
  .buttons {
    display: flex;
    align-items: center;
    justify-content: space-around;
  }

  .ant-col-10 {
    max-width: 30%;
  }

  .ant-col-14 {
    max-width: 70%;
    flex-grow: 1;
  }

  .FormItem.ant-row.ant-form-item {
    .ant-input-number,
    .ant-input,
    .ant-select-selector {
      border: 1px solid #393b4a;
      box-shadow: none;

      &:hover {
        border: 1px solid #1890ff;
      }
    }
  }
}
.ant-collapse-content-box {
  .noContent {
    color: #7f838c;
  }
}
.ant-select-dropdown {
  background: #2d2f38;
  .ant-select-item {
    background: #2d2f38;
    color: #a5a8b2;
    &:hover {
      background: #3d404d;
      color: #fff;
    }
  }
}
.ant-select-tree-dropdown {
  background: #232630;
  box-shadow: none;
  .ant-select-tree {
    color: #a5a8b2;
  }
  li {
    .ant-select-tree-node-content-wrapper {
      color: #a5a8b2;
      &:hover {
        background: transparent;
        color: #1890ff;
      }
      &.ant-select-tree-node-selected {
        background: none;
      }
    }
  }
}
</style>
