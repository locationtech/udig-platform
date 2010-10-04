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

    @SuppressWarnings("unchecked")
    public Object adapt( IResolve resolve, Class adapter, IProgressMonitor monitor )
            throws IOException {

        if (adapter.isAssignableFrom(JGrassServiceMover.class)) {
            this.resolve = resolve;
            this.monitor = monitor;
            return new JGrassServiceMover(resolve);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public boolean canAdapt( IResolve resolve, Class adapter ) {
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
