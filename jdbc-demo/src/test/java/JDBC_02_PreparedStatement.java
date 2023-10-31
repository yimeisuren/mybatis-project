package com.xiong;

import com.xiong.com.xiong.entities.Person;
import com.xiong.com.xiong.entities.User;
import com.xiong.com.xiong.utils.JDBCUtil;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xiong
 * @date 2022/10/23
 */
public class JDBC_02_PreparedStatement {


    /**
     * sql注入问题, 将输出的字符串复制到sql文件中即可查看
     */
    @Test
    public void whyNotUseStatementTest() {
        //输入用户名和密码
        String username = "1' OR ";
        String password = " OR '1' = '1";

        String sql = String.format("SELECT `id`,`username`,`password` FROM `user` WHERE username = '%s' AND password = '%s'", username, password);
        System.out.println(sql);
    }

    @Test
    public void insertTest() {
        Connection connection = JDBCUtil.getConnection();
        PreparedStatement preparedStatement = null;
        String sql = "insert `user` values (?,?,?)";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setObject(1, 3);
            preparedStatement.setObject(2, "admin");
            preparedStatement.setObject(3, "admin");

            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtil.close(preparedStatement, connection);
        }
    }

    @Test
    public void deleteTest() {
        Connection connection = JDBCUtil.getConnection();
        PreparedStatement preparedStatement = null;
        String sql = "delete from `user` where `username` = ? and `password` = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setObject(1, "admin");
            preparedStatement.setObject(2, "admin");

            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtil.close(preparedStatement, connection);
        }
    }

    @Test
    public void updateTest() {
        //todo: 增删改的逻辑相同, 去除硬编码和循环次数即可抽象出增删改的通用方法
        Connection connection = JDBCUtil.getConnection();
        PreparedStatement preparedStatement = null;
        String sql = "update `user` set `password` = ? where `username` = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setObject(1, "user_update");
            preparedStatement.setObject(2, "user");


            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtil.close(preparedStatement, connection);
        }
    }

    /**
     * 通用增删改操作
     *
     * @param sql        sql执行语句
     * @param parameters 填充占位符
     */
    private void commonMethod(String sql, Object... parameters) {
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

    @Test
    public void commonTest() {
        String insertSql = "insert `user` values (?,?,?)";
        commonMethod(insertSql, 3, "admin", "admin");
    }


    @Test
    public void basicQueryTest() {
        Connection connection = JDBCUtil.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String sql = "select `username`, `password` from `user` where id > ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setObject(1, 1);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {

                //todo: 将返回结果封装到对象中, (1). 需要确定具体为哪些属性赋值; (2). 需要处理表名和实体类字段不匹配的问题
                String username = (String) resultSet.getObject(1);
                String password = (String) resultSet.getObject(2);
                System.out.println("username = " + username);
                System.out.println("password = " + password);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtil.close(preparedStatement, connection, resultSet);
        }
    }

    private List<User> commonQueryInUserTable(String sql, Object... parameters) {
        Connection connection = JDBCUtil.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<User> result = new ArrayList<>();

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

                //创建对象
                User user = new User();

                //为对象set属性值
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    try {
                        //todo: 需要解决表字段和实体类对象不一致的问题
                        Object object = resultSet.getObject(i + 1);
                        //获取数据库列名的别名: 有别名则获取别名, 没有则原本字段名
                        String columnName = metaData.getColumnLabel(i + 1);
                        Field field = User.class.getDeclaredField(columnName);
                        field.setAccessible(true);
                        field.set(user, object);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                //添加到列表中
                result.add(user);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtil.close(preparedStatement, connection, resultSet);
        }

        return result;
    }

    @Test
    public void commonQueryInUserTest() {
        String sql = "select username, password from `user` where id > ?";
        List<User> users = commonQueryInUserTable(sql, 1);
        users.forEach(System.out::println);
    }

    @Test
    public void commonQueryTest() {
        String sql = "select name, birthday birth from `person` where id > ?";
        List<Person> peoples = JDBCUtil.commonQuery(Person.class, sql, 0);
        peoples.forEach(System.out::println);
    }

}
