package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 商品图片管理参数
 * Created by mall on 2024/06/22.
 */
public class PmsProductImageParam {

    @ApiModelProperty(value = "商品ID")
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @ApiModelProperty(value = "图片URL列表")
    private List<String> imageUrls;

    @ApiModelProperty(value = "操作类型：0->添加，1->删除，2->替换")
    private Integer operationType;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public Integer getOperationType() {
        return operationType;
    }

    public void setOperationType(Integer operationType) {
        this.operationType = operationType;
    }
}
