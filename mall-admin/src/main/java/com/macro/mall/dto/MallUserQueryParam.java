package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * 商城用户查询参数
 * Created by mall on 2025/06/22.
 */
public class MallUserQueryParam {

    @ApiModelProperty(value = "页码，从1开始", example = "1")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "每页条数", example = "10")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "关键词搜索（用户名、手机号、邮箱等）")
    private String keyword;    @ApiModelProperty(value = "用户状态：0-禁用（冻结），1-启用（正常）")
    private Integer status;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
