


PostGIS
~~~~~~~

PostGIS adds support for geographic objects to the `PostgreSQL
database`_. In effect, PostGIS "spatially enables" the PostgreSQL
server, allowing it to be used as spatial database for geographic
information systems.

PostGIS follows the OpenGIS `Simple Features Specification for SQL`_
(SFSQL).



Geometry Objects
----------------

PostGIS supports the range of geometry objects:
Geometry Object WKT Example Point POINT(0 0) LineString LINESTRING(0
0,1 1,1 2) Polygon POLYGON((0 0,4 0,4 4,0 4,0 0),(1 1, 2 1, 2 2, 1 2,1
1)) MultiPoint MULTIPOINT(0 0,1 2) MultiLineString MULTILINESTRING((0
0,1 1,1 2),(2 3,3 2,5 4)) MiltiPolygon MULTIPOLYGON(((0 0,4 0,4 4,0
4,0 0),(1 1,2 1,2 2,1 2,1 1)), ((-1 -1,-1 -2,-2 -2,-2 -1,-1 -1)))
GeometryCollection GEOMETRYCOLLECTION(POINT(2 3),LINESTRING((2 3,3
4)))
These geometry objects are in accordance with the SFSQL specification
mentioned above; the examples are given using the "Well Known Text"
format - the same format used for selections in uDig (see `Constraint
Query Language`_ )



Required PostGIS Tables
-----------------------

When you install the PostGIS extension into your database two tables
will be created:


+ geometry_columns - records what spatial data is stored in which
  table; along with some information about the kind of geometry object
  and what SRID is expected
+ spatial_ref_sys - records definitions of spatial reference systems
  according to SRID


For uDig to recognize your database you will need to have at least one
entry in your geometry_columns table.


+ `PostGIS page`_
+ `http://postgis.refractions.net/`_
+ `http://www.postgresql.com/`_
+ `Introduction to PostGIS`_ (FOSS4G 2007 workshop)


.. _Constraint Query Language: Constraint Query Language.html
.. _http://www.postgresql.com/: http://www.postgresql.com/
.. _http://postgis.refractions.net/: http://postgis.refractions.net/
.. _PostGIS page: PostGIS page.html
.. _Simple Features Specification for SQL: http://www.opengis.org/docs/99-049.pdf
.. _Introduction to PostGIS: http://www.foss4g2007.org/workshops/W-04/


