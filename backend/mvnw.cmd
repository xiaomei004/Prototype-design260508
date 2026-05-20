@ECHO OFF
SETLOCAL

SET BASE_DIR=%~dp0
IF "%BASE_DIR:~-1%"=="\" SET BASE_DIR=%BASE_DIR:~0,-1%
SET WRAPPER_DIR=%BASE_DIR%\.mvn\wrapper
SET WRAPPER_JAR=%WRAPPER_DIR%\maven-wrapper.jar
SET DOWNLOADER_JAVA=%WRAPPER_DIR%\MavenWrapperDownloader.java
SET DOWNLOADER_CLASS=%WRAPPER_DIR%\MavenWrapperDownloader.class

IF NOT EXIST "%WRAPPER_JAR%" (
  ECHO Maven wrapper jar not found, downloading...
  PUSHD "%WRAPPER_DIR%"
  IF DEFINED JAVA_HOME (
    "%JAVA_HOME%\bin\javac.exe" MavenWrapperDownloader.java
    "%JAVA_HOME%\bin\java.exe" -cp . MavenWrapperDownloader "%BASE_DIR%"
  ) ELSE (
    javac MavenWrapperDownloader.java
    java -cp . MavenWrapperDownloader "%BASE_DIR%"
  )
  IF EXIST "%DOWNLOADER_CLASS%" DEL /Q "%DOWNLOADER_CLASS%"
  POPD
)

IF DEFINED JAVA_HOME (
  SET JAVA_EXE=%JAVA_HOME%\bin\java.exe
) ELSE (
  SET JAVA_EXE=java
)

"%JAVA_EXE%" -classpath "%WRAPPER_JAR%" "-Dmaven.multiModuleProjectDirectory=%BASE_DIR%" org.apache.maven.wrapper.MavenWrapperMain %*
