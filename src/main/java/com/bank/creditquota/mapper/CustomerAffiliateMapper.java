package com.bank.creditquota.mapper;

import com.bank.creditquota.entity.CustomerAffiliate;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface CustomerAffiliateMapper {
    int insert(CustomerAffiliate record);
    int updateById(CustomerAffiliate record);
    int deleteById(Long id);
    CustomerAffiliate selectById(Long id);
    List<CustomerAffiliate> selectAll();
    List<CustomerAffiliate> selectByCustomerId(@Param("customerId") String customerId);
    List<CustomerAffiliate> selectByAffiliateId(@Param("affiliateId") String affiliateId);
    List<CustomerAffiliate> selectByAffiliateType(@Param("affiliateType") String affiliateType);
}