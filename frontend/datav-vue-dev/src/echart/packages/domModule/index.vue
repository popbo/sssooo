<template>
  <div :class="b()" :style="domModuleStyleone">
    <div
        :style="domModuleStyle"
        class="layout"
        v-if="dataChart.value"
        >
        <div>
          <subgroupModule :nav="list"></subgroupModule>
        </div>
    </div>
    <div v-else class="img-b-d">
      <div class="img"></div>
    </div>
  </div>
</template>

<script>
import { url } from '@/config.js'
import request from '@/axios'
import create from "../../create";
import subgroupModule from './subgroupModule.vue'
import { EventBus } from '@/bus.js'
export default create({
  name: "domModule",
  inject: ['contain', 'container'],
  provide() {
    return {
      contain: this.contain,
      container: this.container,
    }
  },
  data () {
    return {
      list:[],
      detail:{},
    };
  },
  components:{
    subgroupModule
  },
  computed: {
    domModuleStyle(){
      let detailWidth = this.detail.width || 1920
      let detailHeight = this.detail.height || 1080
      let widthVal = this.component.width/detailWidth;
      let heightVal = this.component.height/detailHeight;
      const round = Math.round;
      const width = detailWidth * widthVal;
      const height = detailHeight * heightVal;
      // const size = this.getWindowSize();
      const left = round(this.component.width-width) / 2;
      const top = round(this.component.height-height) / 2;
     
      return {
        transform:`scale(${widthVal}, ${heightVal})`,
        marginLeft: left+'px',
        marginTop: top+'px',
        position: 'relative',
      }
    },
    domModuleStyleone() {
      return {
        background:`url(${ this.detail.backgroundImage}) 0% 0% / 100% 100% ${this.detail.backgroundColor}`,
        cursor: 'default'
      }
    },
  },
  props: {
    option: {
      type: Object,
      default: () => {
        return {};
      }
    },
  },
  watch:{
    dataChart:{
      handler(val,old){
        if(val.value){
          if(val.value !== old.value){
            this.getList(val.value)
          }
        }
      },
    },
  },
  created () { 
  },
  mounted () {
    EventBus.$on('setDomModule',(setData)=>{
      let eventListArr = setData.eventList.filter(
        item => item.eventType === setData.eventType
      )
      // console.log('eventListArr',eventListArr,setData)
      let sumList = [];
      let parentList = [];
      let nav = this.list;
      const detailList = item =>{
          if(item.list){
            item.list.forEach(ele => {
              sumList.push(ele)
              detailList(ele)
            })
          }
          if(item.children){
            item.children.forEach(ele => {
              sumList.push(ele)
              detailList(ele)
            })
          }
        }
      if(nav){
        for(let i = 0; i < nav.length; i++){
          sumList.push(nav[i])
          detailList(nav[i])
        }
      }
      parentList = sumList;
      if(eventListArr){
        for (let i = 0; i < eventListArr.length; i++){
          let itemObj = eventListArr[i]
          switch(itemObj.eventAction){
            case 2: {
              // 隐藏组件
              let hideIndexList = itemObj.showHideChild.index
                for (let k = 0; k < hideIndexList.length; k++) {
                  for (let j = 0; j < parentList.length; j++) {
                    if (hideIndexList[k] === parentList[j].index) {
                      // 如果没有在配置页面触发该组件的隐藏按钮，那么display属性就不是一个响应式的属性，所以要先把他变为响应式
                      try{
                        if(parentList[j].component.prop==='tips'){
                          this.$setSessionItem('imgBorderTypeChange',false)
                        }
                      }catch(err){
                        console.log(err)
                      }
                      setTimeout(() => {
                        this.$set(parentList[j], 'display', '')
                        parentList[j].display = true
                      }, 100)
                    }
                  }
                }
              break
            }
            case 3: {
              let hideIndexList = itemObj.showHideChild.index
                for (let k = 0; k < hideIndexList.length; k++) {
                  for (let j = 0; j < parentList.length; j++) {
                    if (hideIndexList[k] === parentList[j].index) {
                      setTimeout(() => {
                        console.log('parentList[j]==>', parentList[j])
                        parentList[j].display = false
                      }, 100)
                    }
                  }
                }
              break
            }
            case 5:{
              // console.log('页面联动6666',itemObj,this.detail)
              if(itemObj.paramNameValList){
                itemObj.paramNameValList.forEach(item=>{
                  if(item.currenScreenName){
                    if(this.detail.pageParamsList){
                      this.detail.pageParamsList.forEach(itemList=>{
                        if(itemList.paramsKey===item.currenScreenName){
                          if(setData.params.termParams){
                            itemList.termParams = setData.params.termParams
                          }
                          if(setData.assemblyType==='atepicker'){
                            // 当前值
                            itemList.paramsValue = setData.params.value.split(',')
                          }else{
                            // 当前值
                            itemList.paramsValue = setData.params.value || setData.params.label
                          }
                        }
                      })
                    }
                  }
                })
              }
              this.getClickType(1,setData,itemObj.eventAction)
              break
            }
            case 8: {
                if(itemObj.settingMultiparameter){
                  if(itemObj.settingMultiparameter.lableName){
                    if(this.detail.multiparameterList){
                      this.detail.multiparameterList.forEach(item=>{
                        if(item.paramsKey===itemObj.settingMultiparameter.lableName){
                          if(setData.params.termParams){
                            item.termParams = setData.params.termParams
                          }
                          if(itemObj.settingMultiparameter.value==='comName'){
                            // 图层参数值
                            item.paramsValue = this.$attrs.comParams || ''
                          }else{
                            if(setData.assemblyType==='atepicker'){
                              // 当前值
                              item.paramsValue = setData.params.value.split(',')
                            }else{
                              // 当前值
                              item.paramsValue = setData.params.value || setData.params.label
                            }
                          }
                        }
                      })
                    }
                  }
                }
                this.getClickType(9,setData,itemObj.eventAction)
                break
              }
          }
        }
      }
    })
  },
  methods: {
    getClickType(type,setData,clickType){
      let child = [];
      let sqlChild = [];
      let websocketChild = []
      let settingEventList = [];
      if(this.detail.eventList){
        settingEventList = this.detail.eventList.filter(item=>{
          return item.eventAction === type
        });
      }
      settingEventList.forEach(item=>{
        child = [...item.child,...child]
        sqlChild = [...item.sqlChild,...sqlChild]
        websocketChild = [...item.websocketChild,...websocketChild]
      })
      let upDateObj = {}
      if(child.length>0){
        let testOld = []; 
        let childData = []
        let newObj  = {}
        child.forEach(el => {
          let oldObj = { index: el.index, children: [] }
          let txtObj = { keyName: el.keyName, paramName: el.paramName };
          oldObj.children.push(txtObj);
          testOld.push(oldObj);
        });
        testOld.forEach((c,i)=>{
          if(!newObj[c.index]){
            childData.push(c)
            newObj[c.index] = true
          }else{
            childData.forEach(el=>{
              if(el.index === testOld[i].index){
                el.children = [...el.children, ...testOld[i].children]
              }
            })
          }
        })
        if(clickType===8){
          childData.forEach(item=>{
            upDateObj[item.index] = {
              p: this.getChildData(item.children)
            }
          })
        }
        if(clickType===5){
          childData.forEach(item=>{
            upDateObj[item.index] = {
              p: this.PageParamsListChildData(item.children)
            }
          })
        }
      }
      if(sqlChild.length>0){
        let testOld = []; 
        let sqlChildData = []
        let newObj  = {}
        sqlChild.forEach(el => {
          let oldObj = { index: el.index, children: [] }
          let txtObj = { fieldId: el.fieldId, fieldType: el.fieldType, paramsType: el.paramsType, sqlId: el.sqlId };
          oldObj.children.push(txtObj);
          testOld.push(oldObj);
        });
        testOld.forEach((c,i)=>{
          if(!newObj[c.index]){
            sqlChildData.push(c)
            newObj[c.index] = true
          }else{
            sqlChildData.forEach(el=>{
              if(el.index === testOld[i].index){
                el.children = [...el.children, ...testOld[i].children]
              }
            })
          }
        })
        if(clickType===8){
          sqlChildData.forEach(item=>{
            upDateObj[item.index] = {
              p: this.getSqlChildData(item.children)
            }
          })
        }
        if(clickType===5){
          sqlChildData.forEach(item=>{
            upDateObj[item.index] = {
              p: this.PageParamsListSqlChildData(item.children)
            }
          })
        }
      //  console.log(sqlChildData,'sqlChildData')
      }
      if(websocketChild.length>0){
        console.log('111')
      }
      // console.log(upDateObj,999)
      Object.keys(upDateObj).forEach(ele => {
        setData.refList[ele].updateData(upDateObj[ele].p)
      })
    },
    PageParamsListChildData(children){
      let parms = {}
        if(children.length>0){
          children.forEach(item=>{
            if(item.paramName){
              parms[item.paramName]=this.getSettingPageParamsList(item.keyName,true)
            }
          })
        }
        return parms
    },
    getChildData(children){
        let parms = {}
        if(children.length>0){
          children.forEach(item=>{
            if(item.paramName){
              parms[item.paramName]=this.getSettingMultiparameter(item.keyName,true)
            }
          })
        }
        return parms
    },
    PageParamsListSqlChildData(children){
      let p = []
        if(children.length>0){
          children.forEach(item=>{
            if(Object.prototype.toString.call(this.getSettingPageParamsList(item.paramsType).paramsValue).slice(8,-1) === 'Array'){
                if(this.getSettingPageParamsList(item.paramsType).paramsValue.length>1){
                  this.getSettingPageParamsList(item.paramsType).paramsValue.forEach((timeItem,index)=>{
                    if(index===0){
                      p.push({
                        term: 'ge', 
                        value: timeItem, 
                        fieldId: item.fieldId
                      })
                    }else{
                      p.push({
                        term: 'le', 
                        value: timeItem, 
                        fieldId: item.fieldId
                      })
                    }
                  })
                }else{
                  this.getSettingPageParamsList(item.paramsType).paramsValue.forEach((timeItem)=>{
                    p.push({
                      term: 'eq', 
                      value: timeItem, 
                      fieldId: item.fieldId
                    })
                  })
                }
              }else{
                p.push({
                  fieldId: item.fieldId,
                  term: this.getSettingPageParamsList(item.paramsType).termParams || 'eq',
                  value: this.getSettingPageParamsList(item.paramsType).paramsValue || ''
                })
            }
          })
        }
        return p
    },
    getSqlChildData(children){
      let p = []
        if(children.length>0){
          children.forEach(item=>{
            if(Object.prototype.toString.call(this.getSettingMultiparameter(item.paramsType).paramsValue).slice(8,-1) === 'Array'){
                if(this.getSettingMultiparameter(item.paramsType).paramsValue.length>1){
                  this.getSettingMultiparameter(item.paramsType).paramsValue.forEach((timeItem,index)=>{
                    if(index===0){
                      p.push({
                        term: 'ge', 
                        value: timeItem, 
                        fieldId: item.fieldId
                      })
                    }else{
                      p.push({
                        term: 'le', 
                        value: timeItem, 
                        fieldId: item.fieldId
                      })
                    }
                  })
                }else{
                  this.getSettingMultiparameter(item.paramsType).paramsValue.forEach((timeItem)=>{
                    p.push({
                      term: 'eq', 
                      value: timeItem, 
                      fieldId: item.fieldId
                    })
                  })
                }
              }else{
                p.push({
                  fieldId: item.fieldId,
                  term: this.getSettingMultiparameter(item.paramsType).termParams || 'eq',
                  value: this.getSettingMultiparameter(item.paramsType).paramsValue || ''
                })
            }
          })
        }
        return p
    },
    getSettingMultiparameter(type,aplFlage){
      let multiparameterList = []
      if(this.detail.multiparameterList){
        multiparameterList = this.detail.multiparameterList
      }
      if(multiparameterList.length>0){
        let parms = multiparameterList.filter(item=>{
            return item.paramsKey === type
        })
        if(aplFlage){
          if(Object.prototype.toString.call(parms[0].paramsValue).slice(8,-1) === 'Array'){
              return parms[0].paramsValue.join(',') || ''
            }
          return parms[0].paramsValue || ''
        }else{
          return parms[0] || ''
        }
      }
      return ''
    },
    getSettingPageParamsList(type,aplFlage){
      let pageParamsList = []
      if(this.detail.pageParamsList){
        pageParamsList = this.detail.pageParamsList
      }
      if(pageParamsList.length>0){
        let parms = pageParamsList.filter(item=>{
            return item.paramsKey === type
        })
        if(aplFlage){
          if(Object.prototype.toString.call(parms[0].paramsValue).slice(8,-1) === 'Array'){
              return parms[0].paramsValue.join(',') || ''
            }
          return parms[0].paramsValue || ''
        }else{
          return parms[0] || ''
        }
      }
      return ''
    },
    getList(id){
      request({
      url: url + '/visual/detail?id='+id,
      method:'get'
    }).then(res=>{
      if(res.data.data){
        let data = JSON.parse(res.data.data.config.component)
        let detail = JSON.parse(res.data.data.config.detail)
        this.detail = detail
        this.list = data
      }
    })
    },
  },
});
</script>
<style lang="scss" scoped>
.layout{
    transform-origin:  0 0;
    // cursor: default !important;
}
.img-b-d{
  position: relative;
  width: 100%;
  height: 100%;
  background-color: #181a24 !important;
  .img{
      position: absolute;
      left: 50%;
      top: 50%;
      transform: translate(-50%,-50%);
      width: 126px;
      height: 126px;
      background: url('~@/assets/div2.png') no-repeat;
    }
}
</style>



