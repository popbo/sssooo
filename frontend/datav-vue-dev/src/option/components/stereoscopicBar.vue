<template>
  <div>
    <el-collapse accordion>
      <el-collapse-item title="柱体设置">
        <el-form-item label="柱体样式">
          <el-select v-model="main.activeOption.cylinderStyle">
            <el-option
              v-for="item in cylinderOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="TOPN">
          <el-select v-model="main.activeOption.TOPN" placeholder="请选择">
            <el-option
              v-for="item in topnOptions"
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
      cylinderOptions: [
        {
          label: '立方体',
          value: 'cube',
        },
        {
          label: '圆柱体',
          value: 'cylinder',
        },
      ],
      topnOptions: [
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
    }
  },

  created() {},

  methods: {},
}
</script>

<style scoped lang="scss"></style>
