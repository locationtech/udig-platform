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
package net.refractions.udig.tools.edit.validator;

import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.behaviour.IEditValidator;
import net.refractions.udig.tools.edit.support.EditUtils;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.PrimitiveShape;

/**
 * Disallows:
 * <ul>
 * <li>Self intersections</li>
 * </ul>
 * @author Jesse
 * @since 1.1.0
 */
public class PolygonCreationValidator implements IEditValidator {

    public String isValid( EditToolHandler handler, MapMouseEvent event, EventType type ) {

        PrimitiveShape shell = handler.getCurrentShape();

        assert shell==shell.getEditGeom().getShell();

        Point newPoint = Point.valueOf(event.x, event.y);
        int lastPointIndex = shell.getNumPoints()-1;
        if( shell.getNumPoints()>2 && EditUtils.instance.intersection(shell.getPoint(lastPointIndex), newPoint, shell, 0, lastPointIndex) ){
            return Messages.ValidHoleValidator_selfIntersection;
        }
        return null;
    }

}
