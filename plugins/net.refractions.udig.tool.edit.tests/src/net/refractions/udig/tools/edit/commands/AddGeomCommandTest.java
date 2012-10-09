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
import net.refractions.udig.project.command.CommandManager;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import net.refractions.udig.tools.edit.support.TestHandler;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

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
     * Test method for 'net.refractions.udig.tools.edit.commands.AddGeomCommand.run(IProgressMonitor)'
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
