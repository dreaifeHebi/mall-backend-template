package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * 上传结果DTO
 * Created by mall on 2025/06/22.
 */
public class UploadResultDto {
    @ApiModelProperty(value = "上传文件的URL")
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
