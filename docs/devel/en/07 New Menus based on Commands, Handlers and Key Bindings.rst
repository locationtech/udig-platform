07 New Menus based on Commands, Handlers and Key Bindings
=========================================================

The menu systems has been over hauled for Eclipse 3.3; so best practise has changed a bit for uDig
1.1.

* :doc:`Commands`

* :doc:`Handlers`

* :doc:`Key Bindings`

* :doc:`Context`

* :doc:`Menus`


Related Information:

-  `New menu contribution
   extention <http://richclientplatform.blogspot.com/2007/07/new-menu-contribution-extension.html>`_

Commands
========

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

* :doc:`org.eclipse.ui.commands`

   extension point
-  `platform command framework <http://wiki.eclipse.org/Platform_Command_Framework#Commands>`_ in
   the eclipse wiki
-  `Basic workbench extension points using
   commands <http://help.eclipse.org/help33/topic/org.eclipse.platform.doc.isv/guide/workbench_cmd_commands.htm>`_
   in the PDE programmers guide.

Creating a new Command using the Extension Point:

::

    <extension
          point="org.eclipse.ui.commands">
       <category
             description="Actions that change what is shown"
             id="net.refractions.udig.navigation"
             name="Navigation">
       </category>
       <command
             categoryId="net.refractions.udig.navigation"
             description="Show all content"
             id="net.refractions.udig.show"
             name="Show All">
       </command>
    </extension>

Creating a new Command using Java:

::

    ICommandService cmdService =
      (ICommandService) getSite().getService(ICommandService.class);

    Category navigation =
      cmdService.getCategory("net.refractions.udig.navigation");

    if (!navigation.isDefined()) {
      navigation.define("Navigation", "Actions that change what is shown");
    }

    Command show =
      cmdService.getCommand("net.refractions.udig.show");

    if (!show.isDefined()) {
      show.define("Show", "Show content on screen", navigation);
    }

To get the workbench ICommandService:

::

    public class StartupOperations implements IStartup {
        public void earlyStartup(){
           IWorkbench workbench = PlatformUI.getWorkbench();
           ICommandService cmdService = (ICommandService) workbench.getService( ICommandService.class );
           ...
        }
    }

Handlers
========

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
**org.eclipse.ui.ISources**; and we will probably have to contribute some uDig specific ones. For
now we are sticking with the normal RCP stuff:

-  Selection
-  activeContexts - used to make uDig nice and modal
-  etc...

Creating a Handler using the Extention Point:

::

    <extension
          point="org.eclipse.ui.handlers">
       <handler
             class="net.refractions.udig.handlers.showMap"
             commandId="net.refractions.udig.show">
          <activeWhen>
             <with variable="activeContexts">
                <iterate operator="or">
                   <equals value="net.refractions.udig.contexts.map"/>
                </iterate>
             </with>
          </activeWhen>
       </handler>
       <handler
             class="net.refractions.udig.handlers.showFeature"
             commandId="net.refractions.udig.show">
          <activeWhen>
             <with variable="activeContexts">
                <iterate operator="or">
                   <equals value="net.refractions.udig.contexts.feature"/>
                </iterate>
             </with>
          </activeWhen>
       </handler>
       <handler
             class="net.refractions.udig.handlers.showSelection"
             commandId="net.refractions.udig.show">
          <activeWhen>
             <with variable="activeContexts">
                <iterate operator="or">
                   <equals value="net.refractions.udig.contexts.selection"/>
                </iterate>
             </with>
          </activeWhen>
       </handler>
    </extension>

Creating a Handler using Java:

::

    // pending

Key Bindings
============

Key bindings are one of the things that cannot be updated Programatically; however they will only be
active when the context is active.

::

    <extension
          point="org.eclipse.ui.bindings">
       <key
             commandId="net.refractions.show"
             contextId="net.refractions.udig.contexts.feature"
             schemeId="org.eclipse.ui.defaultAcceleratorConfiguration"
             sequence="HOME">
       </key>
       <key
             commandId="net.refractions.show"
             contextId="net.refractions.udig.contexts.map"
             schemeId="org.eclipse.ui.defaultAcceleratorConfiguration"
             sequence="HOME">
       </key>
       <key
             commandId="net.refractions.show"
             contextId="net.refractions.udig.contexts.selection"
             schemeId="org.eclipse.ui.defaultAcceleratorConfiguration"
             sequence="HOME">
       </key>
    </extension>

Context
=======

Contexts are used to make the application respond to what is going on (in a context sensitive
manner). For uDig this means that the application menu and tool bars will respond based on what kind
of content the current View or Map Editor is working with.

To make your view context sensitive add the following to createPartControl(..):

::

    public void createPartControl(Composite parent) {
      ...
      IContextService contextService =
         (IContextService) getSite().getService(IContextService.class);

      IContextActivation contextActivation =
         contextService.activateContext("net.refractions.udig.contexts.selection");
    }

Related Information:

* :doc:`Contexts`

   in the PDE programmers guide.

Creating a Context using the Extension Point:

::

    <extension
          point="org.eclipse.ui.contexts">
       <context
             description="To allow interaction with layer selection"
             id="net.refractions.udig.contexts.selection"
             name="Layer Selection"
             parentId="org.eclipse.ui.contexts.window">
       </context>
    </extension>

Creating a Context using Java:

::

    Context layerSelection = contextService
        .getContext("net.refractions.udig.contexts.selection");
    if (!layerSelection .isDefined()) {
      tacos.define("Layer Selection", "To allow interaction with layer selection",
          "org.eclipse.ui.contexts.window");
    }

Menus
=====

Menus are where the rubber meets the road; commands and controls can be dropped into the correct
part of the application using a range of targets.

The locationURI targets are similar to the old menus path stuff - but are far more capable:

-  menu:org.eclipse.ui.main.menu?after=additions
    Drop a menu into the usual spot on the main menubar
-  toolbar:net.refractions.udig.views.catalog?after=additions
    Add actions to the catalog toolbar
-  etc...

You can do the same trick with expressions (ie using **visibleWhen**) to make your application
change based on what is going on.

To reproduce the usual visibility based on ActionSet story; your visibileWhen expression will need
to check the global action sets list. As action sets are enabled/disabled during Perspective changes
your menus will follow suite.

Menu Using Extension Point:

You can contribute menu contributions using an extension point; the locationURI can refer to the top
level menu bar:

::

    <extension
             point="org.eclipse.ui.menus">
          <menuContribution
                locationURI="menu:org.eclipse.ui.main.menu?after=additions">
             <menu
                   id="net.refractions.udig.menu.navigate"
                   label="Navigate"
                   mnemonic="N"
                   tooltip="Navigation Commands">
                <command
                      commandId="net.refractions.udig.show">            
                <visibleWhen>
                   <with
                         variable="activeContexts">
                      <iterate
                            operator="or">
                         <equals
                               value="net.refractions.udig.contexts.selection">
                         </equals>
                      </iterate>
                   </with>
                </visibleWhen>
                </command>
             </menu>
          </menuContribution>

Or the a view toolbar (example shows the catalog view):

-  menu:net.refractions.udig.catalog.ui.CatalogView?after=additions

Or it can refer to the view context toolbar (example shows the layers view):

-  toolbar:net.refractions.udig.project.ui.layerManager?after=additions

Contributing to pop up menus are a bit more tricky; because you really want to only contribute when
the user has selected something you are interested in (example will work for any context menu where
the selection can adapt to an IMap):

::

    <menuContribution
                locationURI="popup:org.eclipse.ui.popup.any?after=additions">
             <command
                   commandId="net.refractions.udig.project.ui.command.new.layer">
                <visibleWhen>
                   <with
                         variable="activeMenuSelection">
                      <iterate>
                         <adapt
                               type="net.refractions.udig.project.IMap">
                         </adapt>
                      </iterate>
                   </with>
                </visibleWhen>
             </command>
          </menuContribution>

You can also contribute to a specific popup:

-  popup:net.refractions.udig.project.ui.layerManager?after=additions

There is also the org.eclipse.ui.popupMenus extension point using Actions:

::

    <extension
             point="org.eclipse.ui.popupMenus">
         <objectContribution
                adaptable="true"
                objectClass="net.refractions.udig.project.ILayer"
                id="net.refractions.udig.project.ui.LayerContribution">
             <action
                   label="%zoomToLayer.label"
                   icon="icons/elcl16/zoom_layer_co.gif"
                   tooltip="%zoomToLayer.tooltip"
                   class="net.refractions.udig.project.ui.internal.actions.ZoomToLayer"
                   style="push"
                   id="net.refractions.udig.project.ui.zoomTo"/>
             ...more then one action can be contributed...
          </objectContribution>
      </extension>

