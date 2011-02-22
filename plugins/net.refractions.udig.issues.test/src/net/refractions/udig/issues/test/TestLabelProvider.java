package net.refractions.udig.issues.test;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import net.refractions.udig.issues.Column;
import net.refractions.udig.issues.IIssuesLabelProvider;

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
