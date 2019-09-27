/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tutorials.shpexport;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureIterator;
import org.locationtech.udig.ui.operations.IOp;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.Name;

import org.locationtech.jts.geom.Geometry;

public class CalculateLengthOp implements IOp {

	public void op(final Display display, Object target,
			IProgressMonitor monitor) throws Exception {		
		SimpleFeatureSource source = (SimpleFeatureSource) target;

		SimpleFeatureCollection featureCollection = source.getFeatures();

		double length = 0.0;
		FeatureIterator<SimpleFeature> iterator = featureCollection.features();
		try {
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				Geometry geometry = (Geometry) feature.getDefaultGeometry();

				length = length + geometry.getLength();
			}
		} finally {
			iterator.close();
		}
		final Name name = source.getName();
		final double answer = length;
		display.asyncExec(new Runnable() {
			public void run() {
				Shell shell = display.getActiveShell();
				MessageDialog.openInformation(shell, "Total Length",
						"Length of " + name + ": " + answer);
			}
		});
	}
	
}
