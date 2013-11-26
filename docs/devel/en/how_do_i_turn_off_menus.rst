How Do I Turn Off Menus
#######################

If you have just completed the RCP Tutorial and want to figure out how to "scrub out the udig menus"
and start your application with a clean slate ... you are out of luck (that is not the way Eclipse
RCP works). One of the Eclipse House Rules is "Add don't replace" meaning that the uDig menus cannot
be removed (they can however be turned off).

UDIGApplication
===============

Review the page on UDIGApplication and remember it is just an example showcasing how you can use the
framework.

* :doc:`using_udigapplication`


Read the above page; here are some additional ways you can take charge:

#. UDIGApplication can be configured:

   * :doc:`using_the_udigworkbenchadvisor`

   * :doc:`using_udigmenubuilder`

   * :doc:`using_workbenchconfigurations`


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

.. warning::
   **Eclipse Help**

   Extension Point Schema: `org.eclipse.ui.actionSets <http://help.eclipse.org/indigo/topic/org.eclipse.platform.doc.isv/reference/extension-points/org_eclipse_ui_actionSets.html>`_
   The above links are the most recent at the time of writing; use the eclipse help provided with your 
   eclipse for accurate documentation.

There are two ways to contribute menus:

* :doc:`menus_using_actions_and_actionsets`

* :doc:`menus_using_commands_and_handlers`


Check the eclipse help for details; you may find that some uDig menu contributions need to be
patched so you can turn them off smoothly. If so jump on udig-devel with a patch and we will be
happy to help.

Also note that we process some of our uDig extension points into menu and toolbar contributions:

* :doc:`menus_using_operations_and_tools`


Finally editors can contribute their own batch of menus when they are open; including the uDig "Map
Editor".

Activities
----------

The **Activities** extension point is designed to do exactly what you want - turn off parts of the
user interface that are not useful to your users. It is much more powerful than just turning off
menus; it can also turn off views, toolbar contributions and so on.

.. warning::
   **Eclipse Help**

   Extension Point Schema: `org.eclipse.ui.activties <http://help.eclipse.org/indigo/topic/org.eclipse.platform.doc.isv/reference/extension-points/org_eclipse_ui_activities.html>`_
   The above links are the most recent at the time of writing; use the eclipse help provided with 
   your eclipse for accurate documentation.

MapViewer
---------

Finally we have split apart uDig so you can embed it into an existing RCP Application in the form of
a MapViewer. While you do not get the full uDig Map Editor this is a good choice if you just want to
add a Map to an existing business application such as an asset manager or business intelligence
front end.

This is the subject of a couple of the commercial training tutorials.

The source code for this tutorial is available in the SDK:

-  "org.locationtech.udig.tutorials.rcp" and "org.locationtech.udig.tutorials.rcp-feature"
-  "org.locationtech.udig.tutorials.tool-view" (also shows how to remove tools from the toolbar)

For details on how to review source code:

* :doc:`examples`


You are of course encouraged to contact one of the uDig PSC members to arrange a training course for
your team.
