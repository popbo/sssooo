<!-- 通用配置-->
<template>
  <div>
    <!-- 折叠公共配置 -->
    <el-collapse accordion>
      <!-- 标题设置 -->
      <template v-if="main.vaildProp('titleList')">
        <el-collapse-item title="标题设置">
          <el-form-item label="显示">
            <avue-switch v-model="main.activeOption.titleShow"></avue-switch>
          </el-form-item>
          <el-form-item label="标题">
            <avue-input v-model="main.activeOption.title"></avue-input>
          </el-form-item>
          <el-form-item label="字体颜色">
            <avue-input-color
              v-model="main.activeOption.titleColor"
            ></avue-input-color>
          </el-form-item>
          <el-form-item label="字体大小">
            <el-input-number
              v-model="main.activeOption.titleFontSize"
              controls-position="right"
              :min="0"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="字体位置">
            <avue-select
              v-model="main.activeOption.titlePostion"
              :dic="dicOption.textAlign"
            >
            </avue-select>
          </el-form-item>
          <el-form-item label="副标题">
            <avue-input v-model="main.activeOption.subtext"></avue-input>
          </el-form-item>
          <el-form-item label="字体颜色">
            <avue-input-color
              v-model="main.activeOption.subTitleColor"
            ></avue-input-color>
          </el-form-item>
          <el-form-item label="字体大小">
            <el-input-number
              v-model="main.activeOption.subTitleFontSize"
              controls-position="right"
              :min="0"
            ></el-input-number>
          </el-form-item>
        </el-collapse-item>
      </template>
      <!-- 轴设置 -->
      <template v-if="main.vaildProp('barList')">
        <el-collapse-item title="X轴设置">
          <el-form-item label="显示">
            <avue-switch v-model="main.activeOption.xAxisShow"> </avue-switch>
          </el-form-item>
          <el-form-item label="名称">
            <avue-input v-model="main.activeOption.xAxisName"> </avue-input>
          </el-form-item>
          <el-form-item label="名称字体大小">
            <el-input-number
              v-model="main.activeOption.xAxisNameSize"
              controls-position="right"
              :min="0"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="显示网格线">
            <avue-switch v-model="main.activeOption.xAxisSplitLineShow">
            </avue-switch>
          </el-form-item>
          <el-form-item label="网格线样式">
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
          <el-form-item label="网格线颜色">
            <div style="display: flex; align-items: center">
              <el-color-picker
                v-model="main.activeOption.xAxisSplitLineTypeColor"
                size="small"
              />
              <avue-input
                v-model="main.activeOption.xAxisSplitLineTypeColor"
                disabled
                style="width: 90px"
              />
            </div>
          </el-form-item>
          <el-form-item label="标签间距">
            <el-input-number
              v-model="main.activeOption.xAxisinterval"
              controls-position="right"
              :min="0"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="文字角度">
            <el-input-number
              v-model="main.activeOption.xAxisRotate"
              controls-position="right"
              :min="0"
            ></el-input-number>
          </el-form-item>
          <el-form-item
            label="轴反转"
            v-if="
              main.activeComponent &&
              main.activeComponent.prop !== 'stereoscopicBar'
            "
          >
            <avue-switch v-model="main.activeOption.xAxisInverse">
            </avue-switch>
          </el-form-item>
          <el-form-item label="标签字体大小">
            <el-input-number
              v-model="main.activeOption.xNameFontSize"
              controls-position="right"
              :min="0"
            ></el-input-number>
          </el-form-item>
        </el-collapse-item>
        <el-collapse-item title="Y轴设置">
          <el-form-item label="名称">
            <avue-input v-model="main.activeOption.yAxisName"> </avue-input>
          </el-form-item>
          <el-form-item label="名称字体大小">
            <el-input-number
              v-model="main.activeOption.yAxisNameSize"
              controls-position="right"
              :min="0"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="显示">
            <avue-switch v-model="main.activeOption.yAxisShow"> </avue-switch>
          </el-form-item>
          <el-form-item label="轴网格线">
            <avue-switch v-model="main.activeOption.yAxisSplitLineShow">
            </avue-switch>
          </el-form-item>
          <el-form-item label="网格线样式">
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
          <el-form-item label="网格线颜色">
            <div style="display: flex; align-items: center">
              <el-color-picker
                v-model="main.activeOption.yAxisSplitLineTypeColor"
                size="small"
              />
              <avue-input
                v-model="main.activeOption.yAxisSplitLineTypeColor"
                disabled
                style="width: 90px"
              />
            </div>
          </el-form-item>
          <el-form-item
            label="反转"
            v-if="
              main.activeComponent &&
              main.activeComponent.prop !== 'stereoscopicBar'
            "
          >
            <avue-switch v-model="main.activeOption.yAxisInverse">
            </avue-switch>
          </el-form-item>
          <el-form-item label="标签字体大小">
            <el-input-number
              v-model="main.activeOption.yNameFontSize"
              controls-position="right"
              :min="0"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="轴值设置" v-show="main.vaildProp('settingAxis')">
            <avue-switch v-model="main.activeOption.axisFalge"> </avue-switch>
          </el-form-item>
          <template v-if="main.activeOption.axisFalge">
            <el-form-item label="轴最小值" v-show="main.vaildProp('settingAxis')">
              <el-input-number
              v-model="main.activeOption.axisMin"
              controls-position="right"
              :min="0"
              ></el-input-number>
            </el-form-item>
            <el-form-item label="轴最大值" v-show="main.vaildProp('settingAxis')">
              <el-input-number
                v-model="main.activeOption.axisMax"
                controls-position="right"
                :min="0"
                ></el-input-number>
            </el-form-item>
            <el-form-item label="刻度大小" v-show="main.vaildProp('settingAxis')">
              <el-input-number
                v-model="main.activeOption.splitNumber"
                controls-position="right"
                :min="0"
                ></el-input-number>
            </el-form-item>
          </template>
        </el-collapse-item>
      </template>
      <!-- 数值设置 -->
      <template v-if="main.vaildProp('labelList')">
        <el-collapse-item title="数值设置">
          <el-form-item label="显示">
            <avue-switch v-model="main.activeOption.labelShow"> </avue-switch>
          </el-form-item>
          <el-form-item label="字体大小" v-show="main.activeOption.labelShow">
            <el-input-number
              v-model="main.activeOption.labelShowFontSize"
              controls-position="right"
              :min="0"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="字体颜色" v-show="main.activeOption.labelShow">
            <avue-input-color v-model="main.activeOption.labelShowColor">
            </avue-input-color>
          </el-form-item>
          <!-- <el-form-item label="跟随饼图颜色">
            <avue-switch v-model="followPieColor"> </avue-switch>
          </el-form-item> -->
          <el-form-item label="字体粗细" v-show="main.activeOption.labelShow">
            <avue-select
              v-model="main.activeOption.labelShowFontWeight"
              :dic="dicOption.fontWeight"
            >
            </avue-select>
          </el-form-item>
          <el-form-item
            label="数值角度"
            v-if="main.activeOption.labelShow && main.vaildProp('bar')"
          >
            <el-input-number
              v-model="main.activeOption.xLableRotate"
              controls-position="right"
              :min="0"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="字体系列">
            <el-select v-model="main.activeOption.fontFamily">
              <el-option
                v-for="(item, index) in allFontFamilyArr"
                :key="index"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="线条长度1" v-show="main.vaildProp('pieLine')">
            <el-input-number
              v-model="main.activeOption.labelLine1"
              controls-position="right"
              :min="0"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="线条长度2" v-show="main.vaildProp('pieLine')">
            <el-input-number
              v-model="main.activeOption.labelLine2"
              controls-position="right"
              :min="0"
            ></el-input-number>
          </el-form-item>
        </el-collapse-item>
      </template>
      <!-- 提示语设置  -->
      <template v-if="main.vaildProp('tipList')">
        <el-collapse-item title="提示语设置">
          <el-form-item label="轮播" v-show="main.vaildProp('tipCarouselList')">
            <avue-switch :disabled="main.activeOption.sliding" v-model="main.activeOption.tipCarousel"></avue-switch>
          </el-form-item>
          <el-form-item label="轮播时间" v-show="main.activeOption.tipCarousel">
            <el-input-number
              v-model="main.activeOption.tipCarouselTime"
              controls-position="right"
              :min="1"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="显示">
            <avue-switch v-model="main.activeOption.tipShow"> </avue-switch>
          </el-form-item>
          <el-form-item label="字体大小">
            <el-input-number
              v-model="main.activeOption.tipFontSize"
              controls-position="right"
              :min="0"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="字体颜色">
            <avue-input-color
              v-model="main.activeOption.tipColor"
            ></avue-input-color>
          </el-form-item>
          <el-form-item label="背景颜色">
            <avue-input-color
              v-model="main.activeOption.tipBackgroundColor"
            ></avue-input-color>
          </el-form-item>
          <el-form-item label="边框大小">
            <el-input-number
              v-model="main.activeOption.tipBorder"
              controls-position="right"
              :min="0"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="边框颜色">
            <avue-input-color
              v-model="main.activeOption.tipBorderColor"
            ></avue-input-color>
          </el-form-item>
        </el-collapse-item>
      </template>
      <!-- 轴距离设置 -->
      <template v-if="main.vaildProp('postionList')">
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
      </template>
      <!-- 图例设置 -->
      <template v-if="main.vaildProp('legendList')">
        <el-collapse-item title="图例操作">
          <el-form-item label="图例">
            <avue-switch v-model="main.activeOption.legend"></avue-switch>
          </el-form-item>
          <template v-if="main.vaildProp('xYlegendList')">
            <el-form-item label="数值显示">
              <avue-switch v-model="main.activeOption.numberUnit"></avue-switch>
            </el-form-item>
            <template v-if="main.activeOption.numberUnit">
              <el-form-item label="数值距离">
                <el-input-number
                  v-model="main.activeOption.unitWidth"
                  controls-position="right"
                  :min="0"
                ></el-input-number>
              </el-form-item>
              <el-form-item label="数值字体系列">
                <el-select v-model="main.activeOption.unitFontFamily">
                  <el-option
                    v-for="(item, index) in allFontFamilyArr"
                    :key="index"
                    :label="item.label"
                    :value="item.value"
                  ></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="单位">
                <avue-input v-model="main.activeOption.unitText"></avue-input>
              </el-form-item>
              <el-form-item label="单位下移">
                <el-input-number
                  v-model="main.activeOption.belowWidth"
                  controls-position="right"
                  :min="0"
                ></el-input-number>
              </el-form-item>
              <el-form-item label="单位字体大小">
                <el-input-number
                  v-model="main.activeOption.unitFontSize"
                  controls-position="right"
                  :min="0"
                ></el-input-number>
              </el-form-item>
              <el-form-item label="单位字体系列">
                <el-select v-model="main.activeOption.belowFontFamily">
                  <el-option
                    v-for="(item, index) in allFontFamilyArr"
                    :key="index"
                    :label="item.label"
                    :value="item.value"
                  ></el-option>
                </el-select>
              </el-form-item>
            </template>
            <el-form-item label="位置数值控制">
              <avue-switch v-model="main.activeOption.numericalControl"></avue-switch>
            </el-form-item>
            <template v-if="main.activeOption.numericalControl">
              <el-form-item label="左右位置">
                <el-input-number
                  v-model="main.activeOption.numberX"
                  controls-position="right"
                  :min="0"
                ></el-input-number>
              </el-form-item>
              <el-form-item label="上下位置">
                <el-input-number
                  v-model="main.activeOption.numberY"
                  controls-position="right"
                  :min="0"
                ></el-input-number>
              </el-form-item>
            </template>
            <template v-else>
              <el-form-item label="左右位置">
                <avue-select
                  v-model="main.activeOption.legendPostionX"
                  :dic="dicOption.xAlign"
                >
                </avue-select>
              </el-form-item>
              <el-form-item label="上下位置">
                <avue-select
                  v-model="main.activeOption.legendPostionY"
                  :dic="dicOption.yAlign"
                >
                </avue-select>
              </el-form-item>
            </template>
          </template>
          <el-form-item label="位置" v-else>
            <avue-select
              v-model="main.activeOption.legendPostion"
              :dic="dicOption.textAlign"
            >
            </avue-select>
          </el-form-item>
          <el-form-item label="类型" v-if="main.vaildProp('iconList')">
            <avue-select
              v-model="main.activeOption.iconType"
              :dic="dicOption.lineTypeList"
            >
            </avue-select>
          </el-form-item>
          <el-form-item label="类型" v-if="main.vaildProp('barIconList')">
            <avue-select
              v-model="main.activeOption.iconType"
              :dic="dicOption.barlineTypeList"
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
            ></el-input-number>
          </el-form-item>
          <el-form-item label="字体系列">
            <el-select v-model="main.activeOption.legendFontFamily">
              <el-option
                v-for="(item, index) in allFontFamilyArr"
                :key="index"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="字体颜色" v-if="main.vaildProp('funnel')">
            <avue-input-color v-model="main.activeOption.legendColor">
            </avue-input-color>
          </el-form-item>
        </el-collapse-item>
      </template>
      <!-- 颜色设置 -->
      <template v-if="main.vaildProp('colorList')">
        <el-collapse-item title="自定义配色">
          <el-form-item
            label="文字颜色"
            v-show="!main.vaildProp('noBarColorList')"
          >
            <avue-input-color v-model="main.activeOption.nameColor">
            </avue-input-color>
          </el-form-item>
          <el-form-item
            label="轴线颜色"
            v-show="!main.vaildProp('noBarColorList')"
          >
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
      </template>
      <!-- 饼图颜色设置 -->
      <template v-if="main.vaildProp('pieColorList')">
        <el-collapse-item title="自定义配色">
          <el-form-item
            label="文字颜色"
            v-show="!main.vaildProp('noBarColorList')"
          >
            <avue-input-color v-model="main.activeOption.nameColor">
            </avue-input-color>
          </el-form-item>
          <el-form-item
            label="轴线颜色"
            v-show="!main.vaildProp('noBarColorList')"
          >
            <avue-input-color
              v-model="main.activeOption.lineColor"
            ></avue-input-color>
          </el-form-item>
          <avue-crud
            :option="colorOption1"
            :data="main.activeOption.barColor"
            @row-save="rowSave"
            @row-del="rowDel"
            @row-update="rowUpdate"
          ></avue-crud>
        </el-collapse-item>
      </template>
      <!-- 雷达图颜色设置 -->
      <template v-if="main.vaildProp('radar')">
        <el-collapse-item title="自定义配色">
          <el-form-item label="轴线颜色">
            <avue-input-color
              v-model="main.activeOption.lineColor"
            ></avue-input-color>
          </el-form-item>
          <!-- <el-form-item label="背景颜色">
            <avue-input-color
              v-model="main.activeOption.bgColor"
            ></avue-input-color>
          </el-form-item> -->
          <el-form-item label="雷达覆盖区外背景颜色">
            <avue-input-color
              v-model="main.activeOption.externalBgColor"
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
      </template>
      <!-- 动画配置 -->
      <template v-if="main.vaildProp('animationList')">
        <el-collapse-item title="轮播动效">
          <el-form-item label="自动轮播">
            <avue-switch v-model="main.activeOption.showAnimation">
            </avue-switch>
          </el-form-item>
          <el-form-item label="间隔">
            <el-input-number
              v-model="main.activeOption.animationInterval"
              controls-position="right"
              :min="0"
              :max="10000"
            ></el-input-number>
          </el-form-item>
          <!-- <el-form-item
            label="排行个数"
            v-if="main.vaildProp('animationBarList')"
          >
             <el-input-number
              v-model="main.activeOption.rankCount"
              controls-position="right"
             :min="0"
              :max="10"
            />
          </el-form-item> -->
        </el-collapse-item>
      </template>
    </el-collapse>
  </div>
</template>
<script>
import { dicOption, colorOption, colorOption1 } from '@/option/config'
import optionsGetFont from '@/mixins/optionsGetFont.js'
export default {
  inject: ['main'],
  mixins: [optionsGetFont],
  data() {
    return {
      // 是否跟随饼图颜色
      followPieColor: this.main.activeOption.labelShowColor,
      dicOption: dicOption,
      colorOption: colorOption,
      colorOption1: colorOption1,
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

<style scoped lang="scss">
.el-color-picker {
  vertical-align: middle;
}
/deep/.avue-crud__header {
  background-color: transparent;
}
</style>
