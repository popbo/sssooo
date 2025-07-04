<template>
  <div class="customs">
    <!-- <div class="customsTitle">
      <span>自定义选项</span>
      <i class="el-icon-close" @click="handleCustom(false)"></i>
    </div> -->
    <el-dialog :visible.sync="tipShow" width="20%" :modal="false">
      <div class="tip-content">
        <i class="el-icon-warning" style="color: red"></i>
        <span>存在重复或为空的选项，请检查修改</span>
      </div>
      <span slot="title" class="dialog-title">错误</span>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="tipShow = false" size="mini"
          >确 定</el-button
        >
      </span>
    </el-dialog>
    <div class="customsBody">
      <div class="bodyTitle">
        <span>自定义选项</span>
        <i class="el-icon-plus" @click="handleAdd" style="cursor: pointer"></i>
      </div>
      <el-table
        v-if="tableData.length > 0"
        :data="tableData"
        border
        style="width: 100%"
        :header-cell-style="{ background: '#3e3f4c' }"
        :cell-style="{ background: '#232630', userSelect: 'none' }"
        max-height="300"
      >
        >
        <el-table-column fixed label="默认">
          <template slot-scope="scope">
            <span v-if="currentComponent === 'checkbox'">
              <input
                type="checkbox"
                v-model="selected"
                :value="scope.row.id"
                style="cursor: pointer"
              />
            </span>
            <el-radio
              :label="scope.row.id"
              v-model="selected"
              v-else
            ></el-radio>
          </template>
        </el-table-column>
        <el-table-column label="标题" show-overflow-tooltip>
          <template slot-scope="scope">
            <el-input v-model.trim="scope.row.label"></el-input>
          </template>
        </el-table-column>
        <el-table-column label="选项值" show-overflow-tooltip>
          <template slot-scope="scope">
            <el-input v-model.trim="scope.row.value"></el-input>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="删除" width="60px">
          <template slot-scope="scope">
            <el-button
              @click="handleDel(scope.row, scope.$index)"
              type="text"
              size="small"
              icon="el-icon-delete-solid"
            />
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="customsFooter">
      <el-button type="primary" size="small" @click="handleModify"
        >确定</el-button
      >
      <el-button
        type="info"
        size="small"
        @click="handleCustom(false)"
        style="background: #303640; border: none"
        >取消</el-button
      >
    </div>
  </div>
</template>

<script>
import { deepClone } from '../../echart/util'
import { uuid } from '@/utils/utils.min.js'
export default {
  name: 'CustomOption',
  props: ['dataList', 'activeComponent', 'radioOption'],
  data() {
    return {
      tableData: [],
      // 单选情况下的选中值
      selected: '',
      currentComponent: this.activeComponent,
      tipShow: false,
    }
  },
  methods: {
    handleCustom(val) {
      this.$emit('customChange', val)
    },
    // 点击确定后修改数据
    handleModify() {
      // 传递最新的选中项
      this.$emit('selectedChange', this.selected)
      if (this.validateData(this.tableData)) {
        this.$emit('update:dataList', this.tableData)
        this.handleCustom(false)
      } else {
        // return this.$message.warning('标题或值不能为空')
        this.tipShow = true
      }
    },
    // 删除数据
    handleDel(val, index) {
      this.tableData.splice(index, 1)
    },
    handleAdd() {
      this.tableData.push({
        id: uuid(),
        label: '',
        value: '',
      })
    },
    validateData(data) {
      const valueLength = new Set(data.map(item => String(item.value))).size
      const labelLength = new Set(data.map(item => String(item.label))).size
      const listLength = data.length
      if (listLength > valueLength || listLength > labelLength) {
        return false
      }
      return data.every(item => item.label !== '' && item.value !== '')
    },
  },
  created() {
    this.tableData = deepClone(this.dataList)
    //第一次加载时，接收option配置项
    // if (this.currentComponent === 'radioMultiple') {
    //   this.selected = this.radioOption.selected
    // }
    this.selected = this.radioOption.selected
  },
}
</script>

<style lang="scss" scoped>
.customs {
  position: absolute;
  width: 100%;
  height: 100%;
  background: #232630;
  z-index: 999;
  .customsTitle {
    height: 35px;
    background-color: #3e3f4c;
    display: flex;
    justify-content: space-between;
    font-size: 14px;
    padding: 0 20px;
    > span {
      line-height: 35px;
    }
    > i {
      line-height: 35px;
      cursor: pointer;
    }
  }
  .customsBody {
    margin: 2px 0;
    max-height: 500px;
    .bodyTitle {
      width: 100%;
      height: 45px;
      display: flex;
      text-align: center;
      background-color: #3e3f4c;
      margin: 2px 0;
      > span {
        width: 80%;
        line-height: 45px;
        font-size: 14px;
      }
      > i {
        width: 20%;
        line-height: 45px;
        font-weight: 900;
      }
    }
  }
  .customsFooter {
    padding: 10px 0;
    display: flex;
    justify-content: center;
  }
}
.tip-content {
  display: flex;
  justify-content: center;
  align-items: center;
  > i {
    font-size: 30px !important;
    margin: 0 10px !important;
  }
  > span {
    color: rgba(255, 255, 255, 0.7);
  }
}
::v-deep .el-table tbody tr:hover > td {
  background-color: transparent !important;
}
::v-deep .cell {
  text-align: center;
}
::v-deep .el-table--group {
  border: none;
}
::v-deep .el-table td,
.el-table th.is-leaf {
  border-bottom: 1px solid #363841 !important;
}
::v-deep .el-radio__label {
  display: none !important;
}
::v-deep .el-table::before {
  z-index: 100 !important;
}
</style>
