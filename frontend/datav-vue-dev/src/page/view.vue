<template>
  <el-scrollbar class="build views">
    <container :props="props"
               :option="option"
               ref="container"></container>
  </el-scrollbar>
  
</template>
<script>
import { EventBus } from '@/bus.js'
import init from '@/mixins/'
import createFontCssLink from '@/mixins/createFontCssLink.js'
export default {
  props: {
    option: Object,
    props: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  
  mixins: [init, createFontCssLink],
  mounted() {
    this.localSocket()
    // 因为 body middle content设置了底色那么在预览页面和发布页面如果大屏的背景图片是透明或者设置了背景颜色是透明的，这样会显示不出来，所以设置为透明底色
    const body = document.body
    body.style.setProperty('background-color', 'transparent') // content middle
    const middleDiv = document.getElementsByClassName('middle')[0]
    middleDiv.style.setProperty('background-color', 'transparent')
    const contentDiv = document.getElementById('content')
    contentDiv.style.setProperty('background-color', 'transparent')
  },
  methods:{
    localSocket() {
      if ('WebSocket' in window) {
        let wsUrl =
          window.location.protocol +
          '//' +
          window.location.host +
          '/stdc-visual/ws/event'
        wsUrl = wsUrl.replace('https', 'ws').replace('http', 'ws')
        // let wsUrl = `ws://10.255.111.102:18061/stdc-visual/ws/event`;
        console.log('wsUrl', wsUrl)
        let socket = new WebSocket(wsUrl)
        socket.onopen = function () {
          console.log('websocket连接成功')
        }
        socket.onmessage = function (msg) {
          let eventId = JSON.parse(msg.data).eventId
          let openInterface = {
            isWebsocket: true,
            eventId: eventId,
          }
          EventBus.$emit('openInterfaceWebsocket',openInterface)
          console.log(eventId, 'WebSocket33333')
        }
        socket.onclose = function () {
          console.log('websocket连接已关闭')
        }
        socket.onerror = function () {
          console.log('websocket连接发生错误')
        }
      } else {
        // 浏览器不支持 WebSocket
        console.log('您的浏览器不支持 WebSocket!')
      }
    },
  }
}
</script>
<style lang="scss">
@import "../styles/style.scss";
</style>