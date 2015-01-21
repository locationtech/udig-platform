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
package org.locationtech.udig.issues.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.udig.core.enums.Priority;
import org.locationtech.udig.core.enums.Resolution;
import org.locationtech.udig.issues.FeatureIssue;
import org.locationtech.udig.issues.IIssue;
import org.locationtech.udig.issues.IRemoteIssuesList;
import org.locationtech.udig.issues.listeners.IIssuesListListener;
import org.locationtech.udig.issues.listeners.IssuesListEvent;
import org.locationtech.udig.issues.listeners.IssuesListEventType;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureStore;
import org.geotools.data.Query;
import org.geotools.data.Transaction;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class StrategizedIssuesListTest {

    public SimpleFeatureType featureType;
    public IRemoteIssuesList list;
    public DataStore store;
    public static final CoordinateReferenceSystem crs;
    static{
        try {
            crs=CRS.decode("EPSG:4326");//$NON-NLS-1$
        } catch (NoSuchAuthorityCodeException e) {
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        } catch (FactoryException e) {
            throw (RuntimeException) new RuntimeException( ).initCause( e );
		} 
    }

    @Before
    public void setUp() throws Exception {
        FeatureIssue.setTesting(true);
        DataStore[] ds=new DataStore[1];
        SimpleFeatureType[] ft=new SimpleFeatureType[1];
        
        list=IssuesListTestHelper.createInMemoryDatastoreIssuesList(ds, ft);
        
        featureType=ft[0];
        store=ds[0];

        IssuesListTestHelper.addFeatures(ds[0], ft[0]);
        
        list.refresh();
    }

    @Test
    public void testInitialLoad() throws Exception {
        assertEquals(4, list.size());
        assertEquals("0", list.get(0).getId()); //$NON-NLS-1$
        assertEquals("1", list.get(1).getId()); //$NON-NLS-1$
        assertEquals("2", list.get(2).getId()); //$NON-NLS-1$
        assertEquals("3", list.get(3).getId()); //$NON-NLS-1$
        assertEquals( 4, store.getFeatureSource(featureType.getName().getLocalPart()).getCount(Query.ALL));
    }
    
    @Test
    public void testAddIIssue() throws Exception {
        FeatureIssue issue = IssuesListTestHelper.createFeatureIssue("new"); //$NON-NLS-1$
        list.add(issue);
        assertEquals(5, list.size());
        assertEquals("new", list.get(4).getId()); //$NON-NLS-1$
        list.refresh();
        assertEquals(5, list.size());
        assertEquals("new", list.get(4).getId()); //$NON-NLS-1$
        assertEquals("groupID", list.get(4).getGroupId()); //$NON-NLS-1$
        assertEquals("test description", list.get(4).getDescription()); //$NON-NLS-1$
        assertEquals( 5, store.getFeatureSource(featureType.getName().getLocalPart()).getCount(Query.ALL));
    }

    @Test
    public void testRemoveInt() throws Exception {
        list.remove(1);
        assertEquals(3, list.size());
        assertEquals("0", list.get(0).getId()); //$NON-NLS-1$
        assertEquals("2", list.get(1).getId()); //$NON-NLS-1$
        assertEquals("3", list.get(2).getId()); //$NON-NLS-1$
        assertEquals( 3, store.getFeatureSource(featureType.getName().getLocalPart()).getCount(Query.ALL));
    }
    
    @Test
    public void testBackendRemovedIssue() throws Exception{
    	FeatureStore<SimpleFeatureType, SimpleFeature> fs = (FeatureStore<SimpleFeatureType, SimpleFeature>) store
				.getFeatureSource(featureType.getName().getLocalPart());
        FilterFactory factory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        Expression expr2 = factory.literal("1");
		Expression expr1 = factory.property(IssuesListTestHelper.ISSUE_ID_ATTR);
		Filter filter = factory.equals(expr1, expr2); 
        fs.removeFeatures(filter);
        list.refresh();
        for( IIssue issue : list ) {
            System.out.println(issue.getId());
        }
        assertEquals(3, list.size());
        assertEquals("0",list.get(0).getId()); //$NON-NLS-1$
        assertEquals("2",list.get(1).getId()); //$NON-NLS-1$
        assertEquals("3",list.get(2).getId()); //$NON-NLS-1$
    }

    @Test
    public void testBackendAddedIssue()throws Exception{
        IssuesListTestHelper.createFeature("new",  //$NON-NLS-1$
                Resolution.IN_PROGRESS, 
                Priority.CRITICAL, 
                new ReferencedEnvelope(0,10,0,10,crs), 
                store.getFeatureWriterAppend(featureType.getName().getLocalPart(), Transaction.AUTO_COMMIT));
        final IssuesListEvent[] change=new IssuesListEvent[1];
        list.addListener(new IIssuesListListener(){

            public void notifyChange( IssuesListEvent event ) {
                change[0]=event;
            }
            
        });
        list.refresh();
        assertEquals(5, list.size());
        assertEquals("new", list.get(4).getId()); //$NON-NLS-1$
        assertEquals(IssuesListEventType.REFRESH, change[0].getType());
    }
    
    @Test
    public void testRemoveMany() throws Exception {
        List<IIssue> sublist = new ArrayList<IIssue>();
        sublist.addAll(list.subList(0, 2));
        list.removeAll(sublist);
        assertEquals(2, list.size());
        assertEquals("2", list.get(0).getId()); //$NON-NLS-1$
        assertEquals("3", list.get(1).getId()); //$NON-NLS-1$
    }

    @Test
    public void testAddMany() throws Exception {
        List<IIssue> newIssues=new ArrayList<IIssue>();
        newIssues.add(IssuesListTestHelper.createFeatureIssue("new1")); //$NON-NLS-1$
        newIssues.add(IssuesListTestHelper.createFeatureIssue("new2")); //$NON-NLS-1$
        list.addAll(newIssues);
        list.refresh();
        assertEquals(6, list.size());
    }

    @Test
    public void testModifyIssue() throws Exception{
        String newDescription = "new modified description"; //$NON-NLS-1$
        
        list.clear();
        list.add(IssuesListTestHelper.createFeatureIssue("test")); //$NON-NLS-1$
        
        list.refresh();
        assertEquals(1, list.size());
        
        IIssue issue = list.get(0);
        issue.setDescription(newDescription);
        ((IRemoteIssuesList)list).save(issue);
        FilterFactory factory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());

        Expression expr2 = factory.literal(issue.getId());
		Expression expr1 = factory.property(IssuesListTestHelper.ISSUE_ID_ATTR);
		Filter filter = factory.equals(expr1, expr2); 

        SimpleFeature next = store.getFeatureSource(featureType.getName().getLocalPart()).getFeatures(filter).features().next();
        assertEquals(newDescription, next.getAttribute(IssuesListTestHelper.DESCRIPTION_ATTR));
    }
    
    @Test
    public void testAddIssueWithBounds() throws Exception {
        list.add(IssuesListTestHelper.createFeatureIssue("id")); //$NON-NLS-1$
        // no exception? good.
    }

    @Test
    public void testNullID() throws Exception {
        list.clear();
        list.add(IssuesListTestHelper.createFeatureIssue(null));
        assertNotNull(list.get(0).getId());
        
        SimpleFeature next = this.store.getFeatureSource(this.featureType.getName().getLocalPart()).getFeatures().features().next();
        Object id = next.getAttribute("id"); //$NON-NLS-1$
        assertNotNull(id);
    }

    @Test
    public void testRefreshWithModifiedFeatures() throws Exception {
        String description="New Description: blah blah blah"; //$NON-NLS-1$
        list.get(0).setDescription(description);
        
        list.refresh();
        
        assertEquals(description, list.get(0).getDescription());
    }
}
