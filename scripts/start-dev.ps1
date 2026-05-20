$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ProjectDir = Resolve-Path (Join-Path $ScriptDir "..")
$BackendScript = Join-Path $ScriptDir "start-backend.cmd"
$FrontendScript = Join-Path $ScriptDir "start-frontend.cmd"

if (-not (Test-Path $BackendScript)) {
    Write-Error "找不到后端启动脚本: $BackendScript"
    exit 1
}

if (-not (Test-Path $FrontendScript)) {
    Write-Error "找不到前端启动脚本: $FrontendScript"
    exit 1
}

Write-Host "[INFO] Starting backend and frontend windows..." -ForegroundColor Cyan

Start-Process -FilePath "cmd.exe" -ArgumentList "/k", "`"$BackendScript`"" -WorkingDirectory $ProjectDir
Start-Process -FilePath "cmd.exe" -ArgumentList "/k", "`"$FrontendScript`"" -WorkingDirectory $ProjectDir

Write-Host "[INFO] Backend and frontend windows opened." -ForegroundColor Green
