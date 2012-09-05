Quickstart
----------

This is a quick introduction to the uDig application included as part of online help.

Natural Earth Sample Data
~~~~~~~~~~~~~~~~~~~~~~~~~

This quickstart makes use of sample data kindly made available by the `Natural
Earth <http://www.naturalearthdata.com>`_ project:

#. Right click on the following links and choose **Save Link As...**:

   `data_1_3.zip <http://udig.refractions.net/files/data/data_1_3.zip>`_ 

   .. tip::
      If you are using this in a classroom setting or *OSGeo Live* the files should be on your DVD

#. If you have your own GIS data please feel free to experiment. Making use of the above sample data
   will enable you to follow along with this tutorial step by step.

The *data_1_3.zip* download is the full dataset used by :doc:`Walkthrough 1`.

.. note:: Natural Earth Data

  We would like to thank `www.naturalearthdata.com <http://www.naturalearthdata.com>`_ for this sample data. Please
  visit their web site to download individual files (and at different scales for more detailed work):
  
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

#. Choose :menuselection:`uDig 1.3 --> uDig` from the start menu
#. The application will take a few moments to start up

   |image0|

The uDig application keeps a configuration folder in your home directory, the folder will be created
the first time the application is launched. If you have any difficulties, or are running uDig on
Linux or Mac please review the :doc:`Running uDig` reference page.

Welcome View
~~~~~~~~~~~~

#. When you start up uDig for the first time the **Welcome** view is displayed on the right hand side
   of the screen. This view has links to tutorials, documentation and the project web site.
   
   .. figure:: /getting_started/walkthrough1/images/welcome.png
      :width: 80%
      :alt: welcome screen

#. To close the :guilabel:`Welcome` view click the :guilabel:`x` next to the word welcome.
   
   You can return to the :guilabel:`Welcome` view at any time using the menu bar :menuselection:`Help --> Welcome`.

Workbench
~~~~~~~~~

The Workbench window offers multiple Editors (each showing a Map) and supporting Views (offering
information about the current Map).

.. figure:: /getting_started/walkthrough1/images/workbench.jpg
   :width: 80%
   :alt: workbench tour

The above screen shows the uDig application in action with an open :doc:`Map editor` surrounded by the
:doc:`Projects view`, :doc:`Layers view` and :doc:`Catalog view`. These views will be described further
as we demonstrate their use.

The application provides:

* :guilabel:`menubar` is located along the top of the screen
* :guilabel:`toolbar` for quick access to common actions
* :guilabel:`context menu` accessed by right click on the current selection 
* Editors can provide :guilabel:`Palette` of tools
* Views provide both a :guilabel:`View toolbar` for common actions ending with a downward arrow to access a more extensive :guilabel:`View menu`
* When selecting content in an :guilabel:`Editor` or :guilabelL`View` 

Files
~~~~~

To start out with we are going to load some of the sample data you downloaded earlier.

#. Choose :menuselection:`Layer --> Add` from the menu bar to open up the :doc:`Add Data wizard`
#. Select :doc:`Files` from the list of data sources
#. Press **Next** to open up a file dialog
#. Select the following file from your data folder: **10m_admin_0_countries.shp**
#. Press **Open**

   -  A new :doc:`Map editor` will be opened based on the contents of your
      shapefile. The default name and projection of the Map has been taken from your shapefile.
   -  You can see the **Catalog view** has been updated with an entry for **10m_admin_0_countries.shp**. This
      view is used to track the use of resources by the uDig application.
   -  The **Layers view** shows a single layer is displayed on this map. This view is used to change
      the order and appearance of information in your Map.
   -  The **Projects** view has been updated to show that your map is stored in a
      **project**. You can have multiple projects open at a time, each project can have
      several maps.

#. Open up your **data** folder in the file system
#. Drag the file **NE1_50M_SR_W.tif** onto the Map Editor, a new layer is added to to the map.
#. You can see the order the layers are drawn in the layer view. Right now the **NE1_50M_SR_W** layer
   is drawn on top of the **10m_admin_0_countries** layer.
#. Select the **NE1_50M_SR_W.tif** layer in the catalog view and drag it to the bottom of the list

   .. figure:: /images/quickstart/QuickstartCountriesMap.jpg
      :width: 80%
      :alt: Countries Map

#. You can also use the up and down buttons in the :guilabel:`Layers` view to reorder.

.. tip::
   You can also drag and drop shapefiles directly into the uDig application!

.. note::
   A common questions asked when using uDig for the first time is how big a
   shapefile can uDig load?
   
   The application uses a fixed amount of memory for each layer, and does not load
   shapefiles into memory. We have a policy of keeping data on disk and drawing
   information, such as this shapefile, onto the screen as needed.

Map
~~~

The :guilabel:`Map Editor` includes :guilabel:`Palette` of tools for map interaction. Use the navigation
tools along the top of the :guilabel:`Palette` to control where the :guilabel:`Map Editor` is looking.

#. The |image3| :guilabel:`Zoom` tool is available by default

   -  Use the zoom tool by drawing a box using the left mouse button around the area of the world you
      wish to see.
   -  To zoom out draw a box with the right mouse button. The current map extents will be located
      within the box you draw.

   .. tip:: 
      Most tools allow you to Pan by holding the center button and control the scale using the scroll wheel.

#. The |image4| :guilabel:`Pan` tool can be used to scroll around your map with out changing scale.
   
   - You can change the behavior of the :guilabel:`Pan` tool using the tool option
     area located along the bottom edge of the map.

#. The :guilabel:`toolbar` is updated to reflect the current map and contains several
   actions to control rendering:

   - |image5| Show All, can be used to return to the full map area at any time
   - |image6| Zoom In and |image7| Zoom Out can be used to change the scale by a fixed amount
   - Actions to :guilabel:`Redraw Map` and :guilabel:`Stop Drawing` are available

#. The application :guilabel:`menubar` is also updated to work with the current map.

   -  You can use :menuselection:`Navigation --> Back` and :menuselection:`Navigation --> Forward` in the menu bar to cycle though
      previously visited locations.

Web Map Server
~~~~~~~~~~~~~~

One of the reasons to use an application like uDig is to access all the great free geospatial
information available on the web. This section covers the use of **Web Map Servers** which make
available layers of information that you can mix into your own maps.

#. Select :menuselection:`File --> New --> New Map` from the menu bar
#. Change to the :guilabel:`Web` view, click on the tab next to the :guilabel:`Catalog` view 
   to reveal the :guilabel:`Web` view.
#. Click on the link :guilabel:`WMS:dm solutions` link

   .. figure:: /images/quickstart/WebViewClick.png
      :alt: Copy WMS URL

#. From the :doc:`Resource Selection page` we are going to choose the
   following layers:

   -  Elevation/Bathymetry
   -  Parks
   -  Cities

   .. figure:: /images/quickstart/AddWMSLayers.png
      :alt: Add WMS Map

#. Press **Finish** to add these layers to your map

   .. figure:: /images/quickstart/WMSMap.png
      :width: 80%
      :alt: WMS Map

#. Use the |image11| :guilabel:`Zoom` tool to move closer to one of the Parks
#. Switch to the |image12| :guilabel:`Info` tool and click on one the parks.

   The :doc:`Information view` is opened for more details on the area clicked.

.. tip:: 
    You can switch between the zoom and info tools by pressing :kbd:`Z` and :kbd:`I` on the keyboard.

Style
~~~~~

#. From the :guilabel:`Projects` view select :guilabel:`project --> 10m admin 0 countries`.
   You can double click to open this Map, or right click for :menuselection:`Context menu --> Open Map`.
#. With the map open use the :guilabel:`Layers` view to select the :guilabel:`10m admin 0 countries` layer.
#. Open up the :doc:`Style Editor dialog` by right clicking on :guilabel:`10m admin 0 countries`
   layer and using :menuselection:`Context menu --> Change Style`.
#. We are going to change a few things about how countries are displayed.
   
   The :guilabel:`Polygons` page provides a series of tabs for us to fill in:
   
   - :guilabel:`Border` Click on the :guilabel:`Color` and change the color to **BLACK**
   - :guilabel:`Fill` uncheck the box disable fill
   - :guilabel:`Label` check the box to enable labels. For the :guilabel:`label` value select
     :guilabel:`NAME` from this list of attributes.

   .. figure:: /images/quickstart/StyleEditor.png
      :width: 80%
      :alt: Style Editor

#. Press :guilabel:`Apply` to see what this looks like on your Map, the Layer view will also be updated to
   reflect the current style
#. When you are happy with the result you can press :guilabel:`Close` to dismiss the dialog
#. It is a bit hard to see what is going with the raster layer providing so much details.
   Select :menuselection:`Map --> Mylar` from the menu bar to focus on the selected layer
#. Using the :guilabel:`Layer` view select the different layers in turn to see the effect.

   .. figure:: /images/quickstart/MapMylar.jpg
      :width: 80%
      :alt: Mylar Map

#. You can turn off this effect at any time using :menuselection:`Map --> Mylar` from the menu bar

What is Next
~~~~~~~~~~~~

This is only the first step on the road to using uDig. There is a lot more great material (and
ability) left for your to discover in our walkthroughs.

-  :doc:`Walkthrough 1` - Try out the use of PostGIS, extract data from a Web
   Feature Server and explore the use of Themes with our powerful Color Brewer technology.
-  :doc:`Walkthrough 2` - Learn how to create shapefiles and use the Edit tools
   to manipulate feature data, covers the installation of GeoServer and editing with a Web Feature
   Server.

.. |image0| image:: /getting_started/walkthrough1/images/splash.png
.. |image3| image:: /images/quickstart/zoom_mode.gif
.. |image4| image:: /images/quickstart/pan_mode.gif
.. |image5| image:: /images/quickstart/zoom_extent_co.gif
.. |image6| image:: /images/quickstart/zoom_in_co.gif
.. |image7| image:: /images/quickstart/zoom_out_co.gif
.. |image11| image:: /images/quickstart/zoom_mode.gif
.. |image12| image:: /images/quickstart/info_mode.gif

