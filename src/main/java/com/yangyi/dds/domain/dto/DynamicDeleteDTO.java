package com.yangyi.dds.domain.dto;

import com.yangyi.dds.controller.validation.ValidationGroup_Add;
import com.yangyi.dds.controller.validation.ValidationGroup_Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * 动态数据DTO
 * Created by IntelliJ IDEA.
 *
 * @author yangyi
 * @date 2020/07/03
 */
@ApiModel(description = "动态数据删除DTO")
@Data
@Accessors(chain = true)
public class DynamicDeleteDTO {

    @NotNull(message = "数据源信息（dataSourceDTO）不能为null", groups = {ValidationGroup_Add.class, ValidationGroup_Update.class})
    @ApiModelProperty(value = "数据源信息",name = "dataSourceDTO",example = "", required = true)
    private DataSourceDTO dataSourceDTO;

    @NotNull(message = "删除数据（batchDeleteDTO）不能为null", groups = {ValidationGroup_Add.class, ValidationGroup_Update.class})
    @ApiModelProperty(value = "删除数据，支持批量删除",name = "batchDeleteDTO",example = "", required = true)
    private BatchDeleteDTO batchDeleteDTO;

    /*@NotNull(message = "删除数据（deleteDTO）不能为null", groups = {ValidationGroup_Add.class, ValidationGroup_Update.class})
    @ApiModelProperty(value = "删除数据",name = "deleteDTO",example = "")
    private DeleteDTO deleteDTO;*/
}
