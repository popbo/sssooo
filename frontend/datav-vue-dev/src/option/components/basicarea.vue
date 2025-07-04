<!-- 预测面积图配置 -->
<template>
  <div class="basicarea-text">
    <el-collapse accordion>
      <el-form-item label="文字颜色">
        <avue-input-color v-model="main.activeOption.nameColor">
        </avue-input-color>
      </el-form-item>
      <el-form-item label="轴线颜色">
        <avue-input-color
          v-model="main.activeOption.lineColor"
        ></avue-input-color>
      </el-form-item>
      <el-collapse-item title="基本属性">
        <el-collapse>
          <el-collapse-item
            title="实际"
            style="background-color:#bbbbbb,color:#000000"
          >
            <el-form-item label="平滑曲线">
              <avue-switch
                v-model="main.activeOption.whichActive[0].smooth"
              ></avue-switch>
            </el-form-item>
            <el-form-item label="拐点显示">
              <avue-switch
                type="textarea"
                v-model="main.activeOption.whichActive[0].showSymbol"
              ></avue-switch>
            </el-form-item>
            <el-form-item label="线条样式">
              <el-select
                v-model="main.activeOption.whichActive[0].lineStyle.type"
                placeholder="请选择"
              >
                <el-option
                  v-for="item in options"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="线条宽度">
              <el-input-number
                v-model="main.activeOption.whichActive[0].lineWidth"
                controls-position="right"
                :min="0"
              ></el-input-number>
            </el-form-item>
            <el-form-item label="面积堆积">
              <avue-switch
                v-model="main.activeOption.whichActive[0].areaStyle"
              ></avue-switch>
            </el-form-item>
            <template v-if="main.activeOption.whichActive[0].areaStyle">
              <avue-crud
                :option="basicAreaColorOption"
                :data="main.activeOption.relatyBarColor"
                @row-update="relatyRowUpdate"
              ></avue-crud>
            </template>
          </el-collapse-item>

          <el-collapse-item title="预测">
            <el-form-item label="平滑曲线">
              <avue-switch v-model="main.activeOption.whichActive[1].smooth">
              </avue-switch>
            </el-form-item>
            <el-form-item label="拐点显示">
              <avue-switch
                type="textarea"
                v-model="main.activeOption.whichActive[1].showSymbol"
              ></avue-switch>
            </el-form-item>
            <el-form-item label="线条样式">
              <el-select
                v-model="main.activeOption.whichActive[1].lineStyle.type"
                placeholder="请选择"
              >
                <el-option
                  v-for="item in options"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="线条宽度">
              <el-input-number
                v-model="main.activeOption.whichActive[1].lineWidth"
                controls-position="right"
                :min="0"
              ></el-input-number>
            </el-form-item>
            <el-form-item label="面积堆积">
              <avue-switch
                v-model="main.activeOption.whichActive[1].areaStyle"
              ></avue-switch>
            </el-form-item>
            <template v-if="main.activeOption.whichActive[1].areaStyle">
              <avue-crud
                :option="basicAreaColorOption"
                :data="main.activeOption.calculateBarColor"
                @row-update="calculateRowUpdate"
              ></avue-crud>
            </template>
          </el-collapse-item>
        </el-collapse>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script>
import { dicOption, colorOption, basicAreaColorOption } from '@/option/config'

export default {
  data() {
    return {
      dicOption: dicOption,
      colorOption: colorOption,
      basicAreaColorOption: basicAreaColorOption,
      options: [
        {
          value: 'dashed',
          label: '------',
        },
        {
          value: 'dotted',
          label: '......',
        },
        {
          value: 'solid',
          label: '——————',
        },
      ],
    }
  },
  inject: ['main'],
  methods: {
    relatyRowUpdate(row, index, done) {
      this.main.activeOption.relatyBarColor.splice(index, 1, row)
      done()
    },
    calculateRowUpdate(row, index, done) {
      this.main.activeOption.calculateBarColor.splice(index, 1, row)
      done()
    },
  },
  mounted() {},
  created() {
    // console.log('初始时barCOLOR', this.main.activeOption)
  },
}
</script>

<style lang="scss" scoped>
::v-deep .avue-crud__header {
  min-height: 0px !important;
}
</style>
