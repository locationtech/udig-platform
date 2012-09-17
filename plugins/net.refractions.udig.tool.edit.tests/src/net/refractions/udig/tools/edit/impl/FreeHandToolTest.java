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

import static org.junit.Assert.assertNull;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.impl.EditManagerImpl;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.AbstractEditTool;
import net.refractions.udig.tools.edit.EditToolConfigurationHelper;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.support.Point;

import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Geometry;

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
