/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.issues.test;

import net.refractions.udig.core.enums.Priority;
import net.refractions.udig.issues.Column;
import net.refractions.udig.issues.IIssue;
import net.refractions.udig.issues.IIssuesViewSorter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class TestSorter {

    public static class Sorter1 implements IIssuesViewSorter {
        public String getExtensionID() {
            return null;
        }

        public int compare( Viewer viewer, ViewerSorter defaultSorter, Column selectedColumn, boolean direction, Object e1, Object e2 ) {
            if( ((IIssue) e1).getPriority()==Priority.LOW)
                return -1;
            if( ((IIssue) e2).getPriority()==Priority.LOW)
                return 1;
            return 0;
        }

        public int category( ViewerSorter defaultSorter, Object element ) {
            return 0;
        }

        public boolean isSorterProperty( ViewerSorter defaultSorter, Object element, String property ) {
            return false;
        }

    }

    public static class Sorter2 implements IIssuesViewSorter {

        public String getExtensionID() {
            return null;
        }

        public int compare( Viewer viewer, ViewerSorter defaultSorter, Column selectedColumn, boolean direction, Object e1, Object e2 ) {
            if( ((IIssue) e1).getPriority()==Priority.WARNING)
                return -1;
            if( ((IIssue) e2).getPriority()==Priority.WARNING)
                return 1;
            return 0;
        }

        public int category( ViewerSorter defaultSorter, Object element ) {
            return 0;
        }

        public boolean isSorterProperty( ViewerSorter defaultSorter, Object element, String property ) {
            return false;
        }

    }

}
