<!--折线图配置 -->
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
        :disabled="main.activeOption.sliding || main.activeOption.smooth"
        type="textarea"
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
    <el-form-item label="点的大小" v-show="main.activeOption.animation">
      <el-input-number
        controls-position="right"
        :min="1"
        v-model="main.activeOption.animationSize"
      ></el-input-number>
    </el-form-item>
    <el-form-item label="x轴0刻度">
      <avue-switch
        type="textarea"
        v-model="main.activeOption.axleX"
      ></avue-switch>
    </el-form-item>
    <el-collapse accordion>
      <el-collapse-item title="折线设置">
        <el-form-item label="平滑曲线">
          <avue-switch v-model="main.activeOption.smooth"> </avue-switch>
        </el-form-item>
        <el-form-item label="面积堆积">
          <avue-switch v-model="main.activeOption.areaStyle"></avue-switch>
        </el-form-item>
        <el-form-item label="线条宽度">
          <el-input-number
            v-model="main.activeOption.lineWidth"
            controls-position="right"
            :min="0"
            :max="100"
          />
        </el-form-item>
        <el-form-item label="点的大小">
          <el-input-number
            v-model="main.activeOption.symbolSize"
            controls-position="right"
            :min="0"
            :max="100"
          />
        </el-form-item>
        <el-form-item label="拐点显示">
          <avue-switch v-model="main.activeOption.showSymbol"></avue-switch>
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
  data(){
    return {
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
    }
  }
}
</script>
