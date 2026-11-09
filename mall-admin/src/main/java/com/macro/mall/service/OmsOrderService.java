package com.macro.mall.service;

import com.macro.mall.dto.*;
import com.macro.mall.model.OmsOrder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 订单管理Service
 * Created by macro on 2018/10/11.
 */
public interface OmsOrderService {
    /**
     * 分页查询订单
     */
    List<OmsOrder> list(OmsOrderQueryParam queryParam, Integer pageSize, Integer pageNum);

    /**
     * 批量发货
     */
    @Transactional
    int delivery(List<OmsOrderDeliveryParam> deliveryParamList);

    /**
     * 批量关闭订单
     */
    @Transactional
    int close(List<Long> ids, String note);

    /**
     * 批量删除订单
     */
    int delete(List<Long> ids);

    /**
     * 获取指定订单详情
     */
    OmsOrderDetail detail(Long id);

    /**
     * 修改订单收货人信息
     */
    @Transactional
    int updateReceiverInfo(OmsReceiverInfoParam receiverInfoParam);

    /**
     * 修改订单费用信息
     */
    @Transactional
    int updateMoneyInfo(OmsMoneyInfoParam moneyInfoParam);

    /**
     * 修改订单备注
     */
    @Transactional
    int updateNote(Long id, String note, Integer status);

    /**
     * 批量删除订单
     */
    @Transactional
    int batchDelete(List<Long> orderIds);

    /**
     * 导出订单CSV
     */
    void exportOrdersCsv(OmsOrderQueryParam queryParam, javax.servlet.http.HttpServletResponse response);

    /**
     * 单个发货
     */
    @Transactional
    int deliverOrder(Long orderId, String deliveryCompany, String deliverySn);

    /**
     * 获取订单详情（包含完整信息）
     */
    OmsOrderDetail getOrderDetail(Long orderId);

    /**
     * 确认订单支付
     */
    @Transactional
    int confirmPayment(OmsOrderPaymentConfirmParam confirmParam);
}
