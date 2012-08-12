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
 * Attachment information container. This is designed to be able to parse ({@link #LinkInfo(String)}
 * ) and format ({@link LinkInfo#toString()}) the info string from the resource property file.
 * 
 * @author Naz Chan
 */
public class LinkInfo implements ILinkInfo {

    /**
     * Attachment label
     */
    private String label;

    /**
     * Attachment description
     */
    private String description;

    /**
     * Attachment info - file or url metadata
     */
    private String info;

    /**
     * Attachment type
     */
    private IDocument.Type type;

    /**
     * Document configuration
     */
    private String config;

    public static final String DELIMITER = "~"; //$NON-NLS-1$

    public LinkInfo(String attachmentInfo) {
        fromString(attachmentInfo);
    }

    public LinkInfo(String label, String info, IDocument.Type type) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    private void fromString(String attachmentInfo) {
        final String[] defValues = attachmentInfo.split(DELIMITER);
        info = getCleanValue(defValues[0]);
        type = Type.valueOf(defValues[1]);
        if (defValues.length > 2) {
            label = getCleanValue(defValues[2]);
        } else {
            label = null;
        }
        if (defValues.length > 3) {
            description = getCleanValue(defValues[3]);
        } else {
            description = null;
        }
    }

    private String getCleanValue(String text) {
        if (text != null) {
            final String cleanText = text.trim();
            if (cleanText.length() > 0) {
                return cleanText;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (info != null) {
            sb.append(info);
        }
        sb.append(DELIMITER);
        if (type != null) {
            sb.append(type);
        }
        sb.append(DELIMITER);
        if (label != null) {
            sb.append(label);
        }
        sb.append(DELIMITER);
        if (description != null) {
            sb.append(description);
        }
        return sb.toString();
    }

}
