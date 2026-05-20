@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "BACKEND_SCRIPT=%SCRIPT_DIR%start-backend.cmd"
set "FRONTEND_SCRIPT=%SCRIPT_DIR%start-frontend.cmd"

if not exist "%BACKEND_SCRIPT%" (
  echo [ERROR] Backend start script not found: %BACKEND_SCRIPT%
  pause
  exit /b 1
)

if not exist "%FRONTEND_SCRIPT%" (
  echo [ERROR] Frontend start script not found: %FRONTEND_SCRIPT%
  pause
  exit /b 1
)

echo [INFO] Opening backend and frontend windows...
start "Campus Animal Backend" cmd.exe /k ""%BACKEND_SCRIPT%""
start "Campus Animal Frontend" cmd.exe /k ""%FRONTEND_SCRIPT%""
echo [INFO] Backend and frontend windows opened.
