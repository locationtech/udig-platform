/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tutorials.preferredzoomlevels;

import java.io.IOException;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.navigation.SetViewportBBoxCommand;
import org.locationtech.udig.project.ui.tool.AbstractActionTool;
import org.locationtech.udig.ui.ProgressManager;

import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.filter.Filter;

/**
 * This is mostly for testing zoom to an area when preferred zoom levels are enabled
 * @author jeichar
 *
 */
public class ZoomToFeatures extends AbstractActionTool {

	@Override
	public void run() {
		ILayer selectedLayer = getContext().getSelectedLayer();
		Filter selectionFilter = selectedLayer.getFilter();
		try {
			SimpleFeatureSource source = selectedLayer.getResource(SimpleFeatureSource.class, ProgressManager.instance().get());
			ReferencedEnvelope bounds = source.getFeatures(selectionFilter).getBounds();
			getContext().sendASyncCommand(new SetViewportBBoxCommand(bounds, true));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void dispose() {

	}

}
