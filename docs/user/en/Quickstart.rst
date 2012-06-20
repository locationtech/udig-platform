Quickstart
----------

This is a quick introduction to the uDig application included as part of online help.

Natural Earth Sample Data
~~~~~~~~~~~~~~~~~~~~~~~~~

This quickstart makes use of sample data kindly made available by the `Natural
Earth <http://www.naturalearthdata.com>`_ project:

#. Right click on the following links and choose **Save Link As...**:
   :doc:`data\_1\_3.zip`


   -  If you are using this in a classroom setting or OSGeo live dvd the files should be on your DVD

#. If you have your own GIS data please feel free to experiment. Making use of the above sample data
   will enable you to follow along with this tutorial step by step.

This is actually the full dataset used by :doc:`Walkthrough 1`. The contents of
this dataset are as follows (in case you want to download higher resolution data):

* `HYP\_50M\_SR\_W.zip <http://www.naturalearthdata.com/http//www.naturalearthdata.com/download/50m/raster/HYP_50M_SR_W.zip>`_,
* `10m-populated-places-simple.zip <http://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/cultural/10m-populated-places-simple.zip>`_,
* `10m-admin-0-countries.zip <http://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/cultural/10m-admin-0-countries.zip>`_,
* `10m-admin-1-states-provinces-shp.zip <http://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/cultural/10m-admin-1-states-provinces-shp.zip>`_,
* `10m-urban-area.zip <http://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/cultural/10m-urban-area.zip>`_,
* `10m-geography-regions-polys.zip <http://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/physical/10m-geography-regions-polys.zip>`_,
* `10m-geography-regions-points.zip <http://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/physical/10m-geography-regions-points.zip>`_,
* `10m-geography-regions-elevation-points.zip <http://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/physical/10m-geography-regions-elevation-points.zip>`_,
* `10m-geography-marine-polys.zip <http://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/physical/10m-geography-marine-polys.zip>`_,
* `10m-land.zip <http://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/physical/10m-land.zip>`_,
* `10m-ocean.zip <http://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/physical/10m-ocean.zip>`_,
* `physical/10m-lakes.zip <http://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/physical/10m-lakes.zip>`_,
* `physical/10m-rivers-lake-centerlines.zip <http://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/physical/10m-rivers-lake-centerlines.zip>`_

uDig Application
~~~~~~~~~~~~~~~~

#. Choose **uDig 1.1 > uDig** from the start menu
#. The application will take a few moments to start up
    |image0|

The uDig application keeps a configuration folder in your home directory, the folder will be created
the first time the application is launched. If you have any difficulties, or are running uDig on
Linux or Mac please review the `Running uDig <Running%20uDig.html>`_ reference page.

Welcome View
~~~~~~~~~~~~

#. When you start up uDig for the first time the **Welcome** view takes up the entire display. This
   screen has links to tutorials, documentation and the project website.
#. Click the arrow labelled **Workbench** in the upper right corner, to reveal the contents of the uDig workbench.

    |image1|

You can return to the Welcome view at any time by selecting the **Help > Welcome** from the menu
bar.

Workbench
~~~~~~~~~

The Workbench window offers multiple Editors (each showing a Map) and supporting Views (offering
information about the current Map).

.. figure:: /images/quickstart/workbench.png
   :align: center
   :alt: 

Shown above is a typical uDig session with the `Map editor <Map%20editor.html>`_, `Projects
view <Projects%20view.html>`_, `Layers view <Layers%20view.html>`_ and `Catalog
view <Catalog%20view.html>`_ labelled . These views will be described further as we demonstrate
their use.

Files
~~~~~

To start out with we are going to load some of the sample data you downloaded earlier.

#. Choose **Layer > Add** from the menu bar to open up the `Add Data
   wizard <Add%20Data%20wizard.html>`_
#. Select `Files <Files%20page.html>`_ from the list of data sources
#. Press **Next** to open up a file dialog
#. Select the following files from your data folder: **countries.shp**
#. Press **Open**

   -  A new `Map editor <Map%20editor.html>`_ will be opened based on the contents of your
      shapefile. The default name and projection of the Map has been taken from your shapefile.
   -  You can see the **Catalog view** has been updated with an entry for **countries.shp**. This
      view is used to track the use of resources by the uDig application.
   -  The **Layers view** shows a single layer is displayed on this map. This view is used to change
      the order and appearance of information in your Map.
   -  The **Projects** view has been updated to show that your map is stored in
      **projects>countries**. You can have multiple projects open at a time, each project can have
      several maps.

#. Open up your data folder in windows
#. Drag the file **clouds.jpg** onto the Map Editor, a new layer is added to to the map.
#. You can see the order the layers are drawn in the layer view. Right now the **clouds.jpg** layer
   is drawn ontop of the countries layer.
#. Select the **clouds.jpg** layer in the catalog view and drag it to the bottom of the list
    |image2|

**Info:** One of the most common questions asked when uDig is considered for an organization is how
much memory the application uses. Unlike most GIS applications uDig can get by with a fixed amount
of memory. The above shapefile is not loaded into memory, we have a policy of keeping data on disk
and drawing data like this shapefile onto the screen as needed.

.. tip::
    You can also drag and drop shapefiles directly into the uDig application!

Map
~~~

You can control where in the world the Map Editor is looking by using the navigation tools in the
tool bar along the top of the screen.

#. The |image3| **Zoom** tool is available by default

   -  Use the zoom tool by drawing a box using the left mouse button around the area of the wold you
      wish to see.
   -  To zoom out draw a box with the right mouse button. The current map extents will be located
      within the box you draw.

#. The |image4| **Pan** tool can be used to scroll around your map with out changing scale.
#. There are also several navigation buttons that can be used at any time:

   -  |image5| Show All, can be used to return to the full extents at any time
   -  |image6| Zoom In and |image7| Zoom Out can be used to change the scale by a fixed amount.
   -  You can use **Navigation > Back** and **Navigation > Forward** in the menu bar to cycle though
      previously visited locations.


.. tip:: 
    Most tools allow you to Pan by holding the center button and control the scale using the scroll wheel.

Web Map Server
~~~~~~~~~~~~~~

One of the reasons to use an application like uDig is to access all the great free geospatial
information available on the web. This section covers the use of **Web Map Servers** which make
available layers of information that you can mix into your own maps.

#. Select **File > New > New Map** from the menu bar
#. Change to the **Web view**, click on the tab next to the **Catalog view** to reveal the Web view.
#. Click on the link **WMS:dm solutions** link
    |image8|
#. From the `Resource Selection page <Resource%20Selection%20page.html>`_ we are going to choose the
   following layers:

   -  Elevation/Bathymetry\*
   -  Parks
   -  Cities
       |image9|

#. Press **Finish** to add these layers to your map
    |image10|
#. Use the |image11| Zoom Tool to move closer to one of the Parks
#. Switch to the |image12| and click on one the parks to learn more about it

.. tip:: 
    You can switch between the zoom and info tools by pressing **Z** and **I** on the keyboard.

Style
~~~~~

#. Select the **project > countries**, you can double click to open this Map, or Right Click and
   choose **Open Map**
#. Select the **countries** layer in the Layer view
#. Open up the Style Editor by right clicking on **countries** layer and choosing **Change Style**
#. We are going to change a few things about how countries are displayed

   -  Line: Click on the **Color** and change the color to **BLACK**
   -  Fill: uncheck the box to turn off fill
   -  Label: check the box, and choose **CNTRY\_NAME** from the list of attributes
       |image13|

#. Press **Apply** to see what this looks like on your Map, the Layer view will also be updated to
   reflect the current style
#. When you are happy with the result you can press **Close** to dismiss the dialog
#. Some files include style settings, Select **Layer > Add ...** from the menu bar
#. Select **Files** from the list of data sources and press **Next**
#. Using the From the file chooser open up **timezone.shp** and press **Open**
#. It is a bit hard to see what is going on with the **clouds.jpg** layer providing so much details.
   Select **Map > Mylar** from the menu bar to focus on the selected layer
#. Using the Layer view select **timezone**, **countries** and **clouds.jpg** in turn to see the
   effect
    |image14|
#. You can turn off this effect at any time using **Map > Mylar** from the menu bar

What is Next
~~~~~~~~~~~~

This is only the first step on the road to using uDig. There is a lot more great material (and
ability) left for your to discover in our walkthroughs.

-  `Walkthrough 1 <Walkthrough%201.html>`_ - Try out the use of PostGIS, extract data from a Web
   Feature Server and explore the use of Themes with our powerful Color Brewer technology.
-  `Walkthrough 2 <Walkthrough%202.html>`_ - Learn how to create shapefiles and use the Edit tools
   to manipulate feature data, covers the installation of GeoServer and editing with a Web Feature
   Server.

.. |image0| image:: /images/quickstart/Quickstart1Splash.png
.. |image1| image:: /images/quickstart/welcome.png
.. |image2| image:: /images/quickstart/QuickstartCountriesMap.jpg
.. |image3| image:: /images/quickstart/zoom_mode.gif
.. |image4| image:: /images/quickstart/pan_mode.gif
.. |image5| image:: /images/quickstart/zoom_extent_co.gif
.. |image6| image:: /images/quickstart/zoom_in_co.gif
.. |image7| image:: /images/quickstart/zoom_out_co.gif
.. |image8| image:: /images/quickstart/WebViewClick.png
.. |image9| image:: /images/quickstart/AddWMSLayers.png
.. |image10| image:: /images/quickstart/WMSMap.png
.. |image11| image:: /images/quickstart/zoom_mode.gif
.. |image12| image:: /images/quickstart/info_mode.gif
.. |image13| image:: /images/quickstart/StyleEditor.png
.. |image14| image:: /images/quickstart/MapMylar.jpg
