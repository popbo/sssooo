<template>
  <div class="slider-box">
    {{ sliderOption.minValue }}
    <div>
      <avue-slider
        v-model="activeOption[attr]"
        :max="sliderOption.maxValue"
        :min="sliderOption.minValue"
        :step="sliderOption.step"
        style="min-width: 100px"
      ></avue-slider>
    </div>
    <span class="slider-text">
      {{ activeOption[attr] }}
    </span>
    / {{ sliderOption.maxValue }}
  </div>
</template>

<script>
export default {
  name: 'slider',
  props: ['activeOption', 'attr', 'sliderOption'],
}
</script>

<style lang="scss" scoped>
.slider-box {
  display: flex;
  color: #fff;
  align-items: center;
  justify-content: space-around;
  font-size: 14px;
  opacity: 0.8;
  line-height: 34px;
  .slider-text {
    font-size: 12px;
    color: #99ff00;
  }
}
</style>
