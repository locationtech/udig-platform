/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.awt.Dimension;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.util.factory.GeoTools;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.udig.core.internal.FeatureUtils;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.tests.support.MapTests;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;

/**
 * Test Command
 * @author jones
 * @since 1.1.0
 */
public class CreateOrSetFeatureTest {

    private Map map;
    private ILayer layer;
    private SimpleFeature original;
    private FeatureSource<SimpleFeatureType, SimpleFeature> resource;

    @Before
    public void setUp() throws Exception {
        map=MapTests.createDefaultMap("TypeName", 1, true, new Dimension(10,10)); //$NON-NLS-1$
        layer=map.getMapLayers().get(0);
        resource = layer.getResource(FeatureSource.class, new NullProgressMonitor());
        original=resource.getFeatures().features().next();
    }
    
    /*
     * Test method for 'org.locationtech.udig.tools.edit.commands.CreateOrSetFeature.run(IProgressMonitor)'
     */
    @Test
    public void testCreateNew() throws Exception {
        GeometryFactory fac=new GeometryFactory();
        fac.createPoint(new Coordinate(10,10));
        CreateNewOrSelectExitingFeatureCommand command=new CreateNewOrSelectExitingFeatureCommand("newID", layer, fac.createPoint(new Coordinate(10,10))); //$NON-NLS-1$
        command.setMap(map);
        NullProgressMonitor nullProgressMonitor = new NullProgressMonitor();
        command.run(nullProgressMonitor);
        
        FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        Filter filter = filterFactory.id(FeatureUtils.stringToId(filterFactory, original.getID()));
        filter = filterFactory.not(filter);
		SimpleFeature feature=resource.getFeatures(
				filter).features().next();
        assertEquals(2, getCount());
        assertEquals(new Coordinate(10,10), ((Geometry) feature.getDefaultGeometry()).getCoordinates()[0]);
        assertFalse( feature.getID().equals(original));
        command.rollback(nullProgressMonitor);
        
        assertEquals(1, getCount());
        feature=resource.getFeatures(filterFactory.id(FeatureUtils.stringToId(filterFactory,original.getID()))).features().next();
        assertEquals(original.getID(), feature.getID());
        assertFalse( new Coordinate(10,10).equals(((Geometry) feature.getDefaultGeometry()).getCoordinates()[0] ));
        
    }
    
    /*
     * Test method for 'org.locationtech.udig.tools.edit.commands.CreateOrSetFeature.run(IProgressMonitor)'
     */
    @Test
    public void testModifyExistingFeature() throws Exception {
        GeometryFactory fac=new GeometryFactory();
        fac.createPoint(new Coordinate(10,10));
        CreateNewOrSelectExitingFeatureCommand command=new CreateNewOrSelectExitingFeatureCommand(original.getID(), layer, fac.createPoint(new Coordinate(10,10)));
        command.setMap(map);
        NullProgressMonitor nullProgressMonitor = new NullProgressMonitor();
        command.run(nullProgressMonitor);
        
        FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
		SimpleFeature feature=resource.getFeatures(filterFactory.id(FeatureUtils.stringToId(filterFactory, original.getID()))).
					features().next();
        assertEquals(1, getCount());
        assertEquals(original.getID(), feature.getID());
        assertEquals(new Coordinate(10,10), ((Geometry) feature.getDefaultGeometry()).getCoordinates()[0]);
        
        command.rollback(nullProgressMonitor);
        
        feature=resource.getFeatures(filterFactory.id(FeatureUtils.stringToId(filterFactory, original.getID()))).features().next();
        assertEquals(original.getID(), feature.getID());
        assertFalse( new Coordinate(10,10).equals(((Geometry) feature.getDefaultGeometry()).getCoordinates()[0] ));
        
    }

    private int getCount() throws Exception {
        FeatureCollection<SimpleFeatureType, SimpleFeature>  features = resource.getFeatures();
        FeatureIterator<SimpleFeature> iter=features.features();
        int i=0;
        while(iter.hasNext()){
            iter.next();
            i++;
        }
        return i;
    }

}
