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
    }"
    @click="handleClick"
    @dblclick="handleDbClick"
  >
    <div class="radios-content" :style="[boxStyle]">
      <div v-for="(item, index) in dataChart" :key="index">
        <el-radio v-model="option.selected" :label="item.id">{{
          item.label
        }}</el-radio>
      </div>
    </div>
  </div>
</template>

<script>
import create from '../../create'
export default create({
  name: 'radioMultiple',
  data() {
    return {
      radio: true,
    }
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
    boxStyle() {
      return {
        display: 'flex',
        flexDirection:
          this.option.extenDirection === 'transverse' ? 'row' : 'column',
        justifyContent: 'space-between',
      }
    },
  },
})
</script>
<style lang="scss" scoped>
.radios-content {
  width: 100%;
  height: 100%;
  margin: 5px;
  flex-wrap: wrap !important;
  padding: 10px;
}
::v-deep .el-radio__inner {
  width: 26px;
  height: 26px;
  border: 2px solid var(--btnBorderColor);
  background-color: var(--optionBgColor);
}
::v-deep .el-radio__inner::after {
  width: 12px;
  height: 12px;
  background-color: var(--selectedColor);
}
::v-deep .el-radio__label {
  display: inline-block;
  min-width: 80px;
  padding-left: 5px !important;
  color: var(--optionColor);
  font-size: var(--fontSize);
  font-weight: var(--fontWeight);
  text-align: var(--textAlign);
}
::v-deep .el-radio__input.is-checked .el-radio__inner {
  border-color: var(--btnBorderColor);
  background-color: var(--selectedColor);
}
::v-deep .el-radio__input.is-checked + .el-radio__label {
  color: var(--optionColor);
}
</style>
