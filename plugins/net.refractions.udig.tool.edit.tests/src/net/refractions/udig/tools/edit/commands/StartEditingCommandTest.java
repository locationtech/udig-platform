/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.tools.edit.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import net.refractions.udig.tools.edit.support.ShapeType;
import net.refractions.udig.tools.edit.support.TestHandler;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

@SuppressWarnings("nls")
public class StartEditingCommandTest {
    private TestHandler handler;
    private EditBlackboard bb;
    private EditGeom editGeom;
    private PrimitiveShape hole;
    private EditGeom editGeom2;
    private Layer layer;
    private SimpleFeature feature;

    @Before
    public void setUp() throws Exception {
        handler=new TestHandler();
        bb= handler.getEditBlackboard();
        editGeom = bb.getGeoms().get(0);
        bb.addPoint(10,10, editGeom.getShell());
        bb.addPoint(20,10, editGeom.getShell());
        bb.addPoint(30,10, editGeom.getShell());
        
        hole = editGeom.newHole();
        bb.addPoint(15,10, hole);
        bb.addPoint(25,10, hole);
        bb.addPoint(35,10, hole);
        
        hole = editGeom.newHole();
        bb.addPoint(17,10, hole);
        bb.addPoint(27,10, hole);
        bb.addPoint(35,10, hole);
        
        editGeom2 = bb.newGeom(null, null);
        
        bb.addPoint(10,15, editGeom2.getShell());
        bb.addPoint(20,15, editGeom2.getShell());
        bb.addPoint(30,15, editGeom2.getShell());
        
        hole = editGeom2.newHole();
        bb.addPoint(15,15, hole);
        bb.addPoint(25,15, hole);
        bb.addPoint(35,15, hole);
        
        hole = editGeom2.newHole();
        bb.addPoint(17,15, hole);
        bb.addPoint(27,15, hole);
        bb.addPoint(35,15, hole);
        
        handler.setCurrentShape(hole);
        handler.setCurrentState(EditState.CREATING);
        
        layer = (Layer) handler.getContext().getMap().getMapLayers().get(0);
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = layer.getResource(FeatureSource.class, null).getFeatures();
        feature = collection.features().next();
        ((Map)handler.getContext().getMap()).getEditManagerInternal().setEditFeature(feature, layer);
    }
    
    /*
     * Test method for 'net.refractions.udig.tools.edit.commands.StartEditingCommand.run(IProgressMonitor)'
     */
    @Test
    public void testRun() throws Exception {
        
        MapMouseEvent event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        StartEditingCommand command=new StartEditingCommand(handler, event, ShapeType.POLYGON);
        command.setMap((Map) handler.getContext().getMap());        
        
        DeselectEditGeomCommand command2 = new DeselectEditGeomCommand(handler, handler.getEditBlackboard().getGeoms());
        command2.setMap((Map) handler.getContext().getMap());
        command2.run(new NullProgressMonitor());
        command.run(new NullProgressMonitor());
        assertEquals(EditState.CREATING, handler.getCurrentState());
        assertEquals(1, handler.getEditBlackboard().getGeoms().size());
        assertEquals(Point.valueOf(10,10), handler.getEditBlackboard().getGeoms().get(0).getShell().getPoint(0));
        SimpleFeature editFeature = handler.getContext().getEditManager().getEditFeature();
		assertNull( editFeature );
        
        command.rollback(new NullProgressMonitor());
        command2.rollback(new NullProgressMonitor());
        
        assertEquals(editGeom2.getShell().getPoint(0), handler.getCurrentGeom().getShell().getPoint(0));
        assertEquals(editGeom2.getShell().getPoint(1), handler.getCurrentGeom().getShell().getPoint(1));
        assertEquals(hole.getPoint(0), handler.getCurrentShape().getPoint(0));
        assertEquals(hole.getPoint(1), handler.getCurrentShape().getPoint(1));
        assertTrue(handler.getCurrentGeom().getHoles().contains(handler.getCurrentShape()));
        assertEquals(EditState.CREATING, handler.getCurrentState());
        assertEquals(2, bb.getGeoms().size());

        editFeature = handler.getContext().getEditManager().getEditFeature();
        
        assertEquals("feature id", feature.getID(), editFeature.getID());
        assertFeatureEqual("edit feature",feature, editFeature);
        assertEquals(layer, handler.getContext().getEditManager().getEditLayer());
        
    }
    
    private void assertFeatureEqual( String msg, SimpleFeature expected, SimpleFeature actual ){
        if( expected == null ){
            assertNull( msg, actual );
        }
        assertEquals( msg, expected.getID(), actual.getID() );
        assertEquals( msg, expected.getIdentifier(), actual.getIdentifier() );

        // assertEquals( msg, expected.getFeatureType(), actual.getFeatureType() );    
        //FeatureTypes.equals( expected, actual );
        //assertEquals( msg, expected, actual );
    }

}
