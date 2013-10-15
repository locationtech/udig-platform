/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tool.select;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.runtime.IAdaptable;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.collection.DecoratingFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * A feature collection that adapts to other objects.
 * 
 * @author Jesse Eichar
 * @since 1.1.0
 */
public class AdaptableFeatureCollection extends
		DecoratingFeatureCollection<SimpleFeatureType, SimpleFeature> implements
		IAdaptable {
    
    protected Set<Object> adapters = new CopyOnWriteArraySet<Object>();

    public AdaptableFeatureCollection( final FeatureCollection<SimpleFeatureType, SimpleFeature> wrapped ) {
        super( wrapped );
    }
    @SuppressWarnings("unchecked")
    public Object getAdapter( Class adapter ) {
        for( Object obj : adapters ) {
            if( adapter.isAssignableFrom(obj.getClass()) )
                return obj;
        }
        return null;
    }

    public void addAdapter( Object adapter ) {
        adapters.add(adapter);
    }

    public boolean removeAdapter( Object adapter ) {
        return adapters.remove(adapter);
    }

    public void clearAdapters() {
        adapters.clear();
    }
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((adapters == null) ? 0 : adapters.hashCode());
        result = PRIME * result + ((delegate == null) ? 0 : delegate.hashCode());
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final AdaptableFeatureCollection other = (AdaptableFeatureCollection) obj;
        if (adapters == null) {
            if (other.adapters != null)
                return false;
        } else if (!adapters.equals(other.adapters))
            return false;
        if (delegate== null) {
            if (other.delegate != null)
                return false;
        } else if (!delegate.equals(other.delegate))
            return false;
        return true;
    }
}
