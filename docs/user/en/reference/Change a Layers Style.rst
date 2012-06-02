


Change a Layers Style
~~~~~~~~~~~~~~~~~~~~~



Change a Layers Style
~~~~~~~~~~~~~~~~~~~~~

The style of a layer may be changed by opening the Style Editor
dialog, in one of 3 ways:

Select a layer in the Layers View and...


+ Click the "Change Style" button
+ Layer > Change Style...
+ Right-click and choose "Change Style..."




Simple
~~~~~~

Modifications to the line border width and colour, polygon fills,
point markers, and text can be made on this page. Scale can also be
taken into account (thus hiding features or text at various zoom
levels).



The above image shows the inputs used to generate the map below. This
polygon layer illustrates voting areas with text representing the
number of votes cast, rotated to 315 degrees.



Text symbols for line strings have slightly different alignment
options:



The perpendicular offset (set to 7 in the above image), defines the
space between a line and its identifying text.





Theme
~~~~~

Thematic styling can be performed from the theme page, utilizing
attribute data to communicate more information in a map by using
colours.



The above image shows the inputs required to generate a theme
illustrating population density (in the image below). The inputs are:


+ Attribute: what to generate the theme from (eg. speed limit, votes,
  name)
+ Classes: number of colours to use in the theme
+ Break: method to use in generation

    + Quantile: equal number of features in each class
    + Equal Interval: each class has an equal width
    + Unique Values: one value per class, if possible

+ Normalize: for numerical values, the attribute to use for
  normalization (eg. attribute "population" normalized by "square
  kilometers" results in "population density")
+ Else: behaviour for null and infinite values ( *hide*, or isolate in
  *first* or *last* class)


Suitability options:


+ Show: filters the available palettes

    + All: does not filter
    + Numerical: for numerical data

        + Sequential: evenly spaced colours suitable for ranged data
        + Diverging: accentuates upper and lower boundaries (center values are
          neutral-coloured)

    + Categorical: for non-numerical data (text)

+ Toggle buttons: hide/show based on suitability for the colour-blind,
  CRT monitors, projectors, LCD monitors, colour printing, or
  photocopying.






XML
~~~

Should you need to do something a little more complex than what the
simple or theme pages offer, the XML page allows you to directly
modify the Styled Layer Descriptor document (this describes how you
would like your layer drawn). The SLD specification outlines the
format to use: `http://www.opengeospatial.org/docs/02-070.pdf`_



Style View
~~~~~~~~~~

Alternatively, you may use the deprecated "Simple" Style View:


#. Window > Show View > Style
#. Select the desired layer in the Layer View
#. Edit the attributes of the style. > <img src="download
   attachments/3600/style.jpg" align="absmiddle" border="0"/>
#. Click on the apply button:


`Layer`_
> <a href="Layers view.html" title="Layers view">Layers view< a>
> <a href="Style View.html" title="Style View">Style View< a>

.. _http://www.opengeospatial.org/docs/02-070.pdf: http://www.opengeospatial.org/docs/02-070.pdf
.. _Layer: Layer.html


