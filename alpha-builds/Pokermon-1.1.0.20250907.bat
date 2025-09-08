@echo off
echo Starting Pokermon v1.1.0.20250907...
cd /d "%~dp0"
if exist "pokermon.jar" (
    java -jar pokermon.jar %*
) else (
    echo Error: pokermon.jar not found in the same directory
    pause
)
