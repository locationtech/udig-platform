/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004-2011, Refractions Research Inc.
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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Icon;

import net.refractions.udig.ui.graphics.AWTSWTImageUtils;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Provides metadata information about a service.
 * <p>
 * Information is provided in the form of a single, simple, Java bean.
 * You can treat this bean as a "view" on more complete metadata information
 * that may be accessable via a subclass (or other resolve target). This
 * bean offers up service metadata information to the uDig search
 * facilities, this information may also be displayed to users.
 * </p>
 * <p>
 * Much of the names and motivation have been taken from Dublin Code
 * and it's application profile for RDF.
 * </p>
 * 
 * @author David Zwiers, Refractions Research
 * @since 0.6
 * @version 1.2
 */
public class IServiceInfo {

    protected String title, description, _abstract;
    protected URI schema;
    protected URI source, publisher;
    protected String[] keywords;
    protected ImageDescriptor icon;
    private Icon awtIcon;

    protected IServiceInfo() {
        // to be used in an over-ride
    }

    public IServiceInfo( String title, String description, String _abstract, URI source,
            URI publisher, URI schema, String[] keywords, ImageDescriptor icon ) {
        this.title = title;
        this.description = description;
        this._abstract = _abstract;
        this.schema = schema;
        this.source = source;
        this.publisher = publisher;
        int i = 0;
        if( keywords!=null )
            i=keywords.length;
        String[] k=new String[i];
        if( keywords!=null )
            System.arraycopy(keywords, 0, k, 0, k.length);
        this.keywords = k;
        this.icon = icon;
    }

    /**
     * Returns the service title, may be empty or null if unsupported.
     * <p>
     * Note this is always metadata, and is in user terms.
     * </p>
     * 
     * @return title, may be empty, null if unsupported.
     */
    public String getTitle() {
        return title;
    }
    

    /**
     * Returns the an abbreviated title, may be empty or null if unsupported.
     * <p>
     * Note this is always metadata, and is in user terms.
     * </p>
     * 
     * @return title, may be empty, null if unsupported.
     */
    public String getShortTitle() {
        return getTitle();
    }
    
    

    /**
     * Returns the service keywords. Maps to the Dublin Core Subject element.
     * 
     * @return
     */
    public Set<String> getKeywords() { // aka Subject
        if( keywords == null ){
            return Collections.emptySet();
        }
        return new HashSet<String>(Arrays.asList(keywords));

    }

    /**
     * Returns the service description.
     * 
     * This use is understood to be in agreement with "dublin-core",
     * implementors may use either abstract or description as needed.
     * <p>
     * Dublin Core:
     * <quote>
     * A textual description of the content of the resource, including
     * abstracts in the case of document-like objects or content
     * descriptions in the case of visual resources.
     * </quote>
     * 
     * When providing actual dublin-core metadata you can gather up
     * all the description information into a single string for
     * searching.
     * 
     * @return Description of visual contents
     */
    public String getDescription() {
        return description;
    }

    /**
     * Return the service abstract.
     * 
     * This use is understood to be in agreement with OGC Open Web Services,
     * implementors may use either abstract or description as needed.
     * <p>
     * When working with an Open Web Service this method is a direct match,
     * you may also choose it when providing actual dublin-core information
     * if the description element is specifically an abstract.
     * </p>
     * 
     * @return text Abstract of document-like services
     */
    public String getAbstract() {
        return _abstract;
    }

    /**
     * Return the service publisher
     * 
     * @return
     */
    public URI getPublisher() {
        return publisher;
    }

    /**
     * Returns the xml schema namespace for this service type.
     * <p>
     * Maps to the Dublin Code Format element.
     * </p>
     * 
     * @return namespace for service type
     */
    public URI getSchema() { // aka format
        return schema;
    }

    /**
     * Returns the service source. Maps to the Dublin Core Server Element
     * 
     * @return
     */
    public URI getSource() { // aka server
        return source;
    }

    /**
     * Base symbology (with out decorators) representing this IService.
     * <p>
     * The ImageDescriptor returned should conform the the Eclipse User Interface Guidelines (16x16
     * image with a 16x15 glyph centered).
     * </p>
     * <p>
     * This plug-in provides default images based on service type:
     * 
     * <pre><code>
     *  &lt;b&gt;return&lt;/b&gt; ISharedImages.getImagesDescriptor( IService );
     * </code></pre>
     * 
     * <ul>
     * <p>
     * Any LabelProvider should use the default image, a label decorator should be used to pick up
     * these images in a separate thread. This allows services like WFS make blocking request to
     * pick up the image from their GetCapabilities.
     * </p>
     * 
     * @return ImageDescriptor symbolizing this IService.
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

    /**
     * Check the completeness of available metadata for this service, this value is used to rank
     * services so that the Catalog can make an informed decision on what service handle to use.
     * <p>
     * Sub classes are encouraged to override this method, implementing there own completeness
     * 
     * @return the completeness of possible metadata for this service. A value of 1 = complete 
     * and 0 = incomplete, a value in-between represents a partial completeness.
     */
    public double getMetric() {
        return 0;
    }

}
