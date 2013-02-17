Expression viewer
#################

Expression viewers are used in :doc:`Style Editor dialog` and :doc:`Transform dialog` when a formula is used to calculate a value on a feature by
feature basis.

This is a powerful technique that can be used for on the fly styling, or directly for data
processing.

.. figure:: images/expression_viewer/CQLExpressionViewer.png
   :align: center
   :alt: 

You can switch between appropriate options using the drop down arrow on the right hand side.
Depending on what the expression is for, and what kind of information you are working with,
different options will be listed some of which are described below:

* `Constraint Query Language`_

* `Builder`_

* `RGB Color`_

* `Numbers`_


When defining an expression you can make use of the full range of :doc:`/concepts/Constraint Query Language` functions.

-  To concatenate several values into a single text output:

   ::

       Concatenate( NAME, ' ', CITY_CODE, ' Zone ', ZONE)

-  To smoothly interpolate between a min and a max value (used here to write out LEVEL as a
   percentage):

   ::

       Concatenate( Interpolate( LEVEL, 42, 0, 3046, 100, "linear"), "%" )

**Related concepts**

:doc:`concepts/Constraint Query Language`


**Related reference**

:doc:`Style Editor dialog`

:doc:`Transform dialog`


Constraint Query Language
=========================

This is a plain viewer offering simple constraint query language input.

It does offer dynamic help with auto completion for:

-  Attributes are listed from the feature type you are working with

   .. image:: images/expression_viewer/CQLExpressionViewerAttribute.png

-  Function are listed, with pop up function reference
   
   .. image:: images/expression_viewer/CQLExpressionViewerFunction.png

Builder
=======

A friendly general purpose viewer offering constraint query language input. Provides a list of
attributes, operations and values that can be inserted into your expression.

.. figure:: images/expression_viewer/BuilderExpressionViewer.png
   :align: center
   :alt: 

RGB Color
=========

The following expression viewers can only be used when defining colours. They are often used when
defining fill or stroke colour.

.. figure:: images/expression_viewer/RGBExpressionViewer.png
   :align: center
   :alt: 

Numbers
=======

The following expression viewers can only be used when defining numbers. An example would be
defining fill opacity or line width.

.. figure:: images/expression_viewer/NumberExpressionViewer.png
   :align: center
   :alt: 
