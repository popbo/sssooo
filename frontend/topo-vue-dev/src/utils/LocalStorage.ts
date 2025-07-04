// src/utils/localStorage.ts
/**
 * window.localStorage 浏览器永久缓存
 */
export const localStorage = {
  // 设置永久缓存
  set(key: string, val: any) {
    window.localStorage.setItem(key, JSON.stringify(val));
  },
  // 获取永久缓存
  get(key: string) {
    const json: any = window.localStorage.getItem(key);
    return JSON.parse(json);
  },
  // 移除永久缓存
  remove(key: string) {
    window.localStorage.removeItem(key);
  },
  // 移除全部永久缓存
  clear() {
    window.localStorage.clear();
  }
};
 
// 通信数据保存
// httpDate
const httpDate = 'httpDate';
export function getHttpDate() {
  return localStorage.get(httpDate);
}
 
export function setHttpDate(httpDateStatus: {}) {
  localStorage.set(httpDate, httpDateStatus);
}


// websocket
const websocketDate = 'websocketDate';
export function getWebsocketDate() {
  return localStorage.get(websocketDate);
}
 
export function setWebsocketDate(websocketDateStatus: {}) {
  localStorage.set(websocketDate, websocketDateStatus);
}
 
// mqttData
const mqttDate = 'mqttData';
 
export function getMqttDate() {
  return localStorage.get(mqttDate);
}
 
export function setMqttDate(mqttDateStatus: {}) {
  localStorage.set(mqttDate, mqttDateStatus);
}