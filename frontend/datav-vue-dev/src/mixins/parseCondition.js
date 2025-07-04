export default {
  computed: {
    // 根据文本框的值和配置的渲染告警条件计算出符合的条件项
    coincidentCondition() {
      const index = this.parseCondition(this.drawConditionList)
      return this.drawConditionList[index]
    },
  },
  methods: {
    parseCondition(arr) {
      // 思路是在arr条件中每个条件项都是或的关系，因此要筛选出那个dataChart.value满足的条件
      // coincidentindex保存满足的条件的index值
      let coincidentindex
      // 首先判断是否配置了渲染条件，如果配置了那么渲染条件的数组长度大于0
      if (arr.length > 0) {
        for (let i = 0; i < arr.length; i++) {
          // 首先判断一下条件项有没有配置分支条件，即filter数组长度是否大于0
          //
          let filter = arr[i].filter
          if (filter.length > 0) {
            let logic = arr[i].logic // 条件分支之间的关系是且还是或  and 且  or 或
            let enterValue = arr[i].enterValue // 表达式的输入值是否是dataChart.value
            let filterResult = this.handleFilter(filter, logic, enterValue, this.dataChart.value)
            if (filterResult) {
              coincidentindex = i
              break
            }
          }
        }
        return coincidentindex
      } else {
        // 没有配置渲染条件数组
      }
    },
    handleFilter(filter, logic, enterValue, chartValue) {
      const isAllNumber = function (chartValue, itemValue) {
        let num1 = Number(chartValue)
        let num2 = Number(itemValue)
        return !isNaN(num1) && !isNaN(num2)
      }
      const switchFnc = function (item) {
        let result
        switch (item.term) {
          case 'eq': // '='
            result = chartValue === item.value
            break
          case 'ne': // '!='
            result = chartValue !== item.value
            break
          case 'lt': // '<' 如果是小于号，那么要看比较的两个值是否都是数字,这个比较只限于数字的比较
            if (isAllNumber(chartValue, item.value)) {
              result = Number(chartValue) < Number(item.value)
            } else {
              result = false
            }
            break
          case 'le': // '<='
            if (isAllNumber(chartValue, item.value)) {
              result = Number(chartValue) <= Number(item.value)
            } else {
              result = false
            }
            break
          case 'gt': // '>'
            if (isAllNumber(chartValue, item.value)) {
              result = Number(chartValue) > Number(item.value)
            } else {
              result = false
            }
            break
          case 'ge': // '>='
            if (isAllNumber(chartValue, item.value)) {
              result = Number(chartValue) >= Number(item.value)
            } else {
              result = false
            }
            break
        }
        return result
      }
      // 先确定下是否是以dataChart.value作为表达式的输入值
      if (enterValue === 'dqz') {
        // 当条件分支间的关系为且
        if (logic === 'and') {
          // 如果为且的话那么就要求filter的每一项都返回true,那么可以选择数组的every方法
          const allResult = filter.every((item) => {
            return switchFnc(item)
          })
          return allResult
        } else if (logic === 'or') { // 当条件分支间的关系为否
          const allResult = filter.some((item) => {
            return switchFnc(item)
          })
          return allResult
        }
      }
    },
  }
}