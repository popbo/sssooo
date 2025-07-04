<!-- 文字配置 -->
<template>
  <div>
    <el-collapse accordion>
      <el-collapse-item
        title="跑马灯设置"
        v-show="currentComponent === 1"
      >
        <el-form-item label="开启">
          <avue-switch v-model="main.activeOption.scroll"></avue-switch>
        </el-form-item>
        <template v-if="main.activeOption.scroll">
          <el-form-item label="滚动速度">
            <avue-input v-model="main.activeOption.speed"></avue-input>
          </el-form-item>
        </template>
      </el-collapse-item>
      <el-collapse-item title="超链设置" v-show="currentComponent === 2">
        <el-form-item label="开启">
          <avue-switch v-model="main.activeOption.link"></avue-switch>
        </el-form-item>
        <template v-if="main.activeOption.link">
          <el-form-item label="打开方式">
            <avue-radio
              v-model="main.activeOption.linkTarget"
              :dic="dicOption.target"
            >
            </avue-radio>
          </el-form-item>
          <el-form-item label="超链地址">
            <avue-input v-model="main.activeOption.linkHref"></avue-input>
          </el-form-item>
        </template>
      </el-collapse-item>
    </el-collapse>
    <el-form-item label="文本内容">
      <avue-input v-model="main.activeObj.data.value"></avue-input>
    </el-form-item>
    <el-form-item label="字体大小">
      <el-input-number
        v-model="main.activeOption.fontSize"
        controls-position="right"
        :min="0"
        :max="200"
      />
    </el-form-item>
    <el-form-item label="字体颜色">
      <avue-input-color v-model="main.activeOption.color"></avue-input-color>
    </el-form-item>
    <el-form-item label="字体倾斜">
      <avue-switch v-model="main.activeOption.fontStyle"></avue-switch>
    </el-form-item>
    <el-form-item label="字体间距">
      <el-input-number
        v-model="main.activeOption.split"
        controls-position="right"
        :min="0"
        :max="400"
      />
    </el-form-item>
    <el-form-item label="字体行高">
      <el-input-number
        v-model="main.activeOption.lineHeight"
        controls-position="right"
        :min="0"
        :max="400"
      />
    </el-form-item>
    <el-form-item label="字体背景">
      <avue-input-color
        v-model="main.activeOption.backgroundColor"
      ></avue-input-color>
    </el-form-item>
    <template>
      <el-form-item label="背景颜色图片">
        <img
          v-if="main.activeOption.backgroundImg"
          :src="main.activeOption.backgroundImg"
          alt=""
          width="100%"
        />
        <el-input v-model="main.activeOption.backgroundImg">
          <div
            @click="
              main.handleOpenImg('activeOption.backgroundImg')
            "
            slot="append"
          >
            <i class="iconfont icon-img"></i>
          </div>
        </el-input>
      </el-form-item>
    </template>
    <!-- <el-form-item label="字体阴影">
      <avue-input v-model="main.activeOption.textShadow"></avue-input>
    </el-form-item> -->
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
    <el-form-item label="对齐方式">
      <avue-select
        v-model="main.activeOption.textAlign"
        :dic="dicOption.textAlign"
      >
      </avue-select>
    </el-form-item>
    <el-form-item label="边角大小">
      <el-input-number
        v-model="main.activeOption.borderWidth"
        controls-position="right"
        :min="0"
        :max="200"
      />
    </el-form-item>
    <el-collapse accordion>
      <el-collapse-item title="字体阴影">
        <el-form-item label="水平阴影位置">
          <el-input-number
            v-model="textShadow.hShadow"
            controls-position="right"
            :min="0"
            :max="10"
          />
        </el-form-item>
        <el-form-item label="垂直阴影位置">
            <el-input-number
              v-model="textShadow.vShadow"
              controls-position="right"
              :min="0"
              :max="10"
            />
        </el-form-item>
        <el-form-item label="模糊距离">
          <el-input-number
            v-model="textShadow.blur"
            controls-position="right"
            :min="0"
            :max="10"
          />
        </el-form-item>
        <el-form-item label="颜色">
          <avue-input-color v-model="textShadow.color"></avue-input-color>
        </el-form-item>
      </el-collapse-item>
    </el-collapse>
    <el-collapse accordion v-show="currentComponent === 0">
      <el-collapse-item title="选中设置">
        <el-form-item label="选中开关">
          <avue-switch v-model="main.activeOption.clickSelect"></avue-switch>
        </el-form-item>
        <el-form-item label="字体颜色">
          <avue-input-color v-model="main.activeOption.selectColor"></avue-input-color>
        </el-form-item>
        <el-form-item label="背景颜色">
          <avue-input-color v-model="main.activeOption.selectBackground"></avue-input-color>
        </el-form-item>
        <template>
          <el-form-item label="背景颜色图片">
            <img
              v-if="main.activeOption.selectImg"
              :src="main.activeOption.selectImg"
              alt=""
              width="100%"
            />
            <el-input v-model="main.activeOption.selectImg">
              <div
                @click="
                  main.handleOpenImg('activeOption.selectImg')
                "
                slot="append"
              >
                <i class="iconfont icon-img"></i>
              </div>
            </el-input>
          </el-form-item>
        </template>
        <el-form-item label="下划线">
          <avue-switch v-model="main.activeOption.underline"></avue-switch>
        </el-form-item>
        <el-form-item label="下划线高度">
          <el-input-number
            v-model="main.activeOption.underlineHeight"
            controls-position="right"
            :min="0"
            :max="400"
          />
        </el-form-item>
        <el-form-item label="下划线颜色">
          <avue-input-color v-model="main.activeOption.underlineColor"></avue-input-color>
        </el-form-item>
        <el-form-item label="取消选中">
          <avue-switch v-model="main.activeOption.cancelClickSelect"></avue-switch>
        </el-form-item>
        <el-form-item label="时间" v-show="main.activeOption.cancelClickSelect">
          <el-input-number
            v-model="main.activeOption.cancelTime"
            controls-position="right"
            :min="0"
          />
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="单位设置">
        <el-form-item label="单位开关">
          <avue-switch v-model="main.activeOption.unitFlage"></avue-switch>
        </el-form-item>
        <el-form-item label="单位内容">
          <avue-input v-model="main.activeOption.unitText"></avue-input>
        </el-form-item>
        <el-form-item label="字体大小">
          <el-input-number
            v-model="main.activeOption.unitFontSize"
            controls-position="right"
            :min="0"
            :max="200"
          />
        </el-form-item>
        <el-form-item label="字体颜色">
          <avue-input-color v-model="main.activeOption.unitColor"></avue-input-color>
        </el-form-item>
        <el-form-item label="文字粗细">
          <avue-select
            v-model="main.activeOption.unitFontWeight"
            :dic="dicOption.fontWeight"
          >
          </avue-select>
        </el-form-item>
        <el-form-item label="字体系列">
          <el-select v-model="main.activeOption.unitFontFamily">
            <el-option
              v-for="(item, index) in allFontFamilyArr"
              :key="index"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="上边距">
          <el-input-number
            v-model="main.activeOption.unitTop"
            controls-position="right"
            :min="0"
            :max="200"
          />
        </el-form-item>
        <el-form-item label="左边距">
          <el-input-number
            v-model="main.activeOption.unitLeft"
            controls-position="right"
            :min="0"
            :max="200"
          />
        </el-form-item>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script>
import { dicOption } from '@/option/config'
import optionsGetFont from '@/mixins/optionsGetFont.js'
export default {
  mixins: [optionsGetFont],
  inject: ['main'],
  data() {
    return {
      textShadow: {
        hShadow: 0,
        vShadow: 0,
        blur: 0,
        color: 'rgba(255,255,255,0)',
      },
      dicOption: dicOption,
    }
  },
  methods: {
    // 获取初始阴影值赋值给textShadow设置组
    getInitTextShadow() {
      this.textShadow = this.main.activeOption.textShadow
    },
  },
  watch: {
    textShadow: {
      deep: true,
      handler(newValue) {
        this.main.activeOption.textShadow = newValue
      },
    },
  },
  computed: {
    currentComponent() {
      if(this.main.activeObj.type===undefined){
        if(this.main.activeObj.name==='文本框'){
          this.main.activeObj.type = 0
        }
        if(this.main.activeObj.name==='跑马灯'){
          this.main.activeObj.type = 1
        }
        if(this.main.activeObj.name==='超链接'){
          this.main.activeObj.type = 2
        }
      }
      return this.main.activeObj.type || 0
    },
  },
  created() {
    this.getInitTextShadow()
  },
  updated() {
    this.getInitTextShadow()
  },
}
</script>

<style lang="scss" scoped>
.shadow-item {
  font-size: 12px;
}
</style>
