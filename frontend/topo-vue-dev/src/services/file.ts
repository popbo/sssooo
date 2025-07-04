import axios from '@/http';

export async function upload(
  blob: Blob,
  shared = true,
  filename = 'thumb.png',
  directory = '/2D/缩略图'
) {
  const form = new FormData();
  form.append('filename', filename);
  form.append('randomName', '1');
  // form.append('public', shared + '');
  form.append('share', shared + '');
  form.append('file', blob);
  form.append('type', '图片');
  // form.append('folderId', 'test');
  form.append('directory', directory);
  // if (folderId) {
  //   form.append('tags', ['2D', folderId].toString());
  // }
  const ret: any = await axios.post('/image/minio/upload', form);
  if (ret.error) {
    return null;
  }

  return ret;
}

export async function delImage(image: string) {
  // await axios.delete('/api' + image);
  await axios.delete(`${image}`);
  return true;
}

/**
 * 删除图片组件
 * @param image 图片路径
 * @param folder 文件夹
 */
export async function deleteImageComponent(image: string, folder?: string) {
  await axios.post('/api/user/component/image/delete', {
    folder,
    image,
  });
}

// export async function addImage(image: string) {
//   const ret: any = await axios.post('/api/user/image', { image });
//   if (ret.error) {
//     return '';
//   }

//   return ret.id;
// }

export async function addImageUrl(image: string, folder?: any) {
  let list = [];
  let folderRet: any = await axios.post('/data/folders/get', {
    query: {
      type: 'topo2d-components',
      name: folder.name,
      id: folder.folderId,//携带id
      username:localStorage.getItem('username')
    },
  });
  if (folderRet.error) {
    return;
  }
  const imgRet: any = await axios.post('/data/topo2d-onlines/add', {
    folderId: folder.id,
    folder: folder.name,
    image,
    hasFolder: true,
    type: '2D',
    categoryValue: 'topo2dnocategorykey9999999999999',
  });
  if (imgRet.error) {
    return;
  }
  list = folderRet.list;
  //TODO 不一定需要folder
  list.push({
    id: '',
    image,
    name: '',
    onlineId: imgRet._id,
    // folder,
  });
  const ret: any = await axios.post('/data/folders/update', {
    _id: folderRet._id,
    list,
    username:localStorage.getItem('username')
  });

  if (ret.error) {
    return false;
  }

  return true;
  // const ret: any = await axios.post('/api/user/component/image/', {
  //   image,
  //   folder,
  // });
  // if (ret.error) {
  //   return false;
  // }
  // return true;
}

/**
 * 读取文件内容，返回字符串
 * @param file 文件
 */
export async function readFile(file: Blob) {
  return new Promise<string>((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => {
      resolve(reader.result as string);
    };
    reader.onerror = reject;
    // readAsText 使 result 一定是字符串
    reader.readAsText(file);
  });
}

export function dataURLtoBlob(base64: string) {
  var arr = base64.split(','),
    mime = arr[0].match(/:(.*?);/)[1],
    bstr = atob(arr[1]),
    n = bstr.length,
    u8arr = new Uint8Array(n);
  while (n--) {
    u8arr[n] = bstr.charCodeAt(n);
  }
  return new Blob([u8arr], { type: mime });
}

/**
 * 图片转 Blob
 * @param img 图片
 */
export function saveToBlob(img: HTMLImageElement): Blob {
  const canvas: HTMLCanvasElement = document.createElement('canvas');
  canvas.setAttribute('origin-clean', 'false');
  canvas.width = img.width;
  canvas.height = img.height;

  const context = canvas.getContext('2d');
  context.filter = window.getComputedStyle(img).filter;
  context.drawImage(img, 0, 0, canvas.width, canvas.height);
  return dataURLtoBlob(canvas.toDataURL());
}
