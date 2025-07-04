<template>
  <div>
    <el-form-item label="维度显示位置">
      <el-select v-model="main.activeOption.category">
        <el-option label="X轴" value="x_category"></el-option>
        <el-option label="Y轴" value="y_category"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="图标类型">
      <el-select v-model="main.activeOption.geometricOrImage">
        <el-option label="几何图形" value="geometric"></el-option>
        <el-option label="自定义图片" value="image"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item
      label="图标"
      v-if="main.activeOption.geometricOrImage === 'geometric'"
    >
      <el-select v-model="main.activeOption.symbol">
        <el-option
          v-for="(item, index) in geometricArr"
          :label="item.label"
          :value="item.value"
          :key="index"
        ></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="图标" v-else>
      <img
        v-if="main.activeOption.imgSymbol"
        :src="main.activeOption.imgSymbol"
        alt=""
        width="25%"
      />
      <el-input v-model="main.activeOption.imgSymbol">
        <div
          @click="main.handleOpenImg('activeOption.imgSymbol')"
          slot="append"
        >
          <i class="iconfont icon-img"></i>
        </div>
      </el-input>
    </el-form-item>
    <el-form-item label="图标宽度">
      <el-input-number
        v-model="main.activeOption.symbolSize[0]"
        controls-position="right"
        :min="0"
      ></el-input-number>
    </el-form-item>
    <el-form-item label="图标高度">
      <el-input-number
        v-model="main.activeOption.symbolSize[1]"
        controls-position="right"
        :min="0"
      ></el-input-number>
    </el-form-item>
    <el-form-item label="图标间距">
      <el-input-number
        v-model="main.activeOption.symbolSplit"
        controls-position="right"
        :min="0"
      ></el-input-number>
    </el-form-item>
    <!-- <el-form-item label="字体颜色">
      <avue-input-color v-model="main.activeOption.color"></avue-input-color>
    </el-form-item> -->
    <el-form-item label="柱间距">
      <el-input-number
        v-model="main.activeOption.barSplit"
        controls-position="right"
        :min="0"
      ></el-input-number>
    </el-form-item>
    <el-collapse accordion>
      <el-collapse-item title="X轴设置">
        <el-form-item label="隐藏">
          <avue-switch v-model="main.activeOption.xAxisShow"> </avue-switch>
        </el-form-item>
        <el-form-item label="标签字体大小">
          <el-input-number
            v-model="main.activeOption.xAxisLabelFontSize"
            controls-position="right"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <!-- <el-form-item label="标签颜色">
          <div style="
                display: flex;
                align-items: center;
                justify-content: space-between;
              ">
            <el-color-picker v-model="main.activeOption.xAxisLabelColor" size="small" />
            <avue-input v-model="main.activeOption.xAxisLabelColor" disabled />
          </div>
        </el-form-item> -->
        <el-form-item label="隐藏轴线">
          <avue-switch v-model="main.activeOption.xAxisLineShow"> </avue-switch>
        </el-form-item>
        <!-- <el-form-item label="轴线颜色">
          <div style="
                display: flex;
                align-items: center;
                justify-content: space-between;
              ">
            <el-color-picker v-model="main.activeOption.xAxisLineColor" size="small" />
            <avue-input v-model="main.activeOption.xAxisLineColor" disabled />
          </div>
        </el-form-item> -->
        <el-form-item label="网格线">
          <avue-switch v-model="main.activeOption.xAxisSplitLineShow">
          </avue-switch>
        </el-form-item>
        <el-form-item label="格线样式">
          <el-select
            v-model="main.activeOption.xAxisSplitLineType"
            placeholder="请选择"
            :disabled="main.activeOption.xAxisSplitLineShow ? false : true"
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
        <el-form-item label="格线大小">
          <el-input-number
            v-model="main.activeOption.xAxisSplitLineWidth"
            controls-position="right"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="网格线颜色">
          <div
            style="
              display: flex;
              align-items: center;
              justify-content: space-between;
            "
          >
            <el-color-picker
              v-model="main.activeOption.xAxisSplitLineColor"
              size="small"
            />
            <avue-input
              v-model="main.activeOption.xAxisSplitLineColor"
              disabled
            />
          </div>
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="Y轴设置">
        <el-form-item label="隐藏">
          <avue-switch v-model="main.activeOption.yAxisShow"> </avue-switch>
        </el-form-item>
        <el-form-item label="标签字体大小">
          <el-input-number
            v-model="main.activeOption.yAxisLabelFontSize"
            controls-position="right"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <!-- <el-form-item label="标签颜色">
          <div style="
                display: flex;
                align-items: center;
                justify-content: space-between;
              ">
            <el-color-picker v-model="main.activeOption.yAxisLabelColor" size="small" />
            <avue-input v-model="main.activeOption.yAxisLabelColor" disabled />
          </div>
        </el-form-item> -->
        <el-form-item label="隐藏轴线">
          <avue-switch v-model="main.activeOption.yAxisLineShow"> </avue-switch>
        </el-form-item>
        <!-- <el-form-item label="轴线颜色">
          <div style="
                display: flex;
                align-items: center;
                justify-content: space-between;
              ">
            <el-color-picker v-model="main.activeOption.yAxisLineColor" size="small" />
            <avue-input v-model="main.activeOption.yAxisLineColor" disabled />
          </div>
        </el-form-item> -->
        <el-form-item label="网格线">
          <avue-switch v-model="main.activeOption.yAxisSplitLineShow">
          </avue-switch>
        </el-form-item>
        <el-form-item label="格线样式">
          <el-select
            v-model="main.activeOption.yAxisSplitLineType"
            placeholder="请选择"
            :disabled="main.activeOption.yAxisSplitLineShow ? false : true"
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
        <el-form-item label="格线大小">
          <el-input-number
            v-model="main.activeOption.yAxisSplitLineWidth"
            controls-position="right"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="网格线颜色">
          <div
            style="
              display: flex;
              align-items: center;
              justify-content: space-between;
            "
          >
            <el-color-picker
              v-model="main.activeOption.yAxisSplitLineColor"
              size="small"
            />
            <avue-input
              v-model="main.activeOption.yAxisSplitLineColor"
              disabled
            />
          </div>
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="数值设置">
        <el-form-item label="显示">
          <avue-switch v-model="main.activeOption.labelShow"></avue-switch>
        </el-form-item>
        <el-form-item label="字体大小">
          <el-input-number
            v-model="main.activeOption.labelShowFontSize"
            controls-position="right"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="字体颜色">
          <avue-input-color v-model="main.activeOption.labelShowColor">
          </avue-input-color>
        </el-form-item>
        <!-- <el-form-item label="跟随饼图颜色">
            <avue-switch v-model="followPieColor"> </avue-switch>
          </el-form-item> -->
        <el-form-item label="字体粗细">
          <avue-select
            v-model="main.activeOption.labelShowFontWeight"
            :dic="dicOption.fontWeight"
          >
          </avue-select>
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="坐标轴边距设置">
        <el-form-item
          v-for="grid in gridOptions"
          :key="grid.value"
          :label="grid.label"
        >
          <el-input-number
            v-model="main.activeOption[grid.value]"
            controls-position="right"
            :min="0"
            :max="400"
          ></el-input-number>
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="图例操作">
        <el-form-item label="图例">
          <avue-switch v-model="main.activeOption.legend"></avue-switch>
        </el-form-item>
        <el-form-item label="位置">
          <avue-select
            v-model="main.activeOption.legendPostion"
            :dic="dicOption.textAlign"
          >
          </avue-select>
        </el-form-item>
        <el-form-item label="布局朝向">
          <avue-select
            v-model="main.activeOption.legendOrient"
            :dic="dicOption.orientList"
          >
          </avue-select>
        </el-form-item>
        <el-form-item label="图例宽度">
          <el-input-number
            v-model="main.activeOption.legendWidth"
            controls-position="right"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="图例高度">
          <el-input-number
            v-model="main.activeOption.legendHeight"
            controls-position="right"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="字体大小">
          <el-input-number
            v-model="main.activeOption.legendFontSize"
            controls-position="right"
            :min="0"
          />
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="自定义配色">
        <el-form-item label="文字颜色">
          <avue-input-color v-model="main.activeOption.nameColor">
          </avue-input-color>
        </el-form-item>
        <el-form-item label="轴线颜色">
          <avue-input-color
            v-model="main.activeOption.lineColor"
          ></avue-input-color>
        </el-form-item>
        <avue-crud
          :option="colorOption"
          :data="main.activeOption.barColor"
          @row-save="rowSave"
          @row-del="rowDel"
          @row-update="rowUpdate"
        ></avue-crud>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script>
import { dicOption, colorOption } from '@/option/config'
export default {
  inject: ['main'],
  data() {
    return {
      dicOption: dicOption,
      colorOption: colorOption,
      geometricArr: [
        {
          label: '圆形',
          value: 'circle',
        },
        {
          label: '矩形',
          value: 'rect',
        },
        {
          label: '圆角矩形',
          value: 'roundRect',
        },
        {
          label: '三角形',
          value: 'triangle',
        },
        {
          label: '钻石',
          value: 'diamond',
        },
        {
          label: '大头针',
          value: 'pin',
        },
        {
          label: '箭头',
          value: 'arrow',
        },
      ],
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
      gridOptions: [
        { label: '左边距(像素)', value: 'gridX' },
        { label: '顶边距(像素)', value: 'gridY' },
        { label: '右边距(像素)', value: 'gridX2' },
        { label: '底边距(像素)', value: 'gridY2' },
      ],
    }
  },
  created() {},

  methods: {
    rowSave(row, done) {
      this.main.activeOption.barColor.push(row)
      done()
    },
    rowDel(row, index) {
      if (this.main.activeOption.barColor.length > 1) {
        this.main.activeOption.barColor.splice(index, 1)
      } else {
        this.$message.error('请至少保留一项')
      }
    },
    rowUpdate(row, index, done) {
      this.main.activeOption.barColor.splice(index, 1, row)
      done()
    },
  },
}
</script>
