@echo off
echo =================================================================
echo   Compilando o APK Android do Sistema de Votacao via Docker
echo =================================================================
echo.
echo Iniciando container Docker para compilar o APK...
echo.

docker compose --profile apk run --rm apk-builder

echo.
if exist "cooperativa-votacao.apk" (
    echo =================================================================
    echo   [SUCESSO] APK gerado com sucesso na pasta principal!
    echo   Arquivo: %CD%\cooperativa-votacao.apk
    echo =================================================================
) else (
    echo [AVISO] Nao foi possivel encontrar cooperativa-votacao.apk na raiz.
)
echo.
pause
