@ECHO OFF
SET BINDIR=%~dp0
CD /D "%BINDIR%"
"%ProgramFiles%\Java\jre7\bin\java.exe" -Xmx1024M -Xms1024M -jar craftbukkit-1.7.2.jar
PAUSE