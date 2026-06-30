package com.macro.mall.portal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.macro.mall.common.service.RedisService;
import com.macro.mall.mapper.SysSettingMapper;
import com.macro.mall.model.SysSetting;
import com.macro.mall.model.SysSettingExample;
import com.macro.mall.portal.domain.WechatImageResult;
import com.macro.mall.portal.service.WechatImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 微信图片服务实现类
 * Created by mall on 2025/07/06.
 */
@Slf4j
@Service
public class WechatImageServiceImpl implements WechatImageService {
    
    @Value("${mall.upload.path:e:/backend/mall-master/uploads/}")
    private String uploadPath;
    
    @Value("${mall.upload.domain:http://localhost:8085}")
    private String domain;
    
    @Autowired
    private RedisService redisService;
    
    @Autowired
    private SysSettingMapper sysSettingMapper;
    
    // Redis key for caching wechat service image
    private static final String WECHAT_SERVICE_IMAGE_KEY = "mall:wechat:service:image";
    private static final String CUSTOMER_QRCODE_URL_KEY = "customer_qrcode_url";
    
    @Override
    public WechatImageResult getWechatServiceInfo() {
        try {
            // 首先尝试从Redis缓存中获取
            Object cachedImageUrl = redisService.get(WECHAT_SERVICE_IMAGE_KEY);
            if (cachedImageUrl != null) {
                WechatImageResult result = new WechatImageResult();
                result.setImageUrl(cachedImageUrl.toString());
                result.setImageType("service");
                result.setTitle("客服微信二维码");
                result.setDescription("扫码添加客服微信");
                result.setAvailable(checkImageExists(cachedImageUrl.toString()));
                return result;
            }
            
            // 从数据库获取客服二维码URL
            SysSetting qrCodeSetting = getSettingByKey(CUSTOMER_QRCODE_URL_KEY);
            if (qrCodeSetting != null && StrUtil.isNotBlank(qrCodeSetting.getSettingValue())) {
                WechatImageResult result = new WechatImageResult();
                result.setImageUrl(qrCodeSetting.getSettingValue());
                result.setImageType("service");
                result.setTitle("客服微信二维码");
                result.setDescription("扫码添加客服微信");
                result.setAvailable(checkImageExists(qrCodeSetting.getSettingValue()));
                
                // 缓存结果
                redisService.set(WECHAT_SERVICE_IMAGE_KEY, result.getImageUrl(), 3600L);
                return result;
            }
            
            // 如果数据库中没有，查找最新的客服微信图片（兼容旧方式）
            WechatImageResult latestImage = getLatestWechatImage();
            if (latestImage != null) {
                latestImage.setImageType("service");
                latestImage.setTitle("客服微信二维码");
                latestImage.setDescription("扫码添加客服微信");
                // 缓存结果
                redisService.set(WECHAT_SERVICE_IMAGE_KEY, latestImage.getImageUrl(), 3600L);
                return latestImage;
            }
            
            return null;
        } catch (Exception e) {
            log.error("获取客服微信信息失败", e);
            return null;
        }
    }
    
    @Override
    public List<WechatImageResult> getWechatImageList(Integer pageNum, Integer pageSize) {
        try {
            List<WechatImageResult> allImages = scanWechatImages();
            
            // 分页处理
            int startIndex = (pageNum - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, allImages.size());
            
            if (startIndex >= allImages.size()) {
                return new ArrayList<>();
            }
            
            return allImages.subList(startIndex, endIndex);
        } catch (Exception e) {
            log.error("获取微信图片列表失败", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<WechatImageResult> getWechatImagesByType(String imageType, Integer pageNum, Integer pageSize) {
        List<WechatImageResult> allImages = getWechatImageList(pageNum, pageSize);
        
        // 根据类型过滤
        if (StrUtil.isNotBlank(imageType)) {
            return allImages.stream()
                    .filter(image -> imageType.equals(image.getImageType()))
                    .collect(Collectors.toList());
        }
        
        return allImages;
    }
    
    @Override
    public WechatImageResult getLatestWechatImage() {
        try {
            List<WechatImageResult> allImages = scanWechatImages();
            if (!allImages.isEmpty()) {
                // 返回最新上传的图片（按文件名排序，最新的在前面）
                return allImages.get(0);
            }
            return null;
        } catch (Exception e) {
            log.error("获取最新微信图片失败", e);
            return null;
        }
    }
    
    @Override
    public Resource loadImageAsResource(String imagePath) throws Exception {
        try {
            // 确保路径安全，防止目录遍历攻击
            if (imagePath.contains("..") || imagePath.contains("\\")) {
                throw new SecurityException("非法的文件路径");
            }
            
            Path filePath = Paths.get(uploadPath).resolve(imagePath).normalize();
            Resource resource = new FileSystemResource(filePath);
            
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("图片文件不存在或不可读: " + imagePath);
            }
        } catch (Exception e) {
            log.error("加载图片资源失败: {}", imagePath, e);
            throw e;
        }
    }
    
    @Override
    public boolean checkImageExists(String imageUrl) {
        try {
            if (StrUtil.isBlank(imageUrl)) {
                return false;
            }
            
            // 如果是MinIO URL，直接返回true（假设MinIO中的文件都存在）
            if (imageUrl.contains("localhost:9000") || imageUrl.contains("minio")) {
                return true;
            }
            
            // 从URL中提取相对路径（兼容旧的本地文件）
            String relativePath = extractRelativePathFromUrl(imageUrl);
            if (StrUtil.isBlank(relativePath)) {
                return false;
            }
            
            Path filePath = Paths.get(uploadPath).resolve(relativePath);
            return Files.exists(filePath) && Files.isReadable(filePath);
        } catch (Exception e) {
            log.error("检查图片是否存在失败: {}", imageUrl, e);
            return false;
        }
    }
    
    /**
     * 扫描微信图片目录
     */
    private List<WechatImageResult> scanWechatImages() {
        List<WechatImageResult> imageList = new ArrayList<>();
        
        try {
            log.info("开始扫描微信图片目录，uploadPath: {}", uploadPath);
            File qrcodeDir = new File(uploadPath, "qrcode");
            log.info("qrcode目录路径: {}", qrcodeDir.getAbsolutePath());
            log.info("qrcode目录是否存在: {}", qrcodeDir.exists());
            log.info("qrcode目录是否为目录: {}", qrcodeDir.isDirectory());
            
            if (!qrcodeDir.exists() || !qrcodeDir.isDirectory()) {
                log.warn("qrcode目录不存在或不是目录");
                return imageList;
            }
            
            // 递归扫描目录
            scanDirectory(qrcodeDir, imageList, "qrcode/");
            
            log.info("扫描完成，找到图片数量: {}", imageList.size());
            
            // 按文件名倒序排序（最新的在前面）
            imageList.sort((a, b) -> b.getFileName().compareTo(a.getFileName()));
            
        } catch (Exception e) {
            log.error("扫描微信图片目录失败", e);
        }
        
        return imageList;
    }
    
    /**
     * 递归扫描目录
     */
    private void scanDirectory(File directory, List<WechatImageResult> imageList, String relativePath) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        
        for (File file : files) {
            if (file.isDirectory()) {
                // 递归扫描子目录
                scanDirectory(file, imageList, relativePath + file.getName() + "/");
            } else if (isImageFile(file)) {
                // 处理图片文件
                WechatImageResult imageResult = createImageResult(file, relativePath);
                if (imageResult != null) {
                    imageList.add(imageResult);
                }
            }
        }
    }
    
    /**
     * 检查是否为图片文件
     */
    private boolean isImageFile(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || 
               fileName.endsWith(".png") || fileName.endsWith(".gif") || 
               fileName.endsWith(".bmp") || fileName.endsWith(".webp");
    }
    
    /**
     * 创建图片结果对象
     */
    private WechatImageResult createImageResult(File file, String relativePath) {
        try {
            WechatImageResult result = new WechatImageResult();
            
            String fileName = file.getName();
            String fullRelativePath = relativePath + fileName;
            
            result.setFileName(fileName);
            result.setImageUrl(domain + "/uploads/" + fullRelativePath);
            result.setFileSize(file.length());
            result.setUploadTime(new Date(file.lastModified()));
            result.setAvailable(true);
            
            // 根据文件名判断图片类型
            if (fileName.contains("wechat_qr") || fileName.contains("service")) {
                result.setImageType("service");
                result.setTitle("客服微信二维码");
                result.setDescription("扫码添加客服微信");
            } else {
                result.setImageType("other");
                result.setTitle("微信图片");
                result.setDescription("微信相关图片");
            }
            
            // 生成缩略图URL（可以后续实现缩略图生成功能）
            result.setThumbnailUrl(result.getImageUrl());
            
            return result;
        } catch (Exception e) {
            log.error("创建图片结果对象失败: {}", file.getAbsolutePath(), e);
            return null;
        }
    }
    
    /**
     * 从URL中提取相对路径
     */
    private String extractRelativePathFromUrl(String imageUrl) {
        try {
            if (StrUtil.isBlank(imageUrl)) {
                return null;
            }
            
            // 查找 /uploads/ 的位置
            int uploadsIndex = imageUrl.indexOf("/uploads/");
            if (uploadsIndex == -1) {
                return null;
            }
            
            // 提取 uploads/ 之后的部分
            return imageUrl.substring(uploadsIndex + 9); // 9 = "/uploads/".length()
        } catch (Exception e) {
            log.error("提取相对路径失败: {}", imageUrl, e);
            return null;
        }
    }
    
    /**
     * 根据key获取系统设置
     */
    private SysSetting getSettingByKey(String key) {
        try {
            SysSettingExample example = new SysSettingExample();
            example.createCriteria().andSettingKeyEqualTo(key);
            List<SysSetting> settings = sysSettingMapper.selectByExample(example);
            return settings.isEmpty() ? null : settings.get(0);
        } catch (Exception e) {
            log.error("获取系统设置失败: {}", key, e);
            return null;
        }
    }
}
