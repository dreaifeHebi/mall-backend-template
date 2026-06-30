package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModelProperty;

/**
 * 前台品牌信息
 * Created by macro on 2025/10/12.
 */
public class PmsPortalBrandInfo {

    @ApiModelProperty("品牌ID")
    private Long id;

    @ApiModelProperty("品牌名称")
    private String name;

    @ApiModelProperty("品牌副标题")
    private String subTitle;

    @ApiModelProperty("品牌图片URL")
    private String logoUrl;

    @ApiModelProperty("该品牌在指定分类下的商品数量")
    private Integer productCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public Integer getProductCount() {
        return productCount;
    }

    public void setProductCount(Integer productCount) {
        this.productCount = productCount;
    }

    @Override
    public String toString() {
        return "PmsPortalBrandInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", logoUrl='" + logoUrl + '\'' +
                ", productCount=" + productCount +
                '}';
    }
}