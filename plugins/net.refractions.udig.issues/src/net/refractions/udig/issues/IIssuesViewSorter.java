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
package net.refractions.udig.issues;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;


/**
 * Interface for a sorting strategy for sorting and expanding elements/branches in the issues view.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public interface IIssuesViewSorter{
    /**
     * Returns a negative, zero, or positive number depending on whether
     * the first element is less than, equal to, or greater than
     * the second element.
     * 
     * @param viewer viewer that the sorter is sorting
     * @param defaultSorter the default sorter.  
     * @param selectedColumn the selected column.
     * @param direction if true then the order should be ascending if false then descending.  This is changed when the header of the 
     * selected column is clicked.  It is normal table functionality in many apps.
     * @param e1 the first object
     * @param e2 the second object
     * @return a negative number if the first element is less  than the 
     *  second element; the value <code>0</code> if the first element is
     *  equal to the second element; and a positive number if the first
     *  element is greater than the second element
     */
    public int compare( Viewer viewer, ViewerSorter defaultSorter, Column selectedColumn, boolean direction, Object e1, Object e2 );

    /**
     * Returns the extension id so that the system can instantiate the 
     * sorter again in the future after the workbench has been shutdown.
     * 
     * @return pluginID.extensionid.
     */
    String getExtensionID();
    /**
     * Returns whether this viewer sorter would be affected 
     * by a change to the given property of the given element.
     * <p>
     * The default implementation of this method returns <code>false</code>.
     * Subclasses may reimplement.
     * </p>
     *
     * @param defaultSorter the default sorter.  
     * @param element the element
     * @param property the property
     * @return <code>true</code> if the sorting would be affected,
     *    and <code>false</code> if it would be unaffected
     */
    public boolean isSorterProperty( ViewerSorter defaultSorter, Object element, String property );
    /**
     * Returns the category of the given element. The category is a
     * number used to allocate elements to bins; the bins are arranged
     * in ascending numeric order. The elements within a bin are arranged
     * via a second level sort criterion.
     * <p>
     * The default implementation of this framework method returns
     * <code>0</code>. Subclasses may reimplement this method to provide
     * non-trivial categorization.
     * </p>
     *
     * @param defaultSorter the default sorter.  
     * @param element the element
     * @return the category
     */
    public int category( ViewerSorter defaultSorter,  Object element );
}
