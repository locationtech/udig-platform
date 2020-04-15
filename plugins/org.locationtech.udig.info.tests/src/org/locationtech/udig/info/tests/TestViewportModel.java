/**
 * 
 */
package org.locationtech.udig.info.tests;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.SortedSet;

import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.render.IViewportModelListener;
import org.locationtech.udig.project.render.displayAdapter.MapDisplayEvent;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.joda.time.DateTime;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

public class TestViewportModel implements ViewportModel {

	private Dimension displaySize;
	private ReferencedEnvelope bbox;
	private CoordinateReferenceSystem crs;

	TestViewportModel() {
	}
	
	

	/**
	 * @param displaySize
	 * @param bbox
	 * @param crs
	 */
	public TestViewportModel(Dimension displaySize, ReferencedEnvelope bbox, CoordinateReferenceSystem crs) {
		super();
		this.displaySize = displaySize;
		this.bbox = bbox;
		this.crs = crs;
	}



	public double getAspectRatio() {
		// TODO Auto-generated method stub
		return 0;
	}

	public ReferencedEnvelope getBounds() {
		return this.bbox;
	}

	public CoordinateReferenceSystem getCRS() {
		return this.crs;
	}

	public Coordinate getCenter() {
		// TODO Auto-generated method stub
		return null;
	}

	public double getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Map getMapInternal() {
		// TODO Auto-generated method stub
		return null;
	}

	public Coordinate getPixelSize() {
		// TODO Auto-generated method stub
		return null;
	}

	public RenderManager getRenderManagerInternal() {
		// TODO Auto-generated method stub
		return null;
	}

	public double getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isInitialized() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSetCRS() {
		// TODO Auto-generated method stub
		return false;
	}

	public ViewportModel panUsingScreenCoords(int xpixels, int ypixels) {
		// TODO Auto-generated method stub
		return null;
	}

	public ViewportModel panUsingWorldCoords(double x, double y) {
		// TODO Auto-generated method stub
		return null;
	}

	public Coordinate pixelToWorld(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setBounds(Envelope value) {
		// TODO Auto-generated method stub
		
	}

	public void setBounds(double minx, double maxx, double miny, double maxy) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	public void setCRS(CoordinateReferenceSystem value) {
		// TODO Auto-generated method stub
		
	}

	public void setCenter(Coordinate value) {
		// TODO Auto-generated method stub
		
	}

	public void setHeight(double value) {
		// TODO Auto-generated method stub
		
	}

	public void setInitialized(boolean initialized) {
		// TODO Auto-generated method stub
		
	}

	public void setMapInternal(Map value) {
		// TODO Auto-generated method stub
		
	}

	public void setRenderManagerInternal(RenderManager value) {
		// TODO Auto-generated method stub
		
	}

	public void setScale(double scaleDenominator) {
		// TODO Auto-generated method stub
		
	}

	public void setWidth(double value) {
		// TODO Auto-generated method stub
		
	}

	public void unsetCRS() {
		// TODO Auto-generated method stub
		
	}

	public Point worldToPixel(Coordinate coord) {
        Point2D w = new Point2D.Double(coord.x, coord.y);
        AffineTransform at = worldToScreenTransform();
        Point2D p = at.transform(w, new Point2D.Double());
        return new Point((int) p.getX(), (int) p.getY());
	}

	public AffineTransform worldToScreenTransform() {
		return worldToScreenTransform(getBounds(), this.displaySize);
	}

	public ViewportModel zoom(double zoom) {
		// TODO Auto-generated method stub
		return null;
	}

	public void zoomToBox(Envelope box) {
		// TODO Auto-generated method stub
		
	}

	public void zoomToExtent() {
		// TODO Auto-generated method stub
		
	}

	public TreeIterator eAllContents() {
		// TODO Auto-generated method stub
		return null;
	}

	public EClass eClass() {
		// TODO Auto-generated method stub
		return null;
	}

	public EObject eContainer() {
		// TODO Auto-generated method stub
		return null;
	}

	public EStructuralFeature eContainingFeature() {
		// TODO Auto-generated method stub
		return null;
	}

	public EReference eContainmentFeature() {
		// TODO Auto-generated method stub
		return null;
	}

	public EList eContents() {
		// TODO Auto-generated method stub
		return null;
	}

	public EList eCrossReferences() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object eGet(EStructuralFeature arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object eGet(EStructuralFeature arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean eIsProxy() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean eIsSet(EStructuralFeature arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public Resource eResource() {
		// TODO Auto-generated method stub
		return null;
	}

	public void eSet(EStructuralFeature arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	public void eUnset(EStructuralFeature arg0) {
		// TODO Auto-generated method stub
		
	}

	public EList eAdapters() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean eDeliver() {
		// TODO Auto-generated method stub
		return false;
	}

	public void eNotify(Notification arg0) {
		// TODO Auto-generated method stub
		
	}

	public void eSetDeliver(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	public void sizeChanged(MapDisplayEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void addViewportModelListener(IViewportModelListener listener) {
		// TODO Auto-generated method stub
		
	}

	public IMap getMap() {
		// TODO Auto-generated method stub
		return null;
	}

	public double getScaleDenominator() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void removeViewportModelListener(IViewportModelListener listener) {
		// TODO Auto-generated method stub
		
	}

	public AffineTransform worldToScreenTransform(Envelope mapExtent, Dimension displaySize) {
		double scaleX = displaySize.getWidth() / mapExtent.getWidth();
        double scaleY = displaySize.getHeight() / mapExtent.getHeight();

        double tx = -mapExtent.getMinX() * scaleX;
        double ty = (mapExtent.getMinY() * scaleY) + displaySize.getHeight();

        AffineTransform at = new AffineTransform(scaleX, 0.0d, 0.0d, -scaleY, tx, ty);

        return at;
	}



	@Override
	public Object eInvoke(EOperation operation, EList<?> arguments)
			throws InvocationTargetException {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public SortedSet<Double> getDefaultPreferredScaleDenominators() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public boolean isBoundsChanging() {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public SortedSet<Double> getPreferredScaleDenominators() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void setPreferredScaleDenominators(SortedSet<Double> value) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void setBounds(ReferencedEnvelope value) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void setBounds(ReferencedEnvelope value, boolean forceContainBBoxZoom) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public ViewportModel zoom(double zoom, Coordinate fixedPoint) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void setScale(double scaleDenominator, int dpi, int displayWidth,
			int displayHeight) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void setIsBoundsChanging(boolean changing) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public List<DateTime> getAvailableTimesteps() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public DateTime getCurrentTimestep() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void setCurrentTimestep(DateTime value) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public List<Double> getAvailableElevation() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Double getCurrentElevation() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void setCurrentElevation(Double value) {
		// TODO Auto-generated method stub
		
	}
	
}
