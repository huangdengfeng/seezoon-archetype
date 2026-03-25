# 项目概述

## 目的

企业级 SaaS 平台,提供项目管理和团队协作功能。

## 技术栈

- **前端**: React 18, TypeScript, Tailwind CSS, Vite
- **后端**: Node.js 20, Express, TypeScript
- **数据库**: PostgreSQL 15, Redis 7
- **认证**: JWT + refresh tokens, OAuth 2.0
- **部署**: Docker, AWS ECS, CloudFront CDN

## 代码约定

- 使用 ESLint + Prettier
- 命名: camelCase (变量/函数), PascalCase (组件/类)
- 文件名: kebab-case.tsx
- 最大函数长度: 50 行
- 优先使用函数式组件和 hooks

## 架构原则

- 分层架构: routes → controllers → services → repositories
- API 遵循 RESTful 规范
- 前端使用 feature-based 文件组织
- 共享类型定义在 `@types/` 目录

## 测试要求

- 单元测试覆盖率 ≥ 80%
- 关键路径必须有集成测试
- 使用 Vitest (后端) 和 React Testing Library (前端)

## 安全标准

- 所有用户输入必须验证和清理
- 敏感数据使用 AES-256 加密
- API 限流: 100 请求/分钟/用户
- 密码最小 12 字符,必须包含大小写字母+数字+符号

## 性能目标

- 首屏加载 < 2 秒
- API 响应时间 < 200ms (P95)
- 数据库查询优化,避免 N+1 问题
