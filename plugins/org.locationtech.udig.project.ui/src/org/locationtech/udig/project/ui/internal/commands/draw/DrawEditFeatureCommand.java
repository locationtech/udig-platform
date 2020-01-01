/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui.internal.commands.draw;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.styling.Symbolizer;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;
import org.locationtech.udig.project.internal.EditManager;
import org.locationtech.udig.project.internal.ProjectPackage;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.render.IViewportModel;
import org.locationtech.udig.project.ui.commands.AbstractDrawCommand;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.locationtech.udig.ui.Drawing;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * Draws the currently edited feature on the screen.
 * 
 * @author jeichar
 * @since 0.3
 */
public class DrawEditFeatureCommand extends AbstractDrawCommand {
    // private static Symbolizer[] symbs = getSymbolizers();

    ViewportModel model;

    Drawing drawing = Drawing.create();
    Adapter editListener = new AdapterImpl(){
        /**
         * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
         */
        public void notifyChanged( Notification msg ) {
            if (msg.getFeatureID(EditManager.class) == ProjectPackage.EDIT_MANAGER__EDIT_FEATURE) {
                ((ViewportPane) model.getRenderManagerInternal().getMapDisplay()).repaint();
            }

        }
    };

    private boolean doKnots = false;

    private MathTransform mt;

    private boolean errorReported;

    /**
     * Creates a new instance of DrawFeatureCommand
     * 
     * @param model The viewportmodel that the command uses to determine how the victim should be
     *        drawn.
     */
    public DrawEditFeatureCommand( IViewportModel model ) {
        this.model = (ViewportModel) model;
    }

    /**
     * @see org.locationtech.udig.project.internal.command.MapCommand#open()
     */
    public void run( IProgressMonitor monitor ) {
        SimpleFeature feature = model.getMapInternal().getEditManager().getEditFeature();
        if (feature == null)
            return;

        @SuppressWarnings("unchecked") List<Adapter> list = model.getMapInternal().getEditManagerInternal().eAdapters(); //$NON-NLS-1$
        if (!list.contains(editListener))
            list.add(editListener);
        MathTransform mt = null;
        mt = getMathTransform();

        Symbolizer[] symbs = null;
        if (feature.getDefaultGeometry() instanceof Point
                || feature.getDefaultGeometry() instanceof MultiPoint)
            symbs = Drawing.getSymbolizers(Point.class, Color.RED);
        else
            symbs = Drawing.getSymbolizers(LineString.class, Color.RED);
        drawing.drawFeature(graphics, feature, model.worldToScreenTransform(), doKnots, symbs, mt);
    }

    /**
     *
     * @return
     */
    private MathTransform getMathTransform() {
        if( mt==null)
        try {
            mt = model.getMapInternal().getEditManagerInternal().getEditLayerInternal()
                    .layerToMapTransform();
        } catch (Exception e) {
            mt = null;
        }
        return mt;
    }

    /**
     * If doKnots is set to true the edit features will be drawn with vertex knots.
     */
    public void setDrawKnots( boolean doKnots ) {
        this.doKnots = doKnots;
    }

    /**
     * @see org.locationtech.udig.project.ui.commands.AbstractDrawCommand#setValid(boolean)
     */
    @SuppressWarnings("unchecked")//$NON-NLS-1$
    public void setValid( boolean valid ) {
        super.setValid(valid);
        if (!valid) {
            List<Adapter> adapters = model.getMapInternal().getEditManagerInternal().eAdapters();
            adapters.remove(editListener);
        }
    }

    public Rectangle getValidArea() {
        SimpleFeature feature=getMap().getEditManager().getEditFeature();
        if( feature!=null ){
            try {
                Envelope bounds = new ReferencedEnvelope(feature.getBounds())
                        .transform(getMap().getViewportModel().getCRS(), true);
                double[] points = new double[] { bounds.getMinX(),
                        bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY() };
                getMap().getViewportModel().worldToScreenTransform().transform(points, 0, points, 0, 2);
                return new Rectangle((int)points[0], (int)points[1], (int)Math.abs(points[2]-points[0]), (int)Math.abs(points[3]-points[1]));
            } catch (TransformException e) {
                if( !errorReported ){
                    errorReported = true;
                    ProjectUIPlugin.log("error calculating valid area, this will not be reported again", e);
                }
                return null;
            } catch (MismatchedDimensionException e) {
                if( !errorReported ){
                    errorReported = true;
                    ProjectUIPlugin.log("error calculating valid area, this will not be reported again", e);
                }
                return null;
            } catch (FactoryException e) {
                if( !errorReported ){
                    errorReported = true;
                    ProjectUIPlugin.log("error calculating valid area, this will not be reported again", e);
                }
                return null;
            }
        }
        return null;
    }

}
