@echo off
setlocal
set head=%~dp0
set head=%head:~0,-1%

if defined JAVA_HOME goto cont
  echo "ERROR: JAVA_HOME not defined"
  exit
:cont

cd %head%

%JAVA_HOME%\bin\javac.exe -sourcepath src -d out -classpath "lib\tika-app-1.3.jar" src\com\geckotechno\imagecollector\*.java