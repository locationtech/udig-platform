Fonts and Imagery
=================

Where possible these images are taken directly from our coderepository, and represent the **live**
images used by the application right now.

References:

* `<http://wiki.eclipse.org/User_Interface_Guidelines#Design>`_ (amazing background detail on how to do this stuff)
* `<http://projects.opengeo.org/geosilk/>`_ (match core icon concepts for geospatial concepts)
* `http://en.wikipedia.org/wiki/Gill\_Sans <http://en.wikipedia.org/wiki/Gill_Sans>`_

Fonts
^^^^^

The uDig splash screen and tagline are rendered with GillSans Light:

* `<http://en.wikipedia.org/wiki/Gill_Sans>`_

The color used is:

-  #0074bc
-  RGB: 0 116 188
-  HSB: 203 100 74

Example Use:

-  /../plugins/net.refractions.udig/nl/en/splash.bmp

In the above image the font heights are:

-  22.08 pt
-  8.03 pt
-  5.02 pt

Imagery
=======

We store are "raw" materials used when creating new images in version control:

-  net.refractions.udig.dev

Location of Welcome graphics:

-  net.refractions.udig.ui/

Product Branding:

-  net.refractions.udig/

Core Icon Concepts
^^^^^^^^^^^^^^^^^^

UDIG constructs with well defined representations:

+---------------+---------------------------+---------------+------------------------+
| |fai_image01| |     udig logo             | |fai_image02| | project file           |
+---------------+---------------------------+---------------+------------------------+
| |fai_image03| |     feature               | |fai_image04| | grid                   |
+---------------+---------------------------+---------------+------------------------+
| |fai_image05| |     datastore             | |fai_image06| | grid coverge exchange  |
+---------------+---------------------------+---------------+------------------------+
| |fai_image07| |     web feature server    | |fai_image08| | web map server         |
+---------------+---------------------------+---------------+------------------------+
| |fai_image09| |     feature file          | |fai_image10| | grid file              |
+---------------+---------------------------+---------------+------------------------+
| |fai_image11| |     map file              | |fai_image12| | pixel                  |
+---------------+---------------------------+---------------+------------------------+
| |fai_image13| |     repository            | |fai_image14| | web registry service   |
+---------------+---------------------------+---------------+------------------------+
| |fai_image15| |     server                | |fai_image16| | database               |
+---------------+---------------------------+---------------+------------------------+
| |fai_image17| |     project               | |fai_image18| | project missing        |
+---------------+---------------------------+---------------+------------------------+
| |fai_image19| |     map                   | |fai_image20| | empty map              |
+---------------+---------------------------+---------------+------------------------+
| |fai_image21| |     map layer             | |fai_image22| | map layer empty        |
+---------------+---------------------------+---------------+------------------------+
| |fai_image23| |     map folder            | |fai_image24| | map folder missing     |
+---------------+---------------------------+---------------+------------------------+
| |fai_image25| |     paper                 | |fai_image26| | page template          |
+---------------+---------------------------+---------------+------------------------+

.. |fai_image01| image:: /images/fonts_and_imagery/udig_logo16.gif
.. |fai_image02| image:: /images/fonts_and_imagery/project_file_obj.gif
.. |fai_image03| image:: /images/fonts_and_imagery/feature_obj.gif
.. |fai_image04| image:: /images/fonts_and_imagery/grid_obj.gif
.. |fai_image05| image:: /images/fonts_and_imagery/datastore_obj.gif
.. |fai_image06| image:: /images/fonts_and_imagery/gce_obj.gif
.. |fai_image07| image:: /images/fonts_and_imagery/wfs_obj.gif
.. |fai_image08| image:: /images/fonts_and_imagery/wms_obj.gif
.. |fai_image09| image:: /images/fonts_and_imagery/feature_file_obj.gif
.. |fai_image10| image:: /images/fonts_and_imagery/grid_file_obj.gif
.. |fai_image11| image:: /images/fonts_and_imagery/map_file_obj.gif
.. |fai_image12| image:: /images/fonts_and_imagery/pixel_obj.gif
.. |fai_image13| image:: /images/fonts_and_imagery/repository_obj.gif
.. |fai_image14| image:: /images/fonts_and_imagery/wrs_obj.gif
.. |fai_image15| image:: /images/fonts_and_imagery/server_obj.gif
.. |fai_image16| image:: /images/fonts_and_imagery/database_obj.gif
.. |fai_image17| image:: /images/fonts_and_imagery/project_obj.gif
.. |fai_image18| image:: /images/fonts_and_imagery/project_nonexist_obj.gif
.. |fai_image19| image:: /images/fonts_and_imagery/map_obj.gif
.. |fai_image20| image:: /images/fonts_and_imagery/map_empty_obj.gif
.. |fai_image21| image:: /images/fonts_and_imagery/layer_obj.gif
.. |fai_image22| image:: /images/fonts_and_imagery/layer_empty_obj.gif
.. |fai_image23| image:: /images/fonts_and_imagery/mapfolder_obj.gif
.. |fai_image24| image:: /images/fonts_and_imagery/mapfolder_nonexist_obj.gif
.. |fai_image25| image:: /images/fonts_and_imagery/page_obj.gif
.. |fai_image26| image:: /images/fonts_and_imagery/page_template_obj.gif

This is designed to intergrate well with the 
`common Eclipse representations <http://wiki.eclipse.org/User_Interface_Guidelines#Consistency_.26_Reuse>`_, and
play with with the range of overlays etc.

Overlays
^^^^^^^^

These elements are used as overlays (or in the construction of new icons ) and usually represent an
ver (ie action) or agective (ie additional description).

-  /../dev/net.refractions.udig.dev/\_add\_co.gif
-  /../dev/net.refractions.udig.dev/\_edit.gif
-  /../dev/net.refractions.udig.dev/\_error.gif
-  /../dev/net.refractions.udig.dev/\_filter.gif
-  /../dev/net.refractions.udig.dev/\_goto.gif
-  /../dev/net.refractions.udig.dev/\_info.gif
-  /../dev/net.refractions.udig.dev/\_refresh.gif
-  /../dev/net.refractions.udig.dev/\_rem\_co.gif
-  /../dev/net.refractions.udig.dev/\_select\_co.gif
-  /../dev/net.refractions.udig.dev/\_sort\_co.gif
-  /../dev/net.refractions.udig.dev/\_wiz.gif
-  /../dev/net.refractions.udig.dev/\_zoom.gif

Where possible you should "harvest" imagery from the latest eclipse release, these just represent
imagery we were forced to harvest several times.

Wizard Banners
^^^^^^^^^^^^^^

Wizard banners are the largest representation of our imagery in uDig.

-  /../plugins/net.refractions.udig.catalog.ui/icons/wizban/repository\_wiz.gif Repository
-  /../plugins/net.refractions.udig.catalog.ui/icons/wizban/catalog\_wiz.gif Catalog
-  /../plugins/net.refractions.udig.catalog.ui/icons/wizban/datastore\_wiz.gif Feature Generic
-  /../plugins/net.refractions.udig.catalog.ui/icons/wizban/gce\_wiz.gif Raster Generic
-  /../plugins/net.refractions.udig.catalog.ui/icons/wizban/file\_wiz.gif! File Generic
-  /../plugins/net.refractions.udig.catalog.ui/icons/wizban/feature\_file\_wiz.gif File Features
-  /../plugins/net.refractions.udig.catalog.ui/icons/wizban/grid\_file\_wiz.gif File Raster
-  /../plugins/net.refractions.udig.catalog.ui/icons/wizban/server\_wiz.gif! Server
-  /../plugins/net.refractions.udig.catalog.ui/icons/wizban/wrs\_wiz.gif Server Catalog (ie WRS)
-  /../plugins/net.refractions.udig.catalog.ui/icons/wizban/wfs\_wiz.gif Feature Server (ie WFS)
-  /../plugins/net.refractions.udig.catalog.ui/icons/wizban/wms\_wiz.gif Server Map (ie WMS)
-  /../plugins/net.refractions.udig.catalog.ui/icons/wizban/export\_wiz.gif Export
-  /../plugins/net.refractions.udig.catalog.ui/icons/wizban/import\_wiz.gif Import
-  /../plugins/net.refractions.udig.ui/icons/wizban/log\_wiz.png Log Wizard
-  /../plugins/net.refractions.udig.catalog.ui/icons/wizban/add\_wiz.gif Add

When creating a wizard you will need the following background:

-  /../dev/net.refractions.udig.dev/wizbanner.gif

Standard Eclipse Metaphors
^^^^^^^^^^^^^^^^^^^^^^^^^^

As usual please see the user interface guidelines, although the document is from Eclipse2.1:

.. figure:: /images/fonts_and_imagery/metaphor_concepts.gif
   :align: center
   :alt: 

Product Branding
^^^^^^^^^^^^^^^^

Here is some of the source material used for "branding" the uDig application itself; for details on
how branding works please review the :doc:`Custom Application Tutorial <custom_application_tutorial>`.

Application Logo
^^^^^^^^^^^^^^^^

icon128.gif
 icon64.gif
 icon48.gif
 icon32.gif
 icon16.gif
 udig.ico

Historical: |image0| (Based on Eclipse Imagry )

I do not have samples of the current logo uses at the OSG'05 conference as they are not used in the
application.

Splash Screen
^^^^^^^^^^^^^

In addition to the graphics below, room should be allocated for a porgress bar.

Splash English

-  /../plugins/net.refractions.udig/nl/en/splash.bmp

Splash German

-  /../plugins/!net.refractions.udig/nl/de/splash.bmp

Splash French

-  /../plugins/net.refractions.udig/nl/fr/splash.bmp

About
^^^^^

The about image is under strict restrictions of no more the 250x300 pixels (or the text gets
squished).

-  /../plugins/net.refractions.udig/icons/about.gif

Welcome
^^^^^^^

The welcome screen is displayed when the user first opens up uDig.

Welcome Large:

-  /../plugins/net.refractions.udig/intro/css/graphics/UDIG\_WelcomeScreen.gif
-  /../plugins/net.refractions.udig/intro/css/graphics/built%20on%20eclipse.gif

Welcome Small:

-  /../plugins/net.refractions.udig/intro/css/graphics/UDIG\_WelcomeScreen\_small.gif
-  /../plugins/net.refractions.udig/intro/css/graphics/bui\_eclipse\_pos\_logo\_fc\_sm.gif

Welcome Topics:

-  /../plugins/net.refractions.udig/intro/css/graphics/icons/etool/overview72.gif
-  /../plugins/net.refractions.udig/intro/css/graphics/icons/etool/overview48.gif
-  /../plugins/net.refractions.udig/intro/css/graphics/icons/etool/overview48sel.gif
-  /../plugins/net.refractions.udig/intro/css/graphics/icons/etool/samples72.gif
-  /../plugins/net.refractions.udig/intro/css/graphics/icons/etool/samples48.gif
-  /../plugins/net.refractions.udig/intro/css/graphics/icons/etool/samples48sel.gif
-  /../plugins/net.refractions.udig/intro/css/graphics/icons/etool/tutorials72.gif
-  /../plugins/net.refractions.udig/intro/css/graphics/icons/etool/tutorials48.gif
-  /../plugins/net.refractions.udig/intro/css/graphics/icons/etool/tutorials48sel.gif
-  /../plugins/net.refractions.udig/intro/css/graphics/icons/etool/whatsnew72.gif
-  /../plugins/net.refractions.udig/intro/css/graphics/icons/etool/whatsnew48.gif
-  /../plugins/net.refractions.udig/intro/css/graphics/icons/etool/whatsnew48sel.gif!
-  /../plugins/net.refractions.udig/intro/css/graphics/icons/etool/wb48.gif

.. |image0| image:: /images/fonts_and_imagery/udig_logo32.gif
