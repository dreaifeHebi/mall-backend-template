package com.macro.mall.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.macro.mall.bo.AdminUserDetails;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.common.util.RequestUtil;
import com.macro.mall.dao.UmsAdminRoleRelationDao;
import com.macro.mall.dto.AdminDashboardResult;
import com.macro.mall.dto.UmsAdminParam;
import com.macro.mall.dto.UpdateAdminPasswordParam;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.security.util.JwtTokenUtil;
import com.macro.mall.security.util.SpringUtil;
import com.macro.mall.service.UmsAdminCacheService;
import com.macro.mall.service.UmsAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 后台用户管理Service实现类
 * Created by macro on 2018/4/26.
 */
@Service
public class UmsAdminServiceImpl implements UmsAdminService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UmsAdminServiceImpl.class);
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UmsAdminMapper adminMapper;
    @Autowired
    private UmsAdminRoleRelationMapper adminRoleRelationMapper;
    @Autowired
    private UmsAdminRoleRelationDao adminRoleRelationDao;
    @Autowired
    private UmsAdminLoginLogMapper loginLogMapper;
    @Autowired
    private OmsOrderMapper orderMapper;
    @Autowired
    private UmsMemberMapper memberMapper;
    @Autowired
    private PmsProductMapper productMapper;

    @Override
    public UmsAdmin getAdminByUsername(String username) {
        //先从缓存中获取数据
        UmsAdmin admin = getCacheService().getAdmin(username);
        if (admin != null) return admin;
        //缓存中没有再从数据库中获取
        UmsAdminExample example = new UmsAdminExample();
        example.createCriteria().andUsernameEqualTo(username);
        List<UmsAdmin> adminList = adminMapper.selectByExample(example);
        if (adminList != null && adminList.size() > 0) {
            admin = adminList.get(0);
            //将数据库中的数据存入缓存中
            getCacheService().setAdmin(admin);
            return admin;
        }
        return null;
    }

    @Override
    public UmsAdmin register(UmsAdminParam umsAdminParam) {
        UmsAdmin umsAdmin = new UmsAdmin();
        BeanUtils.copyProperties(umsAdminParam, umsAdmin);
        umsAdmin.setCreateTime(new Date());
        umsAdmin.setStatus(1);
        //查询是否有相同用户名的用户
        UmsAdminExample example = new UmsAdminExample();
        example.createCriteria().andUsernameEqualTo(umsAdmin.getUsername());
        List<UmsAdmin> umsAdminList = adminMapper.selectByExample(example);
        if (umsAdminList.size() > 0) {
            return null;
        }
        //将密码进行加密操作
        String encodePassword = passwordEncoder.encode(umsAdmin.getPassword());
        umsAdmin.setPassword(encodePassword);
        adminMapper.insert(umsAdmin);
        return umsAdmin;
    }

    @Override
    public String login(String username, String password) {
        String token = null;
        //密码需要客户端加密后传递
        try {
            UserDetails userDetails = loadUserByUsername(username);
            if(!passwordEncoder.matches(password,userDetails.getPassword())){
                Asserts.fail("密码不正确");
            }
            if(!userDetails.isEnabled()){
                Asserts.fail("帐号已被禁用");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenUtil.generateToken(userDetails);
//            updateLoginTimeByUsername(username);
            insertLoginLog(username);
        } catch (AuthenticationException e) {
            LOGGER.warn("登录异常:{}", e.getMessage());
        }
        return token;
    }

    /**
     * 添加登录记录
     * @param username 用户名
     */
    private void insertLoginLog(String username) {
        UmsAdmin admin = getAdminByUsername(username);
        if(admin==null) return;
        UmsAdminLoginLog loginLog = new UmsAdminLoginLog();
        loginLog.setAdminId(admin.getId());
        loginLog.setCreateTime(new Date());
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        loginLog.setIp(RequestUtil.getRequestIp(request));
        loginLogMapper.insert(loginLog);
    }

    /**
     * 根据用户名修改登录时间
     */
    @Override
    public String refreshToken(String oldToken) {
        return jwtTokenUtil.refreshHeadToken(oldToken);
    }

    @Override
    public UmsAdmin getItem(Long id) {
        return adminMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<UmsAdmin> list(String keyword, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        UmsAdminExample example = new UmsAdminExample();
        UmsAdminExample.Criteria criteria = example.createCriteria();
        if (!StrUtil.isEmpty(keyword)) {
            criteria.andUsernameLike("%" + keyword + "%");
            example.or(example.createCriteria().andNickNameLike("%" + keyword + "%"));
        }
        return adminMapper.selectByExample(example);
    }

    @Override
    public int update(Long id, UmsAdmin admin) {
        admin.setId(id);
        UmsAdmin rawAdmin = adminMapper.selectByPrimaryKey(id);
        if(rawAdmin.getPassword().equals(admin.getPassword())){
            //与原加密密码相同的不需要修改
            admin.setPassword(null);
        }else{
            //与原加密密码不同的需要加密修改
            if(StrUtil.isEmpty(admin.getPassword())){
                admin.setPassword(null);
            }else{
                admin.setPassword(passwordEncoder.encode(admin.getPassword()));
            }
        }
        int count = adminMapper.updateByPrimaryKeySelective(admin);
        getCacheService().delAdmin(id);
        return count;
    }

    @Override
    public int delete(Long id) {
        int count = adminMapper.deleteByPrimaryKey(id);
        getCacheService().delAdmin(id);
        getCacheService().delResourceList(id);
        return count;
    }

    @Override
    public int updateRole(Long adminId, List<Long> roleIds) {
        int count = roleIds == null ? 0 : roleIds.size();
        //先删除原来的关系
        UmsAdminRoleRelationExample adminRoleRelationExample = new UmsAdminRoleRelationExample();
        adminRoleRelationExample.createCriteria().andAdminIdEqualTo(adminId);
        adminRoleRelationMapper.deleteByExample(adminRoleRelationExample);
        //建立新关系
        if (!CollectionUtils.isEmpty(roleIds)) {
            List<UmsAdminRoleRelation> list = new ArrayList<>();
            for (Long roleId : roleIds) {
                UmsAdminRoleRelation roleRelation = new UmsAdminRoleRelation();
                roleRelation.setAdminId(adminId);
                roleRelation.setRoleId(roleId);
                list.add(roleRelation);
            }
            adminRoleRelationDao.insertList(list);
        }
        getCacheService().delResourceList(adminId);
        return count;
    }

    @Override
    public List<UmsRole> getRoleList(Long adminId) {
        return adminRoleRelationDao.getRoleList(adminId);
    }

    @Override
    public List<UmsResource> getResourceList(Long adminId) {
        //先从缓存中获取数据
        List<UmsResource> resourceList = getCacheService().getResourceList(adminId);
        if(CollUtil.isNotEmpty(resourceList)){
            return  resourceList;
        }
        //缓存中没有从数据库中获取
        resourceList = adminRoleRelationDao.getResourceList(adminId);
        if(CollUtil.isNotEmpty(resourceList)){
            //将数据库中的数据存入缓存中
            getCacheService().setResourceList(adminId,resourceList);
        }
        return resourceList;
    }

    @Override
    public int updatePassword(UpdateAdminPasswordParam param) {
        if(StrUtil.isEmpty(param.getUsername())
                ||StrUtil.isEmpty(param.getOldPassword())
                ||StrUtil.isEmpty(param.getNewPassword())){
            return -1;
        }
        UmsAdminExample example = new UmsAdminExample();
        example.createCriteria().andUsernameEqualTo(param.getUsername());
        List<UmsAdmin> adminList = adminMapper.selectByExample(example);
        if(CollUtil.isEmpty(adminList)){
            return -2;
        }
        UmsAdmin umsAdmin = adminList.get(0);
        if(!passwordEncoder.matches(param.getOldPassword(),umsAdmin.getPassword())){
            return -3;
        }
        umsAdmin.setPassword(passwordEncoder.encode(param.getNewPassword()));
        adminMapper.updateByPrimaryKey(umsAdmin);
        getCacheService().delAdmin(umsAdmin.getId());
        return 1;
    }

    @Override
    public UserDetails loadUserByUsername(String username){
        //获取用户信息
        UmsAdmin admin = getAdminByUsername(username);
        if (admin != null) {
            List<UmsResource> resourceList = getResourceList(admin.getId());
            return new AdminUserDetails(admin,resourceList);
        }
        throw new UsernameNotFoundException("用户名或密码错误");
    }

    @Override
    public UmsAdminCacheService getCacheService() {
        return SpringUtil.getBean(UmsAdminCacheService.class);
    }

    @Override
    public void logout(String username) {
        //清空缓存中的用户相关数据
        UmsAdmin admin = getCacheService().getAdmin(username);
        getCacheService().delAdmin(admin.getId());
        getCacheService().delResourceList(admin.getId());
    }

    @Override
    public AdminDashboardResult getDashboardData(Integer days) {
        AdminDashboardResult result = new AdminDashboardResult();
        
        try {
            // 获取所有订单数据用于统计
            OmsOrderExample allOrderExample = new OmsOrderExample();
            List<OmsOrder> allOrders = orderMapper.selectByExample(allOrderExample);
            
            // 当前日期
            LocalDate now = LocalDate.now();
            LocalDate monthStart = now.withDayOfMonth(1);
            LocalDate weekStart = now.minusDays(now.getDayOfWeek().getValue() - 1);
            LocalDate lastMonthStart = monthStart.minusMonths(1);
            LocalDate lastWeekStart = weekStart.minusWeeks(1);
            
            // 按状态统计订单数量
            long waitingPayOrders = allOrders.stream()
                    .filter(order -> order.getStatus() == 0) // 待付款
                    .count();
            
            long completedOrders = allOrders.stream()
                    .filter(order -> order.getStatus() == 3) // 已完成
                    .count();
            
            long waitingReceiveOrders = allOrders.stream()
                    .filter(order -> order.getStatus() == 2) // 待确认收货
                    .count();
            
            long waitingDeliveryOrders = allOrders.stream()
                    .filter(order -> order.getStatus() == 1) // 待发货
                    .count();
            
            long shippedOrders = allOrders.stream()
                    .filter(order -> order.getStatus() == 2) // 已发货（待确认收货）
                    .count();
            
            // 统计退款申请数量（这里简化处理，实际需要查询退款表）
            long waitingRefundOrders = 0; // TODO: 需要查询退款表
            
            // 本月统计
            long monthOrderCount = allOrders.stream()
                    .filter(order -> {
                        LocalDate orderDate = order.getCreateTime().toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate();
                        return !orderDate.isBefore(monthStart);
                    })
                    .count();
            
            BigDecimal monthSalesAmount = allOrders.stream()
                    .filter(order -> {
                        LocalDate orderDate = order.getCreateTime().toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate();
                        return !orderDate.isBefore(monthStart) && (order.getStatus() == 2 || order.getStatus() == 3);
                    })
                    .map(order -> order.getPayAmount() != null ? order.getPayAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 本周统计
            long weekOrderCount = allOrders.stream()
                    .filter(order -> {
                        LocalDate orderDate = order.getCreateTime().toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate();
                        return !orderDate.isBefore(weekStart);
                    })
                    .count();
            
            BigDecimal weekSalesAmount = allOrders.stream()
                    .filter(order -> {
                        LocalDate orderDate = order.getCreateTime().toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate();
                        return !orderDate.isBefore(weekStart) && (order.getStatus() == 2 || order.getStatus() == 3);
                    })
                    .map(order -> order.getPayAmount() != null ? order.getPayAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 上月统计（用于同比计算）
            long lastMonthOrderCount = allOrders.stream()
                    .filter(order -> {
                        LocalDate orderDate = order.getCreateTime().toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate();
                        return !orderDate.isBefore(lastMonthStart) && orderDate.isBefore(monthStart);
                    })
                    .count();
            
            BigDecimal lastMonthSalesAmount = allOrders.stream()
                    .filter(order -> {
                        LocalDate orderDate = order.getCreateTime().toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate();
                        return !orderDate.isBefore(lastMonthStart) && orderDate.isBefore(monthStart) 
                               && (order.getStatus() == 2 || order.getStatus() == 3);
                    })
                    .map(order -> order.getPayAmount() != null ? order.getPayAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 上周统计（用于同比计算）
            long lastWeekOrderCount = allOrders.stream()
                    .filter(order -> {
                        LocalDate orderDate = order.getCreateTime().toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate();
                        return !orderDate.isBefore(lastWeekStart) && orderDate.isBefore(weekStart);
                    })
                    .count();
            
            BigDecimal lastWeekSalesAmount = allOrders.stream()
                    .filter(order -> {
                        LocalDate orderDate = order.getCreateTime().toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate();
                        return !orderDate.isBefore(lastWeekStart) && orderDate.isBefore(weekStart)
                               && (order.getStatus() == 2 || order.getStatus() == 3);
                    })
                    .map(order -> order.getPayAmount() != null ? order.getPayAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 设置统计结果
            result.setMonthOrderCount((int) monthOrderCount);
            result.setMonthSalesAmount(monthSalesAmount);
            result.setPendingOrderCount((int) (waitingPayOrders + waitingDeliveryOrders + waitingReceiveOrders));
            result.setLowStockProductCount(0); // TODO: 实现库存不足统计
            result.setHotProductCount((int) completedOrders);
            result.setActiveUserCount(0); // TODO: 实现活跃用户统计
            
            // 今日统计（使用本周数据作为今日数据的替代）
            result.setTodayOrderCount((int) weekOrderCount);
            result.setTodaySalesAmount(weekSalesAmount);
            result.setTodayUserCount(0); // TODO: 实现今日新增用户统计
            result.setTodayProductViewCount(0); // TODO: 实现今日商品访问量统计
            result.setMonthUserCount(0); // TODO: 实现本月新增用户统计
            result.setMonthProductViewCount(0); // TODO: 实现本月商品访问量统计
            
            LOGGER.info("Dashboard统计完成: 本月订单{}笔, 销售额{}, 本周订单{}笔, 销售额{}", 
                       monthOrderCount, monthSalesAmount, weekOrderCount, weekSalesAmount);
            LOGGER.info("订单状态统计: 待付款{}, 待发货{}, 待确认收货{}, 已完成{}", 
                       waitingPayOrders, waitingDeliveryOrders, waitingReceiveOrders, completedOrders);
            
        } catch (Exception e) {
            LOGGER.error("获取Dashboard统计数据失败", e);
            // 返回空数据而不是null，避免前端报错
            result.setMonthOrderCount(0);
            result.setMonthSalesAmount(BigDecimal.ZERO);
            result.setTodayOrderCount(0);
            result.setTodaySalesAmount(BigDecimal.ZERO);
            result.setPendingOrderCount(0);
            result.setLowStockProductCount(0);
            result.setHotProductCount(0);
            result.setActiveUserCount(0);
            result.setTodayUserCount(0);
            result.setTodayProductViewCount(0);
            result.setMonthUserCount(0);
            result.setMonthProductViewCount(0);
        }
        
        return result;
    }

    @Override
    public int setWechatServiceImage(String imageUrl) {
        // 这里简化处理，实际项目中应该有一个 sys_setting 表来存储系统配置
        try {
            // 使用用户缓存临时存储，key使用特殊前缀
            UmsAdmin tempAdmin = new UmsAdmin();
            tempAdmin.setId(-1L); // 使用特殊ID
            tempAdmin.setUsername("wechat_service_image");
            tempAdmin.setNickName(imageUrl); // 临时存储在昵称字段
            getCacheService().setAdmin(tempAdmin);
            return 1;
        } catch (Exception e) {
            LOGGER.error("设置微信客服图片失败", e);
            return 0;
        }
    }

    @Override
    public String getWechatServiceImage() {
        try {
            // 从缓存获取
            UmsAdmin tempAdmin = getCacheService().getAdmin("wechat_service_image");
            return tempAdmin != null ? tempAdmin.getNickName() : null;
        } catch (Exception e) {
            LOGGER.error("获取微信客服图片失败", e);
            return null;
        }
    }
}
