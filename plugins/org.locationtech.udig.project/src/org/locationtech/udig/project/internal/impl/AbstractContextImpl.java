/**
 * <copyright></copyright> $Id$
 */
package org.locationtech.udig.project.internal.impl;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;

import org.locationtech.udig.project.IEditManager;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.IProject;
import org.locationtech.udig.project.internal.AbstractContext;
import org.locationtech.udig.project.internal.EditManager;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.render.IRenderManager;
import org.locationtech.udig.project.render.IViewportModel;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;

import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.Decimator;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.LiteShape2;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.referencing.operation.matrix.GeneralMatrix;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.TransformException;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

/**
 * Default implementation 
 * 
 * @author Jesse
 * @since 1.0.0
 */
public abstract class AbstractContextImpl implements AbstractContext {
  
    /**
     * The cached value of the '
     * {@link #getRenderManagerInternal() <em>Render Manager Internal</em>}' reference. 
     * 
     * @see #getRenderManagerInternal()
     */
    protected RenderManager renderManagerInternal = null;

    /**
     * The cached value of the '{@link #getMapInternal() <em>Map Internal</em>}' reference. 
     * 
     * @see #getMapInternal()
     */
    protected Map mapInternal = null;

    public AbstractContextImpl( ) {
        // do nothing just so subclasses can call it
    }
    public AbstractContextImpl( AbstractContextImpl impl ) {
        setMapInternal(impl.getMapInternal());
        setRenderManagerInternal(impl.getRenderManagerInternal());
    }

    public IViewportModel getViewportModel() {
        RenderManager renderManagerInternal2 = getRenderManagerInternal();
        if (renderManagerInternal2 != null) {
            return renderManagerInternal2.getViewportModelInternal();
        }
        return getMap().getViewportModel();
    }

    public IProject getProject() {
        return getMapInternal().getProject();
    }

    public IEditManager getEditManager() {
        if( getMapInternal() != null){
            return getMapInternal().getEditManager();
        }
        return null;
    }

    public Coordinate getPixelSize() {
        return getViewportModel().getPixelSize();
    }

    public ViewportModel getViewportModelInternal() {
        return getMapInternal().getViewportModelInternal();
    }

    public EditManager getEditManagerInternal() {
        return getMapInternal().getEditManagerInternal();
    }

    public RenderManager getRenderManagerInternal() {
        return renderManagerInternal;
    }

    public void setRenderManagerInternal( RenderManager newRenderManagerInternal ) {
        renderManagerInternal = newRenderManagerInternal;
    }

    public IMapDisplay getMapDisplay() {
        if (getRenderManager() == null || getRenderManagerInternal().isDisposed() ){
            return null;
        }        
        return getRenderManager().getMapDisplay();
    }

    public IRenderManager getRenderManager() {
        return getRenderManagerInternal();
    }

    public IMap getMap() {
        return getMapInternal();
    }

    public Map getMapInternal() {
        return mapInternal;
    }

    public void setMapInternal( Map newMapInternal ) {
        mapInternal = newMapInternal;
    }

    public Project getProjectInternal() {
        return getMapInternal().getProjectInternal();
    }

    public AffineTransform worldToScreenTransform() {
        return getViewportModel().worldToScreenTransform();
    }

    public Point worldToPixel( Coordinate coord ) {
        return getViewportModel().worldToPixel(coord);
    }

    public MathTransform2D worldToScreenMathTransform() {
        GeneralMatrix matrix = new GeneralMatrix(getViewportModelInternal()
                .worldToScreenTransform());
        try {
            return (MathTransform2D) ReferencingFactoryFinder.getMathTransformFactory(null)
                    .createAffineTransform(matrix);
        } catch (Exception e) {
            return null;
        }
    }

    public Coordinate pixelToWorld( int x, int y ) {
        return getViewportModel().pixelToWorld(x, y);
    }

    public ReferencedEnvelope worldBounds( Rectangle rectangle ) {
        Envelope start = new Envelope(rectangle.x, rectangle.x + rectangle.width, rectangle.y,
                rectangle.y + rectangle.height);

        Envelope end = null;
        try {
            end = JTS.transform(start, null, worldToScreenMathTransform().inverse(), 10);
        } catch (Exception e) {
            return null;
        }
        return new ReferencedEnvelope(end, getViewportModel().getCRS());
    }

    public Envelope getPixelBoundingBox( Point screenLocation ) {
        return getBoundingBox(screenLocation, 1);
    }

    public ReferencedEnvelope getBoundingBox( Point screenLocation, int scalefactor ) {
        Coordinate center = pixelToWorld(screenLocation.x, screenLocation.y);
        Coordinate size = getPixelSize();
        double dw = (size.x / 2) * scalefactor;
        double dh = (size.y / 2) * scalefactor;
        Envelope e = new Envelope(center.x - dw, center.x + dw, center.y - dh, center.y + dh);
        return new ReferencedEnvelope(e, getCRS());
    }

    public CoordinateReferenceSystem getCRS() {
        return getViewportModel().getCRS();
    }

    public Shape toShape( ReferencedEnvelope envelope ) {
        return toShape(new GeometryFactory().toGeometry(envelope), envelope.getCoordinateReferenceSystem());
    }

    public Shape toShape( Geometry geometry, CoordinateReferenceSystem crs ) {
        try {
            MathTransform transform = CRS.findMathTransform(crs, getCRS(), true);
            MathTransformFactory factory = ReferencingFactoryFinder.getMathTransformFactory(null);
            MathTransform toScreen = factory.createAffineTransform(new GeneralMatrix(
                    worldToScreenTransform()));
            transform = factory.createConcatenatedTransform(transform, toScreen);
            return new LiteShape2(geometry, transform, new Decimator(transform, new Rectangle()), false);
        } catch (FactoryException e) {
            return null;
        } catch( TransformException e){
            return null;
        }
    }

    public Query getQuery( ILayer layer, boolean selection ) {
        return layer.getQuery(selection);
    }

    public FeatureCollection<SimpleFeatureType, SimpleFeature>  getFeaturesInBbox( ILayer layer, Envelope bbox ) throws IOException {
        return layer.getResource(FeatureSource.class, null).getFeatures(
                layer.createBBoxFilter(bbox, null));
    }

    /**
     * @see org.locationtech.udig.project.tool.ToolContext#getMapLayers()
     */
    public List<ILayer> getMapLayers() {
        return getMap().getMapLayers();
    }

    public ILayer getSelectedLayer() {
        return getEditManager().getSelectedLayer();
    }
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((mapInternal == null) ? 0 : mapInternal.hashCode());
        result = PRIME * result + ((renderManagerInternal == null) ? 0 : renderManagerInternal.hashCode());
        return result;
    }
    @Override
    public boolean equals( Object obj ) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final AbstractContextImpl other = (AbstractContextImpl) obj;
        if (mapInternal == null) {
            if (other.mapInternal != null)
                return false;
        } else if (!mapInternal.equals(other.mapInternal))
            return false;
        if (renderManagerInternal == null) {
            if (other.renderManagerInternal != null)
                return false;
        } else if (!renderManagerInternal.equals(other.renderManagerInternal))
            return false;
        return true;
    }
    
    public Point tranformCoordinate( Envelope bbox, Dimension displaySize, Coordinate coordinate ) {
        AffineTransform at = getViewportModelInternal().worldToScreenTransform(bbox, displaySize);
        Point2D w = new Point2D.Double(coordinate.x, coordinate.y);
        Point2D p = at.transform(w, new Point2D.Double());
        return new Point((int) p.getX(), (int) p.getY());
    }

} // AbstractContextImpl
