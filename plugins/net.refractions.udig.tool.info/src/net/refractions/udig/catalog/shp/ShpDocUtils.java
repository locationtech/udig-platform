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
package net.refractions.udig.catalog.shp;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

/**
 * Utility methods for shape document processing related classes.
 * 
 * @author Naz Chan
 * 
 */
public final class ShpDocUtils {

    /**
     * Gets the absolute path with the url as the parent path and the relative path as the child
     * path.
     * 
     * @param url
     * @param relativePath
     * @return absolute path
     */
    public static String getAbsolutePath(URL url, String relativePath) {
        if (relativePath != null && relativePath.trim().length() > 0) {
            try {
                final File parentFile = new File(new URI(url.toString()));
                final File childFile = new File(parentFile.getParent(), relativePath);
                return childFile.getAbsolutePath();
            } catch (URISyntaxException e) {
                // Should not happen as shapefile should be a valid file
                e.printStackTrace();
            }            
        }
        return null;
    }
    
    /**
     * Gets the relative path with the url as the parent path and the absolute path as the child
     * path.
     * 
     * @param url
     * @param absolutePath
     * @return relative path
     */
    public static String getRelativePath(URL url, String absolutePath) {
        if (absolutePath != null && absolutePath.trim().length() > 0) {
            try {
                final File parentFile = new File(new URI(url.toString()));
                final File parentDir = parentFile.getParentFile();
                final File childFile = new File(absolutePath);
                return parentDir.toURI().relativize(childFile.toURI()).getPath();
            } catch (URISyntaxException e) {
                // Should not happen as shapefile should be a valid file
                e.printStackTrace();
            }
        }
        return null;
    }
    
    /**
     * Deletes a file from the file system.
     * 
     * @param fileObj
     * @return true if successful, otherwise false
     */
    public static boolean deleteFile(Object fileObj) {
        if (fileObj instanceof File) {
            return deleteFile((File) fileObj);
        }
        return false;
    }
    
    /**
     * Deletes a file from the file system.
     * 
     * @param file
     * @return true if successful, otherwise false
     */
    public static boolean deleteFile(File file) {
        if (file != null) {
            if (file.exists()) {
                return file.delete();    
            }
        }
        return false;
    }
    
    /**
     * Copies a read-only copy of the old file into new file directory.
     * 
     * @param oldFilePath
     * @param newFileDir
     * @return new file
     */
    public static File copyFile(String oldFilePath, File newFileDir) {
        return copyFile(new File(oldFilePath), newFileDir);
    }
    
    /**
     * Copies a read-only copy of the old file into new file directory.
     * 
     * @param oldFile
     * @param newFileDir
     * @return new file
     */
    public static File copyFile(File oldFile, File newFileDir) {
        try {
            if (!newFileDir.exists()) {
                newFileDir.mkdir();
            }
            final File newFile = new File(newFileDir, oldFile.getName());
            if (!newFile.exists()) {
                FileUtils.copyFileToDirectory(oldFile, newFileDir);
                newFile.setReadOnly();
            }
            return newFile;
        } catch (IOException e) {
            // Should not happen
            e.printStackTrace();
        }
        return null;
    }
    
}
