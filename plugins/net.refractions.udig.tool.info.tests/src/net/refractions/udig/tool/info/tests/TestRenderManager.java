/**
 * 
 */
package net.refractions.udig.tool.info.tests;

import java.awt.image.RenderedImage;
import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.render.IRenderManager;
import net.refractions.udig.project.render.IRenderer;
import net.refractions.udig.project.render.displayAdapter.IMapDisplay;

import com.vividsolutions.jts.geom.Envelope;

public class TestRenderManager implements IRenderManager {

	private IMapDisplay mapDisplay;

	TestRenderManager() {
	}
	
	/**
	 * @param mapDisplay
	 */
	public TestRenderManager(IMapDisplay mapDisplay) {
		super();
		this.mapDisplay = mapDisplay;
	}



	public void clearSelection(ILayer layer) {
		// TODO Auto-generated method stub
		
	}

	public RenderedImage getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	public IMap getMap() {
		// TODO Auto-generated method stub
		return null;
	}

	public IMapDisplay getMapDisplay() {
		return this.mapDisplay;
	}

	public List<IRenderer> getRenderers() {
		// TODO Auto-generated method stub
		return null;
	}

	public void refresh(Envelope bounds) {
		// TODO Auto-generated method stub
		
	}

	public void refresh(ILayer layer, Envelope bounds) {
		// TODO Auto-generated method stub
		
	}

	public void refreshSelection(ILayer layer, Envelope bounds) {
		// TODO Auto-generated method stub
		
	}

	public void stopRendering() {
		// TODO Auto-generated method stub
		
	}
	
}