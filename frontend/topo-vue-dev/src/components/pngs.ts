import axios from '@/http';
import { filename, getFolders } from './icons';
const market = import.meta.env.VITE_MARKET;

const normalFolder = market ? '/2d/png/' : '/png/';
/**
 * 请求 png 的目录
 * @returns
 */
export async function getPngFolders() {
  return await getFolders(normalFolder);
}

/**
 * 请求 png 目录下的所有 png
 * @param name 目录名
 * @returns
 */
export async function getPngs(name: string) {
  const files = (await axios.get(normalFolder + name + '/')) as any[];
  if (!files || !files.map) {
    return [];
  }
  return files.map((f) => ({
    name: filename(f.name),
    image: normalFolder + name + '/' + f.name,
  }));
}
