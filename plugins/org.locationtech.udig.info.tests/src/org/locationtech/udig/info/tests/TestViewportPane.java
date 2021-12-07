/**
 *
 */
package org.locationtech.udig.info.tests;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Control;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplayListener;
import org.locationtech.udig.project.ui.commands.IDrawCommand;
import org.locationtech.udig.project.ui.internal.MapPart;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseListener;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseMotionListener;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseWheelListener;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.project.ui.render.glass.GlassPane;

public class TestViewportPane implements ViewportPane {

    private Dimension displaySize;

    public TestViewportPane() {
        super();
    }

    /**
     * @param displaySize
     */
    public TestViewportPane(Dimension displaySize) {
        super();
        this.displaySize = displaySize;
    }

    @Override
    public void addDrawCommand(IDrawCommand command) {

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
    public void dispose() {

    }

    @Override
    public void enableDrawCommands(boolean enable) {

    }

    @Override
    public Control getControl() {
        return null;
    }

    @Override
    public MapPart getMapPart() {
        return null;
    }

    @Override
    public BufferedImage image(int w, int h) {
        return null;
    }

    @Override
    public boolean isDisposed() {
        return false;
    }

    @Override
    public boolean isVisible() {
        return false;
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
    public void removePaneListener(IMapDisplayListener listener) {

    }

    @Override
    public void renderDone() {

    }

    @Override
    public void renderStarting() {

    }

    @Override
    public void renderUpdate() {

    }

    @Override
    public void repaint() {

    }

    @Override
    public void repaint(int x, int y, int width, int height) {

    }

    @Override
    public void setCursor(Cursor cursor) {

    }

    @Override
    public void setRenderManager(RenderManager manager) {

    }

    @Override
    public int getDPI() {
        return 0;
    }

    @Override
    public Dimension getDisplaySize() {

        return displaySize;
    }

    @Override
    public int getHeight() {
        return displaySize.height;
    }

    @Override
    public int getWidth() {
        return displaySize.width;
    }

    @Override
    public void update() {

    }

    @Override
    public GlassPane getGlass() {
        return null;
    }

    @Override
    public void setGlass(GlassPane glass) {

    }

}
