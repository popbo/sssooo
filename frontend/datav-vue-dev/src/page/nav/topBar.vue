<template>
  <div class="top-nav">
    <div class="log">
      <img src="@/assets/yiwei_home.png" alt="" />
    </div>
    <el-menu
      class="top-menu"
      mode="horizontal"
      :style="{ 'margin-left': '260px', position: 'absolute' }"
      active-text-color="#FFFFFF"
      :default-active="activeIndex"
    >
      <div v-for="item in routes" :key="item.path" class="nav-item">
        <app-link :to="item.path">
          <el-menu-item :index="item.path">{{ item.meta.title }}</el-menu-item>
        </app-link>
      </div>
    </el-menu>
    <UserMenu></UserMenu>
  </div>
</template>
<script>
import { topBarRouter } from "@/router/topBarRouter";
import { dicOption } from "@/option/config";
import appLink from "./appLink";
import UserMenu from './UserMenu'
export default {
  data() {
    return {
      routes: topBarRouter,
      dialogTableVisible: false,
      dicOption: dicOption,
    };
  },
  computed: {
    activeIndex() {
      return this.$route.path;
    },
  },
  created() {
    // console.log(this.dicOption.fontFamily);
  },
  methods: {},
  components: {
    appLink,
    UserMenu
  },
};
</script>

<style scoped lang='scss'>
.top-nav {
  width: 100%;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1001;
  overflow: hidden;
  border-bottom: 1px solid #121111;
  background-color: #1f212c;
  .log {
    padding: 0 16px;
    line-height: 56px;
    height: 56px;
    font-size: 24px;
    font-weight: bold;
    float: left;
    img {
      width: auto;
      max-height: 45px;
      vertical-align: middle;
    }
  }

  .el-menu {
    float: left;
    border: none !important;
    background-color: #1f212c !important;
    .nav-item {
      display: inline-block;
      .el-menu-item:not(.is-active) {
        color: #bfbfbf;
        opacity: 0.65;
        &:hover {
          background-color: rgba(158, 158, 158, 0.2) !important;
          opacity: 1;
        }

        &:focus {
          background-color: #324f62 !important;
          opacity: 1;
          color: #f4f4f5;
        }
      }
      .el-menu-item.is-active {
        background-color: #2681ff;
        color: #f4f4f5 !important;
        opacity: 1;
      }
    }
  }
}
</style>
