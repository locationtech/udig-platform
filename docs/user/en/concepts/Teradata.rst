Teradata
~~~~~~~~

The Teradata Database is a commercial relational database (RDBMS) that specializes in parallel
processing and scalability. From version 12.0, Teradata has added geospatial support, closely
following the SQL/MM standard (SQL Multimedia and Applications Packages). Geospatial support was
available through an add-on in version 12.0 and became standard in version 13.0.

uDig connects to a Teradata database via JDBC.

For more information on Teradata and the Teradata Database system, please go to
`http://www.teradata.com <http://www.teradata.com>`_.

Compatibility
^^^^^^^^^^^^^

The uDig Teradata extension is compatible with 1.2.2 and higher. uDig can connect to Teradata
databases version 12.0 or higher. Version 12.0 of the Teradata Database requires the optional
geospatial extension to be installed.

Issues/Errata
^^^^^^^^^^^^^

The version of the Teradata plugin that ships with uDig 1.2.2 has some issues that may cause
performance issues with large data sets and a potential application freeze when editing features.
There is a patch to address these issues, which can be installed in the following way:

-  Follow the instructions to :doc:`install a new plugin </Finding new plugins>`.
-  When asked for the site location (step 5) enter `<http://udig.refractions.net/files/update-teradata/>`_

-  After completing the wizard, restart uDig.

This patch will be incorporated into all future releases of uDig.

Read/write access
^^^^^^^^^^^^^^^^^

The Teradata datastore in uDig supports full transactional capabilities, including feature creation,
editing, and deleting.

To support editing, a table must have one of the following:

-  a primary key
-  a unique primary index
-  an identity (sequential) column

.. note::
   It is not recommended to solely use an identity column, as spatial index triggers are not supported
   when referencing an identity column. See the section on Spatial Indexes for more details.

Query Banding
^^^^^^^^^^^^^

Teradata connections in uDig utilize Query Banding. Query Banding is a feature which allows any
application to associate context information with each query it issues to the database. In practice
this can be used for purposes of workload management (i.e. request prioritization), debugging, and
logging.

uDig sends the following information as part of a standard request:

-  Name of application (i.e. uDig)
-  Authenticated username (if set up)
-  Hostname (if available)
-  Type of statement (i.e. "SELECT", "INSERT", "DELETE")

The only details that can be modified from within uDig is the Name of application and the Connection
Mode .  See the :doc:`/Teradata page` for more details.

Spatial indexes
^^^^^^^^^^^^^^^

uDig will read from a spatial index if its exists. The convention for a spatial index table name is:

::

    [TABLENAME]_[GEOMETRYCOLUMN]_idx

So for a layer called "STATES" with a geometry column called "GEOM", the index table should be
called **STATES\_GEOM\_idx**.

.. warning::
   Make sure to match the case of all tables and columns. If the geometry column is called "GEOM"
   (upper case) and the index created is called STATES\_geom\_idx (lower case), the index will not be
   properly linked to the table.

This index table should contain two columns:

-  A column that maps to the primary key of the spatial data table
-  The tessellation cell ID (cellid)

The tessellation cell ID is the ID of the cell where that feature is contained.


Geometry column
^^^^^^^^^^^^^^^

As per the SQL/MM standard, in order to make a Teradata table spatially enabled, an entry needs to
be created for that table in the ``geometry_columns`` table. This table is stored, like all other
spatially-related tables, in the SYSSPATIAL database.

Tessellation
^^^^^^^^^^^^

Tessellation is the name of Teradata's spatial index. In order to activate tessellation for a given
layer, an entry (row) needs to be placed in the ``SYSSPATIAL.tessellation`` table. This table should
have the following schema:

+-----------------------+----------+-----------------------------------------------+
| **Table name**        | **Type** |  **Description**                              |
+-----------------------+----------+-----------------------------------------------+
| **F_TABLE_SCHEMA**    | varchar  | Name of the spatial database/schema           |
|                       |          | containing the table                          |
+-----------------------+----------+-----------------------------------------------+
| **F_TABLE_NAME**      | varchar  | Name of the spatial table                     |
+-----------------------+----------+-----------------------------------------------+
| **F_GEOMETRY_COLUMN** | varchar  | Column that contains the spatial data         |
+-----------------------+----------+-----------------------------------------------+
| **U_XMIN**            | float    | Minimum X value for the tessellation universe |
+-----------------------+----------+-----------------------------------------------+
| **U_YMIN**            | float    | Minimum Y value for the tessellation universe |
+-----------------------+----------+-----------------------------------------------+
| **U_XMAX**            | float    | Maximum X value for the tessellation universe |
+-----------------------+----------+-----------------------------------------------+
| **U_YMAX**            | float    | Maximum Y value for the tessellation universe |
+-----------------------+----------+-----------------------------------------------+
| **G_NX**              | integer  | Number of X grids                             |
+-----------------------+----------+-----------------------------------------------+
| **G_NY**              | integer  | Number of Y grids                             |
+-----------------------+----------+-----------------------------------------------+
| **LEVELS**            | integer  | Number of levels in the grid                  |
+-----------------------+----------+-----------------------------------------------+
| **SCALE**             | float    | Scale value for the grid                      |
+-----------------------+----------+-----------------------------------------------+
| **SHIFT**             | float    | Shift value for the grid                      |
+-----------------------+----------+-----------------------------------------------+

For more information about Tessellation, please see the Teradata documentation.

.. warning::
   The tessellation table values are case sensitive and so must match the case of the tables and columns.

Installing the Teradata extension
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Teradata database support is not enabled by default and requires the Teradata drivers to be
downloaded prior to use.

To get the Teradata drivers, please visit:
http://downloads.teradata.com/download/connectivity/jdbc-driver

.. note::
   You will need to log in to Teradata's site in order to download this artifact.

Extract the contents of the archive to any convenient directory. There should be two files in this
archive:

-  terajdbc4.jar
-  tdgssconfig.jar

Then navigate to :menuselection:`Layer --> Add... --> Teradata`. You will be shown a dialog (see below). Insert the
locations of the two files extracted above into the dialog box and then click **Restart**.

.. figure:: /images/teradata/td_drivers.png
   :align: center
   :alt:

On some installations, a second dialog may appear stating that a directory is not accessible to
uDig. If this occurs, make sure that uDig has write privileges to the installation directory,
restart uDig, and try again.

.. figure:: /images/teradata/td_drivers_admin.png
   :align: center
   :alt:

After uDig restarts, navigate back to :menuselection:`Layer --> Add... --> Teradata`. If everything was installed
correctly, you will now be shown a dialog box for **Teradata Connection Settings.** Please continue
on to the :doc:`Teradata connection page </Teradata page>` for details.
