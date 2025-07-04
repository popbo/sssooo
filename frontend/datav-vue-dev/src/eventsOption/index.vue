<template>
  <el-dialog title="事件设置" :visible.sync="eventOptiondialogVisible" width="80%" :before-close="handleClose">
    <div>
      <div class="event-setting-main">
        <div class="ev-set-top">
          <span>鼠标左键单击</span>
        </div>
        <div>
          <el-row :gutter="20">
            <el-col :span="4">
              <setEventList :eventList="eventList" :currentIndex.sync="currentIndex"></setEventList>
            </el-col>
            <el-col :span="20">
              <div class="eventConfigBox" ref="eventConfigBox">
                <eventConfig v-for="(item, index) in eventList" :key="index" :ref="'eventConfig' + index"></eventConfig>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="setVisible(false)">取 消</el-button>
      <el-button type="primary" @click="eventOptiondialogVisible = false">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import setEventList from './setEventList'
import eventConfig from './eventConfig'
export default {
  props: {
    eventOptiondialogVisible: Boolean,
  },
  data() {
    return {
      eventList: ['联动', '跳转', '服务调用', '弹出窗口', '关闭弹窗', '页面联动', '定义'],
      currentIndex: Number // 当前选中的事件
    }
  },

  created() {},

  methods: {
    handleClose() {
      this.setVisible(false)
    },
    setVisible(val) {
      this.$emit('update:eventOptiondialogVisible', val)
    },
  },
  watch: {
    currentIndex: {
      handler: function(newVal, oldVal) {
        // 根据当前的index获取到当前的refs值
        let currentEventConfig = 'eventConfig' + newVal
        // 获取当前dom元素的offsetTop值
        let offsetTop = this.$refs[currentEventConfig][0].$el.offsetTop
        // 把当前dom元素距离顶部的距离赋值给滚动属性并减去上部的内边距
        this.$refs.eventConfigBox.scrollTop = offsetTop - 6
      }
    }
  },
  components: {
    setEventList,
    eventConfig,
  },
}
</script>

<style scoped lang='scss'>
.event-setting-main {
  margin-left: 80px;
}
.ev-set-top {
  margin-bottom: 10px;
  span {
    font-size: 20px;
    color: #fff;
  }
}
.eventConfigBox {
  width: 100%;
  height: 500px;
  overflow: scroll;
  padding: 5px 5px;
  border: 1px solid#4f9eff;
  scroll-behavior:smooth
}
.eventConfig {
  width: 100%;
}
</style>
