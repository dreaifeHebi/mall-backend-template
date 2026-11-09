package com.macro.mall.portal.service.impl;

import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.portal.domain.MemberProductCollection;
import com.macro.mall.portal.repository.MemberProductCollectionRepository;
import com.macro.mall.portal.service.MemberCollectionService;
import com.macro.mall.portal.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * 会员收藏Service实现类
 * Created by macro on 2018/8/2.
 */
@Service
public class MemberCollectionServiceImpl implements MemberCollectionService {
    @Autowired
    private PmsProductMapper productMapper;
    @Autowired
    private UmsMemberService memberService;
    @Autowired
    private MemberProductCollectionRepository collectionRepository;

    @Override
    public int add(MemberProductCollection productCollection) {
        if (productCollection.getProductId() == null) {
            return 0;
        }
        PmsProduct product = productMapper.selectByPrimaryKey(productCollection.getProductId());
        if (product == null || product.getDeleteStatus() == 1) {
            return 0;
        }

        UmsMember currentMember = memberService.getCurrentMember();
        fillSnapshot(productCollection, currentMember, product);
        return collectionRepository.save(productCollection);
    }

    @Override
    public int delete(Long productId) {
        UmsMember currentMember = memberService.getCurrentMember();
        return collectionRepository.deleteByMemberIdAndProductId(currentMember.getId(), productId);
    }

    @Override
    public Page<MemberProductCollection> list(Integer pageNum, Integer pageSize) {
        UmsMember currentMember = memberService.getCurrentMember();
        return collectionRepository.findByMemberId(
                currentMember.getId(),
                PageRequest.of(Math.max(pageNum - 1, 0), pageSize)
        );
    }

    @Override
    public MemberProductCollection detail(Long productId) {
        UmsMember currentMember = memberService.getCurrentMember();
        return collectionRepository.findByMemberIdAndProductId(currentMember.getId(), productId);
    }

    @Override
    public void clear() {
        UmsMember currentMember = memberService.getCurrentMember();
        collectionRepository.deleteAllByMemberId(currentMember.getId());
    }

    private void fillSnapshot(MemberProductCollection collection, UmsMember member, PmsProduct product) {
        collection.setMemberId(member.getId());
        collection.setMemberNickname(member.getNickname());
        collection.setMemberIcon(member.getIcon());
        collection.setProductName(product.getName());
        collection.setProductPic(product.getPic());
        collection.setProductSubTitle(product.getSubTitle());
        collection.setProductPrice(product.getPrice() == null ? null : product.getPrice().toPlainString());
    }
}
