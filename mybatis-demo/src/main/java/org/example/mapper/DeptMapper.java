package org.example.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.entities.Dept;

public interface DeptMapper {
    Dept queryById(@Param("id") int id);
}