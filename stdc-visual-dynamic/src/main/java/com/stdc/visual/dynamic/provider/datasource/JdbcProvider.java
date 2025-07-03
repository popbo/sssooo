package com.stdc.visual.dynamic.provider.datasource;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.wall.WallFilter;
import com.alibaba.fastjson.JSON;
import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.log.utils.LogUtil;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.StringPool;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.common.utils.JsonUtil;
import com.stdc.visual.dynamic.base.datasource.dto.DatasourceTypes;
import com.stdc.visual.dynamic.base.datasource.dto.TableDesc;
import com.stdc.visual.dynamic.base.datasource.dto.TableFiled;
import com.stdc.visual.dynamic.base.datasource.dto.configuration.*;
import com.stdc.visual.dynamic.base.datasource.po.DatasourceDriver;
import com.stdc.visual.dynamic.base.datasource.request.DatasourceRequest;
import com.stdc.visual.dynamic.mapper.DatasourceDriverMapper;
import com.stdc.visual.dynamic.mapper.DatasourceMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: wang_jie
 * @data: 2022/5/16--19:44
 * @describe: jdbc数据源生产者
 */
@Service("jdbc")
public class JdbcProvider extends DatasourceProvider {
    private static Map<String, DruidDataSource> jdbcConnection = new HashMap<>();
    public ExtendedJdbcClassLoader extendedJdbcClassLoader;
    private Map<String, ExtendedJdbcClassLoader> customJdbcClassLoaders = new HashMap<>();
    static private String FILE_PATH = "D:\\ProjectForWork\\DocumentProject\\Stdc-Java\\stdc-visual-service\\stdc-visual-dynamic\\src\\main\\resources";
    private static final String REG_WITH_SQL_FRAGMENT = "((?i)WITH[\\s\\S]+(?i)AS?\\s*\\([\\s\\S]+\\))\\s*(?i)SELECT";
    public static final Pattern WITH_SQL_FRAGMENT = Pattern.compile(REG_WITH_SQL_FRAGMENT);

    @Value("${driver.path}")
    private String driverPath = "/opt/driver";

    @Autowired
    private DatasourceDriverMapper datasourceDriverMapper;

    /**
     * 加载jdbc连接jar包,本地路径没有就在内存中加载
     *
     * @throws Exception
     */
    @PostConstruct
    public void init() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        extendedJdbcClassLoader = new ExtendedJdbcClassLoader(new URL[]{new File(driverPath).toURI().toURL()}, classLoader);
        File file = new File(driverPath);
        File[] array = file.listFiles();
        Optional.ofNullable(array).ifPresent(files -> {
            for (File tmp : array) {
                if (tmp.getName().endsWith(".jar")) {
                    try {
                        extendedJdbcClassLoader.addFile(tmp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 增加缓存机制 key 由 'provider_sql_' dsr.datasource.id dsr.table dsr.query共4部分组成，命中则使用缓存直接返回不再执行sql逻辑
     * @param dsr
     * @return
     * @throws Exception
     */
    /**
     * 这里使用声明式缓存不是很妥当
     * 改为chartViewService中使用编程式缓存
     *
     * @Cacheable( value = JdbcConstants.JDBC_PROVIDER_KEY,
     * key = "'provider_sql_' + #dsr.datasource.id + '_' + #dsr.table + '_' + #dsr.query",
     * condition = "#dsr.pageSize == null || #dsr.pageSize == 0L"
     * )
     */
    @Override
    public List<String[]> getData(DatasourceRequest dsr){
        List<String[]> list = new LinkedList<>();
        LogUtil.info("\njdbc执行sql：\n"+dsr.getQuery());
        JdbcConfiguration jdbcConfiguration = JsonUtil.toJavaObj(dsr.getDatasource().getConfiguration(), JdbcConfiguration.class);
        int queryTimeout = jdbcConfiguration.getQueryTimeout() > 0 ? jdbcConfiguration.getQueryTimeout() : 0;
        try (Connection connection = getConnectionFromPool(dsr); Statement stat = getStatement(connection,queryTimeout); ResultSet rs = stat.executeQuery(rebuildSqlWithFragment(dsr.getQuery()))) {

            list = fetchResult(rs);

            if (dsr.isPageable() && (dsr.getDatasource().getType().equalsIgnoreCase(DatasourceTypes.sqlServer.name()) || dsr.getDatasource().getType().equalsIgnoreCase(DatasourceTypes.db2.name()))) {
                Integer realSize = dsr.getPage() * dsr.getPageSize() < list.size() ? dsr.getPage() * dsr.getPageSize() : list.size();
                list = list.subList((dsr.getPage() - 1) * dsr.getPageSize(), realSize);
            }
        }catch (SQLTimeoutException e){
            LogUtil.error(e.getMessage());
            BaseException.throwException("执行sql超时");
        } catch (SQLException e) {
            if (e.getMessage().contains("timeout")){
                LogUtil.error(e.getMessage());
                BaseException.throwException("执行sql超时");
            }
            e.printStackTrace();
            BaseException.throwException(ResultCode.get("sql_error") + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            BaseException.throwException(ResultCode.get("datasource_connect_error") + e.getMessage());
        }
        return list;
    }

    private Connection getConnectionForTdengine(DatasourceRequest datasourceRequest) throws Exception {
//        Connection connect = null;
//        TdengineConfiguration tdengineConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), TdengineConfiguration.class);
//        String username = tdengineConfiguration.getUsername();
//        String password = tdengineConfiguration.getPassword();
//        String jdbcurl = tdengineConfiguration.getJdbc();
//        String driverClassName = "com.taosdata.jdbc.TSDBDriver";
//        Driver driverClass = (Driver) extendedJdbcClassLoader.loadClass(driverClassName).newInstance();
//        Properties props = new Properties();
//        if (StringUtils.isNotBlank(username)) {
//            props.setProperty("user", username);
//            if (StringUtils.isNotBlank(password)) {
//                props.setProperty("password", password);
//            }
//        }
//        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//        try {
//            Thread.currentThread().setContextClassLoader(extendedJdbcClassLoader);
//            connect = driverClass.connect(jdbcurl, props);
//        } catch (Exception e) {
//            LogUtil.error(e);
//            throw e;
//        } finally {
//            Thread.currentThread().setContextClassLoader(classLoader);
//        }
//        return connect;


        String username = null;
        String password = null;
        String defaultDriver = null;
        String jdbcurl = null;
        //自定义数据库驱动
        String customDriver = null;
        DatasourceDriver deDriver = null;
        DatasourceTypes datasourceType = DatasourceTypes.valueOf(datasourceRequest.getDatasource().getType());
        Properties props = new Properties();
        TdengineConfiguration tdengineConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), TdengineConfiguration.class);
        username = tdengineConfiguration.getUsername();
        password = tdengineConfiguration.getPassword();
        defaultDriver = "com.taosdata.jdbc.TSDBDriver";
        jdbcurl = tdengineConfiguration.getJdbc();
        Class.forName(defaultDriver);
        if (StringUtils.isNotBlank(username)) {
            props.setProperty("user", username);
            if (StringUtils.isNotBlank(password)) {
                props.setProperty("password", password);
            }
        }
        return DriverManager.getConnection(jdbcurl, props);
    }

    public Statement getStatement(Connection connection, int queryTimeout) throws Exception{
        if(connection == null){
            throw new Exception("Failed to get connection!");
        }
        Statement stat = connection.createStatement();
        try {
            stat.setQueryTimeout(queryTimeout);
        }catch (Exception e){
        }
        return stat;
    }

    public void exec(DatasourceRequest datasourceRequest) throws Exception {
        try (Connection connection = getConnectionFromPool(datasourceRequest); Statement stat = connection.createStatement()) {
            Boolean result = stat.execute(datasourceRequest.getQuery());
        }catch (SQLTimeoutException e){
            LogUtil.error(e.getMessage());
            BaseException.throwException("执行sql超时");
        } catch (SQLException e) {
            if (e.getMessage().contains("timeout")){
                LogUtil.error(e.getMessage());
                BaseException.throwException("执行sql超时");
            }
            BaseException.throwException(ResultCode.get("sql_error") + e.getMessage());
        } catch (Exception e) {
            BaseException.throwException(ResultCode.get("datasource_connect_error") + e.getMessage());
        }
    }

    @Override
    public List<String[]> fetchResult(DatasourceRequest datasourceRequest) throws Exception {
        JdbcConfiguration jdbcConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), JdbcConfiguration.class);
        int queryTimeout = jdbcConfiguration.getQueryTimeout() > 0 ? jdbcConfiguration.getQueryTimeout() : 0;
        try (Connection connection = getConnectionFromPool(datasourceRequest); Statement stat =  getStatement(connection,queryTimeout); ResultSet rs = stat.executeQuery(rebuildSqlWithFragment(datasourceRequest.getQuery()))) {
            return fetchResult(rs);
        }catch (SQLTimeoutException e){
            LogUtil.error(e.getMessage());
            BaseException.throwException("执行sql超时");
        } catch (SQLException e) {
            if (e.getMessage().contains("timeout")){
                LogUtil.error(e.getMessage());
                BaseException.throwException("执行sql超时");
            }
            BaseException.throwException(e);
        } catch (Exception e) {
            BaseException.throwException(e);
        }
        return new ArrayList<>();
    }

    private List<String[]> fetchResult(ResultSet rs) throws Exception {
        List<String[]> list = new LinkedList<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        while (rs.next()) {
            String[] row = new String[columnCount];
            for (int j = 0; j < columnCount; j++) {
                int columType = metaData.getColumnType(j + 1);
                switch (columType) {
                    case Types.DATE:
                        if (rs.getDate(j + 1) != null) {
                            row[j] = rs.getDate(j + 1).toString();
                        }
                        break;
                    case Types.BOOLEAN:
                        row[j] = rs.getBoolean(j + 1) ? "1" : "0";
                        break;
                    default:
                        row[j] = rs.getString(j + 1);
                        break;
                }
            }
            list.add(row);
        }
        return list;
    }

    @Override
    public List<TableFiled> getTableFields(DatasourceRequest datasourceRequest) throws Exception {
        if (datasourceRequest.getDatasource().getType().equalsIgnoreCase("mongo")) {
            datasourceRequest.setQuery("select * from " + datasourceRequest.getTable());
            return fetchResultField(datasourceRequest);
        }
        List<TableFiled> list = new LinkedList<>();
        try (Connection connection = getConnectionFromPool(datasourceRequest)) {
            //tdengine额外进行处理
            if (datasourceRequest.getDatasource().getType().equalsIgnoreCase("tdengine")) {
                String sql = "describe " + datasourceRequest.getTable() + ";";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);
                while (resultSet.next()){
                    TableFiled tableFiled = getTableFiledForTdengine(resultSet, datasourceRequest);
                    if (ObjectUtil.isNotEmpty(tableFiled)){
                        list.add(tableFiled);
                    }
                }
                resultSet.close();
            }
            //原操作
            else {
                if (datasourceRequest.getDatasource().getType().equalsIgnoreCase("oracle")) {
                    OracleConfiguration oracleConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), OracleConfiguration.class);
                    //判断是否为默认驱动
                    if (isDefaultClassLoader(oracleConfiguration.getCustomDriver())) {
                        Method setRemarksReporting = extendedJdbcClassLoader.loadClass("oracle.jdbc.driver.OracleConnection").getMethod("setRemarksReporting", boolean.class);
                        setRemarksReporting.invoke(((DruidPooledConnection) connection).getConnection(), true);
                    }
                }
                DatabaseMetaData databaseMetaData = connection.getMetaData();
                ResultSet resultSet = databaseMetaData.getColumns(null, "%", datasourceRequest.getTable(), "%");
                while (resultSet.next()) {
                    String tableName = resultSet.getString("TABLE_NAME");
                    String database;
                    if (datasourceRequest.getDatasource().getType().equalsIgnoreCase(DatasourceTypes.ck.name())) {
                        database = resultSet.getString("TABLE_SCHEM");
                    } else {
                        database = resultSet.getString("TABLE_CAT");
                    }
                    if (database != null) {
                        if (tableName.equals(datasourceRequest.getTable()) && database.equalsIgnoreCase(getDatabase(datasourceRequest))) {
                            TableFiled tableFiled = getTableFiled(resultSet, datasourceRequest);
                            list.add(tableFiled);
                        }
                    } else {
                        if (tableName.equals(datasourceRequest.getTable())) {
                            TableFiled tableFiled = getTableFiled(resultSet, datasourceRequest);
                            list.add(tableFiled);
                        }
                    }
                }
                resultSet.close();
            }
        } catch (SQLException e) {
            BaseException.throwException(e);
        } catch (Exception e) {
            if (datasourceRequest.getDatasource().getType().equalsIgnoreCase("ds_doris")) {
                datasourceRequest.setQuery("select * from " + datasourceRequest.getTable());
                return fetchResultField(datasourceRequest);
            } else {
                BaseException.throwException(ResultCode.get("datasource_connect_error") + e.getMessage());
            }

        }
        return list;
    }

    private TableFiled getTableFiled(ResultSet resultSet, DatasourceRequest datasourceRequest) throws SQLException {
        TableFiled tableFiled = new TableFiled();
        String colName = resultSet.getString("COLUMN_NAME");
        tableFiled.setFieldName(colName);
        String remarks = resultSet.getString("REMARKS");
        if (remarks == null || remarks.equals("")) {
            remarks = colName;
        }
        tableFiled.setRemarks(remarks);
        String dbType = resultSet.getString("TYPE_NAME").toUpperCase();
        tableFiled.setFieldType(dbType);
        if (dbType.equalsIgnoreCase("LONG")) {
            tableFiled.setFieldSize(65533);
        }
        if (StringUtils.isNotEmpty(dbType) && dbType.toLowerCase().contains("date") && tableFiled.getFieldSize() < 50) {
            tableFiled.setFieldSize(50);
        }

        if (datasourceRequest.getDatasource().getType().equalsIgnoreCase(DatasourceTypes.ck.name())) {
//            QueryProvider qp = ProviderFactory.getQueryProvider(datasourceRequest.getDatasource().getType());
//            tableFiled.setFieldSize(qp.transFieldSize(dbType));
        } else {
            if (datasourceRequest.getDatasource().getType().equalsIgnoreCase(DatasourceTypes.hive.name()) && tableFiled.getFieldType().equalsIgnoreCase("BOOLEAN")) {
                tableFiled.setFieldSize(1);
            } else {
                String size = resultSet.getString("COLUMN_SIZE");
                if (size == null) {
                    tableFiled.setFieldSize(1);
                } else {
                    tableFiled.setFieldSize(Integer.valueOf(size));
                }
            }
        }
        return tableFiled;
    }

    private TableFiled getTableFiledForTdengine(ResultSet resultSet, DatasourceRequest datasourceRequest) throws SQLException {
        TableFiled tableFiled = new TableFiled();
        String colName = resultSet.getString("field");
        if (StringUtil.equals("device_type",colName)){
            return null;
        }
        tableFiled.setFieldName(colName);
        String remarks = resultSet.getString("note");
        if (remarks == null || remarks.equals("")) {
            remarks = colName;
        }
        tableFiled.setRemarks(remarks);
        String dbType = resultSet.getString("type").toUpperCase();
        tableFiled.setFieldType(dbType);

        String length = resultSet.getString("length");
        tableFiled.setFieldSize(Integer.parseInt(length));

        if (StringUtils.isNotEmpty(dbType) && dbType.toLowerCase().contains("date") && tableFiled.getFieldSize() < 50) {
            tableFiled.setFieldSize(50);
        }
        return tableFiled;
    }

    private String getDatabase(DatasourceRequest datasourceRequest) {
        DatasourceTypes datasourceType = DatasourceTypes.valueOf(datasourceRequest.getDatasource().getType());
        switch (datasourceType) {
            case mysql:
            case de_doris:
            case ds_doris:
            case mariadb:
                MysqlConfiguration mysqlConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), MysqlConfiguration.class);
                return mysqlConfiguration.getDataBase();
            case gbase:
                GbaseConfiguration gbaseConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), GbaseConfiguration.class);
                return gbaseConfiguration.getDataBase();
            case sqlServer:
                SqlServerConfiguration sqlServerConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), SqlServerConfiguration.class);
                return sqlServerConfiguration.getDataBase();
            case pg:
                PgConfiguration pgConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), PgConfiguration.class);
                return pgConfiguration.getDataBase();
            default:
                JdbcConfiguration jdbcConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), JdbcConfiguration.class);
                return jdbcConfiguration.getDataBase();
        }
    }

    @Override
    public List<TableFiled> fetchResultField(DatasourceRequest datasourceRequest) throws Exception {
        JdbcConfiguration jdbcConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), JdbcConfiguration.class);
        int queryTimeout = jdbcConfiguration.getQueryTimeout() > 0 ? jdbcConfiguration.getQueryTimeout() : 0;
        try (Connection connection = getConnectionFromPool(datasourceRequest); Statement stat = getStatement(connection,queryTimeout); ResultSet rs = stat.executeQuery(rebuildSqlWithFragment(datasourceRequest.getQuery()))) {
            return fetchResultField(rs, datasourceRequest);
        } catch (SQLException e) {
            BaseException.throwException(e);
        } catch (Exception e) {
            BaseException.throwException(ResultCode.get("datasource_connect_error") + e.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    public Map<String, List> fetchResultAndField(DatasourceRequest datasourceRequest) throws Exception {
        Map<String, List> result = new HashMap<>();
        List<String[]> dataList;
        List<TableFiled> fieldList;
        JdbcConfiguration jdbcConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), JdbcConfiguration.class);
        int queryTimeout = jdbcConfiguration.getQueryTimeout() > 0 ? jdbcConfiguration.getQueryTimeout() : 0;
        try (Connection connection = getConnectionFromPool(datasourceRequest); Statement stat =  getStatement(connection, queryTimeout); ResultSet rs = stat.executeQuery(rebuildSqlWithFragment(datasourceRequest.getQuery()))) {
            fieldList = fetchResultField(rs, datasourceRequest);
            result.put("fieldList", fieldList);
            dataList = fetchResult(rs);
            result.put("dataList", dataList);
            return result;
        }catch (SQLTimeoutException e){
            LogUtil.error(e.getMessage());
            BaseException.throwException("执行sql超时");
        } catch (SQLException e) {
            if (e.getMessage().contains("timeout")){
                LogUtil.error(e.getMessage());
                BaseException.throwException("执行sql超时");
            }
            BaseException.throwException(e);
        } catch (Exception e) {
            BaseException.throwException(e);
        }
        return new HashMap<>();
    }

    private List<TableFiled> fetchResultField(ResultSet rs, DatasourceRequest datasourceRequest) throws Exception {
        List<TableFiled> fieldList = new ArrayList<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int j = 0; j < columnCount; j++) {
            String f = metaData.getColumnName(j + 1);
            String l = StringUtils.isNotEmpty(metaData.getColumnLabel(j + 1)) ? metaData.getColumnLabel(j + 1) : f;
            String t = metaData.getColumnTypeName(j + 1);
            if (datasourceRequest.getDatasource().getType().equalsIgnoreCase(DatasourceTypes.hive.name()) && l.contains("\\.")) {
                l = l.split("\\.")[1];
            }
            TableFiled field = new TableFiled();
            field.setFieldName(l);
            field.setRemarks(l);
            field.setFieldType(t);

            if (datasourceRequest.getDatasource().getType().equalsIgnoreCase(DatasourceTypes.ck.name())) {
//                QueryProvider qp = ProviderFactory.getQueryProvider(datasourceRequest.getDatasource().getType());
//                field.setFieldSize(qp.transFieldSize(t));
            } else {
                field.setFieldSize(metaData.getColumnDisplaySize(j + 1));
            }
            if (t.equalsIgnoreCase("LONG")) {
                field.setFieldSize(65533);
            } //oracle LONG
            if (StringUtils.isNotEmpty(t) && t.toLowerCase().contains("date") && field.getFieldSize() < 50) {
                field.setFieldSize(50);
            }
            fieldList.add(field);
        }
        return fieldList;
    }

    @Override
    public List<TableDesc> getTables(DatasourceRequest datasourceRequest) throws Exception {
        List<TableDesc> tables = new ArrayList<>();
        String queryStr = getTablesSql(datasourceRequest);
        try (Connection con = getConnectionFromPool(datasourceRequest); Statement statement = con.createStatement(); ResultSet resultSet = statement.executeQuery(queryStr)) {
            while (resultSet.next()) {
                tables.add(getTableDesc(datasourceRequest, resultSet));
            }
        } catch (Exception e) {
            BaseException.throwException(e);
        }

        String queryView = getViewSql(datasourceRequest);
        if (queryView != null) {
            try (Connection con = getConnectionFromPool(datasourceRequest); Statement statement = con.createStatement(); ResultSet resultSet = statement.executeQuery(queryView)) {
                while (resultSet.next()) {
                    tables.add(getTableDesc(datasourceRequest, resultSet));
                }
            } catch (Exception e) {
                BaseException.throwException(e);
            }
        }

        return tables;
    }

    private TableDesc getTableDesc(DatasourceRequest datasourceRequest, ResultSet resultSet) throws SQLException {
        TableDesc tableDesc = new TableDesc();
        DatasourceTypes datasourceType = DatasourceTypes.valueOf(datasourceRequest.getDatasource().getType());
        if (datasourceType == DatasourceTypes.oracle) {
            tableDesc.setRemark(resultSet.getString(3));
        }
        if (datasourceType == DatasourceTypes.mysql) {
            tableDesc.setRemark(resultSet.getString(2));
        }
        tableDesc.setName(resultSet.getString(1));
        return tableDesc;
    }

    @Override
    public List<String> getSchema(DatasourceRequest datasourceRequest) throws Exception {
        List<String> schemas = new ArrayList<>();
        String queryStr = getSchemaSql(datasourceRequest);
        try (
                Connection con = getConnection(datasourceRequest);
                Statement statement = con.createStatement();
                ResultSet resultSet = statement.executeQuery(queryStr)
        ) {
            while (resultSet.next()) {
                schemas.add(resultSet.getString(1));
            }
            return schemas;
        }catch (SQLTimeoutException e){
            LogUtil.error(e.getMessage());
            BaseException.throwException("执行sql超时");
        } catch (Exception e) {
            if (e.getMessage().contains("timeout")){
                LogUtil.error(e.getMessage());
                BaseException.throwException("执行sql超时");
            }
            BaseException.throwException(e);
        }
        return new ArrayList<>();
    }

    @Override
    public void checkStatus(DatasourceRequest datasourceRequest) throws Exception {
        String queryStr = getTablesSql(datasourceRequest);
        try (Connection con = getConnection(datasourceRequest); Statement statement = con.createStatement(); ResultSet resultSet = statement.executeQuery(queryStr)) {

        }catch (SQLTimeoutException e){
            LogUtil.error(e.getMessage());
            BaseException.throwException("执行sql超时");
        } catch (Exception e) {
            if (e.getMessage().contains("timeout")){
                LogUtil.error(e.getMessage());
                BaseException.throwException("执行sql超时");
            }
            e.printStackTrace();
            BaseException.throwException(e.getMessage());
        }
    }

    @Override
    public void handleDatasource(DatasourceRequest datasourceRequest, String type) throws Exception {
        DruidDataSource dataSource = null;
        switch (type) {
            case "add":
                checkStatus(datasourceRequest);
                dataSource = jdbcConnection.get(datasourceRequest.getDatasource().getId());
                if (dataSource == null) {
                    addToPool(datasourceRequest);
                }
                break;
            case "edit":
                dataSource = jdbcConnection.get(datasourceRequest.getDatasource().getId());
                if (dataSource != null) {
                    dataSource.close();
                }
                checkStatus(datasourceRequest);
                addToPool(datasourceRequest);
                break;
            case "delete":
                dataSource = jdbcConnection.get(datasourceRequest.getDatasource().getId());
                if (dataSource != null) {
                    dataSource.close();
                }
                break;
            default:
                break;
        }
    }

    private Connection getConnectionFromPool(DatasourceRequest datasourceRequest) throws Exception {
        //为tdengine单开获取连接逻辑
        if (StringUtil.equals("tdengine",datasourceRequest.getDatasource().getType())){
            return getConnectionForTdengine(datasourceRequest);
        }
        DruidDataSource dataSource = jdbcConnection.get(datasourceRequest.getDatasource().getId());
        if (dataSource == null) {
            handleDatasource(datasourceRequest, "add");
        }
        dataSource = jdbcConnection.get(datasourceRequest.getDatasource().getId());
        Connection co = dataSource.getConnection();
        return co;
    }

    private Connection getConnection(DatasourceRequest datasourceRequest) throws Exception {
        //为tdengine单开获取连接逻辑
        if (StringUtil.equals("tdengine",datasourceRequest.getDatasource().getType())){
            return getConnectionForTdengine(datasourceRequest);
        }
        String username = null;
        String password = null;
        String defaultDriver = null;
        String jdbcurl = null;
        //自定义数据库驱动
        String customDriver = null;
        DatasourceDriver deDriver = null;
        DatasourceTypes datasourceType = DatasourceTypes.valueOf(datasourceRequest.getDatasource().getType());
        Properties props = new Properties();
        switch (datasourceType) {
            case tdengine:
                TdengineConfiguration tdengineConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), TdengineConfiguration.class);
                username = tdengineConfiguration.getUsername();
                password = tdengineConfiguration.getPassword();
                defaultDriver = "com.taosdata.jdbc.TSDBDriver";
                jdbcurl = tdengineConfiguration.getJdbc();
                customDriver = tdengineConfiguration.getCustomDriver();
                break;
            case mysql:
            case mariadb:
            case de_doris:
            case ds_doris:
                MysqlConfiguration mysqlConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), MysqlConfiguration.class);
                username = mysqlConfiguration.getUsername();
                password = mysqlConfiguration.getPassword();
                defaultDriver = "com.mysql.jdbc.Driver";
                jdbcurl = mysqlConfiguration.getJdbc();
                customDriver = mysqlConfiguration.getCustomDriver();
                break;
            case gbase:
                GbaseConfiguration gbaseConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), GbaseConfiguration.class);
                username = gbaseConfiguration.getUsername();
                password = gbaseConfiguration.getPassword();
                defaultDriver = "com.gbasedbt.jdbc.Driver";
                jdbcurl = gbaseConfiguration.getJdbc();
                customDriver = gbaseConfiguration.getCustomDriver();
                break;
            case dm:
                DMConfiguration dmConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), DMConfiguration.class);
                username = dmConfiguration.getUsername();
                password = dmConfiguration.getPassword();
                defaultDriver = dmConfiguration.getDriver();
                jdbcurl = dmConfiguration.getJdbc();
                customDriver = dmConfiguration.getCustomDriver();
                break;
            case kingbase:
                KingBaseConfiguration kingBaseConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), KingBaseConfiguration.class);
                username = kingBaseConfiguration.getUsername();
                password = kingBaseConfiguration.getPassword();
                defaultDriver = kingBaseConfiguration.getDriver();
                jdbcurl = kingBaseConfiguration.getJdbc();
                customDriver = kingBaseConfiguration.getCustomDriver();
                break;
            case sqlServer:
                SqlServerConfiguration sqlServerConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), SqlServerConfiguration.class);
                username = sqlServerConfiguration.getUsername();
                password = sqlServerConfiguration.getPassword();
                defaultDriver = sqlServerConfiguration.getDriver();
                jdbcurl = sqlServerConfiguration.getJdbc();
                customDriver = sqlServerConfiguration.getCustomDriver();
                break;
            case oracle:
                OracleConfiguration oracleConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), OracleConfiguration.class);
                username = oracleConfiguration.getUsername();
                password = oracleConfiguration.getPassword();
                defaultDriver = oracleConfiguration.getDriver();
                jdbcurl = oracleConfiguration.getJdbc();
                customDriver = oracleConfiguration.getCustomDriver();
                props.put("oracle.net.CONNECT_TIMEOUT", "5000");
                break;
            case pg:
                PgConfiguration pgConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), PgConfiguration.class);
                username = pgConfiguration.getUsername();
                password = pgConfiguration.getPassword();
                defaultDriver = pgConfiguration.getDriver();
                jdbcurl = pgConfiguration.getJdbc();
                customDriver = pgConfiguration.getCustomDriver();
                break;
            case ck:
                CHConfiguration chConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), CHConfiguration.class);
                username = chConfiguration.getUsername();
                password = chConfiguration.getPassword();
                defaultDriver = chConfiguration.getDriver();
                jdbcurl = chConfiguration.getJdbc();
                customDriver = chConfiguration.getCustomDriver();
                break;
            case mongo:
                MongodbConfiguration mongodbConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), MongodbConfiguration.class);
                username = mongodbConfiguration.getUsername();
                password = mongodbConfiguration.getPassword();
                defaultDriver = mongodbConfiguration.getDriver();
                jdbcurl = mongodbConfiguration.getJdbc();
                customDriver = mongodbConfiguration.getCustomDriver();
                break;
            case redshift:
                RedshiftConfigration redshiftConfigration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), RedshiftConfigration.class);
                username = redshiftConfigration.getUsername();
                password = redshiftConfigration.getPassword();
                defaultDriver = redshiftConfigration.getDriver();
                jdbcurl = redshiftConfigration.getJdbc();
                customDriver = redshiftConfigration.getCustomDriver();
                break;
            case hive:
                HiveConfiguration hiveConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), HiveConfiguration.class);
                username = hiveConfiguration.getUsername();
                password = hiveConfiguration.getPassword();
                defaultDriver = hiveConfiguration.getDriver();
                jdbcurl = hiveConfiguration.getJdbc();
                customDriver = hiveConfiguration.getCustomDriver();
                break;
            case db2:
                Db2Configuration db2Configuration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), Db2Configuration.class);
                username = db2Configuration.getUsername();
                password = db2Configuration.getPassword();
                defaultDriver = db2Configuration.getDriver();
                jdbcurl = db2Configuration.getJdbc();
                customDriver = db2Configuration.getCustomDriver();
                break;
            default:
                break;
        }

        if (StringUtils.isNotBlank(username)) {
            props.setProperty("user", username);
            if (StringUtils.isNotBlank(password)) {
                props.setProperty("password", password);
            }
        }

        Connection conn;
        String driverClassName;
        ExtendedJdbcClassLoader jdbcClassLoader;
        if (isDefaultClassLoader(customDriver)) {
            driverClassName = defaultDriver;
            jdbcClassLoader = extendedJdbcClassLoader;
        }else {
            if (deDriver == null){
                deDriver = datasourceDriverMapper.selectById(customDriver);
            }
            driverClassName = deDriver.getDriverClass();
            jdbcClassLoader = getCustomJdbcClassLoader(deDriver);
        }
        Driver driverClass = (Driver) jdbcClassLoader.loadClass(driverClassName).newInstance();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(jdbcClassLoader);
            conn = driverClass.connect(jdbcurl, props);
        } catch (Exception e) {
            LogUtil.error(e);
            throw e;
        } finally {
            Thread.currentThread().setContextClassLoader(classLoader);
        }
        return conn;
    }

    private void addToPool(DatasourceRequest datasourceRequest) throws PropertyVetoException, SQLException {
        if (datasourceRequest.getDatasource().getType().equals("tdengine")){
            return;
        }
        DruidDataSource druidDataSource = new DruidDataSource();
        JdbcConfiguration jdbcConfiguration = setCredential(datasourceRequest, druidDataSource);
        druidDataSource.setInitialSize(jdbcConfiguration.getInitialPoolSize());// 初始连接数
        druidDataSource.setMinIdle(jdbcConfiguration.getMinPoolSize()); // 最小连接数
        druidDataSource.setMaxActive(jdbcConfiguration.getMaxPoolSize()); // 最大连接数
        if (datasourceRequest.getDatasource().getType().equals(DatasourceTypes.mongo.name()) || datasourceRequest.getDatasource().getType().equals(DatasourceTypes.hive.name())) {
            WallFilter wallFilter = new WallFilter();
            wallFilter.setDbType(DatasourceTypes.mysql.name());
            druidDataSource.setProxyFilters(Arrays.asList(new Filter[]{wallFilter}));
        }
        druidDataSource.init();
        jdbcConnection.put(datasourceRequest.getDatasource().getId(), druidDataSource);
    }


    private JdbcConfiguration setCredential(DatasourceRequest datasourceRequest, DruidDataSource dataSource) throws PropertyVetoException {
        DatasourceTypes datasourceType = DatasourceTypes.valueOf(datasourceRequest.getDatasource().getType());
        JdbcConfiguration jdbcConfiguration = new JdbcConfiguration();
        switch (datasourceType) {
            case tdengine:
                TdengineConfiguration tdengineConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), TdengineConfiguration.class);
                dataSource.setUrl(tdengineConfiguration.getJdbc());
                dataSource.setDriverClassName(tdengineConfiguration.getDriver());
                dataSource.setValidationQuery("select 1");
                jdbcConfiguration = tdengineConfiguration;
                break;
            case mysql:
            case mariadb:
            case de_doris:
            case ds_doris:
                MysqlConfiguration mysqlConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), MysqlConfiguration.class);
                dataSource.setUrl(mysqlConfiguration.getJdbc());
                dataSource.setDriverClassName(mysqlConfiguration.getDriver());
                dataSource.setValidationQuery("select 1");
                jdbcConfiguration = mysqlConfiguration;
                break;
            case kingbase:
                KingBaseConfiguration kingBaseConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), KingBaseConfiguration.class);
                dataSource.setUrl(kingBaseConfiguration.getJdbc());
                dataSource.setDriverClassName(kingBaseConfiguration.getDriver());
                dataSource.setValidationQuery("select 1");
                jdbcConfiguration = kingBaseConfiguration;
                break;
            case gbase:
                GbaseConfiguration gbaseConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), GbaseConfiguration.class);
                dataSource.setUrl(gbaseConfiguration.getJdbc());
                dataSource.setDriverClassName(gbaseConfiguration.getDriver());
                dataSource.setValidationQuery("select * from systables where tabid > 99 and tabtype = 'T'");
                jdbcConfiguration = gbaseConfiguration;
                break;
            case dm:
                DMConfiguration dmConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), DMConfiguration.class);
                dataSource.setUrl(dmConfiguration.getJdbc());
                dataSource.setDriverClassName(dmConfiguration.getDriver());
                jdbcConfiguration = dmConfiguration;
                break;
            case sqlServer:
                SqlServerConfiguration sqlServerConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), SqlServerConfiguration.class);
                dataSource.setDriverClassName(sqlServerConfiguration.getDriver());
                dataSource.setUrl(sqlServerConfiguration.getJdbc());
                dataSource.setValidationQuery("select 1");
                jdbcConfiguration = sqlServerConfiguration;
                break;
            case oracle:
                OracleConfiguration oracleConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), OracleConfiguration.class);
                dataSource.setDriverClassName(oracleConfiguration.getDriver());
                dataSource.setUrl(oracleConfiguration.getJdbc());
                dataSource.setValidationQuery("select 1 from dual");
                jdbcConfiguration = oracleConfiguration;
                break;
            case pg:
                PgConfiguration pgConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), PgConfiguration.class);
                dataSource.setDriverClassName(pgConfiguration.getDriver());
                dataSource.setUrl(pgConfiguration.getJdbc());
                jdbcConfiguration = pgConfiguration;
                break;
            case ck:
                CHConfiguration chConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), CHConfiguration.class);
                dataSource.setDriverClassName(chConfiguration.getDriver());
                dataSource.setUrl(chConfiguration.getJdbc());
                jdbcConfiguration = chConfiguration;
                break;
            case mongo:
                MongodbConfiguration mongodbConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), MongodbConfiguration.class);
                dataSource.setDriverClassName(mongodbConfiguration.getDriver());
                dataSource.setUrl(mongodbConfiguration.getJdbc());
                jdbcConfiguration = mongodbConfiguration;
                break;
            case redshift:
                RedshiftConfigration redshiftConfigration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), RedshiftConfigration.class);
                dataSource.setPassword(redshiftConfigration.getPassword());
                dataSource.setDriverClassName(redshiftConfigration.getDriver());
                dataSource.setUrl(redshiftConfigration.getJdbc());
                jdbcConfiguration = redshiftConfigration;
                break;
            case hive:
                HiveConfiguration hiveConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), HiveConfiguration.class);
                dataSource.setPassword(hiveConfiguration.getPassword());
                dataSource.setDriverClassName(hiveConfiguration.getDriver());
                dataSource.setUrl(hiveConfiguration.getJdbc());
                jdbcConfiguration = hiveConfiguration;
                break;
            case db2:
                Db2Configuration db2Configuration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), Db2Configuration.class);
                dataSource.setPassword(db2Configuration.getPassword());
                dataSource.setDriverClassName(db2Configuration.getDriver());
                dataSource.setUrl(db2Configuration.getJdbc());
                jdbcConfiguration = db2Configuration;
            default:
                break;
        }

        dataSource.setUsername(jdbcConfiguration.getUsername());
        //新增驱动管理功能
        ExtendedJdbcClassLoader classLoader = null;
        if(isDefaultClassLoader(jdbcConfiguration.getCustomDriver())){
            //使用默认驱动
            classLoader = extendedJdbcClassLoader;
        }else {
            DatasourceDriver datasourceDriver = datasourceDriverMapper.selectById(jdbcConfiguration.getCustomDriver());
            try {
                classLoader = getCustomJdbcClassLoader(datasourceDriver);
            } catch (Exception e) {
                LogUtil.error(e);
            }
        }
        dataSource.setDriverClassLoader(classLoader);
        dataSource.setPassword(jdbcConfiguration.getPassword());
        return jdbcConfiguration;
    }

    private String getTablesSql(DatasourceRequest datasourceRequest) throws Exception {
        DatasourceTypes datasourceType = DatasourceTypes.valueOf(datasourceRequest.getDatasource().getType());
        switch (datasourceType) {
            case mysql:
            case mariadb:
                JdbcConfiguration jdbcConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), JdbcConfiguration.class);
                return String.format("SELECT TABLE_NAME,TABLE_COMMENT FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_SCHEMA = '%s' ;", jdbcConfiguration.getDataBase());
            case de_doris:
            case ds_doris:
            case tdengine:
            case hive:
                return "show tables";
            case kingbase:
                return "select relname from sys_stat_user_tables";
            case gbase:
                return "select * from systables where tabid > 99 and tabtype = 'T'";
            case dm:
                DMConfiguration dmConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), DMConfiguration.class);
                return String.format("select TABLE_NAME from dba_tables where owner='%s'",dmConfiguration.getDataBase());
            case sqlServer:
                SqlServerConfiguration sqlServerConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), SqlServerConfiguration.class);
                if (StringUtils.isEmpty(sqlServerConfiguration.getSchema())) {
                    throw new Exception(ResultCode.get("schema_is_empty"));
                }
                return "SELECT TABLE_NAME FROM \"DATABASE\".INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_SCHEMA = 'DS_SCHEMA' ;"
                        .replace("DATABASE", sqlServerConfiguration.getDataBase())
                        .replace("DS_SCHEMA", sqlServerConfiguration.getSchema());
            case oracle:
                OracleConfiguration oracleConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), OracleConfiguration.class);
                if (StringUtils.isEmpty(oracleConfiguration.getSchema())) {
                    throw new Exception(ResultCode.get("schema_is_empty"));
                }
                return "select table_name, owner, comments from all_tab_comments where owner='" + oracleConfiguration.getSchema() + "' AND table_type = 'TABLE'";
            case pg:
                PgConfiguration pgConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), PgConfiguration.class);
                if (StringUtils.isEmpty(pgConfiguration.getSchema())) {
                    throw new Exception(ResultCode.get("schema_is_empty"));
                }
                return "SELECT tablename FROM  pg_tables WHERE  schemaname='SCHEMA' ;".replace("SCHEMA", pgConfiguration.getSchema());
            case ck:
                CHConfiguration chConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), CHConfiguration.class);
                return "SELECT name FROM system.tables where database='DATABASE';".replace("DATABASE", chConfiguration.getDataBase());
            case redshift:
                RedshiftConfigration redshiftConfigration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), RedshiftConfigration.class);
                if (StringUtils.isEmpty(redshiftConfigration.getSchema())) {
                    throw new Exception(ResultCode.get("schema_is_empty"));
                }
                return "SELECT tablename FROM  pg_tables WHERE  schemaname='SCHEMA' ;".replace("SCHEMA", redshiftConfigration.getSchema());
            case db2:
                Db2Configuration db2Configuration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), Db2Configuration.class);
                if (StringUtils.isEmpty(db2Configuration.getSchema())) {
                    throw new Exception(ResultCode.get("schema_is_empty"));
                }
                return "select TABNAME from syscat.tables  WHERE TABSCHEMA ='DE_SCHEMA' AND \"TYPE\" = 'T'".replace("DE_SCHEMA", db2Configuration.getSchema());
            default:
                return "show tables;";
        }
    }

    private String getViewSql(DatasourceRequest datasourceRequest) throws Exception {
        DatasourceTypes datasourceType = DatasourceTypes.valueOf(datasourceRequest.getDatasource().getType());
        switch (datasourceType) {
            case tdengine:
            case mysql:
            case mariadb:
            case de_doris:
            case ds_doris:
            case ck:
                return null;
            case sqlServer:
                SqlServerConfiguration sqlServerConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), SqlServerConfiguration.class);
                if (StringUtils.isEmpty(sqlServerConfiguration.getSchema())) {
                    throw new Exception(ResultCode.get("schema_is_empty"));
                }
                return "SELECT TABLE_NAME FROM DATABASE.INFORMATION_SCHEMA.VIEWS WHERE  TABLE_SCHEMA = 'DS_SCHEMA' ;"
                        .replace("DATABASE", sqlServerConfiguration.getDataBase())
                        .replace("DS_SCHEMA", sqlServerConfiguration.getSchema());
            case oracle:
                OracleConfiguration oracleConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), OracleConfiguration.class);
                if (StringUtils.isEmpty(oracleConfiguration.getSchema())) {
                    throw new Exception(ResultCode.get("schema_is_empty"));
                }
                return "select table_name, owner, comments from all_tab_comments where owner='" + oracleConfiguration.getSchema() + "' AND table_type = 'VIEW'";
            case pg:
                PgConfiguration pgConfiguration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), PgConfiguration.class);
                if (StringUtils.isEmpty(pgConfiguration.getSchema())) {
                    throw new Exception(ResultCode.get("schema_is_empty"));
                }
                return "SELECT viewname FROM  pg_views WHERE schemaname='SCHEMA' ;".replace("SCHEMA", pgConfiguration.getSchema());
            case redshift:
                RedshiftConfigration redshiftConfigration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), RedshiftConfigration.class);
                if (StringUtils.isEmpty(redshiftConfigration.getSchema())) {
                    throw new Exception(ResultCode.get("schema_is_empty"));
                }
                return "SELECT viewname FROM  pg_views WHERE schemaname='SCHEMA' ;".replace("SCHEMA", redshiftConfigration.getSchema());

            case db2:
                Db2Configuration db2Configuration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), Db2Configuration.class);
                if (StringUtils.isEmpty(db2Configuration.getSchema())) {
                    throw new Exception(ResultCode.get("schema_is_empty"));
                }
                return "select TABNAME from syscat.tables  WHERE TABSCHEMA ='DE_SCHEMA' AND \"TYPE\" = 'V'".replace("DE_SCHEMA", db2Configuration.getSchema());

            default:
                return null;
        }
    }

    private String getSchemaSql(DatasourceRequest datasourceRequest) {
        DatasourceTypes datasourceType = DatasourceTypes.valueOf(datasourceRequest.getDatasource().getType());
        Db2Configuration db2Configuration = JsonUtil.toJavaObj(datasourceRequest.getDatasource().getConfiguration(), Db2Configuration.class);
        switch (datasourceType) {
            case oracle:
                return "select * from all_users";
            case sqlServer:
                return "select name from sys.schemas;";
            case db2:
                return "select SCHEMANAME from syscat.SCHEMATA   WHERE \"DEFINER\" ='USER'".replace("USER", db2Configuration.getUsername().toUpperCase());
            case pg:
                return "SELECT nspname FROM pg_namespace;";
            case redshift:
                return "SELECT nspname FROM pg_namespace;";
            default:
                return "show tables;";
        }
    }

    private static String rebuildSqlWithFragment(String sql) {
        if (!sql.toLowerCase().startsWith("with")) {
            Matcher matcher = WITH_SQL_FRAGMENT.matcher(sql);
            if (matcher.find()) {
                String withFragment = matcher.group();
                if (!com.alibaba.druid.util.StringUtils.isEmpty(withFragment)) {
                    if (withFragment.length() > 6) {
                        int lastSelectIndex = withFragment.length() - 6;
                        sql = sql.replace(withFragment, withFragment.substring(lastSelectIndex));
                        withFragment = withFragment.substring(0, lastSelectIndex);
                    }
                    sql = withFragment + " " + sql;
                    sql = sql.replaceAll(" " + "{2,}", " ");
                }
            }
        }
        return sql;
    }
    protected boolean isDefaultClassLoader(String customDriver) {
        return StringUtils.isEmpty(customDriver) || customDriver.equalsIgnoreCase("default");
    }

    protected ExtendedJdbcClassLoader getCustomJdbcClassLoader(DatasourceDriver deDriver) throws Exception {
        if (deDriver == null) {
            throw new Exception("Can not found custom Driver");
        }
        ExtendedJdbcClassLoader customJdbcClassLoader = customJdbcClassLoaders.get(deDriver.getId());
        if (customJdbcClassLoader == null) {
            return addCustomJdbcClassLoader(deDriver);
        } else {
            if (StringUtils.isNotEmpty(customJdbcClassLoader.getDriver()) && customJdbcClassLoader.getDriver().equalsIgnoreCase(deDriver.getDriverClass())) {
                return customJdbcClassLoader;
            } else {
                customJdbcClassLoaders.remove(deDriver.getId());
                return addCustomJdbcClassLoader(deDriver);
            }
        }
    }
    private synchronized ExtendedJdbcClassLoader addCustomJdbcClassLoader(DatasourceDriver deDriver) throws Exception {
        if (!StringUtil.hasText(deDriver.getJarPath())){
            BaseException.throwException(ResultCode.get("datasource_driver_jarPath_is_null"));
        }
        String[] split = deDriver.getJarPath().split(StringPool.SLASH);
        String[] pathS = new String[split.length -1];
        for (int i = 0; i < split.length - 1; i++) {
            pathS[i] = split[i];
        }
        String custom_driver_path = String.join(StringPool.SLASH, pathS);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        while (classLoader.getParent() != null) {
            classLoader = classLoader.getParent();
            if (classLoader.toString().contains("ExtClassLoader")) {
                break;
            }
        }
        ExtendedJdbcClassLoader customJdbcClassLoader = new ExtendedJdbcClassLoader(new URL[]{new File(custom_driver_path + deDriver.getId()).toURI().toURL()}, classLoader);
        customJdbcClassLoader.setDriver(deDriver.getDriverClass());
        File file = new File(custom_driver_path);
        File[] array = file.listFiles();
        Optional.ofNullable(array).ifPresent(files -> {
            for (File tmp : array) {
                if (tmp.getName().endsWith(".jar")) {
                    try {
                        customJdbcClassLoader.addFile(tmp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        customJdbcClassLoaders.put(deDriver.getId(), customJdbcClassLoader);
        return customJdbcClassLoader;
    }

    public static void main(String[] args) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            Class.forName("com.taosdata.jdbc.TSDBDriver");
            String jdbcUrl = "jdbc:TAOS://10.255.102.104:6030/unittec_iot_tt?.level=1&project=1&user=root&password=taosdata";
            Connection conn = DriverManager.getConnection(jdbcUrl);
            Statement stmt = conn.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error");
        }
    }
}
