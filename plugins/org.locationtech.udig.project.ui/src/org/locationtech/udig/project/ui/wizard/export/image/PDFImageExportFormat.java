/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.wizard.export.image;

import java.awt.image.BufferedImage;
import java.io.File;

import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;

/**
 * A strategy for exporting to PDF
 *
 * @author jesse
 * @author Frank Gasdorf
 * @since 1.1.0
 */
public class PDFImageExportFormat extends ImageExportFormat {


    private static final int PDF_DEFAULT_USER_UNIT = 72;
    private static final String PREFSTORE_PARAM_MARGIN_TOP = "MarginTop";
    private static final String PREFSTORE_PARAM_MARGIN_BOTTOM = "MarginBottom";
    private static final String PREFSTORE_PARAM_MARGIN_RIGHT = "MarginRight";
    private static final String PREFSTORE_PARAM_MARGIN_LEFT = "MarginLeft";
    private static final String PREFSTORE_PARAM_DPI = "DPI";
    private static final String PREFSTORE_PARAM_PAGEFORMAT = "Pageformat";
    private static final String PREFSTORE_PARAM_LANDSCAPE = "Landscape";

    private Combo dpiCombo;
    private Spinner marginTopSpinner;
    private Spinner marginBottomSpinner;
    private Spinner marginLeftSpinner;
    private Spinner marginRightSpinner;
    private Combo paperCombo;
    private Button landscape;

    public String getExtension() {
        return "pdf"; //$NON-NLS-1$
    }

    public String getName() {
        return "PDF";
    }

    @Override
    public boolean useStandardDimensionControls() {
        return false;
    }

    @Override
    public void write( IMap map, BufferedImage image, File destination ) {
        savePreferences();
        Image2Pdf.write(image, destination.getAbsolutePath(), paper(),
               new Insets(
                       Paper.toPixels(this.marginTopSpinner.getSelection(), PDF_DEFAULT_USER_UNIT),
                       Paper.toPixels(this.marginLeftSpinner.getSelection(), PDF_DEFAULT_USER_UNIT),
                       Paper.toPixels(this.marginBottomSpinner.getSelection(), PDF_DEFAULT_USER_UNIT),
                       Paper.toPixels(this.marginRightSpinner.getSelection(), PDF_DEFAULT_USER_UNIT)
                       ), landscape());
    }

    @Override
    public void createControl( Composite comp ) {

        final Group group = new Group(comp, SWT.NONE);
        group.setText(Messages.ImageExportPage_PDF_Group_Description);
        group.setLayout(new GridLayout(4, false));

        createPaperLabel(group);
        createPaperCombo(group);
        createLandscapeCheckbox(group);
        createDpiCombo(group);
        createMarginsGroup(group);

        loadAndApplyPreferences();
        setControl(group);
    }

    private void createDpiCombo(Group group) {
        ImageExportPage.createLabel(group, "DPI:");
        dpiCombo = new Combo(group, SWT.READ_ONLY);
        dpiCombo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true,
                false));
        dpiCombo.setItems(new String[] { "72", //$NON-NLS-1$
                                         "144",  //$NON-NLS-1$
                                         "300" }); //$NON-NLS-1$

        dpiCombo.setData(0 + "", 72); //$NON-NLS-1$
        dpiCombo.setData(1 + "", 144); //$NON-NLS-1$
        dpiCombo.setData(2 + "", 300); //$NON-NLS-1$
        dpiCombo.select(0);

    }

    private void createMarginsGroup(Group group) {
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = false;
        gridData.verticalAlignment = GridData.CENTER;
        gridData.horizontalSpan = 4;
        Group marginsGroup = new Group(group, SWT.NONE);
        marginsGroup.setLayoutData(gridData);
        marginsGroup.setLayout(gridLayout);
        marginsGroup.setText(Messages.ImageExportPage_marginsGroup);
        Label topLabel = new Label(marginsGroup, SWT.NONE);
        topLabel.setText(Messages.ImageExportPage_topMargin);
        marginTopSpinner = new Spinner(marginsGroup, SWT.NONE);
        marginTopSpinner.setSelection(10);
        Label bottomLabel = new Label(marginsGroup, SWT.NONE);
        bottomLabel.setText(Messages.ImageExportPage_bottomMargin);
        marginBottomSpinner = new Spinner(marginsGroup, SWT.NONE);
        marginBottomSpinner.setSelection(10);
        Label leftLabel = new Label(marginsGroup, SWT.NONE);
        leftLabel.setText(Messages.ImageExportPage_leftMargin);
        marginLeftSpinner = new Spinner(marginsGroup, SWT.NONE);
        marginLeftSpinner.setSelection(10);
        Label rightLabel = new Label(marginsGroup, SWT.NONE);
        rightLabel.setText(Messages.ImageExportPage_rightMargin);
        marginRightSpinner = new Spinner(marginsGroup, SWT.NONE);
        marginRightSpinner.setSelection(10);

    }

    private void createPaperLabel(Group group) {
        ImageExportPage.createLabel(group, Messages.ImageExportPage_size_Label);
    }

    private void createPaperCombo(Group group) {
        paperCombo = new Combo(group, SWT.READ_ONLY);
        paperCombo
                .setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        Paper[] paperTypes = Paper.values();
        for (Paper paper : paperTypes) {
            paperCombo.add(paper.name());
        }
        paperCombo.select(0);
    }

    private void createLandscapeCheckbox(Group group) {
        landscape = new Button(group, SWT.CHECK);
        landscape.setText(Messages.ImageExportPage_landscapeLabel);

        final GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        gridData.horizontalSpan = 2;
        landscape
            .setLayoutData(gridData);
    }

    public boolean landscape() {
        return landscape.getSelection();
    }

    public Paper paper() {
        return Paper
                .valueOf(paperCombo.getItem(paperCombo.getSelectionIndex()));
    }

    /**
     * gets the output DPI
     *
     * @return output DPI
     */
    @Override
    public int getDPI() {
        return Integer.valueOf(dpiCombo.getItem(dpiCombo.getSelectionIndex())).intValue();
    }


    @Override
    public int getHeight( double mapwidth, double mapheight ) {
        // ignore viewport size of the current map and use paper format instead
        int paperHeight = paper().getPixelHeight(landscape(), getDPI());
        int topMargin = Paper.toPixels(marginTopSpinner.getSelection(), PDF_DEFAULT_USER_UNIT);
        int bottomMargin = Paper.toPixels(marginBottomSpinner.getSelection(), PDF_DEFAULT_USER_UNIT);

        return paperHeight - topMargin - bottomMargin;
    }

    @Override
    public int getWidth( double mapwidth, double mapheight ) {
        // ignore viewport size of the current map and use paper format instead
        int paperWidth = paper().getPixelWidth(landscape(), getDPI());
        int rightMargin = Paper.toPixels(marginRightSpinner.getSelection(), PDF_DEFAULT_USER_UNIT);
        int leftMargin = Paper.toPixels(marginLeftSpinner.getSelection(), PDF_DEFAULT_USER_UNIT);

        return paperWidth - rightMargin - leftMargin;
    }

    private void savePreferences() {
    	final IPreferenceStore prefStore = ProjectUIPlugin.getDefault().getPreferenceStore();
        prefStore.setValue(PREFSTORE_PARAM_MARGIN_TOP, marginTopSpinner.getSelection());
        prefStore.setValue(PREFSTORE_PARAM_MARGIN_BOTTOM, marginBottomSpinner.getSelection());
        prefStore.setValue(PREFSTORE_PARAM_MARGIN_LEFT, marginLeftSpinner.getSelection());
        prefStore.setValue(PREFSTORE_PARAM_MARGIN_RIGHT, marginRightSpinner.getSelection());
        prefStore.setValue(PREFSTORE_PARAM_DPI, dpiCombo.getSelectionIndex());
        prefStore.setValue(PREFSTORE_PARAM_LANDSCAPE, landscape());
        prefStore.setValue(PREFSTORE_PARAM_PAGEFORMAT, paperCombo.getSelectionIndex());
    }

    private void loadAndApplyPreferences() {
    	final IPreferenceStore prefStore = ProjectUIPlugin.getDefault().getPreferenceStore();
        final int dpiFromStore = prefStore.getInt(PREFSTORE_PARAM_DPI);
        final int marginTop = prefStore.getInt(PREFSTORE_PARAM_MARGIN_TOP);
        final int marginBottom = prefStore.getInt(PREFSTORE_PARAM_MARGIN_BOTTOM);
        final int marginLeft = prefStore.getInt(PREFSTORE_PARAM_MARGIN_LEFT);
        final int marginRight = prefStore.getInt(PREFSTORE_PARAM_MARGIN_RIGHT);
        final boolean landscapeFromStore = prefStore.getBoolean(PREFSTORE_PARAM_LANDSCAPE);
        final int pageFormatFromStore = prefStore.getInt(PREFSTORE_PARAM_PAGEFORMAT);

        if (dpiFromStore != 0) {
            dpiCombo.select(dpiFromStore);
        }
        if (marginTop != 0) {
            marginTopSpinner.setSelection(marginTop);
        }
        if (marginBottom != 0) {
            marginBottomSpinner.setSelection(marginBottom);
        }
        if (marginLeft != 0) {
            marginLeftSpinner.setSelection(marginLeft);
        }
        if (marginRight != 0) {
            marginRightSpinner.setSelection(marginRight);
        }
        if (landscapeFromStore) {
            landscape.setSelection(true);
        }
        if (pageFormatFromStore != 0) {
            paperCombo.select(pageFormatFromStore);
        }
    }
}
