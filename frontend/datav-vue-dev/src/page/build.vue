<template>
  <div class="build" :style="{
    '--containerWidth':containerWidth+'px',
    '--containerHeight':containerHeight+'px'
  }">
    <imglist ref="imglist" @change="handleSetimg"></imglist>
    <engineeringEdit
      ref="engineeringEdit"
      @engineeringOk="engineeringOk"
    ></engineeringEdit>
    <editColor
      ref="editColor"
      @saveColor='getSaveColor'
      :colorList='wholeColorList'
    ></editColor>
    <threeModelType ref="threeDimglist"></threeModelType>
    <headers
      ref="headers"
      :releaseDialogVisible.sync="releaseDialogVisible"
      :releaseInfo.sync="releaseInfo"
      :isReleaseFlag.sync="isReleaseFlag"
    ></headers>
    <top ref="top"></top>
    <div class="app" :class="{ 'app--none': !menuFlag }">
      <div
        class="menu"
        v-show="menuFlag && menuShow"
        @click.self="handleMouseDown"
      >
        <!-- <p class="title">图层</p> -->
        <div class="btn_tool_box">
          <div class="head_btn" @click="handleToolClick('StepTop')">
            <el-tooltip effect="dark" content="上移" placement="top">
              <i class="iconfont icon-shangyi"></i>
            </el-tooltip>
          </div>
          <div class="head_btn" @click="handleToolClick('StepBottom')">
            <el-tooltip effect="dark" content="下移" placement="top">
              <i class="iconfont icon-xiayi-copy"></i>
            </el-tooltip>
          </div>
          <div class="head_btn" @click="handleToolClick('Bottom')">
            <el-tooltip effect="dark" content="置底" placement="top">
              <i class="iconfont icon-zhidi-copy"></i>
            </el-tooltip>
          </div>
          <div class="head_btn" @click="handleToolClick('Top')">
            <el-tooltip effect="dark" content="置顶" placement="top">
              <i class="iconfont icon-zhiding"></i>
            </el-tooltip>
          </div>
        </div>
        <layer
          ref="layer"
          :nav="showTips ? navTips : nav"
          :serachVal="serachVal"
        >
          <template v-slot:header>
            <el-input
              class="search_input"
              placeholder="请输入图层名称"
              prefix-icon="el-icon-search"
              v-model.trim="serachVal"
              size="mini"
            >
            </el-input>
          </template>
        </layer>
      </div>
      <!-- 添加组件展示区域 开始 -->
      <!-- <div class="menu" v-show="menuFlag&&componentShow" @click.self="handleMouseDown">
        <p class="title">组件</p>
        <componentMenu ref="componentMenu"></componentMenu>
      </div> -->
      <!-- 图层和组件改为tab标签页的形式 -->
      <!-- <el-tabs v-model="activeName" @tab-click="handleClick" class="tabs menu left-tab" stretch>
        <el-tab-pane label="图层" name="first">
          <layer ref="layer" :nav="nav"></layer>
        </el-tab-pane>
        <el-tab-pane label="组件" name="second">
          <componentMenu ref="componentMenu"></componentMenu>
        </el-tab-pane>
      </el-tabs> -->
      <!-- 添加组件展示区域 结束 -->
      <!-- 中间区域 -->
      <div ref="section" class="section">
        <div class="refer-line-img" @click="imgClick">
          <img :src="isShowReferLine ? imgOpenData : imgClose" />
        </div>
        <sketch-rule
          :thick="thick"
          :scale="scale"
          :width="width"
          :height="height"
          :startX="startX"
          :startY="startY"
          :isShowReferLine="isShowReferLine"
          :palette="palette"
          :shadow="shadow"
          :horLineArr="lines.h"
          :verLineArr="lines.v"
        />
        <div
          ref="screensRef"
          id="screens"
          :class="dragSlide ? 'dragghanle' : ''"
          @mousedown.stop="dragMousedown"
          @mouseup="dragMouseup"
          @mousemove="dragMousemove"
          @wheel="handleWheel"
          @scroll="handleScroll"
        >
          <div ref="containerRef" class="screen-container">
            <div class="canvas" ref="canvas" :style="canvasStyle">
              <container
                v-show="!showTips"
                ref="container"
                :wscale="scale"
              ></container>
              <editTips
                v-if="showTips"
                ref="editTips"
                :wscale="scale"
                :showTipsCopy='showTipsCopy'
              ></editTips>
            </div>
          </div>
        </div>
        <!-- 这个区域表明整个大屏的尺寸 缩放比例 点击组件后组件的位置坐标 -->
        <position
          :wscale.sync="scale"
          :activeObj="activeObj"
          :activeComponent="activeComponent"
        ></position>
      </div>
      <div class="menu params" v-show="menuFlag && paramsShow">
        <!-- <p class="title">操作</p> -->
        <el-tabs class="tabs" stretch v-model="tabsActive">
          <el-tab-pane name="0">
            <el-tooltip
              slot="label"
              effect="dark"
              content="配置"
              placement="top"
            >
              <div><i class="el-icon-setting"></i>配置</div>
            </el-tooltip>
            <el-form label-width="110px" label-position="left" size="mini">
              <!-- 组件配置 -->
              <template v-if="!vaildProp('', [undefined])">
                <p class="title">{{ activeObj.title }}</p>
                <el-form-item label="序号">
                  <div style="display: flex;">
                    <el-input :id="activeObj.index" v-model="activeObj.index" readonly></el-input>
                    <i @click="copyImg(activeObj.index)" title="复制" class="el-icon-document-copy copy-f" style="margin-top: 7px;margin-left: 2px;cursor: pointer;"></i>
                  </div>
                </el-form-item>
                <el-form-item>
                  <template slot="label">
                    图层名称
                    <el-tooltip
                      class="item"
                      effect="dark"
                      content="该值可以作为事件中数据刷新的参数值, API联动的键值名称需要写为comName"
                      placement="top-start"
                    >
                      <i class="el-icon-info"></i>
                    </el-tooltip>
                  </template>
                  <avue-input v-model="activeObj.name"></avue-input>
                </el-form-item>
                <el-form-item>
                  <template slot="label">
                    图层参数值
                    <el-tooltip
                      class="item"
                      effect="dark"
                      content="该值可以作为事件中数据刷新的参数值, API联动的键值名称需要写为comParams"
                      placement="top-start"
                    >
                      <i class="el-icon-info"></i>
                    </el-tooltip>
                  </template>
                  <avue-input v-model="activeObj.comParams"></avue-input>
                </el-form-item>
                <el-form-item label="隐藏">
                  <avue-switch v-model="activeObj.display"></avue-switch>
                </el-form-item>
                <el-form-item label="关联设置" v-if="isDataStorage">
                  <el-button
                    size="mini"
                    plain
                    class="block myBtn"
                    @click="relevanceCom"
                    >关联组件</el-button
                  >
                </el-form-item>
                <!-- <template v-if="vaildProp('colorList')">
                  <el-form-item label="系统配色">
                    <avue-switch v-model="activeOption.switchTheme"></avue-switch>
                  </el-form-item>
                  <el-form-item label="配色选择" v-if="activeOption.switchTheme">
                    <avue-select v-model="activeOption.theme" :dic="dicOption.themeList">
                    </avue-select>
                  </el-form-item>
                </template> -->
                <component :is="activeComponent.prop + 'Option'"></component>
                <main-option></main-option>
              </template>
              <!-- 多选配置选项 -->
              <template v-else-if="isSelectActive">
                <el-form-item label="水平方式">
                  <el-tooltip content="左对齐" placement="top">
                    <i
                      class="el-icon-s-fold icon"
                      @click="$refs.container.handlePostionSelect('left')"
                    ></i>
                  </el-tooltip>
                  <el-tooltip content="居中对齐" placement="top">
                    <i
                      class="el-icon-s-operation icon"
                      @click="$refs.container.handlePostionSelect('center')"
                    ></i>
                  </el-tooltip>
                  <el-tooltip content="右对齐" placement="top">
                    <i
                      class="el-icon-s-unfold icon"
                      @click="$refs.container.handlePostionSelect('right')"
                    ></i>
                  </el-tooltip>
                </el-form-item>
                <el-form-item label="垂直方式">
                  <el-tooltip content="顶对齐" placement="top">
                    <i
                      class="el-icon-s-fold icon"
                      @click="$refs.container.handlePostionSelect('top')"
                    ></i>
                  </el-tooltip>
                  <el-tooltip content="中部对齐" placement="top">
                    <i
                      class="el-icon-s-operation icon"
                      @click="$refs.container.handlePostionSelect('middle')"
                    ></i>
                  </el-tooltip>
                  <el-tooltip content="底对齐" placement="top">
                    <i
                      class="el-icon-s-unfold icon"
                      @click="$refs.container.handlePostionSelect('bottom')"
                    ></i>
                  </el-tooltip>
                </el-form-item>
                <el-form-item label-width="0">
                  <el-button
                    type="primary"
                    size="mini"
                    class="block"
                    @click="handleFloder"
                    >成组</el-button
                  >
                </el-form-item>
                <el-form-item label-width="0">
                  <el-button
                    type="danger"
                    size="mini"
                    class="block"
                    @click="handleDeleteSelect"
                    >删除</el-button
                  >
                </el-form-item>
              </template>
              <!-- 主屏的配置项 -->
              <template v-else>
                <el-form-item label="大屏名称">
                  <avue-input v-model="visual.title"></avue-input>
                </el-form-item>
                <el-form-item label="大屏宽度">
                  <el-input-number
                    v-model="config.width"
                    controls-position="right"
                    :min="0"
                  />
                </el-form-item>
                <el-form-item label="大屏高度">
                  <el-input-number
                    v-model="config.height"
                    controls-position="right"
                    :min="0"
                  />
                </el-form-item>
                <el-form-item label="大屏简介">
                  <avue-input
                    v-model="config.info"
                    type="textarea"
                    :min-rows="5"
                  ></avue-input>
                </el-form-item>
                <el-form-item label="背景颜色">
                  <avue-input-color
                    v-model="config.backgroundColor"
                  ></avue-input-color>
                </el-form-item>
                <el-form-item label="背景图片">
                  <div v-if="config.backgroundImage" class="bgimg-box">
                    <!-- <div class="delete-border-box"></div> -->
                    <i class="el-icon-circle-close" @click="clearBgImg"></i>
                    <img
                      v-if="config.backgroundImage"
                      :src="config.backgroundImage"
                      @click="
                        handleOpenImg('config.backgroundImage', 'background')
                      "
                      alt=""
                      width="100%"
                    />
                  </div>
                  <el-button
                    v-else
                    size="mini"
                    type="primary"
                    @click="
                      handleOpenImg('config.backgroundImage', 'background')
                    "
                    >选择背景图片</el-button
                  >
                </el-form-item>
                <el-form-item label="工程化编辑">
                  <el-button
                    style="width: 105px"
                    type="primary"
                    @click="openEngineering"
                    >配置</el-button
                  >
                  <el-tooltip
                    class="item engineering-ti"
                    effect="dark"
                    content="使用工程化编辑，请您先保存一下大屏。"
                    placement="top-start"
                  >
                    <i class="el-icon-info"></i>
                  </el-tooltip>
                </el-form-item>
                <!-- <el-form-item label="全局显示隐藏">
                  <el-switch
                    v-model="overallValue"
                    @change="overallChange"
                  >
                  </el-switch>
                </el-form-item> -->
                <el-form-item label="全局颜色配置">
                  <el-button
                    style="width: 105px"
                    type="primary"
                    @click="openGetColor"
                    >配置</el-button
                  >
                  <el-tooltip
                    class="item engineering-ti"
                    effect="dark"
                    content="可以统一更改柱形图、2.5d柱状图，3d饼图，折线图、饼图，象形图，斑马柱状图，山峰柱状图，雷达图，散点图，漏斗图，折线柱状混合图，双轴混合图的配色"
                    placement="top-start"
                  >
                    <i class="el-icon-info"></i>
                  </el-tooltip>
                </el-form-item>
                <el-form-item label="公共地址">
                  <avue-input
                    type="textarea"
                    :min-rows="3"
                    v-model="config.url"
                  ></avue-input>
                </el-form-item>
                <el-form-item label="公共请求参数">
                  <el-button
                    size="mini"
                    type="primary"
                    @click="openCode('query')"
                    >编辑函数</el-button
                  >
                </el-form-item>
                <el-form-item label="公共请求头">
                  <el-button
                    size="mini"
                    type="primary"
                    @click="openCode('header')"
                    >编辑函数</el-button
                  >
                </el-form-item>
                <el-form-item label="页面参数">
                  <el-button size="mini" type="primary" @click="editPageParams"
                    >编辑参数</el-button
                  >
                </el-form-item>
                <el-form-item label="水印(预览有效)">
                  <avue-switch v-model="config.mark.show"></avue-switch>
                </el-form-item>
                <template v-if="config.mark.show">
                  <el-form-item label="内容">
                    <avue-input v-model="config.mark.text"></avue-input>
                  </el-form-item>
                  <el-form-item label="大小">
                    <el-input-number
                      v-model="config.mark.fontSize"
                      controls-position="right"
                      :min="0"
                    />
                  </el-form-item>
                  <el-form-item label="颜色">
                    <avue-input-color
                      v-model="config.mark.color"
                    ></avue-input-color>
                  </el-form-item>
                  <el-form-item label="角度">
                    <el-input-number
                      v-model="config.mark.degree"
                      controls-position="right"
                      :min="0"
                    />
                  </el-form-item>
                </template>
                <el-form-item label="对齐线">
                  <el-switch
                    v-model="alignLineEnable"
                    @change="changeStatus"
                  ></el-switch>
                </el-form-item>
                <el-form-item label="滤镜">
                  <el-switch v-model="config.filter.show"></el-switch>
                </el-form-item>
                <template v-if="config.filter.show">
                  <el-form-item label="色相">
                    <!-- <el-slider v-model="config.filter.hueRotate" :min="-180" :max="180"></el-slider> -->
                    <sliderInputNum
                      :min="-180"
                      :max="180"
                      v-model="config.filter.hueRotate"
                      :unit="'deg'"
                    ></sliderInputNum>
                  </el-form-item>
                  <el-form-item label="饱和度">
                    <sliderInputNum
                      :min="-100"
                      :max="100"
                      v-model="config.filter.saturate"
                      :unit="'%'"
                    ></sliderInputNum>
                  </el-form-item>
                  <el-form-item label="明度">
                    <sliderInputNum
                      :min="-100"
                      :max="100"
                      v-model="config.filter.brightness"
                      :unit="'%'"
                    ></sliderInputNum>
                  </el-form-item>
                  <el-form-item label="对比度">
                    <sliderInputNum
                      :min="-100"
                      :max="100"
                      v-model="config.filter.contrast"
                      :unit="'%'"
                    ></sliderInputNum>
                  </el-form-item>
                  <el-form-item label="不透明度">
                    <sliderInputNum
                      :min="0"
                      :max="100"
                      v-model="config.filter.opacity"
                      :unit="'%'"
                    ></sliderInputNum>
                  </el-form-item>
                  <el-form-item label="灰度">
                    <sliderInputNum
                      :min="0"
                      :max="100"
                      v-model="config.filter.grayscale"
                      :unit="'%'"
                    ></sliderInputNum>
                  </el-form-item>
                </template>
              </template>
            </el-form>
          </el-tab-pane>
          <!-- 数据配置 -->
          <el-tab-pane name="1" v-if="vaildProp('dataList') && !isSelectActive">
            <el-tooltip
              slot="label"
              effect="dark"
              content="数据"
              placement="top"
            >
              <div><i class="iconfont icon-shujuku"></i>数据</div>
            </el-tooltip>
            <el-form label-width="120px" label-position="left" size="mini">
              <el-form-item label="数据类型">
                <avue-radio
                  v-model="activeObj.dataType"
                  :dic="newDataType"
                ></avue-radio>
              </el-form-item>
              <el-form-item label="刷新时间">
                <!-- <avue-input-number v-model="activeObj.time" :min="0"></avue-input-number> -->
                <el-input-number
                  v-model="activeObj.time"
                  controls-position="right"
                  :min="0"
                ></el-input-number>
              </el-form-item>
              <!-- 轮播图数据设置 -->
              <el-collapse
                accordion
                v-if="vaildProp('swiperConfig') && activeObj.dataType === 0"
              >
                <el-collapse-item title="轮播项">
                  <div class="swiper-handles">
                    <i
                      @click="appendSwiper"
                      class="el-icon-plus"
                      style="color: #b4b7c1"
                    ></i>
                    <i
                      @click="deleteSwiper(0)"
                      class="el-icon-delete-solid"
                      style="color: #b4b7c1"
                    ></i>
                  </div>
                  <el-form
                    ref="form"
                    :model="form"
                    label-width="80px"
                    v-if="activeObj.data"
                  >
                    <el-form-item
                      :label="'轮播图' + (index + 1)"
                      v-for="(item, index) in activeObj.data"
                      :key="index"
                    >
                      <el-form-item label="标题">
                        <el-input
                          v-model="activeObj.data[index].title"
                        ></el-input>
                      </el-form-item>
                      <el-form-item label="图片">
                        <div class="img-box" v-if="!!item.value">
                          <img
                            v-if="!!item.value"
                            :src="item.value"
                            alt=""
                            class="swiper-image"
                          />
                          <div class="image-mask">
                            <div class="handle-img">
                              <span
                                @click="
                                  handleOpenImg(
                                    'activeOption.backgroundImage',
                                    index
                                  )
                                "
                                >更改</span
                              >
                              <span>|</span>
                              <span @click="deleteSwiper(index + 1)">删除</span>
                            </div>
                          </div>
                        </div>
                        <div class="noImg-box" v-else>
                          <i class="el-icon-upload"></i>
                          <span
                            @click="
                              handleOpenImg(
                                'activeOption.backgroundImage',
                                index
                              )
                            "
                            >点击上传图片</span
                          >
                        </div>
                        <el-input v-model="activeObj.data[index].value">
                          <div
                            @click="
                              handleOpenImg(
                                'activeOption.backgroundImage',
                                index
                              )
                            "
                          >
                            <!-- <i class="iconfont icon-img"></i> -->
                          </div>
                        </el-input>
                      </el-form-item>
                    </el-form-item>
                  </el-form>
                </el-collapse-item>
              </el-collapse>
              <template v-if="isApi">
                <el-form-item label-width="0">
                  <el-button
                    size="mini"
                    type="primary"
                    class="block"
                    @click="handleSql"
                  >
                    编辑Api接口
                  </el-button>
                </el-form-item>
              </template>
              <template v-if="isWebSocket">
                <el-form-item label-width="0">
                  <el-button
                    size="mini"
                    type="primary"
                    class="block"
                    @click="handleSql"
                  >
                    编辑实时接口
                  </el-button>
                </el-form-item>
              </template>
              <el-form-item label-width="0" v-if="isStatic">
                <el-button
                  size="mini"
                  plain
                  class="block myBtn"
                  @click="handleSql"
                >
                  <span>编辑静态数据</span>
                </el-button>
              </el-form-item>

              <!-- <el-form-item label-width="0" v-if="!isSql">
                <el-button
                  size="mini"
                  plain
                  class="block myBtn"
                  @click="openCode('stylesFormatter')"
                  >编辑样式</el-button
                >
              </el-form-item> -->
              <el-form-item label-width="0" v-if="!isSql">
                <el-button
                  size="mini"
                  plain
                  class="block myBtn"
                  @click="openCode('dataFormatter')"
                  >数据处理</el-button
                >
              </el-form-item>
              <el-form-item label-width="0" v-if="!isSql">
                <el-button
                  size="mini"
                  plain
                  class="block myBtn"
                  @click="handleRes"
                  >刷新数据</el-button
                >
              </el-form-item>
            </el-form>
            <!-- sql查询配置区域 开始-->
            <sql-data
              v-if="isSql"
              @on-handleRes="sqlHandleRes"
              :activeObj="activeObj"
              :tableFields.sync="tableFields"
              :tabsActive="tabsActive"
              :activeIndex="activeIndex"
              @updateCheckedDimAndMea="updateCheckedDimAndMea"
              ref="sqlData"
            ></sql-data>
            <!-- sql查询配置区域 结束-->
          </el-tab-pane>
          <!-- 交互事件配置 -->
          <!-- <el-tab-pane name="2" v-if="vaildProp('eventList')">
            <el-tooltip slot="label" effect="dark" content="交互" placement="top">
              <div><i class="el-icon-thumb"></i></div>
            </el-tooltip>
            <el-form label-width="120px" label-position="left" size="mini">
              <el-form-item label="子类">
                <avue-select multiple v-model="activeObj.child.index" :dic="childList" :props="childProps">
                </avue-select>
              </el-form-item>
              <el-form-item label="参数名称">
                <avue-input v-model="activeObj.child.paramName"></avue-input>
              </el-form-item>
            </el-form>
          </el-tab-pane> -->
          <!-- 其他事件配置 -->
          <el-tab-pane name="3" :lazy="true">
            <!-- 事件这里需要延迟加载，否则大屏的事件会拿不到值 -->
            <el-tooltip
              slot="label"
              effect="dark"
              content="事件"
              placement="top"
            >
              <div><i class="iconfont icon-shijian"></i>事件</div>
            </el-tooltip>
            <!-- <el-form label-width="120px" label-position="left" size="mini">
              <el-form-item label="鼠标左键单击">
                <el-button size="mini" type="primary" @click="openCode('clickFormatter')"
                  >编辑</el-button
                >
              </el-form-item>
              <el-form-item label="鼠标左键双击">
                <el-button size="mini" type="primary" @click="openCode('dblClickFormatter')"
                  >编辑</el-button
                >
              </el-form-item>
              <el-form-item label="初始化控件">
                <el-button size="mini" type="primary" @click="openCode('initFormatter')">编辑</el-button>
              </el-form-item>
              <el-form-item label="渲染完成" v-if="vaildProp('labelFormatterList')">
                <el-button size="mini" type="primary" @click="openCode('labelFormatter')"
                  >编辑</el-button
                >
              </el-form-item>
            </el-form>
            <div>
              <componentEvent v-if="showRelationship" :myActiveObj="activeObj" :eventListType="eventListType" @updateActiveObj="updateActiveObj" :childList="childList" :authenticationArr="dicOption.authenticationArr"></componentEvent>
            </div> -->
            <newComponentEvent
              :activeObj="activeObj"
              :childList="childList"
              :authenticationArr="dicOption.authenticationArr"
              :activeIndex="activeIndex"
              :tableColomData.sync="tableColomData"
            ></newComponentEvent>
          </el-tab-pane>
          <!-- 基本参数配置 -->
          <el-tab-pane name="4" v-if="isActive && !isSelectActive && vaildProp('conditionList')">
            <el-tooltip
              slot="label"
              effect="dark"
              content="条件"
              placement="top"
            >
              <div><i class="iconfont icon-tiaojianshijian"></i>条件</div>
            </el-tooltip>
            <!-- 此处把位置配置的页面改为条件配置的页面 -->
            <conditionCon
              :activeObj="activeObj"
              :activeIndex="activeIndex"
              :tableFields.sync="tableFields"
              @on-handleRes="sqlHandleRes"
            ></conditionCon>
          </el-tab-pane>
          <el-tab-pane
            name="5"
            v-if="vaildProp('renderingList') && !isSelectActive"
          >
            <el-tooltip
              slot="label"
              effect="dark"
              content="渲染/告警"
              placement="top"
            >
              <div><i class="iconfont icon-gaojing"></i>渲染</div>
            </el-tooltip>
            <!-- 渲染告警配置部分 -->
            <drawWarn
              :activeObj="activeObj"
              :activeIndex="activeIndex"
              :tableFields.sync="tableFields"
              @drawWarnSaveData="drawWarnSaveData"
            ></drawWarn>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
    <codeedit
      @submit="codeClose"
      v-if="code.box"
      :type="code.type"
      :value="code.obj"
      :visible.sync="code.box"
    ></codeedit>
    <!-- <eventOptionDialog v-if="code.box" :eventOptiondialogVisible.sync="code.box"></eventOptionDialog> -->
    <!-- 鼠标右键事件 -->
    <contentmenu ref="contentmenu" @showTipsHandleCopy='showTipsHandleCopy'></contentmenu>
    <!-- 数据部分弹窗 begin -->
    <el-dialog
      append-to-body
      :close-on-click-modal="false"
      :visible.sync="show"
      width="60%"
    >
      <el-form size="small" v-if="show" label-width="130px">
        <template v-if="isStatic">
          <el-form-item label="数据值" label-position="top">
            <el-button size="mini" type="primary" @click="openCode('data')"
              >编辑JSON</el-button
            >
          </el-form-item>
          <el-form-item label="数据值" label-position="top">
            <el-upload
              :show-file-list="false"
              :auto-upload="false"
              accept=".xls,.xlsx"
              :on-change="handleImport"
            >
              <el-button size="mini" type="success">导入数据(Excel)</el-button>
            </el-upload>
          </el-form-item>
        </template>
        <template v-else-if="isSql">
          <el-form-item label="数据源选择">
            <avue-select :dic="DIC.sql" v-model="db"></avue-select>
          </el-form-item>
          <el-form-item label="SQL语句" label-position="top">
            <monaco-editor
              v-model="sql"
              language="sql"
              height="100"
            ></monaco-editor>
          </el-form-item>
        </template>
        <!-- 接口数据源 开始 -->
        <template v-else-if="isApi">
          <el-form-item label="接口地址">
            <avue-input v-model="activeObj.url"></avue-input>
          </el-form-item>
          <el-form-item label="请求方式">
            <avue-select
              v-model="activeObj.dataMethod"
              :dic="dicOption.dataMethod"
            ></avue-select>
          </el-form-item>
          <el-form-item label="请求头">
            <el-button
              size="small"
              type="primary"
              @click="openCode('dataHeader')"
              >编辑函数</el-button
            >
          </el-form-item>
          <el-form-item label="请求参数">
            <el-button
              size="small"
              type="primary"
              @click="openCode('dataQuery')"
              >编辑函数</el-button
            >
          </el-form-item>
          <el-form-item label="认证方式">
            <el-select
              v-model="activeObj.apiAuthenticationMethod"
              @change="apiAuthenticationMethodChange(activeObj, $event)"
              placeholder="请选择"
            >
              <el-option
                v-for="(item, index) in dicOption.authenticationArr"
                :key="index"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <template v-if="activeObj.apiAuthenticationMethod === 'basicAuth'">
            <el-form-item label="用户名">
              <el-input
                v-model="activeObj.apiAuthenticationForm.userName"
              ></el-input>
            </el-form-item>
            <el-form-item label="密码">
              <el-input
                v-model="activeObj.apiAuthenticationForm.password"
              ></el-input>
            </el-form-item>
            <el-form-item label="认证地址">
              <el-input
                v-model="activeObj.apiAuthenticationForm.authenticationUrl"
              ></el-input>
            </el-form-item>
          </template>
        </template>
        <!-- 接口数据源 结束 -->

        <!-- 实时接口数据源 开始 -->
        <template v-else-if="isWebSocket">
          <el-form-item label="接口地址">
            <el-input v-model="activeObj.webSocketUrl"></el-input>
          </el-form-item>
          <el-form-item label="请求头">
            <el-button
              size="small"
              type="primary"
              @click="openCode('websocketHeader')"
              >编辑函数</el-button
            >
          </el-form-item>
          <el-form-item label="请求参数">
            <el-button
              size="small"
              type="primary"
              @click="openCode('websocketQuery')"
              >编辑函数</el-button
            >
          </el-form-item>
          <el-form-item label="认证方式">
            <el-select
              v-model="activeObj.authenticationMethod"
              placeholder="请选择"
            >
              <el-option
                v-for="(item, index) in dicOption.authenticationArr"
                :key="index"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <template v-if="activeObj.authenticationMethod === 'basicAuth'">
            <el-form-item label="用户名">
              <el-input
                v-model="activeObj.authenticationForm.userName"
              ></el-input>
            </el-form-item>
            <el-form-item label="密码">
              <el-input
                v-model="activeObj.authenticationForm.password"
              ></el-input>
            </el-form-item>
            <el-form-item label="认证地址">
              <el-input
                v-model="activeObj.authenticationForm.authenticationUrl"
              ></el-input>
            </el-form-item>
          </template>
        </template>
        <!-- 实时接口数据源 结束 -->
        <el-form-item label="响应数据">
          <monaco-editor
            v-model="dataRes"
            disabled
            height="300"
          ></monaco-editor>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <!-- <el-button size="small" type="danger" @click="openCode('dataFormatter')"
          >数据处理</el-button
        > -->
        <el-button size="small" type="primary" @click="handleRes"
          >刷新数据</el-button
        >
      </span>
    </el-dialog>
    <!-- 数据部分弹窗 end -->
    <!-- 发布对话框 开始 -->
    <release-dialog
      ref="releaseDialog"
      :releaseDialogVisible.sync="releaseDialogVisible"
      :releaseInfo.sync="releaseInfo"
      @releaseCbFn="handleReleaseCbFn"
      @cancelReleaseCbFn="handleCancelReleaseCbFn"
      @saveData="saveData(arguments)"
    ></release-dialog>
    <!-- 发布对话框 结束 -->

    <!-- 数据源组件关联 开始 -->
    <dataStorageRelevanceDrawer
      v-if="isDataStorage"
      :dataStorageRelevanceVisible.sync="dataStorageRelevanceVisible"
      :activeIndex="activeIndex"
      :childList="childList"
    ></dataStorageRelevanceDrawer>
    <!-- 数据源组件关联 结束 -->
    <!-- 页面参数设置对话框 -->
    <el-dialog
      title="设置页面参数"
      :visible.sync="pageParamsDialogVisible"
      width="30%"
    >
      <pageParams
        ref="pageParams"
        :pageParamsList="config.pageParamsList"
        v-if="pageParamsDialogVisible"
      ></pageParams>
      <span slot="footer" class="dialog-footer">
        <el-button @click="pageParamsDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="pageParamsConfirm">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import MonacoEditor from '@/page/components/editor'
import layer from './group/layer'
import top from './group/top'
import position from './group/position'
import headers from './group/header'
import imglist from './group/imglist'
import engineeringEdit from './group/engineeringEdit'
import editColor from './group/editColor'
import threeModelType from './group/threeModelType'
import contentmenu from './group/contentmenu'
import codeedit from './group/code'
import eventOptionDialog from '@/eventsOption'
import componentMenu from './parkStoreroom/componentMenu'
import { dicOption } from '@/option/config'
import init from '@/mixins/'
import createFontCssLink from '@/mixins/createFontCssLink.js'
// import { createFile } from '@/utils/utils';
import components from '@/option/components'
import SketchRule from 'vue-sketch-ruler'
import { getList } from '@/api/db'
// import crypto from '@/utils/crypto';
import crypto from '@/utils/crypto.min.js'
import sqlData from '@/dataConnection/sqlData'
import conditionCon from '@/dataConnection/condition'
import componentSql from '@/dataConnection/componentSql'
import componentEvent from '@/dataConnection/componentEvent'
import newComponentEvent from '@/dataConnection/newComponentEvent'
import drawWarn from '@/components/draw-warn/index.vue'
// import { uuid, createFile } from '@/utils/utils'
import { uuid, createFile } from '@/utils/utils.min.js'
import releaseDialog from '@/page/components/releaseDialog.vue'
import dataStorageRelevanceDrawer from '@/page/components/dataStorageRelevanceDrawer.vue' // 数据源组件关联组件侧滑对话框
import sliderInputNum from '@/components/sliderInputNum'
import { myStorage } from '@/utils/storage.js'
import { saveReleaseInfo, closeRelease } from '@/api/share.js'
import pageParams from '@/page/components/pageParams.vue'
import { getVersionData } from '@/api/visual'
import { EventBus } from '@/bus.js'
import findTree from "xe-utils/findTree";
let __this = null
export default {
  mixins: [init, components, createFontCssLink],
  data() {
    return {
      overallValue:false,
      wholeColorList:[ {
          color1: '#188df0',
          color2: 'rgba(48, 158, 248, 0.1)',
          postion: 90,
          _index: 0,
          _show: true,
        },
        {
          color1: '#1ee7e7',
          color2: 'rgba(30, 231, 231, 0.1)',
          postion: 90,
          _index: 1,
          _show: true,
        },
        {
          color1: '#2F54EB',
          color2: 'rgba(47, 84, 235, 0.1)',
          postion: 90,
          _index: 2,
          _show: true,
        },
        {
          color1: '#BAE6FF',
          color2: 'rgba(186, 230, 255, 0.1)',
          postion: 90,
          _index: 3,
          _show: true,
        },
        {
          color1: '#00D68A',
          color2: 'rgba(0, 214, 138, 0.1)',
          postion: 90,
          _index: 4,
          _show: true,
        },
        {
          color1: '#FBAD47',
          color2: 'rgba(251, 173, 71, 0.1)',
          postion: 90,
          _index: 5,
          _show: true,
        },
      ],
      showTipsCopy:0,
      altSelect:[],
      containerWidth:10000,
      containerHeight:7000,
      multiparameterList: [],
      flageBuild: true,
      menuShow: true,
      paramsShow: true,
      componentShow: true, // 控制左侧组件显示与否
      show: false,
      isReleaseFlag: false,
      keys: {
        ctrl: false,
      },
      dataRes: '',
      db: '',
      sql: '',
      // nav: [], // 这个nav可以注释掉，因为已经在mixins的index.js中声明过了
      DIC: {
        sql: [],
      },
      loading: '',
      childProps: {
        label: 'name',
        value: 'index',
      },
      key: '',
      menuFlag: true,
      code: {
        box: false,
        type: '',
        obj: '',
      },
      form: {},
      dicOption: dicOption,
      tabsActive: 0,
      // 标尺
      scale: 0.65, //初始化标尺的缩放
      startX: 0, //x轴标尺开始的坐标数值
      startY: 0,
      lines: {
        //初始化水平标尺上的参考线
        h: [],
        v: [],
      },
      rendIndex: 0, //重新渲染组件
      shadow: { x: 0, y: 0 }, // 阴影大小
      thick: 20, //标尺的厚度
      width: 0, // 标尺宽,后面会初始化
      height: 0, // 标尺高,后面会初始化
      isShowReferLine: true, // 显示参考线
      isImgOpen: true, //眼镜打开
      imgOpenData:
        'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACgAAAAbCAYAAAAOEM1uAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAIGNIUk0AAHolAACAgwAA+f8AAIDpAAB1MAAA6mAAADqYAAAXb5JfxUYAAAQNSURBVHja7JdvSON1HMdfP126/shSmaZ1ntuZbTLihOnSdJlPhIquB0VR1DZM9En0wCB3qCXKVOh86mmakdGDOqyHityBidYN1HPYZqbhOZprMGTOUk/9/XryWyxvek5NIu4Lg/H+fPj8Xt/P98/n8xUkSeK/PIT7gP8GoCAI8cTQAoWAHkgFRCAA3AKmgeBRA8VkOSZgMvAy8DZQCqQf4OcFRoDPgYmzAnwdaAAuxpFlCbgGfAR4ThUwYhcE4QngExnw71FWVuax2WwBk8mkSE9PV+7t7Ymrq6vbw8PD0uDgYO7CwsK5KPc14ENJkj497FtxAwqCYAK+kvcbANXV1U6Hw6HIyMh4GlAckJHwzMzMrM1my3a5XNoo01XgPUmSdk8MCLwEfAmoAPLz872jo6OrOTk5xVGBQ0tLS575+fnt7OzsRIPBcD4pKelctL2/v3+mtrbWLIpigixfA94BNk8C+JoMlyRn7WZvb68mISEhI+IQCASmKyoq0jweT25EU6lU4aGhoZnKykpzdNzl5eWbRqPxyWAwmCZL3wJvAHeOA/iCPMsHARwOx7jdbi+JXs7t7e3lrKys1LW1NVWsJXa73ZN6vb40WltfX3cbDIZ0r9ebKUv9wLvxAhYC1+V7je7u7rG6urrn9vu1tbWNNzU1lR90KgsLC5emp6cfB5TRejgc9mg0msyoTNYDXfEAjgFmQGxpaZlobm6OBbFrMpl+dTqd+YdcLztbW1ve5ORk7X6D3++f0ul0+aFQKAXYAF6RJOn6UQGDQBrgE0VRJQjCw7EAjUbj8tTUVN4hgLubm5u3lUrlhVjG+vr6ya6ursgWaJck6fJRAW8AzwNia2vrRGNjY8xltNvtEx0dHc8eRKfX62+73W418NB+m8/nm9LpdE+Fw+FHgD+AS/Fk8CJwI7IHe3t7x2pqau7agxsbG/NqtTp3a2tLGQtwcnJyvKSk5K7JhcPhnzQaTXYwGEyVpQ+AK8c5xd9EZt/e3v59Q0ND6f5LeXFx8cfy8vILfr9fHdEUCsVOX1/fhMViqdgfOxQKzRUUFKh9Pt+JTnHk76vyPZgcqR49PT3nExMTM/+x0XZ3fePj47/Mzc2RlZVFVVVVRkpKin5/3JWVFWdRUVFeIBCInN7v5NJ55ySV5EUZ8lEArVb728jIiDcvL++ZONqo0MDAwK2ampoyURQTZXlI7ob+PI1aXAR8AegiutVqdXZ2dt6zFs/OzrosFstjLpcr+iR3A+9LkrRzKs2CrGUCV4C3on3NZrPHarX+Xlxc/MARu5nLkiRdPbVuJsa4BDQCxjj6QRH4GvgY+PksOmqF3FG/KVcc9T066s+AH86y5Y8eOXI282XQyJtkRv6d/pvk/rPz/wT41wBibRrpeMs+PAAAAABJRU5ErkJggg==', // 左上角图片
      imgClose:
        'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABkAAAAZCAYAAADE6YVjAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAIGNIUk0AAHolAACAgwAA+f8AAIDpAAB1MAAA6mAAADqYAAAXb5JfxUYAAAPYSURBVHja7JVPTFRXFIe/+96gGRHEhQzBFMJKSKSkLTgtGxemSXcktohW2RBmTAjUClIYZ6Cxi+fQDgOCLJgJhqTSBAtLdjaxaSqQWnDSNp2U0I6FFh0SDdV0oMx7pwuZCUXjnyZNuvBsbnLvufe7555zfleJCP+1qReQfwVRSqWmqoFDIE3A+iZXQDbGlMmmNTatP5xPn/0ohOOgLgNtIB8DOlAKvAzsBTKBP4FF4Dvge1DrzwsBaAAuAJ8CxbpuezU/P397QcFLZGVlcf/+fRYXF1lc/G3VNJM/AJ8Dw8CdZ4QoQI4AIWBXaWkpQ0ND5v79+zW73Z5+n9XVVWZnb8rExIQ2MnKZWCz2M/Dhw1d4eiTngXafz2dmZ2ebPp9P6+vrl5qaI2p8fFyi0aheUlJiHj78tpaTs0sHJB6PW4HAJ3og0I2I+AHPkyAeu91uDA4OmrW1tRpAT09vsr29XXM4ciWZTJKXl2ctLCyoHTsy1ZUro+J0OjNSWR8ZGbFcLpeeSCTOAucfBykDpvr7+7c3NjamS+bevXvJ4uIS4vE7tra2tjW/32+7ffu21Ne7rJmZb7VIJKL27Nmjp0ADAwM0NjauAW+IyM2tkAGn8/WG69e/NjVNS20iHA6vd3Z2qrq6OtMwDFswGLROnz6dcffu3WRRUZEVCARwuVzbUv6WZVmVlZXa9PT0RRFp2gq56na7Dw0ODv6jGc6cOWNdu/Zl8saNb/RgMGi1tLSo7u5uaW5u1srLK8yDBw/aursD2ubmcbtPqnA4dFVE3twKuVBeXv7e5OSkabPZ0pGEQuG/WltbicV+0Xbv3m0LBnvWW1qatbNnvclLl4b0c+c+ErfblcoLa2trptPp1CORSEBEWrdCSoBpwzB2ejyedE6Wl5fNsrIyqaiokFAopBwOh/J4PKbf79/mcDiS0WiUnJwcWyoKr9erDMNYBg6ISOxx1dWg6/pAb2+vtZF8DWBqanr96NEalUgkpKCgQJaWljRN04jH43R1dcmpU6dsgBiGobxer3qoGnz2pD5pBfx1dXWaz+czi4qKNECtrKyYY2NjVjQa1fftK7aqq99hdHRUmpqaVEdHhzU/P58xPDycAN4Hws8iK28B/tzc3LJjx96lqqrKOnCggszMzLRmPHjwQM3MzFj19fX63NwcwDJQBUymK+ApEEDtBDkOnFBKvZafv9deWFhIdnYWKysr3Lr1K0tLvydE5CvgJ1AnQZqBi88DSV1aA0qAV4CCDRX+A1gAZoEfN/w/ALqAEyAjzwvZ8mc8KukblgGqD/gCZOyxkBd//P8G8vcAMK383pmr7aEAAAAASUVORK5CYII=',
      dragSlide: false, // 拖动滚动条标记
      activeName: 'second',
      tableFields: {
        dimensionListData: [],
        quotaListData: [],
      },
      showRelationship: false,
      copyUseActiveIndex: '',
      fieldTypeList: [
        { label: '度量值', value: 1 },
        { label: '维度值', value: 2 },
      ],
      releaseDialogVisible: false,
      releaseInfo: {},
      eventListType: '',
      alignLineEnable: this.$store.state.alignLine.enable,
      dataStorageRelevanceVisible: false,
      activeFolder: null,
      serachVal: '',
      pageParamsDialogVisible: false,
      visualId: '',
      tableColomData: [],
    }
  },
  components: {
    MonacoEditor,
    imglist,
    layer,
    codeedit,
    top,
    position,
    headers,
    contentmenu,
    SketchRule,
    componentMenu,
    eventOptionDialog,
    sqlData,
    conditionCon, // 条件配置组件
    drawWarn,
    componentSql,
    componentEvent,
    newComponentEvent,
    releaseDialog,
    dataStorageRelevanceDrawer,
    sliderInputNum,
    pageParams,
    threeModelType,
    engineeringEdit,
    editColor,
  },

  computed: {
    isKeysCtrl() {
      return this.keys.ctrl == true
    },
    isStatic() {
      return this.activeObj.dataType == 0
    },
    isApi() {
      return this.activeObj.dataType == 1
    },
    isSql() {
      return this.activeObj.dataType == 2
    },
    // 文字组件新增websocket
    isWebSocket() {
      return this.activeObj.dataType == 3
    },
    // 文字和图片新增数据源组件数据来源
    isDataFromDataStorage() {
      return this.activeObj.dataType == 4
    },
    isFolder() {
      return this.activeObj.children
    },
    isActive() {
      return this.active.length !== 0
    },
    isDataStorage() {
      if (
        this.activeObj &&
        this.activeObj.component &&
        this.activeObj.component.prop === 'dataStorage'
      ) {
        return true
      } else {
        return false
      }
    },
    isSelectActive() {
      return this.active.length > 1
    },
    childList() {
      return this.list.filter(ele => {
        if (['tabs'].includes(ele.component.prop)) {
          return false
        }
        return true
      })
    },
    activeComponent() {
      return this.activeObj.component || {}
    },
    activeOption() {
      return this.activeObj.option || {}
    },
    activeObj() {
      if (this.validatenull(this.activeIndex)) return {}
      let item = {}
      if (this.showTips) {
        item = this.findTipsList(this.activeIndex) || {}
      } else {
        item = this.findlist(this.activeIndex)
      }
      if (!item.child) {
        item.child = {}
      }
      if (!item.sqlChild) {
        item.sqlChild = {}
        // item.sqlChild = {"filterObj":{}};
        // this.$set(item.sqlChild,'filterObj',{})
      }
      if (!item.sqlChild || !item.sqlChild.filterObj) {
        item.sqlChild.filterObj = {}
        // item.sqlChild = {"filterObj":{}};
        // this.$set(item.sqlChild,'filterObj',{})
      }
      if (!item.serverChild) {
        item.serverChild = {}
      }
      if (!item.serverChild || !item.serverChild.method) {
        item.serverChild.url = ''
        item.serverChild.method = 'GET'
        item.serverChild.params = []
      }

      return item
    },
    activeList() {
      let result = []
      this.active.forEach(ele => {
        const item = this.findnav(ele, true)
        result.push(item.obj)
      })
      return result
    },
    /* 标尺用的 */
    palette() {
      return {
        bgColor: '#181b24', // ruler bg color
        longfgColor: '#BABBBC', // ruler longer mark color
        shortfgColor: '#9C9C9C', // ruler shorter mark color
        fontColor: '#fff', // ruler font color
        shadowColor: '#181b24', // ruler shadow color
        lineColor: '#EB5648',
        borderColor: '#B5B5B5',
        cornerActiveColor: '#fff',
      }
    },
    // 画布大小,一定要是computer里面,否则缩放页面会失效
    canvasStyle() {
      return {
        width: this.config.width + 'px',
        height: this.config.height + 'px', 
        transform: `scale(${this.scale})`,
      }
    },
    // 数据类型新加了一个
    newDataType() {
      return this.dicOption.dataType //组件全部可以展示实时接口数据源的组件类型
      // const propWithWebsocketList = ['text', 'dataStorage',] // 这个数组包含了可以展示实时接口数据源的组件类型
      // if (this.activeObj && this.activeObj.component) {
      //   const prop = this.activeObj.component.prop
      //   if (propWithWebsocketList.includes(prop)) {
      //     return this.dicOption.dataType
      //   } else {
      //     return this.dicOption.dataType.filter(item => item.value !== 3)
      //   }
      // }
    },
    showTips() {
      return this.$store.state.showTips
    },
  },
  watch: {
    menuShow:{
      handler(){
        this.initSize()
      },
      deep: true,
    },
    paramsShow:{
      handler(){
        this.initSize()
      },
      deep: true,
    },
    activeObj: {
      handler(val) {
        if (this.activeObj.sql && this.isSql) {
          let mode = JSON.parse(crypto.decrypt(this.activeObj.sql))
          this.db = mode.id
          this.sql = mode.sql
        } else {
          this.db = ''
          this.sql = ''
        }
      },
      deep: true,
    },
    // "activeObj.sqlChild.index": {
    //   handler(newVal, oldVal) {
    //     let compareList = this.compareListFn(newVal, oldVal);
    //   },
    //   deep: true,
    // },
    'activeObj.sqlChild.filterObj': {
      handler(newVal, oldVal) {
        let compareObj = this.compareObjFn(newVal, oldVal)
        this.updataSqlData(compareObj)
      },
      deep: true,
    },
    menuFlag() {
      this.setResize()
    },
    overactive(n, o) {
      ;[o, n].forEach((ele, index) => {
        if (!ele) return
        this.setActive(ele, index === 1, 'setOverActive')
      })
    },
    active(n, o) {
      // 这个数组会记录上一次点击和这一次点击对应的图标的index值，上一次点击的放在数组的第一位，当前点击的放在数组的第二位
      ;[o, n].forEach((ele, index) => {
        ele.forEach(item => {
          this.setActive(item, index === 1, 'setActive')
        })
      })
      //隐藏右键菜单
      this.$refs.contentmenu.hide()
      // 初始化选项卡
      this.tabsActive = '0'
    },
    tabsActive: {
      handler(val) {
        this.showRelationship = false
      },
      deep: true,
    },
    'config.width': {
      handler() {
        this.initSize()
      },
    },
    'config.height': {
      handler() {
        this.initSize()
      },
    },
    'config.backgroundColor': {
      handler(newVal) {
        if (newVal === null) {
          this.config.backgroundColor = ''
        }
      },
    },
    scale:{
      handler(){
        this.$nextTick(()=>{
          this.handleScroll()
        })
      },
      deep:true
    }
  },
  created() {
    __this = this
    this.listen()
    this.iniresize()
    // this.initSqlList()
  },
  mounted() {
    myStorage.remove('copyArr')
    // console.log('config===========>', this.config)
    this.localSocket()
    this.initFun()
    this.$nextTick(() => {
      this.initSize()
    })
    this.visualId = this.$route.params.id
  },
  filters: {
    // 转义组件名称
    transformName: function (id) {
      // console.log('转义组件名称', __this.childList);
      var p = __this.childList.filter(item => {
        return item.index == id
      })
      if (p.length > 0) {
        return p[0].name
      } else {
        return '未知'
      }
    },
  },
  methods: {
    copyImg(index){
       //获取input对象
      let input = document.getElementById(index)
      console.log(input,'input')
      //选中input标签
      input.select()
      //执行复制
      document.execCommand('copy')
      this.$message.success({
        message:'复制成功!',
        duration:800
      })
    },
    overallChange(val) {
      let sumList = []
      const detailList = item =>{
        if(item.children){
          item.children.forEach(ele => {
            sumList.push(ele)
            detailList(ele)
          })
        }
      }
      if(this.nav){
        for(let i = 0; i < this.nav.length; i++){
          sumList.push(this.nav[i])
          detailList(this.nav[i])
        }
      }
      sumList.forEach(item=>{
        setTimeout(() => {
          this.$set(item, 'display', '')
          item.display = val
        }, 100)
      })
    },
    openGetColor(){
      if(this.config.overallSituationColor){
        this.wholeColorList = this.config.overallSituationColor
      }
      this.$refs.editColor.visible = true
    },
    getSaveColor(colorList){
      this.config.overallSituationColor=colorList;
      this.nav.forEach(item=>{
        if(item.option){
          if(item.option.barColor){
            if(this.config.overallSituationColor){
              if(this.config.overallSituationColor.length>0){
                item.option.barColor = this.deepClone(this.config.overallSituationColor)
              }
            }
          }
          //象形图时
          if(item.option.pictorialBarColor){
            if(this.config.overallSituationColor){
              if(this.config.overallSituationColor.length>0){
                item.option.pictorialBarColor = this.deepClone(this.config.overallSituationColor)
              }
            }
          }
        }
        if(item.list){
          if(item.list.length > 0) {
            this.getNavList(item.list)
          }
        }
        if(item.children){
          if(item.children.length > 0) {
            this.getNavList(item.children)
          }
        }
      })
      this.$refs.editColor.visible = false
    },
    getNavList(dataList){
      if(dataList){
        if(dataList.length>0){
          dataList.forEach(item=>{
            if(item.option){
              if(item.option.barColor){
                if(this.config.overallSituationColor){
                  if(this.config.overallSituationColor.length>0){
                    item.option.barColor = this.deepClone(this.config.overallSituationColor)
                  }
                }
              }
              //象形图时
              if(item.option.pictorialBarColor){
                if(this.config.overallSituationColor){
                  if(this.config.overallSituationColor.length>0){
                    item.option.pictorialBarColor = this.deepClone(this.config.overallSituationColor)
                  }
                }
              }
            }
            if(item.list){
              if(item.list.length > 0) {
                this.getNavList(item.list)
              }
            }
            if(item.children){
              if(item.children.length > 0) {
                this.getNavList(item.children)
              }
            }
          })
        }
      }
    },
    showTipsHandleCopy(){
      this.showTipsCopy =  this.showTipsCopy + 1;
    },
    engineeringOk() {
      this.$refs.container.initData()
      this.$refs.container.initFun()
      this.$forceUpdate()
    },
    getAltSelect(list){
      list.forEach(item=>{
        this.altSelect.push(item.index)
        if(item.children){
          this.getAltSelect(item.children)
        }
      })
    },
    openEngineering() {
      this.$refs.engineeringEdit.imgVisible = true
    },
    localSocket() {
      if ('WebSocket' in window) {
        let wsUrl =
          window.location.protocol +
          '//' +
          window.location.host +
          '/stdc-visual/ws/event'
        wsUrl = wsUrl.replace('https', 'ws').replace('http', 'ws')
        // let wsUrl = `ws://192.168.152.43:8083/stdc-visual/ws/event`;
        console.log('wsUrl', wsUrl)
        let socket = new WebSocket(wsUrl)
        socket.onopen = function () {
          console.log('websocket连接成功')
        }
        socket.onmessage = function (msg) {
          let eventId = JSON.parse(msg.data).eventId
          let openInterface = {
            isWebsocket: true,
            eventId: eventId,
          }
          EventBus.$emit('openInterfaceWebsocket', openInterface)
        }
        socket.onclose = function () {
          console.log('websocket连接已关闭')
        }
        socket.onerror = function () {
          console.log('websocket连接发生错误')
        }
      } else {
        // 浏览器不支持 WebSocket
        console.log('您的浏览器不支持 WebSocket!')
      }
    },
    updateCheckedDimAndMea(item) {
      console.log('我传参了', item)
      this.tableColomData = item
    },
    // 新增轮播图
    appendSwiper() {
      this.activeObj.data.push({
        value: '',
        title: `标题${this.activeObj.data.length + 1}`,
      })
    },
    // 删除轮播图
    deleteSwiper(_index) {
      _index
        ? this.activeObj.data.splice(_index - 1, 1)
        : this.activeObj.data.pop()
    },
    handleClick(tab, event) {
      // console.log(tab, event);
    },
    handleImport(file, fileLis) {
      this.$Export.xlsx(file.raw).then(data => {
        this.activeObj.data = data.results
        this.$message.success('导入成功')
        this.handleRes()
      })
    },
    // 发布
    saveData(argus) {
      const [releaseFn, _this] = argus
      const cloneReleaseFn = releaseFn.bind(_this)
      this.isReleaseFlag = true
      this.$nextTick(() => {
        this.$refs.headers.handleBuild(cloneReleaseFn)
      })
    },
    handleRefresh() {
      // 如果中间部分在弹框的编辑页面也就是使用的subgroupTips组件，那么就要调用editTips的handleRefresh
      if (this.showTips) {
        return this.$refs.editTips.handleRefresh()
      } else {
        return this.$refs.container.handleRefresh()
      }
    },
    handleRes() {
      if (this.isSql) {
        this.$set(
          this.activeObj,
          'sql',
          crypto.encrypt(
            JSON.stringify({
              id: this.db,
              sql: this.sql,
            })
          )
        )
      }
      this.handleRefresh().then(res => {
        if (!this.validatenull(res)) {
          this.dataRes = JSON.stringify(res || {}, null, 4)
        } else {
          this.dataRes = ''
        }
        this.$message.success('数据刷新成功')
      })
    },

    // 处理sql查询时的数据刷新
    sqlHandleRes(sqlData) {
      // this.$set(this.activeObj, 'sqlData', sqlData)
      // 现在直接把sqlData直接加入到base.js的组件配置属性中了所以不需要再用$set去设置响应式的了，这样做也是方便如果用户先去配置筛选条件而没有配置sql查询
      Object.assign(this.activeObj.sqlData, sqlData)
      // console.log(this.activeObj)
      this.handleRefresh()
    },
    handleSql() {
      this.show = true
      this.dataRes = ''
      this.$nextTick(() => {
        let result = false
        if (this.isSql && !this.validatenull(this.sql)) {
          result = true
        } else if (this.isApi && !this.validatenull(this.activeObj.url)) {
          result = true
        } else {
          result = true
        }
        if (result) this.handleRes()
      })
    },
    initSqlList() {
      getList(1, 100).then(res => {
        const data = res.data.data
        this.DIC.sql = data.records.map(ele => {
          return {
            label: ele.name,
            value: ele.id,
          }
        })
      })
    },
    codeClose(value) {
      if (this.configData.includes(this.code.type)) {
        this.config[this.code.type] = value
      } else {
        // this.activeObj[this.code.type] = value;
        // 这行代码会出现的问题是，dataQuery, dataHeader， websocketHeader， websocketQuery是后添加的属性，所以不是响应式
        // this.activeObj[this.code.type] = value;
        let item
        if (this.tipsIndex) {
          item = this.findTipsList(this.activeIndex)
        } else {
          item = this.findlist(this.activeIndex)
        }
        this.$set(item, this.code.type, value)
      }
    },
    openCode(type) {
      this.showRelationship = false
      if (type == 'clickFormatter' || type == 'initFormatter') {
        this.eventListType = type
        this.showRelationship = true
      } else {
        this.code.type = type
        if (this.configData.includes(type)) {
          // 这个是没有选中图表的时候公共配置打开公共请求参数和公共请求头的时候
          this.code.obj = this.config[type]
        } else {
          // 选中图表后配置请求参数和请求头
          this.code.obj = this.activeObj[type]
        }
        this.code.box = true
      }
    },
    initFun() {
      ;['setScale', 'setResize'].forEach(ele => {
        this[ele] = this.$refs.container[ele]
      })
      ;['handleAdd'].forEach(ele => {
        // this[ele] = this.$refs.componentMenu[ele];
        this[ele] = this.$refs.top[ele]
      })
    },
    // 右键菜单
    handleContextMenu(e, item = {}) {
      if (!item.index || this.isKeysCtrl) return
      else if (!this.isSelectActive) {
        this.active = [item.index]
        this.activeIndex = item.index
      }
      this.$nextTick(() => this.$refs.contentmenu.show(e.clientX, e.clientY))
    },
    //监听键盘的按键
    listen() {
      // document.onkeydown = (e) => {
      //   console.log('chufale')
      //   console.log(e.keyCode)
      //   this.keys.ctrl = e.keyCode === 17;
      //   if (e.target.nodeName == 'TEXTAREA' || e.target.nodeName == 'INPUT') return;
      //   // 按下空格键
      //   if (e.keyCode == 32) {
      //     e.preventDefault();
      //     this.keys.space = true;
      //   }
      // }; // contentmenu.vue中已经使用了onkeydown导致这个被覆盖了所以改成addEventListener的添加方式
      // document.onkeyup = () => {
      //   this.keys.ctrl = false;
      // };
      document.addEventListener('keydown', e => {
        // console.log(
        //   'this.active.length',
        //   this.active.length,
        //   this.isSelectActive
        // )
        this.keys.ctrl = e.keyCode === 17
        if (e.target.nodeName == 'TEXTAREA' || e.target.nodeName == 'INPUT')
          return
        // 按下空格键
        if (e.keyCode == 32) {
          e.preventDefault()
          this.keys.space = true
        }
      })
      document.addEventListener('keyup', e => {
        if (e.keyCode == 32) {
          e.preventDefault()
          this.keys.space = false
        }
      })
      document.addEventListener('keydown', e => {
        if(e.keyCode == 65 && e.altKey && e.ctrlKey){
          if(!this.$store.state.showTips){
            this.altSelect = []
            if(this.nav.length>0){
              this.getAltSelect(this.nav)
            }
            this.active = this.altSelect
          }
        }
        // 同时按下 CTRL和 Z  撤销
        if (e.keyCode == 90 && e.ctrlKey) {
          // 触发撤销事件
          this.editorUndo()
        }
        // 同时按下 CTRL和 S  保存
        if (e.keyCode == 83 && e.ctrlKey) {
          // 禁止默认浏览器默认行为，默认会保存改网页对应的html文件
          e.preventDefault()
          // 触发撤销事件
          this.$refs.headers.handleBuild()
        }
        // 按下删除键
        if (e.keyCode == 46 && this.activeIndex && this.active.length < 2) {
          //这是delete健，当然也可以根据自己的需求更改
          this.$refs.contentmenu.handleDel() //操作方法
        }
        if (this.isSelectActive && this.active.length > 1) {
          if (e.keyCode === 46) {
            // console.log(7777,e)
            this.$confirm(`是否删除所选图层?`, '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning',
            }).then(() => {
              this.active.forEach(index => {
                const params = this.findnav(index)
                console.log('params==>', params)
                params.parent.splice(params.count, 1)
              })
              this.handleInitActive()
            })
          }
        }
        // 按下 CTRL + ALT + ↑ 向上移动图层
        if (e.keyCode == 38 && e.ctrlKey && e.altKey && this.activeIndex) {
          // const currentIndex = this.nav.findIndex(item => item.index === this.activeIndex)
          // if (currentIndex == 0) return
          // this.nav.splice(currentIndex -1, 0, this.nav.splice(currentIndex, 1)[0])
          // nav数组可能包含的子项为一个文件夹其中的children包含着子项，因此不能简单的单层查找
          // this.showTips? this.navTips : this.nav ==> this.findIndexAndList的第二个参数要根据是否在tips的编辑页面决定传入的组件数组
          const { list, currentIndex } = this.findIndexAndList(
            this.activeIndex,
            this.showTips ? this.navTips : this.nav
          )
          if (currentIndex == 0) return
          list.splice(currentIndex - 1, 0, list.splice(currentIndex, 1)[0])
        }
        // 按下 CTRL + ALT + ↓ 向下移动图层
        if (e.keyCode == 40 && e.ctrlKey && e.altKey && this.activeIndex) {
          const { list, currentIndex } = this.findIndexAndList(
            this.activeIndex,
            this.showTips ? this.navTips : this.nav
          )
          if (currentIndex == list.length - 1) return
          list.splice(currentIndex + 1, 0, list.splice(currentIndex, 1)[0])
        }
        if (e.keyCode == 67 && e.ctrlKey) {
            let copyArr = []
            this.active.forEach(ele => {
              // 判断当前查找项是在tips还是还是build页面中
              const item = this.showTips
                ? this.findnav(ele, true, this.navTips)
                : this.findnav(ele, true)
              const obj = this.deepClone(item.obj)
              obj.index = uuid()
              // obj.left = obj.left + obj.component.width / 2
              // obj.top = obj.top - obj.component.height / 2
              // 如果当前复制粘贴的组件是一个弹框组件那么就需要把弹框组件的子组件的index值以及parentTipsIndex重新设置，不然会造成与本体里的子组件的序号值相同
              if (obj.component.prop === 'tips') {
                obj.list.forEach(item => {
                  item.index = uuid()
                  item.parentTipsIndex = obj.index
                })
              }
              copyArr.unshift(obj)
            })
            myStorage.set('copyArr', copyArr)
        }
        // 同时按下 CTRL 和  V  粘贴 this.cursorPosition !== 'INPUT'
        if (e.keyCode == 86 && e.ctrlKey) {
          if (
            e.target.tagName === 'BODY' ||
            e.target.offsetParent._prevClass == 'el-input avue-draggable__focus'
          ) {
            let active = []
            let copyArr = myStorage.get('copyArr')
            copyArr.forEach(item => {
              item.index = uuid()
              if (!this.showTips) {
                // this.nav.push(item)
                this.nav.unshift(item)
              } else {
                const tipsObj = this.findlist(this.tipsIndex) || {}
                this.$set(item, 'parentTipsIndex', this.tipsIndex)
                // tipsObj.list.push(item)
                tipsObj.list.unshift(item)
              }
              active.unshift(item.index)
            })
              this.$nextTick(()=>{
                this.active = active;
                this.activeIndex = null
              })
            }
          // this.$parent.handleInitActive();
        }
      })
      document.addEventListener('keyup', () => {
        this.keys.ctrl = false
      })
    },
    findIndexAndList(activeIndex, arr) {
      let list, currentIndex
      arr.forEach((f_item, f_index) => {
        // 如果该项为一个文件夹那么就要对其的children数组进行遍历
        if (f_item.children) {
          let resultIndex = f_item.children.findIndex(s_item => {
            return s_item.index === activeIndex
          })
          if (resultIndex > -1) {
            list = f_item.children
            currentIndex = resultIndex
          }
        } else {
          // 如果不包含children那么就直接比较index值
          if (f_item.index === activeIndex) {
            list = arr
            currentIndex = f_index
          }
        }
      })
      return { list, currentIndex }
    },
    setActive(val, result, fun) {
      let obj = null
      if (this.showTips) {
        obj = this.$refs.editTips.getDragObj(val)
      } else {
        obj = this.$refs.container.getDragObj(val)
      }

      if (!this.validatenull(obj)) obj[0][fun](result)
    },
    //批量成组
    handleFloder() {
      let floder = createFile()
      let list = this.active
      let params = findTree(this.nav,(item)=> item.index===list[0])
      if(params){
        if(params.parent){
          list.forEach(ele => {
            let item = findTree(this.nav,(item)=> item.index===ele)
            if(item){
              if(item.parent){
                item.parent.children.splice(item.index, 1)
              }else{
                this.nav.splice(item.index, 1)
              }
              floder.children.push(item.item)
            }
          })
          params.parent.children.push(floder)
        }else{
          list.forEach(ele => {
            let item = findTree(this.nav,(item)=> item.index===ele)
            if(item){
              if(item.parent){
                item.parent.children.splice(item.index, 1)
              }else{
                this.nav.splice(item.index, 1)
              }
                floder.children.push(item.item)
            }
          })
          this.nav.push(floder)
        }
      }
      this.handleInitActive()
    },
    //批量删除
    handleDeleteSelect() {
      this.$confirm(`是否批量删除所选图层?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        this.active.forEach(ele => {
          let item = findTree(this.nav,(item)=> item.index===ele)
          if(item){
            if(item.parent){
              item.parent.children.splice(item.index, 1)
            }else{
              this.nav.splice(item.index, 1)
            }
          }
        })
        this.handleInitActive()
      })
    },
    vaildProp(name, list) {
      // console.log('name', name)
      // console.log('list', list)
      // console.log('this.activeComponent', this.activeComponent)
      if (list)
        return (
          list.includes(this.activeComponent.prop) || this.active.length > 1
        )
      return this.dicOption[name].includes(this.activeComponent.prop)
    },
    formatTooltip(val) {
      return parseInt(val)
    },
    //打开图库
    handleOpenImg(item, type) {
      // console.log('打开图片')
      this.$refs.imglist.openImg(item, type)
    },
    //打开3d模型
    handleOpen3d() {
      this.$refs.threeDimglist.openImg()
    },
    // 渲染图片回调
    drawWarnSaveData(parms) {
      if (this.activeObj.component.prop == 'img') {
        if (!this.activeObj.option.sizeLock) {
          if (parms.drawConditionList) {
            if (parms.drawConditionList[0].imgUrl) {
              function setSizeCb(_this, width, height) {
                _this.activeObj.component.width = width
                _this.activeObj.component.height = height
              }
              this.setImgNaturalSize(
                parms.drawConditionList[0].imgUrl,
                setSizeCb
              )
            }
          }
        }
      }
    },
    //图库框回调赋值
    handleSetimg(val, type, index) {
      console.log(val, type)
      let params = type.split('.')[1]
      if (type.includes('config')) {
        this.config[params] = val
      } else if (type.includes('activeObj.data.value')) {
        this.activeObj.data.value = val
        // 如果当前组件是图片那么要把图片的原始尺寸设置给width和height
        if (this.activeObj.component.prop == 'img') {
          if (!this.activeObj.option.sizeLock) {
            function setSizeCb(_this, width, height) {
              _this.activeObj.component.width = width
              _this.activeObj.component.height = height
            }
            this.setImgNaturalSize(val, setSizeCb)
          }
        }
      } else if (type.includes('activeObj.data')) {
        this.activeObj.data = val
      } else if (type.includes('activeObj')) {
        this.activeObj[params] = val
      } else if (type.includes('activeOption')) {
        this.activeOption[params] = val
        // this.activeObj.data[index].value = val
        console.log(this.activeOption, this.activeObj)
        // if (this.activeObj.component.prop == 'img') {
        //   function setSizeCb(_this, width, height) {
        //     _this.activeObj.component.width = width
        //     _this.activeObj.component.height = height
        //   }
        //   this.setImgNaturalSize(val, setSizeCb)
        // }
      }
    },
    /* **************************标尺方法开始******************************* */
    // 滚轮触发
    handleScroll() {
      const screensRect = this.$refs.screensRef.getBoundingClientRect()
      const canvasRect = this.$refs.canvas.getBoundingClientRect()
      // 标尺开始的刻度
      const startX =
        (screensRect.left + this.thick - canvasRect.left) / this.scale
      const startY =
        (screensRect.top + this.thick - canvasRect.top) / this.scale
      // console.log(startX,77777)
      this.startX = startX >> 0
      this.startY = startY >> 0
    },
    // 控制缩放值
    handleWheel(e) {
      if (e.ctrlKey || e.metaKey) {
        e.preventDefault()
        const nextScale = parseFloat(
          Math.max(0.2, this.scale - e.deltaY / 500).toFixed(2)
        )
        this.scale = nextScale
      }
      this.$nextTick(() => {
        this.handleScroll()
      })
    },
    // 初始化标尺数值
    initSize() {
      this.$nextTick(() => {
        let menuShow = 0;
        let paramsShow = 0;
        if(!this.menuShow){
          menuShow = 187 // 图层隐藏时
        }
        if(!this.paramsShow){
          paramsShow = 340 //操作隐藏时
        }
        let width = window.innerWidth - 537 + menuShow + paramsShow;
        let height = window.innerHeight - 86;
        this.width = width - this.thick;
        this.height = height - this.thick;
        // 画布阴影部分
        this.shadow = { x: 0, y: 0, width, height }
        // 滚动居中
        let rate =(this.width-105) / this.config.width
        // let rate = Math.min((this.width-120) / this.config.width, (this.height-120) / this.config.height)
        this.scale = rate
        // this.config.scale = this.scale
        let dom = this.$refs.containerRef.getBoundingClientRect()
        // 滚动位置
        setTimeout(()=>{
          this.$refs.screensRef.scrollLeft= 0;
          this.$refs.screensRef.scrollTop= 0 
          const screensRect = this.$refs.screensRef.getBoundingClientRect()
          const canvasRect = this.$refs.canvas.getBoundingClientRect()
          // 标尺开始的刻度
          const startX =
            (screensRect.left + this.thick - canvasRect.left) / this.scale
          const startY =
            (screensRect.top + this.thick - canvasRect.top) / this.scale
          let x = this.containerWidth/this.scale  // containerWidth .screen-container 宽度
          let y = this.containerHeight/this.scale  // containerHeight .screen-container 高度
          let a 
          let b
          //计算滚动距离
          let c = 55/(dom.width/x) //58 中间内容距离左边尺标距离
          let d = 38/(dom.height/y)//38 中间内容距离上边尺标距离
          a =  (-startX-c) / x 
          b =  (-startY-d) / y
          this.$refs.screensRef.scrollLeft =  dom.width *a
          this.$refs.screensRef.scrollTop = dom.height *b
        })
      })
    },
    // resize
    iniresize() {
      window.addEventListener('resize', () => {
        this.initSize()
        this.rendIndex++
      })
    },
    // 图片点击事件
    imgClick() {
      this.isShowReferLine = !this.isShowReferLine
    },
    // 鼠标按下事件
    dragMousedown(e) {
      // 如果按下了空格键,且按下鼠标左键,那么鼠标变抓手,开启滚动条随鼠标拖动的操作
      this.handleInitActive()
      if (this.keys.space) {
        this.dragSlide = true
        window.stardragEvent = e
        window.startSlideX = this.$refs.screensRef.scrollLeft
        window.startSlideY = this.$refs.screensRef.scrollTop
        // console.log(window.startSlideX,5555)
      } else {
        // console.log(e)
        if (e.target.id === 'container') {
          this.$refs.container.handleMouseDown(e)
        }
      }
    },
    //鼠标抬起操作
    dragMouseup() {
      this.dragSlide = false
    },
    // 鼠标移动骚操作
    dragMousemove(e) {
      if (this.dragSlide) {
        let x = e.clientX - window.stardragEvent.clientX
        let y = e.clientY - window.stardragEvent.clientY
        this.$refs.screensRef.scrollLeft = window.startSlideX - x
        this.$refs.screensRef.scrollTop = window.startSlideY - y
      }
    },
    selectNav(item) {
      if (Array.isArray(item)) {
        if(this.isKeysCtrl){
          this.active = this.active.concat(item)
        }else{
          if (this.getInclude1(this.active, item)) {
            const arr = this.active.filter(x => !item.some(y => y === x))
            this.active = arr
          } else {
            this.active  = item
          }
        }
        // if (this.getInclude1(this.active, item)) {
        //   const arr = this.active.filter(x => !item.some(y => y === x))
        //   this.active = arr
        // } else {
        //   if (this.active.length > 0) {
        //     let temp = []
        //     for (const itemData of item) {
        //       this.active.find(i => i === itemData) ? '' : temp.push(itemData)
        //     }
        //     this.active = this.active.concat(temp)
        //   } else {
        //     this.active = this.active.concat(item)
        //   }
        // }
      } else if (this.isKeysCtrl) {
        if (this.active.length > 0) {
          let falge = true
          this.active.forEach((aitem, index) => {
            if (aitem === item) {
              this.active.splice(index, 1)
              falge = false
            }
          })
          if (falge) {
            this.active.push(item)
          }
        } else {
          this.active.push(item)
        }
      } else if (!this.active.includes(item)) {
        this.active = [item]
        this.activeIndex = item
      }
      let active = this.active.filter((value, index, self) => {
        return self.indexOf(value) === index;
      });
      this.active  = active
      // console.log('this.active',this.active )
    },
    getInclude1(arr1, arr2) {
      let temp = []
      for (const item of arr2) {
        arr1.find(i => i === item) ? temp.push(item) : ''
      }
      return temp.length === arr2.length ? true : false
    },
    // 清除背景图片
    clearBgImg() {
      this.config.backgroundImage = ''
    },
    filterList(id) {
      var p = this.childList.filter(item => {
        return item.index == id
      })
      return p
    },
    // 对比两个数组 返回 addList 和delList
    compareListFn(newList = [], oldList = []) {
      let copyNew = this.deepClone(newList)
      let copyOld = this.deepClone(oldList)
      for (let i = 0; i < copyNew.length; i++) {
        for (let j = 0; j < copyOld.length; j++) {
          if (copyNew[i] == copyOld[j]) {
            copyNew.splice(i, 1)
            copyOld.splice(j, 1)
            i--
            j--
            continue
          }
        }
      }
      return { addList: copyNew, delList: copyOld }
    },
    // 对比两个对象，返回addList 和DelList
    compareObjFn(newObj = {}, oldObj = {}) {
      let copyNew = this.deepClone(newObj)
      let copyOld = this.deepClone(oldObj)
      for (let newKey in copyNew) {
        // for (let oldKey in copyOld) {
        //   if (newKey[newKey] == copyOld[oldKey]) {
        //     delete copyNew[newKey];
        //     delete copyOld[oldKey];

        //     continue;
        //   }
        // }
        if (copyOld[newKey]) {
          delete copyNew[newKey]
          delete copyOld[newKey]
        }
      }
      return { addObj: copyNew, delObj: copyOld }
    },
    // 更新sqlData
    updataSqlData(compareObj) {
      if (this.activeObj.sqlChild && this.activeObj.sqlChild.index) {
        for (var i = 0; i < this.activeObj.sqlChild.index.length; i++) {
          for (var j = 0; j < this.childList.length; j++) {
            if (this.activeObj.sqlChild.index[i] == this.childList[j].index) {
              this.addAndSumFn(compareObj, this.childList[j].sqlData)
              break
            }
          }
        }
      }
    },
    //  增加新增对象 去掉删除对象
    addAndSumFn(compareObj, proObj) {
      if (compareObj.addObj) {
        //新增
        Object.assign(proObj, compareObj.addObj)
      } else {
        for (var key in compareObj.delObj) {
          delete proObj[key]
        }
      }
    },
    // 更新activeObj
    updateActiveObj(nelVal) {
      Object.assign(this.activeObj, nelVal)
    },
    changeStatus(status) {
      let alignLine = {
        enable: status,
        top: 0,
        bottom: 0,
        left: 0,
        right: 0,
        vertical: 0,
        horizontal: 0,
        topShow: false,
        bottomShow: false,
        leftShow: false,
        rightShow: false,
        verticalShow: false,
        horizontalShow: false,
      }
      this.$store.commit('SET_ALIGNLINE_INFO', alignLine)
    },
    handleToolClick(action) {
      this.$refs.headers.handleToolClick(action)
    },
    // 点击关联组件
    relevanceCom() {
      this.dataStorageRelevanceVisible = true
    },
    // 在图库点击图片后要把图片的原始尺寸配置给拖拽框
    setImgNaturalSize(src, cb) {
      let img = new Image()
      img.src = src
      if (img.complete) {
        cb(this, img.width, img.height)
      } else {
        img.onload = () => {
          cb(this, img.width, img.height)
        }
      }
    },
    // 修复新增组件没有apiAuthenticationForm对象认证方式选择basic auth时不出现相关选项的问题
    apiAuthenticationMethodChange(activeObj, val) {
      if (val === 'basicAuth') {
        if (!activeObj.apiAuthenticationForm) {
          this.$set(activeObj, 'apiAuthenticationForm', {
            authenticationUrl: '',
            userName: '',
            password: '',
          })
        }
      }
    },
    // 处理发布之后的回调函数
    async handleReleaseCbFn(infoData) {
      this.$refs.headers.updateVersion(infoData)
      const result = await getVersionData(this.visualId, infoData.version)
      this.$bus.$emit('getNewVersion', result.data.data)
      this.$refs.headers.handleBuild(async () => {
        try {
          infoData.id = this.obj.config.visualId
          // infoData.configId = this.obj.config.id
          const res = await saveReleaseInfo(infoData)
          if (res.data.success) {
            this.$refs.releaseDialog.$refs.releaseForm.resetFields()
            // 关闭表单
            this.releaseDialogVisible = false
            const openlink = window.location.origin + '/share/' + infoData.path
            window.open(openlink, '_blank')
          } else {
            this.$message.error(res.data.msg)
          }
        } catch (error) {
          console.log(error)
        }
        // 重置表单
      }, infoData)
    },
    async handleCancelReleaseCbFn() {
      try {
        const res = await closeRelease({
          id: this.obj.config.visualId,
          isRelease: 0,
        })
        if (res.data.success) {
          this.$message.success('已取消发布')
        }
      } catch (error) {
        console.log(error)
      }
    },
    editPageParams() {
      this.pageParamsDialogVisible = true
    },
    pageParamsConfirm() {
      this.config.pageParamsList = this.deepClone(
        this.$refs.pageParams.tableData
      )
      //多参数查询需要一份数据
      let multiparameterList = this.deepClone(this.$refs.pageParams.tableData)
      multiparameterList.forEach(item => {
        item.paramsValue = ''
      })
      this.config.multiparameterList = multiparameterList
      this.pageParamsDialogVisible = false
    },
  },
}
</script>
<style lang="scss">
@import '../styles/style.scss';
@import '~@/styles/buildVariables.scss';
.refer-line-img {
  position: absolute;
  left: 0;
  z-index: 5;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  img {
    width: 100%;
  }
}
.engineering-ti {
  position: relative;
  top: 3px;
  font-size: 20px !important;
  color: #409eff !important;
  margin-left: 5px !important;
}
#screens {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: auto;
}
.screen-container {
  position: relative;
  width: var(--containerWidth);
  height: var(--containerHeight);
  background-color: $bgc5;
  // background: url(https://img.alicdn.com/tfs/TB184VLcPfguuRjSspkXXXchpXa-14-14.png) repeat;
}

.dragghanle {
  cursor: pointer;
}
.canvas {
  position: absolute;
  top: 50%;
  left: 50%;
}
.section {
  flex: 1;
  overflow: hidden;
  position: relative;
  height: 100%;
}
.bgimg-box {
  position: relative;
  &:hover {
    i {
      opacity: 1;
    }
  }
  i {
    position: absolute;
    font-size: 24px !important;
    top: -12px;
    right: -12px;
    color: #3a89fe !important;
    cursor: pointer;
    opacity: 0;
  }
}
.btn_tool_box {
  width: 100%;
  height: 30px;
  display: flex;
  justify-content: space-around;
  align-items: center;
  background-color: $bgc1;
  .head_btn {
    width: 26px;
    height: 22px;
    background-color: $bgc1;
    &:hover {
      background-color: $bgc6;
    }
  }
}
</style>
<style lang="scss">
.left-tab {
  .el-tabs__content {
    overflow-y: auto;
    height: 851px;
  }
  // .el-tabs__item,
  // .is-top {
  //   color: #bcc9d4 !important;
  // }
}
.tabs_pane {
  > .el-tabs__header {
    padding: 0 16px;
  }
  .el-tabs__item {
    color: #fff;
    &.is-active {
      color: #409eff;
    }
  }
}
.tabs {
  height: 100%;
  overflow-y: auto;
}
.swiper-handles {
  display: flex;
  justify-content: flex-end;
  margin: 5px 0;
  > i {
    margin: 0 10px;
    cursor: pointer;
  }
}
.img-box {
  height: 50px;
  position: relative;
  overflow: hidden;
}
.noImg-box {
  height: 50px;
  display: flex;
  flex-direction: column;
  // justify-content: center;
  align-items: center;
  border: 1px solid rgba($color: #3a89fe, $alpha: 0.5);
  font-size: 12px;
  > span {
    margin-left: 10px;
    color: #fff;
    cursor: pointer;
  }
}
.swiper-image {
  height: 100%;
}
.image-mask {
  opacity: 0;
  position: absolute;
  background-color: rgba($color: #000, $alpha: 0.5);
  left: 0;
  top: 0;
  height: 100%;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #fff;
  font-size: 12px;
}
.image-mask:hover {
  opacity: 1;
}
.handle-img {
  display: flex;
  justify-content: center;
  > span {
    margin: 0 5px;
    cursor: pointer;
  }
  > span:nth-child(2) {
    font-weight: 900;
  }
}
</style>
