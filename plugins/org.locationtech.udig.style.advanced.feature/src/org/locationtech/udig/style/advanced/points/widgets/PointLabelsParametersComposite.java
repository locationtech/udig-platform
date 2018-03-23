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
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.geotools.styling.TextSymbolizer;
import org.locationtech.udig.style.advanced.common.IStyleChangesListener.STYLEEVENTTYPE;
import org.locationtech.udig.style.advanced.common.ParameterComposite;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.RuleWrapper;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.TextSymbolizerWrapper;
import org.locationtech.udig.style.advanced.internal.Messages;
import org.locationtech.udig.style.advanced.internal.VendorOptions;
import org.locationtech.udig.style.advanced.utils.Alignments;
import org.locationtech.udig.style.advanced.utils.Utilities;
import org.locationtech.udig.style.sld.SLD;
import org.locationtech.udig.style.sld.editor.FontEditor;
import org.locationtech.udig.ui.ColorEditor;
import org.opengis.filter.expression.Expression;

/**
 * A composite that holds widgets for labels parameter setting.
 *
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class PointLabelsParametersComposite extends ParameterComposite {

    private final Composite parent;
    private final String[] numericAttributesArrays;
    private final String[] allAttributesArrays;

    private Composite mainComposite;
    private Button labelEnableButton;
    private Spinner labelOpacitySpinner;
    private Combo labelOpacityAttributecombo;
    private Button haloColorButton;
    private ColorEditor haloColorEditor;
    private Spinner haloRadiusSpinner;
    private Combo anchorVerticalCombo;
    private Combo anchorHorizontalCombo;
    private Spinner xDisplacementSpinner;
    private Spinner yDisplacementSpinner;
    private Spinner rotationSpinner;
    private Combo rotationAttributecombo;
    private Text maxDisplacementText;
    private Text autoWrapText;
    private Text spaceAroundText;
    private FontEditor fontEditor;
    private Button fontButton;
    private ColorEditor fontColorEditor;
    private Button fontColorButton;
    private Combo labelNameAttributecombo;
    private Text labelNameText;

    public PointLabelsParametersComposite( Composite parent, String[] numericAttributesArrays, String[] allAttributesArrays ) {
        this.parent = parent;
        this.numericAttributesArrays = numericAttributesArrays;
        this.allAttributesArrays = allAttributesArrays;
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
        TextSymbolizerWrapper textSymbolizerWrapper = ruleWrapper.getTextSymbolizersWrapper();
        boolean widgetEnabled = true;
        if (textSymbolizerWrapper == null) {
            widgetEnabled = false;
            /*
             * create a dummy local one to create the widgets
             */
            TextSymbolizer newSymbolizer = Utilities.createDefaultTextSymbolizer(SLD.POINT);
            textSymbolizerWrapper = new TextSymbolizerWrapper(newSymbolizer, null, SLD.POINT);
        }

        mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        mainComposite.setLayout(new GridLayout(3, true));

        labelEnableButton = new Button(mainComposite, SWT.CHECK);
        GridData labelEnableButtonGD = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        labelEnableButtonGD.horizontalSpan = 3;
        labelEnableButton.setLayoutData(labelEnableButtonGD);
        labelEnableButton.setText(Messages.PointLabelsParametersComposite_0);
        labelEnableButton.setSelection(widgetEnabled);
        labelEnableButton.addSelectionListener(this);

        // header
        new Label(mainComposite, SWT.NONE);
        Label valueLabel = new Label(mainComposite, SWT.NONE);
        valueLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        valueLabel.setText(Messages.PointLabelsParametersComposite_1);
        Label fieldsLabel = new Label(mainComposite, SWT.NONE);
        fieldsLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        fieldsLabel.setText(Messages.PointLabelsParametersComposite_2);

        // label name
        Label labelNameLabel = new Label(mainComposite, SWT.NONE);
        labelNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        labelNameLabel.setText(Messages.PointLabelsParametersComposite_3);

        labelNameText = new Text(mainComposite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        GridData labelNameTextGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        labelNameText.setLayoutData(labelNameTextGD);
        labelNameText.addFocusListener(this);
        labelNameText.setToolTipText(Messages.LabelNameField_TooltipText);
        labelNameAttributecombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData labelNameAttributecomboGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        labelNameAttributecombo.setLayoutData(labelNameAttributecomboGD);
        labelNameAttributecombo.setItems(allAttributesArrays);
        labelNameAttributecombo.addSelectionListener(this);
        labelNameAttributecombo.select(0);
        String labelName = textSymbolizerWrapper.getLabelName();
        if (labelName != null) {
            int index = getAttributeIndex(labelName, allAttributesArrays);
            if (index != -1) {
                labelNameAttributecombo.select(index);
            } else {
                labelNameText.setText(labelName);
            }
        } else {
            labelNameText.setText(""); //$NON-NLS-1$
        }

        // label alpha
        Label labelOpactityLabel = new Label(mainComposite, SWT.NONE);
        labelOpactityLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        labelOpactityLabel.setText(Messages.PointLabelsParametersComposite_5);
        labelOpacitySpinner = new Spinner(mainComposite, SWT.BORDER);
        labelOpacitySpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        labelOpacitySpinner.setMaximum(100);
        labelOpacitySpinner.setMinimum(0);
        labelOpacitySpinner.setIncrement(10);
        String opacity = textSymbolizerWrapper.getOpacity();
        Double tmpOpacity = isDouble(opacity);
        int tmp = 100;
        if (tmpOpacity != null) {
            tmp = (int) (tmpOpacity.doubleValue() * 100);
        }
        labelOpacitySpinner.setSelection(tmp);
        labelOpacitySpinner.addSelectionListener(this);
        labelOpacityAttributecombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        labelOpacityAttributecombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        labelOpacityAttributecombo.setItems(numericAttributesArrays);
        labelOpacityAttributecombo.addSelectionListener(this);
        labelOpacityAttributecombo.select(0);
        if (tmpOpacity == null) {
            int index = getAttributeIndex(opacity, numericAttributesArrays);
            if (index != -1) {
                labelOpacityAttributecombo.select(index);
            }
        }

        // rotation
        Label rotationLabel = new Label(mainComposite, SWT.NONE);
        rotationLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        rotationLabel.setText(Messages.PointLabelsParametersComposite_6);
        rotationSpinner = new Spinner(mainComposite, SWT.BORDER);
        rotationSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        rotationSpinner.setMaximum(360);
        rotationSpinner.setMinimum(-360);
        rotationSpinner.setIncrement(45);
        String rotationStr = textSymbolizerWrapper.getRotation();
        Double tmpRotation = isDouble(rotationStr);
        tmp = 0;
        if (tmpRotation != null) {
            tmp = tmpRotation.intValue();
        }
        rotationSpinner.setSelection(tmp);
        rotationSpinner.addSelectionListener(this);
        rotationAttributecombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        rotationAttributecombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        rotationAttributecombo.setItems(numericAttributesArrays);
        rotationAttributecombo.addSelectionListener(this);
        rotationAttributecombo.select(0);
        if (tmpRotation == null) {
            int index = getAttributeIndex(rotationStr, numericAttributesArrays);
            if (index != -1) {
                rotationAttributecombo.select(index);
            }
        }

        // font
        Label fontLabel = new Label(mainComposite, SWT.NONE);
        fontLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        fontLabel.setText(Messages.PointLabelsParametersComposite_7);

        fontEditor = new FontEditor(mainComposite);
        GridData fontButtonGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        fontButtonGD.horizontalSpan = 2;
        fontButton = fontEditor.getButton();
        fontButton.setLayoutData(fontButtonGD);
        fontEditor.setListener(this);

        FontData[] fontData = textSymbolizerWrapper.getFontData();
        if (fontData != null) {
            fontEditor.setFontList(fontData);
        }

        // font color
        Label fontColorLabel = new Label(mainComposite, SWT.NONE);
        fontColorLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        fontColorLabel.setText(Messages.PointLabelsParametersComposite_8);

        fontColorEditor = new ColorEditor(mainComposite);
        fontColorButton = fontColorEditor.getButton();
        fontColorButton.addSelectionListener(this);
        GridData fontColorButtonGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        fontColorButtonGD.horizontalSpan = 2;
        fontColorButton.setLayoutData(fontColorButtonGD);
        Color tmpColor = null;;
        try {
            tmpColor = Color.decode(textSymbolizerWrapper.getColor());
        } catch (Exception e) {
            tmpColor = Color.black;
        }
        fontColorEditor.setColor(tmpColor);

        // label halo
        Label haloLabel = new Label(mainComposite, SWT.NONE);
        haloLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        haloLabel.setText(Messages.PointLabelsParametersComposite_9);

        haloColorEditor = new ColorEditor(mainComposite);
        haloColorButton = haloColorEditor.getButton();
        haloColorButton.addSelectionListener(this);
        GridData haloColorButtonGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        haloColorButton.setLayoutData(haloColorButtonGD);
        tmpColor = null;;
        try {
            tmpColor = Color.decode(textSymbolizerWrapper.getHaloColor());
        } catch (Exception e) {
            tmpColor = Color.black;
        }
        haloColorEditor.setColor(tmpColor);

        haloRadiusSpinner = new Spinner(mainComposite, SWT.BORDER);
        haloRadiusSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        haloRadiusSpinner.setMaximum(20);
        haloRadiusSpinner.setMinimum(0);
        haloRadiusSpinner.setIncrement(1);
        String haloRadius = textSymbolizerWrapper.getHaloRadius();
        Double tmpRadius = isDouble(haloRadius);
        tmp = 0;
        if (tmpRadius != null) {
            tmp = tmpRadius.intValue();
        }
        haloRadiusSpinner.setSelection(tmp);
        haloRadiusSpinner.addSelectionListener(this);

        // anchor
        Label anchorLabel = new Label(mainComposite, SWT.NONE);
        anchorLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        anchorLabel.setText(Messages.PointLabelsParametersComposite_10);

        anchorVerticalCombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        anchorVerticalCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        anchorVerticalCombo.setItems(Alignments.toVerticalStrings());
        anchorVerticalCombo.addSelectionListener(this);
        String anchorY = textSymbolizerWrapper.getAnchorY();
        Alignments vertAlign = Alignments.verticalAlignmentfromDouble(anchorY);
        anchorVerticalCombo.select(vertAlign.toIndex());

        anchorHorizontalCombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        anchorHorizontalCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        anchorHorizontalCombo.setItems(Alignments.toHorizontalStrings());
        anchorHorizontalCombo.addSelectionListener(this);
        String anchorX = textSymbolizerWrapper.getAnchorX();
        Alignments horAlign = Alignments.horizontalAlignmentfromDouble(anchorX);
        anchorHorizontalCombo.select(horAlign.toIndex());

        // displacement
        Label displacementLabel = new Label(mainComposite, SWT.NONE);
        displacementLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        displacementLabel.setText(Messages.PointLabelsParametersComposite_11);

        String displacementX = textSymbolizerWrapper.getDisplacementX();
        String displacementY = textSymbolizerWrapper.getDisplacementY();
        Double tmpXdisplacement = Utilities.isNumber(displacementX, Double.class);
        Double tmpYdisplacement = Utilities.isNumber(displacementY, Double.class);
        if (tmpXdisplacement == null || tmpYdisplacement == null) {
            tmpXdisplacement = 0.0;
            tmpYdisplacement = 0.0;
        }
        xDisplacementSpinner = new Spinner(mainComposite, SWT.BORDER);
        xDisplacementSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        xDisplacementSpinner.setMaximum(Utilities.DISPLACEMENT_MAX);
        xDisplacementSpinner.setMinimum(Utilities.DISPLACEMENT_MIN);
        xDisplacementSpinner.setIncrement(Utilities.DISPLACEMENT_STEP);
        xDisplacementSpinner.setSelection((int) (10 * tmpXdisplacement));
        xDisplacementSpinner.setDigits(1);
        xDisplacementSpinner.addSelectionListener(this);

        yDisplacementSpinner = new Spinner(mainComposite, SWT.BORDER);
        yDisplacementSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        yDisplacementSpinner.setMaximum(Utilities.DISPLACEMENT_MAX);
        yDisplacementSpinner.setMinimum(Utilities.DISPLACEMENT_MIN);
        yDisplacementSpinner.setIncrement(Utilities.DISPLACEMENT_STEP);
        yDisplacementSpinner.setSelection((int) (10 * tmpYdisplacement));
        yDisplacementSpinner.setDigits(1);
        yDisplacementSpinner.addSelectionListener(this);

        // vendor options
        Group vendorOptionsGroup = new Group(mainComposite, SWT.SHADOW_ETCHED_IN);
        GridData vendorOptionsGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        vendorOptionsGD.horizontalSpan = 3;
        vendorOptionsGroup.setLayoutData(vendorOptionsGD);
        vendorOptionsGroup.setLayout(new GridLayout(2, false));
        vendorOptionsGroup.setText(Messages.PointLabelsParametersComposite_12);

        // max displacement
        Label maxDisplacementLabel = new Label(vendorOptionsGroup, SWT.NONE);
        maxDisplacementLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        maxDisplacementLabel.setText(VendorOptions.VENDOROPTION_MAXDISPLACEMENT.toGuiString());
        maxDisplacementText = new Text(vendorOptionsGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        GridData maxDisplacementTextGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        maxDisplacementText.setLayoutData(maxDisplacementTextGD);
        maxDisplacementText.addFocusListener(this);
        String maxDisplacementVO = textSymbolizerWrapper.getMaxDisplacementVO();
        if (maxDisplacementVO != null) {
            maxDisplacementText.setText(maxDisplacementVO);
        } else {
            maxDisplacementText.setText(""); //$NON-NLS-1$
        }

        // autoWrap
        Label autoWrapLabel = new Label(vendorOptionsGroup, SWT.NONE);
        autoWrapLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        autoWrapLabel.setText(VendorOptions.VENDOROPTION_AUTOWRAP.toGuiString());
        autoWrapText = new Text(vendorOptionsGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        GridData autoWrapTextGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        autoWrapText.setLayoutData(autoWrapTextGD);
        autoWrapText.addFocusListener(this);
        String autoWrapVO = textSymbolizerWrapper.getAutoWrapVO();
        if (autoWrapVO != null) {
            autoWrapText.setText(autoWrapVO);
        } else {
            autoWrapText.setText(""); //$NON-NLS-1$
        }

        // spaceAround
        Label spaceAroundLabel = new Label(vendorOptionsGroup, SWT.NONE);
        spaceAroundLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        spaceAroundLabel.setText(VendorOptions.VENDOROPTION_SPACEAROUND.toGuiString());
        spaceAroundText = new Text(vendorOptionsGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        GridData spaceAroundTextGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        spaceAroundText.setLayoutData(spaceAroundTextGD);
        spaceAroundText.addFocusListener(this);
        String spaceAroundVO = textSymbolizerWrapper.getSpaceAroundVO();
        if (spaceAroundVO != null) {
            spaceAroundText.setText(spaceAroundVO);
        } else {
            spaceAroundText.setText(""); //$NON-NLS-1$
        }

        checkEnablements();
    }

    /**
     * Initialize the composite with values from a rule.
     * 
     * @param ruleWrapper the rule to take the info from.
     */
    public void update( RuleWrapper ruleWrapper ) {
        TextSymbolizerWrapper textSymbolizerWrapper = ruleWrapper.getTextSymbolizersWrapper();
        if (textSymbolizerWrapper == null) {
            labelEnableButton.setSelection(false);
            checkEnablements();
            return;
        } else {
            labelEnableButton.setSelection(true);
        }

        String labelName = textSymbolizerWrapper.getLabelName();
        if (labelName != null) {
            int index = getAttributeIndex(labelName, allAttributesArrays);
            if (index != -1) {
                labelNameAttributecombo.select(index);
            } else {
                labelNameText.setText(labelName);
                labelNameAttributecombo.select(0);
            }
        } else {
            labelNameText.setText(""); //$NON-NLS-1$
        }

        FontData[] fontData = textSymbolizerWrapper.getFontData();
        if (fontData != null) {
            fontEditor.setFontList(fontData);
        }

        String color = textSymbolizerWrapper.getColor();
        if (color != null) {
            fontColorEditor.setColor(Color.decode(color));
        }

        String opacity = textSymbolizerWrapper.getOpacity();
        Double tmpOpacity = isDouble(opacity);
        int tmp = 100;
        if (tmpOpacity != null) {
            tmp = (int) (tmpOpacity.doubleValue() * 100);
        }
        labelOpacitySpinner.setSelection(tmp);
        if (tmpOpacity == null) {
            int index = getAttributeIndex(opacity, numericAttributesArrays);
            if (index != -1) {
                labelOpacityAttributecombo.select(index);
            }
        }

        Color tmpColor = null;;
        try {
            tmpColor = Color.decode(textSymbolizerWrapper.getHaloColor());
        } catch (Exception e) {
            tmpColor = Color.black;
        }
        haloColorEditor.setColor(tmpColor);

        String haloRadius = textSymbolizerWrapper.getHaloRadius();
        Double tmpRadius = isDouble(haloRadius);
        tmp = 0;
        if (tmpRadius != null) {
            tmp = tmpRadius.intValue();
        }
        haloRadiusSpinner.setSelection(tmp);

        // anchor
        String anchorY = textSymbolizerWrapper.getAnchorY();
        Alignments vertAlign = Alignments.verticalAlignmentfromDouble(anchorY);
        anchorVerticalCombo.select(vertAlign.toIndex());

        String anchorX = textSymbolizerWrapper.getAnchorX();
        Alignments horAlign = Alignments.horizontalAlignmentfromDouble(anchorX);
        anchorHorizontalCombo.select(horAlign.toIndex());

        // displacement
        String displacementX = textSymbolizerWrapper.getDisplacementX();
        String displacementY = textSymbolizerWrapper.getDisplacementY();
        Double tmpXdisplacement = Utilities.isNumber(displacementX, Double.class);
        Double tmpYdisplacement = Utilities.isNumber(displacementY, Double.class);
        if (tmpXdisplacement == null || tmpYdisplacement == null) {
            tmpXdisplacement = 0.0;
            tmpYdisplacement = 0.0;
        }
        xDisplacementSpinner.setSelection((int) (10 * tmpXdisplacement));
        yDisplacementSpinner.setSelection((int) (10 * tmpYdisplacement));

        // rotation
        String rotationStr = textSymbolizerWrapper.getRotation();
        Double tmpRotation = isDouble(rotationStr);
        tmp = 0;
        if (tmpRotation != null) {
            tmp = tmpRotation.intValue();
        }
        rotationSpinner.setSelection(tmp);
        if (tmpRotation == null) {
            int index = getAttributeIndex(rotationStr, numericAttributesArrays);
            if (index != -1) {
                rotationAttributecombo.select(index);
            }
        }

        // max displacement
        String maxDisplacementVO = textSymbolizerWrapper.getMaxDisplacementVO();
        if (maxDisplacementVO != null) {
            maxDisplacementText.setText(maxDisplacementVO);
        } else {
            maxDisplacementText.setText(""); //$NON-NLS-1$
        }

        // autoWrap
        String autoWrapVO = textSymbolizerWrapper.getAutoWrapVO();
        if (autoWrapVO != null) {
            autoWrapText.setText(autoWrapVO);
        } else {
            autoWrapText.setText(""); //$NON-NLS-1$
        }

        // spaceAround
        String spaceAroundVO = textSymbolizerWrapper.getSpaceAroundVO();
        if (spaceAroundVO != null) {
            spaceAroundText.setText(spaceAroundVO);
        } else {
            spaceAroundText.setText(""); //$NON-NLS-1$
        }

        checkEnablements();
    }

    private void checkEnablements() {
        boolean comboIsNone = comboIsNone(labelNameAttributecombo);
        boolean selected = labelEnableButton.getSelection();
        if (!selected) {
            setEnabled(false);
        } else {
        	setEnabled(true);
            labelNameText.setEnabled(comboIsNone);
            comboIsNone = comboIsNone(rotationAttributecombo);
            rotationSpinner.setEnabled(comboIsNone);
            comboIsNone = comboIsNone(labelOpacityAttributecombo);
            labelOpacitySpinner.setEnabled(comboIsNone);
        }
    }

    private void setEnabled( boolean enable ) {
        labelOpacitySpinner.setEnabled(enable);
        labelOpacityAttributecombo.setEnabled(enable);
        haloColorButton.setEnabled(enable);
        haloColorEditor.setEnabled(enable);
        haloRadiusSpinner.setEnabled(enable);
        anchorVerticalCombo.setEnabled(enable);
        anchorHorizontalCombo.setEnabled(enable);
        xDisplacementSpinner.setEnabled(enable);
        yDisplacementSpinner.setEnabled(enable);
        rotationSpinner.setEnabled(enable);
        rotationAttributecombo.setEnabled(enable);
        maxDisplacementText.setEnabled(enable);
        autoWrapText.setEnabled(enable);
        spaceAroundText.setEnabled(enable);
        fontEditor.setEnabled(enable);
        fontButton.setEnabled(enable);
        fontColorEditor.setEnabled(enable);
        fontColorButton.setEnabled(enable);
        labelNameAttributecombo.setEnabled(enable);
        labelNameText.setEnabled(enable);
    }

    public void widgetSelected( SelectionEvent e ) {
        Object source = e.getSource();
        if (source.equals(labelEnableButton)) {
            boolean selected = labelEnableButton.getSelection();
            setEnabled(selected);
            notifyListeners(String.valueOf(selected), false, STYLEEVENTTYPE.LABELENABLE);
        } else if (source.equals(labelNameAttributecombo)) {
            int index = labelNameAttributecombo.getSelectionIndex();
            String nameField = labelNameAttributecombo.getItem(index);
            notifyListeners(nameField, true, STYLEEVENTTYPE.LABEL);
        } else if (source.equals(fontButton)) {
            FontData[] fontData = fontEditor.getFontList();
            if (fontData.length > 0) {
                FontData fd = fontData[0];
                String name = fd.getName();
                String style = String.valueOf(fd.getStyle());
                String height = String.valueOf(fd.getHeight());
                Color color = fontEditor.getAWTColor();
                Expression colorExpr = ff.literal(color);
                String fontColor = colorExpr.evaluate(null, String.class);

                notifyListeners(new String[]{name, style, height, fontColor}, false, STYLEEVENTTYPE.LABELFONT);
                fontColorEditor.setColor(color);
                notifyListeners(fontColor, false, STYLEEVENTTYPE.LABELCOLOR);
            }
        } else if (source.equals(fontColorButton)) {
            Color color = fontColorEditor.getColor();
            Expression colorExpr = ff.literal(color);
            String fontColor = colorExpr.evaluate(null, String.class);
            fontEditor.setColorValue(color);
            notifyListeners(fontColor, false, STYLEEVENTTYPE.LABELCOLOR);
        } else if (source.equals(haloColorButton)) {
            Color color = haloColorEditor.getColor();
            Expression colorExpr = ff.literal(color);
            String haloColor = colorExpr.evaluate(null, String.class);
            notifyListeners(haloColor, false, STYLEEVENTTYPE.LABELHALOCOLOR);
            
          //keeps the halo radius the same;  
            int radius = haloRadiusSpinner.getSelection();
            if (radius == 0){
            	//if we change the color chances are we want at least some halo
            	//this also keeps the radius insync with the style symbolizer;
            	radius = 1;
            	haloRadiusSpinner.setSelection(radius);
            }
            notifyListeners(String.valueOf(radius), false, STYLEEVENTTYPE.LABELHALORADIUS);
        } else if (source.equals(haloRadiusSpinner)) {
            int radius = haloRadiusSpinner.getSelection();

            notifyListeners(String.valueOf(radius), false, STYLEEVENTTYPE.LABELHALORADIUS);
        } else if (source.equals(anchorHorizontalCombo) || source.equals(anchorVerticalCombo)) {
            int s1 = anchorVerticalCombo.getSelectionIndex();
            String vItem = anchorVerticalCombo.getItem(s1);
            int s2 = anchorHorizontalCombo.getSelectionIndex();
            String hItem = anchorHorizontalCombo.getItem(s2);

            Alignments vAlignment = Alignments.toAlignment(vItem);
            Alignments hAlignment = Alignments.toAlignment(hItem);
            String h = String.valueOf(hAlignment.toDouble());
            String v = String.valueOf(vAlignment.toDouble());
            notifyListeners(new String[]{h, v}, false, STYLEEVENTTYPE.LABELANCHOR);
        } else if (source.equals(rotationSpinner)) {
            int rotation = rotationSpinner.getSelection();
            String rotationStr = String.valueOf(rotation);

            notifyListeners(rotationStr, false, STYLEEVENTTYPE.LABELROTATION);
        } else if (source.equals(rotationAttributecombo)) {
            int index = rotationAttributecombo.getSelectionIndex();
            String rotationField = rotationAttributecombo.getItem(index);

            notifyListeners(rotationField, true, STYLEEVENTTYPE.LABELROTATION);
        } else if (source.equals(labelOpacitySpinner)) {
            int opacity = labelOpacitySpinner.getSelection();
            String opacityStr = String.valueOf(opacity);

            notifyListeners(opacityStr, false, STYLEEVENTTYPE.LABELOPACITY);
        } else if (source.equals(labelOpacityAttributecombo)) {
            int index = labelOpacityAttributecombo.getSelectionIndex();
            String opacityField = labelOpacityAttributecombo.getItem(index);

            notifyListeners(opacityField, true, STYLEEVENTTYPE.LABELOPACITY);
        } else if (source.equals(xDisplacementSpinner) || source.equals(yDisplacementSpinner)) {
            double x = Utilities.getDoubleSpinnerSelection(xDisplacementSpinner);
            double y = Utilities.getDoubleSpinnerSelection(yDisplacementSpinner);

            String displacementStr = x + "," + y; //$NON-NLS-1$
            notifyListeners(displacementStr, false, STYLEEVENTTYPE.LABELDISPLACEMENT);
        }

        checkEnablements();
    }

    public void focusGained( FocusEvent e ) {
    }

    public void focusLost( FocusEvent e ) {
        Object source = e.getSource();
        if (source.equals(maxDisplacementText)) {
            String text = maxDisplacementText.getText();
            notifyListeners(text, false, STYLEEVENTTYPE.LABELMAXDISPLACEMENT_VO);
        } else if (source.equals(spaceAroundText)) {
            String text = spaceAroundText.getText();
            notifyListeners(text, false, STYLEEVENTTYPE.LABELSPACEAROUND_VO);
        } else if (source.equals(autoWrapText)) {
            String text = autoWrapText.getText();
            notifyListeners(text, false, STYLEEVENTTYPE.LABELAUTOWRAP_VO);
        } else if (source.equals(labelNameText)) {
            String text = labelNameText.getText();
            notifyListeners(text, false, STYLEEVENTTYPE.LABEL);
        }
        checkEnablements();
    }
}
