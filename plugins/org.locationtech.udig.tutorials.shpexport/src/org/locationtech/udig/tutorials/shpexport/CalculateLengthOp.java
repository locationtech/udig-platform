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

import java.util.Iterator;

import org.locationtech.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;

import com.vividsolutions.jts.geom.Geometry;

public class CalculateLengthOp implements IOp {

	public void op(final Display display, Object target,
			IProgressMonitor monitor) throws Exception {		
		FeatureSource<SimpleFeatureType, SimpleFeature> source = (FeatureSource<SimpleFeatureType, SimpleFeature>) target;

		FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection = source
				.getFeatures();

		double length = 0.0;
		Iterator<SimpleFeature> iterator = featureCollection.iterator();
		try {
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				Geometry geometry = (Geometry) feature.getDefaultGeometry();

				length = length + geometry.getLength();
			}
		} finally {
			featureCollection.close(iterator);
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
