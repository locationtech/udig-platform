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
package net.refractions.udig.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import net.refractions.udig.core.StaticProvider;

import org.geotools.data.DataUtilities;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

@SuppressWarnings("nls")
public class FidSelectionTest {

    private ArrayList<SimpleFeature> features;
    private SimpleFeature feature1;
    private SimpleFeature feature2;

    @Before
    public void setUp() throws Exception {
        SimpleFeatureType type = DataUtilities.createType("type", "name:String,id:int"); //$NON-NLS-1$ //$NON-NLS-2$

        features = new ArrayList<SimpleFeature>(2);

        final String name1 = "name1"; //$NON-NLS-1$
        final String name2 = "name2"; //$NON-NLS-1$
        
        feature1 = SimpleFeatureBuilder.build(type, new Object[]{name1, 1},"1");
        feature2 = SimpleFeatureBuilder.build(type, new Object[]{name2, 2},"2");

        features.add(feature1);
        features.add(feature2);

    }

    @Test
    public void testGetFirstElement() {
        Set<String> fids=new HashSet<String>();
        FidSelection selection=new FidSelection(fids, new StaticProvider<Collection<SimpleFeature>>(features));
        
        try{
            selection.getFirstElement();
            fail("should throw exception cause end programmers should be calling isEmpty() first"); //$NON-NLS-1$
        }catch( NoSuchElementException e ){
            // good
        }
        
        fids.add( feature2.getID() );
        
        selection=new FidSelection(fids, new StaticProvider<Collection<SimpleFeature>>(features));
        assertEquals( feature2, selection.getFirstElement() );

        fids.add( feature1.getID() );
        
        selection=new FidSelection(fids, new StaticProvider<Collection<SimpleFeature>>(features));
        assertEquals( feature1, selection.getFirstElement() );

        selection=new FidSelection(fids, new StaticProvider<Collection<SimpleFeature>>(new ArrayList<SimpleFeature>()));
        try{
            selection.getFirstElement();
            fail("should throw exception cause end programmers should be calling isEmpty() first"); //$NON-NLS-1$
        }catch( NoSuchElementException e ){
            // good
        }
    }

    @Test
    public void testIterator() {

        Set<String> fids=new HashSet<String>();
        FidSelection selection=new FidSelection(fids, new StaticProvider<Collection<SimpleFeature>>(features));
        
        assertNumberElements( selection, 0 );
        
        fids.add( feature2.getID() );
        fids.add( feature1.getID() );
        selection=new FidSelection(fids, new StaticProvider<Collection<SimpleFeature>>(features));
        assertNumberElements( selection, 2 );
        
        selection=new FidSelection(fids, new StaticProvider<Collection<SimpleFeature>>(new ArrayList<SimpleFeature>()));
        assertNumberElements(selection, 0 );

    }

    private void assertNumberElements( final FidSelection selection, final int i ) {
        int j=0;
        for( Iterator<?> iter = selection.iterator(); iter.hasNext(); ) {
            iter.next();
            j++;
        }
        
        assertEquals(i,j);
    }

    @Test
    public void testSize() {
        Set<String> fids=new HashSet<String>();
        FidSelection selection=new FidSelection(fids, new StaticProvider<Collection<SimpleFeature>>(features));
        
        assertEquals( 0, selection.size() );
        
        fids.add( feature2.getID() );
        fids.add( feature1.getID() );
        selection=new FidSelection(fids, new StaticProvider<Collection<SimpleFeature>>(features));
        assertEquals( 2, selection.size() );

        selection=new FidSelection(fids, new StaticProvider<Collection<SimpleFeature>>(new ArrayList<SimpleFeature>()));
        assertEquals(0, selection.size() );
    }

    @Test
    public void testToArray() {
        Set<String> fids=new HashSet<String>();
        FidSelection selection=new FidSelection(fids, new StaticProvider<Collection<SimpleFeature>>(features));
        
        assertEqualsArray( new Object[0], selection.toArray() );
        
        fids.add( feature2.getID() );
        fids.add( feature1.getID() );
        selection=new FidSelection(fids, new StaticProvider<Collection<SimpleFeature>>(features));
        assertEqualsArray( new Object[]{feature1,feature2}, selection.toArray() );

        selection=new FidSelection(fids, new StaticProvider<Collection<SimpleFeature>>(new ArrayList<SimpleFeature>()));
        assertEqualsArray(new Object[0], selection.toArray() );
        
    }

    private void assertEqualsArray( Object[] objects, Object[] objects2 ) {
        assertEquals( objects.length, objects2.length);
        for( int i = 0; i < objects2.length; i++ ) {
            assertEquals( objects[i], objects2[i]);
        }
    }

    @Test
    public void testToList() {
        Set<String> fids=new HashSet<String>();
        FidSelection selection=new FidSelection(fids, new StaticProvider<Collection<SimpleFeature>>(features));
        
        assertEqualsArray( new Object[0], selection.toList().toArray() );
        
        fids.add( feature2.getID() );
        fids.add( feature1.getID() );
        selection=new FidSelection(fids, new StaticProvider<Collection<SimpleFeature>>(features));
        assertEqualsArray( new Object[]{feature1,feature2} , selection.toList().toArray() );
        
        selection=new FidSelection(fids, new StaticProvider<Collection<SimpleFeature>>(new ArrayList<SimpleFeature>()));
        assertEqualsArray(new Object[0], selection.toList().toArray() );
        
    }

    @Test
    public void testIsEmpty() {
        Set<String> fids=new HashSet<String>();
        FidSelection selection=new FidSelection(fids, new StaticProvider<Collection<SimpleFeature>>(features));
        
        assertTrue( selection.isEmpty() );
        
        fids.add( feature2.getID() );
        fids.add( feature1.getID() );
        selection=new FidSelection(fids, new StaticProvider<Collection<SimpleFeature>>(features));
        assertFalse( selection.isEmpty() );
        
        selection=new FidSelection(fids, new StaticProvider<Collection<SimpleFeature>>(new ArrayList<SimpleFeature>()));
        assertTrue( selection.isEmpty() );
        
    }

}
