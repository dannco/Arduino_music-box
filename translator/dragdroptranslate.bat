@echo off
setlocal enabledelayedexpansion
set var=
for /f "delims=" %%x in (%1) do set var=!var! %%x
java PiezoParser "%var%"
