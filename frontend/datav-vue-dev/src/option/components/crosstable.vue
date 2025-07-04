<!-- 表格配置 -->
<template>
  <div>
    <!-- <el-form-item label="系统配色">
      <avue-switch v-model="colorMatchingSystem"></avue-switch>
    </el-form-item> -->
    <el-form-item label="边框">
      <avue-switch v-model="main.activeOption.border"> </avue-switch>
    </el-form-item>
    <el-form-item label="边框粗细">
      <el-input-number
        v-model="main.activeOption.borderSize"
        style="width: 90px"
        controls-position="right"
        :min="0"
      ></el-input-number>
      <i style="margin-left: 5px">px</i>
    </el-form-item>
    <el-form-item label="边框颜色">
      <div style="display: flex; align-items: center">
        <el-color-picker
          v-model="main.activeOption.borderColor"
        ></el-color-picker>
        <avue-input
          v-model="main.activeOption.borderColor"
          style="width: 90px"
          disabled
        >
        </avue-input>
      </div>
    </el-form-item>
    <el-collapse accordion>
      <el-collapse-item title="表格样式">
        <div
          style="
            display: flex;
            align-items: center;
            justify-content: center;
            color: rgba(195, 239, 255, 1);
            font-weight: 400;
            font-size: 14px;
            margin-top: 10px;
          "
        >
          <span>表头样式</span>
        </div>
        <el-form-item label="字体大小">
          <el-input-number
            v-model="main.activeOption.tableHeaderSize"
            controls-position="right"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="背景颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.tableHeaderbgcolor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="字体颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.tableHeadercolor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="对齐方式">
          <avue-select
            v-model="main.activeOption.tableHeaderTextAlign"
            :dic="dicOption.textAlign"
          >
          </avue-select>
        </el-form-item>
        <el-form-item label="左侧表头宽度">
          <el-input-number
            v-model="main.activeOption.leftColumnWidth"
            style="width: 90px"
            controls-position="right"
            :min="0"
          ></el-input-number>
          <i style="color: #bcc9d4">px</i>
        </el-form-item>
        <div
          style="
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 10px 0;
          "
        >
          <hr width="80%" color="#5b6b73" />
        </div>
        <div
          style="
            display: flex;
            align-items: center;
            justify-content: center;
            color: rgba(195, 239, 255, 1);
            font-weight: 400;
            font-size: 14px;
            margin-top: 10px;
          "
        >
          <span>数据区样式</span>
        </div>
        <el-form-item label="文字颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.tableDataColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="字体大小">
          <el-input-number
            v-model="main.activeOption.tableDateSize"
            controls-position="right"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="奇行颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.nthColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="偶行颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.othColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="数据区背景颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.tableDataBackground"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="横向网格线颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.transverseColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="纵向网格线颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.portraitColor"
          ></avue-input-color>
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="分页加载">
        <el-form-item label="分页">
          <avue-switch v-model="main.activeOption.paging"></avue-switch>
        </el-form-item>
        <el-form-item label="每页行数">
          <el-input-number
            v-model="main.activeOption.maxRows"
            controls-position="right"
            :min="1"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="加载上限">
          <el-input-number
            v-model="main.activeOption.maxSize"
            controls-position="right"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="分页栏背景色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.paginationbgcolr"
          ></avue-input-color>
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="表头设置">
        <el-form-item label="列表头设置">
          <avue-input
            v-model="main.activeOption.colHeadName"
            placeholder="请输入列表头名"
          ></avue-input>
        </el-form-item>
        <el-form-item label="行表头设置">
          <avue-input
            v-model="main.activeOption.rowHeadName"
            placeholder="请输入行表头名"
          ></avue-input>
        </el-form-item>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script>
import { tableOption, dicOption } from '@/option/config'
import codeedit from '../../page/group/code'
import build from '../../page/build.vue'

export default {
  data() {
    return {
      dicOption: dicOption,
      tableOption: tableOption,
      code: {
        box: false,
        type: 'data',
        obj: {},
      },
    }
  },
  inject: ['main'],
  components: {
    codeedit,
    build,
  },
  methods: {
    rowSave(row, done) {
      this.main.activeOption.column.push(row)
      done()
    },
    rowDel(row, index) {
      this.main.activeOption.column.splice(index, 1)
    },
    rowUpdate(row, index, done) {
      this.main.activeOption.column.splice(index, 1, row)
      done()
    },
    codeClose(value) {
      this.main.activeOption.column = value
    },
    openCode() {
      this.code.obj = this.main.activeOption.column
      this.code.box = true
    },
  },
}
</script>

<style></style>
