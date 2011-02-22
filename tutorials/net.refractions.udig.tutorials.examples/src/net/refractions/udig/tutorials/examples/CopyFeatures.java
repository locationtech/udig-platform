/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.tutorials.examples;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.commands.edit.CopyFeaturesCommand;

import org.geotools.filter.Filter;

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
