<template>
  <div>
    <el-collapse accordion>
      <el-collapse-item title="线条设置">
        <el-form-item label="线条形状">
          <avue-select
            v-model="main.activeOption.lineShape"
            :dic="lineLsit"
          >
          </avue-select>
        </el-form-item>
        <el-form-item label="线条样式">
          <avue-select
            v-model="main.activeOption.linestyle"
            :dic="options"
          >
          </avue-select>
        </el-form-item>
        <el-form-item label="线条颜色">
          <avue-input-color v-model="main.activeOption.lineColor"></avue-input-color>
        </el-form-item>
        <el-form-item label="线条粗细">
          <el-input-number
            v-model="main.activeOption.lineWidth"
            controls-position="right"
            :min="1"
          />
        </el-form-item>
        <el-form-item label="显示连线文字">
          <avue-switch v-model="main.activeOption.characters"> </avue-switch>
        </el-form-item>
        <el-form-item label="字体颜色">
          <avue-input-color v-model="main.activeOption.charactersColor"></avue-input-color>
        </el-form-item>
        <el-form-item label="字体系列">
          <el-select v-model="main.activeOption.charactersFontFamily">
            <el-option
              v-for="(item, index) in allFontFamilyArr"
              :key="index"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="文字粗细">
          <avue-select
            v-model="main.activeOption.charactersFontWeight"
            :dic="dicOption.fontWeight"
          >
          </avue-select>
        </el-form-item>
        <el-form-item label="文字大小">
          <el-input-number
            v-model="main.activeOption.charactersFontSize"
            controls-position="right"
            :min="0"
          />
        </el-form-item>
      </el-collapse-item>
    </el-collapse>
    <el-collapse accordion>
      <el-collapse-item title="节点设置">
        <div class="titleone">1级节点：</div>
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
        <el-form-item label="文字粗细">
          <avue-select
            v-model="main.activeOption.fontWeight"
            :dic="dicOption.fontWeight"
          >
          </avue-select>
        </el-form-item>
        <el-form-item label="文字大小">
          <el-input-number
            v-model="main.activeOption.fontSize"
            controls-position="right"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="节点样式">
          <avue-select
            v-model="main.activeOption.type"
            :dic="tyleLsit"
          >
          </avue-select>
        </el-form-item>
        <el-form-item label="节点大小" v-show="main.activeOption.type==='circle'">
          <el-input-number
            v-model="main.activeOption.width"
            controls-position="right"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="节点大小" v-show="main.activeOption.type==='rect' || main.activeOption.type==='roundRect'">
          <div class="rect-type">
            <el-input-number
              v-model="main.activeOption.rectWidth"
              controls-position="right"
              :min="0"
            />
            <el-input-number
              v-model="main.activeOption.rectHeight"
              controls-position="right"
              style="margin-left:5px"
              :min="0"
            />
          </div>
        </el-form-item>
        <!-- <el-form-item label="圆角大小" v-show="main.activeOption.type==='rect' || main.activeOption.type==='roundRect'">
          <el-input-number
            v-model="main.activeOption.fillet"
            controls-position="right"
            :min="0"
          />
        </el-form-item> -->
        <el-form-item label="节点背景色">
          <avue-input-color v-model="main.activeOption.backgroundColor"></avue-input-color>
        </el-form-item>
        <el-form-item label="边框粗细">
          <el-input-number
            v-model="main.activeOption.boderWidth"
            controls-position="right"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="边框颜色">
          <avue-input-color v-model="main.activeOption.borderColor"></avue-input-color>
        </el-form-item>
        <div class="add-button">
            <el-button type="primary" size="mini"
              @click="addType" >+新增节点</el-button
            >
        </div>
        <div v-for="(item,index) in main.activeOption.list" :key="index">
          <div class="titleone">
            <span>{{index+2}}级节点：</span>
            <i class="el-icon-delete jie-delete" @click="getDelete(index)"></i>
          </div>
          <el-form-item label="字体颜色">
            <avue-input-color v-model="item.color"></avue-input-color>
          </el-form-item>
          <el-form-item label="字体系列">
            <el-select v-model="item.fontFamily">
              <el-option
                v-for="(item, index) in allFontFamilyArr"
                :key="index"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="文字粗细">
            <avue-select
              v-model="item.fontWeight"
              :dic="dicOption.fontWeight"
            >
            </avue-select>
          </el-form-item>
          <el-form-item label="文字大小">
          <el-input-number
            v-model="item.fontSize"
            controls-position="right"
            :min="0"
          />
          </el-form-item>
          <el-form-item label="节点样式">
            <avue-select
              v-model="item.type"
              :dic="tyleLsit"
            >
            </avue-select>
          </el-form-item>
          <el-form-item label="节点大小" v-show="item.type==='circle'">
            <el-input-number
              v-model="item.width"
              controls-position="right"
              :min="0"
            />
          </el-form-item>
          <el-form-item label="节点大小" v-show="item.type==='rect' || item.type==='roundRect'">
            <div class="rect-type">
              <el-input-number
                v-model="item.rectWidth"
                controls-position="right"
                :min="0"
              />
              <el-input-number
                v-model="item.rectHeight"
                controls-position="right"
                style="margin-left:5px"
                :min="0"
              />
            </div>
          </el-form-item>
          <!-- <el-form-item label="圆角大小" v-show="item.type==='rect' || item.type==='roundRect'">
            <el-input-number
              v-model="item.fillet"
              controls-position="right"
              :min="0"
            />
          </el-form-item> -->
          <el-form-item label="节点背景色">
            <avue-input-color v-model="item.backgroundColor"></avue-input-color>
          </el-form-item>
          <el-form-item label="边框粗细">
            <el-input-number
              v-model="item.boderWidth"
              controls-position="right"
              :min="0"
            />
          </el-form-item>
          <el-form-item label="边框颜色">
            <avue-input-color v-model="item.borderColor"></avue-input-color>
          </el-form-item>
        </div>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script>
import { dicOption } from '@/option/config'
import optionsGetFont from '@/mixins/optionsGetFont.js'
export default {
  mixins: [optionsGetFont],
  data() {
    return {
      dicOption: dicOption,
      tyleLsit:[ {
          label: '圆形',
          value: 'circle',
        },{
          label: '矩形',
          value: 'rect',
        },{
          label: '圆角矩形',
          value: 'roundRect',
        },
      ],
      lineLsit:[ {
          label: '直线',
          value: 0,
        },{
          label: '曲线',
          value: 0.3,
        },
      ],
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
  inject: ['main'],
  methods: {
    addType(){
      let parms = {
        color:'#fff',
        fontFamily:'SourceHanSansCN-Regular',
        fontWeight:'normal',
        type:'circle',
        width:70,
        fontSize:16,
        rectWidth:80,
        rectHeight:80,
        backgroundColor:"#309ef8",
        boderWidth:1,
        borderColor:"#30c0fa",
        fillet:0,
      }
      this.main.activeOption.list.push(parms)
    },
    getDelete(index){
      this.main.activeOption.list.splice(index,1)
    }
  },
  created() {
  },
}
</script>

<style lang="scss" scoped>
.titleone{
  position: relative;
  font-size: 16px;
  color: #859094;
  margin-left: 15px;
  margin-bottom: 5px;
}
.add-button {
  margin-left: 20px;
  margin-bottom: 15px;
}
.rect-type{
  display: flex;
}
.jie-delete{
  position: absolute;
  font-size: 14px;
  cursor: pointer;
  right: 25px;
  top: 10px;
}
</style>
