package com.bank.creditquota.mapper;

import com.bank.creditquota.entity.QuotaType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface QuotaTypeMapper {
    int insert(QuotaType quotaType);
    
    int updateById(QuotaType quotaType);
    
    int deleteById(Long id);
    
    QuotaType selectById(Long id);
    
    List<QuotaType> selectAll();
    
    QuotaType selectByName(@Param("quotaTypeName") String quotaTypeName);
}