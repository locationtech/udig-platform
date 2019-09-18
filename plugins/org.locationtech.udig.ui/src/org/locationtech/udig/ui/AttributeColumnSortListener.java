/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.ui;

import java.util.Comparator;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;
import org.opengis.feature.simple.SimpleFeature;

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
            dir = SWT.UP;
        }
        // sort the data based on column and direction
        Comparator<SimpleFeature> comparator = getComparator(selectedColumn, dir);
        featureTable.sort( comparator, dir, selectedColumn );
        // update data displayed in tree
    }

    private Comparator<SimpleFeature> getComparator(TableColumn currentColumn, int dir) {
        if( columnProperty.equals(FeatureTableControl.FEATURE_ID_COLUMN_PROPERTY) ){
            return new FIDComparator(dir);
        }
        return new AttributeComparator(dir, columnProperty); 
    };

}
