package com.macro.mall.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.macro.mall.dao.*;
import com.macro.mall.dto.PmsProductImageParam;
import com.macro.mall.dto.PmsProductParam;
import com.macro.mall.dto.PmsProductQueryParam;
import com.macro.mall.dto.PmsProductResult;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.service.PmsProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 商品管理Service实现类
 * Created by macro on 2018/4/26.
 */
@Service
public class PmsProductServiceImpl implements PmsProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PmsProductServiceImpl.class);
    @Value("${mall.upload.path:/tmp/mall/uploads}")
    private String uploadPath;
    @Value("${mall.upload.url:/uploads}")
    private String uploadUrl;
    @Autowired
    private PmsProductMapper productMapper;
    @Autowired
    private PmsMemberPriceDao memberPriceDao;
    @Autowired
    private PmsMemberPriceMapper memberPriceMapper;
    @Autowired
    private PmsProductLadderDao productLadderDao;
    @Autowired
    private PmsProductLadderMapper productLadderMapper;
    @Autowired
    private PmsProductFullReductionDao productFullReductionDao;
    @Autowired
    private PmsProductFullReductionMapper productFullReductionMapper;
    @Autowired
    private PmsSkuStockDao skuStockDao;
    @Autowired
    private PmsSkuStockMapper skuStockMapper;
    @Autowired
    private PmsProductAttributeValueDao productAttributeValueDao;
    @Autowired
    private PmsProductAttributeValueMapper productAttributeValueMapper;
    @Autowired
    private CmsSubjectProductRelationDao subjectProductRelationDao;
    @Autowired
    private CmsSubjectProductRelationMapper subjectProductRelationMapper;
    @Autowired
    private CmsPrefrenceAreaProductRelationDao prefrenceAreaProductRelationDao;
    @Autowired
    private CmsPrefrenceAreaProductRelationMapper prefrenceAreaProductRelationMapper;
    @Autowired
    private PmsProductDao productDao;
    @Autowired
    private PmsProductVertifyRecordDao productVertifyRecordDao;
    @Autowired
    private PmsProductCategoryMapper productCategoryMapper;
    @Autowired
    private PmsProductAttributeMapper productAttributeMapper;
    @Autowired
    private PmsProductAttributeCategoryMapper productAttributeCategoryMapper;

    @Override
    public int create(PmsProductParam productParam) {
        int count;
        //创建商品
        PmsProduct product = productParam;
        product.setId(null);
        
        productMapper.insertSelective(product);
        //根据促销类型设置价格：会员价格、阶梯价格、满减价格
        Long productId = product.getId();
        
        // 为烟草产品设置香烟属性（仅当用户提供了属性值时）
        if (isTobaccoProduct(product)) {
            setupCigaretteAttributesIfProvided(productParam, productId);
        }
        
        //会员价格
        relateAndInsertList(memberPriceDao, productParam.getMemberPriceList(), productId);
        //阶梯价格
        relateAndInsertList(productLadderDao, productParam.getProductLadderList(), productId);
        //满减价格
        relateAndInsertList(productFullReductionDao, productParam.getProductFullReductionList(), productId);
        //确保SKU库存信息正确处理
        ensureSkuStockList(productParam, productId);
        //修复SKU ID为0或null的问题，然后处理sku的编码
        fixSkuIds(productParam.getSkuStockList(), productId);
        handleSkuStockCode(productParam.getSkuStockList(),productId);
        //添加sku库存信息
        relateAndInsertList(skuStockDao, productParam.getSkuStockList(), productId);
        //添加商品参数,添加自定义商品规格
        relateAndInsertList(productAttributeValueDao, productParam.getProductAttributeValueList(), productId);
        //关联专题
        relateAndInsertList(subjectProductRelationDao, productParam.getSubjectProductRelationList(), productId);
        //关联优选
        relateAndInsertList(prefrenceAreaProductRelationDao, productParam.getPrefrenceAreaProductRelationList(), productId);
        count = 1;
        return count;
    }
    
    /**
     * 判断是否为烟草产品（电子烟、纸卷烟、加热式香烟）
     */
    private boolean isTobaccoProduct(PmsProduct product) {
        if (product.getProductCategoryId() == null) {
            return false;
        }
        
        PmsProductCategory category = productCategoryMapper.selectByPrimaryKey(product.getProductCategoryId());
        if (category == null) {
            return false;
        }
        
        // 判断是否为烟草相关分类
        String categoryName = category.getName();
        // return "电子烟".equals(categoryName) || 
        //        "纸卷烟".equals(categoryName) || 
        //        "加热式香烟".equals(categoryName) || 
        //        "香烟".equals(categoryName);
        return true;
    }
    
    /**
     * 为烟草产品设置香烟属性（仅处理用户实际提供的属性值）
     */
    private void setupCigaretteAttributesIfProvided(PmsProductParam productParam, Long productId) {
        // 如果用户没有提供任何属性值，则不进行任何处理
        List<PmsProductAttributeValue> attributeValues = productParam.getProductAttributeValueList();
        if (CollectionUtils.isEmpty(attributeValues)) {
            LOGGER.info("产品 {} 没有提供属性值，跳过香烟属性设置", productId);
            return;
        }
        
        // 查找香烟属性分类
        PmsProductAttributeCategoryExample categoryExample = new PmsProductAttributeCategoryExample();
        categoryExample.createCriteria().andNameEqualTo("香烟");
        List<PmsProductAttributeCategory> categories = productAttributeCategoryMapper.selectByExample(categoryExample);
        
        if (CollectionUtils.isEmpty(categories)) {
            LOGGER.warn("香烟属性分类不存在，请先执行 cigarette_attributes_init.sql 脚本");
            return;
        }
        
        Long cigaretteCategoryId = categories.get(0).getId();
        
        // 查找香烟相关属性
        PmsProductAttributeExample attributeExample = new PmsProductAttributeExample();
        attributeExample.createCriteria().andProductAttributeCategoryIdEqualTo(cigaretteCategoryId);
        List<PmsProductAttribute> attributes = productAttributeMapper.selectByExample(attributeExample);
        
        if (CollectionUtils.isEmpty(attributes)) {
            LOGGER.warn("香烟属性不存在，请先执行 cigarette_attributes_init.sql 脚本");
            return;
        }
        
        // 只验证和设置用户提供的属性值，不添加任何默认值
        for (PmsProductAttributeValue attributeValue : attributeValues) {
            if (attributeValue.getProductId() == null || attributeValue.getProductId().equals(0L)) {
                attributeValue.setProductId(productId);
            }
            
            // 验证属性是否属于香烟分类（可选的验证步骤）
            Long attributeId = attributeValue.getProductAttributeId();
            if (attributeId != null) {
                boolean isValidCigaretteAttribute = attributes.stream()
                    .anyMatch(attr -> attr.getId().equals(attributeId));
                
                if (isValidCigaretteAttribute) {
                    LOGGER.info("为产品 {} 设置香烟属性ID {} 值：{}", 
                        productId, attributeId, attributeValue.getValue());
                } else {
                    LOGGER.debug("产品 {} 的属性ID {} 不属于香烟属性分类", productId, attributeId);
                }
            }
        }
    }


    private void handleSkuStockCode(List<PmsSkuStock> skuStockList, Long productId) {
        if(CollectionUtils.isEmpty(skuStockList))return;
        for(int i=0;i<skuStockList.size();i++){
            PmsSkuStock skuStock = skuStockList.get(i);
            if(StrUtil.isEmpty(skuStock.getSkuCode())){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                StringBuilder sb = new StringBuilder();
                //日期
                sb.append(sdf.format(new Date()));
                //四位商品id
                sb.append(String.format("%04d", productId));
                //三位索引id
                sb.append(String.format("%03d", i+1));
                skuStock.setSkuCode(sb.toString());
            }
        }
    }

    @Override
    public PmsProductResult getUpdateInfo(Long id) {
        return productDao.getUpdateInfo(id);
    }

    @Override
    public int update(Long id, PmsProductParam productParam) {
        int count;
        //更新商品信息
        PmsProduct product = productParam;
        product.setId(id);
        productMapper.updateByPrimaryKeySelective(product);
        //会员价格
        PmsMemberPriceExample pmsMemberPriceExample = new PmsMemberPriceExample();
        pmsMemberPriceExample.createCriteria().andProductIdEqualTo(id);
        memberPriceMapper.deleteByExample(pmsMemberPriceExample);
        relateAndInsertList(memberPriceDao, productParam.getMemberPriceList(), id);
        //阶梯价格
        PmsProductLadderExample ladderExample = new PmsProductLadderExample();
        ladderExample.createCriteria().andProductIdEqualTo(id);
        productLadderMapper.deleteByExample(ladderExample);
        relateAndInsertList(productLadderDao, productParam.getProductLadderList(), id);
        //满减价格
        PmsProductFullReductionExample fullReductionExample = new PmsProductFullReductionExample();
        fullReductionExample.createCriteria().andProductIdEqualTo(id);
        productFullReductionMapper.deleteByExample(fullReductionExample);
        relateAndInsertList(productFullReductionDao, productParam.getProductFullReductionList(), id);
        //修改sku库存信息
        ensureSkuStockList(productParam, id);
        handleUpdateSkuStockList(id, productParam);
        //修改商品参数,添加自定义商品规格
        PmsProductAttributeValueExample productAttributeValueExample = new PmsProductAttributeValueExample();
        productAttributeValueExample.createCriteria().andProductIdEqualTo(id);
        productAttributeValueMapper.deleteByExample(productAttributeValueExample);
        relateAndInsertList(productAttributeValueDao, productParam.getProductAttributeValueList(), id);
        //关联专题
        CmsSubjectProductRelationExample subjectProductRelationExample = new CmsSubjectProductRelationExample();
        subjectProductRelationExample.createCriteria().andProductIdEqualTo(id);
        subjectProductRelationMapper.deleteByExample(subjectProductRelationExample);
        relateAndInsertList(subjectProductRelationDao, productParam.getSubjectProductRelationList(), id);
        //关联优选
        CmsPrefrenceAreaProductRelationExample prefrenceAreaExample = new CmsPrefrenceAreaProductRelationExample();
        prefrenceAreaExample.createCriteria().andProductIdEqualTo(id);
        prefrenceAreaProductRelationMapper.deleteByExample(prefrenceAreaExample);
        relateAndInsertList(prefrenceAreaProductRelationDao, productParam.getPrefrenceAreaProductRelationList(), id);
        count = 1;
        return count;
    }

    private void handleUpdateSkuStockList(Long id, PmsProductParam productParam) {
        //当前的sku信息
        List<PmsSkuStock> currSkuList = productParam.getSkuStockList();
        //当前没有sku直接删除
        if(CollUtil.isEmpty(currSkuList)){
            PmsSkuStockExample skuStockExample = new PmsSkuStockExample();
            skuStockExample.createCriteria().andProductIdEqualTo(id);
            skuStockMapper.deleteByExample(skuStockExample);
            return;
        }
        
        // 修复SKU ID为0或null的问题：将这些SKU的ID设置为商品ID
        fixSkuIds(currSkuList, id);
        
        //获取初始sku信息
        PmsSkuStockExample skuStockExample = new PmsSkuStockExample();
        skuStockExample.createCriteria().andProductIdEqualTo(id);
        List<PmsSkuStock> oriStuList = skuStockMapper.selectByExample(skuStockExample);
        //获取新增sku信息
        List<PmsSkuStock> insertSkuList = currSkuList.stream().filter(item->item.getId()==null).collect(Collectors.toList());
        //获取需要更新的sku信息
        List<PmsSkuStock> updateSkuList = currSkuList.stream().filter(item->item.getId()!=null).collect(Collectors.toList());
        List<Long> updateSkuIds = updateSkuList.stream().map(PmsSkuStock::getId).collect(Collectors.toList());
        //获取需要删除的sku信息
        List<PmsSkuStock> removeSkuList = oriStuList.stream().filter(item-> !updateSkuIds.contains(item.getId())).collect(Collectors.toList());
        handleSkuStockCode(insertSkuList,id);
        handleSkuStockCode(updateSkuList,id);
        //新增sku
        if(CollUtil.isNotEmpty(insertSkuList)){
            relateAndInsertList(skuStockDao, insertSkuList, id);
        }
        //删除sku
        if(CollUtil.isNotEmpty(removeSkuList)){
            List<Long> removeSkuIds = removeSkuList.stream().map(PmsSkuStock::getId).collect(Collectors.toList());
            PmsSkuStockExample removeExample = new PmsSkuStockExample();
            removeExample.createCriteria().andIdIn(removeSkuIds);
            skuStockMapper.deleteByExample(removeExample);
        }
        //修改sku
        if(CollUtil.isNotEmpty(updateSkuList)){
            for (PmsSkuStock pmsSkuStock : updateSkuList) {
                skuStockMapper.updateByPrimaryKeySelective(pmsSkuStock);
            }
        }

    }

    @Override
    public List<PmsProduct> list(PmsProductQueryParam productQueryParam, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        PmsProductExample productExample = new PmsProductExample();
        PmsProductExample.Criteria criteria = productExample.createCriteria();
        criteria.andDeleteStatusEqualTo(0);
        if (productQueryParam.getPublishStatus() != null) {
            criteria.andPublishStatusEqualTo(productQueryParam.getPublishStatus());
        }
        if (productQueryParam.getVerifyStatus() != null) {
            criteria.andVerifyStatusEqualTo(productQueryParam.getVerifyStatus());
        }
        if (!StrUtil.isEmpty(productQueryParam.getKeyword())) {
            criteria.andNameLike("%" + productQueryParam.getKeyword() + "%");
        }
        if (!StrUtil.isEmpty(productQueryParam.getProductSn())) {
            criteria.andProductSnEqualTo(productQueryParam.getProductSn());
        }
        if (productQueryParam.getBrandId() != null) {
            criteria.andBrandIdEqualTo(productQueryParam.getBrandId());
        }
        if (productQueryParam.getProductCategoryId() != null) {
            criteria.andProductCategoryIdEqualTo(productQueryParam.getProductCategoryId());
        }
        
        // 添加排序逻辑
        if (StrUtil.isNotEmpty(productQueryParam.getOrderBy()) && StrUtil.isNotEmpty(productQueryParam.getOrderType())) {
            String orderBy = productQueryParam.getOrderBy();
            String orderType = productQueryParam.getOrderType();
            
            // 验证排序字段是否有效
            if (isValidOrderField(orderBy)) {
                String orderClause = orderBy + " " + orderType;
                productExample.setOrderByClause(orderClause);
            }
        }
        
        return productMapper.selectByExample(productExample);
    }

    /**
     * 验证排序字段是否有效
     * @param orderBy 排序字段
     * @return 是否有效
     */
    private boolean isValidOrderField(String orderBy) {
        // 定义允许的排序字段
        return "id".equals(orderBy) || 
               "name".equals(orderBy) || 
               "price".equals(orderBy) || 
               "stock".equals(orderBy) || 
               "sale".equals(orderBy) || 
               "create_time".equals(orderBy) || 
               "update_time".equals(orderBy) || 
               "sort".equals(orderBy);
    }

    @Override
    public int updateVerifyStatus(List<Long> ids, Integer verifyStatus, String detail) {
        PmsProduct product = new PmsProduct();
        product.setVerifyStatus(verifyStatus);
        PmsProductExample example = new PmsProductExample();
        example.createCriteria().andIdIn(ids);
        List<PmsProductVertifyRecord> list = new ArrayList<>();
        int count = productMapper.updateByExampleSelective(product, example);
        //修改完审核状态后插入审核记录
        for (Long id : ids) {
            PmsProductVertifyRecord record = new PmsProductVertifyRecord();
            record.setProductId(id);
            record.setCreateTime(new Date());
            record.setDetail(detail);
            record.setStatus(verifyStatus);
            record.setVertifyMan("test");
            list.add(record);
        }
        productVertifyRecordDao.insertList(list);
        return count;
    }

    @Override
    public int updatePublishStatus(List<Long> ids, Integer publishStatus) {
        PmsProduct record = new PmsProduct();
        record.setPublishStatus(publishStatus);
        PmsProductExample example = new PmsProductExample();
        example.createCriteria().andIdIn(ids);
        return productMapper.updateByExampleSelective(record, example);
    }

    @Override
    public int updateRecommendStatus(List<Long> ids, Integer recommendStatus) {
        PmsProduct record = new PmsProduct();
        record.setRecommandStatus(recommendStatus);
        PmsProductExample example = new PmsProductExample();
        example.createCriteria().andIdIn(ids);
        return productMapper.updateByExampleSelective(record, example);
    }

    @Override
    public int updateNewStatus(List<Long> ids, Integer newStatus) {
        PmsProduct record = new PmsProduct();
        record.setNewStatus(newStatus);
        PmsProductExample example = new PmsProductExample();
        example.createCriteria().andIdIn(ids);
        return productMapper.updateByExampleSelective(record, example);
    }

    @Override
    public int updateDeleteStatus(List<Long> ids, Integer deleteStatus) {
        PmsProduct record = new PmsProduct();
        record.setDeleteStatus(deleteStatus);
        PmsProductExample example = new PmsProductExample();
        example.createCriteria().andIdIn(ids);
        return productMapper.updateByExampleSelective(record, example);
    }

    @Override
    public List<PmsProduct> list(String keyword) {
        PmsProductExample productExample = new PmsProductExample();
        PmsProductExample.Criteria criteria = productExample.createCriteria();
        criteria.andDeleteStatusEqualTo(0);
        if(!StrUtil.isEmpty(keyword)){
            criteria.andNameLike("%" + keyword + "%");
            productExample.or().andDeleteStatusEqualTo(0).andProductSnLike("%" + keyword + "%");
        }
        return productMapper.selectByExample(productExample);
    }

    /**
     * 修复SKU ID为0或null的问题
     * 当SKU的ID为0或null时，将其设置为商品ID，以解决库存扣减时找不到SKU记录的问题
     * 
     * @param skuList 待修复的SKU列表
     * @param productId 商品ID
     */
    private void fixSkuIds(List<PmsSkuStock> skuList, Long productId) {
        if (CollUtil.isEmpty(skuList) || productId == null) {
            return;
        }
        
        for (PmsSkuStock sku : skuList) {
            if (sku.getId() == null || sku.getId().equals(0L)) {
                LOGGER.info("修复SKU ID: 将商品ID={}的SKU ID从{}设置为{}", productId, sku.getId(), productId);
                sku.setId(productId);
            }
        }
    }

    /**
     * 建立和插入关系表操作
     *
     * @param dao       可以操作的dao
     * @param dataList  要插入的数据
     * @param productId 建立关系的id
     */
    private void relateAndInsertList(Object dao, List<?> dataList, Long productId) {
        try {
            if (CollectionUtils.isEmpty(dataList)) return;
            for (Object item : dataList) {
                Method setId = item.getClass().getMethod("setId", Long.class);
                setId.invoke(item, (Long) null);
                Method setProductId = item.getClass().getMethod("setProductId", Long.class);
                setProductId.invoke(item, productId);
            }
            Method insertList = dao.getClass().getMethod("insertList", List.class);
            insertList.invoke(dao, dataList);
        } catch (Exception e) {
            LOGGER.warn("创建商品出错:{}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public int batchDelete(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return 0;
        }
        
        // 物理删除商品
        PmsProductExample example = new PmsProductExample();
        example.createCriteria().andIdIn(productIds);
        
        // 先删除相关数据
        for (Long productId : productIds) {
            // 删除商品会员价格
            PmsMemberPriceExample memberPriceExample = new PmsMemberPriceExample();
            memberPriceExample.createCriteria().andProductIdEqualTo(productId);
            memberPriceMapper.deleteByExample(memberPriceExample);
            
            // 删除商品阶梯价格
            PmsProductLadderExample ladderExample = new PmsProductLadderExample();
            ladderExample.createCriteria().andProductIdEqualTo(productId);
            productLadderMapper.deleteByExample(ladderExample);
            
            // 删除商品满减价格
            PmsProductFullReductionExample fullReductionExample = new PmsProductFullReductionExample();
            fullReductionExample.createCriteria().andProductIdEqualTo(productId);
            productFullReductionMapper.deleteByExample(fullReductionExample);
            
            // 删除商品SKU库存
            PmsSkuStockExample skuStockExample = new PmsSkuStockExample();
            skuStockExample.createCriteria().andProductIdEqualTo(productId);
            skuStockMapper.deleteByExample(skuStockExample);
        }
        
        return productMapper.deleteByExample(example);
    }

    @Override
    public void exportProductsCsv(PmsProductQueryParam queryParam, HttpServletResponse response) {
        // 设置响应头
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=products.csv");
        
        try (PrintWriter writer = response.getWriter()) {
            // 写入CSV头部
            writer.println("商品编号,商品名称,品牌,商品分类,价格,库存,销量,上架状态");
            
            // 分页查询并写入数据
            int pageSize = 1000;
            int pageNum = 1;
            List<PmsProduct> products;
            
            do {
                PageHelper.startPage(pageNum, pageSize);
                products = list(queryParam, pageSize, pageNum);
                
                for (PmsProduct product : products) {
                    StringBuilder line = new StringBuilder();
                    line.append(product.getProductSn() != null ? product.getProductSn() : "").append(",");
                    line.append(product.getName() != null ? product.getName() : "").append(",");
                    line.append(product.getBrandName() != null ? product.getBrandName() : "").append(",");
                    line.append(product.getProductCategoryName() != null ? product.getProductCategoryName() : "").append(",");
                    line.append(product.getPrice() != null ? product.getPrice() : "0").append(",");
                    line.append(product.getStock() != null ? product.getStock() : "0").append(",");
                    line.append(product.getSale() != null ? product.getSale() : "0").append(",");
                    line.append(product.getPublishStatus() == 1 ? "上架" : "下架");
                    
                    writer.println(line.toString());
                }
                
                pageNum++;
            } while (products.size() == pageSize);
            
        } catch (IOException e) {
            throw new RuntimeException("导出CSV文件失败", e);
        }
    }

    @Override
    public String uploadImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }
        
        // 创建上传目录
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String fileName = UUID.randomUUID().toString() + extension;
        File destFile = new File(uploadDir, fileName);
        
        try {
            file.transferTo(destFile);
            return uploadUrl + "/" + fileName;
        } catch (IOException e) {
            LOGGER.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败");
        }
    }

    @Override
    public int deleteImage(String imageUrl) {
        if (StrUtil.isEmpty(imageUrl)) {
            return 0;
        }
        
        try {
            // 从URL中提取文件名
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            File file = new File(uploadPath, fileName);
            
            if (file.exists() && file.delete()) {
                return 1;
            }
            return 0;
        } catch (Exception e) {
            LOGGER.error("删除图片失败", e);
            return 0;
        }
    }

    @Override
    public List<String> getImageList(Long productId) {
        // 这里简化处理，实际项目中应该有专门的商品图片表
        PmsProduct product = productMapper.selectByPrimaryKey(productId);
        List<String> images = new ArrayList<>();
        
        if (product != null) {
            if (StrUtil.isNotEmpty(product.getPic())) {
                images.add(product.getPic());
            }
            if (StrUtil.isNotEmpty(product.getAlbumPics())) {
                String[] albumPics = product.getAlbumPics().split(",");
                for (String pic : albumPics) {
                    if (StrUtil.isNotEmpty(pic.trim())) {
                        images.add(pic.trim());
                    }
                }
            }
        }
        
        return images;
    }

    @Override
    public int updateImages(Long productId, List<PmsProductImageParam> imageParams) {
        PmsProduct product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        // 更新商品图片
        List<String> imageUrls = new ArrayList<>();
        for (PmsProductImageParam param : imageParams) {
            if (param.getImageUrls() != null) {
                imageUrls.addAll(param.getImageUrls());
            }
        }
        
        if (!imageUrls.isEmpty()) {
            product.setPic(imageUrls.get(0)); // 第一张作为主图
            if (imageUrls.size() > 1) {
                product.setAlbumPics(String.join(",", imageUrls.subList(1, imageUrls.size())));
            } else {
                product.setAlbumPics("");
            }
        } else {
            product.setPic("");
            product.setAlbumPics("");
        }
        
        return productMapper.updateByPrimaryKeySelective(product);
    }

    /**
     * 确保商品有正确的SKU库存信息
     * 如果没有提供SKU信息，则创建一个默认的SKU记录
     * 
     * @param productParam 商品参数
     * @param productId 商品ID
     */
    private void ensureSkuStockList(PmsProductParam productParam, Long productId) {
        List<PmsSkuStock> skuStockList = productParam.getSkuStockList();
        
        // 如果没有SKU信息，创建一个默认的SKU记录
        if (CollUtil.isEmpty(skuStockList)) {
            LOGGER.info("商品ID={}没有SKU信息，创建默认SKU记录", productId);
            PmsSkuStock defaultSku = new PmsSkuStock();
            defaultSku.setId(productId); // 使用商品ID作为SKU ID
            defaultSku.setProductId(productId);
            defaultSku.setSkuCode("SKU_" + productId);
            defaultSku.setPrice(productParam.getPrice());
            defaultSku.setStock(productParam.getStock() != null ? productParam.getStock() : 0);
            defaultSku.setLowStock(productParam.getLowStock() != null ? productParam.getLowStock() : 5);
            defaultSku.setPromotionPrice(productParam.getPromotionPrice());
            defaultSku.setLockStock(0);
            defaultSku.setSale(0);
            
            skuStockList = new ArrayList<>();
            skuStockList.add(defaultSku);
            productParam.setSkuStockList(skuStockList);
        } else {
            // 确保所有SKU都有正确的库存信息
            for (PmsSkuStock sku : skuStockList) {
                if (sku.getStock() == null) {
                    sku.setStock(productParam.getStock() != null ? productParam.getStock() : 0);
                }
                if (sku.getPrice() == null) {
                    sku.setPrice(productParam.getPrice());
                }
                if (sku.getLowStock() == null) {
                    sku.setLowStock(5);
                }
                if (sku.getLockStock() == null) {
                    sku.setLockStock(0);
                }
                if (sku.getSale() == null) {
                    sku.setSale(0);
                }
            }
        }
    }
}
