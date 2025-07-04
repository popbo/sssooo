<template>
  <a-collapse
    v-model:activeKey="activeKey"
    expand-icon-position="left"
    class="PenProps PenProps_box"
    :bordered="false"
  >
    <a-collapse-panel
      key="对齐"
      :header="`${t('对齐')}(${t('参照框')})`"
      class="pensExteriorAlign"
    >
      <div class="flex aligns">
        <i
          v-for="align in nodesAlign"
          :title="t(align.desc)"
          :class="'t-align-' + align.value"
          class="t-icon"
          @click="onNodesAlign(align.value)"
        ></i>
        <i
          class="t-icon t-horizontal-between"
          :title="t('等距分布、左右对齐')"
          @click="onSpaceBetween"
        ></i>
        <i
          class="t-icon t-vertical-between"
          :title="t('等距分布、上下对齐')"
          @click="onSpaceBetweenColumn"
        >
        </i>
      </div>
    </a-collapse-panel>
    <a-collapse-panel
      key="对齐First"
      :header="`${t('对齐')}(${t('参照第一个选中节点')})`"
      class="pensExteriorAlign"
    >
      <div class="flex aligns">
        <i
          v-for="align in nodesAlign"
          :title="t(align.desc)"
          :class="'t-align-' + align.value"
          class="t-icon"
          @click="onNodesAlignByFirst(align.value)"
        ></i>
        <i class="t-icon t-yiyang" :title="t('相同大小')" @click="beSame"></i>
        <i class="t-icon t-geshishua" :title="t('格式刷')" @click="formatPainter"></i>
      </div>
    </a-collapse-panel>
    <a-collapse-panel key="样式" :header="t('样式')">
      <FormEvery
        :configs="styleConfigs"
        :obj="style"
        @change-value="changeStyle"
      />
    </a-collapse-panel>
    <a-collapse-panel key="文字" :header="t('文字')">
      <FormEvery
        :configs="fontConfigs"
        :obj="style"
        @change-value="changeStyle"
      />
    </a-collapse-panel>
  </a-collapse>
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
  toRaw,
} from 'vue';
import FormEvery from '../common/FormEvery.vue';
import { normalFonts, whiteSpaces } from '../defaults';
import { FormItemType } from '../utils';

declare const meta2d: Meta2d;

export default defineComponent({
  name: 'PensExterior',
  components: {
    FormEvery,
  },
  setup() {
    const activeKey = ref(['对齐', '对齐First', '样式', '文字']);

    const nodesAlign = [
      {
        value: 'left',
        desc: '左对齐',
      },
      {
        value: 'right',
        desc: '右对齐',
      },
      {
        value: 'top',
        desc: '顶部对齐',
      },
      {
        value: 'bottom',
        desc: '底部对齐',
      },
      {
        value: 'center',
        desc: '垂直居中',
      },
      {
        value: 'middle',
        desc: '水平居中',
      },
    ];

    const activePens: Ref<Pen[]> = inject('activePens');

    function onNodesAlign(align: string) {
      meta2d.alignNodes(align, meta2d.store.active);
    }

    function onNodesAlignByFirst(align: string) {
      meta2d.alignNodesByFirst(align, meta2d.store.active);
    }

    function onSpaceBetween() {
      meta2d.spaceBetween(meta2d.store.active);
    }

    function onSpaceBetweenColumn() {
      meta2d.spaceBetweenColumn(meta2d.store.active);
    }

    const styleConfigs = computed(() => {
      const configs: FormItemType[] = [
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
              value: 0,
            },
            {
              label: `<svg xmlns="http://www.w3.org/2000/svg" version="1.1" style="height: 20px;">
                  <g fill="none" stroke="white" stroke-width="1">
                    <path stroke-dasharray="5 5" d="M0 9 l85 0" />
                  </g>
                </svg>`,
              value: 1,
            },
            {
              label: `<svg xmlns="http://www.w3.org/2000/svg" version="1.1" style="height: 20px;">
                  <g fill="none" stroke="white" stroke-width="1">
                    <path stroke-dasharray="10 10" d="M0 9 l85 0" />
                  </g>
                </svg>`,
              value: 2,
            },
            {
              label: `<svg xmlns="http://www.w3.org/2000/svg" version="1.1" style="height: 20px;">
                  <g fill="none" stroke="white" stroke-width="1">
                    <path stroke-dasharray="10 10 2 10" d="M0 9 l85 0" />
                  </g>
                </svg>`,
              value: 3,
            },
          ],
        },
        {
          key: 'lineWidth',
          placeholder: 'px',
          type: 'number',
          name: `${t('线条宽度')}`,
        },
        {
          key: 'lineCap',
          type: 'select',
          name: `${t('末端样式')}`,
          options: [
            {
              label: `${t('默认')}`,
              value: 'butt',
            },
            {
              label: `${t('圆形')}`,
              value: 'round',
            },
            {
              label: `${t('方形')}`,
              value: 'square',
            },
          ],
        },
        {
          key: 'lineJoin',
          type: 'select',
          name: `${t('连接样式')}`,
          options: [
            {
              label: `${t('斜角')}`,
              value: 'bevel',
            },
            {
              label: `${t('圆形')}`,
              value: 'round',
            },
            {
              label: `${t('默认')}`,
              value: 'miter',
            },
          ],
        },
        {
          key: 'strokeType',
          type: 'select',
          name: `${t('线条渐变')}`,
          options: [
            {
              label: `${t('无')}`,
              value: 0,
            },
            {
              label: `${t('线性渐变')}`,
              value: 1,
            },
          ],
        },
      ];

      if (!style.strokeType) {
        configs.push({
          key: 'color',
          type: 'color',
          name: `${t('颜色')}`,
        });
      } else {
        configs.push(
          {
            key: 'lineGradientFromColor',
            type: 'color',
            name: `${t('开始颜色')}`,
          },
          {
            key: 'lineGradientToColor',
            type: 'color',
            name: `${t('结束颜色')}`,
          },
          {
            key: 'lineGradientAngle',
            type: 'number',
            name: `${t('渐变角度')}`,
          }
        );
      }

      configs.push(
        {
          key: 'globalAlpha',
          type: 'number',
          placeholder: '(0-1)',
          name: `${t('透明度')}`,
          max: 1,
          min: 0,
          step: 0.1,
        },
        {
          key: 'bkType',
          type: 'select',
          name: `${t('背景')}`,
          options: [
            {
              label: `${t('纯色背景')}`,
              value: 0,
            },
            {
              label: `${t('线性渐变')}`,
              value: 1,
            },
            {
              label: `${t('径向渐变')}`,
              value: 2,
            },
          ],
        }
      );

      if (!style.bkType) {
        configs.push({
          key: 'background',
          name: `${t('背景颜色')}`,
          type: 'color',
        });
      } else {
        configs.push(
          {
            key: 'gradientFromColor',
            name: `${t('开始颜色')}`,
            type: 'color',
          },
          {
            key: 'gradientToColor',
            name: `${t('结束颜色')}`,
            type: 'color',
          }
        );
      }

      if (style.bkType === Gradient.Linear) {
        configs.push({
          key: 'gradientAngle',
          name: `${t('渐变角度')}`,
          type: 'number',
        });
      } else if (style.bkType === Gradient.Radial) {
        configs.push({
          key: 'gradientRadius',
          name: `${t('渐变半径')}`,
          type: 'number',
          min: 0,
          max: 1,
          step: 0.01,
        });
      }

      return configs;
    });

    // 与 font 公用一个
    const style: any = reactive({
      dash: 0,
      lineWidth: 1,
      lineCap: 'butt',
      lineJoin: 'miter',
      globalAlpha: 1,
      bkType: 0,
      fontFamily:
        '"Hiragino Sans GB", "Microsoft YaHei", "Helvetica Neue", "Helvetica, Aria"',
      fontSize: 12,
      fontStyle: 'normal',
      fontWeight: 'normal',
      textAlign: 'center',
      textBaseline: 'middle',
    });

    const fontConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          key: 'fontFamily',
          name: `${t('字体名')}`,
          type: 'autoComplete',
          options: normalFonts.map((font) => ({
            label: font,
            value: font,
          })),
        },
        {
          key: 'fontSize',
          type: 'number',
          name: `${t('字体大小')}`,
        },
        {
          key: 'textColor',
          type: 'color',
          name: `${t('文字颜色')}`,
        },
        {
          key: 'hoverTextColor',
          name: `${t('浮动文字颜色')}`,
          type: 'color',
        },
        {
          key: 'activeTextColor',
          name: `${t('选中文字颜色')}`,
          type: 'color',
        },
        {
          key: 'textBackground',
          name: `${t('文字背景')}`,
          type: 'color',
        },
        {
          key: 'fontStyle',
          name: `${t('倾斜')}`,
          type: 'select',
          options: [
            {
              label: `${t('正常')}`,
              value: 'normal',
            },
            {
              label: `${t('倾斜')}`,
              value: 'italic',
            },
          ],
        },
        {
          key: 'fontWeight',
          name: t('加粗'),
          type: 'select',
          options: [
            {
              label: `${t('正常')}`,
              value: 'normal',
            },
            {
              label: `${t('加粗')}`,
              value: 'bold',
            },
          ],
        },
        {
          key: 'textAlign',
          name: `${t('水平对齐')}`,
          type: 'select',
          options: [
            {
              label: `${t('左对齐')}`,
              value: 'left',
            },
            {
              label: `${t('居中')}`,
              value: 'center',
            },
            {
              label: `${t('右对齐')}`,
              value: 'right',
            },
          ],
        },
        {
          key: 'textBaseline',
          name: `${t('垂直对齐')}`,
          type: 'select',
          options: [
            {
              label: `${t('顶部对齐')}`,
              value: 'top',
            },
            {
              label: `${t('居中')}`,
              value: 'middle',
            },
            {
              label: `${t('底部对齐')}`,
              value: 'bottom',
            },
          ],
        },
        {
          key: 'lineHeight',
          name: `${t('行高')}`,
          type: 'number',
          min: 0,
        },
        {
          key: 'whiteSpace',
          name: `${t('换行')}`,
          type: 'select',
          options: whiteSpaces as any,
        },
        {
          key: 'textLeft',
          type: 'number',
          name: `${t('水平偏移')}`,
        },
        {
          key: 'textTop',
          type: 'number',
          name: `${t('垂直偏移')}`,
        },
      ];

      return configs;
    });

    async function changeStyle({
      value,
      keys,
    }: {
      value: any;
      keys: string[];
    }) {
      const valueObj: any = {};
      if (keys[0] === 'dash') {
        const { lineDashObj } = await import('../defaults');
        for (const pen of activePens.value) {
          // pen.lineDash = lineDashObj[style.dash];
          valueObj.dash = style.dash;
          valueObj.lineDash = lineDashObj[style.dash]; //pen.lineDash;
        }
      }
      for (const pen of activePens.value) {
        // pen[keys[0]] = style[keys[0]];
        valueObj[keys[0]] = style[keys[0]];
        // if (
        //   pen.name === 'combine' &&
        //   pen.showChild === undefined &&
        //   canChangeTogether.includes(keys[0])
        // ) {
        //   setValueContainChildren(pen.id, valueObj);
        //   meta2d.render();
        //   continue;
        // }
        meta2d.setValue(
          {
            id: pen.id,
            ...valueObj,
          },
          {
            render: true,
            history:true
          }
        );
      }
      meta2d.render();
    }

    function beSame() {
      meta2d.beSameByFirst(meta2d.store.active);
    }

    function formatPainter() {
      meta2d.formatPainterByFirst(meta2d.store.active);
    }

    return {
      activeKey,
      nodesAlign,
      onNodesAlign,
      onSpaceBetween,
      styleConfigs,
      style,
      changeStyle,
      fontConfigs,
      onNodesAlignByFirst,
      beSame,
      formatPainter,
      onSpaceBetweenColumn,
    };
  },
});

// 可以一起修改的属性
export const canChangeTogether = [
  'dash',
  'lineWidth',
  'lineCap',
  'lineJoin',
  'strokeType',
  'color',
  'lineGradientFromColor',
  'lineGradientToColor',
  'lineGradientAngle',
  'globalAlpha',
  'bkType',
  'background',
  'gradientFromColor',
  'gradientToColor',
  'gradientAngle',
  'gradientRadius',
  'fontFamily',
  'fontSize',
  'textColor',
  'hoverTextColor',
  'activeTextColor',
  'textBackground',
  'fontStyle',
  'fontWeight',
  'textAlign',
  'textBaseline',
  'lineHeight',
  'whiteSpace',
  'textLeft',
  'textTop',
  'flipX',
  'flipY',
];

export function setValueContainChildren(id: string, valueObj: Pen) {
  const pen = meta2d.store.pens[id];
  const value = { ...valueObj };
  if (valueObj.hasOwnProperty('flipX')) {
    value.flipX = !pen.flipX;
  }
  if (valueObj.hasOwnProperty('flipY')) {
    value.flipY = !pen.flipY;
  }
  meta2d.setValue(
    {
      ...value,
      id,
    },
    {
      render: false,
    }
  );
  const children = pen.children;
  children?.forEach((childId) => {
    setValueContainChildren(childId, valueObj);
  });
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';
.PenProps{
  :deep(.ant-collapse-item){
    .ant-collapse-content{
      color: #b4b7c1;
    }
  }
}
.pensExteriorAlign {
  :deep(.ant-collapse-content-box) {
    padding: 5px;
  }
  .aligns {
    height: 20px;
    align-items: center;
    justify-content: space-around;

    i {
      cursor: pointer;
      &:hover {
        color: $color-primary;
      }
    }
  }
}
</style>
