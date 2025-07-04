<template>
  <div>
    <el-form-item label="方向">
      <el-radio v-model="main.activeOption.direction" label="clockwise"
        >顺时针</el-radio
      >
      <el-radio v-model="main.activeOption.direction" label="anticlockwise"
        >逆时针</el-radio
      >
    </el-form-item>
    <el-form-item label="数据总值">
      <el-input-number
        v-model="main.activeOption.maxValue"
        controls-position="right"
        :min="0"
      />
    </el-form-item>
    <el-form-item label="背景颜色">
      <avue-input-color
        v-model="main.activeOption.backgroundColor"
      ></avue-input-color>
    </el-form-item>
    <el-form-item label="默认字体颜色" label-width="120px">
      <avue-input-color v-model="main.activeOption.color"></avue-input-color>
    </el-form-item>
    <el-form-item label="字体边距设置">
        <el-switch v-model="main.activeOption.margins"></el-switch>
    </el-form-item>
    <el-form-item label="字体左边距" label-width="120px">
      <el-input-number
        v-model="main.activeOption.marginsLfet"
        controls-position="right"
        :min="0"
      />
    </el-form-item>
    <el-form-item label="字体上边距" label-width="120px">
      <el-input-number
        v-model="main.activeOption.marginsTop"
        controls-position="right"
        :min="0"
      />
    </el-form-item>
    <el-form-item label="字体大小" label-width="120px">
      <el-input-number
        v-model="main.activeOption.fontSize"
        controls-position="right"
        :min="0"
      />
    </el-form-item>
    <el-form-item label="字体系列" label-width="120px">
      <el-select v-model="main.activeOption.fontFamily">
        <el-option
          v-for="(item, index) in allFontFamilyArr"
          :key="index"
          :label="item.label"
          :value="item.value"
        ></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="圆点大小" label-width="120px">
      <el-input-number
        v-model="main.activeOption.circleSize"
        controls-position="right"
        :min="0"
      />
    </el-form-item>
    <el-form-item label="圆点颜色" label-width="120px">
       <avue-input-color v-model="main.activeOption.circleColor"></avue-input-color>
    </el-form-item>
    <el-collapse>
      <el-collapse-item title="圆环颜色" name="1">
        <el-form-item label="是否渐变">
          <el-switch v-model="main.activeOption.isGradient"></el-switch>
        </el-form-item>
        <template v-if="main.activeOption.isGradient">
          <el-form-item label="颜色一">
            <avue-input-color
              v-model="main.activeOption.bgColor1"
            ></avue-input-color>
          </el-form-item>
          <el-form-item label="颜色二">
            <avue-input-color
              v-model="main.activeOption.bgColor2"
            ></avue-input-color>
          </el-form-item>
        </template>
        <template v-else>
          <el-form-item label="颜色">
            <avue-input-color
              v-model="main.activeOption.bgColor"
            ></avue-input-color>
          </el-form-item>
        </template>
      </el-collapse-item>
    </el-collapse>

    <el-collapse>
      <el-collapse-item title="圆环半径" name="1">
        <el-form-item label="是否百分比">
          <el-switch v-model="main.activeOption.radiusIspercentage"></el-switch>
        </el-form-item>
        <template v-if="main.activeOption.radiusIspercentage">
          <el-form-item label="圆环内半径">
            <avue-input-number
              v-model="main.activeOption.insideRadius"
              class="radius_input"
              :minRows="0"
            ></avue-input-number
            ><span class="radius_span">%</span>
          </el-form-item>
          <el-form-item label="圆环外半径">
            <avue-input-number
              v-model="main.activeOption.outsideRadius"
              :minRows="0"
              class="radius_input"
            ></avue-input-number
            ><span class="radius_span">%</span>
          </el-form-item>
        </template>
        <template v-else>
          <el-form-item label="圆环内半径">
            <avue-input-number
              v-model="main.activeOption.insideRadius"
              :minRows="0"
              class="radius_input"
            ></avue-input-number
            ><span class="radius_span">px</span>
          </el-form-item>
          <el-form-item label="圆环外半径">
            <avue-input-number
              v-model="main.activeOption.outsideRadius"
              :minRows="0"
              class="radius_input"
            ></avue-input-number
            ><span class="radius_span">px</span>
          </el-form-item>
        </template>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script>
import optionsGetFont from '@/mixins/optionsGetFont.js'
export default {
  inject: ['main'],
  mixins: [optionsGetFont],
  data() {
    return {}
  },

  created() {},

  methods: {},
}
</script>

<style scoped lang="scss">
.radius_input {
  width: 120px !important;
}
.radius_span {
  padding-left: 10px;
  color: #fff;
}
</style>
