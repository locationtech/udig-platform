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
package net.refractions.udig.catalog;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;

import net.refractions.udig.ui.graphics.AWTSWTImageUtils;

import org.eclipse.jface.resource.ImageDescriptor;
import org.geotools.data.Parameter;
import org.geotools.process.ProcessFactory;
import org.opengis.feature.type.Name;

/**
 * Represents a bean style metadata ancestor for metadata about a process.
 * <p>
 * The methods within this class must be non-blocking. This class, and sub-classes represent cached
 * versions of the metadata about a particular service.
 * </p>
 * <p>
 * Much of this interface is based on Dublin Core and the RDF application profile.
 * </p>
 * <p>
 * Any changes to these contents will be communicated by an event from the associated Process.
 * </p>
 * 
 * Based on IGeoResourceInfo
 * 
 * @author gdavis, Refractions Research
 */
public class IProcessInfo {

    protected String title, description, name;
    protected String[] keywords;
    protected URI schema;
    protected ImageDescriptor icon;
    protected ProcessFactory processFactory;
    protected Icon awtIcon;
    protected Name processName;

    protected IProcessInfo() {
        // for over-riding
    }

    public IProcessInfo( String title, String name, String description, URI schema,
            ProcessFactory pf, Name pn, String[] keywords, 
            ImageDescriptor icon ) {
        this.processFactory = pf;
        this.processName = pn;
        
        this.title = title;
        this.description = description;
        this.name = name;
        int i = 0;
        if( keywords!=null )
            i=keywords.length;
        String[] k=new String[i];
        if( keywords!=null )
            System.arraycopy(keywords, 0, k, 0, k.length);
        this.keywords = k;
        this.schema = schema;
        this.icon = icon;
    }

    /**
     * Returns the resource's title
     * 
     * @return a readable title (in current local)
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Returns the process factory
     * 
     * @return the processFactory
     */
    public ProcessFactory getProcessFactory() {
        return this.processFactory;
    }    

    public Name getProcessName(){
        return this.processName;
    }
    
    /**
     * Returns the keywords assocaited with this resource
     * <p>
     * Known Mappings:
     * <ul>
     * <li> Maps to Dublin Core's Subject element
     * </ul>
     * </p>
     * 
     * @return Keywords for use with search, or <code>null</code> unavailable.
     */
    public Set<String> getKeywords() { // aka Subject
        List<String> asList = Arrays.asList(keywords);
        Set<String> set = new HashSet<String>(asList);
        return set;
    }

    /**
     * Returns the resource's description.
     * <p>
     * Known Mappings:
     * <ul>
     * <li>WPS GetCapabilities description
     * </ul>
     * </p>
     * 
     * @return description of resource, or <code>null</code> if unavailable
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the xml schema namespace for this resource type.
     * <p>
     * Known Mappings:
     * <ul>
     * <li>Dublin Code Format element
     * </ul>
     * </p>
     * 
     * @return namespace, used with getName() to identify resource
     */
    public URI getSchema() { // aka namespace
        return schema;
    }

    /**
     * Returns the name of the data ... such as the typeName or LayerName.
     * <p>
     * Known Mappings:
     * <ul>
     * <li>WFS typeName
     * <li>Database table name
     * <li>WMS layer name
     * </ul>
     * </p>
     * 
     * @return name of the data, used with getSchema() to identify resource
     */
    public String getName() { // aka layer/type name
        return name;
    }

    /**
     * Base symbology (with out decorators) representing this resource.
     * <p>
     * The ImageDescriptor returned should conform the the Eclipse User Interface Guidelines (16x16
     * image with a 16x15 glyph centered).
     * </p>
     * <p>
     * This plug-in provides default based on resource type:
     * 
     * <pre><code>
     *  &lt;b&gt;return&lt;/b&gt; ISharedImages.getImagesDescriptor( IGeoResoruce );
     * </code></pre>
     * 
     * <ul>
     * <p>
     * Any LabelProvider should use the default image, a label decorator should be used to pick up
     * these images in a separate thread. This allows resources like WMS to make blocking request of
     * an external service.
     * </p>
     * 
     * @return ImageDescriptor symbolizing this resource
     */
    public Icon getIcon() {
        if( awtIcon!=null ){
            return awtIcon;
        }
        if( icon==null ){
            return null;
        }
        
        Icon awtIcon = AWTSWTImageUtils.imageDescriptor2awtIcon(icon);
        return awtIcon;
        
    }

    /**
     * Default implementation calls getIcon and converts the icon to an ImageDescriptor.
     *
     * @return the icon as an image descriptor
     */
    public ImageDescriptor getImageDescriptor() {
        if( icon!=null ){
            return icon;
        }
        
        Icon icon2 = getIcon();
        if( icon2 == null ){
            return null;
        }
        
        return AWTSWTImageUtils.awtIcon2ImageDescriptor(icon2);
    }

    public Map<String, Parameter< ? >> getInputs() {
        return this.processFactory.getParameterInfo( processName );
    }

    public Map<String, Parameter< ? >> getOutputs() {
        return this.processFactory.getResultInfo(processName,null);
    }
}
