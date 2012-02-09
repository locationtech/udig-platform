/**
 * 
 */
package net.refractions.udig.tool.info.tests;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ILayerListener;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.IStyleBlackboard;
import net.refractions.udig.project.Interaction;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.geotools.data.Query;
import org.geotools.data.ows.Layer;
import org.geotools.data.wms.WebMapServer;
import org.geotools.filter.Filter;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Envelope;

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
		// TODO Auto-generated method stub
		
	}

	public Filter createBBoxFilter(Envelope boundingBox, IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return null;
	}

	// public ReferencedEnvelope getBounds(IProgressMonitor monitor, CoordinateReferenceSystem crs) throws IOException {
	public ReferencedEnvelope getBounds(IProgressMonitor monitor, CoordinateReferenceSystem crs) {
		// TODO Auto-generated method stub
		return null;
	}

	public CoordinateReferenceSystem getCRS(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return null;
	}

	public CoordinateReferenceSystem getCRS() {
		// TODO Auto-generated method stub
		return null;
	}

	public Filter getFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	public IGeoResource getGeoResource() {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> IGeoResource getGeoResource(Class<T> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<IGeoResource> getGeoResources() {
		// TODO Auto-generated method stub
		return null;
	}

	public ImageDescriptor getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	public URL getID() {
		// TODO Auto-generated method stub
		return null;
	}

	public IMap getMap() {
		return this.map;
	}

	public String getName() {
		return "TestMap";
	}

	public IBlackboard getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	public Query getQuery(boolean selection) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
	}

	public int getStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getStatusMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	public IStyleBlackboard getStyleBlackboard() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getZorder() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isApplicable(String toolCategoryId) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
	}

	public MathTransform mapToLayerTransform() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public void refresh(Envelope bounds) {
		// TODO Auto-generated method stub
		
	}

	public void removeListener(ILayerListener listener) {
		// TODO Auto-generated method stub
		
	}

	public void setStatus(int status) {
		// TODO Auto-generated method stub
		
	}

	public void setStatusMessage(String string) {
		// TODO Auto-generated method stub
		
	}

	public int compareTo(ILayer arg0) {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public <T> IGeoResource findGeoResource(Class<T> clazz) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public <T> boolean hasResource(Class<T> resourceType) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public IBlackboard getBlackboard() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public boolean getInteraction(Interaction interaction) {
		// TODO Auto-generated method stub
		return false;
	}



    @Override
    public boolean isShown() {
        // TODO Auto-generated method stub
        return false;
    }
	
}