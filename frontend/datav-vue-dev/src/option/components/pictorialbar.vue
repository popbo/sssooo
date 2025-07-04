<!-- 象形图配置 -->
<template>
  <div>
    <el-form-item label="图标类型">
      <el-select v-model="main.activeOption.geometricOrImage">
        <el-option label="几何图形" value="geometric"></el-option>
        <el-option label="自定义图片" value="image"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item
      label="图标"
      v-if="main.activeOption.geometricOrImage === 'geometric'"
    >
      <el-select v-model="main.activeOption.symbol">
        <el-option
          v-for="(item, index) in geometricArr"
          :label="item.label"
          :value="item.value"
          :key="index"
        ></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="图标" v-else>
      <img
        v-if="main.activeOption.imgSymbol"
        :src="main.activeOption.imgSymbol"
        alt=""
        width="25%"
      />
      <el-input v-model="main.activeOption.imgSymbol">
        <div
          @click="main.handleOpenImg('activeOption.imgSymbol')"
          slot="append"
        >
          <i class="iconfont icon-img"></i>
        </div>
      </el-input>
    </el-form-item>
    <el-form-item label="图标宽度" class="symbolsize-from-item">
      <el-input-number
        v-model="main.activeOption.symbolSize[0]"
        controls-position="right"
        :min="0"
      />
    </el-form-item>
    <el-form-item label="图标高度" class="symbolsize-from-item">
      <el-input-number
        v-model="main.activeOption.symbolSize[1]"
        controls-position="right"
        :min="0"
      />
    </el-form-item>
    <el-form-item label="字体颜色">
      <avue-input-color v-model="main.activeOption.color"></avue-input-color>
    </el-form-item>
    <el-form-item label="轴字体颜色">
      <avue-input-color
        v-model="main.activeOption.nameColor"
      ></avue-input-color>
    </el-form-item>
    <el-collapse accordion>
      <el-collapse-item title="图标颜色设置">
        <avue-crud
          :option="colorOption1"
          :data="main.activeOption.pictorialBarColor"
          @row-save="rowSave"
          @row-del="rowDel"
          @row-update="rowUpdate"
        ></avue-crud>
        <!-- <el-form-item
          class="synbol-color-item"
          v-for="(item, index) in main.activeOption.pictorialBarColor"
          :label="'图标颜色' + (index + 1)"
          :key="index"
        >
          <avue-input-color v-model="item.color1"></avue-input-color>
          <i
            class="el-icon-circle-plus-outline"
            style="cursor: pointer; margin-right: 5px"
            @click="addSymbolColor(index)"
          ></i>
          <i
            v-if="index + 1 > dataLength"
            class="el-icon-delete"
            style="cursor: pointer"
            @click="deleteSymbolColor(index)"
          ></i>
        </el-form-item> -->
      </el-collapse-item>
      <el-collapse-item title="X轴设置">
        <el-form-item label="显示">
          <avue-switch v-model="main.activeOption.xAxisShow"> </avue-switch>
        </el-form-item>
        <el-form-item label="字号">
          <el-input-number
            v-model="main.activeOption.xNameFontSize"
            controls-position="right"
            :min="0"
          />
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="Y轴设置">
        <el-form-item label="显示">
          <avue-switch v-model="main.activeOption.yAxisShow"> </avue-switch>
        </el-form-item>
        <el-form-item label="标签字号">
          <el-input-number
            v-model="main.activeOption.yNameFontSize"
            controls-position="right"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="数值字号">
          <el-input-number
            v-model="main.activeOption.fontSize"
            controls-position="right"
            :min="0"
          />
        </el-form-item>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script>
import { colorOption1 } from '@/option/config'
export default {
  inject: ['main'],
  data() {
    return {
      colorOption1: colorOption1,
      geometricArr: [
        {
          label: '圆形',
          value: 'circle',
        },
        {
          label: '矩形',
          value: 'rect',
        },
        {
          label: '圆角矩形',
          value: 'roundRect',
        },
        {
          label: '三角形',
          value: 'triangle',
        },
        {
          label: '钻石',
          value: 'diamond',
        },
        {
          label: '大头针',
          value: 'pin',
        },
        {
          label: '箭头',
          value: 'arrow',
        },
      ],
    }
  },
  methods: {
    addSymbolColor(index) {
      const colorObj = {
        color1: '#309EF8',
      }
      this.main.activeOption.pictorialBarColor.splice(index + 1, 0, colorObj)
    },
    deleteSymbolColor(index) {
      this.main.activeOption.pictorialBarColor.splice(index, 1)
    },
    rowSave(row, done) {
      this.main.activeOption.pictorialBarColor.push(row)
      done()
    },
    rowDel(row, index) {
      if (this.main.activeOption.pictorialBarColor.length > 1) {
        this.main.activeOption.pictorialBarColor.splice(index, 1)
      } else {
        this.$message.error('请至少保留一项')
      }
    },
    rowUpdate(row, index, done) {
      this.main.activeOption.pictorialBarColor.splice(index, 1, row)
      done()
    },
  },
  // watch: {
  //   'main.activeOption.geometricOrImage': {
  //     handler(newVal, oldVal) {
  //       if (newVal === 'geometric') {
  //         this.main.activeOption.symbol = 'roundRect'
  //       } else {
  //         this.main.activeOption.symbol = '/img/source/source261.png'
  //       }
  //     },
  //   },
  // },
  computed: {
    // 需要一个计算数据长度的属性，来判断图标颜色数组的某一项是否可以删除，即该项的index值大于它才可以删除
    dataLength() {
      return this.main.activeObj.data.length
    },
  },
}
</script>

<style lang="scss">
.synbol-color-item {
  .avue-input-color {
    display: inline-block;
    width: 150px;
    margin-right: 10px;
  }
}
// .symbolsize-from-item {
//   .avue-input-number {
//     display: inline-block;
//     width: 100px !important;
//     margin-right: 10px;
//   }
// }
</style>
