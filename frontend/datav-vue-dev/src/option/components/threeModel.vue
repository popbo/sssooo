<!-- 选项卡配置 -->
<template>
  <div>
   <el-form-item label="模型地址">
      <el-input v-model="main.activeObj.data.model">
        <div @click="main.handleOpen3d()" slot="append" class="openimg_box">
          <i class="iconfont icon-img"></i>
        </div>
      </el-input>
    </el-form-item>
    <el-form-item label="背景颜色">
      <avue-input-color
        type="textarea"
        v-model="main.activeOption.Mbgcolor"
      ></avue-input-color>
    </el-form-item>
  </div>
</template>

<script>
import { dicOption } from '@/option/config'
import optionsGetFont from '@/mixins/optionsGetFont.js'
export default {
  data() {
    return {
      dicOption: dicOption,
    }
  },
  mixins: [optionsGetFont],
  inject: ['main'],
  methods: {
  },
  computed: {},
}
</script>
<style scoped></style>
