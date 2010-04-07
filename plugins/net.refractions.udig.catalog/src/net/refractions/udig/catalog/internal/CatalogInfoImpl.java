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
package net.refractions.udig.catalog.internal;

import java.net.URL;

import net.refractions.udig.catalog.ICatalogInfo;

/**
 * Provides metadata for a catalog
 * 
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public class CatalogInfoImpl extends ICatalogInfo {

    CatalogInfoImpl() {
        super(null, null, null, null);
    }

    /**
     * Construct <code>CatalogInfoImpl</code>.
     * 
     * @param title
     * @param description
     * @param source
     * @param keywords
     */
    public CatalogInfoImpl( String title, String description, URL source, String[] keywords ) {
        super(title, description, source, keywords);
    }
    /**
     * @param desc The desc to set.
     */
    void setDesc( String desc ) {
        this.description = desc;
    }
    /**
     * @param keywords The keywords to set.
     */
    void setKeywords( String[] keywords ) {
        this.keywords = keywords;
    }
    /**
     * @param source The source to set.
     */
    void setSource( URL source ) {
        this.source = source;
    }
    /**
     * @param title The title to set.
     */
    void setTitle( String title ) {
        this.title = title;
    }
}
