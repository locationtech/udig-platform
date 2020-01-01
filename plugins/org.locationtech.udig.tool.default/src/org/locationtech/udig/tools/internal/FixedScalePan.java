/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.tools.internal;

import java.awt.Point;

import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.command.Command;
import org.locationtech.udig.project.command.NavCommand;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.command.navigation.AbstractNavCommand;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.ui.internal.commands.draw.TranslateCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.project.ui.tool.AbstractModalTool;
import org.locationtech.udig.project.ui.tool.ModalTool;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.geometry.jts.ReferencedEnvelope;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;


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
     * @see org.locationtech.udig.project.ui.tool.AbstractTool#mouseDragged(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mouseDragged(MapMouseEvent e) {
        if (dragging) {
            command.setTranslation(e.x- start.x, e.y - start.y);
            context.getViewportPane().repaint();
        }
    }

    /**
     * @see org.locationtech.udig.project.ui.tool.AbstractTool#mousePressed(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
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
     * @see org.locationtech.udig.project.ui.tool.AbstractTool#mouseReleased(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
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
     * @see org.locationtech.udig.project.ui.tool.Tool#dispose()
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
