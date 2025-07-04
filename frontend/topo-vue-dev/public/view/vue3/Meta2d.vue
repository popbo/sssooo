<template>
  <div class="content">
    <div id="meta2d"></div>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, onUnmounted } from 'vue';
let meta2d: any = null;

onMounted(async () => {
  meta2d = new (window as any).Meta2d('meta2d');
  (window as any).registerCommonDiagram();

  fetch('/json/data.json', function (text) {
    var data = JSON.parse(text);
    data.locked = 1;
    meta2d.open(data);
  });
});

function fetch(url, cb) {
  var xhr = new XMLHttpRequest();
  xhr.open('GET', url, true);
  xhr.send();
  xhr.onreadystatechange = function () {
    if (xhr.readyState == 4 && xhr.status == 200) {
      cb && cb(xhr.responseText);
    }
  };
}

onUnmounted(() => {
  meta2d?.destroy();
});
</script>

<style lang="scss" scoped>
html,
body {
  height: 100vh;
  margin: 0;
  padding: 0;
}

.content {
  position: relative;
  background-color: #f4f4f4;
  height: 100vh;
  #meta2d {
    position: absolute !important;
    width: 100%;
    height: 100%;
    touch-action: none;
    overflow: hidden;
  }
}
</style>
