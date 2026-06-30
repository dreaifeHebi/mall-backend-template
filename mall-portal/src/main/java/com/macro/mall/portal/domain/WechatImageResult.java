package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 微信图片结果对象
 * Created by mall on 2025/07/06.
 */
public class WechatImageResult {
    
    @ApiModelProperty(value = "图片ID")
    private Long id;
    
    @ApiModelProperty(value = "图片URL")
    private String imageUrl;
    
    @ApiModelProperty(value = "图片类型")
    private String imageType;
    
    @ApiModelProperty(value = "图片标题")
    private String title;
    
    @ApiModelProperty(value = "图片描述")
    private String description;
    
    @ApiModelProperty(value = "文件名")
    private String fileName;
    
    @ApiModelProperty(value = "文件大小（字节）")
    private Long fileSize;
    
    @ApiModelProperty(value = "图片宽度")
    private Integer width;
    
    @ApiModelProperty(value = "图片高度")
    private Integer height;
    
    @ApiModelProperty(value = "缩略图URL")
    private String thumbnailUrl;
    
    @ApiModelProperty(value = "上传时间")
    private Date uploadTime;
    
    @ApiModelProperty(value = "是否可用")
    private Boolean available;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}
