Handling Shapefiles with different Character Sets
#################################################

There are two ways to specify character set information when working with Shapefiles.

-  `Default Character Set for the  Application`_
-  `Change Shapefile Character Set`_

The shapefile format makes use of a "dbf" database to store attribute data; depending on the
character set the data is recorded in we will need to interpret the attributes differently.

You may need to change the character set for a shapefile if you are having trouble displaying
shapefiles created using several different languages.

Default Character Set for the Application
=========================================

You can set the "Default" character set in the preferences dialog. (Do a search for character set).
This declares the character set that will be used the first time a shapefile is added to the catalog
or to a map.

This setting can be helpful if your organization works with many shapefiles created with the same
language.

Change Shapefile Character Set
==============================

Once a Shapefile is in the catalog its Character set can be modified by:

#. Selecting the Shapefile Service (the top level object in the catalog)
#. Using the "Change Character Set" Operation - this can also be found in the context menu or in the
   Data>Resource menu.

For convenience you can select many Shapefiles and change them all with a single operation.
