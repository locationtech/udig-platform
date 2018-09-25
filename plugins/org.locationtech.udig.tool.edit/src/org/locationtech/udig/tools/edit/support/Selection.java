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

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.support.EditBlackboardEvent.EventType;

/**
 * A set of points that are on the EditBlackboard. Only the edit black board can edit this set. All
 * public add methods will throw and exception.
 * 
 * @author jones
 * @since 1.1.0
 */
public class Selection extends AbstractSet<Point> implements Set<Point> {

    /** long serialVersionUID field */
    private static final long serialVersionUID = 3103252189848308511L;
    private EditBlackboard blackboard;
    protected Map<Point, Collection<LazyCoord>> coordMap = new ConcurrentHashMap<Point, Collection<LazyCoord>>();
    boolean notify = true;
    protected BlackboardListener blackboardListener = new BlackboardListener();

    public Selection( EditBlackboard blackboard ) {
        this.blackboard = blackboard;
        blackboard.getListeners().add(getBlackboardListener());
    }

    public Selection( Selection selection2 ) {
        this(selection2.blackboard);
        coordMap.putAll(selection2.coordMap);
    }

    protected BlackboardListener getBlackboardListener() {
        return blackboardListener;
    }

    /**
     * The list of lazy coordinates. The list is unmodifiable.
     * 
     * @param point
     * @return
     */
    synchronized Collection<LazyCoord> getLazyCoordinates( Point point ) {
        return Collections.unmodifiableCollection(coordMap.get(point));
    }

    /**
     * Adds a point to the selection and all the Coordinates that are in the editblackboard at that
     * point.
     * 
     * @see #getCoordinates(Point);
     * @param o
     * @return
     */
    boolean doAdd( Point o ) {
        boolean result;
        synchronized (this) {
            result = addInternal(o);
        }
        if (result)
            notifyListeners(Collections.singleton(o), null);
        return result;
    }

    /**
     * @param o
     * @param coords
     * @return
     */
    protected boolean addInternal( Point o ) {
        List<LazyCoord> coords = this.blackboard.coordMapping.get(o);
        if (coords != null && coords.size() != 0 && !coordMap.containsKey(o)) {
            coordMap.put(o, new ArrayList<LazyCoord>(coords));
            return true;
        }
        return false;
    }

    boolean doAddAll( Collection< ? extends Point> c ) {
        Set<Point> added = new HashSet<Point>();
        boolean result = false;
        synchronized (this) {
            for( Point point : c ) {
                if (point == null)
                    continue;
                boolean wasAdded = addInternal(point);
                if (wasAdded)
                    added.add(point);
                result = result || wasAdded;
            }
        }
        if (result)
            notifyListeners(added, null);
        return result;
    }

    void doClear() {
        Set<Point> set = null;
        synchronized (this) {
            set = new HashSet<Point>(this.coordMap.keySet());
            coordMap.clear();
        }
        if (set != null && !set.isEmpty())
            notifyListeners(null, set);
    }

    public synchronized boolean contains( Object o ) {
        return coordMap.containsKey(o);
    }
    public synchronized boolean containsAll( Collection< ? > c ) {
        return coordMap.keySet().containsAll(c);
    }

    public synchronized boolean isEmpty() {
        return coordMap.isEmpty();
    }
    public Iterator<Point> iterator() {
        return new Iterator<Point>(){
            Iterator<Point> iter = coordMap.keySet().iterator();
            Point lastPoint;
            public boolean hasNext() {
                return iter.hasNext();
            }

            public Point next() {
                lastPoint = iter.next();
                return lastPoint;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

        };
    }
    boolean doRemove( Object o ) {
        boolean result;
        synchronized (this) {
            result = coordMap.remove(o) != null;
        }
        if (result)
            notifyListeners(null, Collections.singleton((Point) o));
        return result;
    }
    boolean doRetainAll( Collection< ? > c ) {
        boolean result;
        Set<Point> removed = new HashSet<Point>();
        synchronized (this) {
            for( Point p : coordMap.keySet() ) {
                if (p == null)
                    continue;
                if (!c.contains(p)) {
                    removed.add((Point) p);
                }
            }
            result = doRemoveAll(removed, false);
        }
        if (!removed.isEmpty())
            notifyListeners(null, removed);
        return result;
    }

    boolean doRemoveAll( Collection< ? > c, boolean notify ) {
        boolean result = false;
        Set<Point> removed = new HashSet<Point>();
        synchronized (this) {
            for( Object point : c ) {
                if (point == null)
                    continue;
                if (this.coordMap.remove(point) != null) {
                    removed.add((Point) point);
                    result = true;
                }
            }
        }
        if (notify && result)
            notifyListeners(null, removed);

        return result;
    }

    public synchronized int size() {
        return coordMap.size();
    }
    public synchronized Object[] toArray() {
        return coordMap.keySet().toArray();
    }
    public synchronized <T> T[] toArray( T[] a ) {
        return coordMap.keySet().toArray(a);
    }

    protected void notifyListeners( Set<Point> added, Set<Point> removed ) {
        if (!notify)
            return;

        if (added == null) {
            added = Collections.<Point> emptySet();
        }
        if (removed == null) {
            removed = Collections.<Point> emptySet();
        }
        blackboard.notify(new EditBlackboardEvent(blackboard, this, EventType.SELECTION, removed,
                added));
    }

    @Override
    public synchronized String toString() {
        return coordMap.keySet().toString();
    }
    class BlackboardListener extends EditBlackboardAdapter {

        @Override
        public synchronized void changed( EditBlackboardEvent e ) {
            Object oldValue = e.getOldValue();
            switch( e.getType() ) {
            case SET_GEOMS:
                if (size() > 0)
                    doClear();
                break;
            case MOVE_POINT:
                Point newValue = (Point) e.getNewValue();
                if (movePoint(oldValue, newValue)) {
                    notifyListeners(Collections.singleton(newValue), Collections
                            .singleton((Point) oldValue));
                }

                break;
            case REMOVE_POINT:
                doRemove(oldValue);
                break;

            case TRANFORMATION:
                Map<Point, List<Point>> p = e.getTransformationMap();
                HashSet<Point> added = new HashSet<Point>();
                try {
                    notify = false;
                    for( Map.Entry<Point, List<Point>> entry : p.entrySet() ) {
                        if (doRemove(entry.getKey())) {
                            doAddAll(entry.getValue());
                            added.addAll(entry.getValue());
                        }
                    }
                } finally {
                    notify = true;
                }
                notifyListeners(added, p.keySet());

                break;
            default:
                break;
            }

            assertValid();
        }

        /**
         * @param oldValue
         * @param newValue
         */
        private boolean movePoint( Object oldValue, Point newValue ) {
            if (newValue.equals(oldValue))
                return false;
            if (!contains(oldValue))
                return false;
            Collection<LazyCoord> oldCoords = coordMap.get(oldValue);
            Collection<LazyCoord> newCoords = coordMap.get(newValue);
            coordMap.remove(oldValue);
            if (newCoords == null) {
                newCoords = new ArrayList<LazyCoord>();
                coordMap.put(newValue, newCoords);
            }
            newCoords.addAll(oldCoords);
            return true;
        }

        @Override
        public synchronized void batchChange( List<EditBlackboardEvent> e ) {
            if (e.isEmpty())
                return;
            e.get(0).getEditBlackboard().startBatchingEvents();
            try {
                for( EditBlackboardEvent event : e ) {
                    changed(event);
                }
            }
            finally {
                e.get(0).getEditBlackboard().fireBatchedEvents();
            }
        }

    }

    void add( Point point, List<LazyCoord> coords ) {
        Collection<LazyCoord> mappedCoords = coordMap.get(point);
        if (mappedCoords == null) {
            mappedCoords = new ArrayList<LazyCoord>();
            coordMap.put(point, mappedCoords);
        }
        mappedCoords.addAll(coords);
    }

    public void disconnect() {
        blackboard.getListeners().remove(this.blackboardListener);
        notify = false;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify( boolean notify ) {
        this.notify = notify;
    }

    public void assertValid() {
        if (!EditPlugin.isDebugging(EditPlugin.RUN_ASSERTIONS))
            return;

        // for (Map.Entry<Point, Collection<LazyCoord>> entry : this.coordMap.entrySet()) {
        // List<LazyCoord> list = blackboard.coordMapping.get(entry.getKey());
        // if( list!=null && list.isEmpty() ){
        // throw new AssertionError(entry.getKey()+" is not in blackboard"); //$NON-NLS-1$
        // }
        //			
        // for (LazyCoord coord : entry.getValue()) {
        // if( !list.contains(coord) )
        // throw new AssertionError(coord+" is not in blackboard at correct location");
        // //$NON-NLS-1$
        // }
        // }
    }
}
