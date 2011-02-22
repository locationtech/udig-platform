package net.refractions.udig.project.ui;

import java.awt.Dimension;
import java.io.IOException;

import net.refractions.udig.AbstractProjectUITestCase;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.project.ui.internal.AdaptingFilter;
import net.refractions.udig.project.ui.internal.dragdrop.DropFilterAction;
import net.refractions.udig.ui.ViewerDropLocation;
import net.refractions.udig.ui.WaitCondition;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureType;
import org.geotools.filter.Filter;
import org.geotools.filter.FilterFactory;
import org.geotools.filter.FilterFactoryFinder;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * Tests what happens when a filter is dropped on a layer
 *
 * @author jeichar
 */
public class DropFilterActionTest extends AbstractProjectUITestCase {

    private FeatureType sourceType;
    private FeatureType targetType;
    private IGeoResource sourceResource;
    private Map sourceMap;
    private IGeoResource targetResource;
    private Map targetMap;
	private Feature[] sourceFeatures;

    @SuppressWarnings("deprecation")
	protected void setUp() throws Exception {
        super.setUp();
        sourceType=DataUtilities.createType("source", "*geom:LineString,geom2:Point,nAme:String"); //$NON-NLS-1$ //$NON-NLS-2$
        sourceFeatures = new Feature[1];
        GeometryFactory fac=new GeometryFactory();
        sourceFeatures[0]=sourceType.create(new Object[]{fac.createLineString(new Coordinate[]{
                new Coordinate(0,0),
                new Coordinate(10,10),
                new Coordinate(20,20),
        }),
                fac.createPoint(new Coordinate(10,10)), "sourceName"}); //$NON-NLS-1$
        sourceResource=MapTests.createGeoResource(sourceFeatures, true);
        sourceMap=MapTests.createNonDynamicMapAndRenderer(sourceResource, new Dimension(100,100));

        targetType=DataUtilities.createType("target", "*targetGeom:Point,name:String"); //$NON-NLS-1$ //$NON-NLS-2$
        Feature[] targetFeatures = new Feature[1];
        targetFeatures[0]=targetType.create(new Object[]{fac.createPoint(new Coordinate(10,10)), "targetName"}); //$NON-NLS-1$
        targetResource=MapTests.createGeoResource(targetFeatures, true);
        targetMap=MapTests.createNonDynamicMapAndRenderer(targetResource, new Dimension(100,100));

    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test method for 'net.refractions.udig.project.ui.DropFilterAction.perform(Object, Object, IProgressMonitor)'
     */
    public void testPerform() throws Exception {
    	DropFilterAction action=new DropFilterAction();
        FilterFactory fac=FilterFactoryFinder.createFilterFactory();
        Filter filter=fac.createFidFilter(sourceFeatures[0].getID());
        AdaptingFilter aF=new AdaptingFilter(filter, sourceMap.getLayersInternal().get(0));
        final Layer layer = targetMap.getLayersInternal().get(0);
        action.init(null, null, ViewerDropLocation.NONE, layer, filter);
        assertTrue(action.accept());
        assertEquals(Filter.ALL, layer.getFilter());
        action.perform(new NullProgressMonitor());
        assertEquals(filter, layer.getFilter());
        layer.setFilter(Filter.ALL);

        action.init(null, null, ViewerDropLocation.NONE, layer, aF);
        action.perform(new NullProgressMonitor());
        final FeatureSource featureSource = layer.getResource(FeatureSource.class, new NullProgressMonitor());
//        UDIGTestUtil.inDisplayThreadWait(2000000, new WaitCondition(){
        UDIGTestUtil.inDisplayThreadWait(2000, new WaitCondition(){

			public boolean isTrue()  {
				try {
                    return 2==featureSource.getFeatures().size() && layer.getFilter()!=Filter.ALL;
                } catch (IOException e) {
                    throw (RuntimeException) new RuntimeException( ).initCause( e );
                }
			}

        }, true);
        assertEquals( 2, featureSource.getFeatures().size() );
        Filter afterFilter=layer.getFilter();
        FeatureCollection features=featureSource.getFeatures(afterFilter);

        assertEquals(1,features.size());
        Feature addedFeature=features.features().next();

        assertTrue(addedFeature.getDefaultGeometry().equalsExact((Geometry) sourceFeatures[0].getAttribute("geom2"))); //$NON-NLS-1$
        assertEquals(sourceFeatures[0].getAttribute("nAme"), addedFeature.getAttribute("name")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(targetType, addedFeature.getFeatureType());
    }

    @SuppressWarnings("deprecation")
	public void testNoMatchingAttributes() throws Exception {
        targetType=DataUtilities.createType("target2", "noMatch:String"); //$NON-NLS-1$ //$NON-NLS-2$
        Feature[] targetFeatures = new Feature[1];
        targetFeatures[0]=targetType.create(new Object[]{null});
        targetResource=MapTests.createGeoResource(targetFeatures, true);
        targetMap=MapTests.createNonDynamicMapAndRenderer(targetResource, new Dimension(100,100));

    	DropFilterAction action=new DropFilterAction();
        FilterFactory fac=FilterFactoryFinder.createFilterFactory();
        Filter filter=fac.createFidFilter(sourceFeatures[0].getID());
        AdaptingFilter aF=new AdaptingFilter(filter, sourceMap.getLayersInternal().get(0));
        final Layer layer = targetMap.getLayersInternal().get(0);
        action.init(null, null, ViewerDropLocation.NONE, layer, aF);
        action.perform(new NullProgressMonitor());
        final FeatureSource featureSource = layer.getResource(FeatureSource.class, new NullProgressMonitor());
//        UDIGTestUtil.inDisplayThreadWait(200000, new WaitCondition(){
        UDIGTestUtil.inDisplayThreadWait(2000, new WaitCondition(){

			public boolean isTrue()  {
                    return layer.getFilter()!=Filter.ALL;
			}

        }, true);
        assertEquals(1, featureSource.getFeatures().size());
        assertEquals(filter, layer.getFilter());
	}

    public void testPointToGeomDragDrop() throws Exception {
        targetType=DataUtilities.createType("target3", "*targetGeom:Geometry"); //$NON-NLS-1$ //$NON-NLS-2$
        Feature[] targetFeatures = new Feature[1];
        targetFeatures[0]=targetType.create(new Object[]{null});
        targetResource=MapTests.createGeoResource(targetFeatures, true);
        targetMap=MapTests.createNonDynamicMapAndRenderer(targetResource, new Dimension(100,100));

        DropFilterAction action=new DropFilterAction();
        FilterFactory fac=FilterFactoryFinder.createFilterFactory();
        Filter filter=fac.createFidFilter(sourceFeatures[0].getID());
        AdaptingFilter aF=new AdaptingFilter(filter, sourceMap.getLayersInternal().get(0));
        final Layer layer = targetMap.getLayersInternal().get(0);

        action.init( null, null, ViewerDropLocation.NONE,layer, aF);

        action.perform(new NullProgressMonitor());
        final FeatureSource featureSource = layer.getResource(FeatureSource.class, new NullProgressMonitor());
        UDIGTestUtil.inDisplayThreadWait(200000, new WaitCondition(){
//        UDIGTestUtil.inDisplayThreadWait(2000, new WaitCondition(){

			public boolean isTrue()  {
				try {
                    return 2==featureSource.getFeatures().size() && layer.getFilter()!=Filter.ALL;
                } catch (IOException e) {
                    throw (RuntimeException) new RuntimeException( ).initCause( e );
                }
			}

        }, true);
        assertEquals(2, featureSource.getFeatures().size());
        assertEquals(1, featureSource.getFeatures(layer.getFilter()).size());

    }

    /*
     * Test method for 'net.refractions.udig.project.ui.DropFilterAction.perform(Object, Object, IProgressMonitor)'
     */
    public void testPerformOnMap() throws Exception {
        DropFilterAction action=new DropFilterAction();
        FilterFactory fac=FilterFactoryFinder.createFilterFactory();
        Filter filter=fac.createFidFilter(sourceFeatures[0].getID());
        AdaptingFilter aF=new AdaptingFilter(filter, sourceMap.getLayersInternal().get(0));
        final Layer layer = targetMap.getLayersInternal().get(0);
        final FeatureSource featureSource = layer.getResource(FeatureSource.class, new NullProgressMonitor());
        assertEquals(1, featureSource.getCount(Query.ALL));

        assertTrue(action.accept());
        assertEquals(Filter.ALL, layer.getFilter());
        action.init(null, null, ViewerDropLocation.NONE, layer, filter);
        action.perform(new NullProgressMonitor());
        assertEquals(filter, layer.getFilter());
        layer.setFilter(Filter.ALL);

        action.init(null, null, ViewerDropLocation.NONE, layer, aF);

        action.perform(new NullProgressMonitor());
        UDIGTestUtil.inDisplayThreadWait(2000, new WaitCondition(){

            public boolean isTrue()  {
                try {
                    return 2==featureSource.getFeatures().size() && layer.getFilter()!=Filter.ALL;
                } catch (IOException e) {
                    throw (RuntimeException) new RuntimeException( ).initCause( e );
                }
            }

        }, true);
        assertEquals( 2, featureSource.getFeatures().size() );
        FeatureCollection features=featureSource.getFeatures(layer.getFilter());

        assertEquals(1,features.size());
        Feature addedFeature=features.features().next();

        assertTrue(addedFeature.getDefaultGeometry().equalsExact((Geometry) sourceFeatures[0].getAttribute("geom2"))); //$NON-NLS-1$
        assertEquals(sourceFeatures[0].getAttribute("nAme"), addedFeature.getAttribute("name")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(targetType, addedFeature.getFeatureType());
    }

}
