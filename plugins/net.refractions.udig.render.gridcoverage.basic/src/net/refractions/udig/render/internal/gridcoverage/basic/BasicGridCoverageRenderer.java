/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.render.internal.gridcoverage.basic;

import java.awt.Graphics2D;
import java.io.IOException;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.render.impl.RendererImpl;
import net.refractions.udig.project.render.RenderException;
import net.refractions.udig.render.gridcoverage.basic.GridCoverageRendererUtils;
import net.refractions.udig.render.gridcoverage.basic.State;
import net.refractions.udig.render.gridcoverage.basic.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.coverage.AbstractCoverage;
import org.geotools.referencing.CRS;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.renderer.lite.GridCoverageRenderer;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * A special renderer optimized for grid coverages. For the moment the
 * symbolizer parameter considered is opacity
 *
 * @author Jesse Eichar
 * @author Andrea Aime
 * @version $Revision: 1.9 $
 */
public class BasicGridCoverageRenderer extends RendererImpl {

    private GridCoverageRenderer renderer;

    private GridCoverage oldCoverage;

    private CoordinateReferenceSystem oldCrs;

    public synchronized void render(Graphics2D destination,
	    IProgressMonitor monitor) throws RenderException {
	State state = null;
	try {
	    state = prepareRender(monitor);
	} catch (IOException e1) {
	    throw new RenderException(e1);
	}
	doRender(renderer, destination, state);
    }

    /**
     * Renders a GridCoverage
     * @param renderer
     * @param graphics
     * @throws RenderException
     */
    public static void doRender(GridCoverageRenderer renderer,
	    Graphics2D graphics, State state) throws RenderException {
	double scale = state.context.getViewportModel().getScaleDenominator();
	if (scale < state.minScale || scale > state.maxScale)
	    return;

	state.context.setStatus(ILayer.WAIT);
	state.context.setStatusMessage(Messages.BasicGridCoverageRenderer_statusMessage);

	try{
		GridCoverageRendererUtils.paintGraphic(renderer, graphics, state);
	}catch (Throwable t) {
		throw new RenderException(Messages.BasicGridCoverageRenderer_errorPainting,t);
	}
	if (state.context.getStatus() == ILayer.WAIT) {
	    //status hasn't changed... everything looks good
	    state.context.setStatus(ILayer.DONE);
	    state.context.setStatusMessage(null);
	}

    }

    private State prepareRender( IProgressMonitor monitor ) throws IOException {
        GridCoverage coverage = getContext().getGeoResource().resolve(GridCoverage.class, monitor);

        CoordinateReferenceSystem layerCRS = getContext().getLayer().getCRS();
        if (coverage instanceof AbstractCoverage && !CRS.equalsIgnoreMetadata(coverage.getCoordinateReferenceSystem(), layerCRS)) {
            ((AbstractCoverage) coverage).setCRS(layerCRS);
        }

        CoordinateReferenceSystem targetCRS = getViewportCRS();
        if (renderer == null || !oldCoverage.equals(coverage) || !oldCrs.equals(targetCRS)) {
            oldCoverage = coverage;
            oldCrs = targetCRS;
            if (coverage == null) {
                getContext().setStatus(ILayer.WARNING);
                getContext().setStatusMessage(Messages.BasicGridCoverageRenderer_0);
                return null;
            }
            renderer = GridCoverageRendererUtils.createRenderer(coverage, targetCRS);
        }


        return GridCoverageRendererUtils.getRenderState(getContext());
    }

    public void stopRendering() {
	setState(STATE_EDEFAULT);
    }

    public void dispose() {
	// TODO
    }

    public void render(IProgressMonitor monitor) throws RenderException {
	render(getContext().getImage().createGraphics(), monitor);
    }

    private CoordinateReferenceSystem getViewportCRS() {
	return getContext().getViewportModel().getCRS();
    }

}
