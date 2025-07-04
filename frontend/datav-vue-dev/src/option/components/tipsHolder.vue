<template>
  <div>
    <el-form-item label="三位旋转预设">
      <el-select v-model="main.activeOption.rotateType" @change="rotateChange">
        <el-option
          v-for="item in rotateList"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        >
        </el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="x轴旋转">
      <el-input-number
          :disabled='disabledX'
          v-model="main.activeOption.rotateX"
          controls-position="right"
          :min="0"
          style="width: 170px; margin-right:6px ;"
        />
        <span>deg</span>
    </el-form-item>
    <el-form-item label="y轴旋转">
      <el-input-number
          :disabled='disabledY'
          v-model="main.activeOption.rotateY"
          controls-position="right"
          :min="0"
          style="width: 170px; margin-right:6px ;"
        />
         <span>deg</span>
    </el-form-item>
    <el-form-item label="z轴旋转">
      <el-input-number
          :disabled='disabledZ'
          v-model="main.activeOption.rotateZ"
          controls-position="right"
          :min="0"
          style="width: 170px; margin-right:6px ;"
        />
        <span>deg</span>
    </el-form-item>
    <el-form-item label="背景颜色">
      <avue-input-color
          type="textarea"
          v-model="main.activeOption.backgroundColor"
      ></avue-input-color>
    </el-form-item>
    <el-form-item label="背景图片">
      <img
        v-if="main.activeOption.backgroundImage"
        :src="main.activeOption.backgroundImage"
        alt=""
        width="100%"
      />
      <el-input v-model="main.activeOption.backgroundImage">
        <div
          @click="
            main.handleOpenImg('activeOption.backgroundImage', 'background')
          "
          slot="append"
        >
        <i class="iconfont icon-img"></i>
        </div>
      </el-input>
    </el-form-item>
    <el-form-item label="边框颜色">
      <avue-input-color
          type="textarea"
          v-model="main.activeOption.borderColor"
      ></avue-input-color>
    </el-form-item>
    <el-form-item label="边框粗细">
      <el-input-number
          v-model="main.activeOption.borderNumber"
          controls-position="right"
          :min="0"
        />
    </el-form-item>
  </div>
</template>

<script>
import { dicOption } from '@/option/config'
export default {
  data() {
    return {
      dicOption: dicOption,
      disabledX:false,
      disabledY:false,
      disabledZ:false,
      rotateList:[
        {
          label:'无',
          value: 1
        },
        {
          label:'水平向右倾斜',
          value:2
        },
        {
          label:'水平向左倾斜',
          value:3
        },{
          label:'左上方',
          value:4
        },
        {
          label:'右上方',
          value:5
        },
        {
          label:'自定义',
          value: 6
        }
      ]
    }
  },
  inject: ['main'],
  methods: {
    rotateChange(val){
      console.log(val,6666)
      if(val===1){
          this.main.activeOption.rotateX = 0
          this.main.activeOption.rotateY = 0
          this.main.activeOption.rotateZ = 0
          this.disabledX = true
          this.disabledY = true
          this.disabledZ = true
        }else if(val===2){
          this.main.activeOption.rotateX = 0
          this.main.activeOption.rotateY = 30
          this.main.activeOption.rotateZ = 0
          this.disabledX = true
          this.disabledY = true
          this.disabledZ = true
        }else if(val===3){
          this.main.activeOption.rotateX = 0
          this.main.activeOption.rotateY = 330
          this.main.activeOption.rotateZ = 0
          this.disabledX = true
          this.disabledY = true
          this.disabledZ = true
        }else if(val===4){
          this.main.activeOption.rotateX = 0
          this.main.activeOption.rotateY = 330
          this.main.activeOption.rotateZ = 15
          this.disabledX = true
          this.disabledY = true
          this.disabledZ = true
        }else if(val===5){
          this.main.activeOption.rotateX = 0
          this.main.activeOption.rotateY = 30
          this.main.activeOption.rotateZ = 345
          this.disabledX = true
          this.disabledY = true
          this.disabledZ = true
        }else{
          this.main.activeOption.rotateX = 0
          this.main.activeOption.rotateY = 0
          this.main.activeOption.rotateZ = 0
          this.disabledX = false
          this.disabledY = false
          this.disabledZ = false
        }
    }
  },
}
</script>
<style></style>
