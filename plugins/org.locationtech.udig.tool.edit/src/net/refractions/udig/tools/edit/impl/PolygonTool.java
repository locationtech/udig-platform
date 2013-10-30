/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tools.edit.impl;

import java.util.List;
import java.util.Set;

import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.AbstractEditTool;
import net.refractions.udig.tools.edit.Activator;
import net.refractions.udig.tools.edit.Behaviour;
import net.refractions.udig.tools.edit.DefaultEditToolBehaviour;
import net.refractions.udig.tools.edit.EditToolConfigurationHelper;
import net.refractions.udig.tools.edit.EnablementBehaviour;
import net.refractions.udig.tools.edit.activator.AdvancedBehaviourCommandHandlerActivator;
import net.refractions.udig.tools.edit.activator.DeleteGlobalActionSetterActivator;
import net.refractions.udig.tools.edit.activator.DrawGeomsActivator;
import net.refractions.udig.tools.edit.activator.GridActivator;
import net.refractions.udig.tools.edit.activator.SetSnapBehaviourCommandHandlerActivator;
import net.refractions.udig.tools.edit.activator.DrawGeomsActivator.DrawType;
import net.refractions.udig.tools.edit.behaviour.AcceptOnDoubleClickBehaviour;
import net.refractions.udig.tools.edit.behaviour.AcceptWhenOverFirstVertexBehaviour;
import net.refractions.udig.tools.edit.behaviour.AddVertexWhileCreatingBehaviour;
import net.refractions.udig.tools.edit.behaviour.CursorControlBehaviour;
import net.refractions.udig.tools.edit.behaviour.DefaultCancelBehaviour;
import net.refractions.udig.tools.edit.behaviour.DrawCreateVertexSnapAreaBehaviour;
import net.refractions.udig.tools.edit.behaviour.InsertVertexOnEdgeBehaviour;
import net.refractions.udig.tools.edit.behaviour.MoveGeometryBehaviour;
import net.refractions.udig.tools.edit.behaviour.MoveVertexBehaviour;
import net.refractions.udig.tools.edit.behaviour.SelectFeatureBehaviour;
import net.refractions.udig.tools.edit.behaviour.SelectVertexBehaviour;
import net.refractions.udig.tools.edit.behaviour.SelectVertexOnMouseDownBehaviour;
import net.refractions.udig.tools.edit.behaviour.SelectVerticesWithBoxBehaviour;
import net.refractions.udig.tools.edit.behaviour.SetSnapSizeBehaviour;
import net.refractions.udig.tools.edit.behaviour.StartEditingBehaviour;
import net.refractions.udig.tools.edit.behaviour.accept.AcceptChangesBehaviour;
import net.refractions.udig.tools.edit.behaviour.accept.DeselectEditShapeAcceptBehaviour;
import net.refractions.udig.tools.edit.enablement.ValidToolDetectionActivator;
import net.refractions.udig.tools.edit.enablement.WithinLegalLayerBoundsBehaviour;
import net.refractions.udig.tools.edit.support.ShapeType;
import net.refractions.udig.tools.edit.validator.PolygonCreationValidator;

import org.eclipse.swt.SWT;
import org.opengis.filter.spatial.Intersects;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Creates and edits Polygons
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class PolygonTool extends AbstractEditTool {

    @Override
    protected void initEnablementBehaviours( List<EnablementBehaviour> helper ) {
        helper.add(new WithinLegalLayerBoundsBehaviour());
        helper.add(new ValidToolDetectionActivator(new Class[]{Geometry.class, Polygon.class, MultiPolygon.class}));
    }

    @Override
    protected void initCancelBehaviours( List<Behaviour> cancelBehaviours ) {
        cancelBehaviours.add(new DefaultCancelBehaviour());
    }

    @Override
    protected void initEventBehaviours( EditToolConfigurationHelper helper ) {
        helper.add( new DrawCreateVertexSnapAreaBehaviour());
        helper.startAdvancedFeatures();
        helper.add( new CursorControlBehaviour(handler, new ConditionalProvider(handler, Messages.PolygonTool_add_vertex_or_finish, Messages.PolygonTool_create_feature),
                new CursorControlBehaviour.SystemCursorProvider(SWT.CURSOR_SIZEALL),new ConditionalProvider(handler, Messages.PolygonTool_move_vertex,null), 
                new CursorControlBehaviour.SystemCursorProvider(SWT.CURSOR_CROSS), new ConditionalProvider(handler, Messages.PolygonTool_add_vertex, null)));
        helper.stopAdvancedFeatures();
//      vertex selection OR geometry selection should not both happen so make them a mutual exclusion behaviour
        helper.startMutualExclusiveList();
        helper.startOrderedList(false);
        AddVertexWhileCreatingBehaviour addVertexWhileCreatingBehaviour = new AddVertexWhileCreatingBehaviour();
        addVertexWhileCreatingBehaviour.setEditValidator(new PolygonCreationValidator());
        helper.add( addVertexWhileCreatingBehaviour);
        helper.add( new AcceptWhenOverFirstVertexBehaviour());
        helper.stopOrderedList();
        helper.startAdvancedFeatures();
        helper.add(new SelectVertexOnMouseDownBehaviour());
        helper.add( new SelectVertexBehaviour());
        helper.stopAdvancedFeatures();

        helper.startAdvancedFeatures();
        SelectFeatureBehaviour selectGeometryBehaviour = new SelectFeatureBehaviour(new Class[]{Polygon.class, MultiPolygon.class}, Intersects.class);
        selectGeometryBehaviour.initDefaultStrategies(ShapeType.POLYGON);
        helper.add( selectGeometryBehaviour);
        helper.add( new InsertVertexOnEdgeBehaviour() );
        
        helper.startElseFeatures();
        helper.add(new StartEditingBehaviour(ShapeType.POLYGON));
        helper.stopElseFeatures();
        
        helper.stopAdvancedFeatures();
        helper.stopMutualExclusiveList();
        
        helper.startAdvancedFeatures();
        helper.startMutualExclusiveList();
        helper.add( new MoveVertexBehaviour() );
        helper.add( new MoveGeometryBehaviour());
        helper.stopMutualExclusiveList();
        
        
        helper.add( new SelectVerticesWithBoxBehaviour() );
        helper.stopAdvancedFeatures();
        helper.add( new AcceptOnDoubleClickBehaviour() );
        helper.add( new SetSnapSizeBehaviour());
        helper.done();

    }

    @Override
    protected void initAcceptBehaviours( List<Behaviour> acceptBehaviours ) {
        acceptBehaviours.add( new AcceptChangesBehaviour(Polygon.class, false) );
        acceptBehaviours.add( new DeselectEditShapeAcceptBehaviour() );
    }

    @Override
    protected void initActivators( Set<Activator> activators ) {
        DrawType type = DrawGeomsActivator.DrawType.POLYGON;
        Set<Activator> defaults = DefaultEditToolBehaviour.createDefaultCreateActivators(type);
        activators.addAll(defaults);
        activators.add(new DeleteGlobalActionSetterActivator());
        activators.add(new SetSnapBehaviourCommandHandlerActivator());
        activators.add(new AdvancedBehaviourCommandHandlerActivator());
        activators.add(new GridActivator());
   }

}
