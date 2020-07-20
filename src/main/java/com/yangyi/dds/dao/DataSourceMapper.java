package com.yangyi.dds.dao;

import com.yangyi.dds.domain.dto.DeleteDTO;
import com.yangyi.dds.domain.dto.InsertDTO;
import com.yangyi.dds.domain.dto.TableDTO;
import com.yangyi.dds.domain.dto.UpdateDTO;
import com.yangyi.dds.domain.vo.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author yangyi
 * @date 2020/07/01
 */
@Repository("dataSourceMapper")
public interface DataSourceMapper {

    /**
     * 获取当前数据源的所有数据库名称
     * @param vo 查询vo
     * @return 返回数据库名称 list
     */
    List<String> getDatabases(@Param("vo") DataBaseVO vo);

    /**
     * 获取当前数据源的所有数据表名称
     * @param vo 查询vo
     * @return 返回数据库名称 list
     */
    List<Map<String,Object>> getTables(@Param("vo") TableVO vo);

    /**
     * 判断表是否存在
     * @param vo 查询vo
     * @return 存在则返回该表的名称，否则返回 null
     */
    List<String> getTableByTableName(@Param("vo") TableVO vo);

    /**
     * 查询表数据
     * @param vo 查询条件 vo
     * @return List<Map<String,Object>>
     */
    List<Map<String,Object>> getTableData(@Param("vo") TableDataVO vo);

    /**
     * 动态表创建
     * @param dto 动态表dto
     */
    int createTable(@Param("dto") TableDTO dto);

    /**
     * 动态数据插入
     * @param dto 动态数据dto
     */
    int insert(@Param("dto") InsertDTO dto);

    /**
     * 动态数据更新
     * @param dto 数据更新 dto
     */
    int update(@Param("dto") UpdateDTO dto);

    /**
     * 动态数据更新
     * @param dto 数据更新 dto
     */
    int delete(@Param("dto") DeleteDTO dto);

    /**
     * 查询表字段
     * @param vo 字段查询 vo
     * @return List<Map<String,Object>>
     */
    List<Map<String,Object>> getTableFields(@Param("vo") FieldVO vo);

    /**
     * 查询 MySQL 数据源主键字段
     * @param vo 字段查询 vo
     * @return String
     */
    Map<String,Object> getPrimaryKeyForMySQL(@Param("vo") PrimaryKeyVO vo);

    /**
     * 查询 PostgreSQL 数据源主键字段
     * @param vo 字段查询 vo
     * @return String
     */
    Map<String,Object> getPrimaryKeyForPostgreSQL(@Param("vo") PrimaryKeyVO vo);
}
