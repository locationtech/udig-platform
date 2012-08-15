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
package net.refractions.udig.document;

import java.io.File;
import java.net.URL;

import net.refractions.udig.catalog.document.IDocument;
import net.refractions.udig.catalog.document.IDocument.Type;

/**
 * Utility methods for {@link DocumentView} and related UI classes.
 * 
 * @author Naz Chan
 */
public final class DocUtils {

    private static final String DOCUMENT_FORMAT = "%s (%s)"; //$NON-NLS-1$

    /**
     * Gets the document string label.
     * 
     * @param doc
     * @return document label
     */
    public static String getDocStr(IDocument doc) {
        final String docInfoStr = getDocInfoStr(doc.getType(), doc.getValue());
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
    public static String getDocStr(Type type, String info, String label) {
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
    private static String getDocInfoStr(Type type, String infoStr) {
        String infoDisplayValue = "Unassigned"; //$NON-NLS-1$
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
    private static String getDocInfoStr(Type type, Object infoValue) {
        String infoDisplayValue = "Unassigned"; //$NON-NLS-1$
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
        final String[] words = text.split(" "); //$NON-NLS-1$
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

}
