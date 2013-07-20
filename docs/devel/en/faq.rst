FAQ
===

Frequently asked questions on uDig Development.

Community Questions
^^^^^^^^^^^^^^^^^^^

Getting Started
'''''''''''''''

Q: Where to start with eclipse RCP development\*
                                                

Few links for starting with eclipse RCP are provided below:

* `<http://www.eclipse.org/rcp/>`_
* `<http://www.eclipse.org/community/rcp.html>`_
*  Our own :doc:`getting_started` section has a\ :doc:`welcome_new_eclipse_rcp_developers` page

You should also check out the Eclipse RCP forums (**eclipse.platform.rcp** on news.eclipse.org).

Q: Where to Start with uDig development\*
                                         

    I am new in uDig framework and in GIS programming in particular.
    However, i want to make uDig work with a health database, in mysql.
    attached please find my plugin specification.

    basically I want to add a function which connect to a database,
    search an entry on the database, seach the same entry on shape file,
    then highlith the image on the map.

    This is in real life application. Please help me how to go about.

Sounds like fun!

Well we are happy to help with facilities over here, with have a github community repository.
Please sign up to the developers email list and work your way through the initial quickstart and
plug-in tutorial.

Steps:

#. Sign up: `<https://www.locationtech.org/mailman/listinfo/udig-dev>`_
#. Install: :doc:`SDKQuickstart/SDKQuickstart` for simple plugins (or `Home <http://udig.refractions.net/confluence//display/ADMIN/Home>`_ for core development)
#. Follow: :doc:`tool_plugin_tutorial`

From there on out you can start to get our own uDig/Eclipse RCP docs and pick up some Eclipse books
(for help in making a user interface and so on), and I assume you are familiar with the MySQL/JDBC
part of things?

#. Read: :doc:`Home <index>`
#. Read: :doc:`reading_list`

Our docs are on as an needed basis, so as you have questions we will due our best to make a page of
docs to answer email, and help people later. So you you are already on the right track, ask
questions!
 Email: `udig-dev@locationtech.org <mailto:udig-dev@locationtech.org>`_

Q: How to Make a View
                     

    I have managed to develop a plug-in and is linking with a uDig very well. Is it possible to make
    a 'view' to apear like a form, with textboxes and buttons.

    I need to create form which will allow users to enter/select values. For example, i put a link
    on the view and when a user doubleclick it a form is poping up.

    currently my form opens outside eclipse!

Please follow any Eclipse book; or tutorial on the web for instructions on how to make a view.

Here is the one I used when learning:

* `Creating an Eclipse View <http://www.eclipse.org/articles/viewArticle/ViewArticle2.html>`_

In general we are using the Eclipse RCP platform; any instructions or tutorials you find on the
subject will serve you well when working on uDig. While we have taken some notes on tips and tricks
we have found; your best resource is often the Eclipse Help menu (because it is up to date and
matches the version of eclipse you are working with).

Q: Which branch to choose
                         

Please join us in working on the uDig **master** branch for access to the latest developments, bug
fixes and GeoTools libraries.

The **1.2.x** branch is considered stable with no additional releases planned. The project is 
open if your team needs to make a release of 1.2.x we would be happy to assist any volunteers.

Developer Questions
^^^^^^^^^^^^^^^^^^^

Q: Where can I find uDig API javadocs
                                     

* The :doc:`reference` section has a :doc:`javadocs`
* `<http://udig.refractions.net/docs/api-udig>`_

Q: Where can I find uDig extension Point Documentation
                                                      

Extension point documentation is included as part of the SDK (or source code checkout)
 for direct access during development.

To access click on the **show extension point** links in the Eclipse MANIFEST.MF and plugin.xml
editors.

Development Questions
^^^^^^^^^^^^^^^^^^^^^

Q: How can I avoid startup dialogs when developing
                                                  

#. Before launching uDig from your eclipse workspace, modify the run configuration
#. On the **Arguments** tab, add "-DUDIG\_DEVELOPING" to your VM arguments.
#. Now when you clear your workspace and launch uDig, you won't have to close the tips dialog nor
   navigate from the intro screen to the workbench.

Q: How to copy an existing Plugin
                                 

#. File > Import
#. Select **Plugin Development** and **Plugins and Fragments** wizard, and press **Next**
#. On the right you can see EVERYTHING in the uDig SDK
#. Select a plugin that is similar to what you want to do:
   net.refractions.udig.catalog.world.image
#. Press **Add**
#. Press **Finish** to the plugin(s) into your workspace

From here you can hack away:

-  If you keep the plugin with the same name it will be used **INSTEAD** of the uDIG SDK entry
-  Or you can rename the plugin and use it as a good starting point for your own work

Eclipse RCP Questions
^^^^^^^^^^^^^^^^^^^^^

Q: How can I display my views by default
                                        

The views initially displayed are provided by a perspective:

-  Extend the current Map Perspective (that is an extension point); or
-  Create your own perspective

An example of extending a perspective is in the net.refractions.udig.feature.editor/plugin.xml file.

If you define a new perspective and you want it to be loaded by default you have to create a fragment 
for net.refractions.udig.ui and in the fragment override the UDIGWorkbenchAdvisor class to return 
your perspective ID in the getInitialWindowPerspectiveId method.

Examples:

* :doc:`creating_new_perspective_example`
* :doc:`extending_an_existing_perspective_example`

Q: How do I configure keyboard shortcuts
                                        

You need to create a schema, which assigns keyboard bindings to commands. This allows a key press to
invoke the same command that would otherwise be issued in response to an Action (such as a menu item or
toolbar button being pressed).

The actual functionally invoked by a Command can be determined dynamically using a command handler.
This is how the same keyboard short cuts (such as Control-C or Delete) can be performed differently
depending on the view the user is working in.

Examples: :doc:`keyboard_shortcut_example`


Q: How do I get an SWT layout to display properly
                                                 

The size of a control sometimes defaults to zero width and height – this drives us all mad.

The solution lies in the parent composite (rather than the control).

If the composite is using a layout manager to control the size of the controls you can call the
layout() method:

.. code-block:: java

    Composite parent = new Composite(grandParent, SWT.NONE);
    ...
    (create controls)
    ...
    parent.layout();

You should also review the layout manager configuration associated with each control.

If the composite is not using a layout manager you will need to do things by hand:

-  Resize the composite programatically with parent.setSize(...)

Q: How should I use a Progress Monitor
                                      

A few tips on using Progress Monitors:

Always start the progress monitor and do at least 1 bit of work. For example:

.. code-block:: java

    monitor.beginTask("Working", 4);
    monitor.work(1);

Always finish started job.

.. code-block:: java

    try{
      monitor.beginTask("Working", 4);
      monitor.work(1);
      // some work
    }finally{
      monitor.done();
    }

Make use of SubProgressMonitor if sending the monitor to another method:

.. code-block:: java

    try{
      monitor.beginTask("Working", 8);
      monitor.work(1);

      SubProgressMonitor sub=new SubProgressMonitor(monitor, 3);
      doSomeWork(sub);
      sub.done();  // don't forget to make sure the sub monitor is done

      sub=new SubProgressMonitor(monitor, 3);
      doSomeMoreWork(sub);
      sub.done();  // don't forget to make sure the sub monitor is done (callee might not use it)

    }finally{
      monitor.done();
    }

Q: How to wait in the display thread
                                    

All interactions with the user (or widgets) is funnelled through a single display thread. As such
you should not take a trip out to disk or do any serious computation from the display thread or the
application will appear to be frozen and unresponsive.

So how can you perform these tasks - and then update the user interface in response?

-  Recommended: Start a background Job; and when it finishes start a Runnable to update the user
   interface

-  Alternative: Use the display's read and dispatch method to run other jobs that are waiting for
   the display thread.
   Only when there are no more jobs waiting(readAndDispatch returns false when no more jobs are
   waiting) then let the thread sleep.

   .. code-block:: java

       while( condition ) {
           //run a display event continue if there is more work todo.
           if ( display.readAndDispatch() ){
               continue;
           }
                   
           //no more work to do in display thread, wait on request if request has not
           //finished
           if (condition)
               break ;

           Thread.sleep(300);
       }

Q: My plugins export but don't work in uDig
                                           

My plugins export but don't work in uDig. What is going on?

The common suspects are:

-  The plugin depends on another plugin that is not part of the uDig your plugin is installed in. If
   a plugin depends on a missing plugin then that plugin will be deactivated
-  Make sure that all the required resources are checked off in th build.properties editor. The
   build is the important one for running and exporting
-  If you made a plugin that contains code as well as other jars then on the runtime tab of the
   Manifest.MF editor you must make sure that the all the extra jars **and** a . are in the
   Classpath list. (The period is intensional as it indicates the code of this plugin).

   -  This last point is important only if the build.properties Runtime Information maps . to your
      source directory. If the mapping is to a jar then make sure that jar is in the classpath of
      the manifest.

-  The exported plugin and the current udig build were compiled with different jdk. -debug
   -consoleLog as program arguments help to keep track of such errors.

GIS Application Questions
^^^^^^^^^^^^^^^^^^^^^^^^^

Q: How can I find what maps and projects are currently available
                                                                

The **ApplicationGIS** class provides access to all the projects currently registered with 
the uDig instance.

**ApplicationGIS.getProjects()** will return all the projects currently registered with the system.

Given a project all the contained elements can be searched via the **getElements()** method. If a
single type of elements is desired such as maps the **getElements( Class<T> )** method can be used.

To access the first Map in the first project:

.. code-block:: java

    ApplicationGIS.openMap(ApplicationGIS.getProjects().get(0).getElements(IMap.class).get(0));

Q: How can I programmatically commit changes
                                            

There is a Commit Command that can be sent to the map to commit:

.. code-block:: java

    MapCommand commitCommand=EditCommandFactory.getInstance().createCommitCommand();
    map.sendCommandSync(commitCommand);

Q: How can I set up a custom splash screen
                                          

Follow the :doc:`Custom Application Tutorial <custom_application_tutorial>` for branding and
splash screen information.

Alternative: Make a fragment which overrides the provided intro/root.xml or the introContent.xml 
in the net.refractions.udig.ui plugin.

Q: How do I add a layer programmatically
                                        

The **ApplicationGIS** class has a utility method for you:

.. code-block:: java

    ApplicationGIS.addLayersToMap(map, layers, startposition, project);

You can also send the command directly yourself:

.. code-block:: java

    IMap map;
    map.sendCommandASync(new AddLayerCommand(layer) );

Q: How do I add default key bindings to my IWorkbenchPart
                                                         

Simply add this line to the createPartControl() method of your workbench part.

.. code-block:: java

    ApplicationGIS.getToolManager().registerActionsWithPart(this);

This will allow your view to forward common keybindings to the MapEditor so the user can
 change controls and pan around the Map using the keyboard.

Q: How do I create a map programmatically
                                         

There is a command to create a map, which you can send to the project:

.. code-block:: java

    CreateMapCommand command=new CreateMapCommand("MapName", listofGeoResources, project);
    project.sendSync(command);
    IMap map=command.getCreatedMap();

Q: How do I make a new type of layer
                                    

A layer in uDig is (very generally) a set of IGeoResources which are handles for the same dataset.

Example: A WFS and a WMS may be backed onto the same dataset, so the Layer should have access to a
WFSGeoResource and a WMSGeoResource

To define a new **type** of layer one must create a new IGeoResource (and IService since IServices
 contain the IGeoResources).

Example: A Web Terrain layer would require a Web Terrain IService and IGeoResource.

Once a new layer type is created it does not automatically get rendered unless one of its
 IGeoResources resolves to a DataStore, GridCoverage or a WebMapServer. If one of the IGeoResources
 does resolve to one of those objects then you are done and the layer will render. If not then
 a new Renderer must be developed.

For more information:

* :doc:`catalog`

* :doc:`renderers`


Q: How is udig rendering different from geotools rendering
                                                          

Our rendering challenge is a little bit different then the one that the geotools Streaming Renderer
 focuses on.

Often in a OGC Open Web Services workflow there is more then one way to do it. Both a Web Feature
 Server and a Web Map Server may provide the same information, or indeed be able to provide a
 picture for a layer in a map. To make matters interesting a Web Map Server may be able to draw
 more then one layer at the same time. Our api is trying to walk the line between capturing this
 complexity, and hiding it.

When we have figured out which workflow is going to be used for a layer we end up with
 the concept of a Renderer.

Each Layer, or a set of Layers, get a Renderer. Each Renderer produced a raster. Renderers are
 aranged into a stack, and enough event notification is used to so that everyone can play in
 their own thread.

Q: How the do Features and DataStores fit into the picture
                                                          

At the uDig level there are IServices and IGeoResources. These are generic handles to something
 "real."

Example: An IService can be a handle to a WMS or a Datastore. An IGeoResource can be a handle to a
"FeatureSource" or "GridCoverage".

A **Layer** references an IGeoResource... Actually, since two IGeoResources can refer to the same
data (for example a WMS Layer and a WFS FeatureCollection backed onto the same data) a layer can
reference one or more IGeoResources **BUT** only 1 data. A layer has functionality allowing
inspection of the georesources and also map specific information that is unrelated to the
IGeoResource, for example a map name and a style.

**Renderer** is responsible for drawing a layer. There are many different types of renderers. Some
renderers can render Vector data, others can render GridCoverages or make WMS requests. The
BasicFeatureRenderer, for example, only works for Layers that has an IGeoResource that is a handle
for a FeatureSource.

That is all at the uDig level. uDig uses the Geotools library extensively so most of the current
IServices and IGeoResources are handles for Geotools objects. Geotools has DataStores and
FeatureSources for Vector data. A DataStore is a peer of IService. An example of a Datastore is a
PostGIS database. FeatureSources can be obtained from DataStores and are a peer of IGeoResource.
FeatureSources can be used to obtain features of a particular feature type from a DataStore.
FeatureSources are read-only. If the Datastore is read-write you can do an instance check on the
FeatureSource to see if it is a FeatureStore. FeatureStores provide methods for
adding/removing/modifying features.

The next obvious question is: If I am in uDig how do I get a FeatureSource? Here are some common
scenarios that occur in uDig.

FeatureSource access from a layer:

.. code-block:: java

    layer.getResource( FeatureSource.class, monitor );

This is a blocking call you can do a non-blocking check to see if the layer has a FeatureSource by:

.. code-block:: java

    layer.hasResource( FeatureSource.class );

FeatureSource access from an IGeoResource:

.. code-block:: java

    if( resource.canResolve( FeatureSource.class ) )
        return resource.resolve( FeatureSource.class );

If it is possible, always obtain a FeatureSource from a layer. This is because uDig's EditManager
manages transactions for the user (and developer). FeatureSources by default use auto commit
transactions where the FeatureSource obtained from a layer uses a transaction.

Q: How to get geometries from layer
                                   

To get geometries from a layer you need to ask for a FeatureSource object from the layer:

.. code-block:: java

    FeatureSource source=layer.getResource( FeatureSource.class, progressMonitor);

Once you have a feature source you can get all the features from the source by:

.. code-block:: java

    FeatureCollection collection=source.getFeatures();

Q: How to tell when something changes
                                     

The uDig "application model" is maintained with EMF (the eclipse modeling framework). As such it is
very easy to morph uDig to meet your needs.

-  Adaptor: Generic EMF notifications are provided through use of an "Adaptor"

   .. code-block:: java

       layer.eAdapters().add( new AdapterImpl(){
          public void notifyChanged( Notificaiton msg ) {
              if( msg.getNotifier() instnaceof Layer ){
                  Layer layer = (Layer) msg.getNotifier();
                  switch( msg.getFeatureID(Layer.class) ) {
                  case ProjectPackage.LAYER__NAME:
                       System.out.println( layer.getName() +" renamed");
                       break;
                  case ProjectPackage.LAYER__GEO_RESOURCES:
                       System.out.println( "We have new data!");
                       FeatureType schema = layer.getSchema();
                       if( schema != null ){
                            System.out.println( "changed to "+schema.getTypeName() );
                       }
                       break;
                  }
              }
          }
       });

-  Listener: the project sets add/remove listener methods as they are requested on the udig-dev
   email list:

   .. code-block:: java

       layer.addListener( new ILayerListener(){
          public refresh( LayerEvent event ){
              if( event.getType() == LayerEvent.EventType.STYLE ){
                  ILayer layer = event.getSource();
                  IStyleBlackboard style = (IStyleBlackboard ) event.getNewValue();

                  // layer has changed style
              }
          }
       });

An adapter is a traditional pater when you want to use one data model and "morph" it to fit another
interface. One of the side effects of this is you need to pay attention to the origional data, and
pass any changes along.

You can see lots of examples of this idea in Java code. People setting up custom JTreeModels to
visualize an internal data structure etc...

Since this need happens **all** the time the EMF crew decided design for it in mind. It is a much
more difficult, and interesting, problem them simply listening for changes (Indeed it is a superset
of change notification - basically change notification with interface change). The benifit is that
you can "force" EMF models (and thus uDig) into about anything.

So the above example is "an adapter", and we are only paying attention to the changes.

Example: Here is how you can watch the "Viewport Model" (ie. Zoom, Pan, CRS):

.. code-block:: java

    map.getViewportModel().addViewportModelListener(new IViewportModelListener()){
      public void changed(ViewportModel event){
        if( event.getType()==EventType.CRS }
          // crs has changed do something
        else if( event.getType()==EventType.BOUNDS ){
          // bounds have changed do something else
        }
      }
    }

Example: Layer(s) added/removed

-  IMapCompositionListener\* tells you when layers were added to the map, were deleted from the map
   or when the layer list order has changed. For more information take a look at
   **MapCompositionEvent.EventType**:

   .. code-block:: java

       ApplicationGIS.getActiveMap().addMapCompositionListener(new IMapCompositionListener(){
           public void changed( MapCompositionEvent event ) {
               if (event.getType() == EventType.ADDED) {
                   System.out.println("Layer added");
               } else if (event.getType() == EventType.REMOVED) {
                   System.out.println("Layer removed");
               }
               //..
           }
       });

Example: Map opens/created/closes If you have to do something when a map opens, was created or is
about to close, use the extension point *net.refractions.udig.project.mapInterceptor* in your
**plugin.xml**:

.. code-block:: xml

    [..]
      <extension
            point="net.refractions.udig.project.mapInterceptor">
            <mapOpening
                  class="net.refractions.udig.yourplugin.OpenMapListener"
                  id="net.refractions.udig.yourplugin.mapOpens">
            </mapOpening>
      </extension>
    [..]

And create a new class:

.. code-block:: java

    import net.refractions.udig.project.interceptor.MapInterceptor;
    import net.refractions.udig.project.internal.Map;

    public class OpenMapListener implements MapInterceptor{

        public void run(Map map){
            System.out.println("map opens: " + map.getName());
        }
    }

Example: MapEditor and other ViewParts get activated/opened/closed. If you just want to track
changes of the MapEditor, take a look at **LayersView.MapEditorListener**.

.. code-block:: java

    getSite().getWorkbenchWindow().getPartService().addPartListener(new  IPartListener() {

        public void partActivated(IWorkbenchPart part) {
            System.out.println("partActivated: " + part.getTitle());
        }

        public void partBroughtToTop(IWorkbenchPart part) {
            System.out.println("partBroughtToTop: " + part.getTitle());
        }

        public void partClosed(IWorkbenchPart part) {
            System.out.println("partClosed: " + part.getTitle());
        }

        public void partDeactivated(IWorkbenchPart part) {
            System.out.println("partDeactivated: " + part.getTitle());
        }

        public void partOpened(IWorkbenchPart part) {
            System.out.println("partOpened: " + part.getTitle());
        }
    });

Q: Providing Visual Feedback from a View
                                        

You will need to know which Map is being worked on; either by:

-  looking up the current active part and asking it to "adapt" to a Map.
-  having a workbench listener and watching the editors change; and remembering the Map from the
   last MapEditor used.

.. code-block:: java

    IMap map;

    IMap getMap(){
        return map;
        // you will need to figure this out by listening to the workbench IPartListener
        // see LayerView for an example
    }

-  Creating and Executing a DrawShapeCommand: This technique is good for providing quick visual
   feedback **on the screen**; the draw command stays on the screen until you set it to be invalid.
   An example of this being used is to "flash" a feature when it is selected in the view. Most of
   the examples you see for using a DrawShapeCommand will be for using a ToolContext when a tool
   wants to provide visual feedback. A Context object is just a helper class - when you are working
   on a View you will need to do the work yourself.

   .. code-block:: java

       DrawShapeCommand command = new DrawShapeCommand(shape, paint, lineStyle, lineWidth);

   Here is the kind of work a tool context normally does for you:

   .. code-block:: java

       command.setMap( map );
       ViewportPane viewportPane = map.getRenderManager().getMapDisplay();
       viewportPane.addDrawCommand(command);
       Rectangle validArea;
       try{
           validArea = command.getValidArea();
       }catch (Exception e) {
           validArea=null;
       }
       if( validArea!=null )
           viewportPane.repaint(validArea.x, validArea.y, validArea.width, validArea.height);
       else
           viewportPane.repaint();

   A review of how draw commands work - they stay on the map until they are invalid. So at some
   point "later" you can take the command off the screen by:

   .. code-block:: java

       command.setValid(false);

If you want to issue a single command to schedule a series of shapes to be drawn look at the
animation code example...it takes care using a timer to handle the the "later".

-  Implement a MapGraphic displaying data from a Blackboard

My last idea was to create public class TripMapGraphic implements MapGraphic {} , retrieving a
List<Coordinate> from backboard, and drawing them.

This is a **great** way to provide visual feedback **on the map**. An example would be a
TripMapGraphic that shows a route generated from the geotools graph module.

You can add a MapGraphic to the Map at any point by sending an "Add Layer" command to the Map. An
example is the edit tool snapping functionality adding the Graph map graphic when "snapping to
grid".

This second way is good if you want to "mark up" the map for a specific purpose; say highlighting
some roads on the screen in response to finding a route; using a MapGraphic will let your user pan
and zoom around and still see your highlight.

The training course example coordinate map graphic and coordinate tool cover this kind of case. This
example makes use of screen coordiantes; you will need to use the viewport world 2 screen and screen
2 world transformations to record the coordinates "on the map".

Here is an example of storing points on the layer blackboard in lat/lon:

.. code-block:: java

    IBlackboard blackboard = map.getBlackboard();
    List<Coordinate> points =  (List<Coordinate>) blackboard.get("route);

    if (points == null) {
        points = new ArrayList<Coordinate>();
        blackboard.put(BLACKBOARD_KEY,points);
    }
    points.add( new Coordinate(lat,lon) ); // ie DefaultGeographicCRS.WGS84

You can draw these onto the screen in your mapgraphic render method

.. code-block:: java

    public void draw( MapGraphicContext context ) {

        //initialize the graphics handle
         ViewportGraphics g = context.getGraphics();
         g.setColor(Color.BLACK);
         g.setStroke(ViewportGraphics.LINE_SOLID, 1);
         
         //get the map blackboard
         IMap map = context.getLayer().getMap();
         IBlackboard blackboard = map.getBlackboard();
         
         List<Coordinate> coordinates = (List<Coordinate>) blackboard.get("route");
         
         if (coordinates == null) {
             return; //no coordinates to draw
         }
             
        MathTransform data2world = CRS.findMathTransform( DefaultGeographicCRS.WGS84, context.getCRS() );
         
         for (Coordinate coordinate : coordinates) {
            Ellipse2D e = new Ellipse2D.Double(
                    coordinate.x-4,
                    coordinate.y-4,
                    10,10);
            g.draw(e);
            try {
                Coordinate worldCoord = JTS.transform(coordinate, null, data2world );
                Point point = context.worldToPixel(worldCoord);
                g.drawOval( point.x-1, point.y-2, 3, 3);
            } catch (TransformException e1) {
            }
         }
     }

