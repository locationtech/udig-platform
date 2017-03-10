Catalog Preferences
###################

The catalog preferences allow the behavior of the catalog to be set.

.. figure:: /images/catalog_preferences/catalog.png
   :align: center
   :alt: 

General Catalog Preferences
---------------------------

-  Save Temporary Data Types - When checked temporary feature types that have been defined will be
   saved between sessions. The data is not saved, however, only the feature type definition and the
   entry in the catalog.

   -  Example: If a user creates a new FeatureType via the :menuselection:`Layer --> Create` menu item a layer is
      typically added to the open map and an entry is added to the catalog. If this preference is
      not checked the entry will be removed from the catalog on shutdown.


.. _preferences-page-catalog-arcsde:

ArcSDE Preference Page
----------------------

The ArcSDE Preference Page is used to configure ArcSDE support in uDig.

These settings are used to tell uDig where the "jars" are for version of ArcSDE. These jars are
included on your ArcSDE CD. We cannot distribute the jars with uDig.

ArcSDE Java SDK
```````````````

This is a jar similar to "jsde-sdk-XX.jar" where XX is the version number of ArcSDE you are using.

Please copy this jar to your computer from the ESRI CD and hit Browse to tell uDig where the jar is.

Projection Engine:
``````````````````

This is a jar similar to "jsde-jpe-XX.jar" where XX is the version number of your ArcSDE server.

**Downloads**

`ESRI 9.1 tarball <http://support.esri.com/index.cfm?fa=downloads.patchesServicePacks.viewPatch&PID=19&MetaID=1198#install-cUNIX>`_
   (you will need to untar this service pack and locate the two jar files)

**Related reference**

:doc:`ArcSDE page`


.. _preferences-page-catalog-db2:

DB2 Preferences
---------------

Since DB2 is a proprietary database uDig is not shipped with the DB2 jdbc drivers. The DB2
preference provides a way to point uDig to the driver jar files.

#. Open the preferences in 1 of the following 2 ways:

   #. If you have never set the jdbc driver before adding a DB2 Layer or importing DB2 service into
      the catalog will open the DB2 preferences page.
   #. Open the Workbench Preferences under :menuselection:`Window --> Preferences...`

      |image0|

#. Enter location of the JDBC driver or Browse to the file. The driver is typically called
   db2jcc.jar.
#. Enter location of the license jar or Browse to the file. The driver is typically called
   db2jcc\_license\_cu.jar.
#. Press :guilabel:`Apply`. This will copy the files into uDig.

   |image1|

#. If you wish to use DB2 press OK to restart application. If the application is not restarted the
   new jars will not be found and exceptions will occur when you try to add layers.
#. If application is restart you may now add DB2 data.

Since the drivers are copied into the uDig updating the drivers will require the following steps:

#. Open the Workbench Preferences under :menuselection:`Window --> Preferences...`
#. Select DB2 preferences
#. If the location of the JDBC driver has changed enter new location of the driver or Browse to the
   file. If not leave the saved settings.
#. If the location of the license jar has changed enter new location of the jar fileor Browse to the
   file. If not leave the saved settings.
#. Press apply and agree to restart Application.

**Related reference**

:doc:`DB2 page`

.. |image0| image:: /images/db2_preferences/db2Preferences.jpg
.. |image1| image:: /images/db2_preferences/restart.jpg

.. _preferences-page-catalog-wms:

WMS Preference Page
----------------------

The WMS Preference Page is used to configure parameter when connecting to a Web Map Server.

The WMS response timeout controls the max wait time (in sec) for a response from a WMS server. 
The default value is 30 sec.   

	|image2|

**Related reference**

:doc:`Web Map Server page`

.. |image2| image:: /images/wms_preferences/wmsCatalogPreferences.jpg