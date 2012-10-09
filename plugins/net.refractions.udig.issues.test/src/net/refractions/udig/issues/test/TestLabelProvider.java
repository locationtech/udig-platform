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

import net.refractions.udig.issues.Column;
import net.refractions.udig.issues.IIssuesLabelProvider;

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
