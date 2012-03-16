/*
 *    Parkinfo
 *    http://qpws/parkinfo
 *
 *    (C) 2011, Department of Environment Resource Management
 *
 *    This code is provided for department use.
 */
package net.refractions.udig.ui.filter;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

/**
 * TODO add details
 * @author leviputna
 * @since 1.3.0
 */
public class SimpleFilterEditor extends IFilterEditor{

    private FilterCQLEditorPart customFilter;

    /**
     * @param parent
     * @param style
     */
    SimpleFilterEditor( Composite parent, int style ) {
        super(parent, style);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see net.refractions.udig.ui.filter.IFilterEditor#createPart(org.eclipse.swt.widgets.Composite, int)
     */
    @Override
    protected void createPart( Composite parent, int style ) {
        customFilter = new FilterCQLEditorPart(this, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
        customFilter.getControl().setBounds(10, 10, 554, 60);
    }

    /* (non-Javadoc)
     * @see net.refractions.udig.ui.filter.IFilterEditor#setInput(java.lang.Object)
     */
    @Override
    public void setInput( Object input ) {
        if(input instanceof SimpleFeatureType){
            customFilter.setSchema((SimpleFeatureType) input);
        }
    }

    /* (non-Javadoc)
     * @see net.refractions.udig.ui.filter.IFilterEditor#getInput()
     */
    @Override
    public Filter getInput() {
        return customFilter.getInput();
    }

    /* (non-Javadoc)
     * @see net.refractions.udig.ui.filter.IFilterEditor#getSelection()
     */
    @Override
    public ISelection getSelection() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.refractions.udig.ui.filter.IFilterEditor#refresh()
     */
    @Override
    public void refresh() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.refractions.udig.ui.filter.IFilterEditor#isValid()
     */
    @Override
    public boolean isValid() {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.refractions.udig.ui.filter.IFilterEditor#getValidationMessage()
     */
    @Override
    public String getValidationMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.refractions.udig.ui.filter.IFilterEditor#canProcess(java.lang.Object)
     */
    @Override
    public Boolean canProcess( Object input ) {
        // TODO Auto-generated method stub
        return null;
    }

}
