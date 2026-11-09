package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.CustomerServiceDto;
import com.macro.mall.dto.UploadResultDto;
import com.macro.mall.service.CustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 客服管理控制器
 * Created by mall on 2025/06/22.
 */
@Controller
@Api(tags = "CustomerServiceController")
@Tag(name = "CustomerServiceController", description = "客服管理")
@RequestMapping("/admin/customerService")
public class CustomerServiceController {

    @Autowired
    private CustomerService customerService;

    @ApiOperation("获取客服微信信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CustomerServiceDto> getCustomerServiceInfo() {
        CustomerServiceDto result = customerService.getCustomerServiceInfo();
        return CommonResult.success(result);
    }

    @ApiOperation("保存客服微信设置")
    @RequestMapping(value = "/wechat", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<String> saveCustomerServiceWechat(@RequestBody CustomerServiceDto customerServiceDto) {
        customerService.saveCustomerServiceWechat(customerServiceDto);
        return CommonResult.success("保存成功");
    }

    @ApiOperation("上传微信二维码")
    @RequestMapping(value = "/uploadQRCode", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<UploadResultDto> uploadQRCode(@RequestParam("file") MultipartFile file) {
        UploadResultDto result = customerService.uploadQRCode(file);
        return CommonResult.success(result, "上传成功");
    }
}
