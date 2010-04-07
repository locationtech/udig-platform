/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.project.ui.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.render.impl.Styling;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * A wizard page for choosing the layer's styles
 * 
 * @author jeichar
 * @since 0.3
 */
public class MapStylePage extends WizardPage {

    List layers;
    Layer currentLayer;
    HashMap map = new HashMap();

    /**
     * Construct <code>MapNewPage</code>.
     * 
     * @param layers The selected layers
     */
    protected MapStylePage( List layers ) {
        super(
                Messages.MapStylePage_editStyles, Messages.MapStylePage_editStyles, Images.getDescriptor(ImageConstants.NEWMAP_WIZBAN));  
        setDescription(Messages.MapStylePage_editStyles_description); 
        this.layers = layers;
    }

    /**
     * TODO summary sentence for createControl ...
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @param parent
     */
    public void createControl( Composite parent ) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));

        final ListViewer layersView = new ListViewer(composite, SWT.BORDER);
        layersView.setContentProvider(new IStructuredContentProvider(){

            public Object[] getElements( Object inputElement ) {
                if (inputElement instanceof List) {
                    return ((List) inputElement).toArray();
                }
                return null;
            }

            public void dispose() {
                // do nothing
            }

            public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
                // do nothing
            }
        });

        layersView.setLabelProvider(new LabelProvider(){
            /**
             * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
             */
            public String getText( Object element ) {
                if (element instanceof Layer) {
                    return ((Layer) element).getName();
                }
                return null;
            }
        });
        layersView.setInput(layers);
        layersView.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final ListViewer styleView = new ListViewer(composite, SWT.BORDER);
        styleView.setContentProvider(new IStructuredContentProvider(){

            public Object[] getElements( Object inputElement ) {
                if (inputElement instanceof Collection) {
                    return ((Collection) inputElement).toArray();
                }
                return null;
            }

            public void dispose() {
                // do nothing
            }

            public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
                viewer.refresh();
            }
        });
        styleView.setLabelProvider(new LabelProvider(){
            /**
             * TODO summary sentence for getText ...
             * 
             * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
             * @param element
             */
            public String getText( Object element ) {
                if (element instanceof String) {
                    return (String) element;
                }
                return null;
            }
        });
        styleView.addSelectionChangedListener(new ISelectionChangedListener(){

            public void selectionChanged( SelectionChangedEvent event ) {
                if (event.getSelection() instanceof IStructuredSelection) {
                    //IStructuredSelection sel = (IStructuredSelection) event.getSelection();
                    /*
                     * FIXME: Style is no longer used? for( Iterator iter = sel.iterator();
                     * iter.hasNext(); ) { String styleName = (String) iter.next(); if(
                     * styleName.equals("default")) return; StyleImpl style=(StyleImpl)
                     * RenderFactory.eINSTANCE.createStyle(); style.setStyle(
                     * Styling.getStyle(((Integer)Styling.STYLES.get(styleName)).intValue(),
                     * currentLayer.getMetadata().getTypeName()) ); // currentLayer.setStyle(style);
                     * map.put(currentLayer, styleName); }
                     */
                }
            }

        });
        styleView.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        layersView.addSelectionChangedListener(new ISelectionChangedListener(){

            public void selectionChanged( SelectionChangedEvent event ) {
                if (event.getSelection() instanceof IStructuredSelection) {
                    IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                    Object o = selection.getFirstElement();
                    if (selection.size() > 1)
                        layersView.setSelection(new StructuredSelection(o), true);
                    currentLayer = (Layer) o;
                    styleView.setInput(Styling.getStyleNames(currentLayer));
                    Object s = map.get(currentLayer);
                    if (s != null)
                        styleView.setSelection(new StructuredSelection(map.get(currentLayer)));
                }
            }

        });
        setControl(composite);
        setPageComplete(true);
    }

    /**
     * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
     */
    public boolean canFlipToNextPage() {
        return false;
    }

}
