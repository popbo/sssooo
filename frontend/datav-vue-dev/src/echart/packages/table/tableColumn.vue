<template>
    <el-table-column :label="coloumnHeader.label" :prop="coloumnHeader.label">
      <template v-for="(item,index) in coloumnHeader.children">
        <tableColumn
          :key="index"
          v-if="item.children && item.children.length"
          :coloumn-header="item"
          :component="component"
          :option="option"
          :tableOptions="tableOptions"
          :switchFalge="switchFalge"
          :drawConditionList="drawConditionList"
          @changetrainstation="changetrainstation"
          class="more-header"

        ></tableColumn>
        <template v-else>
          <el-table-column
            v-if="item.hide !== true"
            :show-overflow-tooltip="item.prop===option.currentColom&&option.tableSelect?false:true"
            :key="index"
            :prop="item.prop"
            :label="item.label"
            :width="
             item.width ? (item.width * component.width).toFixed(2) / 100 : ''
            "
          >
            <template slot-scope="{ row, column }">
              <span
                v-if="
                  row.extendObj.isUseSvg &&
                  row.extendObj.svgObj.location === 'wzq'
                "
                :class="[{ blink: row.extendObj.isBlink }]"
                v-html="row.extendObj.svgObj.link"
              >
              </span>
              <span
                v-if="row.extendObj.isShowText&&item.prop!==option.currentColom&&getSwitch(row,column,true)"
                :class="[{ blink: row.extendObj.isBlink }]"
                >{{ row[column.property] }}</span
              >
              <!--  <span
                v-if="row.extendObj.isShowText"
                :class="[{ blink: row.extendObj.isBlink }]"
                >{{ row[column.property] }}</span
              > -->
              <span
                v-if="
                  row.extendObj.isUseSvg &&
                  row.extendObj.svgObj.location === 'wzh'
                "
                :class="[{ blink: row.extendObj.isBlink }]"
                v-html="row.extendObj.svgObj.link"
              ></span>
              <span
                v-if="item.prop===option.currentColom&&!option.tableSelect&&getSwitch(row,column,true)"
                :class="[{ blink: row.extendObj.isBlink }]"
                >
                {{ row[column.property] }}
              </span>
               <el-switch
                v-if="getSwitch(row,column)"
                class="table-switch"
                :style="tableSwitch"
                v-model="row.switchFalge"
                @change="e => changetrainstation(e, row)"
                active-color="#13ce66"
                inactive-color="#ff4949">
              </el-switch>
              <el-select v-model="row[option.currentColom]" placeholder="请选择" v-if="item.prop===option.currentColom&&option.tableSelect">
                <el-option
                  v-for="item in tableOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </template>
          </el-table-column>
        </template>
      </template>
    </el-table-column>
  </template>
   
  <script>
  import TableColumn from "./tableColumn.vue";
  export default {
    name: "tableColumn",
    props: {
      coloumnHeader: {
        type: Object,
        required: true,
      },
      component:{
        type: Object,
        required: true,
      },
      option:{
        type: Object,
        required: true,
      },
      tableOptions:{
        type:Array,
        required: true,
      },
      switchFalge:{
        type:Boolean,
        default:false
      },
      drawConditionList:{
        type:Array,
        default:()=>{
          return []
        }
      }
    },
    data(){
      return {
      }
    },
    components:{
      TableColumn
    },
    computed:{
       tableSwitch(){
        return {
          display: 'inline-block',
          height: '100%',
          width: 'auto !important' 
        }
      }
    },
    methods:{
      changetrainstation(e,row) {
        this.emit('changetrainstation',e,row)
      },
      changetrainstation(e,row) {
        if (e) {
          this.updateClick(row, 'openTableClickFormatter')
        } else {
          this.updateClick(row, 'closeTableClickFormatter')
        }
      },
      getSwitch(row,column,other){
        let falge = false;
        let switchData = []
        this.drawConditionList.forEach(item=>{
          if(item.contentStyle==='swh'){
            switchData.push(item)
          }
        })
        let property = column.property
        for (let key in row) {
          let coincidentindex = this.parseCondition(
            switchData,
            key,
            row[key]
          )
          if (coincidentindex !== undefined) {
            let obj = switchData[coincidentindex]
            if(property === obj.drawObjSwitch){
              falge = true
            }
            if(row.switchFalge===undefined){
              if(obj.switch==='sk'){
                this.$set(row,'switchFalge',true)
              }
              if(obj.switch==='sg'){
                this.$set(row,'switchFalge',false)
              }
            }else{
              if(this.switchFalge){
                if(obj.switch==='sk'){
                  this.$set(row,'switchFalge',true)
                }
                if(obj.switch==='sg'){
                  this.$set(row,'switchFalge',false)
                }
              }
            }
        }
      }
      if(other){
        falge = !falge
      }
      return falge
      },
      parseCondition(arr, key, value) {
        // 思路是在arr条件中每个条件项都是或的关系，因此要筛选出那个dataChart.value满足的条件
        // coincidentindex保存满足的条件的index值
        let coincidentindex
        // 首先判断是否配置了渲染条件，如果配置了那么渲染条件的数组长度大于0
        if (arr.length > 0) {
          for (let i = 0; i < arr.length; i++) {
            let enterValue = arr[i].enterValue // 表达式的输入值是否是dataChart.value
            if (key === enterValue) {
              let filter = arr[i].filter
              if (filter.length > 0) {
                let logic = arr[i].logic // 条件分支之间的关系是且还是或  and 且  or 或
                let filterResult = this.handleFilter(
                  filter,
                  logic,
                  enterValue,
                  value
                )
                if (filterResult) {
                  coincidentindex = i
                  break
                }
              }
            }
            // 首先判断一下条件项有没有配置分支条件，即filter数组长度是否大于0
          }
          return coincidentindex
        } else {
          // 没有配置渲染条件数组
        }
      },
      handleFilter(filter, logic, enterValue, chartValue) {
        const isAllNumber = function (chartValue, itemValue) {
          let num1 = Number(chartValue)
          let num2 = Number(itemValue)
          return !isNaN(num1) && !isNaN(num2)
        }
        const switchFnc = function (item) {
          let result
          switch (item.term) {
            case 'eq': // '='
              result = chartValue === item.value
              break
            case 'ne': // '!='
              result = chartValue !== item.value
              break
            case 'lt': // '<' 如果是小于号，那么要看比较的两个值是否都是数字,这个比较只限于数字的比较
              if (isAllNumber(chartValue, item.value)) {
                result = Number(chartValue) < Number(item.value)
              } else {
                result = false
              }
              break
            case 'le': // '<='
              if (isAllNumber(chartValue, item.value)) {
                result = Number(chartValue) <= Number(item.value)
              } else {
                result = false
              }
              break
            case 'gt': // '>'
              if (isAllNumber(chartValue, item.value)) {
                result = Number(chartValue) > Number(item.value)
              } else {
                result = false
              }
              break
            case 'ge': // '>='
              if (isAllNumber(chartValue, item.value)) {
                result = Number(chartValue) >= Number(item.value)
              } else {
                result = false
              }
              break
          }
          return result
        }
        // 当条件分支间的关系为且
        if (logic === 'and') {
          // 如果为且的话那么就要求filter的每一项都返回true,那么可以选择数组的every方法
          const allResult = filter.every(item => {
            return switchFnc(item)
          })
          return allResult
        } else if (logic === 'or') {
          // 当条件分支间的关系为否
          const allResult = filter.some(item => {
            return switchFnc(item)
          })
          return allResult
        }
      },
    }
  };
  </script>
   
  <style scoped>
  </style>