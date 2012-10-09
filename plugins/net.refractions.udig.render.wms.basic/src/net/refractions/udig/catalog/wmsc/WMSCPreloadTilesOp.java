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
package net.refractions.udig.catalog.wmsc;

import net.refractions.udig.catalog.internal.wmsc.WMSCGeoResourceImpl;
import net.refractions.udig.catalog.wmsc.server.TileSet;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

public class WMSCPreloadTilesOp implements IOp {

	public void op(Display display, Object target, IProgressMonitor monitor)
			throws Exception {
		WMSCGeoResourceImpl wmscResource = (WMSCGeoResourceImpl) target;
		TileSet tileSet = wmscResource.getTileSet();
		WMSCTileUtils.preloadAllTilesOnDisk(tileSet);

	}

}
