package com.yangyi.dds.domain.vo;

import com.yangyi.dds.controller.validation.ValidationGroup_Add;
import com.yangyi.dds.controller.validation.ValidationGroup_Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 *
 * @author yangyi
 * @date 2020/07/06
 */
@ApiModel(description = "字段查询VO")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class FieldVO extends BaseVO implements Serializable {

    @ApiModelProperty(value = "表所处的schema，不指定则查所有schema，可能会有重复的表",name = "tableSchema",example = "test")
    private String tableSchema;

    @NotBlank(message = "表名称（tableName）不能为空", groups = {ValidationGroup_Add.class, ValidationGroup_Update.class})
    @ApiModelProperty(value = "表名称，用于指定查询哪个表的字段",name = "tableName", required = true,example = "tab_test")
    private String tableName;

    @ApiModelProperty(value = "字段类型名称，用于指定代表数据源字段类型的列名，系统自动填充",name = "targetFieldType",
            hidden = true, example = "schema_name")
    private String targetFieldType;
}
