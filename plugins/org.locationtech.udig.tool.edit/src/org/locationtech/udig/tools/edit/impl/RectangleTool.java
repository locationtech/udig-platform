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

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.List;
import java.util.Set;

import org.locationtech.udig.tools.edit.AbstractEditTool;
import org.locationtech.udig.tools.edit.Activator;
import org.locationtech.udig.tools.edit.Behaviour;
import org.locationtech.udig.tools.edit.EditToolConfigurationHelper;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.EnablementBehaviour;
import org.locationtech.udig.tools.edit.activator.ClearCurrentSelectionActivator;
import org.locationtech.udig.tools.edit.activator.EditStateListenerActivator;
import org.locationtech.udig.tools.edit.activator.ResetHandlerActivator;
import org.locationtech.udig.tools.edit.activator.SetRenderingFilter;
import org.locationtech.udig.tools.edit.behaviour.CreateShapeBehaviour;
import org.locationtech.udig.tools.edit.behaviour.DefaultCancelBehaviour;
import org.locationtech.udig.tools.edit.behaviour.CreateShapeBehaviour.ShapeFactory;
import org.locationtech.udig.tools.edit.behaviour.accept.AcceptChangesBehaviour;
import org.locationtech.udig.tools.edit.enablement.ValidToolDetectionActivator;
import org.locationtech.udig.tools.edit.enablement.WithinLegalLayerBoundsBehaviour;
import org.locationtech.udig.tools.edit.support.ShapeType;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

/**
 * Tool for drawing and resizing rectangles.
 * 
 * @author jones
 * @since 1.1.0
 */
public class RectangleTool extends AbstractEditTool {
    @Override
    protected void initEnablementBehaviours( List<EnablementBehaviour> helper ) {
        helper.add(new WithinLegalLayerBoundsBehaviour());
        helper.add(new ValidToolDetectionActivator(new Class[]{Geometry.class, MultiLineString.class, 
                LineString.class, LinearRing.class, Polygon.class, MultiPolygon.class}));
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
        acceptBehaviours.add( new AcceptChangesBehaviour(Polygon.class, true){
            @Override
            public boolean isValid( EditToolHandler handler ) {
                return super.isValid(handler) && handler.getCurrentGeom().getShapeType()==ShapeType.POLYGON;
            }
        });
        acceptBehaviours.add( new AcceptChangesBehaviour(LinearRing.class, true){
            @Override
            public boolean isValid( EditToolHandler handler ) {
                return super.isValid(handler) && handler.getCurrentGeom().getShapeType()==ShapeType.LINE;
            }
        });
        
    }

    @Override
    protected void initCancelBehaviours( List<Behaviour> cancelBehaviours ) {
        cancelBehaviours.add(new DefaultCancelBehaviour());
    }

    @Override
    protected void initEventBehaviours( EditToolConfigurationHelper helper ) {

        helper.add(new CreateShapeBehaviour(getShapeFactory()));
        helper.done();
    }

    protected ShapeFactory getShapeFactory() {
        return new CreateShapeBehaviour.ShapeFactory(){

            @Override
            public GeneralPath create(int width, int height) {
                GeneralPath path=new GeneralPath();
                path.append(new Rectangle(width, height).getPathIterator(new AffineTransform()), false);
                return path;
            }
            
        };
    }

}
