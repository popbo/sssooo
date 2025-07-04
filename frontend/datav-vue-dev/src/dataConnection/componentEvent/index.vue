<template>
  <div class="componentEvent-box">
    <el-button size="mini" type="text" icon="el-icon-circle-plus-outline" @click="addTab()">
      添加事件
    </el-button>
    <el-button size="mini" type="text" icon="el-icon-delete" @click="removeTab()">
      删除事件
    </el-button>
    <el-tabs v-model="editableTabsValue" class="tabs tabs_pane" @tab-click="handleTabClick">
      <el-tab-pane
        v-for="item in currentEventObj.eventListChildren"
        :key="item.id"
        :label="item.eventType | transformNameforType"
        :name="item.id"
      >
        <el-form :model="item" label-width="90px">
          <el-form-item label="事件类型" prop="eventType">
            <el-select v-model="item.eventType" class="set-condition-select-ziduan" clearable>
              <el-option
                v-for="subItem in eventList"
                :label="subItem.name"
                :value="subItem.id"
                :key="subItem.id"
              >
              </el-option>
            </el-select>
          </el-form-item>
          <template v-if="item.eventType == 2 || item.eventType == 3">
            <el-form-item label="子类">
              <avue-select
                multiple
                v-model="item.showHideChild.index"
                :dic="allComList"
                :props="childProps"
                filterable
              >
              </avue-select>
            </el-form-item>
          </template>
          <template v-if="item.eventType == 4">
            <el-form-item label="请求类型">
              <avue-select v-model="item.serverChild.method" :dic="methodList" size="mini"> </avue-select>
            </el-form-item>
            <el-form-item label="请求地址">
              <avue-input v-model="item.serverChild.url" size="mini"> </avue-input>
            </el-form-item>
            <el-form-item label="认证方式">
              <el-select v-model="item.serverChild.authenticationMethod" placeholder="请选择" size="mini">
                <el-option
                  v-for="(item, index) in authenticationArr"
                  :key="index"
                  :label="item.label"
                  :value="item.value"
                ></el-option>
            </el-select>
            </el-form-item>
            <template v-if="item.serverChild.authenticationMethod === 'basicAuth'">
              <el-form-item label="认证地址">
                <avue-input v-model="item.serverChild.authenticationForm.authenticationUrl" size="mini"></avue-input>
              </el-form-item>
              <el-form-item label="用户名">
                <avue-input v-model="item.serverChild.authenticationForm.userName" size="mini"></avue-input>
              </el-form-item>
              <el-form-item label="密码">
                <avue-input v-model="item.serverChild.authenticationForm.password" size="mini"></avue-input>
              </el-form-item>
            </template>
            <div class="params_title">
              编辑参数
              <!-- <el-button size="mini" type="primary" @click="addParamItem(item)" style="margin-bottom: 10px" class="myBtn">
              新增
            </el-button> -->
            </div>
            <!-- <el-table :data="item.serverChild.params" border style="width: 310px; margin: 0 12px 10px" class="params_table">
              <el-table-column label="序号" width="50px" align="center">
                <template slot-scope="scope">
                  <span class="table-inner">{{ scope.$index + 1 }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="name" label="字段名" align="center">
                <template slot-scope="scope">
                   <el-input v-model="scope.row.name" placeholder="请输入字段名"></el-input>
                </template>
              </el-table-column>
              <el-table-column prop="val" label="字段值" align="center">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.val" placeholder="请输入字段值"></el-input>
                </template>
              </el-table-column>
              <el-table-column prop="action" label="操作" align="center" width="50px">
                <template slot-scope="scope">
                  <el-button type="text" icon="el-icon-delete" size="mini" @click="deleteItem(item,scope.$index)"></el-button>
                </template>
              </el-table-column>
            </el-table> -->
            <div class="paramJSON_edit_box" v-if="editableTabsValue === item.id">
              <monaco-editor v-model="item.serverChild.paramFun" :height="200"></monaco-editor>
            </div>
          </template>
          <template v-if="item.eventType == 1">
            <el-radio-group v-model="showApi" class="radio-group" @change="handleRadioGroupChange" size="mini">
              <el-radio label="static">静态联动</el-radio>
              <el-radio label="api">API联动</el-radio>
              <el-radio label="sql">SQL联动</el-radio>
              <el-radio label="websocket">websocket联动</el-radio>
            </el-radio-group>
             <el-form v-show="showApi === 'static'" label-width="120px" label-position="left" size="mini">
              <el-form-item label="子类">
                <avue-select
                  multiple
                  v-model="item.staticChild.index"
                  :dic="allComList"
                  :props="childProps"
                  filterable
                >
                </avue-select>
              </el-form-item>
            </el-form>
            <el-form v-show="showApi === 'api'" label-width="120px" label-position="left" size="mini">
              <el-form-item label="子类">
                <avue-select
                  multiple
                  v-model="item.child.index"
                  :dic="allComList"
                  :props="childProps"
                  filterable
                >
                </avue-select>
              </el-form-item>
              <el-form-item label="参数名称">
                <avue-input v-model="item.child.paramName"></avue-input>
              </el-form-item>
              <el-form-item label="键值名称">
                <avue-input v-model="item.child.keyName"></avue-input>
              </el-form-item>
            </el-form>
            <el-form v-show="showApi === 'sql'" label-width="120px" label-position="left" size="mini">
              <el-form-item label="子类">
                <avue-select
                  multiple
                  v-model="item.sqlChild.index"
                  :dic="allComList"
                  :props="childProps"
                  filterable
                >
                </avue-select>
              </el-form-item>
              <el-form-item label="维度">
                <avue-select v-model="item.sqlChild.fieldType" :dic="fieldTypeList"> </avue-select>
              </el-form-item>
              <div v-for="ssitem in item.sqlChild.index" :key="ssitem">
                <div>{{ ssitem | transformName }}</div>
                <componentSql
                  :componentId="ssitem"
                  v-model="item.sqlChild.filterObj[ssitem]" 
                  :childList="filterList(ssitem)"
                  :paramsType.sync="item.sqlChild.paramsType"
                ></componentSql>
              </div>
            </el-form>
            <div v-show="showApi === 'websocket'">
              <el-form label-width="120px" label-position="left" size="mini">
                <el-form-item label="子类">
                  <avue-select
                    multiple
                    v-model="item.websocketChild.index"
                    :dic="allComList"
                    :props="childProps"
                    filterable
                  >
                  </avue-select>
                </el-form-item>
              </el-form>
              <div class="params_title">
              编辑参数
              </div>
              <div class="paramJSON_edit_box">
                <monaco-editor v-model="item.websocketChild.paramFun" :height="200" ref="editor"></monaco-editor>
              </div>
            </div>
          </template>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
  // import { uuid } from '@/utils/utils';
  import { uuid } from '@/utils/utils.min.js';
  import componentSql from '@/dataConnection/componentSql';
  import { dicOption } from '@/option/config';
  import MonacoEditor from '@/page/components/editor'
  let __this = null;
  export default {
    name: 'componentEvent',
    props: {
      myActiveObj: {
        type: Object,
        default: function () {
          return {};
        },
      },
      childList: {
        type: Array,
        default: function () {
          return [];
        },
      },
      eventListType: {
        type: String,
        default: '',
      },
      authenticationArr: {
        type: Array,
        default: function () {
          return [];
        },
      }
    },
    data() {
      return {
        form: {
          eventType: 1,
        },
        fieldListData: [],
        eventList: [
          { name: '数据刷新', id: 1 },
          { name: '组件隐藏', id: 2 },
          { name: '组件显示', id: 3 },
          { name: '调用服务', id: 4 },
        ],
        editableTabsValue: '',
        activeObjInstance: {},
        uuidObj: '',
        childProps: {
          label: 'name',
          value: 'index',
        },
        fieldTypeList: [
          { label: '度量值', value: 1 },
          { label: '维度值', value: 2 },
        ],
        methodList: [
          { label: 'GET', value: 'GET' },
          { label: 'POST', value: 'POST' },
        ],
        showApi: 'static',
        rules: {
          name: [
            { required: true, message: "请输入字段名", trigger: "blur" },
          ],
          val: [{ required: true, message: '请输入字段值', trigger: 'blur' }],
        },
        currentEventObj: {},
        options: [{
          value: '1',
          label: '1'
        }, {
          value: '2',
          label: '2'
        },],
        value: ''
      };
    },
    watch: {
      activeObjInstance: {
        handler(newValue) {
          // console.log("newValuenewValuenewValue",newValue)
          this.$emit('updateActiveObj', newValue);
        },
        deep: true,
      },
      eventListType: {
        handler(newValue) {
          if (this.activeObjInstance.eventList) {
            let findItem = this.activeObjInstance.eventList.find(item => item.type === newValue)
            console.log('findItem==>', findItem)
            if (findItem) {
              this.currentEventObj = findItem
              } else {
                this.currentEventObj = {
                  type: newValue,
                  eventListChildren: []
                }
              }
          }
        }
      }
    },
    filters: {
      // 转义组件名称
      transformName: function (id) {
        // var p = __this.childList.filter((item) => {
        //   return item.index == id;
        // });
        var p = __this.allComList.filter((item) => {
          return item.index == id;
        });
        if (p.length > 0) {
          return p[0].name;
        } else {
          return '未知';
        }
      },
      transformNameforType: function (id) {
        var p = __this.eventList.filter((item) => {
          return item.id == id;
        });
        if (p.length > 0) {
          return p[0].name;
        } else {
          return '未知';
        }
      },
    },
    components: {
      componentSql,
      MonacoEditor
    },
    created() {
      this.uuidObj = uuid();
      this.editableTabsValue = this.uuidObj;
      __this = this;
      // console.log('this.myActiveObj', this.myActiveObj)
      this.activeObjInstance = this.deepClone(this.myActiveObj);
      
      
      if (this.activeObjInstance.eventList) {
           // 此段代码来向后兼容新加的数据刷新的静态联动，要不然之前那些配置了事件的大屏点添加事件会报错
            this.activeObjInstance.eventList.forEach(item_f => {
               if(item_f.eventListChildren) {
                 item_f.eventListChildren.forEach(item_s => {
                   if(!item_s.staticChild) item_s.staticChild = {
                     index: []
                   }
                 })
               }
            })
            let findItem = this.activeObjInstance.eventList.find(item => item.type === this.eventListType)
            if (findItem) {
              this.currentEventObj = findItem
              } else {
                this.currentEventObj = {
                  type: this.eventListType,
                  eventListChildren: []
                }
              }
          } else {
            this.currentEventObj = {
                  type: this.eventListType,
                  eventListChildren: []
              }
          }
      if (this.currentEventObj.eventListChildren &&this.currentEventObj.eventListChildren.length > 0) {
        this.editableTabsValue = this.currentEventObj.eventListChildren[0].id;
      }
    },
    mounted() {
      
    },
    destroyed() {},
    computed: {
      // 所有组件的列表包含tips中设置的
      allComList() {
        let arr = []
        const detail = list =>{
        list.forEach(item_f=>{
          arr.push(item_f)
            if(item_f.list){
              detail(item_f.list)
            }
          })
        }
        detail(this.childList)
        return arr
      }
    },
    methods: {
      removeTab() {
        // let tabs = this.myActiveObj.eventList;
        let tabs = this.currentEventObj.eventListChildren
        let activeName = this.editableTabsValue;
        let targetName = activeName;
        tabs.forEach((tab, index) => {
          let nextTab = tabs[index + 1] || tabs[index - 1];
          if (nextTab) {
            activeName = nextTab.id;
          }
          if (tab.id === targetName) {
            tabs.splice(index, 1);
          }
        });
        this.editableTabsValue = activeName;
        // this.myActiveObj.eventList = tabs.filter(tab => tab.name !== targetName);
      },
      addTab() {
        if (!this.activeObjInstance.eventList) {
          // 如果当前选中组件不存在eventList那么就要给该组件响应式的添加该属性
          console.log('addTab1')
          this.currentEventObj.eventListChildren.push(this.creatInstance(this.uuidObj))
          this.$set(this.activeObjInstance, 'eventList', [this.currentEventObj])
        } else {
          // 如果当前选中组件存在eventList，且当前要添加的事件类型之前已添加过也即eventListChildren存在且长度大于0
          if (this.currentEventObj.eventListChildren && this.currentEventObj.eventListChildren.length > 0 ) {
            console.log('addTab2')
            this.currentEventObj.eventListChildren.push(this.creatInstance())
          } else {
             // 如果当前选中组件存在eventList，且当前要添加的事件类型之前未添加过
             console.log('addTab3')
            this.currentEventObj.eventListChildren.push(this.creatInstance())
            this.activeObjInstance.eventList.push(this.currentEventObj)
          }
        }
      },
      creatInstance(uuidObj) {
        //  uuidObj 第一次生成是需要指定id
        return {
          id: uuidObj || uuid(),
          eventType: 1,
          staticChild: {
            index: [],
          },
          child: {
            index: [],
            paramName: '',
            keyName: '',
          },
          sqlChild: {
            index: [],
            filterObj: {},
            paramsType: 'bindValue'
          },
          websocketChild: {
            index: [],
            paramFun: `function(data){
    return {}
}`, // 按照原参数编辑的方法这样就无需对填写的格式有严格要求，譬如一切字符串都需要双引号
          },
          showHideChild: {
            index: [],
          },
          serverChild: {
            url: '',
            method: 'GET',
            // params: [],
            paramFun: `function(data){
    return {}
}`, // 按照原参数编辑的方法这样就无需对填写的格式有严格要求，譬如一切字符串都需要双引号
            authenticationMethod: 'noAuth',
            authenticationForm: {
              authenticationUrl: "",
              userName: "",
              password: "",
            }
          },
        };
      },
      filterList(id) {
        var p = this.allComList.filter((item) => {
          return item.index == id;
        });
        return p;
      },
      addParamItem(item){
        item.serverChild.params.push({name:'',val:''})
      },
      deleteItem(item,i){
        // console.log("deleteItem",item,i)
        item.serverChild.params.splice(i,1)
      },
      handleRadioGroupChange(val) {
        if(val === 'websocket') {
          this.$nextTick(() => {
            // this.$refs.editor[index].$refs.monacoEditor.editor && this.$refs.editor[index].$refs.monacoEditor.editor.dispose()
            // this.$refs.editor[index].$refs.monacoEditor.initMonaco()
            this.$refs.editor.forEach(
              item => {
                item.$refs.monacoEditor.editor && item.$refs.monacoEditor.editor.dispose()
                item.$refs.monacoEditor.initMonaco()
              }
            )
          })
        }
      },
      handleTabClick(event) {
      if(event.label === '数据刷新') {
        this.$nextTick(() => {
          // event是点击的tabs对应的实例，里面的index表明他是第几个
         this.$refs.editor.forEach(
              item => {
                item.$refs.monacoEditor.editor && item.$refs.monacoEditor.editor.dispose()
                item.$refs.monacoEditor.initMonaco()
              }
            )
          })
      }
      }
    },
  };
</script>

<style scoped lang="scss">
  .componentEvent-box {
    padding-top: 10px;
    .radio-group {
      margin: 0 auto 20px;
      // transform: translateX(50%);
      .el-radio-button__inner {
        background: #232630;
      }
    }
  }
  .el-radio {
    width: unset;
    margin-right: 16px;
    &:last-child {
      margin-right: 0px;
    }
  }
  // .addBtn {
  //   width: 100%;
  //   margin: 0 10px;
  // }
  /deep/.el-collapse-item__header {
    background-color: #2d2f38;
    border-top: 1px solid #393b4a;
    &:hover {
      .el-icon-edit {
        opacity: 1;
      }
    }
    .tj {
      width: 148px;
    }
    .el-input {
      width: 148px;
      height: 35px;
      line-height: 35px;
      .el-input__inner {
        height: 35px;
        line-height: 35px;
      }
    }
    .el-icon-edit {
      margin-left: 5px;
      opacity: 0;
    }
    .unsave {
      display: flex;
      align-items: center;
      flex: 1;
      margin-left: 6px;
      color: #999;
      .circle-red {
        width: 6px;
        height: 6px;
        border-radius: 50%;
        background: #ff3c38;
        margin-right: 4px;
      }
    }
  }
  /deep/.el-collapse-item__wrap {
    background-color: transparent;
  }
  .set-condition-input {
    width: 70px;
    margin-right: 20px;
  }
  .set-condition-select-ziduan {
    width: 170px;
    margin: 0 20px 0 0;
  }
  .set-condition-select {
    width: 80px;
    margin: 0 10px;
  }
  .el-icon-circle-plus-outline {
    font-size: 20px !important;
    cursor: pointer;
  }
  
  .el-table--border, .el-table--group {
    &:after {
      background-color: #181a24;
    }
  }
  .el-table {
    &:before {
      background-color: #181a24;
     
    }
  }
  .params_title {
    margin: 0 0 12px 12px;
    color: #fff;
  }
</style>
<style lang="scss">
  .componentEvent-box {
    .tabs_pane {
      &.tabs {
        .el-tabs__active-bar {
          top: auto;
          bottom: 0;
        }
        .el-tabs__item {
          padding: 0 20px;
        }
      }
    }
    .el-button {
      [class^='el-icon-'] {
        color: #409eff;
      }
    }
    .radio-group {
      .el-radio-button__inner {
        background: #3d404d;
        color: #b4b7c1;
        border: 1px solid #000;
      }
      .is-active {
        .el-radio-button__inner {
          color: #409eff;
          box-shadow: -1px 0 0 0 #000;
        }
      }
    }
  }
  .params_table td, .params_table th.is-leaf,.el-table--border, .el-table--group{
    border-color: #181a24;
    background-color: #181a24;
    color: white;
  }
  .params_table tbody tr:hover>td {
    background-color:#181a24!important
  }
  .paramJSON_edit_box {
    width: 100%;
    padding: 0 10px 20px;
  }
</style>
