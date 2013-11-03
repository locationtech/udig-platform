Map Decorator
=============

Map decorations are implemented as **MapGraphic** classes that draw onto the map without the need to
access data. Examples are the scale bar or legend where what is drawn is completely dictated by
current Map and ViewportModel.

References:

-  :doc:`map_decorator_tutorial`

Finding and Using a Map Decorator
---------------------------------

All the implementations of MapGraphic are gathered into a single IService; with a IGeoResource
representing each implementation.

As such you can look them up using catalog; we have placed the identifier as a static constant -
however it is best to use a string as that way you do not need to depend on the specific
implementation used.

To use **GridMapGraphic** we can look up its definition:

.. code-block:: xml

    <mapGraphic
                class="org.locationtech.udig.mapgraphic.grid.GridMapGraphic"
                icon="icons/obj16/grid_obj.gif"
                id="grid"
                name="%mapgraphic.grid.name"/>

So we can create the correct ID to look that up:

.. code-block:: java

    ID grid_id = new ID( MapGraphicService.SERVICE_ID, "grid" );

Or if you don't want to add the dependency:

.. code-block:: java

    IRepository local = CatalogPlugin.getDefault().getLocal();

    final ID GRID_ID = new ID( "mapgraphic:///localhost/mapgraphic#grid", null );
    IGeoResource gridResource = local.getById(IGeoResource.class, GRID_ID, new NullProgressMonitor() );
    // You can then use this with the AddLayersCommand

Implementing Map Graphic
------------------------

The interface for MapGraphic is straight forward:

.. code-block:: java

    public interface MapGraphic {
        /** extension point id **/
        public static final String XPID = "org.locationtech.udig.mapgraphic.mapgraphic"; //$NON-NLS-1$
        /**
         * Draws the graphic.  Check the clip area of the {@link ViewportGraphics} object to determine what
         * area needs to be refreshed.
         * 
         * @param context The drawing context.
         */
        void draw(MapGraphicContext context);
    }

As you can see you are responsible for implementing a single draw method; the MapGraphicsContext
provides access to the screen, access to the Layer (holding any style settings you require) and the
Map and MapViewport.

Here is a simple example from the :doc:`map_decorator_tutorial` tutorial using screen coordinates:

.. code-block:: java

    public void draw( MapGraphicContext context ) {
        //initialize the graphics handle
         ViewportGraphics g = context.getGraphics();
         g.setColor(Color.RED);
         g.setStroke(ViewportGraphics.LINE_SOLID, 2);
         
         //get the map blackboard
         IMap map = context.getLayer().getMap();
         IBlackboard blackboard = context.getLayer().getMap().getBlackboard();
         
         List<Coordinate> coordinates = (List<Coordinate>) blackboard.get("locations");
         
         if (coordinates == null) {
             return; //no coordinates to draw
         }
             
         //for each coordnate, create a circle and draw
         for (Coordinate coordinate : coordinates) {
            Ellipse2D e = new Ellipse2D.Double(
                    coordinate.x-4,
                    coordinate.y-4,
                    10,10);
            g.draw(e);
         }

If you are working in real world locations (such as WGS84) you will need to convert those locations
to screen coordinates:

.. code-block:: java

    public void draw(MapGraphicContext context) {
            // initialize the graphics handle
            ViewportGraphics g = context.getGraphics();
            g.setColor(Color.RED);
            g.setStroke(ViewportGraphics.LINE_SOLID, 2);

            // get the map blackboard
            IMap map = context.getLayer().getMap();
            IBlackboard blackboard = context.getLayer().getMap().getBlackboard();

            List<Coordinate> coordinates = (List<Coordinate>) blackboard.get("locations");

            if (coordinates == null) {
                return; // no coordinates to draw
            }
            try {
                MathTransform dataToWorldTransform = CRS.findMathTransform(DefaultGeographicCRS.WGS84, context.getCRS() );
        
                // for each location, create a circle and draw
                for (Coordinate location : coordinates) {
                    Coordinate world = JTS.transform(location,  null, dataToWorldTransform);
                    Point pixel = context.worldToPixel(world);
                    Ellipse2D e = new Ellipse2D.Double(pixel.x - 4, pixel.y - 4, 10, 10);
                    g.draw(e);
                }
            }
            catch (FactoryException unableToTransform){
                context.getLayer().setStatusMessage(unableToTransform.getMessage());
            } catch (TransformException outOfBounds) {
                context.getLayer().setStatusMessage(outOfBounds.getMessage());
            }
        }

