<template>
  <a-drawer
    :title="t('选择字体图标')"
    placement="right"
    :closable="false"
    :visible="visible"
    wrapClassName="IconDrawer drawBox"
    @close="close"
  >
    <div class="icons">
      <i
        v-for="(item, index) in icons"
        :key="item.icon"
        class="icon"
        :class="item.icon"
        @click="onSelected(item)"
      ></i>
    </div>
  </a-drawer>
</template>
<script lang="ts">
import { computed, defineComponent, reactive, ref } from 'vue';
import { useStore } from 'vuex';
import { addIcons } from '../utils';
export default defineComponent({
  props: {
    visible: Boolean,
  },
  emits: ['chooseIcon', 'update:visible'],
  setup(props, { emit }) {
    function close() {
      emit('update:visible', false);
    }
    // 实际图标
    const iconUrls = ['icon/通用图标/iconfont.css'];
    const icons = reactive([]);

    iconUrls.forEach(async (url) => {
      const iconList: any = await addIcons(url.replace('.css', '.json'));
      icons.push(...iconList.list);
    });

    function onSelected(item: any) {
      emit('chooseIcon', item);
      close();
    }

    return {
      close,
      icons,
      onSelected,
    };
  },
});
</script>

<style lang="scss" scoped>
.icons {
  width: 100%;
  display: grid;
  grid-template-columns: 1fr 1fr 1fr 1fr;
  gap: 16px;

  .icon {
    margin: 0;
    display: flex;
    align-items: center;
    justify-content: center;

    font-size: 32px;
  }
}
</style>

<style lang="scss">
.drawBox {
  &.IconDrawer{
    .ant-drawer-body {
      padding: 0;
    }
  }
  .ant-drawer-content{
    background: #232630;
    .ant-drawer-header{
      background: #232630;
      border-bottom: 1px solid #0d0e10;
      .ant-drawer-title{
        color: #b4b7c1;
      }
    }
    .ant-drawer-body{
      .icons{
        background: #232630;
        border: 1px solid #0d0e10;
        color: #b4b7c1;
      }
      
    }
  }
}
</style>
