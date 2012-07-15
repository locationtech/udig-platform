Training Materials
~~~~~~~~~~~~~~~~~~

These examples are from the Refractions commercial `training
course <http://www.refractions.net/services/training_course.php>`_

**Example Code**

The uDig SDK can also be found live on our uDig subversion repository:

* `http://svn.refractions.net/udig/udig/trunk/tutorials/ <http://svn.refractions.net/udig/udig/trunk/tutorials/>`_
* `http://svn.refractions.net/udig/udig/branches/1.1.x/udig/tutorials/ <http://svn.refractions.net/udig/udig/branches/1.1.x/udig/tutorials/>`_

This example code is included in your SDK, please visit the `Code Examples <Code%20Examples.html>`_
page for instructions on importing these examples into your workspace for review.

**Training Materials**

The workbooks and slides for the training course are available here:

* `http://svn.refractions.net/udig\_training/trunk <http://svn.refractions.net/udig_training/trunk>`_

This is a private svn repository that is open to those who have taken the training course. The
course materials can be **made available for free** to those working at academic institutions - we
ask for an email from your Professor.

Of course we would much rather come and visit you - see the `support
page <http://udig.refractions.net/confluence//display/UDIG/Links>`_ for more details.

Public Tutorials
^^^^^^^^^^^^^^^^

The following examples are covered as part of the public training materials we have made available
in this developers guide.

 

Technology

Concept

.. figure:: images/icons/emoticons/check.gif
   :align: center
   :alt: 

RCP

Platform class uses the plugin.xml files to wire up your appliation

.. figure:: images/icons/emoticons/check.gif
   :align: center
   :alt: 

RCP

Workbench selection is used to communicate between plugins and a single selection can "Adapt to"
multiple Java Interfaces as needed

Distance Tool Tutorial
----------------------

-  net.refractions.udig.tutorials.distancetool

Example used as part of `Tool Plugin Tutorial <Tool%20Plugin%20Tutorial.html>`_ tutorial.

Custom Application Tutorial
---------------------------

-  net.refractions.udig.tutorials.distancetool

Example used as part of `Custom Application Tutorial <Custom%20Application%20Tutorial.html>`_.

Workbench Selection
-------------------

Example used as part of `Workbench Selection Tutorial <Workbench%20Selection%20Tutorial.html>`_.

UDIG Workshop Introduction
==========================

 

Technology

Concept

.. figure:: images/icons/emoticons/check.gif
   :align: center
   :alt: 

GIS Platform

How catalog IGeoResources "Resolve To" a data access Interface as needed

.. figure:: images/icons/emoticons/check.gif
   :align: center
   :alt: 

GIS Application

How blackboards are used to communicate between plug-ins

IAdaptable
----------

-  net.refractions.udig.tutorials.urladapter

An example of what makes Eclipse such a powerful framework to develop against; example shows how to
add an Adapter to an existing uDig class. This is the technique you will often use to provide your
own API against existing entries in the Catalog.

Export Shapefile
----------------

-  net.refractions.udig.tutorials.shpexport

Make use of an IGeoResource; by requesting a FeatureSource API for data access. This example shows
how to create a new Shapfile using the GeoTools library.

Map Graphic
-----------

-  net.refractions.udig.tutorials.mapgraphic.coordinate
-  net.refractions.udig.tutorials.tool.coordinate

Make use of a Map Graphic for quickly drawing your own thing on the screen. The example uses a Tool
to place points on the Map blackboard, the map graphic takes any points on the Map blackboard and
draws them on screen.

Blackboards are where you store your own data structures in the live uDig application. Each Layer
actually has two blackboards; one called the **style blackboard** that is used to save settings
controlling the appearance; and a second **blackboard** that is yours to play with at runtime.

Often custom applications will place their own domain objects (complete with their own listeners and
user interface views) on a blackboard in order to host their functionality within the uDig
framework.

Feature Editor
--------------

This example shows how to make a FeatureEditor that is used to edit features from **countries.shp**
(included in the Walkthrough 1 sample data).

This tutorials shows how to:

-  make a view
-  list our view when a user right clicks on a selected Feature
-  issue an edit command (asking the EditManager to modify the selected feature)

UDIG Workshop Advanced
======================

These tutorials cover "from disk to screen".

CSV Service
-----------

-  com.csvreader
-  net.refractions.udig.tutorials.catalog.csv

This covers how to package a third-party jar as an eclipse plug-in, and how to make a catalog
service representing a new file format.

Testing
-------

-  net.refractions.udig.tests.catalog.csv

A JUnit plug-in test that tests the above CSV Service. When you run a JUnit Plug-in test a slaved
copy of uDig is started up; the test is run and then the slaved copy of UDig is exited.

CSV Renderer
------------

-net.refractions.udig.tutorials.render.csv

Defines a renderer capable of drawing a "CSVGeoResource" onto the screen. Renderers can be defined
for each kind of data access API (ie a Java Interface). In this case we are using the CSVReader API
packaged up into com.csvreader plug-in above.

Out of the box uDig ships with several renderers based on the GeoTools library. You can find
additional renderers available as community plugins (making use of Nasa World Wind, or C++ APIs like
OSSIM).

Style
-----

-  net.refractions.udig.tutorials.style.color

Contains an alternative CSVRenderer that is used when a color is added to the blackboard.

This tutorial covers:

-  Defining a new StyleContent to store a color on the style blackboard
-  Contributing new functionality to an existing GeoResource - a default Color is added
   CSVGeoResource using an IResolveAdapterFactory
-  Using RenderingMetrics to choose the correct renderer implementation
-  The create of a StyleConfigurator allowing the user to define a Color; this is used in the Style
   Editor dialog; or Style view as required.

Tracking
--------

-  net.refractions.udig.tutorials.rcp
-  net.refractions.udig.tutorials.rcp-feature

This tutorial covers:

-  Gathering just the udig plugins you need into your own feature
-  Adding a MapView to your application
-  Placing a Domain Object on the Map Blackboard (simulating the tracking of seagulls)
-  Creating of a GlassPane to quickly draw updates using Draw2D (to draw the current position of
   seagulls)

