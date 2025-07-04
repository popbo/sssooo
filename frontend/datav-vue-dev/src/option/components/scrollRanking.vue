<!-- 选项卡配置 -->
<template>
  <div>
    <el-form-item label="自动轮播">
      <avue-switch v-model="main.activeOption.rotation"></avue-switch>
    </el-form-item>
    <el-form-item label="轮播时间">
      <el-input-number
        v-model="main.activeOption.rotationTime"
        controls-position="right"
        :min="0"
      ></el-input-number>
    </el-form-item>
    <el-form-item label="每页显示数量">
      <el-input-number
        v-model="main.activeOption.rotationNumber"
        controls-position="right"
        :min="1"
        :max="maxScrollNumber"
      ></el-input-number>
    </el-form-item>
    <el-form-item label="滚动模式">
      <avue-select
        v-model="main.activeOption.animationModel"
        :dic="dicOption.animationModals"
      >
      </avue-select>
    </el-form-item>
    <el-collapse>
      <el-collapse-item title="数据设置">
        <el-form-item label="数值单位">
          <avue-input v-model="main.activeOption.numericalunit"></avue-input>
        </el-form-item>
        <el-form-item label="字体大小">
          <el-input-number
            v-model="main.activeOption.numericalFsize"
            controls-position="right"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="字体颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.numericalColor"
          ></avue-input-color>
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="主题标签设置">
        <el-form-item label="隐藏标签">
          <avue-switch v-model="main.activeOption.theme"></avue-switch>
        </el-form-item>
        <el-form-item label="字体大小" v-show="!main.activeOption.theme">
          <el-input-number
            v-model="main.activeOption.themeFsize"
            controls-position="right"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="字体颜色" v-show="!main.activeOption.theme">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.themeColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="图标">
          <avue-switch v-model="main.activeOption.icon"></avue-switch>
        </el-form-item>
        <el-form-item label="图标间距" v-show="main.activeOption.icon">
          <el-input-number
            v-model="main.activeOption.iconSpacing"
            controls-position="right"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="图标大小" v-show="main.activeOption.icon">
          <el-input-number
            v-model="main.activeOption.iconSize"
            controls-position="right"
            :min="0"
            :max="100"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="图标地址" v-show="main.activeOption.icon">
          <img
            v-if="main.activeOption.value"
            :src="main.activeOption.value"
            alt=""
            width="100%"
          />
          <el-input v-model="main.activeOption.value">
            <div
              @click="main.handleOpenImg('activeOption.value')"
              slot="append"
            >
              <i class="iconfont icon-img"></i>
            </div>
          </el-input>
        </el-form-item>
        <el-form-item label="图标位置" v-show="main.activeOption.icon">
          <avue-select
            :dic="directionList"
            v-model="main.activeOption.iconDirection"
          ></avue-select>
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="进度条设置">
        <el-form-item label="进度条长度">
          <el-input-number
            v-model="main.activeOption.progressWidth"
            controls-position="right"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="进度条高度">
          <el-input-number
            v-model="main.activeOption.progressHeight"
            controls-position="right"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="背景颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.progressBackground"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="渐变">
          <avue-switch v-model="main.activeOption.gradient"></avue-switch>
        </el-form-item>
        <el-form-item label="前景颜色" v-show="!main.activeOption.gradient">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.progressProspect"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="前景色一" v-show="main.activeOption.gradient">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.gradientColorOne"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="前景色二" v-show="main.activeOption.gradient">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.gradientColorTwo"
          ></avue-input-color>
        </el-form-item>
      </el-collapse-item>
    </el-collapse>
    <!-- <el-form-item label="显示main">
      <el-button @click="showMain">显示main</el-button>
    </el-form-item> -->
  </div>
</template>

<script>
import { dicOption } from '@/option/config'
import optionsGetFont from '@/mixins/optionsGetFont.js'
export default {
  data() {
    return {
      dicOption: dicOption,
      directionList: [
        {
          label: '左侧',
          value: 1,
        },
        {
          label: '右侧',
          value: 2,
        },
      ],
    }
  },
  mixins: [optionsGetFont],
  inject: ['main'],
  methods: {
    showMain() {
      let uploadFile = document.getElementById('uploadFile')
      // let self = this;
      uploadFile.click()
      function readerFile(e) {
        let file = e.target.files[0]
        uploadFile.value = ''
        uploadFile.removeEventListener('change', readerFile, false)
        console.log(666, file.type)
        let formData = new FormData()
        formData.append('file', file)
        $.ajax({
          url: `/stdc-visual/oss/custom/svg`,
          type: 'POST',
          data: formData,
          processData: false,
          contentType: false,
          success: result => {
            console.log(result)
          },
        })
      }
      uploadFile.addEventListener('change', readerFile, false)
      // console.log("main==>", this.main.activeOption)
    },
  },
  computed: {
    maxScrollNumber() {
      return this.main.activeOption.maxScrollNumber || 0
    },
  },
}
</script>
<style scoped></style>
