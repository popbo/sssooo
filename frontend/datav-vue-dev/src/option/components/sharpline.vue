<!--折线图配置 -->
<template>
  <div>
    <el-form-item label="线条参数">
      <!-- {{main.activeObj.data.value}} -->
      <avue-input v-model="main.activeObj.data.value"></avue-input>
    </el-form-item>
    <el-form-item label="方向">
      <div class="sharpLine">
        <avue-select
          v-model="main.activeOption.direction"
          :dic="dicOption.lineDirection"
        >
        </avue-select>
      </div>
    </el-form-item>
    <el-form-item label="箭头">
      <avue-select
        v-model="main.activeOption.defaultArrowSet"
        :dic="dicOption.arrowSet"
      >
      </avue-select>
    </el-form-item>
    <el-form-item label="显示样式">
      <avue-select
        v-model="main.activeOption.defaultStyle"
        :dic="dicOption.sharpStyle"
      >
      </avue-select>
    </el-form-item>
    <el-form-item label="线条颜色">
      <avue-input-color
        type="textarea"
        v-model="main.activeOption.bodyColor"
      ></avue-input-color>
    </el-form-item>
    <el-form-item label="线条粗细">
      <el-input-number
        v-model="main.activeOption.borderWidth"
        controls-position="right"
        :min="0"
      />
    </el-form-item>
    <el-form-item label="线条长度">
      <el-input-number
        v-model="main.activeOption.width"
        controls-position="right"
        @blur="changeLength"
        :min="0"
      />
    </el-form-item>
  </div>
</template>

<script>
import { dicOption } from '@/option/config'
export default {
  inject: ['main'],
  data() {
    return {
      dicOption: dicOption,
    }
  },
  created() {
    this.$bus.$on('lineWidth', content => {
      this.main.activeOption.width = content
      // console.log('我被触发了', content)
    })
    console.log('查看数据', this.main.activeOption)
  },
  methods: {
    changeLength(val) {
      // console.log('我输入了')
      // 发送新长度及当前配置所属的线条index
      this.$bus.$emit('inputChangeLength', {
        newLength: val,
        currentIndex: this.main.activeIndex,
      })
    },
  },
  beforeDestroy() {
    //销毁
    this.$bus.$off('lineWidth') //$off解绑当前组件所用到的事件
  },
}
</script>

<style lang="scss" scoped>
.build {
  .sharpLine {
    .el-input-number {
      width: 80%;
    }
  }
}
</style>
