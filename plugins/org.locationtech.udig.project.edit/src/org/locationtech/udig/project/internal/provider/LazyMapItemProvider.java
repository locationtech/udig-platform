/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2022, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.provider;

import java.util.Collection;

/**
 * This is the original lazy map item provided gerneated from EMF with the super class replaced.
 * <p>
 * This is being preserved for the use of LayersView; but it is our intention to replace it with
 * LazyMapItemProvider (configured with a ChildFetcher for listing layers).
 */
public class LazyMapItemProvider extends MapItemProviderDecorator {
    @Override
    public Collection<?> getChildren(Object object) {
        // MAKE THIS LAZY
        return super.getChildren(object);
    }

}
