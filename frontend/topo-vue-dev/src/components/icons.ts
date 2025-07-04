import axios from '@/http';
import { parseSvg } from '@topometa2d/svg';
import {allPngsCombine} from './utils'
const market = import.meta.env.VITE_MARKET;

const normalFolder = market ? '/2d/svg/' : '/svg/';
/**
 * 请求 svg 的目录
 * @returns
 */
export async function getIconFoldersByIF() {
  return await getFolders(normalFolder);
}

export async function getFolders(folderName: string) {
  const ret = (await axios.get(folderName)) as any[];

  if (!ret || !ret.map) {
    return [];
  }
  return await Promise.all(
    ret.map(async (c: any) => {
      const files = (await axios.get(folderName + c.name + '/')) as any[];
      
      const filesCombine=allPngsCombine(files)
      return {
        name: c.name,
        show: true,
        list: [],
        count: filesCombine.length,
        // 用于区别 png 与 svg 文件夹
        svgFolder: folderName === normalFolder ? true : false,
      };
    })
  );
}

/**
 * 请求 svg 目录下的所有 svg
 * @param name 目录名
 * @returns
 */
export async function getIconListByIF(name: string) {
  const files = (await axios.get(normalFolder + name + '/')) as any[];
  return await Promise.all(files.map((f) => svgToPens(f, name)));
}

export function filename(str: string) {
  const i = str.lastIndexOf('.');
  return str.substring(0, i);
}

async function svgToPens(f: any, diretoryName: string) {
  const name = filename(f.name);
  const image = normalFolder + diretoryName + '/' + f.name;
  return {
    name,
    image, // image 只作为缩略图
    componentDatas: parseSvg(await axios.get(image)),
    component: true,
  };
}
