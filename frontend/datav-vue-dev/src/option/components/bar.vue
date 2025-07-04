<!-- 柱状图配置 -->
<template>
  <div>
    <el-form-item label="竖展示">
      <avue-switch
        type="textarea"
        v-model="main.activeOption.category"
      ></avue-switch>
    </el-form-item>
    <el-form-item label="动画特效">
      <avue-switch
        type="textarea"
        :disabled="main.activeOption.sliding"
        v-model="main.activeOption.animation"
      ></avue-switch>
    </el-form-item>
    <el-form-item label="动画时间" v-show="main.activeOption.animation">
      <el-input-number
        v-model="main.activeOption.animationTime"
        controls-position="right"
        :min="1000"
      ></el-input-number>
    </el-form-item>
    <el-form-item label="动画颜色" v-show="main.activeOption.animation">
      <avue-input-color
        type="textarea"
        v-model="main.activeOption.animationColor"
      ></avue-input-color>
    </el-form-item>
    <el-collapse accordion>
      <el-collapse-item title="柱体设置">
        <el-form-item label="柱体宽度">
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
        <el-form-item label="柱体间距">
          <el-input-number
            v-model="main.activeOption.barGap"
            controls-position="right"
            :min="0"
            :max="100"
          ></el-input-number>
        </el-form-item>
        <!-- <el-form-item label="最小高度">
          <avue-slider v-model="main.activeOption.barMinHeight">
          </avue-slider>
        </el-form-item> -->
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
        <el-form-item label="是否堆叠">
          <el-switch v-model="main.activeOption.isStack"></el-switch>
        </el-form-item>
      </el-collapse-item>
    </el-collapse>
    <el-collapse accordion>
      <el-collapse-item title="滑动条设置">
        <el-form-item label="显示">
          <avue-switch v-model="main.activeOption.sliding"> </avue-switch>
        </el-form-item>
        <!-- <el-form-item label="圆角">
          <avue-slider v-model="main.activeOption.barRadius">
          </avue-slider>
        </el-form-item> -->
        <el-form-item label="滚动条高度">
          <el-input-number
            v-model="main.activeOption.slidingHeight"
            controls-position="right"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="背景颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.slidingBarColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="滚动条颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.slidingColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="显示条数">
          <el-input-number
            v-model="main.activeOption.slidingNumber"
            controls-position="right"
            :min="1"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="滚动条底边距">
          <el-input-number
            v-model="main.activeOption.slidingBottom"
            controls-position="right"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="拖拽显示数值">
          <avue-switch v-model="main.activeOption.slidingDrag"> </avue-switch>
        </el-form-item>
        <el-form-item label="字体大小">
          <el-input-number
            v-model="main.activeOption.slidingFont"
            controls-position="right"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="字体颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.slidingFontColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="手柄样式">
          <el-select
            v-model="main.activeOption.handleStyle"
            placeholder="请选择"
          >
            <el-option
              v-for="item in handleOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="手柄大小">
          <el-input-number
            v-model="main.activeOption.handleFont"
            controls-position="right"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="手柄颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.handleColor"
          ></avue-input-color>
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
      handleOptions: [
        {
          value: 1,
          label: '默认',
        },
        {
          value: 2,
          label: '样式一',
        },
      ],
      value: '',
      values: '',
    }
  },
}
</script>

<style></style>
