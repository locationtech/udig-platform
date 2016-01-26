/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.worldimage;

import static org.eclipse.jface.dialogs.MessageDialog.QUESTION;
import static org.locationtech.udig.catalog.worldimage.internal.Messages.InMemoryCoverageLoader_close_button;
import static org.locationtech.udig.catalog.worldimage.internal.Messages.InMemoryCoverageLoader_message;
import static org.locationtech.udig.catalog.worldimage.internal.Messages.InMemoryCoverageLoader_msgTitle;
import static org.locationtech.udig.catalog.worldimage.internal.Messages.InMemoryCoverageLoader_restart_button;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.text.MessageFormat;
import java.util.Hashtable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.geotools.coverage.grid.GeneralGridGeometry;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.udig.catalog.rasterings.AbstractRasterGeoResource;
import org.locationtech.udig.catalog.rasterings.GridCoverageLoader;
import org.locationtech.udig.catalog.rasterings.RasteringsPlugin;
import org.locationtech.udig.internal.ui.UiPlugin;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.geometry.Envelope;

/**
 * Keeps the full coverage in memory and returns the same instance
 * 
 * @author jeichar
 * @since 1.1.0
 */
public class InMemoryCoverageLoader extends GridCoverageLoader {

    private static final GridCoverage EMPTY_COVERAGE;
    static {

        Envelope envelope = new ReferencedEnvelope(0, .00001, 0, 0.00001, DefaultGeographicCRS.WGS84);
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D createGraphics = image.createGraphics();
        createGraphics.drawRect(0, 0, 1, 1);
        createGraphics.dispose();
        EMPTY_COVERAGE = new GridCoverageFactory().create(
                "placeholder", image, envelope); //$NON-NLS-1$

    }
    private volatile SoftReference<GridCoverage> coverage = new SoftReference<GridCoverage>(null);
    private String fileName;

    public InMemoryCoverageLoader( AbstractRasterGeoResource resource, String fileName )
            throws IOException {
        super(resource);
        this.fileName = fileName;
    }

    @Override
    public synchronized GridCoverage load( GeneralGridGeometry geom, IProgressMonitor monitor )
            throws IOException {

        if (coverage.get() == null) {
            try {
                AbstractGridCoverage2DReader reader = resource.resolve(AbstractGridCoverage2DReader.class, monitor);

                GridEnvelope range = reader.getOriginalGridRange();
                GeneralEnvelope env = reader.getOriginalEnvelope();
                GridGeometry2D all = new GridGeometry2D(range, env);
                GridCoverage2D coverage2d = (GridCoverage2D) super.load(all, monitor);
                RenderedImage image = coverage2d.getRenderedImage();

				RasteringsPlugin
						.log("WARNING.  Loading image fully into memory.  It is about " + size(image) + " MB in size decompressed", null); //$NON-NLS-1$//$NON-NLS-2$

                @SuppressWarnings("rawtypes")
                BufferedImage bi = new BufferedImage(image.getColorModel(), (WritableRaster) image
                        .getData(), false, new Hashtable());
                GridCoverageFactory fac = new GridCoverageFactory();

                GridCoverage2D c = fac.create(fileName, bi, env);
                coverage = new SoftReference<GridCoverage>(c);

            } catch (OutOfMemoryError e) {
                updateMemoryLevel();
            } catch (Exception t) {
                updateMemoryLevel();
            }
        }
        return coverage.get();
    }

    private void updateMemoryLevel() throws IOException {

        int heap;
        int originalHeap = UiPlugin.getMaxHeapSize();
        if (originalHeap < 700) {
            heap = 1024;
        } else if( originalHeap < 1500) {
            heap = 2048;
        } else {
            heap = originalHeap * 2;
        }
        
        String os = Platform.getOS();
        if (heap > 1024 && os == Platform.OS_WIN32) {
            heap = 1024;
        }
        
        final int finalHeap = heap;
        coverage = new SoftReference<GridCoverage>(EMPTY_COVERAGE);

        Display.getDefault().asyncExec(new Runnable(){

            public void run() {

                
                Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                String title = InMemoryCoverageLoader_msgTitle;
                String desc = MessageFormat.format(InMemoryCoverageLoader_message, resource
                        .getIdentifier(), finalHeap);
                String[] buttons = {InMemoryCoverageLoader_restart_button,
                        InMemoryCoverageLoader_close_button};
                MessageDialog dialog = new MessageDialog(shell, title, null, desc, QUESTION,
                        buttons, 0){

                    @Override
                    protected void buttonPressed( int buttonId ) {
                        if (buttonId == 0) {
                            try {
                                UiPlugin.setMaxHeapSize(String.valueOf(finalHeap));
                                PlatformUI.getWorkbench().restart();

                            } catch (IOException e) {
                                throw (RuntimeException) new RuntimeException().initCause(e);
                            }
                        }
                        super.buttonPressed(buttonId);
                    }
                };
                dialog.open();

            }
        });
    }

    private double size( RenderedImage bi ) {

    	double bitPerPixel = 0;
        for( int elem : bi.getColorModel().getComponentSize() ) {
            bitPerPixel += elem;
        }

        double width = (double)bi.getWidth();
		double height = (double)bi.getHeight();
		double pixelNum = width*height;
		double bitsNum = pixelNum*bitPerPixel;
		double bytesNum = bitsNum/8.0;
		double megNum = bytesNum/1024.0;
		return megNum;
    }

}
