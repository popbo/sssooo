<template>
  <div>
    <el-form-item label="文本内容">
      <el-input v-model="main.activeObj.data.value"></el-input>
    </el-form-item>
    <el-form-item label="字体大小">
      <el-input-number
        controls-position="right"
        v-model="main.activeOption.fontSize"
        :max="200"
        :min="0"
      ></el-input-number>
    </el-form-item>
    <el-form-item label="字体颜色">
      <avue-input-color v-model="main.activeOption.color"></avue-input-color>
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
    <el-form-item label="字体位置">
      <el-select v-model="main.activeOption.textAlign">
        <el-option
          v-for="(item, index) in dicOption.textAlign"
          :key="index"
          :label="item.label"
          :value="item.value"
        ></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="交互样式">
      <el-button @click="drawer = true">设置</el-button>
    </el-form-item>
    <el-form-item label="边框圆角">
      <el-input-number
        v-model="main.activeOption.borderRadius"
        :max="200"
        controls-position="right"
        :min="0"
      ></el-input-number>
    </el-form-item>
    <el-form-item label="边框宽度">
      <el-input-number
        v-model="main.activeOption.borderWidth"
        controls-position="right"
        :min="0"
        :max="200"
      ></el-input-number>
    </el-form-item>
    <el-form-item label="边框样式">
      <el-select v-model="main.activeOption.borderStyle" placeholder="请选择">
        <el-option
          v-for="(item, index) in dicOption.borderStyle"
          :key="index"
          :label="item.label"
          :value="item.value"
        ></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="边框颜色">
      <avue-input-color
        v-model="main.activeOption.borderColor"
      ></avue-input-color>
    </el-form-item>
    <el-form-item label="可用">
      <el-switch v-model="main.activeOption.enable"></el-switch>
    </el-form-item>
    <el-form-item label="显示">
      <el-switch v-model="main.activeOption.visible"></el-switch>
    </el-form-item>
    <el-form-item label="背景图片">
      <img
        v-if="main.activeOption.backgroundImage"
        :src="main.activeOption.backgroundImage"
        alt=""
        width="80px"
        height="100%"
      />
      <el-input v-model="main.activeOption.backgroundImage">
        <div
          @click="main.handleOpenImg('activeOption.backgroundImage', 'border')"
          slot="append"
        >
          <i class="iconfont icon-img"></i>
        </div>
      </el-input>
    </el-form-item>
    <el-collapse>
      <el-collapse-item title="背景颜色" name="1">
        <el-form-item label="是否渐变">
          <el-switch v-model="main.activeOption.isGradient"></el-switch>
        </el-form-item>
        <template v-if="main.activeOption.isGradient">
          <el-form-item label="颜色一">
            <avue-input-color
              v-model="main.activeOption.bgColor1"
            ></avue-input-color>
          </el-form-item>
          <el-form-item label="颜色二">
            <avue-input-color
              v-model="main.activeOption.bgColor2"
            ></avue-input-color>
          </el-form-item>
        </template>
        <template v-else>
          <el-form-item label="颜色">
            <avue-input-color
              v-model="main.activeOption.bgColor"
            ></avue-input-color>
          </el-form-item>
        </template>
      </el-collapse-item>
    </el-collapse>
    <el-drawer
      title="交互样式设置"
      :visible.sync="drawer"
      direction="rtl"
      :before-close="handleClose"
      size="18%"
    >
      <p>选中</p>
      <el-form-item label="字体颜色">
        <avue-input-color
          v-model="main.activeOption.focusStyle.color"
        ></avue-input-color>
      </el-form-item>
      <el-form-item label="字体大小">
        <el-input-number
          v-model="main.activeOption.focusStyle.fontSize"
          controls-position="right"
          :min="0"
          :max="200"
        ></el-input-number>
      </el-form-item>
      <el-form-item label="背景颜色">
        <avue-input-color
          v-model="main.activeOption.focusStyle.bgColor"
        ></avue-input-color>
      </el-form-item>
      <el-form-item label="边框颜色">
        <avue-input-color
          v-model="main.activeOption.focusStyle.borderColor"
        ></avue-input-color>
      </el-form-item>
      <el-form-item label="是否应用">
        <el-switch v-model="main.activeOption.focusIsUse"></el-switch>
      </el-form-item>
      <el-divider></el-divider>
      <p>移入</p>
      <el-form-item label="字体颜色">
        <avue-input-color
          v-model="main.activeOption.hoverStyle.color"
        ></avue-input-color>
      </el-form-item>
      <el-form-item label="字体大小">
        <el-input-number
          v-model="main.activeOption.hoverStyle.fontSize"
          controls-position="right"
          :min="0"
          :max="200"
        ></el-input-number>
      </el-form-item>
      <el-form-item label="背景颜色">
        <avue-input-color
          v-model="main.activeOption.hoverStyle.bgColor"
        ></avue-input-color>
      </el-form-item>
      <el-form-item label="边框颜色">
        <avue-input-color
          v-model="main.activeOption.hoverStyle.borderColor"
        ></avue-input-color>
      </el-form-item>
      <el-form-item label="是否应用">
        <el-switch v-model="main.activeOption.hoverIsUse"></el-switch>
      </el-form-item>
      <el-button class="myBtn copy_style_btn" @click="copyStyle"
        >复用选中样式</el-button
      >
    </el-drawer>
  </div>
</template>

<script>
import { dicOption } from '@/option/config'
import optionsGetFont from '@/mixins/optionsGetFont.js'
export default {
  mixins: [optionsGetFont],
  data() {
    return {
      drawer: false,
      dicOption: dicOption,
    }
  },
  inject: ['main'],
  methods: {
    handleClose(done) {
      this.$confirm('确认关闭？')
        .then(_ => {
          done()
        })
        .catch(_ => {})
    },
    copyStyle() {
      this.main.activeOption.hoverStyle = this.deepClone(
        this.main.activeOption.focusStyle
      )
    },
  },
}
</script>

<style lang="scss" scoped>
@import '~@/styles/buildVariables.scss';
/deep/ .el-drawer {
  background-color: $bgc2;
  .el-drawer__header {
    color: #fff;
    margin-bottom: 15px;
  }
  .el-drawer__body {
    background-color: $bgc1;
    padding-top: 20px;
  }
}
P {
  width: 100%;
  text-align: center;
}
.el-divider--horizontal {
  width: 90%;
  margin: 30px 20px;
}
.myBtn {
  width: 200px;

  color: #2491f7;
  border-color: #2491f7;
  background-color: transparent;
  &:active {
    color: #1570d1;
    border-color: #1570d1;
    background-color: transparent;
  }
  &:focus {
    background-color: transparent;
  }
  &:hover {
    color: #4fb0ff;
    border-color: #4fb0ff;
    background-color: transparent;
  }
}
.copy_style_btn {
  margin: 0 80px;
}
</style>
