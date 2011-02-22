/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.catalog.internal.worldimage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.URLUtils;
import net.refractions.udig.catalog.rasterings.AbstractRasterGeoResource;
import net.refractions.udig.catalog.worldimage.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.geotools.data.shapefile.prj.PrjFileReader;
import org.geotools.gce.image.WorldImageFormat;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.parameter.DefaultParameterDescriptor;
import org.geotools.parameter.DefaultParameterDescriptorGroup;
import org.geotools.parameter.ParameterGroup;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Provides a handle to a world image resource allowing the service to be lazily
 * loaded.
 * @author mleslie
 * @since 0.6.0
 */
public class WorldImageGeoResourceImpl extends AbstractRasterGeoResource {
    private IGeoResourceInfo info;
    private URL prjURL;
    String name;

    /**
     * Construct <code>WorldImageGeoResourceImpl</code>.
     *
     * @param service Service creating this resource.
     * @param name Human readable name of this resource.
     * @param prjURL Name a projection file associated with this resource
     *                can be expected to have.
     */
    public WorldImageGeoResourceImpl(WorldImageServiceImpl service,
            String name, URL prjURL) {
        super(service, name);
        this.prjURL = prjURL;
    }

    public IGeoResourceInfo getInfo(IProgressMonitor monitor) throws IOException {
        if(this.info == null && getStatus() != Status.BROKEN) {
            this.info = new IGeoResourceWorldImageInfo();
        }
        return this.info;
    }

    /**
     * Convenience method for opening a PrjFileReader.  Stolen from Geotools
     * ShapefileSomethingROther
     *
     * @return A new PrjFileReader
     *
     * @throws IOException If an error occurs during creation.
     */
    private PrjFileReader openPrjReader(URL prjURL)
            throws IOException, FactoryException {
        ReadableByteChannel rbc = null;
        try{
            rbc = getReadChannel(prjURL);
        }
        catch(IOException e){
            CatalogPlugin.getDefault().getLog().log(
                    new org.eclipse.core.runtime.Status(IStatus.WARNING,
                    "net.refractions.udig.catalog", 0,  //$NON-NLS-1$
                    Messages.WorldImageGeoResourceImpl_PrjUnavailable, e ));
        }
        if (rbc == null) {
            return null;
        }

        return new PrjFileReader(rbc);
    }

    /**
     * Convienience method to create a ReadableByteChannel from a URL.
     *
     * @param prjURL
     * @return A Channel for the given file
     * @throws IOException
     */
    private ReadableByteChannel getReadChannel(URL prjURL)
            throws IOException {
        ReadableByteChannel channel = null;

        if (prjURL.getProtocol().equalsIgnoreCase("file")) { //$NON-NLS-1$
        	File file = URLUtils.urlToFile(prjURL);

            if (!file.exists() || !file.canRead()) {
            	throw new FileNotFoundException(file.getAbsolutePath());
            }
            FileInputStream in = new FileInputStream(file);
            channel = in.getChannel();

        } else {
            InputStream in = prjURL.openConnection().getInputStream();
            channel = Channels.newChannel(in);
        }

        return channel;
    }

    public ParameterGroup getReadParameters() {
        try {
            PrjFileReader prjRead=null;
            try{
                if( prjURL!=null ){
                 prjRead = openPrjReader(this.prjURL);
                }
            }catch (FileNotFoundException e) {
                CatalogPlugin.getDefault().getLog().log(
                        new org.eclipse.core.runtime.Status(IStatus.WARNING,
                        "net.refractions.udig.catalog", 0,  //$NON-NLS-1$
                        "", e )); //$NON-NLS-1$
            }
            CoordinateReferenceSystem crsSys = null;
            if (prjRead != null) {
            	crsSys = prjRead.getCoodinateSystem();
            }
            else {
            	//prj file not read, default to lat long
            	crsSys = DefaultEngineeringCRS.GENERIC_2D;
            }

            DefaultParameterDescriptor crs = new DefaultParameterDescriptor("crs", //$NON-NLS-1$
                    CoordinateReferenceSystem.class, null, crsSys);
            ParameterDescriptor env = WorldImageFormat.ENVELOPE;

            // Stolen from WorldImageFormat, as mInfo is not externally accesible
            HashMap<String,String> info1 = new HashMap<String,String>();
            info1.put("name", "WorldImage");  //$NON-NLS-1$//$NON-NLS-2$
            info1.put("description", //$NON-NLS-1$
                "A raster file accompanied by a spatial data file"); //$NON-NLS-1$
            info1.put("vendor", "Geotools"); //$NON-NLS-1$ //$NON-NLS-2$
            info1.put("docURL", "http://www.geotools.org/WorldImageReader+formats"); //$NON-NLS-1$ //$NON-NLS-2$
            info1.put("version", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$
            return new ParameterGroup(new DefaultParameterDescriptorGroup(
                    info1, new GeneralParameterDescriptor[] { crs, env }));

        } catch (MalformedURLException e) {
            CatalogPlugin.getDefault().getLog().log(
                    new org.eclipse.core.runtime.Status(IStatus.WARNING,
                    "net.refractions.udig.catalog", 0,  //$NON-NLS-1$
                    "", e ));  //$NON-NLS-1$
            return super.getReadParameters();
        } catch (IOException e) {
            CatalogPlugin.getDefault().getLog().log(
                    new org.eclipse.core.runtime.Status(IStatus.WARNING,
                    "net.refractions.udig.catalog", 0,  //$NON-NLS-1$
                    "", e ));  //$NON-NLS-1$
            return super.getReadParameters();
        } catch (FactoryException e) {
            CatalogPlugin.getDefault().getLog().log(
                    new org.eclipse.core.runtime.Status(IStatus.WARNING,
                    "net.refractions.udig.catalog", 0,  //$NON-NLS-1$
                    "", e ));  //$NON-NLS-1$
            return super.getReadParameters();
        }
    }

    /**
     * Describes this Resource.
     * @author mleslie
     * @since 0.6.0
     */
    public class IGeoResourceWorldImageInfo extends IGeoResourceInfo {
        IGeoResourceWorldImageInfo() {
            this.keywords = new String[] {
                    "WorldImage", "world image", ".gif", ".jpg", ".jpeg",   //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$
                    ".tif", ".tiff", ".png"};   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

            this.title = getIdentifier().getFile();
            int indexOf = title.lastIndexOf('/');
            if( indexOf>-1 && indexOf<title.length() ){
                title=title.substring(indexOf+1);
            }

            this.name=this.title;
            this.description = getIdentifier().toString();
            this.bounds = getBounds();
        }

        /*
         * @see net.refractions.udig.catalog.IGeoResourceInfo#getBounds()
         */
        public ReferencedEnvelope getBounds() {
            if(this.bounds == null) {
                Envelope env = null;
                try {
                    GridCoverage source = (GridCoverage)findResource();
                    org.opengis.spatialschema.geometry.Envelope ptBounds =
                        source.getEnvelope();
                    env = new Envelope( ptBounds.getMinimum(0), ptBounds.getMaximum(0),
                            ptBounds.getMinimum(1), ptBounds.getMaximum(1));

                    CoordinateReferenceSystem geomcrs =
                        source.getCoordinateReferenceSystem();

                    this.bounds = new ReferencedEnvelope(env, geomcrs);
                    /*
                    if(geomcrs != null) {
                        if(!geomcrs.equals(CRS.decode("EPSG:4269"))) { //$NON-NLS-1$
                            bounds = JTS.transform(bounds, CRS.decode("EPSG:4269")); //$NON-NLS-1$
                        }
                    } else {
                        System.err.println("CRS unknown for WorldImage"); //$NON-NLS-1$
                    }
                    */
                } catch (Exception e) {
                    CatalogPlugin.getDefault().getLog().log(
                            new org.eclipse.core.runtime.Status(IStatus.WARNING,
                            "net.refractions.udig.catalog", 0,  //$NON-NLS-1$
                            "Error while getting the bounds of a layer", e ));   //$NON-NLS-1$

                    this.bounds = new ReferencedEnvelope(new Envelope(-180,180,-90,90), DefaultGeographicCRS.WGS84);
                }
            }
            return this.bounds;
        }
    }
}
