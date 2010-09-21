/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.catalog.ui;

import java.io.File;
import java.net.URL;

import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.ui.internal.Messages;


/**
 * URLs are used for resolve.getId(), this class contains utility methods to help work with urls
 * used in this manner.
 * 
 * @author jgarnett
 * @since 0.9.0
 * @deprecated Please use ID
 */
public class Identifier {
	/**
	 * @deprecated Please use new ID( url ).isFile()
	 * @param url
	 * @return true if url is a file url
	 */
    public static final boolean isFile( URL url ) {
        return "file".equals(url.getProtocol()); //$NON-NLS-1$
    }

    /**
     * @deprecated Please use new ID( url ).isGraphc()
     * @param url
     * @return true if url is a graphic url
     */
    public static boolean isGraphic( URL url ) {
        // http://localhost/mapgraphic
        String HOST = url.getHost();
        String PROTOCOL = url.getProtocol();
        String PATH = url.getPath();
        if ("mapgraphic".equals(PROTOCOL)){
            return true; // we are hitting some mangled url
        }
        if (!"http".equals(PROTOCOL))return false; //$NON-NLS-1$
        if (!"localhost".equals(HOST))return false; //$NON-NLS-1$

        if (!"/mapgraphic".equals(PATH))return false; //$NON-NLS-1$

        return true;
    }

    /**
     * @deprecated Please use new ID( url ).isMemory()
     * @param url
     * @return true url identifies an in memory resource
     */
    public static boolean isMemory( URL url ) {
        String HOST = url.getHost();
        String PROTOCOL = url.getProtocol();
        String PATH = url.getPath();
        if (!"http".equals(PROTOCOL))return false; //$NON-NLS-1$
        if (!"localhost".equals(HOST))return false; //$NON-NLS-1$

        if (!"/scratch".equals(PATH))return false; //$NON-NLS-1$

        return true;
    }

    /**
     * @deprecated Please use new ID( url ).isWMS()
     * @param url
     * @return true if url refers to a web map server
     */
    public static boolean isWMS( URL url ) {
        String PATH = url.getPath();
        String QUERY = url.getQuery();
        String PROTOCOL = url.getProtocol();
        if (!"http".equals(PROTOCOL)) { //$NON-NLS-1$
            return false;
        }
        if (QUERY != null && QUERY.toUpperCase().indexOf("SERVICE=WMS") != -1) { //$NON-NLS-1$
            return true;
        } else if (PATH != null && PATH.toUpperCase().indexOf("GEOSERVER/WMS") != -1) { //$NON-NLS-1$
            return true;
        }
        return false;
    }
    /**
     * @deprecated Please use new ID( url ).isWFS()
     * @param url
     * @return true if ID refers to a web feature server
     */
    public static final boolean isWFS( URL url ) {
        String PATH = url.getPath();
        String QUERY = url.getQuery();
        String PROTOCOL = url.getProtocol();

        if (!"http".equals(PROTOCOL)) { //$NON-NLS-1$
            return false;
        }
        if (QUERY != null && QUERY.toUpperCase().indexOf("SERVICE=WFS") != -1) { //$NON-NLS-1$
            return true;
        } else if (PATH != null && PATH.toUpperCase().indexOf("GEOSERVER/WFS") != -1) { //$NON-NLS-1$
            return true;
        }
        return false;
    }
    /**
     * @deprecated Please use new ID( url ).isJDBC()
     * @param url
     * @return true if ID refers to a database (ie is a jdbc url)
     */
    public static final boolean isJDBC( URL url ) {
    	return new ID( url ).isJDBC();
//        String PROTOCOL = url.getProtocol();
//        String HOST = url.getHost();
//        return "http".equals(PROTOCOL) && HOST != null && HOST.indexOf(".jdbc") != -1; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Aquire "resource " similar to "data/a.shp" from url.
     * <p>
     * According to the followng breakdown:
     * <ul>
     * <li>http path is returned
     * <li>last entry in the file path is returned
     * <li>last two elements of the file path is returned
     * <li>jdbc table name is returned
     * <li>WFS typeName is returned
     * </ul>
     * </p>
     * <p>
     * Here are some examples:
     * <ul>
     * <li>file:///C:/java/workspace/shapefiles/a.shp becomes "a.shp"
     * <li>http://www.refractions.net:8080/geoserver/wfs?REQUEST=GetCapabilities&SERVICE=WFS&type=topp:ROAD
     * becomes "topp:ROAD"
     * <li>http://www.refractions.net:8080/data/a.shp becomes "data/a.shp"
     * <li>ftp://ftp.refractions.net/data/a.shp becomes "data/a.shp"
     * <li>http://kraken.postgis.jdbc:5432/production.... becomes road
     * </ul>
     * <li>
     * @deprecated Please use new ID( url ).labelResource()
     * @param url
     * @return label describing the URL as a resource (ie file or content)
     */
    final static public String labelResource( URL url ) {
        if (url == null)
            return Messages.ResolveLabelProvider_missingText; 

        String HOST = url.getHost();
        String QUERY = url.getQuery();
        String PATH = url.getPath();
        String PROTOCOL = url.getProtocol();
        String REF = url.getRef();

        if (REF != null) {
            return REF;
        }
        if (PROTOCOL == null) {
            return ""; // we do not represent a server (local host does not cut it) //$NON-NLS-1$
        }
        StringBuffer label = new StringBuffer();
        if ("file".equals(PROTOCOL)) { //$NON-NLS-1$
            int split = PATH.lastIndexOf('/');
            if (split == -1) {
                label.append(PATH);
            } else {
                String file = PATH.substring(split + 1);
                int dot = file.lastIndexOf('.');
                if (dot != -1) {
                    file = file.substring(0, dot);
                }
                label.append(file);
            }
        } else if ("http".equals(PROTOCOL) && HOST.indexOf(".jdbc") != -1) { //$NON-NLS-1$ //$NON-NLS-2$
            if (QUERY != null) {
                label.append(QUERY);
            } else {
                label.append(PATH);
            }
        } else if ("http".equals(PROTOCOL)) { //$NON-NLS-1$
            if (QUERY != null && QUERY.toUpperCase().indexOf("SERVICE=WFS") != -1) { //$NON-NLS-1$
                for( String split : QUERY.split("&") ) { //$NON-NLS-1$
                    if (split.toLowerCase().startsWith("type=")) { //$NON-NLS-1$
                        label.append(split.substring(5));
                    }
                }
            } else if (QUERY != null && QUERY.toUpperCase().indexOf("SERVICE=WMS") != -1) { //$NON-NLS-1$
                for( String split : QUERY.split("&") ) { //$NON-NLS-1$
                    if (split.startsWith("LAYER=")) { //$NON-NLS-1$
                        label.append(split.substring(6));
                    }
                }
            } else {
                int split = PATH.lastIndexOf('/');
                if (split == -1) {
                    label.append(PATH);
                } else {
                    label.append(PATH.substring(split + 1));
                }
            }
        } else {
            int split = PATH.lastIndexOf('/');
            if (split == -1) {
                label.append(PATH);
            } else {
                label.append(PATH.substring(split + 1));
            }
        }
        return label.toString();
    }

    /**
     * Aquire "server" similar to "protocol://host:port" from url.
     * <p>
     * According to the followng breakdown:
     * <ul>
     * <li>Protocol http and file is ignored (or assumed)
     * <li>File is reduced to HOST and last path entry
     * <li>jdbc stuff is encoded as *majic* see, this method sorts it out
     * <li>default ports are not displayed
     * <li>service=WFS and service=WMS are *magic* as well
     * </ul>
     * </p>
     * <p>
     * Here are some examples:
     * <ul>
     * <li>file:///C:/java/workspace/shapefiles/a.shp becomes "shapefiles/a.shp"
     * <li>http://www.refractions.net:8080/geoserver/wfs?REQUEST=GetCapabilities&SERVICE=WFS
     * becomes "wfs://www.refractions.net:8080"
     * <li>http://www.refractions.net:8080/data/a.shp becomes "www.refractions.net:8080"
     * <li>ftp://ftp.refractions.net/data/a.shp becomes "ftp://ftp.refractions.net"
     * <li>http://kraken.postgis.jdbc:5432/production becomes postgis://kraken:5432
     * </ul>
     * <li>
     * @deprecated Please use new ID( url ).labelServer()
     * @param server
     * @return label as if url points to just a server
     */
    final static public String labelServer( URL url ) {
        if (url == null)
            return Messages.ResolveLabelProvider_missingText; 

        String HOST = url.getHost();
        int PORT = url.getPort();
        String PATH = url.getPath();
        String PROTOCOL = url.getProtocol();

        if (PROTOCOL == null) {
            return ""; // we do not represent a server (local host does not cut it) //$NON-NLS-1$
        }
        StringBuffer label = new StringBuffer();
        if (isFile(url)) {
            String split[] = PATH.split("\\/"); //$NON-NLS-1$

            if (split.length == 0) {
                label.append(File.separatorChar);
            } else {
                if (split.length < 2) {
                    label.append(File.separatorChar);
                    label.append(split[0]);
                    label.append(File.separatorChar);
                } else {
                    label.append(split[split.length - 2]);
                    label.append(File.separatorChar);
                }
                label.append(split[split.length - 1]);
            }
        } else if (isJDBC(url)) {
            int split2 = HOST.lastIndexOf('.');
            int split1 = HOST.lastIndexOf('.', split2 - 1);
            label.append(HOST.substring(split1 + 1, split2));
            label.append("://"); //$NON-NLS-1$
            label.append(HOST.subSequence(0, split1));
        } else if ("http".equals(PROTOCOL) || "https".equals(PROTOCOL)) { //$NON-NLS-1$ //$NON-NLS-2$
            if (isWMS(url)) {
                label.append("wms://"); //$NON-NLS-1$
            } else if (isWFS(url)) {
                label.append("wfs://"); //$NON-NLS-1$
            }
            label.append(HOST);
        } else {
            label.append(PROTOCOL);
            label.append("://"); //$NON-NLS-1$
            label.append(HOST);
        }
        if (PORT != -1) {
            label.append(":"); //$NON-NLS-1$
            label.append(PORT);
        }
        return label.toString();
    }
}
