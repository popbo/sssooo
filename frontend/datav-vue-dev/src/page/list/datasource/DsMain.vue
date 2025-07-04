<template>
  <de-container>
    <drive-manage
      :dMVisible.sync="driveManageVisible"
      v-if="driveManageVisible"
    />
    <de-aside-container style="padding: 0 0">
      <ds-tree
        ref="dsTree"
        @switch-main="switchMain"
        :dMVisible.sync="driveManageVisible"
      />
    </de-aside-container>
    <de-main-container>
      <component
        :is="component"
        v-if="!!component"
        :params="param"
        @refresh-type="refreshType"
        @switch-component="switchMain"
      />
    </de-main-container>
  </de-container>
</template>

<script>
// 引入从dataEase项目复制过来的容器组件
import DeMainContainer from '@/components/dataease/DeMainContainer'
import DeContainer from '@/components/dataease/DeContainer'
import DeAsideContainer from '@/components/dataease/DeAsideContainer'
import DsTree from './DsTree'
import DsForm from './form'
import DataHome from './DataHome'
import DriveManage from './DriveManage.vue'

export default {
  data() {
    return {
      component: DataHome,
      param: null,
      driveManageVisible: false,
    }
  },

  created() {},

  methods: {
    // 切换main区内容
    switchMain(param) {
      const { component, componentParam } = param
      this.component = DataHome
      this.param = null
      this.$nextTick(() => {
        switch (component) {
          case 'DsForm':
            this.component = DsForm
            this.param = componentParam
            break
          default:
            this.component = DataHome
            this.param = null
            break
        }
      })
    },
    refreshType(datasource) {
      this.datasource = datasource
      console.log(this.datasource)
      this.$refs.dsTree && this.$refs.dsTree.refreshType(datasource)
    },
  },
  components: {
    DeMainContainer,
    DeContainer,
    DeAsideContainer,
    DsTree,
    DriveManage,
  },
}
</script>

<style scoped lang="scss" scoped>
.ms-aside-container {
  height: 100%;
  padding: 0px;
  min-width: 260px;
  max-width: 460px;
}
.ms-main-container {
  height: 100%;
  padding: 0px;
}
/deep/ input {
  background-color: transparent !important;
  border-color: #293241 !important;
  color: #bfbfbf !important;
}
</style>
