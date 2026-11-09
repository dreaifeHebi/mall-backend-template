package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dao.OmsOrderDao;
import com.macro.mall.dao.OmsOrderOperateHistoryDao;
import com.macro.mall.dto.*;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.mapper.OmsOrderItemMapper;
import com.macro.mall.mapper.OmsOrderOperateHistoryMapper;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderExample;
import com.macro.mall.model.OmsOrderItem;
import com.macro.mall.model.OmsOrderItemExample;
import com.macro.mall.model.OmsOrderOperateHistory;
import com.macro.mall.mapper.PmsSkuStockMapper;
import com.macro.mall.model.PmsSkuStock;
import com.macro.mall.service.OmsOrderService;
import com.macro.mall.service.EmailNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单管理Service实现类
 * Created by macro on 2018/10/11.
 */
@Service
public class OmsOrderServiceImpl implements OmsOrderService {
    @Autowired
    private OmsOrderMapper orderMapper;
    @Autowired
    private OmsOrderItemMapper orderItemMapper;
    @Autowired
    private OmsOrderDao orderDao;
    @Autowired
    private OmsOrderOperateHistoryDao orderOperateHistoryDao;
    @Autowired
    private OmsOrderOperateHistoryMapper orderOperateHistoryMapper;
    @Autowired
    private PmsSkuStockMapper skuStockMapper;

    @Autowired
    @Qualifier("adminEmailNotificationService")
    private EmailNotificationService emailNotificationService;

    @Value("${portal.service.base-url:http://localhost:8085}")
    private String portalBaseUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<OmsOrder> list(OmsOrderQueryParam queryParam, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        return orderDao.getList(queryParam);
    }

    @Override
    public int delivery(List<OmsOrderDeliveryParam> deliveryParamList) {
        // 批量发货
        int count = orderDao.delivery(deliveryParamList);
        // 添加操作记录
        List<OmsOrderOperateHistory> operateHistoryList = deliveryParamList.stream()
                .map(omsOrderDeliveryParam -> {
                    OmsOrderOperateHistory history = new OmsOrderOperateHistory();
                    history.setOrderId(omsOrderDeliveryParam.getOrderId());
                    history.setCreateTime(new Date());
                    history.setOperateMan("后台管理员");
                    history.setOrderStatus(2);
                    history.setNote("完成发货");
                    return history;
                }).collect(Collectors.toList());
        orderOperateHistoryDao.insertList(operateHistoryList);
        return count;
    }

    @Override
    public int close(List<Long> ids, String note) {
        OmsOrder record = new OmsOrder();
        record.setStatus(4);
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria().andDeleteStatusEqualTo(0).andIdIn(ids);
        int count = orderMapper.updateByExampleSelective(record, example);
        releaseSkuLockStock(ids);
        List<OmsOrderOperateHistory> historyList = ids.stream().map(orderId -> {
            OmsOrderOperateHistory history = new OmsOrderOperateHistory();
            history.setOrderId(orderId);
            history.setCreateTime(new Date());
            history.setOperateMan("后台管理员");
            history.setOrderStatus(4);
            history.setNote("订单关闭:" + note);
            return history;
        }).collect(Collectors.toList());
        orderOperateHistoryDao.insertList(historyList);
        return count;
    }

    @Override
    public int delete(List<Long> ids) {
        OmsOrder record = new OmsOrder();
        record.setDeleteStatus(1);
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria().andDeleteStatusEqualTo(0).andIdIn(ids);
        return orderMapper.updateByExampleSelective(record, example);
    }

    @Override
    public OmsOrderDetail detail(Long id) {
        return orderDao.getDetail(id);
    }

    @Override
    public int updateReceiverInfo(OmsReceiverInfoParam receiverInfoParam) {
        OmsOrder currentOrder = orderMapper.selectByPrimaryKey(receiverInfoParam.getOrderId());
        if (currentOrder == null) {
            return 0;
        }

        OmsOrder order = new OmsOrder();
        order.setId(receiverInfoParam.getOrderId());
        order.setReceiverName(receiverInfoParam.getReceiverName());
        order.setReceiverPhone(receiverInfoParam.getReceiverPhone());
        order.setReceiverPostCode(receiverInfoParam.getReceiverPostCode());
        order.setReceiverDetailAddress(receiverInfoParam.getReceiverDetailAddress());
        order.setReceiverProvince(receiverInfoParam.getReceiverProvince());
        order.setReceiverCity(receiverInfoParam.getReceiverCity());
        order.setReceiverRegion(receiverInfoParam.getReceiverRegion());
        order.setModifyTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        // 插入操作记录
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(receiverInfoParam.getOrderId());
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(receiverInfoParam.getStatus());
        history.setNote("修改收货人信息");
        orderOperateHistoryMapper.insert(history);

        // 发送订单修改通知邮件
        String newAddress = receiverInfoParam.getReceiverProvince() + " " +
                receiverInfoParam.getReceiverCity() + " " +
                receiverInfoParam.getReceiverRegion() + " " +
                receiverInfoParam.getReceiverDetailAddress();
        emailNotificationService.sendOrderModificationEmail(receiverInfoParam.getOrderId(),
                currentOrder.getMemberId(), newAddress);

        return count;
    }

    @Override
    public int updateMoneyInfo(OmsMoneyInfoParam moneyInfoParam) {
        OmsOrder currentOrder = orderMapper.selectByPrimaryKey(moneyInfoParam.getOrderId());
        if (currentOrder == null) {
            return 0;
        }

        BigDecimal freightAmount = moneyInfoParam.getFreightAmount() != null
                ? moneyInfoParam.getFreightAmount()
                : zeroIfNull(currentOrder.getFreightAmount());
        BigDecimal discountAmount = moneyInfoParam.getDiscountAmount() != null
                ? moneyInfoParam.getDiscountAmount()
                : zeroIfNull(currentOrder.getDiscountAmount());
        BigDecimal payAmount = zeroIfNull(currentOrder.getTotalAmount())
                .add(freightAmount)
                .subtract(zeroIfNull(currentOrder.getPromotionAmount()))
                .subtract(zeroIfNull(currentOrder.getIntegrationAmount()))
                .subtract(zeroIfNull(currentOrder.getCouponAmount()))
                .subtract(discountAmount);
        if (payAmount.compareTo(BigDecimal.ZERO) < 0) {
            payAmount = BigDecimal.ZERO;
        }

        OmsOrder order = new OmsOrder();
        order.setId(moneyInfoParam.getOrderId());
        order.setFreightAmount(freightAmount);
        order.setDiscountAmount(discountAmount);
        order.setPayAmount(payAmount);
        order.setModifyTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        // 插入操作记录
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(moneyInfoParam.getOrderId());
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(moneyInfoParam.getStatus());
        history.setNote("修改费用信息");
        orderOperateHistoryMapper.insert(history);
        return count;
    }

    private BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private void releaseSkuLockStock(List<Long> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return;
        }

        OmsOrderItemExample itemExample = new OmsOrderItemExample();
        itemExample.createCriteria().andOrderIdIn(orderIds);
        List<OmsOrderItem> itemList = orderItemMapper.selectByExample(itemExample);
        for (OmsOrderItem item : itemList) {
            if (item.getProductSkuId() == null || item.getProductQuantity() == null || item.getProductQuantity() <= 0) {
                continue;
            }
            PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(item.getProductSkuId());
            if (skuStock == null) {
                continue;
            }
            Integer currentLockStock = skuStock.getLockStock() == null ? 0 : skuStock.getLockStock();
            PmsSkuStock updateStock = new PmsSkuStock();
            updateStock.setId(skuStock.getId());
            updateStock.setLockStock(Math.max(0, currentLockStock - item.getProductQuantity()));
            skuStockMapper.updateByPrimaryKeySelective(updateStock);
        }
    }

    @Override
    public int updateNote(Long id, String note, Integer status) {
        OmsOrder order = new OmsOrder();
        order.setId(id);
        order.setNote(note);
        order.setModifyTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(id);
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(status);
        history.setNote("修改备注信息：" + note);
        orderOperateHistoryMapper.insert(history);
        return count;
    }

    @Override
    public int batchDelete(List<Long> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return 0;
        }

        // 只允许删除已取消的订单
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria()
                .andIdIn(orderIds)
                .andStatusEqualTo(4); // 4代表已取消状态

        List<OmsOrder> orders = orderMapper.selectByExample(example);
        if (orders.size() != orderIds.size()) {
            throw new RuntimeException("只能删除已取消状态的订单");
        }

        return orderMapper.deleteByExample(example);
    }

    @Override
    public void exportOrdersCsv(OmsOrderQueryParam queryParam, HttpServletResponse response) {
        // 设置响应头
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=orders.csv");

        try (PrintWriter writer = response.getWriter()) {
            // 写入CSV头部
            writer.println("订单编号,用户名,订单状态,订单金额,创建时间,支付时间,发货时间");

            // 分页查询并写入数据
            int pageSize = 1000;
            int pageNum = 1;
            List<OmsOrder> orders;

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            do {
                PageHelper.startPage(pageNum, pageSize);
                orders = orderDao.getList(queryParam);

                for (OmsOrder order : orders) {
                    StringBuilder line = new StringBuilder();
                    line.append(order.getOrderSn()).append(",");
                    line.append(order.getMemberUsername() != null ? order.getMemberUsername() : "").append(",");
                    line.append(getOrderStatusText(order.getStatus())).append(",");
                    line.append(order.getPayAmount() != null ? order.getPayAmount() : "0").append(",");
                    line.append(order.getCreateTime() != null ? dateFormat.format(order.getCreateTime()) : "")
                            .append(",");
                    line.append(order.getPaymentTime() != null ? dateFormat.format(order.getPaymentTime()) : "")
                            .append(",");
                    line.append(order.getDeliveryTime() != null ? dateFormat.format(order.getDeliveryTime()) : "");

                    writer.println(line.toString());
                }

                pageNum++;
            } while (orders.size() == pageSize);

        } catch (IOException e) {
            throw new RuntimeException("导出CSV文件失败", e);
        }
    }

    @Override
    public int deliverOrder(Long orderId, String deliveryCompany, String deliverySn) {
        // 检查订单状态
        OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new RuntimeException("订单状态不是待发货状态");
        }

        // 更新订单状态
        OmsOrder updateOrder = new OmsOrder();
        updateOrder.setId(orderId);
        updateOrder.setStatus(2); // 2代表已发货
        updateOrder.setDeliveryCompany(deliveryCompany);
        updateOrder.setDeliverySn(deliverySn);
        updateOrder.setDeliveryTime(new Date());
        updateOrder.setModifyTime(new Date());

        int count = orderMapper.updateByPrimaryKeySelective(updateOrder);

        // 添加操作记录
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(orderId);
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(2);
        history.setNote("订单发货：" + deliveryCompany + " " + deliverySn);
        orderOperateHistoryMapper.insert(history);

        // 发送国际单号通知邮件
        String trackingUrl = "http://tracking.company.com/track?sn=" + deliverySn;
        emailNotificationService.sendTrackingNumberEmail(orderId, order.getMemberId(),
                deliverySn, deliveryCompany, trackingUrl);

        return count;
    }

    @Override
    public OmsOrderDetail getOrderDetail(Long orderId) {
        return orderDao.getDetail(orderId);
    }

    @Override
    public int confirmPayment(OmsOrderPaymentConfirmParam confirmParam) {
        OmsOrder order = orderMapper.selectByPrimaryKey(confirmParam.getOrderId());
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态不是待付款状态");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OmsOrderPaymentConfirmParam> entity = new HttpEntity<>(confirmParam, headers);
        String url = portalBaseUrl + "/payment/h5/admin/confirm";
        ResponseEntity<CommonResult> response = restTemplate.postForEntity(url, entity, CommonResult.class);
        if (!response.getStatusCode().is2xxSuccessful()
                || response.getBody() == null
                || response.getBody().getCode() != 200) {
            throw new RuntimeException("支付确认失败");
        }

        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(order.getId());
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(1);
        String note = "后台确认付款";
        if (confirmParam.getNote() != null && !confirmParam.getNote().trim().isEmpty()) {
            note += "：" + confirmParam.getNote().trim();
        }
        history.setNote(note);
        orderOperateHistoryMapper.insert(history);

        return 1;
    }

    /**
     * 获取订单状态文本
     */
    private String getOrderStatusText(Integer status) {
        if (status == null)
            return "未知";
        switch (status) {
            case 0:
                return "待付款";
            case 1:
                return "待发货";
            case 2:
                return "已发货";
            case 3:
                return "已完成";
            case 4:
                return "已关闭";
            default:
                return "未知";
        }
    }
}
