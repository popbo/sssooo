<template>
  <div :class="b()">
    <button
      @mouseenter="handleEnter"
      @mouseleave="handleleave"
      @blur="handleBlur"
      @focus="handleFocus"
      @click="handleClick"
      @dblclick="handleDbClick"
      :style="[styleName, hoverStyle, focusStyle, isShow]"
      :disabled="!this.option.enable"
      :class="this.option.enable ? '' : 'is-disabled'"
    >
      {{ dataChart.value }}
    </button>
  </div>
</template>

<script>
import create from '../../create'
import COMMON from '@/config'
import { deepClone } from '../../util'
let bgColor1, bgColor2
import {getExcel} from '../../../api/visual.js'
export default create({
  name: 'functionButton',
  inject: ['contain'],
  data() {
    return {
      hoverStyle: {},
      focusStyle: {},
      currentComponents: '',
      componentData: [],
      startSequence:false,
      nameProportion:''
    }
  },
  props: {
    option: Object,
    component: Object,
    activeIndex:String
  },
  created() {
    // const _that = this
    // this.$bus.$on('componentList', componentList => {
    //   console.log('功能按钮组件接收到列表', componentList)
    //   this.componentList = componentList
    // })
    // 监听其它表格数据变化及时更新数据
    this.$bus.$on('dataChange', () => {
      // console.log('功能按钮监听到表格数据更新')
      // _that.handleGetData(_that.componentIds)
    })
  },
  methods: {
    handleClick() {
      if(this.option.functionType==='selectData'){
        const { value } = this.option.associatedComponent;
        let allComponents = this.getItemRefs()
        if(allComponents[value]){
          let tsData = []
          if(allComponents[value].option.selectTableData){
            tsData = allComponents[value].option.selectTableData;
            let getTdata = []
            if(this.option.tableHeaderType==='whole'){
              getTdata = tsData;
            }else{
              tsData.forEach(item=>{
                if(item[this.option.tableHeaderType]){
                  getTdata.push(item[this.option.tableHeaderType])
                }
              })
            }
            this.updateClick({
              value: this.dataChart.value,
              selectTableData:getTdata
            }, 'clickFormatter')
          }else{
            this.updateClick({
              value: this.dataChart.value,
              selectTableData:[]
            }, 'clickFormatter')
          }
        }
      }else{
        //1.处理组件中定义的点击事件
        this.handleFunctionBtn(this.option.associatedComponent)
        //2.处理事件中定义的点击事件
        this.updateClick(
          {
            value: this.dataChart.value,
          },
          'clickFormatter'
        )
        this.clickFormatter &&
          this.clickFormatter(
            {
              data: this.dataChart,
            },
            this.getItemRefs()
          )
      }
    },
    handleDbClick() {
      this.updateClick(
        {
          value: this.dataChart.value,
        },
        'dblClickFormatter'
      )
      this.clickFormatter &&
        this.clickFormatter(
          {
            data: this.dataChart,
          },
          this.getItemRefs()
        )
    },
    handleFunctionBtn(component) {
      console.log('当前需要导出的组件',this.option, component)
      const { value, name } = component
      if (!component || component.length === 0) {
        return this.$message.warning('未选择需要操作的组件', 2000)
      }
      if (name !== 'table') {
        return this.$message.warning('当前仅支持导出表格组件数据', 2000)
      }
      // 获取选中的组件数据
      this.handleGetData(value)
      switch (this.option.functionType) {
        case 'leadingIn':
          console.log('功能按钮执行导入')
          break
        case 'leadingOut':
          console.log('功能按钮执行导出')
          // console.log('要导出的数据', this.option.leadingOutData)
          this.dataToExcel(this.option.leadingOutData, this)
          break
        case 'download':
          console.log('功能按钮执行下载')
          break
        case 'upload':
          console.log('功能按钮执行上传')
          break
        case 'print':
          console.log('功能按钮执行打印')
          break
        default:
          console.log('功能按钮未定义功能')
          return this.$message.warning('功能按钮未定义功能', 2000)
      }
    },
    // 导出为excel（每个组件数据合并为一个文件，多表）
    dataToExcel(data, self) {
      let oneflage = true;
      let list = data[0]
      if(list.column){
        list.column.forEach(item=>{
          if(item.children){
            oneflage = false
          }
        })
      }
      if(oneflage){
        const leadingData = deepClone(data)
        let excelDatas = leadingData.map((item, index) => {
        let headerData = item.column.map(itemName => itemName.label)
        if(this.startSequence){
          headerData.unshift(this.nameProportion)
        }
        let keys = item.column.map(itemName => itemName.prop)
        return {
          // sheet表一头部
          tHeader: headerData,
          // 表一的数据字段
          filterVal: keys,
          // 表一的整体json数据
          tableDatas: item.data, // 数据
          // 表一的sheet名字
          sheetName: `sheet${index + 1}`,
        }
      })
        // console.log('要导出的数据形式', excelDatas)
        import('./utils/Export2MultipleSheetExcel')
          .then(excel => {
            let tHeader = []
            let dataArr = []
            let sheetnames = []
            for (let i = 0; i < excelDatas.length; i++) {
              tHeader.push(excelDatas[i].tHeader)
              sheetnames.push(excelDatas[i].sheetName)
              dataArr.push(
                self.getformatJson(
                  excelDatas[i].filterVal,
                  excelDatas[i].tableDatas
                )
              )
            }
            let exportName = this.option.exportName || '表格组件导出数据'
            excel.export_json_to_excel({
              header: tHeader,
              data: dataArr,
              sheetname: sheetnames,
              filename: exportName,
              autoWidth: true,
              bookType: 'xlsx',
            })
          })
          .catch(() => {
            this.$message({
              message: '获取表格数据失败，请稍等重试...',
              type: 'error',
            })
          })
      }else{
        let exportName = this.option.exportName || '表格组件导出数据'
        let parms = {
          name:exportName,
          data:{
            column:list.column,
            data: list.data
          }
        }
        getExcel(parms).then(res=>{
          console.log(res)
          if(res.data){
            if(res.data.data){
              window.open(res.data.data)
            }
          }
        })
      }
    },
    // 导出为excel（每个组件数据单独一个文件）
    dataToExcelSingleFile({ column, data }, index) {
      const base64 = s => window.btoa(unescape(encodeURIComponent(s)))
      let jsonData = data
      let str = column.reduce((target, item) => {
        return target + `<td>${item.label}</td>`
      }, '')
      let header = `<tr>${str}</tr>`
      console.log('first', header)
      for (let i = 0; i < jsonData.length; i++) {
        header += '<tr>'

        for (const key in jsonData[i]) {
          // 增加\t为了不让表格显示科学计数法或者其他格式
          header += `<td>${
            jsonData[i][key] === null ? '' : jsonData[i][key] + '\t'
          }</td>`
        }
        header += '</tr>'
      }
      // Worksheet名
      const worksheet = `表格${index + 1}`
      const uri = 'data:application/vnd.ms-excel;base64,'

      // 下载的表格模板数据
      const template = `<html xmlns:o="urn:schemas-microsoft-com:office:office"
        xmlns:x="urn:schemas-microsoft-com:office:excel"
        xmlns="http://www.w3.org/TR/REC-html40">
        <head><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet>
        <x:Name>${worksheet}</x:Name>
        <x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet>
        </x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]-->
        </head><body><table>${header}</table></body></html>`
      // 下载模板
      window.location.href = uri + base64(template)
      const link = document.createElement('a')
      link.href = uri
      // 对下载的文件命名
      link.download = `表格组件导出数据${index + 1}.xls`
      link.click()
    },
    getformatJson(filterVal, jsonData) {
      let list = []
      jsonData.forEach((item,index)=>{
        let typeList = []
        filterVal.forEach(itea=>{
          typeList.push(item[itea])
        })
        if(this.startSequence){
            typeList.unshift(index+1)
          }
        list.push(typeList)
      })
      return list
    },
    handleEnter() {
      if (this.option.hoverIsUse) {
        this.hoverStyle = {
          color: this.option.hoverStyle.color,
          fontSize: this.setPx(this.option.hoverStyle.fontSize || 30),
          background: this.option.hoverStyle.bgColor,
          borderColor: this.option.hoverStyle.borderColor,
        }
      }
    },
    handleleave() {
      if (this.option.hoverIsUse) {
        this.hoverStyle = {}
      }
    },
    handleFocus() {
      if (this.option.focusIsUse) {
        this.focusStyle = {
          color: this.option.focusStyle.color,
          fontSize: this.setPx(this.option.focusStyle.fontSize || 30),
          background: this.option.focusStyle.bgColor,
          borderColor: this.option.focusStyle.borderColor,
        }
      }
    },
    handleBlur() {
      if (this.option.focusIsUse) {
        this.focusStyle = {}
      }
    },
    // 获取实例中的数据
    handleGetData(index) {
      if (index) {
        const allComponents = this.getItemRefs()
        const needComponets = {}
        // this.componentIds.forEach(id => {
        // if (id in allComponents) {
        //   needComponets[id] = allComponents[id]
        // }
        // })
        if (index in allComponents) {
          needComponets[index] = allComponents[index]
        }
        let dataCharts = []
        let key
        let startSequence = false
        let nameProportion = ''
        for (key in needComponets) {
          // 提前表格行数据
          // 如果dataChart为数组直接赋值，为对象赋值为其data属性
          let data = Array.isArray(needComponets[key]._data.dataChart)
            ? needComponets[key]._data.dataChart
            : needComponets[key]._data.dataChart.data
          if (!!needComponets[key].column) {
            dataCharts.push({ column: needComponets[key].option.column, data })
            startSequence = needComponets[key].option.startSequence
            nameProportion = needComponets[key].option.nameProportion
          }
        }
        
        const cloneData = deepClone(dataCharts)
        this.option.leadingOutData = cloneData
        this.startSequence = startSequence
        this.nameProportion = nameProportion
      }
      // console.log('获取数据', index)
    },
  },
  watch: {
    componentsInfo: {
      handler(newValue) {
        if (newValue.length > 0) {
          console.log('当前画布上的组件列表', newValue)
          this.option.componentList = newValue
        }
      },
      deep: true,
      immediate: true,
    },
    'option.backgroundImage': {
      handler(newVal) {
        if (newVal) {
          this.isGradient = false
        }
      },
      immediate: true,
    },
    'option.associatedComponent':{
      handler(newVal) {
        if (newVal) {
          this.option.tableHeaderType='whole'
          let allComponents = this.getItemRefs()
          const { value } = newVal
          if(allComponents[value]){
            let tData = allComponents[value]
            if(tData.option.column){
              this.option.tablecolumnList = tData.option.column
            }
          }
        }
      },
      deep: true,
    },
    tablecolumnList:{
      handler() {
        if (this.option.associatedComponent) {
          let allComponents = this.getItemRefs()
          const { value } = this.option.associatedComponent
          if(allComponents[value]){
            let tData = allComponents[value]
            if(tData.option.column){
              this.option.tablecolumnList = tData.option.column
            }
          }
        }
      },
      deep: true,
    }
  },
  computed: {
    tablecolumnList(){
      let sdata = this.contain.list
      return sdata
    },
    componentsInfo() {
      const list = []
      // 获取当前画布上的所有组件
      this.contain.list.forEach(item => {
        if (item.component.name === 'table') {
          list.push({
            label: item.name,
            value: item.index,
            name: item.component.name,
          })
        }
      })
      return list
    },
    isShow() {
      return this.option.visible
        ? {
            opacity: 1,
          }
        : { opacity: 0 }
    },
    styleName() {
      const obj = Object.assign(
        {},
        {
          borderColor: this.option.borderColor || '#309EF8',
          borderStyle: this.option.borderStyle || 'solid',
          borderWidth: this.setPx(this.option.borderWidth || 0),
          borderRadius: this.setPx(this.option.borderRadius || 4),
          backgroundColor: this.option.isGradient
            ? ''
            : this.option.bgColor || '#264767',
          fontSize: this.setPx(this.option.fontSize || 14),
          fontFamily: this.option.fontFamily,
          textAlign: this.option.textAlign,
          color: this.option.color || '#fff',
        },
        (() => {
          if (this.option.backgroundImage) {
            return {
              backgroundImage: `url(${this.option.backgroundImage})`,
              backgroundSize: '100% 100%',
              backgroundColor: this.option.bgColor || '#264767',
            }
          } else {
            return this.option.isGradient
              ? {
                  background: `linear-gradient(0deg, ${this.option.bgColor1} 0%, ${this.option.bgColor2} 100%)`,
                }
              : {}
          }
        })()
      )
      return obj
    },
  },
})
</script>
<style scoped lang="scss">
button {
  width: 100%;
  height: 100%;
  cursor: pointer;
}
button.is-disabled {
  cursor: not-allowed;
  opacity: 0.5;
}
</style>
