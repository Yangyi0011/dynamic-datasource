package com.yangyi.dds.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 分页查询基类
 * Created by IntelliJ IDEA.
 *
 * @author yangyi
 * @date 2020/07/01
 */
@ApiModel(description = "分页查询基类VO")
@Data
@Accessors(chain = true)
public abstract class BaseVO implements Serializable{

    @ApiModelProperty(value = "当前页",name = "pageIndex",example = "1")
    protected int pageIndex;

    @ApiModelProperty(value = "每页数据条数",name = "pageSize",example = "10")
    protected int pageSize;
}
