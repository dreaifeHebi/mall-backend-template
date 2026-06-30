package com.macro.mall.portal.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * GlobePay签名工具类
 * @author dreaifekks
 * @date 2025/7/26
 */
public class GlobePaySignUtils {

    /**
     * 生成签名
     * @param partnerCode 商户编码
     * @param time UTC毫秒时间戳
     * @param nonceStr 随机字符串
     * @param credentialCode 开发校验码
     * @return 签名字符串
     */
    public static String generateSign(String partnerCode, long time, String nonceStr, String credentialCode) {
        // 按照 partner_code&time&nonce_str&credential_code 的顺序连接
        String validString = partnerCode + "&" + time + "&" + nonceStr + "&" + credentialCode;

        try {
            // 使用SHA256进行签名
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(validString.getBytes(StandardCharsets.UTF_8));

            // 转为Hex小写字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString().toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256算法不支持", e);
        }
    }

    /**
     * 生成随机字符串
     * @return 随机字符串（去掉横线的UUID）
     */
    public static String generateNonceStr() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取当前UTC毫秒时间戳
     * @return UTC毫秒时间戳
     */
    public static long getCurrentUtcTime() {
        return System.currentTimeMillis();
    }

    /**
     * 验证签名
     * @param partnerCode 商户编码
     * @param time UTC毫秒时间戳
     * @param nonceStr 随机字符串
     * @param credentialCode 开发校验码
     * @param sign 签名
     * @return 是否验证通过
     */
    public static boolean verifySign(String partnerCode, long time, String nonceStr, String credentialCode, String sign) {
        String expectedSign = generateSign(partnerCode, time, nonceStr, credentialCode);
        return expectedSign.equals(sign);
    }

    /**
     * 检查签名是否超时
     * @param time 签名时间戳
     * @param validMinutes 有效分钟数
     * @return 是否超时
     */
    public static boolean isSignTimeout(long time, int validMinutes) {
        long currentTime = getCurrentUtcTime();
        long diffMinutes = (currentTime - time) / (1000 * 60);
        return Math.abs(diffMinutes) > validMinutes;
    }
}
