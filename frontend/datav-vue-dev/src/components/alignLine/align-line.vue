<template>
  <div class="ident-line-wp" v-if="alignLine.enable">
    <div
      class="ident-line x-line"
      :style="{ width: `${canvas.width}px`, top: `${alignLine.top}px` }"
      v-show="alignLine.topShow"
    ></div>
    <div
      class="ident-line x-line"
      :style="{ width: `${canvas.width}px`, top: `${alignLine.horizontal}px` }"
      v-show="alignLine.horizontalShow"
    ></div>
    <div
      class="ident-line x-line"
      :style="{ width: `${canvas.width}px`, top: `${alignLine.bottom}px` }"
      v-show="alignLine.bottomShow"
    ></div>
    <div
      class="ident-line y-line"
      :style="{ height: `${canvas.height}px`, left: `${alignLine.left}px` }"
      v-show="alignLine.leftShow"
    ></div>
    <div
      class="ident-line y-line"
      :style="{ height: `${canvas.height}px`, left: `${alignLine.vertical}px` }"
      v-show="alignLine.verticalShow"
    ></div>
    <div
      class="ident-line y-line"
      :style="{ height: `${canvas.height}px`, left: `${alignLine.right}px` }"
      v-show="alignLine.rightShow"
    ></div>
  </div>
</template>

<script>

export default({
  name: 'AlignLine',
  data(){
    return {}
  },
  computed: {
    canvas () {
      return this.$store.state.canvas
    },
    alignLine () {
      return this.$store.state.alignLine
    }
  }
})
</script>

<style lang="scss" scoped>
.ident-line {
  position: absolute;
  top: 0;
  left: 0;
  z-index: 1;
  box-sizing: border-box;
}

.x-line {
  border-top: 1px dashed #00c1de;
}

.y-line {
  border-left: 1px dashed #00c1de;
}
</style>
