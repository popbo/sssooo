<template>
  <de-container>
    <de-aside-container>
      <group :save-status="saveStatus" @switchComponent="switchComponent"></group>
    </de-aside-container>
    <de-main-container>
      <component :is="component" ref="dynamic_component" :param="param" @switchComponent="switchComponent" @saveSuccess="saveSuccess" />
    </de-main-container>
  </de-container>
</template>

<script>
import DeMainContainer from '@/components/dataease/DeMainContainer'
import DeContainer from '@/components/dataease/DeContainer'
import DeAsideContainer from '@/components/dataease/DeAsideContainer'
import Group from './group/Group'
import DataHome from './data/DataHome'
import AddDB from './add/AddDB'
import AddSQL from './add/AddSQL'
import AddExcel from './add/AddExcel'
import AddCustom from './add/AddCustom'
import AddUnion from './add/AddUnion'
import ViewTable from './data/ViewTable'
export default {
  data() {
    return {
      component: DataHome,
      param: {},
      saveStatus: null,
    }
  },

  created() {},

  methods: {
    switchComponent(c) {
      this.param = c.param
      switch (c.name) {
        case 'ViewTable':
          this.component = ViewTable
          break
        case 'AddDB':
          this.component = AddDB
          break
        case 'AddSQL':
          this.component = AddSQL
          break
        case 'AddExcel':
          this.component = AddExcel
          break
        case 'AddCustom':
          this.component = AddCustom
          break
        case 'AddUnion':
          this.component = AddUnion
          break
        case 'FieldEdit':
          this.component = FieldEdit
          break
        default:
          this.component = DataHome
          break
      }
    },
    saveSuccess(val) {
      this.saveStatus = val
    },
  },
  components: {
    DeMainContainer,
    DeContainer,
    DeAsideContainer,
    Group,
    DataHome,
    AddDB,
    AddSQL,
    AddExcel,
    AddCustom
  },
}
</script>

<style scoped lang='scss'>
.ms-aside-container {
  height: 100%;
  padding: 0 0;
  min-width: 260px;
  max-width: 460px;
}

.ms-main-container {
  height: 100%;
  padding: 10px 15px 0 15px;
}
// /deep/ input {
//   background-color: #fff !important;
//   border-color: #c0c4cc !important;
//   color: #333 !important;
// }

</style>
