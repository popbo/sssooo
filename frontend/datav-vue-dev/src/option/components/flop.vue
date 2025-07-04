<!-- 翻牌器配置 -->
<template>
  <div>
    <el-form-item label="整体">
      <avue-switch v-model="main.activeOption.whole"> </avue-switch>
    </el-form-item>
    <el-form-item label="精度">
      <el-input-number
        v-model="main.activeOption.decimals"
        controls-position="right"
        :min="0"
      />
    </el-form-item>
    <!-- <el-form-item label="行数">
      <el-input-number
        v-model="main.activeOption.span"
        controls-position="right"
        :min="0"
      />
    </el-form-item> -->
    <el-collapse accordion>
      <el-collapse-item title="边框设置">
        <el-form-item label="X间距">
          <el-input-number
            v-model="main.activeOption.splitx"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="Y间距">
          <el-input-number
            v-model="main.activeOption.splity"
            controls-position="right"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <!-- <el-form-item label="边框宽度">
       v-model="main.activeOption.width" :max="1000"
        </el-form-item>
        <el-form-item label="边框高度">
           <el-input-number
              controls-position="right"
              v-model="main.activeOption.height" :max="1000"
              :min="0"
            />
        </el-form-item> -->
        <el-form-item label="边框">
          <avue-radio v-model="main.activeOption.type" :dic="dicOption.border">
          </avue-radio>
        </el-form-item>
        <template v-if="main.activeOption.type === 'border'">
          <el-form-item label="边框颜色">
            <avue-input-color
              v-model="main.activeOption.borderColor"
            ></avue-input-color>
          </el-form-item>
          <el-form-item label="边框宽度">
            <el-input-number
              v-model="main.activeOption.borderWidth"
              controls-position="right"
              :min="0"
              :max="10"
            />
          </el-form-item>
        </template>
        <template v-if="main.activeOption.type === 'img'">
          <el-form-item label="图片地址">
            <img
              v-if="main.activeOption.backgroundBorder"
              :src="main.activeOption.backgroundBorder"
              alt=""
              width="100%"
            />
            <el-input v-model="main.activeOption.backgroundBorder">
              <div
                @click="
                  main.handleOpenImg('activeOption.backgroundBorder', 'border')
                "
                slot="append"
              >
                <i class="iconfont icon-img"></i>
              </div>
            </el-input>
          </el-form-item>
        </template>
        <el-form-item label="背景颜色">
          <avue-input-color
            v-model="main.activeOption.backgroundColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="背景图片">
          <img
            v-if="main.activeOption.backgroundImage"
            :src="main.activeOption.backgroundImage"
            alt=""
            width="100%"
          />
          <el-input v-model="main.activeOption.backgroundImage">
            <div
              @click="main.handleOpenImg('activeOption.backgroundImage', '')"
              slot="append"
            >
              <i class="iconfont icon-img"></i>
            </div>
          </el-input>
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="内部设置">
        <el-form-item label="字体大小">
          <el-input-number
            controls-position="right"
            v-model="main.activeOption.fontSize"
            :max="200"
            :min="0"
          />
        </el-form-item>
        <!-- <el-form-item label="字体高亮颜色">
          <avue-input-color v-model="main.activeOption.empColor"></avue-input-color>
        </el-form-item> -->
        <el-form-item label="字体颜色">
          <avue-input-color
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
      </el-collapse-item>
      <!-- <el-collapse-item title="前缀设置">
        <template v-if="!main.activeOption.whole">
          <el-form-item label="前缀内容">
            <avue-input v-model="main.activeOption.prefixText"></avue-input>
          </el-form-item>
        </template>
        <template v-if="!main.activeOption.row">
          <el-form-item label="对齐方式">
            <avue-select v-model="main.activeOption.prefixTextAlign" :dic="dicOption.textAlign">
            </avue-select>
          </el-form-item>
        </template>
        <el-form-item label="颜色">
          <avue-input-color v-model="main.activeOption.prefixColor"></avue-input-color>
        </el-form-item>
        <el-form-item label="字体大小">
           <el-input-number
              controls-position="right"
              v-model="main.activeOption.prefixFontSize" :max="200"
              :min="0"
            />
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="后缀设置">
        <template v-if="!main.activeOption.whole">
          <el-form-item label="后缀内容">
            <avue-input v-model="main.activeOption.suffixText"></avue-input>
          </el-form-item>
        </template>
        <template v-if="!main.activeOption.row">
          <el-form-item label="对齐方式">
            <avue-select v-model="main.activeOption.suffixTextAlign" :dic="dicOption.textAlign">
            </avue-select>
          </el-form-item>
        </template>
        <el-form-item label="颜色">
          <avue-input-color v-model="main.activeOption.suffixColor"></avue-input-color>
        </el-form-item>
        <el-form-item label="字体大小">
           <el-input-number
              controls-position="right"
              v-model="main.activeOption.suffixFontSize" :max="200"
              :min="0"
            />
        </el-form-item>
      </el-collapse-item> -->
      <!-- <el-collapse-item title="单个颜色块设置">
        <el-form-item :label="'颜色块' + (index + 1)" v-for="(item, index) in main.activeOption.colorFlopArr" :key="index">
          <i class="el-icon-edit" @click="editFlop(item)">编辑</i>
        </el-form-item>
      </el-collapse-item> -->
    </el-collapse>
    <el-dialog title="编辑" :visible.sync="dialogFormVisible">
      <el-form :model="currentForm" label-width="120px">
        <el-form-item label="颜色块背景色：">
          <avue-input-color
            v-model="currentForm.backgroundColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="前缀内容：">
          <avue-input v-model="currentForm.prefixText"></avue-input>
        </el-form-item>
        <el-form-item label="后缀内容：">
          <avue-input v-model="currentForm.suffixText"></avue-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="dialogFormVisible = false"
          >确 定</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { dicOption } from '@/option/config'
import optionsGetFont from '@/mixins/optionsGetFont.js'
export default {
  data() {
    return {
      dicOption: dicOption,
      currentForm: {},
      dialogFormVisible: false,
    }
  },
  mixins: [optionsGetFont],
  inject: ['main'],
  watch: {},
  methods: {
    editFlop(currentItem) {
      this.dialogFormVisible = true
      this.currentForm = currentItem
    },
  },
}
</script>

<style></style>
