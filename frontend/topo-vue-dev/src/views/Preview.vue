<template>
  <div class="Preview">
    <div class="left" id="item" v-if="buttons && left">
      <div class="operation">
        <!--<a-button class="return" type="text" @click="onBack">
          <template #icon>
            <LeftOutlined />
          </template>
          返回
        </a-button>-->
        <!-- <div class="fit" :class="{ isMobile: isMobile }">
          <a-tooltip placement="top" v-if="buttons && isMobile">
            <template #title>
              <span>收起</span>
            </template>
            <a-button class="btn3" type="text" @click="shouqi">
              <template #icon>
                <MenuFoldOutlined :style="{ fontSize: '18px' }" />
              </template>
            </a-button>
          </a-tooltip>
          <a-tooltip placement="top">
            <template #title>
              <span>适合窗口大小</span>
            </template>
            <a-button class="btn1" type="text" @click="onFill(true)">
              <template #icon>
                <BorderOutlined />
              </template>
            </a-button>
          </a-tooltip>
          <a-tooltip placement="right">
            <template #title>
              <span>短边适应窗口大小</span>
            </template>
            <a-button class="btn2" type="text" @click="onFill(false)">
              <template #icon>
                <FullscreenExitOutlined />
              </template>
            </a-button>
          </a-tooltip>
        </div> -->
      </div>
      <div class="directory-tree">
        <a-directory-tree
          multiple
          v-model:expandedKeys="expandedKeys"
          v-model:selectedKeys="selectedKeys"
          @select="change"
          :tree-data="treeData"
        >
        </a-directory-tree>
      </div>
    </div>
    <!-- <div
      class="fit2"
      :class="{ isMobile: isMobile }"
      v-if="buttons && operation"
    >
      <a-tooltip placement="top" v-if="buttons && isMobile">
        <template #title>
          <span>收起</span>
        </template>
        <a-button class="btn3" type="text" @click="shouqi">
          <template #icon>
            <MenuFoldOutlined :style="{ fontSize: '18px' }" />
          </template>
        </a-button>
      </a-tooltip>
      <a-tooltip placement="right">
        <template #title>
          <span>适合窗口大小</span>
        </template>
        <a-button class="btn1" type="text" @click="onFill(true)">
          <template #icon>
            <BorderOutlined />
          </template>
        </a-button>
      </a-tooltip>
      <a-tooltip placement="right">
        <template #title>
          <span>短边适应窗口大小</span>
        </template>
        <a-button class="btn2" type="text" @click="onFill(false)">
          <template #icon>
            <FullscreenExitOutlined />
          </template>
        </a-button>
      </a-tooltip>
    </div> -->
    <a-tooltip placement="top" v-if="buttons && !isMobile">
      <template #title>
        <span>{{left?'收起':'展开'}}</span>
      </template>
      <a-button
        class="shouqi"
        :class="{ isMobile: isMobile }"
        type="text"
        @click="shouqi"
      >
        <template #icon>
          <MenuFoldOutlined :style="{ fontSize: '18px' }" />
        </template>
      </a-button>
    </a-tooltip>
    <div ref="meta2dDom" class="canvas" id="meta2d"></div>
    <LDialog />
    <IframeDialog />
  </div>
</template>

<script lang="ts">
import { TreeDataItem } from 'ant-design-vue/es/tree/Tree';
import {
  defineComponent,
  onMounted,
  onUnmounted,
  reactive,
  ref,
  watch,
  watchEffect
} from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  LeftOutlined,
  BorderOutlined,
  FullscreenExitOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
} from '@ant-design/icons-vue';
import { LockState, Meta2d,Options,Pen} from '@topometa2d/core';
import { useStore } from 'vuex';
import { init, setToken, handleVarBind } from '@/services/url';
import { registerNormalShape } from '../components/register';
import LDialog from '@/components/common/LDialog/LDialog.vue';
import IframeDialog from '@/components/common/IframeDialog/IframeDialog.vue';
import axios from '@/http';
import  {pollDate} from './ManualUpdate'
import {showWindowHref} from '@/components/utils'
declare const meta2d: Meta2d;
declare const window: any;

export default defineComponent({
  name: 'Preview',
  components: {
    LeftOutlined,
    BorderOutlined,
    FullscreenExitOutlined,
    MenuFoldOutlined,
    MenuUnfoldOutlined,
    LDialog,
    IframeDialog,
  },
  setup() {
    const route = useRoute();
    const router = useRouter();
    const store = useStore();
    let left = ref(false);
    let operation = ref(true);
    const buttons = ref(false);
    let folderData = reactive([]);
    let treeData = reactive<TreeDataItem[]>([]);

    let expandedKeys = ref<string[]>([]);
    let selectedKeys = ref<string[]>([]);

    let isMobile = /Android|webOS|iPhone|iPod|BlackBerry/i.test(
      navigator.userAgent
    );

    // const onLoadData = (treeNode: any) => {
    //   return new Promise((resolve: (value?: unknown) => void) => {
    //     if (treeNode.dataRef.children) {
    //       resolve();
    //       return;
    //     }

    //     getName(treeNode.dataRef.title).then(function (data) {
    //       treeNode.dataRef.children = data;
    //     });
    //     treeData = [...treeData];
    //     resolve();
    //   });
    // };

    function change(keys,e:any) {
      if (e.node&&e.node.isLeaf) {
        router.replace({
          path: '/preview',
          query: {
            r: Date.now() + '',
            id: keys[0],
            version: route.query.version,
          },
        });
      }
    }

    function shouqi() {
      operation.value = !operation.value;
      left.value = !left.value;
      /*取消自动自适应屏幕*/
      // setTimeout(() => {
      //   meta2d.resize();
      //   onFill(true);
      //   meta2d.render();
      // }, 1);
    }

    async function getFolder(str) {
      let folder: any = await axios.post(`/api/data/preview/folders/list?type=topo2d&username=${localStorage.getItem('username')}&categoryValue=${str}`)
      if (folder&&folder.error) {
        folder = {};
      }
      // 提出 未分类并将未分类放置 最后
      let withoutWeiFengLei = folder.list.filter(item => item.name !== '未分类'); // 创建一个新的数组，其中不包含undefined  
      let WeiFengLei= folder.list.find(item=> item.name === '未分类')
      withoutWeiFengLei.sort((a,b)=> Number(b._id)- Number(a._id)).push(WeiFengLei)
      multipleRequest(withoutWeiFengLei)
      return folder;
      
      // }
      // let folderList = [];
      // folder.list.forEach((_item) => {
      //   folderList.push(_item.name);
      //   let itemData = {
      //     title: _item.name,
      //     key: _item.name,
      //     children: _item.list.map((child) => { return { title: child.name, key: child._id ,isLeaf:true} })
      //   }
      //   folderData.push(itemData);
      //   treeData.push(itemData);
      // })
      // // let folder: any = await axios.get('/api/user/folder');
      // // for (let i = 0; i < folder.meta2d.length; i++) {
      // //   let item = {
      // //     title: folder.meta2d[i],
      // //     key: folder.meta2d[i],
      // //   };
      // //   folderData.push(item);
      // //   treeData.push(item);
      // // }

      // // return folder;

      // let [topo2dList] = await Promise.all([getOldtopo2d()]);
      // let UncategorizedList = topo2dList.filter((item) => { return !folderList.includes(item.folder) });
      // treeData.push(
      //   {
      //     title: '未分类',
      //     key:'未分类_1',
      //     children: UncategorizedList.map((child) => { return { title: child.name, key: child.id ,isLeaf:true} })
      //   }
      // )
    }
    function multipleRequest(arr){
     return arr.map(item=>{
        let itemData = {
          title: item.name,
          key: item.name,
          children: item.list.map(_item=>{
            return { title: _item.name, key: _item.id, isLeaf: true }
          })
        }
        folderData.push(itemData);
        treeData.push(itemData);
        return itemData
      })
    }
    
    async function getOldtopo2d() {
      let res: any = await axios.post(
        '/data/topo2d/list?username=' + localStorage.getItem('username'),
        {
          projection: {
            image: 1,
            _id: 1,
            name: 1,
            folder:1
          },
        },
        {
          params: {
            current: 1,
            pageSize: 100,
          },
        }
      );
      return res.list.map((item) => {
        return {
          image: item.image,
          id: item._id,
          name: item.name,
          folder:item.folder
        };
      });
    }
    // async function getName(folderName) {
    //   let folderInfor = [];
    //   const folder = folderName;

    //   let res: any = await axios.get('/api/user/topologies', {
    //     params: {
    //       folder,
    //       component: false,
    //       pageIndex: 1,
    //       pageCount: 100,
    //     },
    //   });
    //   for (let i = 0; i < res.list.length; i++) {
    //     let infor = { title: '', key: '', isLeaf: true };
    //     infor.title = res.list[i].name;
    //     infor.key = res.list[i].id;
    //     folderInfor.push(infor);
    //   }
    //   return folderInfor;
    // }

    watch(
      () => route.query.r,
      (val) => {
        buttons.value = !!val;
      },
      { immediate: true }
    );

    watch(
      () => route.query.e,
      (val) => {
        if (val) {
          shouqi();
        }
      },
      { immediate: true }
    );

    function onBack() {
      router.back();
    }

    function onFill(fit: boolean = true) {
      meta2d.fitView(fit);
    }

    const meta2dDom = ref('');

    const fn = () => {
      onFill(true);
    };
    const meta2dOptions: Options = {
      // scroll: true,
      // disableEmptyLine: true   // 禁用空连线
      // disableRepeatLine: true,    // 禁用重复连线，即入点出点相同
      // disableDockLine: true,   // 禁用停靠对齐线
      // disableTranslate: true  // 禁用移动
      color:'#a5a8b2',
      hoverColor:undefined,//默认没有hover效果
      activeColor:undefined,//默认没有选中效果
      hoverCursor:'default',//context-menu
    };
    onMounted(async () => {
      
      new Meta2d(meta2dDom.value);
      new Meta2d('meta2d', meta2dOptions);
      // getFolder();
      // const data = await init(route.query, true);
      // getSelectedKeys(data);
      registerNormalShape();
      import('../components/register').then(({ registerOtherShape }) => {
        registerOtherShape(true);
      });
      meta2d.on('opened', fn);
      // meta2d.fitView(true);
      // let { width, height } = meta2d.getRect();
      // let { offsetWidth, offsetHeight } = document.documentElement;
      // if (width < offsetWidth && height < offsetHeight) {
      //   meta2d.scale(1);
      //   meta2d.centerView();
      // } else {
      //   meta2d.fitView(true);
      // }
      
      //TODO 新接口js图形库
      // if (
      //   (window as any).meta2dTools.length !== (window as any).jsDiagramNum
      // ) {
      //   let jsLoadInterval = setInterval(() => {
      //     if (
      //       (window as any).meta2dTools.length == (window as any).jsDiagramNum
      //     ) {
      //       (window as any).registerToolsNew();
      //       clearInterval(jsLoadInterval);
      //     }
      //   }, 500);
      // } else {
      //   (window as any).registerToolsNew();
      // }

      //   let {width,height} = meta2d.getRect();
      //   let { offsetWidth, offsetHeight } = document.documentElement;
      //   console.log("meta2d", meta2d.store.data);
      //   if (width < offsetWidth && height < offsetHeight) {
      //     meta2d.scale(1);
      //     meta2d.centerView();
      //   } else {
      //     meta2d.fitView(true);
      //  }
    
    });

    let isfirst = true;

    watchEffect(async () => {
      if (route.path.includes('preview')) {
        const configObj = window ? window.MyGlobalConfigObject : null;
        const visual_url = configObj ? configObj.visual_url : null;
        console.log("visual_url: " + visual_url);
        let reqUrl = null;
        const linkSearch=showWindowHref()
        let linkParams={
          headtitle:'未命名图纸'
        }
        if(route.query?.id){
          linkParams = await axios.post(`/data/topo2d/topo/editor?id=${linkSearch['id']}`);
        }
        if (linkParams['username']) {
          //从可视化进入
          //reqUrl = '/account/cas/login?account=' + route.query.username;
          reqUrl = '/account/get/token?account=' + linkParams['username'];
        }
        const queryUserName = linkParams['username'];
        const localSavedToken = localStorage.getItem('token');
        const localSavedUser = localStorage.getItem('username');
        const noTokenCondition = !localSavedToken && !localSavedUser && reqUrl;
        const hasTokenUserDiffCondition = localSavedToken && localSavedUser && reqUrl && (localSavedUser != queryUserName);
        if (noTokenCondition || hasTokenUserDiffCondition) {
          const ret: any = await axios.post(reqUrl);
          if (ret && ret.token && ret.username) {
            console.log('getToken成功--->', ret.username);
            localStorage.setItem('token', ret.token);
            localStorage.setItem('username', ret.username);
            location. reload();
          } else {
            console.log('getToken失败');
            localStorage.removeItem('token');
            localStorage.removeItem('username');
            window.location.href = visual_url;
          }
          return;
        } else {
          console.warn("Preview.vue localStorage中username,token: ", localStorage.getItem('username'), localStorage.getItem('token'));
        }
        //  设置 token
        if (route.query.token) {
          const newQuery = setToken(route.query, store);
          router.replace({
            path: '/preview',
            query: newQuery,
          });
          return;
        }

        const data = await init(route.query, true);
        if (route.query.r && isfirst) {
          isfirst = false;
          getFolder((data as any).categoryValue);
          getSelectedKeys(data);
        }
        
        //不可移动
        const options = meta2d.getOptions();
        if((data as any).disableMove){
          options.disableTranslate=true
        } 
        //不可缩放
        if((data as any).disableScale){
          options.disableScale=true
        }
        data.locked = LockState.DisableEdit;
        await handleVarBind(route.query, data);
        data.rule=false
        //自定义hover鼠标形状
        data.pens.forEach(x=>{
          if(x.events&&x.events.length>0){
            const isCon=x.events.filter(item=>{
              return item.name==='click'||item.name==='dblclick'||item.name==='enter'||item.name==='leave'||item.name==='active'||item.name==='inactive'||item.name==='mousedown'||item.name==='mouseup'
            })
            if(isCon.length>0){
              x['hoverCursor']='pointer'
            }
          }else{
            x['hoverCursor']='default'
          }
        })
        meta2d.open(data);
         //⇡⇡⇡刷新打开图纸 解决当刷新后不同配置图元的数据完全相同变化问题
        meta2d.closeHttp();// 关闭刷新自带的http连接，然后打开自己定义的http连接方式
        pollDate(data, meta2d,store)//启用自己写的数据更新函数
        const shortFill=data?.shortFill
        const fill=data?.fill
        if(fill){
          setTimeout(() => {
            meta2d.resize();
            onFill(true);
            meta2d.render();
          }, 1);
        }
        if(shortFill){
          setTimeout(() => {
            meta2d.resize();
            onFill(false)
            meta2d.render();
          }, 1);
        }
        let { width, height } = meta2d.getRect();
        let { offsetWidth, offsetHeight } = document.documentElement;
        if (width < offsetWidth && height < offsetHeight) {
          meta2d.scale(1);
          meta2d.centerView();
        }
        
        if (
          (!isMobile && (<any>meta2d.store.data).autoSizeinPc) ||
          (isMobile && (<any>meta2d.store.data).autoSizeinMobile)
        ) {
          meta2d.fitView(true);
        }
        setTimeout(() => {
          meta2d.render();
        }, 2000);
      }
    });
    function getSelectedKeys(data) {
      console.log('data数据',data)
      // getName(data.folder).then(function (res) {
      //   for (let index = 0; index < treeData.length; index++) {
      //     if (data.folder == treeData[index].title) {
      //       treeData[index].children = res;
      //       treeData = [...treeData];
      //     }
      //   }
      // });
      selectedKeys.value = [data._id];
      expandedKeys.value = [data.folder];
    }
    onUnmounted(() => {
      meta2d.off('opened', fn);
      meta2d?.destroy();
    });

    return {
      onBack,
      onFill,
      buttons,
      meta2dDom,
      expandedKeys,
      selectedKeys,
      shouqi,
      // getName,
      change,
      treeData,
      // onLoadData,
      folderData,
      operation,
      left,
      isMobile,
    };
  },
});
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';
:deep(.ant-tree-node-content-wrapper){
  width: 100%;
  width: 170px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.Preview {
  display: flex;
  height: 100vh;
  position: relative;
  //background: #232630;
  .left {
    border-right: 1.5px solid #0d0e10;
    font-size: 20px;
    height: 100%;
    width: 250px;
    background-color: #181a24;
    z-index: 99;
  }
  .operation {
    width: 100%;
    // display: flex;
    // justify-content: space-between;
    .ant-btn-text{
      color: #a5a8b2;
    }
    .return {
      height: 50px;
      float: left;
    }
    .fit {
      &.isMobile {
        .btn1 {
          margin-top: 10px;
          margin-left: 0px;
        }
      }
      width: 100px;
      float: right;
      height: 50px;

      .btn1 {
        margin-top: 0px;
        margin-left: 30px;
      }
      .btn2 {
        margin-top: 10px;
        margin-left: 0px;
      }
      .btn3 {
        margin-top: 0px;
        margin-left: 0px;
      }
    }
  }

  .directory-tree {
    border-top: solid 1px #0d0e10;
    overflow-y: auto;
    padding: 20px 20px;
    width: 100%;
    height: 93%;
    .ant-tree-directory{
      color: #b4b7c1;
      :deep(.ant-tree-node-content-wrapper){
        color: #b4b7c1;
        &:hover{
          color: #3a89fe;
        }
      }
      :deep(.ant-tree-node-content-wrapper){
        &:hover::before{
          background: none;
        }
      }
      :deep(.ant-tree-child-tree){
        .ant-tree-node-content-wrapper{
          &:hover::before{
            background: none;
          }
        }
      }
      :deep(.ant-tree-treenode-selected){
        .ant-tree-node-content-wrapper::before{
          background: none;
        }
      }
      :deep(.ant-tree-child-tree){
        .ant-tree-treenode-selected{
          .ant-tree-node-content-wrapper::before{
            background: none;
          }
        }
      }
    }
  }
  .shouqi {
    background-color: #fff;
    &.isMobile {
      top: 10px;
      left: 10px;
    }
    position: absolute;
    z-index: 999;
    // width: 50px;
    bottom: 23px;
    left: 20px;
  }

  .fit2 {
    &.isMobile {
      width: 96px;
      top: 10px;
      left: 10px;
    }
    position: absolute;
    z-index: 999;
    width: 50px;
    bottom: 55px;
    left: 20px;
    .btn1 {
      background-color: #ffffff;
    }
    .btn2 {
      background-color: #ffffff;
    }
    .btn3 {
      background-color: #ffffff;
    }
  }

  .canvas {
    flex-grow: 1;
    height: 100%;
  }
}
</style>
