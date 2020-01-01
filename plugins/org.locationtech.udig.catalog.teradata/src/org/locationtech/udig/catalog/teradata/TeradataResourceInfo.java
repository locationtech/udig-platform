/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.teradata;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.IResolve.Status;
import org.locationtech.udig.ui.graphics.Glyph;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.DataSourceException;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Envelope;

import org.locationtech.udig.catalog.teradata.internal.Messages;

class TeradataResourceInfo extends IGeoResourceInfo {

	private TeradataGeoResource owner;

	TeradataResourceInfo(TeradataGeoResource owner) throws IOException {
		this.owner = owner;
		this.name = owner.typename;
		if(owner.desc.geometryType != null) {
			this.icon = Glyph.icon(owner.desc.geometryType);
		}
		
		keywords = new String[] { "teradata", //$NON-NLS-1$
				owner.typename };
	}
	
	@Override
	public synchronized ReferencedEnvelope getBounds() {
		if (bounds == null) {

			try {
				@SuppressWarnings("unchecked")
				FeatureSource<SimpleFeatureType, SimpleFeature> source = owner
						.resolve(FeatureSource.class, new NullProgressMonitor());
				ReferencedEnvelope temp = source.getBounds();

				bounds = temp;
				if (bounds == null) {
					CoordinateReferenceSystem crs = getCRS();
					// try getting an envelope out of the crs
					org.opengis.geometry.Envelope envelope = CRS
							.getEnvelope(crs);

					if (envelope != null) {
						bounds = new ReferencedEnvelope(new Envelope(envelope
								.getLowerCorner().getOrdinate(0), envelope
								.getUpperCorner().getOrdinate(0), envelope
								.getLowerCorner().getOrdinate(1), envelope
								.getUpperCorner().getOrdinate(1)), crs);
					} else {
						// TODO: perhaps access a preference which indicates
						// whether to do a full table scan
						// bounds = new ReferencedEnvelope(new Envelope(),crs);
						// as a last resort do the full scan
						bounds = new ReferencedEnvelope(new Envelope(), crs);
						FeatureIterator<SimpleFeature> iter = source
								.getFeatures().features();
						try {
							while (iter.hasNext()) {
								SimpleFeature element = iter.next();
								if (bounds.isNull())
									bounds.init(element.getBounds());
								else
									bounds.include(element.getBounds());
							}
						} finally {
							iter.close();
						}
					}
				}
			} catch (DataSourceException e) {
				Activator.log(
						"Exception while generating TeradataGeoResource.", e); //$NON-NLS-1$
			} catch (Exception e) {
				CatalogPlugin
						.getDefault()
						.getLog()
						.log(new org.eclipse.core.runtime.Status(
								IStatus.WARNING,
								"org.locationtech.udig.catalog", 0, Messages.TeradataGeoResource_error_layer_bounds, e)); //$NON-NLS-1$
				bounds = new ReferencedEnvelope(new Envelope(), null);
			}

		}
		return bounds;
	}

	public CoordinateReferenceSystem getCRS() {
		SimpleFeatureType ft = getFeatureType();
		if(ft == null) {
			return DefaultGeographicCRS.WGS84;
		}
		
		return ft.getGeometryDescriptor().getCoordinateReferenceSystem();
	}

	private SimpleFeatureType getFeatureType() {
		try {
			return owner.getSchema();
		} catch (IOException e) {
			if (e.getMessage().contains("permission")) { //$NON-NLS-1$
				owner.setStatus(Status.RESTRICTED_ACCESS, e);
			} else {
				owner.setStatus(Status.BROKEN, e);
			}
			Activator
					.log("Unable to retrieve SimpleFeatureType schema for type '" + owner.typename + "'.", e); //$NON-NLS-1$ //$NON-NLS-2$
			keywords = new String[] { "teradata", //$NON-NLS-1$
					owner.typename };
			return null;
		}

	}

	public String getName() {
		return owner.typename;
	}

	public URI getSchema() {
		SimpleFeatureType ft = getFeatureType();
		if (ft == null) return null;
		
		try {
			Name typeName = ft.getName();
			if (typeName.getNamespaceURI() != null) {
				return new URI(ft.getName().getNamespaceURI());
			} else {
				return null; // should probably be GML?
			}
		} catch (URISyntaxException e) {
			return null;
		}
	}

	public String getTitle() {
		return owner.typename;
	}
}
