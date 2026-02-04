package com.bank.creditquota.mapper;

import com.bank.creditquota.entity.Customer;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CustomerMapper {
    int insert(Customer customer);
    
    int updateById(Customer customer);
    
    int deleteById(String customerId);
    
    Customer selectById(String customerId);
    
    List<Customer> selectAll();
    
    List<Customer> selectByParentId(String parentCustomerId);
    
    List<Customer> selectByType(Integer customerType);
}