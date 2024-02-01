adb devices
adb kill-server 
adb start-server
start appium
timeout /t 10
adb shell input keyevent KEYCODE_WAKEUP
start mvn test -Dsurefire.suiteXmlFiles=src/test/resources/testng.xml >> "runningOutput.txt"

