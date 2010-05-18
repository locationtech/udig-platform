set GDAL_DATA=%cd%\gdal_data\
set GDAL_DRIVER_PATH=%cd%\jre\bin\

START /B udig_internal -data %USERPROFILE%\uDig %*
