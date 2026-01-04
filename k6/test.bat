@echo off

set SCRIPT_NAME=stress_test.js

:: 워밍업 2회
for /l %%i in (1,1,2) do (
    echo [Warm-up %%i] 실행 중...
    k6 run --summary-export=./reports/seat__warmup__%%i.json %SCRIPT_NAME% > nul
)

:: 본 테스트 5회
for /l %%i in (1,1,5) do (
    echo [Main Test %%i] 실행 중...
    k6 run --summary-export=./reports/seat__main__%%i.json %SCRIPT_NAME%
)

echo.
echo 모든 테스트가 완료되었습니다. 'reports' 폴더를 확인하세요.
pause