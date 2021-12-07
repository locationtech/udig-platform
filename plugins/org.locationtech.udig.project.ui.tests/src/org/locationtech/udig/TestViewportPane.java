/**
 * uDig - User Friendly Desktop Internet GIS client
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

import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Control;
import org.junit.Ignore;
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

/**
 * A dummy object for testing
 *
 * @author jones
 * @since 1.1.0
 */
@Ignore
public class TestViewportPane extends TestMapDisplay implements ViewportPane {

    public TestViewportPane(Dimension displaySize) {
        super(displaySize);
    }

    @Override
    public BufferedImage image(int w, int h) {
        return null;
    }

    @Override
    public void update() {
    }

    @Override
    public void repaint() {
    }

    @Override
    public void setCursor(Cursor cursor) {
    }

    @Override
    public void removeMouseListener(MapMouseListener l) {
    }

    @Override
    public void removeMouseMotionListener(MapMouseMotionListener l) {
    }

    @Override
    public void removeMouseWheelListener(MapMouseWheelListener l) {
    }

    @Override
    public void addMouseListener(MapMouseListener l) {
    }

    @Override
    public void addMouseMotionListener(MapMouseMotionListener l) {
    }

    @Override
    public void addMouseWheelListener(MapMouseWheelListener l) {
    }

    @Override
    public void addPaneListener(IMapDisplayListener listener) {
    }

    @Override
    public void removePaneListener(IMapDisplayListener listener) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public void addDrawCommand(IDrawCommand command) {
    }

    @Override
    public void renderStarting() {
    }

    @Override
    public void renderUpdate() {
    }

    @Override
    public void renderDone() {
    }

    @Override
    public void setRenderManager(RenderManager manager) {
    }

    @Override
    public MapPart getMapPart() {
        return null;
    }

    @Override
    public Control getControl() {
        return null;
    }

    @Override
    public void repaint(int x, int y, int width, int height) {
    }

    @Override
    public void enableDrawCommands(boolean enable) {
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public boolean isDisposed() {
        return false;
    }

    @Override
    public GlassPane getGlass() {
        return null;
    }

    @Override
    public void setGlass(GlassPane glass) {
    }

}
