Implementing a Service
~~~~~~~~~~~~~~~~~~~~~~

Start by copying a good example:

-  Shapefile plugin, if your DataStore uses files
-  PostGIS plugin, if it uses a database connection

The following extension points are to be extended by your plugin:

Extention

Shapefile

PostGIS

Notes

org.locationtech.udig.catalog.ui.connectionFactory

 

x

Data Wizard Page, parameters for connecting to datastore

org.locationtech.udig.catalog.ui.fileFormat

x

 

This extension point just sets the file extension of your datastore

org.locationtech.udig.catalog.ServiceExtention

x

x

Create datastore from connection parameters

"Settings Panel"

x

 

Preference page, optional

The Shapefile plugin has a preferences page, which is not a mandatory part of a datastore plugin, so
for a start you won't necessary need that.

You are going to have to implement (by rewriting the copy of) a couple of the core classes:

-  IServiceExtension - teaches the catalog how to work with your data
-  IService - Represents the DataStore in the Catalog; connects to the DataStore as needed

   -  IServiceInfo - Descriptive information about the database for file

-  IGeoResource - Represents the content of the DataStore

   -  IGeoResourceInfo - descriptive information about the content. uDig reads many information
      about a feature type from this class, e.g. the CRS, so take care about the correct
      implementation, e.g. by delegation

The "CSV Tutorial" from the training course covers all these steps, the source code is available
from svn here:

* `http://svn.refractions.net/udig/udig/branches/1.1.x/udig/tutorials/org.locationtech.udig.tutorials.catalog.csv/ <http://svn.refractions.net/udig/udig/branches/1.1.x/udig/tutorials/org.locationtech.udig.tutorials.catalog.csv/>`_

