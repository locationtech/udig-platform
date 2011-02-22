package net.refractions.udig.issues.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import net.refractions.udig.core.enums.Priority;
import net.refractions.udig.core.enums.Resolution;
import net.refractions.udig.issues.FeatureIssue;
import net.refractions.udig.issues.IIssue;
import net.refractions.udig.issues.IRemoteIssuesList;
import net.refractions.udig.issues.listeners.IIssuesListListener;
import net.refractions.udig.issues.listeners.IssuesListEvent;
import net.refractions.udig.issues.listeners.IssuesListEventType;

import org.geotools.data.DataStore;
import org.geotools.data.FeatureStore;
import org.geotools.data.Query;
import org.geotools.data.Transaction;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;
import org.geotools.filter.CompareFilter;
import org.geotools.filter.FilterFactory;
import org.geotools.filter.FilterFactoryFinder;
import org.geotools.filter.FilterType;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


public class StrategizedIssuesListTest extends TestCase {

    public FeatureType featureType;
    public IRemoteIssuesList list;
    public DataStore store;
    public static final CoordinateReferenceSystem crs;
    static{
        try {
            crs=CRS.decode("EPSG:4326");//$NON-NLS-1$
        } catch (NoSuchAuthorityCodeException e) {
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        FeatureIssue.setTesting(true);
        DataStore[] ds=new DataStore[1];
        FeatureType[] ft=new FeatureType[1];

        list=IssuesListTestHelper.createInMemoryDatastoreIssuesList(ds, ft);

        featureType=ft[0];
        store=ds[0];

        IssuesListTestHelper.addFeatures(ds[0], ft[0]);

        list.refresh();
    }

    public void testInitialLoad() throws Exception {
        assertEquals(4, list.size());
        assertEquals("0", list.get(0).getId()); //$NON-NLS-1$
        assertEquals("1", list.get(1).getId()); //$NON-NLS-1$
        assertEquals("2", list.get(2).getId()); //$NON-NLS-1$
        assertEquals("3", list.get(3).getId()); //$NON-NLS-1$
        assertEquals( 4, store.getFeatureSource(featureType.getTypeName()).getCount(Query.ALL));
    }

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
        assertEquals( 5, store.getFeatureSource(featureType.getTypeName()).getCount(Query.ALL));
    }

    public void testRemoveInt() throws Exception {
        list.remove(1);
        assertEquals(3, list.size());
        assertEquals("0", list.get(0).getId()); //$NON-NLS-1$
        assertEquals("2", list.get(1).getId()); //$NON-NLS-1$
        assertEquals("3", list.get(2).getId()); //$NON-NLS-1$
        assertEquals( 3, store.getFeatureSource(featureType.getTypeName()).getCount(Query.ALL));
    }

    public void testBackendRemovedIssue()throws Exception{
        FeatureStore fs=(FeatureStore) store.getFeatureSource(featureType.getTypeName());
        FilterFactory factory = FilterFactoryFinder.createFilterFactory();
        CompareFilter filter = factory.createCompareFilter(FilterType.COMPARE_EQUALS);
        filter.addLeftValue(factory.createAttributeExpression(IssuesListTestHelper.ISSUE_ID_ATTR));
        filter.addRightValue(factory.createLiteralExpression("1")); //$NON-NLS-1$
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

    public void testBackendAddedIssue()throws Exception{
        IssuesListTestHelper.createFeature("new",  //$NON-NLS-1$
                Resolution.IN_PROGRESS,
                Priority.CRITICAL,
                new ReferencedEnvelope(0,10,0,10,crs),
                store.getFeatureWriterAppend(featureType.getTypeName(), Transaction.AUTO_COMMIT));
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


    public void testRemoveMany() throws Exception {
        List<IIssue> sublist = new ArrayList<IIssue>();
        sublist.addAll(list.subList(0, 2));
        list.removeAll(sublist);
        assertEquals(2, list.size());
        assertEquals("2", list.get(0).getId()); //$NON-NLS-1$
        assertEquals("3", list.get(1).getId()); //$NON-NLS-1$
    }

    public void testAddMany() throws Exception {
        List<IIssue> newIssues=new ArrayList<IIssue>();
        newIssues.add(IssuesListTestHelper.createFeatureIssue("new1")); //$NON-NLS-1$
        newIssues.add(IssuesListTestHelper.createFeatureIssue("new2")); //$NON-NLS-1$
        list.addAll(newIssues);
        list.refresh();
        assertEquals(6, list.size());
    }

    public void testModifyIssue() throws Exception{
        String newDescription = "new modified description"; //$NON-NLS-1$

        list.clear();
        list.add(IssuesListTestHelper.createFeatureIssue("test")); //$NON-NLS-1$

        list.refresh();
        assertEquals(1, list.size());

        IIssue issue = list.get(0);
        issue.setDescription(newDescription);
        ((IRemoteIssuesList)list).save(issue);
        FilterFactory createFilterFactory = FilterFactoryFinder.createFilterFactory();
        CompareFilter filter = createFilterFactory.createCompareFilter(FilterType.COMPARE_EQUALS);
        filter.addLeftValue(createFilterFactory.createAttributeExpression(IssuesListTestHelper.ISSUE_ID_ATTR));
        filter.addRightValue(createFilterFactory.createLiteralExpression(issue.getId()));

        Feature next = store.getFeatureSource(featureType.getTypeName()).getFeatures(filter).features().next();
        assertEquals(newDescription, next.getAttribute(IssuesListTestHelper.DESCRIPTION_ATTR));
    }

    public void testAddIssueWithBounds() throws Exception {
        list.add(IssuesListTestHelper.createFeatureIssue("id")); //$NON-NLS-1$
        // no exception? good.
    }

    public void testNullID() throws Exception {
        list.clear();
        list.add(IssuesListTestHelper.createFeatureIssue(null));
        assertNotNull(list.get(0).getId());

        Feature next = this.store.getFeatureSource(this.featureType.getTypeName()).getFeatures().features().next();
        Object id = next.getAttribute("id"); //$NON-NLS-1$
        assertNotNull(id);
    }

    public void testRefreshWithModifiedFeatures() throws Exception {
        String description="New Description: blah blah blah"; //$NON-NLS-1$
        list.get(0).setDescription(description);

        list.refresh();

        assertEquals(description, list.get(0).getDescription());
    }
}
