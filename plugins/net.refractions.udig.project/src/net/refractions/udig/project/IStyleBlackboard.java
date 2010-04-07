/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.project;

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
