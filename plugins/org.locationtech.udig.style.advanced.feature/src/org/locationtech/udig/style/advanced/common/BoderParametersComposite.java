/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.advanced.common;


import static org.locationtech.udig.style.advanced.utils.Utilities.ff;

import java.awt.Color;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.locationtech.udig.style.advanced.common.IStyleChangesListener.STYLEEVENTTYPE;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.LineSymbolizerWrapper;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.PointSymbolizerWrapper;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.RuleWrapper;
import org.locationtech.udig.style.advanced.internal.Messages;
import org.locationtech.udig.style.advanced.utils.Utilities;
import org.locationtech.udig.ui.ColorEditor;
import org.opengis.filter.expression.Expression;

/**
 * A composite that holds widgets for polygon border parameter setting.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 */
public class BoderParametersComposite extends ParameterComposite implements ModifyListener {

    private final Composite parent;
    private final String[] numericAttributesArrays;

    private Composite mainComposite;
    private Button borderEnableButton;
    private Spinner borderWidthSpinner;
    private Combo borderWidthAttributecombo;
    private ColorEditor borderColorEditor;
    private Spinner borderOpacitySpinner;
    private Combo borderOpacityAttributecombo;
    private Button borderColorButton;
    private Text graphicsPathText;
    private Button graphicsPathButton;

    private Text dashText;
    private Text dashOffsetText;
    private Combo lineJoinCombo;
    private Combo lineCapCombo;
    private final String[] stringAttributesArrays;
    private Combo borderColorAttributecombo;
    
    private Combo endStyleCombo;
    private Spinner endStyleSizeSpinner;
    private ColorEditor endStyleColorEditor;
    private Combo startStyleCombo;
    private Spinner startStyleSizeSpinner;
    private ColorEditor startStyleColorEditor;
    
    private String geometryProperty;
    
    public BoderParametersComposite( Composite parent, String[] numericAttributesArrays, 
    		String[] stringattributesArrays, String geometryProperty ) {
        this.parent = parent;
        this.numericAttributesArrays = numericAttributesArrays;
        this.stringAttributesArrays = stringattributesArrays;
        this.geometryProperty = geometryProperty;
    }

    @Override
    public Composite getComposite() {
        return mainComposite;
    }

    /**
     * Initialize the panel with pre-existing values.
     * 
     * @param ruleWrapper the {@link RuleWrapper}.
     */
    public void init( RuleWrapper ruleWrapper ) {
        LineSymbolizerWrapper lineSymbolizerWrapper = ruleWrapper.getGeometrySymbolizersWrapper().adapt(
                LineSymbolizerWrapper.class);

        mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        mainComposite.setLayout(new GridLayout(3, true));

        boolean widgetEnabled = lineSymbolizerWrapper.hasStroke();

        borderEnableButton = new Button(mainComposite, SWT.CHECK);
        GridData borderEnableButtonGD = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        borderEnableButtonGD.horizontalSpan = 3;
        borderEnableButton.setLayoutData(borderEnableButtonGD);
        borderEnableButton.setText(Messages.BoderParametersComposite_0);
        borderEnableButton.setSelection(widgetEnabled);
        borderEnableButton.addSelectionListener(this);

        // header
        new Label(mainComposite, SWT.NONE);
        Label valueLabel = new Label(mainComposite, SWT.NONE);
        valueLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        valueLabel.setText(Messages.BoderParametersComposite_1);
        Label fieldsLabel = new Label(mainComposite, SWT.NONE);
        fieldsLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        fieldsLabel.setText(Messages.BoderParametersComposite_2);

        // border width
        Label borderWidthLabel = new Label(mainComposite, SWT.NONE);
        borderWidthLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        borderWidthLabel.setText(Messages.BoderParametersComposite_3);
        borderWidthSpinner = new Spinner(mainComposite, SWT.BORDER);
        borderWidthSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        borderWidthSpinner.setMaximum(500);
        borderWidthSpinner.setMinimum(0);
        borderWidthSpinner.setIncrement(10);
        

        String width = lineSymbolizerWrapper.getStrokeWidth();
        Double tmpWidth = isDouble(width);
        int tmp = 3;
        if (tmpWidth != null) {
            tmp = tmpWidth.intValue();
        }
        borderWidthSpinner.setSelection(tmp * 10);
        borderWidthSpinner.setDigits(1);
        borderWidthSpinner.addSelectionListener(this);
        borderWidthAttributecombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        borderWidthAttributecombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        borderWidthAttributecombo.setItems(numericAttributesArrays);
        borderWidthAttributecombo.addSelectionListener(this);
        borderWidthAttributecombo.select(0);
        if (tmpWidth == null) {
            int index = getAttributeIndex(width, numericAttributesArrays);
            if (index != -1) {
                borderWidthAttributecombo.select(index);
            }
        }

        // border alpha
        Label borderOpactityLabel = new Label(mainComposite, SWT.NONE);
        borderOpactityLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        borderOpactityLabel.setText(Messages.BoderParametersComposite_4);
        borderOpacitySpinner = new Spinner(mainComposite, SWT.BORDER);
        borderOpacitySpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        borderOpacitySpinner.setMaximum(100);
        borderOpacitySpinner.setMinimum(0);
        borderOpacitySpinner.setIncrement(10);

        String opacity = lineSymbolizerWrapper.getStrokeOpacity();
        Double tmpOpacity = isDouble(opacity);
        tmp = 100;
        if (tmpOpacity != null) {
            tmp = (int) (tmpOpacity.doubleValue() * 100);
        }
        borderOpacitySpinner.setSelection(tmp);
        borderOpacitySpinner.addSelectionListener(this);
        borderOpacityAttributecombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        borderOpacityAttributecombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        borderOpacityAttributecombo.setItems(numericAttributesArrays);
        borderOpacityAttributecombo.addSelectionListener(this);
        borderOpacityAttributecombo.select(0);
        if (tmpOpacity == null) {
            int index = getAttributeIndex(opacity, numericAttributesArrays);
            if (index != -1) {
                borderOpacityAttributecombo.select(index);
            }
        }

        // border color
        Label borderColorLabel = new Label(mainComposite, SWT.NONE);
        borderColorLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        borderColorLabel.setText(Messages.BoderParametersComposite_5);
        String color = lineSymbolizerWrapper.getStrokeColor();
        Color tmpColor;
        try {
            tmpColor = Color.decode(color);
        } catch (Exception e) {
            tmpColor = Color.gray;
        }
        borderColorEditor = new ColorEditor(mainComposite);
        borderColorEditor.setColor(tmpColor);
        borderColorButton = borderColorEditor.getButton();
        borderColorButton.addSelectionListener(this);
        GridData borderColorButtonSIMPLEGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        borderColorButton.setLayoutData(borderColorButtonSIMPLEGD);

        borderColorAttributecombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        borderColorAttributecombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        borderColorAttributecombo.setItems(stringAttributesArrays);
        borderColorAttributecombo.addSelectionListener(this);
        borderColorAttributecombo.select(0);
        if (tmpColor == null) {
            int index = getAttributeIndex(color, stringAttributesArrays);
            if (index != -1) {
                borderColorAttributecombo.select(index);
            }
        }

        // graphics fill
        Label graphicsFillLabel = new Label(mainComposite, SWT.RADIO);
        graphicsFillLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        graphicsFillLabel.setText(Messages.BoderParametersComposite_6);

        Composite pathComposite = new Composite(mainComposite, SWT.NONE);
        GridData pathCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        pathCompositeGD.horizontalSpan = 2;
        pathComposite.setLayoutData(pathCompositeGD);
        GridLayout pathLayout = new GridLayout(2, false);
        pathLayout.marginWidth = 0;
        pathLayout.marginHeight = 0;
        pathComposite.setLayout(pathLayout);
        graphicsPathText = new Text(pathComposite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        graphicsPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        try {
            graphicsPathText.setText(lineSymbolizerWrapper.getStrokeExternalGraphicStrokePath());
        } catch (MalformedURLException e1) {
            graphicsPathText.setText(""); //$NON-NLS-1$
        }
        graphicsPathText.addModifyListener(this);
        graphicsPathButton = new Button(pathComposite, SWT.PUSH);
        graphicsPathButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        graphicsPathButton.setText("..."); //$NON-NLS-1$
        graphicsPathButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                FileDialog fileDialog = new FileDialog(graphicsPathText.getShell(), SWT.OPEN);
                String path = fileDialog.open();
                if (path == null || path.length() < 1) {
                    graphicsPathText.setText(""); //$NON-NLS-1$
                } else {
                    graphicsPathText.setText(path);
                }
            }
        });

        // line properties
        // dash
        Label dashLabel = new Label(mainComposite, SWT.NONE);
        GridData dashLabelGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        dashLabel.setLayoutData(dashLabelGD);
        dashLabel.setText(Messages.BoderParametersComposite_8);
        dashText = new Text(mainComposite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        GridData dashGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        dashGD.horizontalSpan = 2;
        dashText.setLayoutData(dashGD);

        String dash = lineSymbolizerWrapper.getDash();
        float[] dashArray = Utilities.getDash(dash);
        if (dashArray != null) {
            dashText.setText(dash);
        } else {
            dashText.setText(""); //$NON-NLS-1$
        }
        dashText.addModifyListener(this);
        // dashoffset
        Label dashOffsetLabel = new Label(mainComposite, SWT.NONE);
        GridData dashOffsetLabelGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        dashOffsetLabel.setLayoutData(dashOffsetLabelGD);
        dashOffsetLabel.setText(Messages.BoderParametersComposite_9);
        dashOffsetText = new Text(mainComposite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        GridData dashOffsetGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        dashOffsetGD.horizontalSpan = 2;
        dashOffsetText.setLayoutData(dashOffsetGD);

        String dashOffset = lineSymbolizerWrapper.getDashOffset();
        Double dashOffsetFloat = isDouble(dashOffset);
        if (dashOffsetFloat != null) {
            dashOffsetText.setText(dashOffset);
        } else {
            dashOffsetText.setText(""); //$NON-NLS-1$
        }
        dashOffsetText.addModifyListener(this);

        // line cap
        Label linCapLabel = new Label(mainComposite, SWT.NONE);
        linCapLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        linCapLabel.setText(Messages.BoderParametersComposite_10);
        lineCapCombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData lineCapGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        lineCapGD.horizontalSpan = 2;
        lineCapCombo.setLayoutData(lineCapGD);
        lineCapCombo.setItems(Utilities.lineCapNames);
        lineCapCombo.addSelectionListener(this);

        String lineCap = lineSymbolizerWrapper.getLineCap();
        if (lineCap != null) {
            int index = getAttributeIndex(lineCap, Utilities.lineCapNames);
            if (index != -1) {
                lineCapCombo.select(index);
            }
        }

        // line join
        Label linJoinLabel = new Label(mainComposite, SWT.NONE);
        linJoinLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        linJoinLabel.setText(Messages.BoderParametersComposite_11);
        lineJoinCombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData lineJoinGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        lineJoinGD.horizontalSpan = 2;
        lineJoinCombo.setLayoutData(lineJoinGD);
        lineJoinCombo.setItems(Utilities.lineJoinNames);
        lineJoinCombo.addSelectionListener(this);

        String lineJoin = lineSymbolizerWrapper.getLineJoin();
        if (lineJoin != null) {
            int index = getAttributeIndex(lineJoin, Utilities.lineJoinNames);
            if (index != -1) {
                lineJoinCombo.select(index);
            }
        }
        
        
        //start and end style
        List<String> ops = new ArrayList<String>();
        ops.addAll(Utilities.lineEndStyles.keySet());
        Collections.sort(ops);
        ops.add(0, ""); //$NON-NLS-1$
        
        // end style
        Label endStyle = new Label(mainComposite, SWT.NONE);
        endStyle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        endStyle.setText(Messages.BoderParametersComposite_7);
        endStyleCombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gd.horizontalSpan = 2;
        endStyleCombo.setLayoutData(lineJoinGD);
        endStyleCombo.setItems(ops.toArray(new String[ops.size()]));
        endStyleCombo.addSelectionListener(this);

        new Label(mainComposite, SWT.NONE);
        
        endStyleSizeSpinner = new Spinner(mainComposite, SWT.BORDER);
        endStyleSizeSpinner.setSelection(10);
        endStyleSizeSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,1));
        endStyleSizeSpinner.setMinimum(0);
        endStyleSizeSpinner.setMaximum(1000);
        endStyleSizeSpinner.setIncrement(1);
        endStyleSizeSpinner.addSelectionListener(this);
        endStyleSizeSpinner.setToolTipText(Messages.BoderParametersComposite_12);
        
        endStyleColorEditor = new ColorEditor(mainComposite);
        endStyleColorEditor.getButton().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        endStyleColorEditor.getButton().addSelectionListener(this);
        endStyleColorEditor.setColor(Color.BLACK);
        
        // end style
        Label beginStyle = new Label(mainComposite, SWT.NONE);
        beginStyle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        beginStyle.setText(Messages.BoderParametersComposite_13);
        startStyleCombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gd.horizontalSpan = 2;
        startStyleCombo.setLayoutData(lineJoinGD);
        startStyleCombo.setItems(ops.toArray(new String[ops.size()]));
        startStyleCombo.addSelectionListener(this);
        
        new Label(mainComposite, SWT.NONE);
        
        startStyleSizeSpinner = new Spinner(mainComposite, SWT.BORDER);
        startStyleSizeSpinner.setSelection(10);
        startStyleSizeSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,1));
        startStyleSizeSpinner.setMinimum(0);
        startStyleSizeSpinner.setMaximum(1000);
        startStyleSizeSpinner.setIncrement(1);
        startStyleSizeSpinner.addSelectionListener(this);
        startStyleSizeSpinner.setToolTipText(Messages.BoderParametersComposite_14);
        
        startStyleColorEditor = new ColorEditor(mainComposite);
        startStyleColorEditor.getButton().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        startStyleColorEditor.getButton().addSelectionListener(this);
        startStyleColorEditor.setColor(Color.BLACK);
        
        checkEnablements();
    }

    /**
     * Update the panel.
     * 
     * @param ruleWrapper the {@link RuleWrapper}.
     */
    public void update( RuleWrapper ruleWrapper ) {
        LineSymbolizerWrapper lineSymbolizerWrapper = ruleWrapper.getGeometrySymbolizersWrapper().adapt(
                LineSymbolizerWrapper.class);

        boolean widgetEnabled = lineSymbolizerWrapper.hasStroke();
        // border
        borderEnableButton.setSelection(widgetEnabled);

        String width = lineSymbolizerWrapper.getStrokeWidth();
        Double tmpWidth = isDouble(width);
        int tmp = 3;
        if (tmpWidth != null) {
            tmp = tmpWidth.intValue();
        }
        borderWidthSpinner.setSelection(tmp * 10);
        if (tmpWidth == null) {
            int index = getAttributeIndex(width, numericAttributesArrays);
            if (index != -1) {
                borderWidthAttributecombo.select(index);
            }
        }

        // border color
        String color = lineSymbolizerWrapper.getStrokeColor();
        Color tmpColor = null;
        try {
            tmpColor = Color.decode(color);
        } catch (Exception e) {
            // ignore and try for field
        }
        if (tmpColor != null) {
            borderColorEditor.setColor(tmpColor);
        } else {
            int index = getAttributeIndex(color, stringAttributesArrays);
            if (index != -1) {
                borderColorAttributecombo.select(index);
            }
        }

        // graphics path
        try {
            graphicsPathText.setText(lineSymbolizerWrapper.getStrokeExternalGraphicStrokePath());
        } catch (MalformedURLException e) {
            graphicsPathText.setText(""); //$NON-NLS-1$
        }

        // border alpha
        String opacity = lineSymbolizerWrapper.getStrokeOpacity();
        Double tmpOpacity = isDouble(opacity);
        tmp = 100;
        if (tmpOpacity != null) {
            tmp = (int) (tmpOpacity.doubleValue() * 100);
        }
        borderOpacitySpinner.setSelection(tmp);
        if (tmpOpacity == null) {
            int index = getAttributeIndex(opacity, numericAttributesArrays);
            if (index != -1) {
                borderOpacityAttributecombo.select(index);
            }
        }

        // dash
        String dash = lineSymbolizerWrapper.getDash();
        float[] dashArray = Utilities.getDash(dash);
        if (dashArray != null) {
            dashText.setText(dash);
        } else {
            dashText.setText(""); //$NON-NLS-1$
        }
        // dash offset
        String dashOffset = lineSymbolizerWrapper.getDashOffset();
        Double dashOffsetDouble = isDouble(dashOffset);
        if (dashOffsetDouble != null) {
            dashOffsetText.setText(dashOffset);
        } else {
            dashOffsetText.setText(""); //$NON-NLS-1$
        }

        // line cap
        String lineCap = lineSymbolizerWrapper.getLineCap();
        if (lineCap != null) {
            int index = getAttributeIndex(lineCap, Utilities.lineCapNames);
            if (index != -1) {
                lineCapCombo.select(index);
            }
        }

        // line join
        String lineJoin = lineSymbolizerWrapper.getLineJoin();
        if (lineJoin != null) {
            int index = getAttributeIndex(lineJoin, Utilities.lineJoinNames);
            if (index != -1) {
                lineJoinCombo.select(index);
            }
        }

        //lets look for a point symbolizer that is associated with the end point
        endStyleColorEditor.setColor(borderColorEditor.getColor());
        startStyleColorEditor.setColor(borderColorEditor.getColor());
        PointSymbolizerWrapper endPnt = lineSymbolizerWrapper.getEndPointStyle();
        if (endPnt != null){
        	endStyleCombo.setText(Utilities.lineEndStyles.inverse().get(endPnt.getMarkName()));
        	endStyleSizeSpinner.setSelection(Integer.valueOf(endPnt.getSize()));
        	try{
        		endStyleColorEditor.setColor(Color.decode(endPnt.getFillColor()));
        	}catch (Exception ex){
        		
        	}
        }
        PointSymbolizerWrapper startPnt = lineSymbolizerWrapper.getStartPointStyle();
        if (startPnt != null){
        	startStyleCombo.setText(Utilities.lineEndStyles.inverse().get(startPnt.getMarkName()));
        	startStyleSizeSpinner.setSelection(Integer.valueOf(startPnt.getSize()));
        	try{
        		startStyleColorEditor.setColor(Color.decode(startPnt.getFillColor()));
        	}catch (Exception ex){
        		
        	}
        }        
        checkEnablements();
    }

    private void checkEnablements() {
    	
    	 boolean selected = borderEnableButton.getSelection();
         //set enabled state of children
         for (Control c : mainComposite.getChildren()){
         	if (c != borderEnableButton){
         		c.setEnabled(selected);
         	}
         }
         
    	if (borderEnableButton.getSelection()){
    		boolean comboIsNone = comboIsNone(borderOpacityAttributecombo);
    		borderOpacitySpinner.setEnabled(comboIsNone);
    		comboIsNone = comboIsNone(borderWidthAttributecombo);
    		borderWidthSpinner.setEnabled(comboIsNone);
    		comboIsNone = comboIsNone(borderColorAttributecombo);
    		borderColorEditor.setEnabled(comboIsNone);
    		
    		boolean end = endStyleCombo.getText().length() > 0;
    		endStyleSizeSpinner.setEnabled(end);
    		endStyleColorEditor.setEnabled(end);
    		
    		boolean start = startStyleCombo.getText().length() > 0;
    		startStyleSizeSpinner.setEnabled(start);
    		startStyleColorEditor.setEnabled(start);
    	}
    }

    public void widgetSelected( SelectionEvent e ) {
        Object source = e.getSource();
        if (source.equals(borderEnableButton)) {
            boolean selected = borderEnableButton.getSelection();
            notifyListeners(String.valueOf(selected), false, STYLEEVENTTYPE.BORDERENABLE);
            
            //set enabled state of children
            for (Control c : mainComposite.getChildren()){
            	if (c != borderEnableButton){
            		c.setEnabled(selected);
            	}
            }
            checkEnablements();
            
        } else if (source.equals(borderWidthSpinner) || source.equals(borderWidthAttributecombo)) {
            boolean comboIsNone = comboIsNone(borderWidthAttributecombo);
            if (comboIsNone) {
                int selection = borderWidthSpinner.getSelection();
                int digits = borderWidthSpinner.getDigits();
                double value = selection / Math.pow(10, digits);
                String strokeWidth = String.valueOf(value);
                notifyListeners(strokeWidth, false, STYLEEVENTTYPE.BORDERWIDTH);
            } else {
                int index = borderWidthAttributecombo.getSelectionIndex();
                String field = borderWidthAttributecombo.getItem(index);
                if (field.length() == 0) {
                    return;
                }
                notifyListeners(field, true, STYLEEVENTTYPE.BORDERWIDTH);
            }
        } else if (source.equals(borderColorButton) || source.equals(borderColorAttributecombo)) {
            boolean comboIsNone = comboIsNone(borderColorAttributecombo);
            if (comboIsNone) {
                Color color = borderColorEditor.getColor();
                Expression colorExpr = ff.literal(color);
                String strokeColor = colorExpr.evaluate(null, String.class);
                notifyListeners(strokeColor, false, STYLEEVENTTYPE.BORDERCOLOR);
            } else {
                int index = borderColorAttributecombo.getSelectionIndex();
                String field = borderColorAttributecombo.getItem(index);
                if (field.length() == 0) {
                    return;
                }
                notifyListeners(field, true, STYLEEVENTTYPE.BORDERCOLOR);
            }
        } else if (source.equals(borderOpacitySpinner) || source.equals(borderOpacityAttributecombo)) {
            boolean comboIsNone = comboIsNone(borderOpacityAttributecombo);
            if (comboIsNone) {
                int opacity = borderOpacitySpinner.getSelection();
                float opacityNorm = opacity / 100f;
                String strokeOpacity = String.valueOf(opacityNorm);
                notifyListeners(strokeOpacity, false, STYLEEVENTTYPE.BORDEROPACITY);
            } else {
                int index = borderOpacityAttributecombo.getSelectionIndex();
                String field = borderOpacityAttributecombo.getItem(index);
                if (field.length() == 0) {
                    return;
                }
                notifyListeners(field, true, STYLEEVENTTYPE.BORDEROPACITY);
            }
        } else if (source.equals(lineCapCombo)) {
            int index = lineCapCombo.getSelectionIndex();
            String item = lineCapCombo.getItem(index);
            if (item.length() == 0) {
                return;
            }
            notifyListeners(item, true, STYLEEVENTTYPE.LINECAP);
        } else if (source.equals(lineJoinCombo)) {
            int index = lineJoinCombo.getSelectionIndex();
            String item = lineJoinCombo.getItem(index);
            if (item.length() == 0) {
                return;
            }
            notifyListeners(item, true, STYLEEVENTTYPE.LINEJOIN);
        } else if (source.equals(endStyleCombo) || source.equals(endStyleSizeSpinner) || 
        		source.equals(endStyleColorEditor.getButton())){
        	String name = endStyleCombo.getItem(endStyleCombo.getSelectionIndex());
        	name = Utilities.lineEndStyles.get(name);
        	int size = endStyleSizeSpinner.getSelection();
        	String color = ff.literal(endStyleColorEditor.getColor()).evaluate(null, String.class);
        	notifyListeners(new String[]{geometryProperty, name, String.valueOf(size), color}, false, STYLEEVENTTYPE.LINEEND);
        	
        	//notify opacity listeners too to ensure opacity is set
       	 	String opacity = null;
       	 	boolean comboIsNone = comboIsNone(borderOpacityAttributecombo);
            if (comboIsNone) { 
                opacity = String.valueOf(borderOpacitySpinner.getSelection() / 100f);
            } else {
                opacity = borderOpacityAttributecombo.getItem(borderOpacityAttributecombo.getSelectionIndex());
                if (opacity.length() == 0) {
               	 opacity = "1"; //$NON-NLS-1$
                }
            }
        	notifyListeners(opacity, !comboIsNone, STYLEEVENTTYPE.BORDEROPACITY);
        } else if (source.equals(startStyleCombo) || source.equals(startStyleSizeSpinner) ||
        		source.equals(startStyleColorEditor.getButton())){
        	String name = startStyleCombo.getItem(startStyleCombo.getSelectionIndex());
        	name = Utilities.lineEndStyles.get(name);
        	int size = startStyleSizeSpinner.getSelection();
        	String color = ff.literal(startStyleColorEditor.getColor()).evaluate(null, String.class);
            
        	notifyListeners(new String[]{geometryProperty, name, String.valueOf(size), color}, false, STYLEEVENTTYPE.LINESTART);
        	
        	
        	//notify opacity listeners too to ensure opacity is set
       	 	String opacity = null;
       	 	boolean comboIsNone = comboIsNone(borderOpacityAttributecombo);
            if (comboIsNone) { 
                opacity = String.valueOf(borderOpacitySpinner.getSelection() / 100f);
            } else {
                opacity = borderOpacityAttributecombo.getItem(borderOpacityAttributecombo.getSelectionIndex());
                if (opacity.length() == 0) {
               	 opacity = "1"; //$NON-NLS-1$
                }
            }
        	notifyListeners(opacity, !comboIsNone, STYLEEVENTTYPE.BORDEROPACITY);
        }

        checkEnablements();
    }

    public void modifyText( ModifyEvent e ) {
        Object source = e.getSource();
        if (source.equals(graphicsPathText)) {
            try {
                String text = graphicsPathText.getText();
                File graphicsFile = new File(text);
                if (graphicsFile.exists() || text.toLowerCase().startsWith("http")) { //$NON-NLS-1$
                    text = graphicsFile.toURI().toURL().toExternalForm();

                    // FIXME bring those to gui
                    String strokeWidth = String.valueOf(1);
                    String strokeSize = String.valueOf(15);

                    notifyListeners(new String[]{text, strokeWidth, strokeSize}, false, STYLEEVENTTYPE.GRAPHICSPATHBORDER);
                }else if (text.length() == 0){
                	notifyListeners(new String[]{text, "0", "0"}, false, STYLEEVENTTYPE.GRAPHICSPATHBORDER); //$NON-NLS-1$ //$NON-NLS-2$
                }
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
        } else if (source.equals(dashText)) {
            String text = dashText.getText();
            float[] dash = Utilities.getDash(text);
            if (dash == null) {
                return;
            }
            notifyListeners(new String[]{text}, false, STYLEEVENTTYPE.DASH);
        } else if (source.equals(dashOffsetText)) {
            String text = dashOffsetText.getText();
            notifyListeners(new String[]{text}, false, STYLEEVENTTYPE.DASHOFFSET);
        }
    }

}
