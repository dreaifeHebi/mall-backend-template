package com.macro.mall.portal.repository;

import com.macro.mall.portal.domain.MemberReadHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 会员商品浏览历史Repository
 * Created by macro on 2018/8/3.
 */
public interface MemberReadHistoryRepository {
    /**
     * 新增或更新时间线记录
     */
    int save(MemberReadHistory memberReadHistory);

    /**
     * 根据会员ID分页查找记录
     */
    Page<MemberReadHistory> findByMemberIdOrderByCreateTimeDesc(Long memberId, Pageable pageable);

    /**
     * 根据会员ID和记录ID删除记录
     */
    int deleteByMemberIdAndIds(Long memberId, List<String> ids);

    /**
     * 根据会员ID删除记录
     */
    void deleteAllByMemberId(Long memberId);
}
