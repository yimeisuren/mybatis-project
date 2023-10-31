package com.xiong.com.xiong.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author xiong
 * @date 2022/10/23
 */
public class JDBCUtil {
    public static Connection getConnection() {
        Connection connection = null;
        try {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream("jdbc.properties");
            Properties properties = new Properties();
            properties.load(inputStream);

            String url = properties.getProperty("url");
            //info的setProperty()中的key是根据Driver的要求传入的, 固定写死
            //properties的getProperty()中的key是根据自己配置文件jdbc.properties中设置的值, 可以自定义, 和配置文件保持统一即可
            Properties info = new Properties();
            info.setProperty("username", properties.getProperty("user"));
            info.setProperty("password", properties.getProperty("pass"));
            info.setProperty("serverTimezone", properties.getProperty("timezone"));

            Class.forName(properties.getProperty("driver"));

            connection = DriverManager.getConnection(url, info);
        } catch (ClassNotFoundException | SQLException | IOException e) {
            e.printStackTrace();
        }
        //连接失败则返回null
        return connection;
    }


    public static void commonMethodWithoutQuery(String sql, Object... parameters) {
        Connection connection = JDBCUtil.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 1; i <= parameters.length; i++) {
                preparedStatement.setObject(i, parameters[i - 1]);
            }

            //todo: 查询操作需要额外对返回结果进行处理
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtil.close(preparedStatement, connection);
        }
    }

    /**
     * 传递泛型的方式, ioc容器中可以使用
     *
     * @param clazz
     * @param sql
     * @param parameters
     * @param <T>
     * @return
     */
    public static  <T> List<T> commonQuery(Class<T> clazz, String sql, Object... parameters) {
        Connection connection = JDBCUtil.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<T> result = new ArrayList<>();

        try {
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i]);
            }
            resultSet = preparedStatement.executeQuery();

            // 获取结果集的元数据, 为了确定resultSet中有多少个属性
            // 不能通过parameters.length来获取, 因为二者并不相等
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {

                //创建实体类对象
                T t = clazz.newInstance();

                //为对象set属性值
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    try {
                        //getColumnLabel可以获取sql语句中的别名, 用于解决实体类字段和数据库表字段不一致的问题
                        Object object = resultSet.getObject(i + 1);
                        //获取数据库列名的别名: 有别名则获取别名, 没有则原本字段名
                        String columnLabel = metaData.getColumnLabel(i + 1);
                        Field field = clazz.getDeclaredField(columnLabel);
                        field.setAccessible(true);
                        field.set(t, object);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                //添加到列表中
                result.add(t);
            }
        } catch (SQLException | InstantiationException | IllegalAccessException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtil.close(preparedStatement, connection, resultSet);
        }

        return result;
    }


    public static void close(PreparedStatement preparedStatement, Connection connection) {
        close(preparedStatement, connection, null);
    }

    public static void close(PreparedStatement preparedStatement, Connection connection, ResultSet resultSet) {
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
