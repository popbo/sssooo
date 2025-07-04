<template>
  <a-menu class="CanvasContextMenu">
    <a-menu-item
      class="menuItem"
      @click="layerMove('top')"
      :disabled="!choosePens() && !choosePen()"
      >{{ t('置顶') }}</a-menu-item
    >
    <a-menu-item
      class="menuItem"
      @click="layerMove('bottom')"
      :disabled="!choosePens() && !choosePen()"
      >{{ t('置底') }}</a-menu-item
    >
    <a-menu-item
      class="menuItem"
      @click="layerMove('up')"
      :disabled="!choosePens() && !choosePen()"
      >{{ t('上一个图层') }}</a-menu-item
    >
    <a-menu-item
      class="menuItem"
      @click="layerMove('down')"
      :disabled="!choosePens() && !choosePen()"
      >{{ t('下一个图层') }}</a-menu-item
    >
    <!-- <a-menu-item v-if="hasImage()" class="menuItem" @click="changeBottom">{{
      isBottom() ? t('去上层') : t('去下层')
    }}</a-menu-item> -->
    <a-menu-divider />
    <template v-if="choosePens()">
      <a-menu-item class="menuItem" @click="combine()">{{
        t('组合')
      }}</a-menu-item>
      <a-menu-item class="menuItem" @click="combine(0)"
        >{{ t('组合') }}{{ t('为状态') }}</a-menu-item
      >
    </template>
    <a-menu-item class="menuItem" v-if="hasChildren()" @click="unCombine()">{{
      t('取消组合')
    }}</a-menu-item>
    <a-menu-item class="menuItem" v-if="hasCombine()" @click="OneCancelCombine()">{{
      t('一键取消组合')
    }}</a-menu-item>
    <a-menu-item
      class="menuItem"
      :disabled="!choosePens() && !choosePen()"
      @click="onLock"
      >{{ getLocked() ? t('解锁') : t('锁定') }}</a-menu-item
    >
    <a-menu-divider />
    <a-menu-item
      class="menuItem"
      :disabled="!choosePens() && !choosePen()"
      @click="onDel"
    >
      {{ t('删除') }}</a-menu-item
    >
    <a-menu-divider />
    <template v-if="isLine()">
      <a-menu-item v-if="isLineType()" class="menuItem" @click="changeType(0)">
        {{ t('变成节点') }}</a-menu-item
      >
      <a-menu-item v-else class="menuItem" @click="changeType(1)">
        {{ t('变成连线') }}</a-menu-item
      >
      <a-menu-divider />
    </template>
    <a-menu-item class="menuItem" :disabled="cantUndo()" @click="onUndo"
      ><span>{{ t('撤销') }}</span>
      <span>Ctrl + Z</span>
    </a-menu-item>
    <a-menu-item class="menuItem" :disabled="cantRedo()" @click="onRedo">
      <span>{{ t('恢复') }}</span>
      <span>Shift + Z</span>
    </a-menu-item>
    <a-menu-divider />
    <a-menu-item
      class="menuItem"
      :disabled="!choosePens() && !choosePen()"
      @click="onCut"
    >
      <span>{{ t('剪切') }} </span>
      <span>Ctrl + X</span></a-menu-item
    >
    <a-menu-item
      class="menuItem"
      :disabled="!choosePens() && !choosePen()"
      @click="onCopy"
    >
      <span> {{ t('复制') }}</span>
      <span>Ctrl + C</span></a-menu-item
    >
    <a-menu-item class="menuItem" @click="onPaste">
      <span>{{ t('粘贴') }}</span>
      <span>Ctrl + V</span>
    </a-menu-item>
  </a-menu>
</template>

<script lang="ts">
import { LockState, Pen, PenType, Meta2d,deepClone } from '@topometa2d/core';
import { defineComponent, inject, onMounted, onUnmounted, ref } from 'vue';

declare const meta2d: Meta2d;

export default defineComponent({
  name: 'CanvasContextMenu',
  props: {
    contextVisible: Boolean,
  },
  emits: ['update:contextVisible'],
  setup(props, { emit }) {
    function closeMenu() {
      emit('update:contextVisible', false);
    }

    function layerMove(type: 'top' | 'bottom' | 'up' | 'down') {
      const pens = meta2d.store.active;
      if (hasImage()) {
        if (type === 'top') {
          meta2d.setValue({
            id: pens[0].id,
            isBottom: false,
          });
          meta2d.top(pens[0]);
        } else if (type === 'bottom') {
          meta2d.setValue({
            id: pens[0].id,
            isBottom: true,
          });
          meta2d.bottom(pens[0]);
        } else if (type === 'up') {
          if (pens[0].isBottom) {
            meta2d.setValue({
              id: pens[0].id,
              isBottom: false,
            });
          } else {
            meta2d.up(pens[0]);
          }
        } else if (type === 'down') {
          if (!pens[0].isBottom) {
            meta2d.setValue({
              id: pens[0].id,
              isBottom: true,
            });
          } else {
            meta2d.down(pens[0]);
          }
        }
      } else {
        if (pens[0].name === 'gif') {
          let zIndex = pens[0].calculative.zIndex;
          if (type === 'top') {
            zIndex == 999;
          }
          if (type === 'bottom') {
            zIndex == -999;
          }
          if (type === 'up') {
            zIndex++;
          }
          if (type === 'down') {
            zIndex--;
          }
          meta2d.setValue({
            id: pens[0].id,
            zIndex,
          });
        } else {
          if (Array.isArray(pens)) {
            for (const pen of pens) {
              meta2d[type](pen);
            }
          }
        }
        meta2d.render();
      }
      closeMenu();
    }

    function choosePen(): boolean {
      return meta2d?.store.active?.length === 1;
    }

    function choosePens(): boolean {
      return meta2d?.store.active?.length > 1;
    }

    function hasChildren(): boolean {
      return choosePen() && meta2d?.store.active[0]?.children?.length > 0;
    }

    function hasCombine(): boolean {
      const filterCombineArr=meta2d?.store.active.filter(function(item) {
        return item.name === 'combine' && item?.children?.length >0&&item.showChild===undefined;
      });
      return filterCombineArr?.length > 0;
    }
    function OneCancelCombine(){
      meta2d?.store.active.forEach(x=>{
        if(x.children?.length>0&&x.showChild===undefined){
        const activeCombineItem = meta2d?.store.data.pens?.filter(y=>y.id===x.id)
          const deepCopyChild=deepClone(activeCombineItem)
          meta2d.uncombine(activeCombineItem[0])
          filterCombine(deepCopyChild[0].children)
        }
      })
      //处理一键取消组合顺序问题pen出现空combine项
      meta2d?.store.data.pens.forEach(item=>{
        if(item.name==='combine'&&item?.children===undefined){
          meta2d.delete([item])
        }
      })
      closeMenu();
    }
    //处理组合下的组合
    function filterCombine(arr){
      arr.forEach(x=>{
        meta2d?.store.data.pens.forEach(y=>{
          if(x===y.id&&y.name==='combine'&&y.showChild===undefined){
            filterCombine(y.children)
            meta2d.uncombine(y)
          }
        })
      })
     
    }
    function getLocked() {
      return meta2d?.store.active?.some((pen: Pen) => pen.locked);
    }

    function combine(showChild?: number) {
      meta2d.combine(meta2d.store.active, showChild);
      closeMenu();
    }

    function unCombine() {
      meta2d.uncombine();
      closeMenu();
    }

    function onLock() {
      const locked = !getLocked();
      const pens = meta2d.store.active;
      if (Array.isArray(pens)) {
        for (const pen of pens) {
          pen.locked = locked ? LockState.DisableMove : LockState.None;
        }
      }
      meta2d.render();
      closeMenu();
    }

    function onDel() {
      meta2d.delete();
      closeMenu();
    }

    function onUndo() {
      meta2d.undo();
      closeMenu();
    }

    function onRedo() {
      meta2d.redo();
      closeMenu();
    }

    function onCut() {
      meta2d.cut();
      closeMenu();
    }

    function onCopy() {
      meta2d.copy();
      closeMenu();
    }

    function onPaste() {
      meta2d.paste();
      closeMenu();
    }

    function cantUndo(): boolean {
      return (
        !!meta2d?.store.data.locked ||
        meta2d?.store.histories.length == 0 ||
        meta2d?.store.historyIndex == null ||
        meta2d?.store.historyIndex < 0
      );
    }

    function cantRedo(): boolean {
      return (
        !!meta2d?.store.data.locked ||
        meta2d?.store.histories.length == 0 ||
        meta2d?.store.historyIndex == null ||
        meta2d?.store.historyIndex > meta2d?.store.histories.length - 2
      );
    }

    function isLine(): boolean {
      return choosePen() && meta2d?.store.active[0]?.name === 'line';
    }

    function isLineType(): boolean {
      return meta2d?.store.active[0]?.type === PenType.Line;
    }

    function changeType(type: number) {
      const id = meta2d.store.active[0].id;
      meta2d.setValue(
        {
          id,
          type,
        },
        {
          history: true,
        }
      );
      closeMenu();
    }

    function hasImage(): boolean {
      const pen = meta2d.store.active[0];
      return choosePen() && pen.image && pen.name !== 'gif';
    }

    function isBottom(): boolean {
      const pen = meta2d.store.active[0];
      return hasImage() && pen.isBottom;
    }

    // function changeBottom() {
    //   const pen = meta2d.store.active[0];
    //   meta2d.setValue({
    //     id: pen.id,
    //     isBottom: !pen.isBottom,
    //   });
    //   closeMenu();
    // }

    return {
      choosePen,
      choosePens,
      layerMove,
      combine,
      hasChildren,
      hasCombine,
      unCombine,
      OneCancelCombine,
      getLocked,
      onLock,
      onDel,
      onUndo,
      onRedo,
      onCut,
      onCopy,
      onPaste,
      cantUndo,
      cantRedo,
      isLine,
      isLineType,
      changeType,
      hasImage,
      isBottom,
      // changeBottom,
    };
  },
});
</script>

<style lang="scss">
@import '@/styles/variables.scss';

.CanvasContextMenu {
  z-index: 9999;
  min-width: 200px;
  background: #2d2f38;
  color: #b4b7c1;
  .menuItem {
    &.ant-dropdown-menu-item-disabled{
      .ant-dropdown-menu-title-content{
        color:#43464d

      }
    }
    &.ant-dropdown-menu-item-disabled,&.ant-dropdown-menu-item{
      &:hover{
        background: #3d404d;
        color:#fff
      }
    }
    .ant-dropdown-menu-title-content {
      display: flex;
      justify-content: space-between;
      color: #b4b7c1;
      &:hover{
        color:#fff
      }
    }
  }
  .ant-dropdown-menu-item-divider,
  .ant-dropdown-menu-submenu-title-divider {
    background-color: #363841;
  }
}
</style>
