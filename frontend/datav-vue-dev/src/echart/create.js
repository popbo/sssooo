import common from './common';
// import common from './common.min';
import bem from './bem';
import { KEY_COMPONENT_NAME } from './variable';
export default function (sfc) {
  sfc.name = KEY_COMPONENT_NAME + sfc.name;
  sfc.mixins = sfc.mixins || [];
  sfc.mixins.push(bem);
  sfc.mixins.push(common);
  return sfc;
}
