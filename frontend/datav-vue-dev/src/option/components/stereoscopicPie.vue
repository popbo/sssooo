<template>
  <div>
    <el-collapse accordion>
      <el-collapse-item title="饼图设置">
        <el-form-item label="设置为环形">
          <avue-switch v-model="main.activeOption.isAnnulus"></avue-switch>
        </el-form-item>
        <template v-if="!main.activeOption.isAnnulus">
          <el-form-item label="饼图半径">
            <el-input-number
              v-model="main.activeOption.radius"
              controls-position="right"
              :min="0"
            ></el-input-number>
          </el-form-item>
        </template>
        <template v-if="main.activeOption.isAnnulus">
          <el-form-item label="内半径">
            <el-input-number
              v-model="main.activeOption.insideRadius"
              controls-position="right"
              :min="0"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="外半径">
            <el-input-number
              v-model="main.activeOption.outsideRadius"
              controls-position="right"
              :min="0"
            ></el-input-number>
          </el-form-item>
        </template>
        <el-form-item label="饼图高度">
          <el-input-number
            v-model="main.activeOption.depth"
            controls-position="right"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="自动排序">
          <avue-switch v-model="main.activeOption.sort"></avue-switch>
        </el-form-item>
        <el-form-item label="不展示零">
          <avue-switch v-model="main.activeOption.notCount"></avue-switch>
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="标题设置">
        <el-form-item label="显示">
          <avue-switch v-model="main.activeOption.isShowTitle"></avue-switch>
        </el-form-item>
        <el-form-item label="名称">
          <el-input v-model="main.activeOption.titleText"></el-input>
        </el-form-item>
        <el-form-item label="字体颜色">
          <avue-input-color
            v-model="main.activeOption.titleTextColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="字体大小">
          <el-input-number
            v-model="main.activeOption.titleTextFontSize"
            controls-position="right"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="字体位置">
          <avue-select
            v-model="main.activeOption.textAlign"
            :dic="dicOption.textAlign"
          >
          </avue-select>
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="数值设置">
        <el-form-item label="显示">
          <avue-switch v-model="main.activeOption.labelShow"> </avue-switch>
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
        <el-form-item label="字体粗细">
          <avue-select
            v-model="main.activeOption.labelShowFontWeight"
            :dic="dicOption.fontWeight"
          >
          </avue-select>
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="提示语设置">
        <el-form-item label="字体大小">
          <el-input-number
            v-model="main.activeOption.tipFontSize"
            controls-position="right"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="字体颜色">
          <avue-input-color
            v-model="main.activeOption.tipColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="背景颜色">
          <avue-input-color
            v-model="main.activeOption.backgroundColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="边框大小">
          <el-input-number
            v-model="main.activeOption.border"
            controls-position="right"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="边框颜色">
          <avue-input-color
            v-model="main.activeOption.borderColor"
          ></avue-input-color>
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="图例操作">
        <el-form-item label="图例">
          <avue-switch v-model="main.activeOption.legend"></avue-switch>
        </el-form-item>
        <el-form-item label="水平位置">
          <avue-select
            v-model="main.activeOption.legendHorizontal"
            :dic="dicOption.textAlign"
          >
          </avue-select>
        </el-form-item>
        <el-form-item label="垂直位置">
          <avue-select
            v-model="main.activeOption.legendVertical"
            :dic="dicOption.verticalAlign"
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
          />
        </el-form-item>
        <el-form-item label="图例高度">
          <el-input-number
            v-model="main.activeOption.legendHeight"
            controls-position="right"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="字体大小">
          <el-input-number
            v-model="main.activeOption.legendFontSize"
            controls-position="right"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="字体颜色">
          <avue-input-color
            v-model="main.activeOption.legendColor"
          ></avue-input-color>
        </el-form-item>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script>
import { dicOption } from '@/option/config'
export default {
  inject: ['main'],
  data() {
    return {
      dicOption: dicOption,
    }
  },

  created() {},

  methods: {
  },
}
</script>

<style scoped lang="scss"></style>
