Using The Udigworkbenchadvisor
##############################

The UDIGWorkbenchAdvisor is a stratagy objected used to configure the application workbench. As such
it defines how the menu and tool bars are structured, provides a default perspective to layout the
views and so on.

You may wish to override the
`UDIGWorkbenchAdvisor <http://svn.geotools.org/udig/trunk/plugins/org.locationtech.udig.ui/src/net/refractions/udig/internal/ui/UDIGWorkbenchAdvisor.java>`_
(link to source code) when creating your own application.

The menus scene in the uDig are a combination of:

-  The UDIGWorkbenchAdvistor providing the general layout
-  Plugins adding additional menus using **org.eclipse.ui.menus** extentions
-  Note the perspectives may set menus visibility, often using "ActionSets" to toggle of a category
   of contributions in one go

For uDig 1.0.1 the straight up use of a WorkbenchAdvisor was hacked out of the way and replaced with
**UDIGMenuBuilder**; the functionality is similar - UDIGMenuBuilder can be used to:

-  Set up the uDig menus from scratch (like a workbench advisor)
-  Add the uDig menus to an existing RCP application

UDIGWorkbnechAdvisor Additions and Group Markers
================================================

When defining menu bar structure "Additions" is used to register a "target" which other
contributions can insert additional menu actions.

Here is an example:

::

    private void fillMenuBar( IWorkbenchWindow window, IActionBarConfigurer configurer ) {
        IMenuManager menuBar = configurer.getMenuManager();
        menuBar.add(createFileMenu(window));
        menuBar.add(createEditMenu(window));
        menuBar.add(createNavigationMenu(window));
        menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));

        menuBar.add(createLayerMenu());
        menuBar.add(createMapMenu());
        menuBar.add(createDataMenu());    
        menuBar.add(createWindowMenu(window));
        menuBar.add(createHelpMenu(window));
    }

You can see that IWorkbenchActionConstants.MB\_ADDITIONS is placed between the Navigation menu and
the Layer menu. Any additional "top level" menus (for example the top level menu "Road" in road
editing application) would appear here.

For the complete list either use the source as shown below or review
 this page:

* :doc:`menus_using_actions_and_actionsets`


Use the Source Luke
===================

While the above list of Additions is great for uDIG 1.1.0 you may be extending a different version
of uDig; or a project based on uDig. Have a look at the source code and check where the group
markers in your application:

Example using the Layer menu from uDig 1.0:

::

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
or before Exit in the file menu, or at the end of the menu most everywhere else.
