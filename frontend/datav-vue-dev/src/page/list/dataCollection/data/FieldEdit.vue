<template>
  <el-row :style="{ height: maxHeight, overflow: 'auto' }">
    <!--    <el-row style="height: 26px;">-->
    <!--      <span style="line-height: 26px;">-->
    <!--        {{ $t('dataset.field_edit') }}-->
    <!--        <span>{{ param.name }}</span>-->
    <!--      </span>-->
    <!--      <el-row style="float: right">-->
    <!--        <el-button size="mini" @click="closeEdit">{{ $t('dataset.cancel') }}</el-button>-->
    <!--        <el-button type="primary" size="mini" @click="saveEdit">{{ $t('dataset.confirm') }}</el-button>-->
    <!--      </el-row>-->
    <!--    </el-row>-->
    <!--    <el-divider />-->
    <el-row>
      <el-form :inline="true">
        <el-form-item class="form-item">
          <el-button
            type="primary"
            v-if="hasDataPermission('manage', param.privileges)"
            size="mini"
            icon="el-icon-circle-plus-outline"
            @click="addCalcField"
            >新建计算字段</el-button
          >
          <el-button
            type="primary"
            v-if="
              hasDataPermission('manage', param.privileges) &&
              table.type !== 'excel' &&
              table.type !== 'custom' &&
              table.type !== 'union'
            "
            size="mini"
            :loading="isSyncField"
            icon="el-icon-refresh-left"
            @click="syncField"
            >同步字段</el-button
          >
        </el-form-item>
        <el-form-item class="form-item" style="float: right; margin-right: 0">
          <el-input
            v-model="searchField"
            size="mini"
            placeholder="搜索"
            prefix-icon="el-icon-search"
            clearable
            class="main-area-input"
          />
        </el-form-item>
      </el-form>
    </el-row>

    <el-collapse v-model="fieldActiveNames" class="style-collapse">
      <el-collapse-item name="d" title="维度">
        <el-table border :data="tableFields.dimensionListData" size="mini">
          <el-table-column property="checked" label="选中" width="60">
            <template slot-scope="scope">
              <el-checkbox
                v-model="scope.row.checked"
                :disabled="!hasDataPermission('manage', param.privileges)"
                @change="saveEdit(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column property="name" label="字段名" width="180">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.name"
                size="mini"
                :disabled="!hasDataPermission('manage', param.privileges)"
                @blur="saveEdit(scope.row)"
                @keyup.enter.native="saveEdit(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column
            v-if="
              !(
                (table.mode === 0 && table.type === 'custom') ||
                table.type === 'union'
              )
            "
            property="originName"
            label="原始名"
            width="100"
          >
            <template slot-scope="scope">
              <span
                v-if="scope.row.extField === 0"
                :title="scope.row.originName"
                class="field-class"
                style="
                  width: 100%;
                  white-space: nowrap;
                  overflow: hidden;
                  text-overflow: ellipsis;
                "
              >
                <span style="font-size: 12px">{{ scope.row.originName }}</span>
              </span>
              <span
                v-else-if="scope.row.extField === 2"
                title="计算字段"
                class="field-class"
                style="
                  width: 100%;
                  white-space: nowrap;
                  overflow: hidden;
                  text-overflow: ellipsis;
                "
              >
                <span style="font-size: 12px; color: #c0c0c0">计算字段</span>
              </span>
            </template>
          </el-table-column>
          <el-table-column property="deType" label="字段类型" width="140">
            <template slot-scope="scope">
              <el-select
                v-model="scope.row.deType"
                size="mini"
                style="display: inline-block; width: 26px"
                :disabled="!hasDataPermission('manage', param.privileges)"
                @change="saveEdit(scope.row)"
              >
                <el-option
                  v-for="item in fields"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                  <span style="float: left">
                    <svg-icon
                      v-if="item.value === 0"
                      icon-class="field_text"
                      class="field-icon-text"
                    />
                    <svg-icon
                      v-if="item.value === 1"
                      icon-class="field_time"
                      class="field-icon-time"
                    />
                    <svg-icon
                      v-if="item.value === 2 || item.value === 3"
                      icon-class="field_value"
                      class="field-icon-value"
                    />
                    <svg-icon
                      v-if="item.value === 5"
                      icon-class="field_location"
                      class="field-icon-location"
                    />
                  </span>
                  <span style="float: left; font-size: 12px">{{
                    item.label
                  }}</span>
                </el-option>
              </el-select>
              <span style="margin-left: 8px">
                <span v-if="scope.row.deType === 0">
                  <svg-icon
                    v-if="scope.row.deType === 0"
                    icon-class="field_text"
                    class="field-icon-text"
                  />
                  <span class="field-class">文本</span>
                </span>
                <span v-if="scope.row.deType === 1">
                  <svg-icon
                    v-if="scope.row.deType === 1"
                    icon-class="field_time"
                    class="field-icon-time"
                  />
                  <span class="field-class">时间</span>
                </span>
                <span v-if="scope.row.deType === 2 || scope.row.deType === 3">
                  <svg-icon
                    v-if="scope.row.deType === 2 || scope.row.deType === 3"
                    icon-class="field_value"
                    class="field-icon-value"
                  />
                  <span v-if="scope.row.deType === 2" class="field-class"
                    >数值</span
                  >
                  <span v-if="scope.row.deType === 3" class="field-class">{{
                    '数值' + '(' + '小数' + ')'
                  }}</span>
                </span>
                <span v-if="scope.row.deType === 5">
                  <svg-icon
                    v-if="scope.row.deType === 5"
                    icon-class="field_location"
                    class="field-icon-location"
                  />
                  <span class="field-class">地理位置</span>
                </span>
              </span>
            </template>
          </el-table-column>
          <el-table-column
            property="deExtractType"
            label="原始类型"
            width="100"
          >
            <template slot-scope="scope">
              <span v-if="scope.row.extField === 0">
                <span v-if="scope.row.deExtractType === 0">
                  <svg-icon
                    v-if="scope.row.deExtractType === 0"
                    icon-class="field_text"
                    class="field-icon-text"
                  />
                  <span class="field-class">文本</span>
                </span>
                <span v-if="scope.row.deExtractType === 1">
                  <svg-icon
                    v-if="scope.row.deExtractType === 1"
                    icon-class="field_time"
                    class="field-icon-time"
                  />
                  <span class="field-class">时间</span>
                </span>
                <span
                  v-if="
                    scope.row.deExtractType === 2 ||
                    scope.row.deExtractType === 3 ||
                    scope.row.deExtractType === 4
                  "
                >
                  <svg-icon
                    v-if="
                      scope.row.deExtractType === 2 ||
                      scope.row.deExtractType === 3 ||
                      scope.row.deExtractType === 4
                    "
                    icon-class="field_value"
                    class="field-icon-value"
                  />
                  <span
                    v-if="
                      scope.row.deExtractType === 2 ||
                      scope.row.deExtractType === 4
                    "
                    class="field-class"
                    >数值</span
                  >
                  <span
                    v-if="scope.row.deExtractType === 3"
                    class="field-class"
                    >{{ '数值' + '(' + '小数' + ')' }}</span
                  >
                </span>
                <span v-if="scope.row.deExtractType === 5">
                  <svg-icon
                    v-if="scope.row.deExtractType === 5"
                    icon-class="field_location"
                    class="field-icon-location"
                  />
                  <span class="field-class">地理位置</span>
                </span>
              </span>
              <span
                v-else-if="scope.row.extField === 2"
                title="计算字段"
                class="field-class"
                style="
                  width: 100%;
                  white-space: nowrap;
                  overflow: hidden;
                  text-overflow: ellipsis;
                "
              >
                <span style="font-size: 12px; color: #c0c0c0">计算字段</span>
              </span>
            </template>
          </el-table-column>
          <!--          <el-table-column property="groupType" :label="$t('dataset.field_group_type')" width="180">-->
          <!--            <template slot-scope="scope">-->
          <!--              <el-radio-group v-model="scope.row.groupType" size="mini">-->
          <!--                <el-radio-button label="d">{{ $t('chart.dimension') }}</el-radio-button>-->
          <!--                <el-radio-button label="q">{{ $t('chart.quota') }}</el-radio-button>-->
          <!--              </el-radio-group>-->
          <!--            </template>-->
          <!--          </el-table-column>-->
          <el-table-column
            property="groupType"
            label="维度/指标转换"
            width="120"
          >
            <template slot-scope="scope">
              <el-button
                icon="el-icon-sort"
                size="mini"
                circle
                :disabled="!hasDataPermission('manage', param.privileges)"
                @click="dqTrans(scope.row, 'd')"
              />
            </template>
          </el-table-column>
          <el-table-column property="" label="操作">
            <template slot-scope="scope">
              <el-button
                v-if="scope.row.extField !== 0"
                :disabled="!hasDataPermission('manage', param.privileges)"
                type="text"
                size="mini"
                @click="editField(scope.row)"
                >编辑</el-button
              >
              <el-button
                v-if="scope.row.extField !== 0"
                :disabled="!hasDataPermission('manage', param.privileges)"
                type="text"
                size="mini"
                @click="deleteField(scope.row)"
                >删除</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </el-collapse-item>

      <el-collapse-item name="q" title="指标">
        <el-table :data="tableFields.quotaListData" size="mini">
          <el-table-column property="checked" label="选中" width="60">
            <template slot-scope="scope">
              <el-checkbox
                v-model="scope.row.checked"
                :disabled="!hasDataPermission('manage', param.privileges)"
                @change="saveEdit(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column property="name" label="字段名" width="180">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.name"
                size="mini"
                :disabled="!hasDataPermission('manage', param.privileges)"
                @blur="saveEdit(scope.row)"
                @keyup.enter.native="saveEdit(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column
            v-if="
              !(
                (table.mode === 0 && table.type === 'custom') ||
                table.type === 'union'
              )
            "
            property="originName"
            label="原始名"
            width="100"
          >
            <template slot-scope="scope">
              <span
                v-if="scope.row.extField === 0"
                :title="scope.row.originName"
                class="field-class"
                style="
                  width: 100%;
                  white-space: nowrap;
                  overflow: hidden;
                  text-overflow: ellipsis;
                "
              >
                <span style="font-size: 12px">{{ scope.row.originName }}</span>
              </span>
              <span
                v-else-if="scope.row.extField === 2"
                title="计算字段"
                class="field-class"
                style="
                  width: 100%;
                  white-space: nowrap;
                  overflow: hidden;
                  text-overflow: ellipsis;
                "
              >
                <span style="font-size: 12px; color: #c0c0c0">计算字段</span>
              </span>
            </template>
          </el-table-column>
          <el-table-column property="deType" label="字段类型" width="140">
            <template slot-scope="scope">
              <el-select
                v-model="scope.row.deType"
                size="mini"
                style="display: inline-block; width: 26px"
                :disabled="!hasDataPermission('manage', param.privileges)"
                @change="saveEdit(scope.row)"
              >
                <el-option
                  v-for="item in fields"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                  <span style="float: left">
                    <svg-icon
                      v-if="item.value === 0"
                      icon-class="field_text"
                      class="field-icon-text"
                    />
                    <svg-icon
                      v-if="item.value === 1"
                      icon-class="field_time"
                      class="field-icon-time"
                    />
                    <svg-icon
                      v-if="item.value === 2 || item.value === 3"
                      icon-class="field_value"
                      class="field-icon-value"
                    />
                    <svg-icon
                      v-if="item.value === 5"
                      icon-class="field_location"
                      class="field-icon-location"
                    />
                  </span>
                  <span style="float: left; font-size: 12px">{{
                    item.label
                  }}</span>
                </el-option>
              </el-select>
              <span style="margin-left: 8px">
                <span v-if="scope.row.deType === 0">
                  <svg-icon
                    v-if="scope.row.deType === 0"
                    icon-class="field_text"
                    class="field-icon-text"
                  />
                  <span class="field-class">文本</span>
                </span>
                <span v-if="scope.row.deType === 1">
                  <svg-icon
                    v-if="scope.row.deType === 1"
                    icon-class="field_time"
                    class="field-icon-time"
                  />
                  <span class="field-class">时间</span>
                </span>
                <span v-if="scope.row.deType === 2 || scope.row.deType === 3">
                  <svg-icon
                    v-if="scope.row.deType === 2 || scope.row.deType === 3"
                    icon-class="field_value"
                    class="field-icon-value"
                  />
                  <span v-if="scope.row.deType === 2" class="field-class"
                    >数值</span
                  >
                  <span v-if="scope.row.deType === 3" class="field-class">{{
                    '数值' + '(' + '小数' + ')'
                  }}</span>
                </span>
                <span v-if="scope.row.deType === 5">
                  <svg-icon
                    v-if="scope.row.deType === 5"
                    icon-class="field_location"
                    class="field-icon-location"
                  />
                  <span class="field-class">地理位置</span>
                </span>
              </span>
            </template>
          </el-table-column>
          <el-table-column
            property="deExtractType"
            label="原始类型"
            width="100"
          >
            <template slot-scope="scope">
              <span v-if="scope.row.extField === 0">
                <span v-if="scope.row.deExtractType === 0">
                  <svg-icon
                    v-if="scope.row.deExtractType === 0"
                    icon-class="field_text"
                    class="field-icon-text"
                  />
                  <span class="field-class">文本</span>
                </span>
                <span v-if="scope.row.deExtractType === 1">
                  <svg-icon
                    v-if="scope.row.deExtractType === 1"
                    icon-class="field_time"
                    class="field-icon-time"
                  />
                  <span class="field-class">时间</span>
                </span>
                <span
                  v-if="
                    scope.row.deExtractType === 2 ||
                    scope.row.deExtractType === 3 ||
                    scope.row.deExtractType === 4
                  "
                >
                  <svg-icon
                    v-if="
                      scope.row.deExtractType === 2 ||
                      scope.row.deExtractType === 3 ||
                      scope.row.deExtractType === 4
                    "
                    icon-class="field_value"
                    class="field-icon-value"
                  />
                  <span
                    v-if="
                      scope.row.deExtractType === 2 ||
                      scope.row.deExtractType === 4
                    "
                    class="field-class"
                    >数值</span
                  >
                  <span
                    v-if="scope.row.deExtractType === 3"
                    class="field-class"
                    >{{ '数值' + '(' + '小数' + ')' }}</span
                  >
                </span>
                <span v-if="scope.row.deExtractType === 5">
                  <svg-icon
                    v-if="scope.row.deExtractType === 5"
                    icon-class="field_location"
                    class="field-icon-location"
                  />
                  <span class="field-class">地理位置</span>
                </span>
              </span>
              <span
                v-else-if="scope.row.extField === 2"
                title="计算字段"
                class="field-class"
                style="
                  width: 100%;
                  white-space: nowrap;
                  overflow: hidden;
                  text-overflow: ellipsis;
                "
              >
                <span style="font-size: 12px; color: #c0c0c0">计算字段</span>
              </span>
            </template>
          </el-table-column>
          <!--          <el-table-column property="groupType" :label="$t('dataset.field_group_type')" width="180">-->
          <!--            <template slot-scope="scope">-->
          <!--              <el-radio-group v-model="scope.row.groupType" size="mini">-->
          <!--                <el-radio-button label="d">{{ $t('chart.dimension') }}</el-radio-button>-->
          <!--                <el-radio-button label="q">{{ $t('chart.quota') }}</el-radio-button>-->
          <!--              </el-radio-group>-->
          <!--            </template>-->
          <!--          </el-table-column>-->
          <el-table-column
            property="groupType"
            label="维度/指标转换"
            width="120"
          >
            <template slot-scope="scope">
              <el-button
                icon="el-icon-sort"
                size="mini"
                circle
                :disabled="!hasDataPermission('manage', param.privileges)"
                @click="dqTrans(scope.row, 'q')"
              />
            </template>
          </el-table-column>
          <el-table-column property="" label="操作">
            <template slot-scope="scope">
              <el-button
                v-if="scope.row.extField !== 0"
                :disabled="!hasDataPermission('manage', param.privileges)"
                type="text"
                size="mini"
                @click="editField(scope.row)"
                >编辑</el-button
              >
              <el-button
                v-if="scope.row.extField !== 0"
                :disabled="!hasDataPermission('manage', param.privileges)"
                type="text"
                size="mini"
                @click="deleteField(scope.row)"
                >删除</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </el-collapse-item>
    </el-collapse>

    <el-dialog
      v-dialogDrag
      :visible="editCalcField"
      :show-close="false"
      class="dialog-css"
      :title="currEditField.id ? '编辑计算字段' : '新建计算字段'"
      append-to-body
    >
      <calc-field-edit
        :param="param"
        :table-fields="tableFields"
        :field="currEditField"
        @onEditClose="closeCalcField"
      />
    </el-dialog>
  </el-row>
</template>

<script>
import { post, fieldListDQ } from '@/api/dataCollection'
import CalcFieldEdit from './CalcFieldEdit'
export default {
  name: 'FieldEdit',
  components: { CalcFieldEdit },
  props: {
    param: {
      type: Object,
      required: true,
    },
    table: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      maxHeight: 'auto',
      tableFields: {
        dimensionList: [],
        quotaList: [],
        dimensionListData: [],
        quotaListData: [],
      },
      fields: [
        { label: '文本', value: 0 },
        { label: '时间', value: 1 },
        { label: '数值', value: 2 },
        { label: '数值' + '(' + '小数' + ')', value: 3 },
        { label: '地理位置', value: 5 },
      ],
      fieldActiveNames: ['d', 'q'],
      searchField: '',
      editCalcField: false,
      currEditField: {},
      isSyncField: false,
    }
  },
  watch: {
    param: function () {
      this.initField()
    },
    searchField(val) {
      this.filterField(val)
    },
  },
  mounted() {
    window.onresize = () => {
      this.calcHeight()
    }
    this.calcHeight()
    this.initField()
  },
  methods: {
    hasDataPermission() {
      return true
    },
    calcHeight() {
      const that = this
      setTimeout(function () {
        const currentHeight = document.documentElement.clientHeight
        that.maxHeight = currentHeight - 56 - 30 - 35 - 26 - 10 - 10 + 'px'
      }, 10)
    },
    initField() {
      fieldListDQ(this.param.id).then(response => {
        this.tableFields = response.data.data
        this.tableFields.dimensionListData = JSON.parse(
          JSON.stringify(this.tableFields.dimensionList)
        )
        this.tableFields.quotaListData = JSON.parse(
          JSON.stringify(this.tableFields.quotaList)
        )
        this.filterField(this.searchField)
      })
    },
    saveEdit(item) {
      // console.log(this.tableFields)
      // const list = this.tableFields.dimensionListData.concat(this.tableFields.quotaListData)
      // batchEdit(list).then(response => {
      //   // this.closeEdit()
      //   this.initField()
      // })
      if (item.name && item.name.length > 50) {
        this.$message.error('字段名不能超过50个字符')
        return
      }

      post('/dataset/field/save', item).then(response => {
        this.initField()
      })
    },

    dqTrans(item, val) {
      if (val === 'd') {
        item.groupType = 'q'
      } else if (val === 'q') {
        item.groupType = 'd'
      }
      this.saveEdit(item)
    },

    addCalcField() {
      this.currEditField = {}
      this.editCalcField = true
    },

    closeCalcField() {
      this.editCalcField = false
      this.initField()
    },

    filterField(val) {
      if (val && val !== '') {
        this.tableFields.dimensionListData = JSON.parse(
          JSON.stringify(
            this.tableFields.dimensionListData.filter(ele => {
              return ele.name
                .toLocaleLowerCase()
                .includes(val.toLocaleLowerCase())
            })
          )
        )
        this.tableFields.quotaListData = JSON.parse(
          JSON.stringify(
            this.tableFields.quotaList.filter(ele => {
              return ele.name
                .toLocaleLowerCase()
                .includes(val.toLocaleLowerCase())
            })
          )
        )
      } else {
        this.tableFields.dimensionListData = JSON.parse(
          JSON.stringify(this.tableFields.dimensionList)
        )
        this.tableFields.quotaListData = JSON.parse(
          JSON.stringify(this.tableFields.quotaList)
        )
      }
    },

    editField(item) {
      this.currEditField = item
      this.editCalcField = true
    },

    deleteField(item) {
      this.$confirm('确认删除', '提示', {
        confirmButtonText: '确认',
        cancelButtonText: '删除',
        type: 'warning',
      })
        .then(() => {
          post('/dataset/field/delete/' + item.id, null).then(response => {
            this.$message({
              type: 'success',
              message: '删除成功',
              showClose: true,
            })
            this.initField()
          })
        })
        .catch(() => {})
    },

    syncField() {
      this.$confirm(
        '同步字段可能会导致已编辑字段发生变更，请确认',
        '确认同步',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
        }
      )
        .then(() => {
          this.isSyncField = true
          post('/dataset/table/syncField/' + this.param.id, null).then(
            response => {
              setTimeout(() => {
                this.isSyncField = false
                this.initField()
                // tips
                let msg = ''
                let type = ''
                if (response.data.mode === 0) {
                  msg = '同步成功'
                  type = 'success'
                } else {
                  msg = '同步成功，请对当前数据集重新执行数据同步操作'
                  type = 'warning'
                }
                this.$message({
                  type: type,
                  message: msg,
                  showClose: true,
                })
              }, 500)
            }
          )
        })
        .catch(() => {})
    },
  },
}
</script>

<style scoped lang="scss">
.el-divider--horizontal {
  margin: 12px 0;
}
span {
  font-size: 14px;
}
.field-class {
  font-size: 12px !important;
}
.el-select /deep/input {
  padding-right: 10px;
}
.el-select /deep/.el-input__suffix {
  right: 0;
}
.el-radio {
  margin-right: 10px !important;
}
.style-collapse /deep/.el-collapse-item__header {
  height: 40px;
  line-height: 40px;
  padding: 0 0 0 10px;
}
.style-collapse /deep/.el-collapse-item__wrap {
  border-bottom: 0 solid #e6ebf5 !important;
}
.style-collapse {
  border-top: 0 solid #e6ebf5 !important;
}
.form-item {
  margin-bottom: 6px;
}

.dialog-css /deep/.el-dialog__title {
  font-size: 14px;
}
.dialog-css /deep/ .el-dialog__header {
  padding: 20px 20px 0;
}
.dialog-css /deep/ .el-dialog__body {
  padding: 10px 20px 20px;
  color: #fbfbfb;
}
.dialog-css /deep/.el-dialog {
  width: 800px !important;
}
/deep/.el-dialog,
.avue-group__item {
  background: #232630;
}
/deep/.el-collapse-item__header {
  color: #bfbfbf;
  font-size: 14px;
  font-weight: 400;
  border-bottom: 1px solid #293241;
  margin-bottom: 10px;
  .el-icon-arrow-right {
    color: #333;
    position: unset;
  }
}
/deep/ .el-collapse-item__wrap {
  background-color: #ccc;
}
</style>
