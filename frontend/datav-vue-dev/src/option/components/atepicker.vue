<template>
  <div style="position: relative">
    <div v-show="custom" class="customs">
      <div class="customsDiv">
        <p class="customsDivP">自定义属性</p>
      </div>
      <div v-if="!main.activeOption.interval">
        <div class="rich-text">
          <p style="line-height: 0; margin-bottom: 5px; margin: 20px">
            <span
              style="
                font-weight: 400;
                font-size: 16px;
                color: rgb(255, 255, 255);
                letter-spacing: 0px;
                line-height: 24px;
                text-decoration: none;
              "
              >根据当前时间：
            </span>
          </p>
          <div style="display: flex; align-items: center; margin-left: 20px">
            <el-radio style="width: 20px" v-model="timeRadio" label="1"
              >加</el-radio
            >
            <el-radio style="width: 20px" v-model="timeRadio" label="2"
              >减</el-radio
            >
            <el-input-number
              v-model="timeNumber"
              style="width: 80px; margin-right: 10px"
              controls-position="right"
              @change="handleChange"
              size="mini"
              :min="0"
              :max="10"
            ></el-input-number>
            <el-select
              v-model="timeUnit"
              placeholder="请选择"
              style="width: 100px"
              size="mini"
            >
              <el-option
                v-for="item in typetimes2"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </div>
        </div>
        <el-checkbox-group
          v-model="checkList"
          style="margin-left: 20px; padding-top: 20px"
        >
          <el-radio v-model="timeRadio" label="Granularity"
            >按自然粒度计算
            <el-tooltip
              class="item"
              effect="light"
              placement="right"
              style="padding-left: 10px"
            >
              <div slot="content">
                自然粒度表示按照自然<br />时间计算即不论当前是<br />几月，都会从1月开始;<br />不论当前是哪天，都会<br />从1号开始，依此类推.
              </div>
              <i class="el-icon-question"></i>
            </el-tooltip>
          </el-radio>
        </el-checkbox-group>
        <el-checkbox-group
          v-model="checkList"
          style="margin-left: 20px; padding-top: 20px"
        >
          <el-radio v-model="timeRadio" label="Update"
            >自动更新
            <el-tooltip
              class="item"
              effect="light"
              placement="right"
              style="padding-left: 10px"
            >
              <div slot="content">
                勾选，表示动态预览<br />后随着时间变化，自<br />动更新默认值;不勾选<br />，则只在每次动态预<br />览或页面刷新时更新<br />默认值。
              </div>
              <i class="el-icon-question"></i>
            </el-tooltip>
          </el-radio>
        </el-checkbox-group>
        <div class="butbon">
          <el-button type="primary" @click="timeChange" size="mini"
            >确定</el-button
          >
          <el-button @click="restTimeChange" size="mini">取消</el-button>
        </div>
      </div>
      <div v-else-if="main.activeOption.interval">
        <div class="rich-text">
          <p style="line-height: 0; margin-bottom: 5px; margin: 20px">
            <span
              style="
                font-weight: 400;
                font-size: 16px;
                color: rgb(255, 255, 255);
                letter-spacing: 0px;
                line-height: 24px;
                text-decoration: none;
              "
              >开始日期根据当前时间：
            </span>
          </p>
          <div style="display: flex; align-items: center; margin-left: 20px">
            <el-radio v-model="intervalStartRadio" label="1" value="jia"
              >加</el-radio
            >
            <el-radio v-model="intervalStartRadio" label="2" value="jie"
              >减</el-radio
            >
            <el-input-number
              v-model="intervalStartTimeNumber"
              style="width: 120px"
              controls-position="right"
              @change="handleChange"
              :min="0"
              :max="10"
            ></el-input-number>
            <el-select
              v-model="intervalStartTimeUnit"
              placeholder="请选择"
              style="width: 120px"
            >
              <el-option
                v-for="item in typetimes3"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </div>
          <p style="line-height: 0; margin-bottom: 5px; margin: 20px">
            <span
              style="
                font-weight: 400;
                font-size: 16px;
                color: rgb(255, 255, 255);
                letter-spacing: 0px;
                line-height: 24px;
                text-decoration: none;
              "
              >结束日期根据当前时间：
            </span>
          </p>
          <div style="display: flex; align-items: center; margin-left: 20px">
            <el-radio v-model="intervalEndRadio" label="1">加</el-radio>
            <el-radio v-model="intervalEndRadio" label="2">减</el-radio>
            <el-input-number
              v-model="intervalEndTimeNumber"
              style="width: 120px"
              controls-position="right"
              @change="handleChange"
              :min="0"
              :max="10"
            ></el-input-number>
            <el-select
              v-model="intervalEndTimeUnit"
              placeholder="请选择"
              style="width: 120px"
            >
              <el-option
                v-for="item in typetimes3"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </div>
        </div>
        <el-checkbox-group
          v-model="checkList"
          style="margin-left: 20px; padding-top: 20px"
        >
          <el-radio v-model="intervalRadio" label="Granularity"
            >按自然粒度计算
            <el-tooltip
              class="item"
              effect="light"
              placement="right"
              style="padding-left: 10px"
            >
              <div slot="content">
                自然粒度表示按照自然<br />时间计算即不论当前是<br />几月，都会从1月开始;<br />不论当前是哪天，都会<br />从1号开始，依此类推.
              </div>
              <i class="el-icon-question"></i>
            </el-tooltip>
          </el-radio>
        </el-checkbox-group>
        <el-checkbox-group
          v-model="checkList"
          style="margin-left: 20px; padding-top: 20px"
        >
          <el-radio v-model="intervalRadio" label="Update"
            >自动更新
            <el-tooltip
              class="item"
              effect="light"
              placement="right"
              style="padding-left: 10px"
            >
              <div slot="content">
                勾选，表示动态预览<br />后随着时间变化，自<br />动更新默认值;不勾选<br />，则只在每次动态预<br />览或页面刷新时更新<br />默认值。
              </div>
              <i class="el-icon-question"></i>
            </el-tooltip>
          </el-radio>
        </el-checkbox-group>
        <div class="butbon">
          <el-button type="primary" @click="timeIntervalChange">确定</el-button>
          <el-button @click="restTimeChange">取消</el-button>
        </div>
      </div>
    </div>
    <el-form-item label="区间">
      <avue-switch v-model="main.activeOption.interval"></avue-switch>
    </el-form-item>
    <el-form-item label="自定义选项">
      <el-button type="primary" @click="opneCustom">自定义选项</el-button>
    </el-form-item>
    <el-form-item label="粒度" v-show="!main.activeOption.interval">
      <el-select
        v-model="main.activeOption.timeGranulation"
        placeholder="请选择"
      >
        <el-option
          v-for="item in typetimes"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        >
        </el-option>
      </el-select>
    </el-form-item>
    <!-- 区间粒度 -->
    <el-form-item label="粒度" v-show="main.activeOption.interval">
      <el-select
        v-model="main.activeOption.intervalGranulation"
        placeholder="请选择"
      >
        <el-option
          v-for="item in intervalTypes"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        >
        </el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="边框粗细">
      <div style="display: flex">
        <el-input-number
          v-model="main.activeOption.borderWidth"
          controls-position="right"
        ></el-input-number>
        <i style="margin-left: 5px">px</i>
      </div>
    </el-form-item>
    <el-form-item label="边框颜色">
      <div style="display: flex">
        <el-color-picker
          v-model="main.activeOption.borderColor"
          show-alpha
        ></el-color-picker>
        <el-input
          placeholder="请选择..."
          v-model="main.activeOption.borderColor"
          :disabled="true"
        />
      </div>
    </el-form-item>
    <el-form-item label="主题配色">
      <div style="display: flex">
        <el-color-picker
          v-model="main.activeOption.themeColor"
          show-alpha
          @change="keepThemeColor"
        ></el-color-picker>
        <el-input
          placeholder="请选择..."
          v-model="main.activeOption.themeColor"
          :disabled="true"
        />
      </div>
    </el-form-item>
    <el-form-item label="默认字体颜色">
      <div style="display: flex">
        <el-color-picker
          v-model="main.activeOption.focusStyle.color"
          show-alpha
        ></el-color-picker>
        <el-input
          placeholder="请选择..."
          v-model="main.activeOption.focusStyle.color"
          :disabled="true"
        />
      </div>
    </el-form-item>
    <el-form-item label="按钮字体颜色">
      <div style="display: flex">
        <el-color-picker v-model="main.activeOption.color"></el-color-picker>
        <el-input
          placeholder="请选择..."
          v-model="main.activeOption.color"
          :disabled="true"
        />
      </div>
    </el-form-item>
    <el-form-item label="按钮背景颜色">
      <div style="display: flex">
        <el-color-picker
          v-model="main.activeOption.focusStyle.bgColor"
          show-alpha
        ></el-color-picker>
        <el-input
          placeholder="请选择..."
          v-model="main.activeOption.focusStyle.bgColor"
          :disabled="true"
        />
      </div>
    </el-form-item>
    <el-form-item label="选中字体颜色">
      <div style="display: flex">
        <el-color-picker
          v-model="main.activeOption.selectedColor"
          show-alpha
        ></el-color-picker>
        <el-input
          placeholder="请选择..."
          v-model="main.activeOption.selectedColor"
          :disabled="true"
        />
      </div>
    </el-form-item>
    <el-form-item label="字体大小">
      <el-input-number
        v-model="main.activeOption.fontSize"
        controls-position="right"
        :min="0"
        :max="200"
      ></el-input-number>
    </el-form-item>
    <el-form-item label="字体粗细">
      <el-select v-model="main.activeOption.fontWeight" placeholder="请选择">
        <el-option
          v-for="item in options"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        >
        </el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="字体对齐">
      <el-select v-model="main.activeOption.textAlign" placeholder="请选择">
        <el-option
          v-for="item in alignment"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        >
        </el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="星期背景颜色">
      <div style="display: flex">
        <el-color-picker
          v-model="main.activeOption.weekColor"
          show-alpha
        ></el-color-picker>
        <el-input
          placeholder="请选择..."
          v-model="main.activeOption.weekColor"
          :disabled="true"
        />
      </div>
    </el-form-item>
    <el-form-item label="背景颜色">
      <div style="display: flex">
        <el-color-picker
          v-model="main.activeOption.Mbgcolor"
          show-alpha
        ></el-color-picker>
        <el-input
          placeholder="请选择..."
          v-model="main.activeOption.Mbgcolor"
          :disabled="true"
        />
      </div>
    </el-form-item>
  </div>
</template>

<script>
import build from '../../page/build.vue'
import { dicOption, rgbaNum } from '@/option/config'
import { EventBus } from '@/bus.js'
export default {
  components: { build },
  inject: ['main'],
  data() {
    return {
      checkList: [],
      typetimes2: [
        {
          value: 'month',
          label: '月',
        },
        {
          value: 'date',
          label: '日',
        },
      ],
      typetimes3: [
        {
          value: 'year',
          label: '年',
        },
        {
          value: 'month',
          label: '月',
        },
        {
          value: 'date',
          label: '日',
        },
      ],
      //  type="year" year / month / date / week / datetime 每天 / datetimerange 日期范围 /daterange 日期范围 多个三角
      typetimes: [
        {
          value: 'year',
          label: '年',
        },
        {
          value: 'month',
          label: '月',
        },
        {
          value: 'week',
          label: '周',
        },
        {
          value: 'date',
          label: '日',
        },
        {
          value: 'datetime',
          label: '时分秒',
        },
      ],
      // 区间粒度
      intervalTypes: [
        // {
        //   value: 'yearrange',
        //   label: '年粒度',
        // },
        {
          value: 'monthrange',
          label: '月粒度',
        },
        {
          value: 'daterange',
          label: '日粒度',
        },
        {
          value: 'datetimerange',
          label: '秒粒度',
        },
      ],
      options: [
        {
          value: 'normal',
          label: '正常',
        },
        {
          value: 'bold',
          label: '加粗',
        },
      ],
      alignment: [
        {
          value: 'left',
          label: '左对齐',
        },
        {
          value: 'center',
          label: '居中',
        },
        {
          value: 'right',
          label: '右对齐',
        },
      ],
      value: '',
      alignments: '',
      num: '1',
      dicOption: dicOption,
      drawer: false,
      style: '',
      custom: false,
      timeRadio: 'Update',
      timeNumber: 0,
      timeUnit: 'date',
      intervalRadio: 'Update',
      intervalStartRadio: '',
      intervalStartTimeUnit: 'date',
      intervalStartTimeNumber: 0,
      intervalEndRadio: '',
      intervalEndTimeUnit: 'date',
      intervalEndTimeNumber: 0,
      timeGranulation: 'date',
      intervalGranulation: 'daterange',
    }
  },
  watch: {
    intervalRadio: {
      handler(val) {
        if (val === 'Update' || val === 'Granularity') {
          this.intervalStartRadio = ''
          this.intervalEndRadio = ''
        }
      },
      deep: true,
    },
    intervalStartRadio: {
      handler(val) {
        if (val === '1' || val === '2') {
          this.intervalRadio = ''
        }
      },
      deep: true,
    },
    intervalEndRadio: {
      handler(val) {
        if (val === '1' || val === '2') {
          this.intervalRadio = ''
        }
      },
      deep: true,
    },
  },
  methods: {
    opneCustom() {
      this.timeRadio = this.main.activeOption.timeRadio
      this.timeNumber = this.main.activeOption.timeNumber
      this.timeUnit = this.main.activeOption.timeUnit
      this.intervalRadio = this.main.activeOption.intervalRadio
      this.intervalStartRadio = this.main.activeOption.intervalStartRadio
      this.intervalStartTimeUnit = this.main.activeOption.intervalStartTimeUnit
      this.intervalStartTimeNumber =
        this.main.activeOption.intervalStartTimeNumber
      this.intervalEndRadio = this.main.activeOption.intervalEndRadio
      this.intervalEndTimeUnit = this.main.activeOption.intervalEndTimeUnit
      this.intervalEndTimeNumber = this.main.activeOption.intervalEndTimeNumber
      this.timeGranulation = this.main.activeOption.timeGranulation
      this.intervalGranulation = this.main.activeOption.intervalGranulation
      this.custom = true
    },
    handleClose(done) {
      this.$confirm('确认关闭？')
        .then(_ => {
          done()
        })
        .catch(_ => {})
    },
    timeChange() {
      this.main.activeOption.timeRadio = this.timeRadio
      this.main.activeOption.timeNumber = this.timeNumber
      this.main.activeOption.timeUnit = this.timeUnit
      this.main.activeOption.intervalRadio = this.intervalRadio
      this.main.activeOption.intervalStartRadio = this.intervalStartRadio
      this.main.activeOption.intervalStartTimeUnit = this.intervalStartTimeUnit
      this.main.activeOption.intervalStartTimeNumber =
        this.intervalStartTimeNumber
      this.main.activeOption.intervalEndRadio = this.intervalEndRadio
      this.main.activeOption.intervalEndTimeUnit = this.intervalEndTimeUnit
      this.main.activeOption.intervalEndTimeNumber = this.intervalEndTimeNumber
      this.main.activeOption.timeGranulation = this.timeGranulation
      this.main.activeOption.intervalGranulation = this.intervalGranulation
      EventBus.$emit('atepickerTimeChange')
      this.custom = false
    },
    timeIntervalChange() {
      let startTime = new Date().getTime()
      let endTime = new Date().getTime()
      if (this.intervalStartRadio === '1') {
        if (this.intervalStartTimeUnit === 'year') {
          startTime = startTime + this.intervalStartTimeNumber * 1000 * 31536000
        }
        if (this.intervalStartTimeUnit === 'month') {
          startTime =
            startTime + this.intervalStartTimeNumber * 1000 * 60 * 60 * 24 * 30
        }
        if (this.intervalStartTimeUnit === 'date') {
          startTime =
            startTime + this.intervalStartTimeNumber * 1000 * 60 * 60 * 24
        }
      }
      if (this.intervalEndRadio === '1') {
        if (this.intervalEndTimeUnit === 'year') {
          endTime = endTime + this.intervalEndTimeNumber * 1000 * 31536000
        }
        if (this.intervalEndTimeUnit === 'month') {
          endTime =
            endTime + this.intervalEndTimeNumber * 1000 * 60 * 60 * 24 * 30
        }
        if (this.intervalEndTimeUnit === 'date') {
          endTime = endTime + this.intervalEndTimeNumber * 1000 * 60 * 60 * 24
        }
      }
      if (this.intervalStartRadio === '2') {
        if (this.intervalStartTimeUnit === 'year') {
          startTime = startTime - this.intervalStartTimeNumber * 1000 * 31536000
        }
        if (this.intervalStartTimeUnit === 'month') {
          startTime =
            startTime - this.intervalStartTimeNumber * 1000 * 60 * 60 * 24 * 30
        }
        if (this.intervalStartTimeUnit === 'date') {
          startTime =
            startTime - this.intervalStartTimeNumber * 1000 * 60 * 60 * 24
        }
      }
      if (this.intervalEndRadio === '2') {
        if (this.intervalEndTimeUnit === 'year') {
          endTime = endTime - this.intervalEndTimeNumber * 1000 * 31536000
        }
        if (this.intervalEndTimeUnit === 'month') {
          endTime =
            endTime - this.intervalEndTimeNumber * 1000 * 60 * 60 * 24 * 30
        }
        if (this.intervalEndTimeUnit === 'date') {
          endTime = endTime - this.intervalEndTimeNumber * 1000 * 60 * 60 * 24
        }
      }
      if (startTime > endTime) {
        return this.$message.error('开始时间不能大于结束时间!')
      }
      this.main.activeOption.timeRadio = this.timeRadio
      this.main.activeOption.timeNumber = this.timeNumber
      this.main.activeOption.timeUnit = this.timeUnit
      this.main.activeOption.intervalRadio = this.intervalRadio
      this.main.activeOption.intervalStartRadio = this.intervalStartRadio
      this.main.activeOption.intervalStartTimeUnit = this.intervalStartTimeUnit
      this.main.activeOption.intervalStartTimeNumber =
        this.intervalStartTimeNumber
      this.main.activeOption.intervalEndRadio = this.intervalEndRadio
      this.main.activeOption.intervalEndTimeUnit = this.intervalEndTimeUnit
      this.main.activeOption.intervalEndTimeNumber = this.intervalEndTimeNumber
      this.main.activeOption.timeGranulation = this.timeGranulation
      this.main.activeOption.intervalGranulation = this.intervalGranulation
      EventBus.$emit('atepickerTimeChange')
      this.custom = false
    },
    restTimeChange() {
      this.custom = false
    },
    copyStyle() {
      this.main.activeOption.hoverStyle = this.deepClone(
        this.main.activeOption.focusStyle
      )
    },
    //配置主题色
    keepThemeColor(val) {
      const colorMatch = `rgba(${rgbaNum(val, 0)},${rgbaNum(val, 1)},${rgbaNum(
        val,
        2
      )},0.3)`
      const colorMatchWeek = `rgba(${rgbaNum(val, 0)},${rgbaNum(
        val,
        1
      )},${rgbaNum(val, 2)},0.2)`
      this.main.activeOption.focusStyle.color = val
      this.main.activeOption.focusStyle.bgColor = val
      this.main.activeOption.borderColor = colorMatch
      this.main.activeOption.weekColor = colorMatchWeek
      console.log('改变了', colorMatch)
    },
  },
  created() {},
  mounted() {
    // this.createStyle()
  },
}
</script>

<style lang="scss" scoped>
@import '~@/styles/buildVariables.scss';
.customs {
  position: absolute;
  width: 100%;
  height: 100%;
  background: #232630;
  z-index: 999;
  .customsDiv {
    width: 100%;
    height: 41px;
    background-color: #3e3f4c;
    border-style: none;
    border-color: unset;
    color: rgb(255, 255, 255);
    font-size: 14px;
    padding: 0px;
    text-align: center;
    line-height: 20px;
    font-weight: normal;
    font-style: normal;
    .customsDivP {
      font-family: SourceHanSansSC;
      font-weight: 400;
      font-size: 14px;
      color: #bcc9d4;
      font-style: normal;
      letter-spacing: 0px;
      line-height: 41px;
      text-decoration: none;
      text-align: left;
      margin-left: 20px;
    }
  }
  .el-radio {
    width: 50px;
  }
  .butbon {
    display: flex;
    padding-top: 20px;
    justify-content: center;
    align-items: center;
  }
}
/deep/ #el-tooltip-9093 {
  width: 150px;
}
</style>
