package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 邮件模板创建参数
 * Created by mall on 2024/06/22.
 */
public class SmsEmailTemplateParam {

    @ApiModelProperty(value = "模板名称", required = true)
    @NotBlank(message = "模板名称不能为空")
    private String templateName;

    @ApiModelProperty(value = "触发场景：0:订单通知,1:单号通知,2:注册确认,3:修改订单,4:找回密码", required = true)
    @NotNull(message = "触发场景不能为空")
    private Integer triggerScene;

    @ApiModelProperty(value = "邮件标题", required = true)
    @NotBlank(message = "邮件标题不能为空")
    private String subject;

    @ApiModelProperty(value = "邮件内容")
    private String content;

    @ApiModelProperty(value = "状态：0->禁用，1->启用")
    private Integer status = 1;

    @ApiModelProperty(value = "备注")
    private String note;

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
