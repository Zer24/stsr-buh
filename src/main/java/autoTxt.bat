@echo off
set "output=all_java_files.txt"
echo Объединение всех .java файлов... > "%output%"
echo ============================================== >> "%output%"
echo. >> "%output%"

for /r %%i in (*.java) do (
    echo. >> "%output%"
    echo ========================================== >> "%output%"
    echo Файл: %%i >> "%output%"
    echo ========================================== >> "%output%"
    echo. >> "%output%"
    type "%%i" >> "%output%"
    echo. >> "%output%"
)

echo Готово! Все .java файлы сохранены в %output%
pause