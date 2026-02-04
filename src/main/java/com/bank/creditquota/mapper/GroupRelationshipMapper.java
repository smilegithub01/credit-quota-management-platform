package com.bank.creditquota.mapper;

import com.bank.creditquota.entity.GroupRelationship;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface GroupRelationshipMapper {
    int insert(GroupRelationship groupRelationship);
    
    int updateById(GroupRelationship groupRelationship);
    
    int deleteById(Long id);
    
    GroupRelationship selectById(Long id);
    
    List<GroupRelationship> selectAll();
    
    List<GroupRelationship> selectByParentCustomerId(@Param("parentCustomerId") String parentCustomerId);
    
    List<GroupRelationship> selectByChildCustomerId(@Param("childCustomerId") String childCustomerId);
    
    List<GroupRelationship> selectByRelationshipType(@Param("relationshipType") String relationshipType);
    
    List<GroupRelationship> selectByStatus(@Param("status") String status);
}