// import { le5leSwitch } from './switch';
import { lineSignal } from './lineSignal';
import { AxleCounterDefault, AxleCounterDefault2 } from './AxleCounterDefault';
import { trainDisplay } from './trainDisplay';
import { MaEInfoShow } from './MaEInfoShow';
import { platformDisplay } from './platformDisplay';
import { platformDisplayUnder } from './platformDisplayUnder';
import { daoCha,daoChaRight,daoChaDwon,daoChaRightDwon } from './daoCha';
import {crossDaocha} from './crossDaocha'
import { floodGate } from './floodGate';
import { zhanTaiMen } from './zhanTaiMen';
import { emergencyStop, skipStop, detainCar } from './emergencyStop';
import { qianYinGongDian } from './qianYinGongDian';
export function customEle() {
  return {
    lineSignal,
    AxleCounterDefault,
    AxleCounterDefault2,
    trainDisplay,
    MaEInfoShow,
    platformDisplay,
    platformDisplayUnder,
    daoCha,
    daoChaRight,
    daoChaDwon,
    daoChaRightDwon,
    crossDaocha,
    floodGate,
    zhanTaiMen,
    emergencyStop,
    skipStop,
    detainCar,
    qianYinGongDian,
  };
}
