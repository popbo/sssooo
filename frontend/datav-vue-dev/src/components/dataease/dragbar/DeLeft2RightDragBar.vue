<template>
  <div v-left-to-right-drag class="drag-bar" />
</template>

<script>
export default {
  name: 'DeLeft2RightDragBar'
}
</script>

<style scoped>

  .drag-bar {
    height: 100%;
    width: 1px;
    position: absolute;
    right: 0px;
    top: 0;
    cursor: col-resize;
    background-color: #293241;;
    border: 0px;
  }

  .blackTheme .drag-bar {
    height: 100%;
    width: 1px;
    position: absolute;
    right: 0px;
    top: 0;
    cursor: col-resize;
    background-color:  #acbac3 !important;
    border: 0px;
  }

  .drag-bar:hover {
    width: 3px;
  }

</style>
