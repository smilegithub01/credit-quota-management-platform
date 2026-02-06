# GitHub Pages 部署指南

## 概述
本项目支持通过GitHub Actions自动部署前端应用到GitHub Pages，并通过GitHub Actions运行后端服务。

## 部署架构

### 前端部署（GitHub Pages）
- **源码位置**: `frontend/` 目录
- **部署分支**: `gh-pages`
- **访问地址**: `https://<username>.github.io/credit-quota-management-platform/`
- **自定义域名**: 可配置为 `credit-quota-management.example.com`

### 后端部署（GitHub Actions）
- **构建流程**: 自动构建JAR包
- **测试环境**: 在GitHub Actions中运行MySQL和Redis
- **部署方式**: 可部署到Vercel、Heroku或自建服务器

## 部署步骤

### 第一步：启用GitHub Pages

1. 进入GitHub仓库设置
2. 点击左侧的 "Pages"
3. 在 "Source" 下拉菜单中选择 "gh-pages" 分支
4. 点击 "Save"

### 第二步：配置自定义域名（可选）

1. 在GitHub Pages设置中输入自定义域名
2. 在域名提供商处添加CNAME记录：
   - 记录类型: CNAME
   - 主机名: credit-quota-management
   - 值: `<username>.github.io`

### 第三步：触发自动部署

1. 推送代码到main分支
2. GitHub Actions会自动构建并部署到gh-pages分支
3. 等待几分钟后访问部署地址

## 手动部署

### 前端手动部署

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 构建项目
npm run build

# 部署到GitHub Pages（需要安装gh-pages包）
npx gh-pages -d dist
```

### 后端手动部署

```bash
# 构建项目
mvn clean package -DskipTests

# 部署到Vercel（需要安装Vercel CLI）
vercel --prod

# 或者部署到Heroku
heroku deploy:jar target/credit-quota-management-platform-1.0.0.jar
```

## 环境变量配置

### 前端环境变量

在 `frontend/.env.production` 文件中配置：

```env
VUE_APP_API_URL=https://api.credit-quota-management.example.com
VUE_APP_DEBUG=false
```

### GitHub Actions环境变量

在GitHub仓库的Settings -> Secrets and variables -> Actions中添加：

- `VERCEL_TOKEN`: Vercel部署令牌
- `HEROKU_API_KEY`: Heroku API密钥
- `CUSTOM_DOMAIN`: 自定义域名

## 访问地址

### 前端应用
- **GitHub Pages**: `https://<username>.github.io/credit-quota-management-platform/`
- **自定义域名**: `https://credit-quota-management.example.com`

### 后端API
- **Vercel**: `https://credit-quota-management.vercel.app`
- **Heroku**: `https://credit-quota-management.herokuapp.com`
- **自建服务器**: `https://api.credit-quota-management.example.com`

## 配置示例

### GitHub Actions配置

项目已包含完整的GitHub Actions工作流文件：

1. `.github/workflows/deploy.yml` - 完整的CI/CD流程
2. `frontend/.github/workflows/deploy-frontend.yml` - 前端部署流程

### 自定义域名配置

如果需要自定义域名，请在域名提供商处添加以下DNS记录：

```
# CNAME记录（用于GitHub Pages）
credit-quota-management.example.com  CNAME  <username>.github.io

# A记录（用于自建服务器）
api.credit-quota-management.example.com  A  <服务器IP地址>
```

## 故障排查

### 前端部署失败

1. **检查构建日志**: 在GitHub仓库的Actions标签页查看
2. **检查依赖**: 确保package.json文件正确
3. **检查路径**: 确保dist目录存在且包含index.html

### 后端部署失败

1. **检查环境变量**: 确保所有必需的环境变量已配置
2. **检查数据库连接**: 确保数据库服务正常运行
3. **检查端口**: 确保端口未被占用

### 自定义域名无法访问

1. **DNS传播**: 等待DNS传播完成（通常需要几分钟到几小时）
2. **CNAME配置**: 确保CNAME记录正确配置
3. **SSL证书**: GitHub Pages会自动为自定义域名生成SSL证书

## 性能优化

### 前端优化

1. **代码分割**: 使用Vue的异步组件
2. **图片优化**: 使用WebP格式和懒加载
3. **CDN加速**: 使用GitHub Pages的全球CDN

### 后端优化

1. **缓存策略**: 使用Redis缓存频繁访问的数据
2. **数据库优化**: 添加索引和查询优化
3. **负载均衡**: 使用多个实例分担流量

## 监控和维护

### 前端监控

1. **Google Analytics**: 集成网站分析
2. **错误监控**: 使用Sentry或类似服务
3. **性能监控**: 使用Lighthouse进行性能测试

### 后端监控

1. **健康检查**: `/actuator/health` 端点
2. **日志监控**: 集成日志服务
3. **性能指标**: `/actuator/metrics` 端点

## 安全考虑

### 前端安全

1. **CORS配置**: 正确配置跨域资源共享
2. **API密钥**: 不要在前端代码中硬编码敏感信息
3. **HTTPS**: 确保使用HTTPS协议

### 后端安全

1. **认证授权**: 实现用户认证和权限控制
2. **输入验证**: 对所有输入进行验证和清理
3. **SQL注入**: 使用参数化查询防止SQL注入

## 扩展功能

### 可选功能

1. **实时更新**: 使用WebSocket实现实时数据更新
2. **移动端适配**: 响应式设计支持移动端
3. **多语言支持**: 支持中英文切换
4. **数据导出**: 支持Excel/CSV格式导出

### 集成服务

1. **邮件通知**: 集成邮件服务发送通知
2. **短信验证**: 集成短信服务进行身份验证
3. **文件存储**: 集成云存储服务存储文件

## 技术栈

### 前端技术栈
- Vue 3
- Element Plus UI组件库
- Vue Router路由管理
- Axios HTTP客户端
- ECharts图表库

### 后端技术栈
- Spring Boot 3.2
- MyBatis ORM框架
- MySQL 8.0数据库
- Redis缓存
- Maven构建工具

## 支持和反馈

如有问题或建议，请通过以下方式联系：
- GitHub Issues: 提交问题
- 邮件: support@example.com
- 文档: 查看项目文档