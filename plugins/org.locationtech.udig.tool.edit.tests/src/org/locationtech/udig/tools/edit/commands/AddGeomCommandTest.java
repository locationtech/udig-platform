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
import org.locationtech.udig.project.command.CommandManager;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.EditGeom;
import org.locationtech.udig.tools.edit.support.PrimitiveShape;
import org.locationtech.udig.tools.edit.support.TestHandler;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

public class AddGeomCommandTest {
    private TestHandler handler;
    private EditBlackboard bb;
    private EditGeom editGeom;
    private PrimitiveShape hole;

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
    }
    
    /*
     * Test method for 'org.locationtech.udig.tools.edit.commands.AddGeomCommand.run(IProgressMonitor)'
     */
    @Test
    public void testRun() throws Exception {
        GeometryFactory factory = new GeometryFactory();
        MultiPolygon mp=factory.createMultiPolygon(new Polygon[]{createPolygon(factory, 0), createPolygon(factory, 10)});
        
        SimpleFeatureType schema = handler.getEditLayer().getSchema();
        SimpleFeature feature = SimpleFeatureBuilder.build(schema, new Object[]{mp, "test"}, "test"); //$NON-NLS-1$ //$NON-NLS-2$
        SelectFeatureCommand command=new SelectFeatureCommand(handler, feature, null);
        
        assertEquals(1, bb.getGeoms().size());
        handler.getContext().sendSyncCommand(command);
        assertEquals(3, bb.getGeoms().size());
        

        ((CommandManager)((Map)handler.getContext().getMap()).getCommandStack()).undo(false);
        assertEquals(1, bb.getGeoms().size());
        assertEquals(editGeom, bb.getGeoms().get(0));
        
    }

    private Polygon createPolygon(  GeometryFactory factory, int i ) {
        return factory.createPolygon(createShellRing(factory, i), new LinearRing[]{createHoleRing(factory, i)});
    }

    private LinearRing createHoleRing( GeometryFactory factory, int offset) {
        return factory.createLinearRing(new Coordinate[]{
                new Coordinate(offset+5, 7),
                new Coordinate(offset+8, 7),
                new Coordinate(offset+8, 8),
                new Coordinate(offset+5, 8),
                new Coordinate(offset+5, 7)
        });
    }

    private LinearRing createShellRing( GeometryFactory factory, int offset ) {
        return factory.createLinearRing(new Coordinate[]{
                new Coordinate(offset+0,5),
                new Coordinate(offset+10,5),
                new Coordinate(offset+10,10),
                new Coordinate(offset+0,10),
                new Coordinate(offset+0,5)
        });
    }
}
