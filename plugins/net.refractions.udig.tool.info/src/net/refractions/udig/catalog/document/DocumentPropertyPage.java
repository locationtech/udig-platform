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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.miginfocom.swt.MigLayout;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.document.IDocument.Type;
import net.refractions.udig.catalog.document.IHotlinkSource.HotlinkDescriptor;
import net.refractions.udig.catalog.internal.shp.ShpGeoResourceImpl;
import net.refractions.udig.catalog.shp.ShpDocPropertyParser;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
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

    private Button attachmentEnable;
    
    private Button documentEnable;
    
    private Button hotlinkEnable;

    private Label hotlinkLabel;
    
    private TableViewer hotlinkViewer;

    private Button addHotlink;

    private Button editHotlink;

    private Button removeButton;

    private List<HotlinkDescriptor> hotlinkList = new ArrayList<HotlinkDescriptor>();

    private Label tableLabel;

    private ShpDocPropertyParser propParser;
    
    // @Override
    // protected Point doComputeSize() {
    // return super.doComputeSize();
    // }
    //
    @Override
    protected Control createContents(Composite parent) {
        final IGeoResource resource = (IGeoResource) getElement().getAdapter(IGeoResource.class);

        if( resource instanceof ShpGeoResourceImpl ){
            propParser = new ShpDocPropertyParser(resource.getIdentifier(), null);
        }
        boolean isEnabled = BasicHotlinkResolveFactory.hasHotlinkDescriptors(resource);
        boolean hasAttachmentSource = resource.canResolve(IAttachmentSource.class);
        boolean hasSchema = resource.canResolve(SimpleFeatureSource.class);
        hotlinkList = new ArrayList<HotlinkDescriptor>();
        if (isEnabled && hasSchema) {
            hotlinkList.addAll(BasicHotlinkResolveFactory.getHotlinkDescriptors(resource));
        }

        Composite page = new Composite(parent, SWT.NO_SCROLL);
        page.setLayout(new MigLayout("insets 0", "[][grow,fill][]", "[][][][][fill][]"));

        Label label;

        label = new Label(page, SWT.SINGLE);
        label.setText("Attachment");
        label.setLayoutData("cell 0 0, gapx related, gapy unrelated");
        label.setEnabled(hasSchema);
        
        Button enableAttachment = new Button(page, SWT.CHECK);
        enableAttachment.setText("Enable support for feature attachments");
        enableAttachment.setLayoutData("cell 1 0 2 1, left, grow x");
        enableAttachment.setEnabled(false);
        
        enableAttachment.setSelection( hasAttachmentSource );
        label = new Label(page, SWT.SINGLE);
        label.setText("Hotlink");
        label.setLayoutData("cell 0 1, gapx related, gapy related");
        label.setEnabled(hasSchema);

        // Area of Interest filter button
        hotlinkEnable = new Button(page, SWT.CHECK);
        hotlinkEnable.setText("Enable hotlink support on marked attributes");
        hotlinkEnable.setLayoutData("cell 1 1 2 1, left, grow x");
        hotlinkEnable.setSelection(isEnabled);
        hotlinkEnable.setEnabled(hasSchema);
        hotlinkEnable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean isEnabled = hotlinkEnable.getSelection();
                enableTableAndButtons(isEnabled);
                if (isEnabled) {
                    hotlinkViewer.setInput(hotlinkList);
                    hotlinkViewer.refresh();
                } else {
                    hotlinkViewer.setInput(EMPTY);
                    hotlinkViewer.refresh();
                }
            }
        });

        hotlinkLabel = new Label(page, SWT.SINGLE);
        hotlinkLabel.setText("Hotlink Attributes");
        hotlinkLabel.setLayoutData("cell 0 2, width pref!, gapx para");

        addHotlink = new Button(page, SWT.CENTER);
        addHotlink.setText("Add...");
        addHotlink.setLayoutData("cell 2 3, growx");
        addHotlink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ISelection sel = hotlinkViewer.getSelection();
                if (!sel.isEmpty() && sel instanceof StructuredSelection) {
                    StructuredSelection selection = (StructuredSelection) sel;
                    HotlinkDescriptor descriptor = (HotlinkDescriptor) selection.getFirstElement();

                    int index = hotlinkList.indexOf(descriptor);
                    addDescriptor(index);
                } else {
                    addDescriptor(-1);
                }
            }
        });

        editHotlink = new Button(page, SWT.CENTER);
        editHotlink.setText("Edit...");
        editHotlink.setLayoutData("cell 2 4, growx");
        editHotlink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ISelection sel = hotlinkViewer.getSelection();
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
                ISelection sel = hotlinkViewer.getSelection();
                if (!sel.isEmpty() && sel instanceof StructuredSelection) {
                    StructuredSelection selection = (StructuredSelection) sel;
                    HotlinkDescriptor descriptor = (HotlinkDescriptor) selection.getFirstElement();

                    removeDescriptor(descriptor);
                }
            }
        });
        Composite tableComposite = new Composite( page, SWT.NONE );
        TableColumnLayout columnLayout = new TableColumnLayout();
        tableComposite.setLayout( columnLayout );
        tableComposite.setLayoutData("cell 0 3 2 4, grow, height 200:100%:100%,width 300:pref:100%, gapx para");
        
        hotlinkViewer = new TableViewer(tableComposite, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        hotlinkViewer.setContentProvider(ArrayContentProvider.getInstance());
        
        
        TableViewerColumn column = new TableViewerColumn(hotlinkViewer, SWT.NONE);
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
        columnLayout.setColumnData( column.getColumn(), new ColumnWeightData( 30, 100, true ));
        
        column = new TableViewerColumn(hotlinkViewer, SWT.NONE);
        column.getColumn().setWidth(60);
        column.getColumn().setMoveable(false);
        column.getColumn().setResizable(true);
        column.getColumn().setText("Hotlink");
        column.getColumn().setAlignment(SWT.CENTER);
        column.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                HotlinkDescriptor descriptor = (HotlinkDescriptor) element;
                return descriptor.getType().toString();
            }
        });
        columnLayout.setColumnData( column.getColumn(), new ColumnPixelData( 40, true, true) );
        
        column = new TableViewerColumn(hotlinkViewer, SWT.NONE);
        column.getColumn().setWidth(140);
        column.getColumn().setMoveable(false);
        column.getColumn().setResizable(true);
        column.getColumn().setText("Action");
        column.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                HotlinkDescriptor descriptor = (HotlinkDescriptor) element;
                if( descriptor.getConfig() == null ){
                    return "Open";
                }
                return descriptor.getConfig();
            }
        });
        
        columnLayout.setColumnData( column.getColumn(), new ColumnWeightData( 60, 100, true ));
        hotlinkViewer.getTable().setHeaderVisible(true);
        hotlinkViewer.getTable().setLinesVisible(true);
        
        hotlinkViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                ISelection sel = event.getSelection();
                editHotlink.setEnabled(!sel.isEmpty());
                removeButton.setEnabled(!sel.isEmpty());
            }
        });
        hotlinkViewer.setInput(hotlinkList);

        enableTableAndButtons(isEnabled);
        return page;
    }

    @Override
    public boolean performOk() {
        final IGeoResource resource = (IGeoResource) getElement().getAdapter(IGeoResource.class);

        if (hotlinkEnable.getSelection()) {
            if (hotlinkViewer.getInput() == EMPTY) {
                ArrayList<HotlinkDescriptor> empty = new ArrayList<HotlinkDescriptor>();
                BasicHotlinkResolveFactory.putHotlinkDescriptors(resource, empty);
                if( propParser != null ){
                    propParser.setFeatureLinks(Collections.<HotlinkDescriptor>emptyList());
                }
            } else {
                BasicHotlinkResolveFactory.putHotlinkDescriptors(resource, hotlinkList);
            }
        } else {
            BasicHotlinkResolveFactory.clearHotlinkDescriptors(resource);
            if( propParser != null ){
                propParser.setFeatureLinks(Collections.<HotlinkDescriptor> emptyList());
            }
        }
        return super.performOk();
    }

    @Override
    protected void performDefaults() {
        final IGeoResource resource = (IGeoResource) getElement().getAdapter(IGeoResource.class);

        boolean isEnabled = BasicHotlinkResolveFactory.hasHotlinkDescriptors(resource);
        boolean hasSchema = resource.canResolve(SimpleFeatureSource.class);
        if (isEnabled && hasSchema) {
            hotlinkList.clear();
            hotlinkList.addAll(BasicHotlinkResolveFactory.getHotlinkDescriptors(resource));
            hotlinkViewer.refresh();
        } else {
            hotlinkList.clear();
            hotlinkViewer.setInput(EMPTY);
            hotlinkViewer.refresh();
        }
        hotlinkEnable.setSelection(isEnabled);
        super.performDefaults();
    }

    @Override
    public boolean performCancel() {
        return super.performCancel(); // no change
    }

    public void enableTableAndButtons(boolean isEnabled) {
        hotlinkLabel.setEnabled(isEnabled);
        hotlinkViewer.getControl().setEnabled(isEnabled);
        boolean hasSelection = isEnabled && !hotlinkViewer.getSelection().isEmpty();

        addHotlink.setEnabled(isEnabled);
        editHotlink.setEnabled(hasSelection);
        removeButton.setEnabled(hasSelection);
    }

    protected void removeDescriptor(HotlinkDescriptor descriptor) {
        hotlinkList.remove(descriptor);
        hotlinkViewer.refresh();
    }

    protected void editDescriptor(HotlinkDescriptor descriptor) {
        final int index = hotlinkViewer.getTable().getSelectionIndex();
        
        // look up shell now from the display thread
        final Shell shell = DocumentPropertyPage.this.getShell();
        
        final HotlinkDescriptorDialog prompt = new HotlinkDescriptorDialog(shell);
        HotlinkDescriptor copy = new HotlinkDescriptor(descriptor);
        prompt.setDescriptor(copy);
        prompt.openInJob(new Runnable() {
            @Override
            public void run() {
                HotlinkDescriptor edited = prompt.getDescriptor();
                if (edited != null && !edited.isEmpty()) {
                    hotlinkList.set(index, edited);
                    hotlinkViewer.refresh();
                    hotlinkViewer.setSelection( new StructuredSelection( edited ) );
                }
            }
        });
    }

    protected void addDescriptor(final int index) {
     // look up shell now from the display thread
        final Shell shell = DocumentPropertyPage.this.getShell();
        final HotlinkDescriptorDialog prompt = new HotlinkDescriptorDialog(shell);
        prompt.openInJob(new Runnable() {
            @Override
            public void run() {
                HotlinkDescriptor created = prompt.getDescriptor();
                if (created != null && !created.isEmpty()) {
                    if (index == -1 ) {
                        hotlinkList.add(created); // append to end
                    } else {
                        int insert = index +0;
                        if( insert < hotlinkList.size() ){
                            hotlinkList.add(insert, created);
                        }
                        else {
                            hotlinkList.add(created); // append to end
                        }
                    }
                    hotlinkViewer.refresh();
                    hotlinkViewer.setSelection( new StructuredSelection( created ) );
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
    class HotlinkDescriptorDialog extends IconAndMessageDialog {
        HotlinkDescriptor descriptor;

        private SimpleFeatureType schema;

        private ComboViewer attributeViewer;

        private ComboViewer typeViewer;

        private Text actionText;

        private Label actionLabel;

        protected HotlinkDescriptorDialog(Shell parentShell) {
            super(parentShell);
            message = "Define hotlink functionality for an attribute.";
            this.descriptor = new HotlinkDescriptor(); // empty if creating a new one
        }

        @Override
        protected Image getImage() {
            return getQuestionImage();
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
        protected void configureShell(Shell shell) {
            super.configureShell(shell);
            shell.setText("Hotlink Definition");
            shell.setImage( getInfoImage() );
        }
        
        @Override
        protected Control createDialogArea(Composite parent) {
            Composite composite = new Composite(parent, SWT.NONE);
            MigLayout layout = new MigLayout("insets panel", "[][grow]", "[][][]");
            composite.setLayout(layout);
            composite.setLayoutData(new GridData(GridData.FILL_BOTH));

            createMessageArea(composite);
            imageLabel.setLayoutData("cell 0 0, grow");
            messageLabel.setLayoutData("cell 1 0 2 1, grow ");
            
            Label label = new Label(composite, SWT.SINGLE);
            label.setText("Attribute");
            label.setLayoutData("cell 0 1, gapx unrelated");

            attributeViewer = new ComboViewer(composite);
            attributeViewer.setContentProvider(ArrayContentProvider.getInstance());
            attributeViewer.getControl().setLayoutData("cell 1 1, growx");
            List<String> attributeNames = getSchemaCandidates();
            attributeViewer.setInput(attributeNames);
            attributeViewer.addSelectionChangedListener( new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    boolean hasAttribute = !event.getSelection().isEmpty();
                    if( getButton(IDialogConstants.OK_ID) != null ){
                        getButton(IDialogConstants.OK_ID).setEnabled(hasAttribute);
                    }
                }
            });
            
            if( !descriptor.isEmpty() ){
                String attributeName = descriptor.getAttributeName();
                if( attributeNames.contains( attributeName )){
                    attributeViewer.setSelection( new StructuredSelection( attributeName) );
                }
            }
            
            label = new Label(composite, SWT.SINGLE);
            label.setText("Hotlink:");
            label.setLayoutData("cell 0 2, gapx unrelated");

            typeViewer = new ComboViewer(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
            typeViewer.setContentProvider(ArrayContentProvider.getInstance());
            typeViewer.setInput(IDocument.Type.values());
            typeViewer.getControl().setLayoutData("cell 1 2");
            typeViewer.setSelection(new StructuredSelection(descriptor.getType()), true);
            typeViewer.addSelectionChangedListener( new ISelectionChangedListener() {
                public void selectionChanged(SelectionChangedEvent event) {
                    if( !event.getSelection().isEmpty() && event.getSelection() instanceof StructuredSelection ){
                        StructuredSelection selection = (StructuredSelection) event.getSelection();
                        IDocument.Type type = (Type) selection.getFirstElement();
                        switch (type ){
                        case WEB:
                        case FILE:
                            {
                                actionLabel.setEnabled( false );
                                actionText.setText("Open");
                                actionText.setEnabled(false);
                            }
                            break;
                        case ACTION:
                            actionLabel.setEnabled( true );
                            actionText.setText( descriptor.getConfig() == null ? "" : descriptor.getConfig());
                            actionText.setEnabled(false);
                        }
                    }
                }
            });
            
            actionLabel = new Label(composite, SWT.SINGLE);
            actionLabel.setText("Action:");
            actionLabel.setLayoutData("cell 0 3, gapx unrelated");

            actionText = new Text(composite,  SWT.SINGLE );
            String actionConfig = descriptor.getConfig();
            if( actionConfig != null ){
                actionText.setText( actionConfig );
            }
            else {
                actionText.setText( "Open" );
                actionText.setEnabled(false);
            }
            actionText.setEnabled(false);
            actionText.setLayoutData("cell 1 3, growx");
            applyDialogFont(composite);
            return composite;
        }

        @Override
        protected void createButtonsForButtonBar(Composite parent) {
            super.createButtonsForButtonBar(parent); //  // create OK and Cancel buttons by default
            
            boolean hasAttribute = !attributeViewer.getSelection().isEmpty();
            getButton(IDialogConstants.OK_ID).setEnabled(hasAttribute);
        }
        @Override
        protected void okPressed() {
            String attributeName = attributeViewer.getCombo().getText();
            if( attributeName == null || attributeName.isEmpty() ){
                return; // nothing!
            }
            StructuredSelection selection = (StructuredSelection) typeViewer.getSelection();
            Type type = (Type) selection.getFirstElement();
            
            String actionConfig = type == Type.ACTION ? actionText.getText() : null;
            
            descriptor = new HotlinkDescriptor(attributeName, type, actionConfig);
            super.okPressed();
        }
        @Override
        protected void cancelPressed() {
            descriptor = null;
            super.cancelPressed();
        }
    }
}
