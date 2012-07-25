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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.miginfocom.swt.MigLayout;
import net.refractions.udig.catalog.IDocument.Type;
import net.refractions.udig.catalog.IDocument;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IHotlink.HotlinkDescriptor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

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

    // @Override
    // protected Point doComputeSize() {
    // return super.doComputeSize();
    // }
    //
    @Override
    protected Control createContents(Composite parent) {
        final IGeoResource resource = (IGeoResource) getElement().getAdapter(IGeoResource.class);

        boolean isEnabled = BasicHotlinkResolveFactory.hasHotlinkDescriptors(resource);
        boolean hasSchema = resource.canResolve(SimpleFeatureSource.class);
        model = new ArrayList<HotlinkDescriptor>();
        if (isEnabled && hasSchema) {
            model.addAll(BasicHotlinkResolveFactory.getHotlinkDescriptors(resource));
        }

        Composite page = new Composite(parent, SWT.NONE);
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
                if (isEnabled) {
                    table.setInput(model);
                    table.refresh();
                } else {
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
                if (!sel.isEmpty() && sel instanceof StructuredSelection) {
                    StructuredSelection selection = (StructuredSelection) sel;
                    HotlinkDescriptor descriptor = (HotlinkDescriptor) selection.getFirstElement();

                    int index = model.indexOf(descriptor);
                    addDescriptor(index);
                } else {
                    addDescriptor(-1);
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
                if (!sel.isEmpty() && sel instanceof StructuredSelection) {
                    StructuredSelection selection = (StructuredSelection) sel;
                    HotlinkDescriptor descriptor = (HotlinkDescriptor) selection.getFirstElement();
                    editDescriptor(descriptor);
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
                if (!sel.isEmpty() && sel instanceof StructuredSelection) {
                    StructuredSelection selection = (StructuredSelection) sel;
                    HotlinkDescriptor descriptor = (HotlinkDescriptor) selection.getFirstElement();

                    removeDescriptor(descriptor);
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
                editButton.setEnabled(!sel.isEmpty());
                removeButton.setEnabled(!sel.isEmpty());
            }
        });
        table.setInput(model);

        enableTableAndButtons(isEnabled);
        return page;
    }

    @Override
    public boolean performOk() {
        final IGeoResource resource = (IGeoResource) getElement().getAdapter(IGeoResource.class);

        if (enableButton.getSelection()) {
            if (table.getInput() == EMPTY) {
                BasicHotlinkResolveFactory.putHotlinkDescriptors(resource,
                        new ArrayList<HotlinkDescriptor>());
            } else {
                BasicHotlinkResolveFactory.putHotlinkDescriptors(resource, model);
            }
        } else {
            BasicHotlinkResolveFactory.clearHotlinkDescriptors(resource);
        }
        return super.performOk();
    }

    @Override
    protected void performDefaults() {
        final IGeoResource resource = (IGeoResource) getElement().getAdapter(IGeoResource.class);

        boolean isEnabled = BasicHotlinkResolveFactory.hasHotlinkDescriptors(resource);
        boolean hasSchema = resource.canResolve(SimpleFeatureSource.class);
        if (isEnabled && hasSchema) {
            model.clear();
            model.addAll(BasicHotlinkResolveFactory.getHotlinkDescriptors(resource));
            table.refresh();
        } else {
            table.setInput(EMPTY);
        }
        super.performDefaults();
    }

    @Override
    public boolean performCancel() {
        return super.performCancel(); // no change
    }

    public void enableTableAndButtons(boolean isEnabled) {
        tableLabel.setEnabled(isEnabled);
        table.getControl().setEnabled(isEnabled);
        boolean hasSelection = isEnabled && !table.getSelection().isEmpty();

        addButton.setEnabled(isEnabled);
        editButton.setEnabled(hasSelection);
        removeButton.setEnabled(hasSelection);
    }

    protected void removeDescriptor(HotlinkDescriptor descriptor) {
        model.remove(descriptor);
        table.refresh();
    }

    protected void editDescriptor(HotlinkDescriptor descriptor) {
        final int index = table.getTable().getSelectionIndex();
        
        // look up shell now from the display thread
        final Shell shell = DocumentPropertyPage.this.getShell();
        IShellProvider shellProvider = new IShellProvider() {
            public Shell getShell() {
                return shell;
            }
        };
        final HotlinkDescriptorDialog prompt = new HotlinkDescriptorDialog(shellProvider);
        HotlinkDescriptor copy = new HotlinkDescriptor(descriptor);
        prompt.setDescriptor(copy);
        prompt.openInJob(new Runnable() {
            @Override
            public void run() {
                HotlinkDescriptor edited = prompt.getDescriptor();
                if (edited != null && !edited.isEmpty()) {
                    model.set(index, edited);
                    table.refresh();
                    table.setSelection( new StructuredSelection( edited ) );
                }
            }
        });
    }

    protected void addDescriptor(final int index) {
     // look up shell now from the display thread
        final Shell shell = DocumentPropertyPage.this.getShell();
        IShellProvider shellProvider = new IShellProvider() {
            public Shell getShell() {
                return shell;
            }
        };
        final HotlinkDescriptorDialog prompt = new HotlinkDescriptorDialog(shellProvider);
        prompt.openInJob(new Runnable() {
            @Override
            public void run() {
                HotlinkDescriptor created = prompt.getDescriptor();
                if (created != null && !created.isEmpty()) {
                    if (index == -1 ) {
                        model.add(created); // append to end
                    } else {
                        int insert = index +0;
                        if( insert < model.size() ){
                            model.add(insert, created);
                        }
                        else {
                            model.add(created); // append to end
                        }
                    }
                    table.refresh();
                    table.setSelection( new StructuredSelection( created ) );
                }
            }
        });
    }

    /**
     * Dialog used to create or edit HotlinkDescriptor.
     * <p>
     * In order to do its job this dialog needs access to a SimpleFeatureType (which must be fetched
     * in a background job). As such we expect this dialog to be opened from a background job, and
     * be configured with a Runnable to invoke when the OKAY button is pressed.
     * 
     * @author Jody Garnett (LISAsoft)
     * @since 1.3.2
     */
    class HotlinkDescriptorDialog extends Dialog {
        HotlinkDescriptor descriptor;

        private SimpleFeatureType schema;

        private ComboViewer attributeViewer;

        private ComboViewer typeViewer;

        protected HotlinkDescriptorDialog(IShellProvider parentShell) {
            super(parentShell);
            this.descriptor = new HotlinkDescriptor(); // empty if creating a new one
        }

        public void setSchema(SimpleFeatureType schema) {
            this.schema = schema;
        }

        public HotlinkDescriptor getDescriptor() {
            return descriptor;
        }

        public void setDescriptor(HotlinkDescriptor descriptor) {
            this.descriptor = descriptor;
        }

        /**
         * Open the dialog from a background job (used to safely fetch the schema), the okayRunnable
         * can be used to update the user interface if the user presses the OK button.
         * 
         * @param okayRunnable
         */
        public void openInJob(final Runnable okayRunnable) {
            Job job = new Job("Prompt Hotlink Dscriptor") {
                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    IGeoResource resource = (IGeoResource) getElement().getAdapter(
                            IGeoResource.class);
                    SimpleFeatureType schema = null;
                    if (resource.canResolve(SimpleFeatureType.class)) {
                        try {
                            schema = resource.resolve(SimpleFeatureType.class, monitor);
                        } catch (IOException e) {
                        }
                    }
                    if (resource.canResolve(SimpleFeatureSource.class)) {
                        SimpleFeatureSource featureSource;
                        try {
                            featureSource = resource.resolve(SimpleFeatureSource.class, monitor);
                            schema = featureSource != null ? featureSource.getSchema() : null;
                        } catch (IOException e) {
                        }
                    }
                    setSchema(schema);
                    Display display = getControl().getDisplay();
                    display.asyncExec( new Runnable(){
                        @Override
                        public void run() {
                            setBlockOnOpen(true);
                        
                            int code = open();
                            if (code == Window.OK && okayRunnable != null) {
                                okayRunnable.run();
                            }
                        }
                    });
                    return Status.OK_STATUS;
                }
            };
            job.schedule();
        }

        /** Shortlist hotlink candidates */
        List<String> getSchemaCandidates() {
            List<String> list = new ArrayList<String>();
            if (schema != null) {
                for( AttributeDescriptor attribute : schema.getAttributeDescriptors() ){
                    if( String.class.isAssignableFrom( attribute.getType().getBinding() ) ){
                        list.add( attribute.getLocalName() );
                    }
                }
            }
            return list;
        }

        @Override
        protected Control createDialogArea(Composite parent) {
            Composite composite = new Composite(parent, SWT.NONE);
            MigLayout layout = new MigLayout("insets panel", "[][grow]", "[][][]");
            composite.setLayout(layout);
            composite.setLayoutData(new GridData(GridData.FILL_BOTH));

            Label label = new Label(composite, SWT.SINGLE);
            label.setText("Attribute");
            label.setLayoutData("cell 0 0, gapx unrelated");

            attributeViewer = new ComboViewer(composite);
            attributeViewer.setContentProvider(ArrayContentProvider.getInstance());
            attributeViewer.getControl().setLayoutData("cell 1 0, growx");
            List<String> attributeNames = getSchemaCandidates();
            attributeViewer.setInput(attributeNames);
            if( !descriptor.isEmpty() ){
                attributeViewer.setSelection( new StructuredSelection( descriptor.getAttributeName()) );
            }
            
            label = new Label(composite, SWT.SINGLE);
            label.setText("Document:");
            label.setLayoutData("cell 0 1, gapx unrelated");

            typeViewer = new ComboViewer(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
            typeViewer.setContentProvider(ArrayContentProvider.getInstance());
            typeViewer.setInput(IDocument.Type.values());
            typeViewer.getControl().setLayoutData("cell 1 1");
            typeViewer.setSelection(new StructuredSelection(descriptor.getType()), true);

            label = new Label(composite, SWT.SINGLE);
            label.setText("Action:");
            label.setLayoutData("cell 0 2, gapx unrelated");

            applyDialogFont(composite);
            return composite;
        }

        @Override
        protected void okPressed() {
            String attributeName = attributeViewer.getCombo().getText();
            String action = null;
            Type type = Type.WEB;
            if (typeViewer.getSelection() instanceof StructuredSelection) {
                StructuredSelection selection = (StructuredSelection) typeViewer.getSelection();
                if (!selection.isEmpty()) {
                    type = (Type) selection.getFirstElement();
                }
            }
            descriptor = new HotlinkDescriptor(attributeName, type, action);
            super.okPressed();
        }
        @Override
        protected void cancelPressed() {
            descriptor = null;
            super.cancelPressed();
        }
    }
}
