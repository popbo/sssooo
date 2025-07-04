<template>
  <div
    :class="b()"
    :style="{
      '--optionColor': option.optionColor,
      '--textAlign': option.textAlign,
      '--fontSize': option.fontSize + 'px',
      '--fontWeight': option.fontWeight,
      '--btnBorderColor': option.btnBorderColor,
      '--selectedColor': option.selectedColor,
      '--optionBgColor': option.optionBgColor,
      '--flexDirection': flexDirection,
    }"
    @click="handleClick"
    @dblclick="handleDbClick"
  >
    <el-checkbox-group v-model="option.selected" size="mini">
      <el-checkbox
        v-for="(item, index) in dataChart"
        :key="index"
        :label="item.id"
        >{{ item.label }}</el-checkbox
      >
    </el-checkbox-group>
  </div>
</template>

<script>
import create from '../../create'
export default create({
  name: 'checkbox',
  data() {
    return {}
  },
  methods: {
    handleClick() {
      this.updateClick(
        {
          value: this.dataChart,
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
    },
    handleDbClick() {
      this.updateClick(
        {
          value: this.dataChart,
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
  },
  computed: {
    flexDirection() {
      return this.option.extenDirection === 'transverse' ? 'row' : 'column'
    },
  },
})
</script>
<style lang="scss" scoped>
::v-deep .el-checkbox-group {
  display: flex;
  justify-content: space-between !important;
  width: 100% !important;
  height: 100% !important;
  margin: 5px !important;
  padding: 10px !important;
  flex-wrap: wrap !important;
  flex-direction: var(--flexDirection);
}
::v-deep .el-checkbox__inner {
  width: 26px;
  height: 26px;
  border: 2px solid var(--btnBorderColor) !important;
  background-color: var(--optionBgColor) !important;
}
::v-deep .el-checkbox__inner::after {
  // border: 0 !important;
  border-color: transparent !important;
  left: 0 !important;
  width: 12px;
  height: 12px;
  background-color: var(--selectedColor);
}
::v-deep .el-checkbox__label {
  display: inline-block;
  min-width: 80px;
  padding-left: 5px !important;
  color: var(--optionColor) !important;
  font-size: var(--fontSize);
  font-weight: var(--fontWeight);
  text-align: var(--textAlign) !important;
}
::v-deep .el-checkbox__input.is-checked .el-checkbox__inner {
  background-color: var(--selectedColor) !important;
}
</style>
