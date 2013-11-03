/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.issues.test;

import org.locationtech.udig.issues.IIssuesExpansionProvider;

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
