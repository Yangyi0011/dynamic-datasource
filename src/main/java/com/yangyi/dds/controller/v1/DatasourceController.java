package com.yangyi.dds.controller.v1;

import com.yangyi.dds.controller.validation.ValidationGroup_Add;
import com.yangyi.dds.controller.validation.ValidationGroup_Update;
import com.yangyi.dds.domain.dto.*;
import com.yangyi.dds.domain.vo.DynamicDataBaseVO;
import com.yangyi.dds.domain.vo.DynamicFieldVO;
import com.yangyi.dds.domain.vo.DynamicTableDataVO;
import com.yangyi.dds.domain.vo.DynamicTableVO;
import com.yangyi.dds.service.DataSourceService;
import com.yangyi.dds.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by IntelliJ IDEA.
 *
 * @author yangyi
 * @date 2020/06/30
 */
@Api(tags = "DatasourceController", description = "数据源管理相关接口")
@RequestMapping("${spring.application.name}/v1/datasourceController")
@RestController
public class DatasourceController {

    @Qualifier("dataSourceService")
    @Autowired
    private DataSourceService dataSourceService;

    @ApiOperation("获取支持的数据源类型")
    @RequestMapping(value = "/getDataSourceType", method = RequestMethod.GET)
    public R getDataSourceType() {
        return R.success(dataSourceService.getDataSourceType());
    }

    @ApiOperation("数据源连接测试")
    @RequestMapping(value = "/testConnection", method = RequestMethod.POST)
    public R testConnection(@RequestBody DataSourceDTO dto) {
        return R.success(dataSourceService.testConnection(dto) ? "连接成功" : "连接失败");
    }

    /*@ApiOperation("获取当前数据源的key")
    @RequestMapping(value = "/getCurrentDataSource", method = RequestMethod.GET)
    public R getCurrentDataSource() {
        return R.success(dataSourceService.getCurrentDataSource());
    }
*/
    @ApiOperation("获取所有存储的数据源信息")
    @RequestMapping(value = "/getDataSources", method = RequestMethod.GET)
    public R getDataSources() {
        return R.success(dataSourceService.getDataSources());
    }

    /*@ApiOperation("切换数据源")
    @RequestMapping(value = "/changeDataSource", method = RequestMethod.POST)
    public R changeDataSource(@RequestBody DataSourceDTO dto) {
        return R.success(dataSourceService.changeDataSource(dto));
    }*/

    /*@ApiOperation("通过key切换数据源")
    @RequestMapping(value = "/changeDataSourceByKey", method = RequestMethod.GET)
    public R changeDataSourceByKey(String dataSourceKey) {
        return R.success(dataSourceService.changeDataSource(dataSourceKey));
    }*/

    /*@ApiOperation("查询所有数据库名称（默认数据源）")
    @RequestMapping(value = "/getDatabases", method = RequestMethod.POST)
    public R getDatabases(@RequestBody DataBaseVO vo) {
        return R.success(dataSourceService.getDatabases(vo));
    }*/

    @ApiOperation("查询所有数据库名称（指定数据源）")
    @RequestMapping(value = "/getDatabasesByDataSource", method = RequestMethod.POST)
    public R getDatabasesByDataSource(@Validated(value = {ValidationGroup_Add.class}) @RequestBody DynamicDataBaseVO vo) {
        return R.success(dataSourceService.getDatabases(vo.getDataSourceDTO(), vo.getDataBaseVO()));
    }

    /*@ApiOperation("查询所有数据表名称（默认数据源）")
    @RequestMapping(value = "/getTables", method = RequestMethod.POST)
    public R getTables(@RequestBody TableVO vo) {
        return R.success(dataSourceService.getTables(vo));
    }*/

    @ApiOperation("查询所有数据表名称（指定数据源）")
    @RequestMapping(value = "/getTablesByDataSource", method = RequestMethod.POST)
    public R getTables(@Validated(value = {ValidationGroup_Add.class}) @RequestBody DynamicTableVO vo) {
        return R.success(dataSourceService.getTables(vo.getDataSourceDTO(),vo.getTableVO()));
    }

    /*@ApiOperation("查询表字段（默认数据源）")
    @RequestMapping(value = "/getTableFields", method = RequestMethod.POST)
    public R getTableFields(@RequestBody FieldVO vo) {
        return R.success(dataSourceService.getTableFields(vo));
    }*/

    @ApiOperation("查询表字段（指定数据源）")
    @RequestMapping(value = "/getTableFieldsByDataSource", method = RequestMethod.POST)
    public R getTableFields(@Validated(value = {ValidationGroup_Add.class}) @RequestBody DynamicFieldVO vo) {
        return R.success(dataSourceService.getTableFields(vo.getDataSourceDTO(),vo.getFieldVO()));
    }

    /*@ApiOperation("查询表是否存在（默认数据源）")
    @RequestMapping(value = "/createTableByDataSource", method = RequestMethod.POST)
    public R isTableExist(@Validated(value = {ValidationGroup_Add.class}) @RequestBody TableVO vo) {
        return R.success(dataSourceService.isTableExist(vo));
    }*/

    @ApiOperation("查询表是否存在（指定数据源）")
    @RequestMapping(value = "/isTableExistByDataSource", method = RequestMethod.POST)
    public R isTableExist(@Validated(value = {ValidationGroup_Add.class}) @RequestBody DynamicTableVO vo) {
        return R.success(dataSourceService.isTableExist(vo.getDataSourceDTO(),vo.getTableVO()));
    }

    /*@ApiOperation("动态表创建（默认数据源）")
    @RequestMapping(value = "/createTable", method = RequestMethod.POST)
    public R createTable(@Validated(value = {ValidationGroup_Add.class}) @RequestBody TableDTO dto) {
        return R.success(dataSourceService.createTable(dto));
    }*/

    @ApiOperation("动态表创建（指定数据源）")
    @RequestMapping(value = "/createTableByDataSource", method = RequestMethod.POST)
    public R createTable(@Validated(value = {ValidationGroup_Add.class}) @RequestBody DynamicTableDTO dto) {
        return R.success(dataSourceService.createTable(dto.getDataSourceDTO(),dto.getTableDTO()));
    }

    /*@ApiOperation("动态数据查询（默认数据源）")
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    public R select(@RequestBody TableDataVO vo) {
        return R.success(dataSourceService.select(vo));
    }*/

    @ApiOperation("动态数据查询（指定数据源）")
    @RequestMapping(value = "/selectByDataSource", method = RequestMethod.POST)
    public R select(@Validated(value = {ValidationGroup_Add.class}) @RequestBody DynamicTableDataVO vo) {
        return R.success(dataSourceService.select(vo.getDataSourceDTO(),vo.getTableDataVO()));
    }

    /*@ApiOperation("动态数据插入（默认数据源）")
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public R insert(@RequestBody InsertDTO dto) {
        return R.success(dataSourceService.insert(dto));
    }*/

    @ApiOperation("动态数据插入（指定数据源）")
    @RequestMapping(value = "/insertByDataSource", method = RequestMethod.POST)
    public R insert(@Validated(value = {ValidationGroup_Add.class}) @RequestBody DynamicInsertDTO dto) {
        return R.success(dataSourceService.insert(dto.getDataSourceDTO(),dto.getInsertDTO()));
    }

    /*@ApiOperation("动态数据更新（默认数据源）")
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public R update(@RequestBody UpdateDTO dto) {
        return R.success(dataSourceService.update(dto));
    }*/

    @ApiOperation("动态数据更新（指定数据源）")
    @RequestMapping(value = "/updateByDataSource", method = RequestMethod.PUT)
    public R update(@Validated(value = {ValidationGroup_Update.class}) @RequestBody DynamicUpdateDTO dto) {
        return R.success(dataSourceService.update(dto.getDataSourceDTO(), dto.getUpdateDTO()));
    }

    /*@ApiOperation("动态数据删除，支持批处理（默认数据源）")
    @RequestMapping(value = "/batchDelete", method = RequestMethod.DELETE)
    public R batchDelete(@RequestBody BatchDeleteDTO dto) {
        return R.success(dataSourceService.batchDelete(dto));
    }*/

    @ApiOperation("动态数据删除，支持批处理（指定数据源）")
    @RequestMapping(value = "/batchDeleteByDataSource", method = RequestMethod.DELETE)
    public R batchDelete(@Validated(value = {ValidationGroup_Add.class}) @RequestBody DynamicDeleteDTO dto) {
        return R.success(dataSourceService.batchDelete(dto.getDataSourceDTO(),dto.getBatchDeleteDTO()));
    }
}
