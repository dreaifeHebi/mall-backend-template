package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 批量操作请求参数
 * Created by mall on 2025/06/22.
 */
public class BatchOperationParam {

    @ApiModelProperty(value = "用户ID数组", required = true)
    @NotEmpty(message = "用户ID不能为空")
    private List<Long> ids;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
