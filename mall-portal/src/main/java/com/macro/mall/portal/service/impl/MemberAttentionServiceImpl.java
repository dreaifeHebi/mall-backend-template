package com.macro.mall.portal.service.impl;

import com.macro.mall.mapper.PmsBrandMapper;
import com.macro.mall.model.PmsBrand;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.domain.MemberBrandAttention;
import com.macro.mall.portal.repository.MemberBrandAttentionRepository;
import com.macro.mall.portal.service.MemberAttentionService;
import com.macro.mall.portal.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * 会员关注Service实现类
 * Created by macro on 2018/8/2.
 */
@Service
public class MemberAttentionServiceImpl implements MemberAttentionService {
    @Autowired
    private PmsBrandMapper brandMapper;
    @Autowired
    private UmsMemberService memberService;
    @Autowired
    private MemberBrandAttentionRepository brandAttentionRepository;

    @Override
    public int add(MemberBrandAttention memberBrandAttention) {
        if (memberBrandAttention.getBrandId() == null) {
            return 0;
        }
        PmsBrand brand = brandMapper.selectByPrimaryKey(memberBrandAttention.getBrandId());
        if (brand == null || brand.getShowStatus() == null || brand.getShowStatus() == 0) {
            return 0;
        }

        UmsMember currentMember = memberService.getCurrentMember();
        fillSnapshot(memberBrandAttention, currentMember, brand);
        return brandAttentionRepository.save(memberBrandAttention);
    }

    @Override
    public int delete(Long brandId) {
        UmsMember currentMember = memberService.getCurrentMember();
        return brandAttentionRepository.deleteByMemberIdAndBrandId(currentMember.getId(), brandId);
    }

    @Override
    public Page<MemberBrandAttention> list(Integer pageNum, Integer pageSize) {
        UmsMember currentMember = memberService.getCurrentMember();
        return brandAttentionRepository.findByMemberId(
                currentMember.getId(),
                PageRequest.of(Math.max(pageNum - 1, 0), pageSize)
        );
    }

    @Override
    public MemberBrandAttention detail(Long brandId) {
        UmsMember currentMember = memberService.getCurrentMember();
        return brandAttentionRepository.findByMemberIdAndBrandId(currentMember.getId(), brandId);
    }

    @Override
    public void clear() {
        UmsMember currentMember = memberService.getCurrentMember();
        brandAttentionRepository.deleteAllByMemberId(currentMember.getId());
    }

    private void fillSnapshot(MemberBrandAttention attention, UmsMember member, PmsBrand brand) {
        attention.setMemberId(member.getId());
        attention.setMemberNickname(member.getNickname());
        attention.setMemberIcon(member.getIcon());
        attention.setBrandName(brand.getName());
        attention.setBrandLogo(brand.getLogo());
        attention.setBrandCity(brand.getSubTitle());
    }
}
