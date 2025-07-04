<template>
  <div class="editColor">
    <el-dialog
            title="配色设置"
            width="650px"
            :visible.sync="visible"
        >
        <div class="editColorText">
          <avue-crud
            :option="colorOption"
            :data="color"
            @row-save="rowSave"
            @row-del="rowDel"
            @row-update="rowUpdate"
          ></avue-crud>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="visible = false">取 消</el-button>
          <el-button type="primary" @click="saveColor">确 定</el-button>
        </span>
    </el-dialog>
  </div>
</template>
<script>
import { colorOption } from '@/option/config'
import { deepClone } from '../../echart/util'
export default {
  name:'editColor',
  data(){
    return {
      color:[],
      visible:false,
      colorOption:colorOption,
    }
  },
  props:{
    colorList:{
      type:Array,
      default:()=>{
        return []
      }
    }
  },
  watch:{
    visible:{
      handler(){
        this.color = deepClone(this.colorList)
      },
      deep:true,
    },
  },
  methods:{
    rowSave(row, done) {
      this.color.push(row)
      done()
    },
    rowDel(row, index) {
      this.color.splice(index, 1)
      console.log(row, index)
    },
    rowUpdate(row, index, done) {
      this.color.splice(index, 1, row)
      done()
    },
    saveColor(){
      this.$confirm('该操作会覆盖图形原有配色，是否确认修改？', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        this.$emit('saveColor',this.color)
      }).catch(() => {})
    },
  }
}
</script>
<style  scoped lang="scss">
.editColorText{
  height: 400px;
  overflow: auto;
}
</style>