/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.tools.internal;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;

import net.refractions.udig.project.command.NavCommand;
import net.refractions.udig.project.internal.command.navigation.NavComposite;
import net.refractions.udig.project.internal.command.navigation.SetViewportCenterCommand;
import net.refractions.udig.project.internal.command.navigation.ZoomCommand;
import net.refractions.udig.project.render.IViewportModel;
import net.refractions.udig.project.ui.commands.DrawCommandFactory;
import net.refractions.udig.project.ui.commands.SelectionBoxCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;
import net.refractions.udig.project.ui.tool.AbstractModalTool;
import net.refractions.udig.project.ui.tool.ModalTool;

/**
 * This class Provides zoom box and click functionality.
 * 
 * @author Jesse Eichar
 * @version 1.3.0
 */
public class Zoom extends AbstractModalTool implements ModalTool {
    /** <code>ZOOMFACTOR</code> field */
    public static final int ZOOMFACTOR = 2;
    /** true if we are in the middle of a zoom (and the map is being rescaled) */
    private boolean zooming;
    private Point start;
    //NavigationCommandFactory factory = NavigationCommandFactory.getInstance();
    DrawCommandFactory dfactory = DrawCommandFactory.getInstance();
    SelectionBoxCommand shapeCommand = new SelectionBoxCommand();

    boolean showContextOnRightClick = false;
    
    /**
     * Creates an new instance of Zoom
     */
    public Zoom() {
        super(MOUSE | MOTION);
    }
    
    public void mouseDragged(MapMouseEvent e) {
        if( start==null ){
            mousePressed(e);
            return;
        }
        shapeCommand.setShape(new Rectangle(Math.min(start.x, e.x), Math.min(start.y, e.y), Math.abs(e.x-start.x), Math.abs(start.y-e.y)));
        context.getViewportPane().repaint();
    }

    Rectangle calculateOriginRectImproved(MapMouseEvent e) {
    	if( start==null )
    		mousePressed(e);
        int x1 = start.x;
        int x2 = e.x;
        int y1 = start.y;
        int y2 = e.y;
        int width1, height1;
        int width2, height2;
        int width, height;
        height1 = Math.abs(y2 - y1);
        width1 = (int) (height1 * context.getViewportModel().getAspectRatio());
        width2 = Math.abs(x2 - x1);
        height2 = (int) (width2 / context.getViewportModel().getAspectRatio());
        // choose heights and widths based on which axis is the longest 
        if( height1 > height2){
            width=width1;
            height=height1;
        }else{
            width=width2;
            height=height2;            
        }

        //center user selected area in center of new box.
        int x=x1,y=y1;
        if (x1 > x2) {
            x = x1 - width+(width-Math.abs(x2-x1))/2;
        }else{
            x=x-(width-Math.abs(x2-x1))/2;
        }
        if (y1 > y2) {
            y = y1 - height+(height-Math.abs(y2-y1))/2;
        }else{
            y=y-(height-Math.abs(y2-y1))/2;
        }
        
        return new Rectangle(x, y, width, height);
    }

    
    /**
     * @see net.refractions.udig.project.tool.AbstractTool#mousePressed(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mousePressed(MapMouseEvent e) {
        if (e.button == MapMouseEvent.BUTTON3 && showContextOnRightClick) {
            ((ViewportPane) e.source).getMapEditor().openContextMenu();
        	return;
    	}
        if ( !e.isAltDown() && !e.isShiftDown() && 
                (e.button == MapMouseEvent.BUTTON1 
                        || e.button == MapMouseEvent.BUTTON3
                        || (e.button == MapMouseEvent.BUTTON1 && e.isControlDown())) ){
            zooming = true;
            start = e.getPoint();
            shapeCommand.setValid(true);
            shapeCommand.setShape(new Rectangle(start.x, start.y, 0, 0));
            getContext().sendASyncCommand(shapeCommand);

        }
    }

    /**
     * @see net.refractions.udig.project.tool.AbstractTool#mouseReleased(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mouseReleased(MapMouseEvent e) {
        if (e.button == MapMouseEvent.BUTTON3  && showContextOnRightClick) {
            ((ViewportPane) e.source).getMapEditor().openContextMenu();
        	return;
    	}
        if (zooming) {
            IViewportModel m = getContext().getViewportModel();
            if ((Math.abs(start.x - e.x)<5) && (Math.abs(start.y - e.y)<5)) {
                switch (e.button) {
                    case MapMouseEvent.BUTTON1: {
                    	if( e.isControlDown() ){
                    		zoomout(m);
                    	} else {
                    		zoomIn(m);
                    	}
                        break;
                    }
                    case MapMouseEvent.BUTTON3: {
                        zoomout(m);
                        break;
                    }
                } //switch
                shapeCommand.setValid(false);
                return;
            }
            Rectangle r = calculateOriginRectImproved(e);
            switch (e.button) {
                case MapMouseEvent.BUTTON1: {
                    if( e.isControlDown() ){
                        zoomout(m,r);
                    }else{
                        zoomin(m, r);
                    }
                    break;
                }
                case MapMouseEvent.BUTTON3: {
                    zoomout(m, r);
                    break;
                }
                default:
                    break;
            }
            zooming = false;
            shapeCommand.setValid(false);
        }
    }

	private void zoomout( IViewportModel m ) {
		NavigationUpdateThread.getUpdater().zoomWithFixedPoint(-ZOOMFACTOR, context, NavigationUpdateThread.DEFAULT_DELAY,start);
    }

	private void zoomIn(IViewportModel m) {
		NavigationUpdateThread.getUpdater().zoomWithFixedPoint(ZOOMFACTOR, context, NavigationUpdateThread.DEFAULT_DELAY,start);
	}

    private void zoomin( IViewportModel m, Rectangle r ) {
    	ZoomCommand cmd = new ZoomCommand(getContext().getMapDisplay().getDisplaySize().getWidth() / r.width);
    	cmd.setFixedPoint(m.pixelToWorld(r.x + r.width / 2, r.y + r.height / 2));
        getContext().sendASyncCommand(cmd);
    }

    private void zoomout( IViewportModel m, Rectangle r ) {
    	ZoomCommand cmd = new ZoomCommand((r.width / getContext().getMapDisplay().getDisplaySize().getWidth()));
    	cmd.setFixedPoint(m.pixelToWorld(
                r.x + r.width / 2, r.y + r.height / 2));
        getContext().sendASyncCommand(cmd);
    }

	public boolean isShowContextOnRightClick() {
		return showContextOnRightClick;
	}

	public void setShowContextOnRightClick(boolean showContextOnRightClick) {
		this.showContextOnRightClick = showContextOnRightClick;
	}
}