package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.config.CsvExportConfig;
import com.macro.mall.dao.OmsOrderDao;
import com.macro.mall.dto.OmsOrderCsvData;
import com.macro.mall.dto.OmsOrderCsvExportParam;
import com.macro.mall.dto.OmsOrderQueryParam;
import com.macro.mall.mapper.OmsOrderItemMapper;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderExample;
import com.macro.mall.model.OmsOrderItem;
import com.macro.mall.model.OmsOrderItemExample;
import com.macro.mall.service.OmsOrderCsvExportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单CSV导出服务实现类
 * Created on 2025/7/3.
 */
@Slf4j
@Service
public class OmsOrderCsvExportServiceImpl implements OmsOrderCsvExportService {
    
    @Autowired
    private OmsOrderDao orderDao;
    
    @Autowired
    private OmsOrderMapper orderMapper;
    
    @Autowired
    private OmsOrderItemMapper orderItemMapper;
    
    @Autowired
    private CsvExportConfig csvConfig;
    
    @Override
    public void exportOrdersCsv(OmsOrderCsvExportParam exportParam, HttpServletResponse response) {
        // 设置默认时间范围（如果没有指定）
        if (exportParam.getStartTime() == null || exportParam.getEndTime() == null) {
            setCurrentMonthRange(exportParam);
        }
        
        // 生成文件名
        String fileName = generateFileName();
        
        // 设置响应头
        setResponseHeaders(response, fileName);
        
        try (PrintWriter writer = response.getWriter()) {
            // 查询订单数据
            List<OmsOrderCsvData> orderCsvDataList = queryOrderCsvData(exportParam);
            
            // 写入CSV内容
            writeCsvContent(writer, orderCsvDataList);
            
        } catch (IOException e) {
            log.error("导出CSV文件失败", e);
            throw new RuntimeException("导出CSV文件失败", e);
        }
    }
    
    @Override
    public void exportCurrentMonthOrdersCsv(HttpServletResponse response) {
        OmsOrderCsvExportParam exportParam = new OmsOrderCsvExportParam();
        setCurrentMonthRange(exportParam);
        exportOrdersCsv(exportParam, response);
    }
    
    @Override
    public void exportOrdersCsvByIds(List<Long> orderIds, HttpServletResponse response) {
        if (CollectionUtils.isEmpty(orderIds)) {
            throw new RuntimeException("订单ID列表不能为空");
        }
        
        // 生成文件名
        String fileName = generateFileName();
        
        // 设置响应头
        setResponseHeaders(response, fileName);
        
        try (PrintWriter writer = response.getWriter()) {
            // 查询指定订单数据
            List<OmsOrderCsvData> orderCsvDataList = queryOrderCsvDataByIds(orderIds);
            
            // 写入CSV内容
            writeCsvContent(writer, orderCsvDataList);
            
        } catch (IOException e) {
            log.error("导出CSV文件失败", e);
            throw new RuntimeException("导出CSV文件失败", e);
        }
    }
    
    /**
     * 设置当前月份时间范围
     */
    private void setCurrentMonthRange(OmsOrderCsvExportParam exportParam) {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        
        exportParam.setStartTime(Date.from(startOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        exportParam.setEndTime(Date.from(endOfMonth.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant()));
    }
    
    /**
     * 生成文件名
     */
    private String generateFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new Date());
        return timestamp + "_" + csvConfig.getOrderFilePrefix() + csvConfig.getFileSuffix();
    }
    
    /**
     * 设置响应头
     */
    private void setResponseHeaders(HttpServletResponse response, String fileName) {
        response.setContentType("text/csv; charset=" + csvConfig.getEncoding());
        response.setCharacterEncoding(csvConfig.getEncoding());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
    }
    
    /**
     * 查询订单CSV数据
     */
    private List<OmsOrderCsvData> queryOrderCsvData(OmsOrderCsvExportParam exportParam) {
        // 构建查询参数
        OmsOrderQueryParam queryParam = new OmsOrderQueryParam();
        
        // 设置时间范围参数
        if (exportParam.getStartTime() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            queryParam.setStartDate(sdf.format(exportParam.getStartTime()));
        }
        if (exportParam.getEndTime() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            queryParam.setEndDate(sdf.format(exportParam.getEndTime()));
        }
        
        queryParam.setStatus(exportParam.getStatus());
        queryParam.setSourceType(exportParam.getSourceType());
        
        // 分页查询订单
        List<OmsOrder> allOrders = new ArrayList<>();
        int pageSize = 1000;
        int pageNum = 1;
        List<OmsOrder> orders;
        
        do {
            PageHelper.startPage(pageNum, pageSize);
            orders = orderDao.getList(queryParam);
            allOrders.addAll(orders);
            pageNum++;
        } while (orders.size() == pageSize);
        
        if (CollectionUtils.isEmpty(allOrders)) {
            return new ArrayList<>();
        }
        
        // 查询订单商品信息
        Map<Long, List<OmsOrderItem>> orderItemsMap = getOrderItemsMap(allOrders);
        
        // 转换为CSV数据
        List<OmsOrderCsvData> csvDataList = allOrders.stream()
                .map(order -> convertToOrderCsvData(order, orderItemsMap.get(order.getId())))
                .collect(Collectors.toList());
        
        return csvDataList;
    }
    
    /**
     * 查询指定订单ID的CSV数据
     */
    private List<OmsOrderCsvData> queryOrderCsvDataByIds(List<Long> orderIds) {
        // 直接通过订单ID查询
        List<OmsOrder> allOrders = new ArrayList<>();
        
        // 分批查询，避免SQL参数过多
        int batchSize = 1000;
        for (int i = 0; i < orderIds.size(); i += batchSize) {
            int end = Math.min(i + batchSize, orderIds.size());
            List<Long> batchIds = orderIds.subList(i, end);
            
            // 使用现有的查询方法
            OmsOrderExample example = new OmsOrderExample();
            example.createCriteria().andIdIn(batchIds).andDeleteStatusEqualTo(0);
            List<OmsOrder> batchOrders = orderMapper.selectByExample(example);
            allOrders.addAll(batchOrders);
        }
        
        if (CollectionUtils.isEmpty(allOrders)) {
            return new ArrayList<>();
        }
        
        // 查询订单商品信息
        Map<Long, List<OmsOrderItem>> orderItemsMap = getOrderItemsMap(allOrders);
        
        // 转换为CSV数据
        return allOrders.stream()
                .map(order -> convertToOrderCsvData(order, orderItemsMap.get(order.getId())))
                .collect(Collectors.toList());
    }
    
    /**
     * 检查日期是否在范围内
     */
    private boolean isInDateRange(Date orderDate, Date startDate, Date endDate) {
        if (orderDate == null) return false;
        if (startDate != null && orderDate.before(startDate)) return false;
        if (endDate != null && orderDate.after(endDate)) return false;
        return true;
    }
    
    /**
     * 获取订单商品映射
     */
    private Map<Long, List<OmsOrderItem>> getOrderItemsMap(List<OmsOrder> orders) {
        List<Long> orderIds = orders.stream().map(OmsOrder::getId).collect(Collectors.toList());
        
        OmsOrderItemExample example = new OmsOrderItemExample();
        example.createCriteria().andOrderIdIn(orderIds);
        List<OmsOrderItem> orderItems = orderItemMapper.selectByExample(example);
        
        return orderItems.stream().collect(Collectors.groupingBy(OmsOrderItem::getOrderId));
    }
    
    /**
     * 转换为CSV数据
     */
    private OmsOrderCsvData convertToOrderCsvData(OmsOrder order, List<OmsOrderItem> orderItems) {
        OmsOrderCsvData csvData = new OmsOrderCsvData();
        
        // 生成UUID格式的订单ID和用户ID
        csvData.setOrderId(generateUuidFromId(order.getId()));
        csvData.setUserId(generateUuidFromId(order.getMemberId()));
        csvData.setOrderDate(order.getCreateTime());
        csvData.setTotalAmount(order.getPayAmount() != null ? order.getPayAmount() : BigDecimal.ZERO);
        csvData.setStatus(getOrderStatusText(order.getStatus()));
        csvData.setPaymentMethod(getPaymentMethodText(order.getPayType()));
        csvData.setItemCount(orderItems != null ? orderItems.size() : 0);
        csvData.setCurrency("CNY");
        
        // 构建收货地址
        String shippingAddress = buildShippingAddress(order);
        csvData.setShippingAddress(shippingAddress);
        csvData.setRecipientName(order.getReceiverName());
        csvData.setRecipientPhone(order.getReceiverPhone());
        
        return csvData;
    }
    
    /**
     * 生成UUID格式ID
     */
    private String generateUuidFromId(Long id) {
        if (id == null) return UUID.randomUUID().toString();
        
        // 基于ID生成固定的UUID
        String idStr = String.format("%016d", id);
        return idStr.substring(0, 8) + "-" + 
               idStr.substring(8, 12) + "-4" + 
               idStr.substring(13, 16) + "-" + 
               "a" + idStr.substring(1, 4) + "-" + 
               idStr.substring(4, 16);
    }
    
    /**
     * 获取订单状态文本
     */
    private String getOrderStatusText(Integer status) {
        if (status == null) return "PENDING";
        
        switch (status) {
            case 0: return "PENDING";
            case 1: return "PAID";
            case 2: return "SHIPPED";
            case 3: return "COMPLETED";
            case 4: return "CANCELLED";
            default: return "PENDING";
        }
    }
    
    /**
     * 获取支付方式文本
     */
    private String getPaymentMethodText(Integer payType) {
        if (payType == null) return "WECHAT";
        
        switch (payType) {
            case 1: return "ALIPAY";
            case 2: return "WECHAT";
            default: return "CREDIT";
        }
    }
    
    /**
     * 构建收货地址
     */
    private String buildShippingAddress(OmsOrder order) {
        StringBuilder address = new StringBuilder();
        
        if (order.getReceiverProvince() != null) {
            address.append(order.getReceiverProvince());
        }
        if (order.getReceiverCity() != null) {
            address.append(order.getReceiverCity());
        }
        if (order.getReceiverRegion() != null) {
            address.append(order.getReceiverRegion());
        }
        if (order.getReceiverDetailAddress() != null) {
            address.append(order.getReceiverDetailAddress());
        }
        
        return address.toString();
    }
    
    /**
     * 写入CSV内容
     */
    private void writeCsvContent(PrintWriter writer, List<OmsOrderCsvData> orderCsvDataList) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String currentTime = dateFormat.format(new Date());
        
        // 写入头部
        writeHeader(writer, currentTime, orderCsvDataList.size());
        
        // 写入数据明细
        writeDataDetails(writer, orderCsvDataList, dateFormat);
        
        // 写入尾部
        writeFooter(writer, currentTime);
    }
    
    /**
     * 写入头部
     */
    private void writeHeader(PrintWriter writer, String currentTime, int recordCount) {
        StringBuilder header = new StringBuilder();
        
        // 创建时间
        header.append(wrapField(currentTime)).append(csvConfig.getDelimiter());
        // 标识符
        header.append(wrapField(csvConfig.getHeaderIdentifier())).append(csvConfig.getDelimiter());
        // 记录数
        header.append(wrapField(String.valueOf(recordCount)));
        
        writer.print(header.toString() + csvConfig.getLineBreak());
    }
    
    /**
     * 写入数据明细
     */
    private void writeDataDetails(PrintWriter writer, List<OmsOrderCsvData> orderCsvDataList, SimpleDateFormat dateFormat) {
        for (OmsOrderCsvData csvData : orderCsvDataList) {
            StringBuilder line = new StringBuilder();
            
            line.append(wrapField(csvData.getOrderId())).append(csvConfig.getDelimiter());
            line.append(wrapField(csvData.getUserId())).append(csvConfig.getDelimiter());
            line.append(wrapField(csvData.getOrderDate() != null ? dateFormat.format(csvData.getOrderDate()) : "")).append(csvConfig.getDelimiter());
            line.append(wrapField(csvData.getTotalAmount().toString())).append(csvConfig.getDelimiter());
            line.append(wrapField(csvData.getStatus())).append(csvConfig.getDelimiter());
            line.append(wrapField(csvData.getPaymentMethod())).append(csvConfig.getDelimiter());
            line.append(wrapField(csvData.getItemCount().toString())).append(csvConfig.getDelimiter());
            line.append(wrapField(csvData.getCurrency())).append(csvConfig.getDelimiter());
            line.append(wrapField(csvData.getShippingAddress() != null ? csvData.getShippingAddress() : "")).append(csvConfig.getDelimiter());
            line.append(wrapField(csvData.getRecipientName() != null ? csvData.getRecipientName() : "")).append(csvConfig.getDelimiter());
            line.append(wrapField(csvData.getRecipientPhone() != null ? csvData.getRecipientPhone() : ""));
            
            writer.print(line.toString() + csvConfig.getLineBreak());
        }
    }
    
    /**
     * 写入尾部
     */
    private void writeFooter(PrintWriter writer, String currentTime) {
        StringBuilder footer = new StringBuilder();
        
        // 生成时间
        footer.append(wrapField(currentTime)).append(csvConfig.getDelimiter());
        // 标识符
        footer.append(wrapField(csvConfig.getFooterIdentifier()));
        
        writer.print(footer.toString() + csvConfig.getLineBreak());
    }
    
    /**
     * 包装字段（用双引号）
     */
    private String wrapField(String field) {
        if (field == null) field = "";
        // 如果字段中包含双引号，需要转义
        String escapedField = field.replace("\"", "\"\"");
        return csvConfig.getWrapper() + escapedField + csvConfig.getWrapper();
    }
}
