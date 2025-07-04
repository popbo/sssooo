<template>
  <div>
    <div v-for="(zitem, zindex) in displayData" :key="zindex">
      <div
        class="switchListBox"
        :style="{
          '--openSliderColor':option.openSliderColor,
          '--openBorderColor':option.openBorderColor,
          '--openBorderNumber':option.openBorderNumber + 'px',
          '--closeSliderColor':option.closeSliderColor,
          '--closeBorderColor':option.closeBorderColor,
          '--closeBorderNumber':option.closeBorderNumber + 'px',
          '--inactiveColor': option.closeBackgroundColor,
          '--borderColor': option.borderColor,
          '--borderNumber': option.borderNumber + 'px',
          '--openTitleColor': option.openTitleColor,
          '--openTitleFont': option.openTitleFont + 'px',
          '--openTitleFamily': option.openTitleFamily,
          '--closeTitleColor': option.closeTitleColor,
          '--closeTitleFont': option.closeTitleFont + 'px',
          '--closeTitleFamily': option.closeTitleFamily,
          '--openColor': option.openBackgroundColor,
        }"
      >
        <div
          class="switchListBox-content"
          v-for="(item, index) in zitem"
          :key="index"
          :class="
            option.direction === 1
              ? 'switchListBox-transverse'
              : 'switchListBox-longitudinal'
          "
          :style="{
            marginLeft: option.spacingleft + 'px',
            marginRight: option.spacingRight + 'px',
            marginTop: option.spacingUpper + 'px',
            marginBottom: option.spacinglower + 'px',
          }"
        >
          <el-switch
            v-model="item.value.current"
            :active-color="option.openBackgroundColor"
            width="66"
            class="switchListBox-switch"
            @change="e => changetrainstation(e, item)"
          >
          </el-switch>
          <div
            class="switchListBox-title"
            :style="{
              color: option.color,
              fontSize: option.fontSize + 'px',
              fontFamily: option.fontFamily,
            }"
          >
            {{ item.label }}
          </div>
        </div>
      </div>
      <div></div>
    </div>
  </div>
</template>
<script>
import create from '../../create'
import components from '@/components/'
import common from '@/config'
import { getFunction } from '@/utils/utils.min.js'
import request from '@/axios'
export default create({
  name: 'switchMultiple',
  inject: ['contain', 'container'],
  provide() {
    return {
      contain: this.contain,
      container: this.container,
    }
  },
  data() {
    return {
      common: common,
      getFunction: getFunction,
      eventInfo: {},
      contentStyle: {},
      styleName: {},
      displayData: [],
      switchData: [],
    }
  },
  props: {
    option: Object,
    component: Object,
  },
  watch: {
    dataChart: {
      handler() {
        console.log('dataChart==>', this.dataChart)
        this.switchData.splice(0, this.switchData.length)
        if (this.dataChart.code == 200) {
          this.deepClone(this.dataChart.data).map(item => {
            item.st = new Date().getTime()
            this.switchData.push(item)
            return item
          })
          this.getDisplayData()
          // console.log("switchData==>", this.switchData)
        } else {
          this.deepClone(this.dataChart).map(item => {
            item.st = new Date().getTime()
            this.switchData.push(item)
            return item
          })
          // console.log("switchData==>", this.switchData)
          this.getDisplayData()
        }
      },
      deep: true,
    },
    'option.number': {
      handler(val) {
        if (val > 0) {
          this.getDisplayData()
        }
      },
    },
    'option.openTitle': {
      handler() {
        this.$nextTick(() => {
          $('.el-switch__core').attr({
            'data-attr': this.option.closeTitle,
            'data-attr1': this.option.openTitle,
          })
        })
      },
    },
    'option.closeTitle': {
      handler() {
        this.$nextTick(() => {
          $('.el-switch__core').attr({
            'data-attr': this.option.closeTitle,
            'data-attr1': this.option.openTitle,
          })
        })
      },
    },
  },
  methods: {
    getDisplayData() {
      let b = []
      let num = this.option.number
      let f
      for (let i = 0; i < this.switchData.length; i = i + num) {
        let c = i + num
        if (c < this.switchData.length) {
          f = this.switchData.slice(i, c)
        } else {
          f = this.switchData.slice(i, this.switchData.length)
        }
        b.push(f)
      }
      this.displayData = b
      console.log('this.displayData', this.displayData)
      this.$nextTick(() => {
        $('.el-switch__core').attr({
          'data-attr': this.option.closeTitle,
          'data-attr1': this.option.openTitle,
        })
      })
    },
    changetrainstation(e, item) {
      if (e) {
        this.updateClick(item, 'openClickFormatter')
      } else {
        this.updateClick(item, 'closeClickFormatter')
      }
      // console.log("option==>", this.option)
      // console.log("item==>",e, item)
      // // if (this.option.auth == 'Basic Auth' && !this.token) {
      // this.getToken({ authenticationUrl: this.option.authUrl, userName: this.option.name, password: this.option.passWorld }).then((res) => {
      //   let token = 'Bearer ' + res.data.token
      //   let dataHeaderWithToken = Object.assign({}, {
      //     'X-Authorization': token,
      //   })
      //   console.log("11111->", item)
      //   let params = {
      //     "method": e?item.event.openKey:item.event.closeKey,
      //     "params": { value: e?item.event.openValue:item.event.closeValue, ts: new Date().getTime() }
      //   }
      //   console.log("params", params)
      //   request({
      //     method: this.option.method,
      //     url: this.option.URL + "/" + item.deviceId,
      //     data: params,
      //     // params: params,
      //     headers: dataHeaderWithToken,
      //   }).then((res) => {
      //     // detail(res)
      //   }) // 这里走的是mockjs的数据所以看不到请求
      // })
      // }
      // let opt = {
      //   URL: this.option.URL,
      //   method: this.option.method
      // }
      // if (e) {
      //   //  开启时请求
      //   opt[item.event.openKey] = item.event.openValue;
      // } else {
      //   //  关闭时请求
      //   opt[item.event.closeKey] = item.event.closeValue;
      // }
      // this.requestFn(opt)
    },
    requestFn(opt) {
      console.log('opt==>', opt)
      request({
        url: opt.URL,
        method: opt.method,
        params: opt.data,
        data: opt.data,
      })
    },
    // 有的认证接口需要获取token
    getToken(
      authObj = {
        authenticationUrl: authObj.authenticationUrl,
        userName: authObj.userName,
        password: authObj.password,
      }
    ) {
      return new Promise((resolve, reject) => {
        try {
          let authenticationUrl = authObj.authenticationUrl
          let userInfo = {
            password: authObj.password,
            username: authObj.userName,
            loginType: 0,
          }
          // 先获取认证的token
          const res = request({
            method: 'post',
            url: authenticationUrl,
            data: userInfo,
          })
          resolve(res)
        } catch (error) {
          // this.$message.error(error);
          console.log(error)
        }
      })
    },
  },
  components: components,
  beforeDestroy() {},
})
</script>
<style lang="scss" scoped>
/deep/.el-switch__core:after {
  content: attr(data-attr);
  background-color: var(--closeSliderColor) !important;
  display: inline-block;
  color: var(--closeTitleColor);
  font-size: var(--closeTitleFont);
  font-family: var(--closeTitleFamily);
  text-align: center;
  line-height: 22px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  width: 22px !important;
  height: 22px !important;
  top: 1px !important;
}

/deep/.el-switch.is-checked .el-switch__core:after {
  content: attr(data-attr1);
  color: var(--openTitleColor);
  font-size: var(--openTitleFont);
  font-family: var(--openTitleFamily);
  background-color: var(--openSliderColor) !important;
}
/deep/ .el-switch.is-checked .el-switch__core::after {
  margin-left: -22.5px;
}
/deep/ .el-switch__core {
  background-color: var(--inactiveColor) !important;
  border: var(--closeBorderNumber) solid var(--closeBorderColor) !important;
  height: 26px !important;
  border-radius: 25px;
}
/deep/.el-switch.is-checked .el-switch__core {
  background-color: var(--openColor) !important;
  border: var(--openBorderNumber) solid var(--openBorderColor) !important;
}
.switchListBox-longitudinal {
  /deep/.el-switch__core:after {
    transform: rotate(90deg);
  }
}
.switchListBox {
  width: 100%;
  height: 100%;
  display: flex;
  // flex-direction: row;
  // flex-wrap: wrap;
  // justify-content: space-between;
  align-content: flex-start;
  .switchListBox-content {
    position: relative;
    text-align: center;
    align-items: center;
  }
  &-title {
    color: #fff;
    text-align: center;
  }
  &-switch {
    transform: rotate(-90deg) translateX(-30%);
    // height: 52px!important;
  }
  .switchListBox-title {
    margin-top: 42px;
  }
}
.switchListBox-transverse {
  display: flex;
  height: 30px;
  .switchListBox-switch {
    transform: rotate(0deg);
    // height: 52px!important;
  }
  .switchListBox-title {
    // position: relative;
    height: 30px;
    line-height: 30px;
    // left: 7px;
    padding-left: 7px;
    margin-top: 0;
  }
}
</style>
