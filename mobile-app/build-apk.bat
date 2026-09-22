@echo off
echo ===================================================
echo   Compilando APK - Cooperativa Votacao Mobile
echo ===================================================

cd /d "%~dp0\android"

if exist gradlew.bat (
    echo Executando build do APK via Gradle Wrapper...
    call gradlew.bat assembleDebug
) else (
    echo Gradle Wrapper nao encontrado. Abra a pasta 'android' no Android Studio para gerar o APK.
)

echo ===================================================
echo Concluido! O APK estara em: android\app\build\outputs\apk\debug\
echo ===================================================
pause
