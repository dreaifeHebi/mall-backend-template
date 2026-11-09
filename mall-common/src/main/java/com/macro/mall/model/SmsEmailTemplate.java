package com.macro.mall.model;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 邮件模板实体类
 * Created by mall on 2024/06/22.
 */
public class SmsEmailTemplate implements Serializable {
    private Long id;

    @ApiModelProperty(value = "模板名称")
    private String templateName;

    @ApiModelProperty(value = "触发场景：0:订单通知,1:单号通知,2:注册确认,3:修改订单,4:密码重置")
    private Integer triggerScene;

    @ApiModelProperty(value = "邮件标题")
    private String subject;

    @ApiModelProperty(value = "邮件内容，支持HTML和占位符")
    private String content;

    @ApiModelProperty(value = "状态：0->禁用，1->启用")
    private Integer status;

    @ApiModelProperty(value = "备注")
    private String note;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Integer getTriggerScene() {
        return triggerScene;
    }

    public void setTriggerScene(Integer triggerScene) {
        this.triggerScene = triggerScene;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
