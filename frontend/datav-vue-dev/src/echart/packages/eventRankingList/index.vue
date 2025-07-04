<template>
  <div class="eventrangklist" :style="eventRankBox">
    <ul :style="ulStyle">
      <li
        v-for="(item, index) in dataChart"
        :key="item + index"
        :style="liStyle(index)"
      >
        <div class="event">
          <div class="icon_title">
            <div
              v-html="option.iconUrl"
              class="div-svg"
              v-if="option.isShowIcon"
            ></div>
            <span class="hed_title" :style="titleStyleName(index)">{{
              item.title
            }}</span>
          </div>
          <div class="time" :style="timeStyleName(index)">
            {{ handleTimeFormat(item.time) }}
          </div>
        </div>
        <div class="detail" :style="detailStyleName(index)">
          {{ item.detail }}
        </div>
      </li>
    </ul>
  </div>
</template>
<script>
import create from '../../create'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import localeData from 'dayjs/plugin/localeData'
dayjs.locale('zh-cn')
dayjs.extend(localeData)
export default create({
  name: 'eventRankingList',
  data() {
    return {
      animate: false,
      setInTime: '', // 定时器
    }
  },
  created() {
    console.log('this.option', this.dataChart)
    if (this.option.autoRotation) {
      this.start()
    }
    //this.start()
  },
  watch: {
    autoRotation(newVal) {
      if (newVal) {
        this.start()
      } else {
        this.stop()
      }
    },
    rotationTime() {
      this.stop()
      this.start()
    },
    animationModel() {
      this.getScrollHeight()
    },
  },
  computed: {
    ulStyle() {
      return {
        transition: this.animate ? 'all 0.5s ease 0s' : 'none 0s ease 0s',
        marginTop: this.animate ? this.getScrollHeight() : 0,
        backgroundColor: this.option.bgColor,
      }
    },
    liStyle() {
      return function (index) {
        let obj = {}
        if (index === 0) {
          obj.background = this.option.firstLineBg || '#263e58'
          // return {
          //   background: this.option.firstLineBg || '#263e58',
          // }
        } else {
          obj.background = this.option.bgColor || '#243244'
          // return {
          //   background: this.option.bgColor || '#243244',
          // }
        }
        let styleObj = {
          height: this.setPx(this.cellHeight),
          'border-bottom-width':
            this.option.borderXWidth >= 0
              ? this.setPx(this.option.borderXWidth)
              : '4px',
          'border-bottom-color':
            this.option.borderXColor && this.option.borderXWidth >= 0
              ? this.option.borderXColor
              : '#232630',
        }
        console.log('数据', Object.assign(styleObj, obj))
        return Object.assign(styleObj, obj)
      }
    },
    titleStyleName() {
      return function (index) {
        let titleColor = ''
        if (index === 0) {
          titleColor = this.option.firstLineTitleColor || '#00c6ff'
        } else {
          titleColor = this.option.titleColor || '#fff'
        }
        return {
          fontFamily: this.option.titleFontStyle || 'SourceHanSansCN-Normal',
          fontWeight: this.option.titleFontWeight || 'normal',
          fontSize: (this.option.titleFontSize || 14) + 'px',
          lineHeight: (this.option.titleLineHeight || 14) + 'px',
          color: titleColor,
        }
      }
    },
    timeStyleName() {
      return function (index) {
        let timeColor = ''
        if (index === 0) {
          timeColor = this.option.firstLineTimeColor || '#008bb3'
        } else {
          timeColor = this.option.timeColor || '#61768b'
        }
        return {
          fontFamily: this.option.timeFontStyle || 'SourceHanSansCN-Normal',
          fontWeight: this.option.timeFontWeight || 'normal',
          fontSize: (this.option.timeFontSize || 14) + 'px',
          lineHeight: (this.option.timeLineHeight || 14) + 'px',
          color: timeColor,
        }
      }
    },
    detailStyleName() {
      return function (index) {
        let detailColor = ''
        if (index === 0) {
          detailColor = this.option.firstLineDetailColor || '#008bb3'
        } else {
          detailColor = this.option.detailColor || '#aac6e2'
        }
        return {
          fontFamily: this.option.detailFontStyle || 'SourceHanSansCN-Normal',
          fontWeight: this.option.detailFontWeight || 'normal',
          fontSize: (this.option.detailFontSize || 14) + 'px',
          lineHeight: (this.option.detailLineHeight || 14) + 'px',
          color: detailColor,
        }
      }
    },
    cellHeight() {
      this.option.borderWidth = this.option.borderWidth
        ? this.option.borderWidth
        : 0
      return parseInt(
        (this.height - this.option.borderWidth) / this.option.count
      )
    },
    autoRotation() {
      return this.option.autoRotation
    },
    animationModel() {
      return this.option.animationModel
    },
    rotationTime() {
      return this.option.rotationTime
    },
    eventRankBox() {
      return {
        height: this.setPx(this.height),
        borderColor: this.option.borderColor || 'transparent',
        borderWidth: this.setPx(this.option.borderWidth || 0),
      }
    },
  },
  methods: {
    // 滚动栏滚动
    showMarquee() {
      let countScoll = 0
      if (this.option.animationModel === 'byItem') {
        countScoll = 1
      } else {
        countScoll = this.option.count
      }
      this.animate = true
      setTimeout(() => {
        let tempD = this.dataChart.slice(0, countScoll)
        this.dataChart = this.dataChart.concat(tempD)
        this.dataChart.splice(0, countScoll)
        this.animate = false
      }, 500)
    },
    stop() {
      clearInterval(this.setInTime)
    },
    start() {
      this.setInTime = setInterval(this.showMarquee, this.option.rotationTime)
    },
    getScrollHeight() {
      if (this.option.animationModel === 'byItem') {
        return `-${this.setPx(this.cellHeight + 4)}`
      } else {
        return `-${this.setPx((this.cellHeight + 4) * this.option.count)}`
      }
    },
    handleTimeFormat(time) {
      if (new Date(time) > 0) {
        const format = (this.option.timeFormat || 'yyyy-MM-dd')
          .replace('dd', 'DD')
          .replace('yyyy', 'YYYY')
        return dayjs(time).format(format)
      } else {
        return time
      }
    },
  },
})
</script>
<style lang="scss" scoped>
.eventrangklist {
  overflow: hidden;
  box-sizing: border-box;
  border-style: solid;
}
.eventrangklist ul {
  transition: all 0.5s;
}
.eventrangklist li {
  overflow: hidden;
  padding: 6px 12px;
  display: flex;
  flex-direction: column;
  border-bottom-style: solid;
  //margin-bottom:4px ;
}
.eventrangklist li .event {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
/deep/.div-svg {
  display: inline-block;
  vertical-align: middle;
  cursor: pointer;
  svg {
    width: 30px;
    height: 30px;
  }
}
</style>
