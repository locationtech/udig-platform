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
		// TODO Auto-generated method stub
		
	}

	public void addMouseListener(MapMouseListener l) {
		// TODO Auto-generated method stub
		
	}

	public void addMouseMotionListener(MapMouseMotionListener l) {
		// TODO Auto-generated method stub
		
	}

	public void addMouseWheelListener(MapMouseWheelListener l) {
		// TODO Auto-generated method stub
		
	}

	public void addPaneListener(IMapDisplayListener listener) {
		// TODO Auto-generated method stub
		
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public void enableDrawCommands(boolean enable) {
		// TODO Auto-generated method stub
		
	}

	public Control getControl() {
		// TODO Auto-generated method stub
		return null;
	}

	public MapEditor getMapEditor() {
		// TODO Auto-generated method stub
		return null;
	}

	public BufferedImage image(int w, int h) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isDisposed() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isVisible() {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeMouseListener(MapMouseListener l) {
		// TODO Auto-generated method stub
		
	}

	public void removeMouseMotionListener(MapMouseMotionListener l) {
		// TODO Auto-generated method stub
		
	}

	public void removeMouseWheelListener(MapMouseWheelListener l) {
		// TODO Auto-generated method stub
		
	}

	public void removePaneListener(IMapDisplayListener listener) {
		// TODO Auto-generated method stub
		
	}

	public void renderDone() {
		// TODO Auto-generated method stub
		
	}

	public void renderStarting() {
		// TODO Auto-generated method stub
		
	}

	public void renderUpdate() {
		// TODO Auto-generated method stub
		
	}

	public void repaint() {
		// TODO Auto-generated method stub
		
	}

	public void repaint(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		
	}

	public void setCursor(Cursor cursor) {
		// TODO Auto-generated method stub
		
	}

	public void setRenderManager(RenderManager manager) {
		// TODO Auto-generated method stub
		
	}

	public int getDPI() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public GlassPane getGlass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setGlass(GlassPane glass) {
		// TODO Auto-generated method stub
		
	}

}
