<template>
  <div class="imagemanage_box">
    <el-container style="height: 100%">
      <el-aside width="200px">
        <ul class="img_type_menu">
          <li v-for="(item, index) in imageTypeList" :class="{'is-active': index === currentIndex }" :key="item.id" @click="chooseImgType(item.type, index)">
            <i :class="'iconfont' + ' ' + item.icon"></i>
            {{item.label}}
          </li>
        </ul>
      </el-aside>
      <el-main>
        <ImgList :imgType='imgType'></ImgList>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import ImgList from './imglist'
export default {
  data() {
    return {
      imageTypeList: [
        {
          label: '大屏封面',
          id: 1,
          type: 'thumbnail',
          icon: 'icon-daping',
        },
        {
          label: '大屏背景',
          id: 2,
          type: 'back',
          icon: 'icon-beijingtupian'
        },
        {
          label: '组件图片',
          id: 3,
          type: 'component',
          icon: 'icon-zujian'
        },
        {
          label: 'svg图片',
          id: 4,
          type: 'svg',
          icon: 'icon-SVGtubiao'
        },
      ],
      imgType: 'thumbnail',
      currentIndex: 0
    }
  },
  components: {
    ImgList
  },
  created() {},

  methods: {
    chooseImgType(type, index) {
      this.imgType = type
      this.currentIndex = index
    }
  },
}
</script>

<style scoped lang='scss'>
.imagemanage_box {
  height: 100%;
}
.el-aside {
  padding-right: 10px;
  border-right: 1px solid #293241;
}
.img_type_menu {
  li {
    cursor: pointer;
    height: 40px;
    line-height: 40px;
    padding-left: 20px;
    &:hover {
      background-color: #3d404d;
      color: #ffffff;
    }
  }
}
.is-active {
  background-color: #3d404d;
  color: #3a89fe;
}
</style>
