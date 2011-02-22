package net.refractions.udig.issues.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

import net.refractions.udig.core.enums.Priority;
import net.refractions.udig.core.enums.Resolution;
import net.refractions.udig.issues.FeatureIssue;
import net.refractions.udig.issues.IListStrategy;
import net.refractions.udig.issues.IRemoteIssuesList;
import net.refractions.udig.issues.StrategizedIssuesList;
import net.refractions.udig.issues.internal.datastore.AbstractDatastoreStrategy;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.tests.support.MapTests;

import org.geotools.data.DataStore;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.memory.MemoryDataStore;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureType;
import org.geotools.feature.SchemaException;
import org.geotools.feature.collection.AbstractFeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;

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
    public static void createFeature( String id, Resolution r, Priority p, ReferencedEnvelope bounds, FeatureWriter writer) throws Exception {
        Feature feature=writer.next();
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
    public static void addFeatures(DataStore store, FeatureType featureType) throws Exception {
        FeatureWriter writer = store.getFeatureWriterAppend(featureType.getTypeName(), Transaction.AUTO_COMMIT);

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
     * @param featureType empty array of size 1.  Will be assigned the FeatureType that is created for the list
     * @return the new issuesList
     * @throws SchemaException
     * @throws IOException
     * @throws Exception
     */
    public static IRemoteIssuesList createInMemoryDatastoreIssuesList(DataStore[] store,
            FeatureType[] featureType) throws SchemaException, IOException, Exception {
        class TestMemoryDataStore extends MemoryDataStore{
            @Override
            protected Map features( String typeName ) throws IOException {
                return super.features(typeName);
            }
        };

        final TestMemoryDataStore ds = new TestMemoryDataStore();
        final FeatureType ft = DataUtilities.createType("IssuesFeatureType",  //$NON-NLS-1$
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
            public FeatureCollection getFeatures() throws IOException {
                return new AbstractFeatureCollection(ft){

                    @Override
                    protected void closeIterator( Iterator close ) {
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    protected Iterator openIterator() {
                        try {
                            ArrayList<Feature> features = new ArrayList<Feature>(ds.features(ft.getTypeName()).values());
                            Collections.sort(features, new Comparator<Feature>(){

                                public int compare( Feature o1, Feature o2 ) {
                                    return ((String)o1.getAttribute(ISSUE_ID_ATTR)).compareTo((String)o2.getAttribute(ISSUE_ID_ATTR));
                                }

                            });
                            final Iterator<Feature> iter=features.iterator();
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
                            return ds.features(ft.getTypeName()).size();
                        } catch (IOException e) {
                            throw (RuntimeException) new RuntimeException( ).initCause( e );
                        }
                    }

                };
            }

            protected FeatureStore getFeatureStore() throws IOException {
                return (FeatureStore) ds.getFeatureSource(ft.getTypeName());
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
        Feature feature=layer.getResource(FeatureSource.class, null).getFeatures().features().next();
        FeatureIssue issue=new FeatureIssue(Priority.WARNING, "test description", layer, feature, "groupID"); //$NON-NLS-1$ //$NON-NLS-2$
        issue.setId(id);
        return issue;
    }

}
