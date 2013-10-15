/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
 package eu.udig.catalog.jgrass.core;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Map;

import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveAdapterFactory;
import net.refractions.udig.catalog.ServiceMover;
import net.refractions.udig.catalog.URLUtils;

import org.eclipse.core.runtime.IProgressMonitor;

import eu.udig.catalog.jgrass.JGrassPlugin;

public class JGrassServiceMover implements IResolveAdapterFactory, ServiceMover {

    private IResolve resolve;
    private IProgressMonitor monitor;

    public JGrassServiceMover() {
    }

    public JGrassServiceMover( IResolve resolve ) {
        this.resolve = resolve;
    }

    public <T> T adapt( IResolve resolve, Class<T>adapter, IProgressMonitor monitor )
            throws IOException {

        if (adapter.isAssignableFrom(JGrassServiceMover.class)) {
            this.resolve = resolve;
            this.monitor = monitor;
            return adapter.cast( new JGrassServiceMover(resolve) );
        }

        return null;
    }

    public boolean canAdapt( IResolve resolve, Class<?> adapter ) {
        return adapter.isAssignableFrom(JGrassServiceMover.class);
    }

    public String move( File destinationFolder ) {
        /*
         * shapefile are moved into a folder that has to exist
         */
        if (destinationFolder.isDirectory() && destinationFolder.exists()) {

            // the folder will be moved inside the workspace data folder keeping the same name of
            // the location
            JGrassService jgrassServiceImpl = (JGrassService) resolve;
            String locationName = null;
            try {
                locationName = jgrassServiceImpl.getInfo(monitor).getTitle();
            } catch (IOException e) {
                JGrassPlugin.log("JGrassPlugin problem: eu.hydrologis.udig.catalog.internal.jgrass#JGrassServiceMover#move", e);  //$NON-NLS-1$
                
                e.printStackTrace();
            }
            String dataPath = destinationFolder.getAbsolutePath();
            File newJGrassLocationFile = new File(dataPath + File.separator + locationName);
            // do not overwrite
            if (newJGrassLocationFile.exists()) {
                return "JGrass Location already existing inside the workspace. Not overwriting.";
            }

            // get the actual location path
            Map<String, Serializable> parametersMap = jgrassServiceImpl.getConnectionParams();
            URL url = (URL) parametersMap.get(JGrassServiceExtension.KEY);
            if (url == null)
                return "No JGrass Service existing at " + locationName;
            File jgrassLocationeFile = URLUtils.urlToFile(url);

            if (jgrassLocationeFile.exists() || jgrassLocationeFile.isDirectory()) {
                boolean success = jgrassLocationeFile.renameTo(newJGrassLocationFile);
                if (!success) {
                    return "Problems occured during consolidation of JGrass Location: "
                            + locationName;
                }
            }
            return null;
        }

        return "Problems in preparing the consolidation environment.";
    }

}
