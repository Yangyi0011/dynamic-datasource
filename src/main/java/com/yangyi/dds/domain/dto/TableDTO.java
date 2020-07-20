package com.yangyi.dds.domain.dto;

import com.yangyi.dds.controller.validation.ValidationGroup_Add;
import com.yangyi.dds.controller.validation.ValidationGroup_Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author yangyi
 * @date 2020/07/08
 */
@ApiModel(description = "表DTO")
@Data
@Accessors(chain = true)
public class TableDTO {

    @ApiModelProperty(value = "表所处的schema，不指定可能会有重复的表",name = "tableSchema",example = "test")
    private String tableSchema;

    @NotBlank(message = "表名（tableName）不能为空", groups = {ValidationGroup_Add.class, ValidationGroup_Update.class})
    @ApiModelProperty(value = "表名",name = "tableName", required = true,example = "tab_test")
    private String tableName;

    @NotNull(message = "数据map（dataMap）不能为空", groups = {ValidationGroup_Add.class, ValidationGroup_Update.class})
    @ApiModelProperty(value = "数据map，key：字段名，value：字段类型及其长度",name = "dataMap", required = true,
            example = "{'id':'int4(10)','name':'char(20)','age':'int4(10)'}")
    private Map<String, Object> dataMap;

    @ApiModelProperty(value = "表备注",name = "comment",example = "备注")
    private String comment;

    @ApiModelProperty(value = "数据源类型，系统自动判断",name = "type", hidden = true,example = "mysql")
    private String type;
}
