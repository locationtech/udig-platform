/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2022, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
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
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.TransformException;

/**
 * Default implementation
 *
 * @author Jesse
 * @since 1.0.0
 */
public abstract class AbstractContextImpl implements AbstractContext {

    /**
     * The cached value of the ' {@link #getRenderManagerInternal() <em>Render Manager
     * Internal</em>}' reference.
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

    public AbstractContextImpl() {
        // do nothing just so subclasses can call it
    }

    public AbstractContextImpl(AbstractContextImpl impl) {
        setMapInternal(impl.getMapInternal());
        setRenderManagerInternal(impl.getRenderManagerInternal());
    }

    @Override
    public IViewportModel getViewportModel() {
        RenderManager renderManagerInternal2 = getRenderManagerInternal();
        if (renderManagerInternal2 != null) {
            return renderManagerInternal2.getViewportModelInternal();
        }
        return getMap().getViewportModel();
    }

    @Override
    public IProject getProject() {
        return getMapInternal().getProject();
    }

    @Override
    public IEditManager getEditManager() {
        if (getMapInternal() != null) {
            return getMapInternal().getEditManager();
        }
        return null;
    }

    @Override
    public Coordinate getPixelSize() {
        return getViewportModel().getPixelSize();
    }

    @Override
    public ViewportModel getViewportModelInternal() {
        return getMapInternal().getViewportModelInternal();
    }

    @Override
    public EditManager getEditManagerInternal() {
        return getMapInternal().getEditManagerInternal();
    }

    @Override
    public RenderManager getRenderManagerInternal() {
        return renderManagerInternal;
    }

    @Override
    public void setRenderManagerInternal(RenderManager newRenderManagerInternal) {
        renderManagerInternal = newRenderManagerInternal;
    }

    @Override
    public IMapDisplay getMapDisplay() {
        if (getRenderManager() == null || getRenderManagerInternal().isDisposed()) {
            return null;
        }
        return getRenderManager().getMapDisplay();
    }

    @Override
    public IRenderManager getRenderManager() {
        return getRenderManagerInternal();
    }

    @Override
    public IMap getMap() {
        return getMapInternal();
    }

    @Override
    public Map getMapInternal() {
        return mapInternal;
    }

    @Override
    public void setMapInternal(Map newMapInternal) {
        mapInternal = newMapInternal;
    }

    @Override
    public Project getProjectInternal() {
        return getMapInternal().getProjectInternal();
    }

    @Override
    public AffineTransform worldToScreenTransform() {
        return getViewportModel().worldToScreenTransform();
    }

    @Override
    public Point worldToPixel(Coordinate coord) {
        return getViewportModel().worldToPixel(coord);
    }

    @Override
    public MathTransform2D worldToScreenMathTransform() {
        GeneralMatrix matrix = new GeneralMatrix(
                getViewportModelInternal().worldToScreenTransform());
        try {
            return (MathTransform2D) ReferencingFactoryFinder.getMathTransformFactory(null)
                    .createAffineTransform(matrix);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Coordinate pixelToWorld(int x, int y) {
        return getViewportModel().pixelToWorld(x, y);
    }

    @Override
    public ReferencedEnvelope worldBounds(Rectangle rectangle) {
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

    @Override
    public Envelope getPixelBoundingBox(Point screenLocation) {
        return getBoundingBox(screenLocation, 1);
    }

    @Override
    public ReferencedEnvelope getBoundingBox(Point screenLocation, int scalefactor) {
        Coordinate center = pixelToWorld(screenLocation.x, screenLocation.y);
        Coordinate size = getPixelSize();
        double dw = (size.x / 2) * scalefactor;
        double dh = (size.y / 2) * scalefactor;
        Envelope e = new Envelope(center.x - dw, center.x + dw, center.y - dh, center.y + dh);
        return new ReferencedEnvelope(e, getCRS());
    }

    @Override
    public CoordinateReferenceSystem getCRS() {
        return getViewportModel().getCRS();
    }

    @Override
    public Shape toShape(ReferencedEnvelope envelope) {
        return toShape(new GeometryFactory().toGeometry(envelope),
                envelope.getCoordinateReferenceSystem());
    }

    @Override
    public Shape toShape(Geometry geometry, CoordinateReferenceSystem crs) {
        try {
            MathTransform transform = CRS.findMathTransform(crs, getCRS(), true);
            MathTransformFactory factory = ReferencingFactoryFinder.getMathTransformFactory(null);
            MathTransform toScreen = factory
                    .createAffineTransform(new GeneralMatrix(worldToScreenTransform()));
            transform = factory.createConcatenatedTransform(transform, toScreen);
            return new LiteShape2(geometry, transform, new Decimator(transform, new Rectangle()),
                    false);
        } catch (FactoryException e) {
            return null;
        } catch (TransformException e) {
            return null;
        }
    }

    public Query getQuery(ILayer layer, boolean selection) {
        return layer.getQuery(selection);
    }

    @Override
    public FeatureCollection<SimpleFeatureType, SimpleFeature> getFeaturesInBbox(ILayer layer,
            Envelope bbox) throws IOException {
        return layer.getResource(FeatureSource.class, null)
                .getFeatures(layer.createBBoxFilter(bbox, null));
    }

    @Override
    public List<ILayer> getMapLayers() {
        return getMap().getMapLayers();
    }

    @Override
    public ILayer getSelectedLayer() {
        return getEditManager().getSelectedLayer();
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((mapInternal == null) ? 0 : mapInternal.hashCode());
        result = PRIME * result
                + ((renderManagerInternal == null) ? 0 : renderManagerInternal.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractContextImpl other = (AbstractContextImpl) obj;
        if (mapInternal == null) {
            if (other.mapInternal != null) {
                return false;
            }
        } else if (!mapInternal.equals(other.mapInternal)) {
            return false;
        }
        if (renderManagerInternal == null) {
            if (other.renderManagerInternal != null) {
                return false;
            }
        } else if (!renderManagerInternal.equals(other.renderManagerInternal)) {
            return false;
        }
        return true;
    }

    @Override
    public Point tranformCoordinate(Envelope bbox, Dimension displaySize, Coordinate coordinate) {
        AffineTransform at = getViewportModelInternal().worldToScreenTransform(bbox, displaySize);
        Point2D w = new Point2D.Double(coordinate.x, coordinate.y);
        Point2D p = at.transform(w, new Point2D.Double());
        return new Point((int) p.getX(), (int) p.getY());
    }

} // AbstractContextImpl
