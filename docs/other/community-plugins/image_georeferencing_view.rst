Image Georeferencing View
#########################

Community Plugins : Image Georeferencing View

This page last changed on Jun 22, 2011 by mauricio.pazos.

`Motivation <#ImageGeoreferencingView-Motivation>`__

`Features <#ImageGeoreferencingView-Features>`__

-  `Image Panel <#ImageGeoreferencingView-ImagePanel>`__
-  `Map Coordinates Panel. <#ImageGeoreferencingView-MapCoordinatesPanel.>`__
-  `Run Georeferencing. <#ImageGeoreferencingView-RunGeoreferencing.>`__
-  `Save and Load Marks. <#ImageGeoreferencingView-SaveandLoadMarks.>`__

`Project Plan <#ImageGeoreferencingView-ProjectPlan>`__

`Downloads <#ImageGeoreferencingView-Downloads>`__

`Running the Georeferencing View <#ImageGeoreferencingView-RunningtheGeoreferencingView>`__

`Help <#ImageGeoreferencingView-Help>`__

`FAQ <#ImageGeoreferencingView-FAQ>`__

Motivation
==========

The image Georeferencing process was recently included in the uDig-platform, it is a good solution,
but we found that does not respond to some of our "user stories". So, we decided to begin a new
development reusing all existent code and extend it with new user interactions and functionalities.

Features
========

| The figure shows the Image Georeferencing View integrated in the uDig desktop. This new tool
allows you to load an image, specify a set of marks in it and associate the set of coordinates for
each one.
|  |image0|

Image Panel
-----------

-  Load Image
-  Add Mark
-  Delete Mark
-  Delete All Marks
-  Move Mark
-  Zoom in/out
-  Fit
-  Pan

Map Coordinates Panel.
----------------------

| Use this panel to edit the list of coordinates (x,y). As result of this action a mark will be
showed on the Map.
|  Georeferencing Map Tools. The coordinates (x,y) associated to a mark in the Image Panel can be
adquired locating a mark in the current map. The coordinates of that mark on the map will be
registered in the Map Coordinate Panel.

-  Add Mark on Map
-  Delete Mark from Map
-  Move Mark on Map

Run Georeferencing.
-------------------

If all parameters are OK for the georeferencing process, this button will be enabled. As result of
the georeferencing process the image will be saved in the path specified and it will be loaded in
the current map. Once this is done, the view does not lose any parameters, so you can change the
image marks or the coordinate set in order to generate a new georeferencing image. This strategy
allows you to work by proof and error until you get the desired result.

Save and Load Marks.
--------------------

You can save the set of marks edited in your session. Thus, you could load those marks and continue
your work or apply the same set of marks in a new image. For example, if you are working with
meteorologycal phenomena in an specific zone, you can load the new image and load the set of marks
saved in the previous session, saving you a lot of work.

Project Plan
============

Iteration

Finished

Status

1.0.0-m4 Analysis Prototype

2011/04/12

|image1|

1.0.0-m5 Image Manipulation

2011/04/20

|image2|

1.0.0-m6 Move the georeferencing tools to coordinate panel

2011/05/4

|image3|

1.0.0-rc1 Improve Zoom Image (in/out), develop Image Pan tool

2011/05/11

|image4|

1.0.0-rc2 Refactoring Image Tools

2011/05/10

|image5|

1.0.0-rc3 Bug fixing

2011/05/19

|image6|

1.0.0 Release Help review / code review

2011/06/03

|image7|

1.0.0 Code review

2011/06/17

|image8|

+----------------------------+
| |image12|                  |
| **Status**                 |
|                            |
| -  |image13| In progress   |
| -  |image14| Released      |
                            
+----------------------------+

Downloads
=========

We publish the last development snapshot in the community update site for uDig 1.2.1 To install this
feature, use the **uDig update manager wizard**

#. Open up Help > Find and Install... from the menu bar
#. Select the **Search for New Features to Install** radio button and press Next
#. If you had not register the uDig 1.2 community site, add **New Remote Site** using the following
   url: http://udig.refractions.net/files/update/1.2/community/
#. From the **Search Results** page place a checkmark next to the latest release available in
   **Axios > Axios uDig Extensions** and press **Next** to download.
#. **Agree** to the license (which is LGPL)
#. **Confirm** the installation location
#. When you get a warning about this feature not being signed just click **Install All**
#. When prompted press **Yes to restart uDig**

Running the Georeferencing View
===============================

To open the Georeferencing View follow this steps:

#. Open or Create a **Map**.
#. In the **Window** menu select **Show View > Other**
#. Expand the Other folder and select **Georeferencing**.
#. Udig will display the **Image Georeferencing View**.

Help
====

| The help is provided with this extension. Additionally, It is available in:
|  http://udig.refractions.net/confluence/display/EN/Image+Georeferencing+view

FAQ
===

| Q: The image (the geotif file) is not presented in the map.
|  A: Be sure that the udig 1.2.1 installation contains the jai-1\_1\_3 and image and
jai\_imageio-1\_1 in its jre/lib

| 

Attachments:

| |image15| `georeferncing-view.png <download/attachments/13238822/georeferncing-view.png>`__
(image/png)

+-------------+----------------------------------------------------------+
| |image17|   | Document generated by Confluence on Aug 11, 2014 12:24   |
+-------------+----------------------------------------------------------+

.. |image0| image:: /images/image_georeferencing_view/georeferncing-view.png
.. |image1| image:: images/icons/emoticons/check.gif
.. |image2| image:: images/icons/emoticons/check.gif
.. |image3| image:: images/icons/emoticons/check.gif
.. |image4| image:: images/icons/emoticons/check.gif
.. |image5| image:: images/icons/emoticons/check.gif
.. |image6| image:: images/icons/emoticons/check.gif
.. |image7| image:: images/icons/emoticons/check.gif
.. |image8| image:: images/icons/emoticons/check.gif
.. |image9| image:: images/icons/emoticons/information.gif
.. |image10| image:: images/icons/emoticons/star_green.gif
.. |image11| image:: images/icons/emoticons/check.gif
.. |image12| image:: images/icons/emoticons/information.gif
.. |image13| image:: images/icons/emoticons/star_green.gif
.. |image14| image:: images/icons/emoticons/check.gif
.. |image15| image:: images/icons/bullet_blue.gif
.. |image16| image:: images/border/spacer.gif
.. |image17| image:: images/border/spacer.gif
