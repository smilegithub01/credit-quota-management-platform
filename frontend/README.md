# 银行信贷额度管控平台前端

基于 Vue 3 + Element Plus + Vite 构建的银行信贷额度管控平台前端应用。

## 项目简介

这是一个为银行信贷额度管控平台开发的现代化前端应用，提供了完整的额度管理、风险管理、审批流程和系统管理等功能的用户界面。

## 技术栈

- **框架**: Vue 3
- **UI库**: Element Plus
- **构建工具**: Vite
- **状态管理**: Vuex
- **路由**: Vue Router
- **HTTP客户端**: Axios
- **图表库**: ECharts
- **样式**: Sass/SCSS

## 功能特性

- 客户信息管理
- 额度查询与管理
- 风险监控与预警
- 审批流程管理
- 系统配置管理
- 响应式设计
- 国际化支持
- 数据可视化

## 项目结构

```
src/
├── api/                 # API接口定义
├── assets/              # 静态资源
├── components/          # 公共组件
├── views/               # 页面视图
│   ├── dashboard/       # 仪表板
│   ├── customer/        # 客户管理
│   ├── quota/           # 额度管理
│   ├── risk/            # 风险管理
│   ├── approval/        # 审批流程
│   └── system/          # 系统管理
├── router/              # 路由配置
├── store/               # 状态管理
├── styles/              # 样式文件
├── utils/               # 工具函数
└── i18n/                # 国际化配置
```

## 安装与运行

### 环境要求

- Node.js >= 16.0.0
- npm 或 yarn

### 安装依赖

```bash
npm install
# 或
yarn install
```

### 开发环境运行

```bash
npm run dev
# 或
yarn dev
```

应用将在 `http://localhost:3000` 启动

### 生产环境构建

```bash
npm run build
# 或
yarn build
```

### 代码检查与格式化

```bash
# 代码检查
npm run lint
# 或
yarn lint

# 代码格式化
npm run format
# 或
yarn format
```

## 配置

### 环境变量

在项目根目录创建 `.env` 文件：

```env
# 开发环境API地址
VUE_APP_API_URL=http://localhost:8080/api/unified-credit

# 生产环境API地址
VUE_APP_API_URL_PROD=https://your-domain.com/api/unified-credit
```

### 代理配置

开发环境下，API请求会被代理到后端服务，配置在 `vite.config.js` 中：

```js
proxy: {
  '/api': {
    target: 'http://localhost:8080', // 后端API地址
    changeOrigin: true,
    secure: false,
    rewrite: (path) => path.replace(/^\/api/, '/api/unified-credit')
  }
}
```

## API接口规范

所有API请求都遵循统一的响应格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": "2026-02-04T10:30:00Z"
}
```

## 国际化

项目支持中英文切换，默认为中文，可通过以下方式切换：

- 在用户设置中选择语言
- 或直接修改localStorage中的lang值

## 部署

构建后的文件位于 `dist/` 目录，可部署到任何静态文件服务器。

## 贡献

欢迎提交 Issue 和 Pull Request 来改进项目。

## 许可证

Apache License 2.0