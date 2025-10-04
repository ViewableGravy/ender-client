# Quick Build Script (code only, faster)
# This only rebuilds the Java code, not resources
# Use this for quick testing when you haven't modified resources

Write-Host "Setting Java environment..." -ForegroundColor Cyan
$env:JAVA_HOME = "$env:USERPROFILE\scoop\apps\openjdk8-redhat\current"

Write-Host "Building JAR (code only)..." -ForegroundColor Cyan
& "$env:USERPROFILE\scoop\apps\ant\current\bin\ant.bat" jar

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed! Check the output above for errors." -ForegroundColor Red
    exit 1
}

Write-Host "Build successful! Starting game from build directory..." -ForegroundColor Green
Write-Host "WARNING: This may have resource errors. Use build-and-run.ps1 for full build." -ForegroundColor Yellow
Set-Location build
java -jar hafen.jar
