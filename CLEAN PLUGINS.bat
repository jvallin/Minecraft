@echo off

:question
set /p choix=Etes-vous sur de vouloir supprimer les .xml ? (o/n)

if /I "%choix%"=="o" (goto :OUI)
if /I "%choix%"=="n" (goto :END)
goto :question

:OUI
MD temp
XCOPY plugins\*.jar temp
RMDIR plugins /s /q
MOVE temp plugins
goto :END

:END
pause