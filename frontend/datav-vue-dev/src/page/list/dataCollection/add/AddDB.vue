<template>
  <el-row style="display: flex;flex-direction: column;height: 100%">
    <el-row style="height: 26px;" class="title-text">
      <span style="line-height: 26px;color:#bfbfbf">
        添加数据库数据集
      </span>
      <el-row style="float: right">
        <el-button size="mini" @click="cancel">
          取消
        </el-button>
        <el-button size="mini" type="primary" :disabled="checkTableList.length < 1" @click="save">
          确认
        </el-button>
      </el-row>
    </el-row>
    <el-divider />
    <el-row>
      <el-form :inline="true">
        <el-form-item class="form-item">
          <el-select v-model="dataSource" filterable :placeholder="'请选择数据库数据集'" size="mini">
            <el-option
              v-for="item in options"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item class="form-item">
          <el-select v-model="mode" filterable placeholder="连接模式" size="mini">
            <el-option label="直连" value="0" />
            <el-option label="定时同步" value="1" :disabled="!kettleRunning || selectedDatasource.type==='es' || selectedDatasource.type==='ck' || selectedDatasource.type==='mongo' || selectedDatasource.type==='redshift' || selectedDatasource.type==='hive'" />
          </el-select>
        </el-form-item>

        <el-form-item v-if="mode === '1'" class="form-item">
          <el-select v-model="syncType" filterable placeholder="连接模式" size="mini">
            <el-option label="立即更新" value="sync_now" />
            <el-option label="稍后同步" value="sync_latter" />
          </el-select>
        </el-form-item>

        <el-form-item class="form-item" style="float: right;">
          <el-input
            v-model="searchTable"
            size="mini"
            placeholder="搜索"
            prefix-icon="el-icon-search"
            clearable
          />
        </el-form-item>
      </el-form>
    </el-row>
    <el-col style="overflow-y: auto;">
      <el-checkbox-group v-model="checkTableList" size="small">
        <el-tooltip v-for="t in tableData" :key="t.name" :disabled="t.enableCheck" effect="dark" :content="'该表已添加至'+': '+t.datasetPath" placement="bottom">
          <el-checkbox
            border
            :label="t.name"
            :disabled="!t.enableCheck"
          />
        </el-tooltip>
      </el-checkbox-group>
    </el-col>
  </el-row>
</template>

<script>
// import { listDatasource, post, isKettleRunning } from '@/api/dataset/dataset'
import { listDatasource, post } from '@/api/dataCollection'

export default {
  name: 'AddDB',
  props: {
    param: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      searchTable: '',
      options: [],
      dataSource: '',
      tables: [],
      checkTableList: [],
      mode: '0',
      syncType: 'sync_now',
      tableData: [],
      kettleRunning: true,
      selectedDatasource: {}
    }
  },
  watch: {
    dataSource(val) {
      if (val) {
        post('/datasource/getTables', { id: val }).then(response => {
          this.tables = response.data.data
          this.tableData = JSON.parse(JSON.stringify(this.tables))
        })
        for (let i = 0; i < this.options.length; i++) {
          if (this.options[i].id === val) {
            this.selectedDatasource = this.options[i]
            this.mode = '0'
          }
        }
      }
    },
    searchTable(val) {
      if (val && val !== '') {
        this.tableData = JSON.parse(JSON.stringify(this.tables.filter(ele => { return ele.name.toLocaleLowerCase().includes(val.toLocaleLowerCase()) })))
      } else {
        this.tableData = JSON.parse(JSON.stringify(this.tables))
      }
    }
  },
  mounted() {
    this.initDataSource()
  },
  activated() {
    this.initDataSource()
  },
  created() {
    // this.kettleState()
  },
  methods: {
    initDataSource() {
      listDatasource().then(response => {
        this.options = response.data.data
      })
    },
    kettleState() {
      isKettleRunning().then(res => {
        this.kettleRunning = res.data
      })
    },
    save() {
      let ds = {}
      this.options.forEach(ele => {
        if (ele.id === this.dataSource) {
          ds = ele
        }
      })
      const sceneId = this.param.id
      const dataSourceId = this.dataSource
      const tables = []
      const mode = this.mode
      const syncType = this.syncType
      this.checkTableList.forEach(function(name) {
        tables.push({
          name: ds.name + '_' + name,
          sceneId: sceneId,
          dataSourceId: dataSourceId,
          type: 'db',
          syncType: syncType,
          mode: parseInt(mode),
          info: JSON.stringify({ table: name })
        })
      })
      post('/dataset/table/batchAdd', tables).then(response => {
        this.$emit('saveSuccess', tables[0])
        this.cancel()
      })
    },

    cancel() {
      this.dataReset()
      this.$emit('switchComponent', { name: '' })
    },

    dataReset() {
      this.searchTable = ''
      this.options = []
      this.dataSource = ''
      this.tables = []
      this.checkTableList = []
    }
  }

}
</script>

<style scoped>
  .el-divider--horizontal {
    margin: 12px 0;
    background-color: #293241;
  }

  .form-item {
    margin-bottom: 6px;
  }

  .el-checkbox {
    margin-bottom: 14px;
    margin-left: 0;
    margin-right: 14px;
  }
.el-checkbox.is-bordered{
  border:1px solid #293241
}
  .el-checkbox.is-bordered + .el-checkbox.is-bordered {
    margin-left: 0;
  }

  span{
    font-size: 14px;
  }
</style>
