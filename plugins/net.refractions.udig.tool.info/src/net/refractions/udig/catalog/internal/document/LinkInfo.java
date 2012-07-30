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
package net.refractions.udig.catalog.internal.document;

import net.refractions.udig.catalog.document.IDocument;
import net.refractions.udig.catalog.document.ILinkInfo;
import net.refractions.udig.catalog.document.IDocument.Type;


/**
 * Info container for document properties as retrieved from the property file.
 * 
 * @author Naz Chan
 */
public class LinkInfo implements ILinkInfo {

    /**
     * Document label
     */
    private String label;
    /**
     * Document info - file location for attachments or attribute name for hotlinks
     */
    private String info;
    /**
     * Document type
     */
    private IDocument.Type type;
    
    public LinkInfo(String label, String info, IDocument.Type type) {
        this.label = label;
        this.info = info;
        this.type = type;
    }
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public IDocument.Type getType() {
        return type;
    }

    public void setType(IDocument.Type type) {
        this.type = type;
    }
    
}
