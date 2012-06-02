


GML
~~~

An XML document containing spatial data. Often these documents are
captured on disk as a file with the extension ".gml".


+ GML Files
+ Geography Markup Language


Please be aware that a "gml" file is actually a normal XML document;
it just happens to be a document is based on the "abstract" Geographic
Markup Language specifications.



GML Files
=========

GML files in udig can currently only be supported if they meet two
very strict restrictions:


+ filename.xsd - an xml schema document defining the "data product"
  being loaded
+ filename.gml - an xml document defined against the schema provided
  above


We only support GML2 based schemas with simple content at this time.
There is improved GML support for WFS 1.1 access; this has not been
packaged for offline use at this time.



Geography Markup Language
=========================

Most markup language specifications are published with the idea of
defining a document format that applications can use to share
information. I am afraid that "GML" handles things a little bit
differently - it provides an XML Schema that can be used as a starting
point when an organization defines a data product.

As such we really do need access to the description of the data
product in order to open up a "gml document".

`Geography Markup Language`_ (we support GML 2.1.2 at this time)

.. _Geography Markup Language: http://www.opengeospatial.org/standards/gml


