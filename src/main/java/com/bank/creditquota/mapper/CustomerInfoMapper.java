package com.bank.creditquota.mapper;

import com.bank.creditquota.entity.CustomerInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CustomerInfoMapper {
    int insert(CustomerInfo customerInfo);
    
    int updateById(CustomerInfo customerInfo);
    
    int deleteById(String customerId);
    
    CustomerInfo selectById(String customerId);
    
    List<CustomerInfo> selectAll();
    
    List<CustomerInfo> selectByCustomerName(@Param("customerName") String customerName);
    
    List<CustomerInfo> selectByCustomerType(@Param("customerType") String customerType);
    
    List<CustomerInfo> selectByStatus(@Param("status") String status);
}