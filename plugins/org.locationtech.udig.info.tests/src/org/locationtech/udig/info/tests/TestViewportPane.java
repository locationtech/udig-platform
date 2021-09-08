/**
 *
 */
package org.locationtech.udig.info.tests;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplayListener;
import org.locationtech.udig.project.ui.commands.IDrawCommand;
import org.locationtech.udig.project.ui.internal.MapEditor;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseListener;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseMotionListener;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseWheelListener;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.project.ui.render.glass.GlassPane;

import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Control;

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

	public void addDrawCommand(IDrawCommand command) {

	}

	public void addMouseListener(MapMouseListener l) {

	}

	public void addMouseMotionListener(MapMouseMotionListener l) {

	}

	public void addMouseWheelListener(MapMouseWheelListener l) {

	}

	public void addPaneListener(IMapDisplayListener listener) {

	}

	public void dispose() {

	}

	public void enableDrawCommands(boolean enable) {

	}

	public Control getControl() {
		return null;
	}

	public MapEditor getMapEditor() {
		return null;
	}

	public BufferedImage image(int w, int h) {
		return null;
	}

	public boolean isDisposed() {
		return false;
	}

	public boolean isVisible() {
		return false;
	}

	public void removeMouseListener(MapMouseListener l) {

	}

	public void removeMouseMotionListener(MapMouseMotionListener l) {

	}

	public void removeMouseWheelListener(MapMouseWheelListener l) {

	}

	public void removePaneListener(IMapDisplayListener listener) {

	}

	public void renderDone() {

	}

	public void renderStarting() {

	}

	public void renderUpdate() {

	}

	public void repaint() {

	}

	public void repaint(int x, int y, int width, int height) {

	}

	public void setCursor(Cursor cursor) {

	}

	public void setRenderManager(RenderManager manager) {

	}

	public int getDPI() {
		return 0;
	}

	public Dimension getDisplaySize() {

		return displaySize;
	}

	public int getHeight() {
		return displaySize.height;
	}

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
