/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.document.model;

import org.locationtech.udig.catalog.document.IAbstractDocumentSource;

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
