package com.macro.mall.service.impl;

import com.macro.mall.dao.DashboardDao;
import com.macro.mall.dto.DashboardDto;
import com.macro.mall.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 仪表盘服务实现类
 * Created by mall on 2025/06/22.
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private DashboardDao dashboardDao;

    @Override
    public DashboardDto getDashboardData() {
        DashboardDto result = new DashboardDto();

        // 基础数据
        result.setTodayOrderCount(dashboardDao.getTodayOrderCount());
        result.setTodayOrderAmount(dashboardDao.getTodayOrderAmount());
        result.setTotalUserCount(dashboardDao.getTotalUserCount());
        result.setTotalProductCount(dashboardDao.getTotalProductCount());
        result.setMonthOrderCount(dashboardDao.getMonthOrderCount());
        result.setMonthOrderAmount(dashboardDao.getMonthOrderAmount());

        // 订单状态统计
        result.setPendingPaymentCount(dashboardDao.getPendingPaymentCount());
        result.setCompletedOrderCount(dashboardDao.getCompletedOrderCount());
        result.setPendingReceiveCount(dashboardDao.getPendingReceiveCount());
        result.setPendingShipmentCount(dashboardDao.getPendingShipmentCount());
        result.setStockOutCount(dashboardDao.getStockOutCount());
        result.setPendingRefundCount(dashboardDao.getPendingRefundCount());
        result.setShippedOrderCount(dashboardDao.getShippedOrderCount());
        result.setPendingReturnCount(dashboardDao.getPendingReturnCount());

        // 商品统计
        result.setProductOfflineCount(dashboardDao.getProductOfflineCount());
        result.setProductOnlineCount(dashboardDao.getProductOnlineCount());
        result.setProductLowStockCount(dashboardDao.getProductLowStockCount());

        // 用户统计
        result.setTodayNewUserCount(dashboardDao.getTodayNewUserCount());
        result.setYesterdayNewUserCount(dashboardDao.getYesterdayNewUserCount());
        result.setMonthNewUserCount(dashboardDao.getMonthNewUserCount());

        // 本周数据
        result.setWeekOrderCount(dashboardDao.getWeekOrderCount());
        result.setWeekOrderAmount(dashboardDao.getWeekOrderAmount());

        // 计算增长率
        calculateGrowthRates(result);

        return result;
    }

    private void calculateGrowthRates(DashboardDto result) {
        // 本月订单增长率
        Integer lastMonthOrderCount = dashboardDao.getLastMonthOrderCount();
        if (lastMonthOrderCount != null && lastMonthOrderCount > 0) {
            double growth = ((double) (result.getMonthOrderCount() - lastMonthOrderCount) / lastMonthOrderCount) * 100;
            result.setMonthOrderGrowth(Math.round(growth * 10.0) / 10.0);
        } else {
            result.setMonthOrderGrowth(0.0);
        }

        // 本周订单增长率
        Integer lastWeekOrderCount = dashboardDao.getLastWeekOrderCount();
        if (lastWeekOrderCount != null && lastWeekOrderCount > 0) {
            double growth = ((double) (result.getWeekOrderCount() - lastWeekOrderCount) / lastWeekOrderCount) * 100;
            result.setWeekOrderGrowth(Math.round(growth * 10.0) / 10.0);
        } else {
            result.setWeekOrderGrowth(0.0);
        }

        // 本月金额增长率
        Long lastMonthOrderAmount = dashboardDao.getLastMonthOrderAmount();
        if (lastMonthOrderAmount != null && lastMonthOrderAmount > 0) {
            double growth = ((double) (result.getMonthOrderAmount() - lastMonthOrderAmount) / lastMonthOrderAmount) * 100;
            result.setMonthAmountGrowth(Math.round(growth * 10.0) / 10.0);
        } else {
            result.setMonthAmountGrowth(0.0);
        }

        // 本周金额增长率
        Long lastWeekOrderAmount = dashboardDao.getLastWeekOrderAmount();
        if (lastWeekOrderAmount != null && lastWeekOrderAmount > 0) {
            double growth = ((double) (result.getWeekOrderAmount() - lastWeekOrderAmount) / lastWeekOrderAmount) * 100;
            result.setWeekAmountGrowth(Math.round(growth * 10.0) / 10.0);
        } else {
            result.setWeekAmountGrowth(0.0);
        }
    }
}
