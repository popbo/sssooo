  // 获取当前日期所在月的第一天的日期
  export const getCurrentMonthFirst = function(date) {
    date.setDate(1)
    let month = parseInt(date.getMonth() + 1)
    let day = date.getDate()
    if (month < 10) {
      month = '0' + month
    }
    if (day < 10) {
      day = '0' + day
    }
    return date.getFullYear() + '-' + month + '-' + day + ' ' + '00:00:00'
  }                     
  // 获取当前日期所在月的最后一天日期
  export const getCurrentMonthLast = function (date) {
    let currentMonth = date.getMonth()
    let nextMonth = ++currentMonth
    let nextMonthFirstDay = new Date(date.getFullYear(), nextMonth, 1)
    let oneDay = 1000 * 60 * 60 * 24
    let lastTime = new Date(nextMonthFirstDay - oneDay)
    let month = parseInt(lastTime.getMonth() + 1)
    let day = lastTime.getDate()
    if (month < 10) {
      month = '0' + month
    }
    if (day < 10) {
      day = '0' + day
    }
    return date.getFullYear() + '-' + month + '-' + day + ' ' + '23:59:59'
}
// 获取当前日期所在周的周一天的日期
  export const getCurrentWeekFirst = function (date) {
    let nowTime = date.getTime()
    let day = date.getDay()
    let oneDayTime = 24 * 60 * 60 * 1000
    //显示周一
    let MondayTime = nowTime - (day - 1) * oneDayTime
    let monday = new Date(MondayTime)
    function add0(m) {
      return m < 10 ? '0' + m : m
    }
    //shijianchuo是整数，否则要parseInt转换
    let y = monday.getFullYear()
    let m = monday.getMonth() + 1
    let d = monday.getDate()
    //let h = time.getHours();
    //let mm = time.getMinutes();
    //let s = time.getSeconds();
    return y + '-' + add0(m) + '-' + add0(d) + ' ' + '00:00:00'
    //return y+''+add0(m)+''+add0(d)+''+add0(h)+':'+add0(mm)+':'+add0(s);
}
  // 获取当前日期所在周的周日的日期
  export const getCurrentWeekLast = function (date) {
    let nowTime = date.getTime()
    let day = date.getDay()
    let oneDayTime = 24 * 60 * 60 * 1000
    //显示周日
    let SundayTime = nowTime + (7 - day) * oneDayTime
    //初始化日期时间
    let sunday = new Date(SundayTime)
    function add0(m) {
      return m < 10 ? '0' + m : m
    }
    //shijianchuo是整数，否则要parseInt转换
    let y = sunday.getFullYear()
    let m = sunday.getMonth() + 1
    let d = sunday.getDate()
    return y + '-' + add0(m) + '-' + add0(d) + ' ' + '23:59:59'
}
  // 格式化日期
  export const dateFormat = function (fmt, date) {
    let ret;
    const opt = {
        "Y+": date.getFullYear().toString(),        // 年
        "m+": (date.getMonth() + 1).toString(),     // 月
        "d+": date.getDate().toString(),            // 日
        "H+": date.getHours().toString(),           // 时
        "M+": date.getMinutes().toString(),         // 分              
        "S+": date.getSeconds().toString()          // 秒
        // 有其他格式化字符需求可以继续添加，必须转化成字符串
    };
    for (let k in opt) {
        ret = new RegExp("(" + k + ")").exec(fmt);
        if (ret) {
            fmt = fmt.replace(ret[1], (ret[1].length == 1) ? (opt[k]) : (opt[k].padStart(ret[1].length, "0")))
        };
    };
    return fmt;
  }