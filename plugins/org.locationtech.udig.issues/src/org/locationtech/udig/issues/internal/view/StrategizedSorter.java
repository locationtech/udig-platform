/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.issues.internal.view;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.locationtech.udig.issues.Column;
import org.locationtech.udig.issues.IIssuesViewSorter;

/**
 * Delegates sorting to a {@link IIssuesViewSorter} object
 *
 * @author Jesse
 * @since 1.1.0
 */
public class StrategizedSorter extends ViewerComparator {

    private IIssuesViewSorter strategy;

    private Column column = Column.PRIORITY;

    private boolean reverse = false;

    private ViewerComparator defaultSorter = new ViewerComparator();

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
    public void setStrategy(IIssuesViewSorter newStrategy) {
        strategy = newStrategy;
    }

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
        return strategy.compare(viewer, defaultSorter, column, !reverse, e1, e2);
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    @Override
    public int category(Object element) {
        return strategy.category(defaultSorter, element);
    }

    @Override
    public boolean isSorterProperty(Object element, String property) {
        return strategy.isSorterProperty(defaultSorter, element, property);
    }

}
