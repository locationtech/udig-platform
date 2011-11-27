package net.refractions.udig.ui.boundary;

import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.SubActionBars;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.PageSite;

/**
 * A data structure used to store the information about a single page.
 */
public class PageRecord {

    /**
     * The part.
     */
    protected IViewPart part;

    /**
     * The page.
     */
    protected IPage page;

    /**
     * The page site resonpsible for hooking the page up to view facilities.
     */
    protected PageSite site;
    
    /**
     * The page's action bars
     */
    protected SubActionBars subActionBars;

    /**
     * Creates a new page record initialized to the given part and page.
     * 
     * @param part
     * @param page
     */
    public PageRecord(IViewPart part, IPage page) {
        this.part = part;
        this.page = page;
    }

    /**
     * Disposes of this page record by <code>null</code>ing its fields.
     */
    public void dispose() {
        part = null;
        page = null;
    }

    public SubActionBars getSubActionBars() {
        return subActionBars;
    }

    public void setSubActionBars( SubActionBars subActionBars ) {
        this.subActionBars = subActionBars;
    }

    public IWorkbenchPart getPart() {
        return part;
    }

    public IPage getPage() {
        return page;
    }
    
    public Control getControl(){
        if( page == null ){
            return null;
        }
        return page.getControl();
    }
    
    public PageSite getSite() {
        if( site == null ){
            // We will create a site for our use
            site = new PageSite( (IViewSite) part.getSite() );
            subActionBars = (SubActionBars) site.getActionBars();
            
            // TOD: client code should listen to the action bars
            // subActionBars.addPropertyChangeListener(actionBarPropListener);

            // insert the sub action bars into the page (so it can add to them)
            page.setActionBars(subActionBars);
        }        
        return site;
    }
}