/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 * (C) 2000, 2010 IBM Corporation and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 * Contributor org.eclipse.ui.internal.util.Util:
 *     IBM Corporation - initial API and implementation
 */
/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.locationtech.udig.core;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PlatformObject;

/**
 * Utility class of helper methods for getting along with the Eclipse Platform.
 *
 * @author Jody Garnett
 * @since 1.2.0
 */
public class Util {

    /**
     * If it is possible to adapt the given object to the given type, this returns the adapter.
     * Performs the following checks:
     * <p>
     * This is based on the eclipse org.eclipse.ui.internal.util.Util class which was not visible to
     * RCP applications. Java 5 templating added for usability.
     * </p>
     *
     * <ol>
     * <li>Returns <code>sourceObject</code> if it is an instance of the adapter type.</li>
     * <li>If sourceObject implements IAdaptable, it is queried for adapters.</li>
     * <li>If sourceObject is not an instance of PlatformObject (which would have already done so),
     * the adapter manager is queried for adapters</li>
     * </ol>
     *
     * Otherwise returns null.
     *
     * @param sourceObject object to adapt, or null
     * @param adapterType type to adapt to
     * @return a representation of sourceObject that is assignable to the adapter type, or null if
     *         no such representation exists
     */
    public static <T> T getAdapter(Object sourceObject, Class<T> adapterType) {
        Assert.isNotNull(adapterType);
        if (sourceObject == null) {
            return null;
        }
        if (adapterType.isInstance(sourceObject)) {
            return adapterType.cast(sourceObject);
        }

        if (sourceObject instanceof IAdaptable) {
            IAdaptable adaptable = (IAdaptable) sourceObject;

            Object result = adaptable.getAdapter(adapterType);
            if (result != null) {
                // Sanity-check
                Assert.isTrue(adapterType.isInstance(result));
                return adapterType.cast(result);
            }
        }

        if (!(sourceObject instanceof PlatformObject)) {
            Object result = Platform.getAdapterManager().getAdapter(sourceObject, adapterType);
            if (result != null) {
                return adapterType.cast(result);
            }
        }
        return null; // sourceObject not available as requested adapterType
    }
}
