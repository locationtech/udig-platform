Menus using Commands and Handlers
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The menu systems has been over hauled for Eclipse 3.3; so best practise has changed a bit since the
use of ActionSets.

Related Information:

* `New menu contribution extention <http://richclientplatform.blogspot.com/2007/07/new-menu-contribution-extension.html>`_

Commands
^^^^^^^^

Commands are used to declare desired behaviour (ie **Show** would be a command). They can be
organized into categories (like Navigation).

Implementations for your commands are called handlers, the handler used may be context sensitive:

-  Show command on the Map Editor may zoom out to show all the layers
-  Show command on the Layer View may zoom out to show the selected layer
-  Show command on the Table View may zoom out to show the current selection

Commands can get a bit more fancy:

-  You can use categories to organize your commands
-  You can use parameters (this is how the "Show View" command is implemented once and configured to
   show multiple views.

Related Information:

* `org.eclipse.ui.commands <http://help.eclipse.org/help33/index.jsp?topic=/org.eclipse.platform.doc.isv/reference/extension-points/org_eclipse_ui_commands.html>`_ extension point
* `platform command framework <http://wiki.eclipse.org/Platform_Command_Framework#Commands>`_ in the eclipse wiki
* `Basic workbench extension points using commands <http://help.eclipse.org/help33/topic/org.eclipse.platform.doc.isv/guide/workbench_cmd_commands.htm>`_ in the PDE programmers guide.

Creating a new Command using the Extension Point:

.. code-block:: xml

    <extension
          point="org.eclipse.ui.commands">
       <category
             description="Actions that change what is shown"
             id="org.locationtech.udig.navigation"
             name="Navigation">
       </category>
       <command
             categoryId="org.locationtech.udig.navigation"
             description="Show all content"
             id="org.locationtech.udig.show"
             name="Show All">
       </command>
    </extension>

Creating a new Command using Java:

.. code-block:: java

    ICommandService cmdService =
      (ICommandService) getSite().getService(ICommandService.class);

    Category navigation =
      cmdService.getCategory("org.locationtech.udig.navigation");

    if (!navigation.isDefined()) {
      navigation.define("Navigation", "Actions that change what is shown");
    }

    Command show =
      cmdService.getCommand("org.locationtech.udig.show");

    if (!show.isDefined()) {
      show.define("Show", "Show content on screen", navigation);
    }

To get the workbench ICommandService:

.. code-block:: java

    public class StartupOperations implements IStartup {
        public void earlyStartup(){
           IWorkbench workbench = PlatformUI.getWorkbench();
           ICommandService cmdService = (ICommandService) workbench.getService( ICommandService.class );
           ...
        }
    }

Handlers
^^^^^^^^

Handlers are an implementation of a command. Several handlers may be defined for a given command -
choosing which one is active is the fun part.

You can define an **activeWhen** expressions in order to make your handler context sensitive.

A handler is chosen based on several bits of magic:

-  Variables used in activeWhen expressions are compared; the handler with the most specific
   variables is chosen
-  Default Handler (ie one with no conditions) is used as the last resort.

Figuring out if the Handler is Enabled

-  You can also define an expression to enabled or disable the handler.

The variables used in the activeWhen and enabledWhen expressions come from
