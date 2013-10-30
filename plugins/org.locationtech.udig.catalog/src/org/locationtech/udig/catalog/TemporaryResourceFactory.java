/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
