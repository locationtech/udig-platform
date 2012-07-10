net.refractions.udig.catalog
============================

Functional Requirements
~~~~~~~~~~~~~~~~~~~~~~~

-  Servers, store server connection information for sharing between projects.

   -  Store data sources (DataStore and GridCoverageExchange)

-  Data Directories, store data directories for sharing between projects.
-  Metadata, provide access to metadata on Servers/Data Directories.
-  Data Discovery, provide enough information for a user to define a new layer
    in their context.

   -  Wizards for DataStore connection
   -  Wizards for DataStore creation

-  Persist Settings, permit exporting and sharing DataStore connection
    information.
-  DataStores Management, lookup actualized DataStores that are in use.
-  Missing Data, entries referred to by imported projects should be maintained,
    allowing the user one location to correct data connection information.
    Local Catalog has a strong interaction with the preferences maintained by the
-  Provide support for temporary results, and facilities for later export

Non-Functional Requirements
~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Ease of Data Location, intent is for the user to be separated from the data
    source; so they need as little technical knowledge as possible.
-  Security, name/password should be left out of the export/share.

Design Notes
~~~~~~~~~~~~

Realized as LocalRegistry, was speced out as LocalCatalog in initial design requirements.
