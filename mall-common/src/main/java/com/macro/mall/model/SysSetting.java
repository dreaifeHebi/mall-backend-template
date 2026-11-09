package com.macro.mall.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统设置
 */
public class SysSetting implements Serializable {
    private Long id;
    
    /**
     * 设置键
     */
    private String settingKey;
    
    /**
     * 设置值
     */
    private String settingValue;
    
    /**
     * 设置名称
     */
    private String settingName;
    
    /**
     * 设置描述
     */
    private String description;
    
    /**
     * 设置类型：1-系统设置，2-业务设置，3-其他
     */
    private Integer type;
    
    /**
     * 是否启用：0-禁用，1-启用
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    private static final long serialVersionUID = 1L;
    
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
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    public Date getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
