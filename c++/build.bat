@ECHO OFF
:: mingw32-g++ -Os -v main.cpp -o main.exe 
x86_64-w64-mingw32-c++.exe -O3 -v main.cpp -o main.exe 
echo.
echo ==================== STARTING ====================
echo.
main.exe