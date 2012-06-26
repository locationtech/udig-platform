Filter viewer
#############

Filter viewers are used in :doc:`Style Editor dialog` and other areas
where selecting the content you wish to work with is important. A formula is used to select content
on a feature by feature basis.

This is a powerful technique that can be used for on the fly styling, or directly for data
processing.

.. figure:: /images/filter_viewer/CQLFilterViewer.png
   :align: center
   :alt: 

You can switch between appropriate options using the drop down arrow on the right hand side.
Depending on what content you are working with different options will be listed some of which are
described below:

* :doc:`Constraint Query Language`

* :doc:`Builder`

* :doc:`Include`


Common examples:

-  To include all content:

   ::

       INCLUDE

-  To exclude all content:

   ::

       EXCLUDE

-  You can compare against numeric, text or date values:

   ::

       ScaleRank >= 3

-  A wide range of comparison operators is available:

   ::

       ScaleRank BETWEEN 3 AND 7

-  Logical operators can also be used:

   ::

       NOT( NAME LIKE 'Can%' )

-  You can make use of the full range of :doc:`Constraint Query Language` function:

   ::

       LEVEL < log( POP_EST )

**Related concepts**

:doc:`Constraint Query Language`


**Related reference**

:doc:`Style Editor dialog`

:doc:`Transform dialog`


Constraint Query Language
=========================

This is a plain viewer offering simple constraint query language input.

.. figure:: /images/filter_viewer/CQLFilterViewer.png
   :align: center
   :alt: 

It does offer dynamic help with auto completion for:

-  Attributes are listed from the feature type you are working with
-  Function are listed, with pop up function reference

Builder
=======

A friendly general purpose viewer offering constraint query language input. Provides a list of
attributes, operations and values that can be inserted into your filter.

.. figure:: /images/filter_viewer/BuilderFilterViewer.png
   :align: center
   :alt: 

Include
=======

Offers an easy way to enable or disable a rule. This is not a general purpose viewer.

.. figure:: /images/filter_viewer/EnableFilterViewer.png
   :align: center
   :alt: 

-  Enable: To include all features.

   ::

       INCLUDE

-  Disable: To ignore all features.

   ::

       EXCLUDE

This viewer is often used in the :doc:`Style Editor dialog` to toggle a
Rule on or off.
