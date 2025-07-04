<template>
  <div
    :class="b()"
    ref="crosstableRef"
    :style="{ '--leftColumnWidth': option.leftColumnWidth + 'px' }"
  >
    <div class="tables" :id="id">
      <div class="tops tableHeader" ref="tops">
        <div class="topTable tableHeader" style="height: 100%">
          <div class="topTable_left">
            <svg
              style="position: absolute; top: 0; left: 0"
              width="100%"
              height="100%"
              class="tableHeader"
            >
              <line
                x1="0"
                y1="0"
                x2="100%"
                y2="100%"
                style="
                  stroke: #fff;
                  stroke-width: 1px;
                  width: 100%;
                  height: 100%;
                "
              ></line>
            </svg>
            <div class="topColHeads">
              {{
                option.colHeadName
                  ? option.colHeadName
                  : option.column.colHeads[0].fieldName
              }}
            </div>
            <div class="topRowHeads">
              {{
                option.rowHeadName
                  ? option.rowHeadName
                  : option.column.rowHeads[0].fieldName
              }}
            </div>
          </div>
          <div class="topTable_right tableHeader">
            <table
              cellpadding="0"
              cellspacing="0"
              :style="{
                styleName,
                border: 'none',
                borderSpacing: 0,
                borderCollapse: 'unset',
                minHeight: '46px',
              }"
              :border="option.border"
              class="tableHeader"
            >
              <tr>
                <td
                  v-for="(item, index) in option.column.rowHeads"
                  :key="index"
                >
                  {{ item.value }}
                </td>
              </tr>
              <tr v-if="option.column.rowHeads[0].child.length > 0">
                <td
                  v-for="(items, index) in option.column.rowHeads"
                  :key="index"
                >
                  <table cellpadding="0" cellspacing="0">
                    <tr>
                      <td v-for="(itemss, index) in items.child" :key="index">
                        {{ itemss.value }}
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
            </table>
          </div>
        </div>
      </div>
      <div>
        <div class="tablesLeftHead">
          <div class="content">
            <div class="tables-container">
              <div class="container tableHeader">
                <!-- 左侧表头 -->
                <table
                  cellpadding="0"
                  cellspacing="0"
                  class="tables-head-table tableHeader"
                  :border="option.border"
                  ref="tableDataHead"
                  :style="{
                    styleName,
                    height: '100%',
                    border: 'none',
                    width: option.leftColumnWidth + 'px',
                  }"
                >
                  <tr
                    v-for="(item, index) in colHead.slice(startIndex, endIndex)"
                    :key="index"
                  >
                    <td class="tableHeader">{{ item.value }}</td>
                    <td v-if="item.child[0].child.length > 0">
                      <table
                        class="tableHeader"
                        style="height: 100%; width: 100%"
                      >
                        <tr v-for="(items, index) in item.child" :key="index">
                          <td>
                            {{ items.value }}
                          </td>
                        </tr>
                      </table>
                    </td>
                    <td>
                      <div
                        v-if="item.child[0].child.length > 0"
                        style="
                          display: flex;
                          flex-direction: column;
                          height: 100%;
                        "
                      >
                        <div class="tableHeader tableLeftCenter">
                          <div
                            v-for="(itemss, index) in item.child[0].child"
                            :key="index"
                          >
                            <p>{{ itemss.value }}</p>
                          </div>
                        </div>
                        <div
                          class="tableHeader tableLeftCenter"
                          v-if="item.child[1]"
                        >
                          <div
                            v-for="(itemss, index) in item.child[1].child"
                            :key="index"
                          >
                            <p>{{ itemss.value }}</p>
                          </div>
                        </div>
                      </div>

                      <table
                        cellpadding="0"
                        cellspacing="0"
                        v-else
                        class="tableHeader"
                        style="width: 100%; height: 100%"
                      >
                        <tr v-for="(itemss, index) in item.child" :key="index">
                          <td>{{ itemss.value }}</td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
                <!-- 数据区 -->
                <table
                  :border="option.border"
                  ref="tableData"
                  :style="{
                    background: option.tableDataBackground,
                    color: option.tableDataColor,
                  }"
                  cellpadding="0"
                  cellspacing="0"
                  class="tableData"
                >
                  <tr
                    ref="tableDataHr"
                    v-for="(item, index) in dataChart.slice(
                      this.startIndex * this.baseVal,
                      this.endIndex * this.baseVal
                    )"
                    :key="index"
                  >
                    <td v-for="(items, index) in item" :key="index">
                      {{ items }}
                    </td>
                  </tr>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="pagination-box" v-show="option.paging">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[option.maxRows]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        >
        </el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
import create from '../../create'
import { uploadCss, obtainCss } from '@/api/visual.js'

export default create({
  name: 'crosstable',
  inject: ['main'],
  data() {
    return {
      heights: '',
      heightss: '',
      heightHead: '',
      scrollCheck: '',
      currentPage: 1,
      total:0,
      pageTotal: 8,
      pageNum: 1,
      pageSize: 5,
      startIndex: 0,
      endIndex: 5,
      spanArr: [],
      pos: '',
      baseVal: 1,
      idinnerHTML: 1,
      coldata: [],
      leftColumnWidth: '',
      colHead:[]
    }
  },
  watch: {
    // dataChart: {
    //   handler(newValue) {
    //     console.log('dataChart改变', newValue)
    //   },
    //   deep: true,
    //   immediate: true,
    // },
    option: {
      handler() {
        this.css()
        if (this.option.column.colHeads[0].child[0].child.length) {
          this.baseVal =
            this.option.column.colHeads[0].child.length *
            this.option.column.colHeads[0].child[0].child.length
        } else {
          this.baseVal = this.option.column.colHeads[0].child.length
        }
        this.colHead = this.option.column.colHeads;
        this.total = this.pageTotal/this.baseVal
        if (this.option.paging) {
          if (this.option.maxRows) {
            this.pageSize = this.option.maxRows
            this.handleCurrentChange(1)
          }
          if (this.option.maxSize) {
            this.total = this.option.maxSize
            if(this.total < this.pageSize){
              this.pageSize = this.total 
              this.handleCurrentChange(1)
            }
            if( this.total > (this.pageTotal/this.baseVal) ){
              this.total = this.pageTotal/this.baseVal
            }
          } else {
            this.colHead = this.option.column.colHeads
            this.dataChart = this.dataChart
          }
        } else {
          if (this.option.maxSize) {
            this.colHead = this.option.column.colHeads
            this.dataChart = this.dataChart
            this.startIndex = 0
            this.endIndex = this.option.maxSize
          } else {
            this.colHead = this.option.column.colHeads
            this.dataChart = this.dataChart
            this.startIndex = 0
            this.endIndex = 1000000
          }
        }
      },
      deep: true,
      immediate:true
    },
  },
  computed: {
    styleName() {
      const obj = Object.assign(
        {},
        {
          borderSize: `${this.option.borderSize}px`,
          borderColor: `#ccc`,
        }
      )
      return obj
    },
  },
  props: {
    option: {
      type: Object,
      default: () => {
        return {}
      },
    },
  },
  created() {
    this.css()
  },
  mounted: function () {},
  methods: {
    handleSizeChange(value) {
      this.pageSize = value
      this.startIndex = (this.pageNum - 1) * this.pageSize
      this.endIndex = this.pageNum * this.pageSize
    },
    handleCurrentChange(value) {
      this.pageNum = value
      this.startIndex = (this.pageNum - 1) * this.pageSize
      this.endIndex = this.pageNum * this.pageSize
    },
    // 计算左侧表头宽度
    // calcLeftColumnWidth() {
    //   if (this.$refs.crosstableRef) {
    //     const width = parseInt(this.$refs.crosstableRef.style.width)
    //     this.leftColumnWidth = isNaN(width) ? '' : `${width * 0.5}px`
    //   }
    // },
    /*   getSpanArr(data) {
        //是否使用2维数组
        //合并完成后 单元格位置变化
        this.spanArr = [];
        this.pos = 0;
        let eleArr= [];
        //拿到对象的 key值
        for(let ele in data[0]){
          eleArr.push(ele)
        }
        for (var i = 0; i < data.length; i++) {
          if (i === 0) {
            //设置table表格行号、设置合并参数，以便相同参数合并
            this.spanArr.push(1);
            this.pos = 0;
          } else {
            eleArr.map(item=>{
            if (data[i][`${item}`] === data[i-1][`${item}`]) {
              this.spanArr[this.pos] += 1;
              this.spanArr.push(0);
            }else{
              this.spanArr.push(1);
              this.pos = i;
            }
            })
          }
        }
      }, */

    async css() {
      let id = this.id.substring(4, this.id.length)
      let { border, borderColor, borderSize } = this.option
      let borders = border ? borderSize : ''
      borderColor = borderColor ? borderColor : '#ccc'

      let style = `
        #${
          'item' + id
        } .tables .tables-container .container .tables-head-table>tr>td {
        border-width:${borders}px;
        border-color:${borderColor};
        border-right:none !important;
       }
        #${'item' + id} .tables-container .tableData>tr>td{
        font-size:${this.option.tableDateSize}px !important;
        border-width:${borders}px;
        border-color:${borderColor};
        }
       table, .topTable_right>table{
          border-collapse:collapse;
          border-spacing: 0;
          border-collapse: unset;
        }
       #${'item' + id} .topTable_right>table>tr>td,#${
        'item' + id
      } .topTable .topTable_left{
          border-width:${borders}px !important;
          border-color:${borderColor} !important;
          border-bottom: none !important;
        }
        #${'item' + id} .topTable .topTable_left{
            border-right: none !important;
        }
        #${'item' + id} .topTable .topTable_left svg line{
          stroke:${borderColor} !important;
          stroke-width: ${borders}px !important;
        }
       #${'item' + id} .topTable_right>table>tr>td table tr td{
          border:none;
        }
          #${'item' + id} .pagination-box .el-pagination{
            background: ${this.option.paginationbgcolr} !important;
          }
          #${'item' + id} .tables-container .tableData tr td{
          font-size:${this.option.tableDateSize}px !important;
        }
       #${'item' + id} .tables .tableHeader{
          font-size: ${this.option.tableHeaderSize}px !important;
          color: ${this.option.tableHeadercolor} !important;
          background-color: ${this.option.tableHeaderbgcolor} !important;
          text-align: ${this.option.tableHeaderTextAlign} !important;
        }
           #${'item' + id} .tables-container .tableData tr:nth-child(odd)  {
            background-color:${this.option.nthColor} !important;
        }
          #${'item' + id} .tables-container .tableData tr:nth-child(even) {
              background-color:${this.option.othColor} !important;
          }
          #${'item' + id} .tables-container .tableData tr  {
              border-botton: ${this.option.transverseColor} ;
              border-top: ${this.option.transverseColor} ;
              border-left: ${this.option.portraitColor} ;
              border-right: ${this.option.portraitColor};
          }
        `
      //提交最新修改CSS
      await uploadCss(id, style)
      //获取css
      obtainCss(id).then(resq => {
        let newStyle = document.createElement('style')
        newStyle.type = 'text/css'
        newStyle.innerHTML = `${resq.data.data}`
        document.getElementsByTagName('head')[0].appendChild(newStyle)
      })
    },
  },
})
</script>
<style scoped lang="scss">
.tables {
  width: 100%;
  height: 100%;
  position: relative;
  box-sizing: content-box;
  .tablesLeftHead {
    // height:90%;
    overflow: hidden;
    overflow-y: auto;
    scrollbar-width: none;
    &::-webkit-scrollbar {
      display: none;
    }
  }
  .tableHeader {
    color: rgba(0, 198, 255, 1);
    box-sizing: border-box;
    border-collapse: collapse;
    border-spacing: 0;
    border-collapse: unset;
  }
  .content {
    height: calc(100% - 20px);
    background: transparent;
  }

  .tables-container {
    display: flex;
    overflow: auto;
    position: relative;
    scrollbar-width: none;
    &::-webkit-scrollbar {
      display: none;
      width: 0 !important;
      overflow: -moz-scrollbars-none;
      -ms-overflow-style: none;
    }
    .container {
      display: flex;
      .tables-head-table {
        width: 50%;
        height: 100%;
        // table-layout: fixed;
        text-align: center;
        color: rgba(0, 198, 255, 1);
        tr {
          box-sizing: content-box;
          td {
            box-sizing: content-box;
            overflow: hidden;
            width: 100px;
          }
        }
      }

      .tableData {
        border: none;
        width: calc(100% - var(--leftColumnWidth));
        table-layout: fixed;
        text-align: center;
        tr {
          box-sizing: content-box;
          td {
            box-sizing: content-box;
            width: 30px;
            overflow: hidden;
          }
        }
      }
    }
  }

  .topTable {
    display: flex;
    height: 20%;
    .topTable_right {
      width: calc(100% - var(--leftColumnWidth));
      // width: 50%;
      & > table {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        table-layout: fixed;
        tr {
          td {
            table {
              width: 100%;
              height: 100%;
              text-align: center;
            }
          }
        }
      }
    }

    .topTable_left {
      width: var(--leftColumnWidth);
      // width: 50%;
      border: 1px solid #fff;
      position: relative;
      border-bottom: none;
      box-sizing: border-box;
      // background-color: rgba(48, 158, 248, 0.1);
      .topColHeads {
        position: absolute;
        top: 40%;
        left: 10%;
      }

      .topRowHeads {
        position: absolute;
        top: 20%;
        right: 10%;
      }
    }
  }

  .pagination-box {
    display: flex;
    height: 32px;
    justify-content: flex-end;
    // position: absolute;
    // bottom: -4px;
    // left: 0;
  }

  .topRight {
    position: relative;
  }
  .tableLeftCenter {
    display: flex;
    flex-direction: column;
    justify-content: space-evenly;
    height: 100%;
  }
}
</style>
