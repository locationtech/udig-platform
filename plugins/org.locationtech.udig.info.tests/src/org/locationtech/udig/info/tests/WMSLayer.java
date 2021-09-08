/**
 *
 */
package org.locationtech.udig.info.tests;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Set;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.IBlackboardListener;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ILayerListener;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.IStyleBlackboard;
import org.locationtech.udig.project.Interaction;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.geotools.data.Query;
import org.geotools.ows.wms.Layer;
import org.geotools.ows.wms.WebMapServer;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import org.locationtech.jts.geom.Envelope;

public class WMSLayer implements ILayer {;

	private IMap map;
	private Layer wmslayer;
	private WebMapServer wms;

	public WMSLayer() {

	}

	/**
	 * @param map
	 * @param wmslayer
	 * @param wms
	 */
	public WMSLayer(IMap map, Layer wmslayer, WebMapServer wms) {
		super();
		this.map = map;
		this.wmslayer = wmslayer;
		this.wms = wms;
	}

	public void addListener(ILayerListener listener) {

	}

	// public ReferencedEnvelope getBounds(IProgressMonitor monitor, CoordinateReferenceSystem crs) throws IOException {
	public ReferencedEnvelope getBounds(IProgressMonitor monitor, CoordinateReferenceSystem crs) {
		return null;
	}

	public CoordinateReferenceSystem getCRS(IProgressMonitor monitor) {
		return null;
	}

	public CoordinateReferenceSystem getCRS() {
		return null;
	}

	public IGeoResource getGeoResource() {
		return null;
	}

	public <T> IGeoResource getGeoResource(Class<T> clazz) {
		return null;
	}

	public List<IGeoResource> getGeoResources() {
		return null;
	}

	public ImageDescriptor getIcon() {
		return null;
	}

	public URL getID() {
		return null;
	}

	public IMap getMap() {
		return this.map;
	}

	public String getName() {
		return "TestMap";
	}

	public IBlackboard getProperties() {
		return null;
	}

	public Query getQuery(boolean selection) {
		return null;
	}

	public <E> E getResource(Class<E> resourceType, IProgressMonitor monitor) throws IOException {
		if (resourceType == Layer.class) {
			return (E) this.wmslayer;
		}

		if (resourceType == WebMapServer.class) {
			return (E) this.wms;
		}
		return null;
	}

	public SimpleFeatureType getSchema() {
		return null;
	}

	public int getStatus() {
		return 0;
	}

	public String getStatusMessage() {
		return null;
	}

	public IStyleBlackboard getStyleBlackboard() {
		return new IStyleBlackboard() {

			@Override
			public boolean addListener(IBlackboardListener listener) {
				return false;
			}

			@Override
			public boolean removeListener(IBlackboardListener listener) {
				return false;
			}

			@Override
			public boolean contains(String key) {
				return false;
			}

			@Override
			public Object get(String key) {
				return null;
			}

			@Override
			public void put(String key, Object value) {

			}

			@Override
			public Float getFloat(String key) {
				return null;
			}

			@Override
			public Integer getInteger(String key) {
				return null;
			}

			@Override
			public String getString(String key) {
				return null;
			}

			@Override
			public void putFloat(String key, float value) {

			}

			@Override
			public void putInteger(String key, int value) {

			}

			@Override
			public void putString(String key, String value) {

			}

			@Override
			public Object remove(String key) {
				return null;
			}

			@Override
			public void clear() {

			}

			@Override
			public void flush() {

			}

			@Override
			public void addAll(IBlackboard blackboard) {

			}

			@Override
			public Set<String> keySet() {
				return null;
			}

			@Override
			public boolean isSelected(String styleId) {
				return false;
			}

		};
	}

	public int getZorder() {
		return 0;
	}

	public boolean isApplicable(String toolCategoryId) {
		return false;
	}

	public <T> boolean isType(Class<T> resourceType) {
		if (resourceType == Layer.class) {
			return true;
		}

		return false;
	}

	public boolean isVisible() {
		return true;
	}

	public MathTransform layerToMapTransform() throws IOException {
		return null;
	}

	public MathTransform mapToLayerTransform() throws IOException {
		return null;
	}

	public void refresh(Envelope bounds) {

	}

	public void removeListener(ILayerListener listener) {

	}

	public void setStatus(int status) {

	}

	public void setStatusMessage(String string) {

	}

	public int compareTo(ILayer arg0) {
		return 0;
	}

	@Override
	public <T> IGeoResource findGeoResource(Class<T> clazz) {
		return null;
	}

	@Override
	public <T> boolean hasResource(Class<T> resourceType) {
		return false;
	}

	@Override
	public IBlackboard getBlackboard() {
		return null;
	}

	@Override
	public boolean getInteraction(Interaction interaction) {
		return false;
	}

	@Override
	public boolean isShown() {
		return false;
	}

	@Override
	public Filter getFilter() {
		return null;
	}

	@Override
	public Filter createBBoxFilter(Envelope boundingBox, IProgressMonitor monitor) {
		return null;
	}

}
