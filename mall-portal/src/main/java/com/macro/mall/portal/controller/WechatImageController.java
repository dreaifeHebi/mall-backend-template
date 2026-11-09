package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.domain.WechatImageResult;
import com.macro.mall.portal.service.WechatImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * 微信图片展示Controller
 * Created by mall on 2025/07/06.
 */
@Controller
@Api(tags = "WechatImageController")
@Tag(name = "WechatImageController", description = "微信图片展示管理")
@RequestMapping("/wechat/image")
public class WechatImageController {
    
    @Autowired
    private WechatImageService wechatImageService;

    @ApiOperation("获取客服微信二维码信息")
    @RequestMapping(value = "/service/info", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<WechatImageResult> getWechatServiceInfo() {
        WechatImageResult result = wechatImageService.getWechatServiceInfo();
        return CommonResult.success(result);
    }

    @ApiOperation("获取微信图片列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<WechatImageResult>> getWechatImageList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        List<WechatImageResult> imageList = wechatImageService.getWechatImageList(pageNum, pageSize);
        return CommonResult.success(imageList);
    }

    @ApiOperation("根据类型获取微信图片")
    @RequestMapping(value = "/type/{imageType}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<WechatImageResult>> getWechatImagesByType(
            @PathVariable String imageType,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        List<WechatImageResult> imageList = wechatImageService.getWechatImagesByType(imageType, pageNum, pageSize);
        return CommonResult.success(imageList);
    }

    @ApiOperation("获取最新上传的微信图片")
    @RequestMapping(value = "/latest", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<WechatImageResult> getLatestWechatImage() {
        WechatImageResult result = wechatImageService.getLatestWechatImage();
        return CommonResult.success(result);
    }

    @ApiOperation("展示微信图片（直接返回图片文件）")
    @RequestMapping(value = "/display/**", method = RequestMethod.GET)
    public ResponseEntity<Resource> displayImage(HttpServletRequest request) {
        // 从请求路径中提取图片路径
        String imagePath = request.getRequestURI().substring(request.getRequestURI().indexOf("/display/") + 9);
        
        try {
            Resource resource = wechatImageService.loadImageAsResource(imagePath);
            
            // 确定内容类型
            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException ex) {
                // 忽略
            }

            // 默认为octet-stream
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @ApiOperation("获取客服微信图片（前台页面专用）")
    @RequestMapping(value = "/service/qrcode", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<String> getServiceQRCode() {
        try {
            WechatImageResult result = wechatImageService.getWechatServiceInfo();
            if (result != null && result.getImageUrl() != null) {
                return CommonResult.success(result.getImageUrl());
            }
            // 如果没有获取到，尝试获取最新的图片
            WechatImageResult latestImage = wechatImageService.getLatestWechatImage();
            if (latestImage != null && latestImage.getImageUrl() != null) {
                return CommonResult.success(latestImage.getImageUrl());
            }
            return CommonResult.failed("暂无客服微信图片");
        } catch (Exception e) {
            return CommonResult.failed("获取客服微信图片失败：" + e.getMessage());
        }
    }

    @ApiOperation("调试接口：检查上传路径和文件")
    @RequestMapping(value = "/debug/config", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<String> debugConfig() {
        try {
            // 获取所有图片进行调试
            List<WechatImageResult> allImages = wechatImageService.getWechatImageList(1, 100);
            StringBuilder debugInfo = new StringBuilder();
            debugInfo.append("找到图片数量: ").append(allImages.size()).append("\n");
            
            for (WechatImageResult image : allImages) {
                debugInfo.append("图片URL: ").append(image.getImageUrl()).append("\n");
                debugInfo.append("文件名: ").append(image.getFileName()).append("\n");
                debugInfo.append("是否可用: ").append(image.getAvailable()).append("\n");
                debugInfo.append("---\n");
            }
            
            return CommonResult.success(debugInfo.toString());
        } catch (Exception e) {
            return CommonResult.failed("调试失败：" + e.getMessage());
        }
    }

    @ApiOperation("检查图片是否存在")
    @RequestMapping(value = "/exists", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Boolean> checkImageExists(@RequestParam String imageUrl) {
        boolean exists = wechatImageService.checkImageExists(imageUrl);
        return CommonResult.success(exists);
    }
}
