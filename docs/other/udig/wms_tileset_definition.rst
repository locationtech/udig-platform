Wms Tileset Definition
######################

uDig : WMS TileSet Definition

This page last changed on Mar 08, 2012 by jgarnett.

Motivation
----------

Using a tile server as a background layer results in a great user interface experience. Occasionally
we run into WMS services that have not provided tile set access. What a shame.

Inspiration
-----------

The only thing unique about the WMS-C (i.e. web map server cached) approach is a small data
structure called a "TileSet". This data structure is provided in the WMS GetCapabilities "Vendor
Specific" section; and outlines:

-  Zoom Levels
-  Exact format of GetMap requests to make (this format includes tiled=true in the string)

It is then up to clients to make requests that conform to that description. By having a consistently
formed GetMap request servers can cache the answers and offer a better experience.

And clients can also cache the answer; which uDig does locally in memory; or locally in a temporary
folder.

Proposal
--------

|image0|

**Catalog TileSet Definition**

For this to work we actually only need one small technical change:

-  The GeoResource for an appropriately configured WMS Layer needs to resolve to both a WMSLayer and
   a TileSet data structure
-  We need a wizard or dialog allow users to fill in that TileSet data structure

   -  Need to ask for tile size (remember to take the WMS GetCapabilities max image size into
      account)
   -  Need to collect the zoom levels; should be able to calculate the rest of the numbers
   -  IDEA: We can test the configuration in the wizard by collecting the first level of information
      into our disk cache. This both tests the settings in a non frustrating way while the user can
      fix the problem; and results in a much better initial user experience when they go to use the
      service)
   -  Use DialogSettings to remember from last time; so users can quickly apply this to several
      layers
   -  Consider making use of WMS Layer structure; so a TileSet definition on a "parent" layer can be
      used by a child layer.

-  From there on out the WMSC Render can have a party and cache files locally etc.

The advantage of this approach is the TileSet definition set up once at the catalog level.

**Tile Cache-Control**

We also propose to extend the functionality of the tile cache by allowing the Cache-Control http
header to describe the maximum cache age for each tile. To allow this any service that sets a
Cache-Control header in the form of "max-age=SECONDS, must-revalidate" where SECONDS is an integer
depicting the number of seconds a tile is to be cached will be respected.

To achieve this a cache file will be written along site the tile image on disk containing the time
in seconds the tile is valid for, when the tile is read from disk this file is accessed then the
time added to the tile images last modified time. If this time is before the current time, its
considered stale (out of date).

If the case the tile service does not set Cache-Control headers the cache file is not written and
the tile is never considered stale. To change this you would need to manually remove the tile from
the disk.

Status
------

Project Steering committee support:

-  Andrea Antonello: +1
-  Jesse Eichar: +1
-  Jody Garnett: +1
-  Mauricio Pazos: +1

Committer Support:

-  

A vote of -1 requires an alternate suggestion; community members are invited to indicate
support/suggestions.

Documentation
-------------

Documentation change to `Users Guide <http://udig.refractions.net/confluence//display/EN/Home>`__
(for an accepted change).

Tasks
=====

A list of the tasks needed to accomplish this change; if you prefer you can use a single Jira issue
with subtasks. It is important to include any deadlines so the community knows when you are working
to a schedule.

 

no progress

|image1|

in progress

|image2|

blocked

|image3|

help needed

|image4|

done

Tasks:

#. |image5| Create an IResolveAdapterFactory that is hard coded to produce a TileSet definition for
   a specific WMS Layer
#. |image6| Create a test to use the new factory - testing its resolve
#. |image7| Hook this into the WMSC Rendering chain

   -  |image8| Teach the WMSC Rendering engine to work from TileSet and abstract
      AbstractOpenWebService
   -  |image9| `GEOT-3969 <http://jira.codehaus.org/browse/GEOT-3969>`__ Add getCapabilities to
      AbstractOpenWebService

#. |image10| Create a TileSet Dialog for a Layer
#. |image11| Use IService.getPersistentProperties() to store the tile set definition between runs

   -  |image12| You need to trick it to store IGeoResource properties
   -  |image13| Title cache uses: service.getPersistentProperties().put(getID().toString() +
      "\_title", title)

#. |image14| Update the IResolveAdaptorFactory implementation to use the above data structure
#. |image15| Add screen snaps to Reference section of documentation for the TileSet Dialog

   -  http://udig.refractions.net/confluence/display/EN/TileSet+Dialog

#. |image16| Add step by step instructions to the Tasks section (how to speed up WMS?)

   -  http://udig.refractions.net/confluence/display/EN/Enabling+a+Tileset+from+a+WMS+Server

#. |image17| Add "Tile Set" to the concepts section with a picture

   -  http://udig.refractions.net/confluence/display/EN/Tileset

#. |image18| Code Review
#. |image19| Pull Request: https://github.com/uDig/udig-platform/pull/84
#. |image20| Create the Cache-Control code to invalidate tiles when a Cache-Control header is
   available
#. |image21| Pull request for Cache-Control code
#. |image22| Review of Cache-Control code

Timeframe: Early Jan

Status:

-  `UDIG-1864 <https://jira.codehaus.org/browse/UDIG-1864>`__

| 

Attachments:

| |image23| `wms tile set.png <download/attachments/13534780/wms%20tile%20set.png>`__ (image/png)

+-------------+----------------------------------------------------------+
| |image25|   | Document generated by Confluence on Aug 11, 2014 12:31   |
+-------------+----------------------------------------------------------+

.. |image0| image:: download/attachments/13534780/wms%20tile%20set.png
.. |image1| image:: images/icons/emoticons/star_yellow.gif
.. |image2| image:: images/icons/emoticons/error.gif
.. |image3| image:: images/icons/emoticons/warning.gif
.. |image4| image:: images/icons/emoticons/check.gif
.. |image5| image:: images/icons/emoticons/check.gif
.. |image6| image:: images/icons/emoticons/check.gif
.. |image7| image:: images/icons/emoticons/check.gif
.. |image8| image:: images/icons/emoticons/check.gif
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
.. |image21| image:: images/icons/emoticons/check.gif
.. |image22| image:: images/icons/emoticons/check.gif
.. |image23| image:: images/icons/bullet_blue.gif
.. |image24| image:: images/border/spacer.gif
.. |image25| image:: images/border/spacer.gif
