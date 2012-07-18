package net.refractions.udig.tools.edit.commands;

import java.util.Collections;

import junit.framework.TestCase;
import net.refractions.udig.project.EditFeature;
import net.refractions.udig.project.command.CommandManager;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditTestControl;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import net.refractions.udig.tools.edit.support.TestHandler;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureTypes;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class DeselectEditGeomsCommandTest extends TestCase {

    private TestHandler handler;
    private EditBlackboard bb;
    private EditGeom editGeom;
    private PrimitiveShape hole;
    private EditGeom editGeom2;
    private Layer layer;
    private SimpleFeature feature;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
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
    public void assertFeatureEqual( String msg, SimpleFeature expected, SimpleFeature actual ){
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
     * Test method for 'net.refractions.udig.tools.edit.commands.DeselectEditGeoms.run(IProgressMonitor)'
     */
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
        
        if( EditTestControl.DISABLE ) return;
        
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
    
    public void testDeselectSingle() throws Exception {
        if( EditTestControl.DISABLE ) return;
        
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
