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
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author yangyi
 * @date 2020/07/01
 */
@ApiModel(description = "表数据查询VO")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TableDataVO extends BaseVO implements Serializable {

    @ApiModelProperty(value = "查询字段，用于指定需要查询哪些字段，不指定则查所有",name = "targetFields",example = "['id','name']")
    private List<String> targetFields;

    @NotBlank(message = "表名称（tableName）不能为空", groups = {ValidationGroup_Add.class, ValidationGroup_Update.class})
    @ApiModelProperty(value = "表名称，用于指定查询哪个表的数据",name = "tableName", required = true,example = "test")
    private String tableName;

    @ApiModelProperty(value = "表所处的schema，不指定可能会有重复的表",name = "tableSchema",example = "public")
    private String tableSchema;

    @ApiModelProperty(value = "排序map，key：字段名，value：排序规则，ASC：升序，DESC：降序",name = "orderMap",example = "{'id':'DESC','name':'ASC','age':'ASC'}")
    private Map<String, String> orderMap;
}
