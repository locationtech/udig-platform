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

import static org.junit.Assert.assertNull;

import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.impl.EditManagerImpl;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tools.edit.AbstractEditTool;
import org.locationtech.udig.tools.edit.EditToolConfigurationHelper;
import org.locationtech.udig.tools.edit.EventType;
import org.locationtech.udig.tools.edit.support.Point;
import org.opengis.feature.simple.SimpleFeature;

import org.locationtech.jts.geom.Geometry;

/**
 * Test the freehand tool
 * @author jones
 * @since 1.1.0
 */
public class FreeHandToolTest extends AbstractToolTest {

    @Override
    protected AbstractEditTool createTool() {
        return new FreeHandTool();
    }
    
    @Ignore(" EditorManager behavior seems to keep last object, expected is NONE")
    @Test
    public void testCommitAndStartNew() throws Exception {
        tool.setHandler(handler);
        tool.testinitAcceptBehaviours(handler.getAcceptBehaviours());
        tool.testinitEventBehaviours(new EditToolConfigurationHelper(handler.getBehaviours()));
        
        SimpleFeature feature = handler.getFeature(0);
        
        EditManagerImpl editManager = (EditManagerImpl) handler.getContext().getEditManager();
        Layer layer = (Layer) handler.getContext().getMapLayers().get(0);
        editManager.setEditFeature(feature, layer);
        
        handler.getEditBlackboard().addGeometry((Geometry) feature.getDefaultGeometry(), feature.getID());
        
        handler.getMouseTracker().setDragStarted(Point.valueOf(0,10));
        
        MapMouseEvent event=new MapMouseEvent(null, 10,10,MapMouseEvent.NONE,MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);
        
        event=new MapMouseEvent(null, 20,10,MapMouseEvent.NONE,MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);

        event=new MapMouseEvent(null, 20,20,MapMouseEvent.NONE,MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);

        event=new MapMouseEvent(null, 10,20,MapMouseEvent.NONE,MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);

        handler.handleEvent(event, EventType.RELEASED);
        
        handler.handleEvent(event, EventType.DOUBLE_CLICK);
        
        assertNull(handler.getContext().getEditManager().getEditFeature());
        //assertFalse(feature.getID().equals(handler.getContext().getEditManager().getEditFeature().getID()) );
        
    }

}
