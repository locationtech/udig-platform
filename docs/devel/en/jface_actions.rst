JFace Actions
=============

The default wizard provided by eclipse will let you add to the top-level toolbar or menu system
using the concept of Actions and ActionSets.

Action
------

You can use action directly - this is an acceptable approach if you are going to use the action
within your plugin.

.. code-block:: java

    newAction = new Action("New...") {
        public void run() {
            registerDatasource();
        }
    };
    newAction.setImageDescriptor(getImageDescriptor("new.gif"));

Example use with a toolbar:

.. code-block:: java

    IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
    mgr.add( newAction );

ActionSet
---------

You can contributing actions via action sets. An action set is a mechanism that allows a plug-in to
contribute menus, menu items, and toolbar items to the main menu bar and toolbar of the Workbench
window. It is important to understand what action sets are meant to be used for. An action set
should contribute common actions which are not specific to any particular view or editor. The class defined
in the plugin ActionSets extension point must implement **org.eclipse.ui.IWorkbenchWindowActionDelegate**
interface if the pulldown attribute is false, or **org.eclipse.ui.IWorkbenchWindowPulldownDelegate**
interface if the pulldown attribute is true. This delegate class can control the enabled/disabled state
of the action when the selection changes, but only once the plug-in is loaded. Until then, the
enabled/disabled state of the action is controlled by other XML attributes like enablesFor and selection.

Workbench and Actions
---------------------

Uses IWorkbenchWindowActionDelegate ...

.. code-block:: java

    public class EditStyleAction implements IWorkbenchWindowActionDelegate {

        public static final String ID = "org.locationtech.udig.style.openStyleEditorAction"; //$NON-NLS-1$

        private Layer selectedLayer;

        public void dispose() {
        }

        public void init( IWorkbenchWindow window ) {

        }

        public void run( IAction action ) {
            Display.getDefault().asyncExec(new Runnable(){
                public void run() {
                    StyleView styleView = null;
                    try {
                        IWorkbenchPage page  = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                        page.showView( StyleView.VIEW_ID );

                        styleView = (StyleView)
                        if (selectedLayer != null);
                            styleView.setSelectedLayer(selectedLayer);
                    }
                    catch (PartInitException e2) {
                        e2.printStackTrace();
                    }
                }
            });
        }

        public void selectionChanged( IAction action, ISelection selection ) {
            if (selection.isEmpty() || !(selection instanceof IStructuredSelection))
                return;

            StructuredSelection sselection = (StructuredSelection)selection;
            if (sselection.getFirstElement() instanceof Layer) {
                selectedLayer = (Layer)sselection.getFirstElement();
            }
        }
    }


View and Actions
----------------

Uses IViewActionDelegate ...
The interface IViewActionDelegate allows the action delegate, during initialization, to target
itself with the view instance it should work with.
The action delegate must implement the method setActiveEditor(IAction action, IEditorPart
targetEditor).
Action enablement is declared in XML because plug-ins are loaded lazily. Until an action is
actually invoked by the user, the plug-in is not loaded and the Workbench uses the enablement logic
declared in XML. Once a plug-in is loaded, the delegate class is notified of selection changes and
can update the enabled/disabled state of the action. Refer to the
org.eclipse.ui.IActionDelegate.selectionChanged(IAction action, ISelection selection) method
documentation for more details.

View Context Menu
-----------------

The class defined for the action must implement org.eclipse.ui.IViewActionDelegate interface if
contributing to a view's context menu.

EnablesFor attribute control the enabled/disabled state of the action based on the current
selection. Its value is the selection count condition which must be met to enable the action. If not
the action is disabled. If attribute is skipped default is enable for any number of items selected.

.. list-table::
   :widths: 50 50
   :header-rows: 1

   * - Formats
     - Description
   * - !
     - 0 items selected
   * - ?
     - 0 or 1 items selected
   * - \+
     - 1 or more items selected
   * - multiple, 2+
     - 2 or more items selected
   * - n
     - a precise number of items selected (e.g. 4)
   * - \-
     - any number of items selected


Here is an example used in udig with the zoom to layer action in the Layers View.
Create an object contribution to ILayer class and add the zoom to layer action to your plugin.

.. code-block:: xml

   <objectContribution
             adaptable="false"
             objectClass="org.locationtech.udig.project.ILayer"
             id="org.locationtech.udig.project.ui.LayerContribution">
      <action
           label="%zoomToLayer.label"
           icon="icons/elcl16/zoom_layer_co.gif"
           tooltip="%zoomToLayer.tooltip"
           class="org.locationtech.udig.project.ui.internal.actions.ZoomToLayer"
           style="push"
           id="org.locationtech.udig.project.ui.zoomTo"/>
   </objectContribution>


Then create the ZoomToLayer class that implements IViewActionDelegate

.. code-block:: java

    public class ZoomToLayer extends ActionDelegate implements IViewActionDelegate {

        IStructuredSelection selection;

        public void selectionChanged( IAction action, ISelection selection ) {
           try{
              this.selection=(IStructuredSelection) selection;
           }catch (Exception e) {
              //do nothing
           }
        }

        public void runWithEvent( IAction action, Event event ) {
           try {
              PlatformUI.getWorkbench().getActiveWorkbenchWindow().run(false, true,
                     new IRunnableWithProgress() {

                        public void run(IProgressMonitor monitor) {
                           Envelope bounds = new Envelope();
                           bounds.setToNull();
                           Map map=((Layer)selection.getFirstElement()).getContextModel().getMap();
                           for( Iterator iter = (selection).iterator(); iter.hasNext(); ) {
                              Layer layer = (Layer) iter.next();
                              if( layer.getContextModel().getMap()!=map )
                                 return;

                              Envelope bbox=null;
                              try {
                                 bbox = layer.getBounds(monitor, map.getViewportModel().getCRS());
                              } catch (IOException e) {
                              }

                              if( bbox==null)
                                 continue;

                              if( bounds.isNull() )
                                 bounds.init(bbox);
                              else
                                 bounds.expandToInclude( bbox );

                              if( !bounds.isNull() ) {
                                 map.sendCommandASync(
                                     NavigationCommandFactory.getInstance().createSetViewportBBoxCommand(bounds));
                              }
                           }
                       }
                   });
            } catch (Exception e) {
                CorePlugin.log(ProjectUIPlugin.getDefault(), e);
            }
        }

Editor and Actions
------------------

Uses IEditorActionDelegate ...
must implement org.eclipse.ui.IEditorActionDelegate interface if contributing to an editor's
context menu.
The interface IEditorActionDelegate allows the action delegate to retarget itself to the active
editor.
The action delegate must implement the method setActiveEditor(IAction action, IEditorPart
targetEditor).

.. code-block:: java

    public class CommitAction implements IEditorActionDelegate {

        public void run(IAction action) {

            MapPart mapPart = ApplicationGISInternal.getActiveMapPart();

            try {
                mapPart.getMap().getEditManagerInternal().commitTransaction();
            } catch (IOException e) {
                // Shouldn't happen but...
                ProjectUIPlugin.getDefault().getLog().log(new Status(IStatus.ERROR,
                        "org.locationtech.udig.project",0,"Error commiting transaction",e)); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        public void selectionChanged(IAction action, ISelection selection) {

        }
    }

Object and Actions
------------------

Uses IObjectActionDelegate ...
For object contributions, the class attribute of the action element is the name of a Java class
that implements the org.eclipse.ui.IObjectActionDelegate interface. The interface
IObjectActionDelegate allows the action delegate to retarget itself to the active part.
The action delegate must implement the method

.. code-block:: java

   setActivePart(IAction action, IWorkbenchPart targetPart)


Q: Where are the Actions?

Proxy Pattern and Actions: In the quest for lazy loading of plug-ins the RCP makes use of the proxy
pattern to delay the loading of Actions. For most instances a particular extention point will create
a proxy based on configuration information; and it will be this proxy that turns around and
creates/calls your class.

Related reference
-----------------

* `Creating an Eclipse View <http://www.eclipse.org/articles/viewArticle/ViewArticle2.html>`_
* `Contributing Actions to the Eclipse workbench <http://www.eclipse.org/articles/Article-action-contribution/Contributing%20Actions%20to%20the%20Eclipse%20Workbench.html>`_

