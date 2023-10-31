package org.example.datasource;

import org.apache.ibatis.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

// 在mybatis-config.xml中配置别名，用以替换原本的数据源
public class MyDataSourceFactory implements DataSourceFactory {
    @Override
    public void setProperties(Properties props) {

    }

    @Override
    public DataSource getDataSource() {
        return null;
    }
}
