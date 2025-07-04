<template>
  <div class="buttonDiv">
    <a-button @click="addEvent" type="primary" class="add-button">{{
      t('添加事件')
    }}</a-button>
  </div>
  <a-collapse
    v-model:activeKey="activeKey"
    expand-icon-position="left"
    class="PenProps Event"
    :bordered="false"
  >
    <a-collapse-panel
      v-for="(event, index) in pen.events"
      :key="String(index)"
      style="width: 100%"
    >
       <!-- :header="`${t('事件')}${Number(index) + 1}`" -->
    <template  #header>
      <!-- 显示名称 -->
      <div class="btn">
       <span v-if="event.flag">
      {{ event.eventName }}
      <a-button size="small" @click.prevent.stop="onShowRename(event, event.id)" class="edit">
       <template #icon>
         <FormOutlined />
       </template>
      </a-button>
      </span>
      <a-input v-else
      v-model:value="modifyItem[event.id].name"
      @click.stop
      @blur="onRename(event)"
      @pressEnter="onRename(event)"
      @keyup.esc="onRename(event, true)"
      ></a-input>

       <a-popconfirm
        :title="t('确定删除吗？')"
        ok-text="是"
        cancel-text="否"
        @confirm="delEvent(index)"
        :getPopupContainer="triggerNode => {
         return triggerNode.parentNode
         }"
        @cancel.stop
      >
        <i class="t-icon t-shanchu extraDelete" @click.stop></i>
      </a-popconfirm>
    </div>
    
    </template>
      <!-- <template #extra>
       
      </template> -->
      <!-- 最上面默认的部分 -->
      <FormEvery
        :configs="eventConfigs"
        :obj="event"
        @change-value="changeValue($event, event)"
      />
      <!-- 事件处理等 -->
      <!-- 1. 打开链接 -->
      <FormEvery
        v-if="event.action === EventAction.Link"
        :configs="linkConfigs"
        :obj="event"
        @change-value="changeValue($event, event)"
      />
      <!-- 2. 动画 -->
      <FormEvery
        v-else-if="
          [
            EventAction.StartAnimate,
            EventAction.PauseAnimate,
            EventAction.StopAnimate,
          ].includes(event.action)
        "
        :configs="animateConfigs"
        :obj="event"
        @change-value="changeValue($event, event)"
      />
      <!-- 2. 动画 -->
      <FormEvery
        v-else-if="
          [
            EventAction.StartVideo,
            EventAction.PauseVideo,
            EventAction.StopVideo,
          ].includes(event.action)
        "
        :configs="videoConfigs"
        :obj="event"
        @change-value="changeValue($event, event)"
      />
      <!-- 3. Function -->
      <FormEvery
        v-else-if="event.action === EventAction.JS"
        :configs="functionConfigs"
        :obj="event"
        :pen="pen"
        @change-value="changeValue($event, event)"
      />
      <!-- 4. WindowFn -->
      <FormEvery
        v-else-if="event.action === EventAction.GlobalFn"
        :configs="windowFnConfigs"
        :obj="event"
        @change-value="changeValue($event, event)"
      />
      <!-- 5. emit -->
      <FormEvery
        v-else-if="event.action === EventAction.Emit"
        :configs="emitConfigs"
        :obj="event"
        @change-value="changeValue($event, event)"
      />

      <!-- 新添加 SetProps -->
      <template v-else-if="event.action === EventAction.SetProps">
        <FormEvery
          :configs="setPropsConfigs"
          :obj="event"
          @change-value="changeValue($event, event)"
        />
        <table class="setPropsTable">
          <thead>
            <tr>
              <td style="padding-left: 11px">key</td>
              <td style="padding-left: 11px">value</td>
              <td>
                <i @click="addProp(event)" class="t-icon t-roundadd"></i>
              </td>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(prop, index) in event.setProps">
              <td>
                <a-auto-complete
                  v-model:value="prop.key"
                  @blur="changePropValue({}, event)"
                >
                  <template #options>
                    <a-select-option
                      v-for="option in eventProps.canSetProps"
                      :value="option.value"
                    >
                      <div v-html="option.label" class="option"></div>
                    </a-select-option>
                  </template>
                </a-auto-complete>
              </td>
              <td>
                <a-input
                  v-model:value="prop.value"
                  @blur="changePropValue({}, event)"
                />
              </td>
              <td>
                <a-popconfirm
                  title="删除该属性吗？"
                  ok-text="是"
                  cancel-text="否"
                  @confirm="delProp(event, index)"
                  :getPopupContainer="triggerNode => {
                    return triggerNode.parentNode
                  }"
                  @cancel.stop
                >
                  <i class="hover delIcon t-icon t-shanchu delete" @click.stop></i>
                </a-popconfirm>
              </td>
            </tr>
          </tbody>
        </table>
      </template>
      <!-- 发送属性数据 -->
      <template v-else-if="event.action === EventAction.SendPropData">
        <FormEvery
          :configs="setPropsConfigs"
          :obj="event"
          @change-value="changeValue($event, event)"
        />
        <table class="setPropsTable">
          <thead>
            <tr>
              <td style="padding-left: 11px">key</td>
              <td style="padding-left: 11px">value</td>
              <td>
                <i @click="addSendProp(event)" class="t-icon t-roundadd"></i>
              </td>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(prop, index) in event.setProps">
              <td>
                <a-auto-complete
                  v-model:value="prop.key"
                  @blur="changeSendPropValue({}, event)"
                >
                <!-- 下拉选项需要修改 -->
                  <template #options>
                    <a-select-option
                      v-for="option in eventProps.canSetProps"
                      :value="option.value"
                    >
                      <div v-html="option.label" class="option"></div>
                    </a-select-option>
                  </template>
                </a-auto-complete>
              </td>
              <td>
                <a-input
                  v-model:value="prop.value"
                  @blur="changeSendPropValue({}, event)"
                />
              </td>
              <td>
                <a-popconfirm
                  title="删除该属性吗？"
                  ok-text="是"
                  cancel-text="否"
                  @confirm="delSendProp(event, index)"
                  :getPopupContainer="triggerNode => {
                    return triggerNode.parentNode
                  }"
                  @cancel.stop
                >
                  <i class="hover delIcon t-icon t-shanchu delete" @click.stop></i>
                </a-popconfirm>
              </td>
            </tr>
          </tbody>
        </table>
      </template>
      <!-- 发送绑定变量数据 -->
      <template v-else-if="event.action === EventAction.SendVarData">
        <FormEvery
          :configs="setPropsConfigs"
          :obj="event"
          @change-value="changeValue($event, event)"
          @blur="blurChange($event, event)"
        />
        <table class="setPropsTable">
          <thead>
            <tr>
              <td style="padding-left: 11px">key</td>
              <td style="padding-left: 11px">value</td>
              <td>
                <i @click="addSendVar(event)" class="t-icon t-roundadd"></i>
              </td>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(prop, index) in event.setProps">
              <td>
                <a-auto-complete
                  v-model:value="prop.key"
                  @blur="changeSendVarValue({}, event)"
                >
                <!-- TODO 监听 目标 的form值 -->
                  <template #options>
                    <a-select-option
                      v-for="option in sendVarOptions"
                      :value="option.value"
                    >
                      <div v-html="option.label" class="option"></div>
                    </a-select-option>
                  </template>
                </a-auto-complete>
              </td>
              <td>
                <a-input
                  v-model:value="prop.value"
                  @blur="changeSendVarValue({}, event)"
                />
              </td>
              <td>
                <a-popconfirm
                  title="删除该属性吗？"
                  ok-text="是"
                  cancel-text="否"
                  @confirm="delSendVar(event, index)"
                  :getPopupContainer="triggerNode => {
                    return triggerNode.parentNode
                  }"
                  @cancel.stop
                >
                  <i class="hover delIcon t-icon t-shanchu delete" @click.stop></i>
                </a-popconfirm>
              </td>
            </tr>
          </tbody>
        </table>
      </template>
      <!-- . where -->
      <FormEvery
        :configs="conditionConfigs"
        :obj="event"
        @change-value="changeValue($event, event)"
      />
      <template v-if="event.where&&event.where.type === 'comparison'">
        <FormEvery
          :configs="whereConfigs"
          :obj="event"
          @change-value="changeValue($event, event)"
        />
      </template>
      <template v-else-if="event.where&&event.where.type === 'custom'">
        <FormEvery
          :configs="designConfigs"
          :obj="event"
          @change-value="changeValue($event, event)"
        />
      </template>
      <!-- where -->
      <!-- <template v-if="event.where">
        <FormEvery
          :configs="whereConfigs"
          :obj="event"
          @change-value="changeValue($event, event)"
        />
        <div class="buttonDiv">
          <a-popconfirm
            :title="t('确定删除吗？')"
            :ok-text="t('Yes')"
            cancel-text="No"
            @confirm="delWhere(event)"
          >
            <a-button danger class="add-button">{{
              `${t('取消')} ${t('触发条件')}`
            }}</a-button>
          </a-popconfirm>
        </div>
      </template> -->
      <!-- <div class="buttonDiv" v-else>
        <a-button @click="addWhere(event)" type="primary" class="add-button">{{
          `${t('设置')} ${t('触发条件')}`
        }}</a-button>
      </div> -->
      <div class="buttonDiv" v-if="event.action===7">
        <a-button @click="setModelStyle(event,index)" type="primary" class="add-button">{{
          `${t('更多弹窗设置')}`
        }}</a-button>
      </div>
      
    </a-collapse-panel>
  </a-collapse>
  <EventModalSet v-model:visible="modalSetVisible"
  @changeModalStyle="changeModalStyle"/>
</template>

<script lang="ts">
import { t } from '@/i18n/i18n';
import Bus from '../common/bus';
import { Meta2d, Event, EventAction, Pen } from '@topometa2d/core';
import {
  computed,
  defineComponent,
  inject,
  reactive,
  Ref,
  ref,
  toRaw,
  watchEffect,
  watch,
} from 'vue';
import FormEvery from '../common/FormEvery.vue';
import EventModalSet from '../common/EventModalSet/EventModalSet.vue';
import { PlusCircleOutlined, MinusCircleOutlined, FormOutlined } from '@ant-design/icons-vue';
import FormItem from '../common/FormItem.vue';
import { FormItemType } from '../utils';
import { message } from 'ant-design-vue';
declare const meta2d: Meta2d;

export default defineComponent({
  name: 'PenEvents',
  components: {
    FormEvery,
    PlusCircleOutlined,
    MinusCircleOutlined,
    FormItem,
    EventModalSet,
    FormOutlined
  },
  setup() {
    const modalSetVisible = ref(false);
    const eventIndex = ref();
    function addEvent() {
      if (!Array.isArray(pen.value.events)) {
        pen.value.events = [];
      }
      // let num = 1
      // if(pen.value.events[pen.value.events.length-1]?.id!==undefined){
      //   num= pen.value.events[pen.value.events.length-1].id+1
      // }
      let num = findMaxId(pen.value.events)+1
      pen.value.events.push({
        eventName: '事件'+num,
        id:num,
        flag: true, 
        where:{type:null}});
      activeKey.value = [
        ...activeKey.value,
        String(pen.value.events.length - 1),
      ];
      meta2d.setValue({ id: pen.value.id, events: pen.value.events },{render:false,history:false,doEvent:false});
    }

    function addWhere(event: Event) {
      event.where = {};
    }
    function setModelStyle(event,index){
      modalSetVisible.value=true
      eventIndex.value=index
      console.log('event',event.modalStyle)
      if(!event.modalStyle){
        event.modalStyle={}
        console.log('进来了')
      }
      Bus.$emit('modalStyle',event.modalStyle)
    }
    function changeModalStyle(obj){
      pen.value.events[eventIndex.value].modalStyle={...obj}
      console.log('接收的值',pen.value.events[eventIndex.value].modalStyle)
    }
    function delWhere(event: Event) {
      delete event.where;
      changeValue({}); // 更改画布中的内容
    }

    const activeKey = ref([]);

    // 此处的 pen 是响应式的
    // const pen: Ref<Pen> = inject('activePen');
    const pen: Ref<Pen | any> = inject('activePen');
    let penid = '';
    watch(
      pen.value,
      () => {
        if (penid !== pen.value.id) {
          if (pen.value.events) {
            for (let index = 0; index < pen.value.events.length; index++) {
              if (
                pen.value.events[index].where
              ) {
                if (!pen.value.events[index].where.type) {
                  if (
                    pen.value.events[index].where.comparison ||
                    pen.value.events[index].where.key ||
                    pen.value.events[index].where.value
                  ) {
                    pen.value.events[index].where.type = 'comparison';
                  } else if (pen.value.events[index].where.fnJs) {
                    pen.value.events[index].where.type = 'custom';
                  }
                }
              } else {
                pen.value.events[index].where = {};
                pen.value.events[index].where.type =null;
              }
              // 添加事件的id
              let item= pen.value.events[index]
              if(item.id===undefined) item.id= findMaxId(pen.value.events)+1
            }
          }
          penid = pen.value.id;
        }
      },
      {
        immediate: true,
      }
    );
    const eventConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          key: 'name',
          type: 'select',
          name: `${t('事件类型')}`,
          options: [
            {
              label: `${t('鼠标移入')}`,
              value: 'enter',
            },
            {
              label: `${t('鼠标移出')}`,
              value: 'leave',
            },
            {
              label: `${t('选中')}`,
              value: 'active',
            },
            {
              label: `${t('取消选中')}`,
              value: 'inactive',
            },
            // {
            //   label: `${t('鼠标按下')}`,
            //   value: 'mousedown',
            // },
            // {
            //   label: `${t('鼠标弹起')}`,
            //   value: 'mouseup',
            // },
            {
              label: `${t('单击')}`,
              value: 'click',
            },
            {
              label: `${t('双击')}`,
              value: 'dblclick',
            },
            {
              label: `${t('值变化')}`,
              value: 'valueUpdate',
            },
          ],
        },
        {
          key: 'action',
          type: 'select',
          name: `${t('事件行为')}`,
          options: [
            {
              label: `${t('打开链接')}`,
              value: EventAction.Link,
            },
            {
              label: `${t('更改属性')}`,
              value: EventAction.SetProps,
            },
            {
              label: `${t('执行动画')}`,
              value: EventAction.StartAnimate,
            },
            {
              label: `${t('暂停动画')}`,
              value: EventAction.PauseAnimate,
            },
            {
              label: `${t('停止动画')}`,
              value: EventAction.StopAnimate,
            },
            {
              label: `${t('播放视频')}`,
              value: EventAction.StartVideo,
            },
            {
              label: `${t('暂停视频')}`,
              value: EventAction.PauseVideo,
            },
            {
              label: `${t('停止视频')}`,
              value: EventAction.StopVideo,
            },
            {
              label: `${t('执行JavaScript')}`,
              value: EventAction.JS,
            },
            // {
            //   label: `${t('执行window函数')}`,
            //   value: EventAction.GlobalFn,
            // },
            {
              label: `${t('弹窗')}`,
              value: EventAction.Emit,
            },
            // {
            //   label: `${t('发送属性数据')}`,
            //   value: EventAction.SendPropData,
            // },
            // {
            //   label: `${t('发送变量数据')}`,
            //   value: EventAction.SendVarData,
            // },
          ],
        },
      ];

      return configs;
    });
    const conditionConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          key: 'where',
          key2:'type',
          type: 'select',
          name: `${t('触发条件')}`,
          placeholder: `${t('无')}`,
          options: [
            {
              label: `${t('无')}`,
              value: null,
            },
            {
              label: `${t('关系运算')}`,
              value: 'comparison',
            },
            // {
            //   label: `${t('代码1')}`,
            //   value: 'code1',
            // },
            // {
            //   label: `${t('代码2')}`,
            //   value: 'code2',
            // },
            {
              label: `${t('自定义代码判断')}`,
              value: 'custom',
            },
          ],
        },
      ];

      return configs;
    });
    const linkConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          key: 'value',
          type: 'text',
          name: `${t('链接地址')}`,
          placeholder: 'URL',
        },
        {
          key: 'params',
          type: 'text',
          name: `${t('打开方式')}`,
          placeholder: '_blank',
        },
      ];
      return configs;
    });

    const animateConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          key: 'value',
          type: 'text',
          name: `${t('动画目标')}`,
          placeholder: `${t('默认自身')}`,
        },
      ];
      return configs;
    });
    const videoConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          key: 'value',
          type: 'text',
          name: `${t('视频目标')}`,
          placeholder: `${t('默认自身')}`,
        },
      ];
      return configs;
    });

    const functionConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          key: 'value',
          type: 'code',
          name: 'JavaScript',
        },
        {
          key: 'params',
          type: 'text',
          name: `${t('函数参数')}`,
        },
      ];
      return configs;
    });

    const windowFnConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          key: 'value',
          type: 'text',
          name: `${t('函数名称')}`,
        },
        {
          key: 'params',
          type: 'text',
          name: `${t('函数参数')}`,
        },
      ];
      return configs;
    });

    const emitConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          key: 'value',
          type: 'select',
          name: `${t('弹窗类型')}`,
          options: [
            {
              label: '示例对话框',
              value: 'l-dialog',
            },
            {
              label: '小窗展示',
              value: 'iframe-dialog',
            },
          ],
        },
        {
          key: 'params',
          type: 'text',
          name: `${t('消息参数')}`,
          tips: '传递动态参数，id=${id}，&链接多个参数',
        },
        {
          key: 'address',
          type: 'text',
          name: `${t('接口地址')}`,
        }
      ];
      return configs;
    });

    const setPropsConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          key: 'params',
          type: 'text',
          name: `${t('目标')}`,
          placeholder: `${t('默认自身')}`,
        },
      ];
      return configs;
    });
    const SendPropDataConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          key: 'params',
          type: 'text',
          name: `${t('目标')}`,
          placeholder: `${t('默认自身')}`,
        },
      ];
      return configs;
    });
    const SendVarDataConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          key: 'params',
          type: 'text',
          name: `${t('目标')}`,
          placeholder: `${t('默认自身')}`,
        },
      ];
      return configs;
    });
    const eventProps = reactive({
      canCheckProps: [],
      canSetProps: [],
    });

    importNormalProps();

    async function importNormalProps() {
      const { canCheckProps, canSetProps } = await import('../defaults');
      eventProps.canCheckProps = canCheckProps;
      eventProps.canSetProps = canSetProps;
    }
    const whereConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          key: 'where',
          key2: 'key',
          name: `${t('属性名')}`,
          type: 'autoComplete',
          options: eventProps.canCheckProps,
        },
        {
          key: 'where',
          key2: 'comparison',
          name: `${t('条件')}`,
          type: 'select',
          options: [
            {
              label: `${t('大于')}`,
              value: '>',
            },
            {
              label: `${t('大于等于')}`,
              value: '>=',
            },
            {
              label: `${t('小于')}`,
              value: '<',
            },
            {
              label: `${t('小于等于')}`,
              value: '<=',
            },
            {
              label: `${t('等于')}`,
              value: '==', // 核心库等价于 =
            },
            {
              label: `${t('不等于')}`,
              value: '!=',
            },
            {
              label: `${t('介于')}`,
              value: '[)',
            },
            {
              label: `${t('不介于')}`,
              value: '![)',
            },
            {
              label: `${t('属于')}`,
              value: '[]',
            },
            {
              label: `${t('不属于')}`,
              value: '![]',
            },
          ],
        },
        {
          key: 'where',
          key2: 'value',
          name: `${t('属性值')}`,
          type: 'text',
          // options: [
          //   {
          //     label: `bool true`,
          //     value: true,
          //   },
          //   {
          //     label: `bool false`,
          //     value: false,
          //   },
          // ],
        },
        // {
        //   key: 'where',
        //   key2: 'fnJs',
        //   name: `${t('高优先级判断')}`,
        //   type: 'code',
        // },
      ];
      return configs;
    });
    const designConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          key: 'where',
          key2: 'fnJs',
          name: `${t('高优先级判断')}`,
          type: 'code',
        },
      ];
      return configs;
    });
    // 改变了某个值
    async function changeValue(
      { value, keys }: { value?: any; keys?: string[] },
      event?: any
    ) {
      if (keys[1] === 'type') {
        if (value === null) {
          if (event.where) {
            delete event.where;
            // event.where = {}
          }
        }
        if(!value){
          event.where={};
          event.where.type = null;
        }
        if (value === 'comparison') {
          event.where = {};
          event.where.type = 'comparison';
        }
        if (value === 'code1') {
          event.where = {};
          event.where.fnJs = 'return true';
          event.where.type = 'code1';
        }
        if (value === 'code2') {
          event.where = {};
          event.where.fnJs = 'return false';
          event.where.type = 'code2';
        }

        if (value === 'custom') {
          event.where = {};
          event.where.type = 'custom';
          event.where.fnJs = ''
        }
      }

      // 解决换事件后所配参数依然生效
      for (let item of eventConfigs.value[1].options) {
        if (item.value === value) {
          for (let option of Object.keys(event)) {
            if (option !== 'name' && option !== 'where' && option !== 'action'&& option !== 'flag'&& option !== 'eventName')
              event[option] = undefined;
          }
          break;
        }
      }
      if (keys && keys[0] === 'value') {
        event.fn = null;
      }
      if (keys && keys[1] === 'fnJs') {
        event.where.fn = null;
      }
      const canvasPen = meta2d.findOne(pen.value.id);
      if (canvasPen) {
        // canvasPen.events = toRaw(pen.value.events) as Event[];
        const { changeType } = await import('../defaults');
        canvasPen.events = (toRaw(pen.value.events) as Event[])?.map(
          (event) => {
            if (event.where&&event.where.type!==null) {
              // 切换类型， true 判断
              event.where.value = changeType(event.where.value as string);
            }
            return event;
          }
        );
      }
    }

    const sendVarOptions = ref([]);
    async function blurChange( { value, keys }: { value?: any; keys?: string[] },
      event?: Event) {
      if (keys[0] === 'params'&&event.action===EventAction.SendVarData) {
          let options = [];
          let _pen = meta2d.findOne(value) ||pen.value;
          _pen&&_pen.form.forEach((item:any) => {
            if (item.dataIds) {
              options.push({
                label: item.name,
                value:item.dataIds.dataId
              })
            }
          })
          sendVarOptions.value = options;
      }
    }

    function delEvent(index: number) {
      pen.value.events.splice(index, 1);
      meta2d.setValue({ id: pen.value.id, events: pen.value.events },{render:false,history:false,doEvent:false});
      // changeValue({});
    }

    watchEffect(() => {
      // 监听数据变化来设置 setProps 属性
      pen.value.events?.forEach((event) => {
        if (event.action === EventAction.SetProps||event.action === EventAction.SendPropData||event.action === EventAction.SendVarData) {
          event.setProps = [];
          !event.value &&
            (event.value = {
              '': '',
            });
          if (typeof event.value === 'object') {
            for (const key of Object.keys(event.value)) {
              event.setProps.push({
                key,
                value: String(event.value[key]),
              });
            }
          }
        }
      });
    });

    const propsConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          key: 'key',
          type: 'autoComplete',
          name: `${t('Key')}`,
          options: eventProps.canSetProps,
        },
        {
          key: 'value',
          type: 'text',
          name: `${t('Value')}`,
        },
      ];
      return configs;
    });

    async function changePropValue($event: any, event: Event | any) {
      // setProps 值转换成对象
      const value = {};
      const { changeType } = await import('../defaults');
      event.setProps.forEach((prop) => {
        value[prop.key] = changeType(prop.value);
      });
      event.value = value;

      changeValue({ value, keys: ['value'] }, event);
    }

    function addProp(event: Event | any) {
      !event.setProps && (event.setProps = []);
      event.setProps.push({
        key: '',
        value: '',
      });
    }

    function delProp(event: Event | any, index: number | any) {
      if (event.setProps?.length === 1) {
        // 只剩一条了，删除就没有了
        message.warning(t('至少保留一条设置属性'));
        return;
      }
      if (event.setProps?.length > 1) {
        event.setProps.splice(index, 1);
      }
      changePropValue(null, event);
    }

    function addSendProp(event: Event | any) {
      !event.setProps && (event.setProps = []);
      event.setProps.push({
        key: '',
        value: '',
      });
    }

    function delSendProp(event: Event | any, index: number | any) {
      if (event.setProps?.length === 1) {
        // 只剩一条了，删除就没有了
        message.warning(t('至少保留一条设置属性'));
        return;
      }
      if (event.setProps?.length > 1) {
        event.setProps.splice(index, 1);
      }
      changePropValue(null, event);
    }

    function addSendVar(event: Event | any) {
      !event.setProps && (event.setProps = []);
      event.setProps.push({
        key: '',
        value: '',
      });
    }

    function delSendVar(event: Event | any, index: number | any) {
      if (event.setProps?.length === 1) {
        // 只剩一条了，删除就没有了
        message.warning(t('至少保留一条设置属性'));
        return;
      }
      if (event.setProps?.length > 1) {
        event.setProps.splice(index, 1);
      }
      changePropValue(null, event);
    }

    async function changeSendPropValue($event: any, event: Event | any) {
      // setProps 值转换成对象
      const value = {};
      const { changeType } = await import('../defaults');
      event.setProps.forEach((prop) => {
        value[prop.key] = changeType(prop.value);
      });
      event.value = value;

      changeValue({ value, keys: ['value'] }, event);
    }

    async function changeSendVarValue($event: any, event: Event | any) {
      // setProps 值转换成对象
      const value = {};
      const { changeType } = await import('../defaults');
      event.setProps.forEach((prop) => {
        value[prop.key] = changeType(prop.value);
      });
      event.value = value;

      changeValue({ value, keys: ['value'] }, event);
    }

    // 双击修改事件名称
    interface modifyItemType{
      name: string,
      oldName: string,
    }
    const modifyItem = reactive<modifyItemType[]>([]);
    const onShowRename = (item: any,id) => {
      let obj: modifyItemType={
        name:item.eventName,
        oldName: item.eventName
      }
      item.flag=false//打开input框
      modifyItem[id]={...obj }
    };
    const onRename = (item: any, esc?: boolean) => {
      let index= item.id
      if (esc) {
        item.flag =true
        return;
      }
      if (item.flag===false) {
        if (modifyItem[index].name.length === 0) {
          item.eventName= modifyItem[index].oldName
          item.flag = true;
          message.error(t('事件名不能为空').toString());
          return;
        }
        item.eventName = modifyItem[index].name;
        item.flag = true
        return;
      }
    };
    // 寻找最大id
    function findMaxId(arr) {
      let maxId =0; // 初始值为1
      arr.forEach(obj => {
        if (obj.hasOwnProperty('id') && typeof obj.id === 'number' && obj.id > maxId) {
          maxId = obj.id;
        }
      });
      return maxId !== 50 ? maxId : 0; // 如果找到最大的id，则返回该值；否则返回null
    }
    return {
      addEvent,
      activeKey,
      pen,
      eventConfigs,
      changeValue,
      addWhere,
      delWhere,
      linkConfigs,
      animateConfigs,
      videoConfigs,
      functionConfigs,
      windowFnConfigs,
      emitConfigs,
      whereConfigs,
      designConfigs,
      conditionConfigs,
      delEvent,
      EventAction,
      propsConfigs,
      changePropValue,
      addProp,
      delProp,
      eventProps,
      setPropsConfigs,
      SendPropDataConfigs,
      SendVarDataConfigs,
      addSendProp,
      delSendProp,
      addSendVar,
      delSendVar,
      changeSendPropValue,
      changeSendVarValue,
      blurChange,
      sendVarOptions,
      modalSetVisible,
      eventIndex,
      setModelStyle,
      changeModalStyle,
      modifyItem,
      onShowRename,
      onRename,
    };
  },
});
</script>

<style lang="scss">
@import '@/styles/variables.scss';
.buttonDiv {
  height: 60px;
  padding: 12px;
  .add-button {
    width: 100%;
  }
}

.extraDelete {
  font-size: 12px;
  font-weight: 400;
}

.Event {
  .ant-collapse-content {
    width: 100%;
  }

  .propDeal {
    display: flex;
    justify-content: space-around;
    font-size: 30px;
    align-items: center;
  }
  .ant-popover-content{
    .ant-popover-inner{
      background: #3d404d;
      .ant-popover-message-title{
        color: #fff;
        white-space: nowrap;
      }
      .ant-popover-buttons{
        display: flex;
        justify-content: flex-end;
      }
    }
    
  }
  .ant-popover-placement-bottom,.ant-popover-placement-bottomLeft,.ant-popover-placement-bottomRight{
    .ant-popover-content{
      .ant-popover-arrow{
        border-top-color:#3d404d;
        border-left-color:#3d404d
      }
    }
  }
.ant-popover-placement-top,.ant-popover-placement-topLeft,.ant-popover-placement-topRight{
    .ant-popover-content{
      .ant-popover-arrow{
        border-right-color:#3d404d;
        border-bottom-color:#3d404d
      }
    }
  }

  .btn {
  display: flex;
  justify-content: space-between;
  input {
    border: 1px solid #393948;
    background-color: #191a24;
  }
  .ant-btn.edit{
    margin-left: 10px;
    border: none;
    background-color: transparent;
    color: #fff;
  }
}
}
.setPropsTable {
  width: 100%;
  td{
    color: #7f838c;
  }
  .ant-input-number,
  .ant-input,
  .ant-select-selector {
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
}

</style>
