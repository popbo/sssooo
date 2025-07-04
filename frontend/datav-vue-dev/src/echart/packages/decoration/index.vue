<template>
  <div :class="[b(),className]"
       :style="styleSizeName"
       @click="handleClick">
    <component :is="isName"
               :ref="id"
               :dur="dur"
               :color="color"
               v-if="reload"
               :style="styleChartName">
      {{dataChart.value}}
    </component>
  </div>
</template>

<script>
import create from "../../create";
// import { EventBus } from '@/bus.js'
export default create({
  name: "decoration",
  inject: ['contain', 'container','main'],
  data () {
    return {
      reload: true
    }
  },
  computed: {
    isName () {
      return 'dvDecoration' + this.option.type
    },
    dur () {
      return this.option.dur
    },
    color () {
      let result = [], color1 = this.option.color1, color2 = this.option.color2
      if (color1) result.push(color1)
      if (color2) result.push(color2)
      this.reload = false;
      this.$nextTick(()=>{
        this.reload = true
      })
      return result
    },
  },
  watch:{
    'contain.list':{
      handler(val){
        let newList = val.filter(item=>{
          return `list${item.index}`===this.id
        })
        if(newList[0].display===false){
          this.reload = false;
          setTimeout(()=>{
            this.reload = true;
          },250)
        }
      },
      deep:true
    },
  },
  methods: {
    handleClick () {
      this.updateClick(this.dataChart, 'clickFormatter');
      this.clickFormatter && this.clickFormatter({
        data: this.dataChart
      }, this.getItemRefs());
    },
    updateChart () {
      this.reload = false;
      this.$nextTick(() => {
        this.reload = true;
      })
    }
  }
});
</script>



