<template>
  <div class="topo-meta2d">
    <Meta2dMenu class="menu" />
    <div class="editor">
      <Meta2dMaterial class="material" />
      <Meta2dCanvas class="canvas" />
      <Meta2dProps class="props" />
       <!-- 底部视图 -->
      <PositionBox/>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import Meta2dMenu from './Meta2dMenu.vue';
import Meta2dMaterial from './Meta2dMaterial.vue';
import Meta2dCanvas from './Meta2dCanvas.vue';
import Meta2dProps from './Meta2dProps.vue';
import PositionBox from './common/PositionBox.vue';
export default defineComponent({
  name: 'Meta2d',
  components: {
    Meta2dMenu,
    Meta2dMaterial,
    Meta2dCanvas,
    Meta2dProps,
    PositionBox
  },
  setup(props) {},
});
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.topo-meta2d {
  height: 100%;
  padding-top: 50px;
  overflow: hidden;
  text-align: left;

  .menu {
    position: fixed;
    top: 0px;
    width: 100%;
    height: 50px;
    background: #232630;
    color:#b4b7c1;
    border-bottom:1px solid #0d0e10;
    z-index: 5;
    flex-shrink: 0;
  }

  & > .editor {
    display: flex;
    height: calc(100vh - 50px);
    overflow-x: hidden;

    .material {
      width: 208px;
      height: 100%;
      border-right: 1px solid #0d0e10;
      background: #181A24;
    }

    & > * {
      flex-shrink: 0;
    }

    .canvas {
      flex: 1;
      :deep(.meta2d-map){
        background: #232630;
        border:1px solid rgb(24, 144, 255);
        div{
          border:1px solid #181a24;
        }
      }
    }

    .props {
      width: 342px;
      height: 100%;
      border-left: 1px solid #0d0e10;
    }
  }
  :deep(.ant-input),:deep(.ant-select-selector),:deep(.ant-input-number){
    background: #181a24;
    color: #b4b7c1;
    border:1px solid #2e303d;
  }
  :deep(.ant-select-selector){
    .anticon{
      color:#b4b7c1;
    }
  }
  /*:deep(.ant-input-number){
    .ant-input-number-handler-wrap{
      background:#b4b7c1;
    }
  }*/
  :deep(.form-item){
    .anticon{
      color: #b4b7c1;
    }
  }
  :deep(.ant-switch){
    background: #181a24;
    &:after{
      background: #3d404c;
    }
    &.ant-switch-checked{
      background: #283851;
      &:after{
        background: #3a89fe;
      }
    }
  }
}
</style>
