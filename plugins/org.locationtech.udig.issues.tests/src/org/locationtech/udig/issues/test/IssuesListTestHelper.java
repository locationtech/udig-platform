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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.locationtech.udig.core.enums.Priority;
import org.locationtech.udig.core.enums.Resolution;
import org.locationtech.udig.issues.FeatureIssue;
import org.locationtech.udig.issues.IListStrategy;
import org.locationtech.udig.issues.IRemoteIssuesList;
import org.locationtech.udig.issues.StrategizedIssuesList;
import org.locationtech.udig.issues.internal.datastore.AbstractDatastoreStrategy;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.tests.support.MapTests;

import org.geotools.data.DataStore;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.memory.MemoryDataStore;
import org.geotools.data.memory.MemoryEntry;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.feature.collection.AdaptorFeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public class IssuesListTestHelper {

    public static final String BOUNDS = "bounds"; //$NON-NLS-1$
    public static final String EXTENSION_ID_ATTR = "extensionPoint"; //$NON-NLS-1$
    public static final String ISSUE_ID_ATTR = "id"; //$NON-NLS-1$
    public static final String GROUP_ID_ATTR = "groupId"; //$NON-NLS-1$
    public static final String RESOLUTION_ATTR = "resolution"; //$NON-NLS-1$
    public static final String PRIORITY_ATTR = "priority"; //$NON-NLS-1$
    public static final String DESCRIPTION_ATTR = "description"; //$NON-NLS-1$
    public static final String ISSUE_MEMENTO_DATA_ATTR = "memento"; //$NON-NLS-1$
    public static final String VIEW_MEMENTO_DATA_ATTR = "viewMenento"; //$NON-NLS-1$
    public static void createFeature(String id, Resolution r, Priority p,
			ReferencedEnvelope bounds,
			FeatureWriter<SimpleFeatureType, SimpleFeature> writer)
			throws Exception {
        SimpleFeature feature=writer.next();
        feature.setAttribute(BOUNDS, IssuesListTestHelper.createBounds(bounds));
        feature.setAttribute(EXTENSION_ID_ATTR, FeatureIssue.EXT_ID);
        feature.setAttribute(ISSUE_ID_ATTR, id);
        feature.setAttribute(GROUP_ID_ATTR, "default"); //$NON-NLS-1$
        feature.setAttribute(RESOLUTION_ATTR, r);
        feature.setAttribute(PRIORITY_ATTR, p);
        feature.setAttribute(DESCRIPTION_ATTR, "description"); //$NON-NLS-1$
        writer.write();
    }
    public static MultiPolygon createBounds( ReferencedEnvelope env ) {
        GeometryFactory factory=new GeometryFactory();
        Polygon[] polygons=new Polygon[]{(Polygon)factory.toGeometry(env)};
        return factory.createMultiPolygon(polygons);
    }
    public static void addFeatures(DataStore store, SimpleFeatureType featureType) throws Exception {
    	FeatureWriter<SimpleFeatureType, SimpleFeature> writer = store.getFeatureWriterAppend(featureType.getName().getLocalPart(), Transaction.AUTO_COMMIT);
        
        createFeature("0", //$NON-NLS-1$ 
                Resolution.UNKNOWN,
                Priority.CRITICAL,
                new ReferencedEnvelope(-180,0,-90,0,StrategizedIssuesListTest.crs),
                writer); 
        
        createFeature("1", //$NON-NLS-1$ 
                Resolution.UNKNOWN,
                Priority.WARNING,
                new ReferencedEnvelope(0,180,-90,0,StrategizedIssuesListTest.crs),
                writer); 
        createFeature("2", //$NON-NLS-1$ 
                Resolution.IN_PROGRESS,
                Priority.WARNING,
                new ReferencedEnvelope(0,180,0,90,StrategizedIssuesListTest.crs),
                writer); 
        createFeature("3", //$NON-NLS-1$ 
                Resolution.IN_PROGRESS,
                Priority.TRIVIAL,
                new ReferencedEnvelope(-180,0,0,90,StrategizedIssuesListTest.crs),
                writer); 
        
        writer.close();
        
        
    }
    /**
     * 
     *
     * @param store empty array of size 1.  Will be assigned the Datastore that is created for the list
     * @param featureType empty array of size 1.  Will be assigned the SimpleFeatureType that is created for the list
     * @return the new issuesList
     * @throws SchemaException
     * @throws IOException
     * @throws Exception
     */
    public static IRemoteIssuesList createInMemoryDatastoreIssuesList(DataStore[] store, 
            SimpleFeatureType[] featureType) throws SchemaException, IOException, Exception {
        class TestMemoryDataStore extends MemoryDataStore{
            @Override
            protected MemoryEntry entry( String typeName ) throws IOException {
                return super.entry(typeName);
            }
        };
        
        final TestMemoryDataStore ds = new TestMemoryDataStore();
        final SimpleFeatureType ft = DataUtilities.createType("IssuesFeatureType",  //$NON-NLS-1$
                "*"+BOUNDS+":MultiPolygon," + //$NON-NLS-1$ //$NON-NLS-2$
                EXTENSION_ID_ATTR+":String,"+ //$NON-NLS-1$
                ISSUE_ID_ATTR+":String,"+ //$NON-NLS-1$
                GROUP_ID_ATTR+":String,"+ //$NON-NLS-1$
                RESOLUTION_ATTR+":String,"+ //$NON-NLS-1$
                PRIORITY_ATTR+":String,"+ //$NON-NLS-1$
                DESCRIPTION_ATTR+":String,"+ //$NON-NLS-1$
                ISSUE_MEMENTO_DATA_ATTR+":String,"+ //$NON-NLS-1$
                VIEW_MEMENTO_DATA_ATTR+":String"); //$NON-NLS-1$
        
        ds.createSchema(ft);
        
        IListStrategy strategy=new AbstractDatastoreStrategy(){
        
            @Override
            public FeatureCollection<SimpleFeatureType, SimpleFeature> getFeatures() throws IOException {
                return new AdaptorFeatureCollection("type", ft){

                    @Override
                    protected void closeIterator( Iterator close ) {
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    protected Iterator openIterator() {
                        try {
                            ArrayList<SimpleFeature> features = new ArrayList<SimpleFeature>(ds.entry(ft.getName().getLocalPart()).getMemory().values());
                            Collections.sort(features, new Comparator<SimpleFeature>(){

                                public int compare( SimpleFeature o1, SimpleFeature o2 ) {
                                    return ((String)o1.getAttribute(ISSUE_ID_ATTR)).compareTo((String)o2.getAttribute(ISSUE_ID_ATTR));
                                }
                                
                            });
                            final Iterator<SimpleFeature> iter=features.iterator();
                            return new Iterator(){

                                public boolean hasNext() {
                                    return iter.hasNext();
                                }

                                public Object next() {
                                    return iter.next();
                                }

                                public void remove() {
                                    throw new UnsupportedOperationException();
                                }
                                
                            };
                        } catch (IOException e) {
                            throw (RuntimeException) new RuntimeException( ).initCause( e );
                        }

                    }

                    @Override
                    public int size() {
                        try {
                            return ds.entry(ft.getName().getLocalPart()).getMemory().size();
                        } catch (IOException e) {
                            throw (RuntimeException) new RuntimeException( ).initCause( e );
                        }
                    }
                    
                };
            }
            
            protected FeatureStore<SimpleFeatureType, SimpleFeature> getFeatureStore() throws IOException {
                return (FeatureStore<SimpleFeatureType, SimpleFeature>) ds.getFeatureSource(ft.getName().getLocalPart());
            }

            public String getExtensionID() {
                return ""; //$NON-NLS-1$
            }

            @Override
            protected DataStore getDataStore() throws IOException {
                return ds;
            }
            
        }; 
        
        StrategizedIssuesList list=new StrategizedIssuesList();
        list.init(strategy);
        
        if( store!=null && store.length>0 ){
            store[0]=ds;
        }
        if( featureType!=null && featureType.length>0 ){
            featureType[0]=ft;
        }
        return list;
    }
    
    
    public static FeatureIssue createFeatureIssue(String id) throws Exception {
        IMap map=MapTests.createDefaultMap("testMap", 1, true, new java.awt.Dimension(10,10)); //$NON-NLS-1$
        ILayer layer=map.getMapLayers().get(0);
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = layer.getResource(FeatureSource.class, null).getFeatures();
        SimpleFeature feature=collection.features().next();
        FeatureIssue issue=new FeatureIssue(Priority.WARNING, "test description", layer, feature, "groupID"); //$NON-NLS-1$ //$NON-NLS-2$
        issue.setId(id);
        return issue;
    }

}
