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
import net.refractions.udig.catalog.document.IDocument.Type;
import net.refractions.udig.catalog.document.ILinkInfo;

/**
 * Document info container for document. This was initially designed to contain document properties
 * retrieved from the property file but can also be reused and/or extended where possible.
 * 
 * @author Naz Chan
 */
public class LinkInfo implements ILinkInfo {

    /**
     * Document label
     */
    private String label;

    /**
     * Document description
     */
    private String description;

    /**
     * Document type
     */
    private IDocument.Type type;

    /**
     * Document info
     * <p>
     *  - file location for attachments or attribute name for hotlinks
     */
    private String info;

    /**
     * Document configuration
     */
    private String config;

    public LinkInfo(String infoStr) {
        fromString(infoStr);
    }
            
    public LinkInfo(String label, String info, Type type) {
        this.label = label;
        this.info = info;
        this.type = type;
    }

    public LinkInfo(String label, String description, String info, Type type, String config) {
        this.label = label;
        this.description = description;
        this.info = info;
        this.type = type;
        this.config = config;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    private void fromString(String infoStr) {
        // TODO implement
    }
    
    @Override
    public String toString() {
        // TODO implement
        return super.toString();
    }
    
}
