/**
 *
 */
package org.locationtech.udig.info.tests;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.lang.reflect.InvocationTargetException;
import java.util.SortedSet;

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
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.render.IViewportModelListener;
import org.locationtech.udig.project.render.displayAdapter.MapDisplayEvent;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

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

	@Override
    public double getAspectRatio() {
		return 0;
	}

	@Override
    public ReferencedEnvelope getBounds() {
		return this.bbox;
	}

	@Override
    public CoordinateReferenceSystem getCRS() {
		return this.crs;
	}

	@Override
    public Coordinate getCenter() {
		return null;
	}

	@Override
    public double getHeight() {
		return 0;
	}

	@Override
    public Map getMapInternal() {
		return null;
	}

	@Override
    public Coordinate getPixelSize() {
		return null;
	}

	@Override
    public RenderManager getRenderManagerInternal() {
		return null;
	}

	@Override
    public double getWidth() {
		return 0;
	}

	@Override
    public boolean isInitialized() {
		return false;
	}

	@Override
    public boolean isSetCRS() {
		return false;
	}

	@Override
    public ViewportModel panUsingScreenCoords(int xpixels, int ypixels) {
		return null;
	}

	@Override
    public ViewportModel panUsingWorldCoords(double x, double y) {
		return null;
	}

	@Override
    public Coordinate pixelToWorld(int x, int y) {
		return null;
	}

	@Override
    public void setBounds(Envelope value) {

	}

	@Override
    public void setBounds(double minx, double maxx, double miny, double maxy) throws IllegalArgumentException {

	}

	@Override
    public void setCRS(CoordinateReferenceSystem value) {

	}

	@Override
    public void setCenter(Coordinate value) {

	}

	@Override
    public void setHeight(double value) {

	}

	@Override
    public void setInitialized(boolean initialized) {

	}

	@Override
    public void setMapInternal(Map value) {

	}

	@Override
    public void setRenderManagerInternal(RenderManager value) {

	}

	@Override
    public void setScale(double scaleDenominator) {

	}

	@Override
    public void setWidth(double value) {

	}

	@Override
    public void unsetCRS() {

	}

	@Override
    public Point worldToPixel(Coordinate coord) {
		Point2D w = new Point2D.Double(coord.x, coord.y);
		AffineTransform at = worldToScreenTransform();
		Point2D p = at.transform(w, new Point2D.Double());
		return new Point((int) p.getX(), (int) p.getY());
	}

	@Override
    public AffineTransform worldToScreenTransform() {
		return worldToScreenTransform(getBounds(), this.displaySize);
	}

	@Override
    public ViewportModel zoom(double zoom) {
		return null;
	}

	@Override
    public void zoomToBox(Envelope box) {

	}

	@Override
    public void zoomToExtent() {

	}

	@Override
    public TreeIterator eAllContents() {
		return null;
	}

	@Override
    public EClass eClass() {
		return null;
	}

	@Override
    public EObject eContainer() {
		return null;
	}

	@Override
    public EStructuralFeature eContainingFeature() {
		return null;
	}

	@Override
    public EReference eContainmentFeature() {
		return null;
	}

	@Override
    public EList eContents() {
		return null;
	}

	@Override
    public EList eCrossReferences() {
		return null;
	}

	@Override
    public Object eGet(EStructuralFeature arg0) {
		return null;
	}

	@Override
    public Object eGet(EStructuralFeature arg0, boolean arg1) {
		return null;
	}

	@Override
    public boolean eIsProxy() {
		return false;
	}

	@Override
    public boolean eIsSet(EStructuralFeature arg0) {
		return false;
	}

	@Override
    public Resource eResource() {
		return null;
	}

	@Override
    public void eSet(EStructuralFeature arg0, Object arg1) {

	}

	@Override
    public void eUnset(EStructuralFeature arg0) {

	}

	@Override
    public EList eAdapters() {
		return null;
	}

	@Override
    public boolean eDeliver() {
		return false;
	}

	@Override
    public void eNotify(Notification arg0) {

	}

	@Override
    public void eSetDeliver(boolean arg0) {

	}

	@Override
    public void sizeChanged(MapDisplayEvent event) {

	}

	@Override
    public void addViewportModelListener(IViewportModelListener listener) {

	}

	@Override
    public IMap getMap() {
		return null;
	}

	@Override
    public double getScaleDenominator() {
		return 0;
	}

	@Override
    public void removeViewportModelListener(IViewportModelListener listener) {

	}

	@Override
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

}
