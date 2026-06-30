package com.macro.mall.portal.dao;

import com.macro.mall.portal.domain.payment.PaymentRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 支付记录Mapper接口
 * @author macrozheng
 * @date 2025/7/26
 */
@Mapper
public interface PaymentRecordMapper {

    /**
     * 插入支付记录
     */
    int insert(PaymentRecord record);

    /**
     * 根据主键更新
     */
    int updateByPrimaryKey(PaymentRecord record);

    /**
     * 根据主键查询
     */
    PaymentRecord selectByPrimaryKey(Long id);

    /**
     * 根据订单号和支付渠道查询
     */
    PaymentRecord selectByOrderSnAndChannel(@Param("orderSn") String orderSn, 
                                          @Param("paymentChannel") String paymentChannel);

    /**
     * 根据订单ID查询支付记录列表
     */
    List<PaymentRecord> selectByOrderId(Long orderId);

    /**
     * 根据第三方订单号查询
     */
    PaymentRecord selectByThirdPartyOrderId(String thirdPartyOrderId);

    /**
     * 根据支付状态查询
     */
    List<PaymentRecord> selectByPaymentStatus(String paymentStatus);

    /**
     * 更新支付状态
     */
    int updatePaymentStatus(@Param("id") Long id, 
                           @Param("paymentStatus") String paymentStatus, 
                           @Param("paymentTime") String paymentTime);

    /**
     * 更新第三方信息
     */
    int updateThirdPartyInfo(@Param("id") Long id,
                            @Param("thirdPartyOrderId") String thirdPartyOrderId,
                            @Param("thirdPartyTradeNo") String thirdPartyTradeNo,
                            @Param("paymentResponse") String paymentResponse);
}
