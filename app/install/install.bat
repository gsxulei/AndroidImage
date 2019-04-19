set buildType=%1
set apkName=%2
set applicationId=%3

::set projectPath=.\build\outputs\apk\debug\
::set apkName=%projectPath%app-debug.apk

echo buildType=%buildType%
echo apkName=%apkName%
echo applicationId=%applicationId%

::adb install -r %apkName%

if "%buildType%"=="debug" (
::adb install -r ..\build\outputs\apk\debug\app-debug.apk
adb install -r %apkName%
adb shell am start -a -n %applicationId%/app.MainActivity
) else (echo "release type do not install")