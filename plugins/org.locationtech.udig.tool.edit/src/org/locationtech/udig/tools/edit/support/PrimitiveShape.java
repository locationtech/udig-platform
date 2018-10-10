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

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.preferences.PreferenceUtil;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Represents the simplest shape. EditGeoms are made up of PrimitiveShapes.
 * <p>
 * This is not a composite pattern.
 * </p>
 * 
 * @author jones
 * @since 1.1.0
 */
public class PrimitiveShape implements Iterable<Point>, Shape {
    private Map<Point, List<PointCoordMap>> pointsToModel = new HashMap<Point, List<PointCoordMap>>();

    private Map<LazyCoord, PointCoordMap> coordsToModel = new HashMap<LazyCoord, PointCoordMap>();

    private List<PointCoordMap> points = new ArrayList<PointCoordMap>();

    private final List<LazyCoord> coordinates = new ArrayList<LazyCoord>();

    private final AtomicReference<Mutator> mutable = new AtomicReference<Mutator>();

    private final EditGeom owner;

    private Envelope envelope;

    String type = "SHELL"; //$NON-NLS-1$

    public PrimitiveShape(EditGeom owner) {
        this.owner = owner;
    }

    public PrimitiveShape(PrimitiveShape shape) {
        this(shape.owner);
        points.addAll(shape.points);
        for (LazyCoord coord : shape.coordinates) {
            coordinates.add(new LazyCoord(coord));
        }
    }

    public int getNumPoints() {
        return points.size();
    }

    public Point getPoint(int i) {
        return points.get(i).point;
    }

    public int getNumCoords() {
        return coordinates.size();
    }

    public Coordinate getCoord(int i) {
        LazyCoord coord = coordinates.get(i);
        Point point = coordsToModel.get(coord).point;
        return coord.get(point);
    }

    @Override
    public String toString() {
        if (points.size() == 0)
            return "[]"; //$NON-NLS-1$
        StringBuffer buffer = new StringBuffer(type + "["); //$NON-NLS-1$
        for (PointCoordMap point : points) {
            buffer.append(point.point.toString());
            buffer.append(","); //$NON-NLS-1$
        }
        buffer.deleteCharAt(buffer.length() - 1);
        buffer.append("]"); //$NON-NLS-1$
        return buffer.toString();
    }

    Mutator getMutator() {
        // Since all copies of a mutable do the same thing this race condition doesn't matter.
        if (mutable.get() == null)
            mutable.set(new Mutator());
        return mutable.get();
    }

    class Mutator implements Iterable<Point> {

        /**
         * Adds a point and its corresponding coordinate
         * 
         * @param p point to add
         * @param coords corresponding coordinate or null.
         */
        public void addPoint(Point p, List<Coordinate> coords) {
            addPoint(points.size(), p, coords);
        }

        /**
         * Adds a point and its corresponding coordinate
         * 
         * @param i index of point location. Coordinate will be added as the last of the coordinates
         *        at that location.
         * @param p point to add
         * @param coords corresponding coordinate or null.
         */
        public List<LazyCoord> addPoint(int i, Point p, List<Coordinate> coords) {
            List<Coordinate> c = coords;
            PointCoordMap bag = getBag(i, p);
            if (c == null)
                c = Collections.singletonList(getEditBlackboard().toCoord(p));
            List<LazyCoord> lazyCoords = addAll(bag.coords.size(), bag.coords, p, c);
            List<PointCoordMap> bags = pointsToModel.get(p);
            if (bags == null) {
                bags = new ArrayList<PointCoordMap>();
                pointsToModel.put(p, bags);
            }
            if (!bags.contains(bag))
                bags.add(bag);

            int index;
            if (coordinates.size() == 0)
                index = 0;
            else if (bag.coords.size() > 1) {
                LazyCoord lazyCoord = bag.coords.get(bag.coords.size() - 2);
                index = coordinates.indexOf(lazyCoord) + 1;
            } else if (i == 0) {
                index = 0;
            } else {
                // add after last coordinate in last bag
                PointCoordMap lastbag = points.get(i - 1);
                if (lastbag == bag)
                    lastbag = points.get(i - 2);

                index = coordinates.indexOf(lastbag.coords.get(lastbag.coords.size() - 1)) + 1;
            }

            for (LazyCoord coord : lazyCoords) {
                coordinates.add(index++, coord);
                coordsToModel.put(coord, bag);
            }
            if (!owner.initializing)
                getEditGeom().setChanged(true);
            return lazyCoords;
        }

        private List<LazyCoord> addAll(int i, List<LazyCoord> coordinates, Point p,
                List<Coordinate> c) {
            int j = i;
            EditBlackboard bb = getEditBlackboard();
            ArrayList<LazyCoord> coords = new ArrayList<LazyCoord>(c.size());
            for (Coordinate coordinate : c) {
                LazyCoord lazyCoord = new LazyCoord(p, coordinate, bb);
                coordinates.add(j++, lazyCoord);
                coords.add(lazyCoord);
            }
            return coords;
        }

        private PointCoordMap getBag(int i, Point p) {
            // if previous point or next point is same as point to add then return that bag
            if (i > 0 && i <= points.size()) {
                if (points.get(i - 1).point.equals(p)) {
                    return points.get(i - 1);
                }
                if (points.size() > i && points.get(i).point.equals(p)) {
                    return points.get(i);
                }
            }
            PointCoordMap bag = new PointCoordMap(p);
            points.add(i, bag);
            return bag;
        }

        /**
         * Adds a coordinate
         * 
         * @param i index of coordinate to add.
         * @param c corresponding coordinate or null.
         */
        public void addCoordinate(int i, Coordinate c) {
            Point p = getEditBlackboard().toPoint(c);
            coordinates.add(i, new LazyCoord(p, c, getEditBlackboard()));
            // /TODO
            getEditGeom().setChanged(true);
        }

        /**
         * Remove a point and all the coordinates mapping to it.
         * 
         * @param i index of point to remove
         */
        public Point removePoint(int i) {
            PointCoordMap p = points.remove(i);
            pointsToModel.remove(p.point);
            List<LazyCoord> coords = p.coords;
            for (LazyCoord coord : coords) {
                for (Iterator<LazyCoord> iter = coordinates.iterator(); iter.hasNext();) {
                    if (iter.next() == coord) {
                        iter.remove();
                    }
                }
            }
            getEditGeom().setChanged(true);
            return p.point;
        }

        /**
         * Remove a coordinate at location i. Mapped points will be deleted too.
         * 
         * @param i index of coordinate to remove
         */
        public Coordinate removeCoordinate(int i) {
            getEditGeom().setChanged(true);
            LazyCoord coord = coordinates.remove(i);
            return coord.get(coordsToModel.get(coord).point);
        }

        /**
         * Remove a point and all the coordinates mapping to it.
         */
        public boolean remove(Point p) {
            List<PointCoordMap> mods = pointsToModel.get(p);
            if (mods == null || mods.size() == 0)
                return false;

            int min = Integer.MAX_VALUE;
            int firstBag = 0;
            int current = 0;
            for (Iterator<PointCoordMap> iter = mods.iterator(); iter.hasNext();) {
                PointCoordMap element = iter.next();
                int i = points.indexOf(element);
                if (i < min) {
                    min = i;
                    firstBag = current;
                }
                current++;
            }

            PointCoordMap shapePoint = mods.remove(firstBag);
            points.remove(shapePoint);
            coordinates.removeAll(shapePoint.coords);
            getEditGeom().setChanged(true);
            return true;
        }

        /**
         * Remove a coordinate at location i. Mapped points will be deleted too.
         * 
         * @param i index of coordinate to remove
         */
        public boolean remove(Coordinate c) {
            getEditGeom().setChanged(true);
            return coordinates.remove(c);
        }

        public LazyCoord removePoint(int pointIndex, Coordinate coord) {

            PointCoordMap p = points.get(pointIndex);
            LazyCoord lcoord = null;
            for (Iterator<LazyCoord> iter = p.coords.iterator(); iter.hasNext();) {
                LazyCoord lc = iter.next();
                if (lc.coord.equals(coord)) {
                    lcoord = lc;
                    iter.remove();
                    break;
                }
            }

            if (p.coords.isEmpty()) {
                points.remove(pointIndex);
                List<PointCoordMap> bags = pointsToModel.get(p.point);
                bags.remove(p);
                if (bags.isEmpty())
                    pointsToModel.remove(p.point);
            }

            if (lcoord == null)
                return null;

            for (Iterator<LazyCoord> iter = coordinates.iterator(); iter.hasNext();) {
                LazyCoord next = iter.next();
                if (next == lcoord) {
                    iter.remove();
                    coordsToModel.remove(next);
                }
            }
            getEditGeom().setChanged(true);
            return lcoord;
        }

        /**
         * Resets shape
         */
        public void clear() {
            getEditGeom().setChanged(true);
            coordinates.clear();
            points.clear();
        }

        public boolean hasPoint(Point pointOnLine) {
            return pointsToModel.containsKey(pointOnLine);
        }

        /**
         * @return Returns an iterator that iterates over all points in shape.
         */
        public ListIterator<Point> iterator() {
            return new ListIterator<Point>() {
                ListIterator<PointCoordMap> iter = points.listIterator();

                PointCoordMap current;

                public boolean hasNext() {
                    return iter.hasNext();
                }

                public Point next() {
                    current = iter.next();
                    return current.point;
                }

                public boolean hasPrevious() {
                    return iter.hasPrevious();
                }

                public Point previous() {
                    current = iter.previous();
                    return current.point;
                }

                public int nextIndex() {
                    return iter.nextIndex();
                }

                public int previousIndex() {
                    return iter.previousIndex();
                }

                public void remove() {
                    iter.remove();
                }

                public void set(Point o) {
                    List<PointCoordMap> maps = pointsToModel.get(current.point);
                    maps.remove(current);
                    if (maps.isEmpty())
                        pointsToModel.remove(current.point);
                    maps = pointsToModel.get(o);
                    if (maps == null) {
                        maps = new ArrayList<PointCoordMap>();
                        pointsToModel.put(o, maps);
                    }
                    maps.add(current);
                    current.point = o;
                    getEditGeom().setChanged(true);
                }

                public void add(Point o) {

                    ArrayList<LazyCoord> list = new ArrayList<LazyCoord>();
                    list.add(new LazyCoord(o, getEditBlackboard().toCoord(o), getEditBlackboard()));
                    iter.add(new PointCoordMap(o, list));
                    getEditGeom().setChanged(true);
                }

            };
        }

        /**
         * @return Returns an iterator that iterates over all coordinates in shape. May be modified
         */
        public ListIterator<Coordinate> coordIterator() {

            return new ListIterator<Coordinate>() {

                ListIterator<LazyCoord> iter = coordinates.listIterator();

                private LazyCoord current;

                public boolean hasNext() {
                    return iter.hasNext();
                }

                public Coordinate next() {
                    current = iter.next();
                    return current.get(coordsToModel.get(current).point);
                }

                public boolean hasPrevious() {
                    return iter.hasPrevious();
                }

                public Coordinate previous() {
                    current = iter.previous();
                    return current.get(coordsToModel.get(current).point);
                }

                public int nextIndex() {
                    return iter.nextIndex();
                }

                public int previousIndex() {
                    return iter.previousIndex();
                }

                public void remove() {
                    iter.remove();
                    PointCoordMap bag = coordsToModel.remove(current);
                    points.remove(bag.point);
                }

                public void set(Coordinate o) {
                    current.set(o, getEditBlackboard().toPoint(o));
                }

                public void add(Coordinate o) {
                    addPoint(iter.nextIndex() - 1, getEditBlackboard().toPoint(o),
                            Collections.singletonList(o));
                }

            };
        }

        /**
         *
         */
        public void reverse() {
            Collections.reverse(points);
            Collections.reverse(coordinates);
        }

        public List<LazyCoord> getLazyCoordsAt(int i) {
            return points.get(i).coords;
        }

        /**
         * Applies the transform to all the points in the shape. This is the start of a migration.
         * This method will update the EditBlackboard rather than the edit blackboard updating
         * itself and the Primitive Shape. The pointCoordCalculator must have been updated already
         * with the new toScreen and toWorld transforms.
         * 
         * @param oldToNew
         */
        Map<? extends Point, ? extends List<Point>> transform(AffineTransform oldToScreen,
                AffineTransform oldToWorld, PointCoordCalculator pointCoordCalculator) {
            if (Math.abs(pointCoordCalculator.toScreen.getDeterminant()
                    - oldToScreen.getDeterminant()) > 0.000001) {
                return transformInternal(pointCoordCalculator);
            } else {

                // pan
                return translate(AffineTransform.getTranslateInstance(
                        pointCoordCalculator.toScreen.getTranslateX() - oldToScreen.getTranslateX(),
                        pointCoordCalculator.toScreen.getTranslateY()
                                - oldToScreen.getTranslateY()),
                        pointCoordCalculator);
            }
        }

        private HashMap<Point, List<Point>> transformInternal(
                PointCoordCalculator pointCoordCalculator) {
            List<PointCoordMap> oldPoints = points;

            HashMap<Point, List<Point>> oldPointToNew = new HashMap<Point, List<Point>>();

            points = new ArrayList<PointCoordMap>();
            pointsToModel.clear();
            coordsToModel.clear();
            EditBlackboard editBlackboard = owner.getEditBlackboard();

            for (PointCoordMap map : oldPoints) {
                Point oldPoint = map.point;
                for (Iterator<LazyCoord> iter = map.coords.iterator(); iter.hasNext();) {
                    LazyCoord c = iter.next();
                    PointCoordMap tmp = map;
                    Coordinate coord = c.get(oldPoint);

                    Point newPoint = pointCoordCalculator.toPoint(coord);
                    // // So that all the vertices don't make a big mess look around point in a 3x3
                    // // radius
                    // // if another point is there add to that point.
                    // Point overLappingPoint;
                    // overLappingPoint = getEditBlackboard().overVertex(newPoint,
                    // PreferenceUtil.instance().getVertexRadius());
                    //
                    // if (overLappingPoint != null && getEditBlackboard().isCollapseVertices())
                    // newPoint = overLappingPoint;

                    if (!newPoint.equals(oldPoint)) {
                        tmp = new PointCoordMap(newPoint);
                        tmp.coords.add(c);
                        iter.remove();
                    }

                    c.pointCoordCalculator = new PointCoordCalculator(pointCoordCalculator);
                    c.start = newPoint;

                    List<Point> pointMapping = oldPointToNew.get(oldPoint);
                    if (pointMapping == null) {
                        pointMapping = new ArrayList<Point>();
                        pointMapping.add(newPoint);
                        oldPointToNew.put(oldPoint, pointMapping);
                    } else {
                        pointMapping.add(newPoint);
                    }
                    tmp.point = newPoint;

                    coordsToModel.put(c, tmp);

                    // update shape
                    points.add(tmp);
                    List<PointCoordMap> list = pointsToModel.get(newPoint);
                    if (list == null) {
                        List<PointCoordMap> l = new ArrayList<PointCoordMap>();
                        l.add(tmp);
                        pointsToModel.put(newPoint, l);
                    } else {
                        list.add(tmp);
                    }

                    // update Blackboard.
                    List<LazyCoord> coords = editBlackboard.coordMapping.get(newPoint);
                    if (coords == null) {
                        List<LazyCoord> l = new ArrayList<LazyCoord>(tmp.coords);
                        editBlackboard.coordMapping.put(newPoint, l);
                    } else {
                        coords.addAll(tmp.coords);
                    }

                    Set<EditGeom> mappedGeoms = editBlackboard.geomMapping.get(newPoint);
                    if (mappedGeoms == null) {
                        Set<EditGeom> l = new HashSet<EditGeom>();
                        l.add(getEditGeom());
                        editBlackboard.geomMapping.put(newPoint, l);
                    } else {
                        if (!mappedGeoms.contains(getEditGeom()))
                            mappedGeoms.add(getEditGeom());
                    }

                }
            }
            return oldPointToNew;
        }

        private Map<? extends Point, ? extends List<Point>> translate(AffineTransform oldToNew,
                PointCoordCalculator pointCoordCalculator) {
            List<PointCoordMap> oldPoints = points;

            HashMap<Point, List<Point>> oldPointToNew = new HashMap<Point, List<Point>>();

            points = new ArrayList<PointCoordMap>();
            pointsToModel.clear();
            EditBlackboard editBlackboard = owner.getEditBlackboard();

            int diffX = (int) oldToNew.getTranslateX();
            int diffY = (int) oldToNew.getTranslateY();
            for (PointCoordMap map : oldPoints) {

                Point oldPoint = map.point;
                for (Iterator<LazyCoord> iter = map.coords.iterator(); iter.hasNext();) {
                    LazyCoord c = iter.next();
                    PointCoordMap tmp = map;
                    Point newPoint = Point.valueOf(map.point.getX() + diffX,
                            map.point.getY() + diffY);

                    if (!newPoint.equals(oldPoint)) {
                        tmp = new PointCoordMap(newPoint);
                        tmp.coords.add(c);
                        iter.remove();
                    }

                    c.pointCoordCalculator = new PointCoordCalculator(pointCoordCalculator);
                    c.start = newPoint;

                    List<Point> pointMapping = oldPointToNew.get(oldPoint);
                    if (pointMapping == null) {
                        pointMapping = new ArrayList<Point>();
                        pointMapping.add(newPoint);
                        oldPointToNew.put(oldPoint, pointMapping);
                    } else {
                        pointMapping.add(newPoint);
                    }
                    tmp.point = newPoint;

                    coordsToModel.put(c, tmp);

                    // update shape
                    points.add(tmp);
                    List<PointCoordMap> list = pointsToModel.get(newPoint);
                    if (list == null) {
                        List<PointCoordMap> l = new ArrayList<PointCoordMap>();
                        l.add(tmp);
                        pointsToModel.put(newPoint, l);
                    } else {
                        list.add(tmp);
                    }

                    // update Blackboard.
                    List<LazyCoord> coords = editBlackboard.coordMapping.get(newPoint);
                    if (coords == null) {
                        List<LazyCoord> l = new ArrayList<LazyCoord>(tmp.coords);
                        editBlackboard.coordMapping.put(newPoint, l);
                    } else {
                        coords.addAll(tmp.coords);
                    }

                    Set<EditGeom> mappedGeoms = editBlackboard.geomMapping.get(newPoint);
                    if (mappedGeoms == null) {
                        Set<EditGeom> l = new HashSet<EditGeom>();
                        l.add(getEditGeom());
                        editBlackboard.geomMapping.put(newPoint, l);
                    } else {
                        if (!mappedGeoms.contains(getEditGeom()))
                            mappedGeoms.add(getEditGeom());
                    }
                }

            }
            return oldPointToNew;
        }

        /**
         * Moves a single LazyCoord from the start point to the end point. Keeps the order of the
         * coordinates in the same order.
         * 
         * @param start
         * @param end
         * @param coord
         */
        public void move(Point start, Point end, LazyCoord coord) {

            PointCoordMap toRemove = null;

            for (PointCoordMap map : pointsToModel.get(start)) {
                if (map.coords.contains(coord)) {
                    List<PointCoordMap> startBags = pointsToModel.get(start);
                    PointCoordMap endBag = null;
                    int startIndex = points.indexOf(map);
                    PointCoordMap before = new PointCoordMap(start);
                    endBag = new PointCoordMap(end);
                    PointCoordMap after = new PointCoordMap(start);

                    points.remove(startIndex);
                    int i = 0;
                    for (LazyCoord coord2 : map.coords) {
                        if (i < map.coords.indexOf(coord)) {
                            before.coords.add(coord2);
                            coordsToModel.put(coord2, before);
                        } else if (i > map.coords.indexOf(coord)) {
                            after.coords.add(coord2);
                            coordsToModel.put(coord2, after);
                        }
                        i++;
                    }
                    boolean addedBefore = false;
                    boolean addedAfter = false;
                    if (before.coords.size() > 0) {
                        points.add(startIndex, before);
                        startBags.add(before);
                        addedBefore = true;
                    }
                    points.add(addedBefore ? startIndex + 1 : startIndex, endBag);
                    if (after.coords.size() > 0) {
                        points.add(addedBefore ? startIndex + 2 : startIndex + 1, after);
                        startBags.add(after);
                        addedAfter = true;
                    }

                    toRemove = map;
                    List<PointCoordMap> endBags = pointsToModel.get(end);

                    if (endBags == null) {
                        endBags = new ArrayList<PointCoordMap>();
                        pointsToModel.put(end, endBags);
                    }
                    if (!endBags.contains(endBag))
                        endBags.add(endBag);

                    endBag.coords.add(coord);
                    coordsToModel.put(coord, endBag);

                    if (addedBefore && !addedAfter || !addedBefore && addedAfter) {
                        attemptBeforeAndAfterVertexCollapse(startIndex);
                        attemptBeforeAndAfterVertexCollapse(startIndex + 1);
                    } else if (addedBefore && addedAfter) {
                        attemptBeforeAndAfterVertexCollapse(startIndex);
                        attemptBeforeAndAfterVertexCollapse(startIndex + 2);
                    } else if (!addedBefore && !addedAfter) {
                        attemptBeforeAndAfterVertexCollapse(startIndex);
                    }

                    break;
                }

            }

            if (toRemove != null) {
                pointsToModel.get(start).remove(toRemove);
            }
        }

        private void attemptBeforeAndAfterVertexCollapse(int index) {
            if (points.size() == 1 || index > points.size() - 1)
                return;

            int before = index - 1;
            int after = index + 1;

            if (before > -1) {
                boolean changed = doCollapseVertices(index, before);
                if (changed) {
                    index = before;
                    after--;
                }
            }

            if (after < points.size()) {
                doCollapseVertices(after, index);
            }
        }

        private boolean doCollapseVertices(int index, int before) {
            boolean changed = false;
            PointCoordMap currentMap = points.get(index);
            PointCoordMap beforeMap = points.get(before);
            if (currentMap.point.equals(beforeMap.point)) {
                changed = true;
                points.remove(index);
                pointsToModel.get(currentMap.point).remove(currentMap);

                for (LazyCoord coord : currentMap.coords) {
                    coordsToModel.put(coord, beforeMap);
                    beforeMap.coords.add(coord);
                }
            }
            return changed;
        }

        public Iterator<Point> getCopyIterator() {
            return new Iterator<Point>() {
                List<PointCoordMap> newPoints = new ArrayList<PointCoordMap>(points);

                Iterator<PointCoordMap> iter;

                public boolean hasNext() {
                    if (iter == null)
                        iter = newPoints.iterator();
                    return iter.hasNext();
                }

                public Point next() {
                    if (iter == null)
                        iter = newPoints.iterator();
                    return iter.next().point;
                }

                public void remove() {
                    throw new IllegalArgumentException("not supported; to inefficient"); //$NON-NLS-1$
                }

            };
        }

        /**
         * Returns lazycoords at point.
         * 
         * @param point
         * @return
         */
        public List<LazyCoord> getLazyCoordsAt(Point point) {
            List<PointCoordMap> bags = pointsToModel.get(point);
            List<LazyCoord> coords = new ArrayList<LazyCoord>();
            if (bags == null) {
                return Collections.<LazyCoord> emptyList();
            }
            for (PointCoordMap map : bags) {
                coords.addAll(map.coords);
            }

            return coords;
        }

        public void move(int deltaX, int deltaY) {
            Map<Point, List<PointCoordMap>> newPointsToModel = new HashMap<Point, List<PointCoordMap>>();

            for (Point point : this) {
                Point dest = Point.valueOf(point.getX() + deltaX, point.getY() + deltaY);
                List<PointCoordMap> oldBags = pointsToModel.remove(point);

                if (oldBags == null)
                    continue;

                newPointsToModel.put(dest, oldBags);

                for (PointCoordMap map : oldBags) {
                    map.point = dest;
                }
            }

            for (Map.Entry<Point, List<PointCoordMap>> entry : newPointsToModel.entrySet()) {
                List<PointCoordMap> newBags = pointsToModel.get(entry.getKey());
                if (newBags == null) {
                    pointsToModel.put(entry.getKey(), entry.getValue());
                } else {
                    newBags.addAll(entry.getValue());
                }
            }

            pointsToModel = newPointsToModel;

        }

    }

    /**
     * @return Returns the owner.
     */
    public EditGeom getEditGeom() {
        return owner;
    }

    public EditBlackboard getEditBlackboard() {
        return owner.getEditBlackboard();
    }

    /**
     * @return Returns an iterator that iterates over all visible points in shape. Not modifyable
     */
    public Iterator<Point> iterator() {
        final PointCoordMap[] array = points.toArray(new PointCoordMap[points.size()]);
        return new Iterator<Point>() {
            int i = -1;

            public boolean hasNext() {
                return i < array.length - 1;
            }

            public Point next() {
                i++;
                return array[i].point;
            }

            public void remove() {
                throw new UnsupportedOperationException(
                        "This is iterator does not allow modification"); //$NON-NLS-1$
            }
        };
    }

    /**
     * @return Returns an iterator that iterates over all visible coordinates in shape. Not
     *         modifyible
     */
    public Iterator<Coordinate> coordIterator() {
        return getMutator().coordIterator();
    }

    private static class PointCoordMap {
        Point point;

        final List<LazyCoord> coords;

        public PointCoordMap(Point p, List<LazyCoord> coords) {
            point = p;
            this.coords = coords;
        }

        public PointCoordMap(Point p) {
            point = p;
            this.coords = new ArrayList<LazyCoord>();
        }

        @Override
        public String toString() {
            return "{" + point + "=" + coords + "}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
    }

    /**
     * Returns an array with all the coordinates in shape.
     * 
     * @return an array with all the coordinates in shape.
     */
    public Coordinate[] coordArray() {
        Coordinate[] array = new Coordinate[coordinates.size()];
        Iterator<Coordinate> iter = coordIterator();
        for (int i = 0; i < array.length; i++) {
            array[i] = iter.next();
        }
        return array;
    }

    /**
     * returns true if the point is contained in this shape.
     * 
     * @param p an arbitrary point
     * @return Returns true if the point is contained in this shape.
     */
    public boolean hasVertex(Point p) {
        return pointsToModel.containsKey(p);
    }

    boolean hasVertex(Point p, LazyCoord coord) {
        List<PointCoordMap> list = pointsToModel.get(p);
        if (list == null)
            return false;

        for (PointCoordMap map : list) {
            if (map.coords == null)
                return false;
            for (LazyCoord coord2 : map.coords) {
                if (coord2 == coord)
                    return true;
            }
        }
        return false;
    }

    /**
     * Returns a {@link ClosestEdge} object that has information about the point that closest to the
     * click paramter and is on an edge of the part.
     * 
     * @param click the point to use as the reference point
     * @return
     */
    public ClosestEdge getClosestEdge(Point click, boolean treatUnknownAsPolygon) {
        if (getNumPoints() == 0)
            return null;
        final int endIndex;
        int startIndex, lastIndex;
        Point coord1;

        if ((getEditGeom().getShapeType() == ShapeType.UNKNOWN && treatUnknownAsPolygon)
                || getEditGeom().getShapeType() == ShapeType.POLYGON) {
            lastIndex = getNumPoints() - 1;
            coord1 = getPoint(lastIndex);
            startIndex = 0;
            endIndex = getNumPoints() - 1;
        } else if ((getEditGeom().getShapeType() == ShapeType.UNKNOWN && !treatUnknownAsPolygon)
                || getEditGeom().getShapeType() == ShapeType.LINE) {
            lastIndex = 0;
            coord1 = getPoint(lastIndex);
            startIndex = 1;
            endIndex = getNumPoints() - 1;

        } else {
            // points don't have edges
            return null;
        }

        double mindist = Double.MAX_VALUE;

        int prev = -1;
        Point closestPoint = null;
        for (int i = startIndex; i <= endIndex; i++) {
            Point coord2 = getPoint(i);
            try {
                Point point = EditUtils.instance.closestPointOnEdge(coord1, coord2, click);
                if (point == null) {
                    continue;
                }
                int x = click.getX() - point.getX();
                int y = click.getY() - point.getY();
                double dist = Math.sqrt(x * x + y * y);
                if (dist < mindist) {
                    mindist = dist;
                    prev = lastIndex;
                    closestPoint = point;
                }
            } finally {
                coord1 = coord2;
                lastIndex = i;
            }
        }

        if (closestPoint == null)
            return null;
        return new ClosestEdge(mindist, prev, closestPoint, this);
    }

    /**
     * Returns the bounding box of the Shape in pixel space.
     * 
     * @return the bounding box of the Shape in pixel space.
     */
    public Rectangle getBounds() {
        int minx = Integer.MAX_VALUE;
        int maxx = Integer.MIN_VALUE;
        int miny = Integer.MAX_VALUE;
        int maxy = Integer.MIN_VALUE;
        for (Point point : getMutator()) {
            if (point.getX() < minx)
                minx = point.getX();
            if (point.getX() > maxx)
                maxx = point.getX();
            if (point.getY() < miny)
                miny = point.getY();
            if (point.getY() > maxy)
                maxy = point.getY();
        }

        return new Rectangle(minx, miny, maxx - minx, maxy - miny);
    }

    /**
     * Returns true if the point is contained in the shape or on the edge. It is considered on the
     * edge if it is within {@link PreferenceUtil#getVertexRadius()} of the edge.
     * 
     * @param point An astrbitrary point
     * @param treatUnknownGeomsAsPolygon if {@link EditGeom#getShapeType()} return UNKOWN this
     *        parameter is used to determine if this shape should be considered a polygon.
     * @return true if the point is contained in the shape.
     */
    public boolean contains(Point point, boolean treatUnknownGeomsAsPolygon) {
        return contains(point, treatUnknownGeomsAsPolygon, false);
    }

    /**
     * Returns true if the point is contained in the shape or on the edge.
     * 
     * @param point An astrbitrary point
     * @param treatUnknownGeomsAsPolygon if {@link EditGeom#getShapeType()} return UNKOWN this
     *        parameter is used to determine if this shape should be considered a polygon.
     * @param ignoreVertexRadius if true then the point is only considered to be on the edge if it
     *        is directly on the edge. (within 1 pixel of the line).
     * @return true if the point is contained in the shape.
     */
    public boolean contains(Point point, boolean treatUnknownGeomsAsPolygon,
            boolean ignoreVertexRadius) {

        if (points.size() == 0)
            return false;

        int vertexRadius;
        if (ignoreVertexRadius)
            vertexRadius = 1;
        else {
            vertexRadius = PreferenceUtil.instance().getVertexRadius();
        }

        if (points.size() == 1) {
            Point click = points.get(0).point;
            int x = click.getX() - point.getX();
            int y = click.getY() - point.getY();
            double dist = Math.sqrt(x * x + y * y);

            return dist < vertexRadius;
        }

        Point overVertex = getEditBlackboard().overVertex(point, vertexRadius);
        if (overVertex != null && pointsToModel.containsKey(overVertex))
            return true;

        ClosestEdge edge = getClosestEdge(point, treatUnknownGeomsAsPolygon);
        if (edge != null && edge.getDistanceToEdge() < vertexRadius)
            return true;

        PrimitiveShapeIterator iter = PrimitiveShapeIterator.getPathIterator(this);
        iter.setPolygon(isPolygon(treatUnknownGeomsAsPolygon));
        GeneralPath path = new GeneralPath();
        path.append(iter, false);
        return path.contains(point.getX(), point.getY());
    }

    public Rectangle2D getBounds2D() {
        return null;
    }

    public boolean contains(double x, double y) {
        return contains(Point.valueOf((int) x, (int) y), true);
    }

    public boolean contains(Point2D p) {
        return contains(Point.valueOf((int) p.getX(), (int) p.getY()), true);
    }

    public boolean intersects(double x, double y, double w, double h) {
        return false;
    }

    public boolean intersects(Rectangle2D r) {
        return false;
    }

    public boolean contains(double x, double y, double w, double h) {
        return false;
    }

    public boolean contains(Rectangle2D r) {
        return false;
    }

    public PathIterator getPathIterator(AffineTransform at) {
        return PrimitiveShapeIterator.getPathIterator(this);
    }

    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return PrimitiveShapeIterator.getPathIterator(this);
    }

    /**
     * Returns all the coordinates that map to the point at location i.
     * 
     * @param i index of a point in the shape
     * @return Returns all the coordinates that map to the point at location i.
     */
    public List<Coordinate> getCoordsAt(int i) {
        PointCoordMap bag = points.get(i);
        return new CoordResolvingList(bag.coords, bag.point);
    }

    public List<Coordinate> getCoordsAt(Point point) {
        return new CoordResolvingList(getMutator().getLazyCoordsAt(point), point);
    }

    public int getAssociatedPointIndex(int coordIndex) {
        return points.indexOf(coordsToModel.get((coordinates.get(coordIndex))));
    }

    public boolean hasVertex(Coordinate start) {
        // Point point = getEditBlackboard().toPoint(start);
        // List<LazyCoord> coords = getMutator().getLazyCoordsAt(point);
        // for( LazyCoord coord : coords ) {
        // if( coord.get(coordsToModel.get(coord).point).equals(start) )
        // return true;
        // }
        for (LazyCoord coord : coordinates) {
            if (coord.get(coordsToModel.get(coord).point).equals(start))
                return true;
        }
        return false;
    }

    public void assertValid() {
        if (!EditPlugin.isDebugging(EditPlugin.RUN_ASSERTIONS))
            return;
        if (getNumCoords() == 0 && getNumPoints() != 0
                || getNumCoords() != 0 && getNumPoints() == 0)
            throw new AssertionError(
                    "Num Coords=" + getNumCoords() + " NumPoints=" + getNumPoints()); //$NON-NLS-1$ //$NON-NLS-2$

        if (getNumCoords() < 1) {
            return;
        }

        for (LazyCoord lz : coordinates) {
            if (!coordsToModel.containsKey(lz))
                throw new AssertionError(lz + " should be in coordsToModel but isn't"); //$NON-NLS-1$
            PointCoordMap bag = coordsToModel.get(lz);
            Point findVertex = bag.point;
            if (findVertex == null)
                throw new AssertionError(bag.point
                        + " should equal findVertex(getEditBlackboard().toPoint(lz.get(bag.point)), snappingRadius, false)"); //$NON-NLS-1$

            boolean found = false;
            for (LazyCoord lz2 : bag.coords) {
                if (lz2 == lz) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new AssertionError(lz + " is not in the mapped bag " + bag); //$NON-NLS-1$
            }
            if (getEditBlackboard().coordMapping.containsKey(bag.point)) {
                found = false;
                List<LazyCoord> list = getEditBlackboard().coordMapping.get(bag.point);
                for (LazyCoord lz2 : list) {
                    if (lz2 == lz) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    throw new AssertionError(lz + " is not in editblackboard's coordMapping"); //$NON-NLS-1$
                }
            } else {
                throw new AssertionError(bag.point + " should be in the blackboard but isn't"); //$NON-NLS-1$
            }

        }

        for (PointCoordMap b : points) {
            for (LazyCoord coordinate : b.coords) {
                if (!coordinates.contains(coordinate)) {
                    throw new AssertionError(
                            coordinate + " is not in the list of coordinate but it should be"); //$NON-NLS-1$
                }
                if (!coordsToModel.containsKey(coordinate)) {
                    throw new AssertionError(coordinate + " is not in coordsToModel map"); //$NON-NLS-1$
                }
                if (!pointsToModel.containsKey(b.point))
                    throw new AssertionError(b.point + " is not in pointsToModel map"); //$NON-NLS-1$
            }
            if (!getEditBlackboard().coordMapping.get(b.point).containsAll(b.coords)) {
                throw new AssertionError("Not all coordinates in bags are in editblackboard"); //$NON-NLS-1$
            }
            if (!getEditBlackboard().geomMapping.get(b.point).contains(getEditGeom())) {
                throw new AssertionError("EditGeom is not mapped to " + b.point //$NON-NLS-1$
                        + " in edit blackboard's geomMapping"); //$NON-NLS-1$
            }
        }

        EditPlugin.trace(EditPlugin.RUN_ASSERTIONS, "shape is valid", null); //$NON-NLS-1$
    }

    public boolean contains(Coordinate startCoord, boolean treatUnknownGeomsAsPolygon) {

        for (int i = 0; i < getNumCoords() - 1; i++) {
            Coordinate intersection = EditUtils.instance.closestCoordinateOnEdge(getCoord(i),
                    getCoord(i + 1), startCoord);
            if (intersection != null && intersection.equals(startCoord))
                return true;
        }

        PrimitiveShapeIterator iter = PrimitiveShapeIterator.getPathIterator(this);
        iter.setPolygon(treatUnknownGeomsAsPolygon);
        Shape s = iter.toShape();
        return s.contains(startCoord.x, startCoord.y);
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
        List<PointCoordMap> list = this.pointsToModel.get(location);
        if (!ignore && list != null && !list.isEmpty())
            return location;
        for (int i = 1; i <= radius; i++) {
            Point result = findVertex(location, i);
            if (result != null)
                return result;
        }
        return null;
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
            Point temp = Point.valueOf(x, minY);
            List<PointCoordMap> list = pointsToModel.get(temp);
            if (list != null && list.size() > 0)
                return temp;
        }

        for (int y = minY + 1; y <= maxY; y++) {
            Point temp = Point.valueOf(maxX, y);
            List<PointCoordMap> list = pointsToModel.get(temp);
            if (list != null && list.size() > 0)
                return temp;
        }

        for (int x = maxX - 1; x >= minX; x--) {
            Point temp = Point.valueOf(x, maxY);
            List<PointCoordMap> list = pointsToModel.get(temp);
            if (list != null && list.size() > 0)
                return temp;
        }

        for (int y = maxY - 1; y >= minY; y--) {
            Point temp = Point.valueOf(minX, y);
            List<PointCoordMap> list = pointsToModel.get(temp);
            if (list != null && list.size() > 0)
                return temp;
        }

        return null;
    }

    public Envelope getEnvelope() {
        if (envelope == null) {
            envelope = new Envelope();
            for (int i = 0; i < getNumCoords(); i++) {
                envelope.expandToInclude(getCoord(i));
            }
        }
        return envelope;
    }

    /**
     * Currently a simple stupid implementation for detecting if 2 polygons overlap. Currently it
     * only check the points not the real coordinates so this method must be used with care. Just
     * iterates through all the edges in one shape and checks if it overlap with an edge in the
     * other shape.
     * 
     * <pre>
     *  ------------------
     *  |   ---------    |
     *  |   |       |    |
     *  ----|-------|-----
     *      |       |
     *      ---------
     * </pre>
     * 
     * Above is considered overlapping but below is <em>only</em> if acceptTouches is true:
     * 
     * <pre>
     *  ------------------
     *  |                |
     *  |                |
     *  ------------------
     *      |       |
     *      ---------
     * </pre>
     * 
     * <p>
     * <b>Note:</b> If one of the shapes is a point and the other is a line or a point then
     * acceptTouches must be true other or this method will always return false.
     * </p>
     * 
     * @param shape2 other shape to test against
     * @param acceptTouches if true then this method will return true if the two shapes simply touch
     *        (but don't fully overlap). See above for more details
     * @return true if the two shapes overlap
     */
    public boolean overlap(PrimitiveShape shape2, boolean treatUnknownAsPolygon,
            boolean acceptTouches) {

        if (getNumPoints() == 0)
            return false;

        if (shape2.getNumPoints() == 0)
            return false;

        if (getNumPoints() == 1)
            return shape2.contains(getPoint(0), treatUnknownAsPolygon, true)
                    && isAcceptableIntersection(shape2, getPoint(0), acceptTouches);

        if (shape2.getNumPoints() == 1)
            return contains(shape2.getPoint(0), treatUnknownAsPolygon, true)
                    && isAcceptableIntersection(this, shape2.getPoint(0), acceptTouches);

        Point last, current;

        PrimitiveShapeIterator piter = PrimitiveShapeIterator.getPathIterator(this);
        piter.setPolygon(isPolygon(treatUnknownAsPolygon));
        Shape s = piter.toShape();

        PrimitiveShapeIterator piter2 = PrimitiveShapeIterator.getPathIterator(shape2);
        piter2.setPolygon(isPolygon(treatUnknownAsPolygon));
        Shape s2 = piter2.toShape();

        for (Point point : shape2) {
            if (s.contains(point.getX(), point.getY())
                    && isAcceptableIntersection(this, point, acceptTouches))
                return true;
        }

        Iterator<Point> iter = iterator();
        last = iter.next();

        while (iter.hasNext()) {
            current = iter.next();

            // now check if this shape's vertices are contained in the other shape
            if (s2.contains(last.getX(), last.getY())
                    && isAcceptableIntersection(shape2, last, acceptTouches))
                return true;

            if (edgeIntersect(last, current, shape2, acceptTouches))
                return true;

            last = current;
        }
        if (isPolygon(treatUnknownAsPolygon)) {
            // do edge from last point to first point
            if (edgeIntersect(getPoint(getNumPoints() - 1), getPoint(0), shape2, acceptTouches))
                return true;
        }

        return false;
    }

    /**
     * detects if the edge intersects with any of the edges in the shape
     */
    private boolean edgeIntersect(Point last, Point current, PrimitiveShape shape2,
            boolean acceptTouches) {
        Iterator<Point> iter2 = shape2.iterator();
        Point last2 = iter2.next();

        while (iter2.hasNext()) {
            Point current2 = iter2.next();

            Point intersection = EditUtils.instance.intersectingLines(last, current, last2,
                    current2);
            if (intersection != null
                    && isAcceptableIntersection(intersection, last2, current2, acceptTouches)
                    && isAcceptableIntersection(intersection, last, current, acceptTouches)) {
                // TODO compare coordinates to ensure it is a real intersection.
                return true;
            }
            last2 = current2;
        }

        return false;
    }

    /**
     * Detects if the intersection is just a touch. If it is <em>not</em> then it returns true. If
     * it is then it returns true only if a touch is considered to be an intersection.
     * 
     * @see #isAcceptableIntersection(Point, Point, Point, boolean)
     */
    private boolean isAcceptableIntersection(PrimitiveShape shape, Point point,
            boolean acceptTouches) {
        if (shape.getNumPoints() == 1) {
            if (point.equals(shape.getPoint(0)))
                return acceptTouches;
        }
        Envelope envelope2 = new Envelope(point.getX() - 0.1, point.getX() + 0.1,
                point.getY() - 0.1, point.getY() + 0.1);
        if (EditUtils.instance.overEdgePixelPrecision(shape, envelope2)) {
            return acceptTouches;
        }
        return true;
    }

    /**
     * Detects if the intersection is just a touch. If it is <em>not</em> then it returns true. If
     * it is then it returns true only if a touch is considered to be an intersection.
     * 
     * @see #isAcceptableIntersection(PrimitiveShape, Point, boolean)
     */
    private boolean isAcceptableIntersection(Point intersection, Point endPoint1, Point endPoint2,
            boolean acceptTouches) {
        if (intersection.equals(endPoint1) || intersection.equals(endPoint2))
            return acceptTouches;
        return true;
    }

    private boolean isPolygon(boolean treatUnknownAsPolygon) {
        return owner.getShapeType() == ShapeType.POLYGON
                || (owner.getShapeType() == ShapeType.UNKNOWN && treatUnknownAsPolygon);
    }

    public List<Point> getPoints() {
        List<Point> result = new ArrayList<Point>();
        for (PointCoordMap point : points) {
            result.add(point.point);
        }
        return result;
    }
}
