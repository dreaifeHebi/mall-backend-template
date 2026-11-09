package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.*;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.service.OmsOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * 订单管理Controller
 * Created by macro on 2018/10/11.
 */
@Controller
@Api(tags = "OmsOrderController")
@Tag(name = "OmsOrderController", description = "订单管理")
@RequestMapping("/order")
public class OmsOrderController {
    @Autowired
    private OmsOrderService orderService;

    @ApiOperation("查询订单")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<OmsOrder>> list(OmsOrderQueryParam queryParam,
                                                   @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                   @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                   @RequestParam(value = "orderBy", defaultValue = "create_time", required = false) String orderBy,
                                                   @RequestParam(value = "orderDirection", defaultValue = "desc", required = false) String orderDirection,
                                                   @RequestParam(value = "startDate", required = false) String startDate,
                                                   @RequestParam(value = "endDate", required = false) String endDate) {
        // 设置默认排序参数
        if (queryParam.getOrderBy() == null || queryParam.getOrderBy().isEmpty()) {
            queryParam.setOrderBy(orderBy);
        }
        if (queryParam.getOrderDirection() == null || queryParam.getOrderDirection().isEmpty()) {
            queryParam.setOrderDirection(orderDirection);
        }
        
        // 设置时间范围参数
        if (startDate != null && !startDate.isEmpty()) {
            queryParam.setStartDate(startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            queryParam.setEndDate(endDate);
        }
        
        List<OmsOrder> orderList = orderService.list(queryParam, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(orderList));
    }

    @ApiOperation("批量发货")
    @RequestMapping(value = "/update/delivery", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delivery(@RequestBody List<OmsOrderDeliveryParam> deliveryParamList) {
        int count = orderService.delivery(deliveryParamList);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("批量关闭订单")
    @RequestMapping(value = "/update/close", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult close(@RequestParam("ids") List<Long> ids, @RequestParam String note) {
        int count = orderService.close(ids, note);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("批量删除订单")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@RequestParam("ids") List<Long> ids) {
        int count = orderService.delete(ids);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("获取订单详情：订单信息、商品信息、操作记录")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<OmsOrderDetail> detail(@PathVariable Long id) {
        OmsOrderDetail orderDetailResult = orderService.detail(id);
        return CommonResult.success(orderDetailResult);
    }

    @ApiOperation("修改收货人信息")
    @RequestMapping(value = "/update/receiverInfo", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateReceiverInfo(@RequestBody OmsReceiverInfoParam receiverInfoParam) {
        int count = orderService.updateReceiverInfo(receiverInfoParam);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改订单费用信息")
    @RequestMapping(value = "/update/moneyInfo", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateReceiverInfo(@RequestBody OmsMoneyInfoParam moneyInfoParam) {
        int count = orderService.updateMoneyInfo(moneyInfoParam);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("备注订单")
    @RequestMapping(value = "/update/note", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateNote(@RequestParam("id") Long id,
                                   @RequestParam("note") String note,
                                   @RequestParam("status") Integer status) {
        int count = orderService.updateNote(id, note, status);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("批量导出订单CSV")
    @RequestMapping(value = "/export", method = RequestMethod.POST)
    @ResponseBody
    public void exportOrdersCsv(OmsOrderQueryParam queryParam, HttpServletResponse response) {
        orderService.exportOrdersCsv(queryParam, response);
    }

    @ApiOperation("单个订单发货")
    @RequestMapping(value = "/delivery/{orderId}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult deliverySingle(@PathVariable Long orderId, @RequestBody OmsOrderDeliveryParam deliveryParam) {
        int count = orderService.deliverOrder(orderId, 
                                            deliveryParam.getDeliveryCompany(), 
                                            deliveryParam.getDeliverySn());
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("获取订单详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<OmsOrderDetail> getOrderDetail(@PathVariable Long id) {
        OmsOrderDetail orderDetail = orderService.getOrderDetail(id);
        return CommonResult.success(orderDetail);
    }

    @ApiOperation("确认订单付款")
    @PostMapping("/confirm/pay")
    @ResponseBody
    public CommonResult<Integer> confirmPayment(@Valid @RequestBody OmsOrderPaymentConfirmParam confirmParam) {
        int count = orderService.confirmPayment(confirmParam);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
}
