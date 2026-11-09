package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.MinioUploadDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 文件上传管理Controller。
 *
 * 模板默认使用本地目录存储，生产环境可在 extensions 中替换为 S3/R2/MinIO 实现。
 * Created by macro on 2019/12/25.
 */
@Controller
@Api(tags = "MinioController")
@Tag(name = "MinioController", description = "本地文件上传管理")
@RequestMapping("/minio")
public class MinioController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MinioController.class);

    @Value("${mall.upload.path:/tmp/mall/uploads}")
    private String uploadPath;

    @Value("${mall.upload.url:/uploads}")
    private String uploadUrl;

    @ApiOperation("文件上传")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult upload(@RequestPart("file") MultipartFile file) {
        try {
            String filename = safeFilename(file.getOriginalFilename());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String objectName = sdf.format(new Date()) + "/" + UUID.randomUUID() + "-" + filename;
            Path targetPath = Paths.get(uploadPath, objectName);
            Files.createDirectories(targetPath.getParent());
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            MinioUploadDto minioUploadDto = new MinioUploadDto();
            minioUploadDto.setName(filename);
            minioUploadDto.setUrl(joinUrl(uploadUrl, objectName));
            return CommonResult.success(minioUploadDto);
        } catch (Exception e) {
            LOGGER.error("上传发生错误: {}！", e.getMessage(), e);
        }
        return CommonResult.failed();
    }

    @ApiOperation("文件删除")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@RequestParam("objectName") String objectName) {
        try {
            String normalized = objectName.replace(uploadUrl, "").replaceFirst("^/+", "");
            Path targetPath = Paths.get(uploadPath, normalized).normalize();
            Path basePath = Paths.get(uploadPath).normalize();
            if (!targetPath.startsWith(basePath)) {
                return CommonResult.failed("非法文件路径");
            }
            Files.deleteIfExists(targetPath);
            return CommonResult.success(null);
        } catch (Exception e) {
            LOGGER.error("删除文件失败: {}", e.getMessage(), e);
        }
        return CommonResult.failed();
    }

    private String safeFilename(String filename) {
        String fallback = "upload";
        String value = filename == null || filename.trim().isEmpty() ? fallback : new File(filename).getName();
        return value.replaceAll("[^A-Za-z0-9._-]", "_");
    }

    private String joinUrl(String baseUrl, String objectName) {
        String base = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        return base + "/" + objectName;
    }
}
