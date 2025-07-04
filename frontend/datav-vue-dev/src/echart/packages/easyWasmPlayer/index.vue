<template>
  <div class="easyWasmPlayer">
    <div ref="playerContainer" :id="id" style="width:100%;height:100%"></div>
  </div>
</template>

<script>
import create from '../../create';
export default create({
  name: 'easyWasmPlayer',
  data() {
    return {
      player:null,
      videoUrl:''
    };
  },
  mounted() {
    this.initPlayer();
  },
  beforeDestroy() {
    this.destroyPlayer();
  },
  methods: {
    callbackfun(e) {
      // console.log('callbackfun', e);
    },
    initPlayer() {
      this.videoUrl = this.data.value
      //实例化播放器
      this.player = new WasmPlayer(null,this.id,this.callbackfun,{HideKbs:false,openAudio:false})
      // 调用播放
      this.player.play(this.videoUrl,1)
    },
    destroyPlayer() {
      if (this.player) {
        this.player.destroy(); // 销毁播放器
        this.player = null;
      }
    },
  },
  watch: {
    "data.value":{
      handler(val){
        if(val){
          this.destroyPlayer()
          setTimeout(()=>{
            this.initPlayer()
          },500)
        }
      },
      deep:true
    }
  },
});
</script>
<style scoped lang="scss">
/deep/ .iconqingxiLOGO{
  display: none !important;
}
/deep/ .no-padding{
  display: none !important;
}
</style>