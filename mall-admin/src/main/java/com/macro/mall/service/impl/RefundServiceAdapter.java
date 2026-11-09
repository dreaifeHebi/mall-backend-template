package com.macro.mall.service.impl;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.domain.refund.RefundApplyParam;
import com.macro.mall.common.domain.refund.RefundAuditParam;
import com.macro.mall.common.domain.refund.RefundRequest;
import com.macro.mall.common.service.refund.RefundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * 退款服务适配器 - 通过HTTP调用Portal服务
 * @author dreaifekks
 * @date 2025/7/27
 */
@Service
public class RefundServiceAdapter implements RefundService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RefundServiceAdapter.class);

    @Value("${portal.service.base-url:http://localhost:8085}")
    private String portalBaseUrl;

    @Resource
    private RestTemplate restTemplate;

    @Override
    public CommonResult<RefundRequest> applyRefund(RefundApplyParam param, Long memberId) {
        // Admin模块不支持此操作
        return CommonResult.failed("管理员不能申请退款");
    }

    @Override
    public CommonResult<RefundRequest> auditRefund(RefundAuditParam param, Long auditorId, String auditorName) {
        try {
            String url = portalBaseUrl + "/admin/refund/audit";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Admin-Id", auditorId.toString());
            headers.set("Admin-Name", auditorName);

            HttpEntity<RefundAuditParam> entity = new HttpEntity<>(param, headers);

            ResponseEntity<CommonResult> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, CommonResult.class);

            return response.getBody();
        } catch (Exception e) {
            LOGGER.error("调用Portal服务审核退款异常", e);
            return CommonResult.failed("审核退款异常: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<RefundRequest> processRefund(Long refundRequestId, Long operatorId, String operatorName) {
        try {
            String url = portalBaseUrl + "/admin/refund/process/" + refundRequestId;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Admin-Id", operatorId.toString());
            headers.set("Admin-Name", operatorName);

            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<CommonResult> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, CommonResult.class);

            return response.getBody();
        } catch (Exception e) {
            LOGGER.error("调用Portal服务处理退款异常", e);
            return CommonResult.failed("处理退款异常: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<RefundRequest> queryRefundStatus(Long refundRequestId) {
        try {
            String url = portalBaseUrl + "/admin/refund/status/" + refundRequestId;

            ResponseEntity<CommonResult> response = restTemplate.exchange(
                url, HttpMethod.GET, null, CommonResult.class);

            return response.getBody();
        } catch (Exception e) {
            LOGGER.error("调用Portal服务查询退款状态异常", e);
            return CommonResult.failed("查询退款状态异常: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> cancelRefund(Long refundRequestId, Long memberId) {
        // Admin模块不支持此操作
        return CommonResult.failed("管理员不能取消会员退款申请");
    }

    @Override
    public CommonResult<List<RefundRequest>> getMemberRefundList(Long memberId, String status, Integer pageNum, Integer pageSize) {
        // Admin模块不支持此操作
        return CommonResult.failed("请使用管理员查询接口");
    }

    @Override
    public CommonResult<RefundRequest> getRefundDetail(Long refundRequestId, Long memberId) {
        // Admin模块不支持此操作
        return CommonResult.failed("请使用管理员查询接口");
    }

    @Override
    public CommonResult<List<RefundRequest>> getAdminRefundList(String status, Integer pageNum, Integer pageSize) {
        try {
            String url = portalBaseUrl + "/admin/refund/list";

            StringBuilder urlBuilder = new StringBuilder(url).append("?pageNum=").append(pageNum).append("&pageSize=").append(pageSize);
            if (status != null && !status.isEmpty()) {
                urlBuilder.append("&status=").append(status);
            }

            ResponseEntity<CommonResult> response = restTemplate.exchange(
                urlBuilder.toString(), HttpMethod.GET, null, CommonResult.class);

            return response.getBody();
        } catch (Exception e) {
            LOGGER.error("调用Portal服务查询管理员退款列表异常", e);
            return CommonResult.failed("查询退款列表异常: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<RefundRequest> getAdminRefundDetail(Long refundRequestId) {
        try {
            String url = portalBaseUrl + "/admin/refund/" + refundRequestId;

            ResponseEntity<CommonResult> response = restTemplate.exchange(
                url, HttpMethod.GET, null, CommonResult.class);

            return response.getBody();
        } catch (Exception e) {
            LOGGER.error("调用Portal服务查询管理员退款详情异常", e);
            return CommonResult.failed("查询退款详情异常: " + e.getMessage());
        }
    }

    @Override
    public CommonResult<Integer> autoQueryPendingRefunds() {
        try {
            String url = portalBaseUrl + "/admin/refund/auto-query";

            ResponseEntity<CommonResult> response = restTemplate.exchange(
                url, HttpMethod.POST, null, CommonResult.class);

            return response.getBody();
        } catch (Exception e) {
            LOGGER.error("调用Portal服务自动查询待处理退款异常", e);
            return CommonResult.failed("自动查询异常: " + e.getMessage());
        }
    }
}
