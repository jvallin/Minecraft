@ECHO OFF

:question
set /p choix=Voulez-vous mettre a jour le monde ? (o/n)

if /I "%choix%"=="o" (goto :OUI)
if /I "%choix%"=="n" (goto :END)
goto :question

:OUI
RMDIR world /s /q
RMDIR world_nether /s /q
RMDIR world_the_end /s /q
MD world
MD world_nether
MD world_the_end
XCOPY "C:\Users\Balckangel\Desktop\Serveur\world" world /e
XCOPY "C:\Users\Balckangel\Desktop\Serveur\world_nether" world_nether /e
XCOPY "C:\Users\Balckangel\Desktop\Serveur\world_the_end" world_the_end /e
goto :END

:END
SET BINDIR=%~dp0
CD /D "%BINDIR%"
"%ProgramFiles%\Java\jre7\bin\java.exe" -Xmx1024M -Xms1024M -jar craftbukkit-1.7.2.jar
PAUSE