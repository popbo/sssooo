<template>
  <el-row :gutter="4">
    <el-col :span="12">
      <el-slider v-model="value" :min="min" :max="max"></el-slider>
    </el-col>
    <el-col :span="8">
      <el-input-number v-model="value" controls-position="right" :min="min" :max="max" size="mini"></el-input-number>
    </el-col>
    <el-col :span="2">{{ unit }}</el-col>
  </el-row>
</template>

<script>
export default {
  name: 'sliderInputNum',
  props: {
    min: {
      type: Number,
      default: 0,
    },
    max: {
      type: Number,
      default: 100,
    },
    unit: {
      type: String,
      default: '',
    },
    value: {
      type: Number,
      default: 0,
    },
  },
  watch: {
    value: {
      handler(newVal) {
        this.$emit('input', newVal)
      }
    }
  },
  data() {
    return {}
  },
  created() {},

  methods: {},
}
</script>

<style scoped lang='scss'>
.el-input-number.is-controls-right {
  /deep/.el-input__inner {
    padding-left: 5px;
    padding-right: 15px;
    text-align: left;
  }
}
</style>
