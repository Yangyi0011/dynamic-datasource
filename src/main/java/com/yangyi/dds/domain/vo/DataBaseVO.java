package com.yangyi.dds.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 *
 * @author yangyi
 * @date 2020/07/01
 */
@ApiModel(description = "数据库查询VO")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class DataBaseVO extends BaseVO implements Serializable {

    @ApiModelProperty(value = "查询字段，系统自动填充",name = "targetField",hidden = true, example = "information_schema.schemata")
    private String targetField;

    @ApiModelProperty(value = "查询源，系统自动填充",name = "targetSource",hidden = true, example = "pg_database")
    private String targetSource;

    @ApiModelProperty(value = "数据源类型，系统自动判断，用于过滤掉系统表",name = "type", hidden = true, example = "mysql")
    private String type;

    @ApiModelProperty(value = "数据库名称，用于模糊查询",name = "baseName",example = "test")
    private String baseName;
}
