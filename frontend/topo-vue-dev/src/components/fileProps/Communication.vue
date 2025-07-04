<template>
  <!-- 添加按钮 -->
  <div class="addCommunication">
    <a-button type="primary" block @click="showModal">
      <template #icon>
        <PlusCircleOutlined />
      </template>
      新增数据源</a-button
    >
  </div>

  <!-- 各个面板 -->
  <a-collapse
    v-model:activeKey="activeKey"
    expand-icon-position="left"
    class="PenProps"
    :bordered="false"
    accordion
  >
  <!-- websocketShow 面板 -->
   <a-collapse-panel  header="WEBSOCKET" class="customCollapse" v-if="websocketShow">
      <a-collapse  accordion>
        <a-collapse-panel
          :key="'websocket' + item.id"
          v-for="(item, index) in websocketData"
          @click="websocketChange(index)"
        >
          <!-- header内容 后面尽量用id-->
          <template #header>
            <div class="btn">
              <span v-if="item.flag">
                <span>{{ item.name }}</span>
                <a-button
                  size="small"
                  @click.prevent.stop="changeFlag('websocket', index)"
                >
                  <template #icon>
                    <FormOutlined />
                  </template>
                </a-button>
              </span>
              <input
                type="text"
                v-else
                v-model="item.name"
                @click.prevent.stop="null"
                @blur.prevent.stop="changeName('websocket', index, $event)"
                @keyup.enter.prevent.stop="enterOnBlur"
              />
              <div>
                  <a-button
                    size="small"
                    @click.prevent.stop="copyCurrent(item, index)"
                    >
                    <template #icon>
                      <img src="@/assets/copyCurrent.png" alt="复制" style="vertical-align: text-bottom;">
                    </template>
                  </a-button>
              <a-popconfirm
                title="确认删除?"
                ok-text="确定"
                cancel-text="取消"
                @confirm="confirm('websocket', index)"
                :getPopupContainer="
                  (triggerNode) => {
                    return triggerNode.parentNode;
                  }
                "
                @cancel.stop=""
              >
                <a-button size="small" @click.stop="" style="margin-left: 0;">
                  <template #icon> <DeleteOutlined /> </template
                ></a-button>
              </a-popconfirm>
            </div>
            </div>
          </template>

          <!-- body内容 -->
          <FormEvery
            :configs="websocketConfigs"
            :obj="item"
            @blur="onWebsocket"
            @change-value="onWebsocket"
          />
          <!-- <a-input
            title="必须以ws开头，否则不保存"
            v-model:value="websocket"
            @blur="onSocket"
            @pressEnter="onSocket"
          ></a-input> -->
        </a-collapse-panel>
    </a-collapse>
  </a-collapse-panel>
    
  <!-- mqttShow 面板 -->
 <a-collapse-panel  header="MQTT" class="customCollapse" v-if="mqttShow">
    <a-collapse  accordion>
      <a-collapse-panel
      :key="'MQTT' + item.id"
      v-for="(item, index) in mqttData"
    >
      <!-- header内容 -->
      <template #header>
        <div class="btn">
          <span v-if="item.flag">
            <span>{{ item.name }}</span>
            <a-button
              size="small"
              @click.prevent.stop="changeFlag('MQTT', index)"
            >
              <template #icon><FormOutlined /></template>
            </a-button>
          </span>
          <input
            type="text"
            v-else
            v-model="item.name"
            @click.prevent.stop="null"
            @blur.prevent.stop="changeName('MQTT', index, $event)"
            @keyup.enter.prevent.stop="enterOnBlur"
          />
          <div>
             <a-button size="small" @click.prevent.stop="copyCurrent(item, index)">
                <template #icon>
                  <img src="@/assets/copyCurrent.png" alt="复制" style="vertical-align: text-bottom;">
                </template>
              </a-button>
          <a-popconfirm
            title="确认删除?"
            ok-text="确定"
            cancel-text="取消"
            @confirm="confirm('MQTT', index)"
            :getPopupContainer="
              (triggerNode) => {
                return triggerNode.parentNode;
              }
            "
            @cancel.stop=""
          >
            <a-button size="small" @click.stop="" style="margin-left: 0;">
              <template #icon> <DeleteOutlined /> </template
            ></a-button>
          </a-popconfirm>
        </div>
        </div>
      </template>

      <!-- body内容 -->
      <FormEvery :configs="mqttConfigs" :obj="item" @blur="onMqtt" />
      </a-collapse-panel>
    </a-collapse>
  </a-collapse-panel>


  <!-- http 面板 -->
 <a-collapse-panel  header="HTTP" class="customCollapse" v-if="httpShow">
  <a-collapse  accordion :default-active-key="activeKey2">
      <a-collapse-panel
      :key="'HTTP' + item.id"
      v-for="(item, index) in httpData"
      @click="httpChange(index)"
    >
      <!-- header内容 -->
      <template #header>
        <!-- HTTP{{ index + 1 }} -->
        <div class="btn">
          <span v-if="item.flag">
            <span>{{ item.name }}</span>
            <a-button
              size="small"
              @click.prevent.stop="changeFlag('HTTP', index)"
            >
              <template #icon>
                <FormOutlined />
              </template>
            </a-button>
          </span>
          <input
            type="text"
            v-else
            v-model="item.name"
            @click.prevent.stop="null"
            @blur.prevent.stop="changeName('HTTP', index, $event)"
            @keyup.enter.prevent.stop="enterOnBlur"
          />
          <div>
              <a-button size="small" @click.prevent.stop="copyCurrent(item, index)">
               <template #icon>
                 <img src="@/assets/copyCurrent.png" alt="复制" style="vertical-align: text-bottom;">
               </template>
              </a-button>
          <a-popconfirm
            title="确认删除?"
            ok-text="确定"
            cancel-text="取消"
            @confirm="confirm('HTTP', index)"
            :getPopupContainer="
              (triggerNode) => {
                return triggerNode.parentNode;
              }
            "
            @cancel.stop=""
          >
            <a-button size="small" @click.stop="activeKey2='HTTP' + item.id" style="margin-left: 0;">
              <template #icon> <DeleteOutlined /> </template
            ></a-button>
          </a-popconfirm>
        </div>
        </div>
      </template>

      <!-- body内容 -->
      <FormEvery
        :configs="httpConfigs"
        :obj="item"
        @change-value="onHttp"
        @blur="onHttp"
        @refreshHttp="getRefreshHttp"
      />
      </a-collapse-panel>
  </a-collapse>
</a-collapse-panel>

  <!--  RealTimeDataBase面板 -->
 <a-collapse-panel  header="UniRTDB" class="customCollapse" v-if="RealTimeDataBaseShow">
  <a-collapse  accordion :default-active-key="activeKey2">
      <a-collapse-panel
      :key="'RealTimeDataBase' + item.id"
      v-for="(item, index) in RealTimeDataBase"
    >
      <!-- header内容 -->
      <template #header>
        <!-- HTTP{{ index + 1 }} -->
        <div class="btn">
          <span v-if="item.flag">
            <span>{{ item.name }}</span>
            <a-button
              size="small"
              @click.prevent.stop="changeFlag('RealTimeDataBase', index)"
            >
              <template #icon>
                <FormOutlined />
              </template>
            </a-button>
          </span>
          <input
            type="text"
            v-else
            v-model="item.name"
            @click.prevent.stop="null"
            @blur.prevent.stop="changeName('RealTimeDataBase', index, $event)"
            @keyup.enter.prevent.stop="enterOnBlur"
          />
          <div>
            <a-button size="small" @click.prevent.stop="copyCurrent(item, index)">
              <template #icon>
                <img src="@/assets/copyCurrent.png" alt="复制" style="vertical-align: text-bottom;">
              </template>
            </a-button>
          <a-popconfirm
            title="确认删除?"
            ok-text="确定"
            cancel-text="取消"
            @confirm="confirm('RealTimeDataBase', index)"
            :getPopupContainer="
              (triggerNode) => {
                return triggerNode.parentNode;
              }
            "
            @cancel.stop=""
          >
            <a-button size="small" @click.stop="activeKey2='RealTimeDataBase' + item.id" style="margin-left: 0;">
              <template #icon> <DeleteOutlined /> </template
            ></a-button>
          </a-popconfirm>
        </div>
        </div>
      </template>

      <!-- body内容 -->
      <FormEvery
        :configs="RealTimeDataBaseConfigs"
        :obj="item"
        @change-value="debounceMyFunction"
        @blur="debounceMyFunction"
        @refreshHttp="getRefreshRealTimeDataBase"
      />
      </a-collapse-panel>
  </a-collapse>
</a-collapse-panel>

  <!-- TDengine -->
  <a-collapse-panel header="TDengine" class="customCollapse" v-if="TDengineShow">
    <a-collapse  accordion :default-active-key="activeKey2">
      <a-collapse-panel  :key="'TDengine' + item.id"
          v-for="(item, index) in TDengine">
         <!-- header内容 -->
        <template #header>
          <!-- HTTP{{ index + 1 }} -->
          <div class="btn">
            <span v-if="item.flag">
              <span>{{ item.name }}</span>
              <a-button
                size="small"
                @click.prevent.stop="changeFlag('TDengine', index)"
              >
                <template #icon>
                  <FormOutlined />
                </template>
              </a-button>
            </span>
            <input
              type="text"
              v-else
              v-model="item.name"
              @click.prevent.stop="null"
              @blur.prevent.stop="changeName('TDengine', index, $event)"
              @keyup.enter.prevent.stop="enterOnBlur"
            />
            <div>
              <a-button size="small" @click.prevent.stop="copyCurrent(item, index)">
                <template #icon>
                  <img src="@/assets/copyCurrent.png" alt="复制" style="vertical-align: text-bottom;">
                </template>
              </a-button>
            <a-popconfirm
              title="确认删除?"
              ok-text="确定"
              cancel-text="取消"
              @confirm="confirm('TDengine', index)"
              :getPopupContainer="triggerNode=> triggerNode.parentNode"
              @cancel.stop=""
            >
              <a-button size="small" @click.stop="activeKey2 = 'TDengine' + item.id" style="margin-left: 0;">
                <template #icon> <DeleteOutlined /> </template>
              </a-button>
            </a-popconfirm>
          </div>
          </div>
        </template>
         <!-- body内容 -->
        <FormEvery
          :configs="TDengineConfigs"
          :obj="item"
          @blur="debounceMyTDengine"
          @change-value="debounceMyTDengine"
          @refreshHttp="getRefreshTDengine"
        />
      </a-collapse-panel>
    </a-collapse>
  </a-collapse-panel>
   

    <!-- <a-collapse-panel key="socketFn" :header="t('消息处理') + 'JavaScript'">
      <FormEvery
        :configs="messageConfigs"
        :obj="messageData"
        @change-value="socketFnChange"
      />
    </a-collapse-panel> -->
  </a-collapse>

  <!-- 模态框 -->
  <div ref="ShowModel" class="ShowModel">
    <a-modal
      v-model:visible="visible"
      width="530px"
      title="数据源类型"
      :getContainer="() => $refs.ShowModel"
      @ok="handleOk"
    >
      <a-form
        ref="formRef"
        :model="formState"
        :rules="rules"
        class="selectDataSourceForm"
      >
        <a-form-item label="请选择数据源" name="dataSourceType">
          <a-select
            v-model:value="formState.dataSourceType"
            placeholder="请选择数据源"
          >
            <a-select-option value="websocket">websocket</a-select-option>
            <a-select-option value="MQTT">MQTT</a-select-option>
            <a-select-option value="HTTP">HTTP</a-select-option>
            <a-select-option value="RealTimeDataBase">UniRTDB</a-select-option>
            <a-select-option value="TDengine">TDengine</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script lang="ts">
import {
  reactive,
  defineComponent,
  ref,
  computed,
  onMounted,
  onUnmounted,
  defineAsyncComponent,
  getCurrentInstance,
  toRefs,
  toRaw,
} from 'vue';
import FormEvery from '../common/FormEvery.vue';
import { t } from '@/i18n/i18n';
import { Meta2d, Meta2dStore, Meta2dData } from '@topometa2d/core';
import { FormItemType, showModal } from '../utils';
import {
  FormOutlined,
  DeleteOutlined,
  PlusCircleOutlined,
  CopyOutlined
} from '@ant-design/icons-vue';
import { useStore } from 'vuex'; //vuex动态仓库
import { connectHttpfix ,connectRealTimeDataBasefix, connectTDenginesfix } from '@/utils/soucerequest';
import {encryptPassword,decryptPassword} from '@/utils/encryption'
import debounce from "lodash/debounce"; // 防抖函数
import { cloneDeep } from 'lodash';
declare const meta2d: Meta2d & {
  store: Meta2dStore & {
    data: Meta2dData & {
      mqttData: any[];
      websocketDate: any[];
      RealTimeDataBase:any[];
      https:any[];
      TDengine:any[]
    };
  };
};
export default defineComponent({
  name: 'Communication',
  components: {
    FormEvery,
    FormOutlined,
    DeleteOutlined,
    PlusCircleOutlined,
    CopyOutlined
  },
  setup() {
    // const activeKey = ref(['websocket', 'MQTT', 'HTTP']);
    const activeKey = ref();
    const activeKey2 = ref('HTTP1');
    const websocket = ref('');
    const reg = /^ws.*/;
    const { ctx: that } = getCurrentInstance() as any; //获取当前组件实例
    const formRef = ref();
    const formState = reactive({
      dataSourceType: null,
    });
    const rules = {
      dataSourceType: [
        { required: true, message: '请选择数据源类型', trigger: 'change' },
      ],
    };
    const mqttData = reactive<any>([
      // {
      //   name: 'MQTT1',
      //   communicationProtocol:'mqtt',//固定属性用于区分当选择绑定时哪种协议绑定
      //   id: 1,
      //   flag: true,
      //   // mqtt: '',
      //   mqttOptions: {
      //     // clientId: '',
      //     // username: '',
      //     // password: '',
      //     // customClientId: ''
      //   },
      //   // mqttTopics: ''
      // },
    ]);
    const mqttShow = computed(() => {
      return mqttData.length === 0 ? false : true;
    });

    // 页面初始化各个协议链接
    const communicationInit = () => {
      // websocket.value = meta2d.store.data.websocket;
      // mqttData.mqtt = meta2d.store.data.mqtt;
      // mqttData.mqttOptions = meta2d.store.data.mqttOptions ?? {};
      // mqttData.mqttTopics = meta2d.store.data.mqttTopics;
      // messageData.socketFn = meta2d.store.data.socketCbJs;
      // httpData.http = meta2d.store.data.http;
      // httpData.httpTimeInterval = meta2d.store.data.httpTimeInterval || 1000;
      // httpData.httpHeaders = meta2d.store.data.httpHeaders || {};

      // 后台拿到数据赋值到页面中里面
      //解决切换图纸时不同图纸的协议会留存 通过初始化数组的长度：即赋值为0达到既可以重置数组又可以保存响应式，
      mqttData.length=0
      websocketData.length=0
      RealTimeDataBase.length=0
      httpData.length=0
      TDengine.length=0

      // mqttData.push(...(meta2d.store.data.mqttData ?? []));
      // websocketData.push(...(meta2d.store.data.websocketDate ?? []));
      // httpData.push(...(meta2d.store.data.https ?? []));
      // RealTimeDataBase.push(...(meta2d.store.data.RealTimeDataBase ?? []));
      RealTimeDataBase.push(...(decryptPassword(meta2d.store.data.RealTimeDataBase,'realtimedatabase') ?? []));
      mqttData.push(...(decryptPassword(meta2d.store.data.mqttData,'mqtt') ?? []));
      httpData.push(...(decryptPassword(meta2d.store.data.https,'http') ?? []));
      websocketData.push(...(decryptPassword(meta2d.store.data.websocketDate,'websocket') ?? []));
      TDengine.push(...(decryptPassword(meta2d.store.data.TDengine, 'TDengine') ?? []));
    };
    onMounted(() => {
      communicationInit();
      meta2d.on('opened', communicationInit);
    });
    const messageData = reactive({
      socketFn: '',
    });

    const messageConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          key: 'socketFn',
          type: 'code',
          name: `${t('消息处理')}`,
        },
      ];
      return configs;
    });
    const mqttConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          key: 'mqtt',
          type: 'text',
          name: `URL${t('地址')}*`,
          tips: '必须以ws开头，否则不保存',
          placeholder: `必须以ws开头，否则不保存`,
        },
        {
          key: 'mqttOptions',
          key2: 'clientId',
          type: 'text',
          name: 'Client ID',
          placeholder: `${t('唯一ID，可为空')}`,
        },
        {
          key: 'mqttOptions',
          key2: 'customClientId',
          type: 'switch',
          name: '关闭自动生成',
          tips: '是否关闭client ID自动生成',
        },
        {
          key: 'mqttOptions',
          key2: 'username',
          type: 'text',
          name: `${t('用户名')}`,
        },
        {
          key: 'mqttOptions',
          key2: 'password',
          type: 'password',
          name: `${t('密码')}`,
        },
        {
          key: 'mqttTopics',
          type: 'text',
          name: 'Topics*',
          placeholder: `${t('多个topic以英文逗号“,”分隔')}`,
        },
      ];

      return configs;
    });

    // const onSocket = () => {
    //   !websocket.value && (meta2d.store.data.websocket = '');
    //   if (reg.test(websocket.value)) {
    //     meta2d.store.data.websocket = websocket.value;
    //   }
    //   meta2d.connectWebsocket();
    // };

    const onMqtt = () => {
      !mqttData[0].mqtt && (meta2d.store.data.mqtt = '');
      if (reg.test(mqttData[0].mqtt)) {
        meta2d.store.data.mqtt = mqttData[0].mqtt;
      }
      meta2d.store.data.mqttOptions = mqttData[0].mqttOptions;
      meta2d.store.data.mqttTopics = mqttData[0].mqttTopics;

      // 存储数据--->遍历每条mqtt如果符合要求就保存原来，不符合赋值为null
      if(mqttData.length>0){
        mqttData.forEach(item=>{
         if(!reg.test(item.mqtt)){
          item.mqtt=null
         }
        })
      }
      meta2d.store.data.mqttData = [...cloneDeep(encryptPassword(mqttData,'mqtt'))];
      meta2d.connectMqtt();
    };

    const socketFnChange = () => {
      meta2d.socketFn = undefined; // 每次赋值清空一下它
      meta2d.store.data.socketCbJs = messageData.socketFn;
      meta2d.listenSocket();
    };

    const httpData = reactive<any>([
      // {
      //   name: 'HTTP1',
      //   communicationProtocol:'http',//固定属性用于区分当选择绑定时哪种协议绑定
      //   id: 1,
      //   flag: true,
      //   http: '',
      //   requestMethod: '',
      //   httpTimeInterval: 1000,
      //   httpHeaders: {},
      //   requestParams: {},
      //   authenticationMethod: '',
      //   username: '', //默认为空
      //   password: '', //默认为空
      //   CertifiedAddress: '', //默认为空
      //   responseData: {},
      // },
    ]);

    //保存一个id,放到httpConfigs用于动态判断
    let authState = ref(false); //默认为false
    let authStateIndex = ref(0); //默认为 0

    // 判断点击http的那个抽屉
    function httpChange(index) {
      authState.value =
        httpData[index]?.authenticationMethod === '2' ? true : false;
      authStateIndex.value = index;
    }

    // http配置项 根据认证方式动态显示
    const httpConfigs = computed(() => {
      /*  const configs: FormItemType[] = [
        {
          key: 'http',
          type: 'text',
          name: `URL${t('地址')}*`,
          placeholder: '/api/device/data?mock=1',
        },
        {
          key: 'requestMethod',
          type: 'select',
          name: '请求方式',
          placeholder: '请选择',
          options: [
            {
              label: 'GET',
              value: 'GET',
            },
            {
              label: 'POST',
              value: 'POST',
            },
          ],
        },
        // {
        //   key: 'httpTimeInterval',
        //   type: 'number',
        //   name: `${t('时间间隔')}`,
        //   placeholder: `${t('单位：毫秒')}`,
        //   min: 0,
        //   step: 50,
        // },
        {
          key: 'httpHeaders',
          type: 'code',
          name: `${t('请求头')}`,
          language: 'json',
          title: '请求头配置',
          isNotString: true,
        },
        {
          key: 'requestParams',
          type: 'code',
          name: `${t('请求参数')}`,
          language: 'json',
          title: '请求参数配置',
          isNotString: true,
        },
        {
          key: 'authenticationMethod',
          type: 'select',
          name: '认证方式',
          options: [
            {
              label: 'No Auth',
              value: '1',
            },
            {
              label: 'Basic Auth',
              value: '2',
            },
          ],
        },
        {
          key: 'responseData',
          type: 'code',
          name: `${t('响应数据')}`,
          language: 'json',
          title: '响应数据',
          isNotString: true,
        },
      ]; */
      let configs: FormItemType[];
      if (authState.value) {
        configs = [
          {
            key: 'httpTimeInterval', //指http的请求频率
            type: 'number',
            name: '刷新时间',
            placeholder: `${t('单位：毫秒')}`,
            min: 0,
            step: 50,
          },
          {
            key: 'http',
            type: 'text',
            name: `URL${t('地址')}*`,
            placeholder: '/api/device/data?mock=1',
          },
          {
            key: 'requestMethod',
            type: 'select',
            name: '请求方式',
            placeholder: '请选择',
            options: [
              {
                label: 'GET',
                value: 'GET',
              },
              {
                label: 'POST',
                value: 'POST',
              },
            ],
          },
          // {
          //   key: 'httpTimeInterval',
          //   type: 'number',
          //   name: `${t('时间间隔')}`,
          //   placeholder: `${t('单位：毫秒')}`,
          //   min: 0,
          //   step: 50,
          // },
          {
            key: 'httpHeaders',
            type: 'code',
            name: `${t('请求头')}`,
            language: 'json',
            title: '请求头配置',
            isNotString: true,
          },
          {
            key: 'requestParams',
            type: 'code',
            name: `${t('请求参数')}`,
            language: 'json',
            title: '请求参数配置',
            isNotString: true,
          },
          {
            key: 'authenticationMethod',
            type: 'select',
            name: '认证方式',
            options: [
              {
                label: 'No Auth',
                value: '1',
              },
              {
                label: 'Basic Auth',
                value: '2',
              },
            ],
          },
          {
            key: 'username',
            type: 'text',
            name: '用户名',
          },
          {
            key: 'password',
            type: 'password',
            name: '密码',
          },
          {
            key: 'CertifiedAddress',
            type: 'text',
            name: '认证地址',
          },
          {
            key: 'responseData',
            type: 'code',
            name: `${t('响应数据')}`,
            language: 'javascript',
            title: '响应数据',
            isNotString: true,
          },
        ];
      } else {
        configs = [
          {
            key: 'httpTimeInterval', //指http的请求频率
            type: 'number',
            name: '刷新时间',
            placeholder: `${t('单位：毫秒')}`,
            min: 0,
            step: 50,
          },
          {
            key: 'http',
            type: 'text',
            name: `URL${t('地址')}*`,
            placeholder: '/api/device/data?mock=1',
          },
          {
            key: 'requestMethod',
            type: 'select',
            name: '请求方式',
            placeholder: '请选择',
            options: [
              {
                label: 'GET',
                value: 'GET',
              },
              {
                label: 'POST',
                value: 'POST',
              },
            ],
          },
          // {
          //   key: 'httpTimeInterval',
          //   type: 'number',
          //   name: `${t('时间间隔')}`,
          //   placeholder: `${t('单位：毫秒')}`,
          //   min: 0,
          //   step: 50,
          // },
          {
            key: 'httpHeaders',
            type: 'code',
            name: `${t('请求头')}`,
            language: 'json',
            title: '请求头配置',
            isNotString: true,
          },
          {
            key: 'requestParams',
            type: 'code',
            name: `${t('请求参数')}`,
            language: 'json',
            title: '请求参数配置',
            isNotString: true,
          },
          {
            key: 'authenticationMethod',
            type: 'select',
            name: '认证方式',
            options: [
              {
                label: 'No Auth',
                value: '1',
              },
              {
                label: 'Basic Auth',
                value: '2',
              },
            ],
          },
          {
            key: 'responseData',
            type: 'code',
            name: `${t('响应数据')}`,
            language: 'javascript',
            title: '响应数据',
            isNotString: true,
          },
        ];
      }
      return configs;
    });

    const httpShow = computed(() => {
      return httpData.length === 0 ? false : true;
    });

    const onHttp = (e?) => {
      // 根据authStateIndex判断 切换的是哪个http数据的认证方式
      if (e.keys[0] === 'authenticationMethod' || e.value === '2') {
        httpChange(authStateIndex.value);
      }

      //存储到Meta2d.store.data中
      meta2d.store.data.https = [...encryptPassword(httpData,'http')];
      // meta2d.store.data.https = [...httpData];

      //多个http同时发送请求-->使用密文
      connectHttpfix([...meta2d.store.data.https]);
    };

    // 保存 websocketConfig的id
    let webAuthState = ref(false); //默认为fasle
    let webAuthStateIndex = ref(0);

    // 判断点击websocket那个抽屉
    function websocketChange(index) {
      webAuthState.value =
        websocketData[index]?.authenticationMethod === '2' ? true : false;
      webAuthStateIndex.value = index;
    }
    // websocket配置项
    const websocketConfigs = computed(() => {
      let configs: FormItemType[];
      //   = [
      //     {
      //       key: 'websocket',
      //       type: 'text',
      //       name: `URL${t('地址')}*`,
      //       placeholder: '/api/device/data?mock=1',
      //     },
      //     {
      //       key: 'requestHeader',
      //       type: 'code',
      //       name: `${t('请求头')}`,
      //       language: 'json',
      //       title: '请求头',
      //       isNotString: true,
      //     },
      //     {
      //       key: 'requestData',
      //       type: 'code',
      //       name: `${t('请求参数')}`,
      //       language: 'json',
      //       title: '请求参数',
      //       isNotString: true,
      //     },
      //     {
      //       key: 'authenticationMethod',
      //       type: 'select',
      //       name: '认证方式',
      //       options: [
      //         {
      //           label: 'No Auth',
      //           value: 1,
      //         },
      //         {
      //           label: 'Basic Auth',
      //           value: 2,
      //         },
      //       ],
      //     },
      //     {
      //       key: 'dataProcessing',
      //       type: 'code',
      //       name: `${t('数据处理')}`,
      //       language: 'json',
      //       title: '数据处理',
      //       isNotString: true,
      //     },
      //   ];
      if (webAuthState.value) {
        configs = [
          {
            key: 'websocket',
            type: 'text',
            name: `URL${t('地址')}*`,
          },
          {
            key: 'requestHeader',
            type: 'code',
            name: `${t('请求头')}`,
            language: 'json',
            title: '请求头',
            isNotString: true,
          },
          {
            key: 'requestData',
            type: 'code',
            name: `${t('请求参数')}`,
            language: 'json',
            title: '请求参数',
            isNotString: true,
          },
          {
            key: 'authenticationMethod',
            type: 'select',
            name: '认证方式',
            options: [
              {
                label: 'No Auth',
                value: '1',
              },
              {
                label: 'Basic Auth',
                value: '2',
              },
            ],
          },
          {
            key: 'username',
            type: 'text',
            name: '用户名',
          },
          {
            key: 'password',
            type: 'password',
            name: '密码',
          },
          {
            key: 'CertifiedAddress',
            type: 'text',
            name: '认证地址',
          },
          {
            key: 'dataProcessing',
            type: 'code',
            name: `${t('数据处理')}`,
            language: 'javascript',
            title: '数据处理',
            isNotString: true,
          },
        ];
      } else {
        configs = [
          {
            key: 'websocket',
            type: 'text',
            name: `URL${t('地址')}*`,
          },
          {
            key: 'requestHeader',
            type: 'code',
            name: `${t('请求头')}`,
            language: 'json',
            title: '请求头',
            isNotString: true,
          },
          {
            key: 'requestData',
            type: 'code',
            name: `${t('请求参数')}`,
            language: 'json',
            title: '请求参数',
            isNotString: true,
          },
          {
            key: 'authenticationMethod',
            type: 'select',
            name: '认证方式',
            options: [
              {
                label: 'No Auth',
                value: '1',
              },
              {
                label: 'Basic Auth',
                value: '2',
              },
            ],
          },
          {
            key: 'dataProcessing',
            type: 'code',
            name: `${t('数据处理')}`,
            language: 'javascript',
            title: '数据处理',
            isNotString: true,
          },
        ];
      }
      return configs;
    });

    const websocketData = reactive<any>([
      // {
      //   websocket: '123',
      //   name: 'websocket1',
      //   communicationProtocol:'http',//固定属性用于区分当选择绑定时哪种协议绑定
      //   id: 1,
      //   flag: true,
      //   dataSet: '',
      //   dimension: '',
      //   measure: '',
      //   legend: {},
      //   authenticationMethod: '',
      //   username: '',
      //   password: '',
      //   CertifiedAddress: '',
      //   responseData: {},
      // },
    ]);

    const websocketShow = computed(() => {
      return websocketData.length === 0 ? false : true;
    });

    const onWebsocket = (e) => {
      // 根据webAuthState判断 切换的是哪个webAuthState数据的认证方式
      if (e.keys[0] === 'authenticationMethod' || e.value === '2') {
        websocketChange(webAuthStateIndex.value);
      }
      //  todo.....
      // !val.websocket && (meta2d.store.data.websocket = '');
      // if (reg.test(val.websocket)) {
      //   meta2d.store.data.websocket = val.websocket;
      // 判断是哪一个建立连接
      //  }
      !websocketData[0].websocket && (meta2d.store.data.websocket = '');
      if (reg.test(websocketData[0].websocket)) {
        meta2d.store.data.websocket = websocketData[0].websocket;
      }
      // 存储操作
      meta2d.store.data.websocketDate = [...encryptPassword(websocketData,'realtimedatabase')];
      // meta2d.store.data.websocketDate = [...websocketData];
      meta2d.connectWebsocket();
    };

    onUnmounted(() => {
      meta2d.off('opened', communicationInit);
    });

    // 模态框
    const visible = ref<boolean>(false);

    // 开启模态框
    function showModal() {
      visible.value = true;
    }
    // 关闭模态框并重置
    function closeModal() {
      visible.value = false;
      formState.dataSourceType = null;
    }

    // 增添
    const store = useStore();
    function handleOk(e?: MouseEvent) {
      formRef.value
        ?.validate()
        .then(() => {
          switch (formState.dataSourceType) {
            case 'HTTP':
              //   let HTTPId = httpData[httpData.length - 1].id
              //     ? httpData[httpData.length - 1].id + 1
              //     : 1;
              let HTTPId =httpData.length > 0 ? findMaxId (httpData): 1;
              let HTTPName = HTTPId === 0 ? 'HTTP1' : 'HTTP' + HTTPId;
              httpData.push({
                name: HTTPName,
                communicationProtocol:'HTTP',//固定属性用于区分当选择绑定时哪种协议绑定
                id: HTTPId,
                copynumber: 0,//复制次数
                flag: true,
                httpTimeInterval: 1000,
                http: '',
                requestMethod: '',
                // httpTimeInterval: 1000,
                httpHeaders: {},
                requestParams: {},
                authenticationMethod: '1',
                username: '',
                password: '',
                CertifiedAddress: '',
                responseData: 'function fn(data) {//data为请求后的数据 \n \n    return null//返回值必须为null或者处理后的结果 \n}',
              });
              activeKey.value='2'
              // activeKey2.value = 'HTTP' + HTTPId; //打开刚添加的那一栏
              // 存储操作->更新meta2d.store.data
              meta2d.store.data.https = [...httpData];
              break;
            case 'websocket':
              let websocketId =websocketData.length > 0? findMaxId(websocketData): 1;
              let websocketName =
                websocketId === 0 ? 'websocket1' : 'websocket' + websocketId;
              websocketData.push({
                name: websocketName,
                communicationProtocol:'websocket',//固定属性用于区分当选择绑定时绑定哪种协议
                id: websocketId,
                copynumber: 0,//复制次数
                flag: true,
                realTimeInterval: 1000,//刷新时间
                refreshTime: '',
                dataSet: '',
                dimension: '',
                measure: '',
                legend: {},
                authenticationMethod: '1',
                username: '',
                password: '',
                CertifiedAddress: '',
                responseData: {},
              });
              activeKey.value='0'
              // activeKey.value = 'websocket' + websocketId;
              // 存储操作
              meta2d.store.data.websocketDate = [...websocketData];
              break;
            case 'MQTT':
              let MQTTId = mqttData.length > 0 ? findMaxId(mqttData): 1;
              let MQTTName = MQTTId === 0 ? 'MQTT1' : 'MQTT' + MQTTId;
              mqttData.push({
                name: MQTTName,
                communicationProtocol:'MQTT',//固定属性用于区分当选择绑定时绑定哪种协议
                id: MQTTId,
                copynumber: 0,//复制次数
                flag: true,
                mqttOptions: {},
              });
              activeKey.value='1'
              // activeKey.value = 'MQTT' + MQTTId;
              // 存储操作
              meta2d.store.data.mqttData = [...mqttData];
              break;
            case 'RealTimeDataBase':
              let RealTimeDataBaseId =
                RealTimeDataBase.length > 0 ?  findMaxId(RealTimeDataBase) : 1;
              let RealTimeDataBaseName = RealTimeDataBaseId === 0 ? 'UniRTDB1' : 'UniRTDB' + RealTimeDataBaseId;
              RealTimeDataBase.push({
                  name: RealTimeDataBaseName,
                  communicationProtocol:'RealTimeDataBase',//固定属性用于区分当选择绑定时绑定哪种协议
                  id: RealTimeDataBaseId,
                  copynumber:0,//复制次数
                  flag: true,
                  RealTimeDataBase: '',//ip地址
                  post: '',//端口
                  username: '', //用户名默认为空
                  password: '', //密码默认为空
                  EquipmentMasterTable: '', //设备主表
                  EquipmentSubTable: '',//设备子表
                  responseData: 'function fn2(data) {//data为请求后的数据 \n \n    return null//返回值必须为null或者处理后的结果 \n}',
                },
             );
              activeKey.value='3'
              meta2d.store.data.RealTimeDataBase = [...RealTimeDataBase];
              store.commit('user/setRealTimeDataBase', RealTimeDataBase);
              break;
            case 'TDengine':
              let TDengineId =
                TDengine.length > 0 ? findMaxId(TDengine) : 1;
              let TDengineName = TDengineId === 0 ? 'TDengine1' : 'TDengine' + TDengineId;
              TDengine.push({
                name: TDengineName,
                communicationProtocol: 'TDengine',//固定属性用于区分当选择绑定时绑定哪种协议
                id: TDengineId,
                TDenginenterval:'',//刷新时间
                copynumber: 0,//复制次数
                flag: true,
                TDengine: '',//ip地址
                post: '',//端口
                username: '', //用户名默认为空
                password: '', //密码默认为空
                EquipmentMasterTable: '', //设备主表
              },
              );
              activeKey.value = '4'
              meta2d.store.data.TDengine = [...TDengine];
              store.commit('user/setTDengine', TDengine);
              break;
              default:
              break;
          }
          //存储到vuex中--》bindModel
          store.commit('user/setHTTPDate', httpData);
          store.commit('user/setwebsocketDate', websocketData);
          store.commit('user/setMQTTDate', mqttData);
          closeModal(); //关闭弹窗
        })
        .catch((error) => {
          console.log('error', error);
        });
    }

    // 快捷复制
    function copyCurrent(obj, index) {
      switch (obj.communicationProtocol) {
        case 'HTTP':
          httpData.splice(index + 1 , 0, { ...obj, name: obj.name, copynumber: 0, id: 'copy-' + findCopyMaxId(httpData) });
          store.commit('user/setHTTPDate', httpData); //存储vuex中的
          meta2d.store.data.https = [...httpData];// 存储数据
          break;
        case 'websocket':
          websocketData.splice(index + 1 , 0, { ...obj, name: obj.name, copynumber: 0, id: 'copy-' + findCopyMaxId(websocketData) });
          store.commit('user/setwebsocketDate', websocketData); //存储vuex中的
          meta2d.store.data.websocketDate = [...websocketData];// 存储数据
          break;
        case 'MQTT':
          mqttData.splice(index + 1 , 0, { ...obj, name: obj.name, copynumber: 0, id: 'copy-' + findCopyMaxId(mqttData) });
          store.commit('user/setMQTTDate', mqttData); //存储vuex中的
          meta2d.store.data.mqttData = [...mqttData];// 存储数据
          break;
        case 'RealTimeDataBase':
          RealTimeDataBase.splice(index+1, 0,{...obj,name: obj.name, copynumber :0,id:'copy-'+ findCopyMaxId(RealTimeDataBase) });
          store.commit('user/setRealTimeDataBase', RealTimeDataBase); //存储vuex中的
          meta2d.store.data.RealTimeDataBase = [...RealTimeDataBase];// 存储数据
          break;
        case 'TDengine':
        TDengine.splice(index + 1, 0, { ...obj, name: obj.name, copynumber: 0, id: 'copy-' + findCopyMaxId(TDengine) });
        store.commit('user/setTDengine', TDengine); //存储vuex中的
        meta2d.store.data.TDengine = [...TDengine];// 存储数据
          break;
          default:
          break;
      }
    }

    // 查询id
    function findMaxId(arr) {
      let maxId = -1;
      // obj为复制出来的时候不参见计算
      arr.forEach((obj) => {
        console.log(obj.id,'obj.id')
        if (obj.id && !String(obj.id).startsWith('copy-') && obj.id > maxId) {
          maxId = obj.id;
        }
      });
      return maxId+1;
    }
    function findCopyMaxId(arr) {
      let maxId = -1;
      // obj为复制出来的时候不参见计算
      arr.forEach((obj) => {
        if (obj.id && String(obj.id).startsWith('copy-') && Number(obj.id.split('-')[1]) > maxId) {
          maxId = Number(obj.id.split('-')[1]);
        }
      });
      return maxId + 1;
    }

    // 删除
    function delItem(str: string, id: number) {
      switch (str) {
        case 'HTTP':
          //todo删除 本地触发vuex去更改
          httpData.splice(id, 1);
          store.commit('user/delHTTPDate', id); //删除vuex中的
          // 存储数据
          meta2d.store.data.https = [...httpData];
          break;

        case 'websocket':
          websocketData.splice(id, 1);
          store.commit('user/delwebsocketDate', id);
          // 存储操作
          meta2d.store.data.websocketDate = [...websocketData];
          break;

        case 'MQTT':
          mqttData.splice(id, 1);
          store.commit('user/delMQTTDate', id);
          // 存储操作
          meta2d.store.data.mqttData = [...mqttData];
          break;

          case 'RealTimeDataBase':
          RealTimeDataBase.splice(id, 1);
          store.commit('user/delRealTimeDataBase', id);
          // 存储操作
          meta2d.store.data.RealTimeDataBase = [...RealTimeDataBase];
          break;
          
        case 'TDengine':
        TDengine.splice(id, 1);
        store.commit('user/delTDengine', id);
        // 存储操作
        meta2d.store.data.TDengine = [...TDengine];
        break;
        default:
          break;
      }
    }
    function confirm(str: string, id: number) {
      delItem(str, id);
    }
    // 重命名
    function changeName(str, id, e) {
      switch (str) {
        case 'HTTP':
          // todo应该同步到vuex中->再次存储覆盖之前的
          httpData[id].name = e.target.value;
          // 存储操作
          meta2d.store.data.https = [...httpData];
          break;

        case 'websocket':
          websocketData[id].name = e.target.value;
          // 存储操作
          meta2d.store.data.websocketDate = [...websocketData];
          break;

        case 'MQTT':
          mqttData[id].name = e.target.value;
          // 存储操作
          meta2d.store.data.mqttData = [...mqttData];
          break;

        case 'RealTimeDataBase':
          RealTimeDataBase[id].name = e.target.value;
          // 存储操作
          meta2d.store.data.RealTimeDataBase = [...RealTimeDataBase];
          break;

        case 'TDengine':
          TDengine[id].name = e.target.value;
          // 存储操作
          meta2d.store.data.TDengine = [...TDengine];
          break;

        default:
          break;
      }
      // 切换输入框或者显示
      changeFlag(str, id);
    }
    //失焦
    function enterOnBlur(e) {
      (e.target as HTMLInputElement).blur();
    }
    // 切换
    function changeFlag(str: string, id: number) {
      switch (str) {
        case 'HTTP':
          httpData[id].flag = !httpData[id].flag;
          break;

        case 'websocket':
          websocketData[id].flag = !websocketData[id].flag;
          break;

        case 'MQTT':
          mqttData[id].flag = !mqttData[id].flag;
          break;

        case 'RealTimeDataBase':
          RealTimeDataBase[id].flag = !RealTimeDataBase[id].flag;
          break;

        case 'TDengine':
          TDengine[id].flag = !TDengine[id].flag;
          break;

        default:
          break;
      }
    }

    // http中刷新
    function getRefreshHttp() {
      // todo 重新执行onHttp函数
      onHttp();
    }

    // 实时数据库配置RealTimeDataBase
    const RealTimeDataBaseConfigs = computed(() => {
      let configs: FormItemType[] = [
          {
            key: 'realTimeInterval', //指realTimeInterval的请求频率
            type: 'number',
            name: '刷新时间',
            placeholder: `${t('单位：毫秒')}`,
            min: 0,
            step: 50,
          },
          {
            key: 'RealTimeDataBase',
            type: 'text',
            name: `${t('地址')}*`,
          },
           {
            key: 'post',
            type: 'text',
            name: `端口号*`,
          },
          {
            key: 'username',
            type: 'text',
            name: '用户名*',
          },
          {
            key: 'password',
            type: 'password',
            name: '密码*',
          },
          {
            key: 'EquipmentMasterTable',
            type: 'text',
            name: '设备主表*',
          },
          {
            key: 'EquipmentSubTable',
            type: 'text',
            name: '设备子表',
            placeholder:'后缀为ai、ao、di、do'
          },
         {
          key: 'responseData',
          type: 'code',
          name: `${t('响应数据')}`,
          language: 'javascript',
          title: '响应数据',
          isNotString: true,
        }, //增加响应数据处理
        ]
      return configs;
    });
    const RealTimeDataBase = reactive<any>([
      // {
      //   name: 'RealTimeDataBase1',
      //   communicationProtocol:'RealTimeDataBase',//固定属性用于区分当选择绑定时哪种协议绑定
      //   id: 1,
      //   flag: true,
      //   RealTimeDataBase: '',
      //   post: '',
      //   username: '', //默认为空
      //   password: '', //默认为空
      //   EquipmentMasterTable: '', //设备主表
      //   EquipmentSubTable: '',//设备子表
      // },
    ]);
    const RealTimeDataBaseShow=computed(() => {
      return RealTimeDataBase.length === 0 ? false : true;
    });
    const onRealTimeDataBase = () => {
      
      // 存储到Meta2d.store.data中 且 加密
      meta2d.store.data.RealTimeDataBase = [...encryptPassword(RealTimeDataBase,'realtimedatabase')];
      // TODO发送配置信息建立调用组态后台api接口（/connect/server），同时传递相关配置参数，建立与实时数据库的连接。
      connectRealTimeDataBasefix([...encryptPassword(RealTimeDataBase,'realtimedatabase')]);
    };

    // 防抖处理
    const debounceMyFunction = debounce(onRealTimeDataBase, 1000)

    // RealTimeDataBase中刷新
    function getRefreshRealTimeDataBase() {

      // todo 重新执行onRealTimeDataBase函数
      onRealTimeDataBase();
    }

    // TDengine
    const TDengineConfigs = computed(() => {
      let configs: FormItemType[] = [
        {
          key: 'TDenginenterval', //指realTimeInterval的请求频率
          type: 'number',
          name: '刷新时间',
          placeholder: `${t('单位：毫秒')}`,
          min: 0,
          step: 50,
        },
        {
          key: 'TDengine',
          type: 'text',
          name: `${t('地址')}*`,
        },
        {
          key: 'post',
          type: 'text',
          name: `端口号*`,
        },
        {
          key: 'username',
          type: 'text',
          name: '用户名*',
        },
        {
          key: 'password',
          type: 'password',
          name: '密码*',
        },
        {
          key: 'EquipmentMasterTable',
          type: 'text',
          name: '数据库名*',
        },
      ]
      return configs;
    });
    const TDengine = reactive<any>([]);
    const TDengineShow = computed(() => {
      return TDengine.length === 0 ? false : true;
    });
    const onTDengine = () => {
      meta2d.store.data.TDengine = [...encryptPassword(TDengine, 'TDengine')];
      connectTDenginesfix([...encryptPassword(TDengine, 'TDengine')]);
    };
    // 防抖处理
    const debounceMyTDengine = debounce(onTDengine, 1000)
    // RealTimeDataBase中刷新
    function getRefreshTDengine() {
      // todo 重新执行onTDengine函数
      onTDengine();
    }

    return {
      activeKey,
      activeKey2,
      mqttConfigs,
      mqttData,
      mqttShow,
      websocket,
      onMqtt,
      messageConfigs,
      messageData,
      socketFnChange,
      httpConfigs,
      httpData,
      httpShow,
      onHttp,
      websocketConfigs,
      websocketData,
      websocketShow,
      onWebsocket,
      visible,
      handleOk,
      showModal,
      delItem,
      changeName,
      changeFlag,
      getRefreshHttp,
      enterOnBlur,
      httpChange,
      websocketChange,
      confirm,
      formRef,
      formState,
      rules,
      RealTimeDataBaseConfigs,//实时数据库配置
      RealTimeDataBase,//实时数据变量
      RealTimeDataBaseShow,//是否展示实时数据
      onRealTimeDataBase,//建立连接
      getRefreshRealTimeDataBase,//刷新时间
      // RealTimeDataBaseChange
      debounceMyFunction,
      copyCurrent,
      TDengineConfigs,
      TDengine,
      TDengineShow,
      onTDengine,
      getRefreshTDengine,
      debounceMyTDengine
    };
  },
});
</script>

<style lang="scss" scoped>
.addCommunication {
  padding: 10px 5px 0;
  margin-bottom: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  /*.ant-btn-primary {
    background-color: transparent;
    border: 1px solid #4587ec;
    color: #5781f9;
  }*/
}

.ShowModel {
  :deep(.ant-modal-content) {
    background-color: #242630;
    color: #fff;
  }
  :deep(.ant-modal-header) {
    background-color: #242630;
    margin: 0 20px;
    padding-left: 0;
    border-bottom: 1px solid #34353e;
    .ant-modal-title {
      color: #9c9ea7;
    }
  }
  .ant-modal-body h6 {
    color: #9c9ea7;
  }
  :deep(.ant-modal-footer) {
    border-top: none;
    .ant-btn {
      border: none;
      background-color: #3e3f4b;
      color: #fff;
      &:last-child {
        background-color: #5781f9;
      }
    }
  }
  :deep(.ant-modal-close) {
    color: #9c9ea7;
  }
  .selectDataSourceForm {
    :deep(.ant-row) {
      flex-direction: column;
      .ant-form-item-label {
        text-align: left;
        label {
          color: #b4b7c1;
        }
      }
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
  .ant-btn {
    margin-left: 10px;
    border: none;
    background-color: transparent;
    color: #fff;
  }
  .ant-btn:last-child{
    margin-left: 0;
  }
}

// 下拉框样式
.selectStyle {
  :deep(.ant-select-dropdown) {
    background-color: #2e2f38 !important;
    color: #fff !important;
  }
  :deep(.rc-virtual-list-holder) {
    background-color: #2e2f38 !important;
    color: #fff !important;
  }
}
:deep(.ant-popover-content) {
  .ant-popover-inner {
    background: #3d404d;
    .ant-popover-message-title {
      color: #fff;
    }
    .ant-popover-buttons {
      display: flex;
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

// 通信面板样式
.PenProps>.customCollapse{
  &> :deep(.ant-collapse-content){
    padding: 0;
    overflow:visible;
    .ant-collapse{
      background-color: transparent;
      border: none;
    }
    &>.ant-collapse-content-box{
      padding-top: 0;
    }
  }
  
}
//二级手风琴间距
.PenProps{
  :deep(.ant-collapse){
    .ant-collapse-item{
      .ant-collapse-header{
        padding-left: 50px;
        .ant-collapse-arrow{
          left:30px
        }
      }
    }
  }
}
</style>
