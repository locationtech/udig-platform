/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package net.refractions.udig.catalog.document;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import net.refractions.udig.catalog.document.IDocument.ContentType;
import net.refractions.udig.catalog.document.IDocument.Type;

/**
 * Access to documents associated with a resource.
 * <p>
 * The files made available are dependent on the implementation - some possible examples are:
 * <ul>
 * <li>Support files associated with an IGeoResource</li>
 * <li>Additional sidecar files associated with a shapefile</li>
 * </ul>
 * 
 * @author Naz Chan
 * @since 1.3.2
 */
public interface IDocumentSource extends IAbstractAttachmentSource {

    /**
     * Gets the list of documents.
     * <p>
     * As an example this will return a SHAPEFILENAME.TXT file that is associated (ie a sidecar
     * file) with the provided shapefile. We may also wish to list a README.txt file in the same
     * directory (as it is the habbit of GIS professionals to record fun information about the
     * entire dataset.
     * </p>
     * This method may require server-side processing and should not be ran in the UI thread.
     * </p>
     * 
     * @param monitor
     * @return documents
     */
    public List<IDocument> getDocuments(IProgressMonitor monitor);
    
    /**
     * Adds a document.
     * </p>
     * This method may require server-side processing and should not be ran in the UI thread.
     * </p>
     * 
     * @param info
     * @param monitor
     * @return added document
     */
    public IDocument add(DocumentInfo info, IProgressMonitor monitor);

    /**
     * Adds a list of documents.
     * </p>
     * This method may require server-side processing and should not be ran in the UI thread.
     * </p>
     * 
     * @param infos
     * @param monitor
     * @return list added documents
     */
    public List<IDocument> add(List<DocumentInfo> infos, IProgressMonitor monitor);
    
    /**
     * Updates the document with the information.
     * </p>
     * This method may require server-side processing and should not be ran in the UI thread.
     * </p>
     * 
     * @param doc
     * @param info
     * @param monitor
     * @return true if successful, otherwise false
     */
    public boolean update(IDocument doc, DocumentInfo info, IProgressMonitor monitor);
    
    /**
     * Removes the document.
     * </p>
     * This method may require server-side processing and should not be ran in the UI thread.
     * </p>
     * 
     * @param doc
     * @param monitor
     * @param true if successful, otherwise false
     */
    public boolean remove(IDocument doc, IProgressMonitor monitor);

    /**
     * Removes the list of documents.
     * </p>
     * This method may require server-side processing and should not be ran in the UI thread.
     * </p>
     * 
     * @param docs
     * @param monitor
     * @param true if successful, otherwise false
     */
    public boolean remove(List<IDocument> docs, IProgressMonitor monitor);

    /**
     * Document info container for document. This was initially designed to contain document
     * properties retrieved from the property file but can also be reused and/or extended where
     * possible. Attachment information container. This is designed to be able to parse (
     * {@link DocumentInfo#fromString(String)} ) and format ({@link DocumentInfo#toString()}) the
     * info string to and/or from the resource property file.
     * <p>
     * <b>String format:</b>
     * <p>
     * "[info] DELIMITER [type] DELIMITER [contentType] DELIMITER [label] DELIMITER [description] DELIMITER [isTemplate]"
     */
    public class DocumentInfo {

        /**
         * Document label
         */
        private String label;

        /**
         * Document description
         */
        private String description;

        /**
         * Document info - file or url metadata
         */
        private String info;
        
        /**
         * Document type
         */
        private Type type;
        
        /**
         * Document content type
         */
        private ContentType contentType;

        /**
         * Flag for templates.
         */
        private boolean isTemplate;
        
        public static final String DELIMITER = "|~|"; //$NON-NLS-1$
        public static final String DELIMITER_REGEX = "\\|~\\|"; //$NON-NLS-1$

        public DocumentInfo(String attachmentInfo) {
            fromString(attachmentInfo);
        }

        public DocumentInfo(String label, String description, String info, ContentType contentType,
                boolean isTemplate, Type type) {
            this.label = label;
            this.description = description;
            this.info = info;
            this.type = type;
            this.contentType = contentType;
            this.isTemplate = isTemplate;
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

        public Type getType() {
            return type;
        }
        
        public void setType(Type type) {
            this.type = type;
        }
        
        public IDocument.ContentType getContentType() {
            return contentType;
        }

        public void setContentType(IDocument.ContentType contentType) {
            this.contentType = contentType;
        }
        
        public boolean isTemplate() {
            return isTemplate;
        }

        public void setTemplate(boolean isTemplate) {
            this.isTemplate = isTemplate;
        }

        private void fromString(String attachmentInfo) {
            final String[] defValues = attachmentInfo.split(DELIMITER_REGEX);
            info = getCleanValue(defValues[0]); 
            type = Type.valueOf(defValues[1]);
            contentType = ContentType.valueOf(defValues[2]);
            if (defValues.length > 2) {
                label = getCleanValue(defValues[3]);
            } else {
                label = null;
            }
            if (defValues.length > 3) {
                description = getCleanValue(defValues[4]);
            } else {
                description = null;
            }
            if (defValues.length > 4) {
                isTemplate = Boolean.parseBoolean(defValues[5]);
            } else {
                isTemplate = false;
            }
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
            if (contentType != null) {
                sb.append(contentType);
            }
            sb.append(DELIMITER);
            if (label != null) {
                sb.append(label);
            }
            sb.append(DELIMITER);
            if (description != null) {
                sb.append(description);
            }
            sb.append(DELIMITER);
            sb.append(Boolean.toString(isTemplate));
            return sb.toString();
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
        
    }
    
}
