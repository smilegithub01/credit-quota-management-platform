package com.bank.creditquota.mapper;

import com.bank.creditquota.entity.CreditApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CreditApplicationMapper {
    int insert(CreditApplication creditApplication);
    
    int updateById(CreditApplication creditApplication);
    
    int deleteById(String applicationId);
    
    CreditApplication selectById(String applicationId);
    
    List<CreditApplication> selectAll();
    
    List<CreditApplication> selectByCustomerId(@Param("customerId") String customerId);
    
    List<CreditApplication> selectByApplicationType(@Param("applicationType") String applicationType);
    
    List<CreditApplication> selectByStatus(@Param("status") String status);
}