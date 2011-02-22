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
package net.refractions.udig.ui;

import java.util.Comparator;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;
import org.geotools.feature.Feature;

/**
 * Listens for clicks on the column header and sorts the columns according to the direction of the column.
 *
 * @author Jesse
 * @since 1.1.0
 */
class AttributeColumnSortListener implements Listener {

    private TableViewer viewer;
    private String columnProperty;
    private FeatureTableControl featureTable;

    public AttributeColumnSortListener( FeatureTableControl featureTable, String columnProperty ) {
        this.viewer=featureTable.getViewer();
        this.featureTable=featureTable;
        this.columnProperty=columnProperty;
    }

    public void handleEvent( Event e ) {
//      determine new sort column and direction
        TableColumn sortColumn = viewer.getTable().getSortColumn();
        final TableColumn selectedColumn = (TableColumn) e.widget;
        int dir = viewer.getTable().getSortDirection();
        if (sortColumn == selectedColumn) {
            dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
        } else {
            dir = SWT.DOWN;
        }
        // sort the data based on column and direction
        Comparator<Feature> comparator = getComparator(selectedColumn, SWT.DOWN);
        featureTable.sort( comparator, dir, selectedColumn );
        // update data displayed in tree
    }

    private Comparator<Feature> getComparator(TableColumn currentColumn, int dir) {
        if( columnProperty.equals(FeatureTableControl.FEATURE_ID_COLUMN_PROPERTY) ){
            return new FIDComparator(dir);
        }
        return new AttributeComparator(dir, columnProperty);
    };

}
