/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.ui.operations;

import org.locationtech.udig.core.AdapterUtil;

/**
 * Default impl of OpFilter
 * @author jones
 * @since 1.1.0
 */
public class AdaptsToFilter implements OpFilter {

    private String targetClass;

    /**
     * @param targetClass Class that the object must resolve to.
     */
    public AdaptsToFilter( String targetClass) {
        this.targetClass=targetClass;
    }
    
    public boolean accept( Object object ) {
        return AdapterUtil.instance.canAdaptTo(targetClass, object);
    }

    public boolean canCacheResult() {
        return true;
    }

    public boolean isBlocking() {
        return false;
    }

    public void addListener( IOpFilterListener listener ) {
        // do nothing
    }

    public void removeListener( IOpFilterListener listener ) {
        // do nothing
    }

}
