package com.macro.mall.portal.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 会员品牌关注
 * 会员互动持久化未启用，仅作为 DTO 使用
 * Created by macro on 2018/8/2.
 */
@Getter
@Setter
public class MemberBrandAttention {
    private String id;
    private Long memberId;
    private String memberNickname;
    private String memberIcon;
    private Long brandId;
    private String brandName;
    private String brandLogo;
    private String brandCity;
    private Date createTime;
}
