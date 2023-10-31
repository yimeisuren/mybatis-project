package org.example.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 员工表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    private Integer id;

    private String name;

    private Integer age;

    /**
     * 部门信息
     * <p>
     * 实体类中不使用外键，而是使用外键对应的对象来维护关系
     * <p>
     * 如果仅仅需要Dept对象中的少部分属性，为了效率也可以直接将Dept对象中的属性直接放置到Employee类中。（从项目的效率出发考虑）
     */
    private Dept dept;
}