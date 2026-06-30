package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;

/**
 * 系统设置参数
 */
public class SysSettingParam {
    
    @ApiModelProperty(value = "设置ID", required = false)
    private Long id;
    
    @NotEmpty(message = "设置键不能为空")
    @ApiModelProperty(value = "设置键", required = true)
    private String settingKey;
    
    @NotEmpty(message = "设置值不能为空")
    @ApiModelProperty(value = "设置值", required = true)
    private String settingValue;
    
    @NotEmpty(message = "设置名称不能为空")
    @ApiModelProperty(value = "设置名称", required = true)
    private String settingName;
    
    @ApiModelProperty(value = "设置描述")
    private String description;
    
    @ApiModelProperty(value = "设置类型：1-系统设置，2-业务设置，3-其他", required = true)
    private Integer type;
    
    @ApiModelProperty(value = "是否启用：0-禁用，1-启用", required = true)
    private Integer status;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSettingKey() {
        return settingKey;
    }
    
    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }
    
    public String getSettingValue() {
        return settingValue;
    }
    
    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }
    
    public String getSettingName() {
        return settingName;
    }
    
    public void setSettingName(String settingName) {
        this.settingName = settingName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
}
