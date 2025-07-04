import { FormItemType } from '@/components/utils';
import axios from '@/http';
import { BindId } from '@topometa2d/core';
import { ref } from 'vue';

type BindModalProps = {
  readonly visible: boolean;
  readonly dataId?: BindId;
  readonly dataIds?: BindId[];
  readonly config?: FormItemType;
};

type BindModalEmit = (
  event: 'update:visible' | 'chooseId',
  ...args: any[]
) => void;

// 绑定变量树，只有一个数据
const binds = ref([]);
// const RealTimeDataBaseTree=ref([])  //TODO后续实时数据库的设备获取移到此处
const treeleafs = ref([]);
export function useBindData(props: Partial<BindModalProps> = {}) {
  async function queryBinds() {
    // binds.value = await axios.get('https://mock.apifox.cn/m1/2265903-0-default/api/device/data');
    getParentsKeys(binds.value);
    getLeaf(binds.value, treeleafs.value);
  }

  //叶子节点所有展开parent keys
  function getParentsKeys(json) {
    for (var i = 0; i < json.length; i++) {
      const sonList = json[i].children;
      sonList &&
        sonList.forEach((son) => {
          if (json[i].parentKeys) {
            son.parentKeys = [...json[i].parentKeys, json[i].id];
          } else {
            son.parentKeys = [json[i].id];
          }
        });
      if (sonList && sonList.length > 0) {
        getParentsKeys(sonList);
      }
    }
  }

  //遍历叶子节点
  function getLeaf(json, arr) {
    for (var i = 0; i < json.length; i++) {
      var sonList = json[i].children;
      if (!sonList || sonList.length == 0) {
        arr.push(json[i]);
      } else {
        getLeaf(sonList, arr);
      }
    }
    return arr;
  }

  const checkedKeys = ref<string[]>([]);
  let checkedBinds = ref<BindId[]>([]);

  function initChecked(dataIds: BindId[]) {
    checkedKeys.value = [];
    checkedKeys.value = dataIds?.map((item) => item.dataId);
    checkedBinds.value = dataIds;
  }

  const handleCheck = (keys: string[], { checkedNodes }) => {
    checkedBinds.value = checkedNodes
      .filter((item) => !item.props.dataRef.children)
      .map((item) => {
        const { id, name } = item.props.dataRef;
        return {
          dataId: id,
          name,
        } as BindId;
      });
  };

  function checkedHandleOk(emit: BindModalEmit) {
    emit('chooseId', {
      ids: checkedBinds.value,
      isTime: props.config.isTime,
      isYCategory: props.config.isYCategory,
    });
    emit('update:visible', false);
  }

  return {
    binds,
    treeleafs,
    queryBinds,
    checkedKeys,
    handleCheck,
    checkedHandleOk,
    initChecked,
    checkedBinds,
  };
}
