


DB2 Preferences
~~~~~~~~~~~~~~~

Since DB2 is a proprietary database uDig is not shipped with the DB2
jdbc drivers. The DB2 preference provides a way to point uDig to the
driver jar files.


#. Open the preferences in 1 of the following 2 ways:

    #. If you have never set the jdbc driver before adding a DB2 Layer or
       importing DB2 service into the catalog will open the DB2 preferences
       page.
    #. Open the Workbench Preferences under **Window > Preferences...** >
       <img src="download attachments/6023/db2Preferences.jpg"
       align="absmiddle" border="0"/>

#. Enter location of the JDBC driver or Browse to the file. The driver
   is typically called db2jcc.jar.
#. Enter location of the license jar or Browse to the file. The driver
   is typically called db2jcc_license_cu.jar.
#. Press Apply. This will copy the files into uDig. > <img
   src="download attachments/6023/restart.jpg" align="absmiddle"
   border="0"/>
#. If you wish to use DB2 press OK to restart application. If the
   application is not restarted the new jars will not be found and
   exceptions will occur when you try to add layers.
#. If application is restart you may now add DB2 data.


Since the drivers are copied into the uDig updating the drivers will
require the following steps:


#. Open the Workbench Preferences under **Window > Preferences...**
#. Select DB2 preferences
#. If the location of the JDBC driver has changed enter new location
   of the driver or Browse to the file. If not leave the saved settings.
#. If the location of the license jar has changed enter new location
   of the jar fileor Browse to the file. If not leave the saved settings.
#. Press apply and agree to restart Application.




