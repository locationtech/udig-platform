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
package net.refractions.udig.document.source;

import java.net.URL;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.document.IAbstractDocumentSource;
import net.refractions.udig.catalog.document.IDocument;

/**
 * The base class for document sources.
 * 
 * @author Naz Chan
 */
public abstract class AbstractDocumentSource implements IAbstractDocumentSource {

    protected URL url;
    protected IGeoResource resource;
    
    protected List<IDocument> docs;
    
    public AbstractDocumentSource(IGeoResource resource) {
        this.url = resource.getIdentifier();
        this.resource = resource;
    }
    
}
