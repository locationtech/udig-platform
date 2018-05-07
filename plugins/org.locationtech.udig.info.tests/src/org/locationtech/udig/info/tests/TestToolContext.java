/**
 * 
 */
package org.locationtech.udig.info.tests;

import java.awt.Point;
import java.util.List;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.Command;
import org.locationtech.udig.project.render.IViewportModel;
import org.locationtech.udig.project.ui.internal.tool.impl.ToolContextImpl;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class TestToolContext extends ToolContextImpl {


	private ReferencedEnvelope bbox;
	private CoordinateReferenceSystem crs;
	private ViewportPane viewportPane;
	private List<ILayer> layers;
	private IViewportModel viewportModel;

	public TestToolContext() {
		super();
	}
	
	

	/**
	 * @param bbox
	 * @param crs
	 * @param viewportPane
	 * @param layers
	 * @param viewportModel
	 */
	public TestToolContext(ReferencedEnvelope bbox, CoordinateReferenceSystem crs, ViewportPane viewportPane, List<ILayer> layers, IViewportModel viewportModel) {
		super();
		this.bbox = bbox;
		this.crs = crs;
		this.viewportPane = viewportPane;
		this.layers = layers;
		this.viewportModel = viewportModel;
	}



	@Override
	public ReferencedEnvelope getBoundingBox(Point screenLocation, int scalefactor) {
		return this.bbox;
	}

	@Override
	public CoordinateReferenceSystem getCRS() {
		return this.crs;
	}

	@Override
	public ViewportPane getViewportPane() {
		return this.viewportPane;
	}

	@Override
	public List<ILayer> getMapLayers() {
		return this.layers;
	}

	@Override
	public IViewportModel getViewportModel() {
		return this.viewportModel;
	}

	@Override
	public void sendASyncCommand(Command command) {
		return;
	}
	
	
}
