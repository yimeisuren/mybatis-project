package org.example.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.entities.User;

import java.util.List;

public interface UserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectByNameAndAge(@Param("xxx") String name, @Param("yyy") int age);

    List<User> selectByAge(int age);

    List<User> selectByName(@Param("name") String name);
}