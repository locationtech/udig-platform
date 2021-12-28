Rendering Preferences
=====================

There are a number of preferences that control the performance of rendering maps and appearance of
the resulting maps. Often the preferences are a trade-off between performance and appearance.

.. figure:: /images/rendering_preferences/renderpreferences.png
   :align: center
   :alt: 

Contents:

* :ref:`preferences-page-rendering-wms`


General Preferences
-------------------

-  Use anti-aliasing - When anti-aliasing is on the map is much smoother and accurate than with it
   off, however it is quite expensive so performance is much worse.
   Default anti-aliasing is off. However if features that should connect appear to be disconnected
   at small zoom levels then turn on antialiasing as it may be an aliasing issue.
-  Render transparencies - When unchecked transparencies in the styles will be ignored.
   Transparencies take slightly longer to render.
   Default is on.

.. _preferences-page-rendering-wms:
   
WMS Preferences
---------------

.. figure:: /images/wms_preferences/wmspreferences.png
   :align: center
   :alt: 

Available Preferences
`````````````````````

-  Image order - Not all Web Map Servers provide all image types as output images. The image order
   sets the order in which the image types are prioritized. For example png is usually preferred
   over jpg because it has an alpha channel so layers below the wms layer can be seen through a png
   (in transparent locations) but not through a jpg.
   However, on the macintosh certain png images can not be decoded so gif is often preferred over a
   png images on macintosh computers.
   As stated this is an advanced option so do not modify it unless you understand the image formats
   and the ramifications of using one image type over another.

Other Preferences
`````````````````

.. _project_preferences-hide-renderer-job:

Hide render jobs in Progress View
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
The option **HIDE_RENDER_JOB** allows to control, whether executed jobs are shown in Progress View.
If this option is available and set to **true**, the jobs are not shown. Otherwise, which is the
default, jobs are shown in Progress View.

This option is not intended to be set or changed at runtime.
Use *.options* file in installation folder and include this file for instance with the Eclipse
command line option -pluginCustomization to configure application behavior:

.. code-block::
   :caption: .options file

    org.locationtech.udig.project/HIDE_RENDER_JOB=true

.. _project_preferences-advanced-projection-support:

Advanced projection support
~~~~~~~~~~~~~~~~~~~~~~~~~~~
The option **ADVANCED_PROJECTION_SUPPORT** allows to control, whether advanced projection support and
continuous map wrapping are enabled for feature renderings such as shape files.
If this option is available and set to **true**, then features such as shape files are wrapped
continuously to the left and the right. Otherwise, which is the default, those features are
rendered only once.

This option is not intended to be set or changed at runtime.
Use *.options* file in installation folder and include this file for instance with the Eclipse
command line option -pluginCustomization to configure application behavior:

.. code-block::
   :caption: .options file

    org.locationtech.udig.project/ADVANCED_PROJECTION_SUPPORT=true

**Related reference**

:doc:`Catalog Preferences`

:doc:`Project Preferences`

:doc:`Tool Preferences`


