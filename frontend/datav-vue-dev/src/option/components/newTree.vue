<template>
  <div>
    <el-form-item label="字体颜色">
      <avue-input-color v-model="main.activeOption.color"></avue-input-color>
    </el-form-item>
    <el-form-item label="字体大小">
      <el-input-number
        v-model="main.activeOption.fontSize"
        :max="200"
        controls-position="right"
        :min="0"
      />
    </el-form-item>
    <el-form-item label="字体系列">
      <el-select v-model="main.activeOption.fontFamily">
        <el-option
          v-for="(item, index) in allFontFamilyArr"
          :key="index"
          :label="item.label"
          :value="item.value"
        ></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="选中字体颜色">
      <avue-input-color
        v-model="main.activeOption.currentColor"
      ></avue-input-color>
    </el-form-item>
    <el-form-item label="一级菜单背景颜色">
      <avue-input-color
        v-model="main.activeOption.externalBgc"
      ></avue-input-color>
    </el-form-item>
    <el-form-item label="展开背景颜色">
      <avue-input-color
        v-model="main.activeOption.interiorBgc"
      ></avue-input-color>
    </el-form-item>
    <el-form-item label="标题高度">
      <el-input-number
        v-model="main.activeOption.titleHeight"
        controls-position="right"
        :min="0"
      ></el-input-number>
    </el-form-item>
    <el-form-item label="标题列间距">
      <el-input-number
        v-model="main.activeOption.titlePaddingLeft"
        controls-position="right"
        :min="0"
        :max="200"
      ></el-input-number>
    </el-form-item>
    <el-form-item label="标题位置">
      <el-select v-model="main.activeOption.titlePosition">
        <el-option
          v-for="item in dicOption.textAlign"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        ></el-option>
      </el-select>
    </el-form-item>
    <el-collapse accordion>
      <el-collapse-item title="角标配置">
        <el-form-item label="角标样式">
          <el-select v-model="main.activeOption.jiaoStyle">
            <el-option
              v-for="(item, index) in styleType"
              :key="index"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="图标" v-if="main.activeOption.jiaoStyle === 2">
          <div style="position: relative; bottom: 10px">
            <span
              class="new-tree-svg"
              v-if="main.activeOption.initialStatesObj.link"
              v-html="svgWithStyle"
              @click="handleChooseSvgClick(f_item)"
            ></span>
            <el-button
              style="position: relative; bottom: 4px"
              plain
              class="myBtn"
              size="mini"
              v-if="!main.activeOption.initialStatesObj.link"
              @click="handleChooseSvgClick()"
              >选择图标</el-button
            >
            <el-button
              style="position: relative; bottom: 4px"
              plain
              icon="el-icon-edit"
              class="myBtn"
              size="mini"
              @click="setSvgStyle()"
              >设置图标</el-button
            >
          </div>
        </el-form-item>
        <div v-else>
          <el-form-item label="角标颜色">
            <avue-input-color
              v-model="main.activeOption.jiaoColor"
            ></avue-input-color>
          </el-form-item>
          <el-form-item label="角标大小">
            <el-input-number
              v-model="main.activeOption.jiaofontSize"
              controls-position="right"
              :min="0"
            ></el-input-number>
          </el-form-item>
        </div>
      </el-collapse-item>
    </el-collapse>
    <el-dialog title="选择图标" :visible.sync="svgDialogVisible" width="30%">
      <svgDiglog ref="svgDiglog" @on-chooseSvg="handleChooseSvg"></svgDiglog>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancelSvg">取 消</el-button>
        <el-button type="primary" @click="confirmSvgObj">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog
      title="图标样式编辑"
      :visible.sync="svgStyleDialogVisible"
      width="30%"
    >
      <el-form :model="svgStyleForm" ref="svgStyleForm" :rules="rules">
        <el-form-item label="颜色：" label-width="80px" prop="svgColor">
          <el-color-picker v-model="svgStyleForm.svgColor"></el-color-picker>
          <el-input
            v-model="svgStyleForm.svgColor"
            class="color_input"
          ></el-input>
        </el-form-item>
        <el-form-item label="大小：" label-width="80px" prop="svgSize">
          <el-input
            v-model="svgStyleForm.svgSize"
            class="color_input"
          ></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancelSetSvgStyle">取 消</el-button>
        <el-button type="primary" @click="confirmSetSvgStyle">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { dicOption } from '@/option/config'
import svgDiglog from '@/components/draw-warn/svgDialog.vue'
import optionsGetFont from '@/mixins/optionsGetFont.js'
export default {
  mixins: [optionsGetFont],
  data() {
    return {
      dicOption: dicOption,
      min: 0,
      styleType: [
        {
          label: '三角形',
          value: 1,
        },
        {
          label: '矩形',
          value: 3,
        },
        {
          label: '自定义图标',
          value: 2,
        },
      ],
      svgDialogVisible: false, // 图标选择对话框
      currentSvgObj: {}, // 暂存当前选中的图标
      svgStyleForm: {
        svgColor: '#409EFF',
        svgSize: 16,
      },
      svgStyleDialogVisible: false, // 图标样式对话框
      rules: {
        svgColor: [
          { required: true, message: '请选择颜色', trigger: 'change' },
        ],
        svgSize: [{ required: true, message: '请设置大小', trigger: 'blur' }],
      },
    }
  },
  inject: ['main'],
  created() {
    this.svgStyleForm.svgColor =
      this.main.activeOption.initialStatesObj.svgColor
    this.svgStyleForm.svgSize = this.main.activeOption.initialStatesObj.svgSize
  },
  methods: {
    handleChooseSvgClick() {
      this.svgDialogVisible = true
    },
    handleChooseSvg(svgObj) {
      this.currentSvgObj = svgObj // 把对话框中选择的图标对象暂存到currentSvgObj中
    },
    // 当点击了选择图标对话框的取消按钮时
    cancelSvg() {
      this.$refs.svgDiglog.currentIndex = '' // 置空一下当前选中的
      this.currentSvgObj = {} // 置空暂存当前选中图标对象的currentSvgObj
      this.svgDialogVisible = false
    },
    confirmSvgObj() {
      this.main.activeOption.initialStatesObj.link = this.currentSvgObj.link
      this.$refs.svgDiglog.currentIndex = '' // 置空一下当前选中的图标Index
      this.currentSvgObj = {} // 置空暂存当前选中图标对象的currentSvgObj
      this.svgDialogVisible = false
    },
    // 当点击了设置图标按钮
    setSvgStyle() {
      this.svgStyleDialogVisible = true // 打开选择图标对话框
    },
    // 当点击设置图标样式对话框的确定按钮时
    confirmSetSvgStyle() {
      this.$refs.svgStyleForm.validate(valid => {
        if (valid) {
          this.main.activeOption.initialStatesObj.svgColor =
            this.svgStyleForm.svgColor
          this.main.activeOption.initialStatesObj.svgSize =
            this.svgStyleForm.svgSize
          this.svgStyleDialogVisible = false // 关闭对话框
        } else {
          return false
        }
      })
    },
    // 当点击设置图标样式对话框的取消按钮时
    cancelSetSvgStyle() {
      this.$refs.svgStyleForm.resetFields()
      this.svgStyleDialogVisible = false // 关闭对话框
    },
  },
  watch: {
    svgStyleDialogVisible(val) {
      if (val) {
        this.svgStyleForm.svgColor =
          this.main.activeOption.initialStatesObj.svgColor
        this.svgStyleForm.svgSize =
          this.main.activeOption.initialStatesObj.svgSize
      }
    },
  },
  components: {
    svgDiglog,
  },
  computed: {
    svgWithStyle() {
      if (this.main.activeOption.initialStatesObj) {
        let source = this.main.activeOption.initialStatesObj.link
        let svgSize = 40 + 'px'
        let svgColor = this.main.activeOption.initialStatesObj.svgColor
        let style = ` style = "width: ${svgSize} ; height: ${svgSize}; fill: ${svgColor}"`
        let reg = /((?<=<svg))/g
        let reg1 = /fill=\"(\S)*\"/g // 去除掉path标签中的fill,要不然无法改色
        let source1 = source.replace(reg1, 'fill')
        return source1.replace(reg, style)
      } else {
        return ''
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.new-tree-svg {
  position: relative;
  top: 10px;
  right: 10px;
  cursor: pointer;
}
</style>
