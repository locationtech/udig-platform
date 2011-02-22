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

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.List;
import java.util.Set;

import net.refractions.udig.tools.edit.AbstractEditTool;
import net.refractions.udig.tools.edit.Activator;
import net.refractions.udig.tools.edit.Behaviour;
import net.refractions.udig.tools.edit.EditToolConfigurationHelper;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EnablementBehaviour;
import net.refractions.udig.tools.edit.activator.EditStateListenerActivator;
import net.refractions.udig.tools.edit.activator.ResetHandlerActivator;
import net.refractions.udig.tools.edit.activator.SetRenderingFilter;
import net.refractions.udig.tools.edit.behaviour.DefaultCancelBehaviour;
import net.refractions.udig.tools.edit.behaviour.CreateShapeBehaviour;
import net.refractions.udig.tools.edit.behaviour.CreateShapeBehaviour.ShapeFactory;
import net.refractions.udig.tools.edit.behaviour.accept.AcceptChangesBehaviour;
import net.refractions.udig.tools.edit.enablement.ValidToolDetectionActivator;
import net.refractions.udig.tools.edit.enablement.WithinLegalLayerBoundsBehaviour;
import net.refractions.udig.tools.edit.support.ShapeType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

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
