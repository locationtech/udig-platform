/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tutorials.tool.coverage;

import java.util.List;
import java.util.Set;

import org.locationtech.udig.tools.edit.AbstractEditTool;
import org.locationtech.udig.tools.edit.Activator;
import org.locationtech.udig.tools.edit.Behaviour;
import org.locationtech.udig.tools.edit.DefaultEditToolBehaviour;
import org.locationtech.udig.tools.edit.EditToolConfigurationHelper;
import org.locationtech.udig.tools.edit.EnablementBehaviour;
import org.locationtech.udig.tools.edit.activator.DrawGeomsActivator.DrawType;
import org.locationtech.udig.tools.edit.behaviour.MoveVertexBehaviour;
import org.locationtech.udig.tools.edit.behaviour.SelectFeatureBehaviour;
import org.locationtech.udig.tools.edit.behaviour.SelectVertexBehaviour;
import org.locationtech.udig.tools.edit.behaviour.SelectVertexOnMouseDownBehaviour;

import org.opengis.filter.spatial.Intersects;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

public class CoverageTool extends AbstractEditTool {
    
    @Override
    protected void initAcceptBehaviours( List<Behaviour> acceptBehaviours ) {
        List<Behaviour> defaults = DefaultEditToolBehaviour.createAcceptAllChanges();
        acceptBehaviours.addAll(defaults);
    }
    
    @Override
    protected void initActivators( Set<Activator> activators ) {
        DrawType geometryType=DrawType.POLYGON;
        Set<Activator> defaults = DefaultEditToolBehaviour.createDefaultCreateActivators(geometryType);
        activators.addAll(defaults);
    }
    
    @Override
    protected void initCancelBehaviours( List<Behaviour> cancelBehaviours ) {
        List<Behaviour> defaults = DefaultEditToolBehaviour.createDefaultCancelBehaviours();
        cancelBehaviours.addAll(defaults);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected void initEnablementBehaviours( List<EnablementBehaviour> enablementBehaviours ) {
        Class< ? extends Geometry>[] classes = new Class[]{
                Polygon.class, MultiPolygon.class
        };
        List<EnablementBehaviour> defaults = DefaultEditToolBehaviour.createValidToolEnablementBehaviour(classes );
        enablementBehaviours.addAll(defaults);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected void initEventBehaviours( EditToolConfigurationHelper helper ) {
        helper.startMutualExclusiveList();
        helper.add(new SelectVertexOnMouseDownBehaviour());
        helper.add(new SelectVertexBehaviour());
        SelectFeatureBehaviour selectFeatureBehaviour =
        	new SelectFeatureBehaviour(new Class[]{Geometry.class}, Intersects.class );
        selectFeatureBehaviour.addSelectionStrategy(new SelectNeightborsStrategy());
        
        helper.add(selectFeatureBehaviour);
        helper.stopMutualExclusiveList();
    
        helper.add( new MoveVertexBehaviour() );
        
        helper.done();
    }

}
