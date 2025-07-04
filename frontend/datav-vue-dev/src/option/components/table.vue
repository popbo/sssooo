<!-- 表格配置 -->
<template>
  <div>
    <!-- <el-form-item label="开启排名">
      <avue-switch v-model="main.activeOption.index"></avue-switch>
    </el-form-item> -->
    <!-- <el-form-item label="边框">
      <avue-switch v-model="main.activeOption.border"> </avue-switch>
    </el-form-item> -->
    <el-form-item label="表格网格线">
      <avue-switch v-model="main.activeOption.isGridlines"> </avue-switch>
    </el-form-item>
    <template v-if="main.activeOption.isGridlines">
      <el-form-item label="横向框线粗细">
        <el-input-number
          v-model="main.activeOption.borderXWidth"
          controls-position="right"
          :min="0"
        />
      </el-form-item>
      <el-form-item label="横向框线颜色">
        <avue-input-color
          type="textarea"
          v-model="main.activeOption.borderXColor"
        ></avue-input-color>
      </el-form-item>
      <el-form-item label="纵向框线粗细">
        <el-input-number
          v-model="main.activeOption.borderYWidth"
          controls-position="right"
          :min="0"
        />
      </el-form-item>
      <el-form-item label="纵向框线颜色">
        <avue-input-color
          type="textarea"
          v-model="main.activeOption.borderYColor"
        ></avue-input-color>
      </el-form-item>
    </template>
    <!-- <el-form-item label="追加模式">
      <avue-switch v-model="main.activeOption.dataAppend"> </avue-switch>
    </el-form-item> -->
    <!-- <el-form-item label="开启滚动">
      <avue-switch v-model="main.activeOption.scroll"> </avue-switch>
    </el-form-item> -->

    <!-- <template v-if="main.activeOption.scroll">
      <el-form-item label="滚动间隔">
        <el-input-number
          v-model="main.activeOption.scrollTime"
          controls-position="right"
          :min="0"
        />
      </el-form-item>
      <el-form-item label="滚动速度">
        <el-input-number
          v-model="main.activeOption.scrollSpeed"
          controls-position="right"
          :min="0"
        />
      </el-form-item>
    </template> -->
    <el-form-item label="自动轮播">
      <avue-switch v-model="main.activeOption.scroll"> </avue-switch>
    </el-form-item>
    <el-form-item label="轮播时间" v-if="main.activeOption.scroll">
      <el-input-number
        v-model="main.activeOption.rotationTime"
        controls-position="right"
        :min="0"
      />
    </el-form-item>
    <el-form-item label="动画模式" v-if="main.activeOption.scroll">
      <avue-select
        v-model="main.activeOption.animationModel"
        :dic="dicOption.animationModals"
      >
      </avue-select>
    </el-form-item>
    <el-form-item label="开启多选">
      <avue-switch v-model="main.activeOption.multipleChoice"> </avue-switch>
    </el-form-item>
    <el-form-item label="选中颜色" v-if="main.activeOption.multipleChoice">
      <avue-input-color
        type="textarea"
        v-model="main.activeOption.choiceColor"
      ></avue-input-color>
    </el-form-item>
    <el-form-item label="选项背景色" v-if="main.activeOption.multipleChoice">
      <avue-input-color
        type="textarea"
        v-model="main.activeOption.choiceBacolor"
      ></avue-input-color>
    </el-form-item>
    <el-form-item label="边框粗细" v-if="main.activeOption.multipleChoice">
      <el-input-number
        :min="1"
        v-model="main.activeOption.choiceBorder"
        controls-position="right"
      ></el-input-number>
    </el-form-item>
    <el-form-item label="边框颜色" v-if="main.activeOption.multipleChoice">
      <avue-input-color
        type="textarea"
        v-model="main.activeOption.choiceBorderColor"
      ></avue-input-color>
    </el-form-item>
    <el-collapse accordion>
      <el-collapse-item title="分页设置">
        <el-form-item label="是否分页">
          <avue-switch v-model="main.activeOption.showPagination"></avue-switch>
        </el-form-item>
        <el-form-item label="位置" label-width="130px">
          <el-select v-model="main.activeOption.paginationPosition">
            <el-option
              v-for="item in dicOption.paginationPosition"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="种类" label-width="130px">
          <el-select v-model="main.activeOption.paginationType">
            <el-option
              v-for="item in paginationTypeList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="宽度" label-width="130px">
          <el-input-number
            :min="30"
            v-model="main.activeOption.paginationWidth"
            controls-position="right"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="高度" label-width="130px">
          <el-input-number
            :min="30"
            v-model="main.activeOption.paginationHidth"
            controls-position="right"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="字体大小" label-width="130px">
          <el-input-number
            :min="12"
            v-model="main.activeOption.paginationFontSize"
            controls-position="right"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="文字粗细" label-width="130px">
          <avue-select
            v-model="main.activeOption.paginationNormal"
            :dic="dicOption.fontWeight"
          >
          </avue-select>
        </el-form-item>
        <el-form-item label="字体颜色" label-width="130px">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.paginationColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="选中字体颜色" label-width="130px">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.paginationScolor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="标签颜色" label-width="130px">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.paginationIconcolor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="标签大小" label-width="130px">
          <el-input-number
            :min="1"
            v-model="main.activeOption.paginationIconFiont"
            controls-position="right"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="悬停标签背景颜色" label-width="130px">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.paginationIconBcolor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="背景颜色" label-width="130px">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.paginationBcolor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="悬停背景颜色" label-width="130px">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.paginationXbcolor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="边框大小" label-width="130px">
          <el-input-number
            :min="0"
            v-model="main.activeOption.paginationBorder"
            controls-position="right"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="边框颜色" label-width="130px">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.paginationBorderColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="选中边框颜色" label-width="130px">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.paginationSborderColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="分页之间距离" label-width="130px">
          <el-input-number
            :min="0"
            v-model="main.activeOption.paginationMargin"
            controls-position="right"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="上边距" label-width="130px">
          <el-input-number
            :min="0"
            v-model="main.activeOption.paginationMarginTop"
            controls-position="right"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="选页框宽度" label-width="130px">
          <el-input-number
            :min="0"
            v-model="main.activeOption.xuanWidth"
            controls-position="right"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="选页框高度" label-width="130px">
          <el-input-number
            :min="0"
            v-model="main.activeOption.xuanHeight"
            controls-position="right"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="显示分页框宽度" label-width="130px">
          <el-input-number
            :min="0"
            v-model="main.activeOption.xanWidth"
            controls-position="right"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="显示分页框高度" label-width="130px">
          <el-input-number
            :min="0"
            v-model="main.activeOption.xanHight"
            controls-position="right"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="下拉框字体大小" label-width="130px">
          <el-input-number
            :min="0"
            v-model="main.activeOption.paginationSelectFonit"
            controls-position="right"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="下拉框字体颜色" label-width="130px">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.paginationSelectColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="下拉框背景颜色" label-width="130px">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.paginationSelectBcolor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="下拉框选中颜色" label-width="130px">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.paginationSelectScolor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="下拉框悬停颜色" label-width="130px">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.paginationSelectHcolor"
          ></avue-input-color>
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="表头设置">
        <el-form-item label="是否隐藏">
          <avue-switch v-model="main.activeOption.showHeader"></avue-switch>
        </el-form-item>
        <el-form-item label="表头网格线">
          <avue-switch v-model="main.activeOption.headerBorder"></avue-switch>
        </el-form-item>
        <el-form-item
          label="表头框线粗细"
          v-if="main.activeOption.headerBorder"
        >
          <el-input-number
            v-model="main.activeOption.headerBorderSize"
            controls-position="right"
            :min="0"
          />
        </el-form-item>
        <el-form-item
          label="表头框线颜色"
          v-if="main.activeOption.headerBorder"
        >
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.headerBorderColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="表头高度">
          <el-input-number
            :min="0"
            v-model="main.activeOption.headerHeight"
            controls-position="right"
          ></el-input-number>
        </el-form-item>
        <!-- <el-form-item label="字体行高">
          <el-input-number
            :min="0"
            v-model="main.activeOption.headerLineHeight"
            controls-position="right"
          ></el-input-number>
        </el-form-item> -->
        <el-form-item label="字体大小">
          <el-input-number
            :min="0"
            v-model="main.activeOption.headerFontSize"
            controls-position="right"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="字体粗细">
          <avue-select
            v-model="main.activeOption.headerFontWeight"
            :dic="dicOption.fontWeight"
          >
          </avue-select>
        </el-form-item>
        <el-form-item label="背景颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.headerBackground"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="字体颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.headerColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="对齐方式">
          <avue-select
            v-model="main.activeOption.headerTextAlign"
            :dic="dicOption.textAlign"
          >
          </avue-select>
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="表格设置">
        <el-form-item label="文本超出设置">
          <avue-radio
            v-model="main.activeOption.isOverflowEllipsis"
            :dic="dicOption.OverflowEllipsis"
          >
          </avue-radio>
        </el-form-item>
        <el-form-item label="显示行数" v-if="!main.activeOption.showPagination">
          <el-input-number
            v-model="main.activeOption.count"
            controls-position="right"
            :min="0"
          />
        </el-form-item>
        <!-- <el-form-item label="表格高度">
          <el-input-number
            v-model="main.activeOption.cellHeight"
            controls-position="right"
            :min="0"
          />
        </el-form-item> -->
        <el-form-item label="字体大小">
          <el-input-number
            v-model="main.activeOption.bodyFontSize"
            controls-position="right"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="文字行高">
          <el-input-number
            v-model="main.activeOption.tdLineHeight"
            controls-position="right"
            :min="1"
            :max="100"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="对齐方式">
          <avue-select
            v-model="main.activeOption.bodyTextAlign"
            :dic="dicOption.textAlign"
          >
          </avue-select>
        </el-form-item>
        <el-form-item label="文字颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.bodyColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="奇行颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.nthColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item label="偶行颜色">
          <avue-input-color
            type="textarea"
            v-model="main.activeOption.othColor"
          ></avue-input-color>
        </el-form-item>
      </el-collapse-item>
      <!-- <el-collapse-item title="表格列设置">
        <avue-crud
          :option="tableOption"
          :data="main.activeOption.column"
          @row-save="rowSave"
          @row-del="rowDel"
          @row-update="rowUpdate"
        >
          <template slot="menuLeft" slot-scope="{}">
            <el-button
              size="mini"
              icon="el-icon-s-operation"
              @click="openCode"
              type="primary"
              >编辑数据</el-button
            >
          </template>
        </avue-crud>
      </el-collapse-item> -->
      <el-collapse-item title="序列号设置">
        <el-form-item label="开启序列">
          <avue-switch v-model="main.activeOption.startSequence"></avue-switch>
        </el-form-item>
        <!-- <el-form-item label="开启排名" v-show="isStartSequence">
          <avue-switch v-mode
          l="main.activeOption.startRank"></avue-switch>
        </el-form-item> -->
        <el-form-item label="名称" v-show="isStartSequence">
          <el-input v-model="main.activeOption.nameProportion" />
        </el-form-item>
        <el-form-item label="图标类型" v-show="isStartSequence">
          <el-select v-model="main.activeOption.imgType">
            <el-option
              v-for="item in imgTypeList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          label="图标地址"
          v-show="isStartSequence && main.activeOption.imgType === 2"
        >
          <img
            v-if="main.activeOption.value"
            :src="main.activeOption.value"
            alt=""
            width="100%"
          />
          <el-input v-model="main.activeOption.value">
            <div
              @click="main.handleOpenImg('activeOption.value')"
              slot="append"
            >
              <i class="iconfont icon-img"></i>
            </div>
          </el-input>
        </el-form-item>
        <el-form-item label="列宽占比" v-show="isStartSequence">
          <el-input-number
            v-model="main.activeOption.widthProportion"
            controls-position="right"
            :min="0"
            :max="100"
          />
        </el-form-item>
        <el-form-item
          label="图片宽度"
          v-show="isStartSequence && main.activeOption.imgType === 2"
        >
          <el-input-number
            v-model="main.activeOption.imgWidth"
            controls-position="right"
            :min="0"
            :max="500"
          />
        </el-form-item>
        <el-form-item
          label="图片高度"
          v-show="isStartSequence && main.activeOption.imgType === 2"
        >
          <el-input-number
            v-model="main.activeOption.imgHeight"
            controls-position="right"
            :min="0"
            :max="500"
          />
        </el-form-item>
        <el-form-item
          label="序号占比"
          v-show="isStartSequence && main.activeOption.imgType === 1"
        >
          <el-input-number
            v-model="main.activeOption.sequenceProportion"
            controls-position="right"
            :min="0"
            :max="50"
          />
        </el-form-item>
        <el-form-item label="字体大小" v-show="isStartSequence">
          <el-input-number
            v-model="main.activeOption.fontSize"
            :min="0"
            controls-position="right"
          >
          </el-input-number>
        </el-form-item>
        <el-form-item label="字体颜色" v-show="isStartSequence">
          <avue-input-color
            v-model="main.activeOption.textColor"
          ></avue-input-color>
        </el-form-item>
        <el-form-item
          label="颜色跟随排名"
          v-show="isStartSequence && main.activeOption.imgType === 1"
        >
          <avue-switch
            v-model="main.activeOption.colorFollowRank"
          ></avue-switch>
        </el-form-item>
        <avue-crud
          :option="colorOption"
          :data="main.activeOption.iconColor"
          @row-save="colorSave"
          @row-del="colorDel"
          @row-update="colorUpdate"
          v-show="isStartSequence && main.activeOption.imgType === 1"
        ></avue-crud>
      </el-collapse-item>
      <el-collapse-item title="列操作设置">
        <el-form-item label="开启操作">
          <avue-switch v-model="main.activeOption.openOperation"></avue-switch>
        </el-form-item>
        <el-form-item
          label="操作列表头文字"
          v-if="main.activeOption.openOperation"
        >
          <el-input
            v-model.lazy="main.activeOption.operateText"
            size="mini"
            maxlength="10"
            show-word-limit
          ></el-input>
        </el-form-item>
        <el-form-item label="列宽占比" v-if="main.activeOption.openOperation">
          <el-input-number
            v-model="main.activeOption.operaColomnWidth"
            controls-position="right"
            :min="0"
            :max="100"
          >
          </el-input-number>
        </el-form-item>
        <el-form-item label="排列方式" v-if="main.activeOption.openOperation">
          <el-select v-model="main.activeOption.buttonArrangement">
            <el-option
              v-for="item in dicOption.lineDirection"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <div class="add-button" v-if="main.activeOption.openOperation">
          <el-button type="primary" size="mini" @click="addButton"
            >+新增按钮</el-button
          >
        </div>
        <div v-if="main.activeOption.openOperation">
          <div
            v-for="(item, index) in main.activeOption.buttonList"
            :key="index"
          >
            <div
              class="botton-type"
              @click="openButton(item)"
              :class="item.openDeploy ? 'open-type-b' : ''"
            >
              <i
                :class="
                  !item.openDeploy
                    ? 'el-icon-arrow-right'
                    : 'el-icon-arrow-down'
                "
              ></i>
              <span>功能按钮</span>
              <i class="el-icon-delete" @click.stop="deleteButton(index)"></i>
            </div>
            <div v-if="item.openDeploy" style="padding-left: 8px">
              <el-form-item label="按钮显示文字">
                <el-input
                  v-model.lazy="item.buttonContext"
                  size="mini"
                  maxlength="5"
                  show-word-limit
                ></el-input>
              </el-form-item>
              <el-form-item label="字体大小">
                <el-input-number
                  :min="0"
                  v-model="item.buttonFontSize"
                  controls-position="right"
                ></el-input-number>
              </el-form-item>
              <el-form-item label="字体颜色">
                <avue-input-color
                  type="textarea"
                  v-model="item.buttonColor"
                ></avue-input-color>
              </el-form-item>
              <el-form-item label="边框角度">
                <el-input-number
                  v-model="item.borderWidth"
                  controls-position="right"
                  :min="0"
                  :max="100"
                ></el-input-number>
              </el-form-item>
              <el-form-item label="背景色渐变">
                <avue-switch v-model="item.backgroundGradient"></avue-switch>
              </el-form-item>
              <el-form-item label="按钮背景色" v-if="!item.backgroundGradient">
                <avue-input-color v-model="item.buttonBgc"></avue-input-color>
              </el-form-item>
              <el-form-item label="颜色一" v-if="item.backgroundGradient">
                <avue-input-color v-model="item.bgColor1"></avue-input-color>
              </el-form-item>
              <el-form-item label="颜色二" v-if="item.backgroundGradient">
                <avue-input-color v-model="item.bgColor2"></avue-input-color>
              </el-form-item>
              <el-form-item label="按钮宽度">
                <el-input-number
                  v-model="item.buttonWidth"
                  controls-position="right"
                  :min="1"
                  :max="100"
                ></el-input-number>
              </el-form-item>
              <el-form-item label="按钮高度">
                <el-input-number
                  v-model="item.buttonHeight"
                  controls-position="right"
                  :min="1"
                  :max="100"
                ></el-input-number>
              </el-form-item>
              <el-form-item label="按钮边框粗细">
                <el-input-number
                  v-model="item.buttonBorderWidth"
                  controls-position="right"
                  :min="0"
                  :max="5"
                ></el-input-number>
              </el-form-item>
              <el-form-item label="按钮边框颜色" v-if="getTableList">
                <avue-input-color
                  v-model="item.buttonBorderColor"
                ></avue-input-color>
              </el-form-item>
            </div>
          </div>
        </div>
      </el-collapse-item>
      <el-collapse-item title="列宽设置">
        <el-form-item label="选择列">
          <el-select
            v-model="main.activeOption.currentColom"
            @change="getCurrentColom"
          >
            <el-option
              v-for="item in tableList"
              :key="item.prop"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="当前列宽占比">
          <el-input-number
            v-model="main.activeOption.currentWidth"
            controls-position="right"
            :min="0"
            :max="100"
            @change="setCurrentWidth"
          >
          </el-input-number>
        </el-form-item>
        <el-form-item label="设置本列为下拉选择">
          <avue-switch v-model="main.activeOption.tableSelect"></avue-switch>
        </el-form-item>
      </el-collapse-item>
    </el-collapse>
    <codeedit
      @submit="codeClose"
      v-model="code.obj"
      v-if="code.box"
      :type="code.type"
      :visible.sync="code.box"
    ></codeedit>
  </div>
</template>

<script>
import { tableOption, dicOption, colorOption2 } from '@/option/config'
import codeedit from '../../page/group/code'
export default {
  data() {
    return {
      dicOption: dicOption,
      tableOption: tableOption,
      tableList: [],
      // colorOption: colorOption2,
      code: {
        box: false,
        type: 'data',
        obj: {},
      },
      imgTypeList: [
        {
          label: '图标',
          value: 1,
        },
        {
          label: '图片',
          value: 2,
        },
      ],
      paginationTypeList: [
        {
          label: '类型一',
          value: 'total, sizes, prev, pager, next, jumper',
        },
        {
          label: '类型二',
          value: 'prev, pager, next',
        },
      ],
    }
  },
  inject: ['main'],
  components: {
    codeedit,
  },
  computed: {
    colorOption() {
      return colorOption2
    },
    isStartSequence() {
      return this.main.activeOption.startSequence
    },
  },
  watch: {
    'main.activeOption.openOperation': {
      handler(val) {
        if (val) {
          if (this.main.activeOption.buttonList.length === 0) {
            let parms = {
              tableButtonId: new Date().getTime(),
              openDeploy: false,
              buttonContext: '查看',
              buttonBgc: '#285178',
              buttonFontSize: 14,
              buttonColor: '#fff',
              buttonBorderWidth: 0,
              buttonBorderColor: '#008000',
              buttonWidth: 56,
              buttonHeight: 30,
              borderWidth: 0,
              backgroundGradient: false,
              bgColor1: '#1DE394',
              bgColor2: '#11C7EE',
            }
            this.main.activeOption.buttonList.push(parms)
          }
        }
      },
      deep: true,
    },
    'main.activeObj.option': {
      handler(val) {
        let tableList = []
        if (val.column) {
          let column = [...val.column]
          this.getTableParameter(tableList, column)
          this.tableList = tableList
        }
      },
      deep: true,
    },
    tableList: {
      handler() {
        let prams = this.tableList.find(item => {
          return item.value === this.main.activeOption.currentColom
        })
        if (!prams) {
          this.main.activeOption.currentColom = ''
          // this.main.activeOption.currentWidth = ''
        }
      },
    },
  },
  methods: {
    getTableParameter(tableParameter, list) {
      for (let i = 0; i < list.length; i++) {
        if (list[i].children && list[i].children.length) {
          this.getTableParameter(tableParameter, list[i].children)
        } else {
          tableParameter.push({
            value: list[i].prop,
            label: list[i].label,
          })
        }
      }
    },
    addButton() {
      let parms = {
        tableButtonId: new Date().getTime(),
        openDeploy: false,
        buttonContext: '查看',
        buttonBgc: '#285178',
        buttonFontSize: 14,
        buttonColor: '#fff',
        buttonBorderWidth: 0,
        buttonBorderColor: '#008000',
        buttonWidth: 56,
        buttonHeight: 30,
      }
      this.main.activeOption.buttonList.push(parms)
      this.getWidth()
    },
    getWidth(){
      let val =  this.main.activeOption.buttonList
      if(val.length>0){
        let widthData = 0
        val.forEach(item=>{
          widthData = (item.buttonWidth+12)+widthData
        })
        if(this.main.activeOption.buttonArrangement === 'transverse'){
          this.main.activeOption.operaColomnWidth = parseInt(widthData/6.9) < 16 ? 16 : parseInt(widthData/6.9)
        }else{
          this.main.activeOption.operaColomnWidth = 16
        }
      }
    },
    deleteButton(index) {
      if (this.main.activeOption.buttonList.length < 2) {
        return this.$message.warning('功能按钮至少有一个！')
      }
      this.main.activeOption.buttonList.splice(index, 1)
      this.getWidth()
    },
    openButton(item) {
      item.openDeploy = !item.openDeploy
    },
    rowSave(row, done) {
      this.main.activeOption.column.push(row)
      done()
    },
    rowDel(row, index) {
      this.main.activeOption.column.splice(index, 1)
    },
    rowUpdate(row, index, done) {
      this.main.activeOption.column.splice(index, 1, row)
      done()
    },
    codeClose(value) {
      this.main.activeOption.column = value
    },
    openCode() {
      this.code.obj = this.main.activeOption.column
      this.code.box = true
    },
    // 颜色操作
    colorSave(row, done) {
      this.main.activeOption.iconColor.push(row)
      done()
    },
    colorDel(row, index) {
      if (this.main.activeOption.iconColor.length > 1) {
        this.main.activeOption.iconColor.splice(index, 1)
      } else {
        this.$message.error('请至少保留一项')
      }
    },
    colorUpdate(row, index, done) {
      this.main.activeOption.iconColor.splice(index, 1, row)
      done()
    },
    getCurrentColom() {
      if (this.main.activeObj.option.column) {
        this.getCurrentColomWidth(this.main.activeObj.option.column)
      }
    },
    setCurrentWidth() {
      if (this.main.activeObj.option.column) {
        this.getCurrentColomWidth(this.main.activeObj.option.column, true)
      }
    },
    getCurrentColomWidth(column, falge) {
      if (column.length > 0) {
        column.forEach(item => {
          if (falge) {
            if (item.prop === this.main.activeOption.currentColom) {
              this.$set(item, 'width', this.main.activeOption.currentWidth)
            }
          } else {
            if (item.prop === this.main.activeOption.currentColom) {
              if (item.width) {
                this.main.activeOption.currentWidth = item.width
              } else {
                this.$set(item, 'width', this.main.activeOption.currentWidth)
              }
            }
          }
          if (item.children) {
            if (item.children.length > 0) {
              this.getCurrentColomWidth(item.children, falge)
            }
          }
        })
      }
    },
  },
  created() {
    let tableList = []

    if (this.main.activeObj.option) {
      if (this.main.activeObj.option.column) {
        console.log(this.main.activeObj.option, 99999)

        let column = [...this.main.activeObj.option.column]
        this.getTableParameter(tableList, column)
        this.tableList = tableList
      }
    }
  },
}
</script>

<style lang="scss" scoped>
/deep/ .avue-radio {
  .el-radio-group {
    width: 100%;
    .el-radio {
      width: 45%;
    }
  }
}
.add-button {
  margin-left: 20px;
  margin-bottom: 15px;
}
.botton-type {
  // display: flex;
  margin-left: 20px;
  width: 89%;
  height: 25px;
  color: #859094;
  margin-bottom: 10px;
  background-color: #3d404d;
  padding-left: 5px;
  line-height: 25px;
  cursor: pointer;
  .el-icon-arrow-right {
    color: #859094 !important;
    margin-right: 4px;
  }
  .el-icon-arrow-down {
    margin-right: 4px;
  }
  span {
    position: relative;
    top: -1px;
  }
  .el-icon-delete {
    margin-left: 190px;
  }
}
.open-type-b {
  color: #fff;
}
.el-input {
  /deep/.el-input__count {
    .el-input__count-inner {
      background: transparent;
    }
  }
}
</style>
