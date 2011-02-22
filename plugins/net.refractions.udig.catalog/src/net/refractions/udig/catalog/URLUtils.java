/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.catalog;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import net.refractions.udig.core.internal.CorePlugin;

import org.geotools.data.DataUtilities;


/**
 * Utilities for dealing with the catalog's use of URLs as identifiers
 *
 * @author Jesse
 * @since 1.1.0
 */
public class URLUtils {

    /**
     * Primarily for testing the comparison of URLS.  it is not a simple thing because different platforms can sometimes
     * create ones with a dangling / or with / vs \ some times file:/// or file:/.
     *
     * @param url1 first url to compare
     * @param url2 second url
     * @param stripRef if the reference should be ignored.  For example searching for the IService
     * of a IGeoResource.  The Service ID would not have a reference but the IGeoResource would so
     * ignore the reference in this case.
     * @return true if they refer to the same resource.
     */
    public static boolean urlEquals( URL url1, URL url2, boolean stripRef ) {
        if( url1==null ){
            if( url2 == null )
                return true;
            return false;
        }
        if( url2==null ){
            return false;
        }
        boolean sameProtocol = url2.getProtocol().equalsIgnoreCase(url1.getProtocol());
        boolean sameHost = ((url2.getHost() == null || "".equals(url2.getHost())) || (url2.getHost() != null && url2.getHost().equalsIgnoreCase(url1.getHost()))); //$NON-NLS-1$
        boolean samePath = ((url2.getPath() == null || "".equals(url2.getPath())) || (url2.getPath() != null && url2.getPath().equalsIgnoreCase(url1.getPath()))); //$NON-NLS-1$
        boolean sameQuery = ((url2.getQuery() == null || "".equals(url2.getQuery())) || (url2.getQuery() != null && url2.getQuery().equalsIgnoreCase(url1.getQuery()))); //$NON-NLS-1$
        boolean sameAuthority = ((url2.getAuthority() == null || "".equals(url2.getAuthority())) || (url2.getAuthority() != null && url2.getAuthority().equalsIgnoreCase(url1.getAuthority()))); //$NON-NLS-1$
        boolean sameRef = !stripRef && ((url2.getRef() == null || "".equals(url2.getRef())) || (url2.getRef() != null && url2.getRef().equalsIgnoreCase(url1.getRef()))); //$NON-NLS-1$
        if (sameProtocol && sameHost && samePath &&
                sameQuery && sameAuthority && sameRef)
            return true;

        String string1 = URLUtils.urlToString(url1, stripRef);
        String string2 = URLUtils.urlToString(url2, stripRef);
        if( stripRef )
        	return string1.startsWith(string2) || string2.startsWith(string1);
        return string1.equalsIgnoreCase(string2);
    }

    /**
     * Provides a standard way of converting to string.
     * <p>
     * Perform as much cleaning on the provided url as we can
     * figure out (example: change all the windows backlashs
     * to forward slashes).
     *
     * @param url Convert this URL to a clean string representation
     * @param ignoreRef ignore the reference in the string.
     * @return the url in string form.  Always uses / and file:/.  (rather than file:///).
     */
    public static String urlToString( URL url, boolean ignoreRef ) {
        String string=url.toExternalForm();
        if( ignoreRef ){
            if( string.contains("#") ) //$NON-NLS-1$
                string=string.substring(0,string.lastIndexOf('#') );
        }
        // deal with withs C:\foo\bar to C:/foo/bar
        string=string.replaceAll("\\\\", "/");  //$NON-NLS-1$//$NON-NLS-2$
        if( string.endsWith("/") ) //$NON-NLS-1$
            string=string.substring(0, string.length()-1);

        // deal with ///C:/foo/bar to /C:/foo/bar
        // 1) file:/C:/foo/bar     absolute, not really correct
        // 2) file://C:/foo/bar    absolute, not really correct
        // 3) file:///C:/foo/bar   absolute, and correct
        // 4) file:///Users/Jesse  absolute mac or linux
        // 5) file:/Jesse          relative path
        // 6) file://Jesse         relative? 90% of the time
        // 7) file:foo             relative to base url
        string=string.replaceAll("/+", "/"); //$NON-NLS-1$ //$NON-NLS-2$

        return string;
    }

    /**
     * Converts the URL to a URL indicating the relative path from the reference file to the location indicated
     * by the destination URL.  If it is not possible to to make a relative path then the destination URL will be returned unchanged.
     * <p>
     * Examples:
     * <ul>
     * <li>reference = c:\foo\bar
     * <p>destination = file:/c:\booger\data<p>
     * <p>return = file:/..\..\booger\data</p></li>
     * <li>reference = c:\foo\bar
     * <p>destination = file:/d:\booger\data<p>
     * <p>return = file:/d:\booger\data</p></li>
     * <li>reference = c:\foo\bar
     * <p>destination = http://booger.com\data<p>
     * <p>return = http://booger.com\data</p></li>
     * </ul>
     * </p>
     *
     * @param reference the "from" file.  The file that the relative path will start at. <b>MUST BE A FILE</b>
     * @param destination the URL to transform to a relative path
     * @return the relative path from reference to destination
     */
    public static URL toRelativePath(File reference, URL destination){
    	if( !destination.getProtocol().equalsIgnoreCase("file")  //$NON-NLS-1$
    			|| destination.getQuery()!=null
    			|| destination.getRef()!=null)
    		return destination;

    	String from = reference.getAbsolutePath();
    	String to = urlToString(destination, false).substring(5);

    	if ( from.equals(to) )
			try {
				return new URL("file:/./"); //$NON-NLS-1$
			} catch (MalformedURLException e1) {
				throw new RuntimeException(e1);
			}

    	int endOfMatch=0;
    	int lastSlash=0;
    	for (int i = 0; i < from.length(); i++) {
			char fromChar = from.charAt(i);
			char toChar = to.charAt(i);

			endOfMatch=i;
			if( fromChar!=toChar ){
				break;
			}
			if( fromChar=='/' )
				lastSlash=i;

		}

    	if( endOfMatch==0 )
    		return destination;

    	String substring = from.substring(lastSlash+1);
		int slashes=substring.split("/").length-1; //$NON-NLS-1$

    	StringBuilder result = new StringBuilder();

    	for (int i = 0; i < slashes; i++) {
			result.append("../"); //$NON-NLS-1$
		}

    	try {
			return new URL("file://"+result.toString()+to.substring(lastSlash+1)); //$NON-NLS-1$
		} catch (MalformedURLException e) {
			return destination;
		}
    }

	/**
     * Creates a URL from the string.  If the String is a relative URL (and is a file) the returned
     * URL will be resolved to the Absolute path with respect to the reference parameter.
     *
     * <p>
     * <ul>
     * <li>reference = c:\foo\bar
     * <p>destination = file:/..\..\booger\data</p>
     * <p> return = file:/c:\booger\data<p>
     * </li>
     * </ul></p>
     *
     * @param base the base of any relative path.  <b>MUST BE A FILE</b>
     * @param urlSpec the url in string form.
     *
     * @return the URL created from the string
     */
    public static URL constructURL( File base, String urlSpec ) throws MalformedURLException {
        URL url = new URL(null, urlSpec, CorePlugin.RELAXED_HANDLER);
        if (!urlSpec.startsWith("file:/") || urlSpec.contains("?") //$NON-NLS-1$ //$NON-NLS-2$
                || urlSpec.contains("#")) { //$NON-NLS-1$
            return url;
        }
        File file = urlToFile(url);
        if (file.exists()) {
            try {
                if (file.getCanonicalPath().equals(file.getPath())) {
                    // explicit (non relative) url
                    return url;
                }
            } catch (IOException e) {
                // won't work so lets continue on and try to resolve the file.
            }
        }

        String substring = urlSpec.substring(5);
        if (substring.startsWith("//")) { //$NON-NLS-1$
            // skip leading / indicating a root folder?
            substring = substring.substring(2);
        }
        try {
            if (substring.charAt(0) == base.getCanonicalPath().charAt(0)) {
                return url;
            }
        } catch (IOException e1) {
            return url;
        }

        file = new File(base.getParentFile(), substring);
        try {
            return new URL("file:" + file.getCanonicalFile());
        } catch (IOException e) {
            return url;
        }
    }

    /**
     * Takes a URL and converts it to a File. The attempts to deal with
     * Windows UNC format specific problems, specifically files located
     * on network shares and different drives.
     *
     * If the URL.getAuthority() returns null or is empty, then only the
     * url's path property is used to construct the file. Otherwise, the
     * authority is prefixed before the path.
     *
     * It is assumed that url.getProtocol returns "file".
     *
     * Authority is the drive or network share the file is located on.
     * Such as "C:", "E:", "\\fooServer"
     *
     * @param url
     * @return
     */
    public static File urlToFile (URL url) {
    	return DataUtilities.urlToFile(url);
    }

    /**
     * Produce a clean filename given a resource typeName.
     * <p>
     * This method will replace all non alpha numeric characters with "_".
     * <p>
     * Example:<code>String filename = URLUtils.cleanFilename("topp:tasmania_citities");</code>
     *
     * @param typeName
     * @return
     */
    public static String cleanFilename( String typeName ){
        StringBuffer fix = new StringBuffer( typeName );
        for( int i=0; i<fix.length();i++){
            char c = fix.charAt( i );
            if( !Character.isLetterOrDigit(c) ){
                fix.setCharAt(i, '_' );
            }
        }
        return fix.toString();
    }

    /**
     * Finds the other files with the same base name as the baseFile but with the optionalExtensions.  The case of the extensions are ignored.
     *
     * @param baseFile the baseFile
     * @param optionalExtensions all the options
     * @return
     */
    public static File[] findRelatedFiles( File baseFile, final String... optionalExtensions ) {
        int index = baseFile.getName().indexOf('.');
        final String base;
        if (index > 0) {
            base = baseFile.getName().substring(0,index);
        } else {
            base = baseFile.getName();
        }
        return baseFile.getParentFile().listFiles(new FilenameFilter(){

            public boolean accept( File dir, String name ) {
                String lower = name.toLowerCase();
                if( name.startsWith(base) ){
                    for( String string : optionalExtensions ) {
                        String ext = string.toLowerCase();
                        if( lower.endsWith(ext) && lower.equalsIgnoreCase(base+ext) ){
                            return true;
                        }
                    }
                }
                return false;
            }

        });
    }
}
