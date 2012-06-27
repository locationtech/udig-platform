File Formats
------------

uDig supports a number of common spatial file formats. Each format defines a way that spatial data
can be persisted to the file system.

Contents:

.. toctree::
   :maxdepth: 1

   GeoTIFF
   GML
   Shapefile
   World Plus Image


Handling of Files in uDig
~~~~~~~~~~~~~~~~~~~~~~~~~

When a file is added into the uDig application it shows up in the :doc:`Catalog view` as a :doc:`Service`. You can think of the file as providing
the service; you can open up the service entry to see the content being provided by the file.

While this devision does not make much sense for a shapefile; it is more useful when working with
rich formats like GML which can contain several kinds of content.

You should also keep in mind that the data is not imported into the uDig application; we are reading
it from disk each and every time. You may see increased performance if you bring data off network
drives onto your local machine.

GIS Data and the use of File Sets
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Please note that due to the age of the GIS industry many spatial file formats actually consist of a
group of files with the same name (and different extensions). Please be aware of this and make sure
to copy all the files in a "set" when moving your data around on disk.

Here are two quick examples:

-  The Shapefile file format where a "filename.shp", "filename.dbf" and "filename.prj" are used to
   capture vector information, the attributes associated with the shapes, and where the shapes
   should be drawn on the map.
-  The World plus Image format where "filename.jpeg", "filename".jgw" and "filename.prj" are used to
   represent raster data and define where it should be drawn on the map.

**Related concepts**

:doc:`Service`


**Related reference**

:doc:`Files page`

:doc:`Catalog view`

