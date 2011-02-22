uDIG Application
----------------
This java project is used to share common dependencies between the plug-ins
comprising the uDig Application.

It contains the "latest" jars at the time of writing:

We have chosen not to include the version number information, this will allow
svn's binary diff facitlities to track changes beween releases.

We relize that this will not result in a large savings given the nature
of jar compression.

The lib directory contains:
-----------+-------+----------------
jts        | 1.4.0 | http://www.vividsolutions.com/jts/download.htm
junit      |       |
junit-src  |       |
wmc        | 0.1.x | pending
wfc        | 0.1.x | pending
gml        | 0.1.x | pending
render     | 0.1.x | pending
-----------+-------+----------------

When using this project in your own Eclipse workspace you
can juggle between using a jar from this lib directory,
, or directly depending on its build output (assuming you have
it in your workspace).

As an example you can make this project depend on "gtbuild" and
turn of exporting of all the geotools-*.jar files, replacing them
with an export of the gtbuild builds results.
