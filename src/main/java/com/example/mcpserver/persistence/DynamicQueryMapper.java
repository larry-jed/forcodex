package com.example.mcpserver.persistence;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DynamicQueryMapper {

    @Select("${sql}")
    List<Map<String, Object>> execute(@Param("sql") String sql);
}
