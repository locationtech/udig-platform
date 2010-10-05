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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.ui.UDIGConnectionFactory;

/**
 * TODO Purpose of
 * <p>
 * connection factory to the JGrass database service
 * </p>
 * <p>
 * <i>Note: based on the WMS plugin</i>
 * </p>
 * 
 * @author Andrea Antonello - www.hydrologis.com
 * @since 1.1.0
 */
public class JGrassConnectionFactory extends UDIGConnectionFactory {

    public boolean canProcess(Object context) {
        if (context instanceof IResolve) {
            IResolve resolve = (IResolve) context;
            try {
                // what enters should be a JGrassService, which resolves to a
                // folder
                return (resolve.canResolve(File.class) && resolve.resolve(
                        File.class, null).isDirectory());
            } catch (IOException e) {
                throw (RuntimeException) new RuntimeException().initCause(e);
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Serializable> createConnectionParameters(Object context) {
        if (context instanceof IResolve) {
            Map<String, Serializable> params = createParams((IResolve) context);
            if (!params.isEmpty())
                return params;
        }
        return Collections.EMPTY_MAP;
    }

    @SuppressWarnings("unchecked")
    static public Map<String, Serializable> createParams(IResolve handle) {
        if (handle instanceof JGrassService) {
            // got a hit!
            JGrassService jgrassService = (JGrassService) handle;
            return jgrassService.getConnectionParams();
        }
        return Collections.EMPTY_MAP;
    }

    /** 'Create' params given the provided url, no magic occurs */
    static public Map<String, Serializable> createParams(URL url) {
        JGrassServiceExtension factory = new JGrassServiceExtension();
        Map<String, Serializable> params = factory.createParams(url);
        if (params != null)
            return params;

        Map<String, Serializable> params2 = new HashMap<String, Serializable>();
        params2.put(JGrassServiceExtension.KEY, url);
        return params2;
    }

    public URL createConnectionURL(Object context) {
        return null;
    }

}
