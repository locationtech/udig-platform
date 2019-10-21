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

import java.util.List;
import java.util.Set;

import org.locationtech.udig.tool.edit.internal.Messages;
import org.locationtech.udig.tools.edit.AbstractEditTool;
import org.locationtech.udig.tools.edit.Activator;
import org.locationtech.udig.tools.edit.Behaviour;
import org.locationtech.udig.tools.edit.DefaultEditToolBehaviour;
import org.locationtech.udig.tools.edit.EditToolConfigurationHelper;
import org.locationtech.udig.tools.edit.EnablementBehaviour;
import org.locationtech.udig.tools.edit.activator.DeleteGlobalActionSetterActivator;
import org.locationtech.udig.tools.edit.activator.DrawGeomsActivator;
import org.locationtech.udig.tools.edit.activator.GridActivator;
import org.locationtech.udig.tools.edit.activator.SetSnapBehaviourCommandHandlerActivator;
import org.locationtech.udig.tools.edit.activator.DrawGeomsActivator.DrawType;
import org.locationtech.udig.tools.edit.behaviour.AcceptOnDoubleClickBehaviour;
import org.locationtech.udig.tools.edit.behaviour.AcceptWhenOverFirstVertexBehaviour;
import org.locationtech.udig.tools.edit.behaviour.AddVertexWhileCreatingBehaviour;
import org.locationtech.udig.tools.edit.behaviour.CursorControlBehaviour;
import org.locationtech.udig.tools.edit.behaviour.DefaultCancelBehaviour;
import org.locationtech.udig.tools.edit.behaviour.SelectFeatureBehaviour;
import org.locationtech.udig.tools.edit.behaviour.SetSnapSizeBehaviour;
import org.locationtech.udig.tools.edit.behaviour.StartHoleCuttingBehaviour;
import org.locationtech.udig.tools.edit.behaviour.accept.AcceptChangesBehaviour;
import org.locationtech.udig.tools.edit.enablement.ValidToolDetectionActivator;
import org.locationtech.udig.tools.edit.enablement.WithinLegalLayerBoundsBehaviour;
import org.locationtech.udig.tools.edit.validator.ValidHoleValidator;

import org.eclipse.swt.SWT;
import org.opengis.filter.spatial.BBOX;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

/**
 * Edits and creates holes in Polygons
 * @author jones
 * @since 1.1.0
 */
public class HoleTool extends AbstractEditTool {

    @Override
    protected void initActivators( Set<Activator> activators ) {
        DrawType type = DrawGeomsActivator.DrawType.POLYGON;
        Set<Activator> defaults = DefaultEditToolBehaviour.createDefaultEditActivators(type);
        activators.addAll(defaults);
        
        activators.add(new DeleteGlobalActionSetterActivator());
        activators.add(new SetSnapBehaviourCommandHandlerActivator());
        activators.add(new GridActivator());
    }

    @Override
    protected void initAcceptBehaviours( List<Behaviour> acceptBehaviours ) {
        acceptBehaviours.add( new AcceptChangesBehaviour(Polygon.class, false) );
    }

    @Override
    protected void initCancelBehaviours( List<Behaviour> cancelBehaviours ) {
        cancelBehaviours.add(new DefaultCancelBehaviour());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initEventBehaviours( EditToolConfigurationHelper helper ) {
        helper.add( new CursorControlBehaviour(handler, new ConditionalProvider(handler, Messages.HoleTool_create_feature, Messages.HoleTool_add_vertex_or_finish),
                new CursorControlBehaviour.SystemCursorProvider(SWT.CURSOR_SIZEALL),new ConditionalProvider(handler, Messages.HoleTool_move_vertex,null), 
                new CursorControlBehaviour.SystemCursorProvider(SWT.CURSOR_CROSS), new ConditionalProvider(handler, Messages.HoleTool_add_vertex, null)));


        helper.startMutualExclusiveList();
        helper.startOrderedList(false);
        AddVertexWhileCreatingBehaviour addVertexWhileCreatingBehaviour = new AddVertexWhileCreatingBehaviour();
        addVertexWhileCreatingBehaviour.setEditValidator(new ValidHoleValidator());
        helper.add( addVertexWhileCreatingBehaviour);
        helper.add( new AcceptWhenOverFirstVertexBehaviour());
        helper.stopOrderedList();
        helper.startOrderedList(true);
        // behaviours that select the geometry and hole
        helper.add(new SelectFeatureBehaviour(new Class[]{Polygon.class, MultiPolygon.class}, BBOX.class));
        helper.add( new StartHoleCuttingBehaviour());
        helper.stopOrderedList();
        helper.stopMutualExclusiveList();
        
        helper.add( new SetSnapSizeBehaviour());
        helper.add( new AcceptOnDoubleClickBehaviour() );
        helper.done();
    }

    @Override
    protected void initEnablementBehaviours( List<EnablementBehaviour> helper ) {
        helper.add(new WithinLegalLayerBoundsBehaviour());
        helper.add(new ValidToolDetectionActivator(new Class[]{Geometry.class, Polygon.class, MultiPolygon.class}));
    }

}
