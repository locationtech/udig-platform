Legend View
###########

uDig : Legend View

This page last changed on Sep 27, 2012 by nazareno.chan@lisasoft.com.

Motivation
----------

They Layer view is focused on showing the user draw order; we want a Legend view that focuses on the
information the user is presenting using their map.

Inspiration
-----------

Our inspiration is a normal paper map "Key"; showing the layers of interest and symbols and
categories as needed.

-  http://www.kidsgeo.com/geography-for-kids/0027-map-key-and-scale.php

Proposal
--------

The Legend View will be a modified Layer View that has folders to group layers; and where layers can
be opened up to show the categories of information they display (for example when theming).

We would also like Legend view to be able to quickly "show and hide" book keeping layers such as
"background" layers and "map decorators" which are used to draw the map but are not the key
information being communicated.

Example:

-  Layer folders may be organised by subject matter; or by content type.
-  When theming by an attribute; the categories of attribute data can be displayed with their
   associated colour or symbol.
-  | When a smooth gradient is displayed we show a smooth gradient with key values marked such as
   min, max and median.

   | Screen mock-ups
   |  (`view as
   slideshow </confluence/plugins/advanced/gallery-slideshow.action?pageId=13534672&decorator=popup&galleryTitle=Screen+mock-ups>`__)

    

   |image0|

    

   |image1|

    

   |image2|

    

    

   Original Layer View

    

   Legend view is similar

    

   Filter map decorators

    

    

   |image3|

    

   |image4|

    

   |image5|

    

    

   Filter background layers

    

   Group into folders

    

   Show categories rendered from style

    

   | PDF Mockup: `LegendView.pdf <download/attachments/13534672/LegendView.pdf>`__

Cannot resolve external resource into attachment.

Outline:

-  We will update our core Map/Layer EMF model. Paul will be using his recent Eclipse Modelling
   Framework experience on this one.

   -  IFolder - used to group layers
   -  ICategory - generated from Style (either from Rules or from categorisation or interpolate
      functions)

-  Legend support classes will require

   -  Control of Display Name
   -  Control of Icon used
   -  Show / Hide in Legend

-  Layer order will be similar to power point (move forward, move backward, move to front, move to
   back buttons).

   -  Layer order buttons will respect "natural" ordering (raster, polygon, line, point) so "move to
      back" on a point layer will place it just in front of the first line layer.
   -  Display order will be handled by enabling users to select a layer and move up and down in the
      map display (and Layer view) but not changing the order in the Legend View.

-  All legend items can be shown/hidden from the legend

   -  Shortcut - Hide/Show Mapgraphic toggle in the legend view toolbar to filter all map graphics
      in one go
   -  Shortcut - Hide/Show Background toggle in the legend view toolbar to filter all background
      layers in one go

-  Consider

   -  Grid toggle for legend view toolbar (would need to add a grid to the map if not already
      present)

Status
------

Project Steering committee support:

-  Andrea Antonello: +1
-  Jesse Eichar: +0
-  Jody Garnett: +1
-  Mauricio Pazos: +1

Committer Support:

-   

A vote of -1 requires an alternate suggestion; community members are invited to indicate
support/suggestions.

Documentation
-------------

-  `Legend View <http://udig.refractions.net/confluence//display/EN/Legend+View>`__ reference page
-  `Using the Legend
   View <http://udig.refractions.net/confluence//display/EN/Using+the+Legend+View>`__ Tasks page

Tasks
=====

A list of the tasks needed to accomplish this change; if you prefer you can use a single Jira issue
with subtasks. It is important to include any deadlines so the community knows when you are working
to a schedule.

 

no progress

|image6|

in progress

|image7|

blocked

|image8|

help needed

|image9|

done

Quick definition of LegendView based on Layer list:

#. |image10| NZ: Introduce Naz to udig-devel (hi Naz!)

   -  |image11| NZ: Review RFC and ask questions
   -  |image12| JG: Hunt down wireframe diagram

#. |image13| NZ: Send to community for review and feedback
#. |image14| PP: Paul will update the EMF module with Legend support classed (icon, display name,
   show/hide in legend) 

   -  |image15| ILayer
   -  |image16| IFolder
   -  |image17| ICategory

#. |image18| NZ: initial Legend View

   -  |image19| Straight up copy of Layers view
   -  |image20| Toggle filtering based on layer type (MapGraphics toggle/Background layers)

#. |image21| CHECKPOINT: Commit and pull request

   -  https://github.com/uDig/udig-platform/pull/101

Making LegendView working against LegendItemList:

#. |image22| Migrate to LegendItemList (that paul created above)

   -  |image23| Migrate content provider over to Map Legend Item List (it will be empty)
   -  Note ICategory is created from SLD and not subject to AddLayerCommand

#. |image24| Create AddFolderItemCommand (call from view menu?)
#. Create AddLayerItemCommand (call from view menu?

   -  |image25| Create AddLayerItemCommand by copying AddLayerCommand as starting point
   -  Aside: Quick Hack if you want to see something - update **AddLayerItemCommand** to add the
      Layer to both lists (you would need to copy the layer as it can only belong in one list)

#. |image26| Create drag/drop action to control moving layers in and out of folders
#. |image27| QA Check and initial `Home <Home.html>`__ user docs

   -  |image28| Reference `Legend
      View <http://udig.refractions.net/confluence//display/EN/Legend+View>`__
   -  |image29| Tasks Page \ `Using the Legend
      View <http://udig.refractions.net/confluence//display/EN/Using+the+Legend+View>`__

#. |image30| CHECKPOINT: Commit an pull request

Integration (team effort):

#. |image31| First attempted failed (Removing ContextModel and and implementing Map.getLayerList()
   by traversing legend in z-order)
#. |image32| Second attempt at updating EMP model

   -  |image33| Leave Context Model in place
   -  |image34| Add Map.getLengend() as a list of LegendItem
   -  |image35| LayerLegendItem as a reference to Layer
   -  |image36| LegendItem will be the data model for LegendView and LegendMapGraphic (volunteer
      permitted)
   -  |image37| Sync with Layers
   -  |image38| Hack: Proof of concept modify AddLayerCommand to test integration
   -  |image39| Listen to model for layer list changes, and add / remove LayerLegendItem

#. |image40| Review how **LayerView** listens to events
#. |image41| CHECKPOINT: Commit and pull request

The following is now out of scope.

Process style into categories for the complete legend experience:

#. Introduce ICategory
#. Generate from Style to a category data model stored on the layer blackboard

   -  Listen to SLD change; and trigger code to review the style and generate out category data
      model

#. Update the Legend Content Providder to check for category information on the layer blackbaord

   -  Update LabelProviders and so on to correctly display the category information

#. Update user guide documentation

   -  Reference `Legend View <http://udig.refractions.net/confluence//display/EN/Legend+View>`__
   -  Tasks Page `Using the Legend
      View <http://udig.refractions.net/confluence//display/EN/Using+the+Legend+View>`__

#. CHECKPOINT: Commit and pull request

Status:

-  `UDIG-1883 <http://jira.codehaus.org/browse/UDIG-1883>`__ Legend View
-  |image42| `UDIG-1884 <http://jira.codehaus.org/browse/UDIG-1884>`__ Legend Support Classes

| 

Attachments:

| |image43| `LegendViewCategory.png <download/attachments/13534672/LegendViewCategory.png>`__
(image/png)
|  |image44| `LegendViewFolder.png <download/attachments/13534672/LegendViewFolder.png>`__
(image/png)
|  |image45|
`LegendViewFilterBackground.png <download/attachments/13534672/LegendViewFilterBackground.png>`__
(image/png)
|  |image46|
`LegendViewFilterDecorators.png <download/attachments/13534672/LegendViewFilterDecorators.png>`__
(image/png)
|  |image47| `LegendView.png <download/attachments/13534672/LegendView.png>`__ (image/png)
|  |image48| `LayerView.png <download/attachments/13534672/LayerView.png>`__ (image/png)
|  |image49| `LegendView.pdf <download/attachments/13534672/LegendView.pdf>`__ (application/pdf)

+-------------+----------------------------------------------------------+
| |image51|   | Document generated by Confluence on Aug 11, 2014 12:31   |
+-------------+----------------------------------------------------------+

.. |image0| image:: download/thumbnails/13534672/LayerView.png
   :target: /confluence/plugins/advanced/gallery-slideshow.action?imageNumber=1&pageId=13534672&decorator=popup&galleryTitle=Screen+mock-ups
.. |image1| image:: download/thumbnails/13534672/LegendView.png
   :target: /confluence/plugins/advanced/gallery-slideshow.action?imageNumber=2&pageId=13534672&decorator=popup&galleryTitle=Screen+mock-ups
.. |image2| image:: download/thumbnails/13534672/LegendViewFilterDecorators.png
   :target: /confluence/plugins/advanced/gallery-slideshow.action?imageNumber=3&pageId=13534672&decorator=popup&galleryTitle=Screen+mock-ups
.. |image3| image:: download/thumbnails/13534672/LegendViewFilterBackground.png
   :target: /confluence/plugins/advanced/gallery-slideshow.action?imageNumber=4&pageId=13534672&decorator=popup&galleryTitle=Screen+mock-ups
.. |image4| image:: download/thumbnails/13534672/LegendViewFolder.png
   :target: /confluence/plugins/advanced/gallery-slideshow.action?imageNumber=5&pageId=13534672&decorator=popup&galleryTitle=Screen+mock-ups
.. |image5| image:: download/thumbnails/13534672/LegendViewCategory.png
   :target: /confluence/plugins/advanced/gallery-slideshow.action?imageNumber=6&pageId=13534672&decorator=popup&galleryTitle=Screen+mock-ups
.. |image6| image:: images/icons/emoticons/star_yellow.gif
.. |image7| image:: images/icons/emoticons/error.gif
.. |image8| image:: images/icons/emoticons/warning.gif
.. |image9| image:: images/icons/emoticons/check.gif
.. |image10| image:: images/icons/emoticons/check.gif
.. |image11| image:: images/icons/emoticons/check.gif
.. |image12| image:: images/icons/emoticons/check.gif
.. |image13| image:: images/icons/emoticons/check.gif
.. |image14| image:: images/icons/emoticons/check.gif
.. |image15| image:: images/icons/emoticons/check.gif
.. |image16| image:: images/icons/emoticons/check.gif
.. |image17| image:: images/icons/emoticons/check.gif
.. |image18| image:: images/icons/emoticons/check.gif
.. |image19| image:: images/icons/emoticons/check.gif
.. |image20| image:: images/icons/emoticons/check.gif
.. |image21| image:: images/icons/emoticons/star_yellow.gif
.. |image22| image:: images/icons/emoticons/check.gif
.. |image23| image:: images/icons/emoticons/check.gif
.. |image24| image:: images/icons/emoticons/check.gif
.. |image25| image:: images/icons/emoticons/check.gif
.. |image26| image:: images/icons/emoticons/check.gif
.. |image27| image:: images/icons/emoticons/check.gif
.. |image28| image:: images/icons/emoticons/check.gif
.. |image29| image:: images/icons/emoticons/check.gif
.. |image30| image:: images/icons/emoticons/star_yellow.gif
.. |image31| image:: images/icons/emoticons/error.gif
.. |image32| image:: images/icons/emoticons/check.gif
.. |image33| image:: images/icons/emoticons/check.gif
.. |image34| image:: images/icons/emoticons/check.gif
.. |image35| image:: images/icons/emoticons/check.gif
.. |image36| image:: images/icons/emoticons/check.gif
.. |image37| image:: images/icons/emoticons/check.gif
.. |image38| image:: images/icons/emoticons/check.gif
.. |image39| image:: images/icons/emoticons/check.gif
.. |image40| image:: images/icons/emoticons/check.gif
.. |image41| image:: images/icons/emoticons/star_yellow.gif
.. |image42| image:: images/icons/emoticons/check.gif
.. |image43| image:: images/icons/bullet_blue.gif
.. |image44| image:: images/icons/bullet_blue.gif
.. |image45| image:: images/icons/bullet_blue.gif
.. |image46| image:: images/icons/bullet_blue.gif
.. |image47| image:: images/icons/bullet_blue.gif
.. |image48| image:: images/icons/bullet_blue.gif
.. |image49| image:: images/icons/bullet_blue.gif
.. |image50| image:: images/border/spacer.gif
.. |image51| image:: images/border/spacer.gif
