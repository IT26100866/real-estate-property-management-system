@echo off
title EstateX - Spring Boot Backend Runner
echo ========================================================
echo   Starting EstateX Spring Boot Backend (Port 8081)...
echo ========================================================

REM Set Java 21 environment
set "JAVA_HOME=C:\Program Files\Java\jdk-21"
set "PATH=%JAVA_HOME%\bin;%PATH%"

REM Use IntelliJ bundled Maven if system maven is not in PATH
set "MVN_CMD=C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2025.2\plugins\maven\lib\maven3\bin\mvn.cmd"

if exist "%MVN_CMD%" (
    echo Using Maven from: %MVN_CMD%
    "%MVN_CMD%" spring-boot:run
) else (
    echo Falling back to system mvn...
    mvn spring-boot:run
)

pause
