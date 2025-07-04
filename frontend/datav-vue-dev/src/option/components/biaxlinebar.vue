<template>
  <div>
    <el-collapse accordion>
      <el-collapse-item title="X轴设置">
          <el-form-item label="显示">
            <avue-switch v-model="main.activeOption.xAxisShow"> </avue-switch>
          </el-form-item>
          <el-form-item label="名称">
            <avue-input v-model="main.activeOption.xAxisName"> </avue-input>
          </el-form-item>
          <el-form-item label="名称字体大小">
            <el-input-number
              v-model="main.activeOption.xAxisNameSize"
              controls-position="right"
              :min="0"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="显示网格线">
            <avue-switch v-model="main.activeOption.xAxisSplitLineShow">
            </avue-switch>
          </el-form-item>
          <el-form-item label="网格线样式">
            <el-select
              v-model="main.activeOption.xAxisSplitLineType"
              placeholder="请选择"
              :disabled="main.activeOption.xAxisSplitLineShow ? false : true"
            >
              <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="网格线颜色">
            <div style="display: flex; align-items: center">
              <el-color-picker
                v-model="main.activeOption.xAxisSplitLineTypeColor"
                size="small"
              />
              <avue-input
                v-model="main.activeOption.xAxisSplitLineTypeColor"
                disabled
                style="width: 90px"
              />
            </div>
          </el-form-item>
          <el-form-item label="标签间距">
            <el-input-number
              v-model="main.activeOption.xAxisinterval"
              controls-position="right"
              :min="0"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="文字角度">
            <el-input-number
              v-model="main.activeOption.xAxisRotate"
              controls-position="right"
              :min="0"
            ></el-input-number>
          </el-form-item>
          <el-form-item
            label="轴反转"
            v-if="
              main.activeComponent &&
              main.activeComponent.prop !== 'stereoscopicBar'
            "
          >
            <avue-switch v-model="main.activeOption.xAxisInverse">
            </avue-switch>
          </el-form-item>
          <el-form-item label="标签字体大小">
            <el-input-number
              v-model="main.activeOption.xNameFontSize"
              controls-position="right"
              :min="0"
            ></el-input-number>
          </el-form-item>
          <el-form-item
            label="文字颜色"
          >
            <avue-input-color v-model="main.activeOption.nameColor">
            </avue-input-color>
          </el-form-item>
          <el-form-item
            label="轴线颜色"
          >
            <avue-input-color
              v-model="main.activeOption.lineColor"
            ></avue-input-color>
          </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="Y轴设置">
          <el-form-item label="显示">
            <avue-switch v-model="main.activeOption.yAxisShow"> </avue-switch>
          </el-form-item>
          <el-form-item label="轴网格线">
            <avue-switch v-model="main.activeOption.yAxisSplitLineShow">
            </avue-switch>
          </el-form-item>
          <el-form-item label="网格线样式">
            <el-select
              v-model="main.activeOption.yAxisSplitLineType"
              placeholder="请选择"
              :disabled="main.activeOption.yAxisSplitLineShow ? false : true"
            >
              <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="网格线颜色">
            <div style="display: flex; align-items: center">
              <el-color-picker
                v-model="main.activeOption.yAxisSplitLineTypeColor"
                size="small"
              />
              <avue-input
                v-model="main.activeOption.yAxisSplitLineTypeColor"
                disabled
                style="width: 90px"
              />
            </div>
          </el-form-item>
          <el-form-item
            label="反转"
            v-if="
              main.activeComponent &&
              main.activeComponent.prop !== 'stereoscopicBar'
            "
          >
            <avue-switch v-model="main.activeOption.yAxisInverse">
            </avue-switch>
          </el-form-item>
          <div class="axle-list">
            <div class="axle" @click="axleClick(index)" :class="aindex===index?'axle-active':''" v-for="(item,index) in axleList" :key="index">
            {{item}}
            </div>
          </div>
          <div v-show="aindex===0">
            <el-form-item label="名称">
              <avue-input v-model="main.activeOption.yLaxisName"> </avue-input>
            </el-form-item>
            <el-form-item label="名称字体大小">
              <el-input-number
                v-model="main.activeOption.yLaxisNameSize"
                controls-position="right"
                :min="0"
              ></el-input-number>
            </el-form-item>
            <el-form-item label="标签字体大小">
              <el-input-number
                v-model="main.activeOption.yLameFontSize"
                controls-position="right"
                :min="0"
              ></el-input-number>
            </el-form-item>
            <el-form-item label="轴线颜色">
              <div style="display: flex; align-items: center">
                <el-color-picker
                  v-model="main.activeOption.yLlineColor"
                  size="small"
                />
                <avue-input
                  v-model="main.activeOption.yLlineColor"
                  disabled
                  style="width: 90px"
                />
              </div>
            </el-form-item>
            <el-form-item label="字体颜色">
              <div style="display: flex; align-items: center">
                <el-color-picker
                  v-model="main.activeOption.yLaxisLabelColor"
                  size="small"
                />
                <avue-input
                  v-model="main.activeOption.yLaxisLabelColor"
                  disabled
                  style="width: 90px"
                />
              </div>
            </el-form-item>
          </div>
          <div v-show="aindex===1">
            <el-form-item label="名称">
              <avue-input v-model="main.activeOption.yRaxisName"> </avue-input>
            </el-form-item>
            <el-form-item label="名称字体大小">
              <el-input-number
                v-model="main.activeOption.yRaxisNameSize"
                controls-position="right"
                :min="0"
              ></el-input-number>
            </el-form-item>
            <el-form-item label="标签字体大小">
              <el-input-number
                v-model="main.activeOption.yRameFontSize"
                controls-position="right"
                :min="0"
              ></el-input-number>
            </el-form-item>
            <el-form-item label="轴线颜色">
              <div style="display: flex; align-items: center">
                <el-color-picker
                  v-model="main.activeOption.yRlineColor"
                  size="small"
                />
                <avue-input
                  v-model="main.activeOption.yRlineColor"
                  disabled
                  style="width: 90px"
                />
              </div>
            </el-form-item>
            <el-form-item label="字体颜色">
              <div style="display: flex; align-items: center">
                <el-color-picker
                  v-model="main.activeOption.yRaxisLabelColor"
                  size="small"
                />
                <avue-input
                  v-model="main.activeOption.yRaxisLabelColor"
                  disabled
                  style="width: 90px"
                />
              </div>
            </el-form-item>
          </div>
      </el-collapse-item>
      <el-collapse-item title="图形样式设置">
          <div>
            <div class="style-type">柱体样式 : </div>
              <el-form-item label="柱体宽度">
                <el-input-number
                  v-model="main.activeOption.barWidth"
                  controls-position="right"
                  :min="0"
                ></el-input-number>
              </el-form-item>
              <el-form-item label="柱体间距">
                <el-input-number
                  v-model="main.activeOption.barGap"
                  controls-position="right"
                  :min="0"
                ></el-input-number>
              </el-form-item>
          </div>
          <div>
            <div class="style-type">折线样式 : </div>
            <el-form-item label="平滑曲线">
              <avue-switch v-model="main.activeOption.smooth"> </avue-switch>
            </el-form-item>
            <el-form-item label="线条粗细">
              <el-input-number
                v-model="main.activeOption.lineWidth"
                controls-position="right"
                :min="0"
              ></el-input-number>
            </el-form-item>
            <el-form-item label="拐点显示">
              <avue-switch v-model="main.activeOption.showSymbol"> </avue-switch>
            </el-form-item>
            <el-form-item label="点的大小">
              <el-input-number
                v-model="main.activeOption.symbolSize"
                controls-position="right"
                :min="0"
              ></el-input-number>
            </el-form-item>
          </div>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script>
export default {
  inject: ["main"],
  data(){
    return{
      aindex:0,
      axleList:['左轴','右轴'],
      options: [
        {
          value: 'dashed',
          label: '------',
        },
        {
          value: 'dotted',
          label: '......',
        },
        {
          value: 'solid',
          label: '——————',
        },
      ],
    }
     
  },
  created(){
    console.log(this.main.activeOption);
  },
  methods:{
    axleClick(index){
      this.aindex = index
    }
  }
};
</script>
<style scoped>
.axle-list{
  padding-left: 20px;
  margin-bottom: 20px;
  display: flex;
}
.style-type{
  font-size: 14px;
  color: #859094;
  margin-left: 15px;
}
.axle{
  height: 35px;
  font-size: 14px;
  width: 100px;
  color: #fff;
  text-align: center;
  cursor: pointer;
  line-height: 35px;
  background-color: #909399;
}
.axle-active{
  background: #409eff;
}
</style>