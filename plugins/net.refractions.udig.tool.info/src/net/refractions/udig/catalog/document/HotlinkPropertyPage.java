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
import java.util.List;
import java.util.Map;

import net.miginfocom.swt.MigLayout;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * Allows configuration of IGeoResource persisted properties defining hotlinks.
 * 
 * @author Jody Garnett (LISAsoft)
 * @since 1.3.2
 */
public class HotlinkPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {

    private Button enableButton;

    private TableViewer table;

    private Button addButton;

    private Button editButton;

    private Button removeButton;

    private List<HotlinkDescriptor> model = new ArrayList<HotlinkDescriptor>();

    @Override
    protected Control createContents(Composite parent) {
        final IGeoResource resource = (IGeoResource) getElement();
        Map<String, Serializable> properties = resource.getPersistentProperties();
        model = new ArrayList<HotlinkDescriptor>();
        model.addAll( BasicHotlinkResolveFactory.hotlinkDescriptors(resource));

        
        Composite page = new Composite( parent, SWT.NONE );
        page.setLayout(new MigLayout("insets 0", "[][grow,fill][]", "[][][fill][]"));
        
        Label label;

        label = new Label(page, SWT.SINGLE);
        label.setText("Hotlink");
        label.setLayoutData("cell 0 0, aligny top, gapx 0 unrelated");

        // Area of Interest filter button
        enableButton = new Button(page, SWT.CHECK);
        enableButton.setText("Enable Hotlink");
        enableButton.setLayoutData("cell 1 0 2 1, left, grow x");
        enableButton.setEnabled(properties.containsKey(BasicHotlinkResolveFactory.HOTLINK));

        label = new Label(page, SWT.SINGLE);
        label.setText("Hotlinks Attributes");
        label.setLayoutData("cell 0 1 2 1, width pref!, left");

        addButton = new Button(page, SWT.CENTER);
        addButton.setText("Add");
        addButton.setLayoutData("cell 2 1 1 1, grow");
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
        editButton.setLayoutData("cell 3 1 1 1, grow");
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
        removeButton.setLayoutData("cell 4 1 1 1, grow");
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
                "cell 1 0 2 4, grow, height 200:50%:70%,width 300:pref:100%");

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
        return null;
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
    }

    private HotlinkDescriptor promptDescriptor() {
        // TODO Auto-generated method stub
        return null;
    }

}
