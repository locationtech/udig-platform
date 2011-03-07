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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ServiceExtension;
import net.refractions.udig.catalog.URLUtils;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.geotools.gce.grassraster.JGrassConstants;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import eu.udig.catalog.jgrass.JGrassPlugin;

/**
 * <p>
 * Creates a service extention for the JGrass database service.
 * </p>
 * 
 * @author Andrea Antonello - www.hydrologis.com
 * @since 1.1.0
 */
public class JGrassServiceExtension implements ServiceExtension {

    /**
     * the jgrass service key, it is used to store the url to the jgrass location
     */
    public static final String KEY = "eu.udig.catalog.jgrass.urlKey"; //$NON-NLS-1$

    public JGrassServiceExtension() {
        super();
    }

    /**
     * this method must check the url to determine if it points to a valid <b>jgrass location folder</b>.
     * It must return <b>null</b> if the url is not intended for this service.
     * 
     * @param url the url points to the actual service itself. In this case to the jgrass location
     *        folder
     * @return a parameter map containing the necessary info or null if the url is not for this
     *         service
     */
    public Map<String, Serializable> createParams( URL url ) {
        Map<String, Serializable> params = null;
        /*
         * does the url represent a jgrass location folder?
         */
        try {
            /*
             * if the file is a file of extention *.jgrass and inside the location, than it was
             * dragged. That is for now the JGrass d&d support.
             */
            File startFile = URLUtils.urlToFile(url);
            if (startFile == null)
                return null;
            String path = startFile.getAbsolutePath();
            if (path.endsWith(".jgrass")) {
                path = new File(path).getParent();
                url = new File(path).toURI().toURL();
            }

            File file = new File(path);
            /*
             * check one: is it a folder?
             */
            if (file.exists() && file.isDirectory()) {
                /*
                 * check two: is it a jgrass folder
                 */
                // does it have a PERMANENT folder?
                File permanentFolder = new File(file.getAbsolutePath() + File.separator
                        + JGrassConstants.PERMANENT_MAPSET);
                if (permanentFolder.exists() && permanentFolder.isDirectory()) {

                    boolean windexists = true;
                    File windFile = new File(permanentFolder + File.separator
                            + JGrassConstants.WIND);
                    if (!windFile.exists()) {
                        windexists = false;
                        // try to see if it is a casesensitivity problem
                        File tmpWindFile = new File(permanentFolder + File.separator
                                + JGrassConstants.WIND.toLowerCase());
                        if (tmpWindFile.exists()) {
                            // if that exists, try to change its case
                            tmpWindFile.renameTo(windFile);
                            windexists = true;
                        }
                    }

                    // does it have a region file?
                    if (windexists) {
                        /*
                         * ok, it is for the service
                         */
                        params = new HashMap<String, Serializable>();
                        params.put(KEY, url);
                        /*
                         * now check the crs
                         */
                        File projWtkFile = new File(file.getAbsolutePath() + File.separator
                                + JGrassConstants.PERMANENT_MAPSET + File.separator
                                + JGrassConstants.PROJ_WKT);
                        if (!setJGrassCrs(projWtkFile)) {
                            return null;
                        }
                    } else {
                        return null;
                    }
                }
            }
        } catch (Throwable e) {
            JGrassPlugin
                    .log(
                            "JGrassPlugin problem: eu.hydrologis.udig.catalog.internal.jgrass#JGrassServiceExtension#createParams", e); //$NON-NLS-1$

            e.printStackTrace();
        }

        return params;
    }

    /**
     * create the JGrass database service
     */
    public IService createService( URL id, Map<String, Serializable> params ) {
        // good defensive programming
        if (params == null)
            return null;

        // check for the properties service key
        if (params.containsKey(KEY)) {
            // found it, create the service handle
            return new JGrassService(params);
        }

        // key not found
        return null;
    }

    public synchronized boolean setJGrassCrs( File projFile ) {
        try {
            if (projFile.exists()) {
                return true;
            } else {

                final ChooseCoordinateReferenceSystemDialog crsChooser = new ChooseCoordinateReferenceSystemDialog();
                Display.getDefault().syncExec(new Runnable(){

                    public void run() {

                        Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
                        crsChooser.open(shell);

                    }

                });

                CoordinateReferenceSystem readCrs = crsChooser.getCrs();
                if (readCrs != null) {
                    BufferedWriter bWriter = new BufferedWriter(new FileWriter(projFile));
                    String crsString = readCrs.toWKT();
                    bWriter.write(crsString);
                    bWriter.close();
                } else {
                    return false;
                }

            }

        } catch (Exception e1) {
            JGrassPlugin
                    .log(
                            "JGrassPlugin problem: eu.hydrologis.udig.catalog.internal.jgrass#JGrassServiceExtension#setJGrassCrs", e1); //$NON-NLS-1$

            e1.printStackTrace();
        }
        return true;
    }

}
