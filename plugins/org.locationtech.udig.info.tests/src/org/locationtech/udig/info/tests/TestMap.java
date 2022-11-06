/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2022, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.info.tests;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.geotools.geometry.jts.ReferencedEnvelope;
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
    public TestMap(IRenderManager renderManager, IViewportModel viewportModel,
            List<ILayer> layers) {
        super();
        this.renderManager = renderManager;
        this.viewportModel = viewportModel;
        this.layers = layers;
    }

    @Override
    public void addMapCompositionListener(IMapCompositionListener listener) {

    }

    @Override
    public void addMapListener(IMapListener listener) {

    }

    @Override
    public void executeASyncWithoutUndo(MapCommand command) {

    }

    @Override
    public void executeSyncWithoutUndo(MapCommand command) {

    }

    @Override
    public String getAbstract() {
        return null;
    }

    @Override
    public double getAspectRatio(IProgressMonitor monitor) {
        return 0;
    }

    @Override
    public IBlackboard getBlackboard() {
        return null;
    }

    @Override
    public ReferencedEnvelope getBounds(IProgressMonitor monitor) {
        return null;
    }

    @Override
    public IEditManager getEditManager() {
        return null;
    }

    @Override
    public URI getID() {
        return null;
    }

    @Override
    public List<ILayer> getMapLayers() {
        return this.layers;
    }

    @Override
    public IRenderManager getRenderManager() {
        return this.renderManager;
    }

    @Override
    public IViewportModel getViewportModel() {
        return this.viewportModel;
    }

    @Override
    public void removeMapCompositionListener(IMapCompositionListener listener) {

    }

    @Override
    public void removeMapListener(IMapListener listener) {

    }

    @Override
    public void sendCommandASync(MapCommand command) {

    }

    @Override
    public void sendCommandSync(MapCommand command) {

    }

    @Override
    public String getName() {
        return "Map"; //$NON-NLS-1$
    }

    @Override
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
