@echo off
REM =============================================
REM Selenium Grid Auto Start Script
REM =============================================

REM Change directory to Selenium Grid folder
cd /d C:\selenium-grid

REM Start Hub
echo Starting Selenium Hub on port 4444...
start cmd /k "java -jar selenium-server-4.39.0.jar hub --port 4444"

REM Wait a few seconds for Hub to start
timeout /t 5

REM Start Nodes
echo Starting Node 1 on port 5555...
start cmd /k "java -jar selenium-server-4.39.0.jar node --hub http://localhost:4444 --port 5555 --max-sessions 4"

echo Starting Node 2 on port 5556...
start cmd /k "java -jar selenium-server-4.39.0.jar node --hub http://localhost:4444 --port 5556 --max-sessions 4"

echo Starting Node 3 on port 5557...
start cmd /k "java -jar selenium-server-4.39.0.jar node --hub http://localhost:4444 --port 5557 --max-sessions 4"

echo Starting Node 4 on port 5558...
start cmd /k "java -jar selenium-server-4.39.0.jar node --hub http://localhost:4444 --port 5558 --max-sessions 4"

echo Starting Node 5 on port 5559...
start cmd /k "java -jar selenium-server-4.39.0.jar node --hub http://localhost:4444 --port 5559 --max-sessions 4"

echo Selenium Grid Hub and Nodes started!
pause
