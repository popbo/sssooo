import { ChartPen, setEchartsOption } from '@topometa2d/chart-diagram';
import { BindId, Meta2d } from '@topometa2d/core';
import { ref } from 'vue';
import { FormItemType } from '../utils';
import store from '@/store/index'
import {closeHttpFix,connectHttpfix} from '@/utils/soucerequest'

declare const meta2d: Meta2d;

export function useBindModal() {
  const visible = ref(false);
  const dataId = ref<BindId>({
    dataId: '',
    name: '',
  });
  const dataIds = ref<BindId[]>([]);
  const currentConfig = ref<FormItemType>({
    key: '',
    type: 'text',
    name: '',
  });
  const showModal = (key: string, index: number, config: FormItemType) => {
    visible.value = true;
    meta2d.closeHttp();
    currentConfig.value = config;
    if (!config.multiple) {
      dataId.value = config.dataIds as BindId;
    } else {
      // 多变量
      dataIds.value = config.dataIds as BindId[];
    }
  };

  function selectId(
    {
      ids,
      isTime,
      isYCategory,
      type,
    }: {
      ids: BindId | BindId[];
      isTime: boolean;
      isYCategory: boolean;
      type: string;
    },
    obj: Record<string, any>
  ) {
    if (!Array.isArray(ids) && type === 'delete') {
      dataId.value = {
        name: '',
        dataId: '',
      };
    }
    currentConfig.value.dataIds = ids;
    if (currentConfig.value.key === 'dataY' && Array.isArray(ids)) {
      setEchartsOption(
        meta2d.store.pens[obj.id] as ChartPen,
        ids,
        isTime,
        isYCategory
      );
    }
    meta2d.store.bindDatas = {};
    meta2d.initBindDatas();
    openHttp();
  }

  return {
    visible,
    showModal,
    config: currentConfig,
    dataId,
    selectId,
    dataIds,
  };
}
function openHttp() {
  // meta2d.closeHttp();
  closeHttpFix()
  // meta2d.store.data.http = '/api/device/data?mock=1';
  // meta2d.store.data.httpTimeInterval = 500;
  // debugger;
  // meta2d.connectHttp();
  connectHttpfix(meta2d.store.data.https)
}
