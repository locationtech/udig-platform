Expression viewer
#################

Expression viewers are used in `Style Editor dialog <Style%20Editor%20dialog.html>`_ and `Transform
dialog <Transform%20dialog.html>`_ when a formula is used to calculate a value on a feature by
feature basis.

This is a powerful technique that can be used for on the fly styling, or directly for data
processing.

.. figure:: /images/expression_viewer/CQLExpressionViewer.png
   :align: center
   :alt: 

You can switch between appropriate options using the drop down arrow on the right hand side.
Depending on what the expression is for, and what kind of information you are working with,
different options will be listed some of which are described below:

* :doc:`Constraint Query Language`

* :doc:`Builder`

* :doc:`RGB Color`

* :doc:`Numbers`


When defining an expression you can make use of the full range of `Constraint Query
Language <Constraint%20Query%20Language.html>`_ functions.

-  To concatenate several values into a single text output:

   ::

       Concatenate( NAME, ' ', CITY_CODE, ' Zone ', ZONE)

-  To smoothly interpolate between a min and a max value (used here to write out LEVEL as a
   percentage):

   ::

       Concatenate( Interpolate( LEVEL, 42, 0, 3046, 100, "linear"), "%" )

**Related concepts**


:doc:`Constraint Query Language`


**Related reference**


:doc:`Style Editor dialog`

 :doc:`Transform dialog`


Constraint Query Language
=========================

This is a plain viewer offering simple constraint query language input.

It does offer dynamic help with auto completion for:

-  Attributes are listed from the feature type you are working with
    |image0|
-  Function are listed, with pop up function reference
    |image1|

Builder
=======

A friendly general purpose viewer offering constraint query language input. Provides a list of
attributes, operations and values that can be inserted into your expression.

.. figure:: /images/expression_viewer/BuilderExpressionViewer.png
   :align: center
   :alt: 

RGB Color
=========

The following expression viewers can only be used when defining colours. They are often used when
defining fill or stroke colour.

.. figure:: /images/expression_viewer/RGBExpressionViewer.png
   :align: center
   :alt: 

Numbers
=======

The following expression viewers can only be used when defining numbers. An example would be
defining fill opacity or line width.

.. figure:: /images/expression_viewer/NumberExpressionViewer.png
   :align: center
   :alt: 

.. |image0| image:: /images/expression_viewer/CQLExpressionViewerAttribute.png
.. |image1| image:: /images/expression_viewer/CQLExpressionViewerFunction.png
