<template>
  <div :class="b()">
    <p>数据源组件</p>
  </div>
</template>

<script>
import create from '../../create'
export default create({
  name: 'dataStorage',
  data() {
    return {}
  },

  created() {},

  methods: {},
})
</script>

<style scoped lang='scss'>
p {
  width: 100%;
  height: 100%;
  text-align: center;
  color: #fff;
  line-height: 50px;
  font-size: 26px;
}
</style>
