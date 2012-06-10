Introduction to the Spatial Toolbox
-----------------------------------

The Spatial Toolbox View gives the possibility to execute tools from loaded libraries on resources
visualized in uDig.

`Introduction to the Spatial
Toolbox <#IntroductiontotheSpatialToolbox-IntroductiontotheSpatialToolbox>`_

:doc:`Before you Start`


-  `Download the Spearfish example
   data <#IntroductiontotheSpatialToolbox-DownloadtheSpearfishexampledata>`_

:doc:`The Spatial toolbox View`


:doc:`Installing JGrasstools`


:doc:`Processing`


-  `Generating Aspect of an Elevation
   Model <#IntroductiontotheSpatialToolbox-GeneratingAspectofanElevationModel>`_

:doc:`Things to Try`


**Related reference**


:doc:`Spatial Toolbox View`


This is how it looks like when it is enabled:

.. figure:: /images/introduction_to_the_spatial_toolbox/omsbox.png
   :align: center
   :alt: 

Before you Start
~~~~~~~~~~~~~~~~

Download the Spearfish example data
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

In the example we will use a raster elevation model of the Spearfish region. You can download it
:doc:`here`


#. Download the following file (you can right click and choose **Save as** in most browsers)

   * :doc:`spearfish\_elevation.zip`


#. Unzip it to your data directory or desktop
#. This download contains:

   -  elevation.asc
   -  elevation.prj

#. Try to drag it into uDig and see if it look like the following:

.. figure:: /images/introduction_to_the_spatial_toolbox/elevation.png
   :align: center
   :alt: 

The Spatial toolbox View
~~~~~~~~~~~~~~~~~~~~~~~~

Once your raster is loaded you are ready to start to configure the spatial toolbox. First let's open
it up:

1 Find it under Window->Show View->Other
 |image0|
 2 And then choose the Spatial toolbox:
 |image1|
 3 It will probably open up a bit too small, so best thing is to detach it and use it in a separate
window anyways.
 4 To do so right click on the view's bar:
 |image2|
 5 And voila'!
 |image3|

Installing JGrasstools
~~~~~~~~~~~~~~~~~~~~~~

The spatial toolbox is able to load and generate graphical user interfaces and link together models
that are developed as OMS3 services.

.. figure:: images/icons/emoticons/warning.gif
   :align: center
   :alt: 

**OMS3**

Object Modeling Services 3 is a standard used for component development employed by the jgrass
project. If you are interested in the details please have a look here:

* :doc:`OMS3 jgrass page`

-  `oms javaforge page <http://www.javaforge.com/project/oms>`_.

To load the modules:

#. Currently one library of spatial tools implemented using OMS3 is the
   `jgrasstools <http://www.jgrasstools.org/>`_ library.
#. Therefore it is possible to jump on the jgrass website to get a processing library to use it in
   uDig.
#. At the time of writing the jgrass 0.7.1 library is packaged and available for use
#. To start to have some fun we need to download two jar files:

   -  the `generic GIS modules
      library <http://jgrasstools.googlecode.com/files/jgt-jgrassgears-0.7.2.jar>`_
   -  the `horton machine <http://jgrasstools.googlecode.com/files/jgt-hortonmachine-0.7.2.jar>`_
      for hydro-geomophologic analyses

#. You can download these two files anywhere; for today we will be placing them in a "jgrass" folder
   in your home directory.
#. Create a "jgrass" folder in your home directory
#. Download the above two files and place them in the jgrass folder
#. Then push the last icon of the toolbar:
    |image4|
#. Which will open the settings dialog:
    |image5|
#. The settings dialog allows you to load the libraries by using the + button.
#. Load the two libraries that you downloaded.
#. It should, apart of the different paths of the files, like like the following:
    |image6|
    #Once you push the ok button, uDig will search for modules inside the library
    |image7|
#. Congratulations; the toolbox should now display the modules that were found
    |image8|

Processing
~~~~~~~~~~

The jgrass project provides a host of valuable processes you can try out; for now we will start with
a simple example.

Generating Aspect of an Elevation Model
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

To try out spatial processing we will simply create a map of aspect using the elevation model you
already loaded from the sample dataset.

#. Type in the search box the letters: **asp**
#. The "Aspect" module should appear in the list:
    |image9|
#. Please select the Aspect module so we can describe the tabs used to control a service.
#. inputs tab:
    |image10|

   -  outputs tab:
       |image11|
   -  documentation tab:
       |image12|

#. We will now set up the Aspect service to work with our sample digital elevation model.
#. To define the input raster to process, simply push the input data button.

   -  A raster reader dialog will open.
   -  You could browse for the raster, but the simplest thing is to drag the raster map from the
      udig layer view into the textfield and let udig do the rest.
       |image13|

#. Output data

   -  In the case of the output data, we can't drag an existing map, so we will have to use the
      browse button, which will open in the same folder of the last inserted map.
   -  There we simply write **aspect.asc** to create an esri ascii also as output \\ (other formats
      supported are tiffs and grass binary rasters)
       |image14|

#. Configure

   -  Before we run the module there are a couple of configuration options that are useful to
      control.
   -  The most important is the amount of memory that we permit the module to use:
       |image15|

#. In the above example the local machine has 8 gigabytes of RAM, allowing us to easily allocate
   2000 megabytes for for the work
#. Execute the module

   -  To execute the module please click the **run** button in the view toolbar
       |image16|
   -  The module will execute opening a console window that gives feedback on what is going on.

#. Once the process is done, the resulting map is loaded in the map window:
    |image17|

Things to Try
~~~~~~~~~~~~~

Congratulations on finishing this tutorials; here are a couple of things to try:

-  Review the documentation of some of the other services made available
-  Check out the jgrass website for additional information about the project

.. |image0| image:: /images/introduction_to_the_spatial_toolbox/spatial_toolbox_01.png
.. |image1| image:: /images/introduction_to_the_spatial_toolbox/spatial_toolbox_02.png
.. |image2| image:: /images/introduction_to_the_spatial_toolbox/spatial_toolbox_03.png
.. |image3| image:: /images/introduction_to_the_spatial_toolbox/spatial_toolbox_04.png
.. |image4| image:: /images/introduction_to_the_spatial_toolbox/spatial_toolbox_05.png
.. |image5| image:: /images/introduction_to_the_spatial_toolbox/spatial_toolbox_06.png
.. |image6| image:: /images/introduction_to_the_spatial_toolbox/spatial_toolbox_07.png
.. |image7| image:: /images/introduction_to_the_spatial_toolbox/spatial_toolbox_08.png
.. |image8| image:: /images/introduction_to_the_spatial_toolbox/spatial_toolbox_09.png
.. |image9| image:: /images/introduction_to_the_spatial_toolbox/spatial_toolbox_10.png
.. |image10| image:: /images/introduction_to_the_spatial_toolbox/spatial_toolbox_11.png
.. |image11| image:: /images/introduction_to_the_spatial_toolbox/spatial_toolbox_12.png
.. |image12| image:: /images/introduction_to_the_spatial_toolbox/spatial_toolbox_13.png
.. |image13| image:: /images/introduction_to_the_spatial_toolbox/spatial_toolbox_14.png
.. |image14| image:: /images/introduction_to_the_spatial_toolbox/spatial_toolbox_15.png
.. |image15| image:: /images/introduction_to_the_spatial_toolbox/spatial_toolbox_16.png
.. |image16| image:: /images/introduction_to_the_spatial_toolbox/spatial_toolbox_17.png
.. |image17| image:: /images/introduction_to_the_spatial_toolbox/spatial_toolbox_18.png
