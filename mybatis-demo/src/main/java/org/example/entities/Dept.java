package org.example.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
    * 部门表
    */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Dept {
    private Integer id;

    private String name;
}