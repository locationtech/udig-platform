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
