/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.core.filter;

import java.util.Set;

import org.opengis.filter.Id;
import org.opengis.filter.identity.Identifier;

/**
 * AdaptingFilter that implements Id interface.
 * 
 * @author Jody
 * @since 1.1.0
 */
class AdaptingId extends AdaptingFilter<Id> implements Id {

    AdaptingId( Id filter ) {
        super(filter);
    }
    
    public Set<Object> getIDs() {
        return wrapped.getIDs();
    }

    public Set<Identifier> getIdentifiers() {
        return wrapped.getIdentifiers();
    }
    public String toString() {
        return "Adapting:"+wrapped.getIDs();
    }
}
