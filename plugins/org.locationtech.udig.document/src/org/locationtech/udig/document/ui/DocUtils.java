/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.document.ui;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.locationtech.udig.catalog.document.IDocument;
import org.locationtech.udig.catalog.document.IDocument.ContentType;

/**
 * Utility methods for {@link DocumentView}, {@link DocumentDialog} and other related UI classes.
 * 
 * @author Naz Chan
 */
public final class DocUtils {

    private static final String UNASSIGNED = "Unassigned"; //$NON-NLS-1$
    private static final String DOCUMENT_FORMAT = "%s (%s)"; //$NON-NLS-1$
    private static final String LABEL_DESC_FORMAT = "%s - %s"; //$NON-NLS-1$
    
    private static final String NEW_FILE_FORMAT = "New File.%s"; //$NON-NLS-1$
    private static final String SAVE_AS_FORMAT = "%s-Copy.%s"; //$NON-NLS-1$
    private static final String FILENAME_FORMAT = "%s.%s"; //$NON-NLS-1$
    
    /**
     * Gets the document string label.
     * 
     * @param doc
     * @return document label
     */
    public static String getDocStr(IDocument doc) {
        String docInfoStr = doc.getContentName();
        if (docInfoStr == null || docInfoStr.isEmpty()) {
            docInfoStr = UNASSIGNED;
        }
        return getDocStr(docInfoStr, doc.getLabel());
    }

    /**
     * Gets the document string label.
     * 
     * @param type
     * @param info
     * @param label
     * @return document label
     */
    public static String getDocStr(ContentType type, String info, String label) {
        final String docInfoStr = getDocInfoStr(type, info);
        return getDocStr(docInfoStr, label);
    }

    /**
     * Gets the document string label
     * 
     * @param docInfoStr
     * @param label
     * @return document label
     */
    private static String getDocStr(String docInfoStr, String label) {
        if (label == null || label.length() == 0) {
            return docInfoStr;
        } else {
            return String.format(DOCUMENT_FORMAT, docInfoStr, label);
        }
    }

    /**
     * Gets the document info string
     * 
     * @param type
     * @param infoStr
     * @return document info string
     */
    private static String getDocInfoStr(ContentType type, String infoStr) {
        String infoDisplayValue = UNASSIGNED;
        if (infoStr != null) {
            switch (type) {
            case FILE:
                infoDisplayValue = getFileDocInfoStr(infoStr);
                break;
            case WEB:
                infoDisplayValue = getWebDocInfoStr(infoStr);
                break;
            case ACTION:
                infoDisplayValue = infoStr;
                break;
            default:
                break;
            }
        }
        return infoDisplayValue;
    }

    /**
     * Gets the document info string
     * 
     * @param type
     * @param infoValue
     * @return document info string
     */
    private static String getDocInfoStr(ContentType type, Object infoValue) {
        String infoDisplayValue = UNASSIGNED;
        if (infoValue != null) {
            switch (type) {
            case FILE:
                infoDisplayValue = getFileDocInfoStr((File) infoValue);
                break;
            case WEB:
                infoDisplayValue = getWebDocInfoStr((URL) infoValue);
                break;
            case ACTION:
                infoDisplayValue = infoValue.toString();
                break;
            default:
                break;
            }
        }
        return infoDisplayValue;
    }

    /**
     * Gets the document info string
     * 
     * @param urlPath
     * @return document info string
     */
    private static String getWebDocInfoStr(String urlPath) {
        return urlPath;
    }

    /**
     * Gets the document info string
     * 
     * @param url
     * @return document info string
     */
    private static String getWebDocInfoStr(URL url) {
        return url.toString();
    }

    /**
     * Gets the document info string
     * 
     * @param filePath
     * @return document info string
     */
    private static String getFileDocInfoStr(String filePath) {
        final File file = new File(filePath);
        if (file.exists()) {
            return getFileDocInfoStr(file);
        }
        return null;
    }

    /**
     * Gets the document info string
     * 
     * @param file
     * @return document info string
     */
    private static String getFileDocInfoStr(File file) {
        return file.getName();
    }

    /**
     * Converts the string to camel case.
     * 
     * @param text
     * @return string in camel case
     */
    public static String toCamelCase(String text) {
        int count = 0;
        final StringBuilder sb = new StringBuilder();
        final String[] words = text.replace('_', ' ').split(" "); //$NON-NLS-1$
        for (String word : words) {
            count++;
            if (word.length() == 1) {
                sb.append(word.toUpperCase());
            } else if (word.length() > 1) {
                sb.append(word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase());
            }
            if (count < words.length) {
                sb.append(" "); //$NON-NLS-1$
            }
        }
        return sb.toString();
    }

    /**
     * Gets the formatted label and description display string.
     * 
     * @param label
     * @param description
     * @return formatted label and description display string
     */
    public static String getLabelAndDescDisplay(String label, String description) {
        if (description != null && description.trim().length() > 0) {
            return String.format(LABEL_DESC_FORMAT, label, description);
        }
        return label;
    }
    
    /**
     * Gets the default filename when saving as another file.
     * 
     * @param file
     * @return default filename
     */
    public static String getSaveAsFilename(File file) {
        final Map<String, String> parts = getNameParts(file);
        return String.format(SAVE_AS_FORMAT, parts.get(F_NAME), parts.get(F_EXT));
    }

    /**
     * Gets a clean filename. This adds an extension from the old file if an extension is not
     * supplied in the new file path.
     * 
     * @param newfilePath
     * @param oldFile
     * @return clean filename
     */
    public static String cleanFilename(String newfilePath, File oldFile) {
        final File newFile = new File(newfilePath);
        final Map<String, String> oldParts = getNameParts(oldFile);
        final Map<String, String> newParts = getNameParts(newFile);
        if (newParts.get(F_EXT) == null) {
            final File newFileDir = newFile.getParentFile();
            final String cleanFilename = String.format(FILENAME_FORMAT, newParts.get(F_NAME),
                    oldParts.get(F_EXT));
            final File cleanNewFile = new File(newFileDir, cleanFilename);
            return cleanNewFile.getAbsolutePath();
        }
        return newfilePath;
    }
    
    /**
     * Gets the default filename when creating a new file from a template.
     * 
     * @param templateFile
     * @param templateProps
     * @return filename
     */
    public static String getFromTemplateFilename(File templateFile, Properties templateProps) {
        final Map<String, String> parts = getNameParts(templateFile);
        return String.format(NEW_FILE_FORMAT, getFromTemplateExt(parts, templateProps));
    }

    /**
     * Gets a clean filename for a file created from template.
     * @param newfilePath
     * @param templateFile
     * @param templateProps
     * @return clean filename
     */ 
    public static String cleanFromTemplateFilename(String newfilePath, File templateFile,
            Properties templateProps) {
        final Map<String, String> parts = getNameParts(templateFile);
        final File tempTemplateFile = new File(String.format(FILENAME_FORMAT, parts.get(F_NAME),
                getFromTemplateExt(parts, templateProps)));
        return cleanFilename(newfilePath, tempTemplateFile);
    }

    /**
     * Gets the correct file extension for a new file created from a template. This return the
     * template's extension if there is no entry in the properties file for the template's
     * extension.
     * 
     * @param parts
     * @param templateProps
     * @return file extension
     */
    private static String getFromTemplateExt(Map<String, String> parts, Properties templateProps) {
        // To normalise with what is used by the properties file
        final String templateExt = parts.get(F_EXT).toLowerCase();
        final String fileExt = templateProps.getProperty(templateExt, templateExt);
        return fileExt;
    }
    
    private static final String F_NAME = "F_NAME"; //$NON-NLS-1$
    private static final String F_EXT = "F_EXT"; //$NON-NLS-1$
    
    /**
     * Gets the name and extension from a filename and stores them in a map.
     * 
     * @param file
     * @return filename parts map
     */
    private static Map<String, String> getNameParts(File file) {
        final String filename = file.getName();
        return getNameParts(filename);
    }
    
    /**
     * Gets the name and extension from a filename and stores them in a map.
     * 
     * @param filename
     * @return filename parts map
     */
    private static Map<String, String> getNameParts(String filename) {
        final Map<String, String> parts = new HashMap<String, String>();
        final int index = filename.lastIndexOf('.');
        if (index == -1) {
            parts.put(F_NAME, filename);
        } else {
            parts.put(F_NAME, filename.substring(0, index));
            parts.put(F_EXT, filename.substring(index + 1));    
        }
        return parts;
    }
    
    /**
     * Gets the name part of the filename.
     * 
     * @param file
     * @return name part of filename
     */
    public static String getName(File file) {
        return getNameParts(file).get(F_NAME);
    }
    
    /**
     * Gets the extension part of the filename.
     * 
     * @param file
     * @return extension part of the filename
     */
    public static String getExtension(File file) {
        return getNameParts(file).get(F_EXT);
    }
    
}
