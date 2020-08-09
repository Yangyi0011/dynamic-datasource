package com.yangyi.dds.service.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yangyi.dds.config.DynamicDataSource;
import com.yangyi.dds.config.DynamicDataSourceContextHolder;
import com.yangyi.dds.controller.validation.ValidationGroup_Add;
import com.yangyi.dds.controller.validation.ValidationGroup_Update;
import com.yangyi.dds.dao.DataSourceMapper;
import com.yangyi.dds.domain.dto.*;
import com.yangyi.dds.domain.vo.*;
import com.yangyi.dds.exception.*;
import com.yangyi.dds.service.DataSourceService;
import com.yangyi.dds.util.DataSourceUtil;
import com.yangyi.dds.util.OrderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author yangyi
 * @date 2020/06/30
 */
@Slf4j
@Service("dataSourceService")
public class DataSourceServiceImpl implements DataSourceService {

    @Qualifier("dataSourceMapper")
    @Autowired
    private DataSourceMapper dataSourceMapper;

    @Override
    public Map<String, Object> getDataSourceType() {
        return DataSourceUtil.dataSourceMap;
    }

    @Override
    public boolean testConnection(@Validated(value = {ValidationGroup_Add.class}) DataSourceDTO dto) {

        // 拼接 url
        String url = DataSourceUtil.getUrlByDataSourceDTO(dto);

        // 获取对应的驱动类
        String driverClass = DataSourceUtil.getDriverClassByType(dto.getType());

        return DataSourceUtil.testConnection(driverClass, url, dto.getUsername(), dto.getPassword());
    }

    @Override
    public String getCurrentDataSource() {

        return DynamicDataSourceContextHolder.getDataSourceKey();
    }

    @Override
    public List<Map<Object, Object>> getDataSources() {

        Map<Object, Object> dataSourceMap = DynamicDataSource.getInstance().getDataSourceMap();

        List<Map<Object, Object>> list = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : dataSourceMap.entrySet()) {

            Object key = entry.getKey();
            DruidDataSource value = (DruidDataSource) entry.getValue();

            String username = value.getUsername();
            String password = value.getPassword();
            String driverClass = value.getDriverClassName();
            String url = value.getUrl();

            HashMap<String, Object> info = new HashMap<>();
            info.put("url", url);
            info.put("driverClass", driverClass);
            info.put("username", username);
            info.put("password", password);

            HashMap<Object, Object> dataSource = new HashMap<>();
            dataSource.put(key, info);

            list.add(dataSource);
        }

        return list;
    }

    /**
     * 切换数据源
     *
     * @param dataSourceKey 数据源key
     * @return 切换成功返回 true，否则返回 false
     */
    @Override
    public boolean changeDataSource(String dataSourceKey) {

        // 指定默认的数据源key
        String dbSourceKey = "default";

        // 如果不指定数据库，则直接连接默认数据库
        if (!StringUtils.isEmpty(dataSourceKey)) {
            dbSourceKey = dataSourceKey.trim();
        }

        // 获取当前连接的数据源对象的key
        String currentKey = DynamicDataSourceContextHolder.getDataSourceKey();

        // 如要切换的数据源就是当前数据源，直接返回成功
        if (dbSourceKey.equals(currentKey)) {
            return true;
        }

        // 储存动态数据源的map不存在该数据源
        if (!DynamicDataSource.isExistDataSource(dbSourceKey)) {
            throw new DataSourceNotExistedException("操作失败，" + dataSourceKey + " 数据源不存在");
        }

        // 存在则直接切换
        DynamicDataSourceContextHolder.setDataSourceKey(dataSourceKey);

        if (log.isInfoEnabled()) {
            log.info("数据源切换完毕，当前数据源为：{}", DynamicDataSourceContextHolder.getDataSourceKey());
        }

        return true;
    }

    /**
     * 数据源切换
     *
     * @param dto 数据源dto
     * @return 切换成功返回 true，否则返回 false
     */
    @Override
    public boolean changeDataSource(@Validated(value = {ValidationGroup_Add.class}) DataSourceDTO dto) {

        // 先测试数据源可用性
        boolean test = testConnection(dto);
        if (!test) {
            throw new DataSourceConnectionException("数据源连接失败");
        }

        // 获取当前的数据源 key
        String currentKey = DynamicDataSourceContextHolder.getDataSourceKey();
        if (currentKey.equals(dto.getDataSourceKey())) {

            if (log.isInfoEnabled()) {
                log.info("数据源切换完毕，当前数据源为：{}", DynamicDataSourceContextHolder.getDataSourceKey());
            }

            // 如要切换的数据源就是当前数据源，直接返回成功
            return true;
        }

        // 拼接URL
        String url = DataSourceUtil.getUrlByDataSourceDTO(dto);

        // 设置数据源默认的key
        /*if (StringUtils.isEmpty(dto.getDataSourceKey())) {
            // 设置数据源的key，默认采用：jdbc的连接url（不带后缀） 作为key，如：jdbc:mysql://127.0.0.1:3306/test
            int i = url.indexOf("?");
            String key = url.substring(0,i == -1 ? url.length() : i);
            dto.setDataSourceKey(key);
        }*/

        // 不再支持手动指定 dataSourceKey
        int i = url.indexOf("?");
        String key = url.substring(0, i == -1 ? url.length() : i);
        dto.setDataSourceKey(key);

        // 判断储存动态数据源实例的map中key值是否存在
        if (DynamicDataSource.isExistDataSource(dto.getDataSourceKey())) {

            // 存在则直接切换
            DynamicDataSourceContextHolder.setDataSourceKey(dto.getDataSourceKey());

            if (log.isInfoEnabled()) {
                log.info("数据源切换完毕，当前数据源为：{}", DynamicDataSourceContextHolder.getDataSourceKey());
            }

            return true;
        }

        // 数据源不存在则先创建再切换
        DruidDataSource dataSource = DataSourceUtil.createDataSource(dto);
        if (dataSource == null) {
            throw new DataSourceCreateException("数据源创建异常");
        }

        // 切换数据源
        DynamicDataSourceContextHolder.setDataSourceKey(dto.getDataSourceKey());

        if (log.isInfoEnabled()) {
            log.info("数据源切换完毕，当前数据源为：{}", DynamicDataSourceContextHolder.getDataSourceKey());
        }

        return true;
    }

    /**
     * 查询所有数据库名称（默认数据源）
     *
     * @param vo 查询vo
     * @return 返回数据库名称 PageInfo
     */
    @Override
    public PageInfo getDatabases(@Validated(value = {ValidationGroup_Add.class}) DataBaseVO vo) {

        if (vo == null) {
            return null;
        }

        // 获取当前数据源 key
        String dataSourceKey = DynamicDataSourceContextHolder.getDataSourceKey();

        // 获取对应的数据源信息
        DruidDataSource dataSource = DynamicDataSource.getInstance().getDataSourceByKey(dataSourceKey);

        if (dataSource == null) {
            log.error("错误，数据源map中获取不到当前的数据源，key={}", dataSourceKey);
            throw new FindException("操作失败，当前数据源不存在或已被删除");
        }

        // 获取数据源连接url
        String url = dataSource.getUrl();

        // 获取数据源类型
        String type = DataSourceUtil.getTypeByUrl(url);

        if (DataSourceUtil.MYSQL_NAME.equalsIgnoreCase(type)) {
            // mysql 数据源
            vo.setTargetField(DataSourceUtil.MYSQL_TARGET_SCHEMA_FIELD);
            vo.setTargetSource(DataSourceUtil.MYSQL_TARGET_SCHEMA_SOURCE);
            vo.setType(DataSourceUtil.MYSQL_NAME);
        } else if (DataSourceUtil.POSTGRESQL_NAME.equalsIgnoreCase(type)) {
            // postgreSQL 数据源
            vo.setTargetField(DataSourceUtil.POSTGRESQL_TARGET_SCHEMA_FIELD);
            vo.setTargetSource(DataSourceUtil.POSTGRESQL_TARGET_SCHEMA_SOURCE);
            vo.setType(DataSourceUtil.POSTGRESQL_NAME);
        } else {
            throw new FindException("操作失败，不支持的数据源类型：" + type);
        }

        PageHelper.startPage(vo.getPageIndex(), vo.getPageSize());
        List<String> list = dataSourceMapper.getDatabases(vo);
        PageInfo<String> pageInfo = new PageInfo<>(list);
        PageHelper.clearPage();

        return pageInfo;
    }

    /**
     * 查询所有数据库名称（指定数据源）
     *
     * @param dto 数据源dto
     * @param vo  数据库查询vo
     * @return 连接失败返回“连接失败”，连接成功返回该数据源的所有数据库数据
     */
    @Override
    public PageInfo getDatabases(@Validated(value = {ValidationGroup_Add.class}) DataSourceDTO dto,
                                 @Validated(value = {ValidationGroup_Add.class}) DataBaseVO vo) {
        // 数据源的切换/释放交给AOP去处理
        // 查询数据
        return getDatabases(vo);
    }

    /**
     * 查询所有数据表名称（默认数据源）
     *
     * @param vo 数据表查询vo
     * @return 返回数据库名称 PageInfo
     */
    @Override
    public PageInfo getTables(@Validated(value = {ValidationGroup_Add.class}) TableVO vo) {

        if (vo == null) {
            return null;
        }

        // 获取当前数据源
        String type = DataSourceUtil.getCurrentDataSourceType();
        vo.setType(type);

        PageHelper.startPage(vo.getPageIndex(), vo.getPageSize());
        List<Map<String, Object>> list = dataSourceMapper.getTables(vo);
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list);
        PageHelper.clearPage();

        return pageInfo;
    }

    /**
     * 查询所有数据库名称（指定数据源）
     *
     * @param dto 数据源dto
     * @param vo  数据表查询vo
     * @return 连接失败返回“连接失败”，连接成功返回该数据源的所有数据库数据
     */
    @Override
    public PageInfo getTables(@Validated(value = {ValidationGroup_Add.class}) DataSourceDTO dto,
                              @Validated(value = {ValidationGroup_Add.class}) TableVO vo) {
        // 数据源的切换/释放交给AOP去处理
        // 查询数据
        return getTables(vo);

    }

    /**
     * 查询表字段（默认数据源）
     *
     * @param vo 字段查询VO
     * @return PageInfo
     */
    @Override
    public PageInfo getTableFields(@Validated(value = {ValidationGroup_Add.class}) FieldVO vo) {

        if (vo == null) {
            return null;
        }

        // 获取当前数据源类型
        String type = DataSourceUtil.getCurrentDataSourceType();

        // 构建主键查询vo
        PrimaryKeyVO primaryKeyVO = new PrimaryKeyVO();
        primaryKeyVO.setTableSchema(vo.getTableSchema());
        primaryKeyVO.setTableName(vo.getTableName());

        // 主键
        String primaryKey = null;
        // 主键字段
        Object column_name = null;
        Map<String, Object> fieldMap = null;

        // 查找主键
        if (DataSourceUtil.MYSQL_NAME.equalsIgnoreCase(type)) {

            // 设置 mysql 字段类型的列名
            vo.setTargetFieldType(DataSourceUtil.MYSQL_TARGET_FIELD_TYPE);

            // 查询 MySQL 主键字段
            fieldMap = dataSourceMapper.getPrimaryKeyForMySQL(primaryKeyVO);
        } else if (DataSourceUtil.POSTGRESQL_NAME.equalsIgnoreCase(type)) {

            // 设置 PostgreSQL 字段类型的列名
            vo.setTargetFieldType(DataSourceUtil.POSTGRESQL_TARGET_FIELD_TYPE);

            // 查询 PostgreSQL 主键字段
            fieldMap = dataSourceMapper.getPrimaryKeyForPostgreSQL(primaryKeyVO);
        } else {
            throw new FindException("操作失败，不支持的数据源类型：" + type);
        }

        // 提取主键字段
        if (fieldMap != null) {
            column_name = fieldMap.get("column_name");
        }

        // 主键字段赋值
        if (!StringUtils.isEmpty(column_name)) {
            primaryKey = column_name.toString();
        }

        // 查找字段
        PageHelper.startPage(vo.getPageIndex(), vo.getPageSize());
        List<Map<String, Object>> list = dataSourceMapper.getTableFields(vo);
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list);
        PageHelper.clearPage();

        // 遍历所有字段，确定是不是主键字段
        List<Map<String, Object>> resultList = new ArrayList<>(list.size());
        for (Map<String, Object> map : list) {

            // 字段名称（必定存在，不会为空）
            String fieldName = map.get("field_name").toString();

            if (fieldName.equals(primaryKey)) {
                map.put("isPrimaryKey", true);
            } else {
                map.put("isPrimaryKey", false);
            }

            resultList.add(map);
        }

        // 替换分页里的list
        pageInfo.setList(resultList);

        return pageInfo;
    }

    /**
     * 查询表字段（指定数据源）
     *
     * @param dto 数据源dto
     * @param vo  字段查询VO
     * @return PageInfo
     */
    @Override
    public PageInfo getTableFields(@Validated(value = {ValidationGroup_Add.class}) DataSourceDTO dto,
                                   @Validated(value = {ValidationGroup_Add.class}) FieldVO vo) {
        // 数据源的切换/释放交给AOP去处理
        // 查询数据
        return getTableFields(vo);

    }

    /**
     * 判断表是否存在（默认数据源）
     *
     * @param vo 请求vo
     * @return 存在返回 true，否则返回 false
     */
    @Override
    public boolean isTableExist(@Validated(value = {ValidationGroup_Add.class}) TableVO vo) {

        Assert.notNull(vo, "请先指定表名");
        Assert.isTrue(!StringUtils.isEmpty(vo.getTableName()), "请先指定表名");

        List<String> list = dataSourceMapper.getTableByTableName(vo);
        return list != null && list.size() != 0;
    }

    /**
     * 判断表是否存在（指定数据源）
     *
     * @param vo 请求vo
     * @return 存在返回 true，否则返回 false
     */
    @Override
    public boolean isTableExist(@Validated(value = {ValidationGroup_Add.class}) DataSourceDTO dto,
                                @Validated(value = {ValidationGroup_Add.class}) TableVO vo) {
        // 数据源的切换/释放交给AOP去处理
        // 判断表是否存在
        return isTableExist(vo);

    }

    /**
     * 动态表创建（指定数据源）
     *
     * @param dataSourceDTO 数据源dto
     * @param tableDTO      动态表dto
     * @return 操作成功返回 true，否则返回 false
     */
    @Override
    public boolean createTable(@Validated(value = {ValidationGroup_Add.class}) DataSourceDTO dataSourceDTO,
                               @Validated(value = {ValidationGroup_Add.class}) TableDTO tableDTO) {
        // 数据源的切换/释放交给AOP去处理
        // 动态表创建
        return createTable(tableDTO);

    }

    /**
     * 动态表创建（默认数据源）
     *
     * @param dto 动态表dto
     * @return 操作成功返回 true，否则返回 false
     */
    @Override
    public boolean createTable(@Validated(value = {ValidationGroup_Add.class}) TableDTO dto) {

        if (dto == null) {
            return false;
        }

        Assert.notNull(dto.getDataMap(), "表的字段信息不能为空");
        Assert.isTrue(dto.getDataMap().size() > 0, "表的字段信息不能为空");

        // 构建 查询vo
        TableVO tableVO = new TableVO();
        tableVO.setTableSchema(dto.getTableSchema())
                .setTableName(dto.getTableName());

        // 判断表是否存在
        boolean tableExist = isTableExist(tableVO);
        if (tableExist) {

            // 表已存在直接返回成功
            return true;
            // throw new TableCreateException("创建失败，表 " + dto.getTableSchema() + "." +dto.getTableName() + " 已存在");
        }

        // 设置备注
        // 获取当前数据源类型
        String type = DataSourceUtil.getCurrentDataSourceType();
        dto.setType(type);

        // 设置主键
        String primaryKey = dto.getTableName() + "_id";
        Map<String, Object> dataMap = dto.getDataMap();
        if (DataSourceUtil.MYSQL_NAME.equalsIgnoreCase(type)) {
            dataMap.put(primaryKey, "int(10) unsigned NOT NULL AUTO_INCREMENT");
        } else {
            dataMap.put(primaryKey, "serial");
        }
        dataMap.put("PRIMARY KEY", "(" + primaryKey + ")");

        // 创建表
        dataSourceMapper.createTable(dto);

        return true;
    }

    /**
     * 动态数据查询（默认数据源）
     *
     * @param vo 查询条件 vo
     * @return 返回表数据 PageInfo
     */
    @Override
    public PageInfo select(@Validated(value = {ValidationGroup_Add.class}) TableDataVO vo) {
        if (vo == null) {
            return null;
        }

        // 校验排序规则
        if (vo.getOrderMap() != null && vo.getOrderMap().size() > 0) {
            vo.getOrderMap().forEach((key, value) -> {

                if (!OrderUtil.JDBC_ORDER.containsKey(value)) {
                    throw new FindException("错误的排序规则：" + value + "，请查询字典后重试");
                }
            });
        }

        PageHelper.startPage(vo.getPageIndex(), vo.getPageSize());
        List<Map<String, Object>> list = dataSourceMapper.getTableData(vo);
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list);
        PageHelper.clearPage();

        return pageInfo;
    }

    /**
     * 动态数据查询（指定数据源）
     *
     * @param dto 数据源dto
     * @param vo  表数据查询vo
     * @return 连接失败返回“连接失败”，连接成功返回 Map<String, Object>
     */
    @Override
    public Map<String, Object> select(@Validated(value = {ValidationGroup_Add.class}) DataSourceDTO dto,
                                      @Validated(value = {ValidationGroup_Add.class}) TableDataVO vo) {
        // 数据源的切换/释放交给AOP去处理

        // 结果 map，存放 表数据分页信息 和 表字段信息
        HashMap<String, Object> resultMap = new HashMap<>();

        // 构建表字段查询vo
        FieldVO fieldVO = new FieldVO();
        fieldVO.setTableSchema(vo.getTableSchema())
                .setTableName(vo.getTableName());

        // 获取当前数据源类型
        String type = DataSourceUtil.getCurrentDataSourceType();
        if (DataSourceUtil.MYSQL_NAME.equalsIgnoreCase(type)) {

            // mysql 数据源
            fieldVO.setTargetFieldType(DataSourceUtil.MYSQL_TARGET_FIELD_TYPE);

        } else if (DataSourceUtil.POSTGRESQL_NAME.equalsIgnoreCase(type)) {

            // PostGre 数据源
            fieldVO.setTargetFieldType(DataSourceUtil.POSTGRESQL_TARGET_FIELD_TYPE);

        } else {
            throw new FindException("操作失败，不支持的数据源类型：" + type);
        }

        // 查询表字段
        List<Map<String, Object>> fieldList = getTableFields(fieldVO).getList();
        resultMap.put("fieldList", fieldList);

        // 主键
        String primaryKey = null;

        // 设置默认的查询字段（查全部字段）
        if (vo.getTargetFields() == null || vo.getTargetFields().size() == 0) {

            if (fieldList != null && fieldList.size() != 0) {

                // 构造数据查询字段 list
                List<String> targetFields = new ArrayList<>();
                for (Map<String, Object> map : fieldList) {

                    // 获取字段名称
                    String fieldName = map.get("field_name").toString();
                    // 获取字段类型
                    String fieldType = map.get("field_type").toString();

                    // 当前字段是否是主键
                    Boolean isPrimaryKey = Boolean.valueOf(map.get("isPrimaryKey").toString());
                    if (isPrimaryKey) {
                        primaryKey = fieldName;
                    }

                    // 处理 PostgreSQL 的特殊字段类型 geometry
                    if ("geometry".equalsIgnoreCase(fieldType)) {

                        // 使用 st_astext 函数对该字段进行数据解析
                        fieldName = "st_astext(" + fieldName + ") as " + fieldName;
                    }

                    // 装入集合
                    targetFields.add(fieldName);
                }

                // 设置查询
                vo.setTargetFields(targetFields);
            }
        }

        // 设置默认的排序规则
        if (vo.getOrderMap() == null || vo.getOrderMap().size() == 0) {
            Map<String, String> orderMap = new HashMap<>();

            // 没有主键则用第一个字段做排序，否则用主键做排序
            if (StringUtils.isEmpty(primaryKey)) {
                if (vo.getTargetFields() != null && vo.getTargetFields().size() > 0) {
                    orderMap.put(vo.getTargetFields().get(0), "ASC");
                }
            } else {
                orderMap.put(primaryKey, "DESC");
            }

            vo.setOrderMap(orderMap);
        }

        // 查询表数据
        PageInfo pageInfo = select(vo);
        resultMap.put("pageInfo", pageInfo);

        return resultMap;

    }

    /**
     * 动态数据插入（默认数据源）
     *
     * @param dto 数据插入dto
     * @return 插入成功返回 true，否则返回 false
     */
    @Override
    public boolean insert(@Validated(value = {ValidationGroup_Add.class}) InsertDTO dto) {

        if (dto == null) {
            return false;
        }

        TableVO tableVO = new TableVO();
        tableVO.setType(DataSourceUtil.getCurrentDataSourceType())
                .setTableSchema(dto.getTableSchema())
                .setTableName(dto.getTargetTable());
        boolean tableExist = isTableExist(tableVO);

        if (!tableExist) {
            throw new InsertException("操作失败，数据表 " + dto.getTableSchema() + "." + dto.getTargetTable() + " 不存在或已被删除");
        }

        return dataSourceMapper.insert(dto) > 0;
    }

    /**
     * 动态数据插入（指定数据源）
     *
     * @param dataSourceDTO 数据源dto
     * @param insertDTO     数据插入dto
     * @return 操作成功返回 true，否则返回 false
     */
    @Override
    public boolean insert(@Validated(value = {ValidationGroup_Add.class}) DataSourceDTO dataSourceDTO,
                          @Validated(value = {ValidationGroup_Add.class}) InsertDTO insertDTO) {
        // 数据源的切换/释放交给AOP去处理
        // 数据插入
        return insert(insertDTO);

    }

    /**
     * 动态数据更新（默认数据源）
     *
     * @param dto 数据更新dto
     * @return 插入成功返回 true，否则返回 false
     */
    @Override
    public boolean update(@Validated(value = {ValidationGroup_Update.class}) UpdateDTO dto) {

        if (dto == null) {
            return false;
        }

        if (dto.getConditionMap() == null || dto.getConditionMap().size() == 0) {
            throw new UpdateException("请先选择要更新的数据");
        }

        TableVO tableVO = new TableVO();
        tableVO.setType(DataSourceUtil.getCurrentDataSourceType())
                .setTableSchema(dto.getTableSchema())
                .setTableName(dto.getTargetTable());
        boolean tableExist = isTableExist(tableVO);

        if (!tableExist) {
            throw new UpdateException("操作失败，数据表 " + dto.getTableSchema() + "." + dto.getTargetTable() + " 不存在或已被删除");
        }

        return dataSourceMapper.update(dto) > 0;
    }

    /**
     * 动态数据更新（指定数据源）
     *
     * @param dataSourceDTO 数据源dto
     * @param updateDTO     数据更新dto
     * @return 操作成功返回 true，否则返回 false
     */
    @Override
    public boolean update(@Validated(value = {ValidationGroup_Update.class}) DataSourceDTO dataSourceDTO,
                          @Validated(value = {ValidationGroup_Update.class}) UpdateDTO updateDTO) {
        // 数据源的切换/释放交给AOP去处理
        // 切换数据源由AOP去处理
        return update(updateDTO);
    }

    /**
     * 动态数据全表删除（默认数据源）
     *
     * @param dto 数据删除dto
     * @return 操作成功返回 true，否则返回 false
     */
    @Override
    public boolean deleteAll(@Validated(value = {ValidationGroup_Add.class}) DeleteDTO dto) {

        // 避免全表删除
        Assert.notNull(dto, "动态数据删除DTO不能为空");

        TableVO tableVO = new TableVO();
        tableVO.setType(DataSourceUtil.getCurrentDataSourceType())
                .setTableSchema(dto.getTableSchema())
                .setTableName(dto.getTargetTable());

        // 判断表是否存在
        boolean tableExist = isTableExist(tableVO);
        if (!tableExist) {
            throw new DeleteException("操作失败，数据表 " + dto.getTableSchema() + "." + dto.getTargetTable() + " 不存在或已被删除");
        }

        return dataSourceMapper.deleteAll(dto) > 0;
    }

    /**
     * 动态数据全表删除（指定数据源）
     *
     * @param dataSourceDTO 数据源dto
     * @param deleteDTO     数据删除dto
     * @return 操作成功返回 true，否则返回 false
     */
    @Override
    public boolean deleteAll(@Validated(value = {ValidationGroup_Add.class}) DataSourceDTO dataSourceDTO,
                             @Validated(value = {ValidationGroup_Add.class}) DeleteDTO deleteDTO) {
        // 切换数据源由AOP去处理
        // 数据删除
        return deleteAll(deleteDTO);
    }

    /**
     * 动态数据删除（默认数据源）
     *
     * @param dto 数据删除dto
     * @return 操作成功返回 true，否则返回 false
     */
    @Override
    public boolean delete(@Validated(value = {ValidationGroup_Add.class}) DeleteDTO dto) {

        // 避免全表删除
        Assert.notNull(dto, "动态数据删除DTO不能为空");
        Assert.notNull(dto.getConditionMap(), "请先选择要删除的数据");
        Assert.isTrue(dto.getConditionMap().size() > 0, "请先选择要删除的数据");

        TableVO tableVO = new TableVO();
        tableVO.setType(DataSourceUtil.getCurrentDataSourceType())
                .setTableSchema(dto.getTableSchema())
                .setTableName(dto.getTargetTable());

        // 判断表是否存在
        boolean tableExist = isTableExist(tableVO);
        if (!tableExist) {
            throw new DeleteException("操作失败，数据表 " + dto.getTableSchema() + "." + dto.getTargetTable() + " 不存在或已被删除");
        }

        return dataSourceMapper.delete(dto) > 0;
    }

    /**
     * 动态数据批量删除（默认数据源）
     *
     * @param dto 批量数据删除dto
     * @return 操作成功返回 true，否则返回 false
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean batchDelete(@Validated(value = {ValidationGroup_Add.class}) BatchDeleteDTO dto) {

        Assert.notNull(dto, "动态数据批量删除DTO不能为空");
        Assert.notNull(dto.getConditionMaps(), "请先选择要删除的数据");
        Assert.isTrue(dto.getConditionMaps().size() > 0, "请先选择要删除的数据");

        String targetTable = dto.getTargetTable();
        List<Map<String, Object>> conditionMaps = dto.getConditionMaps();

        // 循环删除
        conditionMaps.forEach((map) -> {
            DeleteDTO deleteDTO = new DeleteDTO();
            deleteDTO.setTargetTable(targetTable)
                    .setConditionMap(map);

            boolean delete = delete(deleteDTO);
            if (!delete) {
                throw new DeleteException("操作失败，数据不存在或已被删除");
            }
        });

        return true;
    }

    /**
     * 动态数据删除（指定数据源）
     *
     * @param dataSourceDTO 数据源dto
     * @param deleteDTO     数据删除dto
     * @return 操作成功返回 true，否则返回 false
     */
    @Override
    public boolean delete(@Validated(value = {ValidationGroup_Add.class}) DataSourceDTO dataSourceDTO,
                          @Validated(value = {ValidationGroup_Add.class}) DeleteDTO deleteDTO) {
        // 切换数据源由AOP去处理
        // 数据删除
        return delete(deleteDTO);
    }

    /**
     * 动态数据批量删除（指定数据源）
     *
     * @param dataSourceDTO  数据源dto
     * @param batchDeleteDTO 批量数据删除dto
     * @return 操作成功返回 true，否则返回 false
     */
    @Override
    public boolean batchDelete(@Validated(value = {ValidationGroup_Add.class}) DataSourceDTO dataSourceDTO,
                               @Validated(value = {ValidationGroup_Add.class}) BatchDeleteDTO batchDeleteDTO) {
        // 数据源的切换/释放交给AOP去处理
        return batchDelete(batchDeleteDTO);
    }

    /**
     * 修改表（默认数据源）
     *
     * @param sql 修改语句
     */
    @Override
    public boolean alterTable(String sql) {
        Assert.isTrue(!StringUtils.isEmpty(sql), "修改SQL语句不能为空");

        // 非法字符校验
        String testSql = sql.toLowerCase();

        /*final String delete = "delete";
        if (testSql.contains(delete)) {
            throw new UpdateException("操作失败，非法字符：" + delete);
        }
        final String drop = "drop";
        if (testSql.contains(drop)) {
            throw new UpdateException("操作失败，非法字符：" + drop);
        }
        final String update = "update";
        if (testSql.contains(update)) {
            throw new UpdateException("操作失败，非法字符：" + update);
        }*/
        final String other1 = "--";
        if (testSql.contains(other1)) {
            throw new UpdateException("操作失败，非法字符：" + other1);
        }
        /*final String other2  = "=";
        if (testSql.contains(other2)) {
            throw new UpdateException("操作失败，非法字符：" + other2);
        }
        final String other3  = "!=";
        if (testSql.contains(other3)) {
            throw new UpdateException("操作失败，非法字符：" + other3);
        }*/

        // 执行SQL
        int successCnt = dataSourceMapper.alterTable(sql);

        return true;
    }

    /**
     * 修改表（指定数据源）
     *
     * @param sql 修改语句
     */
    @Override
    public boolean alterTable(DataSourceDTO dataSourceDTO, String sql) {
        // 数据源的切换与释放交给AOP去处理
        return alterTable(sql);
    }
}
