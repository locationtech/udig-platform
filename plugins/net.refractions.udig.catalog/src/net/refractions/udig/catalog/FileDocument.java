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
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.geotools.data.DataUtilities;

/**
 * Simple document built around a file.
 * 
 * @author paul.pfeiffer
 * @version 1.3.0
 */
public class FileDocument extends AbstractDocument {

    private File file;

    /**
     * @param f
     */
    public FileDocument( File f ) {
        file = f;
    }

    /*
     * (non-Javadoc)
     * @see net.refractions.udig.catalog.IDocument#getName()
     */
    @Override
    public String getName() {
        return file.getName();
    }

    /*
     * (non-Javadoc)
     * @see net.refractions.udig.catalog.IDocument#getDescription()
     */
    @Override
    public String getDescription() {
        long modified = file.lastModified();
        Date d = new Date(modified);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        String dateString = sdf.format(d);

        return file.getName() + " modified on " + dateString;
    }

    /*
     * (non-Javadoc)
     * @see net.refractions.udig.catalog.IDocument#getURI()
     */
    @Override
    public URI getURI() {
        return file.toURI();
    }

    /*
     * (non-Javadoc)
     * @see net.refractions.udig.catalog.IDocument#open()
     */
    @Override
    public void open() {
        try {
            java.awt.Desktop.getDesktop().open(file);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public String getReferences() {
        return DataUtilities.fileToURL(file).toExternalForm();
    }

}
