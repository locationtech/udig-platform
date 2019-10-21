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
package org.locationtech.udig.tools.edit.behaviour;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geotools.data.FeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.util.factory.GeoTools;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.udig.AbstractProjectUITestCase;
import org.locationtech.udig.project.BlackboardEvent;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.IBlackboardListener;
import org.locationtech.udig.project.internal.EditManager;
import org.locationtech.udig.project.tests.support.MapTests;
import org.locationtech.udig.project.ui.internal.ApplicationGISInternal;
import org.locationtech.udig.project.ui.internal.tool.ToolContext;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.EventType;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.EditBlackboardAdapter;
import org.locationtech.udig.tools.edit.support.EditBlackboardEvent;
import org.locationtech.udig.tools.edit.support.EditGeom;
import org.locationtech.udig.tools.edit.support.PrimitiveShape;
import org.locationtech.udig.tools.edit.support.TestHandler;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.identity.Identifier;
import org.opengis.filter.spatial.BBOX;

public class SelectGeometryBehaviourTest extends AbstractProjectUITestCase {
    final int none=MapMouseEvent.NONE;
    final int ctrl = MapMouseEvent.MOD1_DOWN_MASK;
    final int shift = MapMouseEvent.SHIFT_DOWN_MASK;
    final int alt = MapMouseEvent.ALT_DOWN_MASK;
    final int button1 = MapMouseEvent.BUTTON1;
    final int button2 = MapMouseEvent.BUTTON2;
    private org.locationtech.udig.project.internal.Map map;
    private FeatureCollection<SimpleFeatureType, SimpleFeature>  features;

    java.awt.Point SCREEN=new java.awt.Point(500,500);
    private TestHandler handler;
    
    @Before
    public void setUp() throws Exception {
        map=MapTests.createDefaultMap("Test",3,true,new Dimension(500,500)); //$NON-NLS-1$
        FeatureStore<SimpleFeatureType, SimpleFeature> resource = map.getLayersInternal().get(0).getResource(FeatureStore.class, null);
        features=resource.getFeatures();
        GeometryFactory fac=new GeometryFactory();
        int i=0;
        for( FeatureIterator<SimpleFeature> iter=features.features(); iter.hasNext(); ){
            i++;
            SimpleFeature feature=iter.next();
            Coordinate c=map.getViewportModel().pixelToWorld(i*10,0);
            FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
			Set<Identifier> ids = new HashSet<Identifier>();
			ids.add(filterFactory.featureId(feature.getID()));
			GeometryDescriptor defaultGeometry = feature.getFeatureType().getGeometryDescriptor();
			resource.modifyFeatures(defaultGeometry.getName(), fac.createPoint(c),
                    filterFactory.id(ids));
        }
        ((EditManager)map.getEditManager()).commitTransaction();

        handler=new TestHandler();
        ((ToolContext)handler.getContext()).setMapInternal(map);
        ((ToolContext)handler.getContext()).setRenderManagerInternal(map.getRenderManagerInternal());
    }
    
    /*
     * Test method for 'org.locationtech.udig.tools.edit.mode.MoveVertexMode.isValid(EditToolHandler, MapMouseEvent, EventType)'
     */
    @Test
    public void testIsValid() throws Exception {
        SelectFeatureBehaviour mode=new SelectFeatureBehaviour(new Class[]{Polygon.class, MultiPolygon.class}, BBOX.class);

        assertFalse(mode.isValid(handler, new MapMouseEvent(null, 0, 0, none, none, 0), EventType.DOUBLE_CLICK));

        assertTrue(mode.isValid(handler,  new MapMouseEvent(null, 10, 0, none, none, button1), EventType.RELEASED));
        // not valid for drag events
        assertFalse(mode.isValid(handler, new MapMouseEvent(null, 10, 0, none, none, button1), EventType.DRAGGED));
        // not valid for exit events
        assertFalse(mode.isValid(handler, new MapMouseEvent(null, 10, 0, none, none, button1), EventType.EXITED));
        // not valid for moved events
        assertFalse(mode.isValid(handler, new MapMouseEvent(null, 10, 0, none, none, button1), EventType.MOVED));
        // not valid for pressed events
        assertFalse(mode.isValid(handler, new MapMouseEvent(null, 10, 0, none, none, button1), EventType.PRESSED));

        // ctl down is legal
        assertTrue(mode.isValid(handler, new MapMouseEvent(null, 10,0, ctrl,none, button1), EventType.RELEASED));
        // shift down is legal
        assertTrue(mode.isValid(handler, new MapMouseEvent(null, 10,0, shift, none, button1), EventType.RELEASED));

        // ctl down is not legal
        assertFalse(mode.isValid(handler, new MapMouseEvent(null, 10,0, alt, none, button1), EventType.RELEASED));
        // only 1 modifier is legal
        assertFalse(mode.isValid(handler, new MapMouseEvent(null, 
                0, 0, shift|ctrl, none, button1), EventType.RELEASED));
        
        // button2 is not legal
        assertFalse(mode.isValid(handler,  new MapMouseEvent(null, 10, 0, none, none, button2), EventType.RELEASED));
        
        List<EditGeom> geoms = handler.getEditBlackboard().getGeoms();
        handler.getEditBlackboard().addPoint(10,0, geoms.get(0).getShell());
        handler.setCurrentShape(handler.getEditBlackboard().getGeoms().get(0).getShell());
    }

    /*
     * Test method for 'org.locationtech.udig.tools.edit.mode.MoveVertexMode.run(EditToolHandler, MapMouseEvent, EventType)'
     */
    @Ignore
    @Test
    public void testRun() throws Exception {
        SelectFeatureBehaviour mode=new SelectFeatureBehaviour(new Class[]{Point.class}, BBOX.class);
        
        Listener l=new Listener();
        handler.getBehaviours().add(mode);
        handler.getContext().getMap().getBlackboard().addListener(l);
        handler.setEditBlackboard(new EditBlackboard(SCREEN.x, 
                SCREEN.y, map.getViewportModel().worldToScreenTransform(), map.getLayersInternal().get(0).layerToMapTransform()));
        handler.setContext(ApplicationGISInternal.createContext(map));
        handler.getEditBlackboard().getListeners().add(l);
        
        handler.handleEvent(new MapMouseEvent(null, 10, 0, none, none, button1), EventType.RELEASED);
        assertEquals( org.locationtech.udig.tools.edit.support.Point.valueOf(10,0), handler.getCurrentGeom().getShell().getPoint(0));
        assertTrue( l.set  );
        assertNull(l.old);
        assertEquals( handler.getCurrentGeom(), l.current);

        EditGeom geom=l.added.get(l.added.size()-1);
        handler.handleEvent(new MapMouseEvent(null, 20, 0, none, none, button1), EventType.RELEASED);
        assertEquals( org.locationtech.udig.tools.edit.support.Point.valueOf(20,0), handler.getCurrentGeom().getShell().getPoint(0));
        assertTrue( l.set  );
        assertEquals(geom,l.old);
        assertEquals( handler.getCurrentGeom(), l.current);
        assertEquals( 1, handler.getEditBlackboard().getGeoms().size());
        
        // add using shift
        handler.handleEvent(new MapMouseEvent(null, 10, 0, shift, none, button1), EventType.RELEASED);
        geom=l.added.get(l.added.size()-1);
        assertEquals(1, handler.getEditBlackboard().getGeoms(20,0).size());
        assertFalse( l.set  );
        assertEquals(geom,l.current);
        assertEquals( geom, handler.getCurrentGeom());
        assertEquals( 2, handler.getEditBlackboard().getGeoms().size());
        
        // add using ctrl
        handler.handleEvent(new MapMouseEvent(null, 30, 0, ctrl, none, button1), EventType.RELEASED);
        geom=l.added.get(l.added.size()-1);
        assertEquals(1, handler.getEditBlackboard().getGeoms(30,0).size());
        assertFalse( l.set  );
        assertEquals( geom, handler.getCurrentGeom());
        assertEquals( 3, handler.getEditBlackboard().getGeoms().size());
        
        // remove using ctrl
        handler.handleEvent(new MapMouseEvent(null, 30, 0, ctrl, none, button1), EventType.RELEASED);
        assertEquals(0, handler.getEditBlackboard().getGeoms(30,0).size());
        assertEquals( 2, handler.getEditBlackboard().getGeoms().size());
        
        // click on nothing and all should be deselected
        handler.handleEvent( new MapMouseEvent(null, 0, 0, none, none, button1), EventType.RELEASED);
        assertEquals(0, handler.getEditBlackboard().getGeoms(20,0).size());
        assertEquals( 1, handler.getEditBlackboard().getGeoms().size());
        assertTrue(l.set);
        assertNull( handler.getCurrentGeom() );
        
    }
    
    @Test
    public void testSelectMultiGeom() throws Exception {
        SelectFeatureBehaviour mode=new SelectFeatureBehaviour(new Class[]{MultiLineString.class}, BBOX.class);

        handler.getBehaviours().add(mode);
        handler.setEditBlackboard(new EditBlackboard(500,500, map.getViewportModel().worldToScreenTransform(), 
                map.getLayersInternal().get(0).layerToMapTransform()));
        
        FeatureStore<SimpleFeatureType, SimpleFeature> resource = map.getLayersInternal().get(0).getResource(FeatureStore.class, null);
        GeometryFactory factory=new GeometryFactory();
        LineString line1=factory.createLineString(new Coordinate[]{
                map.getViewportModel().pixelToWorld(10,10), map.getViewportModel().pixelToWorld(10,20)
        });
        LineString line2=factory.createLineString(new Coordinate[]{
                map.getViewportModel().pixelToWorld(20,10), map.getViewportModel().pixelToWorld(20,20)
        });
        
        MultiLineString multiline = factory.createMultiLineString(new LineString[]{line1, line2});
        
        SimpleFeature feature = SimpleFeatureBuilder.build(resource.getSchema(), 
        		new Object[]{multiline, "multiline"}, "testGeom"); //$NON-NLS-1$
        Set<Identifier> ids = new HashSet<Identifier>();
        FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        ids.add(filterFactory.featureId(features.features().next().getID()));
		resource.modifyFeatures(feature.getFeatureType().getGeometryDescriptor().getName(), multiline,
                filterFactory.id(ids));
    
        
        handler.handleEvent(new MapMouseEvent(null, 20, 15, none, none, button1), EventType.RELEASED);
        
        assertTrue(handler.getCurrentShape().hasVertex(org.locationtech.udig.tools.edit.support.Point.valueOf(20,10)));
        assertTrue(handler.getCurrentShape().hasVertex(org.locationtech.udig.tools.edit.support.Point.valueOf(20,20)));
        assertFalse(handler.getCurrentShape().hasVertex(org.locationtech.udig.tools.edit.support.Point.valueOf(10,10)));
        assertFalse(handler.getCurrentShape().hasVertex(org.locationtech.udig.tools.edit.support.Point.valueOf(10,20)));

        handler.handleEvent(new MapMouseEvent(null, 10, 15, none, none, button1), EventType.RELEASED);
        
        assertTrue(handler.getCurrentShape().hasVertex(org.locationtech.udig.tools.edit.support.Point.valueOf(10,10)));
        assertTrue(handler.getCurrentShape().hasVertex(org.locationtech.udig.tools.edit.support.Point.valueOf(10,20)));
        assertFalse(handler.getCurrentShape().hasVertex(org.locationtech.udig.tools.edit.support.Point.valueOf(20,10)));
        assertFalse(handler.getCurrentShape().hasVertex(org.locationtech.udig.tools.edit.support.Point.valueOf(20,20)));
    
    }

    class Listener extends EditBlackboardAdapter implements IBlackboardListener{
        List<EditGeom> added;
        boolean set=false;
        EditGeom old;
        EditGeom current;
        
        @SuppressWarnings("unchecked")
        @Override
        public void changed( EditBlackboardEvent e ) {
            
            switch( e.getType() ) {
            case SET_GEOMS:
                set=true;
                added=(List<EditGeom>) e.getNewValue();
                break;
            case ADD_GEOMS:
                set=false;
                added=(List<EditGeom>) e.getNewValue();
                break;
            case REMOVE_GEOMS:
                set=true;
                added=(List<EditGeom>) e.getNewValue();
                break;

            default:
                
                break;
            }
        }
        
        @Override
        public void batchChange( List<EditBlackboardEvent> e ) {
            for( EditBlackboardEvent event : e ) {
                changed(event);
            }
        }
        
        public void blackBoardChanged( BlackboardEvent event ) {
            if( event.getKey()==EditToolHandler.CURRENT_SHAPE){
                if( event.getNewValue()==null )
                    this.current=null;
                else
                    this.current=((PrimitiveShape) event.getNewValue()).getEditGeom();
                if( event.getOldValue()!=null )
                    this.old=((PrimitiveShape) event.getOldValue()).getEditGeom();      
            }
        }

        public void blackBoardCleared( IBlackboard source ) {
        }

    }
}
