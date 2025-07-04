<template>
  <el-col>
    <el-row>
      <span style="font-weight: 500;">详情</span>
    </el-row>
    <el-col>
      <el-tabs v-if="tabStatus" v-model="tabActive" class="info-tab">
        <el-tab-pane v-if="type === 'chart' && detail.chart" label="视图" name="chart">
          <el-col class="info-item">
            <p class="info-title">名称</p>
            <p class="info-content">{{ detail.chart.name }}</p>
          </el-col>
          <el-col class="info-item">
            <p class="info-title">图表类型</p>
            <svg-icon v-if="detail.chart.type" :icon-class="detail.chart.type" />
          </el-col>
          <el-col class="info-item">
            <p class="info-title">标题</p>
            <p class="info-content">{{ detail.chart.title || 'N/A' }}</p>
          </el-col>
          <el-col class="info-item">
            <p class="info-title">创建者</p>
            <p class="info-content">{{ detail.chart.createBy }}</p>
          </el-col>
          <el-col class="info-item">
            <p class="info-title">创建日期</p>
            <p class="info-content">{{ detail.chart.createTime | timestampFormatDate }}</p>
          </el-col>
        </el-tab-pane>

        <el-tab-pane v-if="detail.table" label="数据集" name="table">
          <el-col class="info-item">
            <p class="info-title">名称</p>
            <p class="info-content">{{ detail.table.name }}</p>
          </el-col>
          <el-col class="info-item">
            <p class="info-title">类型</p>
            <p v-if="detail.table.type === 'db'" class="info-content">数据库数据集</p>
            <p v-if="detail.table.type === 'sql'" class="info-content">SQL数据集</p>
            <p v-if="detail.table.type === 'excel'" class="info-content">Excel 数据集</p>
            <p v-if="detail.table.type === 'custom'" class="info-content">自定义数据集</p>
            <p v-if="detail.table.type === 'union'" class="info-content">关联数据集</p>
          </el-col>
          <el-col v-show="detail.table.type === 'db'" class="info-item">
            <p class="info-title">表格</p>
            <p class="info-content">{{ info.table }}</p>
          </el-col>
          <el-col v-if="detail.table.type === 'db' || detail.table.type === 'sql'" class="info-item">
            <p class="info-title">模式</p>
            <p v-if="detail.table.mode === 0" class="info-content">直连</p>
            <p v-if="detail.table.mode === 1" class="info-content">定时同步</p>
          </el-col>
          <el-col class="info-item">
            <p class="info-title">创建者</p>
            <p class="info-content">{{ detail.table.createBy }}</p>
          </el-col>
          <el-col class="info-item">
            <p class="info-title">创建时间</p>
            <p class="info-content">{{ detail.table.createTime | timestampFormatDate }}</p>
          </el-col>
        </el-tab-pane>

        <el-tab-pane v-if="detail.datasource" label="数据源" name="datasource">
          <el-col class="info-item">
            <p class="info-title">名称</p>
            <p class="info-content">{{ detail.datasource.name }}</p>
          </el-col>
          <el-col class="info-item">
            <p class="info-title">描述</p>
            <p class="info-content">{{ detail.datasource.desc || 'N/A' }}</p>
          </el-col>
          <el-col class="info-item">
            <p class="info-title">类型</p>
            <p v-if="detail.datasource.type === 'mysql'" class="info-content">MySQL</p>
            <p v-if="detail.datasource.type === 'sqlServer'" class="info-content">SQL Server</p>
            <p v-if="detail.datasource.type === 'oracle'" class="info-content">Oracle</p>
            <p v-if="detail.datasource.type === 'hive'" class="info-content">Apache Hive</p>
            <p v-if="detail.datasource.type === 'pg'" class="info-content">PostgreSQL</p>
            <p v-if="detail.datasource.type === 'es'" class="info-content">Elasticsearch</p>
            <p v-if="detail.datasource.type === 'mariadb'" class="info-content">MariaDB</p>
            <p v-if="detail.datasource.type === 'ds_doris'" class="info-content">Doris</p>
            <p v-if="detail.datasource.type === 'ck'" class="info-content">ClickHouse</p>
            <p v-if="detail.datasource.type === 'redshift'" class="info-content">AWS Redshift</p>
            <p v-if="detail.datasource.type === 'mongo'" class="info-content">MongoDB</p>
          </el-col>
          <el-col class="info-item">
            <p class="info-title">创建时间</p>
            <p class="info-content">{{ detail.datasource.createTime | timestampFormatDate }}</p>
          </el-col>
        </el-tab-pane>
      </el-tabs>
    </el-col>
  </el-col>
</template>

<script>
import { post } from '@/api/dataCollection'
export default {
  name: 'DatasetChartDetail',
  props: {
    type: {
      type: String,
      required: true
    },
    data: {
      type: Object,
      required: true
    },
    tabStatus: {
      type: Boolean,
      required: true
    }
  },
  data() {
    return {
      tabActive: 'chart',
      detail: {
        chart: {},
        table: {},
        datasource: {}
      },
      info: {}
    }
  },
  watch: {
    'data': function() {
      this.init()
    },
    'type': function() {
      this.typeChange()
    },
    'tabStatus': function() {
      this.typeChange()
    }
  },
  mounted() {
    this.init()
  },
  methods: {
    init() {
      if (this.data.id) {
        if (this.type === 'dataset') {
          post('/dataset/table/datasetDetail/' + this.data.id, null).then(res => {
            this.detail = res.data.data
            this.info = JSON.parse(res.data.data.table.info)
          })
        } else if (this.type === 'chart') {
          post('/chart/view/chartDetail/' + this.data.id, null).then(res => {
            this.detail = res.data.data
            this.info = JSON.parse(res.data.data.table.info)
          })
        }
      }
    },
    typeChange() {
      if (this.type === 'dataset') {
        this.tabActive = 'table'
      } else if (this.type === 'chart') {
        this.tabActive = 'chart'
      }
    }
  }
}
</script>

<style scoped>
  .info-tab>>>.el-tabs__item{
    font-weight: 400;
    font-size: 12px;
  }
  .info-item{
     margin: 6px 0;
   }
  .info-title{
    margin: 0!important;
    font-weight: 600;
    font-size: 12px;
  }
  .info-content{
    margin: 0!important;
    font-size: 12px;
  }
</style>
