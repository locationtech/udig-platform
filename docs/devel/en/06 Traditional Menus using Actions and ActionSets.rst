06 Traditional Menus using Actions and ActionSets
=================================================

Contribution additional functionality into Eclipse RCP applications is traditionally done with
Actions. This functionality is now deprecated; new code should look into use commands and handlers.

* :doc:`Menu Path Constants`

* :doc:`Use of IAction`

* :doc:`Default RCP Menus Paths`


   -  `Adding an IAction using an
      ActionBarAdvisor <#06TraditionalMenususingActionsandActionSets-AddinganIActionusinganActionBarAdvisor>`_
   -  `Adding an IAction using an
      ActionSet <#06TraditionalMenususingActionsandActionSets-AddinganIActionusinganActionSet>`_

-  `Geospatial Platform Menu
   Paths <#06TraditionalMenususingActionsandActionSets-GeospatialPlatformMenuPaths>`_
-  `Geospatial Application Menu
   Paths <#06TraditionalMenususingActionsandActionSets-GeospatialApplicationMenuPaths>`_

Related Information
^^^^^^^^^^^^^^^^^^^

-  `Basic workbench extention points using
   actions <http://help.eclipse.org/help33/topic/org.eclipse.platform.doc.isv/guide/workbench_basicext.htm>`_
-  `07 New Menus based on Commands, Handlers and Key
   Bindings <07%20New%20Menus%20based%20on%20Commands,%20Handlers%20and%20Key%20Bindings.html>`_
-  `09 uDig menus using Operations and
   Tools <09%20uDig%20menus%20using%20Operations%20and%20Tools.html>`_

* :doc:`Adding a Menu to uDig 1.0`


Menu Path Constants
-------------------

The class UDIGActionBarAdvisor is used to define the menu paths on this page in the usual manner;
submitting a series of contributions to a "MenuManager" relative to series of known **constant**
locations.

These locations are defined as Constants in the following files:

-  IWorkbenchActionConstants: well known locations for RCP applications
-  Constants: well known locations for uDig applications

Use of IAction
--------------

There are many examples of the use IAction, the related extention points are deprecated:

-  org.eclipse.ui.ActionsSets
-  org.eclipse.ui.EditorActions
-  org.eclipse.ui.popupMenus
-  org.eclipse.ui.viewActions

When possible please make use of **org.eclipse.ui.menu**.

Default RCP Menus Paths
-----------------------

For the uDig application (net.refractions.udig.ui) these reference points are contributed directly
by the UDIGActionBarAdvisor. Most of these are from IWorkbenchActionConstants and can be expected in
any RCP application.

Standard menus expected for any RCP application:

-  file
-  edit
-  additions (group)- ie. IWorkbenchActionConstants.MB\_ADDITIONS
-  window
-  help

File menu:

-  file/fileStart
-  file/new.ext
-  file/new
-  file/close.ext
-  file/close
-  file/save.ext
-  file/save
-  file/additions
-  file/print.ext
-  file/import.ext
-  file/import
-  file/export
-  file/mru
-  file/fileEnd
-  file/quit

Edit menu:

-  edit/editStart
-  edit/undo
-  edit/redo
-  edit/undo.ext
-  edit/cut
-  edit/copy
-  edit/paste
-  edit/cut.ext
-  edit/delete
-  edit/selectAll
-  edit/bookmark
-  edit/editEnd

When creating your own RCP application please ensure the above menu paths are available (other uDig
modules expect to be able to use them). This should be pretty easy as they are all based on the
usual IWorkbenchActionConstants.

Examples menu paths:

-  menu:org.eclipse.ui.main.menu?after=additions
-  menu:org.eclipse.ui.main.menu?after=file/new
-  toolbar:org.eclipse.ui.main.toolbar?after=additions

Example for any popup (must use visibleWhen):

-  popup:org.eclipse.ui.popup.any?after=additions

Adding an IAction using an ActionBarAdvisor
-------------------------------------------

::

    IMenuManager fileMenu = menuBar.findMenuUsingPath(IWorkbenchActionConstants.M_FILE);

    IAction exit = ActionFactory.QUIT.create(window);
    fileMenu.insertAfter(Constants.FILE_END, exit);

You would be doing this sort of thing if you are rolling your own ActionBarAdvisor as part of a new
RCP Application. As of Eclipse 3.3 you should be able to use the **org.eclipse.ui.menu** extension
point and avoid this step.

Adding an IAction using an ActionSet
------------------------------------

You can also define ActionSets that map from an IAction to a menu path.

::

    <!-- pending -->

This is the preferred way since the resulting ActionSet can be toggle on and off when switching
perspectives.

Geospatial Platform Menu Paths
==============================

These paths are contributed by the **org.eclipse.ui.menu** extension point as part of
**net.refractions.udig.catalog.ui**.

Data menu:

-  data/dataStart
-  ..
-  data/additions
-  data/dataEnd

Available Views
~~~~~~~~~~~~~~~

Catalog view:

-  menu:net.refractions.udig.catalog.ui.CatalogView?after=additions
-  popup:net.refractions.udig.catalog.ui.CatalogView?after=additions

Search view:

-  menu:net.refractions.udig.catalog.ui.Search?after=additions
-  popup:net.refractions.udig.catalog.ui.Search?after=additions

Available Nouns
~~~~~~~~~~~~~~~

When using a contribution to any context menu:

-  popup:org.eclipse.ui.popup.any?after=additions

Geospatial Application Menu Paths
=================================

Navigation menu:

-  navigate/navStart
-  ...
-  navigation/additions
-  navigation/navEnd

Layer menu:

-  layer/layerStart
-  ...
-  layer/additions
-  layer/LayerEnd

Map menu:

-  map/mapStart
-  ...
-  map/additions
-  map/mapEnd

