# 启动脚本说明

## 单独启动后端

在项目根目录执行：

```powershell
.\scripts\start-backend.cmd
```

## 单独启动前端

在项目根目录执行：

```powershell
.\scripts\start-frontend.cmd
```

## 同时启动前后端

在项目根目录执行：

```powershell
.\scripts\start-dev.cmd
```

或：

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\start-dev.ps1
```

## 默认行为

- 后端脚本会进入 `backend` 目录，先执行 `mvnw.cmd -DskipTests package`，再运行生成的 jar
- 前端脚本会进入 `frontend` 目录
- 如果前端缺少 `node_modules`，会先自动执行 `npm install`
- `start-dev` 会打开两个独立命令窗口，分别运行前端和后端
