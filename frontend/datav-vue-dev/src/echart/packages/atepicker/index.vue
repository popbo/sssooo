<template>
  <div
    :class="b()"
    :style="{
      '--fontSize': option.fontSize + 'px',
      '--focusStyleColor': option.focusStyle.color,
      '--Mbgcolor': option.Mbgcolor,
      '--borderColor': option.borderColor,
      '--textAlign': option.textAlign,
      '--fontWeight': option.fontWeight,
      '--selectedColor': option.selectedColor,
      '--weekColor': option.weekColor,
      '--selectedColor': option.selectedColor,
      '--color': option.color,
      '--focusStyleBgColor': option.focusStyle.bgColor,
    }"
  >
    <el-date-picker
      v-model="timelist"
      :type="currentType"
      range-separator="--"
      :style="[styleName, hoverStyle, focusStyle]"
      :format="currentFormat"
      :popper-class="id"
      @change="handleChange"
      @focus="handleFocus"
    />
  </div>
</template>

<script>
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import localeData from 'dayjs/plugin/localeData'
let weekOfYear = require('dayjs/plugin/weekOfYear')
dayjs.locale('zh-cn')
dayjs.extend(localeData)
dayjs.extend(weekOfYear)
import create from '../../create'
import { uploadCss, obtainCss } from '@/api/visual.js'
import { EventBus } from '@/bus.js'
export default create({
  inject: ['main'],
  name: 'atepicker',
  data() {
    return {
      hoverStyle: {},
      focusStyle: {},
      style: '',
      timelist: '',
    }
  },
  props: {
    option: Object,
  },
  watch: {
    option: {
      handler() {
        this.css()
      },
      deep: true,
    },
    'option.interval': {
      handler() {
        this.getTimelist()
      },
      deep: true,
    },
  },
  created() {
    this.getTimelist()
  },
  mounted() {
    this.css()
    EventBus.$on('atepickerTimeChange', () => {
      this.getTimelist()
    })
    this.$nextTick(() => {
      this.getClick()
    })
  },
  methods: {
    // 默认时间设置
    getTimelist() {
      let date = new Date().getTime()
      let showTime = ''
      let showTimeList = [] //区间时
      // 区间时时间段
      if (this.option.interval) {
        showTimeList = [date, date]
        if (this.option.intervalRadio === 'Granularity') {
          let oneDay = this.getCurrentTime('Granularity')
          let newTime = new Date(oneDay).getTime()
          showTimeList = [newTime, newTime]
        }
        let startTime = new Date().getTime()
        let endTime = new Date().getTime()
        if (this.option.intervalStartRadio === '1') {
          if (this.option.intervalStartTimeUnit === 'year') {
            startTime =
              startTime + this.option.intervalStartTimeNumber * 1000 * 31536000
          }
          if (this.option.intervalStartTimeUnit === 'month') {
            startTime =
              startTime +
              this.option.intervalStartTimeNumber * 1000 * 60 * 60 * 24 * 30
          }
          if (this.option.intervalStartTimeUnit === 'date') {
            startTime =
              startTime +
              this.option.intervalStartTimeNumber * 1000 * 60 * 60 * 24
          }
        }
        if (this.option.intervalEndRadio === '1') {
          if (this.option.intervalEndTimeUnit === 'year') {
            endTime =
              endTime + this.option.intervalEndTimeNumber * 1000 * 31536000
          }
          if (this.option.intervalEndTimeUnit === 'month') {
            endTime =
              endTime +
              this.option.intervalEndTimeNumber * 1000 * 60 * 60 * 24 * 30
          }
          if (this.option.intervalEndTimeUnit === 'date') {
            endTime =
              endTime + this.option.intervalEndTimeNumber * 1000 * 60 * 60 * 24
          }
        }
        if (this.option.intervalStartRadio === '2') {
          if (this.option.intervalStartTimeUnit === 'year') {
            startTime =
              startTime - this.option.intervalStartTimeNumber * 1000 * 31536000
          }
          if (this.option.intervalStartTimeUnit === 'month') {
            startTime =
              startTime -
              this.option.intervalStartTimeNumber * 1000 * 60 * 60 * 24 * 30
          }
          if (this.option.intervalStartTimeUnit === 'date') {
            startTime =
              startTime -
              this.option.intervalStartTimeNumber * 1000 * 60 * 60 * 24
          }
        }
        if (this.option.intervalEndRadio === '2') {
          if (this.option.intervalEndTimeUnit === 'year') {
            endTime =
              endTime - this.option.intervalEndTimeNumber * 1000 * 31536000
          }
          if (this.option.intervalEndTimeUnit === 'month') {
            endTime =
              endTime -
              this.option.intervalEndTimeNumber * 1000 * 60 * 60 * 24 * 30
          }
          if (this.option.intervalEndTimeUnit === 'date') {
            endTime =
              endTime - this.option.intervalEndTimeNumber * 1000 * 60 * 60 * 24
          }
        }
        if (this.option.intervalEndRadio || this.option.intervalStartRadio) {
          showTimeList = [startTime, endTime]
        }
        this.timelist = showTimeList
      } else {
        showTime = date
        // 按自然粒度计算
        if (this.option.timeRadio === 'Granularity') {
          let oneDay = this.getCurrentTime('Granularity')
          showTime = new Date(oneDay).getTime()
        }
        if (this.option.timeRadio === '1') {
          if (this.option.timeUnit === 'date') {
            showTime = this.option.timeNumber * 1000 * 60 * 60 * 24 + date
          }
          if (this.option.timeUnit === 'month') {
            showTime = this.option.timeNumber * 1000 * 60 * 60 * 24 * 30 + date
          }
        }
        if (this.option.timeRadio === '2') {
          if (this.option.timeUnit === 'date') {
            showTime = date - this.option.timeNumber * 1000 * 60 * 60 * 24
          }
          if (this.option.timeUnit === 'month') {
            showTime = date - this.option.timeNumber * 1000 * 60 * 60 * 24 * 30
          }
        }
        this.timelist = showTime
      }
    },
    // 按自然粒度计算
    getCurrentTime(type) {
      let date = new Date()
      let year = date.getFullYear() // 获取年
      let month = date.getMonth() + 1 // 获取月
      month = month > 9 ? month : '0' + month
      let day = date.getDate() // 获取日
      day = day > 9 ? day : '0' + day
      let hour = date.getHours() // 时
      hour = hour > 9 ? hour : '0' + hour
      let minutes = date.getMinutes() // 分
      minutes = minutes > 9 ? minutes : '0' + minutes
      let seconds = date.getSeconds() //秒
      seconds = seconds > 9 ? seconds : '0' + seconds
      if (type === 'Granularity') {
        day = '01'
      }
      return `${year}-${month}-${day} ${hour}:${minutes}:${seconds}`
    },
    handleChange() {
      this.getClick()
    },
    // 各种类型时间处理
    timestampToTime(timestamp) {
      let date = new Date(timestamp)
      let Y = date.getFullYear()
      let M =
        date.getMonth() + 1 < 10
          ? '0' + (date.getMonth() + 1)
          : date.getMonth() + 1
      let D = date.getDate() < 10 ? '0' + date.getDate() : date.getDate()
      let h = date.getHours() < 10 ? '0' + date.getHours() : date.getHours()
      let m =
        date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()
      let s =
        date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds()
      let parms
      if (this.option.interval) {
        parms = this.option.intervalGranulation
      } else {
        parms = this.option.timeGranulation
      }
      if (parms === 'year' || parms === 'yearrange') {
        return `${Y}-01-01 00:00:00`
      }
      if (parms === 'month' || parms === 'monthrange') {
        return `${Y}-${M}-01 00:00:00`
      }
      if (parms === 'week' || parms === 'date' || parms === 'daterange') {
        return `${Y}-${M}-${D} 00:00:00`
      }
      return `${Y}-${M}-${D} ${h}:${m}:${s}`
    },
    getClick() {
      if (this.timelist) {
        if (
          Object.prototype.toString.call(this.timelist).slice(8, -1) === 'Array'
        ) {
          let startTime = this.timestampToTime(this.timelist[0])
          let endTime = this.timestampToTime(this.timelist[1])
          let time = startTime + ',' + endTime
          this.updateClick(
            { value: time, name: time, isInterval: true },
            'clickFormatter'
          )
        } else {
          let time = this.timestampToTime(this.timelist)
          this.updateClick(
            { value: time, name: time, isInterval: false },
            'clickFormatter'
          )
        }
      }
    },
    async css() {
      let id = this.id.substring(4, this.id.length)
      let styles = `
        .${this.id} .el-date-range-picker__time-header .el-date-range-picker__time-picker-wrap .el-date-range-picker__editor .el-input__inner{
              background:${this.option.focusStyle.bgColor}  !important;
             };
            .${this.id}{
          background:#2d547b !important;
        };
        .${this.id} input{
          background:#fff ;
        };
        .${this.id} .el-picker-panel__body-wrapper .el-picker-panel__body .el-date-picker__time-header .el-date-picker__editor-wrap  .el-input .el-input--small input{
          background:#fff ;
        };
            .${this.id}  .el-date-table td.end-date .current span, .${this.id}  .el-date-table td.start-date .current span{
          color:${this.option.selectedColor} !important;
          background:${this.option.selectedColor} !important;
        };
        .${this.id} .el-picker-panel__body-wrapper .el-picker-panel__body .el-picker-panel__content .el-month-table tbody tr .current .cell{
               color:${this.option.selectedColor} !important;
               background:${this.option.selectedColor} !important;
            };
            .${this.id} .el-date-range-picker__time-header input{
                background:red !important;
            }
            .${this.id} .el-date-range-picker__editor  .el-input{
              background:red;
            }
         .${this.id},.${this.id}  .el-picker-panel__body-wrapper{
            border: none;
          }
          .${this.id} .el-date-picker__header{
            // background:${this.option.weekColor};
            color:${this.option.focusStyle.color};
             margin: 0px;
          }
          .${this.id} .el-date-picker__header span, .${this.id} .el-date-picker__header div, .${this.id} .el-picker-panel__content .el-month-table td .cell ,.${this.id} .el-year-table .available,.${this.id} .el-date-picker__header button{
            color:${this.option.focusStyle.color};
          }

          .${this.id} .el-picker-panel__content{
            // background:${this.option.Mbgcolor};
            margin: 0px;
            width:100%;
            border:none;
          }
          .${this.id}.el-date-picker .el-date-picker__time-header{
          border-bottom: 1px solid ${this.option.borderColor};
        }
          .${this.id} .el-picker-panel__content table tr .available,
          .${this.id} .el-date-table td.available.in-range div,
          .${this.id} .el-date-table td.available.in-range div span,
          .${this.id} .el-picker-panel__content table tr .available.in-change,
          .${this.id} .el-date-table.is-week-mode .el-date-table__row.current div {
             color:${this.option.selectedColor};
          }
          .${this.id} .el-date-table th{
            background:${this.option.weekColor};
            color:${this.option.focusStyle.color};
            border:none;
          }
          .${this.id}  .el-picker-panel__body .el-picker-panel__content div{
            color:${this.option.focusStyle.color};
          }

          .${this.id}.el-picker-panel .el-date-range-picker__time-header{
            background-color:${this.option.Mbgcolor};
            border-bottom:1px solid ${this.option.borderColor}
          }
          .${this.id} .el-date-table td.in-range div{
            background-color:${this.option.focusStyle.bgColor};
          }
          .${this.id} .el-date-table td.in-range div:hover,
          ${this.id} .el-date-table.is-week-mode .el-date-table__row:hover div{
            background-color:${this.option.focusStyle.bgColor};
            color:${this.option.color};
          }
          .${this.id} .el-date-table td.in-range div,
          .${this.id} .el-date-table td.in-range div:hover,
          .${this.id} .el-date-table.is-week-mode .el-date-table__row.current div,
          .${this.id} .el-date-table.is-week-mode .el-date-table__row:hover div,
          .${this.id} .el-date-table td.current:not(.disabled) span{
            background-color:${this.option.focusStyle.bgColor};
            color:${this.option.selectedColor}
          }
          .${this.id}  .el-picker-panel__body .el-picker-panel__content button{
            color:${this.option.color}
          }
         .${this.id} .el-picker-panel__footer {
            background:${this.option.Mbgcolor};
           border:none;
         }
         .${this.id} .el-picker-panel__body-wrapper .el-picker-panel__body{
          background:${this.option.Mbgcolor}
         }
         .${this.id} .el-picker-panel__footer .el-button{

            border:1px solid ${this.option.focusStyle.bgColor};
            border-radius:10px;
          width:50px;
          height:22px;
          font-size:12px;
          color:${this.option.color};
          padding:0;
         }
         .${this.id} .el-picker-panel__footer .is-plain{
          color:${this.option.color} !important;
          background:${this.option.focusStyle.bgColor} !important;
          border:none;
          border-radius:10px;
          width:50px;
          height:22px;
          font-size:12px;
         }
         .${this.id} .el-date-table td.today span{
            color:${this.option.color};
          }
          .${this.id} .el-date-table .current td.today span,
          .${this.id} .el-picker-panel__content .el-month-table td.in-range .cell,
          .${this.id} .el-picker-panel__body .td.in-range div,
          .${this.id} .el-date-table td.today.end-date span,
          .${this.id} .el-date-table td.today.start-date span{
            color:${this.option.selectedColor};
          }
          .${this.id} .el-year-table td .cell{
            color:${this.option.focusStyle.color};
          }
          .${this.id} .el-year-table td .cell:hover,
          .${this.id} .el-month-table td .cell:hover,
          .${this.id} .el-year-table td.current:not(.disabled) .cell,
          .${this.id} .el-month-table td.current:not(.disabled) .cell{
            background:${this.option.focusStyle.bgColor};
            color:${this.option.selectedColor};
            border-radius:0
          }
        `
      //提交最新修改CSS
      await uploadCss(id, styles)
      //获取最新css
      obtainCss(id).then(resq => {
        var style = document.createElement('style')
        style.type = 'text/css'
        style.innerHTML = `${resq.data.data}`
        document.getElementsByTagName('head')[0].appendChild(style)
      })
    },
    handleFocus() {
      if (this.option.focusIsUse) {
        this.focusStyle = {
          color: this.option.color,
          fontSize: this.setPx(this.option.focusStyle.fontSize || 30),
          background: this.option.focusStyle.bgColor,
          borderColor: this.option.focusStyle.borderColor,
        }
      }
      this.getClick()
    },
  },
  computed: {
    currentFormat() {
      let parms = ''
      if (this.option.interval) {
        parms = this.option.intervalGranulation
      } else {
        parms = this.option.timeGranulation
      }
      let timeType = 'yyyy/MM/dd'
      if (parms === 'year') {
        timeType = 'yyyy'
      } else if (parms === 'month') {
        timeType = 'yyyy/MM'
      } else if (parms === 'week') {
        timeType = 'yyyy 第 WW 周'
      } else if (parms === 'date') {
        timeType = 'yyyy/MM/dd'
      } else if (parms === 'datetime') {
        timeType = 'yyyy/MM/dd HH:mm:ss'
      } else if (parms === 'yearrange') {
        timeType = 'yyyy'
      } else if (parms === 'monthrange') {
        timeType = 'yyyy/MM'
      } else if (parms === 'daterange') {
        timeType = 'yyyy/MM/dd'
      } else if (parms === 'datetimerange') {
        timeType = 'yyyy/MM/dd HH:mm:ss'
      } else {
        timeType = 'yyyy/MM/dd'
      }
      return timeType
    },
    currentType() {
      let parms = ''
      let type = ''
      if (this.option.interval) {
        parms = this.option.intervalGranulation
      } else {
        parms = this.option.timeGranulation
      }
      // 区间
      if (this.option.interval) {
        type = parms ? parms : 'daterange'
      } else {
        //不是区间时
        type = parms ? parms : 'date'
      }
      return type
    },
    styleName() {
      const obj = Object.assign(
        (() => {
          if (this.option.backgroundImage) {
            return {
              backgroundImage: `url(${this.option.backgroundImage})`,
              backgroundSize: '100% 100%',
            }
          }
          return {}
        })(),
        {
          borderColor: this.option.borderColor || '#309EF8',
          borderStyle: this.option.borderStyle || 'solid',
          borderWidth: this.setPx(this.option.borderWidth || 0),
          borderRadius: this.setPx(this.option.borderRadius || 4),
          background: this.option.bgColor,
          fontSize: this.setPx(this.option.fontSize || 30),
          fontFamily: this.option.fontFamily,
          textAlign: this.option.textAlign,
          color: this.option.color,
        },
        (() => {
          if (!this.option.isGradient) {
            return {
              background: this.option.bgColor || '#309EB6',
            }
          }
          return {}
        })()
      )
      return obj
    },
  },
})
</script>

<style scoped lang="scss">
.el-date-editor {
  width: 100%;
  height: 100%;
  cursor: pointer;
  /deep/ .el-icon-date {
    line-height: 37px;
  }
  &.el-range-editor {
    /deep/ .el-icon-date {
      line-height: 32px;
    }
  }
}

::v-deep .el-range-separator {
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--focusStyleColor);
  padding: 0 12px;
}
::v-deep .el-date-editor .el-range__icon,
.el-date-editor .el-range__close-icon {
  color: var(--focusStyleColor);
}
::v-deep .popper__arrow::after {
  border-bottom-color: var(--Mbgcolor) !important;
}
::v-deep .el-date-editor {
  background: var(--Mbgcolor) !important;
  color: #fff;
  border-color: var(--borderColor) !important;
}
::v-deep input {
  height: 100%;
  color: var(--focusStyleColor) !important;
  background: var(--Mbgcolor) !important;
  font-size: var(--fontSize) !important;
  text-align: var(--textAlign) !important;
  font-weight: var(--fontWeight) !important;
}
::v-deep .el-date-editor .el-range__icon {
  display: none;
}
::v-deep .el-date-editor .el-range-separator {
  min-width: 40px !important;
  padding-left: 2px !important;
  font-size: var(--fontSize);
}
::v-deep .el-range-editor .el-range-input {
  cursor: pointer;
}

::v-deep .el-input__prefix {
  opacity: 0;
}
::v-deep .el-input--prefix .el-input__inner {
  padding-left: 5px;
}
</style>
