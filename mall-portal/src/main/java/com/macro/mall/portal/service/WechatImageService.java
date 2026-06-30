package com.macro.mall.portal.service;

import com.macro.mall.portal.domain.WechatImageResult;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * 微信图片服务接口
 * Created by mall on 2025/07/06.
 */
public interface WechatImageService {
    
    /**
     * 获取客服微信二维码信息
     */
    WechatImageResult getWechatServiceInfo();
    
    /**
     * 获取微信图片列表
     */
    List<WechatImageResult> getWechatImageList(Integer pageNum, Integer pageSize);
    
    /**
     * 根据类型获取微信图片
     */
    List<WechatImageResult> getWechatImagesByType(String imageType, Integer pageNum, Integer pageSize);
    
    /**
     * 获取最新上传的微信图片
     */
    WechatImageResult getLatestWechatImage();
    
    /**
     * 加载图片资源
     */
    Resource loadImageAsResource(String imagePath) throws Exception;
    
    /**
     * 检查图片是否存在
     */
    boolean checkImageExists(String imageUrl);
}
