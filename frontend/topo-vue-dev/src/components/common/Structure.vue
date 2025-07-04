<template>
  <div class="content" id="treeList">
    <Tree :items="items" :active-pens="activeArray" @onJgSelect="onJgSelect"/>
  </div>
</template>

<script lang="ts">
import {
  reactive,
  defineComponent,
  onMounted,
  onUnmounted,
  ref,
  Ref,
  inject,
  watchEffect,
  watch,
  nextTick
} from 'vue';
import Tree from './Tree.vue';
import { Pen, Meta2d } from '@topometa2d/core';
import { format, Meta2dBackData, TreeItem } from '../utils';
declare const meta2d: Meta2d;

export default defineComponent({
  name: 'Structure',
  components: { Tree },
  props: {
    isHidden: {
      type: Boolean
    }
  },
  setup(props) {
    const activePens: Ref<Pen[]> = inject('activePens');
    const activeArray = ref<string[]>([]);
    const items = ref<TreeItem[]>([]);
    let isJg=false
    let activePenObj=null
    console.log('activePens',activeArray.value)
    watchEffect(() => {
      activeArray.value = activePens.value.map((pen) => {
        autoScroll(pen)
        activePenObj=pen
        return pen.id;
      });
      
    });

    watch(()=>props.isHidden,()=>{
      if(props.isHidden&&activePens.value.length!==0){
        nextTick(()=>{
          autoScroll(activePenObj)
        })
      }
    })
    function autoScroll(pen){
      if(!isJg){
          if(items.value.length>24){
            const index = items.value.map(item => item.id).indexOf(pen.id)
            let target=document.getElementById('treeList')
            // target.scrollTop=45
            if(index>=18){
              target.scrollTop=(index-12)*35
            }else{
              target.scrollTop=0
            }
          }
        }
        isJg=false
    }
    function onJgSelect(val){
      if(val==='jg'){
        isJg=true
      }
    }
    onMounted(() => {
      if (meta2d) {
        getTree();
        meta2d.on('inactive', getTree);
        meta2d.on('opened', getTree);
        meta2d.on('add', getTree);
        meta2d.on('undo', getTree);
        meta2d.on('redo', getTree);
        meta2d.on('delete', getTree);
      }
    });

    function getTree() {
      const outerPens = meta2d.store.data.pens.filter((pen) => !pen.parentId);
      items.value = format(outerPens);
      items.value = items.value.reverse();
    }

    onUnmounted(() => {
      if (meta2d) {
        meta2d.off('inactive', getTree);
        meta2d.off('opened', getTree);
        meta2d.off('add', getTree);
        meta2d.off('undo', getTree);
        meta2d.off('redo', getTree);
        meta2d.off('delete', getTree);
      }
    });
    return {
      items,
      activeArray,
      onJgSelect,
      autoScroll
    };
  },
});
</script>
<style lang="scss" scoped>
.content {
}
</style>
