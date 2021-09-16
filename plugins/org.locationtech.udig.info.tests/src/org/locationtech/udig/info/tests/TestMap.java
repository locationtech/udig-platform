/**
 *
 */
package org.locationtech.udig.info.tests;

import java.io.IOException;
import java.util.List;

import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.IEditManager;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.IMapCompositionListener;
import org.locationtech.udig.project.IMapListener;
import org.locationtech.udig.project.IProject;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.internal.LayerFactory;
import org.locationtech.udig.project.render.IRenderManager;
import org.locationtech.udig.project.render.IViewportModel;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.geotools.geometry.jts.ReferencedEnvelope;

public class TestMap implements IMap {

	private IRenderManager renderManager = new TestRenderManager();
	private IViewportModel viewportModel;
	private List<ILayer> layers;

	public TestMap() {
	}

	/**
	 * @param renderManager
	 * @param viewportModel
	 * @param layers
	 */
	public TestMap(IRenderManager renderManager, IViewportModel viewportModel, List<ILayer> layers) {
		super();
		this.renderManager = renderManager;
		this.viewportModel = viewportModel;
		this.layers = layers;
	}

	public void addMapCompositionListener(IMapCompositionListener listener) {

	}

	public void addMapListener(IMapListener listener) {

	}

	public void executeASyncWithoutUndo(MapCommand command) {

	}

	public void executeSyncWithoutUndo(MapCommand command) {

	}

	public String getAbstract() {
		return null;
	}

	public double getAspectRatio(IProgressMonitor monitor) {
		return 0;
	}

	public IBlackboard getBlackboard() {
		return null;
	}

	// public ReferencedEnvelope getBounds(IProgressMonitor monitor) throws IOException {
	public ReferencedEnvelope getBounds(IProgressMonitor monitor) {
		return null;
	}

	public IEditManager getEditManager() {
		return null;
	}

	public URI getID() {
		return null;
	}

	public List<ILayer> getMapLayers() {
		return this.layers;
	}

	public IRenderManager getRenderManager() {
		return this.renderManager;
	}

	public IViewportModel getViewportModel() {
		return this.viewportModel;
	}

	public void removeMapCompositionListener(IMapCompositionListener listener) {

	}

	public void removeMapListener(IMapListener listener) {

	}

	public void sendCommandASync(MapCommand command) {

	}

	public void sendCommandSync(MapCommand command) {

	}

	public String getName() {
		return "Map";
	}

	public IProject getProject() {
		return null;
	}

	@Override
	public <E> List<E> getElements(Class<E> type) {
		return null;
	}

	@Override
	public List<?> getElements() {
		return null;
	}

	@Override
	public LayerFactory getLayerFactory() {
		return null;
	}

}
