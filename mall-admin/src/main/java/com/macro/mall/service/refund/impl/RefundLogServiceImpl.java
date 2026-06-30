package com.macro.mall.service.refund.impl;

import com.macro.mall.dao.refund.RefundProcessLogDao;
import com.macro.mall.domain.refund.RefundProcessLog;
import com.macro.mall.domain.refund.RefundOperationType;
import com.macro.mall.service.refund.RefundLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 退款日志服务实现类
 * @author macrozheng
 * @date 2025/10/13
 */
@Service
public class RefundLogServiceImpl implements RefundLogService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RefundLogServiceImpl.class);
    
    @Autowired
    private RefundProcessLogDao refundProcessLogDao;

    @Override
    public void recordLog(Long refundRequestId, String refundSn, RefundOperationType operationType, 
                         String operationContent, String operationStatus, String requestData, 
                         String responseData, String errorMessage, Long operatorId, String operatorName) {
        recordLog(refundRequestId, refundSn, operationType, operationContent, operationStatus, 
                 requestData, responseData, errorMessage, operatorId, operatorName, null, null);
    }

    @Override
    public void recordLog(Long refundRequestId, String refundSn, RefundOperationType operationType, 
                         String operationContent, String operationStatus, String requestData, 
                         String responseData, String errorMessage, Long operatorId, String operatorName,
                         String operatorIp, Long processingTimeMs) {
        try {
            RefundProcessLog log = new RefundProcessLog();
            log.setRefundRequestId(refundRequestId);
            log.setRefundSn(refundSn);
            log.setOperationType(operationType.getCode());
            log.setOperationNote(operationContent);
            log.setOperationStatus(operationStatus);
            log.setRequestParams(requestData);
            log.setResponseData(responseData);
            log.setErrorMessage(errorMessage);
            log.setOperatorId(operatorId);
            log.setOperatorName(operatorName);
            log.setOperatorIp(operatorIp);
            log.setProcessingTimeMs(processingTimeMs);
            log.setOperationTime(new Date());
            log.setCreateTime(new Date());
            
            refundProcessLogDao.insert(log);
        } catch (Exception e) {
            LOGGER.error("记录退款操作日志失败", e);
        }
    }

    @Override
    public void batchRecordLogs(List<RefundProcessLog> logs) {
        try {
            if (logs != null && !logs.isEmpty()) {
                // 设置时间
                Date now = new Date();
                for (RefundProcessLog log : logs) {
                    if (log.getOperationTime() == null) {
                        log.setOperationTime(now);
                    }
                    if (log.getCreateTime() == null) {
                        log.setCreateTime(now);
                    }
                }
                refundProcessLogDao.batchInsert(logs);
            }
        } catch (Exception e) {
            LOGGER.error("批量记录退款操作日志失败", e);
        }
    }

    @Override
    public List<RefundProcessLog> getRefundLogs(Long refundRequestId) {
        return refundProcessLogDao.selectByRefundRequestId(refundRequestId);
    }

    @Override
    public List<RefundProcessLog> getRefundLogsByType(Long refundRequestId, RefundOperationType operationType) {
        return refundProcessLogDao.selectByRefundRequestIdAndType(refundRequestId, operationType.getCode());
    }

    @Override
    public List<RefundProcessLog> getOperatorLogs(Long operatorId, Date startDate, Date endDate, Integer limit) {
        return refundProcessLogDao.selectByOperator(operatorId, startDate, endDate, limit);
    }

    @Override
    public List<RefundProcessLog> getFailedOperations(Date startDate, Date endDate, Integer limit) {
        return refundProcessLogDao.selectFailedOperations(startDate, endDate, limit);
    }

    @Override
    public Map<String, Object> getLogStatistics(Date startDate, Date endDate) {
        return refundProcessLogDao.selectLogStatistics(startDate, endDate);
    }

    @Override
    public List<RefundProcessLog> getLongestProcessingOperations(Integer limit) {
        return refundProcessLogDao.selectLongestProcessingOperations(limit);
    }

    @Override
    public int cleanExpiredLogs(int retentionDays) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -retentionDays);
            Date expireDate = calendar.getTime();
            
            int deletedCount = refundProcessLogDao.deleteExpiredLogs(expireDate);
            LOGGER.info("清理过期退款日志完成，删除了 {} 条记录", deletedCount);
            return deletedCount;
        } catch (Exception e) {
            LOGGER.error("清理过期退款日志失败", e);
            return 0;
        }
    }
}