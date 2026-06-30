package com.macro.mall.dao.refund;

import com.macro.mall.domain.refund.RefundRequest;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 退款申请数据访问接口
 * @author macrozheng
 * @date 2025/10/13
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
     * 根据订单号查询退款申请列表
     */
    List<RefundRequest> selectByOrderSn(@Param("orderSn") String orderSn);
    
    /**
     * 根据会员ID查询退款申请列表
     */
    List<RefundRequest> selectByMemberId(@Param("memberId") Long memberId, 
                                        @Param("status") String status,
                                        @Param("offset") Integer offset,
                                        @Param("limit") Integer limit);
    
    /**
     * 管理员查询退款申请列表
     */
    List<RefundRequest> selectForAdmin(@Param("status") String status,
                                      @Param("startDate") Date startDate,
                                      @Param("endDate") Date endDate,
                                      @Param("memberUsername") String memberUsername,
                                      @Param("orderSn") String orderSn,
                                      @Param("refundSn") String refundSn,
                                      @Param("offset") Integer offset,
                                      @Param("limit") Integer limit,
                                      @Param("orderId") Long orderId);
    
    /**
     * 根据ID列表批量查询
     */
    List<RefundRequest> selectByIds(@Param("ids") List<Long> ids);
    
    /**
     * 统计会员退款申请数量
     */
    int countByMemberId(@Param("memberId") Long memberId, @Param("status") String status);
    
    /**
     * 统计管理员退款申请数量
     */
    int countForAdmin(@Param("status") String status,
                     @Param("startDate") Date startDate,
                     @Param("endDate") Date endDate,
                     @Param("memberUsername") String memberUsername,
                     @Param("orderSn") String orderSn,
                     @Param("refundSn") String refundSn,
                     @Param("orderId") Long orderId);
    
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
     * 更新审核信息
     */
    int updateAuditInfo(@Param("id") Long id,
                       @Param("status") String status,
                       @Param("auditorId") Long auditorId,
                       @Param("auditorName") String auditorName,
                       @Param("auditNote") String auditNote);
    
    /**
     * 更新处理信息
     */
    int updateProcessInfo(@Param("id") Long id,
                         @Param("status") String status,
                         @Param("thirdPartyRefundId") String thirdPartyRefundId,
                         @Param("processCount") Integer processCount,
                         @Param("failureReason") String failureReason);
    
    /**
     * 批量更新状态
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids,
                         @Param("status") String status,
                         @Param("failureReason") String failureReason);
    
    /**
     * 查询待处理的退款申请（用于自动查询状态）
     */
    List<RefundRequest> selectPendingRefunds(@Param("status") String status,
                                            @Param("limit") Integer limit);
    
    /**
     * 查询失败重试的退款申请
     */
    List<RefundRequest> selectFailedRetryRefunds(@Param("maxProcessCount") Integer maxProcessCount,
                                                @Param("retryIntervalMinutes") Integer retryIntervalMinutes,
                                                @Param("limit") Integer limit);
    
    /**
     * 根据第三方退款单号查询
     */
    RefundRequest selectByThirdPartyRefundId(@Param("thirdPartyRefundId") String thirdPartyRefundId);
    
    /**
     * 查询退款统计信息
     */
    Map<String, Object> selectRefundStatistics(@Param("startDate") Date startDate,
                                              @Param("endDate") Date endDate);
    
    /**
     * 查询每日退款统计
     */
    List<Map<String, Object>> selectDailyRefundStatistics(@Param("startDate") Date startDate,
                                                          @Param("endDate") Date endDate);
    
    /**
     * 查询超时未处理的退款申请
     */
    List<RefundRequest> selectTimeoutRefunds(@Param("timeoutHours") Integer timeoutHours,
                                            @Param("status") String status,
                                            @Param("limit") Integer limit);
    
    /**
     * 根据支付流水号查询退款记录
     */
    List<RefundRequest> selectByPaymentTransactionId(@Param("paymentTransactionId") String paymentTransactionId);
}
