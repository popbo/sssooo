<!-- 选项卡配置 -->
<template>
  <div>
    <template v-if="main.activeOption.type === 'tabs'">
      <el-form-item label="字体大小">
        <el-input-number
          controls-position="right"
          v-model="main.activeOption.fontSize"
          :max="200"
          :min="0"
        />
      </el-form-item>
      <el-form-item label="字体颜色">
        <avue-input-color v-model="main.activeOption.color"></avue-input-color>
      </el-form-item>
    </template>
    <el-form-item label="类型">
      <avue-radio
        v-model="main.activeOption.type"
        :dic="dicOption.tabsTypeList"
      ></avue-radio>
    </el-form-item>
    <template v-if="main.activeOption.type === 'tabs'">
      <el-form-item label="字体间距">
        <el-input-number
          v-model="main.activeOption.split"
          controls-position="right"
          :min="0"
        />
      </el-form-item>
      <el-collapse accordion>
        <el-collapse-item title="边框设置">
          <el-form-item label="背景颜色">
            <avue-input-color
              v-model="main.activeOption.backgroundColor"
            ></avue-input-color>
          </el-form-item>
          <el-form-item label="图标间距">
            <el-input-number
              v-model="main.activeOption.iconSplit"
              controls-position="right"
              :min="0"
            />
          </el-form-item>
          <el-form-item>
            <template slot="label">
              图标大小
              <el-tooltip
                class="item"
                effect="dark"
                content="图标可以在每条数据中添加icon和empIcon属性，属性值为图片地址，可参考静态数据中的写法"
                placement="top-start"
              >
                <i class="el-icon-info"></i>
              </el-tooltip>
            </template>
            <el-input-number
              v-model="main.activeOption.iconSize"
              controls-position="right"
              :min="0"
            />
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
                @click="
                  main.handleOpenImg('activeOption.backgroundImage', 'border')
                "
                slot="append"
              >
                <i class="iconfont icon-img"></i>
              </div>
            </el-input>
          </el-form-item>
          <el-form-item label="边框颜色">
            <avue-input-color
              v-model="main.activeOption.borderColor"
            ></avue-input-color>
          </el-form-item>
          <el-form-item label="边框宽度">
            <el-input-number
              v-model="main.activeOption.borderWidth"
              controls-position="right"
              :max="10"
            />
          </el-form-item>
        </el-collapse-item>
        <el-collapse-item title="高亮设置">
          <el-form-item label="字体高亮颜色">
            <avue-input-color
              v-model="main.activeOption.empColor"
            ></avue-input-color>
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
                @click="
                  main.handleOpenImg(
                    'activeOption.empBackgroundImage',
                    'border'
                  )
                "
                slot="append"
              >
                <i class="iconfont icon-img"></i>
              </div>
            </el-input>
          </el-form-item>
          <el-form-item label="边框颜色">
            <avue-input-color
              v-model="main.activeOption.empBorderColor"
            ></avue-input-color>
          </el-form-item>
          <el-form-item label="边框宽度">
            <el-input-number
              v-model="main.activeOption.empBorderWidth"
              controls-position="right"
              :min="0"
              :max="10"
            />
          </el-form-item>
        </el-collapse-item>
      </el-collapse>
    </template>
  </div>
</template>

<script>
import { dicOption } from '@/option/config'
export default {
  data() {
    return {
      dicOption: dicOption,
    }
  },
  inject: ['main'],
}
</script>

<style></style>
