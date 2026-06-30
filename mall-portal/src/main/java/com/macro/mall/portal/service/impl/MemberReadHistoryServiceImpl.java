package com.macro.mall.portal.service.impl;

import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.domain.MemberReadHistory;
import com.macro.mall.portal.repository.MemberReadHistoryRepository;
import com.macro.mall.portal.service.MemberReadHistoryService;
import com.macro.mall.portal.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 会员浏览记录管理Service实现类
 * Created by macro on 2018/8/3.
 */
@Service
public class MemberReadHistoryServiceImpl implements MemberReadHistoryService {
    @Autowired
    private PmsProductMapper productMapper;
    @Autowired
    private UmsMemberService memberService;
    @Autowired
    private MemberReadHistoryRepository readHistoryRepository;
    
    @Override
    public int create(MemberReadHistory memberReadHistory) {
        if (memberReadHistory.getProductId() == null) {
            return 0;
        }
        PmsProduct product = productMapper.selectByPrimaryKey(memberReadHistory.getProductId());
        if (product == null || product.getDeleteStatus() == 1) {
            return 0;
        }

        UmsMember currentMember = memberService.getCurrentMember();
        fillSnapshot(memberReadHistory, currentMember, product);
        return readHistoryRepository.save(memberReadHistory);
    }

    @Override
    public int delete(List<String> ids) {
        UmsMember currentMember = memberService.getCurrentMember();
        return readHistoryRepository.deleteByMemberIdAndIds(currentMember.getId(), ids);
    }

    @Override
    public Page<MemberReadHistory> list(Integer pageNum, Integer pageSize) {
        UmsMember currentMember = memberService.getCurrentMember();
        return readHistoryRepository.findByMemberIdOrderByCreateTimeDesc(
                currentMember.getId(),
                PageRequest.of(Math.max(pageNum - 1, 0), pageSize)
        );
    }

    @Override
    public void clear() {
        UmsMember currentMember = memberService.getCurrentMember();
        readHistoryRepository.deleteAllByMemberId(currentMember.getId());
    }

    private void fillSnapshot(MemberReadHistory readHistory, UmsMember member, PmsProduct product) {
        readHistory.setMemberId(member.getId());
        readHistory.setMemberNickname(member.getNickname());
        readHistory.setMemberIcon(member.getIcon());
        readHistory.setProductName(product.getName());
        readHistory.setProductPic(product.getPic());
        readHistory.setProductSubTitle(product.getSubTitle());
        readHistory.setProductPrice(product.getPrice() == null ? null : product.getPrice().toPlainString());
    }
}
