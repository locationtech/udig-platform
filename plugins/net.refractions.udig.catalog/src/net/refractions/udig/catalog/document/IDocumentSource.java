/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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

import java.util.List;

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
public interface IDocumentSource extends IAbstractDocumentSource {

    /**
     * Gets the list of documents associated with this feature type.
     * <p>
     * As an example this will return a SHAPEFILENAME.TXT file that is associated (ie a sidecar
     * file) with the provided shapefile. We may also wish to list a README.txt file in the same
     * directory (as it is the habbit of GIS professionals to record fun information about the
     * entire dataset.
     * </p>
     * 
     * @return documents
     */
    public List<IDocument> getDocuments();

    /**
     * Adds the document.
     * 
     * @param info
     * @return added document
     */
    public IDocument add(DocumentInfo info);

    /**
     * Adds the list of documents.
     * 
     * @param infos
     * @return list of new added documents
     */
    public List<IDocument> add(List<DocumentInfo> infos);

    /**
     * Removes the document.
     * 
     * @param true if removed successfully, otherwise false
     */
    public boolean remove(IDocument doc);

    /**
     * Removes the list of documents.
     * 
     * @param true if removed successfully, otherwise false
     */
    public boolean remove(List<IDocument> docs);

    /**
     * Updates the document with the information.
     * 
     * @param doc
     * @param info
     * @return updated document
     */
    public IDocument update(IDocument doc, DocumentInfo info);

    /**
     * Document info container for document. This was initially designed to contain document
     * properties retrieved from the property file but can also be reused and/or extended where
     * possible. Attachment information container. This is designed to be able to parse (
     * {@link DocumentInfo#fromString(String)} ) and format ({@link DocumentInfo#toString()}) the
     * info string to and/or from the resource property file.
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
        private IDocument.Type type;

        /**
         * Flag for templates.
         */
        private boolean isTemplate;
        
        public static final String DELIMITER = "|~|"; //$NON-NLS-1$
        public static final String DELIMITER_REGEX = "\\|~\\|"; //$NON-NLS-1$

        public DocumentInfo(String attachmentInfo) {
            fromString(attachmentInfo);
        }

        public DocumentInfo(String label, String description, String info, Type type,
                boolean isTemplate) {
            this.label = label;
            this.description = description;
            this.info = info;
            this.type = type;
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

        public IDocument.Type getType() {
            return type;
        }

        public void setType(IDocument.Type type) {
            this.type = type;
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
            if (defValues.length > 4) {
                isTemplate = Boolean.parseBoolean(defValues[4]);
            } else {
                isTemplate = false;
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
            sb.append(DELIMITER);
            sb.append(Boolean.toString(isTemplate));
            return sb.toString();
        }
        
    }
    
}
