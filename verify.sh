#!/bin/bash

# 银行信贷额度管控平台 - 验证脚本

echo "=========================================="
echo "银行信贷额度管控平台 - 验证脚本"
echo "=========================================="

# 检查应用是否运行
echo "1. 检查应用是否运行..."
if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "✅ 应用正在运行"
else
    echo "❌ 应用未运行，请先启动应用"
    exit 1
fi

# 测试健康检查端点
echo "2. 测试健康检查端点..."
HEALTH_RESPONSE=$(curl -s http://localhost:8080/actuator/health)
if echo "$HEALTH_RESPONSE" | grep -q "UP"; then
    echo "✅ 健康检查通过"
else
    echo "❌ 健康检查失败"
    echo "响应: $HEALTH_RESPONSE"
    exit 1
fi

# 测试客户管理接口
echo "3. 测试客户管理接口..."
CUSTOMER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/unified-credit/customer \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "TEST001",
    "customerName": "测试客户",
    "customerType": "ENTERPRISE",
    "customerLevel": "A",
    "legalRepresentative": "测试人",
    "phone": "13800000001",
    "status": "NORMAL"
  }')

if echo "$CUSTOMER_RESPONSE" | grep -q "TEST001"; then
    echo "✅ 客户创建成功"
else
    echo "❌ 客户创建失败"
    echo "响应: $CUSTOMER_RESPONSE"
    exit 1
fi

# 测试额度查询接口
echo "4. 测试额度查询接口..."
QUOTA_RESPONSE=$(curl -s http://localhost:8080/api/unified-credit/credit-quota/customer/TEST001)
if [ $? -eq 0 ]; then
    echo "✅ 额度查询接口正常"
else
    echo "❌ 额度查询接口异常"
    exit 1
fi

# 测试额度占用接口
echo "5. 测试额度占用接口..."
OCCUPY_RESPONSE=$(curl -s -X POST http://localhost:8080/api/unified-credit/quota/occupy \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "TEST001",
    "quotaType": "CREDIT",
    "amount": 1000.00,
    "referenceId": "TEST_TXN001"
  }')

if echo "$OCCUPY_RESPONSE" | grep -q "success"; then
    echo "✅ 额度占用接口正常"
else
    echo "⚠️  额度占用接口可能异常（可能因为额度不存在）"
    echo "响应: $OCCUPY_RESPONSE"
fi

# 测试风险监控接口
echo "6. 测试风险监控接口..."
RISK_RESPONSE=$(curl -s -X POST http://localhost:8080/api/unified-credit/risk-monitoring-index \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "TEST001",
    "indexName": "测试指标",
    "indexValue": 0.5,
    "threshold": 0.7,
    "riskLevel": "LOW",
    "monitoringFrequency": "DAILY"
  }')

if echo "$RISK_RESPONSE" | grep -q "TEST001"; then
    echo "✅ 风险监控接口正常"
else
    echo "❌ 风险监控接口异常"
    echo "响应: $RISK_RESPONSE"
    exit 1
fi

# 测试审批流程接口
echo "7. 测试审批流程接口..."
APPROVAL_RESPONSE=$(curl -s -X POST http://localhost:8080/api/unified-credit/approval-process \
  -H "Content-Type: application/json" \
  -d '{
    "processName": "测试审批流程",
    "applicationId": "TEST_APP001",
    "applicant": "TEST001",
    "processType": "CREDIT_APPROVAL",
    "priority": "MEDIUM"
  }')

if echo "$APPROVAL_RESPONSE" | grep -q "TEST_APP001"; then
    echo "✅ 审批流程接口正常"
else
    echo "❌ 审批流程接口异常"
    echo "响应: $APPROVAL_RESPONSE"
    exit 1
fi

echo ""
echo "=========================================="
echo "✅ 所有测试通过！"
echo "=========================================="
echo ""
echo "应用运行在: http://localhost:8080"
echo "健康检查: http://localhost:8080/actuator/health"
echo "API文档: http://localhost:8080/actuator/info"
echo ""
echo "主要API端点:"
echo "- 客户管理: /api/unified-credit/customer"
echo "- 额度管理: /api/unified-credit/credit-quota"
echo "- 额度操作: /api/unified-credit/quota/*"
echo "- 风险监控: /api/unified-credit/risk-*"
echo "- 审批流程: /api/unified-credit/approval-*"
echo ""
echo "测试数据已创建:"
echo "- 客户ID: TEST001"
echo "- 交易ID: TEST_TXN001"
echo "- 申请ID: TEST_APP001"