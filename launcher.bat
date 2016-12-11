@ECHO OFF
start /wait readImage.exe
echo Creating ColorCode and Intensity Files...
SET LookForFile="%cd%\colorCodes.txt"

:CheckForFile
IF EXIST %LookForFile% GOTO FoundIt

GOTO CheckForFile


:FoundIt
start CBIR.exe
exit