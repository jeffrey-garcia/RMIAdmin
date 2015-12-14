@echo off

rem =====================================
rem = This is to automate the invoke of =
rem = RMI Client for my home notebook   =
rem =====================================

rem tskill java
cls

call prototype\setENV.bat
call prototype\setENV2.bat

start "RMI Server" java RMIAdmin.RMIServer -p 1099 -k
start "RMI Client" java RMIAdmin.initRMIClient