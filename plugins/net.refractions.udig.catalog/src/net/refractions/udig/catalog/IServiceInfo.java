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
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Provides metadata information about a service.
 * <p>
 * Information is provided in the form of a single, simple, Java bean.
 * You can treat this bean as a "view" on more complete metadata information
 * that may be accessible via a subclass (or other resolve target). This
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
 */
public class IServiceInfo {

    protected String title, description, _abstract;
    protected URI schema;
    protected URL source, publisher;
    protected String[] keywords;
    protected ImageDescriptor icon;

    protected IServiceInfo() {
        // to be used in an over-ride
    }

    public IServiceInfo( String title, String description, String _abstract, URL source,
            URL publisher, URI schema, String[] keywords, ImageDescriptor icon ) {
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
     * Returns the service keywords. Maps to the Dublin Core Subject element.
     *
     * @return
     */
    public String[] getKeywords() { // aka Subject
        int i = 0;
        if( keywords!=null )
            i=keywords.length;
        String[] k=new String[i];
        if( keywords!=null )
            System.arraycopy(keywords, 0, k, 0, k.length);
        return k;

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
    public URL getPublisher() {
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
    public URL getSource() { // aka server
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
    public ImageDescriptor getIcon() {
        return icon;
    }
}
