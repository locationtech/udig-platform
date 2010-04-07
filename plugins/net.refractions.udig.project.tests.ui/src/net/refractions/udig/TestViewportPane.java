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
package net.refractions.udig;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import net.refractions.udig.project.internal.render.RenderManager;
import net.refractions.udig.project.render.displayAdapter.IMapDisplayListener;
import net.refractions.udig.project.tests.support.TestMapDisplay;
import net.refractions.udig.project.ui.commands.IDrawCommand;
import net.refractions.udig.project.ui.internal.MapPart;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseListener;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseMotionListener;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseWheelListener;
import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;
import net.refractions.udig.project.ui.render.glass.GlassPane;

import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Control;

/**
 * A dummy object for testing
 * 
 * @author jones
 * @since 1.1.0
 */
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
