/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.tools.edit.impl;

import static org.junit.Assert.assertEquals;

import java.awt.Dimension;

import net.refractions.udig.TestViewportPane;
import net.refractions.udig.core.internal.FeatureUtils;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.EditManager;
import net.refractions.udig.project.internal.render.RenderManager;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.AbstractEditTool;
import net.refractions.udig.tools.edit.EditToolConfigurationHelper;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.TestHandler;

import org.geotools.data.FeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureIterator;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

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
            source.modifyFeatures(feature.getFeatureType().getDescriptor("name"), "feature"+i, filterFac.id(FeatureUtils.stringToId(filterFac, feature.getID())));  //$NON-NLS-1$//$NON-NLS-2$
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
            source.modifyFeatures(feature.getFeatureType().getGeometryDescriptor(), geom, filterFac.id(FeatureUtils.stringToId(filterFac, feature.getID())));
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
