The files icu4j-3.2+.jar,jpe_sdk-9.2+.jar and jsde_sdk-9.2+.jar here
are fake ones. They're empty jar files in order for eclipse not to complain
about the plugin missing required jars. When running udig, since the 
geotools arcsde plugin won't find the required ESRI Java API classes, it
will ask for the location of the actual jars.
So, if running udig from within eclipse to connect to ArcSDE, make sure not
to accidentally commit the actual jars, that udig will replaces these fake ones with.
