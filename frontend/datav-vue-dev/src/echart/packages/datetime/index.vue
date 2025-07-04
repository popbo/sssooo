<template>
  <div
    :class="b()"
    :style="styleSizeName"
    ref="main"
    @click="handleClick"
    @dblclick="handledblClick"
  >
    <div :style="styleName" v-if="option.isPortait">
      <p>{{ nowDateSplit[0] }}</p>
      <p>{{ nowDateSplit[1] }}</p>
    </div>
    <p v-else :style="styleName">{{ nowDate }}</p>
  </div>
</template>

<script>
import dayjs from 'dayjs'
import create from '../../create'
import 'dayjs/locale/zh-cn'
import localeData from 'dayjs/plugin/localeData'
dayjs.locale('zh-cn')
dayjs.extend(localeData)
export default create({
  name: 'datetime',
  data() {
    return {
      date: new Date(),
    }
  },
  watch: {
    'option.isPortait'(newVal) {
      if (newVal) {
        this.component.height = 72
      } else {
        this.component.height = 50
      }
    },
  },
  computed: {
    nowDate() {
      if (this.option.format === 'day') {
        return dayjs.weekdays()[dayjs().day()]
      }
      const format = (this.option.format || 'yyyy-MM-dd hh:mm:ss')
        .replace('dd', 'DD')
        .replace('yyyy', 'YYYY')
      const canReverse = ['YYYY-MM-DD hh:mm', 'YYYY-MM-DD hh:mm:ss']
      if (this.option.isReverse) {
        switch (format) {
          case canReverse[0]:
            return dayjs(this.date).format('hh:mm YYYY-MM-DD')
          case canReverse[1]:
            return dayjs(this.date).format('hh:mm:ss YYYY-MM-DD')

          default:
            return dayjs(this.date).format(format)
        }
      } else {
        return dayjs(this.date).format(format)
      }
    },
    styleName() {
      return {
        width: '100%',
        height: '100%',
        textAlign: this.option.textAlign,
        letterSpacing: this.setPx(this.option.split),
        textIndent: this.setPx(this.option.split),
        backgroundColor: this.option.backgroundColor,
        fontWeight: this.option.fontWeight || 'normal',
        fontSize: (this.option.fontSize || 30) + 'px',
        color: this.option.color || '#333',
      }
    },
    nowDateSplit() {
      if (this.option.isPortait) {
        return this.nowDate.split(' ')
      } else {
        return this.nowDate
      }
    },
  },
  created() {
    setInterval(() => {
      this.date = new Date()
    }, 1000)
  },
  props: {
    option: {
      type: Object,
      default: () => {
        return {}
      },
    },
  },
  methods: {
    handleClick() {
      this.clickFormatter &&
        this.clickFormatter(
          {
            data: this.dataChart,
          },
          this.getItemRefs()
        )
    },
    handledblClick() {
      this.dblClickFormatter &&
        this.dblClickFormatter(
          {
            data: this.dataChart,
          },
          this.getItemRefs()
        )
    },
  },
})
</script>
