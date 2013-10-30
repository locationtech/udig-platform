/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.issues.internal.view;

import net.refractions.udig.issues.Column;
import net.refractions.udig.issues.IIssue;
import net.refractions.udig.issues.IIssuesViewSorter;

import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * Sort the viewer by one of the columns
 * 
 * @author jones
 * @since 1.0.0
 */
public class IssuesSorter implements IIssuesViewSorter {
    public int compare( Viewer viewer, ViewerSorter defaultSorter, Column selectedColumn, boolean direction, Object e1, Object e2 ) {
        if( !(e1 instanceof IIssue) )
            return defaultSorter.compare(viewer, e1, e2);
        
        IIssue issue1 = (IIssue) e1;
        IIssue issue2 = (IIssue) e2;

        if (!direction) {
            issue1 = (IIssue) e2;
            issue2 = (IIssue) e1;
        }

        if (viewer instanceof ContentViewer) {
            ContentViewer tviewer = (ContentViewer) viewer;
            IBaseLabelProvider provider = tviewer.getLabelProvider();
            if (provider instanceof ITableLabelProvider) {
                ITableLabelProvider tableProvider = (ITableLabelProvider) provider;
                String text1 = tableProvider.getColumnText(issue1, IssuesView.columnToIndex(selectedColumn));
                String text2 = tableProvider.getColumnText(issue2, IssuesView.columnToIndex(selectedColumn));
                if (text1 != null && text2 != null) {
                    return defaultSorter.compare(viewer, text1, text2);
                }

                switch( selectedColumn ) {
                case PRIORITY:
                    return defaultSorter.compare(null, ((IIssue) issue1).getPriority().ordinal(),
                            ((IIssue) issue2).getPriority().ordinal());
                case RESOLUTION:
                    return defaultSorter.compare(null, ((IIssue) issue1).getResolution().ordinal(),
                            ((IIssue) issue2).getResolution().ordinal());

                }
            }
        }

        return defaultSorter.compare(viewer, issue1, issue2);
    }


    public String getExtensionID() {
        return null;
    }


    public int category( ViewerSorter defaultSorter, Object element ) {
        return defaultSorter.category(element);
    }


    public boolean isSorterProperty( ViewerSorter defaultSorter, Object element, String property ) {
        return defaultSorter.isSorterProperty(element, property);
    }
}
