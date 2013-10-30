/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.catalog.internal;

import java.net.URL;

import org.locationtech.udig.catalog.ICatalogInfo;

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
