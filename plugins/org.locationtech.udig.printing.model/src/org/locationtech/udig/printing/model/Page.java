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
package org.locationtech.udig.printing.model;

import java.util.List;

import org.locationtech.udig.project.internal.ProjectElement;

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
     * @model containment="true" resolveProxy="false" type="org.locationtech.udig.printing.model.Box"
     */
    public List<Box> getBoxes();
}
