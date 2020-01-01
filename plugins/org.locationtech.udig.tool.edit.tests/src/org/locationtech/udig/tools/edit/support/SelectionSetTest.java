/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.geom.AffineTransform;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;

public class SelectionSetTest {

    private TestEditBlackboard blackboard;
    private TestEditBlackboardListener listener;

    @Before
    public void setUp() throws Exception {
        blackboard=new TestEditBlackboard();
        EditGeom editGeom = blackboard.getGeoms().get(0);
        blackboard.addPoint(10,10,editGeom.getShell());
        blackboard.addPoint(20,10,editGeom.getShell());
        blackboard.addPoint(30,10,editGeom.getShell());
        blackboard.addPoint(40,10,editGeom.getShell());
        blackboard.addPoint(10,15,editGeom.getShell());
        blackboard.addPoint(10,25,editGeom.getShell());
        blackboard.addPoint(10,35,editGeom.getShell());
        blackboard.selectionAdd(Point.valueOf(10,10));
        blackboard.selectionAdd(Point.valueOf(20,10));
        blackboard.selectionAdd(Point.valueOf(30,10));
        blackboard.selectionAdd(Point.valueOf(40,10));
        listener=new TestEditBlackboardListener();
        blackboard.getListeners().add(listener);
    }
    
    /*
     * Test method for 'org.locationtech.udig.tools.edit.support.SelectionSet.add(Point)'
     */
    @Test
    public void testAdd() {
        assertFalse(blackboard.selectionAdd(Point.valueOf(100,15)));
        blackboard.selectionAdd(Point.valueOf(10,15));
        assertEquals(1, listener.getAdded().size());
        assertEquals(0, listener.getRemoved().size());
        assertTrue(listener.getAdded().contains(Point.valueOf(10,15)));
        
    }

    /*
     * Test method for 'org.locationtech.udig.tools.edit.support.SelectionSet.addAll(Collection<? extends Point>)'
     */
    @Test
    public void testAddAll() {
        HashSet<Point> set=new HashSet<Point>();
        set.add(Point.valueOf(10,15));
        set.add(Point.valueOf(10,25));
        set.add(Point.valueOf(10,35));
        set.add(Point.valueOf(100,35));
        blackboard.selectionAddAll(set);
        assertEquals(3, listener.getAdded().size());
        assertEquals(0, listener.getRemoved().size());
        assertTrue(listener.getAdded().contains(Point.valueOf(10,15)));
        assertTrue(listener.getAdded().contains(Point.valueOf(10,25)));
        assertTrue(listener.getAdded().contains(Point.valueOf(10,35)));
    }

    /*
     * Test method for 'org.locationtech.udig.tools.edit.support.SelectionSet.clear()'
     */
    @Test
    public void testClear() {
        blackboard.selectionClear();
        assertEquals(0, listener.getAdded().size());
        assertEquals(4, listener.getRemoved().size());
        assertTrue(listener.getRemoved().contains(Point.valueOf(10,10)));
        assertTrue(listener.getRemoved().contains(Point.valueOf(20,10)));
        assertTrue(listener.getRemoved().contains(Point.valueOf(30,10)));
        assertTrue(listener.getRemoved().contains(Point.valueOf(40,10)));
    }
//  if not CopyOnWriteArraySet then this can be used but I'm ok with not allowing removes using iterator.
//    /*
//     * Test method for 'org.locationtech.udig.tools.edit.support.SelectionSet.iterator()'
//     */
//    public void testIterator() {
//        Iterator<Point> iter = blackboard.getSelection().iterator();
//        while (iter.hasNext()){
//            Point p=iter.next();
//            iter.remove();
//            assertEquals(0, listener.getAdded().size());
//            assertEquals(1, listener.getRemoved().size());
//            assertTrue(listener.getRemoved().contains(p));
//        }
//    }

    /*
     * Test method for 'org.locationtech.udig.tools.edit.support.SelectionSet.remove(Object)'
     */
    @Test
    public void testRemove() {
        blackboard.selectionRemove(Point.valueOf(10,10));
        assertEquals(0, listener.getAdded().size());
        assertEquals(1, listener.getRemoved().size());
        assertTrue(listener.getRemoved().contains(Point.valueOf(10,10)));
    }

    /*
     * Test method for 'org.locationtech.udig.tools.edit.support.SelectionSet.removeAll(Collection<?>)'
     */
    @Test
    public void testRemoveAll() {
        HashSet<Point> set=new HashSet<Point>();
        set.add(Point.valueOf(10,10));
        set.add(Point.valueOf(20,10));
        set.add(Point.valueOf(30,10));
        blackboard.selectionRemoveAll(set);
        assertEquals(0, listener.getAdded().size());
        assertEquals(3, listener.getRemoved().size());
        assertTrue(listener.getRemoved().contains(Point.valueOf(10,10)));
        assertTrue(listener.getRemoved().contains(Point.valueOf(20,10)));
        assertTrue(listener.getRemoved().contains(Point.valueOf(30,10)));

    }

    /*
     * Test method for 'org.locationtech.udig.tools.edit.support.SelectionSet.retainAll(Collection<?>)'
     */
    @Test
    public void testRetainAll() {
        HashSet<Point> set=new HashSet<Point>();
        set.add(Point.valueOf(10,10));
        set.add(Point.valueOf(20,10));
        set.add(Point.valueOf(30,15));
        blackboard.selectionRetainAll(set);

        assertEquals(0, listener.getAdded().size());
        assertEquals(2, listener.getRemoved().size());
        assertTrue(listener.getRemoved().contains(Point.valueOf(30,10)));
        assertTrue(listener.getRemoved().contains(Point.valueOf(40,10)));
        
    }
    
    @Test
    public void testMoveVertex() throws Exception {
        blackboard.moveCoords(10,10, 10,15);
        
        assertTrue(blackboard.getSelection().contains(Point.valueOf(10,15)));
        assertEquals(1, listener.getNum());
        assertEquals(1, listener.getAdded().size());
        assertTrue(listener.getAdded().contains(Point.valueOf(10,15)));
    }
    
    @Test
    public void testRemoveVertex() throws Exception {
        blackboard.removeCoordsAtPoint(10,10);
        
        assertEquals(3, blackboard.getSelection().size());
        assertEquals(1, listener.getNum());
        assertFalse( blackboard.getSelection().contains(Point.valueOf(10,10)));
        assertTrue(listener.getRemoved().contains(Point.valueOf(10,10)));
    }
    
    @Test
    public void testSetTranslationTransform() throws Exception {
        blackboard.setToScreenTransform(AffineTransform.getTranslateInstance(0,5));
        assertEquals(1, listener.getNum());
        assertTrue(listener.getAdded().contains(Point.valueOf(10,15)));
        assertTrue(listener.getAdded().contains(Point.valueOf(20,15)));
        assertTrue(listener.getAdded().contains(Point.valueOf(30,15)));
        assertTrue(listener.getAdded().contains(Point.valueOf(40,15)));
        
        assertTrue(listener.getRemoved().contains(Point.valueOf(10,10)));
        assertTrue(listener.getRemoved().contains(Point.valueOf(20,10)));
        assertTrue(listener.getRemoved().contains(Point.valueOf(30,10)));
        assertTrue(listener.getRemoved().contains(Point.valueOf(40,10)));
    }
    
    @Test
    public void testSetTranslationScale() throws Exception {
        blackboard.setToScreenTransform(AffineTransform.getScaleInstance(.5,.5));
        assertEquals(1, listener.getNum());
        assertTrue(listener.getAdded().contains(Point.valueOf(5,5)));
        assertTrue(listener.getAdded().contains(Point.valueOf(10,5)));
        assertTrue(listener.getAdded().contains(Point.valueOf(15,5)));
        assertTrue(listener.getAdded().contains(Point.valueOf(20,5)));
        
        assertTrue(listener.getRemoved().contains(Point.valueOf(10,10)));
        assertTrue(listener.getRemoved().contains(Point.valueOf(20,10)));
        assertTrue(listener.getRemoved().contains(Point.valueOf(30,10)));
        assertTrue(listener.getRemoved().contains(Point.valueOf(40,10)));
    }
    
    /**
     * now case where 2 coords map to same point before set translation but 2 different ones after
     * both should be selected in this case.
     * @throws Exception
     */
    @Test
    public void testSetTransformScale() throws Exception {
        GeometryFactory factory = new GeometryFactory();
        LinearRing linearRing = factory.createLinearRing(new Coordinate[]{
                new Coordinate(10, 10),
                new Coordinate(10, 20.5),
                new Coordinate(10, 20),
                new Coordinate(20, 20),
                new Coordinate(10, 10)
        });
        
        blackboard.selectionClear();
        blackboard.setGeometries(linearRing, null);
        assertEquals(2, blackboard.getCoords(10,20).size());
        blackboard.selectionAdd(Point.valueOf(10,20));
        blackboard.setToScreenTransform(AffineTransform.getScaleInstance(1,2));
            
        assertEquals(2, blackboard.getSelection().size());
        assertTrue(blackboard.getSelection().contains(Point.valueOf(10,40)));
        assertTrue(blackboard.getSelection().contains(Point.valueOf(10,41)));
    }
    
    @Test
    public void testSetGeometries() throws Exception {
        GeometryFactory factory = new GeometryFactory();
        int offset=0;
        LinearRing linearRing = factory.createLinearRing(new Coordinate[]{
                new Coordinate(offset+5, 7),
                new Coordinate(offset+8, 7),
                new Coordinate(offset+8, 8),
                new Coordinate(offset+5, 8),
                new Coordinate(offset+5, 7)
        });
        
        blackboard.setGeometries(linearRing, null);
        
        assertTrue(listener.getRemoved().contains(Point.valueOf(10,10)));
        assertTrue(listener.getRemoved().contains(Point.valueOf(20,10)));
        assertTrue(listener.getRemoved().contains(Point.valueOf(30,10)));
        assertTrue(listener.getRemoved().contains(Point.valueOf(40,10)));
        assertEquals(0, listener.getAdded().size());
        assertEquals(4, listener.getRemoved().size());
    }

    
    class TestEditBlackboardListener extends EditBlackboardAdapter{
        private Set<Point> added;
        private Set<Point> removed;
        private int num=0;

        
        @SuppressWarnings("unchecked")
        @Override
        public void changed( EditBlackboardEvent e ) {
            if( e.getType()!=EditBlackboardEvent.EventType.SELECTION)
                return;
            added=(Set<Point>) e.getNewValue();
            removed=(Set<Point>) e.getOldValue();
            num++;
        }

        /**
         * @return Returns the added.
         */
        public Set<Point> getAdded() {
            return added;
        }

        /**
         * @return Returns the removed.
         */
        public Set<Point> getRemoved() {
            return removed;
        }

        /**
         * @return Returns the num.
         */
        public int getNum() {
            return num;
        }
    }
    
}
