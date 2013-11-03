/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.catalog.document;

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
    public enum Type {
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
    public enum ContentType {
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
        
        /**
         * Checks if the type exists in this enum.
         * 
         * @param type
         * @return true if exists, otherwise false
         */
        public static boolean exists(String type) {
            for (ContentType c : values()) {
                if (c.name().equals(type)) {
                    return true;
                }
            }
            return false;
        }
    };

    /**
     * Gets the document content. The value returned will depend on the document's content type. See
     * {@link #getContentType()} for details.
     * 
     * @return document content
     */
    public Object getContent();
    
    /**
     * Gets the document content's display name. For example the filename of a file or the domain of
     * a web URL.
     * 
     * @return document content's display name
     */
    public String getContentName();
    
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
    public Type getType();

    /**
     * Gets the document content type.
     * <p>
     * The following documents are defined:
     * <ul>
     * <li>{@link ContentType#FILE} value is supplied as a local File</li>
     * <li>{@link ContentType#WEB} value is supplied as URL</li>
     * <li>{@link ContentType#ACTION} value is marked for script use</li>
     * </ul>
     * 
     * @return document content type
     */
    public ContentType getContentType();

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
