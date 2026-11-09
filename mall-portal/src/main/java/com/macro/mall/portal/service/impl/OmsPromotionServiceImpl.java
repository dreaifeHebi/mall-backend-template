package com.macro.mall.portal.service.impl;

import com.macro.mall.model.OmsCartItem;
import com.macro.mall.model.PmsProductFullReduction;
import com.macro.mall.model.PmsProductLadder;
import com.macro.mall.model.PmsSkuStock;
import com.macro.mall.portal.dao.PortalProductDao;
import com.macro.mall.portal.domain.CartPromotionItem;
import com.macro.mall.portal.domain.PromotionProduct;
import com.macro.mall.portal.service.OmsPromotionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Created by macro on 2018/8/27.
 * 促销管理Service实现类
 */
@Service
public class OmsPromotionServiceImpl implements OmsPromotionService {
    @Autowired
    private PortalProductDao portalProductDao;

    @Override
    public List<CartPromotionItem> calcCartPromotion(List<OmsCartItem> cartItemList) {
        System.out.println("=== 开始计算购物车促销，购物车项目数量: " + (cartItemList != null ? cartItemList.size() : 0));
        
        //1.先根据productId对CartItem进行分组，以spu为单位进行计算优惠
        Map<Long, List<OmsCartItem>> productCartMap = groupCartItemBySpu(cartItemList);
        System.out.println("=== 商品分组完成，商品种类数量: " + productCartMap.size());
        
        //2.查询所有商品的优惠相关信息
        List<PromotionProduct> promotionProductList = getPromotionProductList(cartItemList);
        System.out.println("=== 查询促销信息完成，促销商品数量: " + (promotionProductList != null ? promotionProductList.size() : 0));
        
        //3.根据商品促销类型计算商品促销优惠价格
        List<CartPromotionItem> cartPromotionItemList = new ArrayList<>();
        for (Map.Entry<Long, List<OmsCartItem>> entry : productCartMap.entrySet()) {
            Long productId = entry.getKey();
            PromotionProduct promotionProduct = getPromotionProductById(productId, promotionProductList);
            List<OmsCartItem> itemList = entry.getValue();
            // 如果没有找到促销商品信息，则按无优惠处理
            if (promotionProduct == null) {
                System.out.println("=== 商品ID=" + productId + " 没有促销信息，调用 handleNoReduceByItems");
                handleNoReduceByItems(cartPromotionItemList, itemList);
                continue;
            }
            Integer promotionType = promotionProduct.getPromotionType();
            if (promotionType == 1) {
                //单品促销
                for (OmsCartItem item : itemList) {
                    CartPromotionItem cartPromotionItem = new CartPromotionItem();
                    BeanUtils.copyProperties(item,cartPromotionItem);
                    cartPromotionItem.setPromotionMessage("单品促销");                    //商品原价-促销价
                    PmsSkuStock skuStock = getOriginalPrice(promotionProduct, item.getProductSkuId());
                    BigDecimal originalPrice = resolveOriginalPrice(item, promotionProduct, skuStock);
                    cartPromotionItem.setPrice(originalPrice);
                    if (skuStock != null) {
                        //单品促销使用原价
                        cartPromotionItem.setReduceAmount(skuStock.getPromotionPrice() == null ? new BigDecimal(0) : originalPrice.subtract(skuStock.getPromotionPrice()));
                        cartPromotionItem.setRealStock(skuStock.getStock()-skuStock.getLockStock());
                    } else {
                        // 如果找不到SKU信息，使用购物车原价
                        cartPromotionItem.setReduceAmount(new BigDecimal(0));
                        cartPromotionItem.setRealStock(999999);
                    }
                    cartPromotionItem.setIntegration(promotionProduct.getGiftPoint());
                    cartPromotionItem.setGrowth(promotionProduct.getGiftGrowth());
                    cartPromotionItemList.add(cartPromotionItem);
                }
            } else if (promotionType == 3) {
                //打折优惠
                int count = getCartItemCount(itemList);
                PmsProductLadder ladder = getProductLadder(count, promotionProduct.getProductLadderList());
                if(ladder!=null){
                    for (OmsCartItem item : itemList) {
                        CartPromotionItem cartPromotionItem = new CartPromotionItem();
                        BeanUtils.copyProperties(item,cartPromotionItem);
                        String message = getLadderPromotionMessage(ladder);
                        cartPromotionItem.setPromotionMessage(message);                        //商品原价-折扣*商品原价
                        PmsSkuStock skuStock = getOriginalPrice(promotionProduct,item.getProductSkuId());
                        BigDecimal originalPrice = resolveOriginalPrice(item, promotionProduct, skuStock);
                        cartPromotionItem.setPrice(originalPrice);
                        if (skuStock != null) {
                            BigDecimal reduceAmount = originalPrice.subtract(ladder.getDiscount().multiply(originalPrice));
                            cartPromotionItem.setReduceAmount(reduceAmount);
                            cartPromotionItem.setRealStock(skuStock.getStock()-skuStock.getLockStock());
                        } else {
                            // 如果找不到SKU信息，使用购物车原价
                            cartPromotionItem.setReduceAmount(new BigDecimal(0));
                            cartPromotionItem.setRealStock(999999);
                        }
                        cartPromotionItem.setIntegration(promotionProduct.getGiftPoint());
                        cartPromotionItem.setGrowth(promotionProduct.getGiftGrowth());
                        cartPromotionItemList.add(cartPromotionItem);
                    }
                }else{
                    handleNoReduce(cartPromotionItemList,itemList,promotionProduct);
                }
            } else if (promotionType == 4) {
                //满减
                BigDecimal totalAmount= getCartItemAmount(itemList,promotionProductList);
                PmsProductFullReduction fullReduction = getProductFullReduction(totalAmount,promotionProduct.getProductFullReductionList());
                if(fullReduction!=null){
                    for (OmsCartItem item : itemList) {
                        CartPromotionItem cartPromotionItem = new CartPromotionItem();
                        BeanUtils.copyProperties(item,cartPromotionItem);
                        String message = getFullReductionPromotionMessage(fullReduction);
                        cartPromotionItem.setPromotionMessage(message);                        //(商品原价/总价)*满减金额
                        PmsSkuStock skuStock= getOriginalPrice(promotionProduct, item.getProductSkuId());
                        BigDecimal originalPrice = resolveOriginalPrice(item, promotionProduct, skuStock);
                        cartPromotionItem.setPrice(originalPrice);
                        if (skuStock != null) {
                            BigDecimal reduceAmount = originalPrice.divide(totalAmount,RoundingMode.HALF_EVEN).multiply(fullReduction.getReducePrice());
                            cartPromotionItem.setReduceAmount(reduceAmount);
                            cartPromotionItem.setRealStock(skuStock.getStock()-skuStock.getLockStock());
                        } else {
                            // 如果找不到SKU信息，使用购物车原价
                            cartPromotionItem.setReduceAmount(new BigDecimal(0));
                            cartPromotionItem.setRealStock(999999);
                        }
                        cartPromotionItem.setIntegration(promotionProduct.getGiftPoint());
                        cartPromotionItem.setGrowth(promotionProduct.getGiftGrowth());
                        cartPromotionItemList.add(cartPromotionItem);
                    }
                }else{
                    handleNoReduce(cartPromotionItemList,itemList,promotionProduct);
                }
            } else {
                //无优惠
                handleNoReduce(cartPromotionItemList, itemList,promotionProduct);
            }
        }
        
        System.out.println("=== 购物车促销计算完成，返回促销项目数量: " + cartPromotionItemList.size());
        for (int i = 0; i < cartPromotionItemList.size(); i++) {
            CartPromotionItem item = cartPromotionItemList.get(i);
            System.out.println("=== 促销项目[" + i + "]: 商品ID=" + item.getProductId() + 
                             ", 数量=" + item.getQuantity() + 
                             ", 库存=" + item.getRealStock() + 
                             ", 促销信息=" + item.getPromotionMessage());
        }
        
        return cartPromotionItemList;
    }

    /**
     * 查询所有商品的优惠相关信息
     */
    private List<PromotionProduct> getPromotionProductList(List<OmsCartItem> cartItemList) {
        List<Long> productIdList = new ArrayList<>();
        for(OmsCartItem cartItem:cartItemList){
            productIdList.add(cartItem.getProductId());
        }
        return portalProductDao.getPromotionProductList(productIdList);
    }

    /**
     * 以spu为单位对购物车中商品进行分组
     */
    private Map<Long, List<OmsCartItem>> groupCartItemBySpu(List<OmsCartItem> cartItemList) {
        Map<Long, List<OmsCartItem>> productCartMap = new TreeMap<>();
        for (OmsCartItem cartItem : cartItemList) {
            List<OmsCartItem> productCartItemList = productCartMap.get(cartItem.getProductId());
            if (productCartItemList == null) {
                productCartItemList = new ArrayList<>();
                productCartItemList.add(cartItem);
                productCartMap.put(cartItem.getProductId(), productCartItemList);
            } else {
                productCartItemList.add(cartItem);
            }
        }
        return productCartMap;
    }

    /**
     * 获取满减促销消息
     */
    private String getFullReductionPromotionMessage(PmsProductFullReduction fullReduction) {
        StringBuilder sb = new StringBuilder();
        sb.append("Full reduction: spend ");
        sb.append(fullReduction.getFullPrice());
        sb.append(", save ");
        sb.append(fullReduction.getReducePrice());
        return sb.toString();
    }    /**
     * 对没满足优惠条件的商品进行处理
     */
    private void handleNoReduce(List<CartPromotionItem> cartPromotionItemList, List<OmsCartItem> itemList,PromotionProduct promotionProduct) {
        for (OmsCartItem item : itemList) {
            CartPromotionItem cartPromotionItem = new CartPromotionItem();
            BeanUtils.copyProperties(item,cartPromotionItem);
            cartPromotionItem.setPromotionMessage("No discount");
            cartPromotionItem.setReduceAmount(new BigDecimal(0));
            PmsSkuStock skuStock = getOriginalPrice(promotionProduct,item.getProductSkuId());
            cartPromotionItem.setPrice(resolveOriginalPrice(item, promotionProduct, skuStock));
            if(skuStock!=null){
                cartPromotionItem.setRealStock(skuStock.getStock()-skuStock.getLockStock());
            } else {
                // 如果找不到SKU信息，设置默认库存
                cartPromotionItem.setRealStock(999999);
            }
            cartPromotionItem.setIntegration(promotionProduct.getGiftPoint());
            cartPromotionItem.setGrowth(promotionProduct.getGiftGrowth());
            cartPromotionItemList.add(cartPromotionItem);
        }
    }    /**
     * 对没有促销信息的商品进行处理
     */
    private void handleNoReduceByItems(List<CartPromotionItem> cartPromotionItemList, List<OmsCartItem> itemList) {
        for (OmsCartItem item : itemList) {
            System.out.println("=== 处理无促销商品: 商品ID=" + item.getProductId() + 
                             ", SKU ID=" + item.getProductSkuId() + 
                             ", 数量=" + item.getQuantity());
            
            CartPromotionItem cartPromotionItem = new CartPromotionItem();
            BeanUtils.copyProperties(item, cartPromotionItem);
            cartPromotionItem.setPromotionMessage("No discount");
            cartPromotionItem.setReduceAmount(new BigDecimal(0));
            // 由于没有促销信息，使用商品原价
            cartPromotionItem.setPrice(resolveOriginalPrice(item, null, null));
            // 设置一个足够大的库存值，避免库存检查失败
            // 实际库存检查应该在订单扣减库存时进行
            cartPromotionItem.setRealStock(999999); 
            cartPromotionItem.setIntegration(0);
            cartPromotionItem.setGrowth(0);
            cartPromotionItemList.add(cartPromotionItem);
        }
    }

    private PmsProductFullReduction getProductFullReduction(BigDecimal totalAmount,List<PmsProductFullReduction> fullReductionList) {
        //按条件从高到低排序
        fullReductionList.sort(new Comparator<PmsProductFullReduction>() {
            @Override
            public int compare(PmsProductFullReduction o1, PmsProductFullReduction o2) {
                return o2.getFullPrice().subtract(o1.getFullPrice()).intValue();
            }
        });
        for(PmsProductFullReduction fullReduction:fullReductionList){
            if(totalAmount.subtract(fullReduction.getFullPrice()).intValue()>=0){
                return fullReduction;
            }
        }
        return null;
    }

    /**
     * 获取打折优惠的促销信息
     */
    private String getLadderPromotionMessage(PmsProductLadder ladder) {
        StringBuilder sb = new StringBuilder();
        sb.append("打折优惠：");
        sb.append("满");
        sb.append(ladder.getCount());
        sb.append("件，");
        sb.append("打");
        sb.append(ladder.getDiscount().multiply(new BigDecimal(10)));
        sb.append("折");
        return sb.toString();
    }

    /**
     * 根据购买商品数量获取满足条件的打折优惠策略
     */
    private PmsProductLadder getProductLadder(int count, List<PmsProductLadder> productLadderList) {
        //按数量从大到小排序
        productLadderList.sort(new Comparator<PmsProductLadder>() {
            @Override
            public int compare(PmsProductLadder o1, PmsProductLadder o2) {
                return o2.getCount() - o1.getCount();
            }
        });
        for (PmsProductLadder productLadder : productLadderList) {
            if (count >= productLadder.getCount()) {
                return productLadder;
            }
        }
        return null;
    }

    /**
     * 获取购物车中指定商品的数量
     */
    private int getCartItemCount(List<OmsCartItem> itemList) {
        int count = 0;
        for (OmsCartItem item : itemList) {
            count += item.getQuantity();
        }
        return count;
    }    /**
     * 获取购物车中指定商品的总价
     */
    private BigDecimal getCartItemAmount(List<OmsCartItem> itemList, List<PromotionProduct> promotionProductList) {
        BigDecimal amount = new BigDecimal(0);
        for (OmsCartItem item : itemList) {
            //计算出商品原价
            PromotionProduct promotionProduct = getPromotionProductById(item.getProductId(), promotionProductList);
            if (promotionProduct != null) {
                PmsSkuStock skuStock = getOriginalPrice(promotionProduct,item.getProductSkuId());
                amount = amount.add(resolveOriginalPrice(item, promotionProduct, skuStock).multiply(new BigDecimal(item.getQuantity())));
            } else {
                // 如果没有促销信息，使用购物车中的价格
                amount = amount.add(resolveOriginalPrice(item, null, null).multiply(new BigDecimal(item.getQuantity())));
            }
        }
        return amount;
    }    /**
     * 获取商品的原价
     */
    private PmsSkuStock getOriginalPrice(PromotionProduct promotionProduct, Long productSkuId) {
        if (promotionProduct == null || promotionProduct.getSkuStockList() == null || productSkuId == null) {
            return null;
        }
        for (PmsSkuStock skuStock : promotionProduct.getSkuStockList()) {
            if (productSkuId.equals(skuStock.getId())) {
                return skuStock;
            }
        }
        return null;
    }

    private BigDecimal resolveOriginalPrice(OmsCartItem item, PromotionProduct promotionProduct, PmsSkuStock skuStock) {
        if (skuStock != null && skuStock.getPrice() != null) {
            return skuStock.getPrice();
        }
        if (item != null && item.getPrice() != null) {
            return item.getPrice();
        }
        if (promotionProduct != null && promotionProduct.getPrice() != null) {
            return promotionProduct.getPrice();
        }
        return new BigDecimal(0);
    }

    /**
     * 根据商品id获取商品的促销信息
     */
    private PromotionProduct getPromotionProductById(Long productId, List<PromotionProduct> promotionProductList) {
        for (PromotionProduct promotionProduct : promotionProductList) {
            if (productId.equals(promotionProduct.getId())) {
                return promotionProduct;
            }
        }
        return null;
    }
}
