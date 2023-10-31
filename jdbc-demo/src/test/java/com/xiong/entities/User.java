package com.xiong.com.xiong.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiong
 * @date 2022/10/23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /**
     * 实体类对象不应该出现基本数据类型, 例如将id设置为int类型, 则其默认值为0, 而不是null.
     * 如果查询语句没有选择id这一列, 则输出的对象中包含id的默认值0, 会造成歧义. 而null可以明显的推断出sql语句中没有select id这一个属性
     */
    Integer id;
    String username;
    String password;
}
