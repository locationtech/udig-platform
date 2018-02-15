/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.catalog.jgrass.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.ServiceExtension;
import org.locationtech.udig.catalog.URLUtils;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.geotools.gce.grassraster.JGrassConstants;
import org.geotools.gce.grassraster.JGrassMapEnvironment;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.udig.catalog.jgrass.JGrassPlugin;
import org.locationtech.udig.ui.ChooseCoordinateReferenceSystemDialog;

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
    public static final String KEY = "org.locationtech.udig.catalog.jgrass.urlKey"; //$NON-NLS-1$

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
            // is it a map resource
            if (file.exists() && file.isFile()) {
                File cellFolderFile = file.getParentFile();
                if (cellFolderFile.getName().equalsIgnoreCase(JGrassConstants.CELL)) {
                    // try to get its location
                    JGrassMapEnvironment env = new JGrassMapEnvironment(file);
                    file = env.getLOCATION();
                    url = file.toURI().toURL();
                }
            }

            /*
             * check one: is it a folder?
             */
            if (file.exists() && file.isDirectory()) {
                /*
                 * check two: is it a jgrass folder
                 */
                // does it have a PERMANENT folder?
                File permanentFolder = new File(file.getAbsolutePath() + File.separator + JGrassConstants.PERMANENT_MAPSET);
                if (permanentFolder.exists() && permanentFolder.isDirectory()) {

                    boolean windexists = true;
                    File windFile = new File(permanentFolder + File.separator + JGrassConstants.WIND);
                    if (!windFile.exists()) {
                        windexists = false;
                        // try to see if it is a casesensitivity problem
                        File tmpWindFile = new File(permanentFolder + File.separator + JGrassConstants.WIND.toLowerCase());
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
                        File projWtkFile = new File(file.getAbsolutePath() + File.separator + JGrassConstants.PERMANENT_MAPSET
                                + File.separator + JGrassConstants.PROJ_WKT);
                        if (!setJGrassCrs(projWtkFile)) {
                            return null;
                        }
                    } else {
                        return null;
                    }
                }
            }

        } catch (Throwable e) {
            JGrassPlugin.log(
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
            JGrassPlugin.log(
                    "JGrassPlugin problem: eu.hydrologis.udig.catalog.internal.jgrass#JGrassServiceExtension#setJGrassCrs", e1); //$NON-NLS-1$

            e1.printStackTrace();
        }
        return true;
    }

}
