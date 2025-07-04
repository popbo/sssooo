<template>
  <div class="Meta2dMenu flex">
    <!-- 
    <a
      :target="companyName == 'web组态' ? '_blank' : '_self'"
      class="logo"
      :href="companyName == 'web组态' ? 'http://2ds.unittec.com/' : '#'"
    >
      <img src="/img/logo.png" :alt="companyName" />
      {{ t(`${companyName}`) }}
    </a>
	-->
    <img src="/img/newlogo.png" :alt="companyName" />
    <div class="menus flex">
      <div class="flex">
        <!-- <template v-for="leftItem in menus.left" :key="leftItem.name">
          <MenuItem
            v-if="leftItem.key !== 'save'" v-show="leftItem.key != 'file'"
            class="leftItem mr30"
            :menuItem="leftItem"
            @action="action"
          />
          <MenuItem
            v-else
            :needOperate="isNew"
            class="leftItem mr30"
            :menuItem="leftItem"
            @action="action"
          />
        </template> -->
      </div>
      <div class="flex">
        <template v-for="middleItem in menus.middle" :key="middleItem.name">
          <MenuItem
            v-if="middleItem.key === 'edit'"
            class="middleItem mr30"
            :menuItem="middleItem"
            @action="action"
          />
          <MenuItem
            v-if="middleItem.key === 'pen'"
            :active="pen"
            class="middleItem mr30"
            :menuItem="middleItem"
            @action="action"
          />
          <MenuItem
            v-else-if="middleItem.key === 'pencil'"
            :active="pencil"
            class="middleItem mr30"
            :menuItem="middleItem"
            @action="action"
          />
          <MenuItem
            v-else-if="middleItem.key === 'magnifier'"
            v-show="middleItem.key != 'magnifier'"
            :active="magnifier"
            class="middleItem mr30"
            :menuItem="middleItem"
            @action="action"
          />
          <MenuItem
            v-else-if="middleItem.key === 'map'"
            :active="map"
            class="middleItem mr30"
            :menuItem="middleItem"
            @action="action"
          />
        </template>
        <!-- 起点 -->
        <!--<a-dropdown class="MenuItem mr30" overlayClassName="dropMenu">
          <div @click.prevent>
            <div class="iconItem">
              <div class="icon">
                <i :class="fromArrow"></i>
              </div>
              <span class="font">{{ t('起点') }}</span>
            </div>
          </div>
          <template #overlay>
            <a-menu>
              <a-menu-item
                class="menuItem"
                v-for="item in fromArrows"
                :key="item.value"
                @click="onFromArrow(item)"
              >
                <i :class="item.icon" class="arrowIcon"></i>
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>-->
        <!-- 终点 -->
        <!--<a-dropdown class="MenuItem mr30" overlayClassName="dropMenu">
          <div @click.prevent>
            <div class="iconItem">
              <div class="icon">
                <i :class="toArrow"></i>
              </div>
              <span class="font">{{ t('终点') }}</span>
            </div>
          </div>
          <template #overlay>
            <a-menu>
              <a-menu-item
                class="menuItem"
                v-for="item in toArrows"
                :key="item.value"
                @click="onToArrow(item)"
              >
                <i :class="item.icon" class="arrowIcon"></i>
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>-->
        <!-- 连线 -->
        <a-dropdown class="MenuItem mr30" overlayClassName="dropMenu">
          <div @click.prevent>
            <div class="iconItem">
              <div class="icon">
                <i :class="line"></i>
              </div>
              <span class="font">{{ t('连线') }}</span>
            </div>
          </div>
          <template #overlay>
            <a-menu>
              <a-menu-item
                class="menuItem lineItem"
                v-for="item in defaultLines"
                :key="item.value"
                @click="onLine(item)"
              >
                <span>{{ t(item.name) }}</span>
                <i :class="item.icon" class="arrowIcon"></i>
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
        <!-- 线宽 -->
        <!--<a-dropdown
          class="MenuItem mr30"
          overlayClassName="dropMenu"
          v-model:visible="lineWidthVisible"
        >
          <div @click.prevent>
            <div class="iconItem">
              <div class="icon">
                <span class="number">{{ lineWidth }}</span>
                <i class="abs t-icon t-triangle-down rightIcon"></i>
              </div>
              <span class="font">{{ t('线宽') }}</span>
            </div>
          </div>
          <template #overlay>
            <div class="lineWidthView" @click.stop>
              <a-input-number
                v-model:value="lineWidth"
                :min="1"
                @change="onDefaultLineWidth"
              />
            </div>
          </template>
        </a-dropdown>-->
        <!--<a-dropdown class="MenuItem mr30" overlayClassName="dropMenu">
          <div @click.prevent>
            <div class="iconItem">
              <div class="icon">
                <span class="number">{{ scale }}</span>
                <i class="abs t-icon t-triangle-down rightIcon"></i>
              </div>
              <span class="font">{{ t('视图') }}</span>
            </div>
          </div>
          <template #overlay>
            <div class="scaleView">
              <i @click.stop="onScale(-10)" class="t-icon t-reduce mr16"></i>
              <i @click.stop="onScale(10)" class="t-icon t-add mr16"></i>
              <a-button class="mr8" @click.stop="action('fitView')">
                {{ t('窗口大小') }}
              </a-button>
              <a-button @click.stop="onScale(0)">{{ t('重置') }}</a-button>
            </div>
          </template>
        </a-dropdown>-->
        <!-- 自动锚点 -->
        <a-dropdown class="MenuItem mr16" overlayClassName="dropMenu">
          <div @click.prevent="onAutoAnchor">
            <div class="iconItem" 
            :style="{
              color: autoAnchor ? '' : '#56596a',
            }">
              <div
              class="icon" :class="autoAnchor && 'warning'">
                <i
                  class="t-icon"
                  :class="{
                    't-maodian': autoAnchor,
                    't-maodianjiyong': !autoAnchor,
                  }"
                ></i>
              </div>
              <span class="font" :class="autoAnchor && 'warning'">{{
                t('自动锚点')
              }}</span>
            </div>
          </div>
        </a-dropdown>
        <!-- 禁用锚点 -->
        <a-dropdown class="MenuItem" overlayClassName="dropMenu">
          <div @click.prevent="onDisableAnchor">
            <div class="iconItem">
              <div
                class="icon"
                :style="{
                  color: disableAnchor ? '#56596a' : '',
                }"
              >
                <i class="t-icon t-jinzhimiaodian" style="font-size: 15px"></i>
              </div>
              <span
                class="font"
                :style="{
                  color: disableAnchor ? '#56596a' : '',
                }"
                >{{ disableAnchor ? t('显示锚点') : t('禁用锚点') }}</span
              >
            </div>
          </div>
        </a-dropdown>
      </div>
      <!-- 右 -->
      <div class="flex ml16">
        <!-- 锁住 -->
        <a-dropdown class="MenuItem rightItem" overlayClassName="dropMenu">
          <div @click.prevent="onLock">
            <div class="iconItem">
              <div
                class="icon"
                :class="{
                  error: isLock === 2,
                  warning: isLock === 1,
                  primary: !isLock,
                }"
              >
                <i
                  class="t-icon"
                  :class="{
                    't-wufayidong': isLock === 2,
                    't-lock': isLock === 1,
                    't-unlock': !isLock,
                  }"
                ></i>
              </div>
              <span
                class="font"
                :class="{
                  error: isLock === 2,
                  warning: isLock === 1,
                  primary: !isLock,
                }"
              >
                {{
                  isLock === 2
                    ? t('锁定')
                    : isLock === 1
                    ? t('预览')
                    : t('编辑')
                }}
              </span>
            </div>
          </div>
        </a-dropdown>
        <template v-for="rightItem in menus.right" :key="rightItem.name">
          <MenuItem
            v-if="rightItem.action === 'share' && rightItem.isShow"
            :active="shared"
            class="rightItem ml8 help-menu"
            :menuItem="rightItem"
            @action="action"
          />
          <MenuItem
            v-else-if="rightItem.isShow"
            v-show="rightItem.name != '社区' && rightItem.name != '帮助'"
            class="rightItem ml8 help-menu"
            :menuItem="rightItem"
            @action="action"
          />
        </template>
        <!-- <a-dropdown class="MenuItem help-menu ml24" overlayClassName="dropMenu">
          <div @click.prevent>
            <div class="iconItem">
              <div class="icon">
                <i class="t-icon t-yuyan"></i>
              </div>
              <span class="font">{{ t(localeDesc) }}</span>
            </div>
          </div>
          <template #overlay>
            <a-menu>
              <a-menu-item
                class="menuItem"
                v-for="item in localeList"
                :key="item.id"
                @click="onLocale(item.id)"
              >
                <span>{{ item.text }}</span>
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown> -->
      </div>
    </div>
    <!-- <a-dropdown
      v-if="user"
      class="userArea MenuItem"
      overlayClassName="dropMenu"
    >
      <div class="hover flex middle" @click.prevent>
        <img class="mr8" src="/img/avatar.png" style="height: 24px" />
        {{ user.username }}
      </div>
      <template #overlay>
        <a-menu class="menus">
          <template v-for="menu in userMenus" :key="menu.name">
            <a :href="menu.url" v-if="menu.name" :target="menu.target">
              <a-menu-item class="menuItem">
                <span>{{ t(menu.name) }}</span>
              </a-menu-item>
            </a>
            <a-menu-divider v-else />
          </template>
          <a-menu-divider />
          <a-menu-item class="menuItem" @click="onLogout">
            <span>{{ t('退出') }}</span>
          </a-menu-item>
        </a-menu>
      </template>
    </a-dropdown> -->
    <!--<template v-else>
      <div class="loginArea">-->
    <!-- <a :href="getAccountUrl()"><button>{{ t('登录') }}</button></a> -->
    <!-- <a target="_blank" :href="getAccountUrl() + '&signup=true'">
          <button>{{ t('免费使用') }}</button>
        </a> -->
    <!--</div>
    </template>-->
    <a-modal
      wrapClassName="vip-modal"
      v-model:visible="vipVisible"
      :title="t('VIP 授权功能，请开通 VIP')"
      :okText="t('知道了')"
      :cancel-button-props="{ style: 'display: none' }"
      @ok="vipVisible = false"
    >
      <!-- <a-image class="vip-img" src="/img/vip.png" /> -->
      <!-- <img src="/img/vip.png" /> -->
    </a-modal>
    <a-modal
      v-model:visible="showModelSaveAsPop"
      :title="t('另存为')"
      :cancelText="t('取消')"
      :okText="t('确定')"
      :width="400"
      @ok="saveAs"
    >
      <div
        class="saveAsItem"
        v-for="item in saveAsConfigs"
        :key="item.key"
        style="
          display: flex;
          justify-content: space-between;
          padding: 5px;
          height: 42px;
          align-items: center;
        "
      >
        <label
          v-html="item.name"
          class="option"
          style="margin-left: 20px"
        ></label>
        <div style="width: 250px">
          <a-input
            v-model:value="meta2dInfo.name"
            @change="changeValue"
            v-if="item.type == 'text'"
          />
          <a-select
            v-else
            v-model:value="item.vModelValue"
            @change="changeValue"
            :style="item.key == 'folder' ? 'width:225px' : 'width:100%'"
            :options="item.options"
          >
          </a-select>
          <a-tooltip placement="top" v-if="item.key == 'folder'">
            <template #title>
              <span>新建文件夹</span>
            </template>
            <i
              class="t-icon t-box-add"
              style="margin: 4px 0 0 5px"
              @click="showModelNewFolder = true"
            ></i>
          </a-tooltip>
        </div>
      </div>
    </a-modal>
    <a-modal
      v-model:visible="showModelNewFolder"
      :title="t('新建文件夹')"
      :cancelText="t('取消')"
      :okText="t('确定')"
      @ok="okAdd"
    >
      <a-input v-model:value="newFolder" />
    </a-modal>

    <ShareModal v-model:visible="shareVisible" @update:isOnRelease="isRelease" @update:isOffRelease="isRelease"/>
  </div>
</template>

<script lang="ts">
import {
  computed,
  defineComponent,
  nextTick,
  onMounted,
  onUnmounted,
  reactive,
  ref,
} from 'vue';
import { useStore } from 'vuex';
import { message } from 'ant-design-vue';
import {
  LockState,
  Pen,
  PenType,
  Meta2d,
  Meta2dData,
  HotkeyType,
  isShowChild,
  getGlobalColor
} from '@topometa2d/core';
import axios from '@/http';
import MenuItem from './common/MenuItem.vue';
import localforage from 'localforage';
import { useRoute, useRouter } from 'vue-router';
import { MaterialsGroup } from '@/services/material';
import { delImage, upload, readFile, dataURLtoBlob } from '@/services/file';
import { baseVer, compareVersion, upgrade } from '@/services/upgrade';
import { parseSvg } from '@topometa2d/svg';
import {
  localMeta2dDataName,
  showModal,
  Meta2dBackData,
  FormItemType,
} from './utils';
import { t } from '@/i18n/i18n';
import FormEvery from './common/FormEvery.vue';
import ShareModal from './common/ShareModal.vue';
import { isLogin, isVip, noLoginTip, User } from '@/services/user';
// 此处仅仅作为类型使用
import JSZip from 'jszip';
import moment from 'moment';
const market = import.meta.env.VITE_MARKET;

declare const meta2d: Meta2d;
declare const window: Window;
declare const C2S: any;

enum SaveType {
  Save,
  SaveAs,
}

export default defineComponent({
  name: 'Meta2dMenu',
  components: {
    MenuItem,
    FormEvery,
    ShareModal,
  },
  setup: () => {
    const store = useStore();
    const user = computed<User>(() => store.state.user.profile);
    const actions: Record<string, (...params: any) => void> = {};
    const meta2dInfo = ref<Meta2dBackData | any>({});
    const router = useRouter();
    let showModelSaveAsPop = ref<Boolean>(false);
    const shareVisible = ref(false);
    const onLocale = (id: string) => {
      store.commit('locale', id);
    };

    const onLogout = () => {
      store.commit('user/logout');
      meta2d.emit('t-userFolder', []);
      meta2d.emit('t-meta2dFolder', []);
      meta2d.emit('t-save-success', true);
    };

    // const go = (e?: any) => {
    //   if (!e) {
    //     router.push('/');
    //   } else if (e.url) {
    //     window.open(e.url);
    //   } else {
    //     router.push('/view/' + e.name);
    //   }
    // };
    let isRelease=(e)=>{
      shared.value=e
    }
    // let isOffRelease=(e)=>{
    //   shared.value=e
    // }
    let actionTimer: any = 0;
    let actionFlag: boolean = true;
    // 菜单项点击事件
    const action = (action: string, params?: any) => {
      // 点击保存 action=save
      if (!action) {
        return;
      }
      if (actionTimer) {
        clearTimeout(actionTimer);
      }
      if (actionFlag) {
        if (action === 'toggleAnchorMode') {
          meta2d.canvas.drawingLineName && actions.drawPen();
        }
        if (actions[action]) {
          actions[action](params);//保存 actions.save(undefind)
        } else if (meta2d[action]) {
          meta2d[action](params);
        } else if (meta2d.canvas[action]) {
          meta2d.canvas[action](params);
        }
        actionFlag = false;
      }
      actionTimer = setTimeout(() => {
        actionFlag = true;
      }, 1000);
    };

    actions.downloadJson = () => {
      const data: Meta2dBackData = meta2d.data();
      if (data._id) delete data._id;
      checkData(data);
      import('file-saver').then(({ saveAs }) => {
        saveAs(
          new Blob([JSON.stringify(data)], {
            type: 'text/plain;charset=utf-8',
          }),
          `${data.name || 'topo.meta2d'}.json`
        );
      });
    };

    async function newFile(
      noRouter: boolean = false,
      query
    ) {
      store.commit('user/setComponent', query.component)
      meta2d.canvas.drawingLineName && actions.drawPen();
      meta2d.canvas.pencil && actions.drawingPencil();
      meta2d.canvas.magnifierCanvas.magnifier && actions.showMagnifier();
      meta2d.map?.isShow && actions.showMap();
      isNew.value = false;
      await localforage.removeItem(localMeta2dDataName);
      let newQuery={
        id:''
      }
      if(query.id){
        newQuery.id=query.id
      }else{
        newQuery=query
      }
      // 打开文件操作不跳转
      !noRouter &&
        router.replace({
          path: '/',
          query:newQuery,
        });
    }

    const noSaveMsg = t('系统可能不会保存您所做的更改，是否继续？');
    actions.newFile = async (query?: any) => {
      if (isNew.value) {
        if (await showModal(noSaveMsg)) {
          newFile(false, query);
        }
      } else {
        newFile(false, query);
      }
    };

    actions.open = () => {
      actions.load(true);
    };

    actions.load = async (newT: boolean = false) => {
      if (isNew.value) {
        if (await showModal(noSaveMsg)) {
          load(newT);
        }
      } else {
        load(newT);
      }
    };

    function load(newT: boolean = false) {
      const input = document.createElement('input');
      input.type = 'file';
      input.onchange = (event) => {
        const elem = event.target as HTMLInputElement;
        if (elem.files && elem.files[0]) {
          newT && newFile(true);
          // 路由跳转 可能在 openFile 后执行
          if (elem.files[0].name.endsWith('.json')) {
            openJson(elem.files[0], newT);
          } else if (elem.files[0].name.endsWith('.svg')) {
            message.info(
              '可二次编辑但转换存在损失，若作为图片使用，请使用右侧属性面板的上传图片功能'
            );
            openSvg(elem.files[0]);
          } else if (elem.files[0].name.endsWith('.zip')) {
            openZip(elem.files[0], newT);
          } else {
            message.info('打开文件只支持 json，svg，zip 格式');
          }
        }
      };
      input.click();
    }

    async function openSvg(file: File) {
      const text = await readFile(file);
      const pens: Pen[] = parseSvg(text);
      meta2d.canvas.addCaches = pens;
      message.info('svg转换成功，请点击画布决定放置位置');
    }

    const openJson = async (file: File, isNew: boolean = false) => {
      const text = await readFile(file);
      try {
        let data: Meta2dBackData = JSON.parse(text);
        if (!data.name) {
          data.name = file.name.replace('.json', '');
        }
        if (!data.version || compareVersion(data.version, baseVer) === -1) {
          // 如果版本号不存在或者版本号 version < 1.0.0
          data = upgrade(data, baseVer);
        }

        data._id = undefined;
        if (!isNew) {
          delete data.owner;
          delete data.editor;
          delete data.username;
          delete data.editorId;
          delete data.editorName;
        }
        if (!(window as any).meta2dFolder?.includes(data.folder)) {
          delete data.folder;
        }
        meta2d.open(data);
      } catch (e) {
        console.log(e);
      }
    };

    const openZip = async (file: File, isNew: boolean = false) => {
      if (!isLogin(user.value)) {
        message.warn(noLoginTip);
        return;
      }

      // if (!user.value.isVip) {
      //   vipVisible.value = true;
      //   return;
      // }

      const { default: JSZip } = await import('jszip');
      const zip = new JSZip();
      await zip.loadAsync(file);

      let dataStr = '';
      for (const key in zip.files) {
        if (zip.files[key].dir) {
          continue;
        }
        if (key.endsWith('.json')) {
          // 认为只有一个 json 文件
          dataStr = await zip.file(key).async('string');
          break;
        }
      }

      if (!dataStr) {
        return false;
      }

      for (const key in zip.files) {
        if (zip.files[key].dir) {
          continue;
        }

        if (!key.endsWith('.json')) {
          let filename = key.substr(key.lastIndexOf('/') + 1);
          const extPos = filename.lastIndexOf('.');
          let ext = '';
          if (extPos > 0) {
            ext = filename.substr(extPos);
          }
          filename = filename.substring(0, extPos > 8 ? 8 : extPos);
          // 上传文件
          const result = await upload(
            await zip.file(key).async('blob'),
            true,
            filename + ext,
            '/2D/未分类'
          );
          if (result) {
            if (dataStr.replaceAll) {
              dataStr = dataStr.replaceAll(key, result.url);
            } else {
              while (dataStr.includes(key)) {
                dataStr = dataStr.replace(key, result.url);
                // 正则 gm 在特殊情况下报错，例如如下场景
                /**
                 *    
      const data = '{"image":"/image/materials/IoT-Chemical(化学)/Air stripper 2(汽提塔2).svg"}';
      const key = '/image/materials/IoT-Chemical(化学)/Air stripper 2(汽提塔2).svg';
      data.replace(key, '123');
      data.replaceAll(key, '123')
      data.replace(new RegExp(key, 'gm'), '123');
      data.replace(new RegExp(key, 'g'), '123');
                 */
              }
            }
          }
        }
      }

      try {
        let data: Meta2dBackData = JSON.parse(dataStr);
        if (data) {
          if (!data.name) {
            data.name = file.name.replace('.zip', '');
          }
          if (!data.version || compareVersion(data.version, baseVer) === -1) {
            // 如果版本号不存在或者版本号 version < 1.0.0
            data = upgrade(data, baseVer);
          }
          data._id = undefined;
          if (!isNew) {
            delete data.owner;
            delete data.editor;
            delete data.username;
            delete data.editorId;
            delete data.editorName;
          }
          if (!(window as any).meta2dFolder?.includes(data.folder)) {
            delete data.folder;
          }
          meta2d.open(data);
        }
      } catch (e) {
        return false;
      }
    };

    const vipVisible = ref(false);
    actions.downloadZip = async () => {
      if (!isLogin(user.value)) {
        message.warn(noLoginTip);
        return;
      }

      // if (!user.value.isVip) {
      //   vipVisible.value = true;
      //   return;
      // }

      message.info('正在下载打包中，可能需要几分钟，请耐心等待...');
      const [{ default: JSZip }, { saveAs }] = await Promise.all([
        import('jszip'),
        import('file-saver'),
      ]);

      const zip = new JSZip();
      const data: Meta2dBackData = meta2d.data();
      if (data._id) delete data._id;
      checkData(data);
      zip.file(`${data.name || 'topo.meta2d'}.json`, JSON.stringify(data));
      await zipImages(zip, meta2d.store.data.pens);

      const blob = await zip.generateAsync({ type: 'blob' });
      saveAs(blob, `${data.name || 'topo.meta2d'}.zip`);
    };

    actions.downloadHtml = async () => {
      if (!isLogin(user.value)) {
        message.warn(noLoginTip);
        return;
      }

      // if (!user.value.isVip) {
      //   vipVisible.value = true;
      //   return;
      // }

      message.info('正在下载打包中，可能需要几分钟，请耐心等待...');

      const data: Meta2dBackData = meta2d.data();
      if (data._id) delete data._id;
      checkData(data);
      const [{ default: JSZip }, { saveAs }] = await Promise.all([
        import('jszip'),
        import('file-saver'),
      ]);
      const zip = new JSZip();
      zip.file('data.json', JSON.stringify(data));
      await Promise.all([
        zipImages(zip, meta2d.store.data.pens),
        zipFiles(zip),
      ]);
      const blob = await zip.generateAsync({ type: 'blob' });
      saveAs(blob, `${data.name || 'topo.meta2d'}.zip`);
    };

    enum Frame {
      vue2,
      vue3,
      react,
    }

    actions.downloadVue3 = async () => {
      downloadAsFrame(Frame.vue3);
    };

    actions.downloadVue2 = async () => {
      downloadAsFrame(Frame.vue2);
    };

    actions.downloadReact = async () => {
      downloadAsFrame(Frame.react);
    };

    async function downloadAsFrame(type: Frame) {
      if (!isLogin(user.value)) {
        message.warn(noLoginTip);
        return;
      }

      // if (!user.value.isVip) {
      //   vipVisible.value = true;
      //   return;
      // }

      message.info('正在下载打包中，可能需要几分钟，请耐心等待...');

      const data: Meta2dBackData = meta2d.data();
      if (data._id) delete data._id;
      checkData(data);
      const [{ default: JSZip }, { saveAs }] = await Promise.all([
        import('jszip'),
        import('file-saver'),
      ]);
      const zip = new JSZip();
      zip.file('data.json', JSON.stringify(data));
      await Promise.all([
        zipImages(zip, meta2d.store.data.pens),
        type === Frame.vue3
          ? zipVue3Files(zip)
          : type === Frame.vue2
          ? zipVue2Files(zip)
          : zipReactFiles(zip),
      ]);
      const blob = await zip.generateAsync({ type: 'blob' });
      saveAs(blob, `${data.name || 'topo.meta2d'}.zip`);
    }

    async function zipVue3Files(zip: JSZip) {
      const files = [
        '/view/js/marked.min.js',
        '/view/js/lcjs.iife.js',
        '/view/vue3/Meta2d.vue',
        '/view/index.html',
        '/view/package.json',
        '/view/README.md',
        '/view/README.CN.md',
      ] as const;
      // 文件同时加载
      await Promise.all(files.map((filePath) => zipFile(zip, filePath)));
    }

    async function zipVue2Files(zip: JSZip) {
      const files = [
        '/view/js/marked.min.js',
        '/view/js/lcjs.iife.js',
        '/view/vue2/Meta2d.vue',
        '/view/index.html',
        '/view/package.json',
        '/view/README.md',
        '/view/README.CN.md',
      ] as const;
      // 文件同时加载
      await Promise.all(files.map((filePath) => zipFile(zip, filePath)));
    }

    async function zipReactFiles(zip: JSZip) {
      const files = [
        '/view/js/marked.min.js',
        '/view/js/lcjs.iife.js',
        '/view/react/Meta2d.jsx',
        '/view/react/Meta2d.css',
        '/view/index.html',
        '/view/package.json',
        '/view/README.md',
        '/view/README.CN.md',
      ] as const;
      // 文件同时加载
      await Promise.all(files.map((filePath) => zipFile(zip, filePath)));
    }

    async function zipFiles(zip: JSZip) {
      const files = [
        '/view/js/marked.min.js',
        '/view/js/lcjs.iife.js',
        '/view/js/index.js',
        '/view/index.html',
        '/view/index.css',
        '/view/favicon.ico',
        '/view/package.json',
        '/view/README.md',
        '/view/README.CN.md',
      ] as const;
      // 文件同时加载
      await Promise.all(files.map((filePath) => zipFile(zip, filePath)));
    }

    async function zipFile(zip: JSZip, filePath: string) {
      const res: Blob = await axios.get(filePath, { responseType: 'blob' });
      zip.file(filePath.replace('/view', ''), res, { createFolders: true });
    }

    /**
     * 图片放到 zip 里
     * @param pens 可以是非具有 calculative 的 pen
     */
    async function zipImages(zip: JSZip, pens: Pen[]) {
      if (!pens) {
        return;
      }

      // 不止 image 上有图片， strokeImage ，backgroundImage 也有图片
      const imageKeys = [
        {
          string: 'image',
        },
        { string: 'strokeImage' },
        { string: 'backgroundImage' },
      ] as const;
      const images: string[] = [];
      for (const pen of pens) {
        for (const i of imageKeys) {
          const image = pen[i.string];
          if (image) {
            // HTMLImageElement 无法精确控制图片格式
            if (image.startsWith('/')) {
              // 只考虑相对路径下的 image ，绝对路径图片无需下载
              images.push(image);
            }
          }
        }
        // 无需递归遍历子节点，现在所有的节点都在外层
      }
      await Promise.all(images.map((image) => zipImage(zip, image)));
    }

    async function zipImage(zip: JSZip, image: string) {
      const res: Blob = await axios.get(image, {
        responseType: 'blob',
        params: {
          isZip: true,
        },
      });
      zip.file(image, res, { createFolders: true });
    }

    // 画布缩放比例
    const scale = ref(100);
    function onScale(val: number): void {
      if (val) {
        meta2d.scale((scale.value + val) / 100);
      } else {
        meta2d.scale(1);
        meta2d.centerView();
      }
      // 无需修改 scale 的值，scaleListener 监听到后会修改的
    }

    function scaleListener(newScale: number) {
      scale.value = Math.round(newScale * 100);
    }

    let normalValue = reactive({
      localeList: [],
      fromArrows: [],
      toArrows: [],
      defaultLines: [],
      menus: {} as any,
      userMenus: [],
    });
    function openedListener() {
      // TODO: 尚有其它属性未处理
      const {
        locked,
        scale: canvasScale,
        lineWidth: canvasLineWidth,
        fromArrow: canvasFromArrow,
        toArrow: canvasToArrow,
        pens
      } = meta2d.store.data;
      //设置默认背景色
      meta2d.store.data['background'] = '#232630FF';
      customCursor()
      // if (
      //   route.query.visualizationEdit &&
      //   route.query.visualizationEdit === 'true'
      // ) {
        
      //   isLock.value = locked ?? 2; // 画布不可编辑
      //   meta2d.lock(isLock.value);
      //   meta2d.hideInput();
      //   pen.value = false;
      //   pencil.value = false;
      //   console.log('这是不可编辑',isLock.value)
      // } else {
      //   isLock.value = locked; // 画布可编辑
      // }
      // isLock.value = locked; // 画布是否锁
      isLock.value=locked?locked : 0
      scale.value = Math.round(canvasScale * 100);
      lineWidth.value = canvasLineWidth ?? 1;
      fromArrow.value =
        normalValue.fromArrows.find((arrow) => arrow.value === canvasFromArrow)
          ?.icon ?? fromArrowNormal;
      //console.log('fromArrow',fromArrow.value)
      toArrow.value =
        normalValue.toArrows.find((arrow) => arrow.value === canvasToArrow)
          ?.icon ?? toArrowNormal;
      // 业务属性
      shared.value = (meta2d.store.data as Meta2dBackData).shared ?? false;
      if (pens?.length > 0 && !(meta2d.store.data as Meta2dBackData)._id) {
        // opened 一个不来自于后端，认为需要保存，是一个新操作
        isNew.value = true;
      }
    }

    function onSave({ event }) {
      event&&event.preventDefault();
      actions.save();
    }

    nextTick(() => {
      // meta2d.on('scale', scaleListener);
      meta2d.on('opened', openedListener);
      meta2d.on('t-classes', getClasses);
      meta2d.on('t-meta2dFolder', getMeta2dFolder);
      meta2d.on('save', onSave);
      setNormalByOptions();
    });

    /**
     * 根据 options 配置初始值
     */
    function setNormalByOptions() {
      const {
        autoAnchor: autoAnchorOption,
        disableAnchor: disableAnchorOption,
        drawingLineName,
      } = meta2d.store.options;
      autoAnchor.value = autoAnchorOption;
      disableAnchor.value = disableAnchorOption;
      const optionLine = normalValue.defaultLines.find(
        (line) => line.value === drawingLineName
      );
      optionLine && (line.value = optionLine.icon);
    }

    onUnmounted(() => {
      meta2d.off('scale', scaleListener);
      meta2d.off('opened', openedListener);
      meta2d.off('t-classes', getClasses);
      meta2d.off('t-meta2dFolder', getMeta2dFolder);
      meta2d.off('save', onSave);
    });

    const fromArrowNormal = 'icon-zt icon-zt-qidian';
    const fromArrow = ref(fromArrowNormal);
    function onFromArrow(item: { icon: string; value: string }) {
      fromArrow.value = item.icon;
      // 画布默认值
      meta2d.store.data.fromArrow = item.value;
      // 活动层的箭头都变化
      if (meta2d.store.active) {
        meta2d.store.active.forEach((pen: Pen) => {
          if (pen.type === PenType.Line) {
            pen.fromArrow = item.value;
            meta2d.setValue(
              {
                id: pen.id,
                fromArrow: pen.fromArrow,
              },
              {
                render: false,
              }
            );
          }
        });
        meta2d.render();
      }
    }

    const toArrowNormal = 'icon-zt icon-zt-zhongdian';
    const toArrow = ref(toArrowNormal);
    function onToArrow(item: { icon: string; value: string }) {
      toArrow.value = item.icon;
      // 画布默认值
      meta2d.store.data.toArrow = item.value;
      // 活动层的箭头都变化
      if (meta2d.store.active) {
        meta2d.store.active.forEach((pen: Pen) => {
          if (pen.type === PenType.Line) {
            pen.toArrow = item.value;
            meta2d.setValue(
              {
                id: pen.id,
                toArrow: pen.toArrow,
              },
              {
                render: false,
              }
            );
          }
        });
        meta2d.render();
      }
    }

    const lineWidthVisible = ref(false);
    const lineWidth = ref(1);
    function onDefaultLineWidth() {
      // 画布数据默认值
      meta2d.store.data.lineWidth = lineWidth.value;
      // 活动层的线都变化
      if (meta2d.store.active) {
        meta2d.store.active.forEach((pen: Pen) => {
          if (pen.type === PenType.Line) {
            pen.lineWidth = lineWidth.value;
            meta2d.setValue(
              {
                id: pen.id,
                lineWidth: pen.lineWidth,
              },
              {
                render: false,
              }
            );
          }
        });
        meta2d.render();
      }
    }

    const autoAnchor = ref(false);
    function onAutoAnchor() {
      meta2d.store.options.autoAnchor = !meta2d.store.options.autoAnchor;
      autoAnchor.value = meta2d.store.options.autoAnchor;
      //开启自动锚点时，启用显示锚点
      if(autoAnchor.value){
        disableAnchor.value=false
        meta2d.store.options.disableAnchor=false
      }
    }

    function onKeyUp(e: KeyboardEvent) {
      setTimeout(() => {
        switch (e.key) {
          case 'Alt':
            autoAnchor.value = meta2d.store.options.autoAnchor;
            break;
        }
      });
    }
    const isLock = ref(0);

    function onLock() {
      !isLock.value && (isLock.value = 0);
      if (isLock.value === LockState.DisableMove) {
        isLock.value = LockState.None;
      } else {
        isLock.value++;
      }
      meta2d.lock(isLock.value);
      meta2d.hideInput();
      pen.value = false;
      pencil.value = false;
      customCursor()
    }
    function customCursor(){
      meta2d.store.data.pens.forEach(x=>{
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
    }
    // 进入预览页面
    actions.preview = async () => {
       // 新增保存
      await actions.save()
      const data: Meta2dBackData = meta2d.data();
      checkData(data);
      if (
        isNew.value &&
        user.value &&
        data.owner?.id === user.value.id &&
        data._id &&
        !route.query.version
      ) {
        // 有 id ，是修改后保存
        await actions.save(false, SaveType.Save);
      }
      if (!data._id) {
        await localforage.setItem(localMeta2dDataName, JSON.stringify(data));
      }
      setTimeout(()=>{
         const { href } = router.resolve({
          path: '/preview', query: {
            r: Date.now() + '',
            id: data._id,
            version: route.query.version,
          }
        });
        window.open(href, '_blank');
      },100)
      // router.push({
      //   path: '/preview',
      //   query: {
      //     r: Date.now() + '',
      //     id: data._id,
      //     version: route.query.version,
      //   },
      // });
    };
    const shared = ref(false); // 默认值未分享
    actions.share = () => {
      if (!isLogin(user.value)) {
        message.warn(t('请先登录，否则无法发布!'));
        return;
      }
      if (!route.query.id) {
        message.warn(t('请先保存，否则无法发布!'));
        return;
      }
      shareVisible.value = true;
    };

    const downloadImageTips = t(
      '无法下载，宽度不合法，画布可能没有画笔/画布大小超出浏览器最大限制'
    );
    actions.downloadPng = () => {
      const name = (meta2d.store.data as Meta2dBackData).name;
      try {
        meta2d.downloadPng(name ? name + '.png' : undefined);
      } catch (e) {
        message.warn(downloadImageTips);
      }
    };

    actions.downloadSvg = async () => {
      await import('@/assets/canvas2svg');
      if (!C2S) {
        message.error('请先加载canvas2svg.js');
        return;
      }

      const rect = meta2d.getRect();
      if (!isFinite(rect.width)) {
        message.error(downloadImageTips);
        return;
      }
      rect.x -= 10;
      rect.y -= 10;
      const ctx = new C2S(rect.width + 20, rect.height + 20);
      ctx.textBaseline = 'middle';
      ctx.strokeStyle = getGlobalColor(meta2d.store);
      for (const pen of meta2d.store.data.pens) {
        // 不使用 calculative.inView 的原因是，如果 pen 在 view 之外，那么它的 calculative.inView 为 false，但是它的绘制还是需要的
        if (!isShowChild(pen, meta2d.store) || pen.visible == false) {
          continue;
        }
        meta2d.renderPenRaw(ctx, pen, rect);
      }

      let mySerializedSVG = ctx.getSerializedSvg();
      mySerializedSVG = mySerializedSVG.replace(
        '<defs/>',
        `<defs>
    <style type="text/css">
  @font-face {
    font-family: 'ticon';
    src: url('icon/通用图标/iconfont.ttf') format('truetype');
  }
</style>
{{bk}}
  </defs>
{{bkRect}}`
      );

      if (meta2d.store.data.background) {
        mySerializedSVG = mySerializedSVG.replace('{{bk}}', '');
        mySerializedSVG = mySerializedSVG.replace(
          '{{bkRect}}',
          `<rect x="0" y="0" width="100%" height="100%" fill="${meta2d.store.data.background}"></rect>`
        );
      } else {
        mySerializedSVG = mySerializedSVG.replace('{{bk}}', '');
        mySerializedSVG = mySerializedSVG.replace('{{bkRect}}', '');
      }

      mySerializedSVG = mySerializedSVG.replace(/--topo--/g, '&#x');

      const urlObject: any = (window as any).URL || window;
      const export_blob = new Blob([mySerializedSVG]);
      const url = urlObject.createObjectURL(export_blob);

      const a = document.createElement('a');
      a.setAttribute(
        'download',
        `${(meta2d.store.data as Meta2dBackData).name || 'topo.meta2d'}.svg`
      );
      a.setAttribute('href', url);
      const evt = document.createEvent('MouseEvents');
      evt.initEvent('click', true, true);
      a.dispatchEvent(evt);
    };

    const pencil = ref(false); // 铅笔
    const pen = ref(false); // 钢笔
    const magnifier = ref(false); // 放大镜
    const map = ref(false); // 小地图
    const onKeyDown = (e: any) => {
      // TODO: 考虑在每次按键触发中 更改值
      switch (e.key) {
        case 'b':
        case 'B':
          pencil.value = true;
          break;
        case 'v':
        case 'V':
          if (e.ctrlKey || e.metaKey) {
            return;
          } else {
            if (meta2d.canvas.drawingLineName) {
              pen.value = true;
            } else {
              pen.value = false;
            }
          }
          break;
        case 'm':
        case 'M':
          if (meta2d.canvas.magnifierCanvas.magnifier) {
            magnifier.value = true;
          } else {
            magnifier.value = false;
          }
          break;
        case 'Escape':
          pen.value = false;
          pencil.value = false;
          magnifier.value = false;
          line.value = normalValue.defaultLines.find(
            (line) => line.value === meta2d.store.options.drawingLineName
          ).icon;
          break;
        case 'Shift':
          const currentLine = normalValue.defaultLines.find(
            (line) => line.value === meta2d.canvas.drawingLineName
          );
          currentLine && (line.value = currentLine.icon);
          break;
        case 'Enter':
          pen.value = !!meta2d.canvas.drawingLineName;
          break;
        default:
          break;
      }
    };

    function finishLine() {
      setTimeout(() => {
        // 同步代码执行完了后执行这里，在 add 消息中，先发的消息，后清除的 drawingLineName
        pen.value = !!meta2d.canvas.drawingLineName;
        pencil.value = meta2d.canvas.pencil;
        line.value = normalValue.defaultLines.find(
          (line) => line.value === meta2d.store.options.drawingLineName
        ).icon;
      });
    }

    const isNew = ref(false);
    function checkSaveBeforeUnload(e: BeforeUnloadEvent) {
      if (isNew.value) {
        const data: Meta2dBackData = meta2d.data();
        if (
          user.value &&
          // user.value.isVip &&
          data._id &&
          !data.component &&
          data.owner &&
          data.owner?.id === user.value.id
        ) {
          actions.save();
        }
        // 存在未保存的数据，给个提示
        // e = e || window.event;
        // // 兼容IE8和Firefox 4之前的版本
        // if (e) {
        //   e.returnValue = '关闭提示';
        // }
        // // Chrome, Safari, Firefox 4+, Opera 12+ , IE 9+
        // return '关闭提示';
      }
      // setTimeout(() => {
      // e.returnValue = '关闭提示';
      // return '关闭提示';
      // },6000)
    }
    // let changeDataTimer: NodeJS.Timeout = undefined;
    // function changeData() {
    //   isNew.value = true;
    //   // 过于频繁，开个防抖
    //   changeDataTimer && clearTimeout(changeDataTimer);
    //   changeDataTimer = setTimeout(() => {
    //     const data: Meta2dBackData = meta2d.data();
    //     localforage.setItem(localMeta2dDataName, JSON.stringify(data));
    //     changeDataTimer = undefined;
    //   }, 500);
    // }

    let saveTimer: NodeJS.Timeout = undefined;
    let localSaveTimer: NodeJS.Timeout = undefined;
    function autoSave() {
      isNew.value = true;
      //监听到修改数据后自动保存，5秒防抖时间
      localSaveTimer && clearTimeout(localSaveTimer);
      localSaveTimer = setTimeout(() => {
        // const data: Meta2dBackData = meta2d.data();
        //vip才有自动保存功能
        // if (
        //   user.value&&user.value.isVip &&
        //   data._id &&
        //   !data.component &&
        //   data.owner &&
        //   data.owner?.id === user.value.id
        // ) {
        //   actions.save(false, SaveType.Save);
        // } else {
        //保存到本地
        const data: Meta2dBackData = meta2d.data();
        let _localMeta2dDataName = data._id
          ? localMeta2dDataName + '-' + data._id
          : localMeta2dDataName;
        (data as any).localSaveAt = moment().format();
        localforage.setItem(_localMeta2dDataName, JSON.stringify(data));
        // }
        localSaveTimer = undefined;
      }, 3000);

      autoSaveServer();
    }

    //一分钟内保存一次
    function autoSaveServer() {
      if (saveTimer) {
        return;
      }
      saveTimer = setTimeout(() => {
        const data: Meta2dBackData = meta2d.data();
        if (
          user.value &&
          // user.value.isVip &&
          data._id &&
          !data.component &&
          data.owner &&
          data.owner?.id === user.value.id
        ) {
          actions.save(false, SaveType.Save);
        }
        saveTimer = null;
      }, 60000);
    }

    nextTick(() => {
      window.addEventListener('keydown', onKeyDown);
      window.addEventListener('keyup', onKeyUp);
      // 完成连线可以触发 add 方法，TODO: 触发方式并不是很合理
      meta2d.on('add', finishLine);
      // window.onbeforeunload = checkSaveBeforeUnload;
      // meta2d.on('undo', changeData);
      // meta2d.on('redo', changeData);
      // meta2d.on('update', changeData);
      // meta2d.on('valueUpdate', changeData);
      meta2d.on('t-newFile', actions.newFile);
      meta2d.on('disableAnchor', changeDisableAnchor);

      //自动保存
      meta2d.on('undo', autoSave);
      meta2d.on('redo', autoSave);
      meta2d.on('add', autoSave);
      meta2d.on('delete', autoSave);
      meta2d.on('rotatePens', autoSave);
      meta2d.on('translatePens', autoSave);
      meta2d.on('components-update-value', autoSave);
    });
    onUnmounted(() => {
      window.removeEventListener('keydown', onKeyDown);
      window.removeEventListener('keyup', onKeyUp);
      meta2d.off('add', finishLine);
      window.onbeforeunload = null;
      // meta2d.off('undo', changeData);
      // meta2d.off('redo', changeData);
      // meta2d.off('update', changeData);
      // meta2d.off('valueUpdate', changeData);
      meta2d.off('t-newFile', actions.newFile);
      meta2d.off('disableAnchor', changeDisableAnchor);

      meta2d.off('undo', autoSave);
      meta2d.off('redo', autoSave);
      meta2d.off('add', autoSave);
      meta2d.off('delete', autoSave);
      meta2d.off('rotatePens', autoSave);
      meta2d.off('translatePens', autoSave);
      meta2d.off('components-update-value', autoSave);
    });

    const changeDisableAnchor = () => {
      console.log('meta2d.store.options',meta2d.store.options)
      const {
        disableAnchor: disableAnchorOption,
        autoAnchor: autoAnchorOption,
      } = meta2d.store.options;
      disableAnchor.value = disableAnchorOption;
      if (disableAnchorOption && autoAnchorOption) {
        // 禁用瞄点开了，需要关闭自动瞄点
        onAutoAnchor();
      }
    };
    actions.drawPen = () => {
      //取消选中
      meta2d.inactive();
      try {
        if (!meta2d.canvas.drawingLineName) {
          // 开了钢笔，需要关掉铅笔
          meta2d.canvas.pencil && actions.drawingPencil();
          meta2d.drawLine(meta2d.store.options.drawingLineName);
        } else {
          meta2d.finishDrawLine();
          meta2d.drawLine();
        }
        pen.value = !!meta2d.canvas.drawingLineName;
      } catch (e) {
        if (e.message === 'canvas is locked') {
          return message.warn('画布被锁住');
        }
        message.warn(e.message);
      }
    };

    actions.drawingPencil = () => {
      try {
        if (!meta2d.canvas.pencil) {
          // 开了铅笔需要关掉钢笔
          meta2d.canvas.drawingLineName && actions.drawPen();
          meta2d.drawingPencil();
        } else {
          meta2d.stopPencil();
        }
        pencil.value = meta2d.canvas.pencil;
      } catch (e) {
        if (e.message === 'canvas is locked') {
          return message.warn('画布被锁住');
        }
        message.warn(e.message);
      }
    };

    actions.showMagnifier = () => {
      if (!meta2d.canvas.magnifierCanvas.magnifier) {
        meta2d.showMagnifier();
      } else {
        meta2d.hideMagnifier();
      }
      magnifier.value = meta2d.canvas.magnifierCanvas.magnifier;
    };

    actions.showMap = () => {
      if (!meta2d.map?.isShow) {
        meta2d.showMap();
      } else {
        meta2d.hideMap();
      }
      map.value = meta2d.map?.isShow;
    };

    const line = ref('icon-zt icon-zt-lianxian');
    function onLine(item: { icon: string; value: string; name: string }) {
      line.value = item.icon;
      if (meta2d) {
        meta2d.store.options.drawingLineName = item.value;
        meta2d.canvas.drawingLineName &&
          (meta2d.canvas.drawingLineName = item.value);
        meta2d.store.active?.forEach((pen) => {
          meta2d.updateLineType(pen, item.value);
        });
      }
    }

    const route = useRoute();
    actions.save = async (tip = true, type: SaveType = SaveType.Save) => {
      // 保存需要判断登录
      const data: Meta2dBackData = meta2d.data();
      // if (!data.folder) {
      //   message.error('请选择文件夹');
      //   return;
      // }
      data.component=user.value.component
      if (!data._id && route.query.id) {
        data._id = route.query.id as string;
      }
      customCursor()
      //刷新不需要显示缩略图
      data.showMap=false
      console.log('保存',data)
      checkData(data);
      if (!isLogin(user.value)) {
        message.warn(noLoginTip);
        localforage.setItem(localMeta2dDataName, JSON.stringify(data));
        return;
      }
      //TODO 考虑团队图纸问题，由后端验证
      // if (
      //   type === SaveType.Save &&
      //   data._id &&
      //   data.owner &&
      //   data.owner?.id !== user.value.id
      // ) {
      //   message.warning(
      //     t(
      //       '无法保存他人的图纸！若想要保存为自己的图纸，请使用另存为（会生成新的图纸 id）'
      //     )
      //   );
      //   return;
      // }

      if (
        (window as any).beforeSaveMeta2d &&
        !(await (window as any).beforeSaveMeta2d(data))
      ) {
        return;
      }


      if (type === SaveType.SaveAs) {
        //另存为去掉teams信息
        delete (data as any).teams;
      }
      

      //如果不是自己创建的团队图纸，就不去修改缩略图（没有权限去删除缩略图）
      if (!((data as any).teams && data.owner.id !== user.value.id)) {
        let blob: Blob = undefined;
        const rect = meta2d.getRect();
        if(isFinite(rect.width)){
         try {
            blob = dataURLtoBlob(meta2d.toPng(10));
          } catch (e) {
            message.error('宽度不合法，画布大小超出浏览器最大限制');
            return;
          }
        }else{
          //用于空白屏保存
          const defaultPic=`data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAHgAAAB4CAIAAAC2BqGFAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyRpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDYuMC1jMDA2IDc5LmRhYmFjYmIsIDIwMjEvMDQvMTQtMDA6Mzk6NDQgICAgICAgICI+IDxyZGY6UkRGIHhtbG5zOnJkZj0iaHR0cDovL3d3dy53My5vcmcvMTk5OS8wMi8yMi1yZGYtc3ludGF4LW5zIyI+IDxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSIiIHhtbG5zOnhtcD0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wLyIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0UmVmPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VSZWYjIiB4bXA6Q3JlYXRvclRvb2w9IkFkb2JlIFBob3Rvc2hvcCAyMi40IChXaW5kb3dzKSIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZDpENDcyQzI3RjJDNEQxMUVFODk5NEZCQTkyNEU2NkI1RCIgeG1wTU06RG9jdW1lbnRJRD0ieG1wLmRpZDpENDcyQzI4MDJDNEQxMUVFODk5NEZCQTkyNEU2NkI1RCI+IDx4bXBNTTpEZXJpdmVkRnJvbSBzdFJlZjppbnN0YW5jZUlEPSJ4bXAuaWlkOkQ0NzJDMjdEMkM0RDExRUU4OTk0RkJBOTI0RTY2QjVEIiBzdFJlZjpkb2N1bWVudElEPSJ4bXAuZGlkOkQ0NzJDMjdFMkM0RDExRUU4OTk0RkJBOTI0RTY2QjVEIi8+IDwvcmRmOkRlc2NyaXB0aW9uPiA8L3JkZjpSREY+IDwveDp4bXBtZXRhPiA8P3hwYWNrZXQgZW5kPSJyIj8+rjvu8wAADw5JREFUeNrsnftX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`;
          blob = dataURLtoBlob(defaultPic);
        }
        // try {
        //   blob = dataURLtoBlob(meta2d.toPng(10));
        // } catch (e) {
        //   message.error(downloadImageTips);
        //   return;
        // }
        if (data._id && type === SaveType.Save) {
          if (!(await delImage(data.image))) {
            return;
          }
        }

        const file = await upload(blob, true);
        if (!file) {
          return;
        }
        // 缩略图
        data.image = file.url;
        (meta2d.store.data as Meta2dBackData).image = data.image;
      }

      if (data.component) {
        // pens 存储原数据用于二次编辑 ； componentDatas 组合后的数据，用于复用
        data.componentDatas = meta2d.toComponent(
          undefined,
          (meta2d.store.data as Meta2dBackData).showChild,
          false //自定义组合节点生成默认锚点
        );
      } else {
        data.component = false; // 必要值
      }

      let collection = data.component ? 'topo2d-components' : 'topo2d';
      let ret: any;
      if (!data.name) {
        // 文件名称
        data.name = `meta2d.${new Date().toLocaleString()}`;
        (meta2d.store.data as Meta2dBackData).name = data.name;
      }
      !data.version && (data.version = baseVer);

      let list = undefined;
      let folder: any = undefined;
      let folderId = undefined;
      if (!list) {
        list = [];
      }

      if (type === SaveType.SaveAs) {
        // 另存为一定走 新增 ，由于后端 未控制 userId 等属性，清空一下
        const delAttrs = [
          'userId',
          'id',
          'shared',
          'star',
          'view',
          'username',
          'editorName',
          'editorId',
          'createdAt',
          'updatedAt',
          'recommend',
        ];
        for (const k of delAttrs) {
          delete data[k];
        }
        ret = await axios.post(`/data/${collection}/add`, data); // 新增
        if (!data.component) {
          list.push({
            id: ret._id,
            image: data.image,
            name: data.name,
            component: data.component,
          });
        }
      } else {
        if (
          data._id &&
          (data as any).teams &&
          data.owner.id !== user.value.id
        ) {
          // 团队图纸 不允许修改文件夹信息
          delete data.folder;
          ret = await axios.post(`/data/${collection}/update`, data); // 修改
        } else if (
          data._id
          // && (!data.owner || data.owner.id === user.value.id)
        ) {
          ret = await axios.post(`/data/${collection}/update`, data); // 修改
          if (!data.component) {
            list.forEach((i: any) => {
              if (i.id === data._id) {
                i.image = data.image;
              }
            });
          }
          //TODO 处理老接口图纸情况
          let one = list.find((item) => item.id === data._id);
          if (!data.component && !one) {
            list.push({
              id: ret._id,
              image: data.image,
              name: data.name,
              component: data.component,
            });
          }
        } else {
          ret = await axios.post(`/data/${collection}/add`, data); // 新增
          if (!data.component) {
            list.push({
              id: ret._id,
              image: data.image,
              name: data.name,
              component: data.component,
            });
          }
        }
      }

      if (ret.error) {
        return null;
      } else {
        if (!data.component && folderId) {
          let listData = [];
          list.forEach((i: any) => {
            if (i.id === data._id) {
              listData.push(data);
            } else {
              listData.push(i);
            }
          });
        }
        showModelSaveAsPop.value = false;
      }
      //  保存图纸之后的钩子函数
      (window as any).afterSaveMeta2d &&
        (await (window as any).afterSaveMeta2d(ret));
      if (
        !data._id ||
        data.owner?.id !== user.value.id ||
        route.query.version ||
        type === SaveType.SaveAs // 另存为肯定走新增，也会产生新的 id
      ) {
        data._id = ret._id;
        (meta2d.store.data as Meta2dBackData)._id = data._id;
        router.replace({
          path: '/',
          query: {
            id: data._id,
            // r: Date.now() + '',
            // component: data.component + '',
            // headtitle:data.name
          },
        });
      } else {
        // TODO: 打开最近文件功能暂无
        // Store.set('recently', {
        //   id: this.data._id,
        //   image: this.data.image,
        //   name: this.data.name
        // });
        const currentQuery =Object.assign({}, route.query);
        router.replace({
          path: '/',
          query: currentQuery,
        });
      }
   
      store.commit('user/setComponent', data.component)
      tip && message.success('保存成功！',1);
      // // 保存成功，重新请求文件夹--->7/14更新为不在主动调用更新，-->主动清除对应文件下的列表数据，让其同步
      // meta2d.emit('t-save-success', true);//调用
      meta2d.emit('t-save-success', '保存');//调用
      // 已保存，不再是新的，无需提示保存
      isNew.value = false;
      localforage.removeItem(localMeta2dDataName);

      // router.afterEach(() => {
      //   message.destroy(); // 关闭所有的Message提示
      // });
    };







    const saveAsConfigs = computed(() => [
      {
        key: 'name',
        type: 'text',
        name: `${t('文件名')}`,
      },
      {
        key: 'folder',
        type: 'select',
        name: `${t('文件夹')}`,
        options: meta2dFolder.value,
        vModelValue: meta2dInfo.value.folder,
      },
      {
        key: 'class',
        type: 'select',
        name: `${t('分类')}`,
        options: classesOption.value,
        vModelValue: meta2dInfo.value.class,
      },
    ]);
    // 获取下拉框数据
    const classesOption = ref([]);
    function getClasses(classes: any[]) {
      classesOption.value = classes.map((i) => ({
        label: t(i.name),
        value: i.name,
        // name: 'class',
      }));
    }
    const meta2dFolder = ref([]);
    const getMeta2dFolder = (folder) => {
      meta2dFolder.value = folder.map((i) => ({
        label: t(i),
        value: i,
        name: 'folder',
        disabled: i === '未分类',
      }));
      // 不再有未分类，必须添加文件夹名
      // meta2dFolder.value.pop();
    };
    actions.saveAsPop = () => {
      meta2dInfo.value = Object.assign({}, meta2d.store.data);
      showModelSaveAsPop.value = true;
    };
    // 暂存原始文件名
    // 改变选择的数据时
    function changeValue(value, option) {
      if (!option) {
        (meta2d.store.data as any).name = meta2dInfo.value.name;
      } else {
        meta2d.store.data[option.name] = value;
      }
    }
    const saveAs = () => {
      actions.save(true, SaveType.SaveAs);
    };
    // 新建文件夹逻辑
    const showModelNewFolder = ref<Boolean>(false);
    const newFolder = ref('');
    async function getFolder() {
      let folder: any = await axios.get('/user/folder');
      if (folder.error) {
        folder = {};
      }
      return folder;
    }
    async function addFolder(
      name: string,
      type: 'meta2d' | 'component'
    ): Promise<boolean> {
      if (!isLogin(user.value)) {
        message.warn(noLoginTip);
        return false;
      }
      let str= ( type === 'meta2d' ? 'dir' : 'components');//修改为新接口
      const folder: any = await axios.post(`/data/directory${str}/add`, {
        type: `${type === 'meta2d' ? 'topo2d' : 'topo2d-components'}`,
        list: [],
        name,
      });
      if (folder.error) {
        message.warn('新增文件夹失败：' + folder.error);
        return false;
      }
      return true;
    }
    // async function addFolder(
    //   name: string,
    //   type: 'meta2d' | 'user'
    // ): Promise<boolean> {
    //   if (!isLogin(user.value)) {
    //     message.warn(noLoginTip);
    //     return false;
    //   }
    //   const folder: any = await axios.post('/user/folder', {
    //     type,
    //     name,
    //   });
    //   if (folder.error) {
    //     message.warn('新增文件夹失败：' + folder.error);
    //     return false;
    //   }
    //   return true;
    // }
    const okAdd = async () => {
      if (!isLogin(user.value)) {
        message.warn('请先登录');
        return;
      }
      if (!newFolder.value) {
        message.error(t('文件夹不能为空').toString());
        return;
      }
      newFolder.value = newFolder.value.trim();
      if (newFolder.value === '') {
        message.error(t('文件夹不能为空').toString());
        return;
      }
      // const folders = await getFolder();
      // const meta2dList = ref(MaterialsGroup([], folders.meta2d));
      const folder = meta2dFolder.value.find(
        (n) => n.label === newFolder.value
      );
      if (folder) {
        message.error(t('文件夹已经存在').toString());
        return;
      }

      //数据发送到后端
      const addFolderRes = await addFolder(newFolder.value, 'meta2d');
      if (!addFolderRes) {
        // 添加失败，不加入到列表
        return;
      }
      meta2dFolder.value.push({
        label: newFolder.value,
        value: newFolder.value,
      });
      // meta2dFolder.value.push({
      //   name: newFolder.value,
      //   show: true,
      //   end: 10,
      //   list: [],
      // });
      newFolder.value = '';
      showModelNewFolder.value = false;

      meta2d.emit(
        't-meta2dFolder',
        meta2dFolder.value.map((i) => i.value)
      );
    };
    importNormalValue();

    async function importNormalValue() {
      const [
        { localeList },
        { iconMenus: menus, userMenus, fromArrows, toArrows, defaultLines },
      ] = await Promise.all([import('../store/i18n'), import('./defaults')]);
      normalValue.localeList.push(...localeList);
      normalValue.defaultLines.push(...defaultLines);
      normalValue.fromArrows.push(...fromArrows);
      normalValue.toArrows.push(...toArrows);
      normalValue.userMenus.push(...userMenus);
      Object.assign(normalValue.menus, menus);
    }

    const disableAnchor = ref(false);

    function onDisableAnchor() {
      meta2d.store.options.disableAnchor = !meta2d.store.options.disableAnchor;
      // const {
      //   disableAnchor: disableAnchorOption,
      //   autoAnchor: autoAnchorOption,
      // } = meta2d.store.options;
      // disableAnchor.value = disableAnchorOption;
      // if (disableAnchorOption && autoAnchorOption) {
      //   // 禁用瞄点开了，需要关闭自动瞄点
      //   onAutoAnchor();
      // }
      changeDisableAnchor();
    }

    function getAccountUrl() {
      if (market) {
        return `/account/login?cb=${encodeURIComponent(location.href)}`;
      } else {
        let arr = location.host.split('.');
        arr[0] = 'http://account';
        let accountUrl = arr.join('.');
        // return accountUrl;
        return `${
          (window as any).loginUrl ? (window as any).loginUrl : accountUrl
        }?cb=${encodeURIComponent(location.href)}`;
      }
    }

    return {
      locale: computed(() => store.state.locale),
      localeDesc: computed(() => store.getters.localeDesc),
      ...normalValue,
      user,
      onLogout,
      onLocale,
      // go,
      action,
      onScale,
      scale,
      onFromArrow,
      fromArrow,
      toArrow,
      line,
      onLine,
      onToArrow,
      lineWidth,
      onDefaultLineWidth,
      lineWidthVisible,
      autoAnchor,
      onAutoAnchor,
      isLock,
      onLock,
      shared,
      pencil,
      pen,
      magnifier,
      map,
      disableAnchor,
      onDisableAnchor,
      getAccountUrl,
      isNew,
      vipVisible,
      companyName: (window as any).companyName,
      showModelSaveAsPop,
      changeValue,
      saveAsConfigs,
      meta2dInfo,
      saveAs,
      okAdd,
      showModelNewFolder,
      newFolder,
      shareVisible,
      isRelease,
      customCursor
    };
  },
});

/**
 * 有些数据为了辅助计算，在这删掉
 */
export function checkData(data: Meta2dData) {
  const pens = data.pens || [];
  for (let i = 0; i < pens.length; i++) {
    const pen = pens[i];
    pen.events?.forEach((event: any) => {
      delete event.setProps;
    });

    //处理画笔是脏数据的情况
    if (
      !(
        pen.x > -Infinity &&
        pen.x < Infinity &&
        pen.y > -Infinity &&
        pen.y < Infinity &&
        pen.width > -Infinity &&
        pen.width < Infinity &&
        pen.height > -Infinity &&
        pen.height < Infinity
      )
    ) {
      pens.splice(i, 1);
      --i;
    } else if (
      pen.x == null ||
      pen.y == null ||
      pen.width == null ||
      pen.height == null
    ) {
      pens.splice(i, 1);
      --i;
    }
  }

  if (Array.isArray(data.mqttOptions)) {
    // mqttOptions 是数组则认为是脏数据，删掉
    data.mqttOptions = {};
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

@media screen and (max-width: 1280px) {
  .Meta2dMenu {
    // 隐藏部分元素 TODO: 可以加几个

    .help-menu {
      display: none;
    }
  }
}

.Meta2dMenu {
  background: #ffffff;
  //box-shadow: 0px 2px 4px 0px #dad7d7;
  align-items: center;
  .logo {
    align-items: center;

    img {
      margin-left: 8px;
      margin-right: 8px;

      width: 30px;
      height: 30px;
    }
  }

  .menus {
    flex-grow: 1;
    padding: 0 30px;
    justify-content: space-between;
    align-items: center;
  }

  .userArea {
    margin-right: 26px;
  }

  .loginArea {
    margin-right: 26px;

    > :first-child {
      font-size: 14px;
      font-family: PingFangSC, PingFangSC-Regular;
      font-weight: 400;
      color: #474e59;
      line-height: 20px;
      // margin-right: 21px;
    }

    button {
      width: 80px;
      height: 30px;
      background: $color-primary;
      border-radius: 2px;
      border: none;

      font-size: 14px;
      font-family: PingFangSC, PingFangSC-Regular;
      font-weight: 400;
      text-align: center;
      color: #ffffff;
      line-height: 20px;

      white-space: nowrap;

      cursor: pointer;
    }
  }
}

:deep(.ant-dropdown-menu-item) {
  border-top: 2px solid transparent;
}

:deep(.border-top) {
  border-top: 2px solid $color-primary;
}
</style>

<style lang="scss">
.vip-modal {
  .ant-modal-content {
    $modal-border-radius: 12px;
    border-radius: $modal-border-radius;
    box-shadow: 0px 8px 32px 0px rgba(0, 10, 38, 0.15);

    .ant-modal-header {
      border-radius: $modal-border-radius;

      .ant-modal-title {
        font-family: PingFangSC, PingFangSC-Medium;
        color: #0b0d0f;
        line-height: 24px;
      }
    }

    .ant-modal-body {
      display: flex;
      justify-content: center;

      .vip-img {
        width: 195px;
        cursor: pointer;
      }
    }

    .ant-modal-footer {
      border-top: none;
    }
  }
}
.rightItem {
  .iconItem {
    flex-direction: row;
    width: 60px;
    height: 24px;
    background: #303640;
    border: 1px solid #454851;
    &:hover {
      background: #454851;
      border: 1px solid #454851;
      color: #fff;
    }
    .icon {
      margin-right: 5px;
      i {
        font-size: 12px;
      }
    }
  }
}
.ant-notification {
  .ant-notification-notice {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.35);
  }
  .ant-notification-close-icon {
    color: #fff;
  }
  .ant-notification-notice-with-icon {
    .ant-notification-notice-message {
      color: #fff;
    }
  }
}
</style>
