@echo off
echo Compiling Snake Game...
javac -d bin src\snake\*.java
if %ERRORLEVEL% equ 0 (
    echo Starting Snake Game...
    java -cp bin snake.Main
) else (
    echo Compilation failed.
    pause
)
