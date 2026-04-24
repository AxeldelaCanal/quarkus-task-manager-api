@REM Apache Maven Wrapper startup batch script
@REM Licensed to the Apache Software Foundation (ASF)

@SETLOCAL EnableDelayedExpansion

@SET MAVEN_PROJECTBASEDIR=%~dp0
@IF "%MAVEN_PROJECTBASEDIR:~-1%"=="\" SET MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%

@SET WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar
@SET WRAPPER_PROPS=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties

@IF NOT EXIST "%WRAPPER_JAR%" (
    @REM tokens=1,* captures everything after the first = sign (needed for URLs)
    @FOR /F "usebackq tokens=1,* delims==" %%A IN ("%WRAPPER_PROPS%") DO (
        IF "%%A"=="wrapperUrl" SET WRAPPER_DOWNLOAD_URL=%%B
    )
    @REM Use !...! delayed expansion — %...% expands at parse time (empty inside blocks)
    @powershell -Command "(New-Object System.Net.WebClient).DownloadFile('!WRAPPER_DOWNLOAD_URL!', '%WRAPPER_JAR%')"
    @IF ERRORLEVEL 1 (
        ECHO ERROR: Failed to download Maven Wrapper JAR from !WRAPPER_DOWNLOAD_URL!
        EXIT /B 1
    )
)

@SET JAVA_EXE=java.exe
@IF NOT "%JAVA_HOME%"=="" SET JAVA_EXE=%JAVA_HOME%\bin\java.exe

@"%JAVA_EXE%" ^
    -classpath "%WRAPPER_JAR%" ^
    org.apache.maven.wrapper.MavenWrapperMain ^
    -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%" ^
    %*

@IF ERRORLEVEL 1 EXIT /B %ERRORLEVEL%
