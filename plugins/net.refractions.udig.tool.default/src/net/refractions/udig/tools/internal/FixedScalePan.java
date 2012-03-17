/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2008, Refractions Research Inc.
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

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.Command;
import net.refractions.udig.project.command.NavCommand;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.command.navigation.AbstractNavCommand;
import net.refractions.udig.project.internal.render.ViewportModel;
import net.refractions.udig.project.ui.internal.commands.draw.TranslateCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;
import net.refractions.udig.project.ui.tool.AbstractModalTool;
import net.refractions.udig.project.ui.tool.ModalTool;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.geometry.jts.ReferencedEnvelope;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;


/**
 * This tool is supposed to be a fixed scale zoom tool; although
 * it currently doesn't do that properly.
 * 
 * @author Emily Gouge
 * @since 1.2.0
 * @deprecated PanTool with Tool Options now covers this case
 */
public class FixedScalePan extends AbstractModalTool implements ModalTool {
    private boolean dragging=false;
    private Point start=null;
    private ReferencedEnvelope startbounds = null;

    TranslateCommand command;
    /**
     * Creates an new instance of Pan
     */
    public FixedScalePan() {
        super(MOUSE | MOTION);
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseDragged(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mouseDragged(MapMouseEvent e) {
        if (dragging) {
            command.setTranslation(e.x- start.x, e.y - start.y);
            context.getViewportPane().repaint();
        }
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mousePressed(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mousePressed(MapMouseEvent e) {
        
        if (validModifierButtonCombo(e)) {
            ((ViewportPane)context.getMapDisplay()).enableDrawCommands(false);
            dragging = true;
            start = e.getPoint();
            startbounds = new ReferencedEnvelope(context.getViewportModel().getBounds());
            command=context.getDrawFactory().createTranslateCommand(0,0);
            context.sendASyncCommand(command);            
        }
    }

    /**
     * Returns true if the combination of buttons and modifiers are legal to execute the pan.
     * <p>
     * This version returns true if button 1 is down and no modifiers
     * </p>
     * @param e
     * @return
     */
    protected boolean validModifierButtonCombo(MapMouseEvent e) {
        return e.buttons== MapMouseEvent.BUTTON1
                && !(e.modifiersDown());
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseReleased(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mouseReleased(MapMouseEvent e) {
        if (dragging) {

            ((ViewportPane)context.getMapDisplay()).enableDrawCommands(true);
            
            //update translation
            command.setTranslation(e.x- start.x, e.y - start.y);
            context.getViewportPane().repaint();
            
            //compute new offset
            Point end=e.getPoint();
            
            int deltax = (end.x - start.x);
            double deltaxworld = ( getContext().getViewportModel().getWidth() / getContext().getMapDisplay().getWidth() ) * deltax;
                        
            int deltay = end.y - start.y;
            double deltayworld = ( getContext().getViewportModel().getHeight() / getContext().getMapDisplay().getHeight() ) * deltay;

            Coordinate center = startbounds.centre();            
            final Coordinate newc = new Coordinate(center.x - deltaxworld, center.y + deltayworld);
            
            double dw = getContext().getViewportModel().getBounds().getWidth() / 2;
            double dh = getContext().getViewportModel().getBounds().getHeight() / 2;
            
            final Envelope newbounds = new Envelope(newc.x - dw, newc.x + dw, newc.y - dh, newc.y + dh);
            
//            double currentscale = context.getViewportModel().getBounds().getWidth() / context.getMapDisplay().getWidth();
//            double newscale = newbounds.getWidth() / context.getMapDisplay().getWidth();
            
//            if (currentscale != newscale){
//                System.out.println("scale changed; should reload." );
//                System.out.println("current scale:" + currentscale);
//                System.out.println("aaaanew scale:" + newscale);
//            }
            
            //compute new bbox for 
            NavCommand setFinal = new AbstractNavCommand(){

                @Override
                protected void runImpl( IProgressMonitor monitor ) throws Exception {
                    model.setBounds(newbounds);
                }

                
                public Command copy() {
                    return null;
                }

                
                public String getName() {
                    return "Fixed Scale Pan"; //$NON-NLS-1$
                }
            };
            
            ((ViewportPane) getContext().getMapDisplay()).update();
            context.sendASyncCommand(new PanAndInvalidate(setFinal, command));

            dragging = false;
//            System.out.println("pan done.");
        }
    }
    /**
     * @see net.refractions.udig.project.ui.tool.Tool#dispose()
     */
    public void dispose() {
        super.dispose();
    }

    /**
     * Executes the specified pan command, and only after it is executed, expires the last translate command
     */
    private class PanAndInvalidate implements Command, NavCommand {

        private NavCommand command;
        private TranslateCommand expire;

        PanAndInvalidate(NavCommand command, TranslateCommand expire) {
            this.command = command;
            this.expire = expire;
        }

        public Command copy() {
            return new PanAndInvalidate(command, expire);
        }

        public String getName() {
            return "PanAndDiscard"; //$NON-NLS-1$
        }

        public void run( IProgressMonitor monitor ) throws Exception {
            //first we need to expire the current translation
            expire.setValid(false);
            
            //then we can draw
            command.run(monitor);
        }

        public void setViewportModel( ViewportModel model ) {
            command.setViewportModel(model);
        }

        public Map getMap() {
            return command.getMap();
        }

        public void setMap( IMap map ) {
            command.setMap(map);
        }

        public void rollback( IProgressMonitor monitor ) throws Exception {
            command.rollback(monitor);
        }

    }
}