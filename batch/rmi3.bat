@echo off

rem ===========================================
rem = This is to automate the invoke of       =
rem = RMI Client for my CWD Desktop in office =
rem ===========================================

taskkill /IM java.exe
tskill java
cls

call prototype\setENV3.bat

start "RMI Server" java RMIAdmin.RMIServer -p 1099 -k
start "RMI Client" java RMIAdmin.initRMIClient