<template>
  <div
    :class="b()"
    :style="{
      '--subscriptColor': option.subscriptColor,
      '--subscriptFontSize': option.subscriptFontSize + 'px',
      '--checkboxOpacity': checkboxOpacity,
      '--defaultFontColor': option.defaultFontColor,
      '--selectedFontColor': option.selectedFontColor,
      '--fontSize': option.fontSize + 'px',
    }"
  >
    <el-select
      v-model="selectValue"
      :placeholder="option.defaultShow"
      :popper-class="'pop_select_down' + this.id.substring(0, 8)"
      :class="'selectCheckBox' + this.id.substring(0, 8)"
      :style="styleSizeName"
      :multiple="option.isMultiple"
      clearable
      :filterable="option.isFilterable"
      reserve-keyword
      :filter-method="filterableHandler"
      @visible-change="selectVisibleChange"
      @change="v => selectChange(v, showAll ? dataChart : notAllOptions)"
      collapse-tags
    >
      <el-checkbox
        v-if="option.isMultiple && option.showSelectAll"
        v-model="isSelectAll"
        :indeterminate="isIndeterminate"
        class="selectbox"
        style="padding-left: 18px"
        @change="selectAllnotAllOptions"
        >{{ option.selectAllText }}</el-checkbox
      >
      <el-option
        v-for="item in showAll ? dataChart : notAllOptions"
        :key="item[opv] + '0' + Math.random()"
        :label="item[opl]"
        :value="item[opv]"
      >
        <template v-if="option.isMultiple">
          <span class="check" />
          <span>{{ item[opl] }}</span>
        </template>
      </el-option>
    </el-select>
  </div>
</template>

<script>
import create from '../../create'
export default create({
  name: 'select',
  data() {
    return {
      // 父组件的样式名称
      defaultClass: '',
      // placeholder: '',
      // 也是央视名称（因为是多人协作定义时候出现了冲突 导致同功能变量定义重复）
      className: '',
      // 因为是兼容的全局 有的页面是单独写的是style
      style: {},
      //  下拉选择器的 备选项
      dataChart: [],
      //  对应的el-options的value绑定的字段
      opv: 'value',
      // label绑定的字段
      opl: 'label',
      // 父组件传参过来的下拉备选项的初始数据（options用于子组件绑定）
      optionsOri: [],
      //  父组件原来选择器选中的的绑定值 其实和子组件的v-model保持一致
      modelOri: [],
      selectValue: null, // 当下拉选择框为多选时选择的值组成的数组
      isSelectAll: false, // 全选
      isIndeterminate: false, //
      notAllOptions: [], // 模糊查询后产生的新数据
      showAll: true, // 模糊查询后 是否显示全部备选项 默认为true
    }
  },
  watch: {
    'option.isMultiple': {
      handler(newVal) {
        this.selectValue = newVal ? [] : ''
      },
    },
    option: {
      handler() {
        this.createSelectStyle()
      },
      deep: true,
    },
    dataChart: {
      handler(newVal) {
        if (
          this.option.useFirstOption &&
          Array.isArray(newVal) &&
          newVal.length > 0
        ) {
          this.assignInitValue()
        }
      },
      deep: true,
      immediate: true,
    },
    'option.useFirstOption':{
      handler(newVal){
        if (newVal) {
          this.assignInitValue()
        } else {
          this.selectValue = this.option.isMultiple ? [] : ''
        }
      },
      deep:true,
    },
  },
  created() {
    // 当下拉选择框为多选时选择的值为数组，单选时为字符串
    this.selectValue = this.option.isMultiple ? [] : ''
  },
  mounted() {
    this.createSelectStyle()
  },
  computed: {
    checkboxOpacity() {
      if (Array.isArray(this.selectValue) && this.selectValue.length === 0) {
        return 0
      } else {
        return 1
      }
    },
  },
  methods: {
    // 根据模糊查询结果 全选
    selectAllnotAllOptions(v) {
      if (this.showAll) {
        this.selectValue = v ? this.dataChart.map(item => item[this.opv]) : []
      } else {
        if (v) {
          this.notAllOptions.forEach(item => {
            this.selectValue.push(item[this.opv])
          })
          this.selectValue = [...new Set(this.selectValue)]
        } else {
          let listData = this.notAllOptions.map(item => item[this.opv])
          let selectValue = this.deepClone(this.selectValue)
          for (let i = 0; i < listData.length; i++) {
            for (let b = 0; b < this.selectValue.length; b++) {
              if (listData[i] === this.selectValue[b]) {
                selectValue.splice(b, 1)
              }
            }
          }
          this.selectValue = this.deepClone(selectValue)
        }
      }
      // let opArr = this.showAll ? this.dataChart : this.notAllOptions
      // this.selectValue = v ? opArr.map(item => item[this.opv]) : []
      this.isIndeterminate = false
      if (this.selectValue.length > 0) {
        this.updateClick(
          {
            arrayValue: this.selectValue,
            value: this.selectValue.join(),
            termParams: this.option.isMultiple ? 'in' : 'eq',
          },
          'clickFormatter'
        )
      } else {
        this.updateClick({}, 'clickFormatter')
      }
    },
    filterableHandler(value) {
      console.log('filterableHandler==>调用')
      if (value) {
        this.showAll = false
        this.notAllOptions = []
        this.notAllOptions = this.dataChart.filter(item => {
          if (this.option.ignoreCase) {
            return (
              item[this.opv].indexOf(value) > -1 ||
              item[this.opl].indexOf(value) > -1
            )
          } else {
            return (
              item[this.opv].toUpperCase().indexOf(value.toUpperCase()) > -1 ||
              item[this.opl].toUpperCase().indexOf(value.toUpperCase()) > -1
            )
          }
        })
        if (this.option.isMultiple) {
          if (this.selectValue.length === 0) {
            this.isSelectAll = false
            this.isIndeterminate = false
          } else {
            let listData = []
            this.notAllOptions.forEach(item => {
              listData.push(item[this.opv])
            })
            const commonValues = listData.filter(value =>
              this.selectValue.includes(value)
            )
            if (
              commonValues.length > 0 &&
              commonValues.length < listData.length
            ) {
              this.isIndeterminate = true
              this.isSelectAll = false
            }
            if (commonValues.length === listData.length) {
              this.isIndeterminate = false
              this.isSelectAll = true
            }
            if (commonValues.length === 0) {
              this.isIndeterminate = false
              this.isSelectAll = false
            }
          }
        }
      } else {
        if (this.option.isMultiple) {
          if (this.selectValue.length === 0) {
            this.isSelectAll = false
            this.isIndeterminate = false
          } else {
            let listData = []
            this.dataChart.forEach(item => {
              listData.push(item[this.opv])
            })
            const commonValues = listData.filter(value =>
              this.selectValue.includes(value)
            )
            if (
              commonValues.length > 0 &&
              commonValues.length < listData.length
            ) {
              this.isIndeterminate = true
              this.isSelectAll = false
            }
            if (commonValues.length === listData.length) {
              this.isIndeterminate = false
              this.isSelectAll = true
            }
            if (commonValues.length === 0) {
              this.isIndeterminate = false
              this.isSelectAll = false
            }
          }
        }
        this.showAll = true
      }
    },
    assignInitValue() {
      this.selectValue = this.option.isMultiple
        ? [this.dataChart[0].value]
        : this.dataChart[0].value
      this.updateClick(
      { 
            arrayValue: this.selectValue,
            value: this.dataChart[0].value,
            termParams: this.option.isMultiple ? 'in' : 'eq',
      },'contentChangeFormatter')
    },
    // visible-change事件
    selectVisibleChange(v) {
      if (v === false) {
        this.showAll = true
      }
      // this.selectCheck(
      //   this.selectValue,
      //   this.showAll ? this.dataChart : this.notAllOptions
      // );
    },
    // change事件
    selectChange(v, arr) {
      // 选中 对应的复选框更改状态，全选根据当前更改状态
      this.selectCheck(v, arr)
    },
    // 根据选中数据 更新全选框状态
    selectCheck(v, arr) {
      console.log('selectCheck', v, arr)
      if (this.option.isMultiple && arr.length !== 0) {
        let listData = []
        arr.forEach(item => {
          listData.push(item[this.opv])
        })
        const commonValues = listData.filter(value => v.includes(value))
        if (commonValues.length > 0 && commonValues.length < listData.length) {
          this.isIndeterminate = true
          this.isSelectAll = false
        }
        if (commonValues.length === listData.length) {
          this.isIndeterminate = false
          this.isSelectAll = true
        }
        if (commonValues.length === 0) {
          this.isIndeterminate = false
          this.isSelectAll = false
        }
      }
      if (this.option.isMultiple) {
        if (v.length > 0) {
          this.updateClick(
            {
              value: v.join(),
              arrayValue: v,
              termParams: this.option.isMultiple ? 'in' : 'eq',
            },
            'clickFormatter'
          )
        } else {
          this.updateClick({}, 'clickFormatter')
        }
      } else {
        this.updateClick(
          {
            value: v,
            arrayValue: v,
            termParams: this.option.isMultiple ? 'in' : 'eq',
          },
          'clickFormatter'
        )
      }
    },
    createSelectStyle() {
      if (
        this.$route.path.includes('view') ||
        this.$route.path.includes('share') ||
        this.$route.path.includes('build')
      ) {
        let styleText = `
        .selectCheckBox${this.id.substring(0, 8)} .el-select__tags-text {
          font-size: ${this.option.fontSize}px
        }
        .pop_select_down${this.id.substring(0, 8)} {
          background-color: ${this.option.bgColor || '#181a24'};
        }
        .pop_select_down${this.id.substring(0, 8)} .el-select-dropdown__item {
          color: ${this.option.defaultFontColor};
          font-size: ${this.option.fontSize}px;
          line-height: normal !important;
        }
        .pop_select_down${this.id.substring(
          0,
          8
        )} .el-select-dropdown__item.hover {
          color: ${this.option.hoverFontColor};
          background-color: ${this.option.selectedBgColor};
        }
        .pop_select_down${this.id.substring(
          0,
          8
        )} .el-select-dropdown__item.selected {
          color: ${this.option.selectedFontColor} !important;
          background-color: ${this.option.selectedBgColor};
        }
        `
        let el
        if (this.option.backgroundImage) {
          el = `
           .selectCheckBox${this.id.substring(0, 8)} .el-input__inner {
              border-color: ${this.option.borderColor || '#393b4a'} !important;
              font-size: ${this.option.fontSize}px;
              border-width: ${this.option.borderSize}px;
              background: url(${this.option.backgroundImage}) no-repeat;
              background-size: 100% 100%;
            }
            .selectCheckBox${this.id.substring(
              0,
              8
            )} .el-select__tags .el-select__input {
              background-color: transparent !important;
              border: 0px solid transparent;
            }
          `
          styleText = styleText + el
        } else {
          el = `
            .selectCheckBox${this.id.substring(0, 8)} .el-input__inner {
              background-color: ${this.option.bgColor || '#181a24'} !important;
              border-color: ${this.option.borderColor || '#393b4a'} !important;
              font-size: ${this.option.fontSize}px;
              border-width: ${this.option.borderSize}px;
              background:none
            }
            .selectCheckBox${this.id.substring(
              0,
              8
            )} .el-select__tags .el-select__input {
              background-color: ${this.option.bgColor || '#181a24'} !important;
            }
          `
          styleText = styleText + el
        }
        //追加css
        let styleTag = document.createElement('style')
        styleTag.type = 'text/css'
        styleTag.id = 'select' + this.id.substring(0, 8)
        styleTag.innerHTML = styleText
        document.getElementsByTagName('head')[0].appendChild(styleTag)
      }
    },
  },
})
</script>
<style lang="scss" scoped>
// 这个样式解决下拉框为多选的时候样式不正确的问题
::v-deep .el-select {
  .el-input {
    height: 100%;
    .el-input__inner {
      height: 100% !important;
    }
    .el-input__icon {
      line-height: inherit;
    }
  }
}

.el-select-dropdown.is-multiple {
  .el-select-dropdown__item {
    span {
      padding: 0;
      vertical-align: middle;
    }
    .check {
      position: relative;
      box-sizing: border-box;
      display: inline-block;
      width: 14px;
      height: 14px;
      margin-left: 18px;
      margin-right: 10px;
      border: 1px solid #656c7e;
      border-radius: 2px;
    }
  }
  .el-select-dropdown__item.selected {
    &::after {
      top: 0.45rem;
      left: 1.17rem;
      z-index: 1;
      font-weight: normal;
      color: #fff;
    }
  }
}
::v-deep .el-select .el-input .el-select__caret {
  font-size: var(--subscriptFontSize);
  color: var(--subscriptColor);
}
::v-deep .el-select__input {
  font-size: 18px;
}
// ::v-deep .el-select__tags {
//   opacity: var(--checkboxOpacity) !important;
// }
/deep/ input::-webkit-input-placeholder {
  color: var(--selectedFontColor) !important;
  font-size: var(--fontSize) !important;
}
/deep/ .el-select-dropdown__item,
.selected {
  > span {
    color: var(--selectedFontColor) !important;
  }
}
/deep/ .el-select .el-input__inner {
  color: var(--selectedFontColor) !important;
  font-size: var(--fontSize) !important;
}
</style>
