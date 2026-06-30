package com.macro.mall.portal.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.common.config.GlobePayConfig;
import com.macro.mall.portal.domain.payment.GlobePayH5Request;
import com.macro.mall.portal.domain.payment.GlobePayH5Response;
import com.macro.mall.portal.domain.payment.GlobePayOrderQueryResponse;
import com.macro.mall.portal.domain.payment.GlobePayCreditCardRequest;
import com.macro.mall.portal.domain.payment.GlobePayCreditCardResponse;
import com.macro.mall.common.domain.refund.GlobePayRefundRequest;
import com.macro.mall.common.domain.refund.GlobePayRefundResponse;
import com.macro.mall.common.domain.refund.GlobePayRefundQueryResponse;
import com.macro.mall.portal.service.GlobePayService;
import com.macro.mall.portal.util.GlobePaySignUtils;
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
 * GlobePay支付服务实现类
 * @author macrozheng
 * @date 2025/7/26
 */
@Service
public class GlobePayServiceImpl implements GlobePayService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobePayServiceImpl.class);
    
    @Autowired
    private GlobePayConfig globePayConfig;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Override
    public GlobePayH5Response createH5Payment(String orderId, GlobePayH5Request request) {
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
            
            // 构建请求URL
            String url = UriComponentsBuilder
                .fromHttpUrl(globePayConfig.getBaseUrl())
                .path("/api/v1.0/h5_payment/partners/{partner_code}/orders/{order_id}")
                .queryParam("time", time)
                .queryParam("nonce_str", nonceStr)
                .queryParam("sign", sign)
                .buildAndExpand(globePayConfig.getPartnerCode(), orderId)
                .toUriString();
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "application/json");
            
            // 创建请求实体
            HttpEntity<GlobePayH5Request> entity = new HttpEntity<>(request, headers);
            
            // API调用前日志
            LOGGER.info("================= GlobePay API调用开始 =================");
            LOGGER.info("API路径: PUT {}", url);
            LOGGER.info("请求参数: {}", objectMapper.writeValueAsString(request));
            LOGGER.info("========================================================");
            
            long startTime = System.currentTimeMillis();
            
            // 发送PUT请求
            ResponseEntity<String> response = restTemplate.exchange(
                url, 
                HttpMethod.PUT, 
                entity, 
                String.class
            );
            
            long endTime = System.currentTimeMillis();
            
            // API调用后日志
            LOGGER.info("================= GlobePay API响应 =================");
            LOGGER.info("响应状态: {}", response.getStatusCode());
            LOGGER.info("响应内容: {}", response.getBody());
            LOGGER.info("耗时: {}ms", (endTime - startTime));
            LOGGER.info("====================================================");
            
            // 解析响应
            GlobePayH5Response payResponse = objectMapper.readValue(response.getBody(), GlobePayH5Response.class);
            return payResponse;
            
        } catch (Exception e) {
            LOGGER.error("=== GlobePay H5支付订单创建异常 ===");
            LOGGER.error("订单ID: {}", orderId);
            LOGGER.error("异常类型: {}", e.getClass().getSimpleName());
            LOGGER.error("异常信息: {}", e.getMessage());
            LOGGER.error("异常堆栈: ", e);
            LOGGER.error("=== GlobePay H5支付订单创建异常结束 ===");
            
            GlobePayH5Response errorResponse = new GlobePayH5Response();
            errorResponse.setReturnCode("FAIL");
            errorResponse.setReturnMsg("创建支付订单失败: " + e.getMessage());
            return errorResponse;
        }
    }
    
    @Override
    public String generateH5PaymentUrl(String payUrl, String redirectUrl) {
        LOGGER.info("生成H5支付跳转URL: payUrl={}, redirectUrl={}", payUrl, redirectUrl);
        // 生成签名参数
        long time = GlobePaySignUtils.getCurrentUtcTime();
        String nonceStr = GlobePaySignUtils.generateNonceStr();
        String sign = GlobePaySignUtils.generateSign(
            globePayConfig.getPartnerCode(), 
            time, 
            nonceStr, 
            globePayConfig.getCredentialCode()
        );
        
        // 在API返回的payUrl基础上添加必要的查询参数
        return UriComponentsBuilder
            .fromHttpUrl(payUrl)
            .queryParam("time", time)
            .queryParam("nonce_str", nonceStr)
            .queryParam("sign", sign)
            .queryParam("redirect", redirectUrl)
            .build()
            .toUriString();
    }
    
    @Override
    public String generateAlipayPlusPaymentUrl(String payUrl, String redirectUrl) {
        // 生成签名参数
        long time = GlobePaySignUtils.getCurrentUtcTime();
        String nonceStr = GlobePaySignUtils.generateNonceStr();
        String sign = GlobePaySignUtils.generateSign(
            globePayConfig.getPartnerCode(), 
            time, 
            nonceStr, 
            globePayConfig.getCredentialCode()
        );
        
        // 在API返回的payUrl基础上添加必要的查询参数
        return UriComponentsBuilder
            .fromHttpUrl(payUrl)
            .queryParam("time", time)
            .queryParam("nonce_str", nonceStr)
            .queryParam("sign", sign)
            .queryParam("redirect", redirectUrl)
            .build()
            .toUriString();
    }
    
    @Override
    public GlobePayH5Response queryOrderStatus(String orderId) {
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
            
            // 构建查询URL（根据GlobePay官方API文档）
            String url = UriComponentsBuilder
                .fromHttpUrl(globePayConfig.getBaseUrl())
                .path("/api/v1.0/gateway/partners/{partner_code}/orders/{order_id}")
                .queryParam("time", time)
                .queryParam("nonce_str", nonceStr)
                .queryParam("sign", sign)
                .buildAndExpand(globePayConfig.getPartnerCode(), orderId)
                .toUriString();
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");
            
            // API调用前日志
            LOGGER.info("================= GlobePay 订单查询API调用开始 =================");
            LOGGER.info("API路径: GET {}", url);
            LOGGER.info("请求参数: 无");
            LOGGER.info("===============================================================");
            
            long startTime = System.currentTimeMillis();
            
            // 发送GET请求
            ResponseEntity<String> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                new HttpEntity<>(headers), 
                String.class
            );
            
            long endTime = System.currentTimeMillis();
            
            // API调用后日志
            LOGGER.info("================= GlobePay 订单查询API响应 =================");
            LOGGER.info("响应状态: {}", response.getStatusCode());
            LOGGER.info("响应内容: {}", response.getBody());
            LOGGER.info("耗时: {}ms", (endTime - startTime));
            LOGGER.info("=========================================================");
            
            // 解析响应
            return objectMapper.readValue(response.getBody(), GlobePayH5Response.class);
            
        } catch (Exception e) {
            LOGGER.error("查询GlobePay订单状态失败: orderId={}", orderId, e);
            GlobePayH5Response errorResponse = new GlobePayH5Response();
            errorResponse.setReturnCode("FAIL");
            errorResponse.setReturnMsg("查询订单状态失败: " + e.getMessage());
            return errorResponse;
        }
    }
    
    @Override
    public GlobePayOrderQueryResponse queryOrderStatusDetail(String orderId) {
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
            
            // 构建查询URL（根据GlobePay官方API文档）
            String url = UriComponentsBuilder
                .fromHttpUrl(globePayConfig.getBaseUrl())  
                .path("/api/v1.0/gateway/partners/{partner_code}/orders/{order_id}")
                .queryParam("time", time)
                .queryParam("nonce_str", nonceStr)
                .queryParam("sign", sign)
                .buildAndExpand(globePayConfig.getPartnerCode(), orderId)
                .toUriString();
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");
            
            // API调用前日志
            LOGGER.info("================= GlobePay 订单详情查询API调用开始 =================");
            LOGGER.info("订单ID: {}", orderId);
            LOGGER.info("API路径: GET {}", url);
            LOGGER.info("请求头: Accept=application/json");
            LOGGER.info("签名参数: time={}, nonce_str={}, sign={}", time, nonceStr, sign);
            LOGGER.info("================================================================");
            
            long startTime = System.currentTimeMillis();
            
            // 发送GET请求
            ResponseEntity<String> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                new HttpEntity<>(headers), 
                String.class
            );
            
            long endTime = System.currentTimeMillis();
            
            // API调用后日志
            LOGGER.info("================= GlobePay 订单详情查询API响应 =================");
            LOGGER.info("订单ID: {}", orderId);
            LOGGER.info("响应状态: {}", response.getStatusCode());
            LOGGER.info("响应内容: {}", response.getBody());
            LOGGER.info("耗时: {}ms", (endTime - startTime));
            LOGGER.info("===============================================================");
            
            // 解析响应
            GlobePayOrderQueryResponse queryResponse = objectMapper.readValue(response.getBody(), GlobePayOrderQueryResponse.class);
            
            // 详细响应日志
            LOGGER.info("================= GlobePay 订单详情查询解析结果 =================");
            LOGGER.info("订单ID: {}", orderId);
            LOGGER.info("返回码: {}", queryResponse.getReturnCode());
            LOGGER.info("订单状态: {}", queryResponse.getResultCode());
            LOGGER.info("商户订单ID: {}", queryResponse.getPartnerOrderId());
            LOGGER.info("GlobePay订单ID: {}", queryResponse.getOrderId());
            LOGGER.info("渠道订单ID: {}", queryResponse.getChannelOrderId());
            LOGGER.info("订单金额: {}", queryResponse.getTotalFee());
            LOGGER.info("实际支付金额: {}", queryResponse.getRealFee());
            LOGGER.info("币种: {}", queryResponse.getCurrency());
            LOGGER.info("支付渠道: {}", queryResponse.getChannel());
            LOGGER.info("支付时间: {}", queryResponse.getPayTime());
            LOGGER.info("创建时间: {}", queryResponse.getCreateTime());
            LOGGER.info("预授权标记: {}", queryResponse.getPreauthFlag());
            LOGGER.info("预授权状态: {}", queryResponse.getPreauthStatus());
            LOGGER.info("================================================================");
            
            return queryResponse;
            
        } catch (Exception e) {
            LOGGER.error("查询GlobePay订单详情失败: orderId={}", orderId, e);
            GlobePayOrderQueryResponse errorResponse = new GlobePayOrderQueryResponse();
            errorResponse.setReturnCode("FAIL");
            errorResponse.setReturnMsg("查询订单详情失败: " + e.getMessage());
            return errorResponse;
        }
    }
    
    @Override
    public GlobePayH5Response createCreditCardPayment(String orderId, GlobePayH5Request request) {
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
            
            // 步骤1: 创建绑卡请求 (根据新文档)
            String bindCardUrl = UriComponentsBuilder
                .fromHttpUrl(globePayConfig.getBaseUrl())
                .path("/api/v1.0/gateway/partners/{partner_code}/bind_card_orders/{requestId}/create")
                .queryParam("time", time)
                .queryParam("nonce_str", nonceStr)
                .queryParam("sign", sign)
                .buildAndExpand(globePayConfig.getPartnerCode(), orderId)
                .toUriString();
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "application/json");
            
            // 构建绑卡请求体
            String redirectUrl = globePayConfig.getCreditCardReturnUrl() != null ? 
                globePayConfig.getCreditCardReturnUrl() : 
                "http://localhost/portal/h5-payment-test.html";
            
            String bindCardRequestBody = "{\"redirect_url\":\"" + redirectUrl + "\"}";
            
            // API调用前日志 - 模仿支付宝格式
            LOGGER.info("================= GlobePay 信用卡绑卡API调用开始 =================");
            LOGGER.info("API路径: PUT {}", bindCardUrl);
            LOGGER.info("请求参数: {}", bindCardRequestBody);
            LOGGER.info("===============================================================");
            
            long startTime = System.currentTimeMillis();
            
            // 发送PUT请求创建绑卡 (尝试PUT方法)
            ResponseEntity<String> bindCardResponse = restTemplate.exchange(
                bindCardUrl, 
                HttpMethod.PUT, 
                new HttpEntity<>(bindCardRequestBody, headers), 
                String.class
            );
            
            long endTime = System.currentTimeMillis();
            
            // API调用后日志 - 模仿支付宝格式
            LOGGER.info("================= GlobePay 信用卡绑卡API响应 =================");
            LOGGER.info("响应状态: {}", bindCardResponse.getStatusCode());
            LOGGER.info("响应内容: {}", bindCardResponse.getBody());
            LOGGER.info("耗时: {}ms", (endTime - startTime));
            LOGGER.info("==============================================================");
            
            // 检查响应状态码
            if (!bindCardResponse.getStatusCode().is2xxSuccessful()) {
                LOGGER.error("=== GlobePay 信用卡绑卡请求失败 ===");
                LOGGER.error("HTTP状态码: {}", bindCardResponse.getStatusCode());
                LOGGER.error("响应体: {}", bindCardResponse.getBody());
                LOGGER.error("请求URL: {}", bindCardUrl);
                LOGGER.error("请求体: {}", bindCardRequestBody);
                LOGGER.error("=== GlobePay 信用卡绑卡请求失败结束 ===");
                
                GlobePayH5Response errorResponse = new GlobePayH5Response();
                errorResponse.setReturnCode("FAIL");
                errorResponse.setReturnMsg("绑卡请求失败: HTTP " + bindCardResponse.getStatusCode() 
                    + ", 响应: " + bindCardResponse.getBody());
                return errorResponse;
            }
            
            // 检查响应体是否为空或不是JSON格式
            String responseBody = bindCardResponse.getBody();
            if (responseBody == null || responseBody.trim().isEmpty()) {
                LOGGER.error("GlobePay信用卡绑卡响应为空");
                GlobePayH5Response errorResponse = new GlobePayH5Response();
                errorResponse.setReturnCode("FAIL");
                errorResponse.setReturnMsg("绑卡响应为空");
                return errorResponse;
            }
            
            // 尝试解析JSON，如果失败则记录原始响应
            GlobePayCreditCardResponse bindCardResult;
            try {
                bindCardResult = objectMapper.readValue(responseBody, GlobePayCreditCardResponse.class);
                LOGGER.info("GlobePay信用卡绑卡响应解析成功: returnCode={}, bindcardUrl={}", 
                    bindCardResult.getReturnCode(), bindCardResult.getBindcardUrl());
            } catch (Exception parseException) {
                LOGGER.error("=== GlobePay 信用卡绑卡响应解析失败 ===");
                LOGGER.error("解析异常: {}", parseException.getMessage());
                LOGGER.error("原始响应: {}", responseBody);
                LOGGER.error("异常堆栈: ", parseException);
                LOGGER.error("=== GlobePay 信用卡绑卡响应解析失败结束 ===");
                
                GlobePayH5Response errorResponse = new GlobePayH5Response();
                errorResponse.setReturnCode("FAIL");
                errorResponse.setReturnMsg("绑卡响应解析失败: " + parseException.getMessage() 
                    + ", 原始响应: " + responseBody);
                return errorResponse;
            }
            
            // 转换为H5响应格式，返回绑卡URL
            GlobePayH5Response h5Response = new GlobePayH5Response();
            h5Response.setReturnCode(bindCardResult.getReturnCode());
            h5Response.setResultCode(bindCardResult.getResultCode());
            h5Response.setPartnerCode(bindCardResult.getPartnerCode());
            h5Response.setOrderId(orderId);
            h5Response.setChannel("CREDIT_CARD");
            
            // 如果绑卡请求成功，返回绑卡URL
            if ("SUCCESS".equals(bindCardResult.getReturnCode())) {
                h5Response.setPayUrl(bindCardResult.getBindcardUrl());
                h5Response.setReturnMsg("请完成信用卡绑定后再进行支付");
                
                LOGGER.info("=== GlobePay 信用卡绑卡请求成功 ===");
                LOGGER.info("订单ID: {}", orderId);
                LOGGER.info("绑卡URL: {}", bindCardResult.getBindcardUrl());
                LOGGER.info("返回码: {}", bindCardResult.getReturnCode());
                LOGGER.info("=== GlobePay 信用卡绑卡请求成功结束 ===");
            } else {
                // 优先使用return_msg，如果为空则使用message字段
                String errorMsg = bindCardResult.getReturnMsg();
                if (errorMsg == null || errorMsg.trim().isEmpty()) {
                    errorMsg = bindCardResult.getMessage();
                }
                h5Response.setReturnMsg(errorMsg);
                
                LOGGER.error("=== GlobePay 信用卡绑卡请求业务失败 ===");
                LOGGER.error("订单ID: {}", orderId);
                LOGGER.error("返回码: {}", bindCardResult.getReturnCode());
                LOGGER.error("错误消息: {}", errorMsg);
                LOGGER.error("结果码: {}", bindCardResult.getResultCode());
                LOGGER.error("=== GlobePay 信用卡绑卡请求业务失败结束 ===");
            }
            
            return h5Response;
            
        } catch (Exception e) {
            LOGGER.error("=== GlobePay 信用卡绑卡异常 ===");
            LOGGER.error("订单ID: {}", orderId);
            LOGGER.error("异常类型: {}", e.getClass().getSimpleName());
            LOGGER.error("异常信息: {}", e.getMessage());
            LOGGER.error("异常堆栈: ", e);
            LOGGER.error("=== GlobePay 信用卡绑卡异常结束 ===");
            
            GlobePayH5Response errorResponse = new GlobePayH5Response();
            errorResponse.setReturnCode("FAIL");
            errorResponse.setReturnMsg("创建信用卡绑卡失败: " + e.getMessage());
            return errorResponse;
        }
    }
    
    @Override
    public GlobePayH5Response queryBindCardResult(String requestId) {
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
            
            // 查询绑卡结果URL
            String queryUrl = UriComponentsBuilder
                .fromHttpUrl(globePayConfig.getBaseUrl())
                .path("/api/v1.0/gateway/partners/{partner_code}/bind_card_orders/{requestId}")
                .queryParam("time", time)
                .queryParam("nonce_str", nonceStr)
                .queryParam("sign", sign)
                .buildAndExpand(globePayConfig.getPartnerCode(), requestId)
                .toUriString();
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");
            
            // API调用前日志
            LOGGER.info("================= GlobePay 绑卡结果查询API调用开始 =================");
            LOGGER.info("API路径: GET {}", queryUrl);
            LOGGER.info("请求参数: 无");
            LOGGER.info("==================================================================");
            
            long startTime = System.currentTimeMillis();
            
            // 发送GET请求查询绑卡结果
            ResponseEntity<String> response = restTemplate.exchange(
                queryUrl, 
                HttpMethod.GET, 
                new HttpEntity<>(headers), 
                String.class
            );
            
            long endTime = System.currentTimeMillis();
            
            // API调用后日志
            LOGGER.info("================= GlobePay 绑卡结果查询API响应 =================");
            LOGGER.info("响应状态: {}", response.getStatusCode());
            LOGGER.info("响应内容: {}", response.getBody());
            LOGGER.info("耗时: {}ms", (endTime - startTime));
            LOGGER.info("==============================================================");
            
            // 解析查询响应  
            GlobePayCreditCardResponse bindCardResult = objectMapper.readValue(response.getBody(), GlobePayCreditCardResponse.class);
            
            // 转换为H5响应格式
            GlobePayH5Response h5Response = new GlobePayH5Response();
            h5Response.setReturnCode(bindCardResult.getReturnCode());
            h5Response.setResultCode(bindCardResult.getResultCode());
            h5Response.setPartnerCode(bindCardResult.getPartnerCode());
            h5Response.setOrderId(requestId);
            h5Response.setChannel("CREDIT_CARD");
            
            // 如果查询成功且已绑卡，返回member_token
            if ("SUCCESS".equals(bindCardResult.getReturnCode()) && "SUCCESS".equals(bindCardResult.getResultCode())) {
                h5Response.setReturnMsg("绑卡成功，token: " + bindCardResult.getMemberToken());
                h5Response.setPayUrl("member_token:" + bindCardResult.getMemberToken());
            } else {
                String errorMsg = bindCardResult.getReturnMsg();
                if (errorMsg == null || errorMsg.trim().isEmpty()) {
                    errorMsg = bindCardResult.getMessage();
                }
                h5Response.setReturnMsg(errorMsg);
            }
            
            return h5Response;
            
        } catch (Exception e) {
            LOGGER.error("查询GlobePay绑卡结果失败: requestId={}", requestId, e);
            GlobePayH5Response errorResponse = new GlobePayH5Response();
            errorResponse.setReturnCode("FAIL");
            errorResponse.setReturnMsg("查询绑卡结果失败: " + e.getMessage());
            return errorResponse;
        }
    }
    
    @Override
    public GlobePayH5Response createTokenizedPayment(String orderId, String memberToken, GlobePayH5Request request) {
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
            
            // 使用member_token创建支付订单URL
            String paymentUrl = UriComponentsBuilder
                .fromHttpUrl(globePayConfig.getBaseUrl())
                .path("/api/v1.0/gateway/partners/{partner_code}/card_orders/{clientOrderId}/pay_anytime")
                .queryParam("time", time)
                .queryParam("nonce_str", nonceStr)
                .queryParam("sign", sign)
                .buildAndExpand(globePayConfig.getPartnerCode(), orderId)
                .toUriString();
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "application/json");
            
            // 构建支付请求体
            GlobePayCreditCardRequest paymentRequest = new GlobePayCreditCardRequest();
            paymentRequest.setMemberToken(memberToken);
            paymentRequest.setDescription(request.getDescription());
            paymentRequest.setPrice(request.getPrice());
            paymentRequest.setPreauth(false);
            paymentRequest.setExpire("30m");
            paymentRequest.setNotifyUrl(request.getNotifyUrl());
            paymentRequest.setOperator("mall-system");
            
            // 设置客户信息
            GlobePayCreditCardRequest.Customer customer = new GlobePayCreditCardRequest.Customer();
            customer.setName("Test Customer");
            paymentRequest.setCustomer(customer);
            
            String requestBody = objectMapper.writeValueAsString(paymentRequest);
            
            // API调用前日志
            LOGGER.info("================= GlobePay Token支付API调用开始 =================");
            LOGGER.info("API路径: POST {}", paymentUrl);
            LOGGER.info("请求参数: {}", requestBody);
            LOGGER.info("================================================================");
            
            long startTime = System.currentTimeMillis();
            
            // 发送POST请求创建支付
            ResponseEntity<String> response = restTemplate.exchange(
                paymentUrl, 
                HttpMethod.POST, 
                new HttpEntity<>(requestBody, headers), 
                String.class
            );
            
            long endTime = System.currentTimeMillis();
            
            // API调用后日志
            LOGGER.info("================= GlobePay Token支付API响应 =================");
            LOGGER.info("响应状态: {}", response.getStatusCode());
            LOGGER.info("响应内容: {}", response.getBody());
            LOGGER.info("耗时: {}ms", (endTime - startTime));
            LOGGER.info("==========================================================");
            
            // 解析支付响应
            GlobePayCreditCardResponse paymentResult = objectMapper.readValue(response.getBody(), GlobePayCreditCardResponse.class);
            
            // 转换为H5响应格式
            GlobePayH5Response h5Response = new GlobePayH5Response();
            h5Response.setReturnCode(paymentResult.getReturnCode());
            h5Response.setResultCode(paymentResult.getResultCode());
            h5Response.setPartnerCode(paymentResult.getPartnerCode());
            h5Response.setOrderId(paymentResult.getOrderId());
            h5Response.setPartnerOrderId(paymentResult.getPartnerOrderId());
            h5Response.setChannel("CREDIT_CARD");
            
            if ("SUCCESS".equals(paymentResult.getReturnCode())) {
                h5Response.setReturnMsg("支付订单创建成功");
            } else {
                String errorMsg = paymentResult.getReturnMsg();
                if (errorMsg == null || errorMsg.trim().isEmpty()) {
                    errorMsg = paymentResult.getMessage();
                }
                h5Response.setReturnMsg(errorMsg);
                
                LOGGER.error("GlobePay token支付创建失败: returnCode={}, returnMsg={}, message={}", 
                    paymentResult.getReturnCode(), paymentResult.getReturnMsg(), paymentResult.getMessage());
            }
            
            return h5Response;
            
        } catch (Exception e) {
            LOGGER.error("创建GlobePay token支付失败: orderId={}, memberToken={}", orderId, memberToken, e);
            GlobePayH5Response errorResponse = new GlobePayH5Response();
            errorResponse.setReturnCode("FAIL");
            errorResponse.setReturnMsg("创建token支付失败: " + e.getMessage());
            return errorResponse;
        }
    }
    
    @Override
    public String generateCreditCardPaymentUrl(String orderId, String redirectUrl) {
        // 生成签名参数
        long time = GlobePaySignUtils.getCurrentUtcTime();
        String nonceStr = GlobePaySignUtils.generateNonceStr();
        String sign = GlobePaySignUtils.generateSign(
            globePayConfig.getPartnerCode(), 
            time, 
            nonceStr, 
            globePayConfig.getCredentialCode()
        );
        
        // 构建信用卡支付跳转URL
        return UriComponentsBuilder
            .fromHttpUrl(globePayConfig.getBaseUrl())
            .path("/api/v1.0/channels/card/partners/{partner_code}/gateway_orders/{order_id}/view")
            .queryParam("time", time)
            .queryParam("nonce_str", nonceStr)
            .queryParam("sign", sign)
            .queryParam("redirect", redirectUrl)
            .buildAndExpand(globePayConfig.getPartnerCode(), orderId)
            .toUriString();
    }

    @Override
    public boolean verifyCallbackSign(String partnerCode, long time, String nonceStr, String sign) {
        try {
            // 检查签名是否超时
            if (GlobePaySignUtils.isSignTimeout(time, globePayConfig.getSignValidMinutes())) {
                LOGGER.warn("GlobePay回调签名超时: time={}", time);
                return false;
            }
            
            // 验证签名
            boolean valid = GlobePaySignUtils.verifySign(
                partnerCode, 
                time, 
                nonceStr, 
                globePayConfig.getCredentialCode(), 
                sign
            );
            
            if (!valid) {
                LOGGER.warn("GlobePay回调签名验证失败: partnerCode={}, time={}, nonceStr={}, sign={}", 
                    partnerCode, time, nonceStr, sign);
            }
            
            return valid;
        } catch (Exception e) {
            LOGGER.error("验证GlobePay回调签名异常", e);
            return false;
        }
    }

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
            errorResponse.setSuccess(false);
            errorResponse.setCode("ERROR");
            errorResponse.setMessage("退款查询异常: " + e.getMessage());
            return errorResponse;
        }
    }
}
