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
		return 0;
	}

	public ReferencedEnvelope getBounds() {
		return this.bbox;
	}

	public CoordinateReferenceSystem getCRS() {
		return this.crs;
	}

	public Coordinate getCenter() {
		return null;
	}

	public double getHeight() {
		return 0;
	}

	public Map getMapInternal() {
		return null;
	}

	public Coordinate getPixelSize() {
		return null;
	}

	public RenderManager getRenderManagerInternal() {
		return null;
	}

	public double getWidth() {
		return 0;
	}

	public boolean isInitialized() {
		return false;
	}

	public boolean isSetCRS() {
		return false;
	}

	public ViewportModel panUsingScreenCoords(int xpixels, int ypixels) {
		return null;
	}

	public ViewportModel panUsingWorldCoords(double x, double y) {
		return null;
	}

	public Coordinate pixelToWorld(int x, int y) {
		return null;
	}

	public void setBounds(Envelope value) {

	}

	public void setBounds(double minx, double maxx, double miny, double maxy) throws IllegalArgumentException {

	}

	public void setCRS(CoordinateReferenceSystem value) {

	}

	public void setCenter(Coordinate value) {

	}

	public void setHeight(double value) {

	}

	public void setInitialized(boolean initialized) {

	}

	public void setMapInternal(Map value) {

	}

	public void setRenderManagerInternal(RenderManager value) {

	}

	public void setScale(double scaleDenominator) {

	}

	public void setWidth(double value) {

	}

	public void unsetCRS() {

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
		return null;
	}

	public void zoomToBox(Envelope box) {

	}

	public void zoomToExtent() {

	}

	public TreeIterator eAllContents() {
		return null;
	}

	public EClass eClass() {
		return null;
	}

	public EObject eContainer() {
		return null;
	}

	public EStructuralFeature eContainingFeature() {
		return null;
	}

	public EReference eContainmentFeature() {
		return null;
	}

	public EList eContents() {
		return null;
	}

	public EList eCrossReferences() {
		return null;
	}

	public Object eGet(EStructuralFeature arg0) {
		return null;
	}

	public Object eGet(EStructuralFeature arg0, boolean arg1) {
		return null;
	}

	public boolean eIsProxy() {
		return false;
	}

	public boolean eIsSet(EStructuralFeature arg0) {
		return false;
	}

	public Resource eResource() {
		return null;
	}

	public void eSet(EStructuralFeature arg0, Object arg1) {

	}

	public void eUnset(EStructuralFeature arg0) {

	}

	public EList eAdapters() {
		return null;
	}

	public boolean eDeliver() {
		return false;
	}

	public void eNotify(Notification arg0) {

	}

	public void eSetDeliver(boolean arg0) {

	}

	public void sizeChanged(MapDisplayEvent event) {

	}

	public void addViewportModelListener(IViewportModelListener listener) {

	}

	public IMap getMap() {
		return null;
	}

	public double getScaleDenominator() {
		return 0;
	}

	public void removeViewportModelListener(IViewportModelListener listener) {

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
		return null;
	}

	@Override
	public SortedSet<Double> getDefaultPreferredScaleDenominators() {
		return null;
	}

	@Override
	public boolean isBoundsChanging() {
		return false;
	}

	@Override
	public SortedSet<Double> getPreferredScaleDenominators() {
		return null;
	}

	@Override
	public void setPreferredScaleDenominators(SortedSet<Double> value) {

	}

	@Override
	public void setBounds(ReferencedEnvelope value) {

	}

	@Override
	public void setBounds(ReferencedEnvelope value, boolean forceContainBBoxZoom) {

	}

	@Override
	public ViewportModel zoom(double zoom, Coordinate fixedPoint) {
		return null;
	}

	@Override
	public void setScale(double scaleDenominator, int dpi, int displayWidth,
			int displayHeight) {

	}

	@Override
	public void setIsBoundsChanging(boolean changing) {

	}

	@Override
	public List<DateTime> getAvailableTimesteps() {
		return null;
	}

	@Override
	public DateTime getCurrentTimestep() {
		return null;
	}

	@Override
	public void setCurrentTimestep(DateTime value) {

	}

	@Override
	public List<Double> getAvailableElevation() {
		return null;
	}

	@Override
	public Double getCurrentElevation() {
		return null;
	}

	@Override
	public void setCurrentElevation(Double value) {

	}

}
