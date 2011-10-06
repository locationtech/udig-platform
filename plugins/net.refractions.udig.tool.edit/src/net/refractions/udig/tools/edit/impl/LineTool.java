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
import net.refractions.udig.tools.edit.behaviour.StartExtendLineBehaviour;
import net.refractions.udig.tools.edit.behaviour.accept.DeselectEditShapeAcceptBehaviour;
import net.refractions.udig.tools.edit.enablement.ValidToolDetectionActivator;
import net.refractions.udig.tools.edit.enablement.WithinLegalLayerBoundsBehaviour;
import net.refractions.udig.tools.edit.support.ShapeType;

import org.eclipse.swt.SWT;
import org.opengis.filter.spatial.Intersects;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;

/**
 * Creates and edits lines
 * @author jones
 * @since 1.1.0
 */
public class LineTool extends AbstractEditTool {

    @Override
    protected void initActivators( Set<Activator> activators ) {
        DrawType type = DrawGeomsActivator.DrawType.LINE;
        Set<Activator> defaults = DefaultEditToolBehaviour.createDefaultCreateActivators(type);
        activators.addAll(defaults);
        activators.add(new DeleteGlobalActionSetterActivator());
        activators.add(new SetSnapBehaviourCommandHandlerActivator());
        activators.add(new AdvancedBehaviourCommandHandlerActivator());
        activators.add(new GridActivator());
    }

    @Override
    protected void initAcceptBehaviours( List<Behaviour> acceptBehaviours ) {
        List<Behaviour> defaults = DefaultEditToolBehaviour.createDefaultAcceptBehaviour(LineString.class);
        acceptBehaviours.addAll(defaults);

        acceptBehaviours.add( new DeselectEditShapeAcceptBehaviour() );
    }

    @Override
    protected void initCancelBehaviours( List<Behaviour> cancelBehaviours ) {
        cancelBehaviours.add(new DefaultCancelBehaviour());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initEventBehaviours( EditToolConfigurationHelper helper ) {
        helper.add( new DrawCreateVertexSnapAreaBehaviour());

        helper.startAdvancedFeatures();
        helper.add( new CursorControlBehaviour(handler, new ConditionalProvider(handler, Messages.LineTool_select_or_create_feature, Messages.LineTool_add_vertex_or_finish),
                new CursorControlBehaviour.SystemCursorProvider(SWT.CURSOR_SIZEALL),new ConditionalProvider(handler, Messages.LineTool_move_vertex,null), 
                new CursorControlBehaviour.SystemCursorProvider(SWT.CURSOR_CROSS), new ConditionalProvider(handler, Messages.LineTool_add_vertex, null)));
        helper.stopAdvancedFeatures();

//      vertex selection OR geometry selection should not both happen so make them a mutual exclusion behaviour
        helper.startMutualExclusiveList();

        helper.add( new AddVertexWhileCreatingBehaviour());

        helper.startAdvancedFeatures();
        helper.add(new SelectVertexOnMouseDownBehaviour());
        helper.add( new SelectVertexBehaviour());
        helper.stopAdvancedFeatures();
        
        helper.startAdvancedFeatures();
        SelectFeatureBehaviour selectGeometryBehaviour = new SelectFeatureBehaviour(new Class[]{LineString.class, LinearRing.class, MultiLineString.class}, Intersects.class);
        selectGeometryBehaviour.initDefaultStrategies(ShapeType.LINE);
        helper.add(selectGeometryBehaviour);
        helper.add(new InsertVertexOnEdgeBehaviour());

        helper.startElseFeatures();
        helper.add(new StartEditingBehaviour(ShapeType.LINE));
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
        helper.add( new StartExtendLineBehaviour() );
        helper.done();
    }

    @Override
    protected void initEnablementBehaviours( List<EnablementBehaviour> helper ) {
        helper.add(new WithinLegalLayerBoundsBehaviour());
        helper.add(new ValidToolDetectionActivator(new Class[]{Geometry.class, LineString.class, MultiLineString.class}));
    }

}
