/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.support;


import java.awt.geom.PathIterator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.JTS;
import org.geotools.util.factory.GeoTools;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.udig.core.IBlockingProvider;
import org.locationtech.udig.core.internal.FeatureUtils;
import org.locationtech.udig.mapgraphic.grid.GridMapGraphic;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.Interaction;
import org.locationtech.udig.project.ProjectBlackboardConstants;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableComposite;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.ui.AnimationUpdater;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.project.ui.tool.IToolContext;
import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.animation.SearchBoxAnimation;
import org.locationtech.udig.tools.edit.commands.AddVertexCommand;
import org.locationtech.udig.tools.edit.commands.CreateAndSelectHoleCommand;
import org.locationtech.udig.tools.edit.preferences.PreferenceUtil;
import org.locationtech.udig.ui.ProgressManager;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;
import org.opengis.filter.identity.Identifier;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * Methods for determining spatial relationships between points. 
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class EditUtils {


    static final int TOP = 0x8, BOTTOM = 0x4, RIGHT = 0x2, LEFT = 0x1;

    public static final int OVER_EDGE = -1;
    public static final int NO_INTERSECTION = -2;

    private static final String EDIT_FEATURE_BOUNDS = "BOUNDS OF RENDERING FILTER"; //$NON-NLS-1$
    

    public static EditUtils instance=new EditUtils();
    
    public int overVertext( Coordinate[] coords, Envelope env ) {
        if (coords == null)
            return NO_INTERSECTION;
        for( int i = 0; i < coords.length; i++ ) {
            Coordinate coord = coords[i];
            if (env.contains(coord))
                return i;
        }
        return OVER_EDGE;
    }

    /**
     * Return true if the envelope overlaps at least one edge of the shape.  This checks all the
     * Coordinates in the shape so the envelope must be in the "world" space (same projection as coordinates). 
     * 
     * @param shape to search 
     * @param env envelope used to see if it overlaps an edge
     * @return true if the envelope overlaps at least one edge of the shape.
     */
    public boolean overEdgeCoordinatePrecision( PrimitiveShape shape, Envelope env) {
        if( shape.getNumCoords()<2 )
            return false;
        Coordinate endPoint1=shape.getCoord(shape.getNumCoords()-1);
        for( int i = 0; i < shape.getNumCoords(); i++ ) {
            Coordinate endPoint2 = shape.getCoord(i);
            if (overEdge(endPoint1, endPoint2, env) )
                return true;
            endPoint1 = endPoint2;
        }
        return false;
    }

    /**
     * Return true if the envelope overlaps at least one edge of the shape.  This only checks the
     * Points in the shape so the envelope must be in pixel space.  This also means that it is not 
     * completely accurate but it is sufficient for interactive purposes.
     * 
     * @param shape to search 
     * @param env envelope used to see if it overlaps an edge
     * @return true if the envelope overlaps at least one edge of the shape.
     */
    public boolean overEdgePixelPrecision( PrimitiveShape shape, Envelope env ) {
        if( shape.getNumPoints()<2 )
            return false;
        Point point1=shape.getPoint(shape.getNumPoints()-1);
        for( int i = 0; i < shape.getNumPoints(); i++ ) {
            Point point2 = shape.getPoint(i);
            
            Coordinate endPoint1=new Coordinate(point1.getX(), point1.getY());
            Coordinate endPoint2=new Coordinate(point2.getX(), point2.getY());
            if (overEdge(endPoint1, endPoint2, env) )
                return true;
            point1 = point2;
        }
        return false;
    }

    /**
     * Returns true if the envelope overlaps some part of the edge
     *
     * @param endPoint1 one end point of the edge
     * @param endPoint2 the other end point of the edge
     * @param env the reference envelope.
     * @return true if the envelope overlaps some part of the edge
     */
    public boolean overEdge( Coordinate endPoint1, Coordinate endPoint2, Envelope env ) {
        boolean accept = false, done = false;
        double x0 = endPoint1.x;
        double y0 = endPoint1.y;
        double x1 = endPoint2.x;
        double y1 = endPoint2.y;
        
        double xmin=env.getMinX();
        double ymin=env.getMinY();
        double xmax=env.getMaxX();
        double ymax=env.getMaxY();
        
        int outcode0 = compOutCode(x0, y0, xmin, xmax, ymin, ymax);
        int outcode1 = compOutCode(x1, y1, xmin, xmax, ymin, ymax);
        do {
            if (outcode0 == 0 || outcode1 == 0)
                accept = done = true;
            else if ((outcode0 & outcode1) != 0)
                done = true;
            else {
                double x, y;
                int outcodeOut = outcode0 > 0 ? outcode0 : outcode1;
                if ((outcodeOut & TOP) > 0) {
                    x = x0 + (x1 - x0) * (ymax - y0) / (y1 - y0);
                    y = ymax;
                } else if ((outcodeOut & BOTTOM) > 0) {
                    x = x0 + (x1 - x0) * (ymin - y0) / (y1 - y0);
                    y = ymin;
                } else if ((outcodeOut & RIGHT) > 0) {
                    y = y0 + (y1 - y0) * (xmax - x0) / (x1 - x0);
                    x = xmax;
                } else {
                    y = y0 + (y1 - y0) * (xmin - x0) / (x1 - x0);
                    x = xmin;
                }
                if (outcodeOut == outcode0) {
                    double tmpx = x;
                    double tmpy = y;
                    outcode0 = compOutCode(tmpx, tmpy, xmin, xmax, ymin, ymax);
                } else {
                    double tmpx = x;
                    double tmpy = y;
                    outcode1 = compOutCode(tmpx, tmpy, xmin, xmax, ymin, ymax);
                }
            }
        } while( done == false );
        return accept;
    }

    private int compOutCode( double x, double y, double xmin, double xmax, double ymin, double ymax ) {
        int outcode = 0;
        if (y > ymax)
            outcode |= TOP;
        if (y < ymin)
            outcode |= BOTTOM;
        if (x > xmax)
            outcode |= RIGHT;
        if (x < xmin)
            outcode |= LEFT;
        return outcode;
    }

    /**
     * Returns the index of the coordinate closest to the click.
     * @param geometry the geometry to search for the closest coordinate;  The default geometry is searched.
     * @param click the closest coordinate in <i>coordinates</i> will be found with respect to
     *        <i>click</i>
     * @param result the first position will be fill with the closest coordinate in geometry.
     * 
     * @return the index of the coordinate closest to the click.
     */
    public int getClosest( Geometry geometry, Coordinate click, Coordinate[] result ) {
        Coordinate[] coordinates = geometry.getCoordinates();
        int prev = 0;
        double mindist = Double.MAX_VALUE;
        Coordinate closest = coordinates[coordinates.length - 1];
        double x = click.x - closest.x;
        double y = click.y - closest.y;
        mindist = Math.sqrt(x * x + y * y);
        for( int i = 0; i < coordinates.length; i++ ) {
            Coordinate point = coordinates[i];
            
            x = click.x - point.x;
            y = click.y - point.y;
            double dist = Math.sqrt(x * x + y * y);
            if (dist < mindist) {
                mindist = dist;
                prev = i;
                closest = point;
            }
        }
        result[0]=closest;
        if (geometry instanceof LinearRing) {
            return (prev == 0 || prev == coordinates.length) ? coordinates.length - 2 : prev;
        }
        return prev != 0 ? prev - 1 : coordinates.length - 2;
    }

    /**
     * Returns the closest point on the line between <code>vertex1</code> and <code>vertex2</code> to coordinate
     * <code>src</code>
     * <p>
     * All Coordinates must be in the same CRS
     * </p>
     * 
     * @param endPoint1 first vertex of a line.
     * @param endPoint2 second vertex of a line.
     * @param src the closes coordinate is found with respect to src.
     * @return the closest point on the line between <code>vertex1</code> and <code>vertex2</code> to coordinate
     * <code>src</code>
     */
    public Coordinate closestCoordinateOnEdge( Coordinate endPoint1, Coordinate endPoint2, Coordinate src ) {
        Coordinate v = new Coordinate();
        v.x = endPoint2.x - endPoint1.x;
        v.y = endPoint2.y - endPoint1.y;
        double d = (v.x * v.x + v.y * v.y);
        if( d==0 )
            return null;
        double t = ((src.x - endPoint1.x) * v.x + (src.y - endPoint1.y) * v.y)
                / d;
        if ( t<0 || t>1 || Double.isInfinite(t) || Double.isNaN(t))
            return null;
        Coordinate result = new Coordinate();
        result.x = endPoint1.x + t * v.x;
        result.y = endPoint1.y + t * v.y;
        return result;
    }

    /**
     * Returns the closest point on the line between <code>vertex1</code> and <code>vertex2</code> to coordinate
     * <code>src</code>
     * <p>
     * All Coordinates must be in the same CRS
     * </p>
     * 
     * @param endPoint1 first vertex of a line.
     * @param endPoint2 second vertex of a line.
     * @param src the closes coordinate is found with respect to src.
     * @return the closest point on the line between <code>vertex1</code> and <code>vertex2</code> to coordinate
     * <code>src</code>
     */
    public Point closestPointOnEdge( Point endPoint1, Point endPoint2, Point src ) {
        if( endPoint1.equals(src) )
            return src;
        if( endPoint2.equals(src) )
            return src;
        
        Point v = Point.valueOf(endPoint2.getX() - endPoint1.getX(), 
                endPoint2.getY() - endPoint1.getY());
        int i = v.getX() * v.getX() + v.getY() * v.getY();
        if( i==0 )
            return null;
        int j = (src.getX() - endPoint1.getX()) * v.getX();
        int k = (src.getY() - endPoint1.getY()) * v.getY();
        double t = (double)(j + k)
                / (double)i;
        if( t>=1 )
            return endPoint2;
        if( t<=0 )
            return endPoint1;
        
        if (Double.isInfinite(t) || Double.isNaN(t))
            return null;

        return Point.valueOf((int)(endPoint1.getX() + t * v.getX()), 
                (int)(endPoint1.getY() + t * v.getY()));
    }

    /**
     * Convenience method; transforms the click from the viewportModel CRS to the layer's CRS.
     * 
     * @param click
     * @return
     */
    public Coordinate getTransformedClick( Coordinate click, ILayer layer ) {
    
        try {
            MathTransform transform = layer.mapToLayerTransform();
            if (transform == null || transform.isIdentity())
                return click;
            return JTS.transform(click, new Coordinate(), transform);
        } catch (Exception e1) {
            // CorePlugin.log(ToolsPlugin.getDefault(), e1);
            return click;
        }
    }

    /**
     * Finds and returns the EditGeoms that intersect the point.  This maybe an expensive operation if there
     * are a large number of EditGeoms each with many points.  The calculation is done in screen space, however,
     * so the number of coordinates in the shapes do not matter so much.
     *
     * @param editBlackboard
     * @param point
     * @param treatUnknownAsPolygons
     * @return
     */
    public List<EditGeom> getIntersectingGeom( EditBlackboard editBlackboard, Point point, boolean treatUnknownAsPolygons ) {
        List<EditGeom> geoms = editBlackboard.getGeoms();
        List<EditGeom> result= new LinkedList<EditGeom>();
        for( EditGeom geom : geoms ) {
            EditGeomPathIterator iter=EditGeomPathIterator.getPathIterator(geom);
            iter.setPolygon(treatUnknownAsPolygons);
            if( iter.toShape().contains(point.getX(), point.getY()) )
                result.add(geom);
        }
        return result;
    }

    /**
     * Returns the coordinate that is on the grid intersection closest to the coordinate.
     */
    public Coordinate snapToGrid( Point centerPoint, IMap map )  {
        List<ILayer> layers = map.getMapLayers();
        
        // by default choose something that will work
        ILayer found=layers.get(0);
        GridMapGraphic graphic=new GridMapGraphic();
        for( ILayer layer : layers ) {
            if( layer.hasResource(GridMapGraphic.class) ){
                found = layer;
                try {
                    graphic = layer.getResource(GridMapGraphic.class, ProgressManager.instance().get());
                } catch (IOException e) {
                    throw (RuntimeException) new RuntimeException( ).initCause( e );
                }
                break;
            }
        }
        
        
        double[] closest;
        try {
            closest = graphic.closest(centerPoint.getX(), centerPoint.getY(), found);
        } catch (FactoryException e) {
            EditPlugin.log(null, e);
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        } 
        return new Coordinate(closest[0], closest[1], 0);
    }


    /**
     * Searches all the layers in the map and the EditBlackboard for the closest vertex to center point
     * @param includeVerticesInCurrent indicates whether the vertices of the current feature should be considered.
     * @param stateAfterSearch 
     * @return the Point that is the closest vertex.
     */
    public Coordinate getClosestSnapPoint( EditToolHandler handler, EditBlackboard editBlackboard, Point centerPoint, boolean includeVerticesInCurrent, 
            SnapBehaviour snapBehaviour, EditState stateAfterSearch ) {
        
        IToolContext context = handler.getContext();
        MinFinder minFinder = new MinFinder(editBlackboard.toCoord(centerPoint));
        SearchBoxAnimation anim = new SearchBoxAnimation(centerPoint, new IsBusyStateProvider(
                handler));

        try {
            handler.setCurrentState(EditState.BUSY);
            if (snapBehaviour != SnapBehaviour.OFF && snapBehaviour != SnapBehaviour.GRID)
                AnimationUpdater.runTimer(context.getMapDisplay(), anim);
            switch( snapBehaviour ) {
            case OFF:
                return null;
            case SELECTED:
                searchSelection(handler, editBlackboard, centerPoint, includeVerticesInCurrent,
                        minFinder);
                return minFinder.getMinCoord();
            case CURRENT_LAYER:
                searchSelection(handler, editBlackboard, centerPoint, includeVerticesInCurrent,
                        minFinder);
                minFinder.add(searchLayer(handler.getEditLayer(), context, centerPoint));
                break;
            case ALL_LAYERS:
                searchSelection(handler, editBlackboard, centerPoint, includeVerticesInCurrent,
                        minFinder);
                for( ILayer layer : context.getMapLayers() ) {
                    minFinder.add(searchLayer(layer, context, centerPoint));
                }
                break;
            case GRID:
                Coordinate worldCoord = snapToGrid(centerPoint, context.getMap());
                try {
                    return JTS.transform(worldCoord, null,
                            editBlackboard.pointCoordCalculator.mapToLayer);
                } catch (TransformException e) {
                    return null;
                }
            default:
                break;
            }

            Coordinate min = minFinder.getMinCoord();
            try {
                if (min == null)
                    return null;
                return JTS.transform(min, new Coordinate(),
                        editBlackboard.pointCoordCalculator.mapToLayer);
            } catch (Exception e) {
                EditPlugin.log("", e); //$NON-NLS-1$
                return null;
            }
        } finally {
            if (stateAfterSearch == EditState.BUSY)
                handler.setCurrentState(EditState.MODIFYING);
            else
                handler.setCurrentState(stateAfterSearch);
            anim.setValid(false);
        }

    }

    /**
     * Searches the editblackboard and adds the closest vertex to the minFinder
     */
    private void searchSelection( EditToolHandler handler, EditBlackboard editBlackboard, Point centerPoint, boolean includeVerticesInCurrent, MinFinder minFinder ) {
        Point point = editBlackboard.overVertex(centerPoint, PreferenceUtil.instance()
                .getSnappingRadius(), true);
        
        // the vertices in the current geometry should only be considered if inlcudeVerticesInCurrent is true
        boolean containsNonCurrentShape = containsNonCurrentShape(point, editBlackboard, handler.getCurrentShape());
        if( point!=null && (includeVerticesInCurrent || containsNonCurrentShape) )
        	minFinder.add ( editBlackboard.toCoord(point) );
    }

    private boolean containsNonCurrentShape( Point p, EditBlackboard editBlackboard, PrimitiveShape currentShape) {
        if( p==null || currentShape==null )
            return false;
        List<EditGeom> geoms = editBlackboard.getGeoms(p.getX(), p.getY());
        if( geoms.isEmpty() )
            return false;
        if( geoms.size()>1 || geoms.get(0)!=currentShape.getEditGeom() )
            return true;
        
        
        return false;
    }

    /**
     * Searches the layer for coordinates within snapping distance
     *
     * @param layer the layer to search.
     * @param context the context to use for convenience methods
     * @param centerPoint the current centerPoint.
     * @return the closest vertex in the layer within the snapping radius or null.
     */
    private Coordinate searchLayer( ILayer layer, IToolContext context, Point centerPoint  ) {
        if (!layer.hasResource(FeatureSource.class) || 
                !layer.getInteraction(Interaction.EDIT) 
                || !layer.isVisible() )
            return null;
        
        ILayer editLayer = context.getEditManager().getEditLayer();
        SimpleFeature editFeature=context.getEditManager().getEditFeature();
        String editFeatureID=null;
        if( editFeature!=null )
            editFeatureID=editFeature.getID();
        
        Envelope bbox = context.getBoundingBox(
                new java.awt.Point(centerPoint.getX(), centerPoint.getY()),
                PreferenceUtil.instance().getSnappingRadius() * 2);
        try {
            Coordinate tmp = context.pixelToWorld(centerPoint.getX(), centerPoint.getY());
            Coordinate layerCenter = JTS.transform(tmp, new Coordinate(), layer
                    .mapToLayerTransform());
            FeatureCollection<SimpleFeatureType, SimpleFeature>  features = context.getFeaturesInBbox(layer, bbox);
            FeatureIterator<SimpleFeature> iter = null;
            try {
                Coordinate closest = null;
                double minDist = Integer.MAX_VALUE;
                for( iter = features.features(); iter.hasNext(); ) {
                    SimpleFeature feature = iter.next();
                    if( feature.getID().equals(editFeatureID) && layer==editLayer )
                        continue;
                    Coordinate[] result = new Coordinate[1];
                    EditUtils.instance.getClosest((Geometry) feature.getDefaultGeometry(), layerCenter, result);
                    double x = layerCenter.x - result[0].x;
                    double y = layerCenter.y - result[0].y;
                    double distNew = Math.sqrt(x * x + y * y);

                    if (distNew < minDist) {
                        closest = result[0];
                        minDist = distNew;
                    }
                }
                if (closest != null) {
                    Coordinate inMapCoords = new Coordinate();
                    JTS.transform(closest, inMapCoords, layer.layerToMapTransform());
                    java.awt.Point point = context.worldToPixel(inMapCoords);

                    double x = centerPoint.getX() - point.x;
                    double y = centerPoint.getY() - point.y;
                    double distNew = Math.sqrt(x * x + y * y);
                    if( distNew<PreferenceUtil.instance().getSnappingRadius())
                        return inMapCoords;
                    else
                        return null;
                }
            } finally {
                if (iter != null) {
                    iter.close();
                }
            }
        } catch (Exception e) {
            EditPlugin.log("", e); //$NON-NLS-1$
        }
        return null;
    }
    /**
     * Keeps track of the point that is the minimum distance to the center point.
     * @author Jesse
     * @since 1.1.0
     */
    public static class MinFinder{
        private Point centerPoint;
        private Point currentMin;
        private double distance;
        private Coordinate centerCoord;
        private Coordinate minCoord;

        public MinFinder(Point centerPoint){
            if( centerPoint==null )
                throw new NullPointerException("centerPoint cannot be null"); //$NON-NLS-1$
            this.centerPoint=centerPoint;
        }
        
        public MinFinder( Coordinate coord ) {
            this.centerCoord=coord;
        }

        public Point getMin(){
            return currentMin;
        }
        
        public Coordinate getMinCoord(){
            return minCoord;
        }
        
        public void add(Point p) {
            if (p==null || p.equals(centerPoint))
                return;
            if( currentMin==null ){
                currentMin=p;
                distance=dist(p);
                return;
            }
            
            double dist = dist(p);
            if( dist<distance ){
                currentMin=p;
                distance=dist;
            }
            
        }
        
        public double dist(Point p){
            double x = centerPoint.getX() - p.getX();
            double y = centerPoint.getY() - p.getY();
            return Math.sqrt(x * x + y * y);
        }
        public void add(Coordinate p) {
            if (p==null || p.equals(centerCoord))
                return;
            if( minCoord==null ){
                minCoord=p;
                distance=dist(p);
                return;
            }
            
            double dist = dist(p);
            if( dist<distance ){
                minCoord=p;
                distance=dist;
            }
            
        }
        
        public double dist(Coordinate p){
            double x = centerCoord.x - p.x;
            double y = centerCoord.y - p.y;
            return Math.sqrt(x * x + y * y);
        }
    }
    

    /**
     * Returns the intersection where the two lines meet
     */
    public Coordinate intersectingLines( Coordinate line1P1, Coordinate line1P2, Coordinate line2P1, Coordinate line2P2) {
        
        double B1 = line1P1.x-line1P2.x;
        double B2 = line2P1.x-line2P2.x;
        double A1 = line1P2.y-line1P1.y;
        double A2 = line2P2.y-line2P1.y;
        double C1 = A1*line1P1.x+B1*line1P1.y;
        double C2 = A2*line2P1.x+B2*line2P1.y;
        
        double det = A1*B2 - A2*B1;
        if(det == 0){
            //Lines are parallel
            return null;
        }
        double x = (B2*C1 - B1*C2)/det;
        double y = (A1*C2 - A2*C1)/det;

        boolean onLine1=Math.min(line1P1.x, line1P2.x)<=x&& x<=Math.max(line1P1.x, line1P2.x)
            && Math.min(line1P1.y, line1P2.y)<=y && y<=Math.max(line1P1.y, line1P2.y);
        
        boolean onLine2=Math.min(line2P1.x, line2P2.x)<=x&& x<=Math.max(line2P1.x, line2P2.x)
        && Math.min(line2P1.y, line2P2.y)<=y && y<=Math.max(line2P1.y, line2P2.y);
        
        if( onLine1 && onLine2 )
            return new Coordinate(x, y);
        
        return null;
    }
    
    /**
     * Returns the intersection where the two lines meet
     */
    public Point intersectingLines(Point line1P1, Point line1P2, Point line2P1, Point line2P2){
        
        int B1 = line1P1.getX()-line1P2.getX();
        int B2 = line2P1.getX()-line2P2.getX();
        int A1 = line1P2.getY()-line1P1.getY();
        int A2 = line2P2.getY()-line2P1.getY();
        int C1 = A1*line1P1.getX()+B1*line1P1.getY();
        int C2 = A2*line2P1.getX()+B2*line2P1.getY();
        
        double det = A1*B2 - A2*B1;
        if(det == 0){
            //Lines are parallel
            return null;
        }
        double x = (B2*C1 - B1*C2)/det;
        double y = (A1*C2 - A2*C1)/det;

        boolean onLine1=Math.min(line1P1.getX(), line1P2.getX())<=x&& x<=Math.max(line1P1.getX(), line1P2.getX())
            && Math.min(line1P1.getY(), line1P2.getY())<=y && y<=Math.max(line1P1.getY(), line1P2.getY());
        
        boolean onLine2=Math.min(line2P1.getX(), line2P2.getX())<=x&& x<=Math.max(line2P1.getX(), line2P2.getX())
        && Math.min(line2P1.getY(), line2P2.getY())<=y && y<=Math.max(line2P1.getY(), line2P2.getY());
        
        if( onLine1 && onLine2 )
            return Point.valueOf((int)x, (int)y);
        
        return null;
    }

    /**
     * Reverse the order of the vertices in a Shape.  Used because the holes and shells in polygons have to 
     * be in a particular order. 
     * 
     * @param shape
     */
    public void reverseOrder( PrimitiveShape shape ) {
    	synchronized (shape.getEditBlackboard()) {
    		shape.getMutator().reverse();
		}
    }
    

    /**
     * Appends the points defined in the PathIterator to the shape.  Currently curve segments are not supported
     * and if there is a moveto in the middle of the iterator a hole will be created in the shape.
     * If the GeomType is line or point then an exception will be thrown but otherwise the client code
     * must ensure that the request makes sense.
     * 
     * @param iter The iterator to append
     * @param shape the shape to append to.
     * @return Commands that will append the points to the shape.  Nothing is done until commands are run. 
     */
    public UndoableComposite appendPathToShape( EditToolHandler handler, PathIterator iter, PrimitiveShape shape) {
        EditBlackboard bb=shape.getEditBlackboard();
        IBlockingProvider<PrimitiveShape> currentProvider=new StaticShapeProvider(shape);
        return appendPathToShape(iter, shape.getEditGeom().getShapeType(), handler, bb, currentProvider);
    }

    /**
     * Appends the points defined in the PathIterator to the shape.  Currently curve segments are not supported
     * and if there is a move to in the middle of the iterator a hole will be created in the shape.
     * If the GeomType is line or point then an exception will be thrown but otherwise the client code
     * must ensure that the request makes sense.
     * 
     * @param iter The iterator to append
     * @param bb the editblackboard used to add coordinates
     * @param currentProvider2 the shape provider that provides the shape to append the coordinates to
     * @param shapeType the type of geometry that is expected from currentProvider.
     * @return Commands that will append the points to the shape.  Nothing is done until commands are run. 
     */
    public UndoableComposite appendPathToShape( PathIterator iter, ShapeType shapeType, EditToolHandler handler, 
            EditBlackboard bb, IBlockingProvider<PrimitiveShape> currentProvider2 ) {
        IBlockingProvider<PrimitiveShape> currentProvider=currentProvider2;
        
        List<UndoableMapCommand> commands=new ArrayList<UndoableMapCommand>();
        commands.add(new StartBatchingCommand(bb));
        float[] coords=new float[6];
        boolean started=false;
        float[] start=new float[2];
        AddVertexCommand addVertexCommand=null;
        while( !iter.isDone() ){
            int type=iter.currentSegment(coords);
            switch(type){
            case PathIterator.SEG_MOVETO:
                if( !started ){
                    started=true;
                } else {
                    if( shapeType!=ShapeType.POLYGON  )
                        throw new IllegalArgumentException("Holes can not to shapes that are not Polygons.  Current shape is a "+shapeType); //$NON-NLS-1$
                    CreateAndSelectHoleCommand command = new CreateAndSelectHoleCommand(currentProvider);
                    currentProvider=command.getHoleProvider();
                    commands.add(command);
                }
                start[0]=coords[0];
                start[1]=coords[1];
                // no break is intentional.  It has to fall through and add a vertext to the shape
            case PathIterator.SEG_LINETO:            
                addVertexCommand = new AddVertexCommand(handler, bb, currentProvider, Point.valueOf((int)coords[0], (int)coords[1]), false);
                addVertexCommand.setShowAnimation(false);
                commands.add( addVertexCommand);
                break;
            case PathIterator.SEG_CLOSE:
                if (!Point.valueOf((int) coords[0], (int) coords[1]).equals(
                        Point.valueOf((int) start[0], (int) start[1]))) {
                    addVertexCommand = new AddVertexCommand(handler, bb, currentProvider, Point
                            .valueOf((int) start[0], (int) start[1]), false);
                    addVertexCommand.setShowAnimation(false);
                    commands.add(addVertexCommand);
                }
                break;
            default:
                throw new UnsupportedOperationException("not supported"); //$NON-NLS-1$
            
            }
            iter.next();
        }
        
        if (shapeType==ShapeType.POLYGON && addVertexCommand!=null && !addVertexCommand.getPointToAdd().equals(Point.valueOf((int)start[0], (int)start[1]))){
            commands.add( new AddVertexCommand(handler, bb, currentProvider, Point.valueOf((int)start[0], (int)start[1]), false));            
        }
        

        UndoableComposite undoableComposite = new UndoableComposite(commands);
        undoableComposite.getFinalizerCommands().add(new FireEventsCommand(bb));
        return undoableComposite;
    }
    
    private static class StartBatchingCommand extends AbstractCommand implements UndoableMapCommand{
        private EditBlackboard bb;

        StartBatchingCommand( EditBlackboard bb ){
            this.bb=bb;
        }
        public void run( IProgressMonitor monitor ) throws Exception {
            bb.startBatchingEvents();
        }

        public String getName() {
            return null;
        }

        public void rollback( IProgressMonitor monitor ) throws Exception {
        }
        
    }
    
    
    private static class FireEventsCommand extends AbstractCommand implements UndoableMapCommand{
        private EditBlackboard bb;

        FireEventsCommand( EditBlackboard bb ){
            this.bb=bb;
        }
        public void run( IProgressMonitor monitor ) throws Exception {
            bb.fireBatchedEvents();
        }

        public String getName() {
            return null;
        }

        public void rollback( IProgressMonitor monitor ) throws Exception {
        }
        
    }
    public static class StaticShapeProvider implements IBlockingProvider<PrimitiveShape>{
        private PrimitiveShape shape;

        public StaticShapeProvider( PrimitiveShape shape ){
            this.shape=shape;
        }
        
        public PrimitiveShape get(IProgressMonitor monitor, Object... params) {
            return shape;
        }
        
    }
    /**
     * Will retrive the current shape from the EditToolHandler.
     * <p>
     * Please note that only a single shape can be aquired in this manner.
     * <p>
     */
    public static class EditToolHandlerShapeProvider implements IBlockingProvider<PrimitiveShape> {
    
        private EditToolHandler handler;

        /**
         * Lazily grab the current shape from the provided handler. 
         * @param handler
         */
        public EditToolHandlerShapeProvider( EditToolHandler handler ) {
            this.handler = handler;
        }
    
        public PrimitiveShape get(IProgressMonitor monitor, Object... params) {
            return handler.getCurrentShape();
        }    
    }
    

    /**
     * Provider for EditGeoms
     * 
     * @author jones
     * @since 1.1.0
     */
    public static class StaticEditGeomProvider implements IBlockingProvider<EditGeom> {

        private EditGeom geom;

        public StaticEditGeomProvider( EditGeom geom ) {
            this.geom=geom;
        }

        public EditGeom get(IProgressMonitor monitor, Object... params) {
            return geom;
        }

    }


	public static Coordinate midPointOnLine(Coordinate coord, Coordinate coord2) {
		double x=(coord.x+coord2.x)/2;
		double y=(coord.y+coord2.y)/2;
		return new Coordinate(x,y);
	}

    /**
     * The framework stores the current shape and state on a layer when the currently selected layer changes.  This
     * method clears that cache on the layers passed in.  This should be called when the tool is de-actived and when
     * a cancel and accept is run.  This is so that the shapes can be garbage collected.
     *
     * @param layers
     */
    public void clearLayerStateShapeCache(Collection<ILayer> layers) {
        for( ILayer layer : layers ) {
            layer.getBlackboard().put(EditToolHandler.STORED_CURRENT_SHAPE, null);
            layer.getBlackboard().put(EditToolHandler.STORED_CURRENT_STATE, null);
        }
    }
    
    /**
     * When an edit is canceled the selected layer must be re-rendered because they were hidden by {@link #refreshLayer(ILayer, SimpleFeature, Envelope, boolean, boolean)}
     * This method must be called in order to efficiently do that.
     * 
     * @see #refreshLayer(ILayer, SimpleFeature, Envelope, boolean, boolean)
     *
     * @param selectedLayer
     */
    public void cancelHideSelection( ILayer selectedLayer ){
        if( selectedLayer==null )
            return;

        IBlackboard properties = selectedLayer.getBlackboard();
        if( !PreferenceUtil.instance().hideSelectedLayers() ){
            properties.put(ProjectBlackboardConstants.MAP__RENDERING_FILTER, null);
            ((ViewportPane) selectedLayer.getMap().getRenderManager().getMapDisplay()).repaint();
            return;
        }


        Filter filter = (Filter) properties.get(ProjectBlackboardConstants.MAP__RENDERING_FILTER);
        if( filter==null )
            return;
        properties.put(ProjectBlackboardConstants.MAP__RENDERING_FILTER, null);
        
        Envelope env=(Envelope) properties.get(EDIT_FEATURE_BOUNDS);
        properties.put(EDIT_FEATURE_BOUNDS, null);
        selectedLayer.refresh(env);
    }
    
    /**
     * Triggers a re-render that hides the features on the {@link EditBlackboard}.
     */
    public void hideSelectedFeatures( EditToolHandler handler, ILayer selectedLayer ) {
        Envelope env=new Envelope();

        Set<String> fids=new HashSet<String>();
        for( EditGeom geom : handler.getEditBlackboard(selectedLayer).getGeoms() ) {
            if( env.isNull() ){
                env.init(geom.getShell().getEnvelope() );
            }else{
                env.expandToInclude(geom.getShell().getEnvelope());
            }
            String fid = geom.getFeatureIDRef().get();
            if( fid != null ) {
            	fids.add(fid);
            }
        }
        EditUtils.instance.refreshLayer(selectedLayer, fids, env, true, true);
    }


    /**
     * Sets the rendering hint on the layer so that the feature is hidden if hidefeature is true.  If hidefeature is false then
     * the hint is reset all features are shown
     * 
     * <p>
     *  {@link #cancelHideSelection(ILayer)} should be called if the edit is canceled. 
     * </p>
     * @see #cancelHideSelection(ILayer)
     *
     * @param selectedLayer
     * @param feature
     * @param refreshBounds the area to refresh (should be the the area of the feature).  May be null to refresh entire area.  Envelope should be in
     * Layer coordinates.
     * @param hidefeature
     */
    public void refreshLayer(ILayer selectedLayer, SimpleFeature feature, Envelope refreshBounds, boolean forceRefresh, boolean hidefeature)  {
        Set<String> fids = Collections.singleton(feature.getID());
        refreshLayer(selectedLayer, fids, refreshBounds, forceRefresh, hidefeature);
    }

    /**
     * Sets the rendering hint on the layer so that the feature is hidden if hidefeature is true.  If hidefeature is false then
     * the hint is reset all features are shown
     * 
     * <p>
     *  {@link #cancelHideSelection(ILayer)} should be called if the edit is cancelled. 
     * </p>
     * @see #cancelHideSelection(ILayer)
     *
     * @param selectedLayer the currently selected layer
     * @param fids the SimpleFeature Ids of the features that have been selected
     * @param refreshBounds the area to refresh (should be the the area of the features).  May be null to refresh entire area.  Envelope should be in
     * Layer coordinates.
     * @param hidefeature if true then the features are hidden otherwise they will be shown again.
     */
    public void refreshLayer( ILayer selectedLayer, Set<String> fids, Envelope refreshBounds, boolean forceRefresh, boolean hidefeature ) {
        if( selectedLayer==null )
            return;
        IBlackboard properties = selectedLayer.getBlackboard();
        if( !PreferenceUtil.instance().hideSelectedLayers() ){
            properties.put(ProjectBlackboardConstants.MAP__RENDERING_FILTER, null);
            if(forceRefresh || !refreshBounds.isNull()){
            	selectedLayer.refresh(refreshBounds);
            }
            ((ViewportPane) selectedLayer.getMap().getRenderManager().getMapDisplay()).repaint();
            return;
        }
        
        if( !forceRefresh && fids.isEmpty() )
            return;
        
        FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        boolean modified=false;
        for( String fid : fids ) {
            if ( fid == null )
                continue;
            if( hidefeature ){
                setAffectedArea(refreshBounds, properties);
                
                modified = addFidToExcludeFilter(properties, fid, filterFactory) || modified;
            }else{
                //get area to refresh and refresh it
                modified = removeFidFromExcludeFilter(properties, fid, filterFactory) || modified;            
            }
        }

        if( (refreshBounds!=null || forceRefresh) && modified )
            selectedLayer.refresh(refreshBounds);
    }

    private boolean removeFidFromExcludeFilter( IBlackboard properties, String fid, FilterFactory filterFactory ) {
        Filter filter = (Filter) properties.get(ProjectBlackboardConstants.MAP__RENDERING_FILTER);
        Filter f=(Filter) Filter.EXCLUDE;
        boolean modified=false;
        if( filter instanceof Id ){
            Id fidFilter=(Id) filter;
            Set<Identifier> ids = new HashSet<Identifier>(fidFilter.getIdentifiers());
            for (Iterator<Identifier> iter = ids.iterator(); iter.hasNext();) {
				Identifier element = (Identifier) iter.next();
				Object id = element.getID();
				if ( id.equals(fid) ){
					iter.remove();
					break;
				}
					
			}
            f=filterFactory.id(ids);
            if( fidFilter.getIDs().toArray(new String[0]).length==0 )
                f=(Filter) Filter.EXCLUDE;
            
            modified=true;
        }else{
            if( filter!=null ){
                f=filterFactory.id(FeatureUtils.stringToId(filterFactory, fid));
                f = filterFactory.not(f);
                f = filterFactory.or(f, filter);
                modified=true;
            }

        }
        if( f==Filter.EXCLUDE )
            f=null;
        properties.put(ProjectBlackboardConstants.MAP__RENDERING_FILTER, f);

        return modified;
    }

    private boolean addFidToExcludeFilter( IBlackboard properties, String fid, FilterFactory filterFactory ) {
        //get area to refresh and refresh it
        Filter filter = (Filter) properties.get(ProjectBlackboardConstants.MAP__RENDERING_FILTER);

        Filter f;
        boolean modified=false;
        if( filter instanceof Id ){
        	Id fidFilter=(Id) filter;
            Set<Identifier> ids = new HashSet<Identifier>(fidFilter.getIdentifiers());
            ids.add(filterFactory.featureId(fid));
            f=filterFactory.id(ids);
            modified=true;
        }else{
            f = filterFactory.id(FeatureUtils.stringToId(filterFactory,fid));
            
            if( filter!=null ){
            	f = filterFactory.or(f, filter);
            }
            modified=true;
        }
        properties.put(ProjectBlackboardConstants.MAP__RENDERING_FILTER, f);
        
        return modified;
    }

    /**
     * caches the bounds of all the refresh areas for {@link #cancelHideSelection(ILayer)} so it knows what area to refresh
     *
     * @param refreshBounds
     * @param properties
     */
    private void setAffectedArea( Envelope refreshBounds, IBlackboard properties ) {
        Envelope bounds=(Envelope) properties.get(EDIT_FEATURE_BOUNDS);
        if( refreshBounds==null ){
            bounds=null;
        }else{
            if( bounds==null )
                bounds=new Envelope(refreshBounds);
            else{
                bounds.expandToInclude(refreshBounds);
            }
        }
        
        properties.put(EDIT_FEATURE_BOUNDS, bounds);
    }

    /**
     * Returns the Geometry from the collection that the mouse is over/intersects
     *
     * @param geoms Geoms to search through
     * @param location the location 
     * @return the first geom that the location is over/intersects
     */
    public EditGeom getGeomWithMouseOver( Collection<EditGeom> geoms, Point location, boolean treatUnknownAsPolygon) {
        EditGeom over=geoms.iterator().next();
        for( EditGeom geom : geoms ) {
            PrimitiveShapeIterator iter=PrimitiveShapeIterator.getPathIterator(geom.getShell());
            if( iter.toShape().contains(location.getX(), location.getY()) ){
                over=geom;
                break;
            }
            ClosestEdge edge = geom.getShell().getClosestEdge(location, treatUnknownAsPolygon);
            if (edge != null && 
                    edge.getDistanceToEdge() <= PreferenceUtil.instance().getVertexRadius()){
                over=geom;
                break;
            }
        }
        return over;
    }
    
    /**
     * Returns true if the shape has a self intersection.   
     * Only checks the points not the coordinates there for it is quicker but less accurate.
     *
     * @param shape shape to test.
     * @return true if the shape has a self intersection.
     */

    public boolean selfIntersection( PrimitiveShape shape ) {
        if( shape.getNumPoints()<3 )
            return false;
        for( int i=1; i<shape.getNumPoints(); i++ ){
            Point last = shape.getPoint(i-1);
            Point current=shape.getPoint(i);
            if( intersection(last, current, shape, i, shape.getNumPoints()-1, false))
                return true;
        }
        
        return false;
    }

    /**
     * Checks whether the edge from point1 to point2 intersects any edge in the shape from startIndex to the endIndex 
     *
     * @param point1 the first point in the reference edge
     * @param point2 the second point in the reference edge
     * @param shape the shape that is searched for intersections 
     * @param startIndex the index in the shape of the point at which to start searching.  The point indicated will be the
     * first point in the edge.
     * @param endIndex the index to stop the search.  It is the index of the end point of the last edge to compare
     * @return true if there is an intersection between the edge indicated by last,current and the shape
     */
    public boolean intersection( Point point1, Point point2, PrimitiveShape shape, int startIndex, int endIndex ){
        return intersection(point1, point2, shape, startIndex, endIndex, true);
    }
    
    /**
     * Checks whether the edge from point1 to point2 intersects any edge in the shape from startIndex to the endIndex 
     *
     * @param point1 the first point in the reference edge
     * @param point2 the second point in the reference edge
     * @param shape the shape that is searched for intersections 
     * @param startIndex the index in the shape of the point at which to start searching.  The point indicated will be the
     * first point in the edge.
     * @param endIndex the index to stop the search.  It is the index of the end point of the last edge to compare
     * @param referenceLineIntersections if true then if a line crosses one of the endpoints of the reference line
     * it is not considered a intersection since it is the connecting point to the rest of the shape.
     * @return true if there is an intersection between the edge indicated by last,current and the shape
     */
    private boolean intersection( Point point1, Point point2, PrimitiveShape shape, int startIndex, int endIndex,
            boolean referenceLineIntersections) {
        
        
        for( int j=startIndex+1; j<endIndex+1; j++ ){
            Point last2 = shape.getPoint(j-1);
            Point current2 = shape.getPoint(j);
            if( last2.equals(point1) )
                continue; // same edge so continue.
            
            if( linesParallel(point1,point2,last2,current2) ){
                if( sameDirection(point1,point2, last2,current2) && last2.equals(point2) ){
                    return true;
                }else{
                    // no intersection
                    continue;
                }
            }
            
            Point intersectingLines = intersectingLines(point1, point2, last2, current2);
            if( intersectingLines!=null  ){
                Point endPoint2;
                Point endPoint1;
                if( referenceLineIntersections ){
                    endPoint1=point1;
                    endPoint2=point2;
                }else{
                    endPoint1=last2;
                    endPoint2=current2;
                }
                if (!intersectingLines.equals(endPoint1) && !intersectingLines.equals(endPoint2))
                    return true;
            }
        }
        
        return false;
    }
    private boolean sameDirection( Point last, Point current, Point last2, Point current2 ) {
        int dx1 = last.getX()-current.getX();
        int dy1 = last.getY()-current.getY();
        int dy2 = last2.getY()-current2.getY();
        int dx2 = last2.getX()-current2.getX();
        double length1 = Math.sqrt(dx1*dx1+dy1*dy1);
        double length2 = Math.sqrt(dx2*dx2+dy2*dy2);
        
        if( dx1/length1==dx2/length2 && dy1/length1==dy2/length2){
            return false;
        }
        return true;
    }

    private boolean linesParallel( Point line1P1, Point line1P2, Point line2P1, Point line2P2 ) {
        int B1 = line1P1.getX()-line1P2.getX();
        int B2 = line2P1.getX()-line2P2.getX();
        int A1 = line1P2.getY()-line1P1.getY();
        int A2 = line2P2.getY()-line2P1.getY();
        
        double det = A1*B2 - A2*B1;
        if(det == 0){
            //Lines are parallel
            return true;
        }
        return false;
    }
}
