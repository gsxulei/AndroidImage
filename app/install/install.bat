set buildType=%1
set apkName=%2

::set projectPath=.\build\outputs\apk\debug\
::set apkName=%projectPath%app-debug.apk

echo buildType=%buildType%

echo apkName=%apkName%

::adb install -r %apkName%

if "%buildType%"=="debug" (
::adb install -r ..\build\outputs\apk\debug\app-debug.apk
adb install -r %apkName%
adb shell am start -a -n com.x62.simple/.MainActivity
) else (echo "release type do not install")