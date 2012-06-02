


Filter viewer
~~~~~~~~~~~~~

Filter viewers are used in `Style Editor dialog`_ and other areas
where selecting the content you wish to work with is important. A
formula is used to select content on a feature by feature basis.

This is a powerful technique that can be used for on the fly styling,
or directly for data processing.



You can switch between appropriate options using the drop down arrow
on the right hand side. Depending on what content you are working with
different options will be listed some of which are described below:


+ Constraint Query Language
+ Builder
+ Include


Common examples:


+ To include all content:

::

    INCLUDE





+ To exclude all content:

::

    EXCLUDE





+ You can compare against numeric, text or date values:

::

    ScaleRank >= 3





+ A wide range of comparison operators is available:

::

    ScaleRank BETWEEN 3 AND 7





+ Logical operators can also be used:

::

    NOT( NAME LIKE 'Can%' )





+ You can make use of the full range of `Constraint Query Language`_
  function:

::

    LEVEL < log( POP_EST )




`Constraint Query Language`_

`Style Editor dialog`_
> <a href="Transform dialog.html" title="Transform dialog">Transform
dialog< a>



Constraint Query Language
=========================

This is a plain viewer offering simple constraint query language
input.



It does offer dynamic help with auto completion for:


+ Attributes are listed from the feature type you are working with
+ Function are listed, with pop up function reference




Builder
=======

A friendly general purpose viewer offering constraint query language
input. Provides a list of attributes, operations and values that can
be inserted into your filter.





Include
=======

Offers an easy way to enable or disable a rule. This is not a general
purpose viewer.




+ Enable: To include all features.

::

    INCLUDE


+ Disable: To ignore all features.

::

    EXCLUDE




This viewer is often used in the `Style Editor dialog`_ to toggle a
Rule on or off.

.. _Constraint Query Language: Constraint Query Language.html
.. _Style Editor dialog: Style Editor dialog.html


