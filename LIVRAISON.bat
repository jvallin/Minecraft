@echo off

set REP_JAR_LOCAL=C:\Users\Jérémy\Desktop\Minecraft\Dev\plugins
set REP_JAR_DROPBOX=C:\Users\Jérémy\Desktop\Dropbox\Private\Minecraft\Plugins Perso\JAR

set FIC_GIT=C:\tmp\a.bat
echo "%REP_JAR_LOCAL%"
If not exist "%REP_JAR_LOCAL%" (
echo Repertoire Jar Local non valide
goto :END
)

If not exist "%REP_JAR_DROPBOX%" (
echo Repertoire Jar Dropbox non valide
goto :END
)

If not exist %FIC_GIT% (
echo Fichier Git.cmd non valide
goto :END
)


:question
set /p choix=Que Voulez-vous mettre a jour les jar ? (o : oui/n : non)

if /I "%choix%"=="o" (goto :OUI)
if /I "%choix%"=="n" (goto :END)
goto :question

:OUI
rmdir "%REP_JAR_LOCAL%" /s /q
mkdir "%REP_JAR_LOCAL%"
xcopy "%REP_JAR_DROPBOX%" "%REP_JAR_LOCAL%" /e /y
goto :GIT

:GIT
call  "%FIC_GIT%"

:END
pause
