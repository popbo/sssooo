<template>
  <div :class="b()" :style="styleSizeName" @click="handleClick" @dblclick="handleDblClick">
    <div :id="hid" v-if="reload" :style="styleSizeName"></div>
  </div>
</template>

<script>
  // import { uuid } from '@/utils/utils';
  import { uuid } from '@/utils/utils.min.js';
  import create from '../../create';
  export default create({
    name: 'clapper',
    data() {
      return {
        hid: 'main_' + uuid(),
        reload: true,
        config: {},
      };
    },
    computed: {
      autoplay() {
        return this.option.autoplay;
      },
    },
    watch: {
      dataChart: {
        handler() {
          this.reload = false;
          this.$nextTick(() => {
            this.reload = true;
            setTimeout(() => {
              new Clappr.Player({
                parentId: '#' + this.hid,
                source: this.dataChart.value,
                autoPlay: this.autoplay,
                mute: true,
                height: '100%',
                width: '100%',
              });
            });
          });
        },
        deep: true,
      },
    },
    methods: {
      handleClick() {
        this.clickFormatter &&
          this.clickFormatter(
            {
              data: this.dataChart,
            },
            this.getItemRefs()
          );
      },
      handleDblClick() {
        this.dblClickFormatter &&
          this.dblClickFormatter(
            {
              data: this.dataChart,
            },
            this.getItemRefs()
          );
      },
    },
  });
</script>
