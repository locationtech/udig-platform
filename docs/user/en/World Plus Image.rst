World Plus Image
~~~~~~~~~~~~~~~~

To add a normal image, for example a jpg or a gif, you will need to provide a **world** file, a
small text file that says where the corners of the image is.

This page specifically covers the combination of a normal image format with a "world" file. A few
image formats have well known ways of encoding location information in the image "metadata" fields.
As an example a TIFF file with spatial metadata is known as a `GeoTIFF <GeoTIFF.html>`_.

Image Files
^^^^^^^^^^^

An image file is recognized as being in the "World plus Image" format if it is part of a file set
consisting of the following:

 

Example

Description

Image File

filename.jpeg

Contains the raster data

World File

filename.jgw

contains the "world file" information letting us know where to draw the raster on the map

PRJ File

filename.prj

Optional file that defines the coordinates used in the "world file" above

World and Projection Files for an Image
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The extension of a world file is the first and last letters of the image extension with w appended
at the end.

Format

Files

JEPG

jpeg, jgw with optional prj

TIFF

tiff, tfw with optional prj

The contents of a world file are as follows:

Line

Example

Definition

1

0.0359281435

Horizontal scale for a pixel

2

0.0000000000

Rotation for row, usually zero

3

0.0000000000

Rotation for column, usually zero

4

-0.0359281437

Vertical scale for pixel, usually negative as rasters count down from the upper left corner

5

-179.9820349282

Translation, usually "easting" location of the upper left pixel (ie column 0)

6

89.9820359281

Translation, usually "northing" location of the upper left pixel (ie row 0)

The Translation information can be thought of as the location of the upper left pixel, this locaiton
is provided in the units of the projections being used:

-  For lat/long this location should be in the range -180,180 -90,90
-  If your image is not in lat/long you will have to include a **.prj** file, which is a text file
   with the WKT definition of the coordinate system that should be used.

JPEG Example:
^^^^^^^^^^^^^

If your image is **world.jpg** then you need a **world.jgw** file and optionally a **world.prj**
file.

An example world.jgw

::

     
    0.0359281435
    0.0000000000
    0.0000000000
    -0.0359281437
    -179.9820349282
    89.9820359281

An example world.prj is:

::

     GEOGCS["GCS_WGS_1984", DATUM["WGS_1984", SPHEROID["WGS_1984",6378137,298.257223563]], PRIMEM["Greenwich",0], UNIT["Degree",0.017453292519943295]]
     

**Related reference**


* :doc:`ESRI World Image format`

* :doc:`http://geos.gsi.gov.il/vladi/FEFLOW/help/general/file\_format.html#tfw\_file`


