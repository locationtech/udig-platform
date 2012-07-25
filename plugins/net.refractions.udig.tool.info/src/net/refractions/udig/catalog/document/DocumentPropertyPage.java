/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
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
package net.refractions.udig.catalog.document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.miginfocom.swt.MigLayout;
import net.refractions.udig.catalog.IDocument.Type;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IHotlink.HotlinkDescriptor;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Allows configuration of IGeoResource persisted properties defining hotlinks.
 * 
 * @author Jody Garnett (LISAsoft)
 * @since 1.3.2
 */
public class DocumentPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {

    private static final HotlinkDescriptor[] EMPTY = new HotlinkDescriptor[0];

    private Button enableButton;

    private TableViewer table;

    private Button addButton;

    private Button editButton;

    private Button removeButton;

    private List<HotlinkDescriptor> model = new ArrayList<HotlinkDescriptor>();

    private Label tableLabel;

//    @Override
//    protected Point doComputeSize() {
//        return super.doComputeSize();
//    }
//    
    @Override
    protected Control createContents(Composite parent) {
        final IGeoResource resource = (IGeoResource) getElement().getAdapter(IGeoResource.class);
        
        boolean isEnabled = BasicHotlinkResolveFactory.hasHotlinkDescriptors( resource );
        boolean hasSchema = resource.canResolve(SimpleFeatureSource.class);
        model = new ArrayList<HotlinkDescriptor>();
        if( isEnabled && hasSchema ){
            model.addAll( BasicHotlinkResolveFactory.getHotlinkDescriptors(resource));
        }
        
        Composite page = new Composite( parent, SWT.NONE );
        page.setLayout(new MigLayout("insets 0", "[][grow,fill][]", "[][][][][fill][]"));
        
        Label label;

        label = new Label(page, SWT.SINGLE);
        label.setText("Document");
        label.setLayoutData("cell 0 0, gapx 0 unrelated");
        label.setEnabled(hasSchema);
        
        // Area of Interest filter button
        enableButton = new Button(page, SWT.CHECK);
        enableButton.setText("Enable Hotlink");
        enableButton.setLayoutData("cell 1 0 2 1, left, grow x");
        enableButton.setSelection(isEnabled);
        enableButton.setEnabled(hasSchema);
        enableButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean isEnabled = enableButton.getSelection();
                enableTableAndButtons(isEnabled);
                if( isEnabled ){
                    table.setInput(model);
                    table.refresh();
                }
                else {
                    table.setInput(EMPTY);
                    table.refresh();
                }
            }
        });
        
        tableLabel = new Label(page, SWT.SINGLE);
        tableLabel.setText("Document Attributes");
        tableLabel.setLayoutData("cell 0 1 2 1, width pref!, left");
        
        
        addButton = new Button(page, SWT.CENTER);
        addButton.setText("Add");
        addButton.setLayoutData("cell 2 3, growx");
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ISelection sel = table.getSelection();
                if( !sel.isEmpty() && sel instanceof StructuredSelection ){
                    StructuredSelection selection =(StructuredSelection) sel;
                    HotlinkDescriptor descriptor = (HotlinkDescriptor) selection.getFirstElement();
                    
                    int index = model.indexOf(descriptor);
                    addDescriptor( index );
                }
                else {
                    addDescriptor( -1 );
                }
            }
        });
        

        editButton = new Button(page, SWT.CENTER);
        editButton.setText("Edit");
        editButton.setLayoutData("cell 2 4, growx");
        editButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ISelection sel = table.getSelection();
                if( !sel.isEmpty() && sel instanceof StructuredSelection ){
                    StructuredSelection selection =(StructuredSelection) sel;
                    HotlinkDescriptor descriptor = (HotlinkDescriptor) selection.getFirstElement();
                    editDescriptor( descriptor );
                }
            }
        });
        

        removeButton = new Button(page, SWT.CENTER);
        removeButton.setText("Remove");
        removeButton.setLayoutData("cell 2 6, aligny bottom, growx");
        removeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ISelection sel = table.getSelection();
                if( !sel.isEmpty() && sel instanceof StructuredSelection ){
                    StructuredSelection selection =(StructuredSelection) sel;
                    HotlinkDescriptor descriptor = (HotlinkDescriptor) selection.getFirstElement();
                    
                    removeDescriptor( descriptor );
                }
            }
        });
        

        table = new TableViewer(page, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        table.setContentProvider(ArrayContentProvider.getInstance());
        table.getControl().setLayoutData(
                "cell 0 3 2 4, grow, height 200:50%:70%,width 300:pref:100%");
        
        TableViewerColumn column = new TableViewerColumn(table, SWT.NONE);
        column.getColumn().setWidth(100);
        column.getColumn().setMoveable(false);
        column.getColumn().setResizable(true);
        column.getColumn().setText("Attribute");
        column.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                HotlinkDescriptor descriptor = (HotlinkDescriptor) element;
                return descriptor.getAttributeName();
            }
        });
        column = new TableViewerColumn(table, SWT.NONE);
        column.getColumn().setWidth(60);
        column.getColumn().setMoveable(false);
        column.getColumn().setResizable(true);
        column.getColumn().setText("Type");
        column.getColumn().setAlignment(SWT.CENTER);
        column.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                HotlinkDescriptor descriptor = (HotlinkDescriptor) element;
                return descriptor.getType().toString();
            }
        });
        column = new TableViewerColumn(table, SWT.NONE);
        column.getColumn().setWidth(140);
        column.getColumn().setMoveable(false);
        column.getColumn().setResizable(true);
        column.getColumn().setText("Definition");
        column.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return "--";
            }
        });
        table.getTable().setHeaderVisible(true);
        table.getTable().setLinesVisible(true);
        table.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                ISelection sel = event.getSelection();
                editButton.setEnabled( !sel.isEmpty() );
                removeButton.setEnabled( !sel.isEmpty() );
            }
        });
        table.setInput( model );
        
        
        enableTableAndButtons( isEnabled );
        return page;
    }
    @Override
    public boolean performOk() {
        final IGeoResource resource = (IGeoResource) getElement().getAdapter(IGeoResource.class);
        
        if( enableButton.getSelection() ){
            if( table.getInput() == EMPTY ){
                BasicHotlinkResolveFactory.putHotlinkDescriptors( resource, new ArrayList<HotlinkDescriptor>() );
            }
            else {
                BasicHotlinkResolveFactory.putHotlinkDescriptors( resource, model );
            }
        }
        else {
            BasicHotlinkResolveFactory.clearHotlinkDescriptors(resource);
        }
        return super.performOk();
    }
    @Override
    protected void performDefaults() {
        final IGeoResource resource = (IGeoResource) getElement().getAdapter(IGeoResource.class);
        
        boolean isEnabled = BasicHotlinkResolveFactory.hasHotlinkDescriptors( resource );
        boolean hasSchema = resource.canResolve(SimpleFeatureSource.class);
        if( isEnabled && hasSchema ){
            model.clear();
            model.addAll( BasicHotlinkResolveFactory.getHotlinkDescriptors(resource));
            table.refresh();
        }
        else {
            table.setInput(EMPTY);
        }
        super.performDefaults();
    }
    @Override
    public boolean performCancel() {
        return super.performCancel(); // no change
    }
    public void enableTableAndButtons( boolean isEnabled ){
        tableLabel.setEnabled( isEnabled );
        table.getControl().setEnabled( isEnabled );
        boolean hasSelection = isEnabled && !table.getSelection().isEmpty();
        
        addButton.setEnabled( isEnabled );
        editButton.setEnabled( hasSelection );
        removeButton.setEnabled( hasSelection );
    }
    protected void removeDescriptor(HotlinkDescriptor descriptor) {
        model.remove( descriptor );
        table.refresh();
    }

    protected void editDescriptor(HotlinkDescriptor descriptor) {
        table.refresh(descriptor,  true, true );
    }

    protected void addDescriptor(int index) {
        HotlinkDescriptor descriptor = promptDescriptor();
        if( index == -1 ){
            model.add( descriptor );
        }
        else {
            model.add(index, descriptor );
        }
        table.refresh();
    }

    private HotlinkDescriptor promptDescriptor() {
        return new HotlinkDescriptor("fred", Type.FILE );
    }

}
