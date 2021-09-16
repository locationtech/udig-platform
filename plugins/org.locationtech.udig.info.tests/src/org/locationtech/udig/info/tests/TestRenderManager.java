/**
 *
 */
package org.locationtech.udig.info.tests;

import java.awt.image.RenderedImage;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.geotools.geometry.jts.ReferencedEnvelope;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.render.IRenderManager;
import org.locationtech.udig.project.render.IRenderer;
import org.locationtech.udig.project.render.Tile;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;

import org.locationtech.jts.geom.Envelope;

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

	}

	public RenderedImage getImage() {
		return null;
	}

	public IMap getMap() {
		return null;
	}

	public IMapDisplay getMapDisplay() {
		return this.mapDisplay;
	}

	public List<IRenderer> getRenderers() {
		return null;
	}

	public void refresh(Envelope bounds) {

	}

	public void refresh(ILayer layer, Envelope bounds) {

	}

	public void refreshSelection(ILayer layer, Envelope bounds) {

	}

	public void stopRendering() {

	}

	@Override
	public boolean areLayersRelatedByContext(ILayer layer, ILayer contained) {
		return false;
	}

	@Override
	public Map<ReferencedEnvelope, Tile> getTiles(
			Collection<ReferencedEnvelope> bounds) {
		return null;
	}

	@Override
	public Collection<ReferencedEnvelope> computeTileBounds(
			ReferencedEnvelope viewBounds, double worldunitsperpixel) {
		return null;
	}

}
