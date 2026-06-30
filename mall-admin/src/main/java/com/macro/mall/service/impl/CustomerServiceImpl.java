package com.macro.mall.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.macro.mall.dto.CustomerServiceDto;
import com.macro.mall.dto.UploadResultDto;
import com.macro.mall.mapper.SysSettingMapper;
import com.macro.mall.model.SysSetting;
import com.macro.mall.model.SysSettingExample;
import com.macro.mall.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 客服服务实现类
 * Created by mall on 2025/06/22.
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    private SysSettingMapper sysSettingMapper;

    @Value("${mall.upload.path:/uploads/}")
    private String uploadPath;

    @Value("${mall.domain:http://localhost:8080}")
    private String domain;

    @Value("${mall.upload.url:/uploads}")
    private String uploadUrl;

    private static final String CUSTOMER_WECHAT_ID_KEY = "customer_wechat_id";
    private static final String CUSTOMER_SERVICE_NAME_KEY = "customer_service_name";
    private static final String CUSTOMER_QRCODE_URL_KEY = "customer_qrcode_url";

    @Override
    public CustomerServiceDto getCustomerServiceInfo() {
        CustomerServiceDto result = new CustomerServiceDto();
        
        // 获取微信号
        SysSetting wechatIdSetting = getSettingByKey(CUSTOMER_WECHAT_ID_KEY);
        if (wechatIdSetting != null) {
            result.setWechatId(wechatIdSetting.getSettingValue());
        }
        
        // 获取客服姓名
        SysSetting nameSetting = getSettingByKey(CUSTOMER_SERVICE_NAME_KEY);
        if (nameSetting != null) {
            result.setCustomerServiceName(nameSetting.getSettingValue());
        }
        
        // 获取二维码URL
        SysSetting qrCodeSetting = getSettingByKey(CUSTOMER_QRCODE_URL_KEY);
        if (qrCodeSetting != null) {
            result.setQrCodeUrl(qrCodeSetting.getSettingValue());
        }
        
        return result;
    }

    @Override
    public void saveCustomerServiceWechat(CustomerServiceDto customerServiceDto) {
        // 保存微信号
        if (StrUtil.isNotEmpty(customerServiceDto.getWechatId())) {
            saveOrUpdateSetting(CUSTOMER_WECHAT_ID_KEY, customerServiceDto.getWechatId(), "客服微信号");
        }
        
        // 保存客服姓名
        if (StrUtil.isNotEmpty(customerServiceDto.getCustomerServiceName())) {
            saveOrUpdateSetting(CUSTOMER_SERVICE_NAME_KEY, customerServiceDto.getCustomerServiceName(), "客服姓名");
        }
        
        // 保存二维码URL
        if (StrUtil.isNotEmpty(customerServiceDto.getQrCodeUrl())) {
            saveOrUpdateSetting(CUSTOMER_QRCODE_URL_KEY, customerServiceDto.getQrCodeUrl(), "客服微信二维码URL");
        }
    }

    @Override
    public UploadResultDto uploadQRCode(MultipartFile file) {
        try {
            String filename = safeFilename(file.getOriginalFilename());
            String datePath = DateUtil.format(new Date(), "yyyy/MM/dd");
            String objectName = "qrcode/" + datePath + "/" + "wechat_qr_" + UUID.randomUUID() + "-" + filename;
            Path targetPath = Paths.get(uploadPath, objectName);
            Files.createDirectories(targetPath.getParent());
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            LOGGER.info("客服二维码上传成功!");

            UploadResultDto result = new UploadResultDto();
            result.setUrl(joinUrl(uploadUrl, objectName));
            return result;
        } catch (Exception e) {
            LOGGER.error("客服二维码上传失败：{}", e.getMessage());
            throw new RuntimeException("客服二维码上传失败", e);
        }
    }

    private SysSetting getSettingByKey(String key) {
        SysSettingExample example = new SysSettingExample();
        example.createCriteria().andSettingKeyEqualTo(key);
        List<SysSetting> settings = sysSettingMapper.selectByExample(example);
        return settings.isEmpty() ? null : settings.get(0);
    }

    private void saveOrUpdateSetting(String key, String value, String name) {
        SysSetting setting = getSettingByKey(key);
        Date now = new Date();
        
        if (setting == null) {
            // 新增
            setting = new SysSetting();
            setting.setSettingKey(key);
            setting.setSettingValue(value);
            setting.setSettingName(name);
            setting.setType(2); // 业务设置
            setting.setStatus(1); // 启用
            setting.setCreateTime(now);
            setting.setUpdateTime(now);
            sysSettingMapper.insertSelective(setting);
        } else {
            // 更新
            setting.setSettingValue(value);
            setting.setUpdateTime(now);
            sysSettingMapper.updateByPrimaryKeySelective(setting);
        }
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
