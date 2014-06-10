/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.advanced.points.widgets;

import static org.locationtech.udig.style.advanced.utils.Utilities.ff;

import java.awt.Color;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.locationtech.udig.style.advanced.common.IStyleChangesListener.STYLEEVENTTYPE;
import org.locationtech.udig.style.advanced.common.ParameterComposite;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.PointSymbolizerWrapper;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.RuleWrapper;
import org.locationtech.udig.style.advanced.internal.Messages;
import org.locationtech.udig.ui.ColorEditor;
import org.opengis.filter.expression.Expression;

/**
 * A composite that holds widgets for fill parameter setting.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 */
public class PointFillParametersComposite extends ParameterComposite {

    private final Composite parent;
    private final String[] numericAttributesArrays;

    private Composite mainComposite;
    private Button fillEnableButton;
    private ColorEditor fillColorEditor;
    private Button fillColorButton;
    private Spinner fillOpacitySpinner;
    private Combo fillOpacityAttributecombo;

    public PointFillParametersComposite( Composite parent, String[] numericAttributesArrays ) {
        this.parent = parent;
        this.numericAttributesArrays = numericAttributesArrays;
    }

    public Composite getComposite() {
        return mainComposite;
    }

    /**
     * Initialize the composite with values from a rule.
     * 
     * @param ruleWrapper the rule to take the info from.
     */
    public void init( RuleWrapper ruleWrapper ) {
        PointSymbolizerWrapper pointSymbolizerWrapper = ruleWrapper.getGeometrySymbolizersWrapper().adapt(
                PointSymbolizerWrapper.class);

        mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        mainComposite.setLayout(new GridLayout(3, true));

        boolean widgetEnabled = pointSymbolizerWrapper.hasFill();

        fillEnableButton = new Button(mainComposite, SWT.CHECK);
        GridData fillEnableButtonGD = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        fillEnableButtonGD.horizontalSpan = 3;
        fillEnableButton.setLayoutData(fillEnableButtonGD);
        fillEnableButton.setText(Messages.PointFillParametersComposite_0);
        fillEnableButton.setSelection(widgetEnabled);
        fillEnableButton.addSelectionListener(this);

        // header
        new Label(mainComposite, SWT.NONE);
        Label valueLabel = new Label(mainComposite, SWT.NONE);
        valueLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        valueLabel.setText(Messages.PointFillParametersComposite_1);
        Label fieldsLabel = new Label(mainComposite, SWT.NONE);
        fieldsLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        fieldsLabel.setText(Messages.PointFillParametersComposite_2);

        // border alpha
        Label fillOpactityLabel = new Label(mainComposite, SWT.NONE);
        fillOpactityLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        fillOpactityLabel.setText(Messages.PointFillParametersComposite_3);
        fillOpacitySpinner = new Spinner(mainComposite, SWT.BORDER);
        fillOpacitySpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        fillOpacitySpinner.setMaximum(100);
        fillOpacitySpinner.setMinimum(0);
        fillOpacitySpinner.setIncrement(10);
        String opacity = pointSymbolizerWrapper.getFillOpacity();
        Double tmpOpacity = isDouble(opacity);
        int tmp = 100;
        if (tmpOpacity != null) {
            tmp = (int) (tmpOpacity.doubleValue() * 100);
        }
        fillOpacitySpinner.setSelection(tmp);
        fillOpacitySpinner.addSelectionListener(this);
        fillOpacityAttributecombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        fillOpacityAttributecombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        fillOpacityAttributecombo.setItems(numericAttributesArrays);
        fillOpacityAttributecombo.addSelectionListener(this);
        fillOpacityAttributecombo.select(0);
        if (tmpOpacity == null) {
            int index = getAttributeIndex(opacity, numericAttributesArrays);
            if (index != -1) {
                fillOpacityAttributecombo.select(index);
            }
        }

        // fill color
        Label fillColorLabel = new Label(mainComposite, SWT.NONE);
        fillColorLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        fillColorLabel.setText(Messages.PointFillParametersComposite_4);
        Color tmpColor = null;;
        try {
            tmpColor = Color.decode(pointSymbolizerWrapper.getFillColor());
        } catch (Exception e) {
            tmpColor = Color.gray;
        }
        fillColorEditor = new ColorEditor(mainComposite);
        fillColorEditor.setColor(tmpColor);
        fillColorButton = fillColorEditor.getButton();
        fillColorButton.addSelectionListener(this);
        GridData fillColorButtonGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        fillColorButtonGD.horizontalSpan = 2;
        fillColorButton.setLayoutData(fillColorButtonGD);

        checkEnablements();
    }

    /**
     * Initialize the composite with values from a rule.
     * 
     * @param ruleWrapper the rule to take the info from.
     */
    public void update( RuleWrapper ruleWrapper ) {
        PointSymbolizerWrapper pointSymbolizerWrapper = ruleWrapper.getGeometrySymbolizersWrapper().adapt(
                PointSymbolizerWrapper.class);

        boolean widgetEnabled = pointSymbolizerWrapper.hasFill();
        fillEnableButton.setSelection(widgetEnabled);

        Color tmpColor = null;
        try {
            tmpColor = Color.decode(pointSymbolizerWrapper.getFillColor());
        } catch (Exception e) {
            tmpColor = Color.gray;
        }
        fillColorEditor.setColor(tmpColor);

        // fill alpha
        String opacity = pointSymbolizerWrapper.getFillOpacity();
        Double tmpOpacity = isDouble(opacity);
        int tmp = 100;
        if (tmpOpacity != null) {
            tmp = (int) (tmpOpacity.doubleValue() * 100);
        }
        fillOpacitySpinner.setSelection(tmp);
        if (tmpOpacity == null) {
            int index = getAttributeIndex(opacity, numericAttributesArrays);
            if (index != -1) {
                fillOpacityAttributecombo.select(index);
            }
        }

        checkEnablements();
    }

    private void checkEnablements() {
    	for (Control kid : mainComposite.getChildren()){
    		if (kid != fillEnableButton){
    			kid.setEnabled(fillEnableButton.getSelection());
    		}
    	}
    	if (!fillEnableButton.getSelection()){
    		return;
    	}
    	
        boolean comboIsNone = comboIsNone(fillOpacityAttributecombo);
        fillOpacitySpinner.setEnabled(comboIsNone);
    }

    public void widgetSelected( SelectionEvent e ) {
        Object source = e.getSource();
        if (source.equals(fillEnableButton)) {
            boolean selected = fillEnableButton.getSelection();
            notifyListeners(String.valueOf(selected), false, STYLEEVENTTYPE.FILLENABLE);
        } else if (source.equals(fillColorButton)) {
            Color color = fillColorEditor.getColor();
            Expression colorExpr = ff.literal(color);
            String fillColor = colorExpr.evaluate(null, String.class);
            notifyListeners(fillColor, false, STYLEEVENTTYPE.FILLCOLOR);
        } else if (source.equals(fillOpacitySpinner) || source.equals(fillOpacityAttributecombo)) {
            boolean comboIsNone = comboIsNone(fillOpacityAttributecombo);
            if (comboIsNone) {
                int opacity = fillOpacitySpinner.getSelection();
                float opacityNorm = opacity / 100f;
                String fillOpacity = String.valueOf(opacityNorm);
                notifyListeners(fillOpacity, false, STYLEEVENTTYPE.FILLOPACITY);
            } else {
                int index = fillOpacityAttributecombo.getSelectionIndex();
                String field = fillOpacityAttributecombo.getItem(index);
                if (field.length() == 0) {
                    return;
                }
                notifyListeners(field, true, STYLEEVENTTYPE.FILLOPACITY);
            }
        }
        checkEnablements();
    }

}
