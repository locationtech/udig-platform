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
package org.locationtech.udig.tools.edit.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.locationtech.udig.project.command.CommandManager;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.EditGeom;
import org.locationtech.udig.tools.edit.support.PrimitiveShape;
import org.locationtech.udig.tools.edit.support.TestHandler;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class DeselectEditGeomsCommandTest {

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
    
    /*
     * Test method for 'org.locationtech.udig.tools.edit.commands.DeselectEditGeoms.run(IProgressMonitor)'
     */
    @Ignore
    @Test
    public void testRunDeselectAll() throws Exception {

        SimpleFeature editFeature = handler.getContext().getEditManager().getEditFeature();
        assertEquals(feature.getID(), editFeature.getID());
        
        assertFeatureEqual("edit feature test", feature, editFeature);
        
        assertEquals(layer, handler.getContext().getEditManager().getEditLayer());
        
        DeselectEditGeomCommand command = new DeselectEditGeomCommand(handler, bb.getGeoms());
               
        handler.getContext().sendSyncCommand(command);
        assertNull(handler.getCurrentGeom());
        assertNull(handler.getCurrentShape());
        assertEquals(EditState.NONE, handler.getCurrentState());
        assertEquals(1, bb.getGeoms().size());
        assertFalse(bb.getGeoms().contains(editGeom));
        assertFalse(bb.getGeoms().contains(editGeom2));
        
        assertNull(editFeature);
        
        ((CommandManager)((Map)handler.getContext().getMap()).getCommandStack()).undo(false);
        command.rollback(new NullProgressMonitor());
        assertEquals(editGeom2.getShell().getPoint(0), handler.getCurrentGeom().getShell().getPoint(0));
        assertEquals(editGeom2.getShell().getPoint(1), handler.getCurrentGeom().getShell().getPoint(1));
        assertEquals(hole.getPoint(0), handler.getCurrentShape().getPoint(0));
        assertEquals(hole.getPoint(1), handler.getCurrentShape().getPoint(1));
        assertTrue(handler.getCurrentGeom().getHoles().contains(handler.getCurrentShape()));
        assertEquals(EditState.CREATING, handler.getCurrentState());
        assertEquals(2, bb.getGeoms().size());

        assertFeatureEqual("test edit feature", feature, editFeature);
        assertEquals(layer, handler.getContext().getEditManager().getEditLayer());
    }
    
    @Ignore
    @Test
    public void testDeselectSingle() throws Exception {
        DeselectEditGeomCommand command = new DeselectEditGeomCommand(handler, 
                Collections.singletonList(bb.getGeoms().get(1)));
        
        handler.getContext().sendSyncCommand(command);
        assertNull(handler.getCurrentGeom());
        assertNull(handler.getCurrentShape());
        assertEquals(EditState.NONE, handler.getCurrentState());
        assertEquals(1, bb.getGeoms().size());
        assertTrue(bb.getGeoms().contains(editGeom));
        assertFalse(bb.getGeoms().contains(editGeom2));

        ((CommandManager)((Map)handler.getContext().getMap()).getCommandStack()).undo(false);
        assertEquals(editGeom2.getShell().getPoint(0), handler.getCurrentGeom().getShell().getPoint(0));
        assertEquals(editGeom2.getShell().getPoint(1), handler.getCurrentGeom().getShell().getPoint(1));
        assertEquals(hole.getPoint(0), handler.getCurrentShape().getPoint(0));
        assertEquals(hole.getPoint(1), handler.getCurrentShape().getPoint(1));
        assertTrue(handler.getCurrentGeom().getHoles().contains(handler.getCurrentShape()));
        assertEquals(EditState.CREATING, handler.getCurrentState());
        assertEquals(2, bb.getGeoms().size());
    }

}
