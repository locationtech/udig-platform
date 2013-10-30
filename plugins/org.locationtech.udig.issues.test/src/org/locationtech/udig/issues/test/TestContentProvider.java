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

import org.locationtech.udig.issues.IIssuesContentProvider;

import org.eclipse.jface.viewers.Viewer;

public class TestContentProvider {

    public static class Provider1 implements IIssuesContentProvider {

        public static final String CHILD = "provider1"; //$NON-NLS-1$

        public String getExtensionID() {
            return null;
        }

        public Object[] getChildren( Object parentElement ) {
            return new String[]{CHILD};
        }

        public Object getParent( Object element ) {
            return null;
        }

        public boolean hasChildren( Object element ) {
            return !(element instanceof String);
        }

        public Object[] getElements( Object inputElement ) {            
            return getChildren(inputElement);

        }

        public void dispose() {
        }

        public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
        }
    }
    

    public static class Provider2 implements IIssuesContentProvider {

        public static final String CHILD = "provider2"; //$NON-NLS-1$

        public String getExtensionID() {
            return null;
        }

        public Object[] getChildren( Object parentElement ) {
            return new String[]{CHILD};
        }


        public Object getParent( Object element ) {
            return null;
        }

        public boolean hasChildren( Object element ) {
            return !(element instanceof String);
        }

        public Object[] getElements( Object inputElement ) {
            return getChildren(inputElement);
        }

        public void dispose() {
        }

        public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
        }
    }

}
