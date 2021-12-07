/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tutorials.examples;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.IMapCompositionListener;
import org.locationtech.udig.project.IMapListener;
import org.locationtech.udig.project.MapCompositionEvent;
import org.locationtech.udig.project.MapEvent;
import org.locationtech.udig.project.render.RenderException;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.ApplicationGIS.DrawMapParameter;
import org.locationtech.udig.project.ui.BoundsStrategy;
import org.locationtech.udig.project.ui.SelectionStyle;
import org.locationtech.udig.ui.graphics.AWTSWTImageUtils;

/**
 * Canvas that uses ApplicationGIS to draw a Map onto an background image (use MapViewer).
 * <p>
 * This is an example only - please use MapViewer for a real world application; this code uses the
 * same facilities we use for exporting images or printing maps to paper - it is not suitable for
 * dynamically rendered content.
 * <p>
 * This is a *static* map only - it has no interaction with events and cannot notice when the map is
 * changed.
 * <p>
 *
 * @author Jody
 * @since 1.1.0
 */
public class StaticGISWidget extends Canvas {
    /**
     * Delay to use when scheduling the Map to redraw
     */
    protected static final long DELAY = 1000;

    IMap map;

    private Listener repaint = new Listener() {
        @Override
        public void handleEvent(Event e) {
            GC gc = e.gc;

            if (image != null) {
                gc.drawImage(image, 0, 0);
            }
        }
    };

    private IMapCompositionListener compositionListener = new IMapCompositionListener() {
        @Override
        public void changed(MapCompositionEvent event) {
            redrawMap.schedule(DELAY);
        }
    };

    private IMapListener mapListener = new IMapListener() {
        @Override
        public void changed(MapEvent event) {
            redrawMap.schedule(DELAY);
        }
    };

    /**
     * An SWT image to be drawn on each paint event; may be null if map has not yet been rendered
     */
    protected Image image;

    private Listener resize = new Listener() {
        @Override
        public void handleEvent(Event event) {
            redrawMap.schedule(DELAY);
        }
    };

    public StaticGISWidget(Composite parent, int style) {
        super(parent, style);
        addListener(SWT.Paint, repaint);
        addListener(SWT.Resize, resize);
    }

    /**
     * Responsible for producing a background image
     */
    Job redrawMap = new Job("Redraw Map") { //$NON-NLS-1$
        @Override
        protected IStatus run(IProgressMonitor monitor) {
            try {
                Image drawnImage = drawMap(map, monitor);
                setImage(drawnImage);
                return monitor.isCanceled() ? Status.CANCEL_STATUS : Status.OK_STATUS;
            } catch (Throwable t) {
                IStatus error = new Status(Status.ERROR, Activator.PLUGIN_ID,
                        t.getLocalizedMessage());
                t.printStackTrace();
                return error;
            }
        }
    };

    public void setMap(IMap aMap) {
        if (map == aMap)
            return;
        if (map != null) {
            map.removeMapCompositionListener(compositionListener);
            map.removeMapListener(mapListener);
        }
        this.map = aMap;
        if (map != null) {
            map.addMapCompositionListener(compositionListener);
            map.addMapListener(mapListener);
        }
        redrawMap.schedule();
    }

    /**
     * Method used by the redrawMap job when the image is complete.
     *
     * @param drawn
     */
    protected synchronized void setImage(Image drawn) {
        if (image != null) {
            image.dispose();
            image = null;
        }
        image = drawn;
        getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                redraw();
            }
        });
    }

    protected Image drawMap(IMap map2, IProgressMonitor monitor) throws RenderException {

        final Image create[] = new Image[1];
        final int dpi[] = new int[2];
        final Rectangle screen[] = new Rectangle[1];
        getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
                screen[0] = getBounds();
                Point resolution = getDisplay().getDPI();
                dpi[0] = resolution.x;
                dpi[1] = resolution.y;
            }
        });

        Rectangle size = screen[0];
        final BufferedImage buffered = AWTSWTImageUtils.createBufferedImage(size.width,
                size.height);
        buffered.getGraphics().draw3DRect(1, 1, size.width - 3, size.height - 3, false);

        if (map2 != null) {
            ReferencedEnvelope world = map2.getBounds(new NullProgressMonitor());
            DrawMapParameter toDraw = new DrawMapParameter(buffered.createGraphics(),
                    new Dimension(size.width, size.height), map2, new BoundsStrategy(world), dpi[0],
                    SelectionStyle.IGNORE, monitor, true, false);
            ApplicationGIS.drawMap(toDraw);
        }
        getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
                create[0] = AWTSWTImageUtils.convertToSWTImage(buffered);
            }
        });
        return create[0];
    }

    @Override
    public void dispose() {
        removeListener(SWT.Paint, repaint);
        super.dispose();
    }

}
