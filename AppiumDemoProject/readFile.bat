@echo off
set file=C:\Users\jackd\eclipse-workspace\AppiumDemoProject\appiumurl.txt
for /f "tokens=*" %%x in (%file%) do (echo %%x)
pause