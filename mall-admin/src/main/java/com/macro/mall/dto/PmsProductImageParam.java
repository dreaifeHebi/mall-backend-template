package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 商品图片管理参数
 * Created by mall on 2024/06/22.
 */
public class PmsProductImageParam {
    @ApiModelProperty(value = "商品ID", required = true)
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @ApiModelProperty(value = "图片URL", required = true)
    @NotEmpty(message = "图片URL不能为空")
    private String pic;

    @ApiModelProperty(value = "图片说明")
    private String alt;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public Integer getSort() {
        return sort;
    }    public void setSort(Integer sort) {
        this.sort = sort;
    }

    // 兼容方法
    public java.util.List<String> getImageUrls() {
        java.util.List<String> urls = new java.util.ArrayList<>();
        if (pic != null) {
            urls.add(pic);
        }
        return urls;
    }
}
