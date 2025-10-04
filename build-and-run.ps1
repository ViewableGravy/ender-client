# Build and Run Script for Ender Client
# This script builds the entire project and runs it from the bin directory

Write-Host "Setting Java environment..." -ForegroundColor Cyan
$env:JAVA_HOME = "$env:USERPROFILE\scoop\apps\openjdk8-redhat\current"

Write-Host "Building project..." -ForegroundColor Cyan
& "$env:USERPROFILE\scoop\apps\ant\current\bin\ant.bat" bin

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed! Check the output above for errors." -ForegroundColor Red
    exit 1
}

Write-Host "Build successful! Starting game..." -ForegroundColor Green
Set-Location bin
java -jar hafen.jar
