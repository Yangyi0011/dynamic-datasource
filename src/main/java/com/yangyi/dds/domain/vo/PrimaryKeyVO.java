package com.yangyi.dds.domain.vo;

import com.yangyi.dds.controller.validation.ValidationGroup_Add;
import com.yangyi.dds.controller.validation.ValidationGroup_Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 *
 * @author yangyi
 * @date 2020/07/10
 */
@ApiModel(description = "主键查询VO")
@Data
public class PrimaryKeyVO implements Serializable {

    @NotBlank(message = "表所处的schema（tableSchema）不能为空", groups = {ValidationGroup_Add.class, ValidationGroup_Update.class})
    @ApiModelProperty(value = "表所处的schema，查找主键时必须指定",name = "tableSchema", required = true,example = "public")
    private String tableSchema;

    @NotBlank(message = "表名称（tableName）不能为空", groups = {ValidationGroup_Add.class, ValidationGroup_Update.class})
    @ApiModelProperty(value = "表名称，用于指定查询哪个表的字段",name = "tableName", required = true,example = "test")
    private String tableName;
}
