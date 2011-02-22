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

import java.net.URL;

/**
 * Represents a bean style metadata accessor for metadata about a catalog. This may be the result of
 * a request to a metadata service. All methods within an implementation of this interface should
 * NOT block. Much of this is based on Dublin Core and the RDF application profile.
 *
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public class ICatalogInfo {
    protected String title, description;
    protected URL source;
    protected String[] keywords;

    protected ICatalogInfo() {
        // for sub-classes
    }

    public ICatalogInfo( String title, String description, URL source, String[] keywords ) {
        this.title = title;
        this.description = description;
        this.source = source;
        int i = 0;
        if( keywords!=null ){
            i=keywords.length;
            String[] k=new String[i];
            System.arraycopy(keywords, 0, k, 0, k.length);
            this.keywords = k;
        }
    }

    /**
     * returns the catalog title May Not Block.
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * returns the keywords associated with this catalog May Not Block. Maps to Dublin Core's
     * Subject element
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
     * returns the catalog description.
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the catalog source. May Not Block. Maps to the Dublin Core Server Element
     *
     * @return
     */
    public URL getSource() { // aka server
        return source;
    }
}
