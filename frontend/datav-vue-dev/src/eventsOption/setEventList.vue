<template>
  <div class="settingeventlis">
    <div class="settingeventlistBox">
      <span>事件列表</span>
      <div class="tools">
        <i class="el-icon-caret-top" @click="moveUp" title=""></i>
        <i class="el-icon-caret-bottom" @click="moveDown"></i>
        <i class="el-icon-document-copy"></i>
        <i class="el-icon-plus"></i>
      </div>
    </div>
    <div class="settingeventlistSearch">
      <el-input placeholder="请输入事件类型" v-model="eventName">
        <i slot="suffix" class="el-input__icon el-icon-search"></i>
      </el-input>
    </div>
    <div class="ev-set-setting-list">
      <ul>
        <li v-for="(item, index) in eventList" :key="index" @click="selectEvent(index)" :class="{ selected: currentIndex === index }"><span>{{ item }}</span><i class="el-icon-delete"></i></li>
      </ul>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    eventList: {
      default: Array
    },
    currentIndex: Number
  },
  data() {
    return {
      eventName: '',
      // eventList: ['联动', '跳转', '服务调用', '弹出窗口', '关闭弹窗', '页面联动', '定义'],
      // currentIndex: Number,
    }
  },

  created() {},

  methods: {
    // 选中列表中某一个事件名
    selectEvent(index) {
      // this.currentIndex = index
      this.$emit('update:currentIndex', index)
    },
    // 点击上移按钮
    moveUp() {
      let isNumber = typeof this.currentIndex === "number"
      // 确保currentIndex已经赋值，并且大于0
      if (isNumber && this.currentIndex > 0) {
        // this.currentIndex --
        this.$emit('update:currentIndex', this.currentIndex - 1)
      }
    },
    // 点击下移按钮
    moveDown() {
      let isNumber = typeof this.currentIndex === "number"
      // 确保currentIndex已经赋值，并且小于数组的最后一个下标
      if (isNumber && this.currentIndex < this.eventList.length - 1) {
        // this.currentIndex ++
        this.$emit('update:currentIndex', this.currentIndex + 1)
      }
    }
  },
}
</script>

<style scoped lang='scss'>
.settingeventlis {
  border: 1px solid#4f9eff;
  width: 210px;
  height: 500px;
  .settingeventlistBox {
    width: 100%;
    height: 30px;
    line-height: 30px;
    padding: 0 10px;
    overflow: hidden;
    text-overflow: ellipsis;
    background-color: #409eff;
    color: #fff;
    .tools {
      float: right;
      height: 30px;
      line-height: 30px;
      text-indent: 6px;
      i {
        margin: 0 5px;
        cursor: pointer;
      }
    }
  }
  .settingeventlistSearch {
    background-color: #fff;
  }
  .ev-set-setting-list {
    height: 410px;
    ul {
      padding: 0 5px;
      li {
        cursor: pointer;
        padding-left: 10px;
        margin: 5px 0;
        line-height: 25px;
        height: 25px;
        position: relative;
        span {
          overflow: hidden;
          white-space: nowrap;
          text-overflow: ellipsis;
          width: 170px;
          height: 100%;
          display: inline-block;
          line-height: 25px;
        }
        i {
          width: 18px;
          height: 18px;
          color: #fff;
          display: none;
        }
        &:hover {
          background-color: rgba(64, 158, 255, 0.4);
          color: #fff;
          i {
            display: inline-block;
            position: absolute;
            top: calc(50% - 7px);
            right: 10px;
          }
        }
      }
    }
  }
}
.selected {
  background-color: #409eff;
  color: #fff;
}
</style>
