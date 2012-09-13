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

import net.refractions.udig.catalog.internal.shp.ShpGeoResourceImpl;

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
