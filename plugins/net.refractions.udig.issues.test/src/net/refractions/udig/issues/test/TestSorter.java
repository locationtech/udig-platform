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
