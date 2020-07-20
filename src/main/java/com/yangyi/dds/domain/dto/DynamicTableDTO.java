package com.yangyi.dds.domain.dto;

import com.yangyi.dds.controller.validation.ValidationGroup_Add;
import com.yangyi.dds.controller.validation.ValidationGroup_Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * Created by IntelliJ IDEA.
 *
 * @author yangyi
 * @date 2020/07/08
 */
@ApiModel(description = "动态表DTO")
@Data
@Accessors(chain = true)
public class DynamicTableDTO {

    @NotNull(message = "数据源信息（dataSourceDTO）不能为null", groups = {ValidationGroup_Add.class, ValidationGroup_Update.class})
    @ApiModelProperty(value = "数据源信息",name = "dataSourceDTO",example = "", required = true)
    private DataSourceDTO dataSourceDTO;

    @NotNull(message = "表信息（tableDTO）不能为null", groups = {ValidationGroup_Add.class, ValidationGroup_Update.class})
    @ApiModelProperty(value = "表信息，用于创建操作",name = "tableDTO",example = "", required = true)
    private TableDTO tableDTO;
}
