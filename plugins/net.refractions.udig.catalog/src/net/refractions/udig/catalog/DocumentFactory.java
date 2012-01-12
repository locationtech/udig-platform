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
package net.refractions.udig.catalog;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.geotools.data.DataUtilities;

/**
 * Creates an IDocument
 * 
 * @author paul.pfeiffer
 * @version 1.3.1
 */
public class DocumentFactory {

    public IDocument create(Object source) {
        if (source instanceof File) {
            File sourceFile = (File) source;
            return new FileDocument(sourceFile);
        }
        if (source instanceof URL) {
            URL sourceURL = (URL) source;
            return new URLDocument(sourceURL);
        }
        if (source instanceof String) {
            String sourceString = source.toString();
            try {
                URL url = new URL(sourceString);
                if (url.getProtocol().equals("file")) {
                    File file = DataUtilities.urlToFile(url);
                    return new FileDocument(file);
                }
                return new URLDocument(url);
            } catch (MalformedURLException e) {
            }
            File file = new File(sourceString);
            return new FileDocument(file);
        }
        return null;
    }
}
