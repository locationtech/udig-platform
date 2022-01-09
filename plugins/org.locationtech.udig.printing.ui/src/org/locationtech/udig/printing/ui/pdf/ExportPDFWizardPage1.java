/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.printing.ui.pdf;

import java.io.File;
import java.util.Map;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.locationtech.udig.printing.ui.Template;
import org.locationtech.udig.printing.ui.TemplateFactory;
import org.locationtech.udig.printing.ui.internal.Messages;

public class ExportPDFWizardPage1 extends WizardPage implements Listener {

    // public constants
    public static final int CURRENT_MAP_SCALE = 1;

    public static final int CUSTOM_MAP_SCALE = 2;

    public static final int ZOOM_TO_SELECTION = 3;

    // constants
    protected static final int PREFERRED_HEIGHT = 50;

    // widgets
    private CheckboxTableViewer listViewer;

    private Text fileText;

    private Combo directoryNameField;

    private Button directoryBrowseButton;

    private Button exportRasterCheckbox;

    private Button portraitButton;

    private Button landscapeButton;

    private Combo dpiCombo;

    private Combo pageCombo;

    private Button currentScaleButton;

    private Combo customScaleCombo;

    private Button customScaleButton;

    private Button zoomToSelectionButton;

    // other stuff
    private Map<String, TemplateFactory> templateFactories;

    private ExportPDFWizardConfigBean config;

    /**
     * Initialize the page with title and description
     */
    public ExportPDFWizardPage1(Map<String, TemplateFactory> templateFactories,
            ExportPDFWizardConfigBean config) {
        super("ExportPDFFromTemplatePage"); //$NON-NLS-1$
        setTitle(
                org.locationtech.udig.printing.ui.internal.Messages.ExportPDFWizardPage1_PAGE_TITLE);
        setDescription(
                org.locationtech.udig.printing.ui.internal.Messages.ExportPDFWizardPage1_PAGE_DESC);

        this.templateFactories = templateFactories;
        if (config == null) {
            this.config = new ExportPDFWizardConfigBean();
        } else {
            this.config = config;
        }
    }

    /**
     * layout the page
     */
    @Override
    public void createControl(Composite parent) {

        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(
                new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));

        createListViewer(composite, true);
        createDestinationGroup(composite);
        createOptionsGroup(composite);

        updateUIBasedOnTemplate();
        validatePage();

        setControl(composite);
    }

    /**
     * Create a list viewer for displaying the available templates.
     */
    protected void createListViewer(Composite parent, boolean useHeightHint) {
        listViewer = CheckboxTableViewer.newCheckList(parent, SWT.BORDER | SWT.SINGLE);

        GridData data = new GridData(GridData.FILL_BOTH);
        if (useHeightHint) {
            data.heightHint = PREFERRED_HEIGHT;
        }
        listViewer.getTable().setLayoutData(data);
        listViewer.getTable().setFont(parent.getFont());
        listViewer.setContentProvider(new ArrayContentProvider());
        listViewer.setLabelProvider(getLabelProvider());
        listViewer.setInput(templateFactories.values());

        // select first template in list
        if (!templateFactories.isEmpty()) {
            listViewer.setChecked(listViewer.getElementAt(0), true);
        }
        listViewer.addCheckStateListener(new ICheckStateListener() {

            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                Object[] allChecked = listViewer.getCheckedElements();
                for (int i = 0; i < allChecked.length; i++) {
                    if (!allChecked[i].equals(event.getElement())) {
                        listViewer.setChecked(allChecked[i], false);
                    }
                }
                updateUIBasedOnTemplate();
                validatePage();
            }
        });
    }

    /**
     * Change some of the selected "options" based on preferred settings for the selected template.
     */
    private void updateUIBasedOnTemplate() {
        Object[] allChecked = listViewer.getCheckedElements();
        if (allChecked.length == 0) {
            return;
        }
        TemplateFactory selectedFactory = (TemplateFactory) allChecked[0];
        Template selectedTemplate = selectedFactory.createTemplate();
        int orientation = selectedTemplate.getPreferredOrientation();
        if (orientation == Template.ORIENTATION_LANDSCAPE) {
            landscapeButton.setSelection(true);
            portraitButton.setSelection(false);
        } else {
            portraitButton.setSelection(true);
            landscapeButton.setSelection(false);
        }
    }

    /**
     * Create and return a label provider which turns assumes the input element is a string, and
     * returns it.
     *
     * @return an appropriate title
     */
    private ILabelProvider getLabelProvider() {
        return new LabelProvider() {
            @Override
            public String getText(Object element) {
                return ((TemplateFactory) element).getName();
            }
        };
    }

    /**
     * Create the UI controls for editing the output target
     *
     * @param parent
     */
    protected void createDestinationGroup(Composite parent) {

        Font font = parent.getFont();
        // destination specification group
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        composite.setFont(font);

        // to file ... label
        Label fileLabel = new Label(composite, SWT.NONE);
        fileLabel.setText(Messages.ExportPDFWizardPage1_DEST_FILE);
        fileLabel.setFont(font);

        // to file ... text
        fileText = new Text(composite, SWT.LEFT | SWT.BORDER);
        fileText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        fileText.setText(getDefaultOutputFile());
        fileText.addListener(SWT.Modify, this);

        // to directory ... label
        Label directoryLabel = new Label(composite, SWT.NONE);
        directoryLabel.setText(Messages.ExportPDFWizardPage1_DEST_DIR);
        directoryLabel.setFont(font);

        // to directory ... text
        Composite dirComposite = new Composite(composite, SWT.NONE);
        dirComposite.setLayout(new GridLayout(2, false));
        dirComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        directoryNameField = new Combo(dirComposite, SWT.SINGLE | SWT.BORDER);
        GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);

        directoryNameField.setLayoutData(data);
        directoryNameField.setFont(font);
        if (directoryNameField.getText().length() == 0) {
            directoryNameField.setText(System.getProperty("user.home")); //$NON-NLS-1$
        }
        directoryNameField.addListener(SWT.Modify, this);
        directoryNameField.addListener(SWT.Selection, this);

        // destination browse button
        directoryBrowseButton = new Button(dirComposite, SWT.PUSH);
        directoryBrowseButton.setText(Messages.ExportPDFWizardPage1_BROWSE);
        directoryBrowseButton.addListener(SWT.Selection, this);
        directoryBrowseButton.setFont(font);
        setButtonLayoutData(directoryBrowseButton);

    }

    private String getDefaultOutputFile() {
        if (config.getDefaultFilename() != null) {
            return config.getDefaultFilename();
        }
        return Messages.ExportPDFWizardPage1_DEFAULT_FILENAME;
    }

    /**
     * Create the UI control to represent the "options" group
     *
     * @param parent
     */
    protected void createOptionsGroup(Composite parent) {
        // options group
        Group optionsGroup = new Group(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        optionsGroup.setLayout(layout);
        optionsGroup.setLayoutData(
                new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        optionsGroup.setText(
                org.locationtech.udig.printing.ui.internal.Messages.ExportPDFWizardPage1_OPTIONS);
        optionsGroup.setFont(parent.getFont());

        createOptionsGroupControls(optionsGroup);

    }

    /**
     * Create the UI controls that fill up the "options" group.
     *
     * @param group The parent composite which surrounds the options controls
     */
    protected void createOptionsGroupControls(Group group) {
        Font font = group.getFont();
        group.setLayout(new GridLayout(2, true));
        group.setLayoutData(
                new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        Composite left = new Composite(group, SWT.NONE);
        left.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
        left.setLayout(new GridLayout(1, false));

        // radio button group for scale choices
        Group scaleGroup = new Group(left, SWT.NONE);
        scaleGroup.setLayout(new GridLayout(1, false));

        // current scale
        currentScaleButton = new Button(scaleGroup, SWT.RADIO);
        currentScaleButton.setText(Messages.ExportPDFWizardPage1_CURRENT_SCALE);

        // custom scale
        customScaleButton = new Button(scaleGroup, SWT.RADIO);
        customScaleButton.setText(Messages.ExportPDFWizardPage1_CUSTOM_SCALE);

        final Composite customScaleComposite = new Composite(scaleGroup, SWT.NONE);
        customScaleComposite.setLayout(new GridLayout(2, false));
        customScaleCombo = new Combo(customScaleComposite, SWT.NONE);
        customScaleCombo.setText("1000"); //$NON-NLS-1$
        customScaleCombo.setEnabled(false);
        customScaleCombo.addListener(SWT.Modify, this);
        customScaleCombo.addListener(SWT.Selection, this);

        // zoom to selection
        zoomToSelectionButton = new Button(scaleGroup, SWT.RADIO);
        zoomToSelectionButton.setText(Messages.ExportPDFWizardPage1_ZOOM_TO_SELECTION);

        // current scale listener
        customScaleButton.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event event) {

                if (customScaleButton.getSelection() == false) {
                    customScaleCombo.setEnabled(false);
                } else {
                    customScaleCombo.setEnabled(true);
                }
                validatePage();
            }

        });
        currentScaleButton.setSelection(true);

        Composite right = new Composite(group, SWT.NONE);
        right.setLayoutData(new GridData(SWT.LEFT, GridData.VERTICAL_ALIGN_BEGINNING, true, false));
        right.setLayout(new GridLayout(1, false));

        // export raster... checkbox
        exportRasterCheckbox = new Button(right, SWT.CHECK | SWT.LEFT);
        exportRasterCheckbox.setText(
                org.locationtech.udig.printing.ui.internal.Messages.ExportPDFWizardPage1_EXPORT_RASTERS);
        exportRasterCheckbox.setSelection(true);

        // Output DPI ... label and combo
        Composite dpiAndPageComposite = new Composite(right, SWT.NONE);
        dpiAndPageComposite.setLayoutData(
                new GridData(SWT.LEFT, GridData.VERTICAL_ALIGN_BEGINNING, true, false));
        dpiAndPageComposite.setLayout(new GridLayout(2, false));
        Label dpiLabel = new Label(dpiAndPageComposite, SWT.LEFT);
        dpiLabel.setText(
                org.locationtech.udig.printing.ui.internal.Messages.ExportPDFWizardPage1_DPI);
        dpiCombo = new Combo(dpiAndPageComposite, SWT.READ_ONLY);
        dpiCombo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        dpiCombo.setItems(new String[] { "72", //$NON-NLS-1$
                "144", //$NON-NLS-1$
                "300" }); //$NON-NLS-1$

        dpiCombo.setData(0 + "", 72); //$NON-NLS-1$
        dpiCombo.setData(1 + "", 144); //$NON-NLS-1$
        dpiCombo.setData(2 + "", 300); //$NON-NLS-1$
        dpiCombo.select(2);

        // Page Size
        Label pageLabel = new Label(dpiAndPageComposite, SWT.LEFT);
        pageLabel.setText(
                org.locationtech.udig.printing.ui.internal.Messages.ExportPDFWizardPage1_PAGE_SIZE);
        pageCombo = new Combo(dpiAndPageComposite, SWT.READ_ONLY);
        pageCombo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        pageCombo.add("A3"); //$NON-NLS-1$
        pageCombo.add("A4"); //$NON-NLS-1$
        pageCombo.select(0);

        // portrait & landscape
        portraitButton = new Button(dpiAndPageComposite, SWT.RADIO);
        portraitButton.setText("Portrait"); //$NON-NLS-1$
        landscapeButton = new Button(dpiAndPageComposite, SWT.RADIO);
        landscapeButton.setText("Landscape"); //$NON-NLS-1$

    }

    /**
     * returns a boolean indicating whether rasters layers are to be included in the output
     */
    protected boolean getRasterEnabled() {
        return exportRasterCheckbox.getSelection();
    }

    /**
     * Get the value of the destination directory field as it currently appears in the UI.
     *
     * @return output directory
     */
    protected String getDestinationDir() {
        return directoryNameField.getText().trim();
    }

    /**
     * Set the value of the destination directory field. The new value will be updated on the UI.
     *
     * @param value new value
     */
    protected void setDestinationDir(String value) {
        directoryNameField.setText(value);
    }

    /**
     * Gets the filename of the output file, as it's current displayed in the UI.
     *
     * @return the name of the output file (filename only, no directory)
     */
    protected String getOutputFile() {
        return fileText.getText();
    }

    /**
     * Get the page size as it currently appears in the UI.
     *
     * @return a Paper instance representing the page size
     */
    protected String getPageSize() {
        return pageCombo.getText();
    }

    /**
     * Gets the DPI selected in the UI
     *
     * @return the selected DPI
     */
    protected int getDpi() {
        return Integer.valueOf(dpiCombo.getText());
    }

    /**
     * Indicates whether the landscape option is selected
     *
     * @return
     */
    protected boolean isLandscape() {
        return (landscapeButton.getSelection());
    }

    /**
     * Indicates whether a custom scale is set in the UI
     *
     * @return a code indicating which scale option was selected. (CUSTOM_MAP_SCALE,
     *         CURRENT_MAP_SCALE, ZOOM_TO_SELECTION)
     *
     *         If CUSTOM_MAP_SCALE is set, call getCustomScale() for the scale denom value
     */
    protected int getScaleOption() {
        if (currentScaleButton.getSelection() == true)
            return CURRENT_MAP_SCALE;
        if (customScaleButton.getSelection() == true)
            return CUSTOM_MAP_SCALE;
        return ZOOM_TO_SELECTION;
    }

    /**
     * gets the custom scale denominator chosen in the UI. throws an IllegalStateException if a
     * custom scale is not set.
     *
     * @return the custom scale denominator
     */
    protected float getCustomScale() {
        if (getScaleOption() != CUSTOM_MAP_SCALE) {
            throw new IllegalStateException(Messages.ExportPDFWizardPage1_1);
        }
        return Float.parseFloat(customScaleCombo.getText());
    }

    /**
     * Responds to clicks of the "browse" button
     */
    protected void handleDestinationBrowseButtonPressed() {

        // open a directory chooser
        DirectoryDialog dialog = new DirectoryDialog(getContainer().getShell(), SWT.SAVE);
        dialog.setMessage("Choose a destination directory for the PDF document."); //$NON-NLS-1$
        dialog.setText("Destination Directory"); //$NON-NLS-1$
        dialog.setFilterPath(getDestinationDir());
        String selectedDirectoryName = dialog.open();

        // set chosen directory on the wizard page
        if (selectedDirectoryName != null) {
            setErrorMessage(null);
            setDestinationDir(selectedDirectoryName);
        }
    }

    /**
     * Gets the selected template factory.
     *
     * @return the (one) TemplateFactory selected on this page, or null if no factory is selected.
     */
    protected TemplateFactory getTemplateFactory() {
        Object[] elements = listViewer.getCheckedElements();
        assert (elements.length == 0 || elements.length == 1);

        if (elements.length == 1) {
            return (TemplateFactory) elements[0];
        }
        return null;
    }

    /**
     * Handle all events and enablements for widgets in this page
     *
     * @param e Event
     */
    @Override
    public void handleEvent(Event e) {
        Widget source = e.widget;

        if (source == directoryBrowseButton) {
            handleDestinationBrowseButtonPressed();
        }

        if (source == directoryNameField) {
            validatePage();
        }

        if (source == fileText) {
            validatePage();
        }

        if (source == customScaleCombo) {
            validatePage();
        }

    }

    private String getOutputFile(String fbid, String printNum) {
        return "RO_" + fbid + "_yymmdd_" + printNum + ".pdf"; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
    }

    /**
     * validates the page
     */
    private boolean validatePage() {
        boolean valid = (isOneTemplateChecked() && isScaleValid()
                && isTargetValid(fileText.getText(), directoryNameField.getText()));

        setPageComplete(valid);
        return valid;
    }

    /**
     * Determines whether exactly one template is checked in the list
     *
     * @return true if one template is checked, false otherwise.
     */
    private boolean isOneTemplateChecked() {
        if (listViewer.getCheckedElements().length != 1) {
            setMessage(Messages.ExportPDFWizardPage1_SELECT_TEMPLATE);
            return false;
        }
        setErrorMessage(null);
        return true;
    }

    /**
     * Validates the scale setting
     */
    public boolean isScaleValid() {
        if (currentScaleButton.getSelection() == true) {
            setMessage(null);
            return true;
        }

        try {
            float scale = Float.valueOf(customScaleCombo.getText());
            if (scale < 1) {
                setErrorMessage(Messages.ExportPDFWizardPage1_INVALID_SCALE);
            }
        } catch (NumberFormatException e) {
            setErrorMessage(""); //$NON-NLS-1$
            return false;
        }

        setMessage(null);
        return true;

    }

    /**
     * Validate the output file and directory
     *
     * @param file output file
     * @param dir output directory
     * @return true if everything is valid, false otherwise
     */
    private boolean isTargetValid(String file, String dir) {
        File targetDir = new File(dir);
        String filename = file.trim();

        // check that directory exists
        if (dir.length() == 0) {
            setMessage(""); //$NON-NLS-1$
            return false;
        } else if (!targetDir.exists() || !targetDir.isDirectory()) {
            setErrorMessage(""); //$NON-NLS-1$
            return false;
        }
        // Verify the filename is non-empty
        else if (filename.length() == 0) {
            setErrorMessage(""); //$NON-NLS-1$
            return false;
        }
        // Verify the file name does not begin with a dot
        else if (filename.charAt(0) == '.') {
            setErrorMessage(""); //$NON-NLS-1$
            return false;
        }
        // check that the file name ends with ".pdf" (just warn)
        else if (!filename.toUpperCase().endsWith(Messages.ExportPDFWizardPage1_0)) {
            setMessage("It is recommended that you save the file with a .pdf extension.", //$NON-NLS-1$
                    DialogPage.WARNING);
        } else {
            setMessage(null);
        }

        return true;
    }
}
