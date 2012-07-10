06 Tools
========

Tools must extend the **net.refractions.udig.project.ui.tool** extension point. The reference
section provides a list of the extension points and technical documentation for the extension
points.

:doc:`Tool Extension Point and API`


:doc:`Background Tool Extension`


:doc:`Action Tool Extension`


:doc:`Modal Tool Extension`


* :doc:`Preference Page and Tool Options`


:doc:`Tool Categories Extension`


* :doc:`Selection Providers`

* :doc:`ActionSet convention for Tool Category`


:doc:`Tool Cursor Extension`


:doc:`Tool Implementation and Framework`


:doc:`ToolManager`


:doc:`ToolContext`


:doc:`Tool Implementation`


* :doc:`Tool Lifecycle`

* :doc:`Tool Activation`

* :doc:`Tool Enablement`

* :doc:`Tool Lifecycle Listeners`


:doc:`Tool Cursor Implementation`


* :doc:`Using Default System Cursors`

* :doc:`Finding a Cursor at Runtime`

* :doc:`Compatibility`


:doc:`Future Direction`


Tool Extension Point and API
============================

Tools are used to capture user interaction with the Map Editor. Tools have access to a range of
information (via a ToolContext) and can issue commands to update the editor.

.. figure:: /images/06_tools/tool.GIF
   :align: center
   :alt: 

Background Tool Extension
-------------------------

A background tool is always active in the background watching what the user is doing. When used in
this fashion a tool would be limited to providing user feedback.

Example:

-  Cursor position: Shows the current position of the mouse cursor transformed into world
   coordinates.
-  Quick Zoom (Wheel Zoom): Zooms in and out of the map when the mouse wheel is turned or when

Extension Point Example:

::

    <extension point="net.refractions.udig.project.ui.tool">
        <backgroundTool
            name="%cursorPosition.name"
            class="net.refractions.udig.tools.internal.CursorPosition"
            id="net.refractions.udig.tools.backgroundTool1">
        </backgroundTool>
        ...
    </extension>

Action Tool Extension
~~~~~~~~~~~~~~~~~~~~~

A single fire tool that has a run command that is executed when the tool is activated. An action
tool does not change the mouse cursor because it is not modal. If a tool is needed that fires when
clicked within the editor, a modal tool would be a better choice.

Example:

-  Zoom to Extent: A tools that makes the map editor adjust its zoom so that the entire map is
   framed in the editor.

Extension Point example:

::

    <extension point="net.refractions.udig.project.ui.tool">
         <extension
             point="net.refractions.udig.project.ui.tool">
             <actionTool
                   categoryId="net.refractions.udig.tool.category.render"
                   class="net.refractions.udig.tools.internal.RefreshTool"
                   commandIds="net.refractions.udig.tools.refreshCommand"
                   icon="icons/etool16/refresh_co.gif"
                   id="net.refractions.udig.tools.refresh"
                   menuPath="file/refresh"
                   name="%refresh.name"
                   onToolbar="true"
                   tooltip="%refresh.tooltip">
             </actionTool> 
        ...
    </extension>

Modal Tool Extension
~~~~~~~~~~~~~~~~~~~~

A tool that has both **on** and **off** mode. Within UDIG there can only be one active modal tool. A
modal tool does not have a run method, instead is expected to listen to mouse events. The event
methods that are currently available are provided in the **AbstractTool** class with empty
implementations. When active, the cursor defined in extension definition is used as the mouse
cursor.

Example:

-  Zoom: A tool that allows the zoom towards a location in the map so that the features appear
   larger.
-  Pan: A tool that allows the user to adjust which part of the map they are viewing; the zoom stays
   the same.

Extension point example:

::

    <extension point="net.refractions.udig.project.ui.tool">
             <modalTool
                   categoryId="net.refractions.udig.tool.category.zoom"
                   class="net.refractions.udig.tools.internal.Zoom"
                   cursor="crosshair"
                   icon="icons/etool16/zoom_mode.gif"
                   id="net.refractions.udig.tools.Zoom"
                   name="%zoom.tool.name"
                   onToolbar="true"
                   toolCursorId="zoomCursor"
                   tooltip="%zoom.tool.tooltip">
             </modalTool>
        ...
    </extension>

Preference Page and Tool Options
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

You can fill in a \*\ **preferencePageId**\ \* to associate with your Tool; this will be available
when the user clicks on the tool icon in the tool option area of the map status line. The default
functionality is to open the normal Tool Preference page.

1. Start out with a a normal Preference Page.

::

    <extension
                point="org.eclipse.ui.preferencePages">
             <page
                   category="net.refractions.udig.project.ui.preferences.tool"
                   class="net.refractions.udig.tools.internal.NavigationToolPreferencePage"
                   id="net.refractions.udig.tool.default.navPage"
                   name="%navPage.name">
             </page>
          </extension>

With the details looking something like this (note we made a static final constant here):

::

    public class NavigationToolPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
        public static final String SCALE = "scale"; //$NON-NLS-1$
        public static final String TILED = "titled"; //$NON-NLS-1$

        private BooleanFieldEditor scale;
        private BooleanFieldEditor tiled;
        
        public NavigationToolPreferencePage() {
            super(GRID);
            IPreferenceStore store = ToolsPlugin.getDefault().getPreferenceStore();
            setPreferenceStore(store);
            setTitle(Messages.Navigation_Title);
            setDescription(Messages.Navigation_Description);
        }
        protected void createFieldEditors() {
            scale = new BooleanFieldEditor(SCALE, Messages.Navigation_Scale, getFieldEditorParent());
            addField(scale);
            tiled = new BooleanFieldEditor(TILED, Messages.Navigation_Tiled, getFieldEditorParent());
            addField(tiled);
        }
        public void init( IWorkbench workbench ) {
        }
    }

2. Add defaults so the preference page can start out with some good values.

::

    <extension point="org.eclipse.equinox.preferences.preferences">
      <initializer class="net.refractions.udig.tools.internal.NavigationPreferenceInitializer">
      </initializer>
    </extension>

With the class filling in a few default values:

::

    public class NavigationPreferenceInitializer extends AbstractPreferenceInitializer {
        public void initializeDefaultPreferences() {
            Preferences node = DefaultScope.INSTANCE.getNode(ToolsPlugin.ID);
            node.putBoolean(NavigationToolPreferencePage.SCALE,false);
            node.putBoolean(NavigationToolPreferencePage.TILED,false);
        }
    }

3. We can then link to that preference page from our ModalTool definition.

::

    <modalTool
                   categoryId="net.refractions.udig.tool.category.pan"
                   class="net.refractions.udig.tools.internal.PanTool"
                   commandHandler="net.refractions.udig.tools.internal.PanHandler"
                   commandIds="net.refractions.udig.tools.panRightCommand,net.refractions.udig.tools.panLeftCommand,net.refractions.udig.tools.panUpCommand,net.refractions.udig.tools.panDownCommand"
                   icon="icons/etool16/pan_mode.gif"
                   id="net.refractions.udig.tools.Pan"
                   name="%pan.tool.name"
                   onToolbar="true"
                   preferencePageId="net.refractions.udig.tool.default.navPage"
                   toolCursorId="move"
                   tooltip="%pan.tool.tooltip">
                <toolOption
                      class="net.refractions.udig.tools.internal.OptionContribtionItem"
                      id="panOptions">
                </toolOption>
             </modalTool>

You can check the preference settings in your tool (be sure to listen for changes!):

::

    IPropertyChangeListener prefListener = new IPropertyChangeListener(){
            @Override
            public void propertyChange( PropertyChangeEvent event ) {
                String property = event.getProperty();
                if( NavigationToolPreferencePage.SCALE.equals( property ) ||
                        NavigationToolPreferencePage.TILED.equals( property ) ){
                    syncPreference();
                }
            }
        };
        public PanTool() {
            super(MOUSE | MOTION);
            IPreferenceStore preferenceStore = ToolsPlugin.getDefault().getPreferenceStore();
            preferenceStore.addPropertyChangeListener(prefListener);
            syncPreference();
        }
        public void syncPreference(){
            IPreferenceStore preferenceStore = ToolsPlugin.getDefault().getPreferenceStore();
            boolean scale = preferenceStore.getBoolean(NavigationToolPreferencePage.SCALE);
            boolean tiled = preferenceStore.getBoolean(NavigationToolPreferencePage.TILED);
            ...
        }

4. Finally we can a ContributionItem elements (or several!) to the tool option area by filling in
the \*\ **toolOptionContribution**\ \*:

::

    <toolOption
                      class="net.refractions.udig.tools.internal.OptionContribtionItem"
                      id="panOptions">
                </toolOption>

We ask that the tool options act as a short cut to the settings available on the preference page (as
the tool option area may not always be available when the Map is Displayed in a View).

::

    public class OptionContribtionItem extends ToolOptionContributionItem {
            public IPreferenceStore fillFields( Composite parent ) {
                Button check = new Button(parent,  SWT.CHECK );
                check.setText("Scale");
                addField( NavigationToolPreferencePage.SCALE, check );
             
                Button tiled = new Button(parent,  SWT.CHECK );
                tiled.setText("Tiled");
                addField( NavigationToolPreferencePage.TILED, tiled );
                
                return ToolsPlugin.getDefault().getPreferenceStore();
            }
        };

The base class \*\ **ToolOptionContributionItem**\ \* does a lot of work behind the scenes for any
\*\ **Control**\ \* you call \*\ **addField**\ \* on. It will both listen to preference changes and
fill in the values; and also listen to the control and set the preference as needed.

You can take more control of this in your own classes:

-  listen( boolean listen ) - used to add/remove listeners from a control; your listener should
   update the preferenceStore
-  update( IPreferenceStore preferenceStore ) - used to update your control to match the
   preferenceStore
-  dispose() - clean up after your own controls

Tool Categories Extension
-------------------------

A **Category** represents a collection of tools that are always available but are logically similar
and are as a result grouped together.

Each category can have a key assigned to it which has two functions:

-  Active the current tool in the category, if not already active.
-  If the category is active then the next tool in the category will become active.

Tool extenders can also register a list of commands with the framework via the extension point
definition. If this is done the Tool extender must also create a **IHandler** object (part of the
eclipse command framework). An instance of the handler will be created for each command and each
time a command occurs it will be passed to the handler to be handled.

Extension point example:

::

    <extension point="net.refractions.udig.project.ui.tool">
          <category
                commandId="net.refractions.udig.tools.infoCommand"
                id="net.refractions.udig.tool.category.info"
                name="%info.tools.name"/>
        ...
    </extension>

Selection Providers
~~~~~~~~~~~~~~~~~~~

A category can also have a SelectionProvider implementation associated with it; this selection
provider is used as the Workbench selection whenever any of these tools are used on the Map.

::

    <category
                id="com.company.project.tool.selection"
                selectionProvider="com.company.project.tool.internal.MySelectionProvider">
          </category>

This "default" SelectionProvider will be provided to tool implementations via a
setIMapSelectionProviderMethod; any tool that is implementing its own getSelectionProvider method
will be "overriding" the default SelectionProvider defined by the tool category.

Selection Providers should return the kind of content the tool is operating on; and should also
adapt to the IMap or ILayer if appropriate. Selection providers may wish to watch the Map; and the
current layer (if you need an example look at FilterSelectionProvider):

::

    package com.company.project.tool.internal;

    public class MySelectionProvider extends AbstractMapEditorSelectionProvider
            implements IMapEditorSelectionProvider {
        
        /* The current Map */
        private IMap map;

        /**
         * Listen to the EditManager and watch the selected layer change.
         */
        private IEditManagerListener editManagerListener = new IEditManagerListener() {
            public void changed(EditManagerEvent event) {
                if (event.getSource().getMap() != map) {
                    event.getSource().removeListener(this);
                    return;
                }
                if (event.getType() == EditManagerEvent.SELECTED_LAYER) {
                    ILayer oldLayer = (ILayer) event.getOldValue();
                    ILayer selectedLayer = (ILayer) event.getNewValue();
                    if (selectedLayer != null) {
                        updateSelectionBasedOnThisLayer(selectedLayer);
                    }               
                }
            }
        };

        public void setActiveMap(IMap map, MapPart editor) {
            this.map = map;
            if (map == null || map.getMapLayers().size() == 0) {
                updateSelectionBasedOnThisLayer(null);
            } else {
                ILayer selectedLayer = map.getEditManager().getSelectedLayer();
                if (selectedLayer != null) {
                    updateSelectionBasedOnThisLayer(selectedLayer);
                }
            }

            if (!map.getEditManager().containsListener(editManagerListener)){
                map.getEditManager().addListener(editManagerListener);
            }
        }
        
        public void updateSelectionBasedOnThisLayer( ILayer layer ){
            if( layer == null ){
                selection = new StructuredSelection();
                notifyListeners();
                return;
            }
            List<String> names =
                (List<String>) layer.getBlackboard().get("names");
                    
            if( pointIds == null ){
                selection = new StructuredSelection();
                notifyListeners();          
                return;
            }
            SelectionList<String> list = new SelectionList<String>();
            list.addAll( names );
            list.addAdapter( layer );
            list.addAdapter( layer.getMap() );
            
            selection = new StructuredSelection( list );      
            notifyListeners();
        }
    }

Where SelectionList above is something along these lines:

::

    public class SelectionList<T> extends ArrayList<T> implements IAdaptable {
        private static final long serialVersionUID = 3521446731606642486L;

        /**
         * Set of adapters (ie other objects or interfaces) we
         * are returning at the same time.
         */
        protected Set<Object> adapters = new CopyOnWriteArraySet<Object>();
        
        /**
         * Called by client code to return additional interfaces
         * as part of this SelectionList.
         * <p>
         * Example: selectionList.add( currentLayer )
         * 
         * @param adapter The adapter we are interested in communicating to others
         */
        public void addAdapter( Object adapter ) {
            if( adapter==null ){
                throw new NullPointerException("adapter cannont be null"); //$NON-NLS-1$
            }
            adapters.add(adapter);
        }
        @SuppressWarnings("unchecked")
        public Object getAdapter( Class adapter ) {
            if( adapter.isInstance(this)){
                return adapter.cast(this);
            }
            for( Object obj : adapters ) {
                if( adapter.isAssignableFrom(obj.getClass()) ){
                    return obj;
                }
            }
            return null;
        }
    }

ActionSet convention for Tool Category
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

We will check for an ActionSet with the same name as the ToolCategory - you can use this facility to
turn off actions that don't make sense for your perspective.

Tool Cursor Extension
---------------------

Cursors can be defined independently from tools; allowing you to reuse the same cursor for several
tools.

Extension example:

::

    <extension point="net.refractions.udig.project.ui.tool">
        <toolCursor
            hotspotX="10"
            hotspotY="10"
            id="arrowCursor"
            image="icons/pointers/edit_source.gif"/>
        ...
    </extension>

Where:

-  *id* is an unique ID of the cursor to be accessed from any place of UDIG platform to get cursor
   image.
-  *image* path to cursor image inside of plugin
-  *hotspotX, hotspotY* coordinates from top left corner of hot point for the cursor.

Once the tool cursor is defined as an extension it is accessible as a default tool cursor by
**toolCursorId** attribute of a modal tool element. For this to work the ID must be unqiue –
allowing a cursor defined in one plug-in to used by the tool from another plug-in just by ID.

Tool Implementation and Framework
=================================

ToolManager
-----------

The **ToolManager** is the mediator responsible for handling everything to do with tools on behalf
of a map editor or map view.

With this in mind the ToolManager:

-  Is responsible for the current "mode" of the Map; which it represents as the current ModalTool
-  Processes the Tool Extension Point and allows "easy" access to find tools
-  it has methods to add the tools to the tool bar (or menu bar)
-  it provides a palette model listing all tools (for use by the palette view)

To add tool buttons to custom views the ToolManager.createToolAction(ToolID, CategoryID) method will
create an **Action** that can be added to the view.

The tool implementations you provide are wrapped up in a **ToolProxy** (which contains their icon,
name, description and so on). You can look this up at runtime:

::

    ToolManager tools = ApplicationGIS.getToolManager();
    ToolProxy tool = tools.findToolProxy( id );

ToolContext
-----------

All Tools are provided with a ToolContext object by the framework. The tools can use the context to
access the model and to create and send commands which modify the model. Contexts have a large
number of methods to simplify the job of tool authors. Please let us know of methods that would be
useful or should be part of the context objects.

**IMPORTANT**: It is critical that the tools do not make a new reference to the context object
because it is set each time the editor is activated and may change without notification.

Tool Implementation
-------------------

There are several abstract classes available for you to extend.

.. figure:: /images/06_tools/toolframework.GIF
   :align: center
   :alt: 

There are several available subclasses to start you out:

-  AbstractActionTool
-  AbstractTool
-  AbstractModelTool
-  SimpleTool: an implementation of ModalTool with support for a right-click context menu, it serves
   as a great starting place for creating your own tool that involves "selecting a location" or
   "selecting content" on the map and making a range of actions available.

Note when using **AbstractTool** you can use the constructor to define what sort of events you are
interested in, the events come in already expressed in Map coordinates.

::

    class ExampleTool AbstractTool(){
        ExampleTool(){
           super( MOUSE | WHEEL );
        }
        public void mouseReleased( MapMouseEvent e ) {
           ...
        }
        public void mouseWheelMoved( MapMouseWheelEvent e ) {
           ...
        } 
    }

Tool Lifecycle
~~~~~~~~~~~~~~

Tools go through a fixed lifecycle:

-  Extension XML definition processed, the result is held by a ToolProxy until actually needed
-  Default "no argument" constructor is called
-  AbstractTool.init(IConfigurationElement) is called allows the tool to configure itself based on
   the XML definition
-  Tool.setContext called each time an Editor is activated
-  Tool.addListener / removeListener called as needed
-  Tool.setEnabled is called as needed
-  ModalTool.setActive is called as needed
-  Tool.dispose()

Tool Activation
~~~~~~~~~~~~~~~

Tool activation is a life cycle step reserved for ModalTools; when active a modal tool will control
what the Map Editor is doing - the tools cursor will be displayed, its selection will be treated as
the MapEditor selection as far as the work bench is concerned.

::

    ModalTool.boolean isActive()
    ModalTool.setActive( boolean )
    ModalTool.getCursorID()
    ModalTool.setCursorID( String )
    ModalTool.getSelectionProvider()
    ModalTool.setSelectionProvider(IMapEditorSelectionProvider)

Only one modal tool can be active. There is no other opportunity to activate another disabled tool
through UI contributions. When the tool is disabled, its UI contributions are disabled.

When the active tool is being disabled, its functionality is blocked but the tool is still active.
The tool can be enabled by changing of context again. In that case only user manually can switch to
any other enabled tool through UI contributions.

Tool Enablement
~~~~~~~~~~~~~~~

The tool interface has two methods to track isEnabled:

::

    Tool.setEnabled(Boolean)
    Tool.isEnabled()

This lets to enable/disable a tools functionality at any time during tool life cycle. When the tool
is disabled, the cursor for the MapEditor is changed initialized and the functionality is blocked by
unregistering mouse listeners.

There are several ways to perform tool enablement. First way is to let the system performs
enablement on the base of current context (selecting different layers, etc.). The second wayis to
manually calling *Tool.setEnabled(Boolean)* from any place of tool implementation to simply block
its functionality.

Tool Lifecycle Listeners
~~~~~~~~~~~~~~~~~~~~~~~~

I started tool lifecycle listeners: the initial three events are:

-  tool activation (only one modal item can be active at any time).
-  tool enablement (can be performed at any time)
-  context changing (during changing of context the tool also can be enabled or disabled
   automatically)

Tool lifecycle listeners are not used anywhere at the current moment, but it would be good to have
such functionality to listen tools lifecycle events without overriding of Tool class methods.

Tool Cursor Implementation
--------------------------

Cursor is a disposable object and to implement lazy loading the proxy object is used in the same
manner as *ToolProxy* object before:

::

    net.refractions.udig.project.ui.internal.tool.display.CursorProxy

ToolManager is responsible to create full list of cursor proxies and cache them by ID from extension
point. Whenever the actual *org.eclipse.swt.graphics.Cursor* object is needed you must call the
following method:

::

    Cursor IToolManager.findToolCursor(String cursorID);

In most cases the developer does not need the org.eclipse.swt.graphics.Cursor object while working
with tools implementation. The Tools API is extended by the next methods to manage tool cursors:

::

    ModalTool.setCursorID(String cursorID)
    String ModalTool.getCursorID()

These methods are responsible for the cursors management. The set method performs actual updating of
mouse cursor image if it is needed.

Using Default System Cursors
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Systems cursors are listed using constants in SWT class. Constants are integer numbers. While
current Tool Cursors Framework uses string IDs it is recommended to work with mapped constants from
ModalTool interface. These constants are mapped to system cursors. If the system cursor
*SWT.CURSOR\_WAIT* is needed then call routine:

::

    ModalTool.setCursorID(ModalTool.WAIT_CURSOR);

In this case the framework recognizes that the system cursor is requested and sets it for the tool.
You can combine as custom cursors as system using the underlying mechanism transparently.

Finding a Cursor at Runtime
~~~~~~~~~~~~~~~~~~~~~~~~~~~

The developer can declaratively add cursor images through extension mechanism and use them by ID
from any place in source code. If the SWT object is needed call:

::

    IToolManager.findToolCursor(String cursorID).

If you just want to set cursor for the tool just call

::

    ModalTool.setCursorID(String cursorID).

Updating of mouse cursor image is performed automatically by the framework depending on the current
context, active tool, etc.

Compatibility
~~~~~~~~~~~~~

It is possible to support compatibility with cursor extension point under tool extension point as a
default cursor for the tool.

Future Direction
================

Currently all tools are active but in the future would be desirable to have a tool configuration
extension point where udig extenders can define which tools are activated for their application. A
system like the eclipse command framework for Eclipse 3.1 is likely.

This work has been started already by making use of ActionSets and Tool Categories.
