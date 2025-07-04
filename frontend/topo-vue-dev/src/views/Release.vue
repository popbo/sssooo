<template>
  <div class="Release">
    <div ref="meta2dDom" class="canvas" id="meta2d"></div>
    <LDialog />
    <IframeDialog />
  </div>
  <PassWord v-model:dialogVisible="dialogVisible" :password="password"></PassWord>
</template>

<script lang="ts">
import { TreeDataItem } from 'ant-design-vue/es/tree/Tree';
import { message } from 'ant-design-vue';
import {
  defineComponent,
  onMounted,
  onUnmounted,
  reactive,
  ref,
  watch,
  watchEffect,
  computed
} from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  LeftOutlined,
  BorderOutlined,
  FullscreenExitOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
} from '@ant-design/icons-vue';
import { LockState, Meta2d,Options } from '@topometa2d/core';
import { useStore } from 'vuex';
import { init, setToken, handleVarBind } from '@/services/url';
import { registerNormalShape } from '../components/register';
import LDialog from '@/components/common/LDialog/LDialog.vue';
import IframeDialog from '@/components/common/IframeDialog/IframeDialog.vue';
import axios from '@/http';
import Bus from '@/components/common/bus';
import { connectHttpfix, connectRealTimeDataBasefix, closeRealTimeDataBasesFix, closeTDenginesFix,
  connectTDenginesfix, } from '@/utils/soucerequest';
declare const meta2d: Meta2d;
declare const window: any;
import PassWord from './PassWord.vue';

export default defineComponent({
  name: 'Release',
  components: {
    LeftOutlined,
    BorderOutlined,
    FullscreenExitOutlined,
    MenuFoldOutlined,
    MenuUnfoldOutlined,
    LDialog,
    IframeDialog,
    PassWord
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

    const dialogVisible = ref<boolean>()//是否开启密码输入弹窗
    const password = ref<string>(null)

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

    function shouqi() {
      operation.value = !operation.value;
      left.value = !left.value;

      setTimeout(() => {
        meta2d.resize();
        onFill(true);
        meta2d.render();
      }, 1);
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
      console.log('进入页面缩放了')
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
      if (route.path.includes('release')) {
        //  设置 token
        if (route.query.token) {
          const newQuery = setToken(route.query, store);
          router.replace({
            path: '/release',
            query: newQuery,
          });
          return;
        }

        const data = await init(route.query, true);
        console.log("=-发布状态=->", data.shared,data);
        if (typeof data.shared == "undefined" || data.shared == false) {//未发布或已取消发布
          //router.push("/nopublish");
          message.info("大屏还未发布，请先发布后再访问!");
          return;
        }
        // if (route.query.r && isfirst) {
        //   isfirst = false;
        //    getFolder();
        //    getSelectedKeys(data);
        // }
        //不可移动
        const options = meta2d.getOptions();
        if(data.disableMove){
          options.disableTranslate=true
        } 
        //不可缩放
        if(data.disableScale){
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
        closeRealTimeDataBasesFix()//初始化关闭各个请求
        if (data && (data as any).RealTimeDataBase && (data as any).RealTimeDataBase.length > 0) connectRealTimeDataBasefix((data as any).RealTimeDataBase)
        if (data && data.https && data.https.length > 0) connectHttpfix(data.https)//启动http请求
        closeTDenginesFix()
        if (data && (data as any).TDengine && (data as any).TDengine.length > 0) connectTDenginesfix((data as any).RealTimeDataBase)
        // 绑定信息初始化到Vuex中bindDate数据
        if (data && data.pens && data.pens.length > 0) {
          data.pens.forEach(item => {
            if (!item.form) return  //如果不存在绑定信息就退出当前循环
            store.commit('souceDate/setBindDate', {
              id: item.id, //图元id
              data: item.form, //设备id,属性，tag等等信息
            })
          })
         // 触发Date中的轮询拿值
          poll(); //通知轮询
        }

        // 重写数据动态变化
        const bindDate = computed(() => {
          return store.getters['souceDate/getBindDate'];
        });
         // 给绑定的数据集合赋值 先遍历图元，在遍历每个图元内的各个属性挨个赋值
        function getsocueDate() {
          // 赋值操作
          bindDate.value.forEach((pen) => {
            pen.data?.forEach((formItem) => {
              if ('dataIds' in formItem) {
                // 对应绑定的数据池中取数据，根据协议id和协议类型定位那个数据池
                let value: any;
                const { http, dataId, communicationProtocolId, communicationProtocolName } = formItem.dataIds;
                // TODO，在souceDate.ts文件中，使用getDataPool
                value = store.getters['souceDate/getDataPool'](communicationProtocolId, dataId, communicationProtocolName);

                // 赋值 当存在formItem.dataIds时才赋值
                if (value !== null) {
                  //如果value不存在不赋值
                  meta2d.setValue({
                    id: pen.id, //图元id
                    [formItem.key]: value.value,
                  });
                }
              } else {
                // 增添了属性但是没有绑定设备id也即没有tag和设备相对应
                return;
              }
            });
          });
          // 实时数据库的赋值操作
        }

        // 绑定后就去 如果vuex的http有值且bindDate也有值就去一直轮询，没有值的话轮询查找http是有值60秒就停止
        function poll() {
          // 节流多次绑定重置60秒轮询----->是否需要对每个http返回的数据集都去轮询
          let timers = 60;
          let timer = null; //重复绑定清楚定时器
          timer = setInterval(() => {
            if (store.state.souceDate.http.length !== 0 || store.state.souceDate.RealTimeDataBase.length !== 0 || store.state.souceDate.TDengine.length !== 0) {
              timers = 60;
              getsocueDate(); //赋值操作
            } else {
              timers--;
              if (timers === 0) {
                clearInterval(timer); //清除定时器
                timer = null;
              }
            }
          }, 3000);
        }
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
        console.log('进入发布',data)
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
      treeData,
      // onLoadData,
      folderData,
      operation,
      left,
      isMobile,
      dialogVisible,
      password
    };
  },
  // 组件内的路由前置守卫
 async beforeRouteEnter(to, from, next) {
    const data = await init(to.query, true);
    // 这个回调参数e,包含setup中暴露出去的数据以及内置方法
    // 1、比对to.fullPath和 data.sharedurl如果不同跳转到百度2、相同再判断密码
    var matchReg = /.*('\/2d\/release\? ')/;
    let arr = data?.sharedurl.split('/2d/release?')
    if(!data?.sharedcustom|| !data?.shared){ 
      return next((e: any) => {
        if (!(data as any).sharedpassword) {
          e.dialogVisible = false
        } else {
          e.dialogVisible = true
          e.password = (data as any).sharedpassword
        }
      })
    }//如果没有开启发布shared和自定义sharedcustom不进入判断
    if (to.fullPath === ('/release?' + encodeURI(arr[1]))) {
       next( (e: any) => {
        if (!(data as any).sharedpassword) {
          e.dialogVisible = false
        } else {
          e.dialogVisible = true
          e.password = (data as any).sharedpassword
        }
      })
    } else {
      console.log(to.fullPath, '/release?' + arr[1]);
      // return  window.location.href='http://www.baidu.com'
      return message.info("链接地址不正确");
    }
  },
});
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';
:deep(.ant-tree-node-content-wrapper)  {
  width: 100%;
  width: 170px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.Release {
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
    z-index: 99999999;
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
