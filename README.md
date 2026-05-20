# 校园流浪动物管理系统前端原型

当前项目前端已整理为 `Vue 3 + Vite` 结构，用于展示“发现广场、AI 随手拍、校园图鉴、我的”四个核心页面，并为后续对接 `Spring Boot + MySQL` 预留扩展空间。

## 运行方式

进入 `frontend` 目录后运行：

```bash
npm install
npm run dev
```

## 目录结构

```text
.
├── frontend/
│   ├── src/
│   │   ├── views/      # 四个主页面
│   │   ├── components/ # 公共组件
│   │   ├── stores/     # Pinia 状态
│   │   └── assets/     # 样式与图片资源
│   ├── package.json
│   └── vite.config.js
└── docs/               # 需求分析、原型规划与设计说明
```
