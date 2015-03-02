.. _what_is_new_1_4:

What is new for uDig 1.4
========================

.. contents:: :local:
   :depth: 1

License Change
--------------

The uDig project has changed to a dual BSD / EPL license.

.. figure:: /getting_started/walkthrough1/images/splash.png
   :align: center
   :alt:
   :figwidth: 80%

This license change has enabled the uDig project to
`apply <http://locationtech.org/proposals/user-friendly-desktop-internet-gis-udig>`_ to the Eclipse Foundation
`LocationTech <http://locationtech.org>`_ industry working group.

GeoScript Editor
----------------

uDig is pleased to directly offer a scripting environment for the first time. Scripting a powerful technique
in a Geospatial Information Systems allowing non-developers the ability to automate common tasks.

.. figure:: /images/geoscript_editor/GeoScript.png
   :align: center
   :alt: 
   
There is a quick tutorial (:doc:`../getting_started/GeoScript Introduction`) and a reference page for the :doc:`../reference/GeoScript editor`.

For more information and tutorials please checkout GeoScript at `geoscript.org <http://geoscript.org/>`_

Documents
---------

The :doc:`../reference/Document view` added to list resource and feature documents.

Attachments and links:

- Resource attachments: Attach files and web links to a layer for easy access
- Feature attachments:  Attach files and web links to individual features
- Attachments are copied in the same fashion as email attachments, alternatively both web and file links are supported (and are not copied).
- Out of the box support for Shapefile attachments, files are stored in a folder next to your shapefile

Feature Hotlinks:

- Configure an attribute to act as a *hotlinks* for file or website
- Or define custom action to open a local action or fire off a google search using an attribute value

.. figure:: /images/document_view/DocumentView.png
   :align: center
   :alt:
   :figwidth: 80%

For more information, check out the Tasks section on :doc:`../tasks/Working with Documents`.


Graticule
----------

A new Graticule map decoration has been added.

It is also possible to overlay grids in different coordinate systems:

.. figure:: /images/graticule_decoration/whats_new_graticule.png
  :align: center
  :width: 70%
  :alt:

And here the tasks that can get you started with the graticule:

* :doc:`../reference/Graticule Decoration` 
* :doc:`../tasks/Add new Graticule`
* :doc:`../tasks/Add multiple Graticules with different CRS`
