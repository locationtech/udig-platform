/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * (C) C.U.D.A.M. Universita' di Trento
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.udig.catalog.jgrass.core;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.URLUtils;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.ISharedImages;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

import eu.udig.catalog.jgrass.JGrassPlugin;

public class JGTtmsGeoResource extends IGeoResource {

    public static final String READERID = "eu.udig.catalog.jgrass.core.JGTtmsGeoResource.readerid"; //$NON-NLS-1$

    private URL url = null;

    /** error message field */
    private Throwable msg = null;

    /** metadata info field */
    private IGeoResourceInfo info = null;

    private final IService parentService;

    private CoordinateReferenceSystem tmsCrs;

    private JGTtmsProperties tmsProperties = null;

    private File tmsPropertiesFile;

    public JGTtmsGeoResource( JGTtmsService parentService, URL url ) {
        this.parentService = parentService;
        this.url = url;
        tmsPropertiesFile = URLUtils.urlToFile(url);

        tmsProperties = getTmsProperties(tmsPropertiesFile);

        tmsCrs = parentService.getCrs();
    }

    private JGTtmsProperties getTmsProperties( File tmsPropertiesFile ) {
        JGTtmsProperties tmsProperties = new JGTtmsProperties();
        List<String> fileLines = new ArrayList<String>();
        try {
            fileLines = FileUtils.readLines(tmsPropertiesFile);
        } catch (IOException e1) {
            msg = e1;
            e1.printStackTrace();
        }

        for( String line : fileLines ) {
            line = line.trim();
            if (line.length() == 0) {
                continue;
            }

            int split = line.indexOf('=');
            if (split != -1) {
                String value = line.substring(split + 1).trim();
                if (line.startsWith("url")) {

                    int indexOfZ = value.indexOf("ZZZ");
                    tmsProperties.HOST_NAME = value.substring(0, indexOfZ);
                    tmsProperties.tilePart = value.substring(indexOfZ);
                    if (value.startsWith("http")) {
                        // remove http
                        tmsProperties.HOST_NAME = tmsProperties.HOST_NAME.substring(7);
                    } else {
                        tmsProperties.PROTOCOL = "file";
                        tmsProperties.HOST_NAME = tmsPropertiesFile.getParent() + File.separator + tmsProperties.HOST_NAME;
                        tmsProperties.isFile = true;
                    }
                }
                if (line.startsWith("minzoom")) {
                    try {
                        tmsProperties.ZOOM_MIN = Byte.valueOf(value);
                    } catch (Exception e) {
                        // use default: handle exception
                    }
                }
                if (line.startsWith("maxzoom")) {
                    try {
                        tmsProperties.ZOOM_MAX = Byte.valueOf(value);
                    } catch (Exception e) {
                        // use default: handle exception
                    }
                }
                if (line.startsWith("center")) {
                    try {
                        String[] coord = value.split("\\s+"); //$NON-NLS-1$
                        double x = Double.parseDouble(coord[0]);
                        double y = Double.parseDouble(coord[1]);
                        tmsProperties.centerPoint = new Coordinate(x, y);
                    } catch (NumberFormatException e) {
                        // use default
                    }
                }
                if (line.startsWith("type")) {
                    if (value.equals(JGTtmsProperties.TILESCHEMA.tms.toString())) {
                        tmsProperties.type = JGTtmsProperties.TILESCHEMA.tms;
                    }
                }
            }
        }
        return tmsProperties;
    }

    public <T> boolean canResolve( Class<T> adaptee ) {
        // garbage in, garbage out
        if (adaptee == null)
            return false;

        /*
         * in this case our resource is a folder, therefore of type File
         */
        return adaptee.isAssignableFrom(IService.class) || adaptee.isAssignableFrom(IGeoResource.class)
                || adaptee.isAssignableFrom(JGTtmsGeoResource.class) || super.canResolve(adaptee);
    }

    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (adaptee == null)
            return null;

        if (adaptee.isAssignableFrom(IService.class)) {
            return adaptee.cast(parentService);
        }
        if (adaptee.isAssignableFrom(IGeoResourceInfo.class)) {
            return adaptee.cast(getInfo(monitor));
        }
        if (adaptee.isAssignableFrom(File.class)) {
            return adaptee.cast(tmsPropertiesFile);
        }
        if (adaptee.isAssignableFrom(JGTtmsGeoResource.class)) {
            return adaptee.cast(this);
        }
        if (adaptee.isAssignableFrom(IGeoResource.class)) {
            return adaptee.cast(this);
        }
        if (adaptee.isAssignableFrom(CoordinateReferenceSystem.class)) {
            return adaptee.cast(tmsCrs);
        }
        // bad call to resolve
        return super.resolve(adaptee, monitor);
    }
    // public ID getID() {
    // return id;
    // }

    public URL getIdentifier() {
        return url;
    }

    public ID getID() {
        return new ID(url);
    }

    public IService service( IProgressMonitor monitor ) throws IOException {
        return parentService;
    }

    public Throwable getMessage() {
        return msg;
    }

    public Status getStatus() {
        // error occured
        if (msg != null) {
            return Status.BROKEN;
        }

        return Status.CONNECTED;
    }

    /**
     * <p>
     * get some informations about the map resource
     * </p>
     * 
     */
    class JGTtmsGeoResourceInfo extends IGeoResourceInfo {
        public JGTtmsGeoResourceInfo( IProgressMonitor monitor ) {
            String baseName = FilenameUtils.getBaseName(tmsPropertiesFile.getName());
            this.name = baseName;
            this.title = this.name;
            this.description = this.name;

            try {
                Envelope env = new Envelope(tmsProperties.centerPoint);
                env.expandBy(1000.0); // TODO when bounds are realy available change this
                bounds = new ReferencedEnvelope(env, tmsCrs);
                super.icon = CatalogUIPlugin.getDefault().getImageDescriptor(ISharedImages.GRID_OBJ);
            } catch (Exception e) {
                super.icon = AbstractUIPlugin.imageDescriptorFromPlugin(JGrassPlugin.PLUGIN_ID, "icons/obj16/problem.gif"); //$NON-NLS-1$
                e.printStackTrace();
            }

        }

        public ReferencedEnvelope getBounds() {
            return bounds;
        }

        public void setBounds( ReferencedEnvelope newBounds ) {
            bounds = newBounds;
        }
    }

    @Override
    public String getTitle() {
        try {
            createInfo(new NullProgressMonitor());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return info.getTitle();
    }

    protected IGeoResourceInfo createInfo( IProgressMonitor monitor ) throws IOException {
        // support concurrent access
        synchronized (this) {
            if (info == null) {
                info = new JGTtmsGeoResourceInfo(monitor);
            }
        }

        return info;
    }

}
