/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tutorials.examples;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.commands.edit.CopyFeaturesCommand;

import org.opengis.filter.Filter;

/**
 * This example shows how to copy features from one layer to another.  It assumes there are two layers
 * the first layer is the layer that is the source and the second is the destination.
 * 
 *  <p>
 *  The command used does a great deal of conversion if required.  For example it will automatically convert
 *  polygons to lines if the target layer is a line layer.  It will attempt to map attributes by matching the 
 *  attribute names (it ignores case).
 *  </p>
 *  
 * @author Jesse
 */
public class CopyFeatures {
	public void copyFeatures( IMap map, Filter filter) {
		CopyFeaturesCommand command = new CopyFeaturesCommand(map.getMapLayers().get(0), filter,
				map.getMapLayers().get(1));
		
		map.sendCommandASync(command);		
	}
}
