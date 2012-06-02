


Feature Style Pages
~~~~~~~~~~~~~~~~~~~

When the `Style Editor dialog`_ is opened on a feature layer the
following pages are available.


+ Cache
+ Filter
+ Simple Feature
+ Simple Lines
+ Simple Points
+ Simple Polygons
+ Theme
+ XML




Cache
=====

Allows control of loading the image into memory.





Filter
======











Simple Feature
==============

Allows you to modify the feature using the old style Page and
attribute selector - for a more advanced feature style editor choose
the Simple Feature Type Page (see below) corresponding to the layer
type you have selected.






Simple Lines
============

The Simple Lines Page is only available for Line feature layers. When
you have a Line Feature Layer selected this will be the default Page
displayed where you can update the style of the Line Layer.



Preview, Groups and Rules
-------------------------

This area shows a preview of the current set of Rules associated with
the Line Features.



Style Properties
----------------

This area (shown only when a Rule is selected) will allow you to
change the General, Border, Labels and Filter Style.



Style List
----------

Allows you to import and export styles which you use often.




Simple Points
=============

The Simple Points Page is only available for Point feature layer. When
you have a Points Feature Layer selected this will be the default Page
displayed where you can update the style of the Points Layer.



Preview, Groups and Rules
-------------------------

This area shows a preview of the current set of Rules associated with
the Line Features.



Style Properties
----------------

This area (shown only when a Rule is selected) will allow you to
change the General, Border, Fill, Labels and Filter Style.



Style List
----------

Allows you to import and export styles which you use often.







Simple Polygons
===============

The Simple Polygons Page is only available for Polygon feature layers.
When you have a Polygon Feature Layer selected this will be the
default Page displayed where you can update the style of the Polygon
Layer.



Preview, Groups and Rules
-------------------------

This area shows a preview of the current set of Rules associated with
the Line Features.



Style Properties
----------------

This area (shown only when a Rule is selected) will allow you to
change the General, Border, Fill, Labels and Filter Style.



Style List
----------

Allows you to import and export styles which you use often.




Theme
=====

Allows you to choose from a selection of predefined theme's.




Category Definition
-------------------


+ Attribute: attribute used to define the categories
+ Classes: number of classes to categories the data into
+ Break: how to distributed the data into classes

    + Quantile: All classes have the same number of elements
    + Equal Interval: All classes have the same value range

+ Normalize: Attribute used to normalize the attribute. As an example
  POP_CNTRY may be normalized by SQKM to determine population density
+ Else: how to handle data values that cannot be categorized, often
  due to an invalid value.




Color Brewer Palette Selection
------------------------------

The uDig application includes palettes defined by the color brewer
project. These palettes have been experimentally tested on live
subjects.


+ Show:

    + Numerical / Sequential: palettes suitable for numeric values, a
      sequence of colors is used in order to communicate a smooth range of
      data. Higher values are brought to the attention of the eye.
    + Numerical / Diverging: palettes suitable for numerical values, a
      diverging sequence of colors is used in order to emphasis the high and
      low values.
    + Categorical: palettes suitable for categorical data, no value stands
      out more than any other.

+ Filters: palettes may be filter into those suitable for color blind,
  CRT monitors, LCD monitors, Projectors, the printed page and
  photocopying.
+ Palette: choose a palette from the list




Theme
-----


+ Opacity: used to set how much color is shown, the default of 50% is
  interesting when used over top of satellite imagery, a higher value is
  recommended for presentation or printing.
+ Reverse: reverse the order of the theme
+ Remove: remove the selected category
+ Suitability: quickly check for any warnings
+ Theme:

    + Color: You can manually change the color for a category
    + Label: You can change the label used to represent this category in
      the Legend Map Graphic
    + Values: You can change the range of values for which this category
      is applied





XML
===

This page is used to allow raw access to the xml used to express style
information. The XML format used is the **Style Layer Descriptor**
specification by the Open Geospatial Consortium.



Validate
--------

Press this button to check that your XML is valid.

`Style Layer Descriptor`_

.. _Style Layer Descriptor: Style Layer Descriptor.html
.. _Style Editor dialog: Style Editor dialog.html


