<template>
  <div :class="b()"
       :style="styleSizeName">
    <div :ref="id"
         :style="styleChartName"></div>
  </div>
</template>

<script>
import create from "../../create";

export default create({
  name: "sankey",
   computed: {
    x2 () {
      return this.option.gridX2 || 20;
    },
    fontSize () {
      return this.option.fontSize || 14;
    }
  },

  methods: {
    updateChart () {
      var  option = {
            series: {
                type: 'sankey',
                layout: 'none',
                emphasis: {
                focus: 'adjacency'
                },
                data: [
                {
                    name: 'a'
                },
                {
                    name: 'b'
                },
                {
                    name: 'a1'
                },
                {
                    name: 'a2'
                },
                {
                    name: 'b1'
                },
                {
                    name: 'c'
                }
                ],
                links: [
                {
                    source: 'a',
                    target: 'a1',
                    value: 5
                },
                {
                    source: 'a',
                    target: 'a2',
                    value: 3
                },
                {
                    source: 'b',
                    target: 'b1',
                    value: 8
                },
                {
                    source: 'a',
                    target: 'b1',
                    value: 3
                },
                {
                    source: 'b1',
                    target: 'a1',
                    value: 1
                },
                {
                    source: 'b1',
                    target: 'c',
                    value: 2
                }
                ]
  }
};
   
      this.myChart.resize();
      this.myChart.setOption(option, true);
    }
  }
});

</script>
