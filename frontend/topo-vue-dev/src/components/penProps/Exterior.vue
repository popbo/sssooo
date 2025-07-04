<template>
  <a-collapse
    v-model:activeKey="activeKey"
    expand-icon-position="left"
    class="PenProps PenProps_box"
    :bordered="false"
  > 
    <a-collapse-panel
      key="控件"
      :header="t('控件')"
      v-if="pen.type !== 1&&(pen.name==='AxleCounterDefault'||pen.name==='AxleCounterDefault2')"
    >
      <FormEvery
        :configs="controlConfigs"
        :obj="pen"
        @change-value="changeValue"
      />
    </a-collapse-panel>
    <a-collapse-panel
      key="位置与大小"
      :header="t('位置和大小')"
      v-if="pen.type !== 1"
    >
      <FormEvery
        :configs="sizeConfigs"
        :obj="pen"
        @change-value="changeValue"
      />
      <!-- <FormEvery
        v-if="pen.parentId"
        :configs="indeSeleConfigs"
        :obj="pen"
        @change-value="changeValue"
      /> -->
    </a-collapse-panel>
    <a-collapse-panel key="样式" :header="t('样式')">
      <FormEvery
        :configs="styleConfigs"
        :obj="pen"
        @change-value="changeValue"
      />
      <!-- 控制点 -->
      <template v-if="pen.type">
        <FormEvery
          v-for="(controlPoint, i) in controlPoints"
          :key="i"
          :configs="controlPointConfigs(i)"
          :obj="controlPoint"
          @change-value="changeControlPoints($event, i)"
        />
      </template>
    </a-collapse-panel>
    <a-collapse-panel key="位置" :header="t('位置')" v-if="pen.type">
      <!-- 起点坐标 终点坐标 -->
      <FormEvery
        :configs="pointsConfigs"
        :obj="points"
        @change-value="changePoints"
      />
      <FormEvery
        :configs="positionConfigs"
        :obj="pen"
        @change-value="changeValue"
      />
    </a-collapse-panel>
    <a-collapse-panel key="文字" :header="t('文字')">
      <FormEvery
        :configs="textConfigs"
        :obj="pen"
        @change-value="changeValue"
      />
    </a-collapse-panel>
    <a-collapse-panel key="图片">
      <FormEvery :configs="picConfigs" :obj="pen" @change-value="changeValue" />
      <template #header>
        <div class="flex middle collapseTitle">
          {{ t('图片') }}
          <a-tooltip>
            <template #title>
              {{ t('图片、字体图标同时存在时，图片优先') }}</template
            >
            <i class="t-icon t-help-circle"></i>
          </a-tooltip>
        </div>
      </template>
    </a-collapse-panel>
    <a-collapse-panel key="字体图标">
      <FormEvery
        :configs="iconConfigs"
        :obj="pen"
        @change-value="changeValue"
      />
      <template #header>
        <div class="flex middle collapseTitle">
          {{ t('字体图标') }}
          <a-tooltip>
            <template #title>
              {{ t('图片、字体图标同时存在时，图片优先') }}</template
            >
            <i class="t-icon t-help-circle"></i>
          </a-tooltip>
        </div>
      </template>
    </a-collapse-panel>
    <a-collapse-panel key="禁止" :header="t('禁止')">
      <FormEvery
        :configs="disableConfigs"
        :obj="pen"
        @change-value="changeValue"
      />
    </a-collapse-panel>
  </a-collapse>
</template>

<script lang="ts">
import { t } from '@/i18n/i18n';
import {
  Gradient,
  LockState,
  Meta2d,
  Point,
  Pen,
  PenType,
} from '@topometa2d/core';
import {
  computed,
  defineComponent,
  inject,
  reactive,
  Ref,
  ref,
  toRaw,
  watchEffect,
  watch,
  nextTick
} from 'vue';
import FormEvery from '../common/FormEvery.vue';
import { addShowChild, normalFonts, whiteSpaces } from '../defaults';
import {
  canChangeTogether,
  setValueContainChildren,
} from '../pensProps/Exterior.vue';
import { FormItemType, isGif } from '../utils';

declare const meta2d: Meta2d;

export default defineComponent({
  name: 'PenExterior',
  components: {
    FormEvery,
  },
  setup() {
    const activeKey = ref([
      '控件',
      '位置与大小',
      '样式',
      '位置',
      '文字',
      '图片',
      '字体图标',
    ]);
    // 此处的 pen 是响应式的
    const pen: Ref<Pen> = inject('activePen');
    const controlConfigs=computed<FormItemType[]>(() => {
      const configs: FormItemType[] = []
      if(pen.value.name==='AxleCounterDefault'||pen.value.name==='AxleCounterDefault2'){
        configs.push({
          key: 'advancedSet',
          type: 'switch',
          name: `${t('高级设置')}`,
        })
      }
      if(pen.value['advancedSet']){
        configs.push({
          key: 'zoneType',
          type: 'select',
          name: `${t('类型')}`,
          options:[{
            label:'固定',
            value:'static'
          },{
            label:'定位',
            value:'fixed'
          },{
            label:'反位',
            value:'antiSite'
          }]
        })
        const daochaPens=meta2d.store.data.pens.filter(x=>x.name==='daoCha'||x.name==='daoChaRight'||x.name==='daoChaDwon'||x.name==='daoChaRightDwon')
        if(daochaPens.length>0){
          const daochaOption=daochaPens.map(item=>{
            return {
              label:item['statusObj'].Dev_name,
              value:item['statusObj'].Dev_name,
              id:item.id
            }
          })
          configs.push({
            key: 'bindSwitch',
            type: 'select',
            name: `${t('绑定道岔')}`,
            options: daochaOption
          })
        }
        
      }
      return configs
    })
    //位置和大小
    const sizeConfigs = computed<FormItemType[]>(() => {
      const configs: FormItemType[] = [
        /*{
          key: 'x',
          placeholder: 'px',
          type: 'number',
          name: 'X',
          min:0
        },
        {
          key: 'y',
          placeholder: 'px',
          type: 'number',
          name: 'Y',
          min:0
        },
        {
          key: 'width',
          placeholder: 'px',
          type: 'number',
          name: `${t('宽')}`,
          min: 1,
        },
        {
          key: 'height',
          placeholder: 'px',
          type: 'number',
          name: `${t('高')}`,
          min: 1,
        },*/
        {
          key: 'ratio',
          type: 'switch',
          name: `${t('锁定宽高比')}`,
        },
        {
          key: 'borderRadius',
          type: 'number',
          name: `${t('圆角')}`,
          min: 0,
          placeholder: `${t('< 1 比例')}`,
        },
        {
          key: 'rotate',
          type: 'number',
          placeholder: '°',
          name: `${t('旋转')}`,
        },
        {
          key: 'paddingLeft',
          type: 'number',
          name: `${t('内边距')} - ${t('左')}`,
        },
        {
          key: 'paddingRight',
          type: 'number',
          name: `${t('内边距')} - ${t('右')}`,
        },
        {
          key: 'paddingTop',
          type: 'number',
          name: `${t('内边距')} - ${t('上')}`,
        },
        {
          key: 'paddingBottom',
          type: 'number',
          name: `${t('内边距')} - ${t('下')}`,
        },
        {
          key: 'progress',
          type: 'number',
          name: `${t('进度')}`,
          step: 0.1,
          max: 1,
          min: 0,
        },
        {
          key: 'progressColor',
          type: 'color',
          name: `${t('进度颜色')}`,
        },
        {
          key: 'verticalProgress',
          type: 'switch',
          name: `${t('垂直进度')}`,
        },
        {
          key: 'flipX',
          type: 'switch',
          name: `${t('水平翻转')}`,
        },
        {
          key: 'flipY',
          type: 'switch',
          name: `${t('垂直翻转')}`,
        },
        {
          key: 'input',
          type: 'switch',
          name: `${t('输入框')}`,
        },
      ];

      addShowChild(pen.value, configs);
      return configs;
    });
    // 独立选中 config
    const indeSeleConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          // TODO: 核心库无
          key: 'stand',
          type: 'switch',
          name: `${t('独立选中')}`,
        },
      ];
      return configs;
    });
    
    // 改变了某个值
    async function changeValue({
      value,
      keys,
    }: {
      value: any;
      keys: string[];
    }) {
      console.log('原始===>',value, keys);

      if (keys[0] === 'lineName') {
        const canvasPen = meta2d.find(pen.value.id)[0];
        meta2d.updateLineType(canvasPen, value);
        return;
      }
      if (!value && ['width', 'height'].includes(keys[0])) {
        value = 1;
      }
      const valueObj = {
        id: pen.value.id,
        [keys[0]]: value,
      };
      //宽高比锁定
      if ((pen.value as any).ratio) {
        const rect = meta2d.findOne(pen.value.id);
        
        if (keys[0] === 'width') {
          Object.assign(valueObj, {
            height: (value / rect.width) * rect.height,
          });
        } else if (keys[0] === 'height') {
          Object.assign(valueObj, {
            width: (value / rect.height) * rect.width,
          });
        }
      }
      if (keys[0] === 'dash') {
        const { lineDashObj } = await import('../defaults');
        pen.value.lineDash = lineDashObj[(pen.value as any).dash];
        Object.assign(valueObj, {
          lineDash: pen.value.lineDash,
        });
      }
      // 设置 icon 的同时，需要设置 iconFamily
      if (keys[0] === 'icon') {
        pen.value.iconFamily = value.iconFamily;
        pen.value.icon = value.icon;
        Object.assign(valueObj, {
          iconFamily: pen.value.iconFamily,
          icon: pen.value.icon,
        });
      }
      // 设置 image 可能是一个 Gif
      if (keys[0] === 'image') {
        if (isGif(pen.value.image) && pen.value.name === 'image') {
          pen.value.name = 'gif';
          valueObj.name = 'gif';
          valueObj.lockedOnCombine = LockState.None;
        } else if (pen.value.name === 'gif') {
          pen.value.name = 'image';
          valueObj.name = 'image';
          valueObj.lockedOnCombine = undefined;
        }
      }
      if(keys[0]==='advancedSet'){
        if(!value){
          Object.assign(valueObj, {
            bindSwitch: '',
            zoneType:''
          });
        }
      }
      // if (
      //   pen.value.name === 'combine' &&
      //   pen.value.showChild === undefined &&
      //   canChangeTogether.includes(keys[0])
      // ) {
      //   // 是组合节点，且并非是状态操作
      //   // 修改它下面的子节点对应属性
      //   setValueContainChildren(pen.value.id, valueObj);
      //   meta2d.render();
      //   return;
      // }
      meta2d.setValue(toRaw(valueObj), { history: true });
      if (['width', 'height', 'x', 'y', 'ratio'].includes(keys[0])) {
        const rect = meta2d.getPenRect(pen.value);
        Object.assign(pen.value, rect);
      }
    }
    // 样式 configs
    const styleConfigs = computed<FormItemType[]>(() => {
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
      ];

      if (pen.value.type) {
        configs.push({
          key: 'lineName',
          type: 'select',
          name: `${t('连线类型')}`,
          options: [
            {
              label: `<svg xmlns="http://www.w3.org/2000/svg" version="1.1"  style="height: 20px;">
                  <g fill="none" stroke="white" stroke-width="1">
                    <path d="M0 9 a100,50 0 0,1 85,0" />
                  </g>
                </svg>`,
              value: 'curve',
            },
            {
              label: `<svg xmlns="http://www.w3.org/2000/svg" version="1.1"  style="height: 20px;">
                  <g fill="none" stroke="white" stroke-width="1">
                    <path d="M0 4 l40 0 l0 12 l40 0" />
                  </g>
                </svg>`,
              value: 'polyline',
            },
            {
              label: `<svg xmlns="http://www.w3.org/2000/svg" version="1.1"  style="height: 20px;">
                  <g fill="none" stroke="white" stroke-width="1">
                    <path d="M0 9 l85 0" />
                  </g>
                </svg>`,
              value: 'line',
            },
          ],
        });

        if (pen.value.lineName === 'polyline') {
          configs.push({
            // undefined 认为是 true 所以使用下拉框
            key: 'autoPolyline',
            placeholder: t('是'),
            name: `${t('自动计算')}`,
            type: 'select',
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
          });
        }
      }

      configs.push(
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
        }
      );

      if (!pen.value.strokeType) {
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
          key: 'hoverColor',
          type: 'color',
          name: `${t('浮动颜色')}`,
        },
        {
          key: 'activeColor',
          type: 'color',
          name: `${t('选中颜色')}`,
        },
        {
          key: 'lineWidth',
          type: 'number',
          name: `${t('线条宽度')}`,
          min:1
        },
      );
      if (pen.value.type) {
        configs.push(
          {
            key: 'borderColor',
            type: 'color',
            name: `${t('边框颜色')}`,
          },
          {
            key: 'borderWidth',
            type: 'number',
            name: `${t('边框宽度')}`,
          }
        );
      }
      configs.push(
        {
          key: 'bkType',
          name: `${t('背景')}`,
          type: 'select',
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
      )
      if (!pen.value.bkType) {
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

      if (pen.value.bkType === Gradient.Linear) {
        configs.push({
          key: 'gradientAngle',
          name: `${t('渐变角度')}`,
          type: 'number',
        });
      } else if (pen.value.bkType === Gradient.Radial) {
        configs.push({
          key: 'gradientRadius',
          name: `${t('渐变半径')}`,
          type: 'number',
          min: 0,
          max: 1,
          step: 0.01,
        });
      }

      configs.push(
        {
          key: 'hoverBackground',
          name: `${t('浮动背景颜色')}`,
          type: 'color',
        },
        {
          key: 'activeBackground',
          name: `${t('选中背景颜色')}`,
          type: 'color',
        },
        {
          key: 'globalAlpha',
          name: `${t('透明度')}`,
          placeholder: '(0-1)',
          type: 'number',
          max: 1,
          min: 0,
          step: 0.1,
        },
        {
          key: 'anchorColor',
          name: `${t('锚点颜色')}`,
          type: 'color',
        },
        {
          key: 'anchorRadius',
          type: 'number',
          name: `${t('锚点半径')}`,
          min: 1,
        },
        {
          key: 'shadowColor',
          type: 'color',
          name: `${t('阴影颜色')}`,
        },
        {
          key: 'shadowBlur',
          type: 'number',
          name: `${t('阴影模糊')}`,
          min: 0,
        },
        {
          key: 'shadowOffsetX',
          type: 'number',
          name: `${t('阴影')} X ${t('偏移')}`,
        },
        {
          key: 'shadowOffsetY',
          type: 'number',
          name: `${t('阴影')} Y ${t('偏移')}`,
        },
        {
          key: 'textHasShadow',
          type: 'switch',
          name: `${t('文字阴影')}`,
        }
      );
      return configs;
    });

    const arrows = reactive({
      fromArrows: [],
      toArrows: [],
    });
    importNormalArrow();

    async function importNormalArrow() {
      const { fromArrows, toArrows } = await import('../defaults');
      arrows.fromArrows = fromArrows;
      arrows.toArrows = toArrows;
    }
    // 位置 configs 连线才展示
    const positionConfigs = computed<FormItemType[]>(() => {
      const configs: FormItemType[] = [
        {
          key: 'fromArrow',
          type: 'select',
          name: `${t('起点箭头')}`,
          options: arrows.fromArrows.map((item) => ({
            label: `<i class="${item.icon} arrowIcon"></i> `,
            value: item.value,
          })),
        },
        {
          key: 'fromArrowSize',
          type: 'number',
          name: `${t('起点箭头大小')}`,
        },
        {
          key: 'fromArrowColor',
          type: 'color',
          name: `${t('起点箭头颜色')}`,
        },
        {
          key: 'toArrow',
          type: 'select',
          name: `${t('终点箭头')}`,
          options: arrows.toArrows.map((item) => ({
            label: `<i class="${item.icon} arrowIcon"></i> `,
            value: item.value,
          })),
        },
        {
          key: 'toArrowSize',
          type: 'number',
          name: `${t('终点箭头大小')}`,
        },
        {
          key: 'toArrowColor',
          type: 'color',
          name: `${t('终点箭头颜色')}`,
        },
        {
          key: 'autoFrom',
          type: 'switch',
          name: `${t('起点自动关联')}`,
        },
        {
          key: 'autoTo',
          type: 'switch',
          name: `${t('终点自动关联')}`,
        },
      ];

      return configs;
    });

    // 文字 configs
    const textConfigs = computed<FormItemType[]>(() => {
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
          name: `${t('字体大小')}`,
          type: 'number',
          min:0,//字体大小最小值
        },
        {
          key: 'textColor',
          name: `${t('文字颜色')}`,
          type: 'color',
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
          key: 'textWidth',//文本宽度，超出换行。影响worldTextRect的宽度，可以设置<1的值，认为是宽度的百分比
          type: 'number',
          name: `${t('文字宽度')}`,
        },
        {
          key: 'textHeight',//文本高度，超出显示省略号。影响worldTextRect的高度，可以设置<1的值，认为是高度的百分比
          type: 'number',
          name: `${t('文字高度')}`,
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
        {
          // undefined 认为是 true 所以使用下拉框
          key: 'ellipsis',
          type: 'select',
          name: `${t('超出省略')}`,
          placeholder: `${t('是')}`,
          options: [
            {
              label: `${t('否')}`,
              value: false,
            },
            {
              label: `${t('是')}`,
              value: true,
            },
          ],
        },
        {
          key: 'hiddenText',
          type: 'switch',
          name: `${t('隐藏文字')}`,
        },
        {
          key: 'keepDecimal',
          type: 'number',
          name: `${t('保留小数')}`,
          min: 0,
        },
        {
          name: `${t('文本内容')}`,
          key: 'text',
          type: 'textarea',
          rows: 3,
        },
      ];

      return configs;
    });

    // 图片
    const picConfigs = computed<FormItemType[]>(() => {
      const configs: FormItemType[] = [
        {
          key: 'image',
          name: `${t('图片选择')}`,
          type: 'image',
        },
        {
          key: 'image',
          name: `${t('图片地址')}`,
          type: 'text',
        },
        {
          key: 'backgroundImage',
          name: `${t('背景图片')}`,
          type: 'image',
        },
        {
          key: 'backgroundImage',
          name: `${t('背景图片地址')}`,
          type: 'text',
        },
        {
          key: 'strokeImage',
          name: `${t('描绘图片')}`,
          type: 'image',
        },
        {
          key: 'strokeImage',
          name: `${t('描绘图片地址')}`,
          type: 'text',
        },
        {
          key: 'iconWidth',
          name: `${t('宽度')}`,
          placeholder: `${t('自适应')}`,
          type: 'number',
        },
        {
          key: 'iconHeight',
          name: `${t('高度')}`,
          placeholder: `${t('自适应')}`,
          type: 'number',
        },
        {
          key: 'imageRatio',
          name: `${t('保存比例')}`,
          type: 'switch',
        },
        {
          key: 'iconLeft',
          name: `${t('水平偏移')}`,
          type: 'number',
        },
        {
          key: 'iconTop',
          name: `${t('垂直偏移')}`,
          type: 'number',
        },
        {
          key: 'iconAlign',
          name: `${t('对齐方式')}`,
          type: 'select',
          options: [
            {
              label: `${t('居中')}`,
              value: 'center',
            },
            {
              label: `${t('上')}`,
              value: 'top',
            },
            {
              label: `${t('下')}`,
              value: 'bottom',
            },
            {
              label: `${t('左')}`,
              value: 'left',
            },
            {
              label: `${t('右')}`,
              value: 'right',
            },
            {
              label: `${t('左上')}`,
              value: 'left-top',
            },
            {
              label: `${t('右上')}`,
              value: 'right-top',
            },
            {
              label: `${t('左下')}`,
              value: 'left-bottom',
            },
            {
              label: `${t('右下')}`,
              value: 'right-bottom',
            },
          ],
        },
      ];

      /*if (pen.value.image) {
        // 图片存在，可改变上层或下层
        configs.push({
          key: 'isBottom',
          name: `${t('下层')}`,
          type: 'switch',
        });
      }*/

      return configs;
    });

    // 图标字体
    const iconConfigs = computed<FormItemType[]>(() => {
      const configs: FormItemType[] = [
        {
          key: 'icon',
          name: `${t('字体图标')}`,
          type: 'icon',
          iconFamily: pen.value.iconFamily,
        },
        {
          key: 'iconSize',
          name: `${t('图标大小')}`,
          placeholder: `${t('自适应')}`,
          type: 'number',
        },
        {
          key: 'iconRotate',
          name: `${t('图标旋转')}`,
          type: 'number',
          min: 0,
        },
        {
          key: 'iconColor',
          name: `${t('颜色')}`,
          type: 'color',
        },
        {
          key: 'iconAlign',
          name: `${t('对齐方式')}`,
          type: 'select',
          options: [
            {
              label: `${t('居中')}`,
              value: 'center',
            },
            {
              label: `${t('上')}`,
              value: 'top',
            },
            {
              label: `${t('下')}`,
              value: 'bottom',
            },
            {
              label: `${t('左')}`,
              value: 'left',
            },
            {
              label: `${t('右')}`,
              value: 'right',
            },
            {
              label: `${t('左上')}`,
              value: 'left-top',
            },
            {
              label: `${t('右上')}`,
              value: 'right-top',
            },
            {
              label: `${t('左下')}`,
              value: 'left-bottom',
            },
            {
              label: `${t('右下')}`,
              value: 'right-bottom',
            },
          ],
        },
      ];

      return configs;
    });

    // TODO: 目前取起点 next 和终点 prev 的控制点
    const controlPoints = ref<Point[]>([]);
    // 起终点
    const points = reactive({
      from: {},
      to: {},
    });
    function checkDecimal(strNum) {
      // 使用正则表达式匹配小数点后的数字（最多保留两位）
      if (/^\d+\.\d{1,2}$/.test(strNum)) {
          return true; // 小数不超过两位
      } else {
          return false; // 小数超过两位或者没有小数部分
      }
    }
    // 监听赋值的变化
    watch(
      () => pen.value,
      (newval) => {
        //console.log('更新pen的值',pen.value)
        if(!checkDecimal(newval.x)){
          pen.value.x=Math.round(newval.x*100)/100
        }
        if(!checkDecimal(newval.y)){
          pen.value.y=Math.round(newval.y*100)/100
        }
        if(!checkDecimal(newval.width)){
          pen.value.width=Math.round(newval.width*100)/100
        }
        if(!checkDecimal(newval.height)){
          pen.value.height=Math.round(newval.height*100)/100
        }
        
        //去除整体移动时出现小数
        /*if(newval.center){
          pen.value.center.x=Math.round(newval.center.x)
          pen.value.center.y=Math.round(newval.center.y)
        }
        if(newval.length){
          pen.value.length=Math.round(newval.length)
        }
        if(newval.calculative?.worldAnchors){
          newval.calculative.worldAnchors.forEach((x,index)=>{
            pen.value.calculative.worldAnchors[index].x=Math.round(x.x)
            pen.value.calculative.worldAnchors[index].y=Math.round(x.y)
          })
        }
        pen.value.x=Math.round(newval.x)
        pen.value.y=Math.round(newval.y)
        pen.value.width=Math.round(newval.width)
        pen.value.height=Math.round(newval.height)
        
        pen.value.ex=Math.round(newval.ex)
        pen.value.ey=Math.round(newval.ey)*/
        //处理旋转为NAN的情况
        if(!newval.rotate){
          pen.value.rotate=0
        }
        if(!isFinite(newval.animateCycle)){
          pen.value.animateCycle=0
        }
      },
      {
        deep: true,
      }
    );
    watchEffect(() => {
      controlPoints.value = [];
      if (
        pen.value.type === PenType.Line &&
        Array.isArray(pen.value.calculative?.worldAnchors) &&
        pen.value.calculative?.worldAnchors.length >= 2
      ) {
        const worldAnchors = pen.value.calculative?.worldAnchors;
        const from = worldAnchors[0];
        const to = worldAnchors[worldAnchors.length - 1];
        from.next && controlPoints.value.push({ ...from.next });
        to.prev && controlPoints.value.push({ ...to.prev });

        points.from = { ...from };
        points.to = { ...to };
      }
    });

    // 控制点
    const controlPointConfigs = (index: number) => {
      const configs: FormItemType[] = [
        {
          key: 'x',
          name: `${t('控制点')}${index + 1} X`,
          type: 'number',
        },
        {
          key: 'y',
          name: `${t('控制点')}${index + 1} Y`,
          type: 'number',
        },
      ];

      return configs;
    };

    function changeControlPoints(
      { value, keys }: { value: any; keys: string[] },
      index: number
    ) {
      !value && (value = 0);
      value = Number(value);
      const canvasPen = meta2d.find(pen.value.id)[0];
      const worldAnchors = canvasPen.calculative?.worldAnchors;
      const from = worldAnchors[0];
      const to = worldAnchors[worldAnchors.length - 1];

      if (index) {
        // 1 终点
        to.prev[keys[0]] = value;
      } else {
        from.next[keys[0]] = value;
      }

      meta2d.canvas.initLineRect(canvasPen);
      meta2d.render();
    }

    // 起终点
    const pointsConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          key: 'from',
          key2: 'x',
          name: `${t('起点坐标')} X`,
          type: 'number',
        },
        {
          key: 'from',
          key2: 'y',
          name: `${t('起点坐标')} Y`,
          type: 'number',
        },
        {
          key: 'to',
          key2: 'x',
          name: `${t('终点坐标')} X`,
          type: 'number',
        },
        {
          key: 'to',
          key2: 'y',
          name: `${t('终点坐标')} Y`,
          type: 'number',
        },
      ];

      return configs;
    });

    function changePoints({ value, keys }: { value: any; keys: string[] }) {
      !value && (value = 0);
      value = Number(value);
      const canvasPen = meta2d.find(pen.value.id)[0];
      const worldAnchors = canvasPen.calculative?.worldAnchors;
      const from = worldAnchors[0];
      const to = worldAnchors[worldAnchors.length - 1];

      if (keys[0] === 'from') {
        from[keys[1]] = value;
      } else if (keys[0] === 'to') {
        to[keys[1]] = value;
      }

      meta2d.canvas.initLineRect(canvasPen);
      meta2d.render();
    }

    const disableConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          key: 'disableInput',
          name: `${t('禁用输入')}`,
          type: 'switch',
        },
        {
          key: 'disableRotate',
          name: `${t('禁用旋转')}`,
          type: 'switch',
        },
        {
          key: 'disableSize',
          name: `${t('禁用缩放')}`,
          type: 'switch',
        },
        {
          key: 'disableAnchor',
          name: `${t('禁用锚点')}`,
          type: 'switch',
        },
      ];

      return configs;
    });

    return {
      sizeConfigs,
      pen,
      changeValue,
      activeKey,
      styleConfigs,
      positionConfigs,
      textConfigs,
      picConfigs,
      iconConfigs,
      indeSeleConfigs,
      controlPoints,
      controlPointConfigs,
      changeControlPoints,
      points,
      pointsConfigs,
      changePoints,
      disableConfigs,
      controlConfigs,//控件
    };
  },
});
</script>

<style lang="scss">
@import '@/styles/variables.scss';

.collapseTitle {
  i {
    margin-left: 4px;
    font-weight: 500;
  }
}
.PenProps_box .ant-collapse-item .ant-collapse-content{
  height: auto!important;
}
</style>
