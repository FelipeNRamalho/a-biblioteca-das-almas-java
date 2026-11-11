@echo off
cd /d "%~dp0"
if exist "dist\BibliotecaDasAlmas-Menu.jar" (
  java -jar "dist\BibliotecaDasAlmas-Menu.jar"
) else if exist "dist\biblioteca-das-almas-menu-1.0.0.jar" (
  java -jar "dist\biblioteca-das-almas-menu-1.0.0.jar"
)
if errorlevel 1 (
  echo.
  echo Erro: instale o Java 21 e certifique-se de que o comando java funciona no terminal.
  pause
)
