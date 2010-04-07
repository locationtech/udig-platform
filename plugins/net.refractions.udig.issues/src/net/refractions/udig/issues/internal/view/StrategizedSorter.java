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
package net.refractions.udig.issues.internal.view;

import net.refractions.udig.issues.Column;
import net.refractions.udig.issues.IIssuesViewSorter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * Delegates sorting to a {@link IIssuesViewSorter} object
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class StrategizedSorter extends ViewerSorter {

    private IIssuesViewSorter strategy;
    private Column column = Column.PRIORITY;
    private boolean reverse = false;
    private ViewerSorter defaultSorter=new ViewerSorter();
    /**
     * Gets the current strategy being used.
     *
     * @return strategy being used.
     */
    public IIssuesViewSorter getStrategy() {
        return strategy;
    }

    /**
     * Sets the strategy being used.
     *
     * @param newStrategy the new strategy to use.
     */
    public void setStrategy( IIssuesViewSorter newStrategy ) {
        strategy=newStrategy;
    }
    
    @Override
    public int compare( Viewer viewer, Object e1, Object e2 ) {
        return strategy.compare(viewer, defaultSorter, column, !reverse, e1, e2);
    }
    

    public Column getColumn() {
        return column;
    }
    public void setColumn( Column column ) {
        this.column = column;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse( boolean reverse ) {
        this.reverse = reverse;
    }

    public int category( Object element ) {
        return strategy.category(defaultSorter, element);
    }

    public boolean isSorterProperty( Object element, String property ) {
        return strategy.isSorterProperty(defaultSorter, element, property);
    }

    
}
