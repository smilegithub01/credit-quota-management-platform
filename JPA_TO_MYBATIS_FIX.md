# JPA 到 MyBatis 修复说明

## 问题描述

GitHub Actions 构建失败，错误信息显示：
```
Error: /home/runner/work/credit-quota-management-platform/credit-quota-management-platform/src/main/java/com/bank/creditquota/repository/CustomerQuotaRepository.java:[4,47] package org.springframework.data.jpa.repository does not exist
Error: /home/runner/work/credit-quota-management-platform/credit-quota-management-platform/src/main/java/com/bank/creditquota/repository/QuotaTransactionRepository.java:[4,47] package org.springframework.data.jpa.repository does not exist
```

## 问题原因

项目使用了 **MyBatis** 作为 ORM 框架，但两个 Repository 文件却使用了 **JPA** 的注解和接口：
- `CustomerQuotaRepository.java` 使用了 `JpaRepository` 和 `@Query`
- `QuotaTransactionRepository.java` 使用了 `JpaRepository`

这是一个架构冲突：项目配置了 MyBatis，但代码却使用了 JPA。

## 解决方案

将两个 Repository 从 JPA 改为 MyBatis：

### 1. 修改 CustomerQuotaRepository

**修改前**：
```java
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface CustomerQuotaRepository extends JpaRepository<CustomerQuota, Long> {
    List<CustomerQuota> findByCustomerId(String customerId);
    Optional<CustomerQuota> findByCustomerIdAndQuotaTypeId(String customerId, Long quotaTypeId);
    
    @Query("SELECT SUM(cq.totalAmount) FROM CustomerQuota cq WHERE cq.customerId = ?1 AND cq.status = 'ACTIVE'")
    java.math.BigDecimal getTotalQuotaByCustomer(String customerId);
    
    @Query("SELECT SUM(cq.availableAmount) FROM CustomerQuota cq WHERE cq.customerId = ?1 AND cq.status = 'ACTIVE'")
    java.math.BigDecimal getAvailableQuotaByCustomer(String customerId);
}
```

**修改后**：
```java
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Repository
@Mapper
public interface CustomerQuotaRepository {
    int insert(CustomerQuota customerQuota);
    int updateById(CustomerQuota customerQuota);
    int deleteById(Long id);
    CustomerQuota selectById(Long id);
    List<CustomerQuota> selectAll();
    List<CustomerQuota> findByCustomerId(String customerId);
    Optional<CustomerQuota> findByCustomerIdAndQuotaTypeId(String customerId, Long quotaTypeId);
    BigDecimal getTotalQuotaByCustomer(@Param("customerId") String customerId);
    BigDecimal getAvailableQuotaByCustomer(@Param("customerId") String customerId);
}
```

### 2. 修改 QuotaTransactionRepository

**修改前**：
```java
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface QuotaTransactionRepository extends JpaRepository<QuotaTransaction, Long> {
    List<QuotaTransaction> findByCustomerId(String customerId);
    List<QuotaTransaction> findByCustomerQuotaId(Long customerQuotaId);
    List<QuotaTransaction> findByTransactionId(String transactionId);
}
```

**修改后**：
```java
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Repository
@Mapper
public interface QuotaTransactionRepository {
    int insert(QuotaTransaction quotaTransaction);
    int updateById(QuotaTransaction quotaTransaction);
    int deleteById(Long id);
    QuotaTransaction selectById(Long id);
    List<QuotaTransaction> selectAll();
    List<QuotaTransaction> findByCustomerId(@Param("customerId") String customerId);
    List<QuotaTransaction> findByCustomerQuotaId(@Param("customerQuotaId") Long customerQuotaId);
    List<QuotaTransaction> findByTransactionId(@Param("transactionId") String transactionId);
}
```

### 3. 创建 MyBatis XML 映射文件

#### CustomerQuotaRepository.xml
创建了 `src/main/resources/mapper/CustomerQuotaRepository.xml`，包含：
- 结果映射（ResultMap）
- 基础列列表（Base_Column_List）
- 插入、更新、删除、查询方法

#### QuotaTransactionRepository.xml
创建了 `src/main/resources/mapper/QuotaTransactionRepository.xml`，包含：
- 结果映射（ResultMap）
- 基础列列表（Base_Column_List）
- 插入、更新、删除、查询方法

## 架构一致性

现在项目架构保持一致：
- ✅ 使用 **MyBatis** 作为 ORM 框架
- ✅ 所有 Repository 都使用 MyBatis 注解
- ✅ 所有 SQL 查询都在 XML 文件中定义
- ✅ 避免了 JPA 和 MyBatis 的混合使用

## 验证步骤

### 1. 本地验证
```bash
# 编译项目
mvn clean compile -DskipTests

# 检查编译结果
ls -la target/classes/com/bank/creditquota/repository/
```

### 2. GitHub Actions 验证
- 推送代码到 main 分支
- 查看 GitHub Actions 运行结果
- 确认构建成功

### 3. 功能验证
```bash
# 运行应用
mvn spring-boot:run

# 测试 API 端点
curl http://localhost:8080/api/unified-credit/health
```

## 相关文件

### 修改的文件
1. `src/main/java/com/bank/creditquota/repository/CustomerQuotaRepository.java`
2. `src/main/java/com/bank/creditquota/repository/QuotaTransactionRepository.java`

### 新增的文件
1. `src/main/resources/mapper/CustomerQuotaRepository.xml`
2. `src/main/resources/mapper/QuotaTransactionRepository.xml`

## 注意事项

### 1. 方法命名
MyBatis 的方法命名与 JPA 不同：
- JPA: `findByCustomerId(String customerId)`
- MyBatis: `findByCustomerId(@Param("customerId") String customerId)`

### 2. 参数注解
MyBatis 需要使用 `@Param` 注解：
```java
List<QuotaTransaction> findByCustomerId(@Param("customerId") String customerId);
```

### 3. 返回类型
MyBatis 的返回类型需要在 XML 中定义：
```xml
<select id="findByCustomerId" parameterType="java.lang.String" resultMap="QuotaTransactionResultMap">
```

### 4. SQL 查询
MyBatis 的 SQL 查询在 XML 文件中定义，而不是使用注解：
```xml
<select id="getTotalQuotaByCustomer" resultType="java.math.BigDecimal">
    SELECT COALESCE(SUM(total_amount), 0)
    FROM customer_quotas
    WHERE customer_id = #{customerId} AND status = 'ACTIVE'
</select>
```

## 下一步

1. **验证构建**：查看 GitHub Actions 运行结果
2. **测试功能**：确保所有 API 端点正常工作
3. **文档更新**：更新项目文档说明架构选择
4. **代码审查**：检查是否有其他 JPA 相关代码需要清理

## 架构决策

### 为什么选择 MyBatis？
1. **SQL 控制**：可以完全控制 SQL 语句
2. **性能优化**：可以针对特定查询进行优化
3. **学习曲线**：相比 JPA，MyBatis 更容易理解和调试
4. **项目一致性**：项目已经使用了 MyBatis，保持架构统一

### 为什么不使用 JPA？
1. **复杂性**：JPA 的缓存和延迟加载可能引入复杂性
2. **SQL 控制**：JPA 的 SQL 生成可能不够灵活
3. **性能**：对于复杂查询，MyBatis 可能更高效
4. **团队熟悉度**：团队可能更熟悉 MyBatis

## 相关资源

- [MyBatis 官方文档](https://mybatis.org/mybatis-3/zh/index.html)
- [Spring Boot MyBatis 集成](https://mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/)
- [MyBatis XML 映射文件](https://mybatis.org/mybatis-3/zh/sqlmap-xml.html)