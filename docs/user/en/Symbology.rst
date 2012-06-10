Symbology
#########

Symbology is the cartographic practice of representing information using symbols (and colours) on a
Map. The `Legend <Legend.html>`_ offers a quick summery of the symbology used for a
`Map <Map.html>`_, often presenting important layers of information in groups for easy reference.

For Layers with complicated symbology (perhaps listing icons, or changing colour based on attribute)
the symbology will change on a feature by feature basis to reflect the values being communicated by
the map.

General Cartography
-------------------

Maps produced for general use showing a wide range of features. The maps are often produced against
a set standard as part of a series.

The definition of the symbology used is provided by an organisation or government body; often with
very exacting requirements that can difficult to reproduce exactly!

Examples:

-  Maps used for emergency response will often dictate the set of symbols used (in keeping with the
   training of the operators)
-  Maps used for nautical navigation where lives are at risk based on the exact interpretation of
   the map provided. You will often find datasets containing a disclaimer that their information is
   not suitable for this purpose.

Thematic Cartography
--------------------

This is the most common use for a desktop mapping application such as uDig. In this case the map is
produced in order communicate information around a specific dataset; with a map of the physical
location serving as a background.

-  Choropleth: differences in colour or shading used qualitative differences in an attribute.

   -  Useful when the polygons are all roughly the same size; or you run the risk of making larger
      polygons appear more important.
   -  A Dasymetric map is the opposite of this where the features are generated in order all be a
      similar size.

-  Proportional Symbol: Change point symbol size based on an attribute to illustrate relative
   values. \*\* Remember when mapping an attribute to symbol size (i.e. radius) the visual effect is
   not linear and you may wish to adjust accordingly.
-  Contour: A great way to render raster values such as height or atmospheric pressure where lines
   are drawn along the edge of value changes revealing shapes in the underling data; and the
   distance between these lines showing a rapid change in value.
-  Dot: used to represent individual observations or measurements

These maps are often used for purposes such as exploring data or scientific visualisation. Depending
on the dataset and information being communicated a number of techniques may be employed.

**Related concepts**


:doc:`Legend`

 :doc:`Cartography - Map Type (wikipedia)`

 :doc:`Thematic map (wikipedia)`

