import { t } from '@/i18n/i18n';

export function isVip(user: User, day = 0) {
  if (!user || !user.vip) {
    return false;
  }

  const now = new Date().getTime();
  const date = new Date(user.vipExpiry || user.vip).getTime();
  return (date - now) / (1000 * 60 * 60 * 24) + day >= 0;
}

export const noLoginTip = t('请先登录，否则无法保存!');

export function isLogin(user: User) {
  if (!user || !user.username) {
    return false;
  }
  return true;
}

export interface User {
  avatarUrl: string;
  captcha: string;
  createdAt: string | Date;
  deletedAt: string | Date;
  email: string;
  id: string;
  phone: string;
  updatedAt: string | Date;
  username: string;
  vip: number;
  vipExpiry: string | Date;
  isVip: boolean; // 该属性是计算出来的，不是数据库中的
  categoryValue: string; //该属性是根据当前查看图纸来的
  categoryKey: string; //该属性是根据当前查看图纸来的
  component: boolean; // 判断是否'我的组件'
}

function getHabitsStoreName(user: User) {
  let habitsStoreName = 't-material-group-show';
  if (user && user.username) {
    habitsStoreName += '-' + user.id;
  }
  return habitsStoreName;
}

export type ShowGroupsType = { [name: string]: boolean };
export function getShowGroups(user: User): ShowGroupsType {
  const habitsStoreName = getHabitsStoreName(user);
  return JSON.parse(localStorage.getItem(habitsStoreName)) || {};
}

export function setShowGroups(user: User, showGroups: ShowGroupsType) {
  const habitsStoreName = getHabitsStoreName(user);
  localStorage.setItem(habitsStoreName, JSON.stringify(showGroups));
}
