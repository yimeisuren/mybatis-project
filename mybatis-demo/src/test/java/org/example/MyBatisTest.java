package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.entities.Employee;
import org.example.entities.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.example.mapper.EmployeeMapper;
import org.example.mapper.UserMapper;
import org.junit.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

// @Slf4j的作用就是生成一行代码，这行代码能够顺利工作需要在pom.xml中引入日志框架的实现
@Slf4j
public class MyBatisTest {
    private static final String RESOURCE = "mybatis-config.xml";
    private static InputStream inputStream;
    private static SqlSessionFactory sqlSessionFactory;
    private SqlSession sqlSession;

    // BeforeClass和AfterClass都是静态方法，只执行一次，分别在所有的测试方法执行前后执行
    @BeforeClass
    public static void getSqlSessionFactory() throws Exception {
        //加载核心配置文件mybatis-config.xml
        inputStream = Resources.getResourceAsStream(RESOURCE);
        //获取sqlSessionFactoryBuilder
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        //获取sqlSessionFactory
        sqlSessionFactory = sqlSessionFactoryBuilder.build(inputStream);
    }

    @AfterClass
    public static void close() throws Exception {
        inputStream.close();
    }


    @Before
    public void getSqlSession() {
        //获取sqlSession: sqlSession表示java程序和数据库之间和会话
        sqlSession = sqlSessionFactory.openSession(true);
    }

    @After
    public void sqlSessionCommitAndClose() {
        //提交事务，否则修改不生效，设置autoCommit=true可以实现自动提交
        sqlSession.commit();
        sqlSession.close();
    }

    @Test
    public void sqlSessionTest() {
        sqlSession.selectList("org.example.mapper.UserMapper.selectByName");
    }

    @Test
    public void userMapperTest() throws IOException {
        //目标是获取Mapper接口的对象，但是Mapper接口并没有实现类
        //getMapper()实际上创建了Mapper接口的实现类，并返回实现类对象，代理模式
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        //进行功能的测试
        User user = userMapper.selectByNameAndAge("Jone", 18);

        // List<User> users = userMapper.selectByAge(18);
        List<User> users = userMapper.selectByName("J");

        log.info("[{}]", users);
    }

    @Test
    public void employeeMapperTest() {
        EmployeeMapper employeeMapper = sqlSession.getMapper(EmployeeMapper.class);
        Employee baseEmployee = employeeMapper.queryBaseEmployeeById(1);
        System.out.println("baseEmployee = " + baseEmployee);

        Employee employeeOverFlow = employeeMapper.queryBaseEmployeeOverFlowById(1);
        System.out.println("employeeOverFlow = " + employeeOverFlow);

        Employee deptEmployee = employeeMapper.queryDeptEmployeeById(1);
        System.out.println("deptEmployee = " + deptEmployee);

        // 需要注意避免缓存的影响
        Employee deptEmployeeWithSubQuery = employeeMapper.queryDeptEmployeeByIdWithSubQuery(1);
        System.out.println("deptEmployeeWithSubQuery = " + deptEmployeeWithSubQuery);

    }


}
