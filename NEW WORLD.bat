@echo off

:question
set /p choix=Etes-vous sur de vouloir recommencer un monde ? (o/n)

if /I "%choix%"=="o" (goto :OUI)
if /I "%choix%"=="n" (goto :END)
goto :question

:OUI
RMDIR world /s /q
RMDIR world_nether /s /q
RMDIR world_the_end /s /q
goto :END

:END
CALL ".\LAUNCHER.bat"