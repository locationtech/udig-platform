package net.refractions.udig.tools.edit.commands;

import java.util.Collections;

import junit.framework.TestCase;
import net.refractions.udig.project.command.CommandManager;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import net.refractions.udig.tools.edit.support.TestHandler;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.feature.Feature;

public class DeselectEditGeomsCommandTest extends TestCase {

    private TestHandler handler;
    private EditBlackboard bb;
    private EditGeom editGeom;
    private PrimitiveShape hole;
    private EditGeom editGeom2;
    private Layer layer;
    private Feature feature;

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
        feature = layer.getResource(FeatureSource.class, null).getFeatures().features().next();
        ((Map)handler.getContext().getMap()).getEditManagerInternal().setEditFeature(feature, layer);
    }

    /*
     * Test method for 'net.refractions.udig.tools.edit.commands.DeselectEditGeoms.run(IProgressMonitor)'
     */
    public void testRunDeselectAll() throws Exception {

        assertEquals(feature, handler.getContext().getEditManager().getEditFeature());
        assertEquals(layer, handler.getContext().getEditManager().getEditLayer());

        DeselectEditGeomCommand command = new DeselectEditGeomCommand(handler, bb.getGeoms());

        handler.getContext().sendSyncCommand(command);
        assertNull(handler.getCurrentGeom());
        assertNull(handler.getCurrentShape());
        assertEquals(EditState.NONE, handler.getCurrentState());
        assertEquals(1, bb.getGeoms().size());
        assertFalse(bb.getGeoms().contains(editGeom));
        assertFalse(bb.getGeoms().contains(editGeom2));
        assertNull(handler.getContext().getEditManager().getEditFeature());

        ((CommandManager)((Map)handler.getContext().getMap()).getCommandStack()).undo(false);
        command.rollback(new NullProgressMonitor());
        assertEquals(editGeom2.getShell().getPoint(0), handler.getCurrentGeom().getShell().getPoint(0));
        assertEquals(editGeom2.getShell().getPoint(1), handler.getCurrentGeom().getShell().getPoint(1));
        assertEquals(hole.getPoint(0), handler.getCurrentShape().getPoint(0));
        assertEquals(hole.getPoint(1), handler.getCurrentShape().getPoint(1));
        assertTrue(handler.getCurrentGeom().getHoles().contains(handler.getCurrentShape()));
        assertEquals(EditState.CREATING, handler.getCurrentState());
        assertEquals(2, bb.getGeoms().size());

        assertEquals(feature, handler.getContext().getEditManager().getEditFeature());
        assertEquals(layer, handler.getContext().getEditManager().getEditLayer());
    }

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
