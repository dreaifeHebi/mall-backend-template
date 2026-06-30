package com.macro.mall.mapper;

import com.macro.mall.model.CmsBanner;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 轮播图Mapper
 * Created on 2025-10-12
 */
@Mapper
public interface CmsBannerMapper {

    /**
     * 查询所有启用的轮播图（按排序降序）
     */
    @Select("SELECT * FROM cms_banner WHERE status = 1 ORDER BY sort DESC, id ASC LIMIT 5")
    List<CmsBanner> selectEnabledBanners();

    /**
     * 分页查询轮播图
     */
    @Select("SELECT * FROM cms_banner ORDER BY sort DESC, id DESC LIMIT #{limit} OFFSET #{offset}")
    List<CmsBanner> selectByPage(@Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 查询总数
     */
    @Select("SELECT COUNT(*) FROM cms_banner")
    Long countTotal();

    /**
     * 根据ID查询
     */
    @Select("SELECT * FROM cms_banner WHERE id = #{id}")
    CmsBanner selectById(@Param("id") Long id);

    /**
     * 插入轮播图
     */
    @Insert("INSERT INTO cms_banner(title, image_url, link_url, sort, status) " +
            "VALUES(#{title}, #{imageUrl}, #{linkUrl}, #{sort}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CmsBanner banner);

    /**
     * 更新轮播图
     */
    @Update("UPDATE cms_banner SET title = #{title}, image_url = #{imageUrl}, " +
            "link_url = #{linkUrl}, sort = #{sort}, status = #{status}, update_time = CURRENT_TIMESTAMP WHERE id = #{id}")
    int updateById(CmsBanner banner);

    /**
     * 删除轮播图
     */
    @Delete("DELETE FROM cms_banner WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    /**
     * 更新状态
     */
    @Update("UPDATE cms_banner SET status = #{status}, update_time = CURRENT_TIMESTAMP WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
