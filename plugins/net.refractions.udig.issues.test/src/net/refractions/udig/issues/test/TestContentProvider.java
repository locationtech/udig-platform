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

import net.refractions.udig.issues.IIssuesContentProvider;

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
