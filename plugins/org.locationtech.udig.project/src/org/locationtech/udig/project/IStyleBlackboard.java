/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project;

/**
 * Extends the {@link IBlackboard} interface with the concept of "selected" entries.  
 * <p>This concept is added so that multiple styles can be on the blackboard simultaneously but one style will have
 * a stronger weighting than others.
 * </p>
 * <p>Consider a layer that has WMS and WFS resources.  One might want both a SLDStyle on the BB for the WFS but a Named Style for the
 * WMS if the WMS does not support POST SLD.  Further one might want the WMS to be used unless editing is taking place.  
 * In order to do this the Named Style would be marked as "selected" so that the WMS is prefered over the WFS. </p>
 *   
 * @author Jesse
 * @since 1.1.0
 */
public interface IStyleBlackboard extends IBlackboard {
    /**
     * Returns true if the style indicated is marked as selected. 
     *
     * @param styleId the id of the style to check.  (See {@link StyleContent#getId()}.
     * @return true if the style is marked as selected
     */
    boolean isSelected(String styleId);
}
