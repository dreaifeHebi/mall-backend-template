package com.macro.mall.portal.repository;

import com.macro.mall.portal.domain.MemberProductCollection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 会员商品收藏Repository
 * 会员互动持久化未启用
 * Created by macro on 2018/8/2.
 */
public interface MemberProductCollectionRepository {
    /**
     * 新增或更新收藏记录
     */
    int save(MemberProductCollection productCollection);

    /**
     * 根据会员ID和商品ID查找记录
     */
    MemberProductCollection findByMemberIdAndProductId(Long memberId, Long productId);

    /**
     * 根据会员ID和商品ID删除记录
     */
    int deleteByMemberIdAndProductId(Long memberId, Long productId);

    /**
     * 根据会员ID分页查询记录
     */
    Page<MemberProductCollection> findByMemberId(Long memberId, Pageable pageable);

    /**
     * 根据会员ID删除记录
     */
    void deleteAllByMemberId(Long memberId);
}
