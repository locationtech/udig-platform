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

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.locationtech.udig.project.IMap;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 * An export strategy for exporting an image in World+Image format.
 * 
 * @author jesse
 * @since 1.1.0
 */
public class WorldImageExportFormat extends ImageExportFormat{

    private String formatName;
    private String formatExtension;

    public WorldImageExportFormat( String formatName, String formatExtension ) {
        this.formatName = formatName;
        this.formatExtension = formatExtension;
    }
    
    public String getName() {
        return formatName;
    }
    
    public String getExtension() {
        return formatExtension;
    }

    @Override
    public void createControl( Composite parent ) {
        setControl(new Composite(parent, SWT.NONE));
    }
    
    @Override
    public void write( IMap map, BufferedImage image, File destination ) throws IOException {

        ImageIO.write(image, getName(), destination);

        String baseFile = destination.getPath().substring(0, destination.getPath().lastIndexOf(".")); //$NON-NLS-1$
        try {
            createWorldFile(map.getViewportModel().worldToScreenTransform().createInverse(), baseFile);
        } catch (TransformException e) {
            throw (IOException)new IOException(e.getMessage()).initCause(e);
        } catch (NoninvertibleTransformException e) {
            throw (IOException)new IOException(e.getMessage()).initCause(e);
        }
        createProjectionFile(baseFile, map.getViewportModel().getCRS());
    }
    

    /**
     * This method is responsible for creating a projection file using the WKT
     * representation of this coverage's coordinate reference system. We can
     * reuse this file in order to rebuild later the crs.
     * 
     * 
     * @param baseFile
     * @param coordinateReferenceSystem
     * @throws IOException
     */
    private void createProjectionFile(final String baseFile,
            final CoordinateReferenceSystem coordinateReferenceSystem)
            throws IOException {
        try {
            final File prjFile = new File(new StringBuffer(baseFile).append(".prj") //$NON-NLS-1$
                    .toString());
            BufferedWriter out = new BufferedWriter(new FileWriter(prjFile));
            out.write(coordinateReferenceSystem.toWKT());
            out.close();
        }
        catch( Throwable ignore ){
            // projection cannot be represented in WKT
        }
    }

    /**
     * This method is responsible fro creating a world file to georeference an
     * image given the image bounding box and the image geometry. The name of
     * the file is composed by the name of the image file with a proper
     * extension, depending on the format (see WorldImageFormat). The projection
     * is in the world file.
     * 
     * @param gridToWorld
     *            the transformation from the image to the world coordinates.
     * @param baseFile
     *            Basename and path for this image.
     * @throws IOException
     *             In case we cannot create the world file.
     * @throws TransformException
     * @throws TransformException
     */
    private void createWorldFile(final AffineTransform gridToWorld, 
            final String baseFile) throws IOException, TransformException {
        // /////////////////////////////////////////////////////////////////////
        //
        // CRS information
        //
        // ////////////////////////////////////////////////////////////////////
        final boolean lonFirst = (XAffineTransform.getSwapXY(gridToWorld) != -1);

        // /////////////////////////////////////////////////////////////////////
        //
        // World File values
        // It is worthwhile to note that we have to keep into account the fact
        // that the axis could be swapped (LAT,lon) therefore when getting
        // xPixSize and yPixSize we need to look for it a the right place
        // inside the grid to world transform.
        //
        // ////////////////////////////////////////////////////////////////////
        final double xPixelSize = (lonFirst) ? gridToWorld.getScaleX()
                : gridToWorld.getShearY();
        final double rotation1 = (lonFirst) ? gridToWorld.getShearX()
                : gridToWorld.getScaleX();
        final double rotation2 = (lonFirst) ? gridToWorld.getShearY()
                : gridToWorld.getScaleY();
        final double yPixelSize = (lonFirst) ? gridToWorld.getScaleY()
                : gridToWorld.getShearX();
        final double xLoc = gridToWorld.getTranslateX();
        final double yLoc = gridToWorld.getTranslateY();

        // /////////////////////////////////////////////////////////////////////
        //
        // writing world file
        //
        // ////////////////////////////////////////////////////////////////////
        final StringBuffer buff = new StringBuffer(baseFile);
        buff.append(".wld"); //$NON-NLS-1$
        final File worldFile = new File(buff.toString());
        final PrintWriter out = new PrintWriter(new FileOutputStream(worldFile));
        out.println(xPixelSize);
        out.println(rotation1);
        out.println(rotation2);
        out.println(yPixelSize);
        out.println(xLoc);
        out.println(yLoc);
        out.flush();
        out.close();

    }
}
