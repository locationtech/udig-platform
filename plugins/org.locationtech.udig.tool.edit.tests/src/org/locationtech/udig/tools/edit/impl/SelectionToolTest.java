/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.impl;

import static org.junit.Assert.assertEquals;

import java.awt.Dimension;

import org.geotools.data.FeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureIterator;
import org.geotools.util.factory.GeoTools;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.udig.TestViewportPane;
import org.locationtech.udig.core.internal.FeatureUtils;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.EditManager;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tools.edit.AbstractEditTool;
import org.locationtech.udig.tools.edit.EditToolConfigurationHelper;
import org.locationtech.udig.tools.edit.support.Point;
import org.locationtech.udig.tools.edit.support.TestHandler;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;

/**
 * Test the SelectionToolTest 
 * @author jones
 * @since 1.1.0
 */
public class SelectionToolTest extends AbstractToolTest{

    @Before
    public void setUp() throws Exception {
        handler=new TestHandler(3);
        
        FeatureStore<SimpleFeatureType, SimpleFeature> source = ((ILayer) handler.getContext().getMapLayers().get(0)).getResource(FeatureStore.class, null);
        FilterFactory filterFac=CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        GeometryFactory geomFac=new GeometryFactory();
        int i=0;
        for( FeatureIterator<SimpleFeature> iter = source.getFeatures().features(); iter.hasNext(); ){
            SimpleFeature feature = iter.next();
            source.modifyFeatures(feature.getFeatureType().getDescriptor("name").getName(), "feature"+i, filterFac.id(FeatureUtils.stringToId(filterFac, feature.getID())));  //$NON-NLS-1$//$NON-NLS-2$
            Geometry geom;
            if( i==0 ){
                geom=geomFac.createPoint(new Coordinate(0,10));
            }else if( i==1 ){
                geom=geomFac.createLineString(new Coordinate[]{
                        new Coordinate( 10,10), new Coordinate(10,20)
                });
            }else{
                geom=geomFac.createLinearRing(
                        new Coordinate[]{
                                new Coordinate( 20,10), new Coordinate(40,10),
                                new Coordinate( 40,40), new Coordinate(20,40),
                                new Coordinate( 20,10)
                        }
                );
                geom=geomFac.createPolygon((LinearRing) geom, new LinearRing[0]);
            }
            source.modifyFeatures(feature.getFeatureType().getGeometryDescriptor().getName(), geom, filterFac.id(FeatureUtils.stringToId(filterFac, feature.getID())));
            i++;
        }
        ((EditManager) handler.getContext().getEditManager()).commitTransaction();
        ((RenderManager)handler.getContext().getRenderManager()).setMapDisplay(new TestViewportPane(new Dimension(500,500)));
        
        tool.setHandler(handler);
        
        tool.testinitEventBehaviours(new EditToolConfigurationHelper(handler.getBehaviours()));
        
    }
    
    @Override
    protected AbstractEditTool createTool() {
        return new SelectionTool();
    }
    
    @Test
    public void testSelect1FeatureThenAnother() throws Exception{
        
        Point p=handler.getEditBlackboard().toPoint(new Coordinate(0,10));
        tool.mouseReleased(new MapMouseEvent(handler.getContext().getMapDisplay(), p.getX(), p.getY(), 0,0,MapMouseEvent.BUTTON1));
        
        assertEquals("feature0", handler.getContext().getEditManager().getEditFeature().getAttribute("name"));  //$NON-NLS-1$//$NON-NLS-2$

        p=handler.getEditBlackboard().toPoint(new Coordinate(10,15));
        tool.mouseReleased(new MapMouseEvent(handler.getContext().getMapDisplay(), p.getX(), p.getY(), 0,0,MapMouseEvent.BUTTON1));
        
        assertEquals("feature1", handler.getContext().getEditManager().getEditFeature().getAttribute("name")); //$NON-NLS-1$ //$NON-NLS-2$

        p=handler.getEditBlackboard().toPoint(new Coordinate(30,30));
        tool.mouseReleased(new MapMouseEvent(handler.getContext().getMapDisplay(), p.getX(), p.getY(), 0,0,MapMouseEvent.BUTTON1));
        
        assertEquals("feature2", handler.getContext().getEditManager().getEditFeature().getAttribute("name")); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
}
