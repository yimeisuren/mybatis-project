package com.xiong.com.xiong.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author xiong
 * @date 2022/10/23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    Integer id;
    String name;
    Integer age;
    Date birth;
}
