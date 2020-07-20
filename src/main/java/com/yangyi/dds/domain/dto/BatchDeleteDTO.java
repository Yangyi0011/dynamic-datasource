package com.yangyi.dds.domain.dto;

import com.yangyi.dds.controller.validation.ValidationGroup_Add;
import com.yangyi.dds.controller.validation.ValidationGroup_Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * 动态数据DTO
 * Created by IntelliJ IDEA.
 *
 * @author yangyi
 * @date 2020/07/03
 */
@ApiModel(description = "动态数据批量删除DTO")
@Data
@Accessors(chain = true)
public class BatchDeleteDTO {

    @NotBlank(message = "操作表（targetTable）不能为空", groups = {ValidationGroup_Add.class, ValidationGroup_Update.class})
    @ApiModelProperty(value = "操作表",name = "targetTable",example = "tab_test", required = true)
    private String targetTable;

    @ApiModelProperty(value = "过滤条件map，key：字段名，value：字段值。注：如没有过滤条件，则会删除全表数据",name = "conditionMaps",example = "[{'id':1},{'id':2}]")
    private List<Map<String, Object>> conditionMaps;
}
