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
 *
 */
package net.refractions.udig.catalog.ui.wizard;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.internal.ui.ImageConstants;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.internal.Messages;
import net.refractions.udig.catalog.ui.search.ResourceSearchComposite;
import net.refractions.udig.catalog.ui.workflow.ResourceSearchState;
import net.refractions.udig.catalog.ui.workflow.State;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardPage;

import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Allow the user to search for a resource to include in the map.
 * 
 * @author Jody Garnett
 * @since 1.3.3
 */
public class ResourceSearchPage extends WorkflowWizardPage implements IPageChangedListener {
    ResourceSearchComposite search;
    
    private ISelectionChangedListener listener = new ISelectionChangedListener() {
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            ISelection sel = event.getSelection();
            Set<IResolve> set = new HashSet<IResolve>();
            
            if (!sel.isEmpty() && sel instanceof StructuredSelection ){
                StructuredSelection selection = (StructuredSelection) sel;
                
                for( Iterator<?> i = selection.iterator(); i.hasNext(); ){
                    Object item = i.next();
                    if( item instanceof IGeoResource ){
                        IGeoResource resource = (IGeoResource) item;
                        set.add( resource );
                    }
                    else if ( item == ResourceSearchState.IMPORT_PLACEHOLDER ){
                        set.add( ResourceSearchState.IMPORT_PLACEHOLDER );
                    }
                }
            }
            getState().setSelected( set );
            
            //boolean complete = !getState().getSelected().isEmpty();
            //setPageComplete( complete );
            
            getWizard().getContainer().updateButtons();
        }
    };
    
    public ResourceSearchPage( String pageName ) {
        super(pageName);
        setTitle(Messages.ResourceSelectionPage_title); 
        setMessage(Messages.ResourceSelectionPage_message);
        setDescription(Messages.ResourceSelectionPage_description);
        setImageDescriptor(CatalogUIPlugin.getDefault().getImageDescriptor(ImageConstants.CHOOSE_LAYER_WIZARD));
    }

    @Override
    public ResourceSearchState getState() {
        return (ResourceSearchState) super.getState();
    }
    @Override
    public void setState(State state) {
        if( state instanceof ResourceSearchState){
            super.setState(state);            
        }
        else {
            throw new IllegalArgumentException("ResourceSearchState required");
        }
    }
    
    @Override
    public void createControl( Composite parent ) {
        search = new ResourceSearchComposite(parent, SWT.NULL){
            protected void reviewResults(List<IResolve> resolves) {
                resolves.add( ResourceSearchState.IMPORT_PLACEHOLDER );
            }
        };
        setControl( search );
        
        // use the state to initialize ui
        ResourceSearchState state = (ResourceSearchState) getState();        
        setInput(state);
    }

    @Override
    public void shown() {
        setInput( getState() );
    }
    private void listen( boolean listen ){
        if( listen ){
            search.addSelectionChangedListener( listener );
        }
        else {
            search.removeSelectionChangedListener( listener );
        }
    }
    private void setInput( ResourceSearchState state ) {
        try {
            listen(false);
            search.setSearchText( state.getSearch() );   
        }
        finally {
            listen( true );
        }
    }

    Button findButton( Control[] children, int id ) {
        if (((Integer) getShell().getDefaultButton().getData()).intValue() == id) 
            return getShell().getDefaultButton();

        for( Control child : children ) {
            if (child instanceof Button) {
                Button button = (Button) child;
                if (((Integer) button.getData()).intValue() == id)
                    return button;
            }
            if (child instanceof Composite) {
                Composite composite = (Composite) child;
                Button button = findButton(composite.getChildren(), id);
                if (button != null)
                    return button;
            }
        }
        return null;
    }

    public void pageChanged( PageChangedEvent event ) {
    }
    @Override
    public boolean canFlipToNextPage() {
        boolean more = super.canFlipToNextPage();

        Set<IResolve> selected = getState().getSelected();
        if( selected == null || selected.isEmpty() ){
            return false; // please select something
        }
        if( selected.contains( ResourceSearchState.IMPORT_PLACEHOLDER )){
            return true; // we will generate the next page
        }
            
        return more;
    }
    
    
}
