<template>
  <layout-content :header="formType == 'add' ? '新建数据源' : '编辑数据源'">
    <template v-slot:header>
      <el-icon name="back" class="back-button" @click.native="backToList" />
      {{
        params &&
        params.id &&
        params.showModel &&
        params.showModel === 'show' &&
        !canEdit
          ? '数据源信息'
          : formType == 'add'
          ? copyModal
            ? '复制数据源'
            : '新建数据源'
          : '编辑数据源'
      }}
    </template>
    <div>
      <el-form
        ref="dsForm"
        :model="form"
        :rules="rule"
        size="small"
        :disabled="
          params &&
          params.id &&
          params.showModel &&
          params.showModel === 'show' &&
          !canEdit
        "
        label-width="180px"
        label-position="right"
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model.trim="form.name" autocomplete="off" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" autocomplete="off" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select
            v-model="form.type"
            placeholder="请选择数据源类型"
            class="select-width"
            :disabled="
              formType == 'modify' ||
              (formType === 'add' && params && !!params.type)
            "
            @change="changeType()"
          >
            <el-option
              v-for="item in allTypes"
              :key="item.name"
              :label="item.label"
              :value="item.name"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="驱动" prop="customDriver">
          <el-select
            v-model="form.configuration.customDriver"
            placeholder="请选择驱动"
            class="select-width"
          >
            <el-option
              v-for="item in customDrivers"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item
          v-if="form.configuration.dataSourceType == 'jdbc'"
          label="主机名/IP地址"
          prop="configuration.host"
        >
          <el-input v-model="form.configuration.host" autocomplete="off" />
        </el-form-item>
        <el-form-item
          v-if="form.configuration.dataSourceType == 'es'"
          label="地址"
          prop="configuration.url"
        >
          <el-input
            v-model="form.configuration.url"
            placeholder="请输入 Elasticsearch 地址，如: http://es_host:es_port"
            autocomplete="off"
          />
        </el-form-item>
        <el-form-item
          v-if="form.configuration.dataSourceType == 'jdbc'"
          label="数据库名称"
          prop="configuration.dataBase"
        >
          <el-input v-model="form.configuration.dataBase" autocomplete="off" />
        </el-form-item>

        <el-form-item
          v-if="form.type == 'oracle'"
          label="服务名/SID"
          prop="configuration.connectionType"
        >
          <el-radio v-model="form.configuration.connectionType" label="sid"
            >SID</el-radio
          >
          <el-radio
            v-model="form.configuration.connectionType"
            label="serviceName"
          >
            服务名
          </el-radio>
        </el-form-item>

        <el-form-item
          v-if="form.configuration.dataSourceType == 'jdbc'"
          label="用户名"
        >
          <el-input v-model="form.configuration.username" autocomplete="off" />
        </el-form-item>
        <el-form-item
          v-if="form.configuration.dataSourceType == 'jdbc'"
          label="密码"
        >
          <el-input
            v-model="form.configuration.password"
            autocomplete="off"
            show-password
          />
        </el-form-item>
        <el-form-item
          v-if="form.configuration.dataSourceType == 'es'"
          label="用户名"
        >
          <el-input
            v-model="form.configuration.esUsername"
            autocomplete="off"
          />
        </el-form-item>
        <el-form-item
          v-if="form.configuration.dataSourceType == 'es'"
          label="密码"
        >
          <el-input
            v-model="form.configuration.esPassword"
            autocomplete="off"
            show-password
          />
        </el-form-item>

        <el-form-item
          v-if="
            form.configuration.dataSourceType == 'jdbc' &&
            form.type !== 'oracle'
          "
          label="额外的JDBC连接字符串"
        >
          <el-input
            v-model="form.configuration.extraParams"
            autocomplete="off"
          />
        </el-form-item>

        <el-form-item
          v-if="form.configuration.dataSourceType == 'jdbc'"
          label="端口号"
          prop="configuration.port"
        >
          <el-input
            v-model="form.configuration.port"
            autocomplete="off"
            type="number"
            min="0"
          />
        </el-form-item>
        <el-form-item
          v-if="
            form.type == 'oracle' ||
            form.type == 'sqlServer' ||
            form.type == 'pg' ||
            form.type == 'redshift' ||
            form.type == 'db2'
          "
        >
          <el-button icon="el-icon-plus" size="mini" @click="getSchema()">
            获取 Schema
          </el-button>
        </el-form-item>

        <el-form-item
          v-if="
            form.type == 'oracle' ||
            form.type == 'sqlServer' ||
            form.type == 'pg' ||
            form.type == 'redshift' ||
            form.type == 'db2'
          "
          label="数据库 Schema"
        >
          <el-select
            v-model="form.configuration.schema"
            filterable
            placeholder="请选择数据库 Schema"
            class="select-width"
          >
            <el-option
              v-for="item in schemas"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-collapse v-if="form.configuration.dataSourceType == 'jdbc'">
          <el-collapse-item title="高级设置" name="1">
            <el-form-item
              label="初始连接数"
              prop="configuration.initialPoolSize"
            >
              <el-input
                v-model="form.configuration.initialPoolSize"
                autocomplete="off"
                type="number"
                min="0"
                size="small"
              />
            </el-form-item>
            <el-form-item label="最小连接数" prop="configuration.minPoolSize">
              <el-input
                v-model="form.configuration.minPoolSize"
                autocomplete="off"
                type="number"
                min="0"
              />
            </el-form-item>
            <el-form-item label="最大连接数" prop="configuration.maxPoolSize">
              <el-input
                v-model="form.configuration.maxPoolSize"
                autocomplete="off"
                type="number"
                min="0"
              />
            </el-form-item>
            <el-form-item label="查询超时" prop="configuration.queryTimeout">
              <el-input
                v-model="form.configuration.queryTimeout"
                autocomplete="off"
                type="number"
                min="0"
              />
            </el-form-item>
          </el-collapse-item>
        </el-collapse>
      </el-form>
      <div v-if="canEdit" slot="footer" class="dialog-footer">
        <el-button
          v-if="
            formType === 'add'
              ? true
              : hasDataPermission('manage', params.privileges)
          "
          @click="validaDatasource"
          >校验
        </el-button>
        <el-button
          v-if="
            formType === 'add'
              ? true
              : hasDataPermission('manage', params.privileges)
          "
          type="primary"
          @click="save"
          >保存
        </el-button>
      </div>
      <div v-else slot="footer" class="dialog-footer">
        <el-button
          v-if="
            formType === 'add'
              ? true
              : hasDataPermission('manage', params.privileges)
          "
          @click="validaDatasource"
          >校验
        </el-button>
        <el-button
          v-if="
            formType === 'add'
              ? true
              : hasDataPermission('manage', params.privileges)
          "
          type="primary"
          @click="changeEdit"
          >编辑
        </el-button>
      </div>
    </div>
  </layout-content>
</template>

<script>
import LayoutContent from '@/components/business/LayoutContent'
import {
  addDs,
  editDs,
  getSchema,
  validateDs,
  validateDsById,
  getDrivesDetailsByType,
} from '@/api/newdb.js'
// import { $confirm } from '@/utils/message'
import { $confirm } from '@/utils/message.min.js'
// import i18n from '@/lang/index'
export default {
  name: 'DsForm',
  components: { LayoutContent },
  props: {
    params: {
      type: Object,
      default: null,
    },
  },
  data() {
    const checkName = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('名称不能为空'))
      } else {
        let reg = new RegExp(/\s/g)
        if (reg.test(value)) {
          callback(new Error('名称不能包含空格'))
        } else {
          callback()
        }
      }
    }
    return {
      form: {
        configuration: {
          initialPoolSize: 5,
          extraParams: '',
          minPoolSize: 5,
          maxPoolSize: 50,
          maxIdleTime: 30,
          acquireIncrement: 5,
          idleConnectionTestPeriod: 5,
          connectTimeout: 5,
          queryTimeout: 30,
          customDriver: 'default',
        },
      },
      rule: {
        name: [
          { validator: checkName, required: true, trigger: 'blur' },
          { min: 2, max: 25, message: '2-25字符', trigger: 'blur' },
        ],
        description: [
          { min: 0, max: 50, message: '0-50字符', trigger: 'blur' },
        ],
        type: [
          { required: true, message: '请选择数据源类型', trigger: 'change' },
        ],
        'configuration.dataBase': [
          {
            required: true,
            message: '请输入数据库名称',
            trigger: 'blur',
          },
        ],
        'configuration.connectionType': [
          {
            required: true,
            message: '选择连接类型',
            trigger: 'blur',
          },
        ],
        'configuration.username': [
          {
            required: true,
            message: '请输入用户名',
            trigger: 'blur',
          },
        ],
        'configuration.password': [
          {
            required: true,
            message: '请输入密码',
            trigger: 'change',
          },
        ],
        'configuration.host': [
          { required: true, message: '请输入主机', trigger: 'change' },
        ],
        'configuration.url': [
          { required: true, message: '请输入URL地址', trigger: 'change' },
        ],
        'configuration.port': [
          { required: true, message: '请输入端口', trigger: 'change' },
        ],
        'configuration.initialPoolSize': [
          {
            required: true,
            message: '请输入初始连接数',
            trigger: 'change',
          },
        ],
        'configuration.minPoolSize': [
          {
            required: true,
            message: '请输入最小连接数',
            trigger: 'change',
          },
        ],
        'configuration.maxPoolSize': [
          {
            required: true,
            message: '请输入最大连接数',
            trigger: 'change',
          },
        ],
        'configuration.maxIdleTime': [
          {
            required: true,
            message: '请输入最大空闲(秒)',
            trigger: 'change',
          },
        ],
        'configuration.acquireIncrement': [
          {
            required: true,
            message: '请输入增长数',
            trigger: 'change',
          },
        ],
        'configuration.connectTimeout': [
          {
            required: true,
            message: '请输入连接超时(秒)',
            trigger: 'change',
          },
        ],
        'configuration.queryTimeout': [
          {
            required: true,
            message: '请输入查询超时(秒)',
            trigger: 'change',
          },
        ],
      },
      allTypes: [
        {
          name: 'mysql',
          label: 'MySQL',
          type: 'jdbc',
          extraParams:
            'serverTimezone=UTC&characterEncoding=UTF-8&connectTimeout=5000&useSSL=false&allowPublicKeyRetrieval=true',
        },
        { name: 'db2', lable: 'Db2', type: 'jdbc', extraParams: '' },
        { name: 'hive', label: 'Apache Hive', type: 'jdbc', extraParams: '' },
        { name: 'oracle', label: 'Oracle', type: 'jdbc' },
        {
          name: 'sqlServer',
          label: 'SQL Server',
          type: 'jdbc',
          extraParams: '',
        },
        { name: 'pg', label: 'PostgreSQL', type: 'jdbc', extraParams: '' },
        // { name: 'es', label: 'Elasticsearch', type: 'es' },
        // {
        //   name: 'mariadb',
        //   label: 'MariaDB',
        //   type: 'jdbc',
        //   extraParams: 'characterEncoding=UTF-8&connectTimeout=5000&useSSL=false&allowPublicKeyRetrieval=true'
        // },
        // {
        //   name: 'ds_doris',
        //   label: 'Doris',
        //   type: 'jdbc',
        //   extraParams: 'characterEncoding=UTF-8&connectTimeout=5000&useSSL=false&allowPublicKeyRetrieval=true'
        // },
        { name: 'ck', label: 'ClickHouse', type: 'jdbc', extraParams: '' },
        // { name: 'redshift', label: 'AWS Redshift', type: 'jdbc' },
        // { name: 'mongo', label: 'MongoDB', type: 'jdbc', extraParams: '' }
        {
          name: 'gbase',
          label: 'gbase',
          type: 'jdbc',
          extraParams:
            'GBASEDBTSERVER={server};SQLMODE=GBase;DB_LOCALE=zh_CN.57372',
        },
        {
          name: 'dm',
          label: 'dm',
          type: 'jdbc',
          extraParams: '',
        },
        {
          name: 'kingbase',
          label: 'kingbase',
          type: 'jdbc',
          extraParams: '',
        },
        {
          name: 'tdengine',
          label: 'tdengine',
          type: 'jdbc',
          extraParams: '',
        },
      ],
      schemas: [],
      canEdit: false,
      originConfiguration: {},
      customDrivers: [
        {
          description: 'default',
          driverClass: 'default',
          id: 'default',
          jarFileName: 'default',
          jarPath: 'default',
          name: 'default',
          type: 'default',
        },
      ],
    }
  },

  created() {
    if (this.params && this.params.id) {
      const row = this.params
      this.edit(row)
    } else {
      this.create()
      if (this.params && this.params.type && !this.params.isCopy) {
        this.setType()
      }
      if (this.params && this.params.isCopy) {
        this.copyType()
      }
    }
    if (!!this.form.type) {
      this.getCustomDrivers(this.form.type)
    }
  },
  mounted() {},
  methods: {
    hasDataPermission() {
      return true
    },
    setType() {
      this.form.type = this.params.type
      this.form.configuration = {
        initialPoolSize: 5,
        extraParams: '',
        minPoolSize: 5,
        maxPoolSize: 50,
        maxIdleTime: 30,
        acquireIncrement: 5,
        idleConnectionTestPeriod: 5,
        connectTimeout: 5,
        queryTimeout: 30,
        customDriver: 'default',
      }
      this.changeType()
    },
    copyType() {
      this.formType = 'add'
      this.canEdit = true
      this.copyModal = true
      // console.log('传来的数据', this.params)
      this.form = this.params
    },
    changeEdit() {
      this.canEdit = true
      this.formType = 'modify'
    },
    create() {
      this.formType = 'add'
      this.canEdit = true
    },
    edit(row) {
      this.formType = 'modify'
      this.form = Object.assign({}, row)
      this.originConfiguration = this.form.configuration
      this.form.configuration = JSON.parse(this.form.configuration)
    },
    reset() {
      this.$refs.dsForm.resetFields()
    },
    save() {
      if (
        !this.form.configuration.schema &&
        (this.form.type === 'oracle' || this.form.type === 'sqlServer')
      ) {
        this.$message.error('请选择数据库 Schema')
        return
      }
      if (
        this.form.configuration.dataSourceType === 'jdbc' &&
        this.form.configuration.port <= 0
      ) {
        this.$message.error('端口不能小于零')
        return
      }
      if (
        this.form.configuration.initialPoolSize < 0 ||
        this.form.configuration.minPoolSize < 0 ||
        this.form.configuration.maxPoolSize < 0
      ) {
        this.$message.error('高级设置中的参数不能小于零')
        return
      }
      this.$refs.dsForm.validate(valid => {
        if (valid) {
          const method = this.formType === 'add' ? addDs : editDs
          const form = JSON.parse(JSON.stringify(this.form))
          form.configuration = JSON.stringify(form.configuration)
          if (
            this.formType !== 'add' &&
            this.originConfiguration !== form.configuration
          ) {
            $confirm(
              '修改数据源信息，可能会导致该数据源下的数据集不可用，确认修改？',
              () => {
                method(form).then(res => {
                  this.$message.success('保存成功')
                  this.refreshType(form)
                  this.backToList()
                })
              }
            )
          } else {
            method(form).then(res => {
              const { data } = res
              if (data.code === 400) {
                this.$message.error(data.msg)
              } else {
                this.$message.success('保存成功')
                this.refreshType(form)
                this.backToList()
              }
            })
          }
        } else {
          return false
        }
      })
    },
    getSchema() {
      this.$refs.dsForm.validate(valid => {
        if (valid) {
          const data = JSON.parse(JSON.stringify(this.form))
          data.configuration = JSON.stringify(data.configuration)
          getSchema(data).then(res => {
            console.log(res)
            this.schemas = res.data.data
            this.$message.success('成功')
          })
        } else {
          return false
        }
      })
    },
    validaDatasource() {
      if (!this.form.configuration.schema && this.form.type === 'oracle') {
        this.$message.error('请选择数据库 Schema')
        return
      }
      if (
        this.form.configuration.dataSourceType === 'jdbc' &&
        this.form.configuration.port <= 0
      ) {
        this.$message.error('端口不能小于零')
        return
      }
      this.$refs.dsForm.validate(valid => {
        if (valid) {
          const data = JSON.parse(JSON.stringify(this.form))
          data.configuration = JSON.stringify(data.configuration)
          if (data.showModel === 'show' && !this.canEdit) {
            validateDsById(data.id)
              .then(res => {
                if (res.data.success) {
                  this.$message.success('校验成功')
                } else {
                  this.$message.error(res.data.msg)
                }
                this.refreshType(data)
              })
              .catch(res => {
                this.$message.error(res.data.msg)
              })
          } else {
            validateDs(data)
              .then(res => {
                if (res.data.success) {
                  this.$message.success('校验成功')
                } else {
                  this.$message.error(res.data.msg)
                }
              })
              .catch(res => {
                this.$message.error(res.data.msg)
              })
          }
        } else {
          return false
        }
      })
    },
    changeType() {
      console.log('数据库类型改变', this.form.type)
      this.getCustomDrivers(this.form.type)
      for (let i = 0; i < this.allTypes.length; i++) {
        if (this.allTypes[i].name === this.form.type) {
          this.form.configuration.dataSourceType = this.allTypes[i].type
          this.form.configuration.extraParams = this.allTypes[i].extraParams
        }
      }
    },
    backToList() {
      this.$emit('switch-component', {})
    },
    refreshType(form) {
      this.$emit('refresh-type', form)
    },
    // changeDriver(type) {
    //   this.getCustomDrivers(type)
    // },
    async getCustomDrivers(type) {
      try {
        const res = await getDrivesDetailsByType(type)
        // console.log('获取驱动列表结果', res)
        if (res.status === 200) {
          const drivers = res.data.data
          this.customDrivers = [...this.customDrivers, ...drivers]
          // console.log('新驱动列表', this.customDrivers)
        } else {
          return
        }
      } catch (error) {}
    },
  },
}
</script>
<style lang="scss" scoped>
.back-button {
  cursor: pointer;
  margin-right: 10px;
  font-weight: 600;

  &:active {
    transform: scale(0.85);
  }
}

.el-input {
  width: 300px;
}

.el-select {
  width: 300px;
}
/deep/.el-form-item__label {
  color: #bfbfbf;
}
.el-collapse-item {
  position: relative;
}
/deep/ .el-collapse-item__arrow {
  left: 0;
}
/deep/.el-collapse-item__wrap {
  background-color: #171c22;
}
/deep/ .el-collapse-item__header {
  border-bottom: 1px solid #293241;
  color: #bfbfbf;
}
</style>
