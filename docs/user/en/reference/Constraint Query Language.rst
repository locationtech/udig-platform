


Constraint Query Language
~~~~~~~~~~~~~~~~~~~~~~~~~

The Constraint Query Language is used to define expressions and
filters in several parts of the uDig application.


+ Filters

    + Comparisons
    + Text
    + Null
    + Exists
    + Between
    + Spatial Relationships
    + Time
    + Compound Attributes

+ Expressions

    + Literals
    + Geometry
    + Attribute
    + Math
    + Functions

+ Extended CQL

    + Feature ID



The CQL syntax is defined as part of the `OGC Catalog specification`_
(in much the same way the well-known-text representation of geometry
is defined as part of the Simple Feature for SQL specification).



Filters
=======

A **Filters** is used to test content, similar in spirit to a WHERE
statement in SQL. Filters are used to define the selected Features in
a uDig layer.



Comparisons
-----------


::

    CITY = 'Nelson'



::

    ATTR1 < (1 + ((2 / 3) * 4))



::

    ATTR1 < abs(ATTR2)



::

    ATTR1 < 10 AND ATTR2 < 2 OR ATTR3 > 10




Text
----


::

    ATTR1 LIKE 'abc%'



::

    ATTR1 NOT LIKE 'abc%'




Null
----


::

    ATTR1 IS NULL



::

    ATTR1 IS NOT NULL




Exists
------


::

    ATTR1 EXISTS



::

    ATTR1 DOES-NOT-EXIST




Between
-------


::

    ATTR1 BETWEEN 10 AND 20




Spatial Relationships
---------------------


::

    CONTAINS(ATTR1, POINT(1 2))



::

    CROSS(ATTR1, LINESTRING(1 2, 10 15))



::

    INTERSECT(ATTR1, GEOMETRYCOLLECTION (POINT (10 10),POINT (30 30),LINESTRING (15 15, 20 20)) )



::

    BBOX(ATTR1, 10,20,30,40)



::

    DWITHIN(ATTR1, POINT(1 2), 10, kilometers)



::

    BBOX(ATTR1, 10,20,30,40)




Time
----

Before a date.


::

    ATTR1 BEFORE 2006-11-30T01:30:00Z


Before a period


::

    ATTR1 BEFORE 2006-11-30T01:30:00Z/2006-12-31T01:30:00Z


After a date.


::

    ATTR1 AFTER 2006-11-30T01:30:00Z


After a period


::

    ATTR1 AFTER 2006-11-30T01:30:00Z/2006-12-31T01:30:00Z


Temporal predicate with dutation (ten day after 2006-11-30T01:30:00Z )


::

    ATTR1 AFTER 2006-11-30T01:30:00Z/P10D



::

    ATTR1 AFTER 2006-11-30T01:30:00Z/T10H


During predicate


::

    ATTR1 DURING 2006-11-30T01:30:00Z/2006-12-31T01:30:00Z




Compound Attributes
-------------------


::

    gmd:MD_Metadata.gmd:identificationInfo.gmd:MD_DataIdentification.gmd:abstract LIKE  'abc%'




Expressions
===========

Expressions are used to extract a value similar in spirit to a SELECT
statement in SQL. Unlike SQL expressions are strictly untyped; the
following produces the same result:


+ sin( 0 )
+ sin( 0.0 )
+ sin( '0' )


In many cases you will see several functions defined with similar
names; differing only in how they interpret the provided argument
expressions. The functions functions that produce a boolean value are
**very** similar to a filter.



Literals
--------


::

    1



::

    3.14159



::

    'abc'



::

    2006-11-30T01:30:00Z




Geometry
--------

Geometry literals are provided in Well Known Text format:


::

    POINT(1 2)



::

    LINESTRING (15 15, 20 20)


This is the same format used by `PostGIS`_ and other applications.



Attribute
---------


::

    NAME



::

    prefix:name




Math
----


::

    1 + 1



::

    1 + 2 * 3



::

    (1 + 2) * 3




Functions
---------


::

    sin( pi() / 4 )



::

    sin( toRadians( ANGLE ) )



::

    getX( pointN( THE_GEOM, 0 ) )



::

    area( THE_GEOM )



::

    ID()


Function List:


::

    double Area(Geometry)   // alternate implementation
    double area(Geometry)
    long abs( number )      // number treated as long
    double abs_2( number )  // number treated as double
    float abs_3( number )   // number treated as float
    int abs_4( number )    // number treated as int
    double acos( cos )     
    double asin( sin )     
    double atan( tan )     
    double atan2( x, y )    // convert x, y to theta
    boolean between( value, min, max )
    boundary(Geometry)
    boundaryDimension(Geometry)
    buffer(Geometry, double)
    bufferWithSegments(Geometry, double, int)
    double ceil( number )
    centroid(Geometry)
    contains(Geometry, Geometry)
    convexHull(Geometry)
    double cos( radians ) 
    crosses(Geometry, Geometry)
    difference(Geometry, Geometry)
    dimension(Geometry)
    disjoint(Geometry, Geometry)
    distance(Geometry, Geometry)
    double2bool(double)
    endPoint(Geometry)
    envelope(Geometry)
    equalsExact(Geometry, Geometry)
    equalsExactTolerance(Geometry, Geometry, double)
    equalTo(Object, Object)
    double exp( number )
    exteriorRing(Geometry)
    double floor( number )
    String geometryType(Geometry)
    Geometry geomFromWKT(String)
    double geomLength(Geometry)
    Geometry getGeometryN(Geometry, int)
    double getX( Point )
    double getY( Point )
    boolean greaterEqualThan(Object, Object)
    boolean greaterThan(Object, Object)
    double IEEEremainder( dividend, divisor )
    value if_then_else(boolean, value, value )
    in2(value, Object, Object) // tests to is if value is equal to one of the listed objects
    in3(value, Object, Object, Object)
    in4(value, Object, Object, Object, Object)
    in5(value, Object, Object, Object, Object, Object)
    in6(value, Object, Object, Object, Object, Object, Object)
    in7(value, Object, Object, Object, Object, Object, Object, Object)
    in8(value, Object, Object, Object, Object, Object, Object, Object, Object)
    in9(value, Object, Object, Object, Object, Object, Object, Object, Object, Object)
    in10(value, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object)
    boolean int2bbool(int)
    double int2ddouble(int)
    Point interiorPoint(Geometry)
    LinearRing interiorRingN(Geometry, int)
    Geometry intersection(Geometry, Geometry)
    boolean intersects(Geometry, Geometry)
    boolean isClosed(Geometry)
    boolean isEmpty(Geometry)
    isLike(String, String)
    isNull(Object)
    isRing(Geometry)
    isSimple(Geometry)
    isValid(Geometry)
    isWithinDistance(Geometry, Geometry, double)
    lessEqualThan(Object, Object)
    lessThan(Object, Object)
    double log( number )
    double max( number, number )
    float max_2( number, number )
    int max_3( number, number )
    long max_4( number, number )
    double min( number, number )
    float min_2( number, number )
    int min_3( number, number )
    long min_4( number, number )
    not(boolean)
    notEqualTo(Object, Object)
    numGeometries(Geometry)
    numInteriorRing(Geometry)
    numPoints(Geometry)
    overlaps(Geometry, Geometry)
    parseBoolean(String)
    parseDouble(String)
    parseInt(String)
    pointN(Geometry, int)
    double pow( base, exponent )
    double random()
    String relate(Geometry, Geometry)
    boolean relatePattern(Geometry, Geometry, String)
    double rint( number ) // closest integer
    int round( number )
    long round_2( number )
    int roundDouble( number ) // number is treated as a double
    double sin( radians )
    double sqrt( number )
    startPoint(Geometry)
    strConcat(String, String)
    strEndsWith(String, String)
    strEqualsIgnoreCase(String, String)
    strIndexOf(String, String)
    strLastIndexOf(String, String)
    strLength(String)
    strMatches(String, String)
    strStartsWith(String, String)
    strSubstring(String, int, int)
    strSubstringStart(String, int)
    strTrim(String)
    symDifference(Geometry, Geometry)
    double tan( radians )
    touches(Geometry, Geometry)
    double toDegrees( radians )
    double toRadians( degrees )
    String toWKT(Geometry)
    union(Geometry, Geometry)
    within(Geometry, Geometry)




Extended CQL
============

The common query language cannot quite do everything we would like it
to. The following extensions are not strictly common query language
(so please do not expect them to work with other applications).



Feature ID
----------

You can select against a feature identifier using:


::

    IN ('river.1', 'river.2')


Or if you have an integer type as feature id:


::

    IN (300, 301)



+ `Selection using CQL`_



+ `Table view`_
+ Reshape Operation
+ `OGC Catalog Specification`_


.. _OGC Catalog Specification: http://www.opengeospatial.org/standards/cat
.. _PostGIS: PostGIS.html
.. _Table view: Table view.html
.. _Selection using CQL: Selection using CQL.html


