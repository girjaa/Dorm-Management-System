@echo off
if not defined JAVA_HOME (
    if exist "C:\Users\girja\.jdks\ms-21.0.12.1" (
        set "JAVA_HOME=C:\Users\girja\.jdks\ms-21.0.12.1"
    ) else if exist "C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.3\jbr" (
        set "JAVA_HOME=C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.3\jbr"
    )
)
set "PATH=%JAVA_HOME%\bin;%PATH%"

set "IDEA_MVN=C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.3\plugins\maven-plugin\lib\maven3\bin\mvn.cmd"
if exist "%IDEA_MVN%" (
    call "%IDEA_MVN%" %*
) else (
    mvn %*
)
