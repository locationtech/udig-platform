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
package org.locationtech.udig.catalog.shp.op;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.referencing.CRS;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.internal.shp.ShpPlugin;
import org.locationtech.udig.catalog.internal.shp.ShpServiceExtension;
import org.locationtech.udig.core.AdapterUtil;
import org.locationtech.udig.ui.CRSChooserDialog;
import org.locationtech.udig.ui.operations.IOp;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Writes the projection file for a shapefile
 * 
 * @author jesse
 */
public class SetProjection implements IOp {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.locationtech.udig.ui.operations.IOp#op(org.eclipse.swt.widgets.Display,
	 *      java.lang.Object, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void op(final Display display, final Object target,
			IProgressMonitor monitor) throws Exception {
		Object[] targets = (Object[]) target;
		final ShapefileDataStore[] allDatastores = new ShapefileDataStore[targets.length];
		CoordinateReferenceSystem crs = null;

		for (int i = 0; i < allDatastores.length; i++) {
			Object object = targets[i];
			allDatastores[i] = AdapterUtil.instance.adaptTo(
					ShapefileDataStore.class, object, monitor);
			if (crs == null) {
				crs = AdapterUtil.instance.adaptTo(
						CoordinateReferenceSystem.class, object, monitor);
			}
		}

		final CoordinateReferenceSystem finalCRS = crs;

		display.asyncExec(new Runnable() {
			public void run() {
				try {
					CoordinateReferenceSystem initVal = finalCRS;

					if (initVal == null) {
						initVal = allDatastores[0].getSchema()
								.getCoordinateReferenceSystem();
					}
					CRSChooserDialog dialog = new CRSChooserDialog(display
							.getActiveShell(), initVal);
					if (dialog.open() == Window.CANCEL) {
						return;
					}

					CoordinateReferenceSystem result = dialog.getResult();
					if (result != null) {
						for (ShapefileDataStore ds : allDatastores) {
							CoordinateReferenceSystem crs = ds.getSchema().getCoordinateReferenceSystem();
							if (!CRS.equalsIgnoreMetadata(result, crs))
								ds.forceSchemaCRS(result);
							ICatalog catalog = CatalogPlugin.getDefault()
									.getLocalCatalog();
							List<IResolve> members = catalog
									.members(new NullProgressMonitor());
							for (IResolve resolve : members) {
								ShapefileDataStore store = resolve.resolve(
										ShapefileDataStore.class,
										new NullProgressMonitor());
								if (store != null) {
									if (ds.getSchema().getTypeName().equals(
											store.getSchema().getTypeName())) {
										ShpServiceExtension fac = new ShpServiceExtension();
										Map<String, Serializable> params = fac
												.createParams(resolve
														.getIdentifier());
										IService replacement = fac
												.createService(resolve
														.getIdentifier(),
														params);
										catalog.replace(
												resolve.getID(),
												replacement);
									}
								}
							}
						}
					}
				} catch (Exception e) {
					MessageDialog
							.openError(
									display.getActiveShell(),
									"Error writing projection",
									"An unexpected error occurred while setting the projection.\nPlease send error log.");
					ShpPlugin.log("Error writing projection", e);
				}
			}
		});
	}

}
