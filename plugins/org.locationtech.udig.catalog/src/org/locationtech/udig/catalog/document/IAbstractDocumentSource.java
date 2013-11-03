/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.document;

/**
 * Base interface for document sources.
 * 
 * @author Naz Chan
 */
public interface IAbstractDocumentSource {
    
    /**
     * Checks if this document source is enabled and should be used to provide access to documents.
     * 
     * @return true if enabled, otherwise false
     */
    public boolean isEnabled();
    
    /**
     * Checks if this document source allows updating of its enabled state.
     * 
     * @return true if allowed, otherwise false
     */
    public boolean isEnabledEditable();
    
}
