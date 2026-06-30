package com.macro.mall.service;

import com.macro.mall.dto.CustomerServiceDto;
import com.macro.mall.dto.UploadResultDto;
import org.springframework.web.multipart.MultipartFile;

/**
 * 客服服务
 * Created by mall on 2025/06/22.
 */
public interface CustomerService {
    /**
     * 获取客服微信信息
     */
    CustomerServiceDto getCustomerServiceInfo();

    /**
     * 保存客服微信设置
     */
    void saveCustomerServiceWechat(CustomerServiceDto customerServiceDto);

    /**
     * 上传微信二维码
     */
    UploadResultDto uploadQRCode(MultipartFile file);
}
