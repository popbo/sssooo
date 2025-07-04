<!-- iframe配置 -->
<template>
  <div>
    <el-form-item label="页面">
      <el-select v-model="main.activeObj.data.value" placeholder="请选择"  filterable>
        <el-option
          v-for="item in listType"
          :key="item.id"
          :label="item.title"
          :value="item.id">
        </el-option>
      </el-select>
    </el-form-item>
  </div>
</template>

<script>
import { getCurrentCate } from '@/api/visual'
import { url } from '@/config.js'
import request from '@/axios'
export default {
  inject: ["main"],
  data() {
    return {
      listType:[]
    }
  },
  watch:{
    'main.activeObj.data.value':{
      handler:function(val){
        console.log(val,1236)
        if(val){
          this.getList(val)
        }
      },
      deep:true,
      immediate:true
    }
  },
  methods:{
    getList(id){
      request({
      url: url + '/visual/detail?id='+id,
      method:'get'
    }).then(res=>{
      if(res.data.data){
        let data = JSON.parse(res.data.data.config.component)
        let result = []
        const detail = item => {
        if (item.children) {
            item.children.forEach(ele => {
              detail(ele)
            })
          } else {
            result.push(item)
          }
        }
        data.forEach(ele => {
          detail(ele)
        })
        this.main.activeObj.domList = result
      }
    })
    }
  },
  created(){
    let id = this.$route.params.id
    getCurrentCate(id).then(res=>{
       if (res.data.success) {
          this.listType = res.data.data
        }
    })
  }
}
</script>

<style>
</style>