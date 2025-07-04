<template>
  <div class="tree">
    <div
      class="item"
      v-for="(item, i) in items"
      :key="item.id"
      :class="{ gray: item.visible === false }"
    >
      <div
        class="content"
        :class="activePens.includes(item.id)?'active':''"
        :title="`id: ${item.id}
name: ${item.name}
${item.description ? 'description: ' + item.description : ''}
`"
      >
        <div class="text">
          <template v-if="item.children">
            <CaretRightOutlined
              v-if="!item.opened"
              class="mr12 pointer"
              @click.stop="onOpenItem(item, true)"
            />
            <CaretDownOutlined
              v-else
              class="mr12 pointer"
              @click.stop="onOpenItem(item, false)"
            />

            <template v-if="!item.type">
              <FolderOutlined v-if="!item.opened" />
              <FolderOpenOutlined v-else />
            </template>
          </template>

          <template v-else>
            <CaretDownOutlined class="mr12" style="opacity: 0" />
            <FundOutlined v-if="!item.type" />
          </template>

          <RiseOutlined v-if="item.type" />

          <label
            ><span v-if="!item.showInput" @click="onSelect(item,'jg',$event)" @dblclick="showInput(item, true)">{{
              item.description || item.name
            }}</span>
            <a-input
              autoFocus
              v-else
              v-model:value="item.description"
              @change="onChanged(item, 'description')"
              @blur="showInput(item, false)"
            ></a-input
          ></label>
        </div>
        <div class="state">
          <span class="mr8">
            <a-tooltip placement="top" v-if="!item.locked">
              <template #title>
                <span>{{ t('可编辑') }}</span>
              </template>
              <i class="t-icon t-unlock" @click.stop="onLock(item, 1)"></i>
            </a-tooltip>
            <a-tooltip placement="top" v-else-if="item.locked == 1">
              <template #title>
                <span>{{ t('禁止编辑') }}</span>
              </template>
              <i class="t-icon t-lock" @click.stop="onLock(item, 2)"></i>
            </a-tooltip>
            <a-tooltip placement="top" v-else-if="item.locked == 2">
              <template #title>
                <span>{{ t('禁止移动') }}</span>
              </template>
              <i class="t-icon t-wufayidong" @click.stop="onLock(item, 10)"></i>
            </a-tooltip>
            <a-tooltip placement="top" v-else-if="item.locked == 10">
              <template #title>
                <span>{{ t('被禁用') }}</span>
              </template>
              <i class="t-icon t-jinyong" @click.stop="onLock(item, 0)"></i>
            </a-tooltip>
          </span>
          <span>
            <a-tooltip placement="top" v-if="item.visible !== false">
              <template #title>
                <span>{{ t('显示') }}</span>
              </template>
              <EyeOutlined @click.stop="onVisiable(item, false)" />
            </a-tooltip>
            <a-tooltip placement="top" v-else>
              <template #title>
                <span>{{ t('隐藏') }}</span>
              </template>
              <EyeInvisibleOutlined @click.stop="onVisiable(item, true)" />
            </a-tooltip>
          </span>
        </div>
      </div>

      <Tree v-if="item.children && item.opened" :items="item.children" />
    </div>
  </div>
</template>

<script lang="ts">
import { Pen, Meta2d } from '@topometa2d/core';
import { defineComponent, PropType, Ref, ref, toRaw, watch } from 'vue';
import {
  EyeOutlined,
  EyeInvisibleOutlined,
  CaretRightOutlined,
  CaretDownOutlined,
  FundOutlined,
  FolderOutlined,
  FolderOpenOutlined,
  RiseOutlined,
  UnlockOutlined,
  LockOutlined,
  StopOutlined,
} from '@ant-design/icons-vue';
import { TreeItem } from '../utils';

declare const meta2d: Meta2d;
export default defineComponent({
  name: 'Tree',
  components: {
    EyeOutlined,
    EyeInvisibleOutlined,
    CaretRightOutlined,
    CaretDownOutlined,
    FundOutlined,
    FolderOutlined,
    FolderOpenOutlined,
    RiseOutlined,
    UnlockOutlined,
    LockOutlined,
    StopOutlined,
  },
  props: {
    items: {
      type: Array as PropType<TreeItem[]>,
      require: true,
    },
    activePens: {
      type: Array as PropType<string[]>,
      require: true,
      default: () => {
        return [];
      }
    }
  },
  setup(props,context) {
    function showInput(item: TreeItem, show: boolean) {
      item.showInput = show;
    }
    function onSelect(item: TreeItem,type,e) {
      if(e.target == e.currentTarget){
          context.emit('onJgSelect',type)
          let pens = meta2d.find(item.id);
          meta2d.active(pens,true);
          meta2d.gotoView(pens[0]);
          meta2d.resize();
          meta2d.render();
      }
    }

    function onOpenItem(item: TreeItem, open: boolean) {
      item.opened = open;
    }

    function onChanged(item: TreeItem, key: string) {
      const pens = meta2d.find(item.id);
      if (pens.length === 1) {
        pens[0][key] = item[key];
        meta2d.setValue({
          id: pens[0].id,
          [key]: pens[0][key],
        });
      }
    }

    function onLock(item: TreeItem, locked: number) {
      item.locked = locked;
      onChanged(item, 'locked');
    }

    function onVisiable(item: TreeItem, visible: boolean) {
      changeVisible(item, visible);
      const pens = meta2d.find(item.id);
      if (pens.length === 1) {
        meta2d.setVisible(pens[0], visible);
        meta2d.render();
      }
    }

    return {
      showInput,
      onSelect,
      onOpenItem,
      onChanged,
      onLock,
      onVisiable,
    };
  },
});

// 只更改视图，不改变 meta2d 上的数据
function changeVisible(item: TreeItem, visible: boolean) {
  item.visible = visible;
  // 递归改变子节点的可视状态
  if (item.children && item.children.length > 0) {
    for (const child of item.children) {
      changeVisible(child, visible);
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';
.tree {
  // margin-top: 16px;
  & > .item {
    position: relative;
    margin: 10px;

    & > .content {
      display: flex;
      justify-content: space-between;
      align-items: center;

      &:hover {
        color: $color-primary;
      }
    }

    &>.active{
      color: $color-primary;
    }

    .text {
      display: flex;
      align-items: center;

      label {
        margin-right: 10px;
        span {
          -webkit-box-orient: vertical;
          overflow: hidden;
          text-overflow: ellipsis;
          display: -webkit-box;
          word-break: break-all;
          word-break: break-word;
          -webkit-line-clamp: 1;
          line-height: 1;
          height: 14px;
          padding-right: 8px;
        }
      }

      & > .anticon {
        margin-right: 8px;
      }
    }

    .state {
      white-space: nowrap;
    }
  }

  .tree {
    margin-top: 0;
    margin-left: 12px;
    & > .item {
      margin-right: 0;
    }
  }
}
</style>
