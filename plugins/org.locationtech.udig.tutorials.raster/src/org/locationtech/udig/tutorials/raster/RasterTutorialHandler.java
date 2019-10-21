/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tutorials.raster;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.coverage.processing.CoverageProcessor;
import org.geotools.util.factory.Hints;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class RasterTutorialHandler extends AbstractHandler {

    public Object execute( ExecutionEvent event ) throws ExecutionException {
        IWorkbench workbench = PlatformUI.getWorkbench();
        
        Shell shell = workbench.getActiveWorkbenchWindow().getShell();
        FileDialog dialog = new FileDialog( shell, SWT.OPEN );
        dialog.setFilterExtensions(new String[]{"*.jpeg;*.jpg","*.png"});
        String filename = dialog.open();
        if( filename == null ) {
            return null;
        }
        File file = new File( filename );
        
        System.out.println( file );
        try {
            example( file );
        }
        catch( Throwable t ){
            throw new ExecutionException("Example Failed", t );
        }
        return null;
    }

    public static void example( File file ) throws Exception {
        URL url = file.toURL();
        BufferedImage image = ImageIO.read(url);
        
        //coordinates of our raster image, in lat/lon
        double minx = -92.36918018580701;
        double miny = -49.043520894708884;
        
        double maxx = -42.25153935511384;
        double maxy = 2.1002762835725868;
        
        CoordinateReferenceSystem crs = CRS.decode("EPSG:4326");
        ReferencedEnvelope envelope = new ReferencedEnvelope(minx,maxx,miny,maxy,crs);
        
        String name = "GridCoverage";
        
        GridCoverageFactory factory = new GridCoverageFactory();        
        GridCoverage2D gridCoverage = (GridCoverage2D) factory.create(name,image,envelope);
        
        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:24882");

        RenderingHints hints = new RenderingHints(Hints.LENIENT_DATUM_SHIFT, Boolean.TRUE);        
        CoverageProcessor processor = new CoverageProcessor(hints);
                
        ParameterValueGroup param = processor.getOperation("Resample").getParameters();
        param.parameter("Source").setValue( gridCoverage );
        param.parameter("CoordinateReferenceSystem").setValue(targetCRS);
        param.parameter("InterpolationType").setValue("NearestNeighbor");
        
        GridCoverage2D reprojected = (GridCoverage2D) processor.doOperation(param);
        
        ImageViewer.show(gridCoverage, "Normal Grid Coverage");
        ImageViewer.show(reprojected, "Reprojected Grid Coverage");
    }
}
