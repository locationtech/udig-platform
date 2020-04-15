/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.wizard.export.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.render.IViewportModel;
import org.opengis.coverage.grid.GridCoverage;

/**
 * A strategy for exporting to Geotiff.
 * 
 * @author jesse
 * @since 1.1.0
 */
public class GeotiffImageExportFormat extends ImageExportFormat {

    @Override
    public String getExtension() {
        return "tif"; //$NON-NLS-1$
    }

    @Override
    public String getName() {
        return "Geotiff";
    }

    @Override
    public void createControl( Composite parent ) {
        setControl(new Composite(parent, SWT.NONE));
    }
    
    @Override
    public void write( IMap map, BufferedImage image, File destination ) throws IOException {
        GeoTiffWriter writer = new GeoTiffWriter(destination);
        GridCoverage coverage = convertToGridCoverage(map.getViewportModel(), image);
        writer.write(coverage, null);
    }

    private GridCoverage convertToGridCoverage( IViewportModel viewportModel, BufferedImage image ) {
        ReferencedEnvelope env = viewportModel.getBounds();

        GridCoverageFactory factory = new GridCoverageFactory();

        GridCoverage2D gc = (GridCoverage2D) factory.create("GridCoverage", image, env); //$NON-NLS-1$
        return gc;
    }


}
