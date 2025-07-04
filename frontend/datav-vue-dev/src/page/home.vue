<template>
  <div class="home">
    <topBar></topBar>
    <div class="main">
      <router-view></router-view>
    </div>
  </div>
</template>
<script>
import topBar from './nav/topBar'
export default {
  data () {
    return {

    }
  },

  created () {

  },

  methods: {

  },
  components: {
    topBar
  }
}
</script>

<style scoped lang='scss'>
.home {
  height: 100%;
  background: #171b22;
  .main {
    margin-top: 57px;
    // height: 100%;
    height: calc(100% - 57px); // 修改大屏首页部分显示不全的问题
  }
}
</style>
