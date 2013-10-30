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
package net.refractions.udig.tutorial.tool.coordinate;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.commands.draw.DrawShapeCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.AbstractModalTool;

import com.vividsolutions.jts.geom.Coordinate;

public class CoordinateTool extends AbstractModalTool  {
    
    public CoordinateTool() {
        super(MOUSE);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void mousePressed( MapMouseEvent e ) {
        
        //throw a coordinate onto the current map blackboard
        IMap map = ApplicationGIS.getActiveMap();
        if (map == null)
            return;  
        
        IBlackboard blackboard = map.getBlackboard();
        List<Coordinate> points = 
            (List<Coordinate>) blackboard.get("locations");
        if (points == null) {
            points = new ArrayList<Coordinate>();
            blackboard.put("locations",points);
        }
        
        points.add(new Coordinate(e.x,e.y));
       
        Rectangle2D r = new Rectangle2D.Double(e.x,e.y,2,2);
        DrawShapeCommand command = getContext().getDrawFactory()
        	.createDrawShapeCommand(r,Color.BLACK);
        
        getContext().sendASyncCommand(command);
        getContext().getSelectedLayer().refresh(null);
    }

}
