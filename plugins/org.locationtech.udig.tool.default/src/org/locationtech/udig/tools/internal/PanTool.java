/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
import org.locationtech.udig.project.internal.command.navigation.PanCommand;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.internal.render.impl.ViewportModelImpl;
import org.locationtech.udig.project.ui.internal.commands.draw.TranslateCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.project.ui.tool.AbstractModalTool;
import org.locationtech.udig.project.ui.tool.ModalTool;
import org.locationtech.udig.project.ui.tool.options.ToolOptionContributionItem;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.geotools.geometry.jts.ReferencedEnvelope;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

/**
 * Provides Pan functionality for MapViewport; the technique used for panning is controlled via
 * preferences.
 * <p>
 * There are three strateies avaialble {@link Pan}, {@link Scroll}, {@link FixedScale}.
 * 
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 */
public class PanTool extends AbstractModalTool implements ModalTool {
    public static class OptionContribtionItem extends ToolOptionContributionItem {
        public IPreferenceStore fillFields( Composite parent ) {
            Button check = new Button(parent,  SWT.CHECK );
            check.setText("Scale");
            addField( NavigationToolPreferencePage.SCALE, check );
         
            Button tiled = new Button(parent,  SWT.CHECK );
            tiled.setText("Tiled");
            addField( NavigationToolPreferencePage.TILED, tiled );
            
            return ToolsPlugin.getDefault().getPreferenceStore();
        }
    };
    /**
     * Delegate used to control how the PanTool functions; configured using Preference.
     * @author Jody Garnett (LISAsoft)
     * @since 1.2.0
     */
    abstract class ScrollStrategy {
        public void mouseDragged( MapMouseEvent e ) {
        }
        public void mousePressed( MapMouseEvent e ) {
        }
        public void mouseReleased( MapMouseEvent e ) {
        }
        public void dispose() {
        }
    };
    private ScrollStrategy strategy;
    IPropertyChangeListener prefListener = new IPropertyChangeListener(){
        public void propertyChange( PropertyChangeEvent event ) {
            String property = event.getProperty();
            if( NavigationToolPreferencePage.SCALE.equals( property ) ||
                    NavigationToolPreferencePage.TILED.equals( property ) ){
                syncStrategy();
            }
        }
    };
    /**
     * Creates an new instance of Pan
     */
    public PanTool() {
        super(MOUSE | MOTION);
        IPreferenceStore preferenceStore = ToolsPlugin.getDefault().getPreferenceStore();
        preferenceStore.addPropertyChangeListener(prefListener);
        syncStrategy();
    }
    public void syncStrategy(){
        IPreferenceStore preferenceStore = ToolsPlugin.getDefault().getPreferenceStore();
        boolean scale = preferenceStore.getBoolean(NavigationToolPreferencePage.SCALE);
        boolean tiled = preferenceStore.getBoolean(NavigationToolPreferencePage.TILED);
        if (scale) {
            strategy = new FixedScale();
        }
        else if (tiled){
            strategy = new Scroll();
        }
        else {
            strategy = new Pan();
        }
        
    }
    /**
     * Used to recognise a mouse event and pan accodingly.
     * <p>
     * This functionality is overridden by PanMiddleMouse in order to allow the middle mouse button
     * to provide the Pan functionality for any ModalTool.
     * 
     * @param e
     */
    protected boolean validModifierButtonCombo( MapMouseEvent e ) {
        return e.buttons == MapMouseEvent.BUTTON1 && !(e.modifiersDown());
    }
    /**
     * @see org.locationtech.udig.project.ui.tool.AbstractTool#mouseDragged(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mouseDragged( MapMouseEvent e ) {
        strategy.mouseDragged(e);
    }

    /**
     * @see org.locationtech.udig.project.ui.tool.AbstractTool#mousePressed(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mousePressed( MapMouseEvent e ) {
        strategy.mousePressed(e);
    }

    /**
     * @see org.locationtech.udig.project.ui.tool.AbstractTool#mouseReleased(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mouseReleased( MapMouseEvent e ) {
        strategy.mouseReleased(e);
    }
    /**
     * @see org.locationtech.udig.project.ui.tool.Tool#dispose()
     */
    public void dispose() {
        if (strategy != null) {
            strategy.dispose();
            strategy = null;
        }
        super.dispose();
    }

    /**
     * Executes the specified pan command, and only after it is executed, expires the last translate
     * command
     */
    private class PanAndInvalidate implements Command, NavCommand {
        private NavCommand command;
        private TranslateCommand expire;
        PanAndInvalidate( NavCommand command, TranslateCommand expire ) {
            this.command = command;
            this.expire = expire;
        }

        public Command copy() {
            return new PanAndInvalidate(command, expire);
        }

        public String getName() {
            return "PanAndDiscard";
        }

        public void run( IProgressMonitor monitor ) throws Exception {
            // we need to expire the translate command first otherwise
            // the image gets drawn in the wrong spot the first time
            // and we see weird affects
            expire.setValid(false);

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

    /** Basic Pan Functionality for MapViewport */
    public class Pan extends ScrollStrategy {
        private boolean dragging = false;
        private Point start = null;
        private TranslateCommand command;
        public void mouseDragged( MapMouseEvent e ) {
            if (dragging) {
                command.setTranslation(e.x - start.x, e.y - start.y);
                context.getViewportPane().repaint();
            }
        }

        /**
         * @see org.locationtech.udig.project.ui.tool.AbstractTool#mousePressed(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
         */
        public void mousePressed( MapMouseEvent e ) {
            if (validModifierButtonCombo(e)) {
                ((ViewportPane) context.getMapDisplay()).enableDrawCommands(false);
                dragging = true;
                start = e.getPoint();
                command = context.getDrawFactory().createTranslateCommand(0, 0);
                context.sendASyncCommand(command);
            }
        }

        /**
         * @see org.locationtech.udig.project.ui.tool.AbstractTool#mouseReleased(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
         */
        public void mouseReleased( MapMouseEvent e ) {
            if (dragging) {
                ((ViewportPane) context.getMapDisplay()).enableDrawCommands(true);
                Point end = e.getPoint();
                NavCommand finalPan = new PanCommand((start.x - end.x), (start.y - end.y));

                // clear any events before we try to pan. This dramatically reduces the number
                // of images drawn to the screen in the wrong spot
                ((ViewportPane) getContext().getMapDisplay()).update();

                context.sendASyncCommand(new PanAndInvalidate(finalPan, command));
                dragging = false;
            }
        }
    }
    /**
     * This strategy is used for tiled rendering; it pans without using complex Transforms. It pans
     * using the scroll functionality provided by canvas. This can produce a smoother panning
     * experience.
     * <p>
     * It also allows for rendering systems to notice when panning is occurring and update the image
     * as necessary.
     * </p>
     * <p>
     * If you are using the tile rendering system, this tool will allow for tiles to be loaded while
     * panning.
     * </p>
     * <p>
     * Currently, this tool will not produce an improved panning experience for the default udig
     * rendering system as it this system uses advanced graphics and does not pay attention to the
     * IsBoundsChanging attribute of the viewport model. However it could be updated to pay
     * attention to this attribute.
     * </p>
     */
    class Scroll extends ScrollStrategy {
        private boolean dragging = false;
        private org.eclipse.swt.graphics.Point start = null;
        /**
         * @see org.locationtech.udig.project.ui.tool.AbstractTool#mouseDragged(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
         */
        public void mouseDragged( MapMouseEvent e ) {
            if (dragging) {
                org.eclipse.swt.graphics.Point p = Display.getCurrent().map(
                        (Canvas) context.getViewportPane(), null, e.x, e.y);
                int xdiff = p.x - start.x;
                int ydiff = p.y - start.y;

                final ReferencedEnvelope bounds = context.getViewportModel().getBounds();
                Coordinate oldc = context.pixelToWorld(start.x, start.y);
                Coordinate newc = context.pixelToWorld(p.x, p.y);
                double xoffset = newc.x - oldc.x;
                double yoffset = newc.y - oldc.y;

                ReferencedEnvelope newbounds = new ReferencedEnvelope(bounds.getMinX() - xoffset,
                        bounds.getMaxX() - xoffset, bounds.getMinY() - yoffset, bounds.getMaxY()
                                - yoffset, bounds.getCoordinateReferenceSystem());

                ((Canvas) context.getViewportPane()).scroll(xdiff, ydiff, 0, 0, context
                        .getMapDisplay().getWidth(), context.getMapDisplay().getHeight(), true);
                ((ViewportModel) context.getViewportModel()).setBounds(newbounds);
                start = p;
            }
        }

        /**
         * @see org.locationtech.udig.project.ui.tool.AbstractTool#mousePressed(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
         */
        public void mousePressed( MapMouseEvent e ) {
            if (validModifierButtonCombo(e)) {
                ((ViewportPane) context.getMapDisplay()).enableDrawCommands(false);
                ((ViewportModel) context.getViewportModel()).setIsBoundsChanging(true);
                dragging = true;
                start = Display.getCurrent()
                        .map((Canvas) context.getViewportPane(), null, e.x, e.y);
            }
        }
        public void mouseReleased( MapMouseEvent e ) {
            if (dragging) {

                ((ViewportModel) context.getViewportModel()).setIsBoundsChanging(false);
                ((ViewportPane) context.getMapDisplay()).enableDrawCommands(true);

                dragging = false;

                org.eclipse.swt.graphics.Point p = Display.getCurrent().map(
                        (Canvas) context.getViewportPane(), null, e.x, e.y);
                int xdiff = p.x - start.x;
                int ydiff = p.y - start.y;

                ReferencedEnvelope bounds = context.getViewportModel().getBounds();
                Coordinate oldc = context.pixelToWorld(start.x, start.y);
                Coordinate newc = context.pixelToWorld(p.x, p.y);
                double xoffset = newc.x - oldc.x;
                double yoffset = newc.y - oldc.y;

                ReferencedEnvelope newbounds = new ReferencedEnvelope(bounds.getMinX() - xoffset,
                        bounds.getMaxX() - xoffset, bounds.getMinY() - yoffset, bounds.getMaxY()
                                - yoffset, bounds.getCoordinateReferenceSystem());

                ((Canvas) context.getViewportPane()).scroll(xdiff, ydiff, 0, 0, context
                        .getMapDisplay().getWidth(), context.getMapDisplay().getHeight(), true);
                start = p;

                // do one last set bounds that fires all the events to ensure our update is correct
                ((ViewportModelImpl) context.getViewportModel()).setBounds(newbounds);
            }
        }
        public void dispose() {
            super.dispose();
        }
    }
    /**
     * This tool is supposed to be a fixed scale zoom tool.
     * <p>
     * Internally this strategy uses a "TranslateCommand" to give visual feedback during the mouse
     * drag. When the mouse is released the screen is redrawn as needed.
     * <p>
     * What makes this interesting is the "Fixed" scale aspect; this means that the screen may
     * change resolution as the MapViewport tries to maintain the current scale (as you move north
     * and south). Emily reports that the strategy does not actually do this properly.
     * </p>
     * 
     * @author Emily Gouge
     * @since 1.2.0
     */
    class FixedScale extends ScrollStrategy {
        private boolean dragging = false;
        private Point start = null;
        private ReferencedEnvelope startbounds = null;

        TranslateCommand command;

        /**
         * @see org.locationtech.udig.project.ui.tool.AbstractTool#mouseDragged(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
         */
        public void mouseDragged( MapMouseEvent e ) {
            if (dragging) {
                command.setTranslation(e.x - start.x, e.y - start.y);
                context.getViewportPane().repaint();
            }
        }

        /**
         * @see org.locationtech.udig.project.ui.tool.AbstractTool#mousePressed(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
         */
        public void mousePressed( MapMouseEvent e ) {

            if (validModifierButtonCombo(e)) {
                ((ViewportPane) context.getMapDisplay()).enableDrawCommands(false);
                dragging = true;
                start = e.getPoint();
                startbounds = new ReferencedEnvelope(context.getViewportModel().getBounds());
                command = context.getDrawFactory().createTranslateCommand(0, 0);
                context.sendASyncCommand(command);
            }
        }
        /**
         * @see org.locationtech.udig.project.ui.tool.AbstractTool#mouseReleased(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
         */
        public void mouseReleased( MapMouseEvent e ) {
            if (dragging) {

                ((ViewportPane) context.getMapDisplay()).enableDrawCommands(true);

                // update translation
                command.setTranslation(e.x - start.x, e.y - start.y);
                context.getViewportPane().repaint();

                // compute new offset
                Point end = e.getPoint();

                int deltax = (end.x - start.x);
                double deltaxworld = (getContext().getViewportModel().getWidth() / getContext()
                        .getMapDisplay().getWidth()) * deltax;

                int deltay = end.y - start.y;
                double deltayworld = (getContext().getViewportModel().getHeight() / getContext()
                        .getMapDisplay().getHeight()) * deltay;

                Coordinate center = startbounds.centre();
                final Coordinate newc = new Coordinate(center.x - deltaxworld, center.y
                        + deltayworld);

                double dw = getContext().getViewportModel().getBounds().getWidth() / 2;
                double dh = getContext().getViewportModel().getBounds().getHeight() / 2;

                final Envelope newbounds = new Envelope(newc.x - dw, newc.x + dw, newc.y - dh,
                        newc.y + dh);

                // double currentscale = context.getViewportModel().getBounds().getWidth() /
                // context.getMapDisplay().getWidth();
                // double newscale = newbounds.getWidth() / context.getMapDisplay().getWidth();

                // if (currentscale != newscale){
                // System.out.println("scale changed; should reload." );
                // System.out.println("current scale:" + currentscale);
                // System.out.println("aaaanew scale:" + newscale);
                // }

                // compute new bbox for
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
                // System.out.println("pan done.");
            }
        }
        /**
         * @see org.locationtech.udig.project.ui.tool.Tool#dispose()
         */
        public void dispose() {
            super.dispose();
        }

    }
}
