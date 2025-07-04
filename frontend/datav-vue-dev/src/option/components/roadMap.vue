<!-- 选项卡配置 -->
<template>
  <div>
    <el-form-item label="文字位置">
      <el-select v-model="main.activeOption.fontSizePosition">
        <el-option
          v-for="item in characterPosition"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        >
        </el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="文字大小">
      <el-input-number
        v-model="main.activeOption.fontSize"
        controls-position="right"
        :min="0"
        :max="200"
      />
    </el-form-item>
    <el-form-item label="文字颜色">
      <avue-input-color
        type="textarea"
        v-model="main.activeOption.color"
      ></avue-input-color>
    </el-form-item>
    <el-form-item label="文字粗细">
      <avue-select
        v-model="main.activeOption.fontWeight"
        :dic="dicOption.fontWeight"
      >
      </avue-select>
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
    <el-form-item label="线条颜色">
      <avue-input-color
        type="textarea"
        v-model="main.activeOption.lineColor"
      ></avue-input-color>
    </el-form-item>
    <el-form-item label="线条粗细">
      <el-input-number
        v-model="main.activeOption.lineNumber"
        controls-position="right"
        :min="0"
        :max="200"
      />
    </el-form-item>
    <el-form-item label="分岔路位置">
      <el-select v-model="main.activeOption.bifurcation">
        <el-option
          v-for="item in characterPosition"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        >
        </el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="分岔路距离">
      <el-input-number
        v-model="main.activeOption.bifurcationNumber"
        controls-position="right"
        :min="0"
        :max="200"
      />
    </el-form-item>
    <el-form-item label="竖直展示">
      <avue-switch v-model="main.activeOption.vertical"> </avue-switch>
    </el-form-item>
    <el-collapse accordion>
      <el-collapse-item title="节点样式">
        <el-form-item label="每屏节点数">
          <el-input-number
            v-model="main.activeOption.nodenumber"
            controls-position="right"
            :min="0"
            :max="200"
          />
        </el-form-item>
        <el-form-item label="节点样式">
          <el-select v-model="main.activeOption.noodeType">
            <el-option
              v-for="item in nodeList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="图标" v-if="main.activeOption.noodeType===4">
          <div
            v-html="getNodeStyle"
            class="div-svg"
          ></div>
          <el-button
            plain
            icon="el-icon-edit"
            class="myBtn"
            size="mini"
            @click="handleChooseSvgClick('node')"
            >设置图标</el-button
          >
        </el-form-item>
        <el-form-item label="节点颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.nodeiconColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="节点大小">
          <el-input-number
            v-model="main.activeOption.nodeiconSize"
            controls-position="right"
            :min="0"
            :max="200"
          />
        </el-form-item>
      </el-collapse-item>
    </el-collapse>
    <el-collapse accordion>
      <el-collapse-item title="换乘点设置">
        <el-form-item label="显示">
          <avue-switch v-model="main.activeOption.transferShow"> </avue-switch>
        </el-form-item>
        <el-form-item label="换乘点颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.transferColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="换乘点大小">
          <el-input-number
            v-model="main.activeOption.transferSize"
            controls-position="right"
            :min="0"
            :max="200"
          />
        </el-form-item>
        <el-form-item label="图标">
          <div
            v-html="getTransferStyle"
            class="div-svg"
          ></div>
          <el-button
            plain
            icon="el-icon-edit"
            class="myBtn"
            size="mini"
            @click="handleChooseSvgClick('transfer')"
            >设置图标</el-button
          >
        </el-form-item>
      </el-collapse-item>
    </el-collapse>
    <el-collapse accordion>
      <el-collapse-item title="快进/后退设置">
        <el-form-item label="启用">
          <avue-switch v-model="main.activeOption.switchFalge"> </avue-switch>
        </el-form-item>
        <el-form-item label="图标样式">
          <el-select v-model="main.activeOption.switchType">
            <el-option
              v-for="item in forwardList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="图标颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.switchColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="图标大小">
          <el-input-number
            v-model="main.activeOption.switchNumber"
            controls-position="right"
            :min="0"
            :max="200"
          />
        </el-form-item>
      </el-collapse-item>
    </el-collapse>
    <el-collapse accordion>
      <el-collapse-item title="当前车位置设置">
        <el-form-item label="显示">
          <avue-switch v-model="main.activeOption.carShow"> </avue-switch>
        </el-form-item>
        <el-form-item label="车位置颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.carColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="车位置大小">
          <el-input-number
            v-model="main.activeOption.carNumber"
            controls-position="right"
            :min="0"
            :max="200"
          />
        </el-form-item>
        <el-form-item label="图标">
          <div
            v-html="getCartyle"
            class="div-svg"
          ></div>
          <el-button
            plain
            icon="el-icon-edit"
            class="myBtn"
            size="mini"
            @click="handleChooseSvgClick('car')"
            >设置图标</el-button
          >
        </el-form-item>
      </el-collapse-item>
    </el-collapse>
    <el-collapse accordion>
      <el-collapse-item title="轮播动效设置">
        <el-form-item label="自动轮播">
          <avue-switch v-model="main.activeOption.carousel"> </avue-switch>
        </el-form-item>
        <el-form-item label="间隔时间">
            <el-input-number
            v-model="main.activeOption.time"
            controls-position="right"
            :min="0"
          />
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
  </div>
</template>

<script>
import { dicOption } from '@/option/config'
import svgDiglog from '@/components/draw-warn/svgDialog'
import optionsGetFont from '@/mixins/optionsGetFont.js'
export default {
    mixins: [optionsGetFont],
  data() {
    return {
      svgType:'node',
      svgDialogVisible:false,
      currentSvgObj: {}, // 暂存当前选中的图标
      dicOption: dicOption,
      forwardList:[ {
          label:'单箭头',
          value:1
        },
        {
          label:'双箭头',
          value:2
        },
      ],
      characterPosition:[
        {
          label:'居上',
          value:1
        },
        {
          label:'居下',
          value:2
        },
      ],
      nodeList:[
        {
          label:'圆点',
          value:1
        },
        {
          label:'菱形',
          value:2
        },
        {
          label:'空心圆点',
          value:3
        },
        {
          label:'自定义',
          value:4
        }
      ],
    }
  },
  components: {
    svgDiglog,
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
    handleChooseSvgClick(type) {
      this.svgType = type
      this.svgDialogVisible = true // 打开选择图标对话框
    },
    // 当点击了选择图标对话框的确定按钮时
    confirmSvgObj() {
      console.log('当前选中的图标', this.currentSvgObj,this.svgType)
      if (!this.currentSvgObj.link)
        return this.$message.warning({ message: '请选择一个图标' })
        if(this.svgType==='node'){
          this.main.activeOption.nodeiconUrl = this.currentSvgObj.link
        }else if(this.svgType==='transfer'){
          this.main.activeOption.transfericonUrl = this.currentSvgObj.link
        }else if(this.svgType==='car'){
          this.main.activeOption.cariconUrl = this.currentSvgObj.link
        }
      this.svgDialogVisible = false
    },
    svgWithStyle(link,svgSizeNew,svgColor){
      let source = link
      let svgSize = svgSizeNew + 'px'
      let style = ` style = "width: ${svgSize} ; height: ${svgSize}; fill: ${svgColor}"`
      let reg = /((?<=<svg))/g
      let reg1 = /fill=\"(\S)*\"/g // 去除掉path标签中的fill,要不然无法改色
      let source1 = source.replace(reg1, 'fill')
      return source1.replace(reg, style)
    }
  },
  mounted(){
    
  },
  computed: {
    getNodeStyle(){
      let source = this.main.activeOption.nodeiconUrl
      let svgSize = this.main.activeOption.nodeiconSize + 'px'
      let svgColor = this.main.activeOption.nodeiconColor
      let style = ` style = "width: ${svgSize} ; height: ${svgSize}; fill: ${svgColor}"`
      let reg = /((?<=<svg))/g
      let reg1 = /fill=\"(\S)*\"/g // 去除掉path标签中的fill,要不然无法改色
      let source1 = source.replace(reg1, 'fill')
      return source1.replace(reg, style)
    },
    getTransferStyle(){
      let source = this.main.activeOption.transfericonUrl
      let svgSize = this.main.activeOption.transferSize + 'px'
      let svgColor = this.main.activeOption.transferColor
      let style = ` style = "width: ${svgSize} ; height: ${svgSize}; fill: ${svgColor}"`
      let reg = /((?<=<svg))/g
      let reg1 = /fill=\"(\S)*\"/g // 去除掉path标签中的fill,要不然无法改色
      let source1 = source.replace(reg1, 'fill')
      return source1.replace(reg, style)
    },
    getCartyle(){
      let source = this.main.activeOption.cariconUrl
      let svgSize = this.main.activeOption.carNumber + 'px'
      let svgColor = this.main.activeOption.carColor
      let style = ` style = "width: ${svgSize} ; height: ${svgSize}; fill: ${svgColor}"`
      let reg = /((?<=<svg))/g
      let reg1 = /fill=\"(\S)*\"/g // 去除掉path标签中的fill,要不然无法改色
      let source1 = source.replace(reg1, 'fill')
      return source1.replace(reg, style)
    }
  },
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
.top-style{
  color: #fff;
  font-size: 14px;
  padding-left: 20px;
}
</style>
