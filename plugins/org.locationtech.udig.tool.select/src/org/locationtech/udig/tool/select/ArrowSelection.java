/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tool.select;

import java.awt.Point;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.tool.AbstractModalTool;
import org.locationtech.udig.project.ui.tool.ModalTool;
import org.locationtech.udig.tool.select.internal.Messages;
import org.locationtech.udig.ui.PlatformGIS;
import org.opengis.feature.simple.SimpleFeature;

/**
 * Selects and drags single features.
 * 
 * @deprecated {@link ArrowSelectionWithPopup} should be used instead
 * 
 * @author jones
 * @since 1.0.0
 */
public class ArrowSelection extends AbstractModalTool implements ModalTool {

    private int x;
    private int y;

    public ArrowSelection(){
        super(DRAG_DROP|MOUSE);
    }

    @Override
    public void mousePressed( MapMouseEvent e ) {
        x=e.x;
        y=e.y;
    }
    
    @Override
    public void mouseReleased( final MapMouseEvent e ) {
        if (e.x == x && e.y == y) {
            PlatformGIS.run(new IRunnableWithProgress() {

                @SuppressWarnings("unchecked")
                public void run(IProgressMonitor monitor) throws InvocationTargetException,
                        InterruptedException {
                    monitor.beginTask(Messages.ArrowSelection_0, 5);
                    ReferencedEnvelope bbox = getContext().getBoundingBox(new Point(x, y), 5);
                    SimpleFeatureCollection collection = null;
                    FeatureIterator<SimpleFeature> iter = null;
                    try {
                        ILayer selectedLayer = getContext().getSelectedLayer();
                        SimpleFeatureSource source = selectedLayer.getResource(
                                SimpleFeatureSource.class, new SubProgressMonitor(monitor, 1));
                        if (source == null)
                            return;
                        collection = source.getFeatures(selectedLayer.createBBoxFilter(bbox,
                                new SubProgressMonitor(monitor, 1)));
                        iter = collection.features();
                        if (!iter.hasNext()) {
                            if (!e.buttonsDown()) {
                                getContext().sendASyncCommand(
                                        getContext().getEditFactory()
                                                .createNullEditFeatureCommand());
                            }
                            getContext().sendASyncCommand(
                                    getContext().getSelectionFactory().createNoSelectCommand());
                            return;
                        }
                        SimpleFeature feature = iter.next();
                        getContext().sendASyncCommand(
                                getContext().getEditFactory().createSetEditFeatureCommand(feature,
                                        selectedLayer));
                        getContext().sendASyncCommand(
                                getContext().getSelectionFactory().createFIDSelectCommand(
                                        selectedLayer, feature));
                    } catch (IOException e) {

                        // return;
                    } finally {
                        monitor.done();
                        if (iter != null)
                            iter.close();
                    }
                }

            });
        }
    }
}
