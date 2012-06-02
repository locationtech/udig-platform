


Expression viewer
~~~~~~~~~~~~~~~~~

Expression viewers are used in `Style Editor dialog`_ and `Transform
dialog`_ when a formula is used to calculate a value on a feature by
feature basis.

This is a powerful technique that can be used for on the fly styling,
or directly for data processing.



You can switch between appropriate options using the drop down arrow
on the right hand side. Depending on what the expression is for, and
what kind of information you are working with, different options will
be listed some of which are described below:


+ Constraint Query Language
+ Builder
+ RGB Color
+ Numbers


When defining an expression you can make use of the full range of
`Constraint Query Language`_ functions.


+ To concatenate several values into a single text output:

::

    Concatenate( NAME, ' ', CITY_CODE, ' Zone ', ZONE)





+ To smoothly interpolate between a min and a max value (used here to
  write out LEVEL as a percentage):

::

    Concatenate( Interpolate( LEVEL, 42, 0, 3046, 100, "linear"), "%" )




`Constraint Query Language`_

`Style Editor dialog`_
> <a href="Transform dialog.html" title="Transform dialog">Transform
dialog< a>



Constraint Query Language
=========================

This is a plain viewer offering simple constraint query language
input.

It does offer dynamic help with auto completion for:


+ Attributes are listed from the feature type you are working with >
  <img src="download
  attachments/14189115/CQLExpressionViewerAttribute.png"
  align="absmiddle" border="0"/>
+ Function are listed, with pop up function reference > <img
  src="download attachments/14189115/CQLExpressionViewerFunction.png"
  align="absmiddle" border="0"/>




Builder
=======

A friendly general purpose viewer offering constraint query language
input. Provides a list of attributes, operations and values that can
be inserted into your expression.





RGB Color
=========

The following expression viewers can only be used when defining
colours. They are often used when defining fill or stroke colour.





Numbers
=======

The following expression viewers can only be used when defining
numbers. An example would be defining fill opacity or line width.



.. _Constraint Query Language: Constraint Query Language.html
.. _Transform dialog: Transform dialog.html
.. _Style Editor dialog: Style Editor dialog.html


