package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * 邮件模板查询参数
 * Created by mall on 2024/06/22.
 */
public class SmsEmailTemplateQueryParam {

    @ApiModelProperty(value = "模板名称")
    private String templateName;

    @ApiModelProperty(value = "触发场景")
    private Integer triggerScene;

    @ApiModelProperty(value = "状态")
    private Integer status;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
