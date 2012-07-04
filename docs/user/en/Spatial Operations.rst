Spatial Operations
##################

Introduction
============

This is the root page of the user documentation for the **uDig** *Spatial Operations* extensions.
`Axios <http://www.axios.es>`_ is providing under the 
`Spatial Operations and Editing Tools <http://udig.refractions.net/confluence/display/COM/Spatial+Operations+and+Editing+Tools>`_ project promoted by the **Diputación Foral de Gipuzkoa**, `Departamento de Movilidad y Ordenación del Territorio <http://b5m.gipuzkoa.net/web5000/>`_.

These extensions comprise a set of Spatial Operations, where each of them work over the features of
one or more input layers and generally populate a new one with the features resulting of applying
the specific Spatial Operation.

.. list-table:: 
    :header-rows: 0 
    :class: center 

    * - To enable the *Spatial Operations* view,
        go to :menuselection:`Window --> Show View --> Other`
        as shown in *Figure 1*. 
      - The *Show View* dialog will pop up, then
        select the *Spatial Operations* item from the tree view
        as shown in *Figure 2* and press *OK*

    * -  .. image:: images/spatial_operations/sp_1.png
      -  .. image:: images/spatial_operations/sp_2.png

    * - **Figure 1 Open others views**
      - **Figure 2 Open the Spatial Operations View**

Once you told uDig to open the :guilabel:`Spatial Operations` View, it'll be shown as in *Figure 3*.

  .. figure:: /images/spatial_operations/SOoverview.png
     :width: 60%

     **Figure 3 Spatial Operations View**


.. tip::
   Remember Views in **uDig**, as in most RCP based applications are very flexible. So if the standard
   layout does not seem appropriate for you, try changing its position by dragging the tab title,
   setting it as a *Fast View* so its only shown when needed, or set is as a *Detached* view so it
   behaves like a dialog.


Spatial Operations
------------------

Check the links bellow for specific user documentation for each of the available extensions.

.. toctree::
   :hidden:

   Buffer Operation
   Clip Operation
   Dissolve Operation
   Fill Operation
   Hole Cut Operation
   Intersect Operation
   Polygon to Line Operation
   Spatial Join Operation
   Split Operation


-  :doc:`Buffer Operation` — Computes a buffer area around the selected
   geometries.
-  :doc:`Clip Operation` — Computes the geometric difference between two Layers.
-  :doc:`Dissolve Operation` — Creates a new layer containing the Features
   grouped and merged by the selected Property.
-  :doc:`Fill Operation` — Creates a new features using the boundary of polygons
   and the provided LineStrings
-  :doc:`Hole Cut Operation` — Makes a hole inside a polygon feature using
   a lineString feature
-  :doc:`Intersect Operation` — Computes the geometric intersection between
   two layers.
-  :doc:`Polygon to Line Operation` — Transform a polygon layer
   into a LineString layer
-  :doc:`Spatial Join Operation` — Creates a new layer containing the
   features from the second layer that match the indicated spatial relation.
-  :doc:`Split Operation` — Split a polygon layer using a LineString layer

