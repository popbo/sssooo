<!-- 图片配置 -->
<template>
  <div>
    <!-- <el-form-item label="开启旋转">
      <avue-switch v-model="main.activeOption.rotate"></avue-switch>
    </el-form-item> -->
    <el-form-item label="锁定图片尺寸">
      <avue-switch v-model="main.activeOption.sizeLock"></avue-switch>
    </el-form-item>
    <el-form-item
      label="图片宽度锁定值"
      label-width="130px"
      v-show="main.activeOption.sizeLock"
    >
      <el-input-number
        :min="0"
        v-model="main.activeOption.sizeLockObject.width"
        controls-position="right"
      ></el-input-number>
    </el-form-item>
    <el-form-item
      label="图片高度锁定值"
      label-width="130px"
      v-show="main.activeOption.sizeLock"
    >
      <el-input-number
        :min="0"
        v-model="main.activeOption.sizeLockObject.height"
        controls-position="right"
      ></el-input-number>
    </el-form-item>
    <el-form-item label="透明度">
      <el-input-number
        :min="0"
        :max="1"
        :step="0.1"
        v-model="main.activeOption.opacity"
        controls-position="right"
      ></el-input-number>
    </el-form-item>
    <template v-if="main.activeOption.rotate">
      <el-form-item label="旋转时间">
        <el-input-number
          v-model="main.activeOption.duration"
          controls-position="right"
          :min="0"
        />
      </el-form-item>
    </template>
    <el-form-item label="旋转">
      <el-input-number
        v-model="main.activeOption.rotateAngle"
        controls-position="right"
        :min="0"
        :max="360"
      ></el-input-number>
    </el-form-item>
    <el-form-item label="地址动态切换">
      <avue-switch v-model="main.activeOption.dynamicSwitch"></avue-switch>
    </el-form-item>
    <el-form-item label="选中边框设置">
      <avue-switch v-model="main.activeOption.clickBorder"></avue-switch>
    </el-form-item>
    <template v-if="main.activeOption.clickBorder">
      <el-form-item label="边框大小">
        <el-input-number
          v-model="main.activeOption.border"
          controls-position="right"
          :min="0"
        ></el-input-number>
      </el-form-item>
      <el-form-item label="边框颜色">
        <avue-input-color
          v-model="main.activeOption.borderColor"
        ></avue-input-color>
      </el-form-item>
      <el-form-item label="边框阴影大小">
        <el-input-number
          v-model="main.activeOption.borderShadow"
          controls-position="right"
          :min="0"
        ></el-input-number>
      </el-form-item>
      <el-form-item label="边框阴影颜色">
        <avue-input-color
          v-model="main.activeOption.borderShadowColor"
        ></avue-input-color>
      </el-form-item>
    </template>
    <!-- 静态图片 -->
    <template v-if="!main.activeOption.dynamicSwitch">
      <el-form-item label="图片地址">
        <img
          v-if="main.activeObj.data.value"
          :src="main.activeObj.data.value"
          alt=""
          class="img-tu"
        />
        <el-input v-model="main.activeObj.data.value">
          <div
            @click="main.handleOpenImg('activeObj.data.value')"
            slot="append"
            class="openimg_box"
          >
            <i class="iconfont icon-img"></i>
          </div>
        </el-input>
      </el-form-item>
    </template>
    <el-form-item label="图片动效">
      <el-select v-model="main.activeOption.animationType" placeholder="请选择">
        <el-option
          v-for="item in animationOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        >
        </el-option>
      </el-select>
    </el-form-item>
    <template v-if="main.activeOption.animationType === 'rotate'">
      <el-form-item label="倾斜角度">
        <el-input
          v-model="main.activeOption.angleOfInclination"
          class="input_unit"
        ></el-input>
        deg
      </el-form-item>
      <el-form-item label="旋转时间">
        <el-input
          v-model="main.activeOption.rotateTime"
          class="input_unit"
        ></el-input>
        ms
      </el-form-item>
      <el-form-item label="旋转方向">
        <el-select
          v-model="main.activeOption.rotateDirection"
          placeholder="请选择"
        >
          <el-option
            v-for="item in rotateDirectionOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>
    </template>
    <template v-if="main.activeOption.animationType === 'blink'">
      <el-form-item label="动效速度">
        <el-slider
          v-model="main.activeOption.blinkTime"
          :marks="marks"
          :max="2000"
        >
        </el-slider>
        <el-input-number
          v-model="main.activeOption.blinkTime"
          controls-position="right"
          :min="min"
          :max="2000"
        ></el-input-number>
      </el-form-item>
    </template>
    <template v-if="main.activeOption.animationType === 'jump'">
      <el-form-item label="动画周期">
        <el-input
          v-model="main.activeOption.jumpTime"
          class="input_unit"
        ></el-input>
        ms
      </el-form-item>
      <el-form-item label="跳动方向">
        <el-select
          v-model="main.activeOption.jumpDirection"
          placeholder="请选择"
        >
          <el-option
            v-for="item in jumpDirectionOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="跳动开始位置">
        <el-input
          v-model="main.activeOption.jumpStart"
          class="input_unit"
        ></el-input>
        px
      </el-form-item>
      <el-form-item label="跳动结束位置">
        <el-input
          v-model="main.activeOption.jumpEnd"
          class="input_unit"
        ></el-input>
        px
      </el-form-item>
    </template>
  </div>
</template>

<script>
export default {
  inject: ['main'],
  data() {
    return {
      animationOptions: [
        {
          value: 'none',
          label: '无',
        },
        {
          value: 'rotate',
          label: '旋转',
        },
        {
          value: 'blink',
          label: '闪烁',
        },
        {
          value: 'jump',
          label: '跳动',
        },
      ],
      rotateDirectionOptions: [
        {
          value: 'anticlockwise',
          label: '逆时针',
        },
        {
          value: 'clockwise',
          label: '顺时针',
        },
      ],
      jumpDirectionOptions: [
        {
          value: 'upDown',
          label: '上下',
        },
        {
          value: 'leftRight',
          label: '左右',
        },
      ],
      marks: {
        0: '0ms',
        2000: '2000ms',
      },
    }
  },
  watch: {
    'main.activeOption.dynamicSwitch': {
      handler(newVal) {
        if (newVal) {
          this.main.tabsActive = '5'
        }
      },
    },
  },
}
</script>

<style lang="scss" scoped>
/deep/.el-input-group__append {
  padding: 0;
}
.openimg_box {
  width: 40px;
  text-align: center;
}
.input_unit {
  width: 150px;
}
.img-tu {
  max-width: 100% !important;
  max-height: 400px !important;
}
</style>
