<template>
  <div class="eventLinkage">
    <el-collapse v-model="activeNames" @change="handleChange">
      <el-collapse-item title="联动" name="1">
        <el-form ref="form" :model="form" label-width="80px">
          <el-row>
            <el-col :span="12"
              ><el-form-item label="联动组件">
                <el-select
                  v-model="form.components"
                  multiple
                  collapse-tags
                  :disabled="!expansionStatus"
                  placeholder="请选择"
                >
                  <el-option
                    v-for="item in options"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  >
                  </el-option>
                </el-select> </el-form-item
            ></el-col>
            <el-col :span="12"
              ><el-form-item label="组件动作">
                <el-select
                  v-model="form.actions"
                  placeholder="请选择"
                  :disabled="!expansionStatus"
                >
                  <el-option
                    v-for="item in options"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  >
                  </el-option>
                </el-select> </el-form-item
            ></el-col>
          </el-row>
        </el-form>
        <div v-show="expansionStatus">
          <el-card class="box-card">
            <div slot="header" class="clearfix">
              <span>条件设置</span>
              <el-button style="float: right; padding: 3px 0" type="text"
                >展开</el-button
              >
            </div>
            <div></div>
          </el-card>
        </div>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        activeNames: ['1'],
        form: {
          components: [],
          actions: ""
        },
        options: [{
          value: '选项1',
          label: '黄金糕'
        }, {
          value: '选项2',
          label: '双皮奶'
        }, {
          value: '选项3',
          label: '蚵仔煎'
        }, {
          value: '选项4',
          label: '龙须面'
        }, {
          value: '选项5',
          label: '北京烤鸭'
        }],
      }
    },
    props: {
      expansionStatus: {
        type: Boolean,
        default: false
      },
    },
    created() { },

    methods: {
      handleChange(val) {
        console.log(val)
      },
    },
  }
</script>

<style scoped lang='scss'>
.eventLinkage {
  /deep/.el-collapse-item__header {
    color: #333;
    font-size: 14px;
    font-weight: 400;
    .el-icon-arrow-right {
      color: #333;
    }
  }
  /deep/ .el-collapse-item__wrap {
    background-color: #ccc;
  }
}
</style>
