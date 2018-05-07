/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.document.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.miginfocom.swt.MigLayout;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.document.IAttachmentSource;
import org.locationtech.udig.catalog.document.IDocument;
import org.locationtech.udig.catalog.document.IDocument.ContentType;
import org.locationtech.udig.catalog.document.IDocumentSource;
import org.locationtech.udig.catalog.document.IHotlinkSource;
import org.locationtech.udig.catalog.document.IHotlinkSource.HotlinkDescriptor;
import org.locationtech.udig.catalog.internal.shp.ShpGeoResourceImpl;
import org.locationtech.udig.document.DocumentPlugin;
import org.locationtech.udig.document.source.BasicHotlinkDescriptorParser;
import org.locationtech.udig.document.source.ShpDocPropertyParser;
import org.locationtech.udig.document.ui.internal.Messages;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
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
    
    private Button resourceDocumentsFlag;
    private Button featureDocumentsFlag;
    private Button featureHotlinksFlag;
    
    private TableViewer hotlinkViewer;
    private Button addHotlink;
    private Button editHotlink;
    private Button removeButton;

    private List<HotlinkDescriptor> hotlinkList;
    private BasicHotlinkDescriptorParser hotlinkParser;
    private ShpDocPropertyParser propParser;
    
    public static final String ACTION_PARAM = "{0}"; //$NON-NLS-1$
    
    @Override
    protected Control createContents(Composite parent) {

        final Composite page = new Composite(parent, SWT.NO_SCROLL);
        page.setLayout(new MigLayout("insets 0, wrap 1, fill")); //$NON-NLS-1$
        page.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        final Group resourceGrp = new Group(page, SWT.SHADOW_IN);
        resourceGrp.setText(Messages.DocumentPropertyPage_resourceGrpTitle);
        resourceGrp.setLayoutData("w 100%!"); //$NON-NLS-1$
        resourceGrp.setLayout(new MigLayout());
        
        resourceDocumentsFlag = new Button(resourceGrp, SWT.CHECK);
        resourceDocumentsFlag.setText(Messages.DocumentPropertyPage_resourceEnable);
        resourceDocumentsFlag.setLayoutData("growx"); //$NON-NLS-1$
        
        final Group featureGrp = new Group(page, SWT.SHADOW_IN);
        featureGrp.setText(Messages.DocumentPropertyPage_featureGrpTitle);
        featureGrp.setLayoutData("pushy, growy, w 100%!"); //$NON-NLS-1$
        final String layoutConst = "wrap 2, insets 5"; //$NON-NLS-1$
        final String columnConst = "[90%]5[10%]"; //$NON-NLS-1$
        final String rowConst = ""; //$NON-NLS-1$
        featureGrp.setLayout(new MigLayout(layoutConst, columnConst, rowConst));
        
        featureDocumentsFlag = new Button(featureGrp, SWT.CHECK);
        featureDocumentsFlag.setText(Messages.DocumentPropertyPage_featureEnable);
        featureDocumentsFlag.setLayoutData("span 2"); //$NON-NLS-1$
        
        featureHotlinksFlag = new Button(featureGrp, SWT.CHECK);
        featureHotlinksFlag.setText(Messages.DocumentPropertyPage_hotlinkEnable);
        featureHotlinksFlag.setLayoutData("span 2"); //$NON-NLS-1$
        featureHotlinksFlag.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final boolean isEnabled = featureHotlinksFlag.getSelection();
                setTableAndButtonsEnablements(isEnabled);
                hotlinkViewer.setInput(isEnabled ? hotlinkList : EMPTY);
                hotlinkViewer.refresh();
            }
        });

        createHotlinksTable(featureGrp);
        createHotlinksButtons(featureGrp);

        setPageInputAndEnablements();
        
        return page;
    }
    
    private void createHotlinksTable(Composite parent) {
        
        final Composite tableComposite = new Composite(parent, SWT.NONE);
        final TableColumnLayout columnLayout = new TableColumnLayout();
        tableComposite.setLayout(columnLayout);
        tableComposite.setLayoutData("pushy, grow, wmax 85%"); //$NON-NLS-1$
        
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
                    return DocumentPlugin.getDefault().getImageRegistry()
                            .get(IDocumentImages.IMG_OBJ_LINK);
                case ACTION:
                    return DocumentPlugin.getDefault().getImageRegistry()
                            .get(IDocumentImages.IMG_OBJ_ACTION);
                default:
                    break;
                }
                return PlatformUI.getWorkbench().getSharedImages()
                        .getImage(ISharedImages.IMG_OBJ_ELEMENT);
            }
        });
        columnLayout.setColumnData(column.getColumn(), new ColumnWeightData(8, 0, false));
        
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
        
    }
    
    private void createHotlinksButtons(Composite parent) {
        
        final Composite buttonComposite = new Composite(parent, SWT.NONE);
        final String btnLayoutConst = "fillx, insets 0, wrap 1"; //$NON-NLS-1$
        final String btnColConst = "[fill]"; //$NON-NLS-1$
        final String btnRowConst = "[][]push[]"; //$NON-NLS-1$
        buttonComposite.setLayout(new MigLayout(btnLayoutConst, btnColConst, btnRowConst));
        buttonComposite.setLayoutData("grow"); //$NON-NLS-1$
        
        addHotlink = new Button(buttonComposite, SWT.CENTER);
        addHotlink.setText(Messages.Document_Add);
        addHotlink.setLayoutData("grow"); //$NON-NLS-1$
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

        editHotlink = new Button(buttonComposite, SWT.CENTER);
        editHotlink.setText(Messages.Document_Edit);
        editHotlink.setLayoutData(""); //$NON-NLS-1$
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

        removeButton = new Button(buttonComposite, SWT.CENTER);
        removeButton.setText(Messages.Document_Remove);
        removeButton.setLayoutData(""); //$NON-NLS-1$
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
        
    }
    
    private void setPageInputAndEnablements() {
        final IProgressMonitor monitor = new NullProgressMonitor();
        final IGeoResource resource = (IGeoResource) getElement().getAdapter(IGeoResource.class);
        setShpPropertyParser(resource);
        setResourcePropsEnablements(resource, monitor);
        setFeaturePropsEnablements(resource, monitor);
        setHotlinkDescriptorParser(resource);
        setHotlinkPropsEnablements(resource, monitor);
    }
    
    private void setShpPropertyParser(IGeoResource resource) {
        if (resource.canResolve(ShpGeoResourceImpl.class)) {
            propParser = new ShpDocPropertyParser(resource.getIdentifier());
        }
    }
    
    private void setResourcePropsEnablements(IGeoResource resource, IProgressMonitor monitor) {
        resourceDocumentsFlag.setSelection(false);
        resourceDocumentsFlag.setEnabled(false);
        if (resource.canResolve(IDocumentSource.class)) {
            try {
                final IDocumentSource source = resource.resolve(IDocumentSource.class, monitor);
                resourceDocumentsFlag.setSelection(source.isEnabled());
                resourceDocumentsFlag.setEnabled(source.isEnabledEditable());
            } catch (IOException e) {
                // Already disabled
            }
        }
    }
    
    private void setFeaturePropsEnablements(IGeoResource resource, IProgressMonitor monitor) {
        featureDocumentsFlag.setSelection(false);
        featureDocumentsFlag.setEnabled(false);
        if (resource.canResolve(IAttachmentSource.class)) {
            try {
                final IAttachmentSource source = resource.resolve(IAttachmentSource.class, monitor);
                featureDocumentsFlag.setSelection(source.isEnabled());
                featureDocumentsFlag.setEnabled(source.isEnabledEditable());
            } catch (IOException e) {
                // Already disabled
            }
        }
    }
        
    private void setHotlinkDescriptorParser(IGeoResource resource) {
        if (resource.canResolve(IHotlinkSource.class)) {
            hotlinkParser = new BasicHotlinkDescriptorParser(resource);
        }
    }
    
    private void setHotlinkPropsEnablements(IGeoResource resource, IProgressMonitor monitor) {
        featureHotlinksFlag.setSelection(false);
        featureHotlinksFlag.setEnabled(false);
        setTableAndButtonsEnablements(false);
        if (resource.canResolve(IHotlinkSource.class)) {
            try {
                final IHotlinkSource source = resource.resolve(IHotlinkSource.class, monitor);
                final boolean isEnabled = source.isEnabled();
                final boolean isEditable = source.isEnabledEditable();
                featureHotlinksFlag.setSelection(isEnabled);
                featureHotlinksFlag.setEnabled(isEditable);
                setTableAndButtonsEnablements(isEnabled && isEditable);
                if (isEditable) {
                    hotlinkList = new ArrayList<HotlinkDescriptor>();
                    hotlinkList.addAll(hotlinkParser.getDescriptors());
                    hotlinkViewer.setInput(hotlinkList);
                }
            } catch (IOException e) {
                // Already disabled
            }
        }
    }
    
    private void setTableAndButtonsEnablements(boolean isEnabled) {
        hotlinkViewer.getControl().setEnabled(isEnabled);
        boolean hasSelection = isEnabled && !hotlinkViewer.getSelection().isEmpty();
        addHotlink.setEnabled(isEnabled);
        editHotlink.setEnabled(hasSelection);
        removeButton.setEnabled(hasSelection);
    }
    
    @Override
    public boolean performOk() {
        savePersistentProperties();
        savePropertiesFile();
        return super.performOk();
    }

    @Override
    protected void performDefaults() {
        if (resourceDocumentsFlag.isEnabled()) {
            resourceDocumentsFlag.setSelection(propParser.getShapefileFlag());
        }
        if (featureDocumentsFlag.isEnabled()) {
            featureDocumentsFlag.setSelection(propParser.getFeatureDocsFlag());
        }
        if (featureHotlinksFlag.isEnabled()) {
            featureHotlinksFlag.setSelection(hotlinkParser.isEnabled());
            hotlinkList.clear();
            hotlinkList.addAll(hotlinkParser.getDescriptors());
            hotlinkViewer.refresh();
        }
        super.performDefaults();
    }

    private void savePersistentProperties() {
        
        final boolean isEditAllowed = featureHotlinksFlag.isEnabled();
        
        if (isEditAllowed && hotlinkParser != null) {
            // Get enablement flag value
            final boolean isHotlinksEnabled = featureHotlinksFlag.getSelection();
            // Set enablement
            hotlinkParser.setEnabled(isHotlinksEnabled);
            // Set hotlink desriptors
            if (isHotlinksEnabled) {
                if (hotlinkViewer.getInput() == EMPTY) {
                    hotlinkParser.setDescriptors(new ArrayList<HotlinkDescriptor>());
                } else {
                    hotlinkParser.setDescriptors(hotlinkList);
                }
            } else {
                hotlinkParser.clearDescriptors();
            }            
        }
        
    }
    
    /**
     * Saves the attribute hotlinks into the properties file. This creates the properties file if it
     * does not exist in the file system.
     * 
     * @param descriptors
     */
    private void savePropertiesFile() {
        
        final boolean isResourceEditAllowed = resourceDocumentsFlag.isEnabled();
        final boolean isFeatureEditAllowed = featureDocumentsFlag.isEnabled();
        
        if ((isResourceEditAllowed || isFeatureEditAllowed) && propParser != null) {
            // Create properties file
            if (!propParser.hasProperties()) {
                propParser.createPropertiesFile();
            }
            // Set flags
            if (isResourceEditAllowed) {
                propParser.setShapefileFlag(resourceDocumentsFlag.getSelection());    
            }
            if (isFeatureEditAllowed) {
                propParser.setFeatureDocsFlag(featureDocumentsFlag.getSelection());    
            }
            // Write properties
            propParser.writeProperties();
        }
        
    }
    
    @Override
    public boolean performCancel() {
        return super.performCancel(); // no change
    }

    protected void removeDescriptor(HotlinkDescriptor descriptor) {
        hotlinkList.remove(descriptor);
        hotlinkViewer.refresh();
    }

    protected void editDescriptor(HotlinkDescriptor descriptor) {
        final int index = hotlinkViewer.getTable().getSelectionIndex();
        
        // look up shell now from the display thread
        final Shell shell = DocumentPropertyPage.this.getShell();
        
        final HotlinkDescriptor copy = new HotlinkDescriptor(descriptor);
        final HotlinkDescriptorDialog prompt = new HotlinkDescriptorDialog(shell, copy);
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

        private StructuredSelection typeSelection;
        
        private boolean isAddMode = true;
        
        /**
         * Constructor for add mode.
         * 
         * @param parentShell
         */
        protected HotlinkDescriptorDialog(Shell parentShell) {
            super(parentShell);
            this.isAddMode = true;
            this.descriptor = new HotlinkDescriptor();
        }

        /**
         * Constructor for edit mode.
         * 
         * @param parentShell
         * @param descriptor
         */
        protected HotlinkDescriptorDialog(Shell parentShell, HotlinkDescriptor descriptor) {
            super(parentShell);
            this.isAddMode = false;
            this.descriptor = descriptor;
        }
        
        @Override
        protected boolean isResizable() {
            return true;
        }
        
        @Override
        protected Image getImage() {
            return getInfoImage();
        }
        
        public void setSchema(SimpleFeatureType schema) {
            this.schema = schema;
        }

        public HotlinkDescriptor getDescriptor() {
            return descriptor;
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
            shell.setText(Messages.DocumentPropertyPage_title);
            shell.setImage(getQuestionImage());
            resizeDialog(shell);
            super.configureShell(shell);
        }

        protected void resizeDialog(Shell shell) {

            final int HEIGHT = 380;
            final int WIDTH = 460;

            final Display display = PlatformUI.getWorkbench().getDisplay();
            final Point size = (new Shell(display)).computeSize(-1, -1);
            final Rectangle screen = display.getMonitors()[0].getBounds();

            final int xPos = (screen.width - size.x) / 2 - WIDTH / 2;
            final int yPos = (screen.height - size.y) / 2 - HEIGHT / 2;

            shell.setBounds(xPos, yPos, WIDTH, HEIGHT);

        }
        
        @Override
        protected Control createDialogArea(Composite parent) {
            
            Composite composite = new Composite(parent, SWT.NONE);
            MigLayout layout = new MigLayout("insets 0, wrap 2, fillx", "[20%, right]8[80%]", "[]15[][][]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            composite.setLayout(layout);
            composite.setLayoutData(new GridData(GridData.FILL_BOTH));

            String header = Messages.DocumentPropertyPage_editHotlinkHeader;
            if (isAddMode) {
                header = Messages.DocumentPropertyPage_addHotlinkHeader;
            }
            message = header;
            getShell().setText(header);
            
            createMessageArea(composite);
            imageLabel.setLayoutData("cell 0 0, alignx right"); //$NON-NLS-1$
            messageLabel.setLayoutData("cell 1 0 2 1, aligny center"); //$NON-NLS-1$
            final FontData[] fontData = messageLabel.getFont().getFontData(); 
            for (int i = 0; i < fontData.length; i++) {
                fontData[i].setHeight(14);
            };
            messageLabel.setFont(new Font(null, fontData));
            
            Label labelLbl = new Label(composite, SWT.NONE);
            labelLbl.setText(Messages.DocumentPropertyPage_Label);
            labelLbl.setLayoutData(""); //$NON-NLS-1$

            labelText = new Text(composite, SWT.SINGLE | SWT.BORDER);
            labelText.setLayoutData("growx"); //$NON-NLS-1$
            labelText.addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(ModifyEvent e) {
                    refreshButtons();
                }
            });
            
            Label descriptionLbl = new Label(composite, SWT.NONE);
            descriptionLbl.setText(Messages.DocumentPropertyPage_description);
            descriptionLbl.setLayoutData(""); //$NON-NLS-1$

            descriptionText = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
            descriptionText.setLayoutData("growx, h 60!"); //$NON-NLS-1$
            descriptionText.addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(ModifyEvent e) {
                    refreshButtons();
                }
            });
            
            Label label = new Label(composite, SWT.SINGLE);
            label.setText(Messages.DocumentPropertyPage_Attribute);
            label.setLayoutData(""); //$NON-NLS-1$
                        
            attributeViewer = new ComboViewer(composite);
            attributeViewer.setContentProvider(ArrayContentProvider.getInstance());
            attributeViewer.getControl().setLayoutData(""); //$NON-NLS-1$
            final List<String> attributeNames = getSchemaCandidates();
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
                    if (labelText.getText().isEmpty()) {
                        labelText.setText(getLabelFromAttribute());    
                    }
                    refreshButtons();
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
                    if (element instanceof ContentType) {
                        final ContentType type = (ContentType) element;
                        return DocUtils.toCamelCase(type.name());
                    }
                    return super.getText(element);
                }
            });
            typeViewer.setInput(IDocument.ContentType.values());
            typeViewer.getControl().setLayoutData(""); //$NON-NLS-1$
            typeViewer.addSelectionChangedListener( new ISelectionChangedListener() {
                public void selectionChanged(SelectionChangedEvent event) {
                    final ISelection selection = event.getSelection();
                    if (!selection.isEmpty() && selection instanceof StructuredSelection) {
                        final StructuredSelection strucSelection = (StructuredSelection) selection;
                        final IDocument.ContentType type = (ContentType) strucSelection.getFirstElement();
                        if (typeSelection != null && !typeSelection.isEmpty()) {
                            final IDocument.ContentType currentType = (ContentType) typeSelection.getFirstElement();
                            if (currentType == type) {
                                return;
                            }
                        }
                        setActionText(type, descriptor.getConfig());
                        actionText.setFocus();  
                        typeSelection = strucSelection;
                        refreshButtons();
                    }
                }
            });
            
            actionLabel = new Label(composite, SWT.SINGLE);
            actionLabel.setText(Messages.DocumentPropertyPage_Action);
            actionLabel.setLayoutData(""); //$NON-NLS-1$

            actionText = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
            actionText.setLayoutData("growx, h 60!"); //$NON-NLS-1$
            actionText.addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(ModifyEvent e) {
                    refreshButtons();
                }
            });
                        
            applyDialogFont(composite);
            return composite;
        }
        

        
        @Override
        protected void createButtonsForButtonBar(Composite parent) {
            super.createButtonsForButtonBar(parent);
            refreshButtons();
        }
        
        private void refreshButtons() {
            getButton(IDialogConstants.OK_ID).setEnabled(isValidForm());
        }
        
        @Override
        protected Control createContents(Composite parent) {
            final Control control = super.createContents(parent);
            
            if( descriptor.isEmpty() ){
                final ContentType defaultType = ContentType.FILE;
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
                final List<String> attributeNames = getSchemaCandidates();
                final String attributeName = descriptor.getAttributeName();
                if (attributeNames.contains(attributeName)) {
                    attributeViewer.setSelection(new StructuredSelection(attributeName));
                }
                typeViewer.setSelection(new StructuredSelection(descriptor.getType()), true);
                setActionText(descriptor.getType(), descriptor.getConfig());
            }
            labelText.setFocus();
            
            return control;
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
                ContentType type = (ContentType) selection.getFirstElement();
                final String actionConfig = actionText.getText();
                descriptor = new HotlinkDescriptor(label, description, attributeName, type, actionConfig);
                super.okPressed();                
            }
            
        }
        
        /**
         * Checks if the required fields have been filled up. This controls when the Ok button is
         * enabled.
         * 
         * @return true if required fields are filled up, otherwise false
         */
        private boolean isValidForm() {
            final String label = labelText.getText().trim();
            if (label == null || label.length() == 0) {
                return false;
            }
            final ISelection attribute = attributeViewer.getSelection();
            if (attribute.isEmpty()) {
                return false;
            }
            final ISelection type = typeViewer.getSelection();
            if (type.isEmpty()) {
                return false;
            }
            final String action = actionText.getText().trim();
            if (action == null || action.length() == 0) {
                return false;
            }
            return true;
        }
        
        /**
         * Checks if the current inputs are valid. This checks if the hotlink definition already
         * exists in the list. This is the checking used on click of the Ok button.
         * 
         * @return true if valid, otherwise false
         */
        private boolean isValidHotlink() {

            if (!isValidForm()) {
                MessageDialog.openError(getShell(), Messages.DocumentPropertyPage_title,
                        Messages.DocumentPropertyPage_errRequired);
                return false;
            }
            
            final StructuredSelection selection = (StructuredSelection) typeViewer.getSelection();
            final ContentType type = (ContentType) selection.getFirstElement();
            
            final String attributeName = attributeViewer.getCombo().getText();
            for (HotlinkDescriptor hotlink : hotlinkList) {
                if (!descriptor.isEmpty() && hotlink.toString().equals(descriptor.toString())) {
                    continue;
                }
                if (attributeName.equals(hotlink.getAttributeName())) {
                    final ContentType currentType = hotlink.getType();
                    if (ContentType.ACTION == currentType && ContentType.ACTION == type) {
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
     
        private void setActionText(ContentType type, String config) {
            switch (type) {
            case ACTION:
                actionText.setEditable(true);
                actionText.setText(config == null ? "" : config); //$NON-NLS-1$
                break;
            default:
                actionText.setEditable(false);
                actionText.setText(Messages.DocumentPropertyPage_Open);
                break;
            }
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
