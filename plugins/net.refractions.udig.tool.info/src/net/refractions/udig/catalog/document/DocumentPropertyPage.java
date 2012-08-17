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
import net.refractions.udig.document.DocUtils;
import net.refractions.udig.tool.info.InfoPlugin;
import net.refractions.udig.tool.info.internal.Messages;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
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
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.PlatformUI;
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
    
    private Button hotlinkEnable;
    
    private TableViewer hotlinkViewer;

    private Button addHotlink;

    private Button editHotlink;

    private Button removeButton;

    private List<HotlinkDescriptor> hotlinkList = new ArrayList<HotlinkDescriptor>();

    private ShpDocPropertyParser propParser;
    
    public static final String ACTION_PARAM = "{0}"; //$NON-NLS-1$
    
    @Override
    protected Control createContents(Composite parent) {
        IAdaptable target = getElement();
        final IGeoResource resource = (IGeoResource) target.getAdapter(IGeoResource.class);
        if (resource.canResolve(ShpGeoResourceImpl.class)) {
            propParser = new ShpDocPropertyParser(resource.getIdentifier());
        }
        boolean isEnabled = BasicHotlinkResolveFactory.hasHotlinkDescriptors(resource);
        boolean hasAttachmentSource = resource.canResolve(IAttachmentSource.class);
        boolean hasSchema = resource.canResolve(SimpleFeatureSource.class);
        hotlinkList = new ArrayList<HotlinkDescriptor>();
        if (isEnabled && hasSchema) {
            hotlinkList.addAll(BasicHotlinkResolveFactory.getHotlinkDescriptors(resource));
        }

        Composite page = new Composite(parent, SWT.NO_SCROLL);
        page.setLayout(new MigLayout("insets 0", "[][grow,fill][]", "[][][][][fill][]")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        Button enableAttachment = new Button(page, SWT.CHECK);
        enableAttachment.setText(Messages.Document_Attachment_Enable);
        enableAttachment.setLayoutData("span 3, left, grow x, wrap"); //$NON-NLS-1$
        enableAttachment.setEnabled(false);
        enableAttachment.setSelection( hasAttachmentSource );
        
        hotlinkEnable = new Button(page, SWT.CHECK);
        hotlinkEnable.setText(Messages.Document_Hotlink_Enable);
        hotlinkEnable.setLayoutData("span 3, left, grow x, wrap"); //$NON-NLS-1$
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

        addHotlink = new Button(page, SWT.CENTER);
        addHotlink.setText(Messages.Document_Add);
        addHotlink.setLayoutData("cell 2 3, growx"); //$NON-NLS-1$
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
        editHotlink.setText(Messages.Document_Edit);
        editHotlink.setLayoutData("cell 2 4, growx"); //$NON-NLS-1$
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
        removeButton.setText(Messages.Document_Remove);
        removeButton.setLayoutData("cell 2 6, aligny bottom, growx"); //$NON-NLS-1$
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
        tableComposite.setLayoutData("cell 0 3 2 4, grow, height 200:100%:100%,width 300:pref:100%, gapx para"); //$NON-NLS-1$
        
        hotlinkViewer = new TableViewer(tableComposite, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL
                | SWT.H_SCROLL | SWT.FULL_SELECTION);
        hotlinkViewer.setContentProvider(ArrayContentProvider.getInstance());
        
        TableViewerColumn column = new TableViewerColumn(hotlinkViewer, SWT.NONE);
        column.getColumn().setText(""); //$NON-NLS-1$
        column.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return ""; //$NON-NLS-1$
            }
            @Override
            public Image getImage(Object element) {
                final HotlinkDescriptor descriptor = (HotlinkDescriptor) element;
                switch (descriptor.getType()) {
                case FILE:
                    return PlatformUI.getWorkbench().getSharedImages()
                            .getImage(ISharedImages.IMG_OBJ_FILE);
                case WEB:
                    return InfoPlugin.getDefault().getImageRegistry()
                            .get(InfoPlugin.IMG_OBJ_LINK);
                case ACTION:
                    return InfoPlugin.getDefault().getImageRegistry()
                            .get(InfoPlugin.IMG_OBJ_ACTION);
                default:
                    break;
                }
                return PlatformUI.getWorkbench().getSharedImages()
                        .getImage(ISharedImages.IMG_OBJ_ELEMENT);
            }
        });
        columnLayout.setColumnData(column.getColumn(), new ColumnWeightData(8, 0, true));
        
        column = new TableViewerColumn(hotlinkViewer, SWT.NONE);
        column.getColumn().setText(Messages.Document_Label_Column);
        column.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                final HotlinkDescriptor descriptor = (HotlinkDescriptor) element;
                return descriptor.getLabel();
            }
        });
        columnLayout.setColumnData(column.getColumn(), new ColumnWeightData(25, 0, true));
        
        column = new TableViewerColumn(hotlinkViewer, SWT.NONE);
        column.getColumn().setText(Messages.Document_Attribute_Column);
        column.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                HotlinkDescriptor descriptor = (HotlinkDescriptor) element;
                return descriptor.getAttributeName();
            }
        });
        columnLayout.setColumnData( column.getColumn(), new ColumnWeightData( 25, 0, true ));
        
        column = new TableViewerColumn(hotlinkViewer, SWT.NONE);
        column.getColumn().setText(Messages.Document_Hotlink_Column);
        column.getColumn().setAlignment(SWT.CENTER);
        column.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                HotlinkDescriptor descriptor = (HotlinkDescriptor) element;
                return DocUtils.toCamelCase(descriptor.getType().toString());
            }
        });
        columnLayout.setColumnData( column.getColumn(), new ColumnWeightData( 15, 0, true ));
        
        column = new TableViewerColumn(hotlinkViewer, SWT.NONE);
        column.getColumn().setText(Messages.Document_Action_Column);
        column.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                HotlinkDescriptor descriptor = (HotlinkDescriptor) element;
                if( descriptor.getConfig() == null ){
                    return Messages.DocumentPropertyPage_Open;
                }
                return descriptor.getConfig();
            }
        });
        columnLayout.setColumnData( column.getColumn(), new ColumnWeightData( 30, 0, true ));
        
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
        hotlinkViewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                final StructuredSelection selection = (StructuredSelection) hotlinkViewer.getSelection();
                final HotlinkDescriptor descriptor = (HotlinkDescriptor) selection.getFirstElement();
                editDescriptor(descriptor);
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
                savePropertiesFile(null);
            } else {
                BasicHotlinkResolveFactory.putHotlinkDescriptors(resource, hotlinkList);
                savePropertiesFile(hotlinkList);
            }
        } else {
            BasicHotlinkResolveFactory.clearHotlinkDescriptors(resource);
            savePropertiesFile(null);
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

    /**
     * Saves the attribute hotlinks into the properties file. This creates the properties file if it
     * does not exist in the file system.
     * 
     * @param hotlinks
     */
    private void savePropertiesFile(List<HotlinkDescriptor> hotlinks) {
        if( propParser != null ){
            if (!propParser.hasProperties()) {
                propParser.createPropertiesFile();
            }
            if (hotlinks != null) {
                propParser.setHotlinkDescriptors(hotlinks);    
            } else {
                propParser.setHotlinkDescriptors(Collections.<HotlinkDescriptor>emptyList());
            }
            propParser.writeProperties();
        }
    }
    
    @Override
    public boolean performCancel() {
        return super.performCancel(); // no change
    }

    public void enableTableAndButtons(boolean isEnabled) {
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
        
        private Text labelText;
        
        private Text descriptionText;

        protected HotlinkDescriptorDialog(Shell parentShell) {
            super(parentShell);
            message = Messages.DocumentPropertyPage_header;
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
            Job job = new Job("Prompt Hotlink Descriptor") { //$NON-NLS-1$
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
            shell.setText(Messages.DocumentPropertyPage_title);
            shell.setImage( getQuestionImage() );
        }
        
        @Override
        protected Control createDialogArea(Composite parent) {
            
            Composite composite = new Composite(parent, SWT.NONE);
            MigLayout layout = new MigLayout("insets panel, wrap 2", "[right]8[grow]", "[]10[][]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            composite.setLayout(layout);
            composite.setLayoutData(new GridData(GridData.FILL_BOTH));

            createMessageArea(composite);
            imageLabel.setLayoutData("cell 0 0, alignx right"); //$NON-NLS-1$
            messageLabel.setLayoutData("cell 1 0 2 1, aligny center"); //$NON-NLS-1$
            
            Label labelLbl = new Label(composite, SWT.NONE);
            labelLbl.setText(Messages.DocumentPropertyPage_Label);
            labelLbl.setLayoutData(""); //$NON-NLS-1$

            labelText = new Text(composite, SWT.SINGLE | SWT.BORDER);
            labelText.setLayoutData("growx"); //$NON-NLS-1$
            
            Label descriptionLbl = new Label(composite, SWT.NONE);
            descriptionLbl.setText(Messages.DocumentPropertyPage_description);
            descriptionLbl.setLayoutData(""); //$NON-NLS-1$

            descriptionText = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
            descriptionText.setLayoutData("growx, h 60!"); //$NON-NLS-1$
            
            Label label = new Label(composite, SWT.SINGLE);
            label.setText(Messages.DocumentPropertyPage_Attribute);
            label.setLayoutData(""); //$NON-NLS-1$
                        
            attributeViewer = new ComboViewer(composite);
            attributeViewer.setContentProvider(ArrayContentProvider.getInstance());
            attributeViewer.getControl().setLayoutData("growx"); //$NON-NLS-1$
            List<String> attributeNames = getSchemaCandidates();
            attributeViewer.setInput(attributeNames);
            attributeViewer.addSelectionChangedListener( new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    final StructuredSelection selection = (StructuredSelection) event.getSelection();
                    final boolean hasAttribute = !selection.isEmpty();
                    final Button okBtn = getButton(IDialogConstants.OK_ID);
                    if (okBtn != null) {
                        okBtn.setEnabled(hasAttribute);
                    }
                    labelText.setText(getLabelFromAttribute());
                }
            });
            
            label = new Label(composite, SWT.SINGLE);
            label.setText(Messages.DocumentPropertyPage_Hotlink);
            label.setLayoutData(""); //$NON-NLS-1$

            typeViewer = new ComboViewer(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
            typeViewer.setContentProvider(ArrayContentProvider.getInstance());
            typeViewer.setLabelProvider(new LabelProvider() {
                @Override
                public String getText(Object element) {
                    if (element instanceof Type) {
                        final Type type = (Type) element;
                        return DocUtils.toCamelCase(type.name());
                    }
                    return super.getText(element);
                }
            });
            typeViewer.setInput(IDocument.Type.values());
            typeViewer.getControl().setLayoutData(""); //$NON-NLS-1$
            typeViewer.addSelectionChangedListener( new ISelectionChangedListener() {
                public void selectionChanged(SelectionChangedEvent event) {
                    if( !event.getSelection().isEmpty() && event.getSelection() instanceof StructuredSelection ){
                        StructuredSelection selection = (StructuredSelection) event.getSelection();
                        IDocument.Type type = (Type) selection.getFirstElement();
                        setActionText(type, descriptor.getConfig());
                        actionText.setFocus();
                    }
                }
            });
            
            actionLabel = new Label(composite, SWT.SINGLE);
            actionLabel.setText(Messages.DocumentPropertyPage_Action);
            actionLabel.setLayoutData(""); //$NON-NLS-1$

            actionText = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
            actionText.setLayoutData("growx, h 60!"); //$NON-NLS-1$
            
            if( descriptor.isEmpty() ){
                final Type defaultType = Type.FILE;
                typeViewer.setSelection(new StructuredSelection(defaultType), true);
                setActionText(defaultType, null);
            } else {
                final String labelStr = descriptor.getLabel();
                if (labelStr != null) {
                    labelText.setText(labelStr);
                }
                final String descriptionStr = descriptor.getDescription();
                if (descriptionStr != null) {
                    descriptionText.setText(descriptionStr);
                }
                final String attributeName = descriptor.getAttributeName();
                if (attributeNames.contains(attributeName)) {
                    attributeViewer.setSelection(new StructuredSelection(attributeName));
                }
                typeViewer.setSelection(new StructuredSelection(descriptor.getType()), true);
                setActionText(descriptor.getType(), descriptor.getConfig());
            }
            
            applyDialogFont(composite);
            return composite;
        }

        private void setActionText(Type type, String config) {
            switch (type) {
            case ACTION:
                actionText.setEnabled(true);
                actionText.setText(config == null ? "" : config); //$NON-NLS-1$
                break;
            default:
                actionText.setEnabled(false);
                actionText.setText(Messages.DocumentPropertyPage_Open);
                break;
            }
        }
        
        @Override
        protected void createButtonsForButtonBar(Composite parent) {
            super.createButtonsForButtonBar(parent); //  // create OK and Cancel buttons by default
            
            boolean hasAttribute = !attributeViewer.getSelection().isEmpty();
            getButton(IDialogConstants.OK_ID).setEnabled(hasAttribute);
        }
        
        @Override
        protected void okPressed() {
            
            if (isValidHotlink()) {
                String attributeName = attributeViewer.getCombo().getText();
                String label = labelText.getText();
                if( label == null || label.isEmpty() ){
                    label = getLabelFromAttribute();
                }
                String description = descriptionText.getText();
                StructuredSelection selection = (StructuredSelection) typeViewer.getSelection();
                Type type = (Type) selection.getFirstElement();
                final String actionConfig = actionText.getText();
                descriptor = new HotlinkDescriptor(label, description, attributeName, type, actionConfig);
                super.okPressed();                
            }
            
        }
        
        /**
         * Checks if the current inputs are valid. This checks if the hotlink definition inputted
         * already exists in the list.
         * 
         * @return true if valid, otherwise false
         */
        private boolean isValidHotlink() {

            final StructuredSelection selection = (StructuredSelection) typeViewer.getSelection();
            final Type type = (Type) selection.getFirstElement();

            final String action = actionText.getText().trim();
            if (action == null || action.length() == 0) {
                actionText.setFocus();
                MessageDialog.openError(getShell(), Messages.DocumentPropertyPage_title,
                        Messages.DocumentPropertyPage_errActionIsBlank);
                return false;
            }
            
            final String attributeName = attributeViewer.getCombo().getText();
            for (HotlinkDescriptor hotlink : hotlinkList) {
                if (!descriptor.isEmpty() && hotlink.toString().equals(descriptor.toString())) {
                    continue;
                }
                if (attributeName.equals(hotlink.getAttributeName())) {
                    final Type currentType = hotlink.getType();
                    if (Type.ACTION == currentType && Type.ACTION == type) {
                        return true;
                    } else {
                        attributeViewer.getControl().setFocus();
                        MessageDialog.openError(getShell(), Messages.DocumentPropertyPage_title,
                                Messages.DocumentPropertyPage_errExists);
                        return false;
                    }
                }
            }

            return true;
            
        }
        
        @Override
        protected void cancelPressed() {
            descriptor = null;
            super.cancelPressed();
        }
     
        /**
         * Gets the label string from the attribute name value
         * 
         * @return label
         */
        private String getLabelFromAttribute() {
            final StructuredSelection selection = (StructuredSelection) attributeViewer.getSelection();
            final boolean hasAttribute = !selection.isEmpty();
            if (hasAttribute) {
                final String attribute = (String) selection.getFirstElement();
                return DocUtils.toCamelCase(attribute);
            }
            return ""; //$NON-NLS-1$
        }
        
    }
}
