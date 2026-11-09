package com.macro.mall.mapper;

import com.macro.mall.model.SmsEmailTemplate;
import com.macro.mall.model.SmsEmailTemplateExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SmsEmailTemplateMapper {
    long countByExample(SmsEmailTemplateExample example);

    int deleteByExample(SmsEmailTemplateExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SmsEmailTemplate record);

    int insertSelective(SmsEmailTemplate record);

    List<SmsEmailTemplate> selectByExampleWithBLOBs(SmsEmailTemplateExample example);

    List<SmsEmailTemplate> selectByExample(SmsEmailTemplateExample example);

    SmsEmailTemplate selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") SmsEmailTemplate record, @Param("example") SmsEmailTemplateExample example);

    int updateByExampleWithBLOBs(@Param("record") SmsEmailTemplate record, @Param("example") SmsEmailTemplateExample example);

    int updateByExample(@Param("record") SmsEmailTemplate record, @Param("example") SmsEmailTemplateExample example);

    int updateByPrimaryKeySelective(SmsEmailTemplate record);

    int updateByPrimaryKeyWithBLOBs(SmsEmailTemplate record);

    int updateByPrimaryKey(SmsEmailTemplate record);

    // 自定义查询方法
    List<SmsEmailTemplate> selectByPage(@Param("templateName") String templateName,
                                       @Param("triggerScene") Integer triggerScene,
                                       @Param("status") Integer status);

    Long countByCondition(@Param("templateName") String templateName,
                         @Param("triggerScene") Integer triggerScene,
                         @Param("status") Integer status);

    List<SmsEmailTemplate> selectByTriggerSceneAndStatus(@Param("triggerScene") Integer triggerScene, 
                                                        @Param("status") Integer status);
}
