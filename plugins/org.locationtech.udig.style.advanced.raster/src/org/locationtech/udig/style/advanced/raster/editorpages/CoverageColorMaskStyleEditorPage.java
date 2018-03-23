/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.advanced.raster.editorpages;

import java.awt.Color;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.geotools.coverage.grid.GridCoverage2D;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.internal.StyleBlackboard;
import org.locationtech.udig.style.advanced.raster.internal.Messages;
import org.locationtech.udig.style.sld.editor.StyleEditorPage;
import org.locationtech.udig.ui.ColorEditor;

/**
 * The style editor for single banded {@link GridCoverage2D coverages};
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class CoverageColorMaskStyleEditorPage extends StyleEditorPage implements SelectionListener {

    public static String COVERAGE_COLORMASK_ID = "raster-color-mask"; //$NON-NLS-1$

    private Button colorMaskButton;
    private ColorEditor maskColorEditor;

    public CoverageColorMaskStyleEditorPage() {
        super();
        setSize(new Point(500, 450));
    }

    public void createPageContent( Composite parent ) {
        IBlackboard styleBlackboard = getSelectedLayer().getStyleBlackboard();
        String maskColorString = styleBlackboard.getString(COVERAGE_COLORMASK_ID);

        Group colorMaskGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
        colorMaskGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        colorMaskGroup.setLayout(new GridLayout(2, false));
        colorMaskGroup.setText(Messages.CoverageColorMaskStyleEditorPage_0);

        colorMaskButton = new Button(colorMaskGroup, SWT.CHECK);
        colorMaskButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        colorMaskButton.setText(Messages.CoverageColorMaskStyleEditorPage_1);
        colorMaskButton.addSelectionListener(this);

        maskColorEditor = new ColorEditor(colorMaskGroup);
        if (maskColorString != null) {
            String[] colorSplit = maskColorString.split(":"); //$NON-NLS-1$
            Color color = new Color(Integer.parseInt(colorSplit[0]), Integer.parseInt(colorSplit[1]),
                    Integer.parseInt(colorSplit[2]));
            maskColorEditor.setColor(color);
            colorMaskButton.setSelection(true);
        }else{
        	colorMaskButton.setSelection(false);
        	maskColorEditor.setEnabled(false);
        }
    }

    public void widgetSelected( SelectionEvent e ) {
        Object source = e.getSource();
        if (source.equals(colorMaskButton)) {
            maskColorEditor.setEnabled(colorMaskButton.getSelection());
        }
    }

    public void widgetDefaultSelected( SelectionEvent e ) {
    }

    public String getErrorMessage() {
        return null;
    }

    public String getLabel() {
        return null;
    }

    public void gotFocus() {
    }

    public boolean performCancel() {
        return false;
    }

    public boolean okToLeave() {
        return true;
    }

    public boolean performApply() {
        return applyCurrentStyle();
    }

    public boolean performOk() {
        return applyCurrentStyle();
    }

    private boolean applyCurrentStyle() {

        StyleBlackboard styleBlackboard = getSelectedLayer().getStyleBlackboard();

        if (colorMaskButton.getSelection()) {
            Color maskColor = maskColorEditor.getColor();
            String colorStr = maskColor.getRed() + ":" + maskColor.getGreen() + ":" + maskColor.getBlue(); //$NON-NLS-1$ //$NON-NLS-2$
            styleBlackboard.putString(COVERAGE_COLORMASK_ID, colorStr);
        } else {
            styleBlackboard.remove(COVERAGE_COLORMASK_ID);
        }
        return true;
    }

    public void refresh() {
    }

    public void dispose() {
        super.dispose();
    }

    public void styleChanged( Object source ) {

    }

}
