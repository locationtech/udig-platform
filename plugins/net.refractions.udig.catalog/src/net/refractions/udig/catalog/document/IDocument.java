/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2011, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.catalog.document;

/**
 * Document interface.
 * 
 * @author paul.pfeiffer
 * @author Naz Chan
 */
public interface IDocument {

    /**
     * Document type
     */
    public enum DocType {
        /**
         * Linked documents store a reference to the document.
         * <p>
         * This may have either of the following content types:
         * <ul>
         * <li>{@link Type#FILE}</li>
         * <li>{@link Type#WEB}</li>
         * </ul>
         */
        LINKED,
        /**
         * Attachments store a copy of the document.
         * <p>
         * This may have either of the following content types:
         * <ul>
         * <li>{@link Type#FILE}</li>
         * </ul>
         */
        ATTACHMENT,
        /**
         * Hotlinks store reference to the document as a feature's attribute value.
         *          * <p>
         * This may have either of the following content types:
         * <ul>
         * <li>{@link Type#FILE}</li>
         * <li>{@link Type#WEB}</li>
         * <li>{@link Type#ACTION}</li>
         * </ul>
         */
        HOTLINK;
    }

    /**
     * Document content type
     */
    public enum Type {
        /**
         * Documents that refer to files.
         */
        FILE, 
        /**
         * Documents that refer to web pages.
         */
        WEB, 
        /**
         * Documents that refer to custom actions.
         */
        ACTION;
        public static boolean exists(String type) {
            for (Type c : values()) {
                if (c.name().equals(type)) {
                    return true;
                }
            }
            return false;
        }
    };

    /**
     * Gets the document value. The value returned will depend on the document's content type. See
     * {@link #getType()} for details.
     * 
     * @return document info
     */
    public Object getValue();
    
    /**
     * Gets the document label. This is to be used for labelling documents in lists and views.
     * 
     * @return label
     */
    public String getLabel();

    /**
     * Gets the document description. This is to be used for labelling documents in lists and views.
     */
    public String getDescription();

    /**
     * Gets the document type.
     * 
     * @return document type
     */
    public DocType getDocType();

    /**
     * Gets the document content type.
     * <p>
     * The following documents are defined:
     * <ul>
     * <li>{@link Type#FILE} value is supplied as a local File</li>
     * <li>{@link Type#WEB} value is supplied as URL</li>
     * <li>{@link Type#ACTION} value is marked for script use</li>
     * </ul>
     * 
     * @return document content type
     */
    public Type getType();

    /**
     * Open this document; in a platform specific manner.
     * <p>
     * As an example we will expect the operating system to open website URLs; or File references.
     * We may detect the presence of uDig specific things such as projects, SLDs or GetCapabilities
     * references and take appropriate action.
     */
    public boolean open();

    /**
     * Checks if the document has a related content (file or URL). Or if the value does not point to
     * a valid file or URL.
     * 
     * @return true if it has a related content, otherwise false
     */
    public boolean isEmpty();

    /**
     * Checks if the document is set as a template.
     * 
     * @return true if template, otherwise false
     */
    public boolean isTemplate();
    
    /**
     * Gets the document source responsible for listing this document.
     * 
     * @return document source
     */
    public IAbstractDocumentSource getSource();

}
