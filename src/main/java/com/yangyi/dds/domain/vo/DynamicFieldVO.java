package com.yangyi.dds.domain.vo;


import com.yangyi.dds.controller.validation.ValidationGroup_Add;
import com.yangyi.dds.controller.validation.ValidationGroup_Update;
import com.yangyi.dds.domain.dto.DataSourceDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 *
 * @author yangyi
 * @date 2020/07/06
 */
@ApiModel(description = "动态表字段查询VO")
@Data
@Accessors(chain = true)
public class DynamicFieldVO implements Serializable {

    @NotNull(message = "数据源信息（dataSourceDTO）不能为null", groups = {ValidationGroup_Add.class, ValidationGroup_Update.class})
    @ApiModelProperty(value = "数据源信息",name = "dataSourceDTO", required = true,example = "")
    private DataSourceDTO dataSourceDTO;

    @NotNull(message = "查询条件（fieldVO）不能为null", groups = {ValidationGroup_Add.class, ValidationGroup_Update.class})
    @ApiModelProperty(value = "查询条件",name = "fieldVO", required = true,example = "")
    private FieldVO fieldVO;
}
