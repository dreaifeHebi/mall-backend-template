package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * 系统设置查询参数
 */
public class SysSettingQueryParam {
    
    @ApiModelProperty(value = "设置键")
    private String settingKey;
    
    @ApiModelProperty(value = "设置名称")
    private String settingName;
    
    @ApiModelProperty(value = "设置类型：1-系统设置，2-业务设置，3-其他")
    private Integer type;
    
    @ApiModelProperty(value = "是否启用：0-禁用，1-启用")
    private Integer status;
    
    @ApiModelProperty(value = "页码", required = true)
    private Integer pageNum = 1;
    
    @ApiModelProperty(value = "页大小", required = true)
    private Integer pageSize = 10;
    
    public String getSettingKey() {
        return settingKey;
    }
    
    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }
    
    public String getSettingName() {
        return settingName;
    }
    
    public void setSettingName(String settingName) {
        this.settingName = settingName;
    }
    
    public Integer getType() {
        return type;
    }
    
    public void setType(Integer type) {
        this.type = type;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
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
}
