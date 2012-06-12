Spatial Operations
##################

Introduction
============

This is the root page of the user documentation for the **uDig** *Spatial Operations* extensions.
`Axios <http://www.axios.es>`_ is providing under the `Spatial Operations and Editing
Tools <http://udig.refractions.net/confluence/display/COM/Spatial+Operations+and+Editing+Tools>`_
project promoted by the **Diputación Foral de Gipuzkoa**, `**Departamento de Movilidad y Ordenación
del Territorio** <http://b5m.gipuzkoa.net/web5000/>`_.

These extensions comprise a set of Spatial Operations, where each of them work over the features of
one or more input layers and generally populate a new one with the features resulting of applying
the specific Spatial Operation.

+---------------------------------------------------------------------------------------------------+------------------------------------------------------------------------------------------------------------------------------------------+
| To enable the *Spatial Operations* view, go to Window->Show View->Other as shown in *Figure 1*.   | The *Show View* dialog will pop up, then select the *Spatial Operations* item from the tree view as shown in *Figure 2* and press *OK*   |
+---------------------------------------------------------------------------------------------------+------------------------------------------------------------------------------------------------------------------------------------------+
| |image2|                                                                                          | |image3|                                                                                                                                 |
+---------------------------------------------------------------------------------------------------+------------------------------------------------------------------------------------------------------------------------------------------+
| **Figure 1 Open others views**                                                                    | **Figure 2 Open the Spatial Operations View**                                                                                            |
+---------------------------------------------------------------------------------------------------+------------------------------------------------------------------------------------------------------------------------------------------+

Once you told uDig to open the *Spatial Operations* View, it'll be shown as in *Figure 3*.
 :doc:`|image4|`

 **Figure 3 Spatial Operations View**

.. figure:: images/icons/emoticons/information.gif
   :align: center
   :alt: 

Remember Views in **uDig**, as in most RCP based applications are very flexible. So if the standard
layout does not seem appropriate for you, try changing its position by dragging the tab title,
setting it as a *Fast View* so its only shown when needed, or set is as a *Detached* view so it
behaves like a dialog.

Spatial Operations
------------------

Check the links bellow for specific user documentation for each of the available extensions.

-  `Buffer Operation <Buffer%20Operation.html>`_ — Computes a buffer area around the selected
   geometries.
-  `Clip Operation <Clip%20Operation.html>`_ — Computes the geometric difference between two Layers.
-  `Dissolve Operation <Dissolve%20Operation.html>`_ — Creates a new layer containing the Features
   grouped and merged by the selected Property.
-  `Fill Operation <Fill%20Operation.html>`_ — Creates a new features using the boundary of polygons
   and the provided LineStrings
-  `Hole Cut Operation <Hole%20Cut%20Operation.html>`_ — Makes a hole inside a polygon feature using
   a lineString feature
-  `Intersect Operation <Intersect%20Operation.html>`_ — Computes the geometric intersection between
   two layers.
-  `Polygon to Line Operation <Polygon%20to%20Line%20Operation.html>`_ — Transform a polygon layer
   into a LineString layer
-  `Spatial Join Operation <Spatial%20Join%20Operation.html>`_ — Creates a new layer containing the
   features from the second layer that match the indicated spatial relation.
-  `Split Operation <Split%20Operation.html>`_ — Split a polygon layer using a LineString layer

:doc:`Edit`


.. |image0| image:: /images/spatial_operations/sp_1.png
.. |image1| image:: /images/spatial_operations/sp_2.png
.. |image2| image:: /images/spatial_operations/sp_1.png
.. |image3| image:: /images/spatial_operations/sp_2.png
.. |image4| image:: download/thumbnails/2719875/SOoverview.png
