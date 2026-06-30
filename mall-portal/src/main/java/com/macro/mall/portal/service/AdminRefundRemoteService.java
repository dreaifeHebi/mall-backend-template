package com.macro.mall.portal.service;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.domain.refund.RefundApplyParam;
import com.macro.mall.common.domain.refund.RefundRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Admin模块退款服务远程调用客户端
 * @author macrozheng
 * @date 2025/10/14
 */
@Service
public class AdminRefundRemoteService {
    
    @Value("${mall.admin.base-url:http://localhost:8080}")
    private String adminBaseUrl;
    
    private final RestTemplate restTemplate;
    
    public AdminRefundRemoteService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    /**
     * 申请退款
     */
    public CommonResult<RefundRequest> applyRefund(RefundApplyParam param, Long memberId, String memberName) {
        String url = adminBaseUrl + "/admin/refund/member/apply";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Member-Id", memberId.toString());
        headers.set("Member-Name", memberName);
        
        HttpEntity<RefundApplyParam> entity = new HttpEntity<>(param, headers);
        
        try {
            ResponseEntity<CommonResult<RefundRequest>> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, 
                new ParameterizedTypeReference<CommonResult<RefundRequest>>() {});
            return response.getBody();
        } catch (Exception e) {
            return CommonResult.failed("申请退款失败: " + e.getMessage());
        }
    }
    
    /**
     * 取消退款申请
     */
    public CommonResult<Void> cancelRefund(Long refundRequestId, Long memberId, String memberName) {
        String url = adminBaseUrl + "/admin/refund/member/cancel/" + refundRequestId;
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Member-Id", memberId.toString());
        headers.set("Member-Name", memberName);
        
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<CommonResult<Void>> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, 
                new ParameterizedTypeReference<CommonResult<Void>>() {});
            return response.getBody();
        } catch (Exception e) {
            return CommonResult.failed("取消退款失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取会员退款列表
     */
    public CommonResult<Object> getMemberRefundList(Long memberId, String status, Integer pageNum, Integer pageSize) {
        String url = adminBaseUrl + "/admin/refund/member/list?memberId=" + memberId;
        if (status != null) {
            url += "&status=" + status;
        }
        if (pageNum != null) {
            url += "&pageNum=" + pageNum;
        }
        if (pageSize != null) {
            url += "&pageSize=" + pageSize;
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Member-Id", memberId.toString());
        
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<CommonResult<Object>> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, 
                new ParameterizedTypeReference<CommonResult<Object>>() {});
            return response.getBody();
        } catch (Exception e) {
            return CommonResult.failed("获取退款列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取退款详情
     */
    public CommonResult<RefundRequest> getRefundDetail(Long refundRequestId, Long memberId) {
        String url = adminBaseUrl + "/admin/refund/member/" + refundRequestId;
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Member-Id", memberId.toString());
        
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<CommonResult<RefundRequest>> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, 
                new ParameterizedTypeReference<CommonResult<RefundRequest>>() {});
            return response.getBody();
        } catch (Exception e) {
            return CommonResult.failed("获取退款详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 查询退款状态
     */
    public CommonResult<RefundRequest> queryRefundStatus(Long refundRequestId) {
        String url = adminBaseUrl + "/admin/refund/status/" + refundRequestId;
        
        try {
            ResponseEntity<CommonResult<RefundRequest>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, 
                new ParameterizedTypeReference<CommonResult<RefundRequest>>() {});
            return response.getBody();
        } catch (Exception e) {
            return CommonResult.failed("查询退款状态失败: " + e.getMessage());
        }
    }
}