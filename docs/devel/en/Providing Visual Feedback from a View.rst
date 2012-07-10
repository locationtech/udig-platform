Providing Visual Feedback from a View
=====================================

This is response to a question from Frederic Renard.

Hi uDig'gers,

I want to draw lines (car trip history) on a layer from a list of Cooridantes. What should be the
best method ?

Let's explore the options:

* :doc:`Precondition - Know your Map`

-  `Creating and Executing a
   DrawShapeCommand <#ProvidingVisualFeedbackfromaView-CreatingandExecutingaDrawShapeCommand>`_
-  `Implement a MapGraphic displaying data from a
   Blackboard <#ProvidingVisualFeedbackfromaView-ImplementaMapGraphicdisplayingdatafromaBlackboard>`_

Precondition - Know your Map
============================

You will need to know which Map is being worked on; either by:

-  looking up the current active part and asking it to "adapt" to a Map.
-  having a workbench listener and watching the editors change; and remembering the Map from the
   last MapEditor used.

::

    IMap map;

    IMap getMap(){
        return map;
        // you will need to figure this out by listening to the workbench IPartListener
        // see LayerView for an example
    }

It would be nice to make a super class that does all this for you already...

Creating and Executing a DrawShapeCommand
=========================================

This technique is good for providing quick visual feedback **on the screen**; the draw command stays
on the screen until you set it to be invalid. An example of this being used is to "flash" a feature
when it is selected in the view.

Most of the examples you see for using a DrawShapeCommand will be for using a ToolContext when a
tool wants to provide visual feedback. A Context object is just a helper class - when you are
working on a View you will need to do the work yourself.

::

    DrawShapeCommand command = new DrawShapeCommand(shape, paint, lineStyle, lineWidth);

Here is the kind of work a tool context normally does for you:

::

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

A review of how draw commands work - they stay on the map until they are invalid. So at some point
"later" you can take the command off the screen by:

::

    command.setValid(false);

If you want to issue a single command to schedule a series of shapes to be drawn look at the
animation code example...it takes care using a timer to handle the the "later".

Implement a MapGraphic displaying data from a Blackboard
========================================================

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

The training course example coordinate map graphic and coordinate tool cover this kind of case:

* :doc:`http://svn.refractions.net/udig/udig/branches/1.1.x/udig/tutorials/net.refractions.udig.tutorials.tool.coordinate/`

* :doc:`http://svn.refractions.net/udig/udig/branches/1.1.x/udig/tutorials/net.refractions.udig.tutorials.mapgraphic/`


Please note that the above example makes use of screen coordiantes; you will need to use the
viewport world 2 screen and screen 2 world transformations to record the coordinates "on the map".

Here is an example of storing points on the layer blackboard in lat/lon:

::

    IBlackboard blackboard = map.getBlackboard();
    List<Coordinate> points =  (List<Coordinate>) blackboard.get("route);

    if (points == null) {
        points = new ArrayList<Coordinate>();
        blackboard.put(BLACKBOARD_KEY,points);
    }
    points.add( new Coordinate(lat,lon) ); // ie DefaultGeographicCRS.WGS84

You can draw these onto the screen in your mapgraphic render method

::

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

