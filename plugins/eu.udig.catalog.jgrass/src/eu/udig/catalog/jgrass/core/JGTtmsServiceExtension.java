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
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ServiceExtension;
import net.refractions.udig.catalog.URLUtils;

/**
 * <p>
 * Creates a service extention for the JGrasstools TMS service.
 * </p>
 * 
 * @author Andrea Antonello - www.hydrologis.com
 * @since 1.3.2
 */
public class JGTtmsServiceExtension implements ServiceExtension {

    /**
     * the JGrasstools TMS service key
     */
    public static final String KEY = "eu.udig.catalog.jgrasstoolstms.urlKey"; //$NON-NLS-1$

    public JGTtmsServiceExtension() {
        super();
    }

    /**
     * @param url the url points to the actual service itself. 
     * @return a parameter map containing the necessary info or null if the url is not for this
     *         service
     */
    public Map<String, Serializable> createParams( URL url ) {
        Map<String, Serializable> params;
        try {
            params = null;
            File propertiesFile = URLUtils.urlToFile(url);
            if (propertiesFile == null || !propertiesFile.exists())
                return null;
            String path = propertiesFile.getAbsolutePath();
            if (path.endsWith(".mapurl")) {
                url = propertiesFile.toURI().toURL();
                params = new HashMap<String, Serializable>();
                params.put(KEY, url);
                return params;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * create the JGrasstools TMS service
     */
    public IService createService( URL id, Map<String, Serializable> params ) {
        // good defensive programming
        if (params == null)
            return null;

        // check for the properties service key
        if (params.containsKey(KEY)) {
            // found it, create the service handle
            return new JGTtmsService(params);
        }

        // key not found
        return null;
    }
}
