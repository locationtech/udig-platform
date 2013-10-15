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

import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

import net.refractions.udig.tools.edit.behaviour.CreateShapeBehaviour;
import net.refractions.udig.tools.edit.behaviour.CreateShapeBehaviour.ShapeFactory;

/**
 * Draws and adds ellipses to a layer.
 * 
 * @author jones
 * @since 1.1.0
 */
public class EllipseTool extends RectangleTool {
    @Override
    protected ShapeFactory getShapeFactory() {
        ShapeFactory ret = new CreateShapeBehaviour.ShapeFactory() {

            @Override
            public GeneralPath create(int width, int height) {
                GeneralPath path=new GeneralPath();
                path.append(new Ellipse2D.Float(-width, -height,2*width, 2*height), false);
                return path;
            }
            
        };
        ret.setMiddleAsOrigin(true);
        return ret;

    }
}
