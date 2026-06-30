package com.macro.mall.portal.dao;

import com.macro.mall.portal.domain.payment.PaymentMethod;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 支付方式Mapper接口
 * @author dreaifekks
 * @date 2025/7/26
 */
@Mapper
public interface PaymentMethodMapper {

    /**
     * 查询所有启用的支付方式
     */
    List<PaymentMethod> selectEnabledPaymentMethods();

    /**
     * 根据支付渠道查询支付方式
     */
    List<PaymentMethod> selectByChannel(String channel);

    /**
     * 根据支付类型查询支付方式列表
     */
    List<PaymentMethod> selectByType(String type);

    /**
     * 插入支付方式
     */
    int insert(PaymentMethod paymentMethod);

    /**
     * 根据主键查询
     */
    PaymentMethod selectByPrimaryKey(Long id);

    /**
     * 根据支付方式代码查询
     */
    PaymentMethod selectByMethodCode(String methodCode);
}
