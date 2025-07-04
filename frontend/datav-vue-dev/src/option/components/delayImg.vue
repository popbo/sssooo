<template>
  <div>
    <el-form-item label="显示中心图标">
      <el-switch v-model="main.activeOption.hasState"></el-switch>
    </el-form-item>
    <el-form-item label="状态1图标">
      <el-button
        plain
        class="myBtn"
        size="mini"
        v-if="!main.activeOption.initialStatesObj.svgSource"
        @click="handleChooseSvgClick(main.activeOption.initialStatesObj)"
        >选择图标</el-button
      >
      <div
        v-html="main.activeOption.initialStatesObj.svgSource"
        @click="handleChooseSvgClick(main.activeOption.initialStatesObj)"
        class="div-svg"
        v-else
      ></div>
      <el-button
        plain
        icon="el-icon-edit"
        class="myBtn"
        size="mini"
        @click="setSvgStyle(main.activeOption.initialStatesObj)"
        >设置图标</el-button
      >
    </el-form-item>
    <el-form-item label="状态2图标">
      <el-button
        plain
        class="myBtn"
        size="mini"
        v-if="!main.activeOption.finishStatesObj.svgSource"
        @click="handleChooseSvgClick(main.activeOption.finishStatesObj)"
        >选择图标</el-button
      >
      <div
        v-html="main.activeOption.finishStatesObj.svgSource"
        @click="handleChooseSvgClick(main.activeOption.finishStatesObj)"
        class="div-svg"
        v-else
      ></div>
      <el-button
        plain
        icon="el-icon-edit"
        class="myBtn"
        size="mini"
        @click="setSvgStyle(main.activeOption.finishStatesObj)"
        >设置图标</el-button
      >
    </el-form-item>
    <el-form-item label="计时时间">
      <el-input-number
        v-model="main.activeOption.animationTime"
        controls-position="right"
        :min="0"
      ></el-input-number>
    </el-form-item>
    <el-form-item label="计时进度条宽度">
      <el-input-number
        v-model="main.activeOption.strokeWidth"
        controls-position="right"
        :min="0"
      ></el-input-number>
    </el-form-item>
    <el-form-item label="计时进度条颜色">
      <avue-input-color
        v-model="main.activeOption.strokeProgressColor"
      ></avue-input-color>
    </el-form-item>
    <el-form-item label="进度条背景颜色">
      <avue-input-color
        v-model="main.activeOption.strokeBgColor"
      ></avue-input-color>
    </el-form-item>
    <el-form-item label="状态转换提示">
      <el-switch v-model="main.activeOption.hasTips"></el-switch>
    </el-form-item>

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
import svgDiglog from '@/components/draw-warn/svgDialog.vue'
export default {
  inject: ['main'],
  name: 'delayImgOption',
  data() {
    return {
      svgDialogVisible: false, // 图标选择对话框
      currentSvgObj: {}, // 暂存当前选中的图标
      currentStateObj: {}, // 记录是状态1要设置图标还是状态2设置图标
      svgStyleForm: {
        svgColor: '#409EFF',
        svgSize: 10,
      },
      svgStyleDialogVisible: false, // 图标样式对话框
    }
  },

  created() {},

  methods: {
    handleChooseSvgClick(stateObj) {
      this.currentStateObj = stateObj
      this.svgDialogVisible = true
    },
    handleChooseSvg(svgObj) {
      this.currentSvgObj = svgObj // 把对话框中选择的图标对象暂存到currentSvgObj中
    },
    // 当点击了选择图标对话框的取消按钮时
    cancelSvg() {
      this.$refs.svgDiglog.currentIndex = '' // 置空一下当前选中的
      this.currentStateObj = {} // 置空暂存当前操作对象的currentConditionItem
      this.currentSvgObj = {} // 置空暂存当前选中图标对象的currentSvgObj
      this.svgDialogVisible = false
    },
    confirmSvgObj() {
      this.currentStateObj.svgSource = this.currentSvgObj.source // 当点击了确定时把当前选中的Svg图标对象赋值给当前正在操作的条件对象
      this.$refs.svgDiglog.currentIndex = '' // 置空一下当前选中的图标Index
      this.currentSvgObj = {} // 置空暂存当前选中图标对象的currentSvgObj
      this.currentStateObj = {}
      this.svgDialogVisible = false
    },
    // 当点击了设置图标按钮
    setSvgStyle(stateObj) {
      this.currentStateObj = stateObj
      this.svgStyleForm.svgColor = stateObj.svgColor
      this.svgStyleForm.svgSize = stateObj.svgSize
      this.svgStyleDialogVisible = true // 打开选择图标对话框
    },
    // 当点击设置图标样式对话框的确定按钮时
    confirmSetSvgStyle() {
      this.$refs.svgStyleForm.validate(valid => {
        if (valid) {
          // this.currentConditionItem.svgConObj
          Object.assign(this.currentStateObj, this.svgStyleForm)
          this.$refs.svgStyleForm.resetFields()
          this.currentStateObj = {} // 置空暂存当前操作对象的currentStateObj
          this.svgStyleDialogVisible = false // 关闭对话框
        } else {
          return false
        }
      })
    },
    // 当点击设置图标样式对话框的取消按钮时
    cancelSetSvgStyle() {
      this.$refs.svgStyleForm.resetFields()
      this.currentStateObj = {} // 置空暂存当前操作对象的currentConditionItem
      this.svgStyleDialogVisible = false // 关闭对话框
    },
  },
  components: {
    svgDiglog,
  },
}
</script>

<style scoped lang="scss">
/deep/.div-svg {
  width: 40px;
  height: 40px;
  display: inline-block;
  vertical-align: middle;
  margin-right: 10px;
  cursor: pointer;
  svg {
    width: 38px !important;
    height: 38px !important;
  }
}
.color_input {
  width: 100px;
}
.el-color-picker {
  vertical-align: middle;
}
</style>
