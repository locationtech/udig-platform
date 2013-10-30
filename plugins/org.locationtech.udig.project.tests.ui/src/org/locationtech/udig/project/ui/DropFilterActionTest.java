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
package org.locationtech.udig.project.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;
import java.io.IOException;

import org.locationtech.udig.AbstractProjectUITestCase;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.core.filter.AdaptingFilter;
import org.locationtech.udig.core.filter.AdaptingFilterFactory;
import org.locationtech.udig.core.internal.FeatureUtils;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.tests.support.MapTests;
import org.locationtech.udig.project.ui.internal.dragdrop.DropFilterAction;
import org.locationtech.udig.ui.ViewerDropLocation;
import org.locationtech.udig.ui.WaitCondition;
import org.locationtech.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * Tests what happens when a filter is dropped on a layer
 * 
 * @author jeichar
 */
public class DropFilterActionTest extends AbstractProjectUITestCase {

    private SimpleFeatureType sourceType;
    private SimpleFeatureType targetType;
    private IGeoResource sourceResource;
    private Map sourceMap;
    private IGeoResource targetResource;
    private Map targetMap;
	private SimpleFeature[] sourceFeatures;

    @SuppressWarnings("deprecation")
    @Before
    public void setUp() throws Exception {
        sourceType=DataUtilities.createType("source", "*geom:LineString,geom2:Point,nAme:String"); //$NON-NLS-1$ //$NON-NLS-2$
        sourceFeatures = new SimpleFeature[1];
        GeometryFactory fac=new GeometryFactory();
        Object[] atts = new Object[]{fac.createLineString(new Coordinate[]{
                new Coordinate(0,0),
                new Coordinate(10,10),
                new Coordinate(20,20),
        }),
                fac.createPoint(new Coordinate(10,10)), "sourceName"};
        sourceFeatures[0]=SimpleFeatureBuilder.build(sourceType,atts, "id"); //$NON-NLS-1$
        sourceResource=MapTests.createGeoResource(sourceFeatures, true);
        sourceMap=MapTests.createNonDynamicMapAndRenderer(sourceResource, new Dimension(100,100));

        targetType=DataUtilities.createType("target", "*targetGeom:Point,name:String"); //$NON-NLS-1$ //$NON-NLS-2$
        SimpleFeature[] targetFeatures = new SimpleFeature[1];
        atts = new Object[]{fac.createPoint(new Coordinate(10,10)), "targetName"};
		targetFeatures[0]=SimpleFeatureBuilder.build(targetType, atts, "id"); //$NON-NLS-1$
        targetResource=MapTests.createGeoResource(targetFeatures, true);
        targetMap=MapTests.createNonDynamicMapAndRenderer(targetResource, new Dimension(100,100));

    }

    /*
     * Test method for 'org.locationtech.udig.project.ui.DropFilterAction.perform(Object, Object, IProgressMonitor)'
     */
    @Ignore
    @Test
    public void testPerform() throws Exception {
    	DropFilterAction action=new DropFilterAction();
        FilterFactory fac=CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        Filter filter=fac.id(FeatureUtils.stringToId(fac, sourceFeatures[0].getID()));
        
        AdaptingFilter aF = AdaptingFilterFactory.createAdaptingFilter(filter, sourceMap.getLayersInternal().get(0));                    
        
        final Layer layer = targetMap.getLayersInternal().get(0);
        action.init(null, null, ViewerDropLocation.NONE, layer, filter);
        assertTrue(action.accept());
        assertEquals(Filter.EXCLUDE, layer.getFilter());
        action.perform(new NullProgressMonitor());
        assertEquals(filter, layer.getFilter());
        layer.setFilter( null ); // Filter.EXCLUDE;

        action.init(null, null, ViewerDropLocation.NONE, layer, aF);
        action.perform(new NullProgressMonitor());
        final  FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = layer.getResource(FeatureSource.class, new NullProgressMonitor());
//        UDIGTestUtil.inDisplayThreadWait(2000000, new WaitCondition(){
        UDIGTestUtil.inDisplayThreadWait(2000, new WaitCondition(){

			public boolean isTrue()  {
				try {
                    return 2==featureSource.getFeatures().size() && ((org.opengis.filter.Filter)layer.getFilter()) != Filter.EXCLUDE;
                } catch (IOException e) {
                    throw (RuntimeException) new RuntimeException( ).initCause( e );
                }
			}
        	
        }, true);
        assertEquals( 2, featureSource.getFeatures().size() );
        Filter afterFilter=layer.getFilter();
        FeatureCollection<SimpleFeatureType, SimpleFeature>  features=featureSource.getFeatures(afterFilter);
        
        assertEquals(1,features.size());
        SimpleFeature addedFeature=features.features().next();
        
        assertTrue(((Geometry)addedFeature.getDefaultGeometry()).equalsExact((Geometry) sourceFeatures[0].getAttribute("geom2"))); //$NON-NLS-1$
        assertEquals(sourceFeatures[0].getAttribute("nAme"), addedFeature.getAttribute("name")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(targetType, addedFeature.getFeatureType());
    }

    @SuppressWarnings("deprecation")
    @Test
	public void testNoMatchingAttributes() throws Exception {
        targetType=DataUtilities.createType("target2", "noMatch:String"); //$NON-NLS-1$ //$NON-NLS-2$
        SimpleFeature[] targetFeatures = new SimpleFeature[1];
        targetFeatures[0]=SimpleFeatureBuilder.build(targetType,new Object[]{null},"id");
        targetResource=MapTests.createGeoResource(targetFeatures, true);
        targetMap=MapTests.createNonDynamicMapAndRenderer(targetResource, new Dimension(100,100));

    	DropFilterAction action=new DropFilterAction();
        FilterFactory fac=CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        Filter filter=fac.id(FeatureUtils.stringToId(fac, sourceFeatures[0].getID()));

        AdaptingFilter aF = AdaptingFilterFactory.createAdaptingFilter(filter, sourceMap.getLayersInternal().get(0));
        
        final Layer layer = targetMap.getLayersInternal().get(0);
        action.init(null, null, ViewerDropLocation.NONE, layer, aF);
        action.perform(new NullProgressMonitor());
        final  FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = layer.getResource(FeatureSource.class, new NullProgressMonitor());
//        UDIGTestUtil.inDisplayThreadWait(200000, new WaitCondition(){
        UDIGTestUtil.inDisplayThreadWait(2000, new WaitCondition(){

			public boolean isTrue()  {
                    return ((org.opengis.filter.Filter)layer.getFilter())!=Filter.EXCLUDE;
			}
        	
        }, true);
        assertEquals(1, featureSource.getFeatures().size());
        assertEquals(filter, layer.getFilter());
	}
    
    @Ignore
    @Test
    public void testPointToGeomDragDrop() throws Exception {
        targetType=DataUtilities.createType("target3", "*targetGeom:Geometry"); //$NON-NLS-1$ //$NON-NLS-2$
        SimpleFeature[] targetFeatures = new SimpleFeature[1];
        targetFeatures[0]=SimpleFeatureBuilder.build(targetType, new Object[]{null}, "id");
        targetResource=MapTests.createGeoResource(targetFeatures, true);
        targetMap=MapTests.createNonDynamicMapAndRenderer(targetResource, new Dimension(100,100));

        DropFilterAction action=new DropFilterAction();
        FilterFactory fac=CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        Filter filter=fac.id(FeatureUtils.stringToId(fac, sourceFeatures[0].getID()));
        
        AdaptingFilter aF = AdaptingFilterFactory.createAdaptingFilter(filter, sourceMap.getLayersInternal().get(0));
        final Layer layer = targetMap.getLayersInternal().get(0);

        action.init( null, null, ViewerDropLocation.NONE,layer, aF);
        
        action.perform(new NullProgressMonitor());
        final  FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = layer.getResource(FeatureSource.class, new NullProgressMonitor());
        UDIGTestUtil.inDisplayThreadWait(200000, new WaitCondition(){
//        UDIGTestUtil.inDisplayThreadWait(2000, new WaitCondition(){

			public boolean isTrue()  {
				try {
                    return 2==featureSource.getFeatures().size() && ((org.opengis.filter.Filter)layer.getFilter())!=Filter.EXCLUDE;
                } catch (IOException e) {
                    throw (RuntimeException) new RuntimeException( ).initCause( e );
                }
			}
        	
        }, true);
        assertEquals(2, featureSource.getFeatures().size());
        assertEquals(1, featureSource.getFeatures(layer.getFilter()).size());

    }

    /*
     * Test method for 'org.locationtech.udig.project.ui.DropFilterAction.perform(Object, Object, IProgressMonitor)'
     */
    @Ignore
    @Test
    public void testPerformOnMap() throws Exception {
        DropFilterAction action=new DropFilterAction();
        FilterFactory fac=CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        Filter filter=fac.id(FeatureUtils.stringToId(fac, sourceFeatures[0].getID()));
        
        AdaptingFilter aF = AdaptingFilterFactory.createAdaptingFilter(filter, sourceMap.getLayersInternal().get(0));
        final Layer layer = targetMap.getLayersInternal().get(0);
        final  FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = layer.getResource(FeatureSource.class, new NullProgressMonitor());
        assertEquals(1, featureSource.getCount(Query.ALL));
        
        assertTrue(action.accept());
        assertEquals(Filter.EXCLUDE, layer.getFilter());
        action.init(null, null, ViewerDropLocation.NONE, layer, filter);
        action.perform(new NullProgressMonitor());
        assertEquals(filter, layer.getFilter());
        layer.setFilter( null ); // Filter.EXCLUDE
        
        action.init(null, null, ViewerDropLocation.NONE, layer, aF);
        
        action.perform(new NullProgressMonitor());
        UDIGTestUtil.inDisplayThreadWait(2000, new WaitCondition(){

            public boolean isTrue()  {
                try {
                    return 2==featureSource.getFeatures().size() && ((org.opengis.filter.Filter)layer.getFilter())!=Filter.EXCLUDE;
                } catch (IOException e) {
                    throw (RuntimeException) new RuntimeException( ).initCause( e );
                }
            }
            
        }, true);
        assertEquals( 2, featureSource.getFeatures().size() );
        FeatureCollection<SimpleFeatureType, SimpleFeature>  features=featureSource.getFeatures(layer.getFilter());
        
        assertEquals(1,features.size());
        SimpleFeature addedFeature=features.features().next();
        
        assertTrue(((Geometry)addedFeature.getDefaultGeometry()).equalsExact((Geometry) sourceFeatures[0].getAttribute("geom2"))); //$NON-NLS-1$
        assertEquals(sourceFeatures[0].getAttribute("nAme"), addedFeature.getAttribute("name")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(targetType, addedFeature.getFeatureType());
    }

}
