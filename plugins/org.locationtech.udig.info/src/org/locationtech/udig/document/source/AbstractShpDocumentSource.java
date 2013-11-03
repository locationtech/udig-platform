/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.document.source;

import org.locationtech.udig.catalog.internal.shp.ShpGeoResourceImpl;

/**
 * The base class for shape document sources. This sets up needs utility objects like the document
 * factory and the property file parser.
 * 
 * @author Naz Chan 
 */
public abstract class AbstractShpDocumentSource extends AbstractDocumentSource {

    protected ShpDocPropertyParser propParser;
    protected ShpDocFactory docFactory;
    
    public AbstractShpDocumentSource(ShpGeoResourceImpl resource) {
        super(resource);
        this.url = resource.service().getIdentifier();
        this.docFactory = new ShpDocFactory(this);
        this.propParser = new ShpDocPropertyParser(url);
    }
    
}
