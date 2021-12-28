/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.validation.ui;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchViewerComparator;
import org.geotools.validation.Validation;
import org.geotools.validation.dto.ArgumentDTO;
import org.geotools.validation.dto.PlugInDTO;
import org.geotools.validation.dto.TestDTO;
import org.geotools.validation.dto.TestSuiteDTO;
import org.geotools.validation.xml.ValidationException;
import org.geotools.validation.xml.XMLReader;
import org.geotools.validation.xml.XMLWriter;
import org.locationtech.udig.issues.IIssuesManager;
import org.locationtech.udig.issues.IssueConstants;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.graphics.TableSettings;
import org.locationtech.udig.ui.graphics.TableUtils;
import org.locationtech.udig.validation.DTOUtils;
import org.locationtech.udig.validation.GenericValidationResults;
import org.locationtech.udig.validation.ImageConstants;
import org.locationtech.udig.validation.ValidationPlugin;
import org.locationtech.udig.validation.ValidationProcessor;
import org.locationtech.udig.validation.internal.Messages;
import org.opengis.feature.simple.SimpleFeatureType;

public class ValidationDialog extends TitleAreaDialog {

    /**
     * Constant to use as the key for storing the validation processor on the blackboard (for saving
     * the state of the validation dialog box)
     */
    public static final String BLACKBOARD_KEY = "org.locationtech.udig.validation"; //$NON-NLS-1$

    private Button newButton;

    private Button deleteButton;

    private Button exportButton;

    private Button importButton;

    private Button runButton;

    private Button cancelButton;

    private Composite buttonComposite;

    private SelectionListener closeListener;

    private SelectionListener cancelListener;

    private Text nameText;

    private Text descText;

    private TableViewer tableViewer;

    private TableSettings tableSettings;

    private CheckboxTreeViewer treeViewer;

    private ValidationTreeContentProvider contentProvider = null;

    private ProgressMonitorPart progressMonitorPart;

    private Object selectedTreeItem;

    private ValidationProcessor processor;

    /**
     * The key (name) of the testSuite in use (usually there will only be a single testSuite, and
     * this is its name).
     */
    private String defaultTestSuite;

    /**
     * If a layer was selected to get to this dialog, it is the default testSuite. Otherwise the
     * value is "" or even "*".
     */
    private String defaultTypeRef = ""; //$NON-NLS-1$

    private Display display;

    private static String[] typeRefs;

    private static String[] layerNames;

    public ValidationDialog(Shell parentShell, ILayer[] layers) {
        this(parentShell);
        // determine the defaultTypeRef (from the first selected layer)
        String dsID = layers[0].getSchema().getName().getNamespaceURI();
        String typeName = layers[0].getName();
        defaultTypeRef = dsID + ":" + typeName; //$NON-NLS-1$
        // this.layers = layers;
        display = parentShell.getDisplay();

        cancelListener = new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // a test is running and the user just hit cancel
                // cancel the progress monitor
                progressMonitorPart.setCanceled(true);
                cancelButton.setEnabled(false);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }

        };
    }

    protected ValidationDialog(Shell parentShell) {
        super(parentShell);
        setShellStyle(
                SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL | getDefaultOrientation());
        setBlockOnOpen(true);
    }

    public ValidationDialog getDialog() {
        return this;
    }

    @Override
    public Button getCancelButton() {
        return cancelButton;
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Composite composite = new Composite(parent, SWT.FILL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.makeColumnsEqualWidth = true;
        layout.marginWidth = 0; // no spacing here, it's purely a container
        layout.marginHeight = 0;
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        composite.setLayout(layout);
        GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        composite.setLayoutData(data);
        composite.setFont(parent.getFont());

        // create 2 composites in the composite with left and right aligns
        Composite leftComp = new Composite(composite, SWT.LEFT);
        layout = new GridLayout();
        layout.numColumns = 2;
        layout.makeColumnsEqualWidth = true;
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(
                IDialogConstants.HORIZONTAL_SPACING);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        leftComp.setLayout(layout);
        data = new GridData(SWT.LEFT, SWT.NONE, true, false);
        leftComp.setLayoutData(data);
        leftComp.setFont(parent.getFont());

        Composite rightComp = new Composite(composite, SWT.RIGHT);
        layout = new GridLayout();
        layout.numColumns = 2;
        layout.makeColumnsEqualWidth = true;
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(
                IDialogConstants.HORIZONTAL_SPACING);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        rightComp.setLayout(layout);
        data = new GridData(SWT.RIGHT, SWT.NONE, true, false);
        rightComp.setLayoutData(data);
        rightComp.setFont(parent.getFont());

        // Add the buttons
        importButton = new Button(leftComp, SWT.PUSH);
        importButton.setFont(parent.getFont());
        importButton.setText(Messages.ValidationDialog_import);
        importButton.setEnabled(true);
        importButton.addSelectionListener(new ImportSuiteListener());
        setButtonLayoutData(importButton);

        exportButton = new Button(leftComp, SWT.PUSH);
        exportButton.setFont(parent.getFont());
        exportButton.setText(Messages.ValidationDialog_export);
        exportButton.setEnabled(false);
        exportButton.addSelectionListener(new ExportSuiteListener());
        setButtonLayoutData(exportButton);

        runButton = new Button(rightComp, SWT.PUSH);
        runButton.setFont(parent.getFont());
        runButton.setText(Messages.ValidationDialog_run);
        runButton.setEnabled(false);
        runButton.addSelectionListener(new RunTestsListener());
        setButtonLayoutData(runButton);

        cancelButton = new Button(rightComp, SWT.PUSH);
        cancelButton.setText(IDialogConstants.CLOSE_LABEL);
        cancelButton.setFont(parent.getFont());
        cancelButton.setData(Integer.valueOf(IDialogConstants.CANCEL_ID));
        closeListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                // nothing is happening and the user hit cancel (close)
                buttonPressed(((Integer) event.widget.getData()).intValue());
            }
        };
        cancelButton.addSelectionListener(closeListener);
        setButtonLayoutData(cancelButton);

        return composite;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {

    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        setTitle(Messages.ValidationDialog_title);
        setMessage(""); //$NON-NLS-1$
        ImageDescriptor image = ValidationPlugin.getDefault()
                .getImageDescriptor(ImageConstants.IMAGE_WIZBAN);
        if (image != null)
            setTitleImage(image.createImage());

        GridData gd;
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        composite.setLayoutData(gd);
        GridLayout topLayout = new GridLayout(1, false);
        topLayout.marginHeight = 0;
        topLayout.marginWidth = 0;
        composite.setLayout(topLayout);

        // Create the SashForm that contains the selection area on the left,
        // and the edit area on the right
        SashForm sashForm = new SashForm(composite, SWT.FILL);
        sashForm.setOrientation(SWT.HORIZONTAL);
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        sashForm.setLayoutData(gd);
        sashForm.setFont(parent.getFont());

        // Build the validation selection area and put it into the composite.
        Composite validationSelectionArea;
        try {
            validationSelectionArea = createValidationSelectionArea(sashForm);
        } catch (Exception e) {
            // TODO Handle Exception
            throw (RuntimeException) new RuntimeException().initCause(e);
        }
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        validationSelectionArea.setLayoutData(gd);

        // Build the validation edit area and put it into the composite.
        Composite editAreaComp = createValidationEditArea(sashForm);
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        editAreaComp.setLayoutData(gd);

        // add the progress bar
        GridLayout pmlayout = new GridLayout();
        pmlayout.numColumns = 1;
        progressMonitorPart = createProgressMonitorPart(composite, pmlayout);
        progressMonitorPart.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        progressMonitorPart.setVisible(false);

        // Build the separator line that demarcates the button bar
        Label separator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
        gd = new GridData(SWT.FILL, SWT.NONE, true, false);
        // gd.horizontalSpan = 2;
        separator.setLayoutData(gd);

        parent.layout(true);
        applyDialogFont(parent);

        // create a resize listener
        composite.addListener(SWT.Resize, new Listener() {

            @Override
            public void handleEvent(Event event) {
                resizeTable();
                // FIXME: possibly move this listener to the table.resize event
            }

        });

        composite.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                // find the Map
                IMap map = ApplicationGIS.getActiveMap();
                saveDialogState(processor, map);
            }

        });

        return composite;
    }

    /**
     * Creates the validation test suite selection area of the dialog. This area displays a tree of
     * validations that the user may select and modify. The first tier of the tree contains the
     * available validation plugins, and the second tier contains instances of the test.
     *
     * @return the composite used for the validations selection area
     * @throws Exception
     */
    protected Composite createValidationSelectionArea(Composite parent) throws Exception {
        Font font = parent.getFont();
        Composite comp = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = 5;
        layout.marginWidth = 5;
        comp.setLayout(layout);
        comp.setFont(font);

        Label treeLabel = new Label(comp, SWT.NONE);
        treeLabel.setFont(font);
        treeLabel.setText(Messages.ValidationDialog_validations);
        GridData labelData = new GridData();
        treeLabel.setLayoutData(labelData);

        ValidationProcessor tempProcessor = loadDialogState(ApplicationGIS.getActiveMap());
        if (tempProcessor == null) {
            processor = createProcessor(null, null);
            defaultTestSuite = "testSuite1"; //$NON-NLS-1$
        } else {
            processor = tempProcessor;
            if (processor.getTestSuiteDTOs().size() == 1) {
                // a single testSuite exists, grab its name
                defaultTestSuite = (String) processor.getTestSuiteDTOs().keySet().toArray()[0];
            } else if (processor.getTestSuiteDTOs().isEmpty()) {
                // there is no... testSuite, create one
                processor = createProcessor(null, null);
                defaultTestSuite = "testSuite1"; //$NON-NLS-1$
            } else {
                // there are multiple testSuites, choose the largest one
                String thisTestSuite = ""; //$NON-NLS-1$
                int mostTests = -1;
                for (Iterator i = processor.getTestSuiteDTOs().keySet().iterator(); i.hasNext();) {
                    Object thisKey = i.next();
                    int numTests = processor.getTestSuiteDTOs().get(thisKey).getTests().size();
                    if (numTests > mostTests) {
                        mostTests = numTests;
                        thisTestSuite = (String) thisKey;
                    }
                }
                defaultTestSuite = thisTestSuite;
            }
        }

        // create the treeViewer (list of possible validations (plugins) + prepared tests)
        treeViewer = new CheckboxTreeViewer(comp);
        treeViewer.setLabelProvider(new ValidationTreeLabelProvider());
        treeViewer.setComparator(new WorkbenchViewerComparator());
        contentProvider = new ValidationTreeContentProvider();
        treeViewer.setContentProvider(contentProvider);
        treeViewer.addCheckStateListener(new ICheckStateListener() {
            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                Object element = event.getElement();
                Object[] children = contentProvider.getChildren(element);
                if (children != null && children.length > 0) {
                    // parent element was modified, adjust the children accordingly
                    for (int i = 0; i < children.length; i++) {
                        treeViewer.setChecked(children[i], event.getChecked());
                    }
                    // all children are in the same state, therefore the parent is not grayed
                    treeViewer.setGrayed(element, false);
                }

                Object parent = contentProvider.getParent(element);
                if (parent != null) {
                    // child element was modified
                    Object[] siblings = contentProvider.getChildren(parent);
                    boolean oneSiblingChecked = false;
                    boolean allSiblingsChecked = true;
                    for (int i = 0; i < siblings.length; i++) {
                        if (treeViewer.getChecked(siblings[i])) {
                            oneSiblingChecked = true;
                        } else {
                            allSiblingsChecked = false;
                        }
                    }
                    if (allSiblingsChecked) { // mark parent checked and NOT grayed out
                        treeViewer.setGrayed(parent, false);
                        treeViewer.setChecked(parent, true);
                    } else {
                        if (oneSiblingChecked) { // mark parent checked and grayed out
                            treeViewer.setGrayChecked(parent, true);
                        } else { // mark parent NOT checked
                            treeViewer.setGrayChecked(parent, false);
                        }
                    }
                }
            }
        });

        // populate the tree
        treeViewer.setInput(processor);
        treeViewer.expandAll();

        Control control = treeViewer.getControl();
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 250; // initial height of treeViewer (in pixels?)
        gd.verticalSpan = 3;
        control.setLayoutData(gd);
        control.setFont(font);

        // composite to hold the new/delete/save/... buttons
        buttonComposite = new Composite(comp, SWT.NONE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.widthHint = 200;
        buttonComposite.setLayoutData(gd);
        buttonComposite.setFont(comp.getFont());

        GridLayout buttonLayout = new GridLayout(2, false);
        buttonComposite.setLayout(buttonLayout);

        // construct the new validation test button
        newButton = new Button(buttonComposite, SWT.PUSH);
        newButton.setFont(parent.getFont());
        newButton.setText(Messages.ValidationDialog_new);
        newButton.setEnabled(false);
        newButton.addSelectionListener(new NewTestListener());
        setButtonLayoutData(newButton);

        // construct the delete validation test button
        deleteButton = new Button(buttonComposite, SWT.PUSH);
        deleteButton.setFont(parent.getFont());
        deleteButton.setText(Messages.ValidationDialog_delete);
        deleteButton.setEnabled(false);
        deleteButton.addSelectionListener(new DeleteTestListener());
        setButtonLayoutData(deleteButton);

        // construct treeViewer listener
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (!event.getSelection().isEmpty()) {
                    StructuredSelection selection = (StructuredSelection) event.getSelection();
                    if (selection.size() == 1) {
                        Object element = selection.getFirstElement();
                        selectedTreeItem = element; // record the current selection so other events
                                                    // can figure out who is selected
                        if (element instanceof TestDTO) { // test instance was selected
                            newButton.setEnabled(false);
                            deleteButton.setEnabled(true);
                            nameText.setText(((TestDTO) element).getName());
                            nameText.setEditable(true);
                            descText.setText(((TestDTO) element).getDescription());
                            descText.setEditable(true);
                            tableViewer.setInput(element); // pass the args (inside the testDTO)
                            tableViewer.getControl().setEnabled(true);
                            resizeTable();
                        } else if (element instanceof PlugInDTO) { // validation parent (plugin) was
                                                                   // selection
                            newButton.setEnabled(true);
                            deleteButton.setEnabled(false);
                            nameText.setText(((PlugInDTO) selection.getFirstElement()).getName());
                            nameText.setEditable(false);
                            descText.setText(
                                    ((PlugInDTO) selection.getFirstElement()).getDescription());
                            descText.setEditable(false);
                            tableViewer.setInput(null); // hide arguments
                            tableViewer.getControl().setEnabled(false);
                        } else { // this shouldn't be called
                            newButton.setEnabled(false);
                            deleteButton.setEnabled(false);
                            nameText.setText(""); //$NON-NLS-1$
                            nameText.setEditable(false);
                            descText.setText(""); //$NON-NLS-1$
                            descText.setEditable(false);
                            tableViewer.setInput(null); // hide arguments
                            tableViewer.getControl().setEnabled(false);
                        }

                    } else { // more than one selection was made
                        selectedTreeItem = null;
                        newButton.setEnabled(false);
                        deleteButton.setEnabled(false);
                        nameText.setEditable(false);
                        descText.setEditable(false);
                        tableViewer.setInput(null); // hide arguments
                        tableViewer.getControl().setEnabled(false);
                    }
                    updateButtons();
                }
            }

        });

        treeViewer.addCheckStateListener(new ICheckStateListener() {

            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                updateButtons();

                if (event.getElement() instanceof PlugInDTO) {
                    if (event.getChecked()) {
                        // select the parent if is not already
                        ISelection selection;
                        if (selectedTreeItem != event.getElement()) {
                            selection = new StructuredSelection(event.getElement());
                            treeViewer.setSelection(selection);
                        }
                        // automatically create a child if none exist
                        if (!contentProvider.hasChildren(selectedTreeItem)) {
                            TestDTO newTest = addTest();
                            selection = new StructuredSelection(newTest);
                            treeViewer.setSelection(selection);
                        }
                    }
                }
            }

        });

        return comp;
    }

    /**
     * Creates the validations edit area of the dialog. This area displays the name and description
     * of the validation test currently selected. Instances of plugins may only be viewed, while
     * instances of validation tests (within the testSuite) may be modified.
     *
     * @return the composite used for launch configuration editing
     */
    protected Composite createValidationEditArea(Composite parent) {
        // create a composite to place our form objects into
        Font font = parent.getFont();
        Composite comp = new Composite(parent, SWT.NONE);
        // setSelectionArea(comp);
        GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = 5;
        layout.marginWidth = 5;
        comp.setLayout(layout);
        comp.setFont(font);

        // create the "name" label
        Label nameLabel = new Label(comp, SWT.TOP | SWT.LEFT);
        nameLabel.setFont(font);
        nameLabel.setText(Messages.ValidationDialog_name);
        GridData gd = new GridData();
        nameLabel.setLayoutData(gd);

        // create the "name" text box
        nameText = new Text(comp, SWT.BORDER | SWT.SINGLE | SWT.FILL);
        gd = new GridData(SWT.FILL, SWT.TOP, true, false);
        nameText.setLayoutData(gd);
        nameText.addModifyListener(new NameModifiedListener());

        // create the "description" label
        Label descLabel = new Label(comp, SWT.TOP | SWT.LEFT);
        descLabel.setFont(font);
        descLabel.setText(Messages.ValidationDialog_description);
        gd = new GridData();
        nameLabel.setLayoutData(gd);

        // create the "description" text box
        descText = new Text(comp, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        gd = new GridData(SWT.FILL, SWT.TOP, true, false);
        gd.heightHint = 60;
        descText.setLayoutData(gd);
        descText.addModifyListener(new DescModifiedListener());

        // create a new table object
        Table table = new Table(comp, SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.RESIZE);

        // create a tableViewer containing the table
        tableViewer = createTable(table);
        tableViewer.setContentProvider(new ValidationTableContentProvider());
        tableViewer.setLabelProvider(new ValidationTableLabelProvider());
        tableViewer.setInput(null);

        Control control = tableViewer.getControl();
        gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 200; // initial height of treeViewer (in pixels?)
        control.setLayoutData(gd);
        control.setFont(font);

        tableViewer.setCellModifier(new CellModifiedListener());

        CellEditor[] editors = new CellEditor[2];
        editors[0] = null;
        Object[] allLayers = ApplicationGIS.getActiveMap().getMapLayers().toArray();

        ArrayList<String> typeRefsList = new ArrayList<>();
        ArrayList<String> layerNamesList = new ArrayList<>();

        typeRefsList.add(""); //$NON-NLS-1$
        layerNamesList.add(""); //$NON-NLS-1$
        typeRefsList.add("*"); //$NON-NLS-1$
        layerNamesList.add("*"); //$NON-NLS-1$

        for (int i = 0; i < allLayers.length; i++) {
            ILayer thisLayer = (ILayer) allLayers[i];
            SimpleFeatureType schema = thisLayer.getSchema();
            if (schema == null)
                continue;

            String dsID = schema.getName().getNamespaceURI();
            String typeName = thisLayer.getName();

            if (dsID == null || typeName == null)
                continue;

            typeRefsList.add(dsID + ":" + typeName); //$NON-NLS-1$
            layerNamesList.add(typeName);
        }

        typeRefs = typeRefsList.toArray(new String[typeRefsList.size()]);
        layerNames = layerNamesList.toArray(new String[layerNamesList.size()]);

        // create the text/combo cell editor
        editors[1] = new AmbiguousCellEditor(table, table, layerNamesList, typeRefsList);
        // only the layer names will show up in the combo, but typeRef is the real value

        tableViewer.setCellEditors(editors);
        // note: Argument and Value below are internal tags, not labels
        tableViewer.setColumnProperties(new String[] { "Argument", "Value" }); //$NON-NLS-1$//$NON-NLS-2$

        // create a table settings object and configure it
        tableSettings = new TableSettings(table);
        tableSettings.setColumnMin(0, 65);

        return comp;
    }

    protected TableViewer createTable(Table table) {
        TableViewer tableViewer = new TableViewer(table);

        TableLayout layout = new TableLayout();
        layout.addColumnData(new ColumnWeightData(50, 65, true));
        TableColumn nameColumn = new TableColumn(table, SWT.LEFT);
        nameColumn.setText(Messages.ValidationDialog_argument);
        nameColumn.setWidth(65);
        layout.addColumnData(new ColumnWeightData(50, 75, true));
        table.setLayout(layout);
        TableColumn valColumn = new TableColumn(table, SWT.LEFT);
        valColumn.setText(Messages.ValidationDialog_value);
        valColumn.setWidth(75);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        return tableViewer;
    }

    /**
     * Create the progress monitor part in the receiver.
     *
     * @param composite
     * @param pmlayout
     * @return ProgressMonitorPart
     */
    protected ProgressMonitorPart createProgressMonitorPart(Composite composite,
            GridLayout pmlayout) {
        return new ProgressMonitorPart(composite, pmlayout, SWT.DEFAULT) {
            String currentTask = null;

            boolean lockedUI = false;

            @Override
            public void setBlocked(IStatus reason) {
                super.setBlocked(reason);
                if (!lockedUI)// Do not show blocked if we are locking the UI
                    getBlockedHandler().showBlocked(getShell(), this, reason, currentTask);
            }

            @Override
            public void clearBlocked() {
                super.clearBlocked();
                if (!lockedUI)// Do not clear if we never set it
                    getBlockedHandler().clearBlocked();
            }

            @Override
            public void beginTask(String name, int totalWork) {
                super.beginTask(name, totalWork);
                currentTask = name;
            }

            @Override
            public void setTaskName(String name) {
                super.setTaskName(name);
                currentTask = name;
            }

            @Override
            public void subTask(String name) {
                super.subTask(name);
                // If we haven't got anything yet use this value for more context
                if (currentTask == null)
                    currentTask = name;
            }
        };
    }

    // TODO: implement runValidationTestsOptimized

    private GenericValidationResults[] runValidationTestsUnOptimized(IProgressMonitor monitor,
            Object[] element) throws Exception {
        // wander through the tree and run each enabled test in the processor individually
        // filter out the pluginDTOs, leaving only the TestDTOs
        List<TestDTO> tests = new ArrayList<>();
        for (int i = 0; i < element.length; i++) {
            if (element[i] instanceof TestDTO) {
                tests.add((TestDTO) element[i]);
            }
        }

        if (monitor == null)
            monitor = new NullProgressMonitor();
        monitor.beginTask(Messages.ValidationDialog_validating + Messages.ValidationDialog_ellipsis,
                (tests.size() * 11) + 2);
        // update the Lookup Maps
        processor.updateFVLookup();
        // processor.updateIVLookup();
        monitor.worked(1);

        // TODO: ensure typeRefs are copied

        // monitor.beginTask("Running Tests", tests.size());

        // FIXME: use the selected map rather than layer[0]
        ILayer[] layers = (ILayer[]) ApplicationGIS.getActiveMap().getMapLayers().toArray();

        // perform feature tests
        GenericValidationResults[] results = new GenericValidationResults[tests.size()];

        // open the issues list
        openIssuesList();
        monitor.worked(1);

        for (int i = 0; i < tests.size(); i++) {
            results[i] = new GenericValidationResults();
            // check for cancellation
            if (monitor.isCanceled()) {
                break;
            }
            // proceed
            String testName = tests.get(i).getName();
            // TODO: run as either feature or integrity test
            monitor.subTask(""); //$NON-NLS-1$
            monitor.setTaskName(Messages.ValidationDialog_validating + " " + testName //$NON-NLS-1$
                    + Messages.ValidationDialog_ellipsis);
            SubMonitor subMonitor = SubMonitor.convert(monitor, 10);
            processor.runFeatureTest(testName, layers, results[i], subMonitor);
            // processor.runIntegrityTest(test.getName(), layers, results[i], monitor);

            // check for cancellation again...
            if (subMonitor.isCanceled()) {
                monitor.setCanceled(true);
            }
            if (monitor.isCanceled()) {
                break;
            }
            // add to issues list
            monitor.subTask(MessageFormat.format(Messages.ValidationDialog_populating, testName));
            IIssuesManager.defaultInstance.getIssuesList().addAll(results[i].issues);
            monitor.worked(1);
        }
        monitor.done();
        return results;
    }

    /**
     * Based on the name of the argument, this function determines if it is a typeRef or not.
     *
     * @param argName
     * @return
     */
    public static boolean isTypeRef(String argName) {
        if (argName.toLowerCase().contains("typeref")) //$NON-NLS-1$
            return true;
        else
            return false;
    }

    public static String getTypeRefLayer(String typeRef) {
        for (int i = 0; i < typeRefs.length; i++) {
            if (typeRefs[i].equals(typeRef)) {
                return layerNames[i];
            }
        }
        return null;
    }

    /**
     * Creates a validation processor. Both parameters may be null.
     *
     * @param pluginsDir
     * @param testSuiteFile
     * @return
     * @throws Exception
     */
    private ValidationProcessor createProcessor(File pluginsDir, File testSuiteFile)
            throws Exception {
        if (pluginsDir == null) {
            URL pluginURL = ValidationPlugin.getDefault().getBundle().getResource("plugins"); //$NON-NLS-1$
            String pluginsPath = FileLocator.toFileURL(pluginURL).getFile();
            pluginsDir = new File(pluginsPath);
        }
        return new ValidationProcessor(pluginsDir, testSuiteFile);
    }

    /**
     * Resizes the table columns to behave better.
     *
     * @param table
     */
    private void resizeTable() {
        Table table = tableViewer.getTable();
        TableUtils.resizeColumns(table, tableSettings, TableUtils.MODE_LAZY);
    }

    /**
     * Sets the given cursor for all shells currently active for this window's display.
     *
     * @param c the cursor
     */
    private void setDisplayCursor(Cursor c) {
        Shell[] shells = display.getShells();
        for (int i = 0; i < shells.length; i++)
            shells[i].setCursor(c);
    }

    /** Enables/Disables the buttons as appropriate */
    private void updateButtons() {
        // check to see if any tests exist in the testSuite
        boolean testsExist;
        testsExist = processor.testsExist(defaultTestSuite);
        exportButton.setEnabled(testsExist);
        // only allow tests to be run when at least one is enabled
        boolean testsEnabled = false;
        if (testsExist) {
            Object[] elements = treeViewer.getCheckedElements();
            for (int i = 0; i < elements.length; i++) {
                if (elements[i] instanceof TestDTO) {
                    testsEnabled = true;
                    break;
                }
            }
        }
        runButton.setEnabled(testsEnabled);
        if (treeViewer.getSelection().isEmpty()) {
            newButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }
        // clear the message bar (validation complete)
        setMessage(""); //$NON-NLS-1$
    }

    private void openIssuesList() throws PartInitException {
        if (Display.getCurrent() != null)
            try {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                        .showView(IssueConstants.VIEW_ID);
            } catch (PartInitException e) {
                ValidationPlugin.log("error opening issues view", e); //$NON-NLS-1$
            }
        else {
            display.asyncExec(new Runnable() {
                @Override
                public void run() {
                    try {
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                                .showView(IssueConstants.VIEW_ID);
                    } catch (PartInitException e) {
                        ValidationPlugin.log("error opening issues view", e); //$NON-NLS-1$
                    }
                }
            });
        }

    }

    private class DescModifiedListener implements ModifyListener {
        /**
         * Saves changes made to the description field
         */
        @Override
        public void modifyText(ModifyEvent e) {
            // ensure this is a validation test description (plugin descriptions cannot be modified)
            if (selectedTreeItem instanceof TestDTO) {
                // save the text
                TestDTO treeItem = ((TestDTO) selectedTreeItem);
                treeItem.setDescription(descText.getText());
            }
        }
    }

    private class CellModifiedListener implements ICellModifier {
        @Override
        public boolean canModify(Object element, String property) {
            return true;
        }

        @Override
        public Object getValue(Object element, String property) {
            if (property.equals("Argument")) //$NON-NLS-1$
                return ((ArgumentDTO) element).getName();
            else {
                Object value = ((ArgumentDTO) element).getValue();
                // on read of arg value: null --> ""
                // on write of arg value: "" --> null
                if (value == null)
                    return ""; //$NON-NLS-1$
                else
                    return value;
            }
        }

        @Override
        public void modify(Object element, String property, Object value) {
            if (property.equals("Value")) { //$NON-NLS-1$
                // store the new value in the testSuite
                TableItem tableItem = (TableItem) element;
                ArgumentDTO arg = (ArgumentDTO) tableItem.getData();
                if (value instanceof Integer) { // comboBox
                    if (value.equals(-1))
                        arg.setValue(""); //$NON-NLS-1$
                    else {
                        int val = (Integer) value;
                        arg.setValue(typeRefs[val]);
                    }
                } else if (value instanceof String) { // textBox
                    // on read of arg value: null --> ""
                    // on write of arg value: "" --> null
                    if (value.equals("")) //$NON-NLS-1$
                        arg.setValue(null);
                    else
                        arg.setValue(value);
                }
                tableViewer.refresh();
                // update the argument value in the validation test lookups (FV and IV)
                try {
                    processor.setArg((TestDTO) selectedTreeItem, arg);
                } catch (ValidationException e) {
                    // TODO Handle ValidationException
                    throw (RuntimeException) new RuntimeException().initCause(e);
                } catch (IntrospectionException e) {
                    // TODO Handle IntrospectionException
                    throw (RuntimeException) new RuntimeException().initCause(e);
                }
            }
        }
    }

    private class NameModifiedListener implements ModifyListener {
        /**
         * Saves changes made to the name field
         */
        @Override
        public void modifyText(ModifyEvent e) {
            // ensure this is a validation test description (plugin descriptions cannot be modified)
            if (selectedTreeItem instanceof TestDTO) {
                // save the text
                TestDTO treeItem = ((TestDTO) selectedTreeItem);
                boolean renameSuccess = processor.renameValidation(treeItem.getName(),
                        nameText.getText(), defaultTestSuite);
                if (renameSuccess) {
                    treeViewer.refresh();
                    getDialog().setErrorMessage(null);
                } else {
                    // user tried to create 2 identical test names -- complain.
                    getDialog().setErrorMessage(Messages.ValidationDialog_nonUniqueTest);
                }
            }
        }
    }

    private TestDTO addTest() {
        Object selection = selectedTreeItem;
        TestDTO thisTest = null;
        // plugin is selected?
        if (selection instanceof PlugInDTO) {
            // create a new validation test object
            PlugInDTO plugin = (PlugInDTO) selection;
            Validation newTest = null;
            try {
                newTest = processor.createValidation(plugin);
            } catch (Exception e1) {
                // log the exception and return
                MessageDialog.openError(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        "Exception Occurred", e1.getClass().toString() + " " + e1.getMessage()); //$NON-NLS-1$//$NON-NLS-2$
                ValidationPlugin.log(e1.getMessage(), e1);
                return null;
            }
            // determine which TestSuiteDTO to use (for now, use the default)
            String testSuiteDTOKey = defaultTestSuite;
            // add the validation to the processor (creates a testDTO
            // and adds the validation to the appropriate lookup)
            processor.addValidation(newTest, plugin, testSuiteDTOKey);
            // if a defaultTypeRef exists, set any typeRefs to that value
            if ((defaultTypeRef != null) && defaultTypeRef.length() > 0) {
                thisTest = (TestDTO) processor.getTests().get(newTest.getName());
                Map args = thisTest.getArgs();
                for (Iterator i = args.keySet().iterator(); i.hasNext();) {
                    ArgumentDTO arg = (ArgumentDTO) args.get(i.next());
                    String argName = arg.getName();
                    // is it a typeRef?
                    if (isTypeRef(argName)) {
                        // is it empty?
                        if (arg.getValue() == null || arg.getValue().toString().length() == 0) {
                            arg.setValue(defaultTypeRef);
                        }
                    }
                }
            }
            // if the current item is not expanded, expand it to show the new item
            if (!treeViewer.getExpandedState(selectedTreeItem)) {
                treeViewer.setExpandedState(selectedTreeItem, true);
            }
            // if the current parent test is checked and not grayed, check off the new test
            if (treeViewer.getChecked(selectedTreeItem)
                    && !treeViewer.getGrayed(selectedTreeItem)) {
                treeViewer.setChecked(thisTest, true);
            }
            // refresh
            treeViewer.refresh();
            updateButtons();
        }
        return thisTest;
    }

    private class NewTestListener implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent e) {
            addTest();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }

    }

    private class DeleteTestListener implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent e) {
            Object selection = selectedTreeItem;
            // a test is selected?
            if (selection instanceof TestDTO) {
                // find the validation, given the TestDTO
                TestDTO test = (TestDTO) selection;
                // delete the validation test
                processor.removeValidation(test);
                treeViewer.refresh();
                updateButtons();
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
    }

    private class RunTestsListener implements SelectionListener {

        GenericValidationResults[] results = new GenericValidationResults[0];

        @Override
        public void widgetSelected(final SelectionEvent e) {
            final Object[] element = treeViewer.getCheckedElements();

            IRunnableWithProgress process = new IRunnableWithProgress() {
                @Override
                public void run(final IProgressMonitor monitor)
                        throws InvocationTargetException, InterruptedException {
                    try {
                        results = runValidationTestsUnOptimized(monitor, element);
                    } catch (Exception e) {
                        monitor.setCanceled(true);
                        ValidationPlugin.log("Error running validation Tests", e); //$NON-NLS-1$
                    }
                }
            };

            Control focusControl = null;
            Cursor arrowCursor = null;
            Cursor waitCursor = null;
            try {
                focusControl = display.getFocusControl();
                progressMonitorPart.setVisible(true);
                runButton.setEnabled(false);
                cancelButton.removeSelectionListener(closeListener);
                cancelButton.addSelectionListener(cancelListener);
                waitCursor = new Cursor(display, SWT.CURSOR_WAIT);
                setDisplayCursor(waitCursor);
                // Set the arrow cursor to the cancel component.
                arrowCursor = new Cursor(display, SWT.CURSOR_ARROW);
                cancelButton.setText(IDialogConstants.CANCEL_LABEL);
                cancelButton.setCursor(arrowCursor);
                cancelButton.setFocus();
                setMessage("Validation in progress..."); //$NON-NLS-1$
                PlatformGIS.runBlockingOperation(process, progressMonitorPart);
                // progressMonitorPart.setVisible(false);
            } catch (InvocationTargetException e1) {
                // TODO Handle InvocationTargetException
                throw (RuntimeException) new RuntimeException().initCause(e1);
            } catch (InterruptedException e1) {
                // TODO Handle InterruptedException
                throw (RuntimeException) new RuntimeException().initCause(e1);
            } finally {
                // restore listeners
                cancelButton.removeSelectionListener(cancelListener);
                cancelButton.addSelectionListener(closeListener);
                cancelButton.setText(IDialogConstants.CLOSE_LABEL);
                // fix cursors
                setDisplayCursor(null);
                cancelButton.setCursor(null);
                waitCursor.dispose();
                waitCursor = null;
                arrowCursor.dispose();
                arrowCursor = null;
                // enable buttons
                cancelButton.setEnabled(true);
                runButton.setEnabled(true);
                // display response
                if (progressMonitorPart.isCanceled()) {
                    setMessage("Validation was canceled"); //$NON-NLS-1$
                    progressMonitorPart.setCanceled(false);
                } else {
                    int failures = 0;
                    int warnings = 0;
                    if (results != null) {
                        for (int i = 0; i < results.length; i++) {
                            if (results[i] != null) {
                                failures += results[i].failedFeatures.size();
                                warnings += results[i].warningFeatures.size();
                            }
                        }
                    }
                    setMessage("Validation complete, " + failures + " failures and " + warnings //$NON-NLS-1$ //$NON-NLS-2$
                            + " warnings found."); //$NON-NLS-1$
                }
                focusControl.setFocus();
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }

    }

    private class ImportSuiteListener implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent e) {
            // determine the file we want to open
            String fileName = null;
            // spawn a dialog and choose which file to import
            Display display = Display.getCurrent();
            if (display == null) { // not on the ui thread?
                display = Display.getDefault();
            }
            FileDialog importDialog = new FileDialog(display.getActiveShell(), SWT.OPEN);

            // final IPath homepath = Platform.getLocation();
            // exportDialog.setFilterPath(homepath.toOSString());
            importDialog.setFilterNames(new String[] { Messages.ValidationDialog_filesXML,
                    Messages.ValidationDialog_filesAll });
            importDialog.setFilterExtensions(new String[] { "*.xml", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$

            boolean done = false;
            while (!done) {
                fileName = importDialog.open();
                if (fileName == null) {
                    done = true;
                    return;
                } else {
                    // User has selected a file; see if it already exists
                    File file = new File(fileName);
                    if (!file.exists()) {
                        // The file does not exist; yell at the user and try again
                        MessageBox mb = new MessageBox(importDialog.getParent(),
                                SWT.ICON_ERROR | SWT.OK);
                        mb.setMessage(fileName + Messages.ValidationDialog_fileNotExist);
                        mb.open();
                    } else {
                        // File exists, so we're good to go
                        done = true;
                    }
                }
            }

            // read the file
            FileReader reader;
            try {
                reader = new FileReader(fileName);
            } catch (FileNotFoundException e3) {
                MessageDialog.openError(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        Messages.ValidationDialog_fileNotFound, e3.toString());
                ValidationPlugin.log(e3.toString()); // log the error, but don't throw the exception
                return;
            }
            TestSuiteDTO newDTO;
            try {
                newDTO = XMLReader.readTestSuite(fileName, reader, processor.getPluginDTOs());
            } catch (ValidationException e3) {
                String errorMsg = e3.toString();
                MessageDialog.openError(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        "Exception Occurred", errorMsg); //$NON-NLS-1$
                ValidationPlugin.log(errorMsg, e3);
                return;
            }
            try {
                reader.close();
            } catch (IOException e2) {
                MessageDialog.openError(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        "Exception Occurred", e2.toString()); //$NON-NLS-1$
                ValidationPlugin.log(e2.toString(), e2);
                return;
            }
            // get the existing testSuites
            Map<String, TestSuiteDTO> suites = processor.getTestSuiteDTOs();
            // ensure there is at least one test in the new testSuite
            if (newDTO.getTests().isEmpty()) {
                // nothing to see here, move along
                return;
            }
            // if no testSuites exist, just copy the new one directly in
            if (suites.isEmpty()) {
                suites.put(newDTO.getName(), newDTO);
                defaultTestSuite = newDTO.getName();
                // does the testSuite exist? if so, add the new tests to the existing one
            } else if (suites.containsKey(newDTO.getName())) {
                // ensure the current testSuite is selected
                defaultTestSuite = newDTO.getName();
                // get the existing testSuite
                TestSuiteDTO testSuite = suites.get(defaultTestSuite);
                // move the tests to the existing testSuite
                testSuite = processor.moveTests(testSuite, newDTO.getTests(), false);
                suites.put(defaultTestSuite, testSuite); // overwrite the suite
                // a test Suite exists, but it isn't this one; put the new tests into the existing
                // testSuite
            } else {
                TestSuiteDTO testSuite = suites.get(defaultTestSuite);
                Map<String, TestDTO> tests = newDTO.getTests();
                testSuite = processor.moveTests(testSuite, tests, false);
                suites.put(defaultTestSuite, testSuite); // overwrite the suite with new map of
                                                         // tests
            }
            // do multiple testSuites exist? if so, merge them
            while (suites.size() > 1) {
                // find the first testSuite which isn't the defaultTestSuite
                Object key = null;
                for (Iterator i = suites.keySet().iterator(); i.hasNext();) {
                    Object thisKey = i.next();
                    if (!(thisKey.equals(defaultTestSuite))) {
                        key = thisKey;
                        break;
                    }
                }
                if (key != null) {
                    TestSuiteDTO alphaSuite = suites.get(defaultTestSuite);
                    TestSuiteDTO betaSuite = suites.get(key);
                    alphaSuite = processor.moveTests(alphaSuite, betaSuite.getTests(), false);
                    suites.remove(key); // bye betaSuite!
                    suites.put(defaultTestSuite, alphaSuite); // overwrite the suite (alphaSuite has
                                                              // now assimilated betaSuite)
                }
            }
            // all done; save the Map of testSuites and refresh the Tree
            processor.setTestSuiteDTOs(suites);
            treeViewer.refresh();
            updateButtons();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
    }

    private class ExportSuiteListener implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent e) {
            Display display = Display.getCurrent();
            if (display == null) { // not on the ui thread?
                display = Display.getDefault();
            }
            // grab the testSuite we want to save
            TestSuiteDTO testSuite = processor.getTestSuiteDTOs().get(defaultTestSuite);
            // testSuite is empty?
            if (testSuite.getTests().isEmpty()) {
                MessageBox mb = new MessageBox(display.getActiveShell(), SWT.ICON_ERROR | SWT.OK);
                mb.setMessage(Messages.ValidationDialog_noSuitePre + defaultTestSuite
                        + Messages.ValidationDialog_noSuiteSuf);
                mb.open();
                return;
            }
            // check the testSuite to ensure that none of the arguments of a test are null
            if (!DTOUtils.noNullArguments(testSuite))
                return;
            // select the file to export to
            String fileName = null;
            // spawn a dialog and choose which file to export to
            FileDialog exportDialog = new FileDialog(display.getActiveShell(), SWT.SAVE);

            // final IPath homepath = Platform.getLocation();
            // exportDialog.setFilterPath(homepath.toOSString());
            exportDialog.setFilterNames(new String[] { Messages.ValidationDialog_filesXML,
                    Messages.ValidationDialog_filesAll });
            exportDialog.setFilterExtensions(new String[] { "*.xml", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$

            boolean done = false;
            while (!done) {
                fileName = exportDialog.open();
                if (fileName == null) {
                    done = true;
                    return;
                } else {
                    // User has selected a file; see if it already exists
                    File file = new File(fileName);
                    if (file.exists()) {
                        // The file already exists; asks for confirmation
                        MessageBox mb = new MessageBox(exportDialog.getParent(),
                                SWT.ICON_WARNING | SWT.YES | SWT.NO);

                        mb.setMessage(fileName + Messages.ValidationDialog_fileExists);

                        // If they click Yes, we're done and we drop out. If
                        // they click No, we redisplay the File Dialog
                        done = mb.open() == SWT.YES;
                    } else {
                        // File does not exist, so we're good to go
                        done = true;
                    }
                }
            }

            // construct a writer
            Writer writer;
            try {
                writer = new FileWriter(fileName, false);
            } catch (IOException e1) {
                // TODO Handle IOException
                throw (RuntimeException) new RuntimeException().initCause(e1);
            }
            // write the file
            XMLWriter.writeTestSuite(testSuite, writer);
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
    }

    /**
     * Retrieves the ValidationProcessor object from the blackboard, or null if one does not exist.
     *
     * @param map
     * @return
     */
    private ValidationProcessor loadDialogState(IMap map) {
        Object currentState = map.getBlackboard().get(BLACKBOARD_KEY);
        if (currentState instanceof ValidationProcessor)
            return (ValidationProcessor) currentState;
        else
            return null;
    }

    /**
     * Saves the ValidationProcessor object on the blackboard, so we can restore the state of the
     * validation dialog the next time the dialog is opened.
     *
     * @param validationProcessor
     * @param map
     */
    private void saveDialogState(ValidationProcessor validationProcessor, IMap map) {
        map.getBlackboard().put(BLACKBOARD_KEY, validationProcessor);
    }
}
