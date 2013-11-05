How to turn stuff off
=====================

This is a common question on the email list, this page has a couple useful starting points.

Ground Rules: Add Don't Replace
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Just a pointer: The idea of turning off a contribution is not supported by Eclipse - one of the
Eclipse House Rules is "Add Don't Replace". The idea being that the functionality should still 
be present and you should allow the user to choose what implementation they want to work with.

There are a couple rules that follow as a consequence of this:

-  "The Other Rule": Provide a dialog allowing the user to choose between options (in cases where
   the number of options can grow without bound). As an example the **Window > View > Other..**
   menu.

You can also think of this as a developer; even if the user interface does not show the option to
the user other implementations may be extending or working with the classes involved.

Eventually we will replace the uDig "Layers" view with something more capable; when that happens the
implementations still needs to be available for all the applications that extend the Layers view for
their own purpose.

Example How to turn off the Data Menu
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The org.locationtech.udig.catalog.ui plugin contributes a "Data" menu, as an example here are some
ways to "turn it off".

Don't include the plugin
''''''''''''''''''''''''

It is more that you should not include org.locationtech.udig.catalog.ui if you don't want what it is
offering.
This is the approach taken by the "rcp tutorial" where just enough uDig plugins are assembled to
display a map; carefully not taking any "ui" plugins so as to avoid menu contributions to the host 
Eclipse RCP application.

ActionSet
---------

Some extension points provide a mechanism to include/exclude functionality from the current
perspective. For menus, operations and tools this mechanism is called "actionSets".

Indeed the data menu is controlled by an ActionSet "org.locationtech.udig.catalog.ui.data.menu"and
this is on Page 18 of the Custom App tutorial. Just don't include this actionset in your perspective
and this menu will not be added.

If the data menu was not controlled by an actionSet; ask on the email list and/or create a patch for
review. We have a policy of not adding hooks such as this until they are requested. This prevents 
the core team from doing extra work that is not needed, and waits until someone wants and is (able 
to test) the functionality resulting in higher quality.

Activities
^^^^^^^^^^

The hard way (and more general purpose) suitable for projects that are not quite as open as uDig is
to make use of an advanced eclipse Platform facility called **activities**.

Remember that when stuff is connected up - we make use of the Platform class to ask for all the
extensions (in the case of the data menu it would be the eclipse menu system doing the processing).

The actual data structure assembled by the platforms:

-  is based on the plugin.xml file
-  merged with any plugin.xml files added by a "fragment" (consider it a hot patch)
-  modified by magic (activities)

Activities allows you to fliter or strip out plugin contributions:

* `<http://wiki.eclipse.org/FAQ_How_do_I_add_activities_to_my_plug-in%3F>`_
* `<http://stackoverflow.com/questions/1415700/disable-plugin-contributions-in-eclipse-rcp-application>`_
* `<http://www.eobjectsoft.com/product/EclipseInterviewQuestions.htm#q15>`_

The idea is that an activity will filter/process the plugin.xml using an almost XSLT like
transformation. Activitations are controlled using the same enablement system as menus (so you can
make use of checks against the current selection and so forth).

.. code-block:: xml

    <extension point="org.eclipse.ui.activities"> 
      <activity id="my.rcp.app.Activity" 
                description="Contributions from org.locationtech.udig.catalog.ui." 
                name="My RCP Activity" />
      <category id="my.rcp.app.Category" 
                description="my.rcp.app Activities" 
                name="My RCP Category">

         <!-- put the activity in the category -->
         <categoryActivityBinding activityId="my.rcp.app.Activity"
                                  categoryId="my.rcp.app.Category"/>

         <!-- bind all contributions from plugin org.locationtech.udig.ui -->
         <activityPatternBinding id="my.rcp.app.Activity"
                                 pattern="org.locationtech.udig.catalog.ui/.*"/>
    </extension>

The interesting bit is the **pattern** which is a regular expression of things
 to filter out. You can also add isEqualityPattern="true" to target one specific
 entry.

This would turn off the extension "foo" in the **org.locationtech.udig.catalog.ui** plugin.

.. code-block:: xml

    <activityPatternBinding id="my.rcp.app.Activity"
       isEqualityPattern="true"
       pattern="org.locationtech.udig.catalog.ui/foo" />

This would turn off any extension ending in "wizard" from the **org.locationtech.udig.catalog.ui**
plugin

.. code-block:: xml

    <activityPatternBinding id="my.rcp.app.Activity"
       pattern="org.locationtech.udig.catalog.ui/[a-z[.]]*Wizard" />

The above example is adapted from the links above; and needs to be tested.
Since the data menu is already controlled by an ActionSet we have not had
to make use of Activities when working with uDig yet.

Eclipse Help:

* `<http://help.eclipse.org/helios/index.jsp?topic=/org.eclipse.platform.doc.isv/reference/extension-points/org_eclipse_ui_activities.html>`_

