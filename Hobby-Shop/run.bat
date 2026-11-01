@echo off
REM Compile and run Aetherion from the Hobby-Shop folder (needs a JDK, Java 17+).
cd /d "%~dp0"
if not exist build mkdir build
javac -d build src\hobbyshop\*.java || exit /b 1
java -cp build hobbyshop.HobbyShop
pause
