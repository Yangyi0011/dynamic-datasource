package com.yangyi.dds.service;

import com.github.pagehelper.PageInfo;
import com.yangyi.dds.domain.dto.*;
import com.yangyi.dds.domain.vo.DataBaseVO;
import com.yangyi.dds.domain.vo.FieldVO;
import com.yangyi.dds.domain.vo.TableDataVO;
import com.yangyi.dds.domain.vo.TableVO;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author yangyi
 * @date 2020/06/30
 */
public interface DataSourceService {

    /**
     * 获取支持的数据源类型
     * @return 数据源类型 set
     */
    Map<String, Object> getDataSourceType();

    /**
     * 数据源连接测试
     * @param dto 数据源连接信息
     * @return 测试通过返回 true，否则返回 false
     */
    boolean testConnection(DataSourceDTO dto);

    /**
     * 获取当前使用的数据源 key
     * @return 返回数据源的key
     */
    String getCurrentDataSource();

    /**
     * 获取所有存储的数据源信息
     * @return
     */
    List<Map<Object, Object>> getDataSources();

    /**
     * 切换数据源
     * @param dataSourceKey 数据源key
     * @return 切换成功返回 true，否则返回 false
     */
    boolean changeDataSource(String dataSourceKey);

    /**
     * 切换数据源
     * @param dto 数据源dto
     * @return 切换成功返回 true，否则返回 false
     */
    boolean changeDataSource(DataSourceDTO dto);

    /**
     * 查询所有数据库名称（默认数据源）
     * @param vo 查询vo
     * @return PageInfo
     */
    PageInfo getDatabases(DataBaseVO vo);

    /**
     * 查询所有数据库名称（指定数据源）
     * @param dto 数据源dto
     * @param vo 数据源查询vo
     * @return 连接失败返回“连接失败”，连接成功返回 PageInfo
     */
    PageInfo getDatabases(DataSourceDTO dto, DataBaseVO vo);

    /**
     * 查询所有数据库名称（默认数据源）
     * @param vo 数据表查询vo
     * @return PageInfo
     */
    PageInfo getTables(TableVO vo);

    /**
     * 查询所有数据库名称（指定数据源）
     * @param dto 数据源dto
     * @param vo 数据表查询vo
     * @return 连接失败返回“连接失败”，连接成功返回 PageInfo
     */
    PageInfo getTables(DataSourceDTO dto, TableVO vo);

    /**
     * 动态数据查询（默认数据源）
     * @param vo 查询条件 vo
     * @return 返回表数据 PageInfo
     */
    PageInfo select(TableDataVO vo);

    /**
     * 动态数据查询（指定数据源）
     * @param dto 数据源dto
     * @param vo 表数据查询vo
     * @return 连接失败返回“连接失败”，连接成功返回 Map<String, Object>
     */
    Map<String, Object> select(DataSourceDTO dto, TableDataVO vo);

    /**
     * 查询表字段（默认数据源）
     * @param vo 字段查询VO
     * @return PageInfo
     */
    PageInfo getTableFields(FieldVO vo);

    /**
     * 查询表字段（指定数据源）
     * @param dto 数据源dto
     * @param vo 字段查询VO
     * @return PageInfo
     */
    PageInfo getTableFields(DataSourceDTO dto, FieldVO vo);

    /**
     * 判断表是否存在（默认数据源）
     * @param vo 请求vo
     * @return 存在返回 true，否则返回 false
     */
    boolean isTableExist(TableVO vo);

    /**
     * 判断表是否存在（指定数据源）
     * @param dto 数据源dto
     * @param vo 请求vo
     * @return 存在返回 true，否则返回 false
     */
    boolean isTableExist(DataSourceDTO dto, TableVO vo);

    /**
     * 动态表创建（默认数据源）
     * @param dto 动态表dto
     * @return 操作成功返回 true，否则返回 false
     */
    boolean createTable(TableDTO dto);

    /**
     * 动态表创建（指定数据源）
     * @param dataSourceDTO 数据源dto
     * @param tableDTO 动态表dto
     * @return 操作成功返回 true，否则返回 false
     */
    boolean createTable(DataSourceDTO dataSourceDTO, TableDTO tableDTO);

    /**
     * 动态数据插入（默认数据源）
     * @param dto 数据插入dto
     * @return 操作成功返回 true，否则返回 false
     */
    boolean insert(InsertDTO dto);

    /**
     * 动态数据插入（指定数据源）
     * @param dataSourceDTO 数据源dto
     * @param insertDTO 数据插入dto
     * @return 操作成功返回 true，否则返回 false
     */
    boolean insert(DataSourceDTO dataSourceDTO, InsertDTO insertDTO);

    /**
     * 动态数据更新（默认数据源）
     * @param dto 数据更新dto
     * @return 操作成功返回 true，否则返回 false
     */
    boolean update(UpdateDTO dto);

    /**
     * 动态数据更新（指定数据源）
     * @param dataSourceDTO 数据源dto
     * @param updateDTO 数据更新dto
     * @return 操作成功返回 true，否则返回 false
     */
    boolean update(DataSourceDTO dataSourceDTO, UpdateDTO updateDTO);

    /**
     * 动态数据删除（默认数据源）
     * @param dto 数据删除dto
     * @return 操作成功返回 true，否则返回 false
     */
    boolean delete(DeleteDTO dto);

    /**
     * 动态数据批量删除（默认数据源）
     * @param dto 批量数据删除dto
     * @return 操作成功返回 true，否则返回 false
     */
    boolean batchDelete(BatchDeleteDTO dto);

    /**
     * 动态数据删除（指定数据源）
     * @param dataSourceDTO 数据源dto
     * @param deleteDTO 数据删除dto
     * @return 操作成功返回 true，否则返回 false
     */
    boolean delete(DataSourceDTO dataSourceDTO, DeleteDTO deleteDTO);

    /**
     * 动态数据批量删除（指定数据源）
     * @param dataSourceDTO 数据源dto
     * @param batchDeleteDTO 批量数据删除dto
     * @return 操作成功返回 true，否则返回 false
     */
    boolean batchDelete(DataSourceDTO dataSourceDTO, BatchDeleteDTO batchDeleteDTO);
}
