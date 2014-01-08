@ECHO OFF

SET D=%DATE%
SET DA=%D:/=-%

:question
set /p choix=Le serveur est-il arrete ? (o/n)

if /I "%choix%"=="o" (goto :OUI)
if /I "%choix%"=="n" (goto :NON)
goto :question

:OUI
md %DA%
cd %DA%
MD world
MD world_nether
MD world_the_end
MD plugins
XCOPY "..\Serveur\world" "world" /e /r
XCOPY "..\Serveur\world_nether" "world_nether" /e
XCOPY "..\Serveur\world_the_end" "world_the_end" /e
XCOPY "..\Serveur\plugins" "plugins" /e
echo Sauvegarde terminee
goto :END

:NON
echo Sauvegarde impossible
goto :END

:END
PAUSE