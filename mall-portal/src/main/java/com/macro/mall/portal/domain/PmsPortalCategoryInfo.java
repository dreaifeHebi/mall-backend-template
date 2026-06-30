package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 前台商品分类信息
 * Created by macro on 2020/4/6.
 */
@Getter
@Setter
public class PmsPortalCategoryInfo {
    @ApiModelProperty("分类ID")
    private Long id;
    
    @ApiModelProperty("分类名称")
    private String name;
    
    @ApiModelProperty("分类副标题/子名称")
    private String subTitle;
    
    @ApiModelProperty("分类图标")
    private String icon;
    
    @ApiModelProperty("上架商品数量")
    private Integer publishedProductCount;
    
    @ApiModelProperty("分类级别：0->1级；1->2级")
    private Integer level;
    
    @ApiModelProperty("父分类ID")
    private Long parentId;
}