<!-- 选项卡配置 -->
<template>
  <div>
    <!-- <el-form-item label="点击空白隐藏">
      <avue-switch v-model="main.activeOption.showBySpace"></avue-switch>
    </el-form-item>
    <el-form-item label="显示位置">
      <avue-input-number v-model="main.activeOption.fontSize" :max="200"></avue-input-number>
      <avue-radio
        v-model="main.activeOption.showPosition"
        :dic="dicOption.positionList"
      ></avue-radio>
    </el-form-item>
    <el-form-item label="请求地址">
      <avue-input v-model="main.activeOption.URL"></avue-input>
    </el-form-item>
    <el-form-item label="请求方法">
      <avue-select
        :dic="methodList"
        v-model="main.activeOption.method"
      ></avue-select>
    </el-form-item>
    <el-form-item label="认证方式">
      <avue-select :dic="authList" v-model="main.activeOption.auth"></avue-select>
    </el-form-item>
    <el-form-item v-if="main.activeOption.auth=='Basic Auth'" label="用户名">
      <avue-input v-model="main.activeOption.name"></avue-input>
    </el-form-item>
    <el-form-item v-if="main.activeOption.auth=='Basic Auth'" label="密码">
      <avue-input v-model="main.activeOption.passWorld"></avue-input>
    </el-form-item>
    <el-form-item v-if="main.activeOption.auth=='Basic Auth'" label="认证地址">
      <avue-input v-model="main.activeOption.authUrl"></avue-input>
    </el-form-item> -->
    <el-form-item label="每行开关数量">
      <el-input-number
        v-model="main.activeOption.number"
        controls-position="right"
        :min="0"
        :max="200"
      />
    </el-form-item>
    <el-form-item label="开关方向">
      <avue-select
        :dic="directionList"
        v-model="main.activeOption.direction"
      ></avue-select>
    </el-form-item>
    <el-form-item label="开关组间距">
      <div class="spacing-text">
        <div class="spacing">
          <span style="margin-right: 5px">左</span>
          <el-input-number
            v-model="main.activeOption.spacingleft"
            controls-position="right"
            :min="0"
            :max="200"
          />
        </div>
        <div class="spacing">
          <span>右</span>
          <el-input-number
            v-model="main.activeOption.spacingRight"
            controls-position="right"
            :min="0"
            :max="200"
          />
        </div>
      </div>
      <div class="spacing-text">
        <div class="spacing">
          <span style="margin-right: 5px">上</span>
          <el-input-number
            v-model="main.activeOption.spacingUpper"
            controls-position="right"
            :min="0"
            :max="200"
          />
        </div>
        <div class="spacing">
          <span>下</span>
          <el-input-number
            v-model="main.activeOption.spacinglower"
            controls-position="right"
            :min="0"
            :max="200"
          />
        </div>
      </div>
    </el-form-item>
    <el-form-item label="字体颜色">
      <avue-input-color v-model="main.activeOption.color"></avue-input-color>
    </el-form-item>
    <el-form-item label="字体">
      <div class="font-d">
        <div class="font-a">
          <el-input-number
            v-model="main.activeOption.fontSize"
            controls-position="right"
            :min="0"
            :max="200"
          />
        </div>
        <div class="font-b">
          <el-select v-model="main.activeOption.fontFamily">
            <el-option
              v-for="(item, index) in allFontFamilyArr"
              :key="index"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </div>
      </div>
    </el-form-item>
    <!-- <el-form-item label="滑块颜色">
      <div style="display: flex">
        <avue-input-color
          v-model="main.activeOption.sliderColor"
        ></avue-input-color>
      </div>
    </el-form-item>
    <el-form-item label="边框">
      <div style="display: flex">
        <avue-input-color
          v-model="main.activeOption.borderColor"
        ></avue-input-color>
        <el-input-number
          v-model="main.activeOption.borderNumber"
          controls-position="right"
          style="width: 71%"
          :min="0"
          :max="200"
        />
      </div>
    </el-form-item> -->
    <el-collapse>
      <el-collapse-item title="开启样式设置">
        <el-form-item label="标题">
          <avue-input v-model="main.activeOption.openTitle"></avue-input>
        </el-form-item>
        <el-form-item label="字体颜色">
          <avue-input-color
            v-model="main.activeOption.openTitleColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="字体">
          <div class="font-d">
            <div class="font-a">
              <el-input-number
                v-model="main.activeOption.openTitleFont"
                controls-position="right"
                :min="0"
                :max="200"
              />
            </div>
            <div class="font-b">
              <el-select v-model="main.activeOption.openTitleFamily">
                <el-option
                  v-for="(item, index) in allFontFamilyArr"
                  :key="index"
                  :label="item.label"
                  :value="item.value"
                ></el-option>
              </el-select>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="滑块颜色">
          <div style="display: flex">
            <avue-input-color
              v-model="main.activeOption.openSliderColor"
            ></avue-input-color>
          </div>
        </el-form-item>
        <el-form-item label="边框">
          <div style="display: flex">
            <avue-input-color
              v-model="main.activeOption.openBorderColor"
            ></avue-input-color>
            <el-input-number
              v-model="main.activeOption.openBorderNumber"
              controls-position="right"
              style="width: 71%"
              :min="0"
              :max="200"
            />
          </div>
        </el-form-item>
        <el-form-item label="背景颜色">
          <div style="display: flex">
            <avue-input-color
              v-model="main.activeOption.openBackgroundColor"
            ></avue-input-color>
          </div>
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="关闭样式设置">
        <el-form-item label="标题">
          <avue-input v-model="main.activeOption.closeTitle"></avue-input>
        </el-form-item>
        <el-form-item label="字体颜色">
          <avue-input-color
            v-model="main.activeOption.closeTitleColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="字体">
          <div class="font-d">
            <div class="font-a">
              <el-input-number
                v-model="main.activeOption.closeTitleFont"
                controls-position="right"
                :min="0"
                :max="200"
              />
            </div>
            <div class="font-b">
              <el-select v-model="main.activeOption.closeTitleFamily">
                <el-option
                  v-for="(item, index) in allFontFamilyArr"
                  :key="index"
                  :label="item.label"
                  :value="item.value"
                ></el-option>
              </el-select>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="滑块颜色">
          <div style="display: flex">
            <avue-input-color
              v-model="main.activeOption.closeSliderColor"
            ></avue-input-color>
          </div>
        </el-form-item>
        <el-form-item label="边框">
          <div style="display: flex">
            <avue-input-color
              v-model="main.activeOption.closeBorderColor"
            ></avue-input-color>
            <el-input-number
              v-model="main.activeOption.closeBorderNumber"
              controls-position="right"
              style="width: 71%"
              :min="0"
              :max="200"
            />
          </div>
        </el-form-item>
        <el-form-item label="背景颜色">
          <div style="display: flex">
            <avue-input-color
              v-model="main.activeOption.closeBackgroundColor"
            ></avue-input-color>
          </div>
        </el-form-item>
      </el-collapse-item>
    </el-collapse>
    <!-- <el-form-item label="开启Key">
      <avue-input v-model="main.activeOption.openKey"></avue-input>
    </el-form-item>
    <el-form-item label="开启value">
      <avue-input v-model="main.activeOption.openValue"></avue-input>
    </el-form-item>
    <el-form-item label="关闭Key">
      <avue-input v-model="main.activeOption.closeKey"></avue-input>
    </el-form-item>
    <el-form-item label="关闭Value">
      <avue-input v-model="main.activeOption.closeValue"></avue-input>
    </el-form-item>
    <el-form-item label="显示main">
      <el-button @click="showMain">显示main</el-button>
    </el-form-item> -->

    <!-- <el-form-item label="字体颜色">
      <avue-input-color v-model="main.activeOption.color"></avue-input-color>
    </el-form-item>
    <el-form-item label="类型">
      <avue-radio v-model="main.activeOption.type" :dic="dicOption.tabsTypeList"></avue-radio>
    </el-form-item>
    <template v-if="main.activeOption.type === 'tabs'">
      <el-form-item label="字体间距">
        <avue-input-number v-model="main.activeOption.split"></avue-input-number>
      </el-form-item>
      <el-collapse accordion>
        <el-collapse-item title="边框设置">
          <el-form-item label="背景颜色">
            <avue-input-color v-model="main.activeOption.backgroundColor"></avue-input-color>
          </el-form-item>
          <el-form-item label="图标间距">
            <avue-input-number v-model="main.activeOption.iconSplit"></avue-input-number>
          </el-form-item>
          <el-form-item label="图标大小">
            <avue-input-number v-model="main.activeOption.iconSize"></avue-input-number>
          </el-form-item>
          <el-form-item label="图片地址">
            <img
              v-if="main.activeOption.backgroundImage"
              :src="main.activeOption.backgroundImage"
              alt=""
              width="100%"
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
          <el-form-item label="边框颜色">
            <avue-input-color v-model="main.activeOption.borderColor"></avue-input-color>
          </el-form-item>
          <el-form-item label="边框宽度">
            <avue-input-number v-model="main.activeOption.borderWidth" :max="10">
            </avue-input-number>
          </el-form-item>
        </el-collapse-item>
        <el-collapse-item title="高亮设置">
          <el-form-item label="字体高亮颜色">
            <avue-input-color v-model="main.activeOption.empColor"></avue-input-color>
          </el-form-item>
          <el-form-item label="背景图片">
            <img
              v-if="main.activeOption.empBackgroundImage"
              :src="main.activeOption.empBackgroundImage"
              alt=""
              width="100%"
            />
            <el-input v-model="main.activeOption.empBackgroundImage">
              <div
                @click="main.handleOpenImg('activeOption.empBackgroundImage', 'border')"
                slot="append"
              >
                <i class="iconfont icon-img"></i>
              </div>
            </el-input>
          </el-form-item>
          <el-form-item label="边框颜色">
            <avue-input-color v-model="main.activeOption.empBorderColor"></avue-input-color>
          </el-form-item>
          <el-form-item label="边框宽度">
            <avue-input-number v-model="main.activeOption.empBorderWidth" :max="10">
            </avue-input-number>
          </el-form-item>
        </el-collapse-item>
      </el-collapse>
    </template> -->
  </div>
</template>

<script>
import { dicOption } from '@/option/config'
import optionsGetFont from '@/mixins/optionsGetFont.js'
export default {
  data() {
    return {
      dicOption: dicOption,
      methodList: [
        {
          label: 'GET',
          value: 'GET',
        },
        {
          label: 'POST',
          value: 'POST',
        },
      ],
      directionList: [
        {
          label: '横向',
          value: 1,
        },
        {
          label: '纵向',
          value: 2,
        },
      ],
      authList: [
        {
          label: 'no-auth',
          value: 'no-auth',
        },
        {
          label: 'Basic Auth',
          value: 'Basic Auth',
        },
      ],
    }
  },
  mixins: [optionsGetFont],
  inject: ['main'],
  methods: {
    showMain() {
      console.log('main==>', this.main.activeOption)
    },
  },
  computed: {},
}
</script>
<style scoped>
.font-d {
  display: flex;
}
.font-a {
  width: 50%;
}
.font-b {
  width: 50%;
}
.spacing {
  display: flex;
  width: 60%;
}
.spacing-text {
  display: flex;
  margin-bottom: 15px;
}
</style>
