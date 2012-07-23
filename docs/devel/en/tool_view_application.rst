Tool View Application
#####################

This is detailed RCP example showing how to build a custom application using a Map displayed in a
View (rather than in an Editor).

This workbooks is part of a full day course provided by camptocamp.

ToolView Application
====================

Source Code
-----------

This workbook is part of our commercial training materials.

-  See the main `uDig website <http://udig.refractions.net/users/>`_ for details on training and
   support
-  Commercial Training materials are available to Academic students on request

Source code:

-  Available in your uDig SDK (import the :doc:`source code <code_examples>` from the plugins
   view)
-  plugin:

   * `eu.udig.tutorials.tool-view <https://github.com/uDig/udig-platform/tree/master/tutorials/eu.udig.tutorials.tool-view>`_

Introduction
------------

This plug-in demonstrates:

-  how to integrate a map in a view
-  integrate the uDig tool framework into the view
-  how a tool and a mapgraphic can communicate together using the layer's Blackboard to share
   information
-  how to implement a custom modal tool
-  how to implement a mapgraphic that renders shapes dependent on shapes the tool has put on the
   layer's blackboard
-  how to load all the features at the point of a mouse click
-  how to load all the features that touch a given feature
-  how to implement a custom ToolManager to control which tools are displayed in the toolbar
-  how to show animations with a tool
-  how to draw tool feedback

The main classes of interest are:

-  CustomToolManager
-  SendAlertTool
-  ShowAlertsMapGraphic
-  View

