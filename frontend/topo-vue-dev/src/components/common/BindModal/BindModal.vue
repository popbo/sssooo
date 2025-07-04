<template>
  <div class="dialogClass" ref="dialogBox">
    <a-modal
      :getContainer="() => $refs.dialogBox"
      :visible="visible"
      width="900px"
      :bodyStyle="{ height: '500px', overflowY: 'scroll' }"
      @ok="handleOk"
      @cancel="cancel"
    >
      <template #title>
        绑定变量
        <a-tooltip class="ml16" v-if="config.isTime != undefined">
          <template #title
            >默认：实时显示当前状态的最新数值，适用于文本数值显示、进度、柱状图等<br />
            历史趋势：记录一段时间内某个数据的变化轨迹，适用于数据变化趋势类型的折线图等</template
          >
          <a-checkbox v-model:checked="config.isTime">历史趋势</a-checkbox>
        </a-tooltip>
        <!-- Y 轴 分类 checkbox -->
        <template v-if="!config.isTime && config.isYCategory != undefined">
          <a-checkbox v-model:checked="config.isYCategory"
            >y轴分类轴</a-checkbox
          >
        </template>
      </template>
      <template v-if="config.multiple">
        <p>
          当前绑定：
          <template v-if="dataIds">
            <p
              style="display: inline-block"
              v-for="(_dataId, index) in dataIds"
            >
              {{ _dataId.dataId + (index === dataIds.length - 1 ? '' : ',') }}
            </p>
          </template>
          <template v-else>无</template>
        </p>
        <div class="tags">
          <template v-for="(tag, index) in checkedBinds">
            <a-tooltip v-if="tag.name.length > 15" :title="tag.name">
              <a-tag closable @close="onDelTag($event, index)">
                {{ `${tag.name.slice(0, 15)}...` }}
              </a-tag>
            </a-tooltip>
            <a-tag v-else closable @close="onDelTag($event, index)">{{
              tag.name
            }}</a-tag>
          </template>
          <a-input
            :placeholder="t('按回车添加')"
            v-model:value="searchValue"
            @pressEnter="onAddTag"
          />
        </div>
      </template>

      <template v-else>
        <p style="display: inline-block">
          <!-- 当前绑定：{{ dataId?.dataId || '无' }} -->
          <!-- 当前绑定：{{ dataId?.dataId || tagName }} -->
          当前绑定：{{ tagName }}
        </p>

        <!-- v-if="dataId?.dataId" -->
        <a-popconfirm
          title="确定删除?"
          v-if="tagName !== '无'"
          @confirm="delDataIds"
          overlay-class-name="screen-gantt-popover"
        >
          <i
            style="margin-left: 20px"
            class="hover delIcon t-icon t-shanchu delete"
          ></i>
        </a-popconfirm>

        <!-- 绑定通信--校验 根据key值来搜索-->
        <a-tree-select
          v-model:value="communicationValue"
          show-search
          style="width: 100%"
          :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
          placeholder="请搜索或选择要关联的数据源"
          tree-default-expand-all
          :class="{ treeSelectStyle: comflag, treeStyle: true }"
          @change="checkedValue"
        >
          <a-tree-select-node
            key="websocket"
            value="websocket"
            title="websocket"
            :selectable="false"
            v-if="websocketDate.length !== 0"
          >
            <a-tree-select-node
              :key="item.id + item.name"
              :value="item.id + '-' + item.name"
              :title="item.name"
              v-for="item in websocketDate"
            />
          </a-tree-select-node>
          <a-tree-select-node
            key="MQTT"
            value="MQTT"
            title="MQTT"
            :selectable="false"
            v-if="MQTTDate.length !== 0"
          >
            <a-tree-select-node
              :key="item.id + item.name"
              :value="item.name"
              :title="item.name"
              v-for="item in MQTTDate"
            />
          </a-tree-select-node>
          <a-tree-select-node
            key="HTTP"
            value="HTTP"
            title="HTTP"
            :selectable="false"
            v-if="httpDate.length !== 0"
          >
            <a-tree-select-node
              :key="item.id + item.name"
              :value="item.id + '-' + item.communicationProtocol + '|' + item.name"
              :title="item.name"
              v-for="item in httpDate"
            />
          </a-tree-select-node>
          <a-tree-select-node
            key="RealTimeDataBase"
            value="RealTimeDataBase"
            title="UniRTDB"
            :selectable="false"
            v-if="RealTimeDataBase.length !== 0"
          >
            <a-tree-select-node
              :key="item.id + item.name"
              :value="
                item.id + '-' + item.communicationProtocol + '|' + item.name
              "
              :title="item.name"
              v-for="item in RealTimeDataBase"
            />
          </a-tree-select-node>
           <a-tree-select-node
              key="TDengine"
              value="TDengine"
              title="TDengine"
              :selectable="false"
              v-if="TDengine.length !== 0"
            >
              <a-tree-select-node
                :key="item.id + item.name"
                :value="item.id + '-' + item.communicationProtocol + '|' + item.name
                  "
                :title="item.name"
                v-for="item in TDengine"
              />
            </a-tree-select-node>
        </a-tree-select>

        <div class="text" v-show="comflag">*请搜索或选择要关联的数据属性</div>
        <a-input
          v-show="showTree"
          v-model:value="searchValue"
          style="margin-bottom: 8px"
          :class="{ styleSearch: true, aSearch: flag }"
          :placeholder="placeholderWord"
          @blur="onBlur"
        />

        <div :class="{ text: flag }" v-show="flag && showTree">
          *请搜索或选择要关联的数据属性
        </div>
      </template>

      <!-- v-show  由输入框是否处在聚焦状态来显示隐藏 -->
      <a-tree
        v-show="showTree"
        :tree-data="testDate"
        @select="handleSelect"
        v-model:selectedKeys="selectedKeys"
        v-model:checkedKeys="checkedKeys"
        v-model:expandedKeys="expandedKeys"
        v-model:auto-expand-parent="autoExpandParent"
        :replaceFields="{
          key: 'id',
        }"
        :checkable="config.multiple"
        @check="handleCheck"
      >
        <template #title="{ dataRef }">
          <span>{{ dataRef.name }}</span>
        </template>
      </a-tree>
    </a-modal>
  </div>
</template>

<script lang="ts">
// import {Meta2d} from '@topometa2d/core';
// declare const meta2d: Meta2d;
import { Meta2d, Meta2dStore, Meta2dData } from '@topometa2d/core';
declare const meta2d: Meta2d & {
  store: Meta2dStore & {
    data: Meta2dData & {
      mqttData: any[];
      websocketDate: any[];
      RealTimeDataBase: any[];
      TDengine: any[];
    };
  };
};
</script>

<script setup lang="ts">
import { message } from 'ant-design-vue';
import {
  ref,
  inject,
  watch,
  PropType,
  Ref,
  computed,
  watchEffect,
  reactive,
  toRefs,
  onMounted,
  onUnmounted,
  nextTick
} from 'vue';
import { useBindData } from './bindModal';
import { BindId } from '@topometa2d/core';
import { FormItemType } from '@/components/utils';
import { useStore } from 'vuex';
import Bus from '../bus';
import {Decrypt} from '@/utils/encryption'
// 接口
import { getTreeDate } from '@/api/baindModel';
import { getRealTimeDataBaseServerTreeDate , getTDengineServerTreeDate } from '@/api/communication';
const props = defineProps({
  visible: {
    type: Boolean,
    require: true,
  },
  dataId: {
    type: Object as PropType<BindId>,
  },
  dataIds: {
    type: Array as PropType<BindId[]>,
  },
  config: {
    type: Object as PropType<FormItemType>,
  },
  pen: {
    type: Object,
  },
});

const emit = defineEmits(['update:visible', 'chooseId']);
const node: Ref<any> = ref({});
function handleSelect(selectedKeys, e) {
  node.value = e.node;
  if (e.selected) {
    if (e.node.children.length == 0) {
      searchValue.value = e.node.eventKey;
    }
  } else {
    searchValue.value = '';
  }
}

function handleOk() {
  // 校验
  if (
    communicationValue.value?.trim() === '' ||
    communicationValue.value === undefined
  ) {
    comflag.value = true;
    return;
  }
  if (searchValue.value.trim() === '') {
    onBlur();
    return;
  }

  //分割communicationValue
  const e= communicationValue.value
  let num1 = e.lastIndexOf('-');
  let num2 = e.indexOf('|');
  let communicationProtocolId = Number(e.slice(0, num1)) || e.slice(0, num1);
  let communicationProtocolName = e.slice(num1+1, num2);
  if (props.config.multiple) {
    // 多勾选
    checkedHandleOk(emit);
  } else {
    // 单选
    const selected = selectedKeys.value[0];
    let dataId = null;
    let name = null;
    if (selected) {
      if (node.value.eventKey === selected) {
        if (Array.isArray(node.value.children) && node.value.children.length) {
          // 存在孩子
          message.warn('请选择叶子节点');
          return;
        }
        dataId = selected;
        name = node.value.dataRef.name;
      } else {
        const selectNode = treeleafs.value.filter(
          (item) => item.id === selected
        );
        if (selectNode.length > 0) {
          dataId = selected;
          name = selectNode[0].name;
        }
      }

      // 拼接
      // dataId = str + '-' + dataId;
    } else {
      if (searchValue.value.trim() !== '') {
        dataId = searchValue.value;
        name = searchValue.value;
      }
    }

    if (dataId && name) {
      emit('chooseId', {
        ids: {
          dataId,
          name,
          // TODO 3、把解析好的协议种类和数据池id带上(无需考虑协议不选只选设备的情况)
          // communicationProtocolName:HTTP|RealTimeDataBase|wesocket|MQQT|
          // communicationProtocolId:
          communicationProtocolId:communicationProtocolId,
          communicationProtocolName:communicationProtocolName,
          // tag-和设备号保持一致
          tag:dataId
        },
      });

      emit('update:visible', false);
    }
  }
  //点击绑定后保存当前图元id 通信协议id，设备id，到vuex中
  // 绑定时判断当前pen.form有几个属性: key的个数和form的长度有关
  //当 dataId都不同时才添加
  store.commit('souceDate/setBindDate', {
    id: props.pen.id, //图元id
    data: props.pen.form, //设备id
  });

  Bus.$emit('selectDataScouse', 'http'); //通知penProps中Date.vue去更新动态更新
}

function cancel() {
  emit('update:visible', false);
}

const selectedKeys = ref<string[]>([]);

let stopVisible=null
stopVisible = watch(
  () => props.visible,
  (newV) => {
    if (newV) {
      searchValue.value = '';
      // setStyleDisplay(searchValue.value, treeData.value);
      setStyleDisplay(searchValue.value, testDate.value);
      if (!props.config.multiple) {
        selectedKeys.value = [];
        props.dataId?.dataId && selectedKeys.value.push(props.dataId.dataId);
      } else {
        initChecked(props.dataIds);
      }
      flag.value = false;
      getRealTimeDataBaseTree()
    }
  }
);

const expandedKeys = ref<string[]>([]);
const autoExpandParent = ref<boolean>(true);
const searchValue = ref('');

//触发 设备变量回显
Bus.$on('changeBindValue', (obj) => {
  //TODO 4、只有id   console.log(obj)中有协议的Id可以用来重写回显
  if (obj.dataIds) {
    // console.log(obj,obj.dataIds.communicationProtocolName)
    switch (obj.dataIds.communicationProtocolName) {
      case 'HTTP':
        httpDate.value.find((item) => {
          if (item.id === obj.dataIds.communicationProtocolId) {
          communicationValue.value = item.id + '-' + item.communicationProtocol + '|' + item.name; //和node的key格式保持一致
          str.value=item.name//tag显示问题
          communicationProtocolName.value=item.communicationProtocol//回显的设备树问题
          // communicationProtocolId.value=item.id
          }
        });
        break;
     case 'RealTimeDataBase':
        RealTimeDataBase.value.find((item) => {
          if (item.id === obj.dataIds.communicationProtocolId) {
          communicationValue.value = item.id + '-' + item.communicationProtocol + '|' + item.name; //和node的key格式保持一致
           str.value=item.name//tag显示问题
           communicationProtocolName.value=item.communicationProtocol//回显的设备树问题
          communicationProtocolId.value=item.id
          }
        });
        break;
      case 'TDengine':
        TDengine.value.find((item) => {
          if (item.id === obj.dataIds.communicationProtocolId) {
            communicationValue.value = item.id + '-' + item.communicationProtocol + '|' + item.name; //和node的key格式保持一致
            str.value = item.name//tag显示问题
            communicationProtocolName.value = item.communicationProtocol//回显的设备树问题
            communicationProtocolId.value = item.id
          }
        });
        break;
      default:
        break;
    }
    nextTick(()=>{
       searchValue.value = obj.dataIds.dataId //设备变量回显
    })
  }
});

let searchTimer: any = 0;
watch(searchValue, (value) => {
  if (value!==undefined&&value.trim() === '') {
    expandedKeys.value = [];
    searchValue.value = '';
    selectedKeys.value = [];
    setStyleDisplay(value, testDate.value);
    clearTimeout(searchTimer);
    return;
  }
  if (searchTimer) {
    clearTimeout(searchTimer);
  }
  searchTimer = setTimeout(() => {
    const expanded = treeleafs.value.filter(
      (item) => item.name.indexOf(value) > -1 || item.id.indexOf(value) > -1
    );
    let temExpendKeys = [];
    let temSelectKeys = [];
    expanded &&
      expanded.forEach((item) => {
        temExpendKeys.push(...item.parentKeys);
        temSelectKeys.push(item.id);
      });
    setStyleDisplay(value, testDate.value);
    expandedKeys.value = temExpendKeys;
    if (!selectedKeys.value.includes(searchValue.value)) {
      selectedKeys.value = [];
    }
    // searchValue.value = value;
    // selectedKeys.value = temSelectKeys;
  }, 1000);


  
});

const bindTags = ref([]);
function onDelTag(e: any, index: any) {
  e.preventDefault();
  // bindTags.value.splice(index, 1);
  checkedBinds.value.splice(index, 1);
  checkedKeys.value = checkedBinds.value.map((item) => {
    return item.dataId;
  });
  //treeData修改
}

function onAddTag() {
  // bindTags.value.push(searchValue.value);
  if (!checkedBinds.value) {
    checkedBinds.value = [];
  }
  checkedBinds.value.push({
    dataId: searchValue.value,
    name: searchValue.value,
  });
  searchValue.value = '';
}

const setStyleDisplay = (key, nodes) => {
  let nums = 0;
  for (let data of nodes) {
    let bl = false;
    if (
      data.id.indexOf(key) > -1 || //判断key 在data.id中是否存在,没有就是-1
      data.name.indexOf(key) > -1 ||
      key === ''
    ) {
      bl = true;
      nums += 1;
    }
    if (!!data.children) {
      if (data.children.length > 0) {
        let count = setStyleDisplay(key, data.children);
        // 如果想 子节点有key 即使父节点没有key也显示的话 添加下面的语句
        nums += count;
        if (!bl && count == 0) {
          bl = false;
        } else {
          bl = true;
        }
      }
    }
    if (bl) {
      delete data['style'];
    } else {
      data['style'] = 'display: none';
    }
  }
  return nums;
};

// 删除
const delDataIds = () => {
  emit('chooseId', {
    ids: {
      // dataId: '',
      // name: '',
    },
    type: 'delete',
  });
  // 清空搜索值
  communicationValue.value = undefined;
  searchValue.value = '';

  // 清除对应的图元-->覆盖保存最新的
  store.commit('souceDate/setBindDate', {
    id: props.pen.id, //图元id
    data: props.pen.form, //设备id---->TODO如果未绑值，但是tags已经有部分属性怎么此时再次绑定就会覆盖原来的值
  });
};
const {
  binds: treeData,
  treeleafs,
  checkedKeys,
  handleCheck,
  checkedHandleOk,
  initChecked,
  checkedBinds,
} = useBindData(props);

// 测试数据testDate选择当communicationValue选择的是http就显示http相关设备
// 实时数据库的相关设备显示的前提条件
// 1、communicationValue得是实时数据库类型2、有已经配置好的设备表------》不一定
const testDate = computed(() => {
  if(communicationProtocolName.value==='HTTP'){  
    return [...treeData.value]
  }
  if(communicationProtocolName.value==='RealTimeDataBase'){ 
  return [...RealTimeDataBaseTree.value]
  }
  if (communicationProtocolName.value === 'TDengine') {
    return [...TDengineTree.value]
  }
  return [];
});

// 模态框选择
const store = useStore();

// communicationValue树的数据--->当本地没有，vuex也没有同步过来时
const httpDate = computed(() => {
  // return store.state.user.HTTPDate;
  return store.state.user.HTTPDate.length !== 0
    ? store.state.user.HTTPDate
    : meta2d.store.data.https !== undefined
    ? [...meta2d.store.data.https]
    : store.state.user.HTTPDate;
});
const websocketDate = computed(() => {
  return [];
});
const MQTTDate = computed(() => {
  return [];
});
const RealTimeDataBase = computed(() => {
  return store.state.user.RealTimeDataBase.length !== 0
    ? store.state.user.RealTimeDataBase
    : meta2d.store.data.RealTimeDataBase !== undefined
    ? [...meta2d.store.data.RealTimeDataBase]
    : store.state.user.RealTimeDataBase;
});
const TDengine = computed(() => {
  return store.state.user.TDengine.length !== 0
    ? store.state.user.TDengine
    : meta2d.store.data.TDengine !== undefined
      ? [...meta2d.store.data.TDengine]
      : store.state.user.TDengine;
});

const communicationValue = ref<string>(undefined);

const showTree = computed(() => {
  return communicationValue.value !== undefined ? true : false;
});

// 如果通信输入框为空,重置下面输入框的值
let stopWatch=null
let RealTimeDataBaseTree=ref<any>([])
let TDengineTree = ref<any>([])
stopWatch=watch(communicationValue, async (newval) => {
  if (newval === undefined) searchValue.value = '';
    searchValue.value = ''  
    getRealTimeDataBaseTree()
});

// 封装tree请求函数
const getRealTimeDataBaseTree= async ()=>{
  if(communicationProtocolName.value==="RealTimeDataBase"){
    // 发请求拿设备树1、主动选择2、回显选择
    try {
    // TODO目前设备数据未获取用测试数据代替 id协议
    // 根据id查找对象配置信息, 发送确认连接请求
    let arr = meta2d.store.data.RealTimeDataBase.filter(item=>{
      return item.id===communicationProtocolId.value
    })    
    if(arr.length===0) return 
    const{EquipmentMasterTable,EquipmentSubTable,RealTimeDataBase,post,username,password}= arr[0]
    if (!(RealTimeDataBase&&post&&username&&password&&EquipmentMasterTable)) return RealTimeDataBaseTree.value.length = 0
    if(EquipmentMasterTable.trim().length===0) return 
    const resTest: Response = await fetch(`/api/rtdb/connect/server?serverIp=${RealTimeDataBase}&serverPort=${post}&serverUser=${username}&serverPwd=${password}&devMainTable=${EquipmentMasterTable}&devSubTable=${EquipmentSubTable}`);
    const data = await resTest.text();
    if(!JSON.parse(data).success) return//如果返回false就不调用
    const res= await getRealTimeDataBaseServerTreeDate(EquipmentMasterTable,EquipmentSubTable)
    // console.log('设备树',res);
       RealTimeDataBaseTree.value.length=0
       RealTimeDataBaseTree.value.push(...(res as any))    
    //  RealTimeDataBaseTree.value.push({id:"S01_02_CDZ_2",name:"黄石大道站:上行充电桩",children:[{id:"S01_02_CDZ_2_AI_JXDY",name:"进线电压"},{id:"S01_02_CDZ_2_AI_JXDL",name:"进线电流"},{id:"S01_02_CDZ_2_AI_SCDY",name:"输出电压"},{id:"S01_02_CDZ_2_AI_SCZDL",name:"输出总电流"}]})
  } catch (error) {
    console.log('warn',error);
  }
  }else if(communicationProtocolName.value === "HTTP"){
      try {
      // 根据id查找对象配置信息, 发送确认连接请求
      let obj = meta2d.store.data.https.find(item =>  (item as any).id === communicationProtocolId.value)
      if (!obj||!obj.http) return treeData.value.length = 0
      const {http} = obj
      const res = await getTreeDate(http)
      treeData.value.length = 0
      treeData.value.push(...(res as any))
    } catch (error) {
      console.log('warn', error);
    }
  }else if (communicationProtocolName.value === "TDengine") {
     // 发请求拿设备树1、主动选择2、回显选择
    try {
      // TODO目前设备数据未获取用测试数据代替 id协议
      // 根据id查找对象配置信息, 发送确认连接请求
      let arr = meta2d.store.data.TDengine.filter(item => {
        return item.id === communicationProtocolId.value
      })
      if (arr.length === 0) return
      const { EquipmentMasterTable, TDengine, post, username, password } = arr[0]
      if (!(TDengine && post && username && password && EquipmentMasterTable)) return TDengineTree.value.length = 0
      if (EquipmentMasterTable.trim().length === 0) return
      const resTest: Response = await fetch(`/api/rtdb/tdengine/connect/server?serverAddr=${TDengine}&serverPort=${post}&serverUser=${username}&serverPwd=${password}&dbName=${EquipmentMasterTable}`, {
        headers: {
          Authorization: `${localStorage.getItem('token')}`,
        },
      });
      const data = await resTest.text();
      if (!JSON.parse(data).success) return//如果返回false就不调用
      const res = await getTDengineServerTreeDate()
      TDengineTree.value.length = 0
      TDengineTree.value.push(...(res as any))
    } catch (error) {
      console.log('warn', error);
    }
  }
}

const tagName = computed(() => {
  //TODO 2、拼接显示名称
  if (communicationValue.value !== undefined && searchValue.value !== '') {
    return `${str.value}` + '-' + `${searchValue.value}`;
  } else if (
    communicationValue.value !== undefined &&
    searchValue.value === ''
  ) {
    return `${str.value}`;
  } else {
    return '无';
  }
});

// 监听dataId
let stopDataId=null
stopDataId=watch(
  () => props.config.dataIds,
  (nval) => {
    if (nval === undefined) {
      // 清空搜索值
      communicationValue.value = undefined;
      searchValue.value = '';
    }
  }
);

//communicationValu校验和解析'
let communicationProtocolId=ref<Number>(); //绑定协议的 id
let communicationProtocolName=ref<String>(); //绑定协议的类型
let str = ref<String>(); // 用于显示的名称
const comflag = ref(false);
function checkedValue(e) {
  // TODO，1、解析communicationValue通过id获取name,id，和协议种类 communicationValue=id+协议类型
  if (e !== '') {
    comflag.value = false;

    let num1 = e.lastIndexOf('-')
    let num2 = e.indexOf('|');
    communicationProtocolId.value = Number(e.slice(0, num1))|| e.slice(0, num1);
    communicationProtocolName.value = e.slice(num1+1, num2);
    str.value = e.slice(num2 + 1); //保留name.去掉id，类型
  } else {
    comflag.value = true;
  }
}

// searchValue校验
let flag = ref(false);
function onBlur() {
  if (searchValue.value.trim() === '') {
    flag.value = true;
  } else {
    flag.value = false;
  }
}
let stopSearchValue=null
stopSearchValue=watch(searchValue, (value) => {
  if (value!==undefined&&value.trim() !== '') {
    flag.value = false;
  } else {
    flag.value = true;

    // 删除后重置校验
    if (communicationValue.value === undefined) {
      flag.value = false;
    }
  }
});

const placeholderWord=computed(()=>{
  if(flag.value && showTree.value) {
    return ''
  }
  return '请搜索或选择要关联的数据属性'
})


onUnmounted(()=>{
  stopWatch()//销毁监视
  stopDataId()
  stopSearchValue()
  stopVisible()
  Bus.$off('changeBindValue')
})
</script>

<style lang="scss" scoped>
.tags {
  margin-bottom: 10px;
  min-height: 30px;
  display: flex;
  flex-wrap: wrap;

  .ant-tag {
    margin: 6px;
  }

  .ant-input {
    min-width: 200px;
    flex: 1;
  }
}
.styleSearch {
  margin-top: 10px;
}
.aSearch {
  border: 1px solid red !important;
  box-sizing: border-box;
  padding: 4px 11px;
}
.text {
  color: red;
}
:global(.treeSelectStyle) {
  border: 1px solid red;
}
:global(
    .ant-select-focused:not(.ant-select-disabled).ant-select:not(
        .ant-select-customize-input
      )
      .ant-select-selector
  ),
:global(.ant-select:not(.ant-select-disabled):hover .ant-select-selector) {
  border-color: transparent;
}

.dialogClass {
  :deep(.ant-modal-content) {
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
    .ant-modal-body {
      color: #b4b7c1;
      .ant-input,
      .ant-input-search {
        background: #181a24;
        color: #b4b7c1;
        border: 1px solid #2e303d;
      }
      .ant-input-search {
        margin-top: 20px;
        .ant-input {
          border: none;
        }
        .ant-input-search-icon {
          color: #b4b7c1;
        }
      }
      .ant-tree {
        li {
          color: #b4b7c1;
          .ant-tree-node-content-wrapper {
            color: #b4b7c1;
            &:hover {
              background: none;
              color: #1890ff;
            }
            &.ant-tree-node-selected {
              background: none;
              color: #1890ff;
            }
          }
        }
      }
      .ant-select-clear {
        color: #fff;
        background: transparent;
      }
    }
  }
}
</style>
