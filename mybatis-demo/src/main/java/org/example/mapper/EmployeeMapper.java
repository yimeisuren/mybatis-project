package org.example.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.entities.Employee;

public interface EmployeeMapper {
    Employee queryBaseEmployeeById(@Param("id") int id);

    Employee queryBaseEmployeeOverFlowById(@Param("id") int id);

    Employee queryDeptEmployeeById(@Param("id") int id);

    Employee queryDeptEmployeeByIdWithSubQuery(@Param("id") int id);
}