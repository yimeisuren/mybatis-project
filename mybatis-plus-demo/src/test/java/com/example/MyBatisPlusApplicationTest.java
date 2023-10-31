package com.example;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.mapper.UserMapper;
import org.example.model.po.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class MyBatisPlusApplicationTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void queryByIdTest() {
        User user = userMapper.selectById(1L);
        System.out.println(user);
    }

    @Test
    public void insertTest() {
        User user = new User();
        user.setName("张三");
        user.setEmail("张三@qq.com");
        int i = userMapper.insert(user);
        System.out.println("i = " + i);
    }

    @Test
    public void insertByAutoIdTest() {
        User user = new User();
        user.setAge(23);
        user.setName("李四");
        user.setEmail("李四@qq.com");
        int i = userMapper.insert(user);
        System.out.println("i = " + i);
    }

    @Test
    public void updateTest() {
        User user = new User();
        user.setId(1L);
        user.setEmail("Jone@@163.com");
        int i = userMapper.updateById(user);
        System.out.println(i);
    }

    /**
     * 乐观锁测试, 需要先查询, 再进行修改
     */
    @Test
    public void optimisticLockerTest() {
        User user = userMapper.selectById(1L);
        user.setEmail("Jone@hust.edu.cn");
        userMapper.updateById(user);
    }

    /**
     * 逻辑删除测试
     */
    @Test
    public void logicDeleteTest() {
        int i = userMapper.deleteById(1L);
        System.out.println("i = " + i);
    }


//     @Test
//     public void testOptimisticLocker() {
//
// //        两个不同的人在某一时刻同时想要对数据库进行操作
//         int id = 1;
//         User userA = userMapper.selectById(id);
//         // User userB = userMapper.selectById(id);
//         userA.setEmail("Jone@hust.edu.cn");
//         // userB.setUsername("world hello");
//
//         userMapper.updateById(userA);
//
//         //为了让两个人的操作都能够成功, 但这是模拟一个并发的过程, 强行加个while循环有什么意义呢?
//         int i = 0;
//         while (i == 0) {
//             userB = userMapper.selectById(id);
//             userB.setUsername("world hello");
//             i = userMapper.updateById(userB);
//         }
//
//         //查询userB是否执行成功? 乐观锁是执行一次, 失败还是重复执行, 直到成功? 只执行一次, 后面的事务执行失败
//         User user = userMapper.selectById(id);
//         System.out.println(user.getUsername());
//     }

    @Test
    public void testBatchSelect() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        List<User> users = userMapper.selectBatchIds(list);
        for (User user : users) {
            System.out.println(user);
        }
    }

    // @Test
    // public void testSelectMap() {
    //     Map<String, Object> map = userMapper.getMapById(1);
    //     System.out.println(map);
    // }

    @Test
    public void testSelectList() {
        List<User> users = userMapper.selectList(null);
        for (User user : users) {
            System.out.println("user = " + user);
        }
    }

    @Test
    public void testWrapper() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper: 构建一个复杂的查询条件
        queryWrapper.like("username", "root")
                .between("age", 10, 40)
                .isNotNull("email")
                .orderByDesc("age")
                .orderByAsc("sex");
        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
    }

    //and()和or()同时存在的情况
    @Test
    public void testQueryWrapper01() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        //默认情况就是and, 只有or的情况下需要使用.or()
        wrapper.gt("age", 30)
                .like("username", "ro")
                .or()
                .isNull("email");
        List<User> users = userMapper.selectList(wrapper);
        for (User user : users) {
            System.out.println(user);
        }
    }

    @Test
    public void testQueryWrapper02() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        //在and()中再次定义一个queryWrapper
        wrapper.gt("age", 30)
                .and(userQueryWrapper -> userQueryWrapper
                        .like("username", "ro")
                        .or()
                        .isNull("email"));
        List<User> users = userMapper.selectList(wrapper);
        for (User user : users) {
            System.out.println(user);
        }
    }


    //子查询
    @Test
    public void testSubQuery() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        //        设置要查询的字段, 而不是整张表中的所有字段
        wrapper.select("username", "password", "age");
        wrapper.inSql("age", "select age from user where username = 'root'");
        List<User> users = userMapper.selectList(wrapper);
        users.forEach(System.out::println);
    }

    @Test
    public void testConditionQueryWrapper() {
//        假设username, id等从其他地方获取得到, 需要判断输入的值再进行sql语句的拼接
        String username = "root";
        Integer id = 10;

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(username), "username", username)
                .ge(id != null, "uid", id);
        List<User> users = userMapper.selectList(wrapper);
        for (User user : users) {
            System.out.println(user);
        }
    }

    @Test
    public void testLambdaQueryWrapper() {
        Integer id = 10;
//        使用LambdaXXXXWrapper, 将上面的字符串改为方法名, 防止字符串写错
//        两种方式获取LambdaQueryWrapper
//         QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//         LambdaQueryWrapper<User> lambdaQueryWrapper = queryWrapper.lambda();

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(id != null, User::getId, id);
        List<User> users = userMapper.selectList(wrapper);
        for (User user : users) {
            System.out.println(user);
        }
    }


    //     @Autowired
//     private UserService userService;
//
//     @Test
//     public void testIService() {
//         long count = userService.count();
//         System.out.println(count);
//     }
//
//
    //分页插件, 分页查询
    @Test
    public void pageQueryTest() {
        //SELECT uid AS uIndex,username,password,age,gender AS sex,email,is_delete
        // FROM user
        // WHERE is_delete=0
        // LIMIT ?,?
        Page<User> page = new Page<>(2, 3);
        userMapper.selectPage(page, null);

        //获取page对象中的封装的所有数据
        List<User> users = page.getRecords();
        for (User user : users) {
            System.out.println(user);
        }

        System.out.println("page.getTotal() = " + page.getTotal());
    }
//
//     //自定义方法中使用分页插件
//     @Test
//     public void testMyPageFunction() {
//         Page<User> page = new Page<>(1, 3);
//         //getPageGtAge()是自定义mapper映射的方法, 只需要在方法签名中添加一个page对象即可, 相当于将查询结果保存到page对象中
//         userMapper.getPageGtAge(page, 25);
//         List<User> users = page.getRecords();
//         for (User user : users) {
//             System.out.println(user);
//         }
//     }
//


    // @Test
    // public void testEnum() {
    //     User user = new User();
    //     user.setUsername("root");
    //     user.setPassword("hello world");
    //     user.setSex(GenderEnum.FEMALE);
    //     userMapper.insert(user);
    // }
}
