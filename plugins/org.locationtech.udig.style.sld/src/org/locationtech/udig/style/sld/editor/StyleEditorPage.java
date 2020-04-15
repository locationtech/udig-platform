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
package org.locationtech.udig.style.sld.editor;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.geotools.styling.Style;
import org.geotools.styling.StyledLayerDescriptor;
import org.locationtech.udig.style.internal.StyleLayer;
import org.locationtech.udig.style.sld.IEditorPageContainer;
import org.locationtech.udig.style.sld.IStyleEditorPage;
import org.locationtech.udig.style.sld.IStyleEditorPageContainer;

/**
 * Provides a user interface (by extension point) for pages in the SLD Editor
 * 
 * @author chorner
 * @since 1.1
 */
public abstract class StyleEditorPage extends DialogPage implements IStyleEditorPage {

    /** Extension Point ID we are processing */
    protected static final String XPID = "org.locationtech.udig.style.sld.StyleEditorPage"; //$NON-NLS-1$

    /**
     * Context for the page to run in; should have access to style blackboard and so forth.
     */
	private IStyleEditorPageContainer container = null; 
    
    /**
     * Description label.
     * 
     * @see #createDescriptionLabel(Composite)
     */
    private Label descriptionLabel;

    /**
     * Caches size of page.
     */
    private Point size = null;

    public StyleEditorPage() {
    }

    public void applyData(Object data) {
    	
    }
    
    /**
     * Creates the page Control.
     * 
     * Subclasses should not override this method, but instead use createPageContent to create their contents.
     * 
     * @param parent the parent composite
     */
    public void createControl( Composite parent ) {
    	Composite page = new Composite(parent, SWT.NONE); 
    	page.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        page.setLayout(layout);
        Dialog.applyDialogFont(page);
        // initialize the dialog units
        initializeDialogUnits(page);
        //save the control
        setControl(page);
        
        descriptionLabel = createDescriptionLabel(page);
        if (descriptionLabel != null) {
            descriptionLabel.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
        }

        //create the contents
        createPageContent(page);

        //invoke the layout
        parent.layout();
        
        container.addPageChangedListener(new IPageChangedListener() {

            public void pageChanged( PageChangedEvent event ) {
                if (getEditorPage() == event.getSelectedPage()) {
                    gotFocus(); //the current page just got focus, call the abstract method
                }
            }
            
        });
    }

    /**
     * Creates the page content.
     * 
     * Subclasses must define this method and create their child controls here.
     * 
     * @param parent composite to put the page content in
     */
    public abstract void createPageContent( Composite parent );
    
    public abstract String getLabel();
    
    /**
     * Initializes the page (optional)
     * 
     * Subclasses may override this method if they need to initialize.
     *
     * @param bench
     */
    public void init(IWorkbench bench) {
        //don't do anything by default
    }
    
    /**
     * Computes the size for this page's UI control.
     * <p>
     * The default implementation of this <code>IPreferencePage</code>
     * method returns the size set by <code>setSize</code>; if no size
     * has been set, but the page has a UI control, the framework
     * method <code>doComputeSize</code> is called to compute the size.
     * </p>
     *
     * @return the size of the preference page encoded as
     *   <code>new Point(width,height)</code>, or 
     *   <code>(0,0)</code> if the page doesn't currently have any UI component
     */
    public Point computeSize() {
        if (size != null)
            return size;
        Control control = getControl();
        if (control != null) {
            size = doComputeSize();
            return size;
        }
        return new Point(0, 0);
    }

    /**
     * Computes the size needed by this page's UI control.
     * <p>
     * All pages should override this method and set the appropriate sizes
     * of their widgets, and then call <code>super.doComputeSize</code>.
     * </p>
     *
     * @return the size of the preference page encoded as
     *   <code>new Point(width,height)</code>
     */
    protected Point doComputeSize() {
    	Control page = getControl(); 
        if (descriptionLabel != null && page != null) {
            Point bodySize = page.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
            GridData gd = (GridData) descriptionLabel.getLayoutData();
            gd.widthHint = bodySize.x;
        }
        return getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
    }
    
    /**
     * Invoked when the user clicks cancel.
     */
    public abstract boolean performCancel();

    public IStyleEditorPageContainer getContainer() {
        return container;
    }
    
    public void setContainer(IEditorPageContainer container) {
    	if (container instanceof IStyleEditorPageContainer) {
    		this.container = (IStyleEditorPageContainer) container;
    	}
    }

    public void setSize( Point size ) {
        this.size = size;
    }

    @Override
    public void dispose() {
        descriptionLabel = null;
        container = null;
        size = null;
    }

    /**
     * Returns an error message, if applicable. The dialog automagically calls this method and
     * displays it with an error icon if it returns a non-null string. Subclasses should determine
     * the state of their respective pages in this method and return null if everything is okay.
     */
    @Override
    public abstract String getErrorMessage();

    /**
     * Creates and returns an SWT label under the given composite.
     *
     * @param parent the parent composite
     * @return the new label
     */
    protected Label createDescriptionLabel(Composite parent) {
        Label result = null;
        String description = getDescription();
        if (description != null) {
            result = new Label(parent, SWT.WRAP);
            result.setFont(parent.getFont());
            result.setText(description);
        }
        return result;
    }
    
    /**
     * Determines if the page contents are valid input. Subclasses should override if invalid input
     * is possible.
     */
    public boolean isValid() {
        return true;
    }
    
    /**
     * Returns the StyledLayerDescriptor from the style object on the blackboard (and creates one if it is missing).
     *
     * @return SLD Object
     */
    public StyledLayerDescriptor getSLD() {
        return container.getSLD();
    }
    
    /**
     * Returns the current style object from the dialog.
     *
     * @return style
     */
    public Style getStyle() {
        return container.getStyle(); 
    }

    /**
     * Sets the current style object (on our styleblackboard clone).
     * 
     * @param style
     */
    public void setStyle(Style style) {
        container.setStyle(style); 
    }
    
    public StyleEditorPage getEditorPage() {
        return this;
    }
    
    public StyleLayer getSelectedLayer() {
        return container.getSelectedLayer();
    }

    /**
     * Each subclass must implement this method which is called each time the page obtains focus.
     * <p>
     * You can use this method to check out the style and update the state of any widgets prior to display.
     * Implementations Hint: The easiest thing to do is call IEditorPage.refresh() - which you have filled in
     * to update the state of the widgets. You can optimize if you like by checking existing widget state and
     * only updating the controls as needed.
     * </p>
     */
    public abstract void gotFocus();
    
    /**
     * Each subclass must implement this method which is called each time the style object is modified on ANY page.
     * <p>
     * A page implementation will usually just update its contents from scratch; as an optimization
     * you can look at the provided source object and see if you can avoid updating everything.
     * </p>
     * @param source Source of change (often a FeatureTypeStyle or Rule)
     */
    public abstract void styleChanged( Object source );
    
}
