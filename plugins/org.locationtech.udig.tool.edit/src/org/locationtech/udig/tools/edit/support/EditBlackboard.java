/**
 * 
 */
package org.locationtech.udig.tools.edit.support;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.preferences.PreferenceUtil;
import org.locationtech.udig.tools.edit.support.EditBlackboardEvent.EventType;
import org.locationtech.udig.tools.edit.support.PrimitiveShape.Mutator;

import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Maps between each pixel to a list of coordinates. Also provides convenience methods to move all
 * coordinates at a location
 * 
 * @author jeichar
 */
public class EditBlackboard {

    protected Map<Point, List<LazyCoord>> coordMapping;

    Map<Point, Set<EditGeom>> geomMapping;

    private final List<EditGeom> geometries;

    private Set<EditBlackboardListener> listeners = Collections
            .synchronizedSet(new HashSet<EditBlackboardListener>());

    private Selection selection = new Selection(this);

    private volatile int height;

    private volatile int width;

    private boolean collapseVertices;

    PointCoordCalculator pointCoordCalculator;

    private volatile int batchingEvents = 0;

    private List<EditBlackboardEvent> batchedEvents = new LinkedList<EditBlackboardEvent>();

    /**
     * Creates a PixelCoordMap. A default GeomShape will be created and can be obtained by
     * {@link #getGeoms()}.get(0)
     * 
     * @param width width of the current ViewportPane
     * @param height height of the current ViewportPane
     * @param toScreen transform to take the coordinates to a screen location.
     * @param layerToMap transform to transform coordinates from a layer's CRS to the map's CRS
     * @param coords coordinates that need to be mapped.
     */
    public EditBlackboard(int width, int height, AffineTransform toScreen,
            MathTransform layerToMap) {
        collapseVertices = true;
        this.width = width;
        this.height = height;
        coordMapping = new HashMap<Point, List<LazyCoord>>();
        geomMapping = new HashMap<Point, Set<EditGeom>>();
        pointCoordCalculator = new PointCoordCalculator(toScreen, layerToMap);
        geometries = new ArrayList<EditGeom>();

        geometries.add(new EditGeom(this, null));
    }

    /**
     * Returns the list of Listeners so that listeners can be added and removed. This is thread
     * safe. Each Listener can only be added once.
     * 
     * @return Returns the listeners.
     */
    public Set<EditBlackboardListener> getListeners() {
        return listeners;
    }

    /**
     * Adds a Geometry to the contained geometries. If the Geometry is a MultiGeometry it will not
     * be in the mapping only the sub geometries will be in the geometry.
     * 
     * @param geom the geometry that will be added.
     * @param featureId The id of the feature the geometry was part of. Maybe null.
     */
    public Map<Geometry, EditGeom> setGeometries(Geometry geom, String featureId) {
        Map<Geometry, EditGeom> mapping = null;
        ArrayList<EditGeom> old = new ArrayList<EditGeom>(geometries);
        synchronized (this) {
            geometries.clear();
            coordMapping.clear();
            geomMapping.clear();
            mapping = new HashMap<Geometry, EditGeom>();
            doAddGeometry(geom, mapping, featureId);
            if (EditPlugin.isDebugging(EditPlugin.RUN_ASSERTIONS)) {
                for (EditGeom geom2 : mapping.values()) {
                    geom2.assertValid();
                }
            }
        }
        if (mapping != null && !mapping.isEmpty())
            notify(new EditBlackboardEvent(this, this, EventType.SET_GEOMS, old,
                    new ArrayList<EditGeom>(geometries)));
        return mapping;

    }

    /**
     * Adds a Geometry to the contained geometries. If the Geometry is a MultiGeometry it will not
     * be in the mapping only the sub geometries will be in the geometry.
     * 
     * @param geom the geometry that will be added.
     * @param featureID The id of the feature the geometry was part of. Maybe null.
     */
    public Map<Geometry, EditGeom> addGeometry(Geometry geom, String featureID) {
        List<EditGeom> unmodifiableList = null;
        Map<Geometry, EditGeom> mapping = null;
        synchronized (this) {
            mapping = new HashMap<Geometry, EditGeom>();
            doAddGeometry(geom, mapping, featureID);
            unmodifiableList = Collections
                    .unmodifiableList(new ArrayList<EditGeom>(mapping.values()));
            if (EditPlugin.isDebugging(EditPlugin.RUN_ASSERTIONS)) {
                for (EditGeom geom2 : mapping.values()) {
                    geom2.assertValid();
                }
            }
        }
        if (unmodifiableList != null && !unmodifiableList.isEmpty())
            notify(new EditBlackboardEvent(this, this, EditBlackboardEvent.EventType.ADD_GEOMS,
                    null, unmodifiableList));
        return mapping;
    }

    /**
     * Adds a Coordinate the nearest edge .
     * <p>
     * If two edges are equidistant the first edge in the geometry will be targeted edge
     * </p>
     * 
     * @param x the x screen location to place the new coordinate
     * @param y the x screen location to place the new coordinate
     * @param geom the geometry to append the point to.
     * @param treatUnknownAsPolygon declares whether to treat geometries of type UNKNOWN as a
     *        polygon
     * @returns The edge that the point was added to.
     */
    public ClosestEdge addToNearestEdge(int x, int y, EditGeom geom, boolean treatUnknownAsPolygon)
            throws IllegalArgumentException {
        EditBlackboardEvent editBlackboardEvent = null;
        ClosestEdge geomClosest = null;
        synchronized (this) {
            if (!geometries.contains(geom))
                throw new IllegalArgumentException(
                        "EditBlackboard does not contain EditGeom " + geom); //$NON-NLS-1$
            Point p = Point.valueOf(x, y);
            geomClosest = geom.getClosestEdge(p, treatUnknownAsPolygon);
            Coordinate toCoord = toCoord(p);
            LazyCoord lazyCoord = doInsertCoord(p, toCoord, geomClosest.indexOfPrevious + 1,
                    geomClosest.part);

            geom.assertValid();
            editBlackboardEvent = new EditBlackboardEvent(this, geomClosest.part,
                    EventType.ADD_POINT, null, p);
            editBlackboardEvent.privateData = lazyCoord;
        }
        if (editBlackboardEvent != null)
            notify(editBlackboardEvent);
        return geomClosest;
    }

    /**
     * Adds a Coordinate the nearest edge .
     * <p>
     * If two edges of the same geometry are equidistant the first edge in the geometry will be
     * targeted edge
     * </p>
     * <p>
     * If two edges of different geometries are equidistant both will have a vertex added.
     * </p>
     * <p>
     * If the vertex would end up being added to the same location as an existing vertex then no
     * vertext is added
     * </p>
     * 
     * @param x the x screen location to place the new coordinate
     * @param y the x screen location to place the new coordinate
     * @param treatUnknownAsPolygon declares whether to treat geometries of type UNKNOWN as a
     *        polygon
     * @return the list of GeomShapes that had the coordinate added to.
     */
    public List<ClosestEdge> addToNearestEdge(int x, int y, boolean treatUnknownAsPolygon)
            throws IllegalArgumentException {
        EditBlackboardEvent editBlackboardEvent;
        List<ClosestEdge> candidates;
        synchronized (this) {
            candidates = getCandidates(x, y, treatUnknownAsPolygon);
            Point p = Point.valueOf(x, y);
            Coordinate toCoord = toCoord(p);
            if (candidates.size() == 0) {
                EditGeom editGeom = getGeoms().get(0);
                LazyCoord lazyCoord = doAddCoord(p, toCoord, editGeom.getShell());
                editGeom.getShell().assertValid();
                editBlackboardEvent = new EditBlackboardEvent(this, editGeom.getShell(),
                        EventType.ADD_POINT, null, p);
                editBlackboardEvent.privateData = lazyCoord;
            } else {
                Map<EditGeom, PrimitiveShape> changed = new HashMap<EditGeom, PrimitiveShape>();
                for (ClosestEdge edge : candidates) {
                    EditGeom geom = edge.shape;

                    doInsertCoord(p, toCoord, edge.indexOfPrevious + 1, edge.part);
                    changed.put(geom, edge.part);

                    edge.part.assertValid();
                }
                editBlackboardEvent = new EditBlackboardEvent(this, changed.values(),
                        EventType.ADD_POINT_TO_MANY, null, p);
            }
        }
        if (editBlackboardEvent != null)
            notify(editBlackboardEvent);
        return candidates;
    }

    /**
     * Adds a Coordinate at the x,y location on the screen to the end of the geomtry. Closure of the
     * hole is not enforced.
     * 
     * @param x the x screen location to place the new coordinate
     * @param y the x screen location to place the new coordinate
     * @param holeIndex the hole to append to.
     * @return
     * @return added coordinate. <b>Do not modify coordinate otherwise the state of this class will
     *         be wrong and bugs will occur in the code.</b>
     */
    public Coordinate addPoint(int x, int y, PrimitiveShape shape) throws IllegalArgumentException {
        LazyCoord lazyCoord = null;
        EditBlackboardEvent editBlackboardEvent = null;
        synchronized (this) {
            if (!geometries.contains(shape.getEditGeom()))
                throw new IllegalArgumentException("Blackboard does not contain shape:" + shape); //$NON-NLS-1$

            Point point = Point.valueOf(x, y);
            Coordinate coord = toCoord(point);
            lazyCoord = doAddCoord(point, coord, shape);
            shape.assertValid();
            editBlackboardEvent = new EditBlackboardEvent(this, shape, EventType.ADD_POINT, null,
                    point);
            editBlackboardEvent.privateData = lazyCoord;
        }
        if (editBlackboardEvent != null)
            notify(editBlackboardEvent);
        return lazyCoord;
    }

    /**
     * Insert a coordinate into the provided shape.
     * 
     * @param coord
     * @param shape
     * @return Point at which coordinate was added
     */
    public Point addCoordinate(Coordinate coord, PrimitiveShape shape) {
        return insertCoordinate(coord, shape.getNumPoints(), shape);
    }

    /**
     * Insert a coordinate into the provided shape.
     * 
     * @param coord
     * @param index
     * @param shape // * @return Point at which coordinate was added
     */
    public Point insertCoordinate(Coordinate coord, int index, PrimitiveShape shape) {
        EditBlackboardEvent editBlackboardEvent = null;
        Point p = null;
        synchronized (this) {
            if (!geometries.contains(shape.getEditGeom()))
                throw new IllegalArgumentException("Blackboard does not contain shape:" + shape); //$NON-NLS-1$
            p = toPoint(coord);
            LazyCoord lazyCoord = doInsertCoord(p, coord, index, shape);
            shape.assertValid();
            editBlackboardEvent = new EditBlackboardEvent(this, shape, EventType.ADD_POINT, null,
                    p);
            editBlackboardEvent.privateData = lazyCoord;
        }
        if (editBlackboardEvent != null)
            notify(editBlackboardEvent);

        return p;
    }

    /**
     * Adds a Coordinate at the index indicated.
     * <p>
     * The index is the position in the coordinate list in the shell of the geometry.
     * </p>
     * 
     * @param x the x screen location to place the new coordinate
     * @param y the x screen location to place the new coordinate
     * @param pointIndex the index in terms of points in the shape to add the coordinate.
     * @param shape The shape to add the point to
     * @return added coordinate. <b>Do not modify coordinate otherwise the state of this class will
     *         be wrong and bugs will occur in the code.</b>
     */
    public Coordinate insertCoord(int x, int y, int pointIndex, PrimitiveShape shape)
            throws IllegalArgumentException {
        EditBlackboardEvent editBlackboardEvent = null;
        Coordinate coord = null;
        synchronized (this) {
            if (!geometries.contains(shape.getEditGeom())) {
                throw new IllegalArgumentException("Blackboard does not contain shape:" + shape); //$NON-NLS-1$
            }
            Point point = Point.valueOf(x, y);
            coord = toCoord(point);
            LazyCoord lazyCoord = doInsertCoord(point, coord, pointIndex, shape);
            shape.assertValid();
            editBlackboardEvent = new EditBlackboardEvent(this, shape, EventType.ADD_POINT, null,
                    point);
            editBlackboardEvent.privateData = lazyCoord;
        }
        if (editBlackboardEvent != null)
            notify(editBlackboardEvent);
        return coord;
    }

    /**
     * Inserts the list of coordinates into the shape at the index indicated.
     * 
     * @param pointIndex the location that the coordinates will be inserted.
     * @param coords coords that will inserted.insertCoords
     * @param shape the shape that will have the coords inserted.
     */
    public void insertCoords(int pointIndex, Point p, List<Coordinate> coords,
            PrimitiveShape shape) {
        List<LazyCoord> lazyCoords = null;
        synchronized (this) {
            if (!geometries.contains(shape.getEditGeom()))
                throw new IllegalArgumentException("Blackboard does not contain shape:" + shape); //$NON-NLS-1$
            if (coords.isEmpty())
                throw new IllegalArgumentException("Coordinates cannot be empty"); //$NON-NLS-1$
            lazyCoords = shape.getMutator().addPoint(pointIndex, p, coords);
            List<LazyCoord> mappedCoords = coordMapping.get(p);
            if (mappedCoords == null) {
                mappedCoords = new ArrayList<LazyCoord>();
                coordMapping.put(p, mappedCoords);
            }
            mappedCoords.addAll(lazyCoords);
            Set<EditGeom> geoms = geomMapping.get(p);
            if (geoms == null) {
                geoms = new HashSet<EditGeom>();
                geomMapping.put(p, geoms);
            }
            if (!geoms.contains(shape.getEditGeom()))
                geoms.add(shape.getEditGeom());

            if (EditPlugin.isDebugging(EditPlugin.RUN_ASSERTIONS)) {
                for (EditGeom geom : geoms) {
                    geom.assertValid();
                }
            }

        }
        EditBlackboardEvent editBlackboardEvent = new EditBlackboardEvent(this, shape,
                EventType.ADD_POINT, null, p);

        editBlackboardEvent.privateData = lazyCoords;
        if (lazyCoords != null && !lazyCoords.isEmpty())
            notify(editBlackboardEvent);
    }

    /**
     * Returns the GeomShapes that will have the coordinate added if
     * {@linkplain #addToNearestEdge(int, int)} is called.
     * 
     * @param x the x screen location to place the new coordinate
     * @param y the x screen location to place the new coordinate
     * @param treatUnknownAsPolygon declares whether to treat geometries of type UNKNOWN as a
     *        polygon
     * @return the GeomShapes that will have the coordinate added if
     *         {@linkplain #addToNearestEdge(int, int)} is called.
     */
    public synchronized List<ClosestEdge> getCandidates(int x, int y,
            boolean treatUnknownAsPolygon) {
        Point p = Point.valueOf(x, y);
        List<EditGeom> geoms = getGeoms();
        List<ClosestEdge> candidates = new ArrayList<ClosestEdge>();
        int closest = Integer.MAX_VALUE;
        List<ClosestEdge> closestDistances = new ArrayList<ClosestEdge>();

        for (int i = 0; i < geoms.size(); i++) {
            ClosestEdge geomClosest = geoms.get(i).getClosestEdge(p, treatUnknownAsPolygon);
            if (geomClosest == null)
                continue;
            if (geomClosest.distanceToEdge < closest)
                closest = (int) geomClosest.distanceToEdge;
            closestDistances.add(geomClosest);
        }

        for (ClosestEdge edge : closestDistances) {
            if (((int) edge.distanceToEdge) == closest
                    && !edge.part.getMutator().hasPoint(edge.pointOnLine)) {
                candidates.add(edge);
            }
        }

        return candidates;
    }

    /**
     * returns the list of coordinates at location:(x,y) <b>TREAT COORDINATES AS IMMUTABLE!!</b>
     * <p>
     * This is a dangerous method because the coordinates are mutable but if they are modified
     * outside of this class then the model is messed up and crazy bugs will happen. The weakness is
     * permitted for performance reasons. I'm trusting people to not be stupid.
     * </p>
     * 
     * @return the list of coordinates at location:(x,y). <b>TREAT COORDINATES AS IMMUTABLE!!</b>
     */
    public synchronized List<Coordinate> getCoords(int x, int y) {
        Point point = Point.valueOf(x, y);
        List<LazyCoord> list = coordMapping.get(point);
        if (list == null)
            return Collections.<Coordinate> emptyList();
        return new CoordResolvingList(list, point);
    }

    /**
     * The list of geometries mapped.
     * 
     * @return list of geometries mapped.
     */
    public synchronized List<EditGeom> getGeoms() {
        return Collections.unmodifiableList(new ArrayList<EditGeom>(geometries));
    }

    /**
     * Returns a list of the geometries with vertices at the position (x,y). Edges are not
     * calculated.
     * 
     * @param x x coordinate of point
     * @param y x coordinate of point
     * @return
     * @return a list of the geometries at the position (x,y)
     */
    public synchronized List<EditGeom> getGeoms(int x, int y) {
        Set<EditGeom> geoms = geomMapping.get(Point.valueOf(x, y));
        if (geoms == null)
            return Collections.<EditGeom> emptyList();
        return new ArrayList<EditGeom>(geoms);
    }

    /**
     * Moves all the coordinates at location by an offset of (deltaX, deltaY).
     * 
     * @param x x coordinate of coords to move
     * @param y y coordinate of coords to move
     * @param deltaX the number of pixels to move coords in the positive x direction(Screen space)
     * @param deltaY the number of pixels to move coords in the positive y direction(Screen space)
     */
    public List<Coordinate> moveCoords(int x, int y, int endX, int endY) {

        Set<PrimitiveShape> changed = new HashSet<PrimitiveShape>();
        List<LazyCoord> coords = null;
        Point start = Point.valueOf(x, y);
        Point end = Point.valueOf(endX, endY);
        synchronized (this) {

            if (start.equals(end))
                return Collections.<Coordinate> emptyList();

            coords = coordMapping.remove(start);
            if (coords == null || coords.size() == 0)
                return Collections.<Coordinate> emptyList();

            List<LazyCoord> endCoords = coordMapping.get(end);

            if (endCoords != null) {
                endCoords.addAll(coords);
            } else {
                coordMapping.put(end, coords);
            }

            Set<EditGeom> startGeoms = geomMapping.get(start);
            Set<EditGeom> endGeoms = geomMapping.get(end);

            endGeoms = endGeoms == null ? new HashSet<EditGeom>() : endGeoms;
            for (EditGeom geom : startGeoms) {
                geom.setChanged(true);
                if (!endGeoms.contains(geom))
                    endGeoms.add(geom);
                for (PrimitiveShape hole : geom) {
                    for (ListIterator<Point> iter = hole.getMutator().iterator(); iter.hasNext();) {
                        Point p = iter.next();
                        if (p.equals(start)) {
                            changed.add(hole);
                            for (LazyCoord coord : coords) {
                                if (hole.hasVertex(p, coord)) {
                                    iter.set(end);
                                    break;
                                }

                            }
                        }
                    }
                }
            }

            geomMapping.put(end, endGeoms);
            geomMapping.remove(start);
            if (EditPlugin.isDebugging(EditPlugin.RUN_ASSERTIONS))
                for (PrimitiveShape shape : changed) {
                    shape.assertValid();
                }
        }
        if (!changed.isEmpty())
            notify(new EditBlackboardEvent(this, changed, EventType.MOVE_POINT, start, end));
        return new CoordResolvingList(coords, end);
    }

    /**
     * Moves the selection
     * 
     * @param diffX the distance to move the geometry in the x direction. The delta is in pixels.
     * @param diffY the distance to move the geometry in the y direction. The delta is in pixels.
     * @param selection the selection to move.
     */
    public void moveSelection(int diffX, int diffY, Selection selection) {

        if (selection instanceof EditGeomSelection) {
            moveGeom(diffX, diffY, ((EditGeomSelection) selection).getGeom());
        } else {
            doMoveSelection(diffX, diffY, selection);
        }
    }

    private void doMoveSelection(int diffX, int diffY, Selection selection2) {
        if (diffX == 0 && diffY == 0)
            return;

        if (selection.size() == 0)
            return;

        startBatchingEvents();
        try {
            synchronized (this) {
                // we need to order the points such that
                // if there exist two points such that
                // point 1 has the end point x and point 2 has the start point x
                // point 1 must come after point 2
                // no circles will build since we have same movement on all points
                List<Point> orderedStartPointList = new LinkedList<Point>();
                List<Point> orderedEndPointList = new LinkedList<Point>();
                // TODO IBK: ZZ faster sort operation
                for (Point start : selection) {
                    Point end = Point.valueOf(start.getX() + diffX, start.getY() + diffY);
                    int findIndexEnd = orderedStartPointList.lastIndexOf(end);
                    int findIndexStart = orderedEndPointList.indexOf(start);
                    if (findIndexEnd == -1 && findIndexStart == -1) {
                        orderedStartPointList.add(start);
                        orderedEndPointList.add(end);
                    } else if (findIndexStart == -1) {
                        orderedStartPointList.add(findIndexEnd + 1, start);
                        orderedEndPointList.add(findIndexEnd + 1, end);
                    } else if (findIndexEnd == -1) {
                        orderedStartPointList.add(findIndexStart, start);
                        orderedEndPointList.add(findIndexStart, end);
                    } // last case should never happen (see above)

                }

                for (Point start : orderedStartPointList) {
                    Collection<LazyCoord> coords = selection.getLazyCoordinates(start);
                    Point end = Point.valueOf(start.getX() + diffX, start.getY() + diffY);
                    List<LazyCoord> endCoords = coordMapping.get(end);

                    coordMapping.get(start).removeAll(coords);
                    if (endCoords != null) {
                        endCoords.addAll(coords);
                    } else {
                        coordMapping.put(end, new LinkedList<LazyCoord>(coords));
                    }

                    Set<EditGeom> startGeoms = geomMapping.get(start);
                    Set<EditGeom> endGeoms = geomMapping.get(end);
                    /*
                     * ProjectPlugin.log("start("+start+"): "+startGeoms);
                     * ProjectPlugin.log("end("+end+"): "+endGeoms);
                     */
                    Set<PrimitiveShape> changed = new HashSet<PrimitiveShape>();

                    if (startGeoms == null)
                        continue;

                    endGeoms = endGeoms == null ? new HashSet<EditGeom>() : endGeoms;
                    Set<EditGeom> toRemove = new HashSet<EditGeom>();
                    Set<EditGeom> toAdd = new HashSet<EditGeom>();
                    for (Iterator<EditGeom> geomIter = startGeoms.iterator(); geomIter.hasNext();) {
                        EditGeom geom = geomIter.next();
                        geom.setChanged(true);

                        for (PrimitiveShape shape : geom) {

                            for (Iterator<Point> shapeIter = shape.getMutator()
                                    .getCopyIterator(); shapeIter.hasNext();) {
                                Point p = shapeIter.next();
                                if (p.equals(start)) {
                                    toAdd.add(geom);
                                    for (LazyCoord coord : coords) {
                                        if (shape.hasVertex(p, coord)) {
                                            changed.add(shape);
                                            shape.getMutator().move(start, end, coord);
                                        }

                                    }
                                }
                            }
                            if (shape.getMutator().getLazyCoordsAt(start).isEmpty())
                                toRemove.add(geom);
                        }

                        if (!changed.isEmpty())
                            notify(new EditBlackboardEvent(this, changed, EventType.MOVE_POINT,
                                    start, end));
                    }
                    endGeoms.addAll(toAdd);
                    startGeoms.removeAll(toRemove);
                    geomMapping.put(end, endGeoms);
                    if (EditPlugin.isDebugging(EditPlugin.RUN_ASSERTIONS))
                        for (EditGeom geom : endGeoms) {
                            geom.assertValid();
                        }

                }
            }
        } finally {
            fireBatchedEvents();
        }

    }

    /**
     * Moves a geometry deltaX, deltaY pixels.
     * 
     * @param deltaX the distance to move the geometry in the x direction. The delta is in pixels.
     * @param deltaY the distance to move the geometry in the y direction. The delta is in pixels.
     * @param geom the geometry to move.
     */
    private void moveGeom(int deltaX, int deltaY, EditGeom geom) {
        if (!geometries.contains(geom))
            throw new IllegalArgumentException("Blackboard does not contain EditGeom:" + geom); //$NON-NLS-1$

        if (deltaX == 0 && deltaY == 0)
            return;
        synchronized (this) {
            Set<Point> moved = new HashSet<Point>();
            Map<Point, List<LazyCoord>> newCoordMapping = new HashMap<Point, List<LazyCoord>>();

            startBatchingEvents();
            for (PrimitiveShape shape : geom) {
                for (Point point : shape) {
                    if (moved.contains(point))
                        continue;

                    moved.add(point);
                    Point destPoint = Point.valueOf(point.getX() + deltaX, point.getY() + deltaY);

                    List<LazyCoord> coords = shape.getMutator().getLazyCoordsAt(point);
                    coordMapping.get(point).removeAll(coords);

                    newCoordMapping.put(destPoint, coords);

                    geomMapping.get(point).remove(geom);

                    notify(new EditBlackboardEvent(this, Collections.singleton(shape),
                            EventType.MOVE_POINT, point, destPoint));
                }
            }

            for (PrimitiveShape shape : geom) {
                shape.getMutator().move(deltaX, deltaY);
            }

            for (Map.Entry<Point, List<LazyCoord>> entry : newCoordMapping.entrySet()) {
                List<LazyCoord> destCoords = coordMapping.get(entry.getKey());
                if (destCoords == null) {
                    destCoords = new ArrayList<LazyCoord>();
                    coordMapping.put(entry.getKey(), destCoords);
                }
                destCoords.addAll(entry.getValue());

                Set<EditGeom> destGeoms = geomMapping.get(entry.getKey());
                if (destGeoms == null) {
                    destGeoms = new HashSet<EditGeom>();
                    destGeoms.add(geom);
                    geomMapping.put(entry.getKey(), destGeoms);
                } else {
                    if (!destGeoms.contains(geom))
                        destGeoms.add(geom);
                }
            }

            geom.assertValid();
            geom.setChanged(true);
        }
        fireBatchedEvents();
    }

    /**
     * Deletes all the coordinates at a location <b>TREAT COORDINATES AS IMMUTABLE!!</b>
     * 
     * @return the deleted coordinates. <b>TREAT COORDINATES AS IMMUTABLE!!</b>
     */
    public List<Coordinate> removeCoordsAtPoint(int x, int y) {
        Point p = Point.valueOf(x, y);
        HashSet<PrimitiveShape> changed = new HashSet<PrimitiveShape>();
        List<Coordinate> result = null;
        synchronized (this) {

            List<LazyCoord> coords = coordMapping.remove(p);

            result = coords == null ? Collections.<Coordinate> emptyList()
                    : new CoordResolvingList(coords, p);

            Set<EditGeom> geoms = geomMapping.remove(p);
            if (geoms == null)
                return Collections.<Coordinate> emptyList();
            for (EditGeom geom : geoms) {

                for (PrimitiveShape part : geom) {
                    Mutator mutator = part.getMutator();
                    if (mutator.remove(p))
                        changed.add(part);
                    while (mutator.remove(p))
                        ;
                }
            }

            if (EditPlugin.isDebugging(EditPlugin.RUN_ASSERTIONS)) {
                for (PrimitiveShape shape : changed) {
                    shape.assertValid();
                }
            }
        }
        if (!changed.isEmpty())
            notify(new EditBlackboardEvent(this, changed, EventType.REMOVE_POINT, p, null));
        return result;
    }

    /**
     * Deletes the coordinates at the provided point in the provided shape.
     * 
     * @param x x coordinate in screen coords
     * @param y y coordinate in screen coords
     * @param shape shape to delete from.
     */
    public void removeCoords(int x, int y, PrimitiveShape shape) {
        Point p = Point.valueOf(x, y);
        synchronized (this) {
            shape.getMutator().remove(p);
            Set<EditGeom> geoms = geomMapping.get(p);
            if (geoms.isEmpty()) {
                throw new IllegalStateException(
                        "for some reason there was no shape at the location"); //$NON-NLS-1$
            }
            boolean noMoreReferences = true;

            for (PrimitiveShape shape2 : shape.getEditGeom()) {
                if (!shape2.getMutator().getLazyCoordsAt(p).isEmpty()) {
                    noMoreReferences = false;
                    break;
                }
            }

            if (noMoreReferences) {
                geoms.remove(shape.getEditGeom());
            }
            shape.assertValid();
        }
        notify(new EditBlackboardEvent(this, Collections.singleton(shape), EventType.REMOVE_POINT,
                p, null));
    }

    /**
     * Removes a coordinate from the shape. The index indicates which point to remove, the Point
     * parameter is a check to ensure that the correct point is indicated, and the coord is the
     * coordinate that will be removed
     * 
     * @param pointIndex index of point in shape
     * @param coord coordinate to remove
     * @param shape shape to remove from
     */
    public void removeCoordinate(int pointIndex, Coordinate coord, PrimitiveShape shape) {

        if (!geometries.contains(shape.getEditGeom()))
            throw new IllegalArgumentException("Blackboard does not contain shape:" + shape); //$NON-NLS-1$

        Point toRemove = null;
        synchronized (this) {
            toRemove = shape.getPoint(pointIndex);

            Mutator mutator = shape.getMutator();
            LazyCoord removed = mutator.removePoint(pointIndex, coord);

            boolean occupiesPoint = false;

            for (Point point : mutator) {
                if (point.equals(toRemove))
                    occupiesPoint = true;
            }

            List<LazyCoord> coords = coordMapping.get(toRemove);
            for (Iterator<LazyCoord> iter = coords.iterator(); iter.hasNext();) {
                if (iter.next() == removed)
                    iter.remove();
            }
            if (coords.isEmpty()) {
                coordMapping.remove(toRemove);
            }

            if (!occupiesPoint) {
                Set<EditGeom> geoms = geomMapping.get(toRemove);
                geoms.remove(shape.getEditGeom());
                if (geoms.isEmpty())
                    geomMapping.remove(toRemove);
            }

            shape.assertValid();
        }
        if (toRemove != null)
            notify(new EditBlackboardEvent(this, Collections.singleton(shape),
                    EventType.REMOVE_POINT, toRemove, null));
    }

    public synchronized Coordinate toCoord(Point point) {
        return pointCoordCalculator.toCoord(point);
    }

    /**
     * Transforms a Coordinate into the point location it would occupy on the screen.
     * 
     * @param coord coordinate object
     * @return point coordinate would occupy on the screen.
     */
    public synchronized Point toPoint(Coordinate coord) {
        return pointCoordCalculator.toPoint(coord);
    }

    /**
     * Modifies the mapping of points to coordinates so that the transform passed in as a parameter
     * is the new toScreen transform.
     * 
     * @param transform new transform
     */
    public void setToScreenTransform(AffineTransform newToScreen) {
        if (newToScreen == null)
            return;
        AffineTransform oldToScreen = null;
        Map<Point, List<Point>> map = new HashMap<Point, List<Point>>();
        synchronized (this) {
            if (newToScreen.equals(pointCoordCalculator.toScreen))
                return;

            oldToScreen = new AffineTransform(pointCoordCalculator.toScreen);
            AffineTransform oldToWorld = new AffineTransform(pointCoordCalculator.toWorld);

            geomMapping.clear();
            coordMapping.clear();
            PointCoordCalculator calculator = new PointCoordCalculator(newToScreen,
                    pointCoordCalculator.layerToMap);
            for (EditGeom geom : geometries) {
                for (PrimitiveShape shape : geom) {
                    map.putAll(shape.getMutator().transform(oldToScreen, oldToWorld, calculator));
                }
            }

            pointCoordCalculator.toScreen.setTransform(newToScreen);
            pointCoordCalculator.toWorld.setTransform(calculator.toWorld);

            if (EditPlugin.isDebugging(EditPlugin.RUN_ASSERTIONS))
                for (EditGeom geom : geometries) {
                    geom.assertValid();
                }
        }
        notify(new EditBlackboardEvent(this, this, EventType.TRANFORMATION,
                new AffineTransform(oldToScreen), map));
    }

    public Selection getSelection() {
        return selection;
    }

    /**
     * Removes all EditGeometries from blackboard and places an empty one on the black board.
     */
    public void clear() {
        EditBlackboardEvent event = null;
        synchronized (this) {
            event = new EditBlackboardEvent(this, this, EventType.REMOVE_GEOMS,
                    new ArrayList<EditGeom>(geometries), null);
            coordMapping.clear();
            geometries.clear();
            geomMapping.clear();
            geometries.add(new EditGeom(this, null));
            selection.doClear();
        }
        notify(event);
    }

    /**
     * Removes all the geometries in the list from the blackboard and all the coordinates in the
     * geometries
     * 
     * @param geomsToRemove the list of geometries to remove.
     */
    public List<EditGeom> removeGeometries(Collection<EditGeom> geomsToRemove) {
        ArrayList<EditGeom> removed = new ArrayList<EditGeom>();
        synchronized (this) {
            for (EditGeom geom : geomsToRemove) {
                if (!geometries.contains(geom)) {
                    continue;
                }
                removed.add(geom);
                for (PrimitiveShape shape : geom) {
                    for (int i = 0; i < shape.getNumPoints(); i++) {
                        Point point = shape.getPoint(i);
                        List<LazyCoord> coords = shape.getMutator().getLazyCoordsAt(i);
                        coordMapping.get(point).removeAll(coords);
                        geomMapping.get(point).remove(geom);
                    }
                }
            }
            geometries.removeAll(geomsToRemove);
        }
        notify(new EditBlackboardEvent(this, this, EventType.REMOVE_GEOMS, removed, null));
        return removed;
    }

    /**
     * Creates a new EditGeom in the map. If there is only one geometry on the bb and it has no
     * points and has not been editted it will be removed.
     * 
     * @param featureId The id of the feature the geometry was part of. Maybe null.
     * @param type the type of geometry to create if null then the type will be unknown
     * @return the created geometry
     */
    public EditGeom newGeom(String featureId, ShapeType type) {
        EditGeom editGeom = new EditGeom(this, featureId);
        if (type != null)
            editGeom.setShapeType(type);
        synchronized (this) {
            if (!geometries.isEmpty() && !geometries.get(0).isChanged()
                    && geometries.get(0).getShell().getNumPoints() == 0)
                geometries.clear();
            geometries.add(editGeom);
        }
        notify(new EditBlackboardEvent(this, this, EventType.ADD_GEOMS, null,
                Collections.singletonList(editGeom)));
        return editGeom;
    }

    /**
     * @return Returns the height.
     */
    public synchronized int getHeight() {
        return height;
    }

    /**
     * @param height The height to set.
     */
    public synchronized void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return Returns the width.
     */
    public synchronized int getWidth() {
        return width;
    }

    /**
     * @param width The width to set.
     */
    public synchronized void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return Returns the collapseVertices.
     */
    public synchronized boolean isCollapseVertices() {
        return this.collapseVertices;
    }

    /**
     * @param collapseVertices The collapseVertices to set.
     */
    public synchronized void setCollapseVertices(boolean collapseVertices) {
        this.collapseVertices = collapseVertices;
    }

    /**
     * Returns the point closest to location. The search is a square of height and width radius + 1.
     * 
     * @param location the locations to start searching from.
     * @param radius the distance away from location to search.
     * @return the point closest to location or null if no point exists.
     */
    public Point overVertex(Point location, int radius) {
        return overVertex(location, radius, false);
    }

    /**
     * Returns the point closest to location. If ignore is true the point at locations will not be
     * returned. The search is a square of height and width radius + 1.
     * 
     * @param location the locations to start searching from.
     * @param radius radius the distance away from location to search.
     * @param ignore true if the vertex at location is ignored
     * @return the point closest to location or null if no point exists.
     */
    public synchronized Point overVertex(Point location, int radius, boolean ignore) {
        if (!ignore && getCoords(location.getX(), location.getY()).size() != 0)
            return location;
        for (int i = 1; i <= radius; i++) {
            Point result = findVertex(location, i);
            if (result != null)
                return result;
        }
        return null;
    }

    /**
     * Changes the behaviour of the EditBlackboard so that events are not fired but batched together
     * until {@linkplain #fireBatchedEvents()} is called.
     */
    public synchronized void startBatchingEvents() {
        batchingEvents++;
    }

    /**
     * Fires all the batched events and resets the EditBlackboard so that it no longer batches
     * events.
     */
    public void fireBatchedEvents() {
        List<EditBlackboardEvent> events;
        synchronized (this) {
            batchingEvents--;
            if (batchingEvents > 0)
                return;
            events = this.batchedEvents;
            this.batchedEvents = new LinkedList<EditBlackboardEvent>();
        }

        if (events.isEmpty())
            return;

        EditBlackboardListener[] l = listeners.toArray(new EditBlackboardListener[0]);
        startBatchingEvents();
        try {
            for (EditBlackboardListener listener : l) {
                listener.batchChange(events);
            }
        } finally {
            fireBatchedEvents();
        }
    }

    private LazyCoord doAddCoord(Point p, Coordinate c, PrimitiveShape hole) {
        int index = hole.getNumPoints();
        return doInsertCoord(p, c, index, hole);
    }

    private LazyCoord doInsertCoord(Point point, Coordinate c, int pointIndex,
            PrimitiveShape hole) {
        if (hole == null || c == null || point == null)
            throw new IllegalArgumentException(
                    "hole=" + hole + " coordIndex=" + pointIndex + " c=" + c + " p=" + point); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$

        // So that all the vertices don't make a big mess look around point in a 3x3 radius
        // if another point is there add to that point.
        Point overLappingPoint = null;
        if (collapseVertices)
            overLappingPoint = overVertex(point, PreferenceUtil.instance().getVertexRadius());

        Point p = overLappingPoint;
        if (p == null || !collapseVertices)
            p = point;

        Coordinate coord = new Coordinate(c);

        Collection<LazyCoord> added = hole.getMutator().addPoint(pointIndex, p,
                Collections.singletonList(coord));
        LazyCoord lazyCoord = added.iterator().next();
        Collection<EditGeom> geomList = updateMappings(p, lazyCoord);

        if (!geomList.contains(hole.getEditGeom()))
            geomList.add(hole.getEditGeom());
        return lazyCoord;
    }

    /**
     * Updates the coordMapping and geomMapping ensuring that there are lists at the Point and adds
     * c to the CoordMapping.
     */
    private Set<EditGeom> updateMappings(Point p, LazyCoord c) {
        List<LazyCoord> coordList = coordMapping.get(p);
        Set<EditGeom> geomList = geomMapping.get(p);
        if (coordList == null) {
            coordList = new ArrayList<LazyCoord>();
            geomList = new HashSet<EditGeom>();
            coordMapping.put(p, coordList);
            geomMapping.put(p, geomList);
        }
        coordList.add(c);
        return geomList;
    }

    private void doAddGeometry(Geometry geom, Map<Geometry, EditGeom> jtsEditGeomMapping,
            String featureID) {

        Envelope bbox = geom.getEnvelopeInternal();

        if (geom instanceof GeometryCollection) {
            int num = geom.getNumGeometries();
            for (int i = 0; i < num; i++) {
                doAddGeometry(geom.getGeometryN(i), jtsEditGeomMapping, featureID);
            }
        } else {
            EditGeom geomShape = new EditGeom(this, featureID, bbox);
            geomShape.initializing = true;
            geomShape.setShapeType(ShapeType.valueOf(geom));
            geometries.add(geomShape);
            jtsEditGeomMapping.put(geom, geomShape);

            if (geom instanceof Polygon) {
                Polygon poly = (Polygon) geom;
                addShell(poly.getExteriorRing(), geomShape);
                for (int i = 0, numHoles = poly.getNumInteriorRing(); i < numHoles; i++) {
                    addHole(poly.getInteriorRingN(i), geomShape, i);
                }
            } else {
                addShell(geom, geomShape);
            }
            geomShape.initializing = false;

            if (geomShape.isChanged())
                geomShape.setChanged(false);
        }
    }

    private void addHole(Geometry geom, EditGeom geomShape, int holeIndex) {
        PrimitiveShape hole;
        if (holeIndex == geomShape.getHoles().size()) {
            hole = geomShape.newHole();
        } else {
            hole = geomShape.getHoles().get(holeIndex);
        }

        Coordinate[] coords = geom.getCoordinates();
        for (int i = 0; i < coords.length; i++) {
            doAddCoord(toPoint(coords[i]), coords[i], hole);
        }
    }

    private void addShell(Geometry geom, EditGeom editGeom) {
        Coordinate[] coords = geom.getCoordinates();
        for (int i = 0; i < coords.length; i++) {
            doAddCoord(toPoint(coords[i]), coords[i], editGeom.getShell());
            editGeom.getShell().assertValid();
        }
    }

    void notify(EditBlackboardEvent event) {
        if (event == null)
            throw new NullPointerException();
        EditBlackboardListener[] l = null;
        synchronized (this) {
            if (batchingEvents > 0) {
                batchedEvents.add(event);
            } else {
                l = listeners.toArray(new EditBlackboardListener[0]);
            }
        }
        if (l != null) {
            for (EditBlackboardListener listener : l) {
                listener.changed(event);
            }
        }
    }

    /**
     * Searchs for a vertext in a square i pixels away from the location.
     * 
     * @param location center of search
     * @param i distance from center to search (is not an area search)
     * @param ignore
     * @param element
     */
    private Point findVertex(Point location, int i) {

        final int maxX = location.getX() + i;
        final int maxY = location.getY() + i;
        final int minX = location.getX() - i;
        final int minY = location.getY() - i;

        for (int x = minX; x <= maxX; x++) {
            if (getCoords(x, minY).size() > 0)
                return Point.valueOf(x, minY);
        }

        for (int y = minY + 1; y <= maxY; y++) {
            if (getCoords(maxX, y).size() > 0)
                return Point.valueOf(maxX, y);
        }

        for (int x = maxX - 1; x >= minX; x--) {
            if (getCoords(x, maxY).size() > 0)
                return Point.valueOf(x, maxY);
        }

        for (int y = maxY - 1; y >= minY; y--) {
            if (getCoords(minX, y).size() > 0)
                return Point.valueOf(minX, y);
        }

        return null;
    }

    public boolean selectionAdd(Point point) {
        return selection.doAdd(point);
    }

    public boolean selectionAddAll(Collection<Point> points) {
        return selection.doAddAll(points);
    }

    public void selectionClear() {
        selection.doClear();
    }

    public boolean selectionRemoveAll(Collection<Point> points) {
        return selection.doRemoveAll(points, true);
    }

    public boolean selectionRemove(Point point) {
        return selection.doRemove(point);
    }

    public boolean selectionRetainAll(Collection<Point> points) {
        return selection.doRetainAll(points);
    }

    /**
     * Returns true if the blackboard currently holds the noted feature ID.
     * 
     * @param fid SimpleFeature ID
     * @return boolean
     */
    public boolean contains(String fid) {
        List<EditGeom> geoms = getGeoms();
        for (EditGeom geom : geoms) {
            if (geom.getFeatureIDRef().toString().equals(fid))
                return true;
        }
        return false;
    }

    public void setMapLayerTransform(MathTransform mapToLayer) {
        AffineTransform oldToScreen = null;
        Map<Point, List<Point>> map = new HashMap<Point, List<Point>>();
        synchronized (this) {
            if (mapToLayer.equals(pointCoordCalculator.mapToLayer))
                return;

            oldToScreen = new AffineTransform(pointCoordCalculator.toScreen);
            AffineTransform oldToWorld = new AffineTransform(pointCoordCalculator.toWorld);

            geomMapping.clear();
            coordMapping.clear();
            PointCoordCalculator calculator = new PointCoordCalculator(
                    pointCoordCalculator.toScreen, mapToLayer);
            for (EditGeom geom : geometries) {
                for (PrimitiveShape shape : geom) {
                    map.putAll(shape.getMutator().transform(oldToScreen, oldToWorld, calculator));
                }
            }

            pointCoordCalculator.setMapToLayer(mapToLayer);

            if (EditPlugin.isDebugging(EditPlugin.RUN_ASSERTIONS))
                for (EditGeom geom : geometries) {
                    geom.assertValid();
                }
        }
        notify(new EditBlackboardEvent(this, this, EventType.TRANFORMATION, oldToScreen, map));

    }

    /**
     * Sets all the coordinates at the point to provided coordinate. <em>The coodinate must still
     * map to the point</em>
     * 
     * @param point the point at which all the coordinates will be changed
     * @param newValue the new coordinate value of coordinates at the point
     */
    public void setCoords(Point point, Coordinate newValue) {
        synchronized (this) {
            if (!toPoint(newValue).equals(point))
                throw new IllegalArgumentException(
                        "newValue has to map to point.  newValue maps to: " + toPoint(newValue) //$NON-NLS-1$
                                + " but should map to" + point); //$NON-NLS-1$
            List<LazyCoord> coords = coordMapping.get(point);
            if (coords != null) {
                for (LazyCoord coord : coords) {
                    coord.get(point);
                    coord.coord = newValue;
                    coord.x = newValue.x;
                    coord.y = newValue.y;
                    coord.z = newValue.z;
                }
            } else {
                throw new NullPointerException("No points associated with " + point); //$NON-NLS-1$
            }
        }
    }

    /**
     * Returns true if there are no selected geometries on the editblackboard
     *
     * @return true if there are no selected geometries on the editblackboard
     */
    public boolean isEmpty() {
        return geomMapping.isEmpty();
    }

}
