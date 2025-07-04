<!-- 选项卡配置 -->
<template>
  <div>
    <el-form-item label="自动轮播">
      <avue-switch v-model="main.activeOption.isRotation"></avue-switch>
    </el-form-item>
    <el-form-item label="轮播时间">
      <el-input-number
        v-model="main.activeOption.rotationTime"
        controls-position="right"
        :min="0"
      ></el-input-number>
    </el-form-item>
    <el-form-item label="选中第一个">
      <avue-switch v-model="main.activeOption.isFaist"></avue-switch>
    </el-form-item>
    <el-collapse accordion>
      <el-collapse-item title="全局设置">
        <el-form-item label="背景颜色">
          <avue-input-color
            v-model="main.activeOption.backgroundColor"
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
                main.handleOpenImg('activeOption.empBackgroundImage', 'border')
              "
              slot="append"
            >
              <i class="iconfont icon-img"></i>
            </div>
          </el-input>
        </el-form-item>
        <el-form-item label="宽度">
          <el-input-number
            v-model="main.activeOption.width"
            controls-position="right"
            :min="0"
            :max="10000"
          />
        </el-form-item>
        <el-form-item label="圆角">
          <el-input-number
            v-model="main.activeOption.radius"
            :min="0"
            :max="50"
            controls-position="right"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="字体大小">
          <el-input-number
            v-model="main.activeOption.fontSize"
            controls-position="right"
            :min="0"
            :max="200"
          />
        </el-form-item>
        <el-form-item label="文字粗细">
          <avue-select
            v-model="main.activeOption.fontWeight"
            :dic="dicOption.fontWeight"
          >
          </avue-select>
        </el-form-item>
        <el-form-item label="字体颜色">
          <avue-input-color
            v-model="main.activeOption.color"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="对齐方式">
          <avue-select
            v-model="main.activeOption.alignment"
            :dic="dicOption.alignment"
          >
          </avue-select>
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="下划线设置">
        <el-form-item label="下划线">
          <avue-switch v-model="main.activeOption.hasUnderline"></avue-switch>
        </el-form-item>
        <el-form-item
          label="下划线颜色"
          v-show="main.activeOption.hasUnderline"
        >
          <avue-input-color
            v-model="main.activeOption.underlineColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item
          label="下划线粗细"
          v-show="main.activeOption.hasUnderline"
        >
          <el-input-number
            controls-position="right"
            v-model="main.activeOption.underlineWeight"
            :max="10"
            :min="0"
          />
        </el-form-item>
        <!-- <el-form-item
          label="下划线长度"
          v-show="main.activeOption.hasUnderline"
        >
           <el-input-number
              controls-position="right"
               v-model="main.activeOption.underlineWidth"
            :max="10"
              :min="0"
            />
        </el-form-item> -->
        <el-form-item
          label="下划线圆角"
          v-show="main.activeOption.hasUnderline"
        >
          <el-input-number
            controls-position="right"
            v-model="main.activeOption.underlineRadius"
            :max="10"
            :min="0"
          />
        </el-form-item>
        <el-form-item
          label="下划线位置"
          v-show="main.activeOption.hasUnderline"
        >
          <avue-select
            v-model="main.activeOption.linePosition"
            :dic="dicOption.linePosition"
          >
          </avue-select>
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="图标设置">
        <el-form-item label="图标">
          <avue-switch v-model="main.activeOption.hasIcon"></avue-switch>
        </el-form-item>
        <el-form-item label="图标间距" v-show="main.activeOption.hasIcon">
          <el-input-number
            v-model="main.activeOption.iconInterval"
            :min="0"
            :max="50"
            controls-position="right"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="图标大小" v-show="main.activeOption.hasIcon">
          <el-input-number
            v-model="main.activeOption.iconSize"
            :min="0"
            :max="50"
            controls-position="right"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="图标地址" v-show="main.activeOption.hasIcon">
          <img
            v-if="main.activeOption.iconBackgroundImage"
            :src="main.activeOption.iconBackgroundImage"
            alt=""
            width="100%"
          />
          <el-input v-model="main.activeOption.iconBackgroundImage">
            <div
              @click="
                main.handleOpenImg('activeOption.iconBackgroundImage', 'border')
              "
              slot="append"
            >
              <i class="iconfont icon-img"></i>
            </div>
          </el-input>
        </el-form-item>
        <el-form-item label="图标位置" v-show="main.activeOption.hasIcon">
          <avue-select
            v-model="main.activeOption.iconPosition"
            :dic="dicOption.iconPosition"
          >
          </avue-select>
        </el-form-item>
      </el-collapse-item>

      <el-collapse-item title="选中设置">
        <el-form-item label="背景颜色">
          <avue-input-color
            v-model="main.activeOption.selectedBgColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="字体大小">
          <el-input-number
            controls-position="right"
            v-model="main.activeOption.selectedFontSize"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="字体颜色">
          <avue-input-color
            v-model="main.activeOption.selectedColor"
          ></avue-input-color>
        </el-form-item>
      </el-collapse-item>
    </el-collapse>
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

<style>
/* .test {
  align-items: center;
  border-bottom: 1px solid #3300fd;
} */
.activeItem {
  border-bottom: 1px solid #3300fd;
  justify-content: space-between;
  margin: 0 5px;
}
</style>
