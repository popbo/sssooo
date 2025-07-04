<!-- 饼图的配置 -->
<template>
  <div>
    <!-- <el-form-item label="字体大小">
       <el-input-number
              v-model="main.activeOption.fontSize"
              controls-position="right"
              :min="0"
            />
    </el-form-item> -->
    <el-collapse accordion>
      <el-collapse-item title="饼图设置">
        <el-form-item label="圆环半径">
          <el-input-number
            v-model="main.activeOption.radius"
            controls-position="right"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="圆环左边距离">
          <el-input-number
            v-model="main.activeOption.leftWdidth"
            controls-position="right"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="设置为环形">
          <avue-switch @change="getIsAnnulus(main.activeOption.isAnnulus)"  v-model="main.activeOption.isAnnulus"></avue-switch>
        </el-form-item>
        <template v-if="main.activeOption.isAnnulus">
          <el-form-item label="圆环宽度">
            <el-input-number
              v-model="main.activeOption.annulusWidth"
              controls-position="right"
              :min="0"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="边框大小">
            <el-input-number
              v-model="main.activeOption.boderWidth"
              controls-position="right"
              :min="0"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="边框颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.boderColor"
          ></avue-input-color>
        </el-form-item>
        </template>
        <el-form-item label="内环圆环">
          <avue-switch :disabled="!main.activeOption.isAnnulus" v-model="main.activeOption.within"></avue-switch>
        </el-form-item>
        <template v-if="main.activeOption.isAnnulus&&main.activeOption.within">
          <el-form-item label="内环圆环半径">
            <el-input-number
              v-model="main.activeOption.withinRadius"
              controls-position="right"
              :min="0"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="内环环宽度">
            <el-input-number
              v-model="main.activeOption.withinAnnulusWidth"
              controls-position="right"
              :min="0"
            ></el-input-number>
          </el-form-item>
          <template>
            <el-collapse accordion>
              <el-collapse-item title="内环配色">
                <avue-crud
                  :option="colorOption1"
                  :data="main.activeOption.withinBarColor"
                  @row-save="rowSave"
                  @row-del="rowDel"
                  @row-update="rowUpdate"
                ></avue-crud>
              </el-collapse-item>
            </el-collapse>
          </template>
        </template>
        <el-form-item label="南丁格尔玫瑰" label-width="120">
          <avue-switch v-model="main.activeOption.roseType"></avue-switch>
        </el-form-item>
        <el-form-item
          label="
        自动排序"
        >
          <avue-switch v-model="main.activeOption.sort"></avue-switch>
        </el-form-item>
        <el-form-item label="不展示零">
          <avue-switch v-model="main.activeOption.notCount"></avue-switch>
        </el-form-item>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script>
import { colorOption1 } from '@/option/config'
export default {
  inject: ['main'],
  data() {
    return {
      colorOption1: colorOption1,
    }
  },
  methods:{
    getIsAnnulus(value){
      if(!value){
        this.main.activeOption.within = false;
      }
    },
    rowSave(row, done) {
      this.main.activeOption.withinBarColor.push(row)
      done()
    },
    rowDel(row, index) {
      if (this.main.activeOption.withinBarColor.length > 1) {
        this.main.activeOption.withinBarColor.splice(index, 1)
      } else {
        this.$message.error('请至少保留一项')
      }
    },
    rowUpdate(row, index, done) {
      this.main.activeOption.withinBarColor.splice(index, 1, row)
      done()
    },
  }
}
</script>

<style></style>
