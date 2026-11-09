package com.macro.mall.portal.dao.refund;

import com.macro.mall.common.domain.refund.RefundRequest;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 退款申请数据访问接口（Portal模块）
 * @author dreaifekks
 * @date 2025/10/14
 */
public interface RefundRequestDao {

    /**
     * 插入退款申请
     */
    int insert(RefundRequest refundRequest);

    /**
     * 根据ID查询退款申请
     */
    RefundRequest selectById(@Param("id") Long id);

    /**
     * 根据退款单号查询退款申请
     */
    RefundRequest selectByRefundSn(@Param("refundSn") String refundSn);

    /**
     * 根据订单ID查询退款申请列表
     */
    List<RefundRequest> selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据会员ID查询退款申请列表
     */
    List<RefundRequest> selectByMemberId(@Param("memberId") Long memberId,
                                        @Param("status") String status,
                                        @Param("offset") Integer offset,
                                        @Param("limit") Integer limit);

    /**
     * 更新退款申请
     */
    int updateById(RefundRequest refundRequest);

    /**
     * 更新退款状态
     */
    int updateStatus(@Param("id") Long id,
                    @Param("status") String status,
                    @Param("thirdPartyRefundId") String thirdPartyRefundId,
                    @Param("failureReason") String failureReason);

    /**
     * 统计会员退款申请数量
     */
    int countByMemberId(@Param("memberId") Long memberId, @Param("status") String status);
}
