<template>
  <div class="page-param">
    <div class="header">
      <span>参数列表</span>
      <el-button icon="el-icon-plus" circle size="mini" @click="addParams"></el-button>
    </div>
    <div>
      <el-table :data="tableData" border style="width: 100%" size='mini' height="250">
        <el-table-column label="序号" type="index" width="50">
        </el-table-column>
        <el-table-column prop="date" label="参数名" align='center'>
          <template slot-scope="{row}">
            <el-input v-model="row.paramsKey" size="mini"></el-input>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="参数值" align='center'>
          <template slot-scope="{row}">
            <el-input v-model="row.paramsValue" size="mini"></el-input>
          </template>
        </el-table-column>
        <el-table-column label="删除" align='center' width="80">
          <template slot-scope="{ $index }">
            <el-button icon="el-icon-delete" circle size="mini" @click="deleteParams($index)"></el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    pageParamsList: {
      type: Array,
      default: function() {
        return []
      }
    }
  },
  data() {
    return {
      tableData: [],
    }
  },

  created() {
    this.tableData = this.deepClone(this.pageParamsList)
  },

  methods: {
    addParams() {
      this.tableData.push({
        paramsKey: 'param',
        paramsValue: 'param',
      })
    },
    deleteParams(index) {
      this.tableData.splice(index, 1)
    }
  },
}
</script>

<style scoped lang='scss'>
.page-param {
  height: 300px;
}
.header {
  display: flex;
  justify-content: space-between;
  padding: 5px 10px;
}
</style>
