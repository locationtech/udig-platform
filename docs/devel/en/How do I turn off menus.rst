How do I turn off menus
=======================

If you have just completed the RCP Tutorial and want to figure out how to "scrub out the udig menus"
and start your application with a clean slate ... you are out of luck (that is not the way Eclipse
RCP works). One of the Eclipse House Rules is "Add don't replace" meaning that the uDig menus cannot
be removed (they can however be turned off).

UDIGApplication
===============

Review the page on UDIGApplication and remember it is just an example showcasing how you can use the
framework.

* :doc:`04 Using UDIGApplication`


Read the above page; here are some additional ways you can take charge:

#. UDIGApplication can be configured:

   * :doc:`Using the UDIGWorkbenchAdvisor`

   * :doc:`Using UDIGMenuBuilder`

   * :doc:`Using WorkbenchConfigurations`


#. UDIGApplication can be Replaced

   -  Copy the source code (open source means open the source!) as an example for your own custom
      application; in particular you can tweak your copy of UDIGApplication with a different
      workbench advisor and configure the menus to your needs.

#. UDIGApplication can be Reused

   -  You can extend UDIGApplication and just change the methods you need; this allows you to reuse
      the init method that checks for JAI etc...
   -  We also made the checks into a static method so you can do these things from your own
      application

ActionSets
----------

The RCP Tutorial covered how to use action sets as one of the steps; and also again in the bonus
questions (where you "turn on" the style menu contribution). You can use these facilities to "turn
off" things as well.

.. figure:: images/icons/emoticons/warning.gif
   :align: center
   :alt: 

**Eclipse Help**

Extension Point Schema:
:doc:`org.eclipse.ui.actionSets`

 The above links are the most recent at the time of writing; use the eclipse help provided with your
eclipse for accurate documentation.

There are two ways to contribute menus:

-  `06 Traditional Menus using Actions and
   ActionSets <06%20Traditional%20Menus%20using%20Actions%20and%20ActionSets.html>`_
-  `07 New Menus based on Commands, Handlers and Key
   Bindings <07%20New%20Menus%20based%20on%20Commands,%20Handlers%20and%20Key%20Bindings.html>`_

Check the eclipse help for details; you may find that some uDig menu contributions need to be
patched so you can turn them off smoothly. If so jump on udig-devel with a patch and we will be
happy to help.

Also note that we process some of our uDig extension points into menu and toolbar contributions:

-  `09 uDig menus using Operations and
   Tools <09%20uDig%20menus%20using%20Operations%20and%20Tools.html>`_

Finally editors can contribute their own batch of menus when they are open; including the uDig "Map
Editor".

Activities
----------

The **Activities** extension point is designed to do exactly what you want - turn off parts of the
user interface that are not useful to your users. It is much more powerful than just turning off
menus; it can also turn off views, toolbar contributions and so on.

.. figure:: images/icons/emoticons/warning.gif
   :align: center
   :alt: 

**Eclipse Help**

Extension Point Schema:
:doc:`org.eclipse.ui.activties`

 The above links are the most recent at the time of writing; use the eclipse help provided with your
eclipse for accurate documentation.

MapViewer
---------

Finally we have split apart uDig so you can embed it into an existing RCP Application in the form of
a MapViewer. While you do not get the full uDig Map Editor this is a good choice if you just want to
add a Map to an existing business application such as an asset manager or business intelligence
front end.

This is the subject of a couple of the commercial training tutorials.

The source code for this tutorial is available in the SDK:

-  "net.refractions.udig.tutorials.rcp" and "net.refractions.udig.tutorials.rcp-feature"
-  "eu.udig.tutorials.tool-view" (also shows how to remove tools from the toolbar)

For details on how to review source code:

* :doc:`Examples`


You are of course encouraged to contact one of the uDig PSC members to arrange a training course for
your team.
