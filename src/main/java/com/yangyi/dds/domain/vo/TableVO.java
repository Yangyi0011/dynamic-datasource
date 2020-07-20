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
@ApiModel(description = "数据表查询VO")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TableVO extends BaseVO implements Serializable {

    @ApiModelProperty(value = "表所处的schema，不指定可能会有重复的表",name = "tableSchema",example = "public")
    private String tableSchema;

    @ApiModelProperty(value = "表名称，用于模糊查询",name = "tableName",example = "test")
    private String tableName;

    @ApiModelProperty(value = "数据源类型，系统自动判断",name = "type", hidden = true, example = "postgresql")
    private String type;
}
