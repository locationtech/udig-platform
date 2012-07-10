Adding a Menu to uDig 1.0
=========================

One of the first things people are interested in doing is adding a menu to the uDig Application.
This topic is covered in the Rich Client Platform section of the programmer's guide because there is
nothing *special* about adding an Menu to uDig.

However, these notes should help prevent everyone rushing out to buy the same book, or help people
struggling through the same pages of online help.

A bit of background information before we get going:

#. Extend the "Action sets" extension point (this is an Eclipse extension point)
#. Your extensions will be inserted in a "group marker" called something like "additions". The
   actual group marker is specific to the menu. We will have an example in a bit.

To Add a new Menu
~~~~~~~~~~~~~~~~~

To add the a "Data" menu to the top-level menu bar:

#. create org.eclipse.ui.ActionSets --> actionset --> menu
#. put "additions" for the path
    Additions is located between Analysis and Window in the menubar

To Add to an existing Menu
~~~~~~~~~~~~~~~~~~~~~~~~~~

To add the a "Data" menu to an existing menu:

#. Find out the path to the group marker (see the end of this page for examples)
#. create org.eclipse.ui.ActionSets --> actionset --> menu
#. put the path to your group marker:
    layer/additions

Where does "Additions" Come From?
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Additions comes from the
:doc:`UDIGWorkbenchAdvisor`

- the link will take you to the source code. You are probably working with your own workbench
advisor that extends this one.

Here is an example:

::

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
or before **Exit** in the file menu, or at the end of the menu most everywhere else.
