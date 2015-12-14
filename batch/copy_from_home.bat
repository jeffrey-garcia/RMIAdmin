@echo off
cls

color 9F
echo This will replace all the files on the memory stick with that from this computer!
echo Are you sure to continue? Press [ctrl-c] to abort
pause

xcopy /E /C /H /Y /I C:\j2sdk1.4.2_06\002_dev f:\002_dev

pause