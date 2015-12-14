@echo off

rem =============================================
rem = This is to automate the invoke of         =
rem = RMI Client for my Win2K desktop in office =
rem =============================================

cls

call prototype\setENV2.bat

start "RMI Server" java RMIAdmin.RMIServer -p 1099 -k
start "RMI Client" java RMIAdmin.initRMIClient