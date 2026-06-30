package com.macro.mall.portal.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 会员商品浏览历史记录
 * 会员互动持久化未启用，仅作为 DTO 使用
 * Created by macro on 2018/8/3.
 */
@Getter
@Setter
public class MemberReadHistory {
    private String id;
    private Long memberId;
    private String memberNickname;
    private String memberIcon;
    private Long productId;
    private String productName;
    private String productPic;
    private String productSubTitle;
    private String productPrice;
    private Date createTime;
}
