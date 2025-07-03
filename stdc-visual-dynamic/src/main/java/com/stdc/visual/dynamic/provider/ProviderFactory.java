package com.stdc.visual.dynamic.provider;

import com.stdc.visual.dynamic.base.datasource.dto.DatasourceTypes;
import com.stdc.visual.dynamic.provider.datasource.DatasourceProvider;
import com.stdc.visual.dynamic.provider.query.DDLProvider;
import com.stdc.visual.dynamic.provider.query.QueryProvider;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


/**
 * @author: wang_jie
 * @data: 2022/5/16--19:17
 * @describe: 多数据源工厂
 */
@Component
public class ProviderFactory implements ApplicationContextAware {


    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public static DatasourceProvider getProvider(String type) {
        DatasourceTypes datasourceType = DatasourceTypes.valueOf(type);
        switch (datasourceType) {
            case es:
                return context.getBean("es", DatasourceProvider.class);
            default:
                return context.getBean("jdbc", DatasourceProvider.class);
        }
    }

    public static QueryProvider getQueryProvider(String type) {
        DatasourceTypes datasourceType = DatasourceTypes.valueOf(type);
        switch (datasourceType) {
            case tdengine:
                return context.getBean("TdengineQuery", QueryProvider.class);
            case mysql:
            case mariadb:
            case ds_doris:
                return context.getBean("mysqlQuery", QueryProvider.class);
            case gbase:
                return context.getBean("gbaseQuery", QueryProvider.class);
            case kingbase:
                return context.getBean("kingbaseQuery", QueryProvider.class);
            case de_doris:
                return context.getBean("dorisQuery", QueryProvider.class);
            case sqlServer:
                return context.getBean("sqlserverQuery", QueryProvider.class);
            case pg:
                return context.getBean("pgQuery", QueryProvider.class);
            case oracle:
                return context.getBean("oracleQuery", QueryProvider.class);
            case es:
                return context.getBean("esQuery", QueryProvider.class);
            case ck:
                return context.getBean("ckQuery", QueryProvider.class);
            case mongo:
                return context.getBean("mongoQuery", QueryProvider.class);
            case redshift:
                return context.getBean("redshiftQuery", QueryProvider.class);
            case hive:
                return context.getBean("hiveQuery", QueryProvider.class);
            case db2:
                return context.getBean("db2Query", QueryProvider.class);
            case dm:
                return context.getBean("dmQuery", QueryProvider.class);
            default:
                return context.getBean("mysqlQuery", QueryProvider.class);
        }
    }

    public static DDLProvider getDDLProvider(String type) {
        DatasourceTypes datasourceType = DatasourceTypes.valueOf(type);
        switch (datasourceType) {
            case de_doris:
                return context.getBean("dorisDDL", DDLProvider.class);
            default:
                return context.getBean("dorisDDL", DDLProvider.class);
        }
    }
}
