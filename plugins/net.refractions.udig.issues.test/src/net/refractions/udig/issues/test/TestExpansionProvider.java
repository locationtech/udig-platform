package net.refractions.udig.issues.test;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;

import net.refractions.udig.issues.IIssuesExpansionProvider;

public class TestExpansionProvider {

    public static class Provider1 implements IIssuesExpansionProvider{

        public String getExtensionID() {
            return null;
        }

        public boolean expand( TreeViewer viewer, TreeItem item, Object element ) {
            return true;
        }

        public int getAutoExpandLevel() {
            return 0;
        }

    }

    public static class Provider2 implements IIssuesExpansionProvider{

        public String getExtensionID() {
            return null;
        }

        public boolean expand( TreeViewer viewer, TreeItem item, Object element ) {
            return false;
        }

        public int getAutoExpandLevel() {
            return 0;
        }

    }

}
