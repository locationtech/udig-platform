/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
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
package net.refractions.udig.document.model;

import net.refractions.udig.catalog.document.IAbstractDocumentSource;

import org.eclipse.core.runtime.IAdaptable;
import org.opengis.feature.simple.SimpleFeature;

/**
 * Base document item model. This is the parent class of document folder and document.
 * 
 * @author Naz Chan
 */
public abstract class AbstractDocumentItem implements IAdaptable {

    private SimpleFeature feature;
    protected IAbstractDocumentSource source;
    
    public void setFeature(SimpleFeature feature) {
        this.feature = feature;
    }
    
    public IAbstractDocumentSource getSource() {
        return source;
    }

    public void setSource(IAbstractDocumentSource source) {
        this.source = source;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object getAdapter(Class adapter) {
        if (adapter.isAssignableFrom(SimpleFeature.class)) {
            return feature;
        }
        return null;
    }

}
