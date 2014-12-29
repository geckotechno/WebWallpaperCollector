rem @echo off
setlocal
set head=%~dp0
set head=%head:~0,-1%

if defined JAVA_HOME (
  set CMD=%JAVA_HOME%\bin\java.exe
) else (
  set CMD=C:\WINDOWS\system32\java.exe
)

if defined JAVA_HTTP_PROXY_OPTIONS goto cont
  rem set the proxy options if behine corp firewall
  rem example: -Dhttp.useProxy=true -Dhttp.proxyHost=web-proxy.abc.com -Dhttp.proxyPort=8080
  set JAVA_HTTP_PROXY_OPTIONS=
:cont

%CMD% %JAVA_HTTP_PROXY_OPTIONS% -classpath "%head%\out;%head%\lib\tika-app-1.3.jar" com.geckotechno.imagecollector.NgsPhotoOfTheDayExtractor


set /a count=0
for /F %%N in ('dir/s/b/a-d "%USERPROFILE%\Pictures\Wallpaper\Wallpaper-web\ngs-incoming" ^| find /c /v ""') do set count=%%N
echo count=%count%

if %count% GTR 1 (explorer "%USERPROFILE%\Pictures\Wallpaper\Wallpaper-web\ngs-incoming")

pause