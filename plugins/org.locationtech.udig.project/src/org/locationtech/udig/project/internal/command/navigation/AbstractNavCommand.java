/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.command.navigation;

import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.command.NavCommand;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.render.ViewportModel;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Envelope;

/**
 * TODO Purpose of org.locationtech.udig.project.internal.command.navigation
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
	 * @see org.locationtech.udig.project.internal.command.UndoableCommand#rollback()
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
	 * @see org.locationtech.udig.project.internal.command.MapCommand#run()
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
	 * @see org.locationtech.udig.project.internal.command.navigation.NavCommand#setViewportModel(org.locationtech.udig.project.ViewportModelControl)
	 */
	public void setViewportModel(ViewportModel model) {
		this.model = model;
	}

	/**
	 * @see org.locationtech.udig.project.command.MapCommand#setMap(IMap)
	 * @uml.property name="map"
	 */
	public void setMap(IMap map) {
		this.map = (Map) map;
	}

	/**
	 * @see org.locationtech.udig.project.command.MapCommand#getMap()
	 * @uml.property name="map"
	 */
	public Map getMap() {
		return map;
	}

}
