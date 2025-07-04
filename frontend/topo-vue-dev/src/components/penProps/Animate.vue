<template>
  <a-collapse
    v-model:activeKey="activeKey"
    expand-icon-position="left"
    class="PenProps"
    :bordered="false"
  >
    <a-collapse-panel
      v-if="pen.name !== 'video'"
      :header="t('动画')"
      key="动画"
    >
      <!-- 线条 -->
      <FormEvery
        v-if="pen.type === 1"
        :configs="lineAnimateConfigs"
        :obj="pen"
        @change-value="changeValue"
      />
      <!-- 节点 -->
      <template v-else>
        <FormEvery
          :configs="nodeAnimateConfigs"
          :obj="pen"
          @change-value="changeValue"
        />
        <a-form-item
          v-if="pen.animateType === 'custom'"
          class="FormItem"
          :labelCol="{ span: 10 }"
          :wrapperCol="{ span: 14 }"
          labelAlign="left"
          :colon="false"
        >
          <template #label> </template>
          <a
            @click="
              () => {
                showModalAnimate = true;
              }
            "
            >{{ t('编辑') }}</a
          >
        </a-form-item>
      </template>
      <!-- 共有的 -->
      <FormEvery
        :configs="animateConfigs"
        :obj="pen"
        @change-value="changeValue"
      />
      <!-- 按钮 -->
      <div class="buttons">
        <a-button
          class="animateButton mr12"
          type="primary"
          @click="startAnimate"
        >
          <CaretRightOutlined />
          {{ t('播放') }}
        </a-button>
        <a-button
          class="animateButton mr12"
          type="primary"
          @click="pauseAnimate"
        >
          <PauseOutlined />
          {{ t('暂停') }}
        </a-button>
        <a-button class="animateButton" type="primary" @click="stopAnimate">
          <CloseSquareOutlined />
          {{ t('停止') }}
        </a-button>
      </div>
    </a-collapse-panel>
    <a-collapse-panel
      :header="t('视频')"
      key="视频"
      v-if="pen.name === 'video'"
    >
      <FormEvery
        :configs="videoConfigs"
        :obj="pen"
        @change-value="changeValue"
      />
      <!-- 按钮 -->
      <div class="buttons">
        <a-button class="animateButton mr12" type="primary" @click="startVideo">
          <CaretRightOutlined />
          {{ t('播放') }}
        </a-button>
        <a-button class="animateButton mr12" type="primary" @click="pauseVideo">
          <PauseOutlined />
          {{ t('暂停') }}
        </a-button>
        <a-button class="animateButton" type="primary" @click="stopVideo">
          <CloseSquareOutlined />
          {{ t('停止') }}
        </a-button>
      </div>
    </a-collapse-panel>
    <!-- 网页 URL -->
    <a-collapse-panel
      :header="t('网页')"
      key="网页"
      v-else-if="pen.name === 'iframe'"
    >
      <FormEvery
        :configs="iframeConfigs"
        :obj="pen"
        @change-value="changeValue"
      />
    </a-collapse-panel>
    <!-- 鼠标提示 -->
    <a-collapse-panel :header="t('鼠标提示')" key="鼠标提示">
      <FormEvery
        :configs="tipsConfigs"
        :obj="pen"
        @change-value="changeValue"
      />
    </a-collapse-panel>
  </a-collapse>
  <CustomAnimation v-model:visible="showModalAnimate" />
</template>

<script lang="ts">
import {
  computed,
  defineComponent,
  inject,
  onMounted,
  Ref,
  ref,
  toRaw,
  watch,
} from 'vue';
import { t } from '@/i18n/i18n';
import FormEvery from '../common/FormEvery.vue';
import { deepClone, IValue, Pen, Meta2d } from '@topometa2d/core';
import {
  CaretRightOutlined,
  PauseOutlined,
  CloseSquareOutlined,
} from '@ant-design/icons-vue';
import CustomAnimation from './CustomAnimation.vue';
import { FormItemType } from '../utils';

declare const meta2d: Meta2d;

export default defineComponent({
  name: 'PenAnimate',
  components: {
    FormEvery,
    CaretRightOutlined,
    PauseOutlined,
    CloseSquareOutlined,
    CustomAnimation,
  },
  setup(props) {
    const activeKey = ref(['动画', '视频', '网页', '鼠标提示']);

    // 此处的 pen 是响应式的
    const pen: Ref<Pen | any> = inject('activePen');
    // const pen: Ref<Pen> = inject('activePen');

    const videoConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          key: 'audio',
          type: 'text',
          name: `${t('音频URL')}`,
        },
        {
          key: 'video',
          type: 'text',
          name: `${t('视频URL')}`,
        },
        {
          key: 'autoPlay',
          type: 'switch',
          name: `${t('自动播放')}`,
        },
        {
          key: 'nextAnimate',
          type: 'text',
          name: `${t('下个播放')}`,
          placeholder: 'tag',
        },
        {
          key: 'playLoop',
          type: 'switch',
          name: `${t('循环播放')}`,
        },
      ];

      return configs;
    });

    const iframeConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          key: 'iframe',
          type: 'text',
          name: `${t('网页URL')}`,
        },
      ];

      return configs;
    });

    const tipsConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          key: 'title',
          type: 'code',
          name: `${t('Markdown')}`,
          title: 'Markdown',
          language: 'markdown',
        },
        {
          key: 'titleFnJs',
          type: 'code',
          name: `${t('Mark 函数')}`,
        },
      ];

      return configs;
    });

    async function changeValue({
      value,
      keys,
    }: {
      value?: any;
      keys?: string[];
    }) {
      const { lineDashObj, normalAnimate } = await import('../defaults');
      const valueObj: IValue = {
        id: pen.value.id,
        [keys[0]]: pen.value[keys[0]],
      };
      if (keys[0] === 'animateDash') {
        pen.value.animateLineDash = lineDashObj[value];
        Object.assign(valueObj, {
          animateLineDash: pen.value.animateLineDash,
        });
      }
      if (keys[0] === 'animateType') {
        stopAnimate(); // 配置前停下动画
        // 节点动画类型
        // 空动画，清空 frames
        !value && (pen.value.frames = null);
        const frames: Pen[] = deepClone(normalAnimate[value]);
        let showDuration = 0;
        if (frames) {
          pen.value.frames = frames;
          showDuration = meta2d.calcAnimateDuration(toRaw(pen.value));
          pen.value.showDuration = showDuration;
        }
        Object.assign(valueObj, {
          frames: pen.value.frames,
          showDuration,
        });
      }
      if (keys[0] === 'titleFnJs') {
        valueObj.titleFn = null;
      }
      meta2d.setValue(valueObj);
    }

    const nodeAnimateConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          key: 'showDuration', // TODO: 业务属性，仅仅做展示
          type: 'label',
          name: `${t('时长')}`,
        },
        {
          key: 'animateType',
          type: 'select',
          name: `${t('动画效果')}`,
          options: [
            {
              label: `${t('无')}`,
              value: '',
            },
            {
              label: `${t('上下跳动')}`,
              value: 'upDown',
            },
            {
              label: `${t('左右跳动')}`,
              value: 'leftRight',
            },
            {
              label: `${t('心跳')}`,
              value: 'heart',
            },
            {
              label: `${t('成功')}`,
              value: 'success',
            },
            {
              label: `${t('警告')}`,
              value: 'warning',
            },
            {
              label: `${t('错误')}`,
              value: 'error',
            },
            {
              label: `${t('炫耀')}`,
              value: 'show',
            },
            {
              label: `${t('旋转')}`,
              value: 'rotate',
            },
            {
              label: `${t('自定义')}`,
              value: 'custom',
            },
          ],
        },
      ];

      return configs;
    });

    const lineAnimateConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          key: 'lineAnimateType',
          type: 'select',
          name: `${t('动画效果')}`,
          placeholder: `${t('水流')}`,
          options: [
            {
              label: `${t('水流')}`,
              value: 0,
            },
            {
              label: `${t('水珠流动')}`,
              value: 1,
            },
            {
              label: `${t('圆点')}`,
              value: 2,
            },
          ],
        },
      ];
      if (pen.value.lineAnimateType === 1) {
        configs.push({
          key: 'animateDash',
          type: 'select',
          name: `${t('动画线条')}`,
          options: [
            {
              // 线条动画，虚线为 0 ，即 undefined ，默认
              label: `<svg xmlns="http://www.w3.org/2000/svg" version="1.1" style="height: 20px;">
                  <g fill="none" stroke="white" stroke-width="1">
                    <path stroke-dasharray="5 5" d="M0 9 l85 0" />
                  </g>
                </svg>`,
              value: 0,
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
        });
      } else if (pen.value.lineAnimateType === 2) {
        configs.push({
          key: 'animateDotSize',
          type: 'number',
          name: `${t('圆点大小')}`,
          min: 6,
          placeholder: `${t('最小值')} 6`,
        });
      }

      configs.push(
        {
          key: 'animateColor',
          type: 'color',
          name: `${t('动画颜色')}`,
        },
        {
          key: 'animateSpan',
          type: 'slider',
          min: 1,
          max: 5,
          name: `${t('动画速度')}`,
        },
        {
          key: 'animateReverse',
          type: 'switch',
          name: `${t('反向流动')}`,
        }
      );

      return configs;
    });

    const animateConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          key: 'animateCycle',
          type: 'number',
          placeholder: `${t('默认无限循环')}`,
          name: `${t('循环次数')}`,
          min: 0,
        },
        {
          key: 'nextAnimate',
          type: 'text',
          placeholder: 'tag',
          name: `${t('下个动画')}`,
        },
        {
          key: 'autoPlay',
          type: 'switch',
          name: `${t('自动播放')}`,
        },
        {
          key: 'keepAnimateState',
          type: 'switch',
          name: `${t('保持动画状态')}`,
        },
        {
          // undefined 认为是 true 所以使用下拉框
          key: 'linear',
          type: 'select',
          name: `${t('线性播放')}`,
          placeholder: `${t('是')}`,
          options: [
            {
              label: `${t('是')}`,
              value: true,
            },
            {
              label: `${t('否')}`,
              value: false,
            },
          ],
        },
      ];

      return configs;
    });

    function startAnimate() {
      meta2d.startAnimate(pen.value.id);
      meta2d.store.data['animationState']='default'
    }

    function pauseAnimate() {
      meta2d.pauseAnimate(pen.value.id);
    }

    function stopAnimate() {
      meta2d.stopAnimate(pen.value.id);
    }

    function startVideo() {
      meta2d.startVideo(pen.value.id);
    }

    function pauseVideo() {
      meta2d.pauseVideo(pen.value.id);
    }

    function stopVideo() {
      meta2d.stopVideo(pen.value.id);
    }

    // 动画在播放中
    // const isPlaying = computed(() => {
    //   if (
    //     pen.value.calculative.start > 0 &&
    //     !(pen.value.calculative.pause > 0)
    //   ) {
    //     return true;
    //   }
    //   return false;
    // });

    const showModalAnimate = ref(false);

    return {
      activeKey,
      pen,
      t,
      videoConfigs,
      changeValue,
      iframeConfigs,
      tipsConfigs,
      lineAnimateConfigs,
      animateConfigs,
      startAnimate,
      pauseAnimate,
      stopAnimate,
      startVideo,
      pauseVideo,
      stopVideo,
      nodeAnimateConfigs,
      showModalAnimate,
    };
  },
});
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.buttons {
  width: 100%;
  display: flex;

  .animateButton {
    flex: 1;
    padding: 0;
  }
}

.FormItem {
  font-size: 10px;
  margin-bottom: 0;
  margin: 4px 0;
  a {
    margin-left: 8px;
  }
}
</style>
