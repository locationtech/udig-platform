/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.URLUtils;
import org.locationtech.udig.catalog.service.FormatProvider;
import org.locationtech.udig.core.internal.ExtensionPointProcessor;
import org.locationtech.udig.core.internal.ExtensionPointUtil;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;

public class FileConnectionFactory extends UDIGConnectionFactory {

    private static final String FILE_FORMAT_EXTENSION = "org.locationtech.udig.catalog.ui.fileFormat"; //$NON-NLS-1$
    private ArrayList<String> extensionList;
    private ArrayList<FileType> typeList;
    
    public boolean canProcess( Object context ) {
        return createConnectionURL(context) != null;
    }

    public Map<String, Serializable> createConnectionParameters( Object context ) {
        // do nothing, we are not connecting to a specific data store
        return null;
    }

    public URL createConnectionURL( Object context ) {
        URL url = CatalogPlugin.locateURL(context);

        if (url == null) {
            return null;
        }

        url = checkedURL(url);
        if (url == null || url.getFile() == null) {
            return null;
        }

        // Checks whether file is acceptable based on extension.
        String fileExt = url.getFile().substring(url.getFile().lastIndexOf('.') + 1);
        if (fileExt != null)
            fileExt = fileExt.toLowerCase();

        for( String goodExt : getExtensionList() ) {
            goodExt = goodExt.toLowerCase();
            if (fileExt.equals(goodExt.substring(goodExt.lastIndexOf('.') + 1))) {
                // actually do a test
                File f = URLUtils.urlToFile(url);
                if (f.exists())
                    return url;
            }
        }
        return null;
    }

    /** Check that any trailing #layer is removed from the url */
    static public URL checkedURL( URL url ) {
        String check = url.toExternalForm();
        int hash = check.indexOf('#');
        if (hash == -1) {
            return url;
        }
        try {
            return new URL(check.substring(0, hash));
        } catch (MalformedURLException e) {
            return null;
        }
    }
    public static class FileType implements Comparable<FileType> {
        final String name;
        final String extensions;
        FileType( String name, String extensions){
            this.name = name;
            this.extensions = extensions;
        }
        public String getName() {
            return name;
        }
        public String getExtensions() {
            return extensions;
        }
        public List<String> getExtensionList(){
            if( extensions.contains(";") ){
                return Arrays.asList( extensions.split(";"));
            }
            else {
                return Collections.singletonList( extensions );
            }
        }

        @Override
        public int compareTo(FileType o) {
            if( o == null || o.name == null){
                return -1;
            }
            return name.compareTo(o.name);
        }
    }
    
    /**
     * List of all registered FileTypes.
     * @return List of all registered FileTypes
     */
    synchronized List<FileType> getTypeList() {
        if (typeList == null) {
            final Set<FileType> extensionSet = new TreeSet<FileType>();
            ExtensionPointUtil.process(CatalogUIPlugin.getDefault(),
                    FILE_FORMAT_EXTENSION, new ExtensionPointProcessor(){ 
                        public void process( IExtension extension, IConfigurationElement element )
                                throws Exception {
                            if ("fileService".equals(element.getName())) { //$NON-NLS-1$
                                String name = element.getAttribute("name"); //$NON-NLS-1$
                                String ext = element.getAttribute("fileExtension"); //$NON-NLS-1$
                                
                                if( name == null && ext.startsWith("*.")){
                                    if( ext.contains(";") ){
                                     // *.jpg;*.jpeg --> JPG Files
                                        name = ext.substring(2,ext.lastIndexOf(';')).toUpperCase()+" Files";
                                    }
                                    else {
                                        // *.gif --> GIF Files
                                        name =ext.substring(2).toUpperCase()+" Files";
                                    }
                                }
                                if( !name.contains("(")){
                                    name += " ("+ext.replace(";",",")+")";
                                }
                                FileType type = new FileType(name, ext );
                                extensionSet.add( type );
                            }
                            if ("provider".equals(element.getName())) {
                                FormatProvider provider = (FormatProvider) element
                                        .createExecutableExtension("class");
                                
                                String name = null;
                                if( name == null ){
                                    name = provider.getClass().getSimpleName();
                                    if( name.equals("FileDataStoreFormatProvider")){
                                        name = "GeoTools DataStore Files";
                                    }
                                    else if( name.endsWith("FormatProvider")){
                                        name = name.substring(0,name.length()-14); // trim FormatProvider
                                        
                                        name += "Files"; // GDALFormatProvider --> GDAL Files
                                    }
                                }
                                StringBuilder ext = new StringBuilder();
                                Set<String> providerExtensions = provider.getExtensions();
                                if( !providerExtensions.isEmpty() ){
                                    for( String fileExtension : providerExtensions ){
                                        if( ext.length() != 0 ){
                                            ext.append(";");
                                        }
                                        ext.append(fileExtension);
                                    }
                                    if( !name.contains("(")){
                                        if( providerExtensions.size() > 4 ){
                                            // Shorter name for providers supporting multiple formats 
                                            String first = providerExtensions.iterator().next();
                                            name += " ( "+first+" and "+(providerExtensions.size()-1)+" more)";
                                        }
                                        else {
                                            name += " ("+ext.toString().replace(";",",")+")";
                                        }
                                    }
                                    FileType type = new FileType( name, ext.toString() );
                                    extensionSet.add(type);
                                }
                            }
                        }
                    });
            typeList = new ArrayList<FileType>(extensionSet);
        }
        return Collections.unmodifiableList( typeList );
    }
    
    /**
     * List of all known extensions to be used as a quick test that a provided file can be expected to work.
     * 
     * @return List of all known extensions
     */
    @SuppressWarnings("unchecked")
    synchronized List<String> getExtensionList() {
        if (extensionList == null) {
            final List<String> extensionSet = new ArrayList<String>();
            for( FileType type : getTypeList() ){
                List<String> extensions = type.getExtensionList();
                extensionSet.addAll( extensions );
            }
            extensionList = new ArrayList<String>(extensionSet);
        }
        return (List<String>) extensionList.clone();
    }
}
