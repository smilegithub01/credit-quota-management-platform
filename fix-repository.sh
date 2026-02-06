#!/bin/bash

# 银行信贷额度管控平台 - Repository修复脚本

echo "=========================================="
echo "Repository修复脚本"
echo "=========================================="

# 检查Repository文件
echo "1. 检查Repository文件..."
REPO_FILES=$(find src -name "*Repository.java" -type f)
echo "找到Repository文件:"
echo "$REPO_FILES"
echo ""

# 检查JPA导入
echo "2. 检查JPA导入..."
JPA_IMPORTS=$(grep -r "org.springframework.data.jpa" src --include="*.java" | wc -l)
if [ "$JPA_IMPORTS" -gt 0 ]; then
    echo "❌ 发现JPA导入: $JPA_IMPORTS 处"
    grep -r "org.springframework.data.jpa" src --include="*.java"
else
    echo "✅ 未发现JPA导入"
fi
echo ""

# 检查MyBatis导入
echo "3. 检查MyBatis导入..."
MYBATIS_IMPORTS=$(grep -r "org.apache.ibatis" src --include="*.java" | wc -l)
if [ "$MYBATIS_IMPORTS" -gt 0 ]; then
    echo "✅ 发现MyBatis导入: $MYBATIS_IMPORTS 处"
else
    echo "❌ 未发现MyBatis导入"
fi
echo ""

# 检查XML映射文件
echo "4. 检查XML映射文件..."
XML_FILES=$(find src -name "*.xml" -type f | grep -i mapper)
if [ -n "$XML_FILES" ]; then
    echo "✅ 找到XML映射文件:"
    echo "$XML_FILES"
else
    echo "❌ 未找到XML映射文件"
fi
echo ""

# 检查pom.xml
echo "5. 检查pom.xml..."
if [ -f "pom.xml" ]; then
    echo "✅ pom.xml存在"
    
    # 检查JPA依赖
    JPA_DEPS=$(grep -i "jpa" pom.xml | wc -l)
    if [ "$JPA_DEPS" -gt 0 ]; then
        echo "❌ 发现JPA依赖: $JPA_DEPS 处"
        grep -i "jpa" pom.xml
    else
        echo "✅ 未发现JPA依赖"
    fi
    
    # 检查MyBatis依赖
    MYBATIS_DEPS=$(grep -i "mybatis" pom.xml | wc -l)
    if [ "$MYBATIS_DEPS" -gt 0 ]; then
        echo "✅ 发现MyBatis依赖: $MYBATIS_DEPS 处"
    else
        echo "❌ 未发现MyBatis依赖"
    fi
else
    echo "❌ pom.xml不存在"
fi
echo ""

# 检查Git状态
echo "6. 检查Git状态..."
if command -v git &> /dev/null; then
    echo "Git状态:"
    git status --short
    echo ""
    
    echo "最近提交:"
    git log --oneline -5
else
    echo "⚠️  Git未安装"
fi
echo ""

# 建议
echo "=========================================="
echo "修复建议"
echo "=========================================="
echo ""
echo "1. 如果发现JPA导入:"
echo "   - 手动修改Repository文件，移除JPA导入"
echo "   - 添加MyBatis导入: import org.apache.ibatis.annotations.Mapper;"
echo "   - 添加@Mapper注解到接口"
echo ""
echo "2. 如果发现JPA依赖:"
echo "   - 从pom.xml中移除JPA相关依赖"
echo "   - 确保MyBatis依赖正确配置"
echo ""
echo "3. 如果XML映射文件缺失:"
echo "   - 创建对应的XML文件"
echo "   - 确保命名空间正确"
echo ""
echo "4. 如果编译失败:"
echo "   - 尝试: mvn clean compile -DskipTests"
echo "   - 检查Java和Maven是否安装"
echo ""
echo "=========================================="
echo "完成"
echo "=========================================="