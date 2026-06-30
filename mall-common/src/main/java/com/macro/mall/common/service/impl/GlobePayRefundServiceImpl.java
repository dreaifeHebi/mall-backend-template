package com.macro.mall.common.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.common.config.GlobePayConfig;
import com.macro.mall.common.domain.refund.GlobePayRefundRequest;
import com.macro.mall.common.domain.refund.GlobePayRefundResponse;
import com.macro.mall.common.service.GlobePayRefundService;
import com.macro.mall.common.domain.refund.GlobePayRefundQueryResponse;
import com.macro.mall.common.util.GlobePaySignUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * GlobePay退款服务实现类
 * @author macrozheng
 * @date 2025/10/13
 */
@Service
public class GlobePayRefundServiceImpl implements GlobePayRefundService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobePayRefundServiceImpl.class);
    
    @Autowired
    private GlobePayConfig globePayConfig;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public GlobePayRefundResponse createRefund(GlobePayRefundRequest request) {
        try {
            // 生成签名参数
            long time = GlobePaySignUtils.getCurrentUtcTime();
            String nonceStr = GlobePaySignUtils.generateNonceStr();
            String sign = GlobePaySignUtils.generateSign(
                globePayConfig.getPartnerCode(), 
                time, 
                nonceStr, 
                globePayConfig.getCredentialCode()
            );
            
            // 构造请求URL - 根据API文档
            String refundUrl = UriComponentsBuilder
                .fromHttpUrl(globePayConfig.getBaseUrl())
                .path("/api/v1.0/gateway/partners/{partner_code}/orders/{order_id}/refunds/{refund_id}")
                .queryParam("time", time)
                .queryParam("nonce_str", nonceStr)
                .queryParam("sign", sign)
                .buildAndExpand(
                    globePayConfig.getPartnerCode(), 
                    request.getMerTransactionId(),  // order_id 使用商户订单号
                    request.getRefundSn()           // refund_id 使用商户退款单号
                )
                .toUriString();
            
            // 构造请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "application/json");
            
            // 构造请求体 - 根据API文档只需要fee字段
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("fee", request.getRefundAmount());  // 退款金额，单位是货币最小单位
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // API调用前日志
            LOGGER.info("================= GlobePay 退款API调用开始 =================");
            LOGGER.info("API路径: PUT {}", refundUrl);
            LOGGER.info("请求参数: {}", objectMapper.writeValueAsString(requestBody));
            LOGGER.info("===========================================================");
            
            long startTime = System.currentTimeMillis();
            
            // 发送PUT请求 - 根据API文档
            ResponseEntity<String> response = restTemplate.exchange(
                refundUrl,
                HttpMethod.PUT,
                entity,
                String.class
            );
            
            long endTime = System.currentTimeMillis();
            
            // API调用后日志
            LOGGER.info("================= GlobePay 退款API响应 =================");
            LOGGER.info("响应状态: {}", response.getStatusCode());
            LOGGER.info("响应内容: {}", response.getBody());
            LOGGER.info("耗时: {}ms", (endTime - startTime));
            LOGGER.info("======================================================");
            
            // 解析响应
            GlobePayRefundResponse result = objectMapper.readValue(response.getBody(), GlobePayRefundResponse.class);
            LOGGER.info("GlobePay退款响应解析: {}", objectMapper.writeValueAsString(result));
            
            return result;
        } catch (Exception e) {
            LOGGER.error("创建GlobePay退款异常", e);
            GlobePayRefundResponse errorResponse = new GlobePayRefundResponse();
            errorResponse.setReturnCode("FAIL");
            errorResponse.setReturnMsg("退款请求异常: " + e.getMessage());
            errorResponse.setSuccess(false);
            errorResponse.setCode("ERROR");
            errorResponse.setMessage("退款请求异常: " + e.getMessage());
            return errorResponse;
        }
    }

    @Override
    public GlobePayRefundQueryResponse queryRefundStatus(String orderId, String refundId) {
        try {
            // 生成签名参数
            long time = GlobePaySignUtils.getCurrentUtcTime();
            String nonceStr = GlobePaySignUtils.generateNonceStr();
            String sign = GlobePaySignUtils.generateSign(
                globePayConfig.getPartnerCode(), 
                time, 
                nonceStr, 
                globePayConfig.getCredentialCode()
            );
            
            // 构造查询URL - 根据API文档
            String queryUrl = UriComponentsBuilder
                .fromHttpUrl(globePayConfig.getBaseUrl())
                .path("/api/v1.0/gateway/partners/{partner_code}/orders/{order_id}/refunds/{refund_id}")
                .queryParam("time", time)
                .queryParam("nonce_str", nonceStr)
                .queryParam("sign", sign)
                .buildAndExpand(
                    globePayConfig.getPartnerCode(), 
                    orderId,    // order_id 商户支付订单号
                    refundId    // refund_id 商户退款单号
                )
                .toUriString();
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // API调用前日志
            LOGGER.info("================= GlobePay 退款查询API调用开始 =================");
            LOGGER.info("API路径: GET {}", queryUrl);
            LOGGER.info("订单ID: {}, 退款ID: {}", orderId, refundId);
            LOGGER.info("===============================================================");
            
            long startTime = System.currentTimeMillis();
            
            // 发送GET请求
            ResponseEntity<String> response = restTemplate.exchange(
                queryUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
            );
            
            long endTime = System.currentTimeMillis();
            
            // API调用后日志
            LOGGER.info("================= GlobePay 退款查询API响应 =================");
            LOGGER.info("响应状态: {}", response.getStatusCode());
            LOGGER.info("响应内容: {}", response.getBody());
            LOGGER.info("耗时: {}ms", (endTime - startTime));
            LOGGER.info("==========================================================");
            
            // 解析响应
            GlobePayRefundQueryResponse result = objectMapper.readValue(response.getBody(), GlobePayRefundQueryResponse.class);
            LOGGER.info("GlobePay退款查询响应解析: {}", objectMapper.writeValueAsString(result));
            
            return result;
        } catch (Exception e) {
            LOGGER.error("查询GlobePay退款状态异常: orderId={}, refundId={}", orderId, refundId, e);
            GlobePayRefundQueryResponse errorResponse = new GlobePayRefundQueryResponse();
            errorResponse.setReturnCode("FAIL");
            errorResponse.setReturnMsg("退款查询异常: " + e.getMessage());
            errorResponse.setSuccess(false);
            errorResponse.setCode("ERROR");
            errorResponse.setMessage("退款查询异常: " + e.getMessage());
            return errorResponse;
        }
    }
}