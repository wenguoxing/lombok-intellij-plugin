@ECHO OFF

SET pluginVersion=0.19

for %%X in (15.0.2) do call :buildPlugin %%X
ECHO All Done
GOTO :eof

:buildPlugin
SETLOCAL
echo Called with %1
SET IDEA_VERSION=%1
call gradlew clean
call gradlew buildPlugin check
copy build\distributions\lombok-plugin-%pluginVersion%.zip distro\lombok-plugin-%pluginVersion%-%1.zip
ENDLOCAL & SET result=%retval%
