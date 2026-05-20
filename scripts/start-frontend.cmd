@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "PROJECT_DIR=%SCRIPT_DIR%.."
set "FRONTEND_DIR=%PROJECT_DIR%\frontend"

if not exist "%FRONTEND_DIR%\package.json" (
  echo [ERROR] Frontend project not found: %FRONTEND_DIR%
  pause
  exit /b 1
)

echo [INFO] Checking frontend dependencies...
if not exist "%FRONTEND_DIR%\node_modules" (
  cd /d "%FRONTEND_DIR%"
  call npm install
) else (
  cd /d "%FRONTEND_DIR%"
)

echo [INFO] Starting frontend service...
call npm run dev
if errorlevel 1 pause
