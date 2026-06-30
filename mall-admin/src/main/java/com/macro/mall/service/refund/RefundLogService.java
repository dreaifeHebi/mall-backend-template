package com.macro.mall.service.refund;

import com.macro.mall.domain.refund.RefundProcessLog;
import com.macro.mall.domain.refund.RefundOperationType;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 退款日志服务接口
 * @author macrozheng
 * @date 2025/10/13
 */
public interface RefundLogService {
    
    /**
     * 记录退款操作日志
     */
    void recordLog(Long refundRequestId, String refundSn, RefundOperationType operationType, 
                   String operationContent, String operationStatus, String requestData, 
                   String responseData, String errorMessage, Long operatorId, String operatorName);
    
    /**
     * 记录退款操作日志（带IP和处理时间）
     */
    void recordLog(Long refundRequestId, String refundSn, RefundOperationType operationType, 
                   String operationContent, String operationStatus, String requestData, 
                   String responseData, String errorMessage, Long operatorId, String operatorName,
                   String operatorIp, Long processingTimeMs);
    
    /**
     * 批量记录日志
     */
    void batchRecordLogs(List<RefundProcessLog> logs);
    
    /**
     * 获取退款申请的操作日志
     */
    List<RefundProcessLog> getRefundLogs(Long refundRequestId);
    
    /**
     * 根据操作类型获取退款日志
     */
    List<RefundProcessLog> getRefundLogsByType(Long refundRequestId, RefundOperationType operationType);
    
    /**
     * 获取操作员的操作日志
     */
    List<RefundProcessLog> getOperatorLogs(Long operatorId, Date startDate, Date endDate, Integer limit);
    
    /**
     * 获取失败的操作日志
     */
    List<RefundProcessLog> getFailedOperations(Date startDate, Date endDate, Integer limit);
    
    /**
     * 获取日志统计信息
     */
    Map<String, Object> getLogStatistics(Date startDate, Date endDate);
    
    /**
     * 获取处理时间最长的操作
     */
    List<RefundProcessLog> getLongestProcessingOperations(Integer limit);
    
    /**
     * 清理过期日志
     */
    int cleanExpiredLogs(int retentionDays);
}