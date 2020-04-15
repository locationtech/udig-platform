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

import org.locationtech.udig.core.StaticProvider;
import org.locationtech.udig.tool.edit.internal.Messages;
import org.locationtech.udig.tools.edit.AbstractEditTool;
import org.locationtech.udig.tools.edit.Activator;
import org.locationtech.udig.tools.edit.Behaviour;
import org.locationtech.udig.tools.edit.DefaultEditToolBehaviour;
import org.locationtech.udig.tools.edit.EditToolConfigurationHelper;
import org.locationtech.udig.tools.edit.EnablementBehaviour;
import org.locationtech.udig.tools.edit.activator.DrawGeomsActivator;
import org.locationtech.udig.tools.edit.activator.DrawGeomsActivator.DrawType;
import org.locationtech.udig.tools.edit.behaviour.AcceptOnDoubleClickBehaviour;
import org.locationtech.udig.tools.edit.behaviour.CursorControlBehaviour;
import org.locationtech.udig.tools.edit.behaviour.InsertVertexOnEdgeBehaviour;
import org.locationtech.udig.tools.edit.behaviour.SelectFeatureBehaviour;

import org.eclipse.swt.SWT;
import org.opengis.filter.spatial.Intersects;

import org.locationtech.jts.geom.Geometry;

/**
 * A Tool that adds vertices to EditGeoms and selects features.
 * 
 * @author jones
 * @since 1.1.0
 */
public class InsertVertexTool extends AbstractEditTool {

    @Override
    protected void initActivators( Set<Activator> activators ) {
        DrawType type = DrawGeomsActivator.DrawType.POLYGON;
        Set<Activator> defaults = DefaultEditToolBehaviour.createDefaultEditActivators(type);
        activators.addAll(defaults);
    }

    @Override
    protected void initAcceptBehaviours( List<Behaviour> acceptBehaviours ) {
        List<Behaviour> defaults = DefaultEditToolBehaviour.createAcceptAllChanges();
        acceptBehaviours.addAll(defaults);
    }

    @Override
    protected void initCancelBehaviours( List<Behaviour> cancelBehaviours ) {
        List<Behaviour> defaults = DefaultEditToolBehaviour.createDefaultCancelBehaviours();
        cancelBehaviours.addAll(defaults);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initEventBehaviours( EditToolConfigurationHelper helper ) {
        //helper.add( new DrawCreateVertexSnapAreaBehaviour());
        helper.add( new CursorControlBehaviour(handler, new StaticProvider<String>(Messages.AddVertexTool_select_feature),
                null,null, 
                new CursorControlBehaviour.SystemCursorProvider(SWT.CURSOR_CROSS), new StaticProvider<String>(Messages.AddVertexTool_add_vertex)));

//      vertex selection OR geometry selection should not both happen so make them a mutual exclusion behaviour
        helper.startMutualExclusiveList();
        helper.add(new SelectFeatureBehaviour(new Class[]{Geometry.class}, Intersects.class));
        helper.add(new InsertVertexOnEdgeBehaviour());
        helper.stopMutualExclusiveList();
        
        helper.add( new AcceptOnDoubleClickBehaviour() );
        helper.done();
    }

    @Override
    protected void initEnablementBehaviours( List<EnablementBehaviour> enablementBehaviours ) {
        List<EnablementBehaviour> defaults = DefaultEditToolBehaviour.createEnabledWithAllGeometryLayerBehaviour();
        enablementBehaviours.addAll(defaults);
    }


}
