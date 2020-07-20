package com.yangyi.dds.domain.dto;

import com.yangyi.dds.controller.validation.ValidationGroup_Add;
import com.yangyi.dds.controller.validation.ValidationGroup_Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * Created by IntelliJ IDEA.
 *
 * @author yangyi
 * @date 2020/06/30
 */
@ApiModel(description = "数据源信息DTO")
@Data
@Accessors(chain = true)
public class DataSourceDTO {

    @NotBlank(message = "数据源类型（type）不能为空", groups = {ValidationGroup_Add.class, ValidationGroup_Update.class})
    @ApiModelProperty(value = "数据源类型（必填）",name = "type",example = "PostgreSQL", required = true)
    private String type;

    @NotBlank(message = "连接ip地址（ip）不能为空", groups = {ValidationGroup_Add.class, ValidationGroup_Update.class})
    @ApiModelProperty(value = "连接ip地址（必填）",name = "ip",example = "127.0.0.1", required = true)
    private String ip;

    @NotBlank(message = "连接端口号（port）不能为空", groups = {ValidationGroup_Add.class, ValidationGroup_Update.class})
    @ApiModelProperty(value = "连接端口号（必填）",name = "port",example = "5432", required = true)
    private String port;

    @NotBlank(message = "连接用户名（username）不能为空", groups = {ValidationGroup_Add.class, ValidationGroup_Update.class})
    @ApiModelProperty(value = "连接用户名（必填）",name = "username",example = "", required = true)
    private String username;

    @NotBlank(message = "连接密码（password）不能为空", groups = {ValidationGroup_Add.class, ValidationGroup_Update.class})
    @ApiModelProperty(value = "连接密码（必填）",name = "password",example = "", required = true)
    private String password;

    @NotBlank(message = "默认连接的数据库（database）不能为空", groups = {ValidationGroup_Add.class, ValidationGroup_Update.class})
    @ApiModelProperty(value = "默认连接的数据库（必填）",name = "database",example = "postgres", required = true)
    private String database;

    @ApiModelProperty(value = "该数据源存储的key（非必填），若指定需确保唯一，不指定则由系统自动生成",
            name = "dataSourceKey",example = "jdbc:mysql://127.0.0.1:3306/test", hidden = true)
    private String dataSourceKey;
}
