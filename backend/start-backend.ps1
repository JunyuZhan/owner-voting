param(
  [string]$env = "dev"
)

$cfg = "backend/config/env/application-$env.yml"

if (-Not (Test-Path $cfg)) {
  Write-Host "配置文件 $cfg 不存在，请检查环境名！"
  exit 1
}

Write-Host "使用配置文件: $cfg"
java -jar backend.jar --spring.config.location=$cfg --spring.profiles.active=$env 