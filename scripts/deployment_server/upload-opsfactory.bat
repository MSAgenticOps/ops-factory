@echo off
chcp 65001 > nul
setlocal EnableDelayedExpansion

echo ============================================
echo SSH Connection Test and Multiple File Upload
echo ============================================
echo.

set USER=paas
set HOST=192.168.200.35
set PASSWORD=Image0@Huawei123
set REMOTE_PATH=/home/paas/gateway/

set PSCP_PATH=C:\ProgramData\chocolatey\lib\putty.portable\tools\PSCP.EXE
set PLINK_PATH=C:\ProgramData\chocolatey\lib\putty.portable\tools\PLINK.EXE

set "DIST_PATH=C:\zhulin\ops-factory\web-app\dist"
set "ZIP_FILE=C:\zhulin\ops-factory\web-app\dist.zip"

set "FILES[0]=C:\zhulin\ops-factory\gateway\gateway-service\target\gateway-service.jar"
set "FILES[1]=C:\zhulin\ops-factory\gateway\gateway-service\target\lib.zip"
set "FILES[2]=C:\zhulin\goose\handle_ops_app.sh"
set "FILES[3]=C:\zhulin\goose\handle_ops_app.conf"
set "FILES[4]=C:\zhulin\ops-factory\web-app\dist.zip"
set "FILES[5]=C:\zhulin\goose\agents.zip"
set "FILES[6]=C:\zhulin\ops-factory\gateway\config.yaml.example"
set "FILES[7]=C:\zhulin\ops-factory\operation-intelligence\target\operation-intelligence.jar"
set "FILES[8]=C:\zhulin\ops-factory\operation-intelligence\target\oi-lib.zip"
set "FILES[9]=C:\zhulin\ops-factory\operation-intelligence\scripts\dv_server.py"
set "FILES[10]=C:\zhulin\ops-factory\control-center\target\control-center.jar"
set "FILES[11]=C:\zhulin\ops-factory\control-center\target\cc-lib.zip"
set "FILES[12]=C:\zhulin\ops-factory\knowledge-service\target\knowledge-service.jar"
set "FILES[13]=C:\zhulin\ops-factory\knowledge-service\target\ks-lib.zip"
set "FILES[14]=C:\zhulin\ops-factory\skill-market\target\skill-market.jar"
set "FILES[15]=C:\zhulin\ops-factory\skill-market\target\sm-lib.zip"
set "FILES[16]=C:\zhulin\ops-factory\business-intelligence\target\business-intelligence.jar"
set "FILES[17]=C:\zhulin\ops-factory\business-intelligence\target\bi-lib.zip"
set "FILES[18]=C:\zhulin\ops-factory\finops\target\finops.jar"
set "FILES[19]=C:\zhulin\ops-factory\finops\target\finops-lib.zip"

set "OI_CONFIG_SRC=C:\zhulin\ops-factory\operation-intelligence\config.yaml.example"
set "CC_CONFIG_SRC=C:\zhulin\ops-factory\control-center\config.yaml.example"
set "KS_CONFIG_SRC=C:\zhulin\ops-factory\knowledge-service\config.yaml.example"
set "SM_CONFIG_SRC=C:\zhulin\ops-factory\skill-market\config.yaml.example"
set "BI_CONFIG_SRC=C:\zhulin\ops-factory\business-intelligence\config.yaml.example"
set "FINOPS_CONFIG_SRC=C:\zhulin\ops-factory\finops\config.yaml.example"

echo Configuration:
echo   User: %USER%
echo   Host: %HOST%
echo   Remote Path: %REMOTE_PATH%
echo.

echo.
echo ============================================
echo Step 0: 压缩dist文件夹
echo ============================================
echo 源路径: %DIST_PATH%
echo 压缩文件: %ZIP_FILE%
echo.

if exist "%ZIP_FILE%" (
    echo 删除旧的压缩文件: %ZIP_FILE%
    del "%ZIP_FILE%"
)

powershell -Command "Compress-Archive -Path '%DIST_PATH%\*' -DestinationPath '%ZIP_FILE%' -Force"

if exist "%ZIP_FILE%" (
    echo.
    echo ✓ 压缩成功: %ZIP_FILE%
    for %%F in ("%ZIP_FILE%") do echo 文件大小: %%~zF 字节
) else (
    echo.
    echo ✗ 压缩失败: %ZIP_FILE%
    echo 请检查源路径是否存在: %DIST_PATH%
    pause
    exit /b 1
)

echo.
echo ============================================
echo Step 0b: 压缩lib目录
echo ============================================
set "LIB_SRC=C:\zhulin\ops-factory\gateway\gateway-service\target\lib"
set "LIB_ZIP=C:\zhulin\ops-factory\gateway\gateway-service\target\lib.zip"

if exist "%LIB_ZIP%" (
    echo 删除旧的压缩文件: %LIB_ZIP%
    del "%LIB_ZIP%"
)

powershell -Command "Compress-Archive -Path '%LIB_SRC%\*' -DestinationPath '%LIB_ZIP%' -Force"

if exist "%LIB_ZIP%" (
    echo.
    echo ✓ lib目录压缩成功: %LIB_ZIP%
    for %%F in ("%LIB_ZIP%") do echo 文件大小: %%~zF 字节
) else (
    echo.
    echo ✗ lib目录压缩失败: %LIB_ZIP%
    pause
    exit /b 1
)

echo.
echo ============================================
echo Step 0c: 压缩agents目录
echo ============================================
set "AGENTS_SRC=C:\zhulin\ops-factory\gateway\agents"
set "AGENTS_ZIP=C:\zhulin\goose\agents.zip"
set "AGENTS_STAGING=%TEMP%\agents-staging"

if exist "%AGENTS_ZIP%" (
    echo 删除旧的压缩文件: %AGENTS_ZIP%
    del "%AGENTS_ZIP%"
)

if exist "%AGENTS_STAGING%" rmdir /s /q "%AGENTS_STAGING%"

echo 正在准备agents临时目录（排除node_modules和.goose）...
robocopy "%AGENTS_SRC%" "%AGENTS_STAGING%" /e /xd node_modules .goose /njh /njs /ndl /nc /np >nul 2>&1

echo 正在压缩agents目录...
powershell -Command "Compress-Archive -Path '%AGENTS_STAGING%\*' -DestinationPath '%AGENTS_ZIP%' -Force -CompressionLevel Fastest"

if exist "%AGENTS_STAGING%" rmdir /s /q "%AGENTS_STAGING%"

if exist "%AGENTS_ZIP%" (
    echo.
    echo ✓ agents目录压缩成功: %AGENTS_ZIP%
    for %%F in ("%AGENTS_ZIP%") do echo 文件大小: %%~zF 字节
) else (
    echo.
    echo ✗ agents目录压缩失败: %AGENTS_ZIP%
    pause
    exit /b 1
)

echo.
echo ============================================
echo Step 0d: 压缩operation-intelligence lib目录
echo ============================================
set "OI_LIB_SRC=C:\zhulin\ops-factory\operation-intelligence\target\lib"
set "OI_LIB_ZIP=C:\zhulin\ops-factory\operation-intelligence\target\oi-lib.zip"

if exist "%OI_LIB_ZIP%" (
    echo 删除旧的压缩文件: %OI_LIB_ZIP%
    del "%OI_LIB_ZIP%"
)

if exist "%OI_LIB_SRC%" (
    powershell -Command "Compress-Archive -Path '%OI_LIB_SRC%\*' -DestinationPath '%OI_LIB_ZIP%' -Force"
    if exist "%OI_LIB_ZIP%" (
        echo ✓ OI lib目录压缩成功: %OI_LIB_ZIP%
        for %%F in ("%OI_LIB_ZIP%") do echo 文件大小: %%~zF 字节
    ) else (
        echo ✗ OI lib目录压缩失败: %OI_LIB_ZIP%
    )
) else (
    echo 警告: OI lib目录不存在，跳过压缩: %OI_LIB_SRC%
)

echo.
echo ============================================
echo Step 0e: 压缩control-center lib目录
echo ============================================
set "CC_LIB_SRC=C:\zhulin\ops-factory\control-center\target\lib"
set "CC_LIB_ZIP=C:\zhulin\ops-factory\control-center\target\cc-lib.zip"

if exist "%CC_LIB_ZIP%" (
    echo 删除旧的压缩文件: %CC_LIB_ZIP%
    del "%CC_LIB_ZIP%"
)

if exist "%CC_LIB_SRC%" (
    powershell -Command "Compress-Archive -Path '%CC_LIB_SRC%\*' -DestinationPath '%CC_LIB_ZIP%' -Force"
    if exist "%CC_LIB_ZIP%" (
        echo ✓ CC lib目录压缩成功: %CC_LIB_ZIP%
        for %%F in ("%CC_LIB_ZIP%") do echo 文件大小: %%~zF 字节
    ) else (
        echo ✗ CC lib目录压缩失败: %CC_LIB_ZIP%
    )
) else (
    echo 警告: CC lib目录不存在，跳过压缩: %CC_LIB_SRC%
)

echo.
echo ============================================
echo Step 0f: 压缩knowledge-service lib目录
echo ============================================
set "KS_LIB_SRC=C:\zhulin\ops-factory\knowledge-service\target\lib"
set "KS_LIB_ZIP=C:\zhulin\ops-factory\knowledge-service\target\ks-lib.zip"

if exist "%KS_LIB_ZIP%" (
    echo 删除旧的压缩文件: %KS_LIB_ZIP%
    del "%KS_LIB_ZIP%"
)

if exist "%KS_LIB_SRC%" (
    powershell -Command "Compress-Archive -Path '%KS_LIB_SRC%\*' -DestinationPath '%KS_LIB_ZIP%' -Force"
    if exist "%KS_LIB_ZIP%" (
        echo ✓ KS lib目录压缩成功: %KS_LIB_ZIP%
        for %%F in ("%KS_LIB_ZIP%") do echo 文件大小: %%~zF 字节
    ) else (
        echo ✗ KS lib目录压缩失败: %KS_LIB_ZIP%
    )
) else (
    echo 警告: KS lib目录不存在，跳过压缩: %KS_LIB_SRC%
)

echo.
echo ============================================
echo Step 0g: 压缩skill-market lib目录
echo ============================================
set "SM_LIB_SRC=C:\zhulin\ops-factory\skill-market\target\lib"
set "SM_LIB_ZIP=C:\zhulin\ops-factory\skill-market\target\sm-lib.zip"

if exist "%SM_LIB_ZIP%" (
    echo 删除旧的压缩文件: %SM_LIB_ZIP%
    del "%SM_LIB_ZIP%"
)

if exist "%SM_LIB_SRC%" (
    powershell -Command "Compress-Archive -Path '%SM_LIB_SRC%\*' -DestinationPath '%SM_LIB_ZIP%' -Force"
    if exist "%SM_LIB_ZIP%" (
        echo ✓ SM lib目录压缩成功: %SM_LIB_ZIP%
        for %%F in ("%SM_LIB_ZIP%") do echo 文件大小: %%~zF 字节
    ) else (
        echo ✗ SM lib目录压缩失败: %SM_LIB_ZIP%
    )
) else (
    echo 警告: SM lib目录不存在，跳过压缩: %SM_LIB_SRC%
)

echo.
echo ============================================
echo Step 0h: 压缩business-intelligence lib目录
echo ============================================
set "BI_LIB_SRC=C:\zhulin\ops-factory\business-intelligence\target\lib"
set "BI_LIB_ZIP=C:\zhulin\ops-factory\business-intelligence\target\bi-lib.zip"

if exist "%BI_LIB_ZIP%" (
    echo 删除旧的压缩文件: %BI_LIB_ZIP%
    del "%BI_LIB_ZIP%"
)

if exist "%BI_LIB_SRC%" (
    powershell -Command "Compress-Archive -Path '%BI_LIB_SRC%\*' -DestinationPath '%BI_LIB_ZIP%' -Force"
    if exist "%BI_LIB_ZIP%" (
        echo ✓ BI lib目录压缩成功: %BI_LIB_ZIP%
        for %%F in ("%BI_LIB_ZIP%") do echo 文件大小: %%~zF 字节
    ) else (
        echo ✗ BI lib目录压缩失败: %BI_LIB_ZIP%
    )
) else (
    echo 警告: BI lib目录不存在，跳过压缩: %BI_LIB_SRC%
)

echo.
echo ============================================
echo Step 0i: 压缩finops lib目录
echo ============================================
set "FINOPS_LIB_SRC=C:\zhulin\ops-factory\finops\target\lib"
set "FINOPS_LIB_ZIP=C:\zhulin\ops-factory\finops\target\finops-lib.zip"

if exist "%FINOPS_LIB_ZIP%" (
    echo 删除旧的压缩文件: %FINOPS_LIB_ZIP%
    del "%FINOPS_LIB_ZIP%"
)

if exist "%FINOPS_LIB_SRC%" (
    powershell -Command "Compress-Archive -Path '%FINOPS_LIB_SRC%\*' -DestinationPath '%FINOPS_LIB_ZIP%' -Force"
    if exist "%FINOPS_LIB_ZIP%" (
        echo ✓ FinOps lib目录压缩成功: %FINOPS_LIB_ZIP%
        for %%F in ("%FINOPS_LIB_ZIP%") do echo 文件大小: %%~zF 字节
    ) else (
        echo ✗ FinOps lib目录压缩失败: %FINOPS_LIB_ZIP%
    )
) else (
    echo 警告: FinOps lib目录不存在，跳过压缩: %FINOPS_LIB_SRC%
)

echo.
echo ============================================
echo.

set FILE_COUNT=0
set SUCCESS_COUNT=0
set FAIL_COUNT=0

echo [Step 1] Testing SSH connection...
"%PLINK_PATH%" -v -pw %PASSWORD% -batch %USER%@%HOST% whoami
set SSH_EXITCODE=%errorlevel%
echo.
echo SSH Exit Code: %SSH_EXITCODE%
echo.

if %SSH_EXITCODE% neq 0 (
    echo [ERROR] SSH connection failed with exit code %SSH_EXITCODE%
    echo.
    echo Troubleshooting:
    echo 1. Check if the server is running
    echo 2. Verify the IP address: %HOST%
    echo 3. Verify the username: %USER%
    echo 4. Verify the password
    echo 5. Check if password authentication is enabled on the server
    echo 6. Check if SSH key authentication is required instead
    echo.
    goto :end
)

echo [SUCCESS] SSH connection established!
echo.
echo [Step 2] Creating remote directory...
"%PLINK_PATH%" -pw %PASSWORD% -batch %USER%@%HOST% mkdir -p %REMOTE_PATH%
set MKDIR_EXITCODE=%errorlevel%
echo.
echo mkdir Exit Code: %MKDIR_EXITCODE%
echo.

if %MKDIR_EXITCODE% neq 0 (
    echo [ERROR] Failed to create remote directory %REMOTE_PATH%
    goto :end
)

echo [SUCCESS] Remote directory created/verified: %REMOTE_PATH%
echo.
echo [Step 3] Uploading files...
echo.

for /L %%i in (0,1,19) do (
    set "CURRENT_FILE=!FILES[%%i]!"
    if exist "!CURRENT_FILE!" (
        set /a FILE_COUNT+=1
        
        echo [File %%i] Uploading: !CURRENT_FILE!
        echo   Target: %USER%@%HOST%:%REMOTE_PATH%
        
        "%PSCP_PATH%" -pw %PASSWORD% -batch -P 22 "!CURRENT_FILE!" %USER%@%HOST%:%REMOTE_PATH%
        set UPLOAD_EXITCODE=!errorlevel!
        
        if !UPLOAD_EXITCODE! equ 0 (
            echo   Status: SUCCESS
            set /a SUCCESS_COUNT+=1
        ) else (
            echo   Status: FAILED (Exit Code: !UPLOAD_EXITCODE!)
            set /a FAIL_COUNT+=1
        )
        echo.
    )
)

echo ============================================
echo [Step 4] Executing remote script...
echo ============================================
echo.

:: Check if we need to execute remote script
if !SUCCESS_COUNT! gtr 0 (
    echo Found %SUCCESS_COUNT% successfully uploaded files.
    echo Uploading OI config as oi-config.yaml.example...
    if exist "%OI_CONFIG_SRC%" (
        "%PSCP_PATH%" -pw %PASSWORD% -batch -P 22 "%OI_CONFIG_SRC%" %USER%@%HOST%:%REMOTE_PATH%oi-config.yaml.example
        if !errorlevel! equ 0 (
            echo   OI config uploaded as oi-config.yaml.example: SUCCESS
        ) else (
            echo   OI config upload: FAILED
        )
    )
    echo Uploading CC config as cc-config.yaml.example...
    if exist "%CC_CONFIG_SRC%" (
        "%PSCP_PATH%" -pw %PASSWORD% -batch -P 22 "%CC_CONFIG_SRC%" %USER%@%HOST%:%REMOTE_PATH%cc-config.yaml.example
        if !errorlevel! equ 0 (
            echo   CC config uploaded as cc-config.yaml.example: SUCCESS
        ) else (
            echo   CC config upload: FAILED
        )
    )
    echo Uploading KS config as ks-config.yaml.example...
    if exist "%KS_CONFIG_SRC%" (
        "%PSCP_PATH%" -pw %PASSWORD% -batch -P 22 "%KS_CONFIG_SRC%" %USER%@%HOST%:%REMOTE_PATH%ks-config.yaml.example
        if !errorlevel! equ 0 (
            echo   KS config uploaded as ks-config.yaml.example: SUCCESS
        ) else (
            echo   KS config upload: FAILED
        )
    )
    echo Uploading SM config as sm-config.yaml.example...
    if exist "%SM_CONFIG_SRC%" (
        "%PSCP_PATH%" -pw %PASSWORD% -batch -P 22 "%SM_CONFIG_SRC%" %USER%@%HOST%:%REMOTE_PATH%sm-config.yaml.example
        if !errorlevel! equ 0 (
            echo   SM config uploaded as sm-config.yaml.example: SUCCESS
        ) else (
            echo   SM config upload: FAILED
        )
    )
    echo Uploading BI config as bi-config.yaml.example...
    if exist "%BI_CONFIG_SRC%" (
        "%PSCP_PATH%" -pw %PASSWORD% -batch -P 22 "%BI_CONFIG_SRC%" %USER%@%HOST%:%REMOTE_PATH%bi-config.yaml.example
        if !errorlevel! equ 0 (
            echo   BI config uploaded as bi-config.yaml.example: SUCCESS
        ) else (
            echo   BI config upload: FAILED
        )
    )
    echo Uploading FinOps config as finops-config.yaml.example...
    if exist "%FINOPS_CONFIG_SRC%" (
        "%PSCP_PATH%" -pw %PASSWORD% -batch -P 22 "%FINOPS_CONFIG_SRC%" %USER%@%HOST%:%REMOTE_PATH%finops-config.yaml.example
        if !errorlevel! equ 0 (
            echo   FinOps config uploaded as finops-config.yaml.example: SUCCESS
        ) else (
            echo   FinOps config upload: FAILED
        )
    )
    echo Attempting to execute remote script: /home/paas/gateway/handle_ops_app.sh
    
    "%PLINK_PATH%" -pw %PASSWORD% -batch %USER%@%HOST% "cd /home/paas/gateway/"
    if %errorlevel% equ 0 (
        echo Making script executable and running...
        "%PLINK_PATH%" -pw %PASSWORD% -batch %USER%@%HOST% "cd /home/paas/gateway/;dos2unix handle_ops_app.sh handle_ops_app.conf;chmod +x handle_ops_app.sh;sh handle_ops_app.sh"
        
        if %errorlevel% equ 0 (
            echo Remote script executed successfully!
            set "EXEC_SUCCESS=1"
            echo.
            echo ============================================
            echo Remote Script Execution Summary:
            echo   Script: /home/paas/gateway/handle_ops_app.sh
            echo   Server: %USER%@%HOST%
            echo   Status: SUCCESS
            echo   Exit Code: %errorlevel%
            echo ============================================
            echo.
        ) else (
            echo Remote script execution failed with exit code %errorlevel%
            echo.
            echo ============================================
            echo Remote Script Execution Summary:
            echo   Script: /home/paas/gateway/handle_ops_app.sh
            echo   Server: %USER%@%HOST%
            echo   Status: FAILED
            echo   Exit Code: %errorlevel%
            echo ============================================
            echo.
        )
    ) else (
        echo Failed to connect to remote directory
    )
) else (
    echo Skipping remote script execution - no files were uploaded successfully.
    echo.
    echo ============================================
    echo Remote Script Execution Summary:
    echo   Script: /home/paas/gateway/handle_ops_app.sh
    echo   Server: %USER%@%HOST%
    echo   Status: SKIPPED
    echo   Reason: No files uploaded successfully
    echo ============================================
    echo.
)

echo.
echo ============================================
echo Script completed.
echo ============================================
pause


