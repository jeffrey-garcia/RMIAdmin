@echo off
cls

color 9F
echo This will replace all the files on this computer with that from memory stick!
echo Are you sure to continue? Press [ctrl-c] to abort
pause

xcopy /E /C /H /Y /I f:\002_dev C:\j2sdk1.4.2_06\002_dev

pause