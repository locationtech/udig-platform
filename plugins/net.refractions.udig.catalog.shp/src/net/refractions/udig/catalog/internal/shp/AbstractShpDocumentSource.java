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
package net.refractions.udig.catalog.internal.shp;

import java.net.URL;

import net.refractions.udig.catalog.DocumentFactory;
import net.refractions.udig.catalog.IAbstractDocumentSource;
import net.refractions.udig.catalog.IDocumentFolder;

/**
 * The abstract class for shape document sources. This sets up needs utility objects like the
 * document factory and the property file parser.
 * 
 * @author nchan 
 */
public abstract class AbstractShpDocumentSource implements IAbstractDocumentSource {

    protected URL url;
    protected ShpDocPropertyParser propParser;
    protected DocumentFactory docFactory;
    protected IDocumentFolder folder;
    
    public AbstractShpDocumentSource(URL url, String folderName) {
        this.url = url;
        this.docFactory = new DocumentFactory(this);
        this.folder = docFactory.createFolder(folderName);
        this.docFactory.setFolder(folder);
        this.propParser = new ShpDocPropertyParser(url, docFactory);
    }
    
}
