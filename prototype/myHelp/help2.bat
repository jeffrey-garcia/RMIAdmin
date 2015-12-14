rem === run the Javajelp in my office W2k desktop ===

@echo off
cls

rem === This line is necessary for compilation of the HelpMenu.java ===
set CLASSPATH=%CLASSPATH%;C:\j2sdk1.4.0\002_dev\prototype\myHelp\jh.jar
set CLASSPATH=%CLASSPATH%;C:\j2sdk1.4.0\002_dev\prototype\myHelp\
rem ===================================================================

//To load the RMIAdmin Help with hsviewer.jar
C:\j2sdk1.4.0\bin\java -jar C:\javahelp-2_0_02\jh2.0\demos\bin\hsviewer.jar -helpset C:\j2sdk1.4.0\002_dev\prototype\myHelp\RMIAdminHelp.hs

//Example to embed RMIAdmin Help in Swing Application
C:\j2sdk1.4.0\bin\java HelpMenu