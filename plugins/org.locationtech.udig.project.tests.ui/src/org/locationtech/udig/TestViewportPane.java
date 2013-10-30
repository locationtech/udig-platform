/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplayListener;
import org.locationtech.udig.project.tests.support.TestMapDisplay;
import org.locationtech.udig.project.ui.commands.IDrawCommand;
import org.locationtech.udig.project.ui.internal.MapPart;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseListener;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseMotionListener;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseWheelListener;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.project.ui.render.glass.GlassPane;

import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Control;
import org.junit.Ignore;

/**
 * A dummy object for testing
 * 
 * @author jones
 * @since 1.1.0
 */
@Ignore
public class TestViewportPane extends TestMapDisplay implements ViewportPane {

    public TestViewportPane( Dimension displaySize ) {
        super(displaySize);
    }

    public BufferedImage image( int w, int h ) {
        return null;
    }
    
    public void update(){
    }

    public void repaint() {
    }

    public void setCursor( Cursor cursor ) {
    }

    public void removeMouseListener( MapMouseListener l ) {
    }

    public void removeMouseMotionListener( MapMouseMotionListener l ) {
    }

    public void removeMouseWheelListener( MapMouseWheelListener l ) {
    }

    public void addMouseListener( MapMouseListener l ) {
    }

    public void addMouseMotionListener( MapMouseMotionListener l ) {
    }

    public void addMouseWheelListener( MapMouseWheelListener l ) {
    }

    public void addPaneListener( IMapDisplayListener listener ) {
    }

    public void removePaneListener( IMapDisplayListener listener ) {
    }

    public void dispose() {
    }

    public void addDrawCommand( IDrawCommand command ) {
    }

    public void renderStarting() {
    }

    public void renderUpdate() {
    }

    public void renderDone() {
    }

    public void setRenderManager( RenderManager manager ) {
    }

    public MapPart getMapEditor() {
        return null;
    }

    public Control getControl() {
        return null;
    }

    public void repaint( int x, int y, int width, int height ) {
    }

    public void enableDrawCommands( boolean enable ) {
    }

    public boolean isVisible() {
        return false;
    }

    public boolean isDisposed() {
        return false;
    }

    public GlassPane getGlass() {
        return null;
    }

    public void setGlass( GlassPane glass ) {
    }

}
