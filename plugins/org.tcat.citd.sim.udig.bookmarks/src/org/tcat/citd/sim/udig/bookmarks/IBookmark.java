/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
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