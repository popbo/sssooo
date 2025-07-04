<!-- 选项卡配置 -->
<template>
  <div>
    <el-form-item label="显示数量">
      <el-input-number
        v-model="main.activeOption.count"
        controls-position="right"
        :min="0"
      ></el-input-number>
    </el-form-item>
    <el-form-item label="自动轮播">
      <avue-switch v-model="main.activeOption.autoRotation"></avue-switch>
    </el-form-item>
    <el-form-item label="轮播时间(ms)" v-if="main.activeOption.autoRotation">
      <el-input-number
        v-model="main.activeOption.rotationTime"
        controls-position="right"
        :min="0"
      />
    </el-form-item>
    <el-form-item label="动画模式" v-if="main.activeOption.autoRotation">
      <avue-select
        v-model="main.activeOption.animationModel"
        :dic="dicOption.animationModals"
      >
      </avue-select>
    </el-form-item>
    <el-form-item label="时间格式">
      <avue-select
        v-model="main.activeOption.timeFormat"
        :dic="dicOption.timeFormatList"
      >
      </avue-select>
    </el-form-item>

    <el-form-item label="事件背景色">
      <avue-input-color
        type="textarea"
        v-model="main.activeOption.bgColor"
      ></avue-input-color>
    </el-form-item>
    <el-collapse>
      <el-collapse-item title="首行样式设置">
        <el-form-item label="背景色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.firstLineBg"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="标题文本颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.firstLineTitleColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="内容文本颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.firstLineDetailColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="时间文本颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.firstLineTimeColor"
          ></avue-input-color>
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="图标设置">
        <el-form-item label="显示图标">
          <avue-switch v-model="main.activeOption.isShowIcon"></avue-switch>
        </el-form-item>
        <el-form-item label="图标" v-if="main.activeOption.isShowIcon">
          <div
            v-html="main.activeOption.iconUrl"
            class="div-svg"
            @click="handleChooseSvgClick"
          ></div>
          <el-button
            plain
            icon="el-icon-edit"
            class="myBtn"
            size="mini"
            @click="setSvgStyle"
            >设置图标</el-button
          >
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="标题文本设置">
        <el-form-item label="字体">
          <!-- <avue-select
            v-model="main.activeOption.titleFontStyle"
            :dic="dicOption.fontFamily"
          >
          </avue-select> -->
          <el-select v-model="main.activeOption.titleFontStyle">
            <el-option
              v-for="(item, index) in allFontFamilyArr"
              :key="index"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="字体粗细">
          <avue-select
            v-model="main.activeOption.titleFontWeight"
            :dic="dicOption.fontWeight"
          >
          </avue-select>
        </el-form-item>
        <el-form-item label="字体大小">
          <el-input-number
            v-model="main.activeOption.titleFontSize"
            controls-position="right"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="字体行高">
          <el-input-number
            v-model="main.activeOption.titleLineHeight"
            controls-position="right"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="文本颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.titleColor"
          ></avue-input-color>
        </el-form-item>
      </el-collapse-item>

      <el-collapse-item title="时间文本设置">
        <el-form-item label="字体">
          <!-- <avue-select
            v-model="main.activeOption.timeFontStyle"
            :dic="dicOption.fontFamily"
          >
          </avue-select> -->
          <el-select v-model="main.activeOption.timeFontStyle">
            <el-option
              v-for="(item, index) in allFontFamilyArr"
              :key="index"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="字体粗细">
          <avue-select
            v-model="main.activeOption.timeFontWeight"
            :dic="dicOption.fontWeight"
          >
          </avue-select>
        </el-form-item>
        <el-form-item label="字体大小">
          <el-input-number
            v-model="main.activeOption.timeFontSize"
            controls-position="right"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="字体行高">
          <el-input-number
            v-model="main.activeOption.timeLineHeight"
            controls-position="right"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="文本颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.timeColor"
          ></avue-input-color>
        </el-form-item>
      </el-collapse-item>

      <el-collapse-item title="内容文本设置">
        <el-form-item label="字体">
          <!-- <avue-select
            v-model="main.activeOption.detailFontStyle"
            :dic="dicOption.fontFamily"
          >
          </avue-select> -->
          <el-select v-model="main.activeOption.detailFontStyle">
            <el-option
              v-for="(item, index) in allFontFamilyArr"
              :key="index"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="字体粗细">
          <avue-select
            v-model="main.activeOption.detailFontWeight"
            :dic="dicOption.fontWeight"
          >
          </avue-select>
        </el-form-item>
        <el-form-item label="字体大小">
          <el-input-number
            v-model="main.activeOption.detailFontSize"
            controls-position="right"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="字体行高">
          <el-input-number
            v-model="main.activeOption.detailLineHeight"
            controls-position="right"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="文本颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.detailColor"
          ></avue-input-color>
        </el-form-item>
      </el-collapse-item>

      <el-collapse-item title="框线设置">
        <el-form-item label="边框粗细">
          <el-input-number
            v-model="main.activeOption.borderWidth"
            controls-position="right"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="边框颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.borderColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="内框线粗细">
          <el-input-number
            v-model="main.activeOption.borderXWidth"
            controls-position="right"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="内框线颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.borderXColor"
          ></avue-input-color>
        </el-form-item>
      </el-collapse-item>
    </el-collapse>
    <!-- 图标选择对话框 开始 -->
    <el-dialog title="选择图标" :visible.sync="svgDialogVisible" width="30%">
      <svgDiglog ref="svgDiglog" @on-chooseSvg="handleChooseSvg"></svgDiglog>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancelSvg">取 消</el-button>
        <el-button type="primary" @click="confirmSvgObj">确 定</el-button>
      </span>
    </el-dialog>
    <!-- 图标选择对话框 结束 -->
    <!-- 图表样式对话框 开始 -->
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
    <!-- 图表样式对话框 结束 -->
  </div>
</template>

<script>
import svgDiglog from '@/components/draw-warn/svgDialog'
import { dicOption } from '@/option/config'
import optionsGetFont from '@/mixins/optionsGetFont.js'
export default {
  mixins: [optionsGetFont],
  components: {
    svgDiglog,
  },
  data() {
    return {
      dicOption,
      svgDialogVisible: false, // 图标选择对话框
      svgStyleDialogVisible: false,
      currentSvgObj: {}, // 暂存当前选中的图标
      svgStyleForm: {
        svgColor: this.main.activeOption.iconColor,
        svgSize: this.main.activeOption.iconSize,
      },
      rules: {
        svgColor: [
          { required: true, message: '请选择颜色', trigger: 'change' },
        ],
        svgSize: [{ required: true, message: '请设置大小', trigger: 'blur' }],
      },
      // timeFormatList:[{
      //   label: "YYYY-MM-DD HH:MM",
      //   value: 1
      // },{
      //   label: "YYYY-MM-DD",
      //   value: 2
      // }],
    }
  },
  inject: ['main'],
  methods: {
    // 处理选择图标对话框选中一个图标时触发的事件
    handleChooseSvg(choSvgObj) {
      console.log('choSvgObj', choSvgObj)
      this.currentSvgObj = choSvgObj // 把对话框中选择的图标对象暂存到currentSvgObj中
    },
    // 当点击了选择图标对话框的取消按钮时
    cancelSvg() {
      this.$refs.svgDiglog.currentIndex = '' // 置空一下当前选中的
      this.currentSvgObj = {} // 置空暂存当前选中图标对象的currentSvgObj
      this.svgDialogVisible = false
    },
    //显示
    handleChooseSvgClick() {
      this.svgDialogVisible = true // 打开选择图标对话框
    },
    // 当点击了选择图标对话框的确定按钮时
    confirmSvgObj() {
      console.log('当前选中的图标', this.currentSvgObj)
      if (!this.currentSvgObj.link)
        return this.$message.warning({ message: '请选择一个图标' })
      this.main.activeOption.iconUrl = this.svgWithStyle(
        this.currentSvgObj.link
      )
      this.svgDialogVisible = false
    },
    // 计算出svg
    svgWithStyle(link) {
      let source = link
      let svgSize = this.svgStyleForm.svgSize + 'px'
      this.main.activeOption.iconSize = this.svgStyleForm.svgSize
      let svgColor = this.svgStyleForm.svgColor
      this.main.activeOption.iconColor = svgColor
      let style = ` style = "width: ${svgSize} ; height: ${svgSize}; fill: ${svgColor}"`
      let reg = /((?<=<svg))/g
      let reg1 = /fill=\"(\S)*\"/g // 去除掉path标签中的fill,要不然无法改色
      let source1 = source.replace(reg1, 'fill')
      return source1.replace(reg, style)
    },
    cancelSetSvgStyle() {
      this.svgStyleForm.svgSize = this.main.activeOption.iconSize
      this.svgStyleForm.svgColor = this.main.activeOption.iconColor
      this.svgStyleDialogVisible = false // 关闭对话框
    },
    confirmSetSvgStyle() {
      this.$refs.svgStyleForm.validate(valid => {
        if (valid) {
          console.log('我进来了1')
          this.main.activeOption.iconUrl = this.svgWithStyle(
            this.main.activeOption.iconUrl
          )
          this.svgStyleDialogVisible = false // 关闭对话框
        } else {
          return false
        }
      })
    },
    setSvgStyle() {
      this.svgStyleForm.svgSize = this.main.activeOption.iconSize
      this.svgStyleForm.svgColor = this.main.activeOption.iconColor
      this.svgStyleDialogVisible = true // 打开选择图标对话框
    },
  },
  computed: {},
}
</script>
<style lang="scss" scoped>
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
.el-color-picker {
  vertical-align: middle;
}
.color_input {
  width: 100px;
}
</style>
