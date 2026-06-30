package com.macro.mall.service.impl;

import com.macro.mall.mapper.PmsProductAttributeMapper;
import com.macro.mall.mapper.PmsProductAttributeValueMapper;
import com.macro.mall.mapper.PmsProductAttributeCategoryMapper;
import com.macro.mall.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 香烟属性管理服务类
 * 用于处理香烟特有属性的相关业务逻辑
 */
@Service
public class CigaretteAttributeServiceImpl {
    
    @Autowired
    private PmsProductAttributeMapper productAttributeMapper;
    
    @Autowired
    private PmsProductAttributeValueMapper productAttributeValueMapper;
    
    @Autowired
    private PmsProductAttributeCategoryMapper productAttributeCategoryMapper;
    
    /**
     * 获取产品的香烟属性信息
     * @param productId 产品ID
     * @return 香烟属性信息Map
     */
    public Map<String, String> getCigaretteAttributes(Long productId) {
        Map<String, String> cigaretteAttributes = new HashMap<>();
        
        // 查找香烟属性分类
        PmsProductAttributeCategoryExample categoryExample = new PmsProductAttributeCategoryExample();
        categoryExample.createCriteria().andNameEqualTo("香烟");
        List<PmsProductAttributeCategory> categories = productAttributeCategoryMapper.selectByExample(categoryExample);
        
        if (CollectionUtils.isEmpty(categories)) {
            return cigaretteAttributes;
        }
        
        Long cigaretteCategoryId = categories.get(0).getId();
        
        // 查找香烟相关属性
        PmsProductAttributeExample attributeExample = new PmsProductAttributeExample();
        attributeExample.createCriteria().andProductAttributeCategoryIdEqualTo(cigaretteCategoryId);
        List<PmsProductAttribute> attributes = productAttributeMapper.selectByExample(attributeExample);
        
        if (CollectionUtils.isEmpty(attributes)) {
            return cigaretteAttributes;
        }
        
        // 获取该产品的属性值
        PmsProductAttributeValueExample valueExample = new PmsProductAttributeValueExample();
        valueExample.createCriteria().andProductIdEqualTo(productId);
        List<PmsProductAttributeValue> attributeValues = productAttributeValueMapper.selectByExample(valueExample);
        
        // 构建属性名和值的映射
        Map<Long, String> attributeValueMap = new HashMap<>();
        for (PmsProductAttributeValue value : attributeValues) {
            attributeValueMap.put(value.getProductAttributeId(), value.getValue());
        }
        
        // 组装香烟属性信息
        for (PmsProductAttribute attribute : attributes) {
            String value = attributeValueMap.get(attribute.getId());
            if (value != null) {
                cigaretteAttributes.put(attribute.getName(), value);
            }
        }
        
        return cigaretteAttributes;
    }
    
    /**
     * 更新产品的香烟属性
     * @param productId 产品ID
     * @param cigaretteType 香烟类型
     * @param tarContent 焦油量
     * @param nicotineContent 尼古丁含量
     * @param packageCount 包装数量
     * @param pricePerStick 单支价格
     * @return 更新结果
     */
    public boolean updateCigaretteAttributes(Long productId, String cigaretteType, 
                                           String tarContent, String nicotineContent, 
                                           String packageCount, String pricePerStick) {
        try {
            // 查找香烟属性分类
            PmsProductAttributeCategoryExample categoryExample = new PmsProductAttributeCategoryExample();
            categoryExample.createCriteria().andNameEqualTo("香烟");
            List<PmsProductAttributeCategory> categories = productAttributeCategoryMapper.selectByExample(categoryExample);
            
            if (CollectionUtils.isEmpty(categories)) {
                return false;
            }
            
            Long cigaretteCategoryId = categories.get(0).getId();
            
            // 查找香烟相关属性
            PmsProductAttributeExample attributeExample = new PmsProductAttributeExample();
            attributeExample.createCriteria().andProductAttributeCategoryIdEqualTo(cigaretteCategoryId);
            List<PmsProductAttribute> attributes = productAttributeMapper.selectByExample(attributeExample);
            
            if (CollectionUtils.isEmpty(attributes)) {
                return false;
            }
            
            // 构建属性名和ID的映射
            Map<String, Long> attributeIdMap = new HashMap<>();
            for (PmsProductAttribute attribute : attributes) {
                attributeIdMap.put(attribute.getName(), attribute.getId());
            }
            
            // 更新各个属性值
            updateAttributeValue(productId, attributeIdMap.get("香烟类型"), cigaretteType);
            updateAttributeValue(productId, attributeIdMap.get("焦油量"), tarContent);
            updateAttributeValue(productId, attributeIdMap.get("尼古丁含量"), nicotineContent);
            updateAttributeValue(productId, attributeIdMap.get("包装数量"), packageCount);
            updateAttributeValue(productId, attributeIdMap.get("单支价格"), pricePerStick);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 更新单个属性值
     */
    private void updateAttributeValue(Long productId, Long attributeId, String value) {
        if (attributeId == null || value == null) {
            return;
        }
        
        // 查找现有属性值
        PmsProductAttributeValueExample example = new PmsProductAttributeValueExample();
        example.createCriteria()
            .andProductIdEqualTo(productId)
            .andProductAttributeIdEqualTo(attributeId);
        
        List<PmsProductAttributeValue> existingValues = productAttributeValueMapper.selectByExample(example);
        
        if (!CollectionUtils.isEmpty(existingValues)) {
            // 更新现有值
            PmsProductAttributeValue attributeValue = existingValues.get(0);
            attributeValue.setValue(value);
            productAttributeValueMapper.updateByPrimaryKey(attributeValue);
        } else {
            // 创建新值
            PmsProductAttributeValue attributeValue = new PmsProductAttributeValue();
            attributeValue.setProductId(productId);
            attributeValue.setProductAttributeId(attributeId);
            attributeValue.setValue(value);
            productAttributeValueMapper.insertSelective(attributeValue);
        }
    }
}
