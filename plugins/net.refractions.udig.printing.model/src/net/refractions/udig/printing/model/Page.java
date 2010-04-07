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
package net.refractions.udig.printing.model;

import java.util.List;

import net.refractions.udig.project.internal.ProjectElement;

/**
 * Represents a set of positioned Boxes to be drawn. 
 * This class is generally used to draw the boxes on the screen and position
 * them according to a user's wish. They can be repositioned by accessing
 * the location and size properties on each individual Box.
 * 
 * @author Richard Gould
 * @since 0.3
 * @model
 */
public interface Page extends Element, ProjectElement{
    
    /**
     * Each Box represents an item to be drawn and printed, and can be
     * resized and repositioned.
     * 
     * @return the List of Boxes on this Page
     * @model containment="true" resolveProxy="false" type="net.refractions.udig.printing.model.Box"
     */
    public List<Box> getBoxes();
}
