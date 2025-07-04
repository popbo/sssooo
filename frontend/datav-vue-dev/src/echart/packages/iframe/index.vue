<template>
  <div :class="b()"
       :style="styleSizeName"
       ref="main"
       @click="handleClick"
       @dblclick="handleDblClick"
       >
    <iframe :src="dataChart.value" allow="payment" ref='iframe' id="iframeId" name="iframe" v-if="!$attrs.display"
            draggable="false"></iframe>
  </div>
</template>

<script>
import create from "../../create";
export default create({
  name: "iframe",
  inject: ['contain'],
  data () {
    return {
      
    };
  },
  computed: {
  },
  created () { },
  mounted () {
  },
  methods: {
    handleClick () {
      this.clickFormatter && this.clickFormatter({
        data: this.dataChart
      }, this.getItemRefs());
    },
    handleDblClick () {
      this.dblClickFormatter && this.dblClickFormatter({
        data: this.dataChart
      }, this.getItemRefs());
    },
    postData(data) {
      console.log('触发了')
      this.$refs.iframe.contentWindow.postMessage(data, '*');
    }
  },
  props: {
    option: {
      type: Object,
      default: () => {
        return {};
      }
    }
  }
});
</script>


