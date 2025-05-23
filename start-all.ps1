param(
  [string]$env = "dev"
)

Write-Host "启动后端（环境：$env）..."
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd backend; ./start-backend.ps1 $env"

Write-Host "启动前端开发环境..."
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd frontend; npm run dev" 