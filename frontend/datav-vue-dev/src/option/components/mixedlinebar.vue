<template>
  <div>
    <el-form-item label="竖展示">
      <avue-switch
        type="textarea"
        v-model="main.activeOption.category"
      ></avue-switch>
    </el-form-item>
    <el-collapse accordion>
      <el-collapse-item title="柱体设置">
        <el-form-item label="最大宽度">
          <el-input-number
            v-model="main.activeOption.barWidth"
            controls-position="right"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="圆角">
          <el-input-number
            v-model="main.activeOption.barRadius"
            controls-position="right"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="最小高度">
          <el-input-number
            v-model="main.activeOption.barMinHeight"
            controls-position="right"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="TOPN">
          <el-select v-model="main.activeOption.TOPN" placeholder="请选择">
            <el-option
              v-for="item in options"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="N值">
          <el-input-number
            v-model="main.activeOption.Ns"
            controls-position="right"
            :min="0"
          ></el-input-number>
        </el-form-item>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script>
export default {
  inject: ['main'],
  data() {
    return {
      options: [
        {
          value: '0',
          label: '无',
        },
        {
          value: '1',
          label: '前N',
        },
        {
          value: '-1',
          label: '后N',
        },
      ],
      value: '',
      values: '',
    }
  },
  created() {
    console.log(this.main.activeOption)
  },
}
</script>

<style></style>
