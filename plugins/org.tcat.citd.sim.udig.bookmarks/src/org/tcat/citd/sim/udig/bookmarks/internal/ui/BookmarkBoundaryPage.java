/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */

package org.tcat.citd.sim.udig.bookmarks.internal.ui;

import net.refractions.udig.boundary.BoundaryProxy;
import net.refractions.udig.boundary.IBoundaryService;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.tcat.citd.sim.udig.bookmarks.BookmarkBoundaryStrategy;

/**
 * A page to add to the Boundary View used for additional configuration of the boundary.
 * <p>
 * Idea:add a combo to select a bookmark and use the current bookmark as the boundary. 
 * 
 * @author jody
 * @version 1.3.0
 */
public class BookmarkBoundaryPage extends Page {

    private Composite page;
    private BoundaryProxy strategy;

    public BookmarkBoundaryPage() {
        // careful don't do any work here
    }
    
    // We would overrride init if we needed to (remmeber to call super)
    public void init(IPageSite pageSite){
        super.init(pageSite); // provides access to stuff
        IBoundaryService service = PlatformGIS.getBoundaryService();
        strategy = service.findProxy(BookmarkBoundaryStrategy.ID);        
    }
    
    protected BookmarkBoundaryStrategy getStrategy(){
        if( strategy == null ){
            return null;
        }
        return (BookmarkBoundaryStrategy) strategy.getStrategy();
    }
    

    @Override
    public void createControl( Composite parent ) {
        page = new Composite(parent, SWT.NONE);
        
        Label label = new Label(page, SWT.LEFT);
        //label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        label.setText("Bookmarks Page!!! ");
        label.pack();
        
        if (strategy != null ){
            // add any listeners to strategy
            
        }
    }
    
    public void listen( boolean listen ){
        if( listen ){
            // add listeners to ui stuff
        }
        else {
            // remove listeners from ui stuff
        }
    }

    @Override
    public Composite getControl() {
        return page;
    }

    @Override
    public void setFocus() {
        if (page != null && !page.isDisposed()) {
            page.setFocus();
        }
    }
    
    @Override
    public void dispose() {
        if( page != null ){
            // remove any listeners            
        }
        if( strategy != null ){
            // remove any listeners
            strategy = null;
        }
        super.dispose();
    }

}
