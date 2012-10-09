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

import net.refractions.udig.issues.IIssuesExpansionProvider;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;

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
