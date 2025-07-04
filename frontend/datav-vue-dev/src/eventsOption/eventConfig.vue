<template>
  <div class="eventConfig">
    <div>
      <span>动作：</span>
      <el-radio
        v-model="radioValue"
        v-for="(item, index) in eventList"
        :key="index"
        :label="item.type"
        class="select_video"
        >{{ item.label }}</el-radio
      >
      <!-- <el-radio v-model="radioValue" label="2">备选项</el-radio> -->
    </div>
    <eventLinkage :expansionStatus="expansionStatus"></eventLinkage>
    <div class="btnAdd">
      <el-button
        type="primary"
        icon="el-icon-arrow-down"
        @click="toggleExpansion"
      ></el-button>
    </div>
  </div>
</template>

<script>
  import eventLinkage from './components/eventLinkage'
  export default {
    data() {
      return {
        radioValue: 1,
        eventList: [{
          type: 1,
          label: "联动"
        }, {
          type: 2,
          label: "跳转"
        }, {
          type: 3,
          label: "服务调用"
        }, {
          type: 4,
          label: "弹出窗口"
        }, {
          type: 5,
          label: "关闭弹窗"
        }, {
          type: 6,
          label: "页面联动"
        }, {
          type: 7,
          label: "自定义"
        }],
        expansionStatus: false
      }
    },

    created() { },

    methods: {
      toggleExpansion() {
        this.expansionStatus = !this.expansionStatus
      }
    },
    components: {
      eventLinkage
    }
  }
</script>

<style scoped lang='scss'>
.eventConfig {
  width: 100%;
}
.btnAdd {
  width: 100%;
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
}
.select_video {
  width: auto;
}
</style>
