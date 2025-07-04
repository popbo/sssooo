<template>
  <div class="hotteam">
    <div
      class="nimbus-com-item flex mb20"
      draggable
      v-for="(item, index) in hotteams"
      :key="index"
      @dragstart="dragstart($event, index)"
      @dragover.prevent
      @drop="dragDrop($event, index)"
    >
      <router-link
        class="nimbus-com-img mr20"
        :style="{ 'background-image': `url(${item.imgUrl})` }"
        :to="`/team/${item.id}`"
        target="_blank"
      ></router-link>
      <div class="nimbus-com-desc">
        <router-link class="font18 nowrap" :to="`/team/${item.id}`" target="_blank">{{
          item.name
        }}</router-link>
        <div class="font14 nimbus-txt-overflow">{{ item.brief }}</div>
      </div>
      <i class="nb-close" @click="delTeam(index)"></i>
    </div>
  </div>
</template>
<script>
  export default {
    name: 'tips',
    data() {
      return {
        hotteams: [
          {
            id: 11,
            name: '团队1',
            brief: '团队可以是一帮人',
            imgUrl: require('@/assets/wel.png'),
            sort: 1,
          },
          {
            id: 22,
            name: '团队2',
            brief: '团队可以是一帮人',
            imgUrl: require('@/assets/wel.png'),
            sort: 2,
          },
          {
            id: 33,
            name: '团队3',
            brief: '团队可以是一帮人',
            imgUrl: require('@/assets/wel.png'),
            sort: 3,
          },
        ],
        dragIdx: '',
      };
    },
    props: {
      option: Object,
      component: Object,
    },
    methods: {
      addTeam(team) {
        this.hotteams.push(team);
      },
      delTeam(idx) {
        this.hotteams.splice(idx, 1);
      },
      dragstart(e, index) {
        this.dragIdx = index;
      },
      dragDrop(e, index) {
        console.log({ e, index });
        let _teams = this.hotteams; //将hotteams存起来
        let _dragitem = _teams[this.dragIdx]; //将被拖拽的那条数据存起来
        _teams.splice(this.dragIdx, 1); //删除被拖拽的那条数据
        _teams.splice(index, 0, _dragitem); //将被拖拽的那条数据放到数组中指定的位置
      },
    },
  };
</script>
<style lang="scss" scoped>
  .hotteam {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
  }
  .nimbus-com-item {
    width: 496px;
    cursor: move;
    .font18 {
      margin-bottom: 30px;
    }
  }
  .nb-close {
    position: absolute;
    color: #4800fd;
    opacity: 0.26;
    top: 20px;
    right: 20px;
    cursor: pointer;
  }
  .nimbus-com-img {
    height: 200px;
    width: 200px;
    display: inline-block;
  }
</style>
