@echo off
echo ====================================
echo      SISTEMA DE CHAT RMI
echo ====================================
echo.
echo Compilando classes Java...
javac *.java

if %ERRORLEVEL% NEQ 0 (
    echo ERRO: Falha na compilacao!
    pause
    exit /b 1
)

echo.
echo Compilacao concluida com sucesso!
echo.
echo Para executar:
echo   1. Servidor: java ServidorChat
echo   2. Cliente:  java ClienteChat
echo.
echo Pressione qualquer tecla para continuar...
pause > nul
