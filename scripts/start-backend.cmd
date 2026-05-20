@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "PROJECT_DIR=%SCRIPT_DIR%.."
set "BACKEND_DIR=%PROJECT_DIR%\backend"
set "JAR_NAME=campus-animal-backend-0.0.1-SNAPSHOT.jar"
set "JAR_PATH=%BACKEND_DIR%\target\%JAR_NAME%"

if not exist "%BACKEND_DIR%\pom.xml" (
  echo [ERROR] Backend project not found: %BACKEND_DIR%
  pause
  exit /b 1
)

echo [INFO] Packaging backend application...
cd /d "%BACKEND_DIR%"
call mvnw.cmd -q -DskipTests package
if errorlevel 1 (
  echo [ERROR] Backend packaging failed.
  pause
  exit /b 1
)

if not exist "%JAR_PATH%" (
  echo [ERROR] Backend jar not found: %JAR_PATH%
  pause
  exit /b 1
)

echo [INFO] Starting backend jar...
java -jar "%JAR_PATH%"
if errorlevel 1 pause
