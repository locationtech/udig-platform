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
package net.refractions.udig.catalog;

import java.io.IOException;

/**
 * Creates Temporary Resources.  Used by the temporaryResource extension point and the {@link net.refractions.udig.catalog.ICatalog#getTemporaryDescriptorClasses()}
 * and {@link net.refractions.udig.catalog.ICatalog#createTemporaryResource(Object)}.
 * <p>
 * An example is in net.refractions.udig.catalog.memory plugin.  It takes a FeatureType as the
 * param and creates a resource that resolves to a FeatureStore that stores features of the
 * FeatureType.
 * </p>
 * @author Jesse
 * @since 1.1.0
 */
public interface TemporaryResourceFactory {
    /**
     * Creates an IGeoResource that the implements the {@link ITransientResolve} interface or resolve to that interface.
     *
     * @param param A object of the type that is defined in the temporaryResource extension point.  
     * @return Creates an IGeoResource that the implements the {@link ITransientResolve} interface.
     */
    IGeoResource createResource( Object param ) throws IOException;
}
