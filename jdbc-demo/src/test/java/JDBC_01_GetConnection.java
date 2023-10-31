
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * 获取连接
 *
 * @author xiong
 * @date 2022/10/23
 */
public class JDBC_01_GetConnection {


    /**
     * 先执行user.sql文件准备实验环境
     */
    @Test
    public void jdbcFrameworkTest() {
        //设置一些连接信息
        // 1. url包括三部分: 协议, 子协议 和 子名称
        // 2. 协议为jdbc, http, https, files, ftp等
        // 3. 子协议认为是分支, 例如jdbc中可以操作mysql, oracle, db2等
        // 4. 子名称用来标识唯一路径, 例如 host + port + dbName
        String url = "jdbc:mysql://root@localhost:3306/jdbc";
        // String url = "jdbc:mysql://localhost:3306/jdbc";
        // 报错error: Access denied for user ''@'localhost' (using password: YES)
        Properties info = new Properties();
        info.setProperty("user", "root");
        info.setProperty("pass", "root");
        // mysql8.0+版本需要设置时区
        info.setProperty("timezone", "Asia/Shanghai");

        PreparedStatement preparedStatement = null;
        Connection connect = null;

        try {
            //Driver是sun公司制定的jdbc规范(接口), 实际操作应该指定具体的数据库驱动
            //new com.mysql.jdbc.Driver()指定mysql数据库的驱动, 表示接下来操作mysql数据库, 但也引入了第三方的api
            //todo: 将直接new第三方的类对象改为通过反射创建
            Driver driver = new com.mysql.cj.jdbc.Driver();

            //连接数据库<=>获取数据库连接对象
            connect = driver.connect(url, info);
            System.out.println(connect);

            //执行sql<=>获取sql执行对象, 并调用执行方法
            String sql = "insert into `user` values (?,?,?)";
            preparedStatement = connect.prepareStatement(sql);
            //填充占位符
            preparedStatement.setInt(1, 1);
            preparedStatement.setString(2, "root");
            preparedStatement.setString(3, "root");
            preparedStatement.execute(sql);

            // 如果涉及到时间
            // SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            // Date date = format.parse("2022-10-23");
            // preparedStatement.setDate(4, new java.sql.Date(date.getTime()));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {

            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            try {
                if (connect != null) {
                    connect.close();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    @Test
    public void getDriverClassByReflectionTest() {
        Properties info = new Properties();
        info.setProperty("user", "root");
        info.setProperty("pass", "root");
        info.setProperty("timezone", "Asia/Shanghai");
        String url = "jdbc:mysql://root@localhost:3306/jdbc";

        try {
            Driver driver = (Driver) Class.forName("com.mysql.cj.jdbc.Driver").newInstance();

            //todo: 使用DriverManager来连接数据库, 便于管理不同的数据库连接
            Connection connect = driver.connect(url, info);
            System.out.println(connect);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void replaceDriverWithDriverManagerTest() {
        //todo: 去除硬编码, 写入配置文件中
        String url = "jdbc:mysql://root@localhost:3306/jdbc";
        Properties info = new Properties();
        info.setProperty("user", "root");
        info.setProperty("pass", "root");
        info.setProperty("timezone", "Asia/Shanghai");

        try {
            //todo: mysql数据库中可以对注册驱动进一步优化
            Driver mysqlDriver = (Driver) Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            // 仅仅为了演示, url并不符合neo4j的要求
            //Driver neo4jDriver = (Driver) GraphDatabase.driver(url);

            DriverManager.registerDriver(mysqlDriver);
            Connection connection = DriverManager.getConnection(url, info);
            System.out.println(connection);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void optimizeJustInMySQLTest() {
        String url = "jdbc:mysql://root@localhost:3306/jdbc";
        Properties info = new Properties();
        info.setProperty("user", "root");
        info.setProperty("pass", "root");
        info.setProperty("timezone", "Asia/Shanghai");

        try {
            // mysql的驱动(即Driver的实现类)中专门写了静态代码块, 功能就是创建一个实例driver, 然后注册到DriverManager中
            // 静态代码块在类加载的时候执行, 因此Class.forName()加载类的时候会执行这个代码块
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection(url, info);
            System.out.println(connection);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 优点: 和具体的数据库驱动解耦, 想要切换数据库驱动, 只需要在配置文件中设置即可, 不需要修改代码.
     * <p>
     * todo: 如果是neo4j这种数据库, 似乎不可以通过Class.forName()来直接获取Driver类实例对象, 那这样写有用吗?
     */
    @Test
    public void getPropertiesByConfigFileTest() {
        PreparedStatement preparedStatement = null;
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
            System.out.println(connection);

            String sql = "";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (ClassNotFoundException | SQLException | IOException e) {
            e.printStackTrace();
        } finally {
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

        }
    }

}
