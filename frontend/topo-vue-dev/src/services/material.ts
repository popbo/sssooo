import localForage from 'localforage';
// 单实例，避免冲突
const materialsStore = localForage.createInstance({
  name: 'meta2d-materials',
  storeName: 'materials',
  version: 1.0,
  description: '存储的是系统组件',
});

// subclass - subclass
export async function GetDBMaterials(subclass: string) {
  return materialsStore.getItem(subclass);
}

// subclass - subclass
// materials - the data of subclass
export function SetDBMaterials(subclass: string, materials: any[]) {
  return materialsStore.setItem(subclass, materials);
}

export function MaterialsGroup(data: any[], folders?: any[]) {
  const obj: any = {};
  if (!folders) {
    if (data.length === 0) {
      // 长度为0
      obj['Uncategorized(未分类)'] = [];
    } else {
      data.forEach((item: { folder: string }) => {
        if (!item.folder) {
          item.folder = 'Uncategorized(未分类)';
        }

        if (!obj[item.folder]) {
          obj[item.folder] = [item];
        } else {
          obj[item.folder].push(item);
        }
      });
    }
  } else {
    folders.forEach((name: string) => {
      obj[name] = [];
    });
    obj['Uncategorized(未分类)'] = [];
    data.forEach((item: { folder: string }) => {
      if (!obj[item.folder]) {
        obj['Uncategorized(未分类)'].push(item);
      } else {
        obj[item.folder].push(item);
      }
    });
  }

  const list: any[] = [];
  // tslint:disable-next-line:forin
  for (const key in obj) {
    list.push({ name: key, list: obj[key], show: true });
  }

  return list;
}
