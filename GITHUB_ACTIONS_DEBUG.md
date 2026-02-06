# GitHub Actions 调试指南

## 常见错误及解决方案

### 1. MySQL连接失败

**错误表现**：
```
ERROR 2003 (HY000): Can't connect to MySQL server on '127.0.0.1'
```

**解决方案**：
- 在GitHub Actions中，MySQL服务需要等待启动
- 在连接前添加等待时间：`sleep 10`
- 确保MySQL服务健康检查通过

### 2. 前端目录不存在

**错误表现**：
```
cd: frontend: No such file or directory
```

**解决方案**：
- 检查frontend目录是否正确提交到仓库
- 确保package.json文件存在
- 添加条件判断，避免目录不存在时的错误

### 3. Node.js依赖安装失败

**错误表现**：
```
npm ERR! code E404
npm ERR! 404 Not Found
```

**解决方案**：
- 使用`--legacy-peer-deps`参数
- 确保package-lock.json文件存在
- 检查网络连接

### 4. Maven构建失败

**错误表现**：
```
[ERROR] Failed to execute goal
```

**解决方案**：
- 检查pom.xml文件是否正确
- 确保所有依赖项可用
- 清理本地缓存：`mvn clean`

## 调试步骤

### 第一步：检查工作流配置

1. 确认工作流文件位置正确：`.github/workflows/`
2. 检查触发条件：`on: push: branches: [ main ]`
3. 验证工作流语法：使用GitHub Actions编辑器

### 第二步：检查项目结构

```bash
# 检查frontend目录
ls -la frontend/

# 检查package.json
cat frontend/package.json

# 检查Java项目结构
ls -la src/main/java/
```

### 第三步：本地测试

```bash
# 测试Java构建
mvn clean compile -DskipTests

# 测试前端构建（如果存在）
cd frontend
npm install
npm run build
```

### 第四步：查看GitHub Actions日志

1. 访问GitHub仓库的Actions页面
2. 点击失败的工作流运行
3. 查看具体步骤的日志
4. 根据错误信息进行修复

## 优化建议

### 1. 使用缓存

```yaml
- name: Cache Maven dependencies
  uses: actions/cache@v3
  with:
    path: ~/.m2/repository
    key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
    restore-keys: ${{ runner.os }}-m2
```

### 2. 分阶段构建

将构建过程分为多个job，提高效率：
- 第一个job：构建Java项目
- 第二个job：构建前端项目
- 第三个job：部署

### 3. 使用矩阵构建

支持多个Java版本或操作系统：

```yaml
strategy:
  matrix:
    java-version: [17, 21]
    os: [ubuntu-latest, windows-latest]
```

### 4. 添加条件判断

```yaml
- name: Build frontend
  run: |
    if [ -d "frontend" ]; then
      cd frontend
      npm install
      npm run build
    fi
```

## 监控和告警

### 1. 设置通知

在GitHub仓库设置中配置：
- Actions失败时发送邮件通知
- 配置Slack或Teams通知

### 2. 定期清理

```yaml
- name: Cleanup
  run: |
    # 清理临时文件
    rm -rf target/
    rm -rf frontend/node_modules/
```

### 3. 性能监控

监控构建时间：
- 使用GitHub Actions的性能指标
- 优化构建步骤顺序
- 减少不必要的步骤

## 最佳实践

### 1. 保持工作流简洁

- 每个工作流文件专注于一个任务
- 避免在单个工作流中做太多事情
- 使用可重用的工作流

### 2. 使用官方Actions

- 优先使用GitHub官方提供的Actions
- 使用经过验证的第三方Actions
- 避免使用不维护的Actions

### 3. 版本控制

- 将工作流文件纳入版本控制
- 使用语义化版本号
- 记录工作流变更

### 4. 测试工作流

- 在开发分支测试工作流
- 使用`workflow_dispatch`手动触发
- 定期验证工作流功能

## 故障排查清单

- [ ] 工作流文件是否在正确的位置？
- [ ] 触发条件是否正确配置？
- [ ] 项目结构是否完整？
- [ ] 依赖项是否可用？
- [ ] 环境变量是否正确设置？
- [ ] 权限是否足够？
- [ ] 网络连接是否正常？
- [ ] 资源限制是否合理？

## 相关资源

- [GitHub Actions官方文档](https://docs.github.com/en/actions)
- [GitHub Actions最佳实践](https://docs.github.com/en/actions/learn-github-actions/best-practices-for-github-actions)
- [GitHub Actions示例](https://github.com/actions/starter-workflows)