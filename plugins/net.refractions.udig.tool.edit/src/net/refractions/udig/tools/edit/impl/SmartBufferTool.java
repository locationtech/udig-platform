package net.refractions.udig.tools.edit.impl;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.opengis.filter.spatial.Intersects;

import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.AbstractEditTool;
import net.refractions.udig.tools.edit.Activator;
import net.refractions.udig.tools.edit.Behaviour;
import net.refractions.udig.tools.edit.DefaultEditToolBehaviour;
import net.refractions.udig.tools.edit.EditToolConfigurationHelper;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EnablementBehaviour;
import net.refractions.udig.tools.edit.activator.ClearCurrentSelectionActivator;
import net.refractions.udig.tools.edit.activator.EditStateListenerActivator;
import net.refractions.udig.tools.edit.activator.ResetHandlerActivator;
import net.refractions.udig.tools.edit.activator.SetRenderingFilter;
import net.refractions.udig.tools.edit.behaviour.AddVertexWhileCreatingBehaviour;
import net.refractions.udig.tools.edit.behaviour.BufferPolygonCursorControlBehaviour;
import net.refractions.udig.tools.edit.behaviour.BufferPolygonDrawBehaviour;
import net.refractions.udig.tools.edit.behaviour.BufferUpdateBehaviour;
import net.refractions.udig.tools.edit.behaviour.CursorControlBehaviour;
import net.refractions.udig.tools.edit.behaviour.DefaultCancelBehaviour;
import net.refractions.udig.tools.edit.behaviour.DrawCreateVertexSnapAreaBehaviour;
import net.refractions.udig.tools.edit.behaviour.InsertVertexOnEdgeBehaviour;
import net.refractions.udig.tools.edit.behaviour.SelectFeatureBehaviour;
import net.refractions.udig.tools.edit.behaviour.SetSnapSizeBehaviour;
import net.refractions.udig.tools.edit.behaviour.StartEditingBehaviour;
import net.refractions.udig.tools.edit.behaviour.accept.AcceptChangesBehaviour;
import net.refractions.udig.tools.edit.behaviour.accept.DeselectEditShapeAcceptBehaviour;
import net.refractions.udig.tools.edit.enablement.ValidToolDetectionActivator;
import net.refractions.udig.tools.edit.enablement.WithinLegalLayerBoundsBehaviour;
import net.refractions.udig.tools.edit.support.ShapeType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2011, Refractions Research Inc.
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

/**
 * Create a Polygon from a Point or Line input usually involves applying a 
 * Buffer to the Point or Line, the tool will only work on a layer that supports 
 * the Polygon geometry type.
 * 
 * @see BufferGeometryBehaviour
 * @author leviputna
 * @since 1.2.0
 */
public class SmartBufferTool extends AbstractEditTool {

    protected void initEnablementBehaviours( List<EnablementBehaviour> helper ) {
        helper.add(new WithinLegalLayerBoundsBehaviour());
        helper.add(new ValidToolDetectionActivator(new Class[]{Geometry.class, Polygon.class, MultiPolygon.class}));
    }

    @Override
    protected void initActivators( Set<Activator> activators ) {

        activators.add(new EditStateListenerActivator());
        activators.add(new ResetHandlerActivator());
        activators.add(new SetRenderingFilter());
        activators.add(new ClearCurrentSelectionActivator());
    }

    @Override
    protected void initAcceptBehaviours( List<Behaviour> acceptBehaviours ) {
    	List<Behaviour> defaults = DefaultEditToolBehaviour.createDefaultAcceptBehaviour(LineString.class);
        acceptBehaviours.addAll(defaults);

        acceptBehaviours.add( new DeselectEditShapeAcceptBehaviour() );
        
        acceptBehaviours.add( new AcceptChangesBehaviour(Polygon.class, true){
            @Override
            public boolean isValid( EditToolHandler handler ) {
            	return super.isValid(handler) && handler.getCurrentGeom().getShapeType()==ShapeType.POLYGON;
            }
        });
        
    }

    @Override
    protected void initCancelBehaviours( List<Behaviour> cancelBehaviours ) {
        cancelBehaviours.add(new DefaultCancelBehaviour());
    }

    @Override
    protected void initEventBehaviours( EditToolConfigurationHelper helper ) {
    	//helper.add( new DrawCreateVertexSnapAreaBehaviour());
    	
    	helper.add( new AddVertexWhileCreatingBehaviour());
    	
    	BufferUpdateBehaviour bufferUpdateBehaviour = new BufferUpdateBehaviour();
    	helper.add(bufferUpdateBehaviour);
    	helper.add(new BufferPolygonCursorControlBehaviour(bufferUpdateBehaviour));
    	helper.add(new BufferPolygonDrawBehaviour(bufferUpdateBehaviour));
    	
    	//helper.add( new SetSnapSizeBehaviour());
        helper.done();
    }

}