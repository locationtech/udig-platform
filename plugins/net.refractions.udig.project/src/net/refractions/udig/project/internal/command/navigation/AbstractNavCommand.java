/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.command.navigation;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.NavCommand;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.render.ViewportModel;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

/**
 * TODO Purpose of net.refractions.udig.project.internal.command.navigation
 * <p>
 * </p>
 * 
 * @author Jesse
 * @since 1.0.0
 */
public abstract class AbstractNavCommand implements NavCommand {

	private Envelope oldbbox = null;

	protected ViewportModel model = null;

	private Map map;

	private CoordinateReferenceSystem oldCRS;

	/**
	 * @see net.refractions.udig.project.internal.command.UndoableCommand#rollback()
	 */
	public void rollback(IProgressMonitor monitor) throws Exception {      
	    if ( model == null ){
        // what happens if this gets into the wrong stack.  For example if it is part of a composite command.
        return;
    }

		boolean oldDeliver = model.eDeliver();
		try {
			model.eSetDeliver(false);
			if (!CRS.equalsIgnoreMetadata(model.getCRS(), oldCRS)) {
				model.setCRS(oldCRS);
			}
		} finally {
		    model.eSetDeliver(oldDeliver);
		}
		model.zoomToBox(oldbbox);
	}

	/**
	 * @see net.refractions.udig.project.internal.command.MapCommand#run()
	 */
	public void run(IProgressMonitor monitor) throws Exception {
	    if ( model == null ){
	        // what happens if this gets into the wrong stack.  For example if it is part of a composite command.
	        getMap().sendCommandASync(this);
	        return;
	    }
		oldbbox = model.getBounds();
		oldCRS = model.getCRS();
		runImpl(monitor);
	}

	/**
	 * This where the actual implementation of subclasses should go.
	 * 
	 * @throws Exception
	 */
	protected abstract void runImpl(IProgressMonitor monitor) throws Exception;

	/**
	 * @see net.refractions.udig.project.internal.command.navigation.NavCommand#setViewportModel(net.refractions.udig.project.ViewportModelControl)
	 */
	public void setViewportModel(ViewportModel model) {
		this.model = model;
	}

	/**
	 * @see net.refractions.udig.project.command.MapCommand#setMap(IMap)
	 * @uml.property name="map"
	 */
	public void setMap(IMap map) {
		this.map = (Map) map;
	}

	/**
	 * @see net.refractions.udig.project.command.MapCommand#getMap()
	 * @uml.property name="map"
	 */
	public Map getMap() {
		return map;
	}

}
