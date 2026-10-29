@echo off
cd /d "%~dp0"
java -jar "dist\BibliotecaDasAlmas-Menu.jar"
if errorlevel 1 (
  echo.
  echo Erro: instale o Java 21 e certifique-se de que o comando java funciona no terminal.
  pause
)
