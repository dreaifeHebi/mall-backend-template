package com.macro.mall.dao.refund;

import com.macro.mall.domain.refund.RefundProcessLog;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 退款处理日志数据访问接口
 * @author macrozheng
 * @date 2025/10/13
 */
public interface RefundProcessLogDao {
    
    /**
     * 插入退款处理日志
     */
    int insert(RefundProcessLog refundProcessLog);
    
    /**
     * 批量插入退款处理日志
     */
    int batchInsert(@Param("logs") List<RefundProcessLog> logs);
    
    /**
     * 根据退款申请ID查询日志
     */
    List<RefundProcessLog> selectByRefundRequestId(@Param("refundRequestId") Long refundRequestId);
    
    /**
     * 根据退款申请ID和操作类型查询日志
     */
    List<RefundProcessLog> selectByRefundRequestIdAndType(@Param("refundRequestId") Long refundRequestId,
                                                         @Param("operationType") String operationType);
    
    /**
     * 根据退款单号查询日志
     */
    List<RefundProcessLog> selectByRefundSn(@Param("refundSn") String refundSn);
    
    /**
     * 根据操作类型和状态查询日志
     */
    List<RefundProcessLog> selectByTypeAndStatus(@Param("operationType") String operationType,
                                               @Param("operationStatus") String operationStatus,
                                               @Param("startDate") Date startDate,
                                               @Param("endDate") Date endDate,
                                               @Param("limit") Integer limit);
    
    /**
     * 查询操作员的日志
     */
    List<RefundProcessLog> selectByOperator(@Param("operatorId") Long operatorId,
                                           @Param("startDate") Date startDate,
                                           @Param("endDate") Date endDate,
                                           @Param("limit") Integer limit);
    
    /**
     * 查询失败的操作日志
     */
    List<RefundProcessLog> selectFailedOperations(@Param("startDate") Date startDate,
                                                 @Param("endDate") Date endDate,
                                                 @Param("limit") Integer limit);
    
    /**
     * 统计操作日志
     */
    Map<String, Object> selectLogStatistics(@Param("startDate") Date startDate,
                                           @Param("endDate") Date endDate);
    
    /**
     * 查询处理时间最长的操作
     */
    List<RefundProcessLog> selectLongestProcessingOperations(@Param("limit") Integer limit);
    
    /**
     * 根据ID列表批量查询
     */
    List<RefundProcessLog> selectByIds(@Param("ids") List<Long> ids);
    
    /**
     * 删除过期日志
     */
    int deleteExpiredLogs(@Param("expireDate") Date expireDate);
}