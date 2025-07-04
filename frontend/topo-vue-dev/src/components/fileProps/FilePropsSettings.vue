<template>
  <a-collapse
    v-model:activeKey="activeKey"
    expand-icon-position="left"
    class="PenProps"
  >
    <a-collapse-panel key="文件" :header="t('文件')">
      <FormEvery
        :configs="fileConfigs"
        :obj="meta2dInfo"
        @change-value="changeValue"
      />
    </a-collapse-panel>
    <!-- 图纸有，组件无，TODO: component 业务属性 -->
    <a-collapse-panel
      key="画布"
      :header="t('画布')"
      v-if="!meta2dInfo.component"
    >
      <FormEvery
        :configs="meta2dConfigs"
        :obj="meta2dInfo"
        @change-value="changeValue"
      />
    </a-collapse-panel>
    <a-collapse-panel key="全局动画" :header="t('全局动画')" style="padding-bottom:45px;">
      <a-form-item>
        <a-radio-group v-model:value="animationState" @change="changeAnimateState">
          <a-radio value="default">默认</a-radio>
          <a-radio value="play">播放</a-radio>
          <a-radio value="stop">停止</a-radio>
        </a-radio-group>
      </a-form-item>
    </a-collapse-panel>
  </a-collapse>
      <!-- 拷贝功能 -->
      <!-- 模态框  -->
      <div class="copyModelBox">
        <CopyModel ></CopyModel>
      </div>
</template>

<script lang="ts">
import {
  computed,
  defineComponent,
  onMounted,
  onUnmounted,
  reactive,
  ref,
  watchEffect,
  watch,
  nextTick
} from 'vue';
import FormEvery from '../common/FormEvery.vue';
import { LockState, Meta2d, Meta2dData } from '@topometa2d/core';
import { t } from '@/i18n/i18n';
import { FormItemType, Meta2dBackData } from '../utils';
import { useRoute } from 'vue-router';
import CopyModel from './CopyModel.vue'
import { Decrypt } from '@/utils/encryption'
import axios from '@/http';
import store from '@/store';
declare const meta2d: Meta2d;
declare const window: any;
export default defineComponent({
  name: 'FilePropsSettings',
  components: {
    FormEvery,
    CopyModel
  },
  setup() {
    const activeKey = ref(['文件', '画布','全局动画']);
    const meta2dInfo = ref<Meta2dBackData | any>({});
    const route = useRoute();
    const animationState=ref('default')
    onMounted(() => {
      // 不使用同一个对象，避免直接对核心库数据发生改变
      meta2dInfo.value = Object.assign({}, meta2d.store.data);
      meta2d.on('opened', openData);
      meta2d.on('t-classes', getClasses);
      meta2d.on('t-userFolder', getUserFolder);
      meta2d.on('t-meta2dFolder', getMeta2dFolder);
      meta2d.on('changeFolder', changeFolder)
    });
    onUnmounted(() => {
      meta2d.off('opened', openData);
      meta2d.off('t-classes', getClasses);
      meta2d.off('t-userFolder', getUserFolder);
      meta2d.off('t-meta2dFolder', getMeta2dFolder);
      meta2d.off('changeFolder', changeFolder)
    });

  async function openData() {
      // true是编辑页面过来，fasle是新建页面过来
      let flag= route.query.visualizationEdit==='false'?true:false;
      if(flag){
         //visualizationEdit=fasle 新建页面过来，
        window.FileObj = {
          folder:(route.query.fileFolder || route.query.folder || '未分类'), 
          name: route.query.fileName||'未命名图纸',
          folderType: route.query.folderType|| 'topo2dnocategorykey9999999999999',//分类唯一标识
          // class: route.query.folderKey ?? '未分类123',//分类名字class-->
          categoryValue: route.query.folderType ?? 'topo2dnocategorykey9999999999999',
          // categoryKey: route.query.folderKey ?? '未分类',
          password: Decrypt(route.query.password) ?? ''
        }
      }else{
        //visualizationEdit=true 编辑页面过来，如果meta2d.store.data得文件名name没有，文件夹名folder没有，分类没有得需要给一个默认值
          window.FileObj = {
          folder: meta2d.store.data['folder'] || '未分类',
          name: meta2d.store.data['name']|| '未命名图纸',
          folderType: meta2d.store.data['categoryValue']|| 'topo2dnocategorykey9999999999999',//分类唯一标识
          class: meta2d.store.data['categoryKey']||'未分类',//分类名字class-->
          categoryValue:  meta2d.store.data['categoryValue'] || 'topo2dnocategorykey9999999999999',//分类唯一标识
          categoryKey: meta2d.store.data['categoryKey'] || '未分类',//分类名字class-->
          password: meta2d.store.data['password'] ?? ''
        }
      }
      await getFolderType(window.FileObj.categoryValue)

       // 先同步，把window.FileObj的对象加入到meta2d中
        for (let key in window.FileObj) {
          if ((window.FileObj[key] !== '') && window.FileObj.hasOwnProperty(key)) {
            meta2d.store.data[key] = window.FileObj[key]
          }
        }
        meta2dInfo.value = Object.assign({}, meta2d.store.data);
    }

    const fileConfigs = computed(() => {
      const configs: FormItemType[] = [
        // 业务字段
        {
          key: 'name',
          type: 'text',
          name: `${t('文件名')}`,
          placeholder: `${t('文件名')}`,
        },
        {
          key: 'class',
          type: 'select',
          name: `${t('分类')}`,
          options: classesOption.value.map((i) => {
            return {
              label: t(i.name),
              value: i.name,
            };
          }),
        },
        {
          key: 'folder',
          type: 'select',
          name: `${t('文件夹')}`,
          options: meta2dInfo.value.component
            ? userFolder.value
            : meta2dFolder.value,
        },
      ];

      if (meta2dInfo.value.component) {
        // 组件，添加一个值，控制 showChild
        configs.push({
          key: 'showChild',
          type: 'number',
          name: `${t('状态')}`,
          placeholder: `${t('默认显示第几个状态')}`,
          min: 0,
          step: 1,
        });
      }

      return configs;
    });

    const meta2dConfigs = computed(() => {
      const configs: FormItemType[] = [
        {
          key: 'color',
          type: 'color',
          name: `${t('默认')}${t('颜色')}`,
        },
        {
          key: 'penBackground',
          type: 'color',
          name: `${t('画笔填充颜色')}`,
        },
        {
          key: 'background',
          type: 'color',
          name: `${t('背景颜色')}`,
        },
        {
          key: 'bkImage',
          type: 'image',
          name: `${t('背景图片')}`,
        },
        {
          key: 'grid',
          type: 'switch',
          name: `${t('背景网格')}`,
        },
        {
          key: 'gridColor',
          type: 'color',
          name: `${t('网格颜色')}`,
        },
        {
          key: 'gridSize',
          type: 'number',
          name: `${t('网格大小')}`,
          min: 0,
        },
        {
          key: 'gridRotate',
          type: 'number',
          name: `${t('网格角度')}`,
        },
        {
          key: 'rule',
          type: 'switch',
          name: `${t('标尺')}`,
        },
        {
          key: 'ruleColor',
          type: 'color',
          name: `${t('标尺颜色')}`,
        },
        {
          key: 'initJs',
          type: 'code',
          name: `${t('初始化')}JS`,
        },
        // {
        //   key: 'autoSave',
        //   type: 'switch',
        //   name: `${t('自动保存')}`,
        // },
        {
          key: 'autoSizeinPc',
          type: 'switch',
          name: `${t('pc端自动缩放')}`,
        },
        {
          key: 'autoSizeinMobile',
          type: 'switch',
          name: `${t('移动端自动缩放')}`,
        },
        {
          key: 'showMap',
          type: 'switch',
          name: `${t('缩略图')}`,
        },
      ];

      return configs;
    });
    function changeValue({ value, keys }: { value: any; keys: string[] }) {
      console.log(value,'||', keys, '||');
      
      keys[0] === 'gridSize' && (value = Number(value));
      if (keys[0] === 'bkImage') {
        meta2d.setBackgroundImage(value);
      }
      if (keys[0] === 'background') {
        console.log('背景颜色',value)
        meta2d.setBackgroundColor(value);
      }
      if (['grid', 'gridColor', 'gridSize', 'gridRotate'].includes(keys[0])) {
        console.log('进来改变了',value,keys[0])
        const grid: Record<string, any> = {};
        grid[keys[0]] = value;
        meta2d.setGrid(grid);
        if(typeof(meta2d.store.data.gridColor) == "undefined"||keys[0]==='gridColor'&&typeof(value)=='undefined'){
          grid.gridColor='#2b2e39ff'
          meta2d.setGrid({
            gridColor:'#2b2e39ff'
          });
        }
        if(keys[0]==='grid'&&meta2d.store.data.gridColor==='#e2e2e2'){
          grid.gridColor='#2b2e39ff'
          meta2d.setGrid({
            gridColor:'#2b2e39ff'
          });
        }
      }
      if (['rule', 'ruleColor'].includes(keys[0])) {
        const rule: Record<string, any> = {};
        rule[keys[0]] = value;
        meta2d.setRule(rule);
      }
      if(keys[0] === 'showMap'){
        if(value){
          meta2d.showMap();
        }else{
          meta2d.hideMap();
        }
      }
      // if(!(keys[0]==='gridColor'&&typeof(value)=='undefined')){
      //   meta2d.store.data[keys[0]] = value;
      // }
      
      if(keys[0]==='class'){
        //// 保存文件分类信息 categoryValue，categoryKey
        let categoryValue = classesOption.value.find(i => i.name === value).key
        meta2d.store.data['categoryValue']= categoryValue
        meta2d.store.data['categoryKey']=value
        store.commit('user/setProfile', categoryValue)//更新vuex中‘categoryValue’
        // 文件夹名字
        meta2d.store.data['folder'] = '未分类'
        meta2dInfo.value['folder'] = '未分类';
      }

      meta2d.store.data[keys[0]] = value;
      console.log('===>',keys[0],value)
      console.log('rule[keys[0]]',meta2d.store.data)
      meta2d.render();
    }
    function changeAnimateState(e){
      if(e.target.value==='play'){
        meta2d.startAnimate(meta2d.store.data.pens);
      }else if(e.target.value==='stop'){
        meta2d.stopAnimate();
      }else if(e.target.value==='default'){
        autoPlayPen()
      }
      meta2d.store.data['animationState']=e.target.value
      meta2d.render();
    }
    function autoPlayPen(){
      const defaultPen=meta2d.store.data.pens.filter(item=>item.autoPlay)
      meta2d.stopAnimate();
      meta2d.startAnimate(defaultPen);
    }
    const classesOption = ref([]);
    function getClasses(classes: any[]) {
      classesOption.value = classes;
    }

    const userFolder = ref([]);
    const getUserFolder = (folder) => {
      // userFolder.value = folder.map((i) => ({ label: t(i), value: i,disabled:i==='未分类' }));
      userFolder.value = folder.map((i) => ({ label: t(i), value: i }));
    };
    const meta2dFolder = ref([]);
    const getMeta2dFolder = (folder) => {
      //  meta2dFolder.value = folder.map((i) => ({ label: t(i), value: i,disabled:i==='未分类' }));
      meta2dFolder.value = folder.map((i) => ({ label: t(i), value: i }));
    };

    async function getFolderType(value) {
      const ret_FolderType: any = await axios.get(
        '/api/data/topo2d/categoryKey/list?username=' + localStorage.getItem('username')
      );
       if (ret_FolderType&&!ret_FolderType?.error) {
        classesOption.value = ret_FolderType.map(i => {
          return { name: i.categoryKey, key: i.categoryValue }
        })
       }
      //检查是否有未分类，没有加一项未分类
      let flag=true
      classesOption.value.find(item=>{
        if(item.key === "topo2dnocategorykey9999999999999"){
          return flag=false
        }
      })
      if(flag) classesOption.value.push({ name: '未分类', key: "topo2dnocategorykey9999999999999" })
      window.FileObj.categoryKey = classesOption.value.find(i => i.key === value)?.name
      window.FileObj.class = classesOption.value.find(i => i.key === value)?.name
    }
    
    const changeFolder=({value,keys})=>{
      meta2dInfo.value[keys[0]]=value
    }

    return {
      meta2dInfo,
      activeKey,
      fileConfigs,
      meta2dConfigs,
      animationState,
      changeAnimateState,
      changeValue,
      autoPlayPen,
      changeFolder,
    };
  },
});
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';
</style>
<style lang="scss" scoped>
.copyModelBox{
  padding: 15px;
  background: #232630;
  position: fixed;
  bottom: 0;
  z-index: 10;
  height: 62px;
  width: 340px;
  text-align: center;
  border-top: 1px solid #393b4a;
}
.PenProps{
  :deep(.ant-radio-wrapper){
    color: #7f838c;
  }
}
</style>