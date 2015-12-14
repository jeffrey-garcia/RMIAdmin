rem === run the Javahelp on my WinXP notebook ===

@echo off
cls

rem === This line is necessary for compilation of the HelpMenu.java ===
set CLASSPATH=%CLASSPATH%;C:\j2sdk1.4.2_06\002_dev\prototype\myHelp\jh.jar
set CLASSPATH=%CLASSPATH%;C:\j2sdk1.4.2_06\002_dev\prototype\myHelp
rem ===================================================================

//To load the RMIAdmin Help with hsviewer.jar
C:\j2sdk1.4.2_06\bin\java -jar C:\jh2.0\demos\bin\hsviewer.jar -helpset C:\j2sdk1.4.2_06\002_dev\prototype\myHelp\RMIAdminHelp.hs

//Example to embed RMIAdmin Help in Swing Application
C:\j2sdk1.4.2_06\bin\java HelpMenu