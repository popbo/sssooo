<template>
  <div class="component-menu-nav">
    <el-menu class="nav" background-color="#212528" text-color="#fff" active-text-color="#409EFF" @mousedown="contain.handleMouseDown">
      <el-submenu :index="index+''" v-for="(item,index) in baseList" :key="index">
        <template slot="title">
          <el-tooltip effect="dark" :content="item.label" placement="top">
            <i :class="'nav__icon iconfont '+item.icon"></i>
          </el-tooltip>
          <span class="nav__title"> {{ item.label }} </span>
        </template>
        <div>
          <el-menu-item v-for="(citem,cindex) in item.children" @click="handleAdd(citem.option,true)" :key="cindex" class="menu-inline" :index="`${index}-${cindex}`">
            <div class="usehove" :draggable="true" @dragstart="dragStart($event, citem.option)">
              <img :src="citem.option.img" class="inside-img">
              <div class="bottom-text">{{citem.label}}</div>
            </div>
          </el-menu-item>
        </div>
      </el-submenu>
    </el-menu>
  </div>
</template>

<script>
// import { uuid } from '@/utils/utils'
import { uuid } from '@/utils/utils.min.js'
import baseList from '@/option/base'
export default {
  inject: ['contain'],
  provide() {
    return {
      contain: this.contain,
    }
  },
  data() {
    return {
      baseList: baseList,
    }
  },
  methods: {
    handleAdd(option, first = false) {
      let obj = this.deepClone(option)
      obj.left = 0
      obj.top = 0
      // 为了对齐线添加两个属性实时top 和 实时  left
      obj.intimeTop = 0
      obj.intimeLeft = 0
      obj.index = uuid()
      if (first) {
        this.contain.nav.unshift(obj)
      } else {
        this.contain.nav.push(obj)
      }
    },
    dragStart(ev, option) {
      let obj = this.deepClone(option)
      ev.dataTransfer.setData('text', JSON.stringify(obj))
    },
  },
}
</script>

<style lang='scss' scoped>
@import '~@/styles/buildVariables.scss';
.component-menu-nav {
  /deep/ .nav {
    border-bottom: 0 !important;
    height: 45px;
    line-height: 45px;
    // overflow: hidden;
  }
  .nav__icon {
    margin-right: 5px;
  }
  .nav__title {
    font-family: $fontFamily1;
    font-size: $fontSize1;
    color: $fontc1;
  }
  .nav .el-submenu .el-submenu__title,
  .nav .el-menu-item {
    height: 45px;
    line-height: 45px;
    font-size: 12px;
  }
  /deep/.el-submenu__title {
    font-family: $fontFamily1;
    font-size: $fontSize1;
    color: $fontc1;
    background-color: $bgc2 !important;
    &:hover {
      background-color: $bgc4 !important;
    }
  }
  .el-menu {
    border-right: 0px; // 为了去掉右侧的白线
  }
  .el-menu-item {
    height: 130px !important;
  }
  .menu-inline {
    text-align: center;
    display: inline-block !important;
    padding-left: 22px !important;
  }
  .bottom-text {
    color: #b1b1b1;
  }
  .inside-img {
    width: 135px;
    // height: 80px;
    border: 2px solid transparent;
    box-sizing: border-box;
  }
  .inside-img:hover {
    border-color: #006eff;
  }
  .usehove:hover {
    .bottom-text {
      color: #fff;
    }
  }
}
</style>
