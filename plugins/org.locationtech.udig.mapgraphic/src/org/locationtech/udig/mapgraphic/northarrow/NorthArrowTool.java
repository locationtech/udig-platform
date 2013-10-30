/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.mapgraphic.northarrow;
import java.awt.geom.Rectangle2D;

import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.ui.internal.commands.draw.DrawShapeCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.AbstractModalTool;

/**
 * Simple tool that places a screen position on the style blackboard.
 * <p>
 * This style black board entry can be used to position a mapgraphic.
 * </p>
 * Order of operation:
 * <ol>
 * <li>Map Graphic is created with a default position
 * <li>Tool is enabled via a check with StyleBlackBordEntry
 * </ol>
 * @author Jody Garnett 
 * @since 1.1.0
 */
public class NorthArrowTool extends AbstractModalTool  {

    // I wish we had a general location property; consider using the same thing as scalebar and legend
    public static final String STYLE_BLACKBOARD_KEY = "net.refractions.udig.tool.northarrow-style"; //$NON-NLS-1$

    private DrawShapeCommand command;
    
    public NorthArrowTool() {
        super( MOUSE | MOTION );
    }
    @Override
    public void mouseReleased( MapMouseEvent e ) {
    	command.setValid( false );
    	getContext().getViewportPane().repaint();

    	IBlackboard styleBlackboard = getContext().getSelectedLayer().getStyleBlackboard();
    	
    	styleBlackboard.put( STYLE_BLACKBOARD_KEY, e.getPoint() );
    	getContext().getSelectedLayer().refresh( null );
    }
    
    @Override
    public void mouseDragged( MapMouseEvent e ) {
    	Rectangle2D rect = ((Rectangle2D)command.getShape());
		double w = rect.getWidth();
		double h = rect.getHeight();
        rect.setRect(e.x, e.y-h, w, h);
    	getContext().getViewportPane().repaint();
    }
    
    @Override
    public void mousePressed( MapMouseEvent e ) {
//        IBlackboard blackboard = map.getBlackboard();
//        List<Coordinate> points = (List<Coordinate>) blackboard.get(BLACKBOARD_KEY);
//        if (points == null) {
//            points = new ArrayList<Coordinate>();
//            blackboard.put(BLACKBOARD_KEY,points);
//        }
        
        
//        Double length = (Double)styleBlackboard.get( STYLE_BLACKBOARD_KEY );
        
        Rectangle2D r = new Rectangle2D.Double(e.x,e.y-14,1,14);
        command = getContext().getDrawFactory().createDrawShapeCommand(r);//,Color.BLACK);
        
        getContext().sendASyncCommand(command);
        getContext().getSelectedLayer().refresh(null);
    }

}
