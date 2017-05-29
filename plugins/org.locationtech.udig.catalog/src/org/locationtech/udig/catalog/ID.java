/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2008-2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.catalog;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.geotools.data.DataUtilities;

import org.locationtech.udig.core.internal.CorePlugin;

/**
 * Identifier used to lookup entries in an local IRespository or remote ISearch.
 * <p>
 * While an identifier is often defined by URL or URI this class has constructors to help remove any
 * possibility ambiguity. These objects are considered immutable and are very careful to have a fast
 * hashCode function etc...
 * </p>
 * 
 * @author Jody Garnett
 * @since 1.2
 */
public class ID implements Serializable {
    /** long serialVersionUID field */
    private static final long serialVersionUID = 5858146620416500314L;

    private String id;
    private File file;
    private URL url;
    private URI uri;

    /**
     * Used to distinguish type of connection represented
     */
    private String typeQualifier;

    public ID( String txt, String qualifier ) {
        this.id = txt;
        try {
            this.url = new URL(null, txt, CorePlugin.RELAXED_HANDLER);
        } catch (MalformedURLException e) {
        }
        try {
            this.uri = url.toURI();
        } catch (URISyntaxException e) {
        }
        this.file = DataUtilities.urlToFile(url);
        this.typeQualifier = qualifier;
    }
    /**
     * Create an identifier for the provided File.
     * <p>
     * The file will be used to determine id, url and uri.
     * 
     * @param file File
     * @param qualifier Often used to indicate format
     */
    public ID( File file, String qualifier ) {
        this.typeQualifier = qualifier;
        this.file = file;
        try {
            this.id = file.getCanonicalPath();
        } catch (IOException e) {
        }
        this.uri = file.toURI();
        if (id == null) {
            id = uri.toString();
        }
        try {
            this.url = uri.toURL();
            if (id == null) {
                id = url.toString();
            }
        } catch (MalformedURLException e) {
        }
    }

    /**
     * Fully defined id. this shouldn't normally be required (but is useful for test cases)
     */
    public ID( String id, URL url, File file, URI uri, String qualifier ) {
        this.id = id;
        this.url = url;
        this.uri = uri;
        this.file = file;
        this.typeQualifier = qualifier;
    }

    public ID( URL url ) {
        this(url, null);
    }
    /**
     * Create an identifier for the provided URL.
     * <p>
     * The URL will be used to determine id, file, and uri.
     * 
     * @param url URL
     * @param qualifier Often used to indicate protocol like "wms", or "wfs"
     */
    public ID( URL url, String qualifier ) {
        this.typeQualifier = qualifier;
        this.url = url;
        try {
            this.uri = url.toURI();
        } catch (URISyntaxException e) {
        }
        file = DataUtilities.urlToFile(url);
        if (file != null) {
			if (uri != null && uri.isAbsolute()
					&& "file".equals(uri.getScheme())) { //$NON-NLS-1$
				try {
					String fragment = uri.getFragment();
                    if (fragment != null) {
						// a fragment means the service is the file. so 
						// canonicalize the file for id or we will have problems
						// comparing (for example symbolic links)
                        String canonicalPath = file.getCanonicalPath();
						File canonicalFile = file.getCanonicalFile();
						canonicalFile = new File(canonicalPath.substring(0, canonicalPath.length() - fragment.length() - 1));
						URI canonicalURI = canonicalFile.toURI();
						this.uri = new URI(canonicalURI.toASCIIString()+"#"+uri.getRawFragment());
						
						// this results in "re"encoding messing up use of spaces
//						this.uri = new URI(canonicalURI.getScheme(),
//								canonicalURI.getRawSchemeSpecificPart(),
//								this.uri.getFragment());
						this.url = uri.toURL();
				        id = canonicalPath;
				        this.file = canonicalFile;
						//file = null;
//                        try {
//                            file = new File(uri);
//                        } catch (Throwable t) {
//                            System.err.println("Unable to determine file for:" + uri);
//                            // t.printStackTrace();
//                            file = DataUtilities.urlToFile(url);
//                        }
					} else {
					    //file = new File(uri);
					}
				} catch (Throwable t) {
					//file = null;
					System.err.println("Trouble matching file for:"+ uri );
					//file = DataUtilities.urlToFile(url);
				}
			}
		}
        if (id == null && file != null) {
            try {
                String canonicalPath = file.getCanonicalPath();
                id = canonicalPath;
            } catch (IOException e) {
            }
        }
        if (id == null) {
            if (uri != null) {
                id = uri.toString();
            } else {
                id = url.toString();
            }
        }
    }

    public ID( URI uri, String qualifier ) throws IOException {
        this(uri);
        this.typeQualifier = qualifier;
    }
    public ID( URI uri ) throws IOException {
        this.uri = uri;
        if (uri.isAbsolute()) {
            try {
                url = uri.toURL();
            } catch (MalformedURLException noProtocol) {
                url = new URL(null, url.toExternalForm(), CorePlugin.RELAXED_HANDLER);
            }
        }
        if (uri.isAbsolute() && "file".equals(uri.getScheme())) { //$NON-NLS-1$
            // Do you need to consider fragments as with the ID(URL) constructor?
            file = new File(uri);
        } else {
            file = null; // not a file?
        }
        if (file != null) {
            id = file.getCanonicalPath();
        } else if (uri != null) {
            id = uri.toString();
        }
    }

    /**
     * Constructs an ID that can be used to identify a child of the object identified by this id The
     * algorithm for creating a sub child id is:
     * <ol>
     * <li>if the url of this does not have an REF part then the extension will be added to the url
     * as the REF</li>
     * <li>if the url of this <strong>does</strong> have an fragment part then the extension will be
     * added to the end of the url REF with a / at the start</li>
     * </ol>
     * Examples: child "c" of http://xyz.com would get id http://xyz.com#c child "gc" of
     * file://xyz.com#c would get id http://xyz.com#c/gc Type qualifiers and other information are
     * also copied to the child
     * 
     * @param child an identifier for the child within the parent object
     */
    public ID( ID parent, String child ) {
        String extension;

        if (parent.id.contains("#")) { //$NON-NLS-1$
            extension = "/" + child; //$NON-NLS-1$
        } else {
            extension = "#" + child; //$NON-NLS-1$
        }
        this.id = parent.id + extension;
        try {
            this.url = new URL(null, parent.toURL().toString() + extension,
                    CorePlugin.RELAXED_HANDLER);
        } catch (MalformedURLException e1) {
        }
        try {
            this.uri = new URI(parent.uri.toString() + extension);
        } catch (URISyntaxException e) {
        }
        this.file = parent.file;
        typeQualifier = parent.getTypeQualifier();
    }

    public String getTypeQualifier() {
        return typeQualifier;
    }

    public File toFile() {
        return file;
    }
    /**
     * Create a file with the provided extension.
     * <p>
     * Example" id.toFile("properties") will produce "foo.properties" for the shapefile "foo.shp"
     * 
     * @param extension
     * @return
     */
    public File toFile( String extension ) {
        if( extension == null ){
            return file;
        }
        File parent = file.getParentFile();
        String baseFile = toBaseFile();
        
        File target = new File( parent, baseFile+"."+extension); //$NON-NLS-1$
        return target;
    }

    public String toExtension(){
        String name;
        try {
            name = uri.toURL().getFile();
        } catch (MalformedURLException e) {
            name = url.getFile();
        }
        int slash = name.lastIndexOf('/');
        int dot = name.lastIndexOf('.');
        int beginIndex = (slash == -1 && slash < name.length() - 1 ? 0 : slash) + 1;
        if( dot == -1 || dot < beginIndex || dot == name.length()-1 ){
            return ""; // no extension
        }
        else {
            return name.substring(dot+1);
        }
    }
    public String toBaseFile() {
        String name;
        try {
            name = uri.toURL().getFile();
        } catch (MalformedURLException e) {
            name = url.getFile();
        }
        int slash = name.lastIndexOf('/');
        int dot = name.lastIndexOf('.');
        int beginIndex = (slash == -1 && slash < name.length() - 1 ? 0 : slash) + 1;
        int endIndex = dot == -1 || dot < beginIndex ? name.length() : dot;
        return name.substring(beginIndex, endIndex);
        /*
         * String name = file.getName(); int split = name.lastIndexOf('.'); if( split == -1 ){
         * return name; } else { return name.substring(0,split); }
         */
    }

    public URL toURL() {
        return url;
    }

    public URI toURI() {
        return uri;
    }
    public String toString() {
        return id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((typeQualifier == null) ? 0 : typeQualifier.hashCode());
        return result;
    }
    
    public boolean equals( ID other, boolean stripChildRef ){
        if( stripChildRef ){
            String strip1 = stripChildRef();
            String strip2 = other.stripChildRef();
            return strip1.equals( strip2 );
        }
        else {
            return equals( id );
        }
    }
    private String stripChildRef() {
        int splitHash = id.indexOf("#"); // search for first #
        if( splitHash != -1 ){
            int splitSlash = id.indexOf("/", splitHash );
            if( splitSlash != -1 ){
                return id.substring(0, splitSlash );
            }
            else {
                return id.substring(0,splitHash);
            }
        }
        return id;
    }
    
    @Override
    public boolean equals( Object obj ) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ID other = (ID) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (typeQualifier == null) {
            if (other.typeQualifier != null)
                return false;
        } else if (!typeQualifier.equals(other.typeQualifier))
            return false;
        return true;
    }

    //
    // URL Handling
    //
    /**
     * Produce a relative URL for the provided file baseDirectory; if the ID is not a file URL (and
     * not contained by the baseDirectory) it will be returned as provided by toURL().
     * 
     * @see URLUtils.toRelativePath
     * @param baseDirectory
     * @return relative file url if possible; or the same as toURL()
     */
    public URL toURL( File baseDirectory ) {
        return URLUtils.toRelativePath(baseDirectory, toURL());
    }

    public boolean isLocal(){
        return id.contains("localhost");
    }
    
    /**
     * @return true if ID represents a child
     */
    public boolean isChild() {
        return id.toString().contains("#");
    }
    /**
     * @return true if ID represents a File
     */
    public boolean isFile() {
        return file != null;
    }
    /**
     * @return true if ID represents a decorator
     */
    public boolean isDecorator() {
        if (url == null)
            return false;

        String HOST = url.getHost();
        String PROTOCOL = url.getProtocol();
        String PATH = url.getPath();
        if (!"http".equals(PROTOCOL))return false; //$NON-NLS-1$
        if (!"localhost".equals(HOST))return false; //$NON-NLS-1$

        if (!"/mapgraphic".equals(PATH))return false; //$NON-NLS-1$
        return true;
    }
    /**
     * @return true if ID represents a temporary (or memory) resource
     */
    public boolean isTemporary() {
        if (url == null)
            return false;
        String HOST = url.getHost();
        String PROTOCOL = url.getProtocol();
        String PATH = url.getPath();
        if (!"http".equals(PROTOCOL))return false; //$NON-NLS-1$
        if (!"localhost".equals(HOST))return false; //$NON-NLS-1$

        if (!"/scratch".equals(PATH))return false; //$NON-NLS-1$
        return true;
    }
    public boolean isWMS() {
        if (url == null)
            return false;
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
     * @return true if ID refers to a web feature server
     */
    public boolean isWFS() {
        if (url == null){
            return false;
        }
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
     * @return true if ID refers to a database (ie is a jdbc url)
     */
    public boolean isJDBC() {
        return id.startsWith("jdbc") || (url != null && url.getProtocol().contains("jdbc")) ; //$NON-NLS-1$ //$NON-NLS-2$
        // if( url != null){
        // String PROTOCOL = url.getProtocol();
        // String HOST = url.getHost();
        //            return "http".equals(PROTOCOL) && HOST != null && HOST.indexOf(".jdbc") != -1; //$NON-NLS-1$ //$NON-NLS-2$
        // }
    }
    /**
     * @return true url identifies an in memory resource
     */
    public boolean isMemory() {
        String HOST = url.getHost();
        String PROTOCOL = url.getProtocol();
        String PATH = url.getPath();
        if (!"http".equals(PROTOCOL))return false; //$NON-NLS-1$
        if (!"localhost".equals(HOST))return false; //$NON-NLS-1$

        if (!"/scratch".equals(PATH))return false; //$NON-NLS-1$

        return true;
    }
    
    public String labelResource() {
        if (url == null) {
            return id;
        }
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
                file = file.replace("%20", " "); //$NON-NLS-1$ //$NON-NLS-2$
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

    public String labelServer() {
        if (url == null) {
            return id;
        }
        String HOST = url.getHost();
        int PORT = url.getPort();
        String PATH = url.getPath();
        String PROTOCOL = url.getProtocol();

        if (PROTOCOL == null) {
            return ""; // we do not represent a server (local host does not cut it) //$NON-NLS-1$
        }
        StringBuffer label = new StringBuffer();
        if (isFile()) {
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
        } else if (isJDBC()) {
            int split2 = HOST.lastIndexOf('.');
            int split1 = HOST.lastIndexOf('.', split2 - 1);
            if(split1 != -1 && split2 != -1) {
                label.append(HOST.substring(split1 + 1, split2));
                label.append("://"); //$NON-NLS-1$
                label.append(HOST.subSequence(0, split1));
            } else {
                label.append(HOST);
            }
        } else if ("http".equals(PROTOCOL) || "https".equals(PROTOCOL)) { //$NON-NLS-1$ //$NON-NLS-2$
            if (isWMS()) {
                label.append("wms://"); //$NON-NLS-1$
            } else if (isWFS()) {
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

    /**
     * This method will check out the provided object (probably what is being dragged and dropped)
     * and try and manipulate it by hook or crook into an ID that you can check in a canProcess(
     * context ) method or whatever.
     * <p>
     * ID is used (rather then URL) as it supports "jdbc url" format.
     * 
     * @param data
     * @return ID
     */
    public static ID cast( Object data ) {
        if (data == null) {
            // pass null through so error appears in calling code
            return null;
        } else if (data instanceof ID) {
            return (ID) data;
        } else if (data instanceof IService) {
            IService service = (IService) data;
            return service.getID();
        } else if (data instanceof IGeoResource) {
            IGeoResource resource = (IGeoResource) data;
            return resource.getID();
        } else if (data instanceof IResolve) {
            IResolve resolve = (IResolve) data;
            return resolve.getID();
        } else if (data instanceof URL) {
            URL url = (URL) data;
            return new ID(url);
        } else if (data instanceof URI) {
            URI uri = (URI) data;
            try {
                return new ID(uri);
            } catch (IOException e) {
                return null; // could not cast
            }
        } else if (data instanceof File) {
            File file = (File) data;
            return new ID(file, null);
        } else if (data instanceof String) {
            String string = (String) data;
            int index = string.indexOf("\n"); //$NON-NLS-1$
            if (index > -1){
                // just grab the first line in a multi-line drag and drop
                string = string.substring(0, index);
            }
            // try to turn into a string directly
            try {
                URL url = new URL(string);
                return new ID( url );
            } catch (MalformedURLException e) {
            }
            try {
                File file = new File( string );
                return new ID( file, null );
            }
            catch( Throwable t ){
            }
            return new ID( string, null );
        } else {
            return null; // no idea what this should be
        }
    }
}
