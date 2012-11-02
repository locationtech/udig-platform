/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.tutorials.shpexport;

import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
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
