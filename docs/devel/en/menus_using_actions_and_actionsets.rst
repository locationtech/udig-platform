Menus using Actions and ActionSets
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Contribution additional functionality into Eclipse RCP applications is traditionally done with
Actions. This functionality is now deprecated; new code should look into use commands and handlers.

Related Information

* `Basic workbench extention points using actions <http://help.eclipse.org/help33/topic/org.eclipse.platform.doc.isv/guide/workbench_basicext.htm>`_
* :doc:`menus_using_commands_and_handlers`
* :doc:`menus_using_operations_and_tools`


Menu Path Constants
^^^^^^^^^^^^^^^^^^^

The class UDIGActionBarAdvisor is used to define the menu paths on this page in the usual manner;
submitting a series of contributions to a "MenuManager" relative to series of known **constant**
locations.

These locations are defined as Constants in the following files:

-  IWorkbenchActionConstants: well known locations for RCP applications
-  Constants: well known locations for uDig applications

Use of IAction
^^^^^^^^^^^^^^

There are many examples of the use IAction, the related extention points are deprecated:

-  org.eclipse.ui.ActionsSets
-  org.eclipse.ui.EditorActions
-  org.eclipse.ui.popupMenus
-  org.eclipse.ui.viewActions

When possible please make use of **org.eclipse.ui.menu**.

Default RCP Menus Paths
^^^^^^^^^^^^^^^^^^^^^^^

For the uDig application (org.locationtech.udig.ui) these reference points are contributed directly
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
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: java

    IMenuManager fileMenu = menuBar.findMenuUsingPath(IWorkbenchActionConstants.M_FILE);

    IAction exit = ActionFactory.QUIT.create(window);
    fileMenu.insertAfter(Constants.FILE_END, exit);

You would be doing this sort of thing if you are rolling your own ActionBarAdvisor as part of a new
RCP Application. As of Eclipse 3.3 you should be able to use the **org.eclipse.ui.menu** extension
point and avoid this step.

Adding an IAction using an ActionSet
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

You can also define ActionSets that map from an IAction to a menu path.

::

    <!-- pending -->

This is the preferred way since the resulting ActionSet can be toggle on and off when switching
perspectives.

Geospatial Platform Menu Paths
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

These paths are contributed by the **org.eclipse.ui.menu** extension point as part of
**org.locationtech.udig.catalog.ui**.

Data menu:

-  data/dataStart
-  ..
-  data/additions
-  data/dataEnd

Catalog view:

-  menu:org.locationtech.udig.catalog.ui.CatalogView?after=additions
-  popup:org.locationtech.udig.catalog.ui.CatalogView?after=additions

Search view:

-  menu:org.locationtech.udig.catalog.ui.Search?after=additions
-  popup:org.locationtech.udig.catalog.ui.Search?after=additions

Available Nouns
^^^^^^^^^^^^^^^

When using a contribution to any context menu:

-  popup:org.eclipse.ui.popup.any?after=additions

Geospatial Application Menu Paths
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

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

Action Set Group Marker Examples
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

To add the a "Data" menu to the top-level menu bar:

#. create org.eclipse.ui.ActionSets --> actionset --> menu
#. put "additions" for the path

   Additions is located between Analysis and Window in the menubar

To add the a "Data" menu to an existing menu:

#. Find out the path to the group marker (see the end of this page for examples)
#. create org.eclipse.ui.ActionSets --> actionset --> menu
#. put the path to your group marker: layer/additions

Additions comes from the **UDIGWorkbenchAdvisor**, or similar class provided by your Custom
application.

Here is an example from UDIGWorkbenchAdvisor:

.. code-block:: java

    private void fillMenuBar( IWorkbenchWindow window, IActionBarConfigurer configurer ) {
            IMenuManager menuBar = configurer.getMenuManager();
            menuBar.add(createFileMenu(window));
            menuBar.add(createEditMenu(window));
            menuBar.add(createNavigationMenu(window));
            menuBar.add(createLayerMenu());
            menuBar.add(createToolMenu());
            menuBar.add(UiPlugin.getDefault().getOperationMenuFactory().getMenu(window));
            menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
            menuBar.add(createWindowMenu(window));
            menuBar.add(createHelpMenu(window));
        }

You can see that IWorkbenchActionConstants.MB\_ADDITIONS is placed between the Tool menu and the
Window menu.

The same strategy can be used to find out where the group markers are in other menus:

.. code-block:: java

    private MenuManager createLayerMenu() {
            MenuManager menu = new MenuManager(Policy.bind("UDIGWorkbenchAdvisor.layerMenu"), "layer");
            menu.add(new GroupMarker("add.ext")); //$NON-NLS-1$
            menu.add(new Separator());
            menu.add(new GroupMarker("edit.ext")); //$NON-NLS-1$

            menu.add(new Separator());
            menu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));

            return menu;
        }

So for the Layer menu there are three group markers:

-  add.ext
-  edit.ext
-  additions

By convention, "additions" is created in the correct spot (before Window and Help for the menu bar,
or before **Exit** in the file menu, or at the end of the menu most everywhere else.
