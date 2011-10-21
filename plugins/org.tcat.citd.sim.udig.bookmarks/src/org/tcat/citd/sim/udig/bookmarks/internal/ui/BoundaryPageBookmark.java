package org.tcat.citd.sim.udig.bookmarks.internal.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.part.PageBook;

public class BoundaryPageBookmark extends Page implements IPageBookViewPage {

    private PageBook pagebook;

    public BoundaryPageBookmark() {
    }

    @Override
    public void createControl( Composite parent ) {
        pagebook = new PageBook(parent, SWT.NONE);
        
        Label label = new Label(pagebook, SWT.LEFT);
        //label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        label.setText("Bookmarks: ");
        label.pack();
        

    }

    @Override
    public Control getControl() {
        return pagebook;
    }

    @Override
    public void setFocus() {
        if (getControl() != null) {
            getControl().setFocus();
        }
    }

}
