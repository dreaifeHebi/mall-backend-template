package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 邮件模板参数
 * Created by mall on 2024/06/22.
 */
public class SmsEmailTemplateParam {

    @ApiModelProperty(value = "模板名称")
    @NotBlank(message = "模板名称不能为空")
    private String templateName;

    @ApiModelProperty(value = "触发场景：0->用户注册，1->下单成功，2->订单发货，3->密码重置")
    @NotNull(message = "触发场景不能为空")
    private Integer triggerScene;

    @ApiModelProperty(value = "邮件标题")
    @NotBlank(message = "邮件标题不能为空")
    private String emailSubject;

    @ApiModelProperty(value = "邮件内容，支持HTML和占位符")
    @NotBlank(message = "邮件内容不能为空")
    private String emailContent;

    @ApiModelProperty(value = "状态：0->禁用，1->启用")
    private Integer status;

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

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public String getEmailContent() {
        return emailContent;
    }

    public void setEmailContent(String emailContent) {
        this.emailContent = emailContent;
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
