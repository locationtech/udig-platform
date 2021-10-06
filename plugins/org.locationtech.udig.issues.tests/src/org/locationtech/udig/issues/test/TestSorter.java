/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.issues.test;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.locationtech.udig.core.enums.Priority;
import org.locationtech.udig.issues.Column;
import org.locationtech.udig.issues.IIssue;
import org.locationtech.udig.issues.IIssuesViewSorter;

public class TestSorter {

    public static class Sorter1 implements IIssuesViewSorter {
        @Override
        public String getExtensionID() {
            return null;
        }

        @Override
        public int compare(Viewer viewer, ViewerComparator defaultSorter, Column selectedColumn,
                boolean direction, Object e1, Object e2) {
            if (((IIssue) e1).getPriority() == Priority.LOW)
                return -1;
            if (((IIssue) e2).getPriority() == Priority.LOW)
                return 1;
            return 0;
        }

        @Override
        public int category(ViewerComparator defaultSorter, Object element) {
            return 0;
        }

        @Override
        public boolean isSorterProperty(ViewerComparator defaultSorter, Object element,
                String property) {
            return false;
        }

    }

    public static class Sorter2 implements IIssuesViewSorter {

        @Override
        public String getExtensionID() {
            return null;
        }

        @Override
        public int compare(Viewer viewer, ViewerComparator defaultSorter, Column selectedColumn,
                boolean direction, Object e1, Object e2) {
            if (((IIssue) e1).getPriority() == Priority.WARNING)
                return -1;
            if (((IIssue) e2).getPriority() == Priority.WARNING)
                return 1;
            return 0;
        }

        @Override
        public boolean isSorterProperty(ViewerComparator defaultSorter, Object element,
                String property) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public int category(ViewerComparator defaultSorter, Object element) {
            // TODO Auto-generated method stub
            return 0;
        }

    }

}
