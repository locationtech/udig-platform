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

import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ui.internal.Messages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

/**
 * A strategy for exporting to PDF
 *
 * @author jesse
 * @since 1.1.0
 */
public class PDFImageExportFormat extends ImageExportFormat {

    private Combo dpiCombo;
    private Spinner topMarginSpinner;
    private Spinner lowerMarginSpinner;
    private Spinner leftMarginSpinner;
    private Spinner rightMarginSpinner;
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
        Image2Pdf.write(image, destination.getAbsolutePath(), paper(),
                this.leftMarginSpinner.getSelection(),
                this.topMarginSpinner.getSelection(), landscape(), getDPI());
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
        topMarginSpinner = new Spinner(marginsGroup, SWT.NONE);
        topMarginSpinner.setSelection(10);
        Label lowerLabel = new Label(marginsGroup, SWT.NONE);
        lowerLabel.setText(Messages.ImageExportPage_lowerMargin);
        lowerMarginSpinner = new Spinner(marginsGroup, SWT.NONE);
        lowerMarginSpinner.setSelection(10);
        Label leftLabel = new Label(marginsGroup, SWT.NONE);
        leftLabel.setText(Messages.ImageExportPage_leftMargin);
        leftMarginSpinner = new Spinner(marginsGroup, SWT.NONE);
        leftMarginSpinner.setSelection(10);
        Label rightLabel = new Label(marginsGroup, SWT.NONE);
        rightLabel.setText(Messages.ImageExportPage_rightMargin);
        rightMarginSpinner = new Spinner(marginsGroup, SWT.NONE);
        rightMarginSpinner.setSelection(10);

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
        int paperHeight = paper().getPixelHeight(landscape(), getDPI());
        int topMargin = topMarginSpinner.getSelection();
        int lowerMargin = lowerMarginSpinner.getSelection();

        return paperHeight - topMargin - lowerMargin;
    }

    @Override
    public int getWidth( double mapwidth, double mapheight ) {
        int paperWidth = paper().getPixelWidth(landscape(), getDPI());
        int rightMargin = rightMarginSpinner.getSelection();
        int leftMargin = leftMarginSpinner.getSelection();

        return paperWidth - rightMargin - leftMargin;
    }

}
