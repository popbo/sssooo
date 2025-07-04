<template>
  <div>
    <a-dropdown class="MenuItem" overlayClassName="dropMenu">
      <div
        @click.prevent="action(menuItem.action)"
        :style="{
          cursor: 'pointer',
          color: active ? '#1890ff' : ''
        }"
      >
        <div class="iconItem">
          <div class="icon">
            <i :class="menuItem.icon"></i>
            <i
              class="abs t-icon t-triangle-down rightIcon"
              v-if="menuItem.children"
            ></i>
            <div v-if="needOperate" class="redPoint"></div>
          </div>

          <span class="font">{{ t(menuItem.name) }}</span>
        </div>
      </div>
      <template #overlay v-if="menuItem.children">
        <a-menu>
          <template v-for="subItem in menuItem.children" :key="subItem.name">
            <a-menu-item
              v-if="subItem.name && subItem.action"
              class="menuItem"
              @click="action(subItem.action, subItem.params)"
            >
              <div class="nameKeyboard">
                <span>{{ t(subItem.name) }}</span>
              </div>
            </a-menu-item>
            <a
              v-else-if="subItem.name && subItem.url"
              :href="subItem.url"
              :target="subItem.target"
            >
              <a-menu-item class="menuItem"> {{ t(subItem.name) }}</a-menu-item>
            </a>
            <a-menu-divider v-else />
          </template>
        </a-menu>
      </template>
    </a-dropdown>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue';

import { Meta2d } from '@topometa2d/core';

declare const meta2d: Meta2d;

interface MenuItemType {
  name: string;
  icon: string;
  action?: string;
  children?: {
    name?: string;
    keyboard?: string;
    action?: string;

    url?: string;
    target?: string;
    params?: any;
  }[];
}

export default defineComponent({
  name: 'MenuItem',
  props: {
    menuItem: {
      type: Object as PropType<MenuItemType>,
      require: true
    },
    active: {
      type: Boolean
    },
    needOperate: {
      type: Boolean
    }
  },
  emits: ['action'],
  setup(props, { emit }) {
    function action(action?: string, params?: any) {
      // 交由父组件处理，存在 action 才处理
      action && emit('action', action, params);
    }

    return {
      action
    };
  }
});
</script>

<style lang="scss">
@import '@/styles/variables.scss';
.MenuItem {
  .iconItem {
    cursor: pointer;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;

    &:hover {
      color: $color-primary;
    }
    .icon {
      height: 24px;
      line-height: 24px;
      position: relative;
      i {
        font-size: 18px;

        // 编辑图标特殊处理
        &.topo-icon.topo-gongyong--bianji {
          font-size: 17px;
        }

        &.rightIcon {
          font-size: 10px;
        }
      }
      .redPoint {
        position: absolute;
        top: 0px;
        right: -2px;
        width: 6px;
        height: 6px;
        border-radius: 50%;
        background-color: #ff4101;
      }

      span.number {
        font-size: 14px;
        font-family: PingFangSC, PingFangSC-Regular;
        font-weight: 400;
        text-align: center;
        line-height: 20px;
      }
    }

    .font {
      height: 18px;
      font-size: 12px;
      font-family: PingFangSC, PingFangSC-Regular;
      font-weight: 400;
      line-height: 18px;
    }
  }
}
.dropMenu {
  .ant-dropdown-menu{
    background: #2d2f38;
  }
  .menuItem {
    padding-left: 28px;

    min-width: 187px;
    height: 40px;
    background: #2d2f38;
    width: 100%;

    display: flex;
    align-items: center;
    justify-content: center;

    font-size: 14px;
    font-family: PingFangSC, PingFangSC-Regular;
    font-weight: 400;
    line-height: 14px;
    color: #a5a8b2;

    .ant-dropdown-menu-title-content {
      width: 100%;
      .nameKeyboard {
        display: flex;
        align-items: center;
      }
    }

    &:hover {
      background: #3d404d;
      color: #fff;
    }
  }

  .scaleView {
    padding: 8px 16px;
    background-color: #181a24;
    display: flex;
    align-items: center;
    color:#b4b7c1;
    box-shadow: 0 0 10px rgb(0 0 0 / 30%);
    .ant-btn{
      background: #232630;
      color:#b4b7c1;
      border:1px solid #283241;
      &:hover{
        border:1px solid #1890ff;
        color:#1890ff;
      }
    }
    i {
      cursor: pointer;
    }
  }

  .lineWidthView {
    min-width: 220px;
    padding: 8px;
    background-color: #181a24;
    box-shadow: 0 0 10px rgb(0 0 0 / 30%);

    .ant-input-number {
      width: 100%;
      background: #232630;
      border:1px solid #283241;
      color:#b4b7c1
    }
  }

  .lineItem {
    .ant-dropdown-menu-title-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
}

.arrowIcon {
  font-size: 30px;
}
</style>
