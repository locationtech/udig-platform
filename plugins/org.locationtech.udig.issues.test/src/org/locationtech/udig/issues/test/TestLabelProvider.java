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

import org.locationtech.udig.issues.Column;
import org.locationtech.udig.issues.IIssuesLabelProvider;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

public class TestLabelProvider {

    public static class Provider1 implements IIssuesLabelProvider {
        public static final String HEADERTEXT="header1"; //$NON-NLS-1$
        public static final String ROWTEXT="row1"; //$NON-NLS-1$
        
        public String getExtensionID() {
            return null;
        }

        public String getHeaderText( Column column ) {
            if( column == Column.PROBLEM_OBJECT )
                return HEADERTEXT;
            return null;
        }

        public Image getImage( Object element ) {
            return null;
        }

        public String getText( Object element ) {
            return ROWTEXT;
        }

        public void addListener( ILabelProviderListener listener ) {
        }

        public void dispose() {
        }

        public boolean isLabelProperty( Object element, String property ) {
            return false;
        }

        public void removeListener( ILabelProviderListener listener ) {
        }
    }

    public static class Provider2 implements IIssuesLabelProvider {
        public static final String HEADERTEXT="header2"; //$NON-NLS-1$
        public static final String ROWTEXT="row2"; //$NON-NLS-1$
        
        public String getExtensionID() {
            return null;
        }

        public String getHeaderText( Column column ) {
            if( column == Column.PROBLEM_OBJECT )
                return HEADERTEXT;
            return null;
        }

        public Image getImage( Object element ) {
            return null;
        }

        public String getText( Object element ) {
            return ROWTEXT;
        }

        public void addListener( ILabelProviderListener listener ) {
        }

        public void dispose() {
        }

        public boolean isLabelProperty( Object element, String property ) {
            return false;
        }

        public void removeListener( ILabelProviderListener listener ) {
        }
    }

}
