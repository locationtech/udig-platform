package net.refractions.udig.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import net.refractions.udig.core.StaticProvider;

import org.geotools.data.DataUtilities;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;

import junit.framework.TestCase;

public class FidSelectionTest extends TestCase {

    private ArrayList<Feature> features;
    private Feature feature1;
    private Feature feature2;

    protected void setUp() throws Exception {
        super.setUp();
        FeatureType type = DataUtilities.createType("type", "name:String,id:int"); //$NON-NLS-1$ //$NON-NLS-2$

        features = new ArrayList<Feature>(2);

        final String name1 = "name1"; //$NON-NLS-1$
        final String name2 = "name2"; //$NON-NLS-1$

        feature1 = type.create(new Object[]{name1, 1});
        feature2 = type.create(new Object[]{name2, 2});

        features.add(feature1);
        features.add(feature2);

    }

    public void testGetFirstElement() {
        Set<String> fids=new HashSet<String>();
        FidSelection selection=new FidSelection(fids, new StaticProvider<Collection<Feature>>(features));

        try{
            selection.getFirstElement();
            fail("should throw exception cause end programmers should be calling isEmpty() first"); //$NON-NLS-1$
        }catch( NoSuchElementException e ){
            // good
        }

        fids.add( feature2.getID() );

        selection=new FidSelection(fids, new StaticProvider<Collection<Feature>>(features));
        assertEquals( feature2, selection.getFirstElement() );

        fids.add( feature1.getID() );

        selection=new FidSelection(fids, new StaticProvider<Collection<Feature>>(features));
        assertEquals( feature1, selection.getFirstElement() );

        selection=new FidSelection(fids, new StaticProvider<Collection<Feature>>(new ArrayList<Feature>()));
        try{
            selection.getFirstElement();
            fail("should throw exception cause end programmers should be calling isEmpty() first"); //$NON-NLS-1$
        }catch( NoSuchElementException e ){
            // good
        }
    }

    public void testIterator() {

        Set<String> fids=new HashSet<String>();
        FidSelection selection=new FidSelection(fids, new StaticProvider<Collection<Feature>>(features));

        assertNumberElements( selection, 0 );

        fids.add( feature2.getID() );
        fids.add( feature1.getID() );
        selection=new FidSelection(fids, new StaticProvider<Collection<Feature>>(features));
        assertNumberElements( selection, 2 );

        selection=new FidSelection(fids, new StaticProvider<Collection<Feature>>(new ArrayList<Feature>()));
        assertNumberElements(selection, 0 );

    }

    private void assertNumberElements( final FidSelection selection, final int i ) {
        int j=0;
        for( Iterator iter = selection.iterator(); iter.hasNext(); ) {
            iter.next();
            j++;
        }

        assertEquals(i,j);
    }

    public void testSize() {
        Set<String> fids=new HashSet<String>();
        FidSelection selection=new FidSelection(fids, new StaticProvider<Collection<Feature>>(features));

        assertEquals( 0, selection.size() );

        fids.add( feature2.getID() );
        fids.add( feature1.getID() );
        selection=new FidSelection(fids, new StaticProvider<Collection<Feature>>(features));
        assertEquals( 2, selection.size() );

        selection=new FidSelection(fids, new StaticProvider<Collection<Feature>>(new ArrayList<Feature>()));
        assertEquals(0, selection.size() );
    }

    public void testToArray() {
        Set<String> fids=new HashSet<String>();
        FidSelection selection=new FidSelection(fids, new StaticProvider<Collection<Feature>>(features));

        assertEqualsArray( new Object[0], selection.toArray() );

        fids.add( feature2.getID() );
        fids.add( feature1.getID() );
        selection=new FidSelection(fids, new StaticProvider<Collection<Feature>>(features));
        assertEqualsArray( new Object[]{feature1,feature2}, selection.toArray() );

        selection=new FidSelection(fids, new StaticProvider<Collection<Feature>>(new ArrayList<Feature>()));
        assertEqualsArray(new Object[0], selection.toArray() );

    }

    private void assertEqualsArray( Object[] objects, Object[] objects2 ) {
        assertEquals( objects.length, objects2.length);
        for( int i = 0; i < objects2.length; i++ ) {
            assertEquals( objects[i], objects2[i]);
        }
    }

    public void testToList() {
        Set<String> fids=new HashSet<String>();
        FidSelection selection=new FidSelection(fids, new StaticProvider<Collection<Feature>>(features));

        assertEqualsArray( new Object[0], selection.toList().toArray() );

        fids.add( feature2.getID() );
        fids.add( feature1.getID() );
        selection=new FidSelection(fids, new StaticProvider<Collection<Feature>>(features));
        assertEqualsArray( new Object[]{feature1,feature2} , selection.toList().toArray() );

        selection=new FidSelection(fids, new StaticProvider<Collection<Feature>>(new ArrayList<Feature>()));
        assertEqualsArray(new Object[0], selection.toList().toArray() );

    }

    public void testIsEmpty() {
        Set<String> fids=new HashSet<String>();
        FidSelection selection=new FidSelection(fids, new StaticProvider<Collection<Feature>>(features));

        assertTrue( selection.isEmpty() );

        fids.add( feature2.getID() );
        fids.add( feature1.getID() );
        selection=new FidSelection(fids, new StaticProvider<Collection<Feature>>(features));
        assertFalse( selection.isEmpty() );

        selection=new FidSelection(fids, new StaticProvider<Collection<Feature>>(new ArrayList<Feature>()));
        assertTrue( selection.isEmpty() );

    }

}
