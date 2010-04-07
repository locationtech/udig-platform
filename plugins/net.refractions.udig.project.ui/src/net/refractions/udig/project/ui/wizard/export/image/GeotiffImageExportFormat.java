/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.project.ui.wizard.export.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.render.IViewportModel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.geometry.jts.ReferencedEnvelope;
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
