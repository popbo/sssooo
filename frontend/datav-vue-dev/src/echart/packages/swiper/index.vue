<template>
  <div :class="b()" :style="styleSizeName" ref="main">
    <el-carousel
      ref="carousel"
      :type="type"
      :indicator-position="indicator"
      :interval="interval"
      :height="carouselHeight"
      :arrow="showArray"
      @change="changeItem"
      :loop="true"
    >
      <el-carousel-item
        v-for="(item, index) in dataChart"
        :key="index"
        @click="handleClick(item, index)"
        @dblclick="handleDblClick(item, index)"
      >
        <div :style="[containerStyle]" class="container" ref="container">
          <!-- 无轮播内容时占位 -->
          <div :style="[containerStyle]" v-if="!item.value"></div>
          <img
            v-if="typeList.img.test(item.value)"
            :src="item.value"
            draggable="false"
            :style="styleName"
          />
          <video
            muted
            v-bind="params"
            v-else-if="typeList.video.test(item.value)"
            :src="item.value"
            :style="styleName"
          ></video>
          <avue-echart-clapper
            v-else-if="item.type == 'hls'"
            :width="width"
            :height="height"
            :key="index"
            :data="{ value: item.value }"
            :option="hlsOption"
          ></avue-echart-clapper>
          <avue-echart-iframe
            v-else-if="item.type == 'iframe'"
            :width="width"
            :height="height"
            :data="{ value: item.value }"
          ></avue-echart-iframe>
          <div :style="[titleStyle]" v-if="option.showTitle"></div>
          <div
            :style="[titleTextStyle]"
            class="title-text"
            v-if="option.showTitle"
          >
            {{ item.title }}
          </div>
          <div
            class="indicators"
            v-if="option.indicator === 'type3' && type !== 'card'"
          >
            <span
              :class="{ indicatorItem: true, active: index === activeItem }"
              v-for="(item, index) in dataChart"
              :key="index"
              @click="handleActive(index)"
            ></span>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>
    <div
      class="indicators"
      v-if="option.indicator === 'type3' && type === 'card'"
    >
      <span
        :class="{ indicatorItem: true, active: index === activeItem }"
        v-for="(item, index) in dataChart"
        :key="index"
        @click="handleActive(index)"
      ></span>
    </div>
  </div>
</template>

<script>
import templatelist from '../../../page/list/resourceManage/templatelist.vue'
import create from '../../create'
import { setPx } from '../../util'
export default create({
  components: { templatelist },
  name: 'swiper',
  data() {
    return {
      typeList: {
        img: /\.(gif|jpg|jpeg|png|GIF|JPG|PNG|svg)/,
        video: /\.(swf|avi|flv|mpg|rm|mov|wav|asf|3gp|mkv|rmvb|ogg|mp4)/,
      },
      activeItem: 0,
    }
  },
  mounted() {
    console.log('swiper的this', this)
  },
  computed: {
    // 是否显示箭头
    showArray() {
      return this.option.indicator === 'inside' ? 'always' : 'never'
    },
    hlsOption() {
      return {
        autoplay: this.option.autoplay,
      }
    },
    params() {
      let result = {}
      if (this.option.controls) result.controls = 'controls'
      if (this.option.loop) result.loop = 'loop'
      if (this.option.autoplay) result.autoplay = 'autoplay'
      return result
    },
    // 轮播内容
    styleName() {
      return {
        width: '100%',
        height: this.option.showTitle ? '90%' : '100%',
        opacity: this.opacity,
      }
    },
    // 轮播标题样式
    titleStyle() {
      return {
        height: '10%',
        backgroundColor: this.option.showTitleBackground
          ? this.option.titleBackgroundColor
          : '',
        backgroundImage: `url(${this.option.titleBackgroundImage})`,
        opacity: this.option.titleOpcity / 100,
      }
    },
    titleTextStyle() {
      return {
        textAlign: this.option.titlePosition,
        color: this.option.textColor,
        fontSize: setPx(this.option.titleFontSize),
        fontFamily: this.option.titleTextFamily,
      }
    },
    // 轮播容器样式
    containerStyle() {
      return {
        width: '100%',
        height: '100%',
        display: ' flex',
        flexDirection: 'column',
      }
    },
    indicator() {
      if (this.option.indicator === 'type3') {
        return 'none'
      }
      return this.option.indicator === 'outside' ? 'outside' : 'none'
    },
    opacity() {
      return this.option.opacity * 0.01
    },
    type() {
      return this.option.type || ''
    },
    interval() {
      return this.option.interval || 5000
    },
    carouselHeight() {
      if (this.indicator === 'outside') {
        //如果控制器显示在外部要给显示器留出一定高度
        return this.height - 40 + 'px'
      } else {
        return this.height + 'px'
      }
    },
  },
  methods: {
    changeItem(e) {
      this.activeItem = e
    },
    handleClick(item, index) {
      this.clickFormatter &&
        this.clickFormatter(
          {
            type: index,
            item: item,
            value: item.value,
            data: this.dataChart,
          },
          this.getItemRefs()
        )
    },
    handleDblClick(item, index) {
      this.dblClickFormatter &&
        this.dblClickFormatter(
          {
            type: index,
            item: item,
            value: item.value,
            data: this.dataChart,
          },
          this.getItemRefs()
        )
    },
    handleActive(_index) {
      this.activeItem = _index
      this.$refs.carousel.setActiveItem(_index)
    },
  },
  props: {
    option: {
      type: Object,
      default: () => {
        return {}
      },
    },
  },
})
</script>
<style lang="scss" scoped>
.container {
  position: relative;
  .title-text {
    height: 10%;
    width: 100%;
    position: absolute;
    bottom: 0;
    left: 0;
  }
}
.indicators {
  position: absolute;
  left: 50%;
  top: 80%;
  transform: translateX(-50%);
  z-index: 100;
  .indicatorItem {
    display: inline-block;
    background-color: rgba($color: #999, $alpha: 0.9);
    margin: 0 5px;
    border-radius: 50%;
    width: 15px;
    height: 15px;
    text-align: center;
    z-index: 100;
  }
  .active {
    background-color: rgba($color: #fff, $alpha: 0.9);
  }
}
</style>
