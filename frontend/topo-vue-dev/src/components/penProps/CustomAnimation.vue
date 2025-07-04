<template>
  <a-drawer
    :title="t('节点动画')"
    placement="right"
    :closable="false"
    :visible="visible"
    wrapClassName="CustomAnimation drawBox"
    @close="close"
  >
    <div class="buttonDiv">
      <a-button @click="addFrame" type="primary" class="add-button">{{
        `${t('新增')} ${t('动画帧')}`
      }}</a-button>
    </div>
    <a-collapse
      v-model:activeKey="activeKey"
      expand-icon-position="right"
      class="PenProps InnerScrollCollapse"
      :bordered="false"
    >
      <a-collapse-panel
        v-for="(frame, index) in pen.frames"
        :header="`${t('动画帧')} ${Number(index) + 1} / ${pen.frames.length}`"
      >
        <template #extra>
          <a-popconfirm
            :title="t('确定删除吗？')"
            ok-text="是"
            cancel-text="否"
            @confirm="delFrame(index)"
            :getPopupContainer="triggerNode => {
              return triggerNode.parentNode
            }"
            @cancel.stop
          >
            <i class="t-icon t-shanchu extraDelete" @click.stop=""></i>
          </a-popconfirm>
        </template>
        <FormEvery
          :configs="frameConfigs"
          :obj="frame"
          @change-value="changeValue($event, frame)"
        />
        <!-- 颜色 -->
        <FormEvery
          :configs="colorConfigs(frame)"
          :obj="frame"
          @change-value="changeValue($event, frame)"
        />
        <!-- 文字 -->
        <FormEvery
          :configs="textConfigs(frame)"
          :obj="frame"
          @change-value="changeValue($event, frame)"
        />
        <!-- icon -->
        <FormEvery
          :configs="iconConfigs(frame)"
          :obj="frame"
          @change-value="changeValue($event, frame)"
        />
        <!-- image -->
        <FormEvery
          :configs="imageConfigs(frame)"
          :obj="frame"
          @change-value="changeValue($event, frame)"
        />
      </a-collapse-panel>
    </a-collapse>
  </a-drawer>
</template>
<script lang="ts">
import { t } from '@/i18n/i18n';
import { Gradient, Pen, Meta2d } from '@topometa2d/core';
import {
  computed,
  defineComponent,
  inject,
  reactive,
  Ref,
  ref,
  toRaw
} from 'vue';
import FormEvery from '../common/FormEvery.vue';
import { addShowChild, normalFonts, whiteSpaces } from '../defaults';
import { FormItemType } from '../utils';
declare const meta2d: Meta2d;
export default defineComponent({
  props: {
    visible: Boolean
  },
  emits: ['update:visible'],
  setup(props, { emit }) {
    function close() {
      emit('update:visible', false);
    }
    const activeKey = ref([]);
    // 此处的 pen 是响应式的
    const pen: Ref<Pen> = inject('activePen');
    function delFrame(index: number) {
      pen.value.frames.splice(index, 1);
      // changeValue();
      meta2d.setValue({ id: pen.value.id, frames: pen.value.frames },{render:false,history:false,doEvent:false});
    }
    function addFrame() {
      // TODO: 可能会有问题
      meta2d.stopAnimate();
      !Array.isArray(pen.value.frames) && (pen.value.frames = []);
      pen.value.frames.push({
        duration: 200,
        visible: true, // 默认显示
        // strokeType: Gradient.None, // 默认值，需要有值
        // bkType: Gradient.None
      });
      meta2d.setValue({ id: pen.value.id, frames: pen.value.frames },{render:false,history:false,doEvent:false});
    }
    // 改变了某个值
    async function changeValue(
      { value, keys }: { value?: any; keys?: string[] } = {},
      frame?: any
    ) {
      if (keys) {
        if (keys[0] === 'dash') {
          const { lineDashObj } = await import('../defaults');
          frame.lineDash = lineDashObj[value];
        }
        // 设置 icon 的同时，需要设置 iconFamily
        if (keys[0] === 'icon') {
          frame.iconFamily = value.iconFamily;
        }
      }
      // frame 值有 undefined or null 干掉
      pen.value.frames.forEach((frame, index) => {
        for (const key in frame) {
          if (Object.prototype.hasOwnProperty.call(frame, key)) {
            if (frame[key] === undefined || frame[key] === null) {
              delete frame[key];
            }
          }
        }
      });
      // 业务属性，仅仅做展示
      const showDuration = meta2d.calcAnimateDuration(toRaw(pen.value));
      meta2d.setValue({
        id: pen.value.id,
        frames: pen.value.frames,
        showDuration
      });
      (pen.value as any).showDuration = showDuration;
    }
    const frameConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          key: 'duration',
          name: `${t('时长')}(ms)`,
          type: 'number'
        },
        {
          key: 'x',
          placeholder: 'px',
          type: 'number',
          name: `${t('偏移')}X`
        },
        {
          key: 'y',
          placeholder: 'px',
          type: 'number',
          name: `${t('偏移')}Y`
        },
        {
          key: 'scale',
          type: 'number',
          name: `${t('缩放')}`
        },
        {
          key: 'borderRadius',
          type: 'number',
          name: `${t('圆角')}`,
          min: 0,
          placeholder: `${t('< 1 比例')}`
        },
        {
          key: 'rotate',
          type: 'number',
          placeholder: '°',
          name: `${t('旋转')}`
        },
        {
          key: 'progress',
          type: 'number',
          name: `${t('进度')}`,
          step: 0.1,
          max: 1,
          min: 0
        },
        {
          key: 'progressColor',
          type: 'color',
          name: `${t('进度颜色')}`
        },
        {
          key: 'dash',
          type: 'select',
          name: `${t('线条样式')}`,
          options: [
            {
              label: `<svg xmlns="http://www.w3.org/2000/svg" version="1.1" style="height: 20px;">
                  <g fill="none" stroke="white" stroke-width="1">
                    <path d="M0 9 l85 0" />
                  </g>
                </svg>`,
              value: 0
            },
            {
              label: `<svg xmlns="http://www.w3.org/2000/svg" version="1.1" style="height: 20px;">
                  <g fill="none" stroke="white" stroke-width="1">
                    <path stroke-dasharray="5 5" d="M0 9 l85 0" />
                  </g>
                </svg>`,
              value: 1
            },
            {
              label: `<svg xmlns="http://www.w3.org/2000/svg" version="1.1" style="height: 20px;">
                  <g fill="none" stroke="white" stroke-width="1">
                    <path stroke-dasharray="10 10" d="M0 9 l85 0" />
                  </g>
                </svg>`,
              value: 2
            },
            {
              label: `<svg xmlns="http://www.w3.org/2000/svg" version="1.1" style="height: 20px;">
                  <g fill="none" stroke="white" stroke-width="1">
                    <path stroke-dasharray="10 10 2 10" d="M0 9 l85 0" />
                  </g>
                </svg>`,
              value: 3
            }
          ]
        },
        {
          key: 'lineWidth',
          placeholder: 'px',
          type: 'number',
          name: `${t('线条宽度')}`
        },
        {
          key: 'lineDashOffset',
          placeholder: 'px',
          type: 'number',
          name: `${t('线条偏移')}`
        },
        {
          key: 'globalAlpha',
          type: 'number',
          placeholder: '(0-1)',
          name: `${t('透明度')}`,
          max: 1,
          min: 0,
          step: 0.1
        },
        {
          key: 'visible',
          type: 'switch',
          name: `${t('显示')}`
        }
      ];

      addShowChild(pen.value, configs);

      return configs;
    });

    const colorConfigs = (frame: any) => {
      const configs: FormItemType[] = [
        {
          key: 'strokeType',
          type: 'select',
          name: `${t('线条渐变')}`,
          options: [
            {
              label: `${t('无')}`,
              value: 0
            },
            {
              label: `${t('线性渐变')}`,
              value: 1
            }
          ]
        }
      ];

      if (!frame.strokeType) {
        configs.push({
          key: 'color',
          type: 'color',
          name: `${t('颜色')}`
        });
      } else {
        configs.push(
          {
            key: 'lineGradientFromColor',
            type: 'color',
            name: `${t('开始颜色')}`
          },
          {
            key: 'lineGradientToColor',
            type: 'color',
            name: `${t('结束颜色')}`
          },
          {
            key: 'lineGradientAngle',
            type: 'number',
            name: `${t('渐变角度')}`
          }
        );
      }

      configs.push({
        key: 'bkType',
        name: `${t('背景')}`,
        type: 'select',
        options: [
          {
            label: `${t('纯色背景')}`,
            value: 0
          },
          {
            label: `${t('线性渐变')}`,
            value: 1
          },
          {
            label: `${t('径向渐变')}`,
            value: 2
          }
        ]
      });

      if (!frame.bkType) {
        configs.push({
          key: 'background',
          name: `${t('背景颜色')}`,
          type: 'color'
        });
      } else {
        configs.push(
          {
            key: 'gradientFromColor',
            name: `${t('开始颜色')}`,
            type: 'color'
          },
          {
            key: 'gradientToColor',
            name: `${t('结束颜色')}`,
            type: 'color'
          }
        );
      }

      if (frame.bkType === Gradient.Linear) {
        configs.push({
          key: 'gradientAngle',
          name: `${t('渐变角度')}`,
          type: 'number'
        });
      } else if (frame.bkType === Gradient.Radial) {
        configs.push({
          key: 'gradientRadius',
          name: `${t('渐变半径')}`,
          type: 'number',
          min: 0,
          max: 1,
          step: 0.01
        });
      }

      configs.push(
        {
          key: 'shadowColor',
          type: 'color',
          name: `${t('阴影颜色')}`
        },
        {
          key: 'shadowBlur',
          type: 'number',
          name: `${t('阴影模糊')}`,
          min: 0
        },
        {
          key: 'shadowOffsetX',
          type: 'number',
          name: `${t('阴影')} X ${t('偏移')}`
        },
        {
          key: 'shadowOffsetY',
          type: 'number',
          name: `${t('阴影')} Y ${t('偏移')}`
        },
        {
          key: 'textHasShadow',
          type: 'switch',
          name: `${t('文字阴影')}`
        }
      );

      return configs;
    };

    const textConfigs = (frame: any) => {
      const configs: FormItemType[] = [
        {
          key: 'fontFamily',
          name: `${t('字体名')}`,
          type: 'autoComplete',
          options: normalFonts.map(font => ({
            label: font,
            value: font
          }))
        },
        {
          key: 'fontSize',
          name: `${t('字体大小')}`,
          type: 'number'
        },
        {
          key: 'textColor',
          name: `${t('文字颜色')}`,
          type: 'color'
        },
        {
          key: 'hoverTextColor',
          name: `${t('浮动文字颜色')}`,
          type: 'color'
        },
        {
          key: 'activeTextColor',
          name: `${t('选中文字颜色')}`,
          type: 'color'
        },
        {
          key: 'textBackground',
          name: `${t('文字背景')}`,
          type: 'color'
        },
        {
          key: 'fontStyle',
          name: `${t('倾斜')}`,
          type: 'select',
          options: [
            {
              label: `${t('正常')}`,
              value: 'normal'
            },
            {
              label: `${t('倾斜')}`,
              value: 'italic'
            }
          ]
        },
        {
          key: 'fontWeight',
          name: t('加粗'),
          type: 'select',
          options: [
            {
              label: `${t('正常')}`,
              value: 'normal'
            },
            {
              label: `${t('加粗')}`,
              value: 'bold'
            }
          ]
        },
        {
          key: 'textAlign',
          name: `${t('水平对齐')}`,
          type: 'select',
          options: [
            {
              label: `${t('左对齐')}`,
              value: 'left'
            },
            {
              label: `${t('居中')}`,
              value: 'center'
            },
            {
              label: `${t('右对齐')}`,
              value: 'right'
            }
          ]
        },
        {
          key: 'textBaseline',
          name: `${t('垂直对齐')}`,
          type: 'select',
          options: [
            {
              label: `${t('顶部对齐')}`,
              value: 'top'
            },
            {
              label: `${t('居中')}`,
              value: 'middle'
            },
            {
              label: `${t('底部对齐')}`,
              value: 'bottom'
            }
          ]
        },
        {
          key: 'lineHeight',
          name: `${t('行高')}`,
          type: 'number',
          min: 0
        },
        {
          key: 'whiteSpace',
          name: `${t('换行')}`,
          type: 'select',
          options: whiteSpaces as any
        },
        {
          key: 'textWidth',
          type: 'number',
          name: `${t('文字宽度')}`
        },
        {
          key: 'textHeight',
          type: 'number',
          name: `${t('文字高度')}`
        },
        {
          key: 'textLeft',
          type: 'number',
          name: `${t('水平偏移')}`
        },
        {
          key: 'textTop',
          type: 'number',
          name: `${t('垂直偏移')}`
        },
        {
          // undefined 认为是 true 所以使用下拉框
          key: 'ellipsis',
          type: 'select',
          name: `${t('超出省略')}`,
          placeholder: `${t('是')}`,
          options: [
            {
              label: `${t('否')}`,
              value: false
            },
            {
              label: `${t('是')}`,
              value: true
            }
          ]
        },
        {
          name: `${t('文本内容')}`,
          key: 'text',
          type: 'textarea',
          rows: 3
        }
      ];
      return configs;
    };

    const iconConfigs = (frame: any) => {
      const configs: FormItemType[] = [
        {
          key: 'icon',
          name: `${t('图标')}`,
          type: 'icon',
          iconFamily: frame.iconFamily
        },
        {
          key: 'iconColor',
          name: `${t('图标')} ${t('颜色')}`,
          type: 'color'
        },
        {
          key: 'iconRotate',
          name: `${t('图标旋转')}`,
          type: 'number',
          min: 0
        },
        {
          key: 'iconSize',
          name: `${t('图标')} ${t('大小')}`,
          type: 'number'
        },
        {
          key: 'iconAlign',
          name: `${t('对齐方式')}`,
          type: 'select',
          options: [
            {
              label: `${t('居中')}`,
              value: 'center'
            },
            {
              label: `${t('上')}`,
              value: 'top'
            },
            {
              label: `${t('下')}`,
              value: 'bottom'
            },
            {
              label: `${t('左')}`,
              value: 'left'
            },
            {
              label: `${t('右')}`,
              value: 'right'
            },
            {
              label: `${t('左上')}`,
              value: 'left-top'
            },
            {
              label: `${t('右上')}`,
              value: 'right-top'
            },
            {
              label: `${t('左下')}`,
              value: 'left-bottom'
            },
            {
              label: `${t('右下')}`,
              value: 'right-bottom'
            }
          ]
        },
        {
          key: 'iconLeft',
          name: `${t('水平偏移')}`,
          type: 'number'
        },
        {
          key: 'iconTop',
          name: `${t('垂直偏移')}`,
          type: 'number'
        }
      ];
      return configs;
    };

    const imageConfigs = (frame: any) => {
      const configs: FormItemType[] = [
        {
          key: 'image',
          name: `${t('图片')}`,
          type: 'image',
        },
        {
          key: 'iconWidth',
          name: `${t('宽度')}`,
          type: 'number',
        },
        {
          key: 'iconHeight',
          name: `${t('高度')}`,
          type: 'number',
        },]
      return configs;
    }
    return {
      close,
      activeKey,
      pen,
      delFrame,
      addFrame,
      changeValue,
      frameConfigs,
      colorConfigs,
      textConfigs,
      iconConfigs,
      imageConfigs
    };
  },
  components: { FormEvery }
});
</script>

<style lang="scss">
.CustomAnimation {
  .ant-drawer-body {
    padding: 0;
    height: calc(100% - 55px);
    .ant-collapse-header{
      .ant-collapse-extra{
        position: relative;
        z-index: 9;
      }
    }
    .InnerScrollCollapse {
      height: calc(100% - 60px);
      background: #232630;
      .ant-collapse-item-active {
        // TODO: 出现多个时，外部滚动条还是会出现
        height: 100%;

        .ant-collapse-content-active {
          width: 100%;
          height: calc(100% - 22px);
          overflow-y: auto;
        }
      }
    }
  }
  .ant-popover-inner{
    background: #3d404d;
    .ant-popover-message-title{
      color: #fff;
      white-space: nowrap;
    }
    .ant-popover-buttons{
      display: flex;
      justify-content: flex-end;
    }
  }
  .ant-switch-checked{
    background: #283851;
    &:after{
      background: #3a89fe;
    }
  }
  .ant-input-number,.ant-select:not(.ant-select-customize-input) .ant-select-selector{
    background: #181a24;
    border: 1px solid #393b4a!important;
    color: #b4b7c1;
  }
  .ant-select-arrow{
    color: #b4b7c1;
  }
 .ant-popover-placement-bottom,.ant-popover-placement-bottomLeft,.ant-popover-placement-bottomRight{
    .ant-popover-content{
      .ant-popover-arrow{
        border-top-color:#3d404d;
        border-left-color:#3d404d
      }
    }
  }
  .ant-popover-placement-top,.ant-popover-placement-topLeft,.ant-popover-placement-topRight{
    .ant-popover-content{
      .ant-popover-arrow{
        border-right-color:#3d404d;
        border-bottom-color:#3d404d
      }
    }
  }
}

</style>
