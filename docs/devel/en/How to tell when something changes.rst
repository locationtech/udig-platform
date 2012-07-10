How to tell when something changes
==================================

The uDig "application model" is maintained with EMF (the eclipse modeling framework). As such it is
very easy to morph uDig to meet your needs.

That is the good part.

The bad part is uDig (indeed all EMF projects) do not look that "normal", and trying to tell when
something changes falls into the this category.

Step 0 - Look for a Listener
----------------------------

We have set up add/remove listener methods as people request them:

::

    layer.addListener( new ILayerListener(){
       public refresh( LayerEvent event ){
           if( event.getType() == LayerEvent.EventType.STYLE ){
               ILayer layer = event.getSource();
               IStyleBlackboard style = (IStyleBlackboard ) event.getNewValue();

               // layer has changed style
           }
       }
    });

Step 1 - Ask for Help
---------------------

Email the list and ask "How to I tell when this changes". We will set up a normal add/remove
listener.

Step 2 - Use an Adapter
-----------------------

On the off chance you have a deadline, and need to figure it out now you can listen to anything
using the following code example:

Example:

::

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

Backgroun: an adapter is a traditional pater when you want to use one data model and "morph" it to
fit another interface. One of the side effects of this is you need to pay attention to the origional
data, and pass any changes along.

You can see lots of examples of this idea in Java code. People setting up custom JTreeModels to
visualize an internal data structure etc...

Since this need happens **all** the time the EMF crew decided design for it in mind. It is a much
more difficult, and interesting, problem them simply listening for changes (Indeed it is a superset
of change notification - basically change notification with interface change). The benifit is that
you can "force" EMF models (and thus uDig) into about anything.

So the above example is "an adapter", and we are only paying attention to the changes.

Examples of Anything
~~~~~~~~~~~~~~~~~~~~

Viewport changed
^^^^^^^^^^^^^^^^

Here is how you can watch the "Viewport Model" (ie. Zoom, Pan, CRS):

::

    map.getViewportModel().addViewportModelListener(new IViewportModelListener()){
      public void changed(ViewportModel event){
        if( event.getType()==EventType.CRS }
          // crs has changed do something
        else if( event.getType()==EventType.BOUNDS ){
          // bounds have changed do something else
        }
      }
    }

Layer(s) added/removed
^^^^^^^^^^^^^^^^^^^^^^

:doc:`IMapCompositionListener`

tells you when layers were added to the map, were deleted from the map or when the layer list order
has changed. For more information take a look at
`MapCompositionEvent.EventType <http://udig.refractions.net/files/docs/api-udig/net.refractions.udig.project/net/refractions/udig/project/MapCompositionEvent.EventType.html>`_.

::

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

Map opens/created/closes
^^^^^^^^^^^^^^^^^^^^^^^^

If you have to do something when a map opens, was created or is about to close, use the extension
point *net.refractions.udig.project.mapInterceptor*.

Add this to your "plugin.xml":

::

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

::

    import net.refractions.udig.project.interceptor.MapInterceptor;
    import net.refractions.udig.project.internal.Map;

    public class OpenMapListener implements MapInterceptor{

        public void run(Map map){
            System.out.println("map opens: " + map.getName());
        }
    }

MapEditor and other ViewParts get activated/opened/closed/..
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If you just want to track changes of the MapEditor, take a look at
*net.refractions.udig.project.ui.internal.LayersView.MapEditorListener*.

::

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

