/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.tcat.citd.sim.udig.bookmarks;

import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * Defines a bookmark to store a named location
 * 
 * @author paul.pfeiffer
 * @version 1.3.0
 */
public interface IBookmark {

    /**
     * @return Returns the envelope.
     */
    public ReferencedEnvelope getEnvelope();

    /**
     * @param envelope The envelope to set.
     */
    public void setEnvelope( ReferencedEnvelope envelope );

    /**
     * @return Returns the name.
     */
    public String getName();

    /**
     * @param name The name to set.
     */
    public void setName( String name );

}